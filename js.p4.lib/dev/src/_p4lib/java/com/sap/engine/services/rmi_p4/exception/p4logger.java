/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.rmi_p4.exception;

import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.tc.logging.*;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Copyright: Copyright (c) 2003
 * Company: SAP AG - Sap Labs Bulgaria
 *
 * @version 7.0
 */
public class P4Logger {

  private static P4ObjectBroker broker = null;
  private static String DEFAULT_TRACE_FILENAME = "p4_client_trace.log";
  private static int DEFAULT_CLIENT_SEVERITY = P4ExceptionConstants.SEVERITY_ERROR;
  public static final String TRACE_DESTINATION = "p4.client.trace.destination";
  public static final String CLIENT_SEVERITY = "p4.client.trace.severity";
  public static final String LOG_FORMAT = "p4.client.trace.format";
  public static final String DUMP_MESSAGES = "p4.trace.messages";
  public static final String CLIENT_CONSOLE_LOG = "consolelog";
  public static final String DEFAULT_LOG_FORMAT = "%d [%p]:%8s: %30t%-52l:    %m";

  private static Category category = Category.getCategory(P4ResourceAccessor.CATEGORY);
  private static Location location = Location.getLocation(P4ResourceAccessor.LOCATION_PATH);
  private static Location secLocation = Location.getLocation(P4ResourceAccessor.SEC_LOCATION_PATH);
  private static Location namLocation = Location.getLocation(P4ResourceAccessor.NAM_LOCATION_PATH);
  private static String formatString = DEFAULT_LOG_FORMAT;
  protected static boolean prepared = false;
  protected static boolean dumpMessages = false;
  private static Properties logProps = null;
  
  public static final int FATAL = Severity.FATAL;
  public static final int ERROR = Severity.ERROR;
  public static final int WARNING = Severity.WARNING;
  public static final int INFO = Severity.INFO;
  public static final int PATH = Severity.PATH;
  public static final int DEBUG = Severity.DEBUG;
  public static final int ALL = Severity.ALL;

  public static void setCategory(Category category) {
    P4Logger.category = category;
  }

  public static void setLocation(Location location) {
    P4Logger.location = location;
  }

  public static Location getLocation(){
    if(location == null){
      location = Location.getLocation(P4ResourceAccessor.LOCATION_PATH);
    }
    return location;
  }
  
  public static Location getSecLocation(){
    if(secLocation == null){
      secLocation = Location.getLocation(P4ResourceAccessor.SEC_LOCATION_PATH);
    }
    return secLocation;
  }
  
  public static Location getNamLocation(){
    if(namLocation == null){
      namLocation = Location.getLocation(P4ResourceAccessor.NAM_LOCATION_PATH);
    }
    return namLocation;
  }

  public static Category getCategory(){
    if(category == null){
      category = Category.getCategory(P4ResourceAccessor.CATEGORY);
    }
    return category;
  }

  public synchronized static void setLogProperty(Properties logPr) {
    if (broker != null && broker.getClass().getName().equals(P4ObjectBroker.CLIENT_BROKER_CLASS) && (logPr != null)) {
      location = Location.getLocation(P4ResourceAccessor.CLIENT_LOCATION);//can be changes
      if (logPr.containsKey(TRACE_DESTINATION)) {
        if (logPr.getProperty(TRACE_DESTINATION).equalsIgnoreCase(CLIENT_CONSOLE_LOG)) {
          location.removeLogs();
          location.addLog(new ConsoleLog(new TraceFormatter(formatString))); // $JL-CONSOLE_LOG$ $JL-LOG_CONFIG$ only if configurated explicitly
        } else if (logPr.getProperty(TRACE_DESTINATION).equalsIgnoreCase("default")) {
          location.addLog(new FileLog(DEFAULT_TRACE_FILENAME, true));  //$JL-LOG_CONFIG$
        } else {
          location.addLog(new FileLog(logPr.getProperty(TRACE_DESTINATION))); //$JL-LOG_CONFIG$
        }
      }
      if (logPr.containsKey(CLIENT_SEVERITY)) {
        location.setEffectiveSeverity(Integer.parseInt(logPr.getProperty(CLIENT_SEVERITY))); //$JL-LOG_CONFIG$
      }
    }
  }

  public synchronized static void configureDefaultP4Logging() {
   if (P4ObjectBroker.getBroker() == null || !P4ObjectBroker.getBroker().isServerBroker()) {
      location = Location.getLocation(P4ResourceAccessor.CLIENT_LOCATION);//can be changed
      logProps = new Properties();
      logProps.setProperty("formatter[HumanReadable]", "TraceFormatter");
      logProps.setProperty("formatter[HumanReadable].pattern", "%d [%p]: %t%l: %m");

      logProps.setProperty("log[trc]", "FileLog");
      logProps.setProperty("log[trc].formatter", "formatter[HumanReadable]");
      logProps.setProperty("log[trc].pattern", DEFAULT_TRACE_FILENAME);
      logProps.setProperty("log[trc].limit", "10000000");
      logProps.setProperty("log[trc].cnt", "5");

      logProps.setProperty("com.sap.engine.services.rmi_p4.logs", "log[trc]");
      logProps.setProperty("com.sap.engine.services.rmi_p4.severity", "ERROR");
      new PropertiesConfigurator(logProps).configure();//$JL-CONSOLE_LOG$
    }
  }

  public static Properties getProperties() {
    return logProps;
  }

  public synchronized static void updateP4Logging(Properties prop) {
    logProps = prop;
    (new PropertiesConfigurator(logProps)).configure();  //$JL-CONSOLE_LOG$
  }

  public synchronized static void prepare() {
    if (P4ObjectBroker.getImplName().equals(P4ObjectBroker.CLIENT_BROKER_CLASS)) {
      Properties logPr = System.getProperties();
      location = Location.getLocation(P4ResourceAccessor.CLIENT_LOCATION);//can be changed
      category = Category.getCategory(P4ResourceAccessor.CATEGORY);
      if (logPr.containsKey(LOG_FORMAT)) {
        formatString = logPr.getProperty(LOG_FORMAT);
      }
      if (logPr.containsKey(TRACE_DESTINATION)) {
        if (logPr.getProperty(TRACE_DESTINATION).equalsIgnoreCase("default")) {
          location.addLog(new FileLog(DEFAULT_TRACE_FILENAME, new TraceFormatter(formatString), true));  //$JL-LOG_CONFIG$
        } else {
          location.addLog(new FileLog(logPr.getProperty(TRACE_DESTINATION), new TraceFormatter(formatString), true)); //$JL-LOG_CONFIG$
        }
      } else if (System.getenv(TRACE_DESTINATION) != null) {
        String destination = System.getenv(TRACE_DESTINATION);
        if ("default".equals(destination)) {
          location.addLog(new FileLog(DEFAULT_TRACE_FILENAME, new TraceFormatter(formatString), true));  //$JL-LOG_CONFIG$
        } else {
          location.addLog(new FileLog(destination, new TraceFormatter(formatString), true)); //$JL-LOG_CONFIG$
        }
      } else {
        location.addLog(new ConsoleLog(new TraceFormatter(formatString))); //$JL-CONSOLE_LOG$
      }
      if (logPr.containsKey(CLIENT_SEVERITY)) {
        String severity = parseSeverity(logPr.getProperty(CLIENT_SEVERITY));
        location.setEffectiveSeverity(Integer.parseInt(severity)); //$JL-LOG_CONFIG$
      } else if (Boolean.getBoolean("debug")|| "true".equalsIgnoreCase(System.getenv("debug")) || "DEBUG".equalsIgnoreCase(System.getenv(CLIENT_SEVERITY))) { //backwards compatibility with -Ddebug=true
        location.setEffectiveSeverity(Severity.DEBUG);
      } else {
        location.setEffectiveSeverity(DEFAULT_CLIENT_SEVERITY); //$JL-LOG_CONFIG$
      }
    } else {
      if (P4ResourceAccessor.location == null || location == null) {
        location = Location.getLocation(P4ResourceAccessor.LOCATION_PATH);
        P4ResourceAccessor.location = location;
      }
      if (secLocation == null) {
        secLocation = Location.getLocation(P4ResourceAccessor.SEC_LOCATION_PATH);
      }
      if (namLocation == null) {
        namLocation = Location.getLocation(P4ResourceAccessor.NAM_LOCATION_PATH);
      }
      if (P4ResourceAccessor.category == null || category == null) {
        category = Category.getCategory(P4ResourceAccessor.CATEGORY);
        P4ResourceAccessor.category = category;
      }
    }
    if (Boolean.getBoolean(DUMP_MESSAGES)|| "true".equalsIgnoreCase(System.getenv(DUMP_MESSAGES))) {
      dumpMessages = true;
    }
    prepared = true;
  }

  public synchronized static void prepare(P4ObjectBroker broker) {
    P4Logger.broker = broker;
    if (!broker.isServerBroker()) {
     prepare();
    } else {
      if (P4ResourceAccessor.location == null || location == null) {
        location = Location.getLocation(P4ResourceAccessor.LOCATION_PATH);
        //Initialize Security and Naming sub-locations
        secLocation = Location.getLocation(P4ResourceAccessor.SEC_LOCATION_PATH);
        namLocation = Location.getLocation(P4ResourceAccessor.NAM_LOCATION_PATH);
        P4ResourceAccessor.location = location;
      }
      if (P4ResourceAccessor.category == null || category == null) {
        category = Category.getCategory(P4ResourceAccessor.CATEGORY);
        P4ResourceAccessor.category = category;
      }
      if (Boolean.getBoolean(DUMP_MESSAGES)) {
        dumpMessages = true;
      }
      prepared = true;
    }
  }

 public synchronized static boolean isPrepared() {
    return prepared;
  }

public static boolean dumpMessages() {
    return dumpMessages;
  }

 private static String parseSeverity(String severity) {
    if (severity.equalsIgnoreCase("ERROR")) {
      return String.valueOf(Severity.ERROR);
    } else if (severity.equalsIgnoreCase("WARNING")) {
      return String.valueOf(Severity.WARNING);
    } else if (severity.equalsIgnoreCase("INFO")) {
      return String.valueOf(Severity.INFO);
    } else if (severity.equalsIgnoreCase("PATH")) {
      return String.valueOf(Severity.PATH);
    } else if (severity.equalsIgnoreCase("DEBUG")) {
      return String.valueOf(Severity.DEBUG);
    } if (severity.equalsIgnoreCase("ALL")) {
      return String.valueOf(Severity.ALL);
    } else if (severity.equalsIgnoreCase("")) {
      return String.valueOf(Severity.ERROR);
    } else if (severity.equalsIgnoreCase("NONE")) {
      return String.valueOf(Severity.NONE);
    } else {
      return severity;
    }
  }
  /**
   * @deprecated That method exists only for compatibility. Do not use it. Regenerate old stubs and skeletons.
   */
  public static synchronized void traceDebug(Class c, String methodName, Throwable thr) {
    if (location != null) {
      location.debugT(c.getName() + " " + methodName, P4Logger.exceptionTrace(thr));
    }
  }

  public static String exceptionTrace(Throwable t) {
    java.io.ByteArrayOutputStream ostr = new java.io.ByteArrayOutputStream();
    t.printStackTrace(new java.io.PrintStream(ostr));
    return ostr.toString();
  }

  /**
   * @deprecated That method exists only for compatibility. Do not use it. Regenerate old stubs and skeletons.
   */
  public static synchronized void logDebug(String msg) {
    if (location != null) {
      location.debugT(msg);
    }
  }

  public static void changeClientSeverity(int severity) {
    location.setEffectiveSeverity(severity); //$JL-LOG_CONFIG$
  }
  
  /*
   * The following methods are integration with new logging.
   * It should provide functionality to log CSN component, DC name, 
   * and Message ID integration.
   */
  
  /**
   * Trace method wrapper that replace direct invocation from Location.
   * @param severity Severity of the trace. See Severity.ERROR, Severity.WARNING.
   * @param subLocation Class and Method where the trace comes from.
   * @param message Trace message.
   * @return LogRecord which is not used
   */
  public static LogRecord trace(int severity, String subLocation, String message){
    return SimpleLogger.trace (severity, getLocation(), subLocation + " : " + message);
  }
  
  public static LogRecord trace(int severity, Location loc, String subLocation, String message) {
    if (loc == null){
      loc = getLocation();
    }
    return SimpleLogger.trace (severity, getLocation(), subLocation + " : " + message);
  }
  
  /**
   * Trace method with message ID.
   * @param severity Severity of the trace.
   * @param subLocation Class and Method where the trace comes from.
   * @param message Trace message.
   * @param messageID Unique message ID.
   * @return LogRecord which is not used
   */
  public static LogRecord trace(int severity, String subLocation, String message, String messageID) {
    return SimpleLogger.trace (severity, getLocation(), messageID, subLocation + " : " + message);
  }
  
  public static LogRecord trace(int severity, Location loc, String subLocation, String message, String messageID) {
    if (loc == null){
      loc = getLocation();
    }
    return SimpleLogger.trace (severity, loc, messageID, subLocation + " : " + message);
  }
  
  /**
   * Trace method with messageID and parameters for the message
   * This method adds sub location on the begin of message body (separate with " : "). 
   * @param severity Severity of the trace.
   * @param subLocation Class and Method where the trace comes from.
   * @param message Trace message.
   * @param messageID Unique message ID.
   * @param args object array of message parameters.
   * @return LogRecord which is not used
   */
  public static LogRecord trace(int severity, String subLocation, String message, String messageID, Object[] args) {
    return SimpleLogger.trace(severity, getLocation(), messageID, subLocation + " : " + message, args);
  }

  public static LogRecord trace(int severity, Location loc,  String subLocation, String message, String messageID, Object[] args) {
    if (loc == null){
      loc = getLocation();
    }
    return SimpleLogger.trace(severity, loc, messageID, subLocation + " : " + message, args);
  }

  
  /**
   * Trace method with messageID and parameters for the message
   * This method adds sub location on the begin of message body (separate with " : "). 
   * @param severity Severity of the trace.
   * @param subLocation Class and Method where the trace comes from.
   * @param message Trace message.
   * @param messageID Unique message ID.
   * @param args object array of message parameters.
   * @param classLoader ClassLoader instance. It is used to get DC name for this trace.
   * @param throwable Throwable If not null, the exception stack trace is formatted and appended at the end of the trace record as a message parameter
   * @return LogRecord which is not used
   */
  public static LogRecord trace(int severity, String subLocation, String message, String messageID, Object[] args, ClassLoader classLoader, Throwable throwable) {
    return SimpleLogger.trace(severity, getLocation(), getDcNameByClassLoader(classLoader), null, messageID, subLocation + " : " + message, throwable, args);
  }
  
  /**
   * Trace method with message ID, DC Name, CSN component
   * @param severity Severity of the trace.
   * @param subLocation Class and Method where the trace comes from.
   * @param message Trace message.
   * @param messageID Unique message ID.
   * @param dcName DC name, equal to sda file without extension for services.
   * @param csnComponent Used if need to override default CSN component
   * @return LogRecord which is not used
   */
  public static LogRecord trace(int severity, String subLocation, String message, String messageID, String dcName, String csnComponent){
    return SimpleLogger.trace(severity, getLocation(), dcName, csnComponent, messageID, subLocation + " : " + message, null, new Object[]{});
  }  
  
  public static LogRecord trace(int severity, Location loc, String subLocation, String message, String messageID, String dcName, String csnComponent){
    if (loc == null){
      loc = getLocation();
    }
    return SimpleLogger.trace(severity, loc, dcName, csnComponent, subLocation + " : " + message, null, new Object[]{});
  }
  
  /**
   * Wrapper for logs without Category and Location. It gets default ones for P4. 
   * @param severity Severity of the trace.
   * @param dcName DC name, equal to sda file without extension for services.
   * @param csnComponent Used if need to override default CSN component
   * @param messageID Unique message ID.
   * @param message Trace message.
   * @return LogRecord which is not used
   */
  public static LogRecord log(int severity, String dcName, String  csnComponent, String messageID, String message){
    return SimpleLogger.log (severity, Category.SYS_SERVER, getLocation(), dcName, csnComponent, messageID, message);  
  }

  /**
   * Return the CSN component by class-loader name, if assigned. 
   * @param classLoaderName ClassLoader class name as String
   * @return CSN component for this classloader as String or  
   *         null if CSN component not found.
   * 
   * @See com.sap.engine.core.classload.impl0.ClassLoadRuntimeInfoProviderImpl.getCsnComponent(String)
   */
  public static String getCsnComponentByClassLoaderName(String classLoaderName){
    return LoggingUtilities.getCsnComponentByClassLoaderName(classLoaderName);
  }
  
  /**
   * Return the CSN component by class-loader instance, if assigned. 
   * @See com.sap.engine.core.classload.impl0.ClassLoadRuntimeInfoProviderImpl.getCsnComponent(ClassLoader)
   * 
   * @param classLoader ClassLoader class as Class instance
   * @return CSN component for this classloader as String or  
   *         null if CSN component not found.
   */
  public static String getCsnComponentByClassLoader(ClassLoader classLoader){
    return LoggingUtilities.getCsnComponentByClassLoader(classLoader);
  }
  
  /*
   * Returns the CSN component by DC name as String.
   * @param dcName DC name as vendor and component name
   * @See com.sap.engine.services.dc.frame.HookInitializer.getCsnComponentByDcName(String)
   */
  public static String getCsnComponentByDCName(String dcName){
    return LoggingUtilities.getCsnComponentByDCName(dcName);
  }
  
  /**
   * @See com.sap.engine.core.classload.impl0.ClassLoadRuntimeInfoProviderImpl.getDcName(ClassLoader)
   * @param classLoader ClassLoader instance
   * @return DC name for the component of this class-loader.
   */
  public static String getDcNameByClassLoader(ClassLoader classLoader){
    return LoggingUtilities.getDcNameByClassLoader(classLoader);
  }
  
  /*
   * @See com.sap.engine.core.classload.impl0.ClassLoadRuntimeInfoProviderImpl.getDcName(String)
   */
  public static String getDcNameByClassLoaderName(String classLoaderName){
    return LoggingUtilities.getDcNameByClassLoaderName(classLoaderName);
  }
  
}
