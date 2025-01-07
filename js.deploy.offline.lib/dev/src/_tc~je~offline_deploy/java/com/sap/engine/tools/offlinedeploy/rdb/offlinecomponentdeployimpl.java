package com.sap.engine.tools.offlinedeploy.rdb;

import com.sap.engine.core.configuration.bootstrap.ConfigurationManagerBootstrapImpl;
import com.sap.engine.core.configuration.impl.ConfigurationHandlerImpl;
import com.sap.engine.frame.core.configuration.*;
import com.sap.engine.component.info.per.ClassToComponentMapHandler;
import com.sap.engine.component.info.per.PersistentComponentInfo;
import com.sap.engine.component.info.per.RepositoryException;
import com.sap.engine.component.info.lib.ClassNamesRetriever;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

import javax.xml.parsers.ParserConfigurationException;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipFile;
import java.sql.Connection;
import java.sql.SQLException;

public class OfflineComponentDeployImpl implements OfflineComponentDeploy, Constants {

  //Configuration Manager
  private ConfigurationHandlerFactory factory = null;
  //DOM Parser
  private DOMParseHelper domParser = null;
  //current handler
  private ConfigurationHandler handler;
  // Source for DB connections
  private DataSource dataSource;

  //Offline Deploy location
  static Location loc = Location.getLocation(OfflineComponentDeployImpl.class.getName(), KERNEL_DC_NAME, KERNEL_CSN_COMPONENT);
  //auto commit flag
  static boolean autoCommit = true;
  //if flag is true the configuration factory is ConfigurationManagerBootstrapImpl
  static boolean isOffline = true;
  //common data and when auto commit is switched off using begin() method.
  static CommonData commonData = null;
  //Warning array
  static ArrayList<String> warningList = new ArrayList<String>();

  public OfflineComponentDeployImpl(final ConfigurationHandlerFactory configurationHandlerFactory, final Location location, final DataSource ds) throws DeploymentException {
    if (location != null) {
      loc = location;
    }
    factory = configurationHandlerFactory;

    if (ds == null) {
      throw new DeploymentException("Provided DataSource is null, error in Java class that invoked OCDFactory");
    }
    dataSource = ds;
    isOffline = factory instanceof ConfigurationManagerBootstrapImpl;
    try {
      domParser = new DOMParseHelper();
    } catch (ParserConfigurationException e) {
      throw new DeploymentException("Can not initialize parser", e);
    }
  }

  public synchronized ResultStatus deployComponent(final File jar, final byte componentType) throws DeploymentException {
    Utils.info("ASJ.dpl_off.000004", "Upload SDA archive [" + jar.getName() + "]");
    Utils.info("ASJ.dpl_off.000005", "Processing ...");
    String jarPath;
    try {
      jarPath = jar.getCanonicalPath();
    } catch (IOException e) {
      jarPath = jar.getAbsolutePath();
    }
    if (!jar.exists()) {
      throw new DeploymentException("Cannot deploy component because SDA [" + jarPath + "] does not exists");
    }
    ZipFile zip = null;
    try {
      zip = new ZipFile(jar);
      if (handler == null) {
        handler = factory.getConfigurationHandler();
        if (isOffline) {
          StructureConsistentcy.checkStructureConsistentcy(handler);
        }
      }
      updateBinaryVersion(handler);
      String csnComponent = Utils.getCSNComponent(zip);
      String runtimeName = null;
      String componentName = null;
      String providerName = null;
      switch (componentType) {
        case TYPE_TEMPLATE : {
          String[] mapping = TemplateDeployer.deployTemplate(handler, zip, domParser);
          storeTemplateMapping(mapping[0], mapping[1]);
        } break;
        case TYPE_KERNEL : {
          KernelDeployer.deployKernel(handler, zip);
          if (zip.getEntry(TEMPLATE_ENTRY) != null) {
            String[] mapping = TemplateDeployer.deployTemplate(handler, zip, domParser);
            storeTemplateMapping(mapping[0], mapping[1]);
          }
          // Not all kernel components are "kernel.sda", there is also "tj/je/bootstrap_core_lib"
          String[] keyVendorKeyName = Utils.getKeyVendorKeyName(zip);
          providerName = getCorrected(keyVendorKeyName[0]);
          componentName = getCorrected(keyVendorKeyName[1]);
          safelyStoreSrcZip(zip, componentName, providerName);
        } break;
        case TYPE_BOOTSTRAP : {
          KernelDeployer.deployBootstrap(handler, zip);
        } break;
        case TYPE_INTERFACE :
        case TYPE_LIBRARY :
        case TYPE_SERVICE : {
          String[] keyVendorKeyName = Utils.getKeyVendorKeyName(zip);
          providerName = getCorrected(keyVendorKeyName[0]);
          componentName = getCorrected(keyVendorKeyName[1]);
          String dcName = getDCName(keyVendorKeyName[0], keyVendorKeyName[1]);
          Properties component = ComponentDeployer.deploy(handler, zip, domParser, csnComponent, dcName, componentType, isOffline);
          if (!keyVendorKeyName[0].equals(component.getProperty("provider-name")) || !keyVendorKeyName[1].equals(component.getProperty("component-name"))) {
            storeUndeployMapping(keyVendorKeyName[0] + '$' + keyVendorKeyName[1], component.getProperty("provider-name") + '$' + component.getProperty("component-name"), componentType);
          } else {
            //check for old mapping existence
            checkUndeployMapping(keyVendorKeyName[0] + '$' + keyVendorKeyName[1], componentType);
          }
          runtimeName = component.getProperty("runtime-name");
          updateServiceRepository(runtimeName, componentType, component);
          safelyStoreSrcZip(zip, componentName, providerName);
        } break;
        default: {
          throw new DeploymentException("Unsupported software type [" + componentType + "] found");
        }
      }

      int componentInfoType = getPersistentComponentInfoType(componentType);
      if (componentInfoType != PersistentComponentInfo.TYPE_UNKNOWN) {
        ClassNamesRetriever r = new ClassNamesRetriever(zip);
        PersistentComponentInfo c = new PersistentComponentInfo(
            componentName,
            providerName,
            componentInfoType,
            r.getClassListSource()
        );
        safelyStoreClassIndex(c, r.getClassNames());
      }

      if (autoCommit) {
        handler.commit();
      }
      return new ResultStatusImpl(warningList, jarPath, csnComponent, runtimeName);
    } catch (DeploymentException exc) {
      rollbackInternal();
      throw exc;
    } catch (Exception exc) {
      rollbackInternal();
      throw new DeploymentException("Cannot deploy component with SDA [" + jarPath + "]. Reported reason : [" + exc.getMessage() + "]", exc);
    } finally {
      doFinally(zip);
    }
  }

  public synchronized ResultStatus undeployComponent(String providerName, String componentName, byte componentType) throws DeploymentException, ComponentNotDeployedException {
    String name = Utils.modifyComponentName(componentName, providerName);
    Utils.print(Severity.INFO, "ASJ.dpl_off.000006", "Remove component [" + name + "]");
    Utils.print(Severity.INFO, "ASJ.dpl_off.000007", "Processing ...");
    try {
      if (handler == null) {
        handler = factory.getConfigurationHandler();
        if (isOffline) {
          StructureConsistentcy.checkStructureConsistentcy(handler);
        }
      }
      updateBinaryVersion(handler);
      String csnComponent = null;
      String runtimeName = null;
      switch (componentType) {
        case TYPE_TEMPLATE : {
          String template = getTemplateName(providerName + '$' + componentName);
          TemplateDeployer.removeTemplate(handler, template);
        } break;
        case TYPE_KERNEL : {
          KernelDeployer.removeKernel(handler);
          String template = getTemplateName("sap.com$kernel.sda");
          if (template != null) {
            TemplateDeployer.removeTemplate(handler, template);
          }
          safelyRemoveSrcZip(componentName, providerName);
        } break;
        case TYPE_BOOTSTRAP : {
          KernelDeployer.removeBootstrap(handler);
        } break;
        case TYPE_INTERFACE :
        case TYPE_LIBRARY :
        case TYPE_SERVICE : {
          runtimeName = getMappingName(providerName, componentName);
          //init csnComponent only for services libraries and interfaces because for other types it is not persists
          csnComponent = readCSNComponentFromSC(runtimeName, componentType);
          ComponentDeployer.remove(handler, runtimeName, componentType);
          updateServiceRepository(runtimeName, componentType, null);
          safelyRemoveSrcZip(componentName, providerName);
        } break;
        default: {
          throw new DeploymentException("Unsupported software type [" + componentType + "] found");
        }
      }

      int componentInfoType = getPersistentComponentInfoType(componentType);
      if (componentInfoType != PersistentComponentInfo.TYPE_UNKNOWN) {
        PersistentComponentInfo c = new PersistentComponentInfo(
            componentName,
            providerName,
            componentInfoType,
            PersistentComponentInfo.INFO_SOURCE_UNKNOWN
        );
        safelyStoreClassIndex(c, null);
      }

      if (autoCommit) {
        handler.commit();
      }
      return new ResultStatusImpl(warningList, null, csnComponent, runtimeName);
    } catch (ComponentNotDeployedException exc) {
      rollbackInternal();
      throw exc;
    } catch (DeploymentException exc) {
      rollbackInternal();
      throw exc;
    } catch (Exception exc) {
      rollbackInternal();
      throw new DeploymentException("Cannot remove component [" + name + "]. Reported reason : [" + exc.getMessage() + "]", exc);
    } finally {
      doFinally();
    }
  }

  public synchronized void begin() {
    autoCommit = false;
    commonData = new CommonData();
  }

  public synchronized void commit() throws DeploymentException {
    if (!autoCommit) {
      try {
        if (handler != null) {
          try {
            if (commonData.undeployMapping != null) {
              Configuration undeploy = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + UNDEPLOY);
              Utils.storePropertiesAsFile(undeploy, commonData.undeployMapping, UNDEPLOY_MAPPING);
            }
            if (commonData.templateMapping != null) {
              Configuration undeploy = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + UNDEPLOY);
              Utils.storePropertiesAsFile(undeploy, commonData.templateMapping, TEMPLATE_MAPPING);
            }
            if (commonData.serviceRepository != null) {
              Configuration workernode = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + CLUSTERNODE_CONFIG + "/" + WORKERNODE);
              Utils.storeServiceRepository(workernode, commonData.serviceRepository);
            }
            if (commonData.nativeDescriptorMap != null) {
              Configuration natives = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + BIN + "/" + NATIVE);
              NativeDeployer.storeNativeDescriptor(commonData.nativeDescriptorMap, natives);
            }
            handler.commit();
          } catch (Exception e) {
            try {
              handler.rollback();
            } catch (ConfigurationException ce) {
              throw new DeploymentException("Cannot rollback handler [" + handler + "]", ce);
            }
            throw new DeploymentException("Cannot commit handler [" + handler + "]", e);
          }
        }
      } finally {
        if (commonData.zipFiles != null) {
          for (ZipFile zip : commonData.zipFiles) {
            try {
              zip.close();
            } catch (IOException e) {
              // $JL-EXC$
              Utils.warning(e, "ASJ.dpl_off.000001", "Cannot close zip file [" + zip.getName() + "]");
            }
          }
        }
        autoCommit = true;
        handler = null;
        commonData = null;
      }
    }
  }

  public synchronized void rollback() throws DeploymentException {
    if (!autoCommit) {
      try {
        if (handler != null) {
          handler.rollback();
        }
      } catch (ConfigurationException e) {
        throw new DeploymentException("Cannot rollback handler [" + handler + "]", e);
      } finally {
        if (commonData.zipFiles != null) {
          for (ZipFile zip : commonData.zipFiles) {
            try {
              zip.close();
            } catch (IOException e) {
              // $JL-EXC$
              SimpleLogger.traceThrowable(Severity.DEBUG, loc, e, "ASJ.dpl_off.000002", "Cannot close zip file [{0}]", zip.getName());
            }
          }
        }
        autoCommit = true;
        handler = null;
        commonData = null;
      }
    }
  }

  public synchronized void close() {
    if (isOffline) {
      ((ConfigurationManagerBootstrapImpl) factory).shutdown();
    }
  }

  private void doFinally(ZipFile zip) throws DeploymentException {
    if (zip != null) {
      if (autoCommit) {
        try {
          zip.close();
        } catch (IOException e) {
          // $JL-EXC$
          SimpleLogger.traceThrowable(Severity.DEBUG, loc, e, "ASJ.dpl_off.000003", "Cannot close zip file [{0}]", zip.getName());
        }
      } else {
        if (commonData.zipFiles == null) {
          commonData.zipFiles = new ArrayList<ZipFile>();
        }
        commonData.zipFiles.add(zip);
      }
    }
    doFinally();
  }

  private void doFinally() throws DeploymentException {
    warningList.clear();
    try {
      if (autoCommit && handler != null) {
        handler.closeAllConfigurations();
      }
    } catch (ConfigurationException e) {
      throw new DeploymentException("Cannot close all configurations for handler [" + handler + "]", e);
    } finally {
      if (autoCommit) {
        handler = null;
      }
    }
  }

  private void rollbackInternal() throws DeploymentException {
    if (autoCommit && handler != null) {
      try {
        handler.rollback();
      } catch (ConfigurationException e) {
        throw new DeploymentException("Cannot rollback handler [" + handler + "]", e);
      }
    }
  }

  private void safelyStoreClassIndex(final PersistentComponentInfo componentInfo, final Set<String> classNames) {
    Connection ownNonTransactionalConn = null;
    ClassToComponentMapHandler classToComponentMapHandler = null;
    try {
      // Get a non-transactional connection
      ownNonTransactionalConn = dataSource.getConnection();
      ownNonTransactionalConn.setAutoCommit(true);

      // Get the deploy transactional connection
      // Join the Configuration handler deploy transaction
      Connection deploymentConn = ((ConfigurationHandlerImpl)handler).getDistributedConnection();

      // If there is no Configuration handler transaction started
      if (deploymentConn == null) {
        // Workaround: Use our own DB connection.
        //Limitation: No transaction, no rollback
        deploymentConn = ownNonTransactionalConn;
      }

      classToComponentMapHandler = new ClassToComponentMapHandler(deploymentConn, ownNonTransactionalConn);

      if (classNames != null) {
        classToComponentMapHandler.setClassesForComponent(componentInfo, classNames);
      } else {
        classToComponentMapHandler.removeClassesForComponent(componentInfo);
      }
    } catch (RepositoryException e) {
      String warning = String.format("Cannot update component info in DB for component [%s]: %s", componentInfo.toString(), e.toString());
      Utils.warning(e, "ASJ.dpl_off.000020", warning);
    } catch (SQLException e) {
      String warning = String.format("Cannot update component info in DB for component [%s]: %s", componentInfo.toString(), e.toString());
      Utils.warning(e, "ASJ.dpl_off.000020", warning);
    } finally {
      if (classToComponentMapHandler != null) {
        classToComponentMapHandler.close();
      }

      if (ownNonTransactionalConn != null) {
        try {
          ownNonTransactionalConn.close();
        } catch (SQLException e) {
          Utils.warning(e, "ASJ.dpl_off.000019", "Cannot close DB connection after saving class name index for component [" + componentInfo.getName() + "] from vendor [" + componentInfo.getVendor() + "]");
        }
      }
    }
  }

  private void safelyStoreSrcZip(final ZipFile sda, final String componentName, final String vendor) throws ConfigurationException, IOException, DeploymentException {
    try {
      KernelSourceStorageHandler.storeSrcZip(handler, sda, componentName, vendor);
    } catch (Exception e) {
      String warning = String.format("Cannot update component info in DB during component [%s/%s] deploy or update: %s", componentName, vendor, e.toString());
      Utils.warning(e, "ASJ.dpl_off.000021", warning);
    }
  }

  private void safelyRemoveSrcZip(final String componentName, final String vendor) throws ConfigurationException, IOException, DeploymentException {
    try {
      KernelSourceStorageHandler.removeSrcZip(handler, componentName, vendor);
    } catch (Exception e) {
      String warning = String.format("Cannot update component info in DB during component [%s/%s] undeploy: %s", componentName, vendor, e.toString());
      Utils.warning(e, "ASJ.dpl_off.000022", warning);
    }
  }

  private void updateServiceRepository(String name, byte type, Properties properties) throws ConfigurationException, IOException, ClassNotFoundException, DeploymentException {
    Configuration cfg = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + CLUSTERNODE_CONFIG + "/" + WORKERNODE);
    SCRepository serviceRepository;
    //init service repository
    if (autoCommit) {
      serviceRepository = Utils.loadServiceRepository(cfg);
    } else {
      serviceRepository = commonData.serviceRepository;
      if (serviceRepository == null) {
        serviceRepository = Utils.loadServiceRepository(cfg);
        commonData.serviceRepository = serviceRepository;
      }
    }
    if (properties == null) {
      //remove from repository
      serviceRepository.removeComponentProviderProperties(name, type);
    } else {
      //add in repository
      serviceRepository.setComponentProviderProperties(name, type, properties);
    }
    //store file in db if autocommit is on
    if (autoCommit) {
      Utils.storeServiceRepository(cfg, serviceRepository);
    }
  }

  //return <null> if component does not exist
  private String readCSNComponentFromSC(String name, byte type) throws ConfigurationException, IOException, ClassNotFoundException, DeploymentException {
    Configuration cfg = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + CLUSTERNODE_CONFIG + "/" + WORKERNODE);
    String result = null;
    SCRepository serviceRepository;
    //init service repository
    if (autoCommit) {
      serviceRepository = Utils.loadServiceRepository(cfg);
    } else {
      serviceRepository = commonData.serviceRepository;
      if (serviceRepository == null) {
        serviceRepository = Utils.loadServiceRepository(cfg);
        commonData.serviceRepository = serviceRepository;
      }
    }
    Properties componentProps = serviceRepository.getComponentProviderProperties(name, type);
    if (componentProps != null) {
      result = componentProps.getProperty("csn-component");
    }
    return result;
  }

  private void checkUndeployMapping(String key, byte type) throws IOException, ConfigurationException, DeploymentException {
    Configuration undeploy = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + UNDEPLOY);
    Properties mapping;
    if (autoCommit) {
      mapping = Utils.loadPropertiesFromDB(undeploy, UNDEPLOY_MAPPING);
      //if DC and Runtime names are equal, but old mapping exist -> exception
      checkForChangeOfRuntimeName(mapping, key, key, type);
      Utils.storePropertiesAsFile(undeploy, mapping, UNDEPLOY_MAPPING);
    } else {
      mapping = commonData.undeployMapping;
      if (mapping == null) {
        mapping = Utils.loadPropertiesFromDB(undeploy, UNDEPLOY_MAPPING);
        commonData.undeployMapping = mapping;
      }
      //if DC and Runtime names are equal, but old mapping exist -> exception
      checkForChangeOfRuntimeName(mapping, key, key, type);
    }
  }

  private void storeUndeployMapping(String key, String value, byte type) throws IOException, ConfigurationException, DeploymentException {
    Configuration undeploy = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + UNDEPLOY);
    if (autoCommit) {
      Properties mapping = Utils.loadPropertiesFromDB(undeploy, UNDEPLOY_MAPPING);
      checkForChangeOfRuntimeName(mapping, key, value, type);
      mapping.setProperty(key, value);
      Utils.storePropertiesAsFile(undeploy, mapping, UNDEPLOY_MAPPING);
    } else {
      Properties mapping = commonData.undeployMapping;
      if (mapping == null) {
        mapping = Utils.loadPropertiesFromDB(undeploy, UNDEPLOY_MAPPING);
        commonData.undeployMapping = mapping;
      }
      checkForChangeOfRuntimeName(mapping, key, value, type);
      mapping.setProperty(key, value);
    }
  }

  private void checkForChangeOfRuntimeName(Properties mapping, String key, String value, byte type) throws DeploymentException {
    if (mapping.containsKey(key) && !mapping.getProperty(key).equals(value)) {
      String oldValue = mapping.getProperty(key);
      int i1 = key.indexOf('$'); //DC name
      int i2 = value.indexOf('$'); //new Runtime
      int i3 = oldValue.indexOf('$'); //old Runtime
      String runtimeName = Utils.modifyComponentName(value.substring(i2 + 1), value.substring(0, i2));
      String oldRuntimeName = Utils.modifyComponentName(oldValue.substring(i3 + 1), oldValue.substring(0, i3));
      //if component type is interface check for double named interfaces: <interface>=<interface>_api
      if (type == TYPE_INTERFACE) {
        runtimeName = getRuntimeInterfaceName(runtimeName);
        oldRuntimeName = getRuntimeInterfaceName(oldRuntimeName);
      }
      //check if runtime names are equals
      if (runtimeName.equals(oldRuntimeName)) {
        if (key.equals(value)) {
          mapping.remove(key); //removing mapping if difference between DC & Runtime name is fixed
        }
      } else {
        //runtime name is changed -> exception
        throw new DeploymentException("Runtime name of component [" + key.substring(0, i1) + "~" + key.substring(i1 + 1) + "]" + " is changed from ["
                + oldRuntimeName + "] to [" + runtimeName + "]. Please, first remove the DC and then proceed with deployment");
      }
    }
  }

  private void storeTemplateMapping(String key, String value) throws IOException, ConfigurationException {
    Configuration undeploy = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + UNDEPLOY);
    if (autoCommit) {
      Properties mapping = Utils.loadPropertiesFromDB(undeploy, TEMPLATE_MAPPING);
      mapping.setProperty(key, value);
      Utils.storePropertiesAsFile(undeploy, mapping, TEMPLATE_MAPPING);
    } else {
      Properties mapping = commonData.templateMapping;
      if (mapping == null) {
        mapping = Utils.loadPropertiesFromDB(undeploy, TEMPLATE_MAPPING);
        commonData.templateMapping = mapping;
      }
      mapping.setProperty(key, value);
    }
  }

  private void updateBinaryVersion(ConfigurationHandler handler) throws ConfigurationException {
    Configuration binRoot = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + BIN);
    int binVersion;
    if (binRoot.existsConfigEntry(BIN_VERSION)) {
      binVersion = (Integer) binRoot.getConfigEntry(BIN_VERSION) + 1;
    } else {
      binVersion = 1;
    }
    binRoot.modifyConfigEntry(BIN_VERSION, binVersion, true);
  }

  private String getTemplateName(String key) throws IOException, ConfigurationException {
    String result;
    Configuration undeploy = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + UNDEPLOY);
    if (autoCommit) {
      Properties mapping = Utils.loadPropertiesFromDB(undeploy, TEMPLATE_MAPPING);
      result = (String) mapping.remove(key);
      Utils.storePropertiesAsFile(undeploy, mapping, TEMPLATE_MAPPING);
    } else {
      Properties mapping = commonData.templateMapping;
      if (mapping == null) {
        mapping = Utils.loadPropertiesFromDB(undeploy, TEMPLATE_MAPPING);
        commonData.templateMapping = mapping;
      }
      result = (String) mapping.remove(key);
    }
    return result;
  }

  private String getMappingName(String providerName, String componentName) throws IOException, ConfigurationException {
    String result;
    String key = providerName + '$' + componentName;
    Configuration undeploy = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + UNDEPLOY);
    if (autoCommit) {
      Properties mapping = Utils.loadPropertiesFromDB(undeploy, UNDEPLOY_MAPPING);
      result = searchInMapping(mapping, key);
      Utils.storePropertiesAsFile(undeploy, mapping, UNDEPLOY_MAPPING);
    } else {
      Properties mapping = commonData.undeployMapping;
      if (mapping == null) {
        mapping = Utils.loadPropertiesFromDB(undeploy, UNDEPLOY_MAPPING);
        commonData.undeployMapping = mapping;
      }
      result = searchInMapping(mapping, key);
    }
    if (result != null) {
      Utils.print(Severity.INFO, "ASJ.dpl_off.000008", "Found component name mapping : [" + key + "] <-> [" + result + "]");
      int index = result.indexOf('$');
      result = Utils.modifyComponentName(result.substring(index + 1), result.substring(0, index));
    } else {
      result = Utils.modifyComponentName(componentName, providerName);
    }
    return result;
  }

  private String searchInMapping(Properties mapping, String targetKey) {
    //modify key pairs (<keyvendor>$<keyname>) from mapping as defined in com.sap.engine.frame.ComponentNameUtils and compare to targetKey
    String[] keys = mapping.keySet().toArray(new String[mapping.keySet().size()]);
    for (String key : keys) {
      int index = key.indexOf('$');//remark: keyvendor must not contains '$'
      String keyVendor = getCorrected(key.substring(0, index));
      String keyName = getCorrected(key.substring(index + 1));
      if (targetKey.equals(keyVendor + '$' + keyName)) {
        //found mapping
        return (String) mapping.remove(key);
      }
    }
    return null;
  }

  //same as com.sap.engine.frame.ComponentNameUtils.getCorrected(String name), but FORBIDDEN_NAME_SYMBOLS constant is used
  private static String getCorrected(String name) {
    // in case of null or blank string ( white space only ) do not go any further
    if ( name == null || name.trim().length() == 0  ) {
      return name;
    }
    char[] chars = name.toCharArray();
    // for each character check if it is an illegal one and replace it accordingly
    for (int i = 0; i < chars.length; i++) {
      for (int k = 0; k < ILLEGAL_CHARS.length(); k++) {
        if (chars[i] == ILLEGAL_CHARS.charAt(k)) {
          chars[i] = '~';
          break; // no need to check for the rest of the illegal chars
        }
      }
    }
    // since the name of the appliation can't end with '.' if this is the case replace the dot with the legal char
    if (chars[chars.length - 1] == '.' ) {
      chars[chars.length - 1] = '~';
    }
    return new String(chars);
  }

  private String getRuntimeInterfaceName(String interfaceName) {
    if (interfaceName.endsWith("_api")) {
      for (String iName : INTERFACE_NAMES_API) {
        if (interfaceName.equals(iName)) {
          //remove the _api suffix
          return interfaceName.substring(0, interfaceName.length() - 4);
        }
      }
    }
    return interfaceName;
  }

  private String getDCName(String vendor, String name) {
    return getCorrected(vendor) + "/" + getCorrected(name);
  }

  private static int getPersistentComponentInfoType(final byte componentType) {
    switch (componentType) {
      case TYPE_KERNEL    : return PersistentComponentInfo.TYPE_KERNEL;
      case TYPE_INTERFACE : return PersistentComponentInfo.TYPE_INTERFACE; 
      case TYPE_LIBRARY   : return PersistentComponentInfo.TYPE_LIBRARY;
      case TYPE_SERVICE   : return PersistentComponentInfo.TYPE_SERVICE;
      default             : return PersistentComponentInfo.TYPE_UNKNOWN;
    }
  }

}