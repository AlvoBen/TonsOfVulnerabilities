/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.services.security;

import iaik.security.provider.IAIK;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.security.Provider;
import java.security.Security;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.monitor.SystemMonitor;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.locking.LockingContext;
import com.sap.engine.frame.core.locking.ServerInternalLocking;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.frame.core.monitor.CoreMonitor;
import com.sap.engine.frame.core.thread.ContextObject;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.SecurityThreadContext;
import com.sap.engine.interfaces.security.auth.AssertionTicketFactory;
import com.sap.engine.interfaces.security.auth.LoginContextFactory;
import com.sap.engine.interfaces.security.auth.URLCipher;
import com.sap.engine.interfaces.security.auth.WebCallbackHandler;
import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;
import com.sap.engine.interfaces.security.userstore.config.RuntimeLoginModuleConfiguration;
import com.sap.engine.interfaces.security.userstore.config.RuntimeUserStoreConfiguration;
import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;
import com.sap.engine.services.deploy.container.ContainerManagement;
import com.sap.engine.services.security.jacc.PolicyConfigurator;
import com.sap.engine.services.security.login.LoginContextConfiguration;
import com.sap.engine.services.security.login.URLCipherImpl;
import com.sap.engine.services.security.migration.MigrationFramework;
import com.sap.engine.services.security.patch.ChangeDaemon;
import com.sap.engine.services.security.remote.RemoteSecurity;
import com.sap.engine.services.security.server.ModificationContextImpl;
import com.sap.engine.services.security.server.SecurityContextImpl;
import com.sap.engine.services.security.server.deploy.LoginModuleContainer;
import com.sap.engine.services.security.server.deploy.PolicyConfigurationContainer;
import com.sap.engine.services.security.server.deploy.PolicyConfigurationContainerOld;
import com.sap.engine.services.security.server.storage.StartupStorage;
import com.sap.engine.services.security.server.storage.Storage;
import com.sap.engine.system.SystemLoginConfiguration;
import com.sap.engine.system.SystemURLStreamHandlerFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;
import com.sap.security.api.UMFactory;
import com.sap.security.core.server.jaas.AssertionTicketFactoryImpl;

/**
 * @author  Stephan Zlatarev
 * @version 6.30
 */
public class SecurityServerFrame implements ApplicationServiceFrame {

  private static Properties properties = null;
  public static ApplicationServiceContext context = null;
  public static ThreadSystem threadContext = null;
  public static LockingContext lockingContext = null;
  public static ServerInternalLocking internalLock = null;
  public static SecurityServerFrame frame = null;
  public static SystemMonitor systemMonitor = null;
  private static SecurityContext securityImpl = null;
  public static int currentParticipant = -1;
  /* ID for the security context object in the thread context. It is initialized only once per startup. */
  private static int securityThreadContextId = -1;

  private static final String ASSERTION_TICKET = "evaluate_assertion_ticket";

  private static RemoteSecurity remoteSecurity = null;
  private static boolean emergencyMode = false;
  
  private LoginModuleContainer loginModuleContainer;
  private ContainerManagement deployService;
  private PolicyConfigurationContainer policyConfigurationContainer;
  private PolicyConfigurationContainerOld policyConfigurationContainerOld;
  
  //  this variable must be instance variable, because otherwise
  // GarbageCollector will invalidate the SystemLoginConfiguration
  private LoginContextConfiguration defaultConfiguration = null;

  private final static Location LOCATION = Location.getLocation(SecurityServerFrame.class);

  /**
   * This method is invoked when the service is started.
   *
   * @param  serviceContext  service context
   */
  public void start(ApplicationServiceContext serviceContext) throws ServiceException {
    frame = this;
    currentParticipant = serviceContext.getClusterContext().getClusterMonitor().getCurrentParticipant().getClusterId();
    properties = serviceContext.getServiceState().getProperties();
    context = serviceContext;
    threadContext = serviceContext.getCoreContext().getThreadSystem();
    systemMonitor = serviceContext.getContainerContext().getSystemMonitor();
    lockingContext = serviceContext.getCoreContext().getLockingContext();
    com.sap.engine.services.security.server.storage.HandlerPool.setFactory(serviceContext.getCoreContext().getConfigurationHandlerFactory());
    loginModuleContainer = new LoginModuleContainer(serviceContext);
    policyConfigurationContainer = new PolicyConfigurationContainer(serviceContext);
    policyConfigurationContainerOld = new PolicyConfigurationContainerOld(serviceContext);
    
    try {
      internalLock = lockingContext.createServerInternalLocking("$service.security", "Global Synchronization For Security Service");
    } catch (TechnicalLockException tle) {
      SimpleLogger.traceThrowable(Severity.FATAL, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000141", "Security Provider service is not started because of unexpected exception.", tle);
      throw new ServiceException(LOCATION, tle);
    }

    boolean lock;
    lock();
    lock = true;

    // this is kept temporary until JACC is in set class path for sure
    try {
      PolicyConfigurator.setPolicy();
    } catch (NoClassDefFoundError error) {
      SimpleLogger.log(Severity.FATAL, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000003", "Security Provider service is not started because of unexpected exception.");
      SimpleLogger.traceThrowable(Severity.FATAL, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000003", "Security Provider service is not started because of unexpected exception.", error);
    }

    try {
      registerIAIKProvider();
      AssertionTicketFactory.setFactory(new AssertionTicketFactoryImpl());

      ContextObject threadContextObject = null;
      ChangeDaemon changeDaemon = new ChangeDaemon(serviceContext);
      if (changeDaemon.init()) {
        if (!changeDaemon.run()) {
          //releaseLock();
          //lock = false;
        }
      }

      if (isServerInMigrationMode()) {
        MigrationFramework.setConfigurationHandlerFactory(serviceContext.getCoreContext().getConfigurationHandlerFactory());
        MigrationFramework.startMigration();
      }

      ((StartupStorage)Storage.getStorage(null)).globalBegin(lock);

      securityImpl = new SecurityContextImpl(serviceContext);
      initializeLoginConfiguration(securityImpl);

      threadContextObject = new com.sap.engine.services.security.login.SecurityContext();
      securityThreadContextId = serviceContext.getCoreContext().getThreadSystem().registerContextObject("security", threadContextObject);
      updateEmergencyModeFlag();

      ContainerEventListenerImpl clistener = new ContainerEventListenerImpl(this, securityImpl);
      int mask = ContainerEventListener.MASK_INTERFACE_AVAILABLE |
                 ContainerEventListener.MASK_INTERFACE_NOT_AVAILABLE |
                 ContainerEventListener.MASK_SERVICE_STARTED |
                 ContainerEventListener.MASK_SERVICE_STOPPED;
      Set<String> names = new HashSet<String>(8);
      names.add(ContainerEventListenerImpl.SERVICE_NAMING);
      names.add(ContainerEventListenerImpl.SERVICE_KEYSTORE);
      names.add(ContainerEventListenerImpl.SERVICE_JMX);
      names.add(ContainerEventListenerImpl.INTERFACE_KEYSTORE);
      names.add(ContainerEventListenerImpl.INTERFACE_SHELL);
      names.add(ContainerEventListenerImpl.INTERFACE_CROSS);
      names.add(ContainerEventListenerImpl.INTERFACE_CONTAINER);
      names.add(ContainerEventListenerImpl.CERT_REVOC_API);

      serviceContext.getServiceState().registerContainerEventListener(mask, names, clistener);
      serviceContext.getServiceState().registerManagementInterface(clistener.getRemoteSecurity());
      remoteSecurity = clistener.getRemoteSecurity();

      addAssertionTicketAuthenticationTemplate();
      Util.initializeCipher();
      ((StartupStorage)Storage.getStorage(null)).globalCommit();
      
      // initializes the security service and the jaas library with system ID.
      SecurityContextImpl.getSystemID();
      SecurityThreadContext.initialize(serviceContext, securityImpl);

      serviceContext.getContainerContext().getObjectRegistry().registerInterface(securityImpl);
      serviceContext.getContainerContext().getObjectRegistry().registerInterfaceProvider("security", securityImpl);
      
      URLCipher.setInstance(new URLCipherImpl());
      initializeWebCallbackHandler();

      LoginContextFactory.setRootSecurityContext(securityImpl);
    } catch (Throwable e) {
      SimpleLogger.log(Severity.FATAL, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000003", "Security Provider service is not started because of unexpected exception.");
      SimpleLogger.traceThrowable(Severity.FATAL, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000003",  "Security Provider service is not started because of unexpected exception.", e);

      try {
        ((StartupStorage)Storage.getStorage(null)).globalRollback();
      } catch (ConfigurationException ce) {
        //$JL-EXC$
      }

      throw new ServiceException(LOCATION, e);
    } finally {
      try {
        if (lock) {
          releaseLock();
        }
      } catch (TechnicalLockException tle) {
        SimpleLogger.log(Severity.FATAL, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000002", "Unexpected exception while trying to release lock in the Security Provider service.");
        SimpleLogger.traceThrowable(Severity.FATAL, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000002", "Unexpected exception while trying to release lock in the Security Provider service.", tle);

        throw new ServiceException(LOCATION, tle);
      }
    }
  }

  /**
   * Method to get the security thread context for the current thread.
   *
   * @return the security thread context for the current thread or null if the method has been called within a system thread.
   */
  public static com.sap.engine.services.security.login.SecurityContext getCurrentSecurityContextObject() {
    ThreadContext currentThreadContext = threadContext.getThreadContext();
    com.sap.engine.services.security.login.SecurityContext currentSecurityContext = null;

    if (currentThreadContext != null) {
      currentSecurityContext = ((com.sap.engine.services.security.login.SecurityContext) currentThreadContext.getContextObject(securityThreadContextId));
    }

    return currentSecurityContext;
  }

  public static RemoteSecurity getManagementContext() {
    return remoteSecurity;
  }
  public static SecurityContext getSecurityContext() {
    return securityImpl;
  }

  public static ApplicationServiceContext getServiceContext() {
    return context;
  }

  public static Properties getServiceProperties() {
    return properties;
  }

  public static boolean isEmergencyMode() {
    return emergencyMode;
  }

  public static void updateEmergencyModeFlag() {
    emergencyMode = securityImpl.getUserStoreContext().getActiveUserStore().getUserContext().isInEmergencyMode();
  }

  public void stop() {
//    frame = null;
//    threadContext = null;
//    context = null;
//    systemMonitor = null;
//    AbstractLoginModule.setLoginModuleHelper(null);
//    RemoteLoginContextFactory.setRemoteLoginContextFactory(null);
    if (deployService != null) {
      deployService.unregisterContainer(loginModuleContainer.getContainerInfo().getName());
      loginModuleContainer = null;
      deployService.unregisterContainer(policyConfigurationContainer.getContainerInfo().getName());
      policyConfigurationContainer = null;
      deployService.unregisterContainer(policyConfigurationContainerOld.getContainerInfo().getName());
      policyConfigurationContainerOld = null;
    } 
  }

  protected void setServiceProperties(Properties serviceProperties) {
    properties = serviceProperties;
  }

  private void lock() throws ServiceException {
    long waitLimit = 1800000;
    long waitInterval = 1000;
    long beginTime = System.currentTimeMillis();

    try {
      waitLimit = new Long(properties.getProperty("ServiceStartupLockTimeLimit", "" + waitLimit)).longValue();
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Unable to get value of \"ServiceStartupLockTimeLimit\" property! Used default value: " + waitLimit, e);
    }

    try {
      waitInterval = new Long(properties.getProperty("ServiceStartupLockAttemptsInterval", "" + waitInterval)).longValue();
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Unable to get value of \"ServiceStartupLockAttemptsInterval\" property! Used default value: " + waitLimit,  e);
    }

    Exception exception = null;

    while (System.currentTimeMillis() <= beginTime + waitLimit) {
      try {
        internalLock.lock("$service.security", "", ServerInternalLocking.MODE_EXCLUSIVE_NONCUMULATIVE);
        return;
      } catch (Exception e) {
        exception = e;

        synchronized(this) {
          try {
            this.wait(waitInterval);
          } catch (InterruptedException ex) {
            throw new ServiceException(LOCATION, ex);
          }
        }
      }
    }

    throw new ServiceException(LOCATION, exception);
  }

  private void releaseLock() throws TechnicalLockException {
    internalLock.unlock("$service.security", "", ServerInternalLocking.MODE_EXCLUSIVE_NONCUMULATIVE);
  }

  private void initializeLoginConfiguration(SecurityContext root) {
    try {
      Configuration.getConfiguration().refresh();
    } catch (Exception e) {
      try {
        String filename = ((SecurityContextImpl) root).getEnvironment().getServiceState().getWorkingDirectoryName() + File.separator + "fallback.config";

        FileOutputStream fos = new FileOutputStream(filename);
        fos.write(("other{};").getBytes());
        fos.close();
        Security.setProperty("policy.allowSystemProperty", "true");
        System.setProperty("java.security.auth.login.config", filename);
      } catch (Exception ee) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "initializeLoginConfiguration() - fallback.config:",  ee);
      }
    }
    try {
      Configuration.getConfiguration().refresh();
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "initializeLoginConfiguration() - Configuration.getConfiguration().refresh():",  e);
    }

    /////
    //  change the configuration
    defaultConfiguration = new LoginContextConfiguration(root);
    try {
      Security.setProperty("login.configuration.provider", SystemLoginConfiguration.class.getName());
      SystemLoginConfiguration.useConfiguration(defaultConfiguration);
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "initializeLoginConfiguration() - useConfiguration():",  e);
    }

    Configuration.setConfiguration(defaultConfiguration);
  }

  // registers IAIK at first place
  private void registerIAIKProvider() {
    try {
      Provider[] provider = Security.getProviders();

      if ((provider.length == 0) || (!provider[0].getClass().getName().equals(iaik.security.provider.IAIK.class.getName()))) {
        Provider iaik = new iaik.security.provider.IAIK();
        Security.removeProvider(iaik.getName());
        IAIK.addAsJDK14Provider();
            
        Util.SEC_SRV_LOCATION.logT(Severity.DEBUG, "IAIK.addAsJDK14Provider() OK");
      }
    } catch (Throwable e) {
      SimpleLogger.log(Severity.FATAL, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000003", "Security Provider service is not started because of unexpected exception.");
      SimpleLogger.traceThrowable(Severity.FATAL, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000003", "Security Provider service is not started because of unexpected exception.", e);
    }
    
      //Due to problems with IAIK-3DES encryption implementation running with default policy files
      //we need this manual redefinition to use DESede instead of 3DES

        new iaik.asn1.structures.AlgorithmID("1.2.840.113549.3.7", "DES-EDE3-CBC", "DESede/CBC/PKCS5Padding", javax.crypto.spec.IvParameterSpec.class);

    try {
      SystemURLStreamHandlerFactory urlStreamHandlerFactory = new SystemURLStreamHandlerFactory();
      urlStreamHandlerFactory.registerClassLoader(getClass().getClassLoader());
      urlStreamHandlerFactory.registerHandlerPackage("iaik.protocol");
      URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
    } catch (Throwable e) {
      /////
      //  java.net.URL.setURLStreamHandlerFactory() throws new Error("factory already defined")
      //  if a factory has already been registered. In that case just log the message.
      SimpleLogger.log(Severity.ERROR, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000004", "Unable to register IAIK HTTPS stream handler.");
      SimpleLogger.traceThrowable(Severity.ERROR, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000004", "Unable to register IAIK HTTPS stream handler.", e);
    }
  }

  private void addAssertionTicketAuthenticationTemplate() {
    SecurityContext assertionTicketContext = securityImpl.getPolicyConfigurationContext(ASSERTION_TICKET);

    if (assertionTicketContext == null) {
      String loginModuleClassName = "com.sap.security.core.server.jaas.EvaluateAssertionTicketLoginModule";
      UserStoreConfiguration oldConfig = securityImpl.getUserStoreContext().getActiveUserStore().getConfiguration();
      LoginModuleConfiguration[] oldModules = oldConfig.getLoginModules();
      LoginModuleConfiguration[] newModules = new LoginModuleConfiguration[oldModules.length + 1];

      System.arraycopy(oldModules, 0, newModules, 0, oldModules.length);
      newModules[oldModules.length] = new RuntimeLoginModuleConfiguration("EvaluateAssertionTicketLoginModule", "This login module verifies SAP Authentication Assertion tickets.", loginModuleClassName, new Hashtable(), new String[0], new String[0], null);

      RuntimeUserStoreConfiguration config = new RuntimeUserStoreConfiguration(oldConfig);

      config.setLoginModules(newModules);
      securityImpl.getUserStoreContext().updateUserStore(config, this.getClass().getClassLoader());

      securityImpl.registerPolicyConfiguration(ASSERTION_TICKET, SecurityContext.TYPE_TEMPLATE);
      assertionTicketContext = securityImpl.getPolicyConfigurationContext(ASSERTION_TICKET);
      AppConfigurationEntry[] modules = new AppConfigurationEntry[1];
      modules[0] = new AppConfigurationEntry(loginModuleClassName, AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, new Hashtable());
      assertionTicketContext.getAuthenticationContext().setLoginModules(modules);
    }
  }

  private boolean isServerInMigrationMode() {
    final CoreMonitor cm = context.getCoreContext().getCoreMonitor();
    if (cm.getRuntimeMode() == CoreMonitor.RUNTIME_MODE_SAFE &&
         (cm.getRuntimeAction() == CoreMonitor.RUNTIME_ACTION_MIGRATE || cm.getRuntimeAction() == CoreMonitor.RUNTIME_ACTION_SWITCH)) {
      return true;
    }

    return false;
  }
  
  private void initializeWebCallbackHandler() {
    final String METHOD = "initializeWebCallbackHandler";
    final String LOGON_APP_ALIAS = "logon_application_alias";
    
    if (Util.SEC_SRV_LOCATION.bePath()) {
      Util.SEC_SRV_LOCATION.entering(METHOD);
    }
    
    String logonURL = properties.getProperty("LogonURL");
    Properties UMEProperties = UMFactory.getProperties().getProperties();
    String alias = null;
    
    ModificationContextImpl modifications = (ModificationContextImpl) securityImpl.getModificationContext();
    
    modifications.beginModifications();
    com.sap.engine.frame.core.configuration.Configuration configuration = null;

    try {
      configuration = modifications.getConfiguration("security", false, false);
    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, Util.SEC_SRV_LOCATION,  "ASJ.secsrv.000142", "Cannot get security configuration.", e);
      configuration = null;
    }

    try {
      if (configuration != null) {
        if (configuration.existsConfigEntry(LOGON_APP_ALIAS)) {
          alias = (String) configuration.getConfigEntry(LOGON_APP_ALIAS);
        }
      }
    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, Util.SEC_SRV_LOCATION,  "ASJ.secsrv.000143", "Exception while trying to get logon_application_alias.", e);
    } finally {
      modifications.forgetModifications();
    }
    
    WebCallbackHandler.initialize(securityImpl, logonURL, UMEProperties, alias);
    
    if (Util.SEC_SRV_LOCATION.bePath()) {
      Util.SEC_SRV_LOCATION.exiting(METHOD);
    }
  }
  
  public static LoginModuleContainer getLoginModuleContainer() {
    return frame.loginModuleContainer;
  }
  
  public static PolicyConfigurationContainer getPolicyConfigurationContainer() {
    return frame.policyConfigurationContainer;
  }
  
  /**
   * This method is temporary and will be removed later on.
   * @deprecated
   */
  public static PolicyConfigurationContainerOld getOldPolicyConfigurationContainer() {
    return frame.policyConfigurationContainerOld;
  }
  
  public void setDeployService(ContainerManagement ds) {
    this.deployService = ds;
  }
}

