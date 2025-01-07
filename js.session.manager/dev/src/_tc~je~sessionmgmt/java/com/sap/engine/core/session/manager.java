/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.core.session;

import static com.sap.engine.session.ClusterEnv.setClusterEnvironment;
import com.sap.engine.boot.soft.FailoverContext;
import com.sap.engine.session.*;
import com.sap.engine.session.runtime.OnRequestFailoverMode;
import com.sap.engine.session.runtime.SessionFailoverMode;
import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.engine.session.runtime.timeout.TimeoutProcessor;
import com.sap.engine.session.mgmt.ConfigurationEntry;
import com.sap.engine.session.mgmt.SessionConfigurator;
import com.sap.engine.session.spi.persistent.Storage;
import com.sap.engine.session.spi.persistent.PersistentDomainModel;
import com.sap.engine.session.exec.SessionExecContext;
import com.sap.engine.session.exec.ClientContextImpl;
import com.sap.engine.session.usr.UserContext;
import com.sap.engine.session.trace.Trace;
import com.sap.engine.session.trace.TraceManager;
import com.sap.engine.frame.state.ManagementInterface;
import com.sap.engine.frame.cluster.message.ListenerAlreadyRegisteredException;
import com.sap.engine.frame.cluster.message.MessageContext;
import com.sap.bc.proj.jstartup.JStartupFramework;
import com.sap.engine.core.session.management.SessionManagementMonitor;
import com.sap.engine.core.session.management.SessionManagementMonitorImpl;
import com.sap.engine.core.session.persistent.db.DBStorage;
import com.sap.engine.core.session.persistent.sharedmemory.ShMemoryStorage;
import com.sap.engine.core.session.timer.tasks.ShMemoryCheck;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.engine.core.cluster.ClusterManager;
import com.sap.engine.core.thread.ThreadManager;
import com.sap.bc.proj.jstartup.sadm.ShmWebSession;
import com.sap.bc.proj.jstartup.sadm.ShmException;
import com.sap.bc.proj.jstartup.sadm.ShmApiVersion;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.SimpleLogger;

import java.util.*;

/**
 * Author: georgi-s Date: May 20, 2004
 */
public abstract class Manager extends SessionContextFactory implements com.sap.engine.core.Manager {

  public static final String SESSION_GLOBAL_FAILOVER = "session.failover.enabled";

  public static final String SESSION_PERSISTENT_STORRAGE = "session.persistent.storage";

  public static final String SESSION_PERSISTENT_MODE = "session.persistent.mode";

  public static final String SESSION_FAILOVER_APP_CONFIG = "session.failover.app.config";

  public static final String SESSION_USER_CONTEXT_PERSISTENCY = "session.user_context.persistency";

  public static final String SESSION_INVALIDATE_TIMEOUTERS = "session.invalidation.timeouters";

  public static final String SESSION_INVALIDATE_TIMEOUT = "session.invalidation.timeout";

  public static final String REGULAR_CHECK_PERIOD = "regular.check.period";

  public static final String ATTRIBUTE_DELTA_FAILOVER = "attribute.delta.failover";
  
  public static final String MARK_GET_CHUNK = "mark.get.chunk";
  
  public static final String SHM_MONITORING_ENABLED = "enable.shared.memory.monitoring";

  public static String defFactoryClassName = "com.sap.engine.core.session.impl.memory.spi.MemorySessionDomainFactoryImpl";

  public static String defFactory = "MemorySessionDomain";

  private static Location loc = Location.getLocation(Manager.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  private static String helpMenu = "+==========================================================+\n"
      + "| 0  Help                                                  |\n" + "| 1  List session domains                                  |\n"
      + "| 2  Enable/Disable failover                               |\n" + "| 3  List Shm Web Sessions                                 |\n"
      + "| 4  List users                                            |\n" + "| 5  Enable/Disable Debuging                               |\n"
      + "| 3d Enable/Disable Session Trace                          |\n" + "| 3e  Enable/Disable Domain Trace                          |\n"
      + "| 3f  Enable/Disable Session Invalidation                  |\n" + "| 7  Print Session debug info                              |\n"
      + "| 8  Show sesions for invalidation number                  |\n" + "+==========================================================+\n";

  private static String firstLine = "+==========================================================+\n";

  String jvm_vendor;

  private SessionManagementMonitor monitor = null;

  public static final String USER_PERSISTENT_STORAGE_NONE = "NONE";

  public static final String USER_PERSISTENT_STORAGE_DB = "DB";

  public static final String USER_PERSISTENT_STORAGE_SHARED_MEMORY = "SHARED_MEMORY";

  public static final String USER_CONTEXTS_CONTEXT = "USERS_CONTEXT";

  public static final String USER_CONTEXTS_DOMAIN = "USERS_DOMAIN";

  private final String nodeIndex = JStartupFramework.getNodeIndex();

  public boolean init(Properties properties) {
    initProperties(properties);
    initRuntime();
    return true;
  }

  protected void initProperties(Properties properties) {
  	FailoverContext.registerFailoverListener(new SoftShutdown());
  	
    String defaultStorage = ConfigurationEntryBuilder.STORAGE_FILE;
    // String jvm = System.getProperty("JVM", "");
    jvm_vendor = System.getProperty("java.vm.vendor");

    String global_setting = System.getProperty(SESSION_GLOBAL_FAILOVER);
    if (global_setting != null) {
      if (global_setting.equalsIgnoreCase("true")) {
      	StaticConfiguration.enableFailover();
	SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0001", 
	    "Global session failover enabled.");
      } else if (global_setting.equalsIgnoreCase("false")) {
	StaticConfiguration.disableFailover();
	SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0002", 
	    "Global session failover disabled.");
      } else if (global_setting.equalsIgnoreCase("")) {
	SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0003", 
	    "Global session failover setting is not specified.");
      } else { // wrong setting
	SimpleLogger.log(Severity.WARNING, Category.SYS_SERVER, loc, "ASJ.ses.cf0004", 
	    SESSION_GLOBAL_FAILOVER + " property has illegal value. It's accepted as empty setting. Check your properties.");
      }
    } else { // no system property
      global_setting = properties.getProperty(SESSION_GLOBAL_FAILOVER);
      if (global_setting != null) {
	if (global_setting.equalsIgnoreCase("true")) {
	  StaticConfiguration.enableFailover();
	  SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0005", 
	      "Global session failover enabled.");
	} else if (global_setting.equalsIgnoreCase("false")) {
	  StaticConfiguration.disableFailover();
	  SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0006", 
	      "Global session failover disabled.");
	} else if (global_setting.equalsIgnoreCase("")) {
	  SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0007", 
	      "Global session failover setting is not specified.");
	} else { // wrong setting
	  SimpleLogger.log(Severity.WARNING, Category.SYS_SERVER, loc, "ASJ.ses.cf0008", 
	      SESSION_GLOBAL_FAILOVER + " property has illegal value. It's accepted as empty setting. Check your properties.");
	}
      } else { // null - if possible
	SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0009", 
	    "Global session failover setting is not specified.");
      }
    }

    String userStorageProp = properties.getProperty(SESSION_USER_CONTEXT_PERSISTENCY);
    if (userStorageProp == null) {
      userStorageProp = USER_PERSISTENT_STORAGE_DB;
      SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0010", 
	  "Session user context persistency is not set. Will use dafault DB persistency");
    } else { // check correctness
      if (userStorageProp.equals(USER_PERSISTENT_STORAGE_DB)) {
	SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0011", 
	    "Session user context persistency is set to DB");
      } else if (userStorageProp.equalsIgnoreCase(USER_PERSISTENT_STORAGE_SHARED_MEMORY)) {
	SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0012", 
	    "Session user context persistency is set to SHARED_MEMORY");
      } else if (userStorageProp.equalsIgnoreCase(USER_PERSISTENT_STORAGE_NONE)) {
	SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0013", 
	    "Session user context persistency is set to NONE");
      } else { // wrong setting
	SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0014", 
	    SESSION_USER_CONTEXT_PERSISTENCY + "property setting is invalid, will use default DB. Check your properties");
	userStorageProp = USER_PERSISTENT_STORAGE_DB;
      }
    }

    initUserContextPersistency(userStorageProp);

    initStaticConfiguration(properties, defaultStorage);

    if (loc.beInfo()) {
      String msg = "Failover of Session Management is ";
      if (StaticConfiguration.isFailoverDisable()) {
	msg += "Disabled.";
      } else if (StaticConfiguration.isFailoverEnable()) {
	msg += "Enabled.";
      } else {
	msg += "not configured explicitly.";
      }
      loc.infoT(msg);
    }

    // read property session.invalidation.timeouters
    try {
      String timeouters = properties.getProperty(SESSION_INVALIDATE_TIMEOUTERS, "2");
      TimeoutProcessor.INVALIDATION_TIMEOUTERS = Integer.parseInt(timeouters);
    } catch (NumberFormatException nfe) { // dafault
      TimeoutProcessor.INVALIDATION_TIMEOUTERS = 2;
    }
    // read property session.invalidate.timeout
    try {
      String timeout = properties.getProperty(SESSION_INVALIDATE_TIMEOUT, "300000");
      TimeoutProcessor.INVALIDATION_TIMEOUT = Integer.parseInt(timeout);
    } catch (NumberFormatException nfe) { // dafault
      TimeoutProcessor.INVALIDATION_TIMEOUT = 5 * 60 * 1000; // 5 minutes
    }
    // read property regular.check.period
    try {
      String period = properties.getProperty(REGULAR_CHECK_PERIOD, "5");
      ShMemoryCheck.period = Integer.parseInt(period) * 60 * 1000; // transfer
      // minutes
      // to
      // milliseconds
    } catch (NumberFormatException nfe) { // dafault
      ShMemoryCheck.period = 5 * 60 * 1000; // 5 min
    }
    // read property attribute.delta.failover
    // dafault value false
    RuntimeSessionModel.deltaFailoverEnabled = Boolean.parseBoolean(properties.getProperty(ATTRIBUTE_DELTA_FAILOVER, "false"));
    
    //  read property mark.get.chunk
    // dafault value false
    RuntimeSessionModel.markGetChunk = Boolean.parseBoolean(properties.getProperty(MARK_GET_CHUNK, "false"));
    
    SessionExecContext.isEnabledSharedMemoryMonitoring = Boolean.parseBoolean(properties.getProperty(SHM_MONITORING_ENABLED, "false"));
  }

  protected void initRuntime() {

    try {
      ShmApiVersion.setApiVersion(3);
    } catch (Throwable e) {
      loc.traceThrowableT(Severity.ERROR, "There are mismatch between JStartup Versions. Check your BCO Version - it can be too old.", e);
    }

    monitor = new SessionManagementMonitorImpl();
    SessionConfigurator.builder = new ConfigurationEntryBuilder();
    SessionConfigurator.storageSoftShutdown = ConfigurationEntryBuilder.buildStorageForType(ConfigurationEntryBuilder.STORAGE_DB);

    ThreadManager thrManager = (ThreadManager) Framework.getManager(Names.APPLICATION_THREAD_MANAGER);
    thrManager.registerContextObject(SessionExecContext.THREAD_CONTEXT_OBJECT_NAME, new SessionExecContext());
    ThreadContextProxyImpl tcProxy = new ThreadContextProxyImpl();
    SessionExecContext.setThreadContextProxyImpl(tcProxy);




    MessageListenerImpl clusterMessageListener = new MessageListenerImpl();
    ClusterManager clusterManager = (ClusterManager) Framework.getManager(Names.CLUSTER_MANAGER);
    MessageContext messageContext = clusterManager.getMessageContext(MessageListenerImpl.MESSAGE_LISTENER_REGISTRATION_NAME);
    try {
      messageContext.registerListener(clusterMessageListener);
    } catch (ListenerAlreadyRegisteredException e) {
      e.printStackTrace();
    }

    setClusterEnvironment(new ClusterEnvImpl());

    // starts the session threads : SessionInvalidatorQueue and the Session
    // Invalidator Threads
    RuntimeSessionModel.timeoutProcessor = new TimeoutProcessor();
    // start the TimeoutProcessor Thread
    RuntimeSessionModel.timeoutProcessor.setDaemon(true);
    RuntimeSessionModel.timeoutProcessor.start();
    // end part5
  }


  /**
         * Initialize the root Config Entries
         * 
         * @param props
         *                Manager's properties
         * @param defaultStorage
         *                default Session Storage Type
         */
  protected static void initStaticConfiguration(Properties props, String defaultStorage) {
    String storageType = props.getProperty(SESSION_PERSISTENT_STORRAGE);
    if (storageType == null || storageType.trim().equals("")) {
      SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0015", 
	  "Session persistent storrage type is not set. Will use dafault : " + defaultStorage);
      storageType = defaultStorage;
    } else { // check correctness
      if (storageType.equals("FILE")) {
	SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0016", 
	    "Session persistency storrage type is set to FILE");
      } else if (storageType.equalsIgnoreCase("DB")) {
	SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0017", 
	    "Session persistency storrage type is set to DB");
      } else if (storageType.equalsIgnoreCase("SHARED_MEMORY")) {
	SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0018", 
	    "Session persistency storrage type is set to SHARED_MEMORY");
      } else { // wrong setting
	SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0019", 
	    SESSION_PERSISTENT_STORRAGE + "property setting is invalid, will use default : " 
	    + defaultStorage + " . Check your properties");
	storageType = defaultStorage;
      }
    }
    ConfigurationEntryBuilder.defaultStorage = storageType;

    String modeType = props.getProperty(SESSION_PERSISTENT_MODE);
    if (modeType == null) {
      modeType = ConfigurationEntryBuilder.MODE_ON_REQUEST;
      SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0020", 
	  "Session persistence mode is not set. Will use dafault : " + modeType);
    } else { // check correctness
      if (modeType.equalsIgnoreCase(ConfigurationEntryBuilder.MODE_ON_REQUEST)) {
	SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0021", 
	    "Session persistence mode is set to ON_REQUEST");
      } else if (modeType.equalsIgnoreCase(ConfigurationEntryBuilder.MODE_ON_THREAD_END)) {
	SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0022", 
	    "Session persistence mode is set to ON_THREAD_END");
      } else if (modeType.equalsIgnoreCase(ConfigurationEntryBuilder.MODE_ON_SHUTDOWN)) {
	SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0023", 
	    "Session persistence mode is set to ON_SHUTDOWN");
      } else { // wrong setting
	SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.cf0024", 
	    SESSION_PERSISTENT_MODE + "property setting is invalid, will use default ON_REQUEST");
	modeType = ConfigurationEntryBuilder.MODE_ON_REQUEST;
      }
    }
    ConfigurationEntryBuilder.defaultMode = modeType;

    String appConfig = props.getProperty(SESSION_FAILOVER_APP_CONFIG);

    if (appConfig != null && !StaticConfiguration.isFailoverDisable()) {
      ConfigurationEntryBuilder.buildConfigurationEntries(appConfig);
    }

    Storage storage = ConfigurationEntryBuilder.buildStorageForType(storageType);
    if (!StaticConfiguration.isFailoverDisable()) {
      fmode = ConfigurationEntryBuilder.buildFailoverModeForType(modeType);
    }
    if (!StaticConfiguration.isFailoverEnable()) {
      storage = null;
    }

    ConfigurationEntry entry = SessionConfigurator.getConfigurationEntry("HTTP_Session_Context");
    if (entry == null) {
      entry = new ConfigurationEntry("HTTP_Session_Context", storage, fmode);
      SessionConfigurator.addConfigurationEntry("HTTP_Session_Context", entry);
    }

    entry = SessionConfigurator.getConfigurationEntry("/Service/EJB");
    if (entry == null) {
      entry = new ConfigurationEntry("/Service/EJB", storage, fmode);
      SessionConfigurator.addConfigurationEntry("/Service/EJB", entry);
    }
          
  }

  static SessionFailoverMode fmode = new OnRequestFailoverMode();

  private void initUserContextPersistency(String userStorageProp) {
    Storage storage;

    if (userStorageProp.equals(USER_PERSISTENT_STORAGE_DB)) {
      storage = new DBStorage();
    } else if (userStorageProp.equals(USER_PERSISTENT_STORAGE_SHARED_MEMORY)) {
      storage = new ShMemoryStorage();
    } else { 	// USER_PERSISTENT_STORAGE_NONE = "NONE"
      return;
    }

    try {
      PersistentDomainModel model = storage.getDomainModel(USER_CONTEXTS_CONTEXT, USER_CONTEXTS_DOMAIN);
      ClientContextImpl.setPersistentModel(model);
    } catch (Exception e) {
      Trace.logException(e);
    }
  }

  public void loadAdditional() {
    clearShmMonitoring();
  }

  public boolean setProperty(String s, String s1) throws IllegalArgumentException {
    return false;
  }

  public boolean setProperties(Properties properties) throws IllegalArgumentException {
    return false;
  }

  /** @see com.sap.engine.core.Manager#updateProperties(Properties) */
  public void updateProperties(Properties properties) {
    //TODO impl - tuka da update-vame failover setingite online    
  }

  public String getCurrentProperty(String s) {
    return null;
  }

  public void shutDown(Properties properties) {
    ClusterManager clusterManager = (ClusterManager) Framework.getManager(Names.CLUSTER_MANAGER);
    MessageContext messageContext = clusterManager.getMessageContext(MessageListenerImpl.MESSAGE_LISTENER_REGISTRATION_NAME);
    messageContext.unregisterListener();
  }

  public Properties getCurrentProperties() {
    return null;
  }

  public byte getStatus() {
    return 0;
  }

  public String getDebugInfo(int i) {

    switch (i) {
    case 0: {
      return helpMenu;
    }
    case 1: {
      StringBuffer result = new StringBuffer(firstLine);
      Iterator contexts = SessionContextFactory.getInstance().contexts();
      while (contexts.hasNext()) {
	SessionContext ctx = (SessionContext) contexts.next();
	result.append("  ");
	result.append(ctx.getName());
	result.append("\n");
	Iterator domains = ctx.rootDomains();
	while (domains.hasNext()) {
	  SessionDomain domain = (SessionDomain) domains.next();
	  result.append("    ");
	  result.append(domain.getName());
	  result.append("\n");
	}
      }
      result.append(firstLine);
      return result.toString();
    }
    case 2: {
      if (StaticConfiguration.isFailoverDisable()) {
	StaticConfiguration.enableFailover();
	return "Failover enabled!";
      } else {
	StaticConfiguration.disableFailover();
	return "Failover disabled!";
      }
    }
    case 3: {
      try {
	ShmWebSession[] shmSessions = ShmWebSession.getAllSessions();
	StringBuffer result = new StringBuffer(firstLine);
	for (ShmWebSession shmSession : shmSessions) {
	  try {
	    shmSession.release();
	  } catch (IllegalStateException is) {
	    loc.traceThrowableT(Severity.WARNING, "ERROR durin debug command [List Shm Web Sessions].\n", is);
	  }
	  result.append(shmSession);
	  result.append("\n");
	}
	result.append(firstLine);
	return result.toString();
      } catch (ShmException e) {
	return e.toString();
      }
    }
    case 4: {
      Iterator usr = ClientContextImpl.getAccessor().userContexts().iterator();
      HashMap<String, Integer> groupByUser = new HashMap<String, Integer>();
      while (usr.hasNext()) {
	UserContext uc = (UserContext) usr.next();
	String userName = uc.getUser();
	Integer sessions = groupByUser.get(userName);
	if (sessions == null) {
	  groupByUser.put(userName, 1);
	} else {
	  groupByUser.put(userName, sessions + 1);
	}
      }
      StringBuffer result = new StringBuffer(firstLine);
      usr = groupByUser.keySet().iterator();
      while (usr.hasNext()) {
	String name = (String) usr.next();
	result.append(name);
	result.append("     ");
	result.append(groupByUser.get(name));
	result.append("\n");
      }
      result.append(firstLine);
      return result.toString();
    }
    case 5: {
      Trace.trace = !Trace.trace;
      return "Debug enabled:" + Trace.trace;
    }
    case 61: {
      return "Session trace enabled:" + TraceManager.changeTrace(TraceManager.SESSION_TRACE);
    }
    case 62: {
      return "Domain trace enabled:" + TraceManager.changeTrace(TraceManager.DOMAIN_TRACE);
    }
    case 63: {
      return "Session invalidation trace enabled:" + TraceManager.changeTrace(TraceManager.SESSION_INVALIDATION_TRACE);
    }
    case 7: {
      boolean flag = Trace.trace;
      Trace.trace = true;
      TraceManager.print();
      Trace.trace = flag;
      return "Trace info is printed";
    }
    case 8: {
      return "Sessions for invalidations : " + RuntimeSessionModel.timeoutProcessor.invalidatorQueue.size();
    }
    }
    return helpMenu;
  }

  public ManagementInterface getManagementInterface() {
    return monitor;
  }

  public String lockInfo() {
    return nodeIndex;
  }

  protected abstract void clearShmMonitoring();

}
