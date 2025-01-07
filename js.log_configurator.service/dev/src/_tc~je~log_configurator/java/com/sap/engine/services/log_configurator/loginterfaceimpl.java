package com.sap.engine.services.log_configurator;

/**
 * Title:        Logging
 * Description:  Logging API
 * Copyright:    Copyright (c) 2002
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

//import java.io.File;
//import java.io.InputStream;
import java.util.Properties;
//import java.util.NoSuchElementException;
//
//import com.sap.tc.logging.FileLog;
//import com.sap.tc.logging.Severity;
//import com.sap.tc.logging.Formatter;
//
import com.sap.engine.interfaces.log.LogInterface;
import com.sap.engine.interfaces.log.Logger;
import com.sap.engine.interfaces.log.LoggerAlreadyExistingException;
//import com.sap.engine.lib.util.*;
//import com.sap.engine.services.log_configurator.admin.LogConfigurator;
//import com.sap.engine.services.log_configurator.admin.LogXMLParser;
//import com.sap.engine.lib.logging.descriptors.LogConfiguration;
//import com.sap.engine.frame.container.monitor.SystemMonitor;
//import com.sap.engine.frame.container.monitor.ServiceMonitor;
//import com.sap.engine.frame.ServiceException;

/**
 * @author Miroslav Petrov
 * @version 6.30
 * @deprecated
 */
public class LogInterfaceImpl implements LogInterface {
//	public static final String DEFAULT_DEBUG_LOG_FILE_NAME = "DefaultDebugLogFileName";
//	public static final String DEFAULT_TRACE_LOG_FILE_NAME = "DefaultTraceLogFileName";
//	public static final String DEFAULT_INFO_LOG_FILE_NAME = "DefaultInfoLogFileName";
//	public static final String DEFAULT_NOTICE_LOG_FILE_NAME = "DefaultNoticeLogFileName";
//	public static final String DEFAULT_WARNING_LOG_FILE_NAME = "DefaultWarningLogFileName";
//	public static final String DEFAULT_ERROR_LOG_FILE_NAME = "DefaultErrorLogFileName";
//	public static final String DEFAULT_CRITICAL_LOG_FILE_NAME = "DefaultCriticalLogFileName";
//	public static final String DEFAULT_ALERT_LOG_FILE_NAME = "DefaultAlertLogFileName";
//	public static final String DEFAULT_EMERGENCY_LOG_FILE_NAME = "DefaultEmergencyLogFileName";
//
//	public static final String DEFAULT_LOG_LEVEL = "DefaultLogLevel";
//	public static final String DEFAULT_ENCODING = "DefaultEncoding";
//	public static final String DEFAULT_ZIP_DIRECTORY = "DefaultZipDirectory";
//	public static final String DEFAULT_FORMATTER = "DefaultFormatter";
//	public static final String DEFAULT_DUMP_ON_CONSOLE = "DefaultDumpOnConsole";
//	public static final String DEFAULT_CONSOLE_DUMP_LEVEL = "DefaultConsoleDumpLevel";
//
//	public static final String[] DEFAULT_FILE_NAMES = {DEFAULT_DEBUG_LOG_FILE_NAME, DEFAULT_TRACE_LOG_FILE_NAME,
//													   DEFAULT_INFO_LOG_FILE_NAME, DEFAULT_NOTICE_LOG_FILE_NAME,
//													   DEFAULT_WARNING_LOG_FILE_NAME, DEFAULT_ERROR_LOG_FILE_NAME,
//													   DEFAULT_CRITICAL_LOG_FILE_NAME, DEFAULT_ALERT_LOG_FILE_NAME,
//													   DEFAULT_EMERGENCY_LOG_FILE_NAME};
//
//	public static final String[] DEFAULT_SAP_ENGINE_PROPERTIES = {
//													DEFAULT_DEBUG_LOG_FILE_NAME, DEFAULT_TRACE_LOG_FILE_NAME,
//													DEFAULT_INFO_LOG_FILE_NAME, DEFAULT_NOTICE_LOG_FILE_NAME,
//													DEFAULT_WARNING_LOG_FILE_NAME, DEFAULT_ERROR_LOG_FILE_NAME,
//													DEFAULT_CRITICAL_LOG_FILE_NAME, DEFAULT_ALERT_LOG_FILE_NAME,
//													DEFAULT_EMERGENCY_LOG_FILE_NAME, DEFAULT_LOG_LEVEL,
//													DEFAULT_ENCODING, DEFAULT_ZIP_DIRECTORY, DEFAULT_FORMATTER,
//													DEFAULT_DUMP_ON_CONSOLE, DEFAULT_CONSOLE_DUMP_LEVEL};
//
//	public static final String[] SAP_ENGINE_LEVELS = {"DEBUG", "TRACE", "INFO", "NOTICE", "WARNING", "ERROR", "CRITICAL", "ALERT", "EMERGENCY"};
//
//	public static final byte[] SAP_ENGINE_LEVEL_VALUES = {DEBUG, TRACE, INFO, NOTICE, WARNING, ERROR, CRITICAL, ALERT, EMERGENCY};
//
//	private HashMapObjectInt engineStringToEngineIntMapping = null;
//	private HashMapObjectInt engineStringToSapIntMapping = null;
//	private HashMapIntInt engineIntToSapIntMapping = null;
//	private HashMapIntObject engineIntToSapStringMapping = null;
//
//	private ConcurrentHashMapObjectObject loggers = null;
//	private ConcurrentHashMapObjectObject usedFiles = null;
//	private HashMapObjectInt usedFilesCounters = null;
//	private Properties properties = null;
//	private Properties defaultLoggingProperties = null;
//	private SystemMonitor systemMonitor = null;
//
//	private boolean closed = false;
//
//	private String configurationFileName = null;
//	private LogConfigurator logConfigurator = null;
//
//	public LogInterfaceImpl(Properties properties, SystemMonitor systemMonitor) {
//	  this.properties = properties;
//	  this.systemMonitor = systemMonitor;
//	  initMappings();
//
//	  String configurationFileName = "." + File.separatorChar + "cfg" + File.separatorChar + "kernel" + File.separatorChar + "log-configuration.xml";
//	  logConfigurator = new LogConfigurator(configurationFileName);
//
//	  loggers = new ConcurrentHashMapObjectObject();
//	  buildDefaultLoggingProperties();
//	  usedFiles = new ConcurrentHashMapObjectObject();
//	  usedFilesCounters = new HashMapObjectInt();
//	  addDefaultFiles(usedFiles);
//	}
//
//	protected LogConfigurator getLogConfigurator() {
//	  return logConfigurator;
//	}
//
//	protected void close() {
//	  synchronized (loggers) {
//		closed = true;
//		Object[] existingLoggers = loggers.getAllKeys();
//		for (int i = 0; i < existingLoggers.length; i++) {
//		  ((LoggerImpl) loggers.remove((String) existingLoggers[i])).closeLogger();
//		}
//
//		closeAllFiles();
//		usedFiles = null;
//		usedFilesCounters = null;
//		defaultLoggingProperties = null;
//	  }
//	}
//
//	private void closeAllFiles() {
//	  Object[] files = usedFiles.getAllValues();
//	  for (int i = 0; i < files.length; i++) {
//		 ((FileLog) files[i]).close();
//	  }
//	}
//
//	private void initMappings() { //to do - should be based on some property files
//	  engineStringToEngineIntMapping = new HashMapObjectInt(SAP_ENGINE_LEVELS.length, 2, 1, new IntHashHolderImpl());
//	  for (int i = 0; i < SAP_ENGINE_LEVELS.length; i++) {
//		engineStringToEngineIntMapping.put(SAP_ENGINE_LEVELS[i], SAP_ENGINE_LEVEL_VALUES[i]);
//	  }
//
//	  engineIntToSapIntMapping = new HashMapIntInt(SAP_ENGINE_LEVELS.length, 2, 1, new IntHashHolderImpl());
//	  engineIntToSapIntMapping.put(DEBUG, Severity.DEBUG);
//	  engineIntToSapIntMapping.put(TRACE, Severity.PATH);
//	  engineIntToSapIntMapping.put(INFO, Severity.INFO);
//	  engineIntToSapIntMapping.put(NOTICE, Severity.INFO);
//	  engineIntToSapIntMapping.put(WARNING, Severity.WARNING);
//	  engineIntToSapIntMapping.put(ERROR, Severity.ERROR);
//	  engineIntToSapIntMapping.put(CRITICAL, Severity.ERROR);
//	  engineIntToSapIntMapping.put(ALERT, Severity.FATAL);
//	  engineIntToSapIntMapping.put(EMERGENCY, Severity.FATAL);
//
////    engineIntToSapIntMapping.put(DEBUG, 1);
////    engineIntToSapIntMapping.put(TRACE, 101);
////    engineIntToSapIntMapping.put(INFO, 201);
////    engineIntToSapIntMapping.put(NOTICE, 201);
////    engineIntToSapIntMapping.put(WARNING, 301);
////    engineIntToSapIntMapping.put(ERROR, 401);
////    engineIntToSapIntMapping.put(CRITICAL, 401);
////    engineIntToSapIntMapping.put(ALERT, 501);
////    engineIntToSapIntMapping.put(EMERGENCY, 601);
//
//	  engineStringToSapIntMapping = new HashMapObjectInt(SAP_ENGINE_LEVELS.length, 2, 1, new IntHashHolderImpl());
//	  for (int i = 0; i < SAP_ENGINE_LEVELS.length; i++) {
//		engineStringToSapIntMapping.put(SAP_ENGINE_LEVELS[i], engineIntToSapIntMapping.get(SAP_ENGINE_LEVEL_VALUES[i]));
//	  }
//
//	  engineIntToSapStringMapping = new HashMapIntObject(SAP_ENGINE_LEVELS.length, 2, 1, new IntHashHolderImpl());
//	  for (int i = 0; i < SAP_ENGINE_LEVEL_VALUES.length; i++) {
//		String sapLevelName = Severity.toString(engineIntToSapIntMapping.get(SAP_ENGINE_LEVEL_VALUES[i]));
//		engineIntToSapStringMapping.put(SAP_ENGINE_LEVEL_VALUES[i], sapLevelName);
//	  }
//
//	}
//
//
//	public String getMappedProviderLevelAsString(byte level) {
//	  return (String) engineIntToSapStringMapping.get(level);
//	}
//
//
//	public int getMappedProviderLevel(byte level) {
//	  return engineIntToSapIntMapping.get(level);
//	}
//
//	protected int getSapLevel(String engineLevel) {
//	  return engineStringToSapIntMapping.get(engineLevel);
//	}
//
//	protected int calculateEffectiveSeverityLevel(String engineLevel) {
//	  return getSapLevel(engineLevel) + getEngineLevel(engineLevel);
//	}
//
//	protected int getEngineLevel(String engineLevelAsNumber) {
//	  return engineStringToEngineIntMapping.get(engineLevelAsNumber);
//	}
//
//	private void buildDefaultLoggingProperties() {
//	  defaultLoggingProperties = new Properties();
//	  for (int i = 0; i < DEFAULT_FILE_NAMES.length; i++) {
//		String fileName = properties.getProperty(DEFAULT_FILE_NAMES[i], "./log/services/" + SAP_ENGINE_LEVELS[i] + ".log"); // TO DO - place default value in log dir
//		defaultLoggingProperties.setProperty(LoggerImpl.FILE_NAMES[i], fileName);
//	  }
//
//	  defaultLoggingProperties.setProperty(LoggerImpl.LOG_LEVEL, properties.getProperty(DEFAULT_LOG_LEVEL, "INFO")); //TO DO - add check for correctness !!!
//	  defaultLoggingProperties.setProperty(LoggerImpl.ENCODING, properties.getProperty(DEFAULT_ENCODING, "US-ASCII"));
//	  defaultLoggingProperties.setProperty(LoggerImpl.ZIP_DIRECTORY, properties.getProperty(DEFAULT_ZIP_DIRECTORY, "logBackup"));
//	  defaultLoggingProperties.setProperty(LoggerImpl.FORMATTER, properties.getProperty(DEFAULT_FORMATTER, "com.sap.tc.logging.TraceFormatter"));
//	  defaultLoggingProperties.setProperty(LoggerImpl.DUMP_ON_CONSOLE, properties.getProperty(DEFAULT_DUMP_ON_CONSOLE, "true"));
//	  defaultLoggingProperties.setProperty(LoggerImpl.CONSOLE_DUMP_LEVEL, properties.getProperty(DEFAULT_CONSOLE_DUMP_LEVEL, "ERROR"));
//	}
//
//	private void addDefaultFiles(ConcurrentHashMapObjectObject repository) {
//	  String encoding = defaultLoggingProperties.getProperty(LoggerImpl.ENCODING);
//	  Formatter formatter = null;
//	  try {
//		formatter = (Formatter) Class.forName(defaultLoggingProperties.getProperty(LoggerImpl.FORMATTER)).newInstance();
//	  } catch (Exception exc) {}
//
//	  for (int i = 0; i < LoggerImpl.FILE_NAMES.length; i++) {
//		String fileName = defaultLoggingProperties.getProperty(LoggerImpl.FILE_NAMES[i]);
//		buildParentDirectories(fileName);
//
//		FileLog file = new FileLog(fileName); //TO DO - add formatter
//		file.setEncoding(encoding);
//		if (formatter != null) {
//		  file.setFormatter(formatter);
//		}
//		repository.put(fileName, file);
//	  }
//	}
//
//
//	private boolean buildParentDirectories(String fileName) {
//	  int index = fileName.lastIndexOf('/');
//	  if (index > 0) {
//		String parentDirectories = fileName.substring(0, index);
//		return new File(parentDirectories).mkdirs(); //TO DO add logs that file's parent directories cannot be created
//	  }
//	  return true;
//	}
//
//
//	public FileLog getFile(String fileName, String encoding, Formatter formatter) {
//	  synchronized (usedFiles) {
//		FileLog file = (FileLog) usedFiles.get(fileName);
//		if (file == null) {
//		  buildParentDirectories(fileName);
//		  file = new FileLog(fileName);
//		  file.setEncoding(encoding);
//		  if (formatter != null) {
//			file.setFormatter(formatter);
//		  }
//		  usedFiles.put(fileName, file);
//		  usedFilesCounters.put(fileName, 1);
//		} else {
//		  if (!isDefaultFile(fileName)) {
//			int currentUsers = usedFilesCounters.get(fileName);
//			usedFilesCounters.put(fileName, ++currentUsers);
//		  }
//		}
//		return file;
//	  }
//	}
//
//
//	protected void setEncoding(String fileName, String encoding) {
//	  synchronized (usedFiles) {
//		int count = 0;
//
//		try {
//		  count = usedFilesCounters.get(fileName);
//		} catch (NoSuchElementException exc) {
//		  return; // default log file
//		}
//
//		if (count == 1) {
//		  ((FileLog) usedFiles.get(fileName)).setEncoding(encoding);
//		}
//	  }
//	}
//
//
//	protected void setFormatter(String fileName, Formatter formatter) throws Exception {
//	  if (formatter == null) {
//		return;
//	  }
//	  synchronized (usedFiles) {
//		int count = 0;
//
//		try {
//		  count = usedFilesCounters.get(fileName);
//		} catch (NoSuchElementException exc) {
//		  return; // default log file
//		}
//
//		if (count == 1) {
//		  ((FileLog) usedFiles.get(fileName)).setFormatter(formatter);
//		}
//	  }
//	}
//
//	protected void removeFile(String fileName) {
//	  synchronized (usedFiles) {
//		int count = 0;
//		try {
//		  count = usedFilesCounters.get(fileName);
//		} catch (NoSuchElementException exc) {
//		  return; // default log file
//		}
//		if (count > 1) {
//		  usedFilesCounters.put(fileName, --count);
//		} else {
//		  FileLog file = (FileLog) usedFiles.remove(fileName);
//		  file.close();
//		  usedFilesCounters.remove(fileName);
//		}
//	  }
//	}
//
//
//	private boolean isDefaultFile(String fileName) {
//	  synchronized (defaultLoggingProperties) {
//		for (int i = 0; i < LoggerImpl.FILE_NAMES.length; i++) {
//		  if (fileName.equals(getDefaultLoggingProperty(LoggerImpl.FILE_NAMES[i]))) {
//			return true;
//		  }
//		}
//	  }
//
//	  return false;
//	  //it's also possible this check to be performed based on whether there is an entry for the fileName in usedFilesCounters
//	}
//
//	public void activateLogger(String name) {   // TO DO - make this automatically at startup
//	  ServiceMonitor serviceMonitor = systemMonitor.getService(name);
//	  InputStream inputStream = null;
//	  try {
//		inputStream = serviceMonitor.getDescriptorContainer().getPersistentEntryStream("log-configuration.xml", false);
//	  } catch (NullPointerException nexc) { // TO DO remove this as soon as getDescriptor method is implemented by the ServiceManager
////      System.out.println("   Couldn't activate logger for service : " + name + ". getDescriptorContainer() method is not implemented yet in the ServiceManager:) ");
//	  }
//	  if (inputStream != null) {
//		LogConfiguration configuration = new LogXMLParser().parse(inputStream);
//		logConfigurator.configure(configuration);
//	  }
//	}
//
//	public Logger createLogger(String name, Properties properties) throws LoggerAlreadyExistingException {
//	  Logger result = null;
//	  synchronized (loggers) {
//		if (closed) {
//		  return null;
//		}
//
//		result = (Logger) loggers.get(name);
//		if (result != null) {
//		  throw new LoggerAlreadyExistingException(result);
//		} else {
//		  result = new LoggerImpl(name, properties, this);
//		  loggers.put(name, result);
//		}
//	  }
//
//	  return result;
//	}
//
//
//	public Logger getLogger(String name) {
//	  synchronized (loggers) {
//		if (closed) {
//		  return null;
//		} else {
//		  return (Logger) loggers.get(name);
//		}
//	  }
//	}
//
//	public boolean destroyLogger(String name) {
//	  LoggerImpl logger = null;
//	  synchronized (loggers) {
//		if (closed) {
//		  return false;
//		}
//		logger = (LoggerImpl) loggers.remove(name);
//	  }
//	  if (logger != null) {
//		logger.closeLogger();
//		return true;
//	  } else {
//		return false;
//	  }
//	}
//
//	public String[] getLoggerNames() {
//	  synchronized (loggers) {
//		if (closed) {
//		  return new String[0];
//		} else {
//		  return (String[]) loggers.getAllKeys();
//		}
//	  }
//	}
//
//
//
//	public Properties getDefaultLoggingProperties() {
//	  try {
//		return (Properties) defaultLoggingProperties.clone();
//	  } catch (NullPointerException exc) {
//		return null;
//	  }
//	}
//
//	public String getDefaultLoggingProperty(String key) {
//	  try {
//		return defaultLoggingProperties.getProperty(key);
//	  } catch (NullPointerException exc) {
//		return null;
//	  }
//	}
//
//	public boolean setDefaultLoggingProperty(String key, String value) {
//	  try {
//		// defaultLoggingProperties.setProperty(key, value);
//		// return true;
//		properties.setProperty(key, value);
//
//		for (int i=0; i<DEFAULT_SAP_ENGINE_PROPERTIES.length; i++) {
//		  if (DEFAULT_SAP_ENGINE_PROPERTIES[i].equals(key)) {
//			defaultLoggingProperties.setProperty(LoggerImpl.SAP_ENGINE_PROPERTIES[i], value);
//			break;
//		  }
//		}
//		return true;
//	  } catch (NullPointerException exc) {
//		return false;
//	  }
//	}
//
//	public Properties getConfigurationProperties() {
//	  return new Properties();
//	}
//
//	public String getConfigurationProperty(String key) {
//	  return null;
//	}
//
//	public boolean setConfigurationProperty(String key, String value) {
//	  return false;
//	}
//
//	public MultiFileLog[] getAllMultiFileLogs() {
//	  Object[] loggerImpls = loggers.getAllValues();
//	  MultiFileLog[] result = new MultiFileLog[ loggerImpls.length ];
//	  for ( int i = 0; i < result.length; i ++ ) {
//		result[ i ] = ((LoggerImpl) loggerImpls[ i ]).getMultiFileLog();
//	  }
//	  return result;
//	}
//
//	public ConcurrentHashMapObjectObject getUsedFiles() {
//	  return usedFiles;
//	}


   //Empty implementation
  public void activateLogger(String name) {
  }

  public Logger createLogger(String name, Properties properties) throws LoggerAlreadyExistingException {
	return new LoggerImpl();
  }

  public Logger getLogger(String name) {
	return null;
  }

  public boolean destroyLogger(String name) {
	return true;
  }

  public String[] getLoggerNames() {
	return new String[0];
  }

  public Properties getDefaultLoggingProperties() {
	return null;
  }

  public String getDefaultLoggingProperty(String key) {
	return null;
  }

  public boolean setDefaultLoggingProperty(String key, String value) {
	return false;
  }

  public Properties getConfigurationProperties() {
	return null;
  }

  public String getConfigurationProperty(String key) {
	return null;
  }

  public boolean setConfigurationProperty(String key, String value) {
	return false;
  }
}
