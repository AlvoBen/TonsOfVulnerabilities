package com.sap.engine.core.log.impl1;

/**
 * Title:        Logging
 * Description:  Logging API
 * Copyright:    Copyright (c) 2001
 * Company:      SAP Labs Bulgaria LTD., Sofia, Bulgaria.
 * Url:          Http://www.saplabs.bg
 *               All rights reserved.
 *
 *               This software is the confidential and proprietary information
 *               of SAP AG International ("Confidential Information").
 *               You shall not disclose such  Confidential Information
 *               and shall use it only in accordance with the terms of
 *               the license agreement you entered into with SAP AG.
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.LogManager;

import com.sap.bc.proj.jstartup.JStartupFramework;
import com.sap.engine.boot.FileClassLoader;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Manager;
import com.sap.engine.core.log.api.LogManagerManagementInterface;
import com.sap.engine.core.log.impl1.archive.ArchivingManager;
import com.sap.engine.frame.state.ManagementInterface;
import com.sap.engine.frame.state.ManagementListener;
import com.sap.engine.lib.logging.LogConfigurationUpdater;
import com.sap.engine.lib.logging.SapLoggingPrintStream;
import com.sap.engine.lib.logging.descriptors.LogConfiguration;
import com.sap.engine.lib.logging.descriptors.LogControllerDescriptor;
import com.sap.engine.lib.logging.descriptors.LogDestinationDescriptor;
import com.sap.engine.lib.logging.descriptors.LogFormatterDescriptor;
import com.sap.engine.system.SystemEnvironment;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.ConsoleLog;
import com.sap.tc.logging.EventDispatcherFactory;
import com.sap.tc.logging.FileLog;
import com.sap.tc.logging.Formatter;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.LogController;
import com.sap.tc.logging.Severity;

/**
 * @author Miroslav Petrov
 * @version 6.30
 */
public class LogManagerImpl implements Manager, LogManagerManagementInterface {//$JL-EXC$ //$JL-LOG_CONFIG$ //$JL-SYS_OUT_ERR$
	
  public static final String KERNEL_CONFIG_DIR = "." + File.separator + "cfg" + File.separator + "kernel" + File.separator;
  public static final String CUSTOM_CONFIG_FILE_NAME = "custom-log-configuration.xml";
  public static final String CONFIG_FILE_NAME = "log-configuration.xml";
	
  public static final String CUSTOM_CONFIGURATION_FILE = KERNEL_CONFIG_DIR + CUSTOM_CONFIG_FILE_NAME;
  public static final String CONFIGURATION_FILE = KERNEL_CONFIG_DIR + CONFIG_FILE_NAME;

  private static final String LOG_CONFIGURATION_KEY = "log-configuration";
  private static final String ACA_CONFIGURATION_KEY = "ACAConfiguration";
  
  private static String DEFAULT_TRACEFILE_PATTERN = null;
  private static final int DEFAULT_TRACEFILE_LIMIT = 10485760;
  private static final int DEFAULT_TRACEFILE_COUNT = 21;
  
  private static String DEFAULT_LOGFILE_PATTERN = null;
  private static final int DEFAULT_LOGFILE_LIMIT = 10485760;
  private static final int DEFAULT_LOGFILE_COUNT = 7;

  private Properties properties;

  private ArchivingManager archivingManager;

  private FileLog defaultTraceFile;
  private LogConfiguration configuration;
  private String[] singleTraceFileExcluded;
  private boolean singleTraceFileIsForced;
  
  public LogManagerImpl() {
    properties = null;
    configuration = null;
    defaultTraceFile = null;
    singleTraceFileExcluded = null;
    singleTraceFileIsForced = false;
    //construct default log and trace patterns
    String instanceNumber = JStartupFramework.getParam("SAPSYSTEM");
    DEFAULT_TRACEFILE_PATTERN = "./log/defaultTrace_" + instanceNumber + ".trc";
    DEFAULT_LOGFILE_PATTERN = "./log/defaultLog_" + instanceNumber + ".log";
    ConsoleLog.setEnable(false);
  }

  private LogConfiguration getEmergencyConfiguration() {
    LogConfiguration configuration = new LogConfiguration();

    // Root Category Settings
    LogController controller = Category.getRoot();
    LogControllerDescriptor controllerDescriptor = new LogControllerDescriptor();
    controllerDescriptor.setName(controller.getName());

    controller.setEffectiveSeverity(Severity.INFO);
    controllerDescriptor.setEffectiveSeverity(Severity.INFO);

    FileLog log = getDefaultLogFile0();
    LogDestinationDescriptor logDescriptor = new LogDestinationDescriptor();
    logDescriptor.setName("DEFAULT_LOG_FILE");
    logDescriptor.setType("FileLog");
    logDescriptor.setPattern(log.getPattern());
    logDescriptor.setLimit(log.getLimit());
    logDescriptor.setCount(log.getCnt());
    logDescriptor.setRealLog(log);

    Formatter formatter = log.getFormatter();
    LogFormatterDescriptor formatterDescriptor = new LogFormatterDescriptor();
    formatterDescriptor.setRealFormatter(formatter);
    formatterDescriptor.setType(formatter.getClass().getName());
    logDescriptor.setFormatter(formatterDescriptor);

    log.setEffectiveSeverity(Severity.ALL);
    logDescriptor.setEffectiveSeverity(Severity.ALL);

    configuration.addLogDestination(logDescriptor);

    controller.addLog(log);
    
    controllerDescriptor.addDestination(
      logDescriptor, LogControllerDescriptor.ASSOCIATION_TYPE_PUBLIC);

    configuration.addLogController(controllerDescriptor);

    // Root Location Settings
    controller = Location.getRoot();
    controllerDescriptor = new LogControllerDescriptor();
    controllerDescriptor.setName(controller.getName());

    controller.setEffectiveSeverity(Severity.ERROR);
    controllerDescriptor.setEffectiveSeverity(Severity.ERROR);

    log = defaultTraceFile = new FileLog(DEFAULT_TRACEFILE_PATTERN, DEFAULT_TRACEFILE_LIMIT, DEFAULT_TRACEFILE_COUNT);
    logDescriptor = new LogDestinationDescriptor();
    logDescriptor.setName("DEFAULT_TRACE_FILE");
    logDescriptor.setType("FileLog");
    logDescriptor.setPattern(log.getPattern());
    logDescriptor.setLimit(log.getLimit());
    logDescriptor.setCount(log.getCnt());
    logDescriptor.setRealLog(log);

    formatter = log.getFormatter();
    formatterDescriptor = new LogFormatterDescriptor();
    formatterDescriptor.setRealFormatter(formatter);
    formatterDescriptor.setType(formatter.getClass().getName());
    logDescriptor.setFormatter(formatterDescriptor);

    log.setEffectiveSeverity(Severity.ALL);
    logDescriptor.setEffectiveSeverity(Severity.ALL);

    configuration.addLogDestination(logDescriptor);

    controller.addLog(log);
    
    controllerDescriptor.addDestination(
      logDescriptor, LogControllerDescriptor.ASSOCIATION_TYPE_PUBLIC);

    configuration.addLogController(controllerDescriptor);

    return configuration;
  }

  /**
   *
   */
  private FileLog getDefaultLogFile0() {
    int limit = DEFAULT_LOGFILE_LIMIT;
    try {
      limit = Integer.parseInt(properties.getProperty("DefaultLogFile_Limit"));

      if(limit < 0) {
        limit = DEFAULT_LOGFILE_LIMIT;
      }
    } catch(Exception exc) { /* Use defaults. */
    }

    int count = DEFAULT_LOGFILE_COUNT;
    try {
      count = Integer.parseInt(properties.getProperty("DefaultLogFile_Count"));

      if(count < 0) {
        count = DEFAULT_LOGFILE_COUNT;
      }
    } catch(Exception exc) { /* Use defaults. */
    }

    String pattern = 
      properties.getProperty("DefaultLogFile_Pattern", DEFAULT_LOGFILE_PATTERN);

    return new FileLog(pattern, limit, count);
  }

  /**
   *
   */
  private void checkConsoleLogs() {
    try {
    	
      if("NO".equalsIgnoreCase(properties.getProperty("ConsoleLogs_UseSapAPI"))) {
        return;
      }

      /* SAP Logging API will be used for the console streams. */
      Location stdErrLocation = Location.getLocation("System.err");
      stdErrLocation.setEffectiveSeverity(Severity.ERROR);
      System.setErr(new SapLoggingPrintStream(stdErrLocation, Severity.ERROR));

      Location stdOutLocation = Location.getLocation("System.out");
      stdOutLocation.setEffectiveSeverity(Severity.ERROR);
      System.setOut(new SapLoggingPrintStream(stdOutLocation, Severity.INFO));
    } catch(Exception exc) {
      exc.printStackTrace();
    }
  }
  /**
   *
   */
  private void createSystemStreamsDescriptors() {
    try {
      LogController controller = Location.getLocation("System.err");
      LogControllerDescriptor controllerDescriptor = new LogControllerDescriptor();
      controllerDescriptor.setName(controller.getName());
      controllerDescriptor.setEffectiveSeverity(controller.getEffectiveSeverity());
      this.configuration.addLogController(controllerDescriptor);

      controller = Location.getLocation("System.out");
      controllerDescriptor = new LogControllerDescriptor();
      controllerDescriptor.setName(controller.getName());
      controllerDescriptor.setEffectiveSeverity(controller.getEffectiveSeverity());
      this.configuration.addLogController(controllerDescriptor);
    } catch(Exception exc) {
      exc.printStackTrace();
    }
  } // </createSystemStreamsDescriptors(void)>
  
  private void reconfigureStandardConsoleHandler() {
	  Location logManagerLocation = Location.getLocation("LogManagerImpl.reconfigureStandardConsoleHandler");
	  
	  // create custom properties for java.util.logging.LogManager
	  Properties javaLoggingProps = new Properties();
	  
	  //load the standard java logging properties
	  String loggingPropsPath = System.getProperty("java.home") + "/lib/logging.properties";	 
	  try {
		File standardLoggingProps = new File(loggingPropsPath);  
		javaLoggingProps.load(new FileInputStream(standardLoggingProps));
	  } catch (Exception e) {
	      //set standard properties if the property file is not found
		  javaLoggingProps.setProperty(".level", "INFO");
	      javaLoggingProps.setProperty("java.util.logging.FileHandler.pattern", "%h/java%u.log");
	      javaLoggingProps.setProperty("java.util.logging.FileHandler.limit", "50000");
	      javaLoggingProps.setProperty("java.util.logging.FileHandler.count", "1");
	      javaLoggingProps.setProperty("java.util.logging.FileHandler.formatter", "java.util.logging.XMLFormatter");
	      
		  logManagerLocation.errorT("Failed to read java logging properties from " 
				  + loggingPropsPath + " Setting standard properties: " 
				  + javaLoggingProps.toString());
	  }
	  
	  //override the standard ConsoleHandler
	  javaLoggingProps.setProperty("handlers", "com.sap.tc.logging.SapConsoleHandler");
	  javaLoggingProps.setProperty("com.sap.tc.logging.SapConsoleHandler.level", "INFO");
	  javaLoggingProps.setProperty("com.sap.tc.logging.SapConsoleHandler.formatter", 
		  "java.util.logging.SimpleFormatter");
	  
	  try {	  
		  ByteArrayOutputStream propsOutStream = new ByteArrayOutputStream();
		  javaLoggingProps.store(propsOutStream, "java.util.logging properties");
		  ByteArrayInputStream propsInpStream = new ByteArrayInputStream(propsOutStream.toByteArray()); 
	
		  // reconfigure java.util.logging.LogManager
		  LogManager.getLogManager().readConfiguration(propsInpStream);
	  } catch (IOException e) {
		  logManagerLocation.errorT("The following problem occured while trying " +
		  		"to reconfigure java.util.LogManager with cutom properties : " + javaLoggingProps.toString());
	  }
  }

  /**
   * Initialazing Manager with specified properties. Initialize references which are
   * nessesary for current manager and are mandatory. Is there is an error with this, must
   * be returned false and must be notify log manager for it. It's forbiden to throw
   * exception.
   * 
   * @param properties - Manager properties.
   * @return if initialization of the Manager is successful returns true
   */
  public boolean init(Properties properties) {
    this.properties = properties;
    checkConsoleLogs();
    try {
      archivingManager = new ArchivingManager(properties);
      EventDispatcherFactory.getEventDispatcher().addListener(archivingManager);
      archivingManager.startArchiving();
      
      this.configuration = 
        (LogConfiguration) Framework.getKernelObject(LOG_CONFIGURATION_KEY);
      
      Location.getRoot().resetAll();
      Category.getRoot().resetAll();
      
      LogConfigurationUpdater.init(
        properties.getProperty("SpecificDestinationLogControllers"));
      
      // update Logging API from DB
      LogConfigurationUpdater.updateLoggingAPI(this.configuration, true);
      
      // update Logging API according to ACA Configuration
      setPropertyOnline(ACA_CONFIGURATION_KEY, properties.getProperty(ACA_CONFIGURATION_KEY));      

      if(this.configuration == null) {
        System.err.println(
          "Serialized object \"log-configuration\" from bootstrap not found!");
        
        throw new Exception(
          "Serialized object \"log-configuration\" from bootstrap not found!");
      }
      
      // override the default java.util.ConsoleHandler with com.sap.tc.loggong.SapConsoleHandler
      reconfigureStandardConsoleHandler();
      
    } catch(Throwable t) {
      try {
        Location.getRoot().resetAll();
        Category.getRoot().resetAll();
        this.configuration = getEmergencyConfiguration();
        checkConsoleLogs();

        System.err.println();
        
        System.err.println(
          "Log Manager: WARNING: Switched to emergency log settings due to:");
        
        t.printStackTrace();
        System.err.println();
      } catch(Throwable emergency) {
        Location.getRoot().resetAll();
        Category.getRoot().resetAll();
        this.configuration = new LogConfiguration();
        checkConsoleLogs();

        System.err.println();
        
        System.err.println(
          "Log Manager: WARNING: Unable to apply log settings to the kernel due to:");
        
        emergency.printStackTrace();
        System.err.println();
      }
    }

    createSystemStreamsDescriptors();
    FileClassLoader.setLogSuccessfullyStarted();
    return true;
  }

  /**
   * The Framework invokes it when all Managers are initialized. Second pass over all
   * registered Managers. Initialize references, type callback, which are nessesary for
   * current manager and are not mandatory. Is there is an error with this, must be notify
   * log manager for it. It's forbiden to throw exception.
   */
  public void loadAdditional() {
  }

  /**
   * Run-time calling the Manager to change properties
   * 
   * @param key - Manager Property key to change
   * @param value - Manager Property value to set
   * @return if changed successful retruns true else reboot is needed
   * @deprecated use updateProperties(Properties properties) instead
   */
  public boolean setProperty(String key, String value) throws IllegalArgumentException {
	setPropertyOnline(key, value);
    return true;
  }

  /**
   * Run-time calling the Manager to change properties
   * 
   * @param props - Manager Properties to set
   * @return if changed successful retruns true else reboot is needed
   * @deprecated use updateProperties(Properties properties) instead
   */
  public boolean setProperties(Properties props) throws IllegalArgumentException {
    Enumeration<?> keys = props.keys();
    while(keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      if(!setProperty(key, props.getProperty(key))) {
        return false;
      }
    }

    return true;
  }  
  
  /*
   * This method is introduced, because the public boolean setProperty method is deprecated
   */
  private void setPropertyOnline(String key, String value) {
		//online editable properties
		// streams redirection
		if ("ConsoleLogs_UseSapAPI".equals(key)) {
			// if stream redirection is to be deactivated
			if ("NO".equalsIgnoreCase(value) || "OFF".equalsIgnoreCase(value)
					|| "FALSE".equals(value)) {
				// if streams were redirected
				if (!"NO".equalsIgnoreCase(properties.getProperty(key))
						&& !"OFF".equalsIgnoreCase(properties.getProperty(key))
						&& !"FALSE".equalsIgnoreCase(properties
								.getProperty(key))) {
					// set standard system streams
					System.err
							.println("Log Manager: WARNING: \"System.err\" stream is redirected to "
									+ "the \"standard\" error output stream of the current process.");
	
					System.setErr(SystemEnvironment.STD_ERR);
	
					System.out
							.println("Log Manager: WARNING: \"System.out\" stream is redirected to "
									+ "the \"standard\" output stream of the current process.");
	
					System.setOut(SystemEnvironment.STD_OUT);
				}
				// if streams were not redirected
			} else if ("NO".equalsIgnoreCase(properties.getProperty(key))
					|| "OFF".equalsIgnoreCase(properties.getProperty(key))
					|| "FALSE".equalsIgnoreCase(properties.getProperty(key))) {
				// redirect streams
				properties.setProperty(key, "YES");
				checkConsoleLogs();
				return;
			}
		}
	
		// archiving
		if ("ArchiveOldLogFiles".equalsIgnoreCase(key)) {
			// if archiving is to be deactivated (value to be set is "OFF" or
			// "NO")
			if ("NO".equalsIgnoreCase(value) || "OFF".equalsIgnoreCase(value)
					|| "FALSE".equalsIgnoreCase(value)) {
				// if archiving was turned on
				if (!"NO".equalsIgnoreCase(properties.getProperty(key))
						&& !"OFF".equals(properties.getProperty(key))
						&& !"FALSE".equalsIgnoreCase(properties
								.getProperty(key))) {
					// deactivate archiving
					System.out
							.println("Log Manager: INFO: Archiving is being deactivated.");
					properties.setProperty(key, value);
					archivingManager.setProperties(properties);
					return;
				}
				// if archiving is to be activated
			} else if ("NO".equalsIgnoreCase(properties.getProperty(key))
					|| "OFF".equalsIgnoreCase(properties.getProperty(key))
					|| "FALSE".equalsIgnoreCase(properties.getProperty(key))) {
				// activate archiving
				System.out
						.println("Log Manager: INFO: Archiving is being activated.");
				properties.setProperty(key, value);
				archivingManager.setProperties(properties);
				return;
			}
		}
		
		//Application Centric Administration (ACA) configuration properties
		if (ACA_CONFIGURATION_KEY.equalsIgnoreCase(key)) {
			if (value != null) {
				if (value.equals("")) {
					// set value to a list of 2 epty mappings separated by ';'
					// this way the severities of all DCs affected by ACA will be reset 
					value = ";";			
				}
				
				// nonempty value - split the items and parse each of them
				System.out.println("INFO : Application Centric Configuration is currently ON");
				String[] dcToSeverity = value.split(";");
				//populate the mapping and pass it to Logging API
				Properties dcSeverityProps = new Properties();
				for (int i = 0; i < dcToSeverity.length; i++) {
					if (dcToSeverity[i].length() == 0) {
						continue;
					}
					int dashIdx = dcToSeverity[i].indexOf("-");
					String dcName = dcToSeverity[i].substring(0, dashIdx);
					String dcSeverity = dcToSeverity[i].substring(dashIdx + 1);
					dcSeverityProps.setProperty(dcName, dcSeverity);
				}
				//update Logging API
				Location logManagerACA = Location.getLocation("LogManagerACA");
				logManagerACA.errorT("LogManagerACA Configuration : Log Manager is updating " +
							"Logging API with the following ACA Properties : " + dcSeverityProps.toString());
				LogController.setSeverityFromDC(dcSeverityProps);
			} else {
				//empty value
				System.out.println("INFO : Application Centric Configuration is currently OFF");
			}
		}
	
		properties.setProperty(key, value);
  }
  /** @see com.sap.engine.core.Manager#updateProperties(Properties) */
  public void updateProperties(Properties props) {
    Enumeration<?> keys = props.keys();
    while(keys.hasMoreElements()) {
		//set next property
		String key = (String) keys.nextElement();
		String value = props.getProperty(key);
		setPropertyOnline(key, value);
    }
  }  
  
  /**
   * Get current property
   *
   * @param key - property key
   * @return property value
   * @deprecated current properties should be taken using configuration library
   */
  public String getCurrentProperty(String key) {
    return properties.getProperty(key);
  }

  /**
   * Get current properties, which are used by the manager. This method is
   * invoked by Framework for visualization of this properties.
   *
   * @return   currently used properties by the manager.
   * @deprecated current properties should be taken using configuration library
   */
  public Properties getCurrentProperties() {
    return properties;
  }

  /**
   * Shutting down the Manager when reboot is needed. All resources must be disposed.
   * 
   * @param properties - Shut down properties. One of this property is if there is stoped
   *          cluster or only local machine.
   */
  public void shutDown(Properties properties) {
    if(archivingManager != null) {
      EventDispatcherFactory.getEventDispatcher().removeListener(archivingManager);
      archivingManager.stopArchiving();
    }
  }

  /**
   * The Framework invokes it to check the status of the manager. Returned value is used
   * from AI System.
   * 
   * @return status of the manager, which is number between 0 and 100. This is degree
   *         value of the burdering of manager.
   */
  public byte getStatus() {
    return 0;
  }

  /**
   * Get debug information about the manager's current state.
   * 
   * @param flag can be used to determines which parts of the info to be returned.
   * @return a String object containing the debug info. A return value of
   *         <code>null</code> means that this manager does not provide debug
   *         information.
   */
  public String getDebugInfo(int flag) {
    return null;
  }

  /* ManagementInterface Related Section */

  /**
   * Retrieves the ManagementInterface implementation for the manager
   * 
   * @return an object that is used for runtime monitoring and management of this manager.
   */
  public ManagementInterface getManagementInterface() {
    return this;
  }

  /**
   *
   */
  public void registerManagementListener(ManagementListener listener) {
  }

  /**
   *
   */
  public LogConfiguration getLogConfiguration() {
    return configuration;
  }

  /** @deprecated */
  public Object getDatabaseFilter() {
    return new Object();
  }

  /** @deprecated */
  public String[] getDatabaseFiltered() {
    return new String[0];
  }

  /** @deprecated */
  public void enableDbFiltering(String[] targets) {
  }

  /** @deprecated */
  public void disableDbFiltering(String[] targets) {
  }

  /**
   *
   */
  public FileLog getDefaultTraceFile() {
    return defaultTraceFile;
  }

  /**
   *
   */
  public boolean singleTraceFileIsForced() {
    return singleTraceFileIsForced;
  }

  /**
   *
   */
  public String[] getSingleTraceFileExcluded() {
    return singleTraceFileExcluded;
  }

  /**
   *
   */
  public boolean isSingleTraceFileExcluded(String target) {
    target += Location.SEPARATOR;

    for(int i = singleTraceFileExcluded.length; --i >= 0;) {
      // if (target.startsWith(singleTraceFileExcluded[i] + Location.SEPARATOR)) {
      if(target.startsWith(singleTraceFileExcluded[i])) {
        return true;
      }
    }

    return false;
  }
}