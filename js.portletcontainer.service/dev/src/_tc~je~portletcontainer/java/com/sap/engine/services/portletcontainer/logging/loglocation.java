package com.sap.engine.services.portletcontainer.logging;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.LogRecord;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;
import com.sap.engine.services.portletcontainer.LogContext;
/**
*
* To change the template for this generated type comment go to
* Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
* @author Diyan Yordanov
*/
public class LogLocation {
  private String locationName = null;
  private Location location = null;

  public LogLocation(String locationName) {
    this.locationName = locationName;
    location = Location.getLocation(locationName);
  } //end of constructor

  public Location getLocation() {
    return location;
  } //end of getLocation()

  public void trace(String msg) {
    if (location.beDebug() || location.bePath()) {
      location.pathT(LogContext.getExceptionStackTrace(new Exception(msg)));
    } else {
      location.infoT(msg);
    }
  } //end of trace(String msg)

  public void traceFatal(String msg, Throwable t) {
    location.fatalT(
      msg + " The error is: " + LogContext.getExceptionStackTrace(t));
  } //end of traceFatal(String msg, Throwable t)

  public void traceError(String msg) {
    location.errorT(msg);
  } //end of traceError(String msg)

  public void traceError(String msg, Throwable t) {
    location.errorT(
      msg + " The error is: " + LogContext.getExceptionStackTrace(t));
  } //end of traceError(String msg, Throwable t)

  public void traceWarning(String msg) {
    location.warningT(msg);
  } //end of traceWarning(String msg)

  public void traceWarning(String msg, Throwable t) {
    location.warningT(
      msg + " The error is: " + LogContext.getExceptionStackTrace(t));
  } //end of traceWarning(String msg, Throwable t)

  public void traceInfo(String msg) {
    location.infoT(msg);
  } //end of traceInfo(String msg)

  public void traceDebug(String msg) {
    location.debugT(msg);
  } //end of traceDebug(String msg)

  public void tracePath(String msg) {
    location.pathT(msg);
  } //end of tracePath(String msg)

  //new logging methods related to messegaID adoption
  //---------------------------- without message arguments -------------------------------------------

  /**
   * Traces an error message trough a certain location using the messageID concept.
   * 
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.web.000135
   * @param msg the message text itself
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String traceError(String msgID, String msg, String dcName, String csnComponent) {
    LogRecord logRecord = SimpleLogger.trace(Severity.ERROR, location, dcName, csnComponent, msgID, msg, null, new Object[0]);
    String logId = null;
    if (logRecord != null) {
      logId = logRecord.getId().toString();
    }

    return logId;
  }

  /**
   * Traces an error message trough a certain location using the messageID concept. It is especially meant to format and write exception stack traces.
   * 
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.web.000135
   * @param msg the message text itself
   * @param t if not null, the exception stack trace is formatted and appended at the end of the trace record as a message parameter
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String traceError(String msgID, String msg, Throwable t, String dcName, String csnComponent) {
    LogRecord logRecord = SimpleLogger.trace(Severity.ERROR, location, dcName, csnComponent, msgID, msg, t, new Object[0]);
    String logId = null;
    if (logRecord != null) {
      logId = logRecord.getId().toString();
    }

    return logId;
  }

  /**
   * Traces a warning message trough a certain location using the messageID concept.
   * 
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.web.000135
   * @param msg the message text itself
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String traceWarning(String msgID, String msg, String dcName, String csnComponent) {
    LogRecord logRecord = SimpleLogger.trace(Severity.WARNING, location, dcName, csnComponent, msgID, msg, null, new Object[0]);
    String logId = null;
    if (logRecord != null) {
      logId = logRecord.getId().toString();
    }

    return logId;
  }

  /**
   * Traces a warning message trough a certain location using the messageID concept. It is especially meant to format and write exception stack traces.
   * 
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.web.000135
   * @param msg the message text itself
   * @param t if not null, the exception stack trace is formatted and appended at the end of the trace record as a message parameter
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String traceWarning(String msgID, String msg, Throwable t, String dcName, String csnComponent) {
    LogRecord logRecord = SimpleLogger.trace(Severity.WARNING, location, dcName, csnComponent, msgID, msg, t, new Object[0]);
    String logId = null;
    if (logRecord != null) {
      logId = logRecord.getId().toString();
    }

    return logId;
  }

  // --------------------------------- with message arguments -------------------------------------------------

  /**
   * Traces an error message (with arguments) trough a certain location using the messageID concept.
   * 
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.web.000135
   * @param msg the message text itself
   * @param args values to be placed in the text message. Each of the message place-holders {0}, {1} will be replaced with args[0], args[1], etc.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String traceError(String msgID, String msg, Object[] args, String dcName, String csnComponent) {
    LogRecord logRecord = SimpleLogger.trace(Severity.ERROR, location, dcName, csnComponent, msgID, msg, null, args);
    String logId = null;
    if (logRecord != null) {
      logId = logRecord.getId().toString();
    }

    return logId;
  }

  /**
   * Traces an error message (with arguments) trough a certain location using the messageID concept. It is especially meant to format and write exception stack traces.
   * 
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.web.000135
   * @param msg the message text itself
   * @param args values to be placed in the text message. Each of the message place-holders {0}, {1} will be replaced with args[0], args[1], etc.
   * @param t if not null, the exception stack trace is formatted and appended at the end of the trace record as a message parameter
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String traceError(String msgID, String msg, Object[] args, Throwable t, String dcName, String csnComponent) {
    LogRecord logRecord = SimpleLogger.trace(Severity.ERROR, location, dcName, csnComponent, msgID, msg, t, args);
    String logId = null;
    if (logRecord != null) {
      logId = logRecord.getId().toString();
    }

    return logId;
  }

  /**
   * Traces a warning message (with arguments) trough a certain location using the messageID concept.
   * 
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.web.000135
   * @param msg the message text itself
   * @param args values to be placed in the text message. Each of the message place-holders {0}, {1} will be replaced with args[0], args[1], etc.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String traceWarning(String msgID, String msg, Object[] args, String dcName, String csnComponent) {
    LogRecord logRecord = SimpleLogger.trace(Severity.WARNING, location, dcName, csnComponent, msgID, msg, null, args);
    String logId = null;
    if (logRecord != null) {
      logId = logRecord.getId().toString();
    }

    return logId;
  }

  /**
   * Traces an error message (with arguments) trough a certain location using the messageID concept. It is especially meant to format and write exception stack traces.
   * 
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.web.000135
   * @param msg the message text itself
   * @param args values to be placed in the text message. Each of the message place-holders {0}, {1} will be replaced with args[0], args[1], etc.
   * @param t if not null, the exception stack trace is formatted and appended at the end of the trace record as a message parameter
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String traceWarning(String msgID, String msg, Object[] args, Throwable t, String dcName, String csnComponent) {
    LogRecord logRecord = SimpleLogger.trace(Severity.WARNING, location, dcName, csnComponent, msgID, msg, t, args);
    String logId = null;
    if (logRecord != null) {
      logId = logRecord.getId().toString();
    }

    return logId;
  }
}