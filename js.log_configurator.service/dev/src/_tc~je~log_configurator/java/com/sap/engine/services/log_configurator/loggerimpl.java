/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.log_configurator;

import com.sap.engine.interfaces.log.Logger;
import com.sap.engine.interfaces.log.LogRecord;
import com.sap.engine.interfaces.log.LogInterface;
import com.sap.engine.services.log_configurator.admin.LogConfigurator;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Log;
import com.sap.tc.logging.FileLog;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Formatter;
import com.sap.tc.logging.ConsoleLog;

import java.util.Properties;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author Miroslav Petrov
 * @version 6.30
 * @deprecated
 */
public class LoggerImpl implements Logger {
//  public static final String DEBUG_LOG_FILE_NAME = "DebugLogFileName";
//  public static final String TRACE_LOG_FILE_NAME = "TraceLogFileName";
//  public static final String INFO_LOG_FILE_NAME = "InfoLogFileName";
//  public static final String NOTICE_LOG_FILE_NAME = "NoticeLogFileName";
//  public static final String WARNING_LOG_FILE_NAME = "WarningLogFileName";
//  public static final String ERROR_LOG_FILE_NAME = "ErrorLogFileName";
//  public static final String CRITICAL_LOG_FILE_NAME = "CriticalLogFileName";
//  public static final String ALERT_LOG_FILE_NAME = "AlertLogFileName";
//  public static final String EMERGENCY_LOG_FILE_NAME = "EmergencyLogFileName";
//
//  public static final String LOG_LEVEL = "LogLevel";
//  public static final String ENCODING = "Encoding";
//  public static final String ZIP_DIRECTORY = "ZipDirectory";
//  public static final String FORMATTER = "Formatter";
//  public static final String DUMP_ON_CONSOLE = "DumpOnConsole";
//  public static final String CONSOLE_DUMP_LEVEL = "ConsoleDumpLevel";
//
//
//  public static final String[] FILE_NAMES = {DEBUG_LOG_FILE_NAME, TRACE_LOG_FILE_NAME,
//                                             INFO_LOG_FILE_NAME, NOTICE_LOG_FILE_NAME,
//                                             WARNING_LOG_FILE_NAME, ERROR_LOG_FILE_NAME,
//                                             CRITICAL_LOG_FILE_NAME, ALERT_LOG_FILE_NAME,
//                                             EMERGENCY_LOG_FILE_NAME};
//
//
//  public static final String[] SAP_ENGINE_PROPERTIES = {DEBUG_LOG_FILE_NAME, TRACE_LOG_FILE_NAME,
//                                                        INFO_LOG_FILE_NAME, NOTICE_LOG_FILE_NAME,
//                                                        WARNING_LOG_FILE_NAME, ERROR_LOG_FILE_NAME,
//                                                        CRITICAL_LOG_FILE_NAME, ALERT_LOG_FILE_NAME,
//                                                        EMERGENCY_LOG_FILE_NAME, LOG_LEVEL,
//                                                        ENCODING, ZIP_DIRECTORY, FORMATTER,
//                                                        DUMP_ON_CONSOLE, CONSOLE_DUMP_LEVEL};
//
//  public static final char LOG_RECORD_DELIMITER = ' ';
//  private LogInterfaceImpl logInterface = null;
//  private String name = null;
//  private Location location = null;
//  private MultiFileLog destination = null;
//  private ConsoleLog consoleLog = null;
//
//
//  private Properties loggingProperties = null;
//
//  public LoggerImpl(String name, Properties properties, LogInterfaceImpl logInterface) {
//    this.name = name;
//    this.logInterface = logInterface;
//    loggingProperties = extractLoggingProperties(properties);
//    initLogger();
//  }
//
//  private Properties extractLoggingProperties(Properties properties) {
//    if (properties == null) {
//      return logInterface.getDefaultLoggingProperties();
//    }
//    Properties result = new Properties();
//    for (int i = 0; i < SAP_ENGINE_PROPERTIES.length; i++) {
//      String property = properties.getProperty(SAP_ENGINE_PROPERTIES[i],
//                                               logInterface.getDefaultLoggingProperty(SAP_ENGINE_PROPERTIES[i]));
//      result.setProperty(SAP_ENGINE_PROPERTIES[i], property);
//    }
//    return result;
//  }
//
//
//  private void initLogger() {
//    location = Location.getLocation(name); //TO DO - should be more sofisticated
//
//    String logLevel = loggingProperties.getProperty(LOG_LEVEL);
//    location.setEffectiveSeverity(logInterface.calculateEffectiveSeverityLevel(logLevel));
//
//    destination = new MultiFileLog(loggingProperties.getProperty(ZIP_DIRECTORY));
//
//    String encoding = loggingProperties.getProperty(ENCODING);
//    String formatterName = loggingProperties.getProperty(FORMATTER);
//    Formatter formatter = null;
//    try {
//      formatter = (Formatter) Class.forName(formatterName).newInstance();
//    } catch (Exception exc) {}
//    for (int i = 0; i < FILE_NAMES.length; i++) {
//      String fileName = loggingProperties.getProperty(FILE_NAMES[i]);
//      FileLog file = logInterface.getFile(fileName, encoding, formatter); //should we also set severity to file logs?
//      destination.addLog(LogInterfaceImpl.SAP_ENGINE_LEVEL_VALUES[i], file);
//    }
//
//    try {
//      if (loggingProperties.getProperty(DUMP_ON_CONSOLE).equalsIgnoreCase("true")) {
//        consoleLog = new ConsoleLog();
//        consoleLog.setEffectiveSeverity(
//          logInterface.calculateEffectiveSeverityLevel(loggingProperties.getProperty(CONSOLE_DUMP_LEVEL)));
//        location.addLog(consoleLog);
//      }
//    }catch (Exception exc) {}
//
//    location.addLog(destination);
//
//    LogConfigurator configurator = logInterface.getLogConfigurator();
//    configurator.registerLog(name, destination);
//    configurator.registerLogController(name, location);
//  }
//
//  protected void closeLogger() {
//    for (int i = 0; i < FILE_NAMES.length; i++) {
//      logInterface.removeFile(loggingProperties.getProperty(FILE_NAMES[i]));
//    }
//
//    LogConfigurator configurator = logInterface.getLogConfigurator();
//    configurator.unregisterLog(name);
//    configurator.unregisterLogController(name);
//
//    location = null;
//    destination = null;
//    loggingProperties = null;
//    logInterface = null;
//  }
//
//
//  public String getName() {
//    return name;
//  }
//
//  public synchronized boolean setLoggingProperty(String key, String value) {
//    try {
//      if (key == null) {
//        return false;
//      } else if (key.equals(LOG_LEVEL)) {
//        int sapSeverity = logInterface.calculateEffectiveSeverityLevel(value); //thorws an exception if the value is incorrect
//        loggingProperties.setProperty(LOG_LEVEL, value);
//        location.setEffectiveSeverity(sapSeverity);
//        return true;
//      } else if (key.equals(ENCODING)) {
//        loggingProperties.setProperty(ENCODING, value);
//        for (int i = 0; i < FILE_NAMES.length; i++) {
//          logInterface.setEncoding(loggingProperties.getProperty(FILE_NAMES[i]), value); // fails for default files and files that are used by another logger
//        }
//        return true;
//      } else if (key.equals(FORMATTER)) {
//        Formatter formatter = null;
//        try {
//          formatter = (Formatter) Class.forName(value).newInstance();
//        } catch (Exception exc) {
//          return false;
//        }
//        loggingProperties.setProperty(FORMATTER, value);
//        for (int i = 0; i < FILE_NAMES.length; i++) {
//          logInterface.setFormatter(loggingProperties.getProperty(FILE_NAMES[i]), formatter); // fails for default files and files that are used by another logger
//        }
//        return true;
//      } else if (key.equals(ZIP_DIRECTORY)) {
//        //TO DO
//        return false;
//      } else if (key.equals(DUMP_ON_CONSOLE)) {
//        if (value.toUpperCase().equals("TRUE") && (consoleLog == null)) {
//          consoleLog = new ConsoleLog();
//          consoleLog.setEffectiveSeverity(logInterface.calculateEffectiveSeverityLevel(loggingProperties.getProperty(CONSOLE_DUMP_LEVEL)));
//          location.addLog(consoleLog);
//          loggingProperties.setProperty(DUMP_ON_CONSOLE, value);
//          return true;
//        } else if (value.toUpperCase().equals("FALSE") && (consoleLog != null)) {
//          location.removeLog(consoleLog);
//          consoleLog = null;
//          loggingProperties.setProperty(DUMP_ON_CONSOLE, value);
//          return true;
//        } else {
//          return false;
//        }
//      } else if (key.equals(CONSOLE_DUMP_LEVEL)) {
//        if (consoleLog != null) {
//          consoleLog.setEffectiveSeverity(logInterface.calculateEffectiveSeverityLevel(value));
//        }
//        loggingProperties.setProperty(CONSOLE_DUMP_LEVEL, value);
//        return true;
//      } else {
//        for (int i = 0; i < FILE_NAMES.length; i++) {
//          if (key.equals(FILE_NAMES[i])) {
//            Formatter formatter = null;
//            try {
//              formatter = (Formatter) Class.forName(value).newInstance();
//            } catch (Exception exc) {}
//
//            FileLog file = logInterface.getFile(value, loggingProperties.getProperty(ENCODING), formatter);
//            String oldFileName = (String) loggingProperties.remove(FILE_NAMES[i]);
//            loggingProperties.setProperty(FILE_NAMES[i], value);
//            logInterface.removeFile(oldFileName);
//            destination.addLog(LogInterfaceImpl.SAP_ENGINE_LEVEL_VALUES[i], file);
//            return true;
//          }
//        }
//        return false; //unrecognized property
//      }
//    } catch (Exception exc) {
//      return false;
//    }
//  }
//
//
//
//  public String getLoggingProperty(String key) {
//    try {
//      return loggingProperties.getProperty(key);
//    } catch (NullPointerException exc) {
//      //do nothing - may be thrown if the logger is already destroyed
//      return null;
//    }
//  }
//
//  public Properties getLoggingProperties() {
//    try {
//      return (Properties) loggingProperties.clone();
//    } catch (NullPointerException exc) {
//      return null;
//    }
//  }
//
//  public void log(byte level, String message) {
//    try {
//      location.logT(logInterface.getMappedProviderLevel(level) + level, message);  //logs with level sap level + engine level
//    } catch (NullPointerException exc) {
//      //do nothing - may be thrown if the logger is already destroyed
//    }
//  }
//
//  public void log(byte level, byte[] message) {
//    log(level, new String(message));
//  }
//
//  public void log(byte level, byte[] message, int off, int len) {
//    log(level, new String(message, off, len));
//  }
//
//  public void log(byte level, String message, String user, String clientIp) {
//    if (user != null) {
//      message+=(LOG_RECORD_DELIMITER + "user=" + user);
//    }
//
//    if (clientIp != null) {
//      message+=(LOG_RECORD_DELIMITER + "clientIp=" + clientIp);
//    }
//
//    log(level, message);
//  }
//
//  public void log(byte level, byte[] message, String user, String clientIp) {
//    String strMessage = new String(message);
//    if (user != null) {
//      strMessage+=(LOG_RECORD_DELIMITER + "user=" + user);
//    }
//
//    if (clientIp != null) {
//      strMessage+=(LOG_RECORD_DELIMITER + "clientIp=" + clientIp);
//    }
//
//    log(level, strMessage);
//  }
//
//  public void log(byte level, byte[] message, byte[] user, byte[] clientIp) {
//    String strMessage = new String(message);
//
//    if (user != null) {
//      String strUser = new String(user);
//      strMessage+=(LOG_RECORD_DELIMITER + "user=" + strUser);
//    }
//
//    if (clientIp != null) {
//      String strClientIp = new String(clientIp);
//      strMessage+=(LOG_RECORD_DELIMITER + "clientIp=" + strClientIp);
//    }
//
//    log(level, strMessage);
//  }
//
//  public void log(byte level, byte[] message, int off, int len, byte[] user, byte[] clientIp) {
//    String strMessage = new String(message, off, len);
//
//    if (user != null) {
//      String strUser = new String(user);
//      strMessage+=(LOG_RECORD_DELIMITER + "user=" + strUser);
//    }
//
//    if (clientIp != null) {
//      String strClientIp = new String(clientIp);
//      strMessage+=(LOG_RECORD_DELIMITER + "clientIp=" + strClientIp);
//    }
//
//    log(level, strMessage);
//  }
//
//  public void logThrowable(byte level, Throwable t) {
//    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//    t.printStackTrace(new PrintStream(byteStream));
//    log(level, byteStream.toString());
//  }
//
//  public void logThrowable(byte level, Throwable t, String user, String clientIp) {
//    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//    t.printStackTrace(new PrintStream(byteStream));
//    String message = byteStream.toString();
//
//    if (user != null) {
//      message+=(LOG_RECORD_DELIMITER + "user=" + user);
//    }
//
//    if (clientIp != null) {
//      message+=(LOG_RECORD_DELIMITER + "clientIp=" + clientIp);
//    }
//
//    log(level, message);
//  }
//
//  public void log(LogRecord record) {
//    byte level = record.getLevel();
//    String message = record.getMessage();
//    String user = record.getUser();
//    String clientIp = record.getClientIP();
//
//    if (user != null) {
//      message+=(LOG_RECORD_DELIMITER + "user=" + user);
//    }
//
//    if (clientIp != null) {
//      message+=(LOG_RECORD_DELIMITER + "clientIp=" + clientIp);
//    }
//
//    String[] additionalFields = record.getAdditionalFieldNames();
//    for (int i = 0; i < additionalFields.length; i++) {
//      message+=(LOG_RECORD_DELIMITER + additionalFields[i] + "=" + record.getAdditionalFieldValue(additionalFields[i]));
//    }
//
//    log(level, message);
//  }
//
//  public boolean flush() {
//    try {
//      destination.flush();
//      return true;
//    } catch (Exception exc) {
//      return false;
//    }
//  }
//
//  public MultiFileLog getMultiFileLog() {
//    return destination;
//  }


  //Empty implementation
  public String getName() {
    return null;
  }

  public boolean setLoggingProperty(String key, String value) {
    return false;
  }

  public String getLoggingProperty(String key) {
    return null;
  }

  public Properties getLoggingProperties() {
    return null;
  }

  public void log(byte level, String message) {
  }

  public void log(byte level, byte[] message) {
  }

  public void log(byte level, byte[] message, int off, int len) {
  }

  public void log(byte level, String message, String user, String clientIp) {
  }

  public void log(byte level, byte[] message, String user, String clientIp) {
  }

  public void log(byte level, byte[] message, byte[] user, byte[] clientIp) {
  }

  public void log(byte level, byte[] message, int off, int len, byte[] user, byte[] clientIp) {
  }

  public void logThrowable(byte level, Throwable t) {
  }

  public void logThrowable(byte level, Throwable t, String user, String clientIp) {
  }

  public void log(LogRecord record) {
  }

  public boolean flush() {
    return false;
  }
}
