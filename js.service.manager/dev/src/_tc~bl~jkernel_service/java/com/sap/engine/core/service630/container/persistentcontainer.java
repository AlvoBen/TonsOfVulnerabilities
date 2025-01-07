package com.sap.engine.core.service630.container;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.bootstrap.online.Manager;
import com.sap.engine.bootstrap.online.ThrowableEntry;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.engine.core.database.DatabaseManager;
import com.sap.engine.core.cluster.ClusterManager;
import com.sap.engine.core.service630.ResourceUtils;
import com.sap.engine.frame.NestedProperties;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.container.deploy.ComponentNotDeployedException;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.addons.PropertyEntry;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.frame.core.database.DatabaseException;
import com.sap.engine.lib.config.api.ClusterConfiguration;
import com.sap.engine.lib.config.api.CommonClusterFactory;
import com.sap.engine.lib.config.api.ConfigurationLevel;
import com.sap.engine.lib.config.api.component.ComponentHandler;
import com.sap.engine.lib.config.api.component.ComponentProperties;
import com.sap.engine.lib.config.api.component.ManagerHandler;
import com.sap.engine.lib.config.api.exceptions.ClusterConfigurationException;
import com.sap.engine.tools.offlinedeploy.rdb.DeploymentException;
import com.sap.engine.tools.offlinedeploy.rdb.OCDFactory;
import com.sap.engine.tools.offlinedeploy.rdb.OfflineComponentDeploy;
import com.sap.engine.tools.offlinedeploy.rdb.ResultStatus;
import com.sap.engine.tools.offlinedeploy.rdb.SCRepository;
import com.sap.engine.tools.offlinedeploy.rdb.SCRepositoryRegenerator;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

import javax.sql.DataSource;

/**
 * This class is singleton and provides access to DB and FS resources. It use configuration library to read components properties
 * and use offline deploy and bootstrap to deploy and synch runtime libraries.
 *
 * @see com.sap.engine.lib.config.api.CommonClusterFactory
 * @see com.sap.engine.tools.offlinedeploy.rdb.OfflineComponentDeploy
 * @see com.sap.engine.bootstrap.online.Manager
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class PersistentContainer {

  public final static String CURRENT_INSTANCE_ROOT = "cluster_config/system/instances/current_instance";
  public final static String TEMP_DIRECTORY_NAME = "./temp/service_manager";

  private static final Category CATEGORY = Category.SYS_SERVER;
  private static final Location LOCATION = Location.getLocation(PersistentContainer.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  private MemoryContainer memoryContainer;
  private ConfigurationHandlerFactory configContext = (ConfigurationHandlerFactory) Framework.getManager(Names.CONFIGURATION_MANAGER);

  private ConfigurationLevel instanceLevel;
  private ConfigurationLevel templateLevel;
  private ConfigurationLevel defaultTemplateLevel;
  private ConfigurationLevel customGlobalLevel;

  PersistentContainer (MemoryContainer memoryContainer) {
    this.memoryContainer = memoryContainer;
    //clean up tmp directory
    File tmp_dir = new File(TEMP_DIRECTORY_NAME);
    if(tmp_dir.exists()) {
      deleteDirectory(tmp_dir, false);
    }
    initLocalAndGlobalLevels();
  }

  private void deleteDirectory(File directoy, boolean deleteRoot) {
    File[] files = directoy.listFiles();
    for (File file : files) {
      if (file.isDirectory()) {
        deleteDirectory(file, true);
      } else {
        if (file.exists()) {
          file.delete();
        }
      }
    }
    if (deleteRoot) {
      directoy.delete();
    }
  }

  private void initLocalAndGlobalLevels() {
    try {
      CommonClusterFactory factory = ClusterConfiguration.getClusterFactory(configContext);
      int instanceId = ((ClusterManager) Framework.getManager(Names.CLUSTER_MANAGER)).getClusterMonitor().getCurrentParticipant().getGroupId();
      factory.setWorkingDirectory(TEMP_DIRECTORY_NAME);
      instanceLevel = factory.openConfigurationLevel(CommonClusterFactory.LEVEL_INSTANCE, "ID" + instanceId);
      templateLevel = instanceLevel.getParent();
      defaultTemplateLevel = templateLevel.getParent();
      customGlobalLevel = factory.openGlobalConfigurationLevel();
    } catch (ClusterConfigurationException e) {
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000022",
            "Cannot initialize global, local and instance configuration levels");
      }
      if (SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
            "Cannot initialize global, local and instance configuration levels", e);
      }
      throw new ServiceRuntimeException(LOCATION, e);
    }
  }

  ConfigurationLevel getInstanceLevel() {
    return instanceLevel;
  }

  ConfigurationLevel getTemplateLevel() {
    return templateLevel;
  }
  
  ConfigurationLevel getDefaultTemplateLevel() {
    return defaultTemplateLevel;
  }

  ConfigurationLevel getCustomGlobalLevel() {
    return customGlobalLevel;
  }

  /////////////////////////////////////////// READ COMPONENTS FROM DB //////////////////////////////////////////////////

  void createComponents(Hashtable<String, InterfaceWrapper> interfaces, Hashtable<String, LibraryWrapper> libraries, Hashtable<String, ServiceWrapper> services) throws IOException, ClassNotFoundException, ConfigurationException, DeploymentException {
    ConfigurationHandler handler = null;
    SCRepository componentsRepository;
    try {
      long time = System.currentTimeMillis();
      handler = configContext.getConfigurationHandler();
      Configuration root = getConfiguration(handler, CURRENT_INSTANCE_ROOT, ConfigurationHandler.READ_ACCESS);
      Configuration cfg = root.getSubConfiguration("cfg");
      InputStream is = null;
      try {
        if (cfg.existsFile("components.properties")) {
          is = cfg.getFile("components.properties");
          ObjectInputStream ois = new ObjectInputStream(is);
          componentsRepository = (SCRepository) ois.readObject();
        } else {
          componentsRepository = SCRepositoryRegenerator.regenerate(cfg);
        }
      } finally {
        if (is != null) is.close();
      }
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Component properties created for " + (System.currentTimeMillis() - time) + "ms.");
      }
      time = System.currentTimeMillis();
      for (Object propsObj : componentsRepository.getComponentsByType(OfflineComponentDeploy.TYPE_INTERFACE).values()) {
        InterfaceWrapper wrapper = new InterfaceWrapper(memoryContainer, (Properties) propsObj);
        interfaces.put(wrapper.getComponentName(), wrapper);
      }
      for (Object propsObj : componentsRepository.getComponentsByType(OfflineComponentDeploy.TYPE_LIBRARY).values()) {
        LibraryWrapper wrapper = new LibraryWrapper(memoryContainer, (Properties) propsObj);
        libraries.put(wrapper.getComponentName(), wrapper);
      }
      for (Object propsObj : componentsRepository.getComponentsByType(OfflineComponentDeploy.TYPE_SERVICE).values()) {
        ServiceWrapper wrapper = new ServiceWrapper(memoryContainer, (Properties) propsObj);
        services.put(wrapper.getComponentName(), wrapper);
      }
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Create component wrappers for " + (System.currentTimeMillis() - time) + "ms.");
      }
    } finally {
      closeConfigurationHandler(handler);
    }
  }

  ComponentWrapper createComponent(String componentName, byte componentType) throws ServiceException {
    ConfigurationHandler handler = null;
    try {
      handler = configContext.getConfigurationHandler();
      Configuration root = getConfiguration(handler, getComponentRootPath(componentName, componentType), ConfigurationHandler.READ_ACCESS);
      Properties componentProperties = root.getPropertySheetInterface().getProperties();
      return createComponentWrapper(componentProperties, componentType);
    } catch (ConfigurationException e) {
      Object[] param = new Object[] {getComponentTypeAsString(componentType), componentName};
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000023", "Error creating [{0}] [{1}]", param);
      }
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.ERROR_CREATING_COMPONENT), param), e);
    } finally {
      closeConfigurationHandler(handler);
    }
  }

  private String getComponentRootPath(String componentName, byte componentType) {
    switch (componentType) {
      case ComponentWrapper.TYPE_INTERFACE : return CURRENT_INSTANCE_ROOT + "/cfg/interfaces/" + componentName + "/provider";
      case ComponentWrapper.TYPE_LIBRARY : return CURRENT_INSTANCE_ROOT + "/cfg/ext/" + componentName + "/provider";
      case ComponentWrapper.TYPE_SERVICE : return CURRENT_INSTANCE_ROOT + "/cfg/services/" + componentName + "/provider";
      default : return null;
    }
  }

  private ComponentWrapper createComponentWrapper(Properties properties, byte componentType) {
    switch (componentType) {
      case ComponentWrapper.TYPE_INTERFACE : return new InterfaceWrapper(memoryContainer, properties);
      case ComponentWrapper.TYPE_LIBRARY : return new LibraryWrapper(memoryContainer, properties);
      case ComponentWrapper.TYPE_SERVICE : return new ServiceWrapper(memoryContainer, properties);
      default : return null;
    }
  }

  ////////////////////////////////////// READ PROPERTIES FROM DB ///////////////////////////////////////////////////////

  public NestedProperties getComponentProperties(String name, boolean isManager, boolean onlineModifiable) throws ServiceException {
    NestedProperties result = new NestedProperties();
    try {
      ComponentProperties componentProperties;
      if (isManager) {
        componentProperties = instanceLevel.getManagerAccess().getProperties(name, true);
      } else {
        componentProperties = instanceLevel.getComponentAccess().getServiceProperties(name, true);
      }
      initComponentProperties(componentProperties, result, onlineModifiable);
    } catch (Exception e) {
      Object[] param = new Object[] {name, ((isManager) ? "manager" : "service"), e.getMessage()};
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000024",
            "Component [{0}] [{1}] properties read error. Detailed info is: [{2}]",
            param);
      }
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.PROPERTIES_READ_ERROR), param), e);
    }
    return result;
  }

  private void initComponentProperties(ComponentProperties componentProperties, NestedProperties properties, boolean onlineModifiable) throws ConfigurationException, ClusterConfigurationException {
    PropertyEntry[] entries = componentProperties.getPropertySheet().getAllPropertyEntries();
    for (PropertyEntry entry : entries) {
      if (onlineModifiable) {
        //initialize online modifiable only
        int flags = (entry.getCustomFlags() == -1) ? entry.getDefaultFlags() : entry.getCustomFlags();
        if ((flags & PropertyEntry.ENTRY_TYPE_ONLINE_MODIFIABLE) == PropertyEntry.ENTRY_TYPE_ONLINE_MODIFIABLE) {
          properties.setProperty(entry.getName(), entry.getValue().toString());
        }
      } else {
        //initialize all properties
        properties.setProperty(entry.getName(), entry.getValue().toString());
      }
    }
    String[] names = componentProperties.getAllNestedPropertiesNames();
    for (String name : names) {
      ComponentProperties nestedComponentProperties = componentProperties.getNestedProperties(name);
      NestedProperties nestedProperties = properties.getNestedProperties(name, true);
      initComponentProperties(nestedComponentProperties, nestedProperties, onlineModifiable);
    }
  }

  /**
   * configuration library must be used
   */
  public Properties getComponentProperties(String name, boolean isGlobal, boolean isDefault, boolean isManager) throws ServiceException {
    Properties result = new Properties();
    ConfigurationLevel level = isGlobal ? templateLevel : instanceLevel;
    try {
      ComponentProperties cp;
      if (isManager) {
        cp = level.getManagerAccess().getProperties(name, true);
      } else {
        cp = level.getComponentAccess().getServiceProperties(name, true);
      }
      PropertyEntry[] propertyEntries = cp.getPropertySheet().getAllPropertyEntries();
      for (PropertyEntry propertyEntry : propertyEntries) {
        if (!isGlobal) {
          //collect local values only
          if (propertyEntry.isInherited()) {
            continue;
          }
        }
        if (isDefault) {
          result.setProperty(propertyEntry.getName(), propertyEntry.getDefault().toString());
        } else {
          if (propertyEntry.getCustom() != null) {
            result.setProperty(propertyEntry.getName(), propertyEntry.getCustom().toString());
          }
        }
      }
    } catch (Exception e) {
      Object[] param = new Object[] {name, ((isManager) ? "manager" : "service"), e.getMessage()};
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000025",
            "Component [{0}] [{1}] properties read error. Detailed info is: [{2}]",
            param);
      }
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.PROPERTIES_READ_ERROR), param), e);
    }
    return result;
  }

  /**
   * configuration library must be used
   */
  public Set getSecuredPropertiesKeys(String name, boolean isGlobal, boolean isManager) throws ServiceException {
    Set<String> result = new HashSet<String>();
    ConfigurationLevel level = isGlobal ? templateLevel : instanceLevel;
    try {
      ComponentProperties cp;
      if (isManager) {
        cp = level.getManagerAccess().getProperties(name, true);
      } else {
        cp = level.getComponentAccess().getServiceProperties(name, true);
      }
      PropertyEntry[] propertyEntries = cp.getPropertySheet().getAllPropertyEntries();
      for (PropertyEntry propertyEntry : propertyEntries) {
        if (!isGlobal) {
          //collect local values only
          if (propertyEntry.isInherited()) {
            continue;
          }
        }
        if (propertyEntry.isSecure()) {
          result.add(propertyEntry.getName());
        }
      }
    } catch(Exception e) {
      Object[] param = new Object[] {name, ((isManager) ? "manager" : "service")};
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000026",
            "Component [{0}] [{1}] secured property keys read error", param);
      }
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.SECURED_KEYS_READ_ERROR), param), e);
    }
    return result;
  }

  ////////////////////////////////////// STORE PROPERTIES IN DB ////////////////////////////////////////////////////////

  /**
   * use configuration library
   */
  public void storeComponentProps(String name, Properties properties, boolean isGlobal, boolean isDefault, Set secured, boolean isManager) throws ServiceException {
    ConfigurationLevel level = isGlobal ? templateLevel : instanceLevel;
    ComponentProperties cp = null;
    try {
      if (isManager) {
        cp = level.getManagerAccess().getProperties(name, false);
      } else {
        cp = level.getComponentAccess().getServiceProperties(name, false);
      }
      PropertySheet props = cp.getPropertySheet();
      for (Object keyObj : properties.keySet()) {
        String key = (String) keyObj;
        String value = properties.getProperty(key);
        PropertyEntry entry;
        try {
          entry = props.getPropertyEntry(key);
          if (secured != null && secured.contains(key) && !entry.isSecure()) {
            entry.setSecure(true);
          }
          if (isDefault) {
            entry.setDefault(value);
          } else {
            entry.setValue(value);
          }
        } catch (NameNotFoundException e) {
          //$JL-EXC$
          if (secured != null && secured.contains(key)) {
            entry = props.createSecurePropertyEntry(key, value, null);
          } else {
            entry = props.createPropertyEntry(key, value, null);
          }
          if (!isDefault) {
            entry.setValue(value);
          }
        }
      }
      cp.applyChanges();
    } catch (Exception e) {
      if (cp != null) {
        try {
          cp.discardChanges();
        } catch (ClusterConfigurationException cce) {
          if (LOCATION.beWarning()) {
            LOCATION.traceThrowableT(Severity.WARNING, ResourceUtils.getString(ResourceUtils.CANT_DISCARD_CHANGES), cce);
          }
        }
      }
      Object[] param = new Object[] {name, ((isManager) ? "manager" : "service")};
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000027", "Component [{0}] [{1}] property store error",
            param);
      }
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.PROPERTIES_STORE_ERROR), param), e);
    }
  }

  ////////////////////////////////////// REMOVE PROPERTIES FROM DB /////////////////////////////////////////////////////

  /**
   * it's not possible to remove properties because it's always inherit form globals configurations!
   * properties are removed from current instance and it's parent only
   *
   * use configuration library
   */
  public void removeComponentProperties(String name, String[] keys, boolean isGlobal, boolean isManager) throws ServiceException {
    ConfigurationLevel level = isGlobal ? templateLevel : instanceLevel;
    ComponentProperties cp = null;
    try {
      if (isManager) {
        cp = level.getManagerAccess().getProperties(name, false);
      } else {
        cp = level.getComponentAccess().getServiceProperties(name, false);
      }
      PropertySheet props = cp.getPropertySheet();
      for (String key : keys) {
        props.deletePropertyEntry(key);
      }
      cp.applyChanges();
    } catch (Exception e) {
      if (cp != null) {
        try {
          cp.discardChanges();
        } catch (ClusterConfigurationException cce) {
          if (LOCATION.beWarning()) {
            LOCATION.traceThrowableT(Severity.WARNING, ResourceUtils.getString(ResourceUtils.CANT_DISCARD_CHANGES), cce);
          }
        }
      }
      Object[] param = new Object[] {name, ((isManager) ? "manager" : "service")};
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000028",
            "Component [{0}] [{1}] property delete error", param);
      }
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.PROPERTIES_DELETE_ERROR), param), e);
    }
  }

  ////////////////////////////////////// RESTORE PROPERTIES IN DB //////////////////////////////////////////////////////

  /**
   * 1. if keys != null -> result[0] - non-secured keys; result[1] - secured keys
   * 2. if keys == null -> result[0] - restore nameset
   *
   * use configuration library
   */
  public Set[] restoreComponentProperties(String name, boolean isGlobal, String[] keys, boolean isManager) throws ServiceException {
    Set[] result;
    ConfigurationLevel level = isGlobal ? templateLevel : instanceLevel;
    ComponentProperties cp = null;
    try {
      if (isManager) {
        cp = level.getManagerAccess().getProperties(name, false);
      } else {
        cp = level.getComponentAccess().getServiceProperties(name, false);
      }
      PropertySheet props = cp.getPropertySheet();
      PropertyEntry[] entries = props.getAllPropertyEntries();
      if (keys != null) {
        result = new Set[2];
        Set<String> secKeyset = new HashSet<String>();
        Set<String> nonSecKeyset = new HashSet<String>();
        result[0] = secKeyset; //secured keyset
        result[1] = nonSecKeyset; //non secured keyset
        for (String key : keys) {
          PropertyEntry entry = props.getPropertyEntry(key);
          if (entry.isSecure()) {
            secKeyset.add(key);
          } else {
            nonSecKeyset.add(key);
          }
          if (!entry.isInherited()) {//restore only local properties
            entry.restoreDefault();
          }
        }
      } else {
        result = new Set[1];
        Set<String> restore = new HashSet<String>();
        result[0] = restore; //restore nameset
        for (PropertyEntry entry : entries) {
          if (!entry.isInherited()) {//restore only local properties
            entry.restoreDefault();
            restore.add(entry.getName());
          }
        }
      }
      cp.applyChanges();
    } catch (Exception e) {
      if (cp != null) {
        try {
          cp.discardChanges();
        } catch (ClusterConfigurationException cce) {
          if (LOCATION.beWarning()) {
            LOCATION.traceThrowableT(Severity.WARNING, ResourceUtils.getString(ResourceUtils.CANT_DISCARD_CHANGES), cce);
          }
        }
      }
      Object[] param = new Object[] {name, ((isManager) ? "manager" : "service")};
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000029",
            "Component [{0}] [{1}] property restore error", param);
      }
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.PROPERTIES_DELETE_ERROR), param), e);
    }
    return result;
  }

  ////////////////////////////////////// READ ALL PROPERTIES FROM DB ///////////////////////////////////////////////////

  /**
   * @deprecated
   *
   * map[0] manager_name --> default properties; map[1] manager_name --> custom properties; map[2] manager_name --> secured keys
   * map[3] service_name --> default properties; map[4] service_name --> custom properties; map[5] service_name --> secured keys
   */
  Map[] getGlobalProperties() throws ConfigurationException {
    HashMap<String, Properties> mngDefProps = new HashMap<String, Properties>();
    HashMap<String, Properties> mngCusProps = new HashMap<String, Properties>();
    HashMap<String, HashSet> mngSecKeys = new HashMap<String, HashSet>();
    HashMap<String, Properties> srvDefProps = new HashMap<String, Properties>();
    HashMap<String, Properties> srvCusProps = new HashMap<String, Properties>();
    HashMap<String, HashSet> srvSecKeys = new HashMap<String, HashSet>();
    Map[] result = new Map[] {mngDefProps, mngCusProps, mngSecKeys, srvDefProps, srvCusProps, srvSecKeys};
    try {
      ComponentProperties componentProperties;
      ManagerHandler managerHandler = templateLevel.getManagerAccess();
      String[] managerNames = managerHandler.listManagerNames();
      for (String managerName : managerNames) {
        componentProperties = managerHandler.getProperties(managerName, true);
        PropertySheet props = componentProperties.getPropertySheet();
        PropertyEntry[] propertyEntries = props.getAllPropertyEntries();
        Properties mngDefaultProperties = new Properties();
        Properties mngCustomProperties = new Properties();
        HashSet<String> mngSecuredKeys = new HashSet<String>();
        for (PropertyEntry propertyEntry : propertyEntries) {
          mngDefaultProperties.setProperty(propertyEntry.getName(), propertyEntry.getDefault().toString());
          if (propertyEntry.getCustom() != null) {
            mngCustomProperties.setProperty(propertyEntry.getName(), propertyEntry.getCustom().toString());
          }
          if (propertyEntry.isSecure()) {
            mngSecuredKeys.add(propertyEntry.getName());
          }
        }
        mngDefProps.put(managerName, mngDefaultProperties);
        mngCusProps.put(managerName, mngCustomProperties);
        mngSecKeys.put(managerName, mngSecuredKeys);
      }
      ComponentHandler componentHandler = templateLevel.getComponentAccess();
      String[] serviceNames = componentHandler.listComponentNames(ComponentHandler.TYPE_SERVICE);
      for (String serviceName : serviceNames) {
        if (memoryContainer.getServices().containsKey(serviceName)) {
          componentProperties = componentHandler.getServiceProperties(serviceName, true);
          PropertySheet props = componentProperties.getPropertySheet();
          PropertyEntry[] propertyEntries = props.getAllPropertyEntries();
          Properties serviceDefaultProperties = new Properties();
          Properties serviceCustomProperties = new Properties();
          HashSet<String> serviceSecuredKeys = new HashSet<String>();
          for (PropertyEntry propertyEntry : propertyEntries) {
            serviceDefaultProperties.setProperty(propertyEntry.getName(), propertyEntry.getDefault().toString());
            if (propertyEntry.getCustom() != null) {
              serviceCustomProperties.setProperty(propertyEntry.getName(), propertyEntry.getCustom().toString());
            }
            if (propertyEntry.isSecure()) {
              serviceSecuredKeys.add(propertyEntry.getName());
            }
          }
          srvDefProps.put(serviceName, serviceDefaultProperties);
          srvCusProps.put(serviceName, serviceCustomProperties);
          srvSecKeys.put(serviceName, serviceSecuredKeys);
        }
      }
    } catch (ClusterConfigurationException e) {
      throw new ConfigurationException(e);
    }
    return result;
  }

  /////////////////////////////////// UPLOAD AND REMOVE COMPONENTS /////////////////////////////////////////////////////

  String deployComponentInDB(File jar, byte componentType) throws ServiceException {
    OfflineComponentDeploy deploy;
    try {
      deploy = OCDFactory.createOfflineComponentDeploy(configContext, LOCATION, getSystemDatasource());
      ResultStatus status = deploy.deployComponent(jar, getDeployType(componentType));
      if (status.getStatus() == ResultStatus.WARNING) {
        if (LOCATION.beWarning()) {
          String[] list = status.getWarnings();
          for (String str : list) {
            LOCATION.warningT(str);
          }
        }
      }
      return status.getRuntimeName();
    } catch (DeploymentException e) {
      Object[] param = new Object[] {getComponentTypeAsString(componentType), jar.getName()};
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.krn_srv.000030", "Deploy failed for [{0}] with SDA [{1}]", param);
      }
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.DEPLOY_FAILED), param), e);
    }
  }

  String removeComponentFromDB(String providerName, String componentName, byte componentType) throws ServiceException, ComponentNotDeployedException {
    OfflineComponentDeploy deploy;
    try {
      deploy = OCDFactory.createOfflineComponentDeploy(configContext, LOCATION, getSystemDatasource());
      ResultStatus status = deploy.undeployComponent(providerName, componentName, getDeployType(componentType));
      if (status.getStatus() == ResultStatus.WARNING) {
        if (LOCATION.beWarning()) {
          String[] list = status.getWarnings();
          for (String str : list) {
            LOCATION.warningT(str);
          }
        }
      }
      return status.getRuntimeName();
    } catch (com.sap.engine.tools.offlinedeploy.rdb.ComponentNotDeployedException e) {
      throw new ComponentNotDeployedException(LOCATION, e);
    } catch (DeploymentException e) {
      Object[] param = new Object[] {getComponentTypeAsString(componentType), providerName + "~" + componentName};
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.krn_srv.000031", "Undeploy failed for [{0}] [{1}]", param);
      }
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.UNDEPLOY_FAILED), param), e);
    }
  }

  private DataSource getSystemDatasource() {
    DatabaseManager dbManager = (DatabaseManager)Framework.getManager(Names.DATABASE_MANAGER);

    if (dbManager == null) {
      SimpleLogger.trace(Severity.WARNING, LOCATION, "Cannot get the Database Manager");
      return null;
    }

    try {
      return dbManager.getSystemDataSource();
    } catch (DatabaseException e) {
      if (SimpleLogger.isWritable(Severity.WARNING, LOCATION)) {
        SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, "Cannot get the default datasource", e);
      }
      return null;
    }
  }

  private byte getDeployType(byte componentType) {
    switch (componentType) {
      case ComponentWrapper.TYPE_INTERFACE : return OfflineComponentDeploy.TYPE_INTERFACE;
      case ComponentWrapper.TYPE_LIBRARY : return OfflineComponentDeploy.TYPE_LIBRARY;
      case ComponentWrapper.TYPE_SERVICE : return OfflineComponentDeploy.TYPE_SERVICE;
      default : return OfflineComponentDeploy.TYPE_LIBRARY;
    }
  }

  private String getComponentTypeAsString(byte type) {
    switch (type) {
      case ComponentWrapper.TYPE_INTERFACE : return "interface";
      case ComponentWrapper.TYPE_LIBRARY : return "library";
      case ComponentWrapper.TYPE_SERVICE : return "service";
      default : return "";
    }
  }

  void synchBinaries() throws ServiceException {
    Manager bootstrap = Manager.getInstance(configContext);
    byte result = bootstrap.synchBinaries();
    //using bootstrap synchronization
    if (result != Manager.FINISHED_OK) {
      Enumeration enumeration = bootstrap.getExceptions();
      while (enumeration.hasMoreElements()) {
        ThrowableEntry entry = (ThrowableEntry) enumeration.nextElement();
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000032", "Error synchronizing binaries");
        SimpleLogger.traceThrowable(entry.getSeverity(), LOCATION,
            entry.getMessage(), entry.getThrowable());
      }
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.ERROR_SYNCHRONIZING_BINARIES)));
    }
  }

  /////////////////////////////////// GET & CLOSE CFG HANDLERS /////////////////////////////////////////////////////////

  private Configuration getConfiguration(ConfigurationHandler handler, String configurationName, int access) throws ConfigurationException {
    Configuration result = null;
    int count = 1800;
    boolean success = false;
    while (!success) {
      try {
        result = handler.openConfiguration(configurationName, access);
        success = true;
      } catch (ConfigurationLockedException e) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException ie) {
          // $JL-EXC$
        }
        if (count-- == 0) {
          if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
            SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
                "ASJ.krn_srv.000033",
                "Configuration [{0}] is locked. Detailed info is: [{1}]",
                new Object[] {configurationName, e.toString()});
          }
          throw e;
        }
      }
    }
    return result;
  }

  private void closeConfigurationHandler(ConfigurationHandler handler) {
    try {
      if (handler != null) {
        handler.closeAllConfigurations();
      }
    } catch (ConfigurationException e) {
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000034",
            "Error while [handler.closeAllConfigurations()] called. Detailed info is [{0}]",
                e.toString());
      }
      if(SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
            "Error while [handler.closeAllConfigurations()] called. " +
              "Detailed info is [" + e.toString() + "]", e);
      }
    }
  }

}