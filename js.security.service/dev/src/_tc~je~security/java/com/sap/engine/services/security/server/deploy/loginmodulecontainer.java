/**
 * Copyright (c) 2007 by SAP Labs Bulgaria,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 *
 */

/*
 * Created on 2007-3-1 by I027020
 */

package com.sap.engine.services.security.server.deploy;

import java.io.File;
import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.core.CoreContext;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.load.LoadContext;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;
import com.sap.engine.services.deploy.container.AdditionalAppInfo;
import com.sap.engine.services.deploy.container.ApplicationDeployInfo;
import com.sap.engine.services.deploy.container.ContainerDeploymentInfo;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterfaceExtension;
import com.sap.engine.services.deploy.container.DeployCommunicator;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.FileUpdateInfo;
import com.sap.engine.services.deploy.container.ProgressListener;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.container.op.start.ApplicationStartInfo;
import com.sap.engine.services.deploy.container.op.start.ContainerStartInfo;
import com.sap.engine.services.deploy.container.op.util.StartUp;
import com.sap.engine.services.security.server.ConfigurationLock;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * @author Ralin Chimev
 *
 * 
 */

public class LoginModuleContainer implements ContainerInterfaceExtension {

  private static final String LOGIN_MODULE_CONTAINER_NAME = "com.sap.security.login-modules";
  private static final String LOGIN_MODULE_CONTAINER_FILE_EXTENSION = ".xml";
  public static final String SECURITY_SERVICE = "security";
  public static final String CONFIGURATION_XML_FILE = "LoginModuleConfiguration" + LOGIN_MODULE_CONTAINER_FILE_EXTENSION;
  public static final String PREVIOUS_DEPLOYMENT_CONFIGURATION_XML_FILE = "PreviousDeploymentLoginModuleConfiguration" + LOGIN_MODULE_CONTAINER_FILE_EXTENSION;
  public static final String META_INF_CONFIGURATION_XML_FILE = "META-INF/" + CONFIGURATION_XML_FILE;
  public static final String LOGIN_MODULES_CONFIGURATION_NAME = "login-module";
  public static final String LOGIN_MODULE_CONTAINER_LOCATION = AuthenticationTraces.ROOT_LOCATION + ".LoginModuleContainer";
  public static final String LOGIN_MODULE_CONTAINER_CATEGORY = "Deploy";

  private static final Location LOCATION = Location.getLocation(LOGIN_MODULE_CONTAINER_LOCATION);
  private static final Category CATEGORY = Category.getCategory(Category.SYS_SECURITY, LOGIN_MODULE_CONTAINER_CATEGORY);

  private ContainerInfo containerInfo;
  private ApplicationServiceContext serviceContext;
  private DeployCommunicator communicator;
  private ConfigurationLock configurationLock = new ConfigurationLock();
  private Hashtable<String, HashMap<String, String>> pendingApplications = null;
  
  public LoginModuleContainer(ApplicationServiceContext serviceContext) {    
    this.serviceContext = serviceContext;
    containerInfo = new ContainerInfo();
    containerInfo.setServiceName(serviceContext.getServiceState().getServiceName());
    containerInfo.setName(LOGIN_MODULE_CONTAINER_NAME);
    containerInfo.setFileNames(new String[] {CONFIGURATION_XML_FILE, META_INF_CONFIGURATION_XML_FILE}); 
    containerInfo.setJ2EEContainer(false);
    containerInfo.setSupportingLazyStart(true);
    containerInfo.setModuleName(LoginModuleContainer.class.getName());
    containerInfo.setSupportingParallelism(true); 
    containerInfo.setPriority(ContainerInfo.MAX_PRIORITY);
    
    pendingApplications = new Hashtable<String, HashMap<String, String>>();
  }

  /**
   * @see com.sap.engine.services.deploy.container.ContainerInterface#getContainerInfo()
   */
  public ContainerInfo getContainerInfo() {
    return containerInfo;
  }

  /** 
   * @see com.sap.engine.services.deploy.container.ContainerInterface#getApplicationName(java.io.File)
   */
  public String getApplicationName(File arg0) throws DeploymentException {
    return containerInfo.getName();
  }

  /*
   * Tries to loads the login module class with the deployed application class loader
   */
  public Class getLoginModuleClass(String loginModuleClassName) throws ClassNotFoundException {

    final String METHOD_NAME = "getLoginModuleClass";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {loginModuleClassName});
    }

    CoreContext coreContext = serviceContext.getCoreContext();
    LoadContext classLoaderContext = coreContext.getLoadContext();
    
    if (classLoaderContext != null) {
      String[] myApplications = communicator.getMyApplications();
      for ( int i = 0; i < myApplications.length; i++ ) {
        String applicationName = myApplications[i];
        ClassLoader applicationClassLoader = classLoaderContext.getClassLoader(applicationName);

        if (applicationClassLoader == null) {
          startLazyApplication(applicationName);
          applicationClassLoader = classLoaderContext.getClassLoader(applicationName);
        }

        if (applicationClassLoader != null) {
          try {
            Class loginModuleClass = applicationClassLoader.loadClass(loginModuleClassName);	        

            if (LOCATION.beInfo()) {
              LOCATION.infoT("Application [{0}] has loaded login module class [{1}] with class loader [{2}]", new Object[] {applicationName, loginModuleClassName, applicationClassLoader});
            }	        

            if (LOCATION.bePath()) {
              LOCATION.exiting(METHOD_NAME, loginModuleClass.getName());
            }
            return loginModuleClass;

          } catch (ClassNotFoundException ex) {
            if (LOCATION.beDebug()) {
              LOCATION.debugT("Application [{0}] cannot load login module class [{1}] with class loader [{2}]", new Object[] {applicationName, loginModuleClassName, applicationClassLoader});
            }
          }
        } 
      }
    }    

    SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.secsrv.000090", "No application classloader can load login module class: {0}. " +
    		"Probably application that deployed the login module is stopped and cannot be started.", 
    		new Object[] {loginModuleClassName});

    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, null);
    }

    throw new ClassNotFoundException("No application classloader can load login module class: " + loginModuleClassName);
  }

  /* 
   * Starts the application if it is lazy and returns its class loader.
   */
  private void startLazyApplication(String applicationName) {
    final String METHOD_NAME = "startLazyApplication";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {applicationName});
    }

    try {
      int applicationStartUpMode = communicator.getStartUpMode(applicationName);
      int lazyMode = StartUp.LAZY.getId().intValue();
      
      // Start application only if it is lazy
      if (applicationStartUpMode == lazyMode) {
        communicator.startApplicationAndWait(applicationName);
        
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Application [{0}] successfully started.", new Object[] {applicationName});
        }
      } else {
        if (LOCATION.beWarning()) {
          LOCATION.warningT("Application [{0}] is not lazy and cannot be started. Class loader for the application is not available.", 
              new Object[] {applicationName});
        }
      }
    } catch(WarningException e) {
      SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, "ASJ.secsrv.000166", "Exception occurred during lazy application start up.", e);
    } catch(RemoteException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000166", "Exception occurred during lazy application start up.", e);
    }

    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  /** 
   * @see com.sap.engine.services.deploy.container.ContainerInterface#deploy(java.io.File[], com.sap.engine.services.deploy.container.ContainerDeploymentInfo, java.util.Properties)
   */
  public ApplicationDeployInfo deploy(File[] files, ContainerDeploymentInfo containerDeploymentInfo, Properties properties) throws DeploymentException {

    final String METHOD_NAME = "deploy";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {files, containerDeploymentInfo, properties});
    }

    File xmlFile = getConfigurationXmlFile(files);
    if (xmlFile == null) {
      if (LOCATION.beDebug()) {
        LOCATION.debugT("No configuration xml file found that describes login module configurations being deployed.");
      }
      if (LOCATION.bePath()) {
        LOCATION.exiting(METHOD_NAME, null);
      } 
      return null;
    }
    
    LoginModuleConfiguration[] lmcToDeploy = null;
    try {
      lmcToDeploy = DeployUtil.readLoginModules(new FileInputStream(xmlFile));
    } catch (Exception e) {
      throw new DeploymentException("Error reading login modules from configuration xml file.", e);
    }

    executePrechecks(files, xmlFile, lmcToDeploy);    

    executeDeploy(xmlFile, containerDeploymentInfo, lmcToDeploy);
    
    ApplicationDeployInfo deployInfo = new ApplicationDeployInfo();    
    deployInfo.setDeployedComponentNames(new String[] { xmlFile.getName() });

    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, deployInfo);
    } 
    return deployInfo;
  }

  private void executeDeploy(File file, ContainerDeploymentInfo containerDeploymentInfo, LoginModuleConfiguration[] lmcToDeploy) throws DeploymentException {

    final String METHOD_NAME = "executeDeploy";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {file, containerDeploymentInfo});
    }

    boolean check = DeployUtil.checkDisplayNameInUserstore(lmcToDeploy);
    if (!check) {
      throw new DeploymentException("Deploy failed because login module configuration with the same display name as the login module configuration described in the configuration xml file already exists in the userstore.");
    }

    check = DeployUtil.checkClassNameInUserstore(lmcToDeploy);
    if (!check) {
      throw new DeploymentException("Deploy failed because login module configuration with the same class name as the login module configuration described in the configuration xml file already exists in the userstore.");
    }

    addPendingApplication(containerDeploymentInfo, lmcToDeploy);
    
    try {

      for ( LoginModuleConfiguration lmc : lmcToDeploy) {
        DeployUtil.storeLoginModule(lmc, containerDeploymentInfo.getConfiguration(), configurationLock);
      }

      DeployUtil.storeLoginModuleConfigurationXmlFile(containerDeploymentInfo.getConfiguration(), file);

      LOCATION.infoT("Deploy of application {0} finished successfully.", new Object[] {containerDeploymentInfo.getApplicationName()});

    } catch (Exception ex) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000167", "Saving login module configuration in userstore failed.", ex);
      throw new DeploymentException("Saving login module configuration in userstore failed.", ex);
    }

    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  private void executePrechecks(File[] files, File xmlFile, LoginModuleConfiguration[] lmcToDeploy) throws DeploymentException {

    final String METHOD_NAME = "executePrechecks";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {files, xmlFile});
    }

    //	only one xml file per deploy component
    boolean check1 = DeployUtil.checkLoginModuleConfigurationXmlFile(files);
    if (!check1) {
      throw new DeploymentException("Only one configuration xml file can be used in login module deployment. Check security log for error details.");
    }

    //	only distinct CNs in xml file
    boolean check2 = DeployUtil.checkClassNameUniqueInXml(lmcToDeploy);
    if (!check2) {
      throw new DeploymentException("Login module class name is not unique in configuration xml file.  Check security log for error details.");
    }

    //	only distinct DNs in xml file
    boolean check3 = DeployUtil.checkDisplayNameUniqueInXml(lmcToDeploy);
    if (!check3) {
      throw new DeploymentException("Login module display name is not unique in configuration xml file.  Check security log for error details.");
    }

    //	only distinct DNs in xml file
    boolean check4 = DeployUtil.checkClassNameEqualDisplayName(lmcToDeploy);
    if (!check4) {
      throw new DeploymentException("Login module class name matches display name of another login module in configuration xml or userstore. Check security log for error details.");
    }
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  private File getConfigurationXmlFile(File[] files) {    
    for ( int i = 0 ; i < files.length ; i++ ) {
      String fileName = files[i].getName();
      if (fileName.equalsIgnoreCase(CONFIGURATION_XML_FILE)) {
        return files[i];
      }
    }    
    return null;
  }

  private boolean isSecurityComponent(File[] files) {
    return getConfigurationXmlFile(files) != null;
  }

  /**
   * @see com.sap.engine.services.deploy.container.ContainerInterface#notifyDeployedComponents(java.lang.String, java.util.Properties)
   */
  public void notifyDeployedComponents(String arg0, Properties arg1) throws WarningException {

  }

  /**
   * @see com.sap.engine.services.deploy.container.ContainerInterface#prepareDeploy(java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
   */
  public void prepareDeploy(String arg0, Configuration arg1) throws DeploymentException, WarningException {

  }

  /**
   * @see com.sap.engine.services.deploy.container.ContainerInterface#commitDeploy(java.lang.String)
   */
  public void commitDeploy(String applicationName) throws WarningException {
    DeployUtil.refreshUserstore();
    //remove application because it's already deployed
    pendingApplications.remove(applicationName);
  }

  /**
   * @see com.sap.engine.services.deploy.container.ContainerInterface#rollbackDeploy(java.lang.String)
   */
  public void rollbackDeploy(String applicationName) throws WarningException {
    //remove application because it must be rollbacked
    removePendingLoginModules(applicationName);
  }

  /**
   * @see com.sap.engine.services.deploy.container.ContainerInterface#needUpdate(java.io.File[], com.sap.engine.services.deploy.container.ContainerDeploymentInfo, java.util.Properties)
   */
  public boolean needUpdate(File[] files, ContainerDeploymentInfo arg1, Properties arg2) throws DeploymentException, WarningException {
    return isSecurityComponent(files);
  }

  /**
   * @see com.sap.engine.services.deploy.container.ContainerInterface#needStopOnUpdate(java.io.File[], com.sap.engine.services.deploy.container.ContainerDeploymentInfo, java.util.Properties)
   */
  public boolean needStopOnUpdate(File[] arg0, ContainerDeploymentInfo arg1, Properties arg2) throws DeploymentException, WarningException {
    return false;
  }

  /**
   * @see com.sap.engine.services.deploy.container.ContainerInterface#makeUpdate(java.io.File[], com.sap.engine.services.deploy.container.ContainerDeploymentInfo, java.util.Properties)
   */
  public ApplicationDeployInfo makeUpdate(File[] files, ContainerDeploymentInfo containerDeploymentInfo, Properties props) throws DeploymentException {

    final String METHOD_NAME = "makeUpdate";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {files, containerDeploymentInfo, props});
    }

    File xmlFile = getConfigurationXmlFile(files);
    if (xmlFile == null) {
      SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.secsrv.000170", "No configuration xml file found that describes login module configurations being deployed.");
      if (LOCATION.bePath()) {
        LOCATION.exiting(METHOD_NAME, null);
      }
      return null;
    }

    LoginModuleConfiguration[] lmcToDeploy = null;
    try {
      lmcToDeploy = DeployUtil.readLoginModules(new FileInputStream(xmlFile));
    } catch (Exception e) {
      throw new DeploymentException("Error reading login modules from configuration xml file.", e);
    }
    
    executePrechecks(files, xmlFile, lmcToDeploy);

    executeUpdate(xmlFile, containerDeploymentInfo, lmcToDeploy);

    ApplicationDeployInfo deployInfo = new ApplicationDeployInfo();    
    deployInfo.setDeployedComponentNames(new String[] { xmlFile.getName() });

    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, deployInfo);
    }
    return deployInfo;
  }

  private void executeUpdate(File file, ContainerDeploymentInfo containerDeploymentInfo, LoginModuleConfiguration[] lmcToDeploy) throws DeploymentException {

    final String METHOD_NAME = "executeUpdate";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {file, containerDeploymentInfo});
    }

    try {

      Configuration appConfig = containerDeploymentInfo.getConfiguration();
      Configuration lmConfig = appConfig.getSubConfiguration(LOGIN_MODULES_CONFIGURATION_NAME);
      LoginModuleConfiguration[] lmcDeployed = DeployUtil.readLoginModuleConfigurtionsFromAppConfig(lmConfig);
      LoginModuleConfiguration[] lmcPrevXML = DeployUtil.readLoginModuleConfigurationFromXmlFile(containerDeploymentInfo.getConfiguration(), CONFIGURATION_XML_FILE);

      boolean check = DeployUtil.checkLoginModuleRelationsToPreviousXml(lmcDeployed, lmcToDeploy);
      if (!check) {
        throw new DeploymentException("Login module class name or display name matches two different login modules from previous deployment.");
      }

      check = DeployUtil.checkRenameDisplayName(lmcDeployed, lmcToDeploy);
      if (!check) {
        throw new DeploymentException("Login module was deployed with different display name during previous deployment. Renaming display names is not supported.");
      }

      for ( int i = 0 ; i < lmcToDeploy.length ; i++ ) {

        LoginModuleConfiguration lmcNewXML = lmcToDeploy[i];

        // CHECK: DN in US
        boolean checkDNinUS = DeployUtil.checkLoginModuleDisplayNameInUserstore(lmcNewXML);

        if (LOCATION.beInfo()) {
          LOCATION.infoT("Display name [{0}] exists in userstore. Result: [{1}]", new Object[] {lmcNewXML.getName(), new Boolean(checkDNinUS)});
        }

        if (!checkDNinUS) {           

          addPendingApplication(containerDeploymentInfo, lmcNewXML);
          
          LOCATION.infoT("Add login module with display name [{0}] in userstore", new Object[] {lmcNewXML.getName()});

          // Add LM in US
          DeployUtil.storeLoginModule(lmcNewXML, containerDeploymentInfo.getConfiguration(), configurationLock);
          

        } else { 

          LoginModuleConfiguration lmcUS = DeployUtil.findLoginModuleInPrevious(lmcNewXML.getName(), lmcDeployed);
          LoginModuleConfiguration lmcOrgXML = DeployUtil.findLoginModuleInPrevious(lmcNewXML.getName(), lmcPrevXML);

          if (lmcUS != null) { // otherwise normal deploy        

            if (LOCATION.beInfo()) {
              LOCATION.infoT("Login module matched by display name [{0}] in previous configuration.", new Object[] {lmcNewXML.getName()});
            }

            // RENAME CLASS NAME  
            boolean checkRenameCN = lmcUS.getLoginModuleClassName().equals(lmcNewXML.getLoginModuleClassName());

            if (LOCATION.beInfo()) {
              LOCATION.infoT("Login module [{0}] class name from previous xml EQUALS the login module [{1}] class name from new xml. Result: [{2}].", 
                  new Object[] {lmcUS.getName(), lmcNewXML.getName(), new Boolean(checkRenameCN)});
            }         

            // CHECK: rename CN        
            if (!checkRenameCN) {

              // Update CN in US
              DeployUtil.updateClassName(lmcUS.getLoginModuleClassName(), lmcNewXML, lmConfig);
              
              SimpleLogger.log(Severity.INFO, CATEGORY, LOCATION, "ASJ.secsrv.000091", "Class name from [{0}] to [{1}] of login module [{2}] in user store is updated.", 
                  new Object[] {lmcUS.getLoginModuleClassName(), lmcNewXML.getLoginModuleClassName(), lmcNewXML.getName()});

              // Update CN in PC
              DeployUtil.updateCNinPolicyConfigurations(lmcUS, lmcNewXML);
              
              SimpleLogger.log(Severity.INFO, CATEGORY, LOCATION, "ASJ.secsrv.000092", "Class name from [{0}] to [{1}] of login module [{2}] in policy configurations is updated.", 
                  new Object[] {lmcUS.getLoginModuleClassName(), lmcNewXML.getLoginModuleClassName(), lmcNewXML.getName()});
            }

            // RENAME OPTIONS
            boolean checkRenameOP = lmcOrgXML.getOptions().equals(lmcNewXML.getOptions());

            if (LOCATION.beInfo()) {
              LOCATION.infoT("Login module [{0}] options in original xml EQUAL login module [{1}] options from new xml. Result: [{2}].", 
                  new Object[] {lmcOrgXML.getName(), lmcNewXML.getName(), new Boolean(checkRenameOP)});
            }

            // CHECK: options in org xml differ than options in new xml
            if (!checkRenameOP) {

              // CHECK: options in userstore differ than options in new xml
              boolean checkUserChangedOptions = lmcUS.getOptions().equals(lmcOrgXML.getOptions());

              if (LOCATION.beInfo()) {
                LOCATION.infoT("Login module [{0}] options in userstore EQUAL login module [{1}] options from original xml. Result: [{2}].", 
                    new Object[] {lmcUS.getName(), lmcOrgXML.getName(), new Boolean(checkRenameOP)});
              }

              if (checkUserChangedOptions) {

                // Update OP
                DeployUtil.updateOptions(lmcNewXML, lmConfig, lmcOrgXML.getOptions());
                
                SimpleLogger.log(Severity.INFO, CATEGORY, LOCATION, "ASJ.secsrv.000093", "Options from {0} to {1} of login module {2} in user store are updated.", 
                    new Object[] {lmcUS.getOptions(), lmcNewXML.getOptions(), lmcNewXML.getName()});

                // Update OP in PC
                DeployUtil.updateOPinPolicyConfigurations(lmcNewXML, lmcOrgXML.getOptions());
                
                SimpleLogger.log(Severity.INFO, CATEGORY, LOCATION, "ASJ.secsrv.000094", "Options from {0} to {1} of login module {2} in policy configurations are updated.", 
                    new Object[] {lmcUS.getOptions(), lmcNewXML.getOptions(), lmcNewXML.getName()});

              } else {
                SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.secsrv.000095", "Options of login module [{0}] will not be updated as they are changed by the administrator in the user store.", 
                    new Object[] {lmcNewXML.getName()});
              }
            }
          }
        }

        // Re-Deploy
        SimpleLogger.log(Severity.INFO, CATEGORY, LOCATION, "ASJ.secsrv.000096", "Update of login module configuration [{0}] has finished successfully.", 
            new Object[] {lmcNewXML.getName()});
      }

      DeployUtil.storeLoginModuleConfigurationXmlFile(containerDeploymentInfo.getConfiguration(), file);

      LOCATION.infoT("Deploy of application {0} finished successfully.", new Object[] {containerDeploymentInfo.getApplicationName()});

    } catch (DeploymentException dex) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000167", "Error during update of application.", dex);
      throw dex;
    } catch (Exception ex) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000167", "Error during update of application.", ex);
      throw new DeploymentException("Error during update of application.", ex);
    } finally {
      if (LOCATION.bePath()) {
        LOCATION.exiting(METHOD_NAME);
      }
    }
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#notifyUpdatedComponents(java.lang.String, com.sap.engine.frame.core.configuration.Configuration, java.util.Properties)
   */
  public void notifyUpdatedComponents(String applicationName, Configuration applicationConfiguration, Properties properties) throws WarningException {
    final String METHOD_NAME = "notifyUpdatedComponents";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {applicationName, applicationConfiguration, properties});
    }

    updateRuntime(applicationName);
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, null);
    } 
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#prepareUpdate(java.lang.String)
   */
  public void prepareUpdate(String arg0) throws DeploymentException, WarningException {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#commitUpdate(java.lang.String)
   */
  public ApplicationDeployInfo commitUpdate(String applicationName) throws WarningException {
    final String METHOD_NAME = "commitUpdate";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {applicationName});
    }

    DeployUtil.refreshUserstore();
    
    updateRuntime(applicationName);
    
    pendingApplications.remove(applicationName);
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, null);
    }    
    return null;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#rollbackUpdate(java.lang.String, com.sap.engine.frame.core.configuration.Configuration, java.util.Properties)
   */
  public void rollbackUpdate(String applicationName, Configuration arg1, Properties arg2) throws WarningException {
  //remove application because it must be rollbacked
    removePendingLoginModules(applicationName);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#remove(java.lang.String)
   */
  public void remove(String arg0) throws DeploymentException, WarningException {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#downloadApplicationFiles(java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
   */
  public void downloadApplicationFiles(String arg0, Configuration arg1) throws DeploymentException, WarningException {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#prepareStart(java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
   */
  public void prepareStart(String arg0, Configuration arg1) throws DeploymentException, WarningException {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#commitStart(java.lang.String)
   */
  public void commitStart(String arg0) throws WarningException {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#rollbackStart(java.lang.String)
   */
  public void rollbackStart(String arg0) throws WarningException {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#prepareStop(java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
   */
  public void prepareStop(String arg0, Configuration arg1) throws DeploymentException, WarningException {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#commitStop(java.lang.String)
   */
  public void commitStop(String applicationName) throws WarningException {
    final String METHOD_NAME = "commitStop";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {applicationName});
    }

    updateRuntime(applicationName);
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#rollbackStop(java.lang.String)
   */
  public void rollbackStop(String arg0) throws WarningException {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#notifyRuntimeChanges(java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
   */
  public void notifyRuntimeChanges(String arg0, Configuration arg1) throws WarningException {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#prepareRuntimeChanges(java.lang.String)
   */
  public void prepareRuntimeChanges(String arg0) throws DeploymentException, WarningException {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#commitRuntimeChanges(java.lang.String)
   */
  public ApplicationDeployInfo commitRuntimeChanges(String arg0) throws WarningException {
    return null;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#rollbackRuntimeChanges(java.lang.String)
   */
  public void rollbackRuntimeChanges(String arg0) throws WarningException {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#getClientJar(java.lang.String)
   */
  public File[] getClientJar(String arg0) {
    return null;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#addProgressListener(com.sap.engine.services.deploy.container.ProgressListener)
   */
  public void addProgressListener(ProgressListener arg0) {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#removeProgressListener(com.sap.engine.services.deploy.container.ProgressListener)
   */
  public void removeProgressListener(ProgressListener arg0) {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#needStopOnSingleFileUpdate(com.sap.engine.services.deploy.container.FileUpdateInfo[], com.sap.engine.services.deploy.container.ContainerDeploymentInfo, java.util.Properties)
   */
  public boolean needStopOnSingleFileUpdate(FileUpdateInfo[] arg0, ContainerDeploymentInfo arg1, Properties arg2) throws DeploymentException, WarningException {
    return false;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#makeSingleFileUpdate(com.sap.engine.services.deploy.container.FileUpdateInfo[], com.sap.engine.services.deploy.container.ContainerDeploymentInfo, java.util.Properties)
   */
  public ApplicationDeployInfo makeSingleFileUpdate(FileUpdateInfo[] arg0, ContainerDeploymentInfo arg1, Properties arg2) throws DeploymentException {
    return null;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#notifySingleFileUpdate(java.lang.String, com.sap.engine.frame.core.configuration.Configuration, java.util.Properties)
   */
  public void notifySingleFileUpdate(String arg0, Configuration arg1, Properties arg2) throws WarningException {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#prepareSingleFileUpdate(java.lang.String)
   */
  public void prepareSingleFileUpdate(String arg0) throws DeploymentException, WarningException {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#commitSingleFileUpdate(java.lang.String)
   */
  public ApplicationDeployInfo commitSingleFileUpdate(String arg0) throws WarningException {
    return null;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#rollbackSingleFileUpdate(java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
   */
  public void rollbackSingleFileUpdate(String arg0, Configuration arg1) throws WarningException {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#applicationStatusChanged(java.lang.String, byte)
   */
  public void applicationStatusChanged(String arg0, byte arg1) {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#getResourcesForTempLoader(java.lang.String)
   */
  public String[] getResourcesForTempLoader(String applicationName) throws DeploymentException {
    return null;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#acceptedAppInfoChange(java.lang.String, com.sap.engine.services.deploy.container.AdditionalAppInfo)
   */
  public boolean acceptedAppInfoChange(String arg0, AdditionalAppInfo arg1) throws DeploymentException {
    return false;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#needStopOnAppInfoChanged(java.lang.String, com.sap.engine.services.deploy.container.AdditionalAppInfo)
   */
  public boolean needStopOnAppInfoChanged(String arg0, AdditionalAppInfo arg1) {
    return false;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#makeAppInfoChange(java.lang.String, com.sap.engine.services.deploy.container.AdditionalAppInfo, com.sap.engine.frame.core.configuration.Configuration)
   */
  public void makeAppInfoChange(String arg0, AdditionalAppInfo arg1, Configuration arg2) throws WarningException, DeploymentException {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#appInfoChangedCommit(java.lang.String)
   */
  public void appInfoChangedCommit(String arg0) throws WarningException {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#appInfoChangedRollback(java.lang.String)
   */
  public void appInfoChangedRollback(String arg0) throws WarningException {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#notifyAppInfoChanged(java.lang.String)
   */
  public void notifyAppInfoChanged(String arg0) throws WarningException {

  }

  public void setDeployCommunicator(DeployCommunicator communicator) {
    this.communicator = communicator;    
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterfaceExtension#notifyRemove(java.lang.String)
   */
  public void notifyRemove(String applicationName) throws WarningException {
    final String METHOD_NAME = "notifyRemove";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {applicationName});
    }
    
    updateRuntime(applicationName);
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME);  
    }
  }

  /**
   * Invoked on the server, which receives the request for removing an application. 
   * After that the application configuration is removed from DB and all other servers 
   * is invoked the method notifyRemove(String applicationName)
   * @see com.sap.engine.services.deploy.container.ContainerInterfaceExtension#remove(java.lang.String, com.sap.engine.frame.core.configuration.ConfigurationHandler, com.sap.engine.frame.core.configuration.Configuration)
   */

  public void remove(String applicationName, ConfigurationHandler operationHandler, Configuration appConfiguration) throws DeploymentException, WarningException {

    final String METHOD_NAME = "remove";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {applicationName, operationHandler, appConfiguration});
    }

    StringBuffer warnings = new StringBuffer();
    
    try {

      Configuration lmConfig = appConfiguration.getSubConfiguration(LOGIN_MODULES_CONFIGURATION_NAME);
      LoginModuleConfiguration[] lmcDeployed = DeployUtil.readLoginModuleConfigurtionsFromAppConfig(lmConfig);
      
      DeployUtil.cleanApplicationConfiguration(appConfiguration, configurationLock);

      String pcWarnings = DeployUtil.checkLoginModulesUsedInPolicyConfigurations(appConfiguration, lmcDeployed);
      if (pcWarnings != null) {
        warnings.append(pcWarnings);
      }

    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000168", "Error cleaning application configuration.", e);
      warnings.append("[ERROR]: Error cleaning application configuration. Exception message: " + e.getMessage());
    }

    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME);  
    }

    if (warnings.length() != 0) {
      LOCATION.warningT("Remove of application {0} finished successfully with warnings. \nWarnings: \n{1}", 
          new Object[] {applicationName, warnings.toString()});
      WarningException warningException = new WarningException("Deployed finished successfully with warnings.");
      warningException.addWarning(warnings.toString());
      throw warningException;
    } else {
      LOCATION.infoT("Remove of application {0} finished successfully.", new Object[] {applicationName});
    }
  }
  
  private void updateRuntime(String applicationName) {

    final String METHOD_NAME = "updateRuntime";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {applicationName});
    }

    try {
      
      Configuration applicationConfiguration = communicator.getAppConfigurationForReadAccess(applicationName);
      Configuration lmConfig = applicationConfiguration.getSubConfiguration(LOGIN_MODULES_CONFIGURATION_NAME);
      
      LoginModuleConfiguration[] lmcDeployed = DeployUtil.readLoginModuleConfigurtionsFromAppConfig(lmConfig);
      LoginModuleConfiguration[] lmcPrevXML = DeployUtil.readLoginModuleConfigurationFromXmlFile(applicationConfiguration, PREVIOUS_DEPLOYMENT_CONFIGURATION_XML_FILE);
      
      Set<String> names = new HashSet<String>();

      // Add the login module names deployed previously
      for (LoginModuleConfiguration lmc : lmcPrevXML) {
        String displayName = lmc.getName();
        String className = lmc.getLoginModuleClassName();
        names.add(displayName);
        names.add(className);
      }

      // Add the login module names updated now
      for (LoginModuleConfiguration lmc : lmcDeployed) {
        String displayName = lmc.getName();
        String className = lmc.getLoginModuleClassName();
        names.add(displayName);
        names.add(className);
      }
      
      // Add all login modules display names from UserStore which use the class name being updated
      LoginModuleConfiguration[] lmcUserstore = DeployUtil.getLoginModuleConfigurations();
      for (LoginModuleConfiguration lmc : lmcUserstore) {
        String displayName = lmc.getName();
        String className = lmc.getLoginModuleClassName();
        if (names.contains(className) && !names.contains(displayName)) {
          names.add(displayName); 
        }
      }

      if (LOCATION.beDebug()) {
        LOCATION.debugT("All policy configurations which use the deployed login modules will be invalidated to reload their login modules with the new application class loader.");
      }
      
      // Make all policy configurations that use these login modules either by class name or by display name
      // to be reloaded. That releases the class loaders for the custom login modules being updated.
      DeployUtil.reloadPolicyConfigurations(names);

    } catch(Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000169", "Error refreshing policy configurations.", e);
    } finally {
      if (LOCATION.bePath()) {
        LOCATION.exiting(METHOD_NAME);
      }
    }
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterfaceExtension#commitRemove(java.lang.String)
   */
  public void commitRemove(String applicationName) throws WarningException {    
    final String METHOD_NAME = "commitRemove";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME, new Object[] {applicationName});
    }

    DeployUtil.refreshUserstore();
    
    updateRuntime(applicationName);
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterfaceExtension#makeStartInitially(com.sap.engine.services.deploy.container.op.start.ContainerStartInfo)
   */
  public ApplicationStartInfo makeStartInitially(ContainerStartInfo arg0) throws DeploymentException {
    return null;
  }

  /**
   * This method checks if given login module name (display or class name) is in deployment and is not committed yet
   * @param lmName is a login module's display name or class name 
   * @return true if given login module name exists in the list of login modules in deployment
   */
  public boolean isLoginModuleInDeploy(String lmName) {
    Collection<HashMap<String, String>> entries = pendingApplications.values();
    HashMap<String, String> appLoginModules = null;
    
    for (Iterator<HashMap<String, String>> iterator = entries.iterator(); iterator.hasNext();) {
      appLoginModules = iterator.next();
      
      if (appLoginModules.containsKey(lmName) || appLoginModules.containsValue(lmName)) {
        return true;
      }
    }
    
    return false;
  }
  
  private void addPendingApplication(ContainerDeploymentInfo containerDeploymentInfo, LoginModuleConfiguration[] lmcToDeploy) {
    //add all pending login modules' display and class names in a list 
    HashMap<String, String> lmc = pendingApplications.get(containerDeploymentInfo.getApplicationName());
    
    if (lmc == null) {
      lmc = new HashMap<String, String>();
    }
    
    for (LoginModuleConfiguration loginModuleConfiguration : lmcToDeploy) {
      lmc.put(loginModuleConfiguration.getName(), loginModuleConfiguration.getLoginModuleClassName());
    }
    
    //add application and its login modules for deploy
    pendingApplications.put(containerDeploymentInfo.getApplicationName(), lmc);
  }
  
  private void addPendingApplication(ContainerDeploymentInfo containerDeploymentInfo, LoginModuleConfiguration lmcNewXML) {
    HashMap<String, String> lmc = pendingApplications.get(containerDeploymentInfo.getApplicationName());
    
    if (lmc == null) {
      lmc = new HashMap<String, String>();
    }
    
    lmc.put(lmcNewXML.getName(), lmcNewXML.getLoginModuleClassName());
    
    pendingApplications.put(containerDeploymentInfo.getApplicationName(), lmc);
  }
  
  private void removePendingLoginModules(String applicationName) throws WarningException {
    HashMap<String, String> lmConfigs = pendingApplications.get(applicationName);
    
    if (lmConfigs != null) {
      String[] lmNames = lmConfigs.keySet().toArray(new String[lmConfigs.size()]);
      
      try {
        DeployUtil.removeLoginModuleLinks(lmNames, this.configurationLock);
      } catch (Exception e) {
        LOCATION.traceThrowableT(Severity.WARNING, "Cannot remove login module links [{0}] from userstore.", new String[] {lmNames.toString()}, e);
        throw new WarningException("Cannot remove login module links [{0}] from userstore.", new String[] {lmNames.toString()});
      } finally {
        pendingApplications.remove(applicationName);
      }
      
    } else {
      throw new WarningException("Cannot remove login module links from userstore for application {0}.", new String[] {applicationName});
    }
  }
}
