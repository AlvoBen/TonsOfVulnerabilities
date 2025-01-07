/*
 * Copyright (c) 2000-2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.sap.engine.services.httpserver.lib.ParseUtils;
import com.sap.engine.services.httpserver.lib.Responses;
import com.sap.engine.services.httpserver.exceptions.HttpException;
import com.sap.engine.services.httpserver.exceptions.HttpResourceAccessor;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.LogRecord;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/*
 *
 * @author Maria Jurova
 * @version 4.0
 */
public class Log {
  //Categories
  /**
   * Used when logging problems connected with HTTP Provider Service.
   */
  public final static Category CATEGORY_HTTP =
    Category.getCategory(Category.getRoot(), "System/Server");
  /**
   * Used ONLY for CHANGE LOG!!!
   */
  public final static Category CATEGORY_CHANGE_LOG_PROPERTIES =
    Category.getCategory(Category.getRoot(), "System/Changes/Properties");

  //Locations
  /**
   * Used when tracing problems connected with HTTP Provider service.
   */
  public final static Location LOCATION_HTTP =
    Location.getLocation("com.sap.engine.services.httpserver");
  /**
   * Dumps the request in the traces.
   * For severity "debug" the dump is in HEX format.
   * For severity "path" the dump is in String format.
   * For severity "info" the dump is in String formant, and only the HTTP headers are traced.
   */
  public static final Location LOCATION_HTTP_TRACE_REQUEST =
    Location.getLocation("com.sap.engine.services.httpserver.HttpTraceRequest");
  /**
   * Dumps the response in the traces.
   * For severity "debug" the dump is in HEX format.
   * For severity "path" the dump is in String format.
   * For severity "info" the dump is in String formant, and only the HTTP headers are traced.
   */
  public static final Location LOCATION_HTTP_TRACE_RESPONSE =
    Location.getLocation("com.sap.engine.services.httpserver.HttpTraceResponse");
  /**
   * Used when tracing problems connected with processing HTTP Request.
   */
  public static final Location LOCATION_HTTP_REQUEST =
    Location.getLocation("com.sap.engine.services.httpserver.HttpRequest"); 
  /**
   * Used when tracing problems connected with processing HTTP Response.
   */
  public static final Location LOCATION_HTTP_RESPONSE =
    Location.getLocation("com.sap.engine.services.httpserver.HttpResponse");
  /**
   * Used when tracing problems connected with HTTP Provider service MBeans.
   */
  
  public static final Location LOCATION_HTTP_MBEANS =
    Location.getLocation("com.sap.engine.services.httpserver.HttpMBeans");
  /**
   * Used when tracing problems connected with SSL attributes 
   */
  public static final Location LOCATION_HTTP_SSL_ATTRIBUTES =
    Location.getLocation("com.sap.engine.services.httpserver.SslAttributes");
  /**
   * Used when tracing Request memory consumption.
   */
  public static final Location LOCATION_MEMORY_STATISTIC =
    Location.getLocation("com.sap.engine.services.httpserver.HttpMemSatistic");
  /**
   * Used when tracing resource consumption management.
   */
  public static final Location LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT =
    Location.getLocation("com.sap.engine.services.httpserver.HttpRCM");
  
  public static final Location LOCATION_REUQEST_PRESERVATION=
	  Location.getLocation("com.sap.engine.services.http.RequestPreservation");
  /**
   * Used only for exceptions in <code>HttpResourceAccessor</code>.
   */
  private static final String LOCATION_EXCEPTIONS = "com.sap.engine.services.Http";
  /**
   * Used when tracing messages connected to session size calculation feature.
   */
  public static final Location LOCATION_HTTP_SESSION_SIZE =
    Location.getLocation("com.sap.engine.services.httpserver.sessionSize");
  /**
   * Indicated whether a detailed error response will be returned, or only the log ID of the problem.
   */
  private static boolean isDetailedErrorResponse = false;

  public static void init() {
    HttpResourceAccessor.init(Location.getLocation(LOCATION_EXCEPTIONS));
  }

  public static void setDetailetErrorResponse(boolean detailed) {
    isDetailedErrorResponse = detailed;
  }

  //MessageID Adoption related logging methods

  //logs without message arguments

  /**
   * Issues an info log message through CATEGORY_HTTP category. It is also dispatched to traces through LOCATION_HTTP location.
   * Makes use of Message ID concept.
   *
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void logInfo(String msgId, String msg, byte[] hostBytes, String dcName, String csnComponent) {
    // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    SimpleLogger.log(Severity.INFO, CATEGORY_HTTP, LOCATION_HTTP, dcName, csnComponent, msgId, getFirstLine(hostName) + msg, new Object[0]);
  }

  /**
   * Issues a warning log message through CATEGORY_HTTP category. It is also dispatched to traces through LOCATION_HTTP location.
   * Makes use of Message ID concept.
   *
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param hostBytes IP of the client's request.  It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void logWarning(String msgId, String msg, byte[] hostBytes, String dcName, String csnComponent) {
    // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    SimpleLogger.log(Severity.WARNING, CATEGORY_HTTP, LOCATION_HTTP, dcName, csnComponent, msgId, getFirstLine(hostName) + msg, new Object[0]);
  }

  /**
   * Issues a warning log message through CATEGORY_HTTP category. It is also dispatched to traces through LOCATION_HTTP location.
   * Makes use of Message ID concept.
   * Note that in this method a detailed error response is generated if this feature is switched on.
   *
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param t if not null, the exception stacktrace is formatted and appended at the end of the trace record as a message parameter
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * 
   * @return the log ID, or NULL if there is no log ID generated.
   */
  public static String logWarning(String msgId, String msg, Throwable ex, byte[] hostBytes, String dcName, String csnComponent) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    msg += getFirstLine(hostName);

    traceWarning(LOCATION_HTTP, msgId, msg, ex, hostBytes, dcName, csnComponent);

    // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
    LogRecord record = SimpleLogger.log(Severity.WARNING, CATEGORY_HTTP, LOCATION_HTTP, dcName, csnComponent, msgId, msg, new Object[0]);
    if (record == null) {
      return null;
    } else {
      return record.getId().toString();
    }
  }

  /**
   * Issues an error log message through CATEGORY_HTTP category. It is also dispatched to traces through LOCATION_HTTP location.
   * Makes use of Message ID concept.
   *
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void logError(String msgId, String msg, byte[] hostBytes, String dcName, String csnComponent) {
    // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    SimpleLogger.log(Severity.ERROR, CATEGORY_HTTP, LOCATION_HTTP, dcName, csnComponent, msgId, getFirstLine(hostName) + msg, new Object[0]);
  }

  /**
   * Issues an error log message through CATEGORY_HTTP category. It is also dispatched to traces through LOCATION_HTTP location.
   * Makes use of Message ID concept.
   *
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param t if not null, the exception stacktrace is formatted and appended at the end of the trace record as a message parameter
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void logError(String msgId, String msg, Throwable ex, byte[] hostBytes, String dcName, String csnComponent) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    msg += getFirstLine(hostName);

    traceError(LOCATION_HTTP, msgId, msg, ex, hostBytes, dcName, csnComponent);

    // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
    SimpleLogger.log(Severity.ERROR, CATEGORY_HTTP, LOCATION_HTTP, dcName, csnComponent, msgId, msg, new Object[0]);
  }

  /**
   * Issues a fatal log message through CATEGORY_HTTP category. It is also dispatched to traces through LOCATION_HTTP location.
   * Makes use of Message ID concept.
   *
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param t if not null, the exception stacktrace is formatted and appended at the end of the trace record as a message parameter
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void logFatal(String msgId, String msg, Throwable ex, byte[] hostBytes, String dcName, String csnComponent) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    msg += getFirstLine(hostName);

    // SimpleLogger.trace(int severity, Location location, String dcName, String csnComponent, String messageID, String message, Exception exc, Object... args)
    SimpleLogger.trace(Severity.FATAL, LOCATION_HTTP, dcName, csnComponent, msgId, msg, ex, new Object[0]);

    // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
    SimpleLogger.log(Severity.FATAL, CATEGORY_HTTP, LOCATION_HTTP, dcName, csnComponent, msgId, msg, new Object[0]);
  }

  /**
   * Issues a fatal log message through CATEGORY_HTTP category. It is also dispatched to traces through LOCATION_HTTP location.
   * Makes use of Message ID concept.
   *
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void logFatal(String msgId, String msg, byte[] hostBytes, String dcName, String csnComponent) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
    SimpleLogger.log(Severity.FATAL, CATEGORY_HTTP, LOCATION_HTTP, dcName, csnComponent, msgId, getFirstLine(hostName) + msg, new Object[0]);
  }

  // logs with message arguments

  /**
   * Issues an info log message through CATEGORY_HTTP category. It is also dispatched to traces through LOCATION_HTTP location.
   * Makes use of Message ID concept.
   *
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param args values to be placed in the text message. Each of the message placeholders {0}, {1} will be replaced with args[0], args[1], etc.
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void logInfo(String msgId, String msg, Object[] args, byte[] hostBytes, String dcName, String csnComponent) {
    // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    SimpleLogger.log(Severity.INFO, CATEGORY_HTTP, LOCATION_HTTP, dcName, csnComponent, msgId, getFirstLine(hostName) + msg, args);
  }

  /**
   * Issues a warning log message through CATEGORY_HTTP category. It is also dispatched to traces through LOCATION_HTTP location.
   * Makes use of Message ID concept.
   *
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param args values to be placed in the text message. Each of the message placeholders {0}, {1} will be replaced with args[0], args[1], etc.
   * @param hostBytes IP of the client's request.  It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void logWarning(String msgId, String msg, Object[] args, byte[] hostBytes, String dcName, String csnComponent) {
    // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    SimpleLogger.log(Severity.WARNING, CATEGORY_HTTP, LOCATION_HTTP, dcName, csnComponent, msgId, getFirstLine(hostName) + msg, args);
  }

  /**
   * Issues a warning log message through CATEGORY_HTTP category. It is also dispatched to traces through LOCATION_HTTP location.
   * Makes use of Message ID concept.
   * Note that in this method a detailed error response is generated if this feature is switched on.
   *
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param args values to be placed in the text message. Each of the message placeholders {0}, {1} will be replaced with args[0], args[1], etc.
   * @param t if not null, the exception stacktrace is formatted and appended at the end of the trace record as a message parameter
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * 
   * @return the log ID, or NULL if there is no log ID generated.
   */
  public static String logWarning(String msgId, String msg, Object[] args, Throwable ex, byte[] hostBytes, String dcName, String csnComponent) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    msg += getFirstLine(hostName);

    traceWarning(LOCATION_HTTP, msgId, msg, args, ex, hostBytes, dcName, csnComponent);

    // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
    LogRecord record = SimpleLogger.log(Severity.WARNING, CATEGORY_HTTP, LOCATION_HTTP, dcName, csnComponent, msgId, msg, args);
    if (record == null) {
      return null;
    } else {
      return record.getId().toString();
    }
  }

  /**
   * Issues an error log message through CATEGORY_HTTP category. It is also dispatched to traces through LOCATION_HTTP location.
   * Makes use of Message ID concept.
   *
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param args values to be placed in the text message. Each of the message placeholders {0}, {1} will be replaced with args[0], args[1], etc.
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void logError(String msgId, String msg, Object[] args, byte[] hostBytes, String dcName, String csnComponent) {
    // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    SimpleLogger.log(Severity.ERROR, CATEGORY_HTTP, LOCATION_HTTP, dcName, csnComponent, msgId, getFirstLine(hostName) + msg, args);
  }

  /**
   * Issues an error log message through CATEGORY_HTTP category. It is also dispatched to traces through LOCATION_HTTP location.
   * Makes use of Message ID concept.
   *
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param args values to be placed in the text message. Each of the message placeholders {0}, {1} will be replaced with args[0], args[1], etc.
   * @param t if not null, the exception stacktrace is formatted and appended at the end of the trace record as a message parameter
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void logError(String msgId, String msg, Object[] args, Throwable ex, byte[] hostBytes, String dcName, String csnComponent) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    msg += getFirstLine(hostName);

    traceError(LOCATION_HTTP, msgId, msg, args, ex, hostBytes, dcName, csnComponent);

    // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
    SimpleLogger.log(Severity.ERROR, CATEGORY_HTTP, LOCATION_HTTP, dcName, csnComponent, msgId, msg, args);
  }

  /**
   * Issues a fatal log message through CATEGORY_HTTP category. It is also dispatched to traces through LOCATION_HTTP location.
   * Makes use of Message ID concept.
   *
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param args values to be placed in the text message. Each of the message placeholders {0}, {1} will be replaced with args[0], args[1], etc.
   * @param t if not null, the exception stacktrace is formatted and appended at the end of the trace record as a message parameter
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void logFatal(String msgId, String msg, Object[] args, Throwable ex, byte[] hostBytes, String dcName, String csnComponent) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    msg += getFirstLine(hostName);

    // SimpleLogger.trace(int severity, Location location, String dcName, String csnComponent, String messageID, String message, Exception exc, Object... args)
    SimpleLogger.trace(Severity.FATAL, LOCATION_HTTP, dcName, csnComponent, msgId, msg, ex, args);

    // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
    SimpleLogger.log(Severity.FATAL, CATEGORY_HTTP, LOCATION_HTTP, dcName, csnComponent, msgId, msg, args);
  }

  /**
   * Issues a fatal log message through CATEGORY_HTTP category. It is also dispatched to traces through LOCATION_HTTP location.
   * Makes use of Message ID concept.
   *
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix]-[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param args values to be placed in the text message. Each of the message placeholders {0}, {1} will be replaced with args[0], args[1], etc.
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void logFatal(String msgId, String msg, Object[] args, byte[] hostBytes, String dcName, String csnComponent) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    msg += getFirstLine(hostName);
    // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
    SimpleLogger.log(Severity.FATAL, CATEGORY_HTTP, LOCATION_HTTP, dcName, csnComponent, msgId, msg, args);
  }

  //traces without arguments

  /**
   * Traces an error message trough a certain location using the messageID concept. It is especially meant to format and write exception stacktraces.
   *
   * @param location location where the message will be dispatched to
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix]-[range][number] (e.g. ASJ-web-000135
   * @param msg the message text itself
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param t if not null, the exception stacktrace is formatted and appended at the end of the trace record as a message parameter
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void traceError(Location location, String msgId, String msg, Throwable ex, byte[] hostBytes, String dcName, String csnComponent) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    // SimpleLogger.trace(int severity, Location location, String dcName, String csnComponent, String messageID, String message, Exception exc, Object... args)
    SimpleLogger.trace(Severity.ERROR, location, dcName, csnComponent, msgId, getFirstLine(hostName) + msg, ex, new Object[0]);
  }

  /**
   * Traces an error message trough a certain location using the messageID concept. It is especially meant to format and write exception stacktraces.
   *
   * @param location location where the message will be dispatched to
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix]-[range][number] (e.g. ASJ-web-000135
   * @param msg the message text itself
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void traceError(Location location, String msgId, String msg, byte[] hostBytes, String dcName, String csnComponent) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    // SimpleLogger.trace(int severity, Location location, String dcName, String csnComponent, String messageID, String message, Exception exc, Object... args)
    SimpleLogger.trace(Severity.ERROR, location, dcName, csnComponent, msgId, getFirstLine(hostName) + msg, null, new Object[0]);
  }

  /**
   * Traces a warning message trough a certain location using the messageID concept. It is especially meant to format and write exception stacktraces.
   *
   * @param location location where the message will be dispatched to
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix]-[range][number] (e.g. ASJ-web-000135
   * @param msg the message text itself
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param t if not null, the exception stacktrace is formatted and appended at the end of the trace record as a message parameter
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void traceWarning(Location location, String msgId, String msg, Throwable ex, byte[] hostBytes, String dcName, String csnComponent) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    // SimpleLogger.trace(int severity, Location location, String dcName, String csnComponent, String messageID, String message, Exception exc, Object... args)
    SimpleLogger.trace(Severity.WARNING, location, dcName, csnComponent, msgId, getFirstLine(hostName) + msg, ex, new Object[0]);
  }

  /**
   * Traces a warning message trough a certain location using the messageID concept. It is especially meant to format and write exception stacktraces.
   *
   * @param location location where the message will be dispatched to
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix]-[range][number] (e.g. ASJ-web-000135
   * @param msg the message text itself
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void traceWarning(Location location, String msgId, String msg, byte[] hostBytes, String dcName, String csnComponent) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    // SimpleLogger.trace(int severity, Location location, String dcName, String csnComponent, String messageID, String message, Exception exc, Object... args)
    SimpleLogger.trace(Severity.WARNING, location, dcName, csnComponent, msgId, getFirstLine(hostName) + msg, null, new Object[0]);
  }

  //traces with arguments

  /**
   * Traces an error message trough a certain location using the messageID concept. It is especially meant to format and write exception stacktraces.
   *
   * @param location location where the message will be dispatched to
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix]-[range][number] (e.g. ASJ-web-000135
   * @param msg the message text itself
   * @param args values to be placed in the text message. Each of the message placeholders {0}, {1} will be replaced with args[0], args[1], etc.
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param t if not null, the exception stacktrace is formatted and appended at the end of the trace record as a message parameter
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void traceError(Location location, String msgId, String msg, Object[] args, Throwable ex, byte[] hostBytes, String dcName, String csnComponent) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    // SimpleLogger.trace(int severity, Location location, String dcName, String csnComponent, String messageID, String message, Exception exc, Object... args)
    SimpleLogger.trace(Severity.ERROR, location, dcName, csnComponent, msgId, getFirstLine(hostName) + msg, ex, args);
  }

  /**
   * Traces an error message trough a certain location using the messageID concept. It is especially meant to format and write exception stacktraces.
   *
   * @param location location where the message will be dispatched to
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix]-[range][number] (e.g. ASJ-web-000135
   * @param msg the message text itself
   * @param args values to be placed in the text message. Each of the message placeholders {0}, {1} will be replaced with args[0], args[1], etc.
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void traceError(Location location, String msgId, String msg, Object[] args, byte[] hostBytes, String dcName, String csnComponent) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    // SimpleLogger.trace(int severity, Location location, String dcName, String csnComponent, String messageID, String message, Exception exc, Object... args)
    SimpleLogger.trace(Severity.ERROR, location, dcName, csnComponent, msgId, getFirstLine(hostName) + msg, null, args);
  }

  /**
   * Traces a warning message trough a certain location using the messageID concept. It is especially meant to format and write exception stacktraces.
   *
   * @param location location where the message will be dispatched to
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix]-[range][number] (e.g. ASJ-web-000135
   * @param msg the message text itself
   * @param args values to be placed in the text message. Each of the message placeholders {0}, {1} will be replaced with args[0], args[1], etc.
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param t if not null, the exception stacktrace is formatted and appended at the end of the trace record as a message parameter
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void traceWarning(Location location, String msgId, String msg, Object[] args, Throwable ex, byte[] hostBytes, String dcName, String csnComponent) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    // SimpleLogger.trace(int severity, Location location, String dcName, String csnComponent, String messageID, String message, Exception exc, Object... args)
    SimpleLogger.trace(Severity.WARNING, location, dcName, csnComponent, msgId, getFirstLine(hostName) + msg, ex, args);
  }

  /**
   * Traces a warning message trough a certain location using the messageID concept. It is especially meant to format and write exception stacktraces.
   *
   * @param location location where the message will be dispatched to
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix]-[range][number] (e.g. ASJ-web-000135
   * @param msg the message text itself
   * @param args values to be placed in the text message. Each of the message placeholders {0}, {1} will be replaced with args[0], args[1], etc.
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void traceWarning(Location location, String msgId, String msg, Object[] args, byte[] hostBytes, String dcName, String csnComponent) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    // SimpleLogger.trace(int severity, Location location, String dcName, String csnComponent, String messageID, String message, Exception exc, Object... args)
    SimpleLogger.trace(Severity.WARNING, location, dcName, csnComponent, msgId, getFirstLine(hostName) + msg, null, args);
  }

  /**
   * Traces a path message trough a certain location. It is especially meant to format and write exception stacktraces.
   *
   * @param location location where the message will be dispatched to
   * @param msg the message text itself
   * @param clientId  the id accociated to the client
   * @param callerClass  the name of the class which makes the trace
   * @param method  the name of the method which makes the trace 
   */
  public static void tracePath(Location location, String msg, int clientId, String callerClass, String method) {
    //SimpleLogger.trace(Severity.PATH, location, dcName, csnComponent, msgId, "client [" + clientId + "] " + callerClass + "." + method + "(): " + msg, null, args);      
    location.pathT("client [" + clientId + "] " + callerClass + "." + method + "(): " + msg);    
  }
  
  /**
   * Traces a path message trough a certain location. It is especially meant to format and write exception stacktraces.
   *
   * @param location location where the message will be dispatched to
   * @param msg the message text itself
   * @param exc exception
   * @param clientId  the id accociated to the client
   * @param callerClass  the name of the class which makes the trace
   * @param method  the name of the method which makes the trace 
   */
  public static void tracePath(Location location, String msg, Throwable exc, int clientId, String callerClass, String method) {
    //SimpleLogger.trace(Severity.PATH, location, dcName, csnComponent, msgId, "client [" + clientId + "] " + callerClass + "." + method + "(): " + msg, null, args);
    location.pathT("client [" + clientId + "] " + callerClass + "." + method + ": " + msg + "The exception is: " + getExceptionStackTrace(exc));   
  }
    
  /**
   * Traces an info message trough a certain location using SimpleLogger
   *
   * @param location location where the message will be dispatched to   
   * @param msg the message text itself
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void traceInfo(Location location, String msg, byte[] hostBytes) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    // SimpleLogger.trace(int severity, Location location, String dcName, String csnComponent, String messageID, String message, Exception exc, Object... args)
    SimpleLogger.trace(Severity.INFO, location, getFirstLine(hostName) + msg);
  }
  
  /**
   * Traces an info message trough a certain location using SimpleLogger. It is especially meant to format and write exception stacktraces.
   *
   * @param location location where the message will be dispatched to  
   * @param msg the message text itself
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param ex if not null, the exception stacktrace is formatted and appended at the end of the trace record as a message parameter
   */
  public static void traceInfo(Location location, String msg, Throwable ex, byte[] hostBytes) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    // SimpleLogger.trace(int severity, Location location, String dcName, String csnComponent, String messageID, String message, Exception exc, Object... args)
    SimpleLogger.trace(Severity.WARNING, location, getFirstLine(hostName) + msg, ex);
  }
  
  /**
   * Traces an info message trough a certain location using the messageID concept. It is especially meant to format and write exception stacktraces.
   *
   * @param location location where the message will be dispatched to
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix]-[range][number] (e.g. ASJ-web-000135
   * @param msg the message text itself
   * @param args values to be placed in the text message. Each of the message placeholders {0}, {1} will be replaced with args[0], args[1], etc.
   * @param hostBytes IP of the client's request. It is added in front of the message.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public static void traceInfo(Location location, String msgId, String msg, Object[] args, byte[] hostBytes, String dcName, String csnComponent) {
    byte[] hostName = ParseUtils.inetAddressByteToString(hostBytes);
    // SimpleLogger.trace(int severity, Location location, String dcName, String csnComponent, String messageID, String message, Exception exc, Object... args)
    SimpleLogger.trace(Severity.INFO, location, dcName, csnComponent, msgId, getFirstLine(hostName) + msg, null, args);
  }
  
  public static String getExceptionStackTrace(Throwable t) {
    ByteArrayOutputStream ostr = new ByteArrayOutputStream();
    t.printStackTrace(new PrintStream(ostr));
    return ostr.toString();
  }

  private static String getFirstLine(byte[] hostName) {
    if (hostName == null) {
      hostName = "<internal>".getBytes();
    }
    return "IP address " + new String(hostName) + "\r\n";
  }

  private static String formatException(String exc) {
    return Responses.toHtmlView(exc);
  }

  /**
   * 
   * @param t the exception that must be formated
   * @param logID the log ID of the generated log entry
   * 
   * @return the formated exception
   */
  public static String formatException(Throwable t, String logID) {
    if (isDetailedErrorResponse) {
      return formatException(getExceptionStackTrace(t));
    }
    
    //The record can be NULL ONLY if somebody changes the default severity, because for the LOG the default severity is INFO i.e. always must be a record returned.
    HttpException http_ex;
    if (logID == null) {
      http_ex = new HttpException(HttpException.HTTP_LOG_ID_NULL, new Object[]{LOCATION_HTTP.getName()});
    } else {
      //This check indicates whether log viewer application exists.
      //If it is existing then a link to it with filter log id will be added to the browser.
      if (ServiceContext.getServiceContext().getHttpProvider().containsApplicationAlias(Constants.LOG_VIEWER_ALIAS_NAME)) {
        logID = "<a href=" + Constants.LOG_VIEWER_ALIAS_NAME + "/LVApp?conn=filter[Log_ID:" + logID + "]view[SAP%20Logs%20(Java)]>" + logID + "</a>";
      }
      http_ex = new HttpException(HttpException.HTTP_LOG_ID, new Object[]{logID});
    }
    return http_ex.getLocalizedMessage();    
  }//end of formatException(Throwable t, String logID, String messageID)

}
