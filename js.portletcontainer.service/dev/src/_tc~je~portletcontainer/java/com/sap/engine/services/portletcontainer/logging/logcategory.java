package com.sap.engine.services.portletcontainer.logging;
import com.sap.tc.logging.Category;
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
public class LogCategory {
  private String categoryName = null;
  private Category category = null;
  private int LOCATION = -1;
  
  public Category getCategory() {
    return category;
  }

  public LogCategory(String categoryName, int location) {
    this.categoryName = categoryName;
    this.LOCATION = location;
    category = Category.getCategory(Category.getRoot(), categoryName);
  } //end of constructor

  public void logFatal(Location location, String msg) {
    category.fatalT(location, msg);
  } //end of logFatal(Location location, String msg)

  public void logFatal(Location location, String msg, Throwable error) {
    category.fatalT(location, msg + " The error is: " + error.getMessage());
    LogContext.getLocation(LOCATION).traceFatal(msg, error);
  } //end of logFatal(Location location, String msg, Throwable error)

  public void logError(Location location, String msg) {
    category.errorT(location, msg);
  } //end of logError(Location location, String msg)

  public void logError(Location location, String msg, Throwable t) {
    if (t == null) {
      category.errorT(location, msg);
    } else {
      category.errorT(location, msg + " The error is: " + t.getMessage());
      LogContext.getLocation(LOCATION).traceError(msg, t);
    }
  } //end of logError(Location location, String msg, Throwable t)

  public void logWarning(Location location, String msg) {
    category.warningT(location, msg);
  } //end of logWarning(Location location, String msg)

  public void logWarning(Location location, String msg, Throwable t) {
    if (t == null) {
      category.warningT(location, msg);
    } else {
      category.warningT(location, msg + " The error is: " + t.getMessage());
      LogContext.getLocation(LOCATION).traceWarning(msg, t);
    }
  } //end of logWarning(Location location, String msg, Throwable t)

  public void logInfo(Location location, String msg) {
    category.infoT(location, msg);
  } //end of logInfo(Location location, String msg)

  /**
   * Issues a fatal log message through a certain category. The message is also forwarded to the traces through the location specified.
   * Uses Message ID concept.
   * 
   * @param location through this location the log message is forwarded to the traces
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String logFatal(Location location, String msgId, String msg, String dcName, String csnComponent) {
    LogRecord logRecord = SimpleLogger.log(Severity.FATAL, category, location, dcName, csnComponent, msgId, msg, new Object[0]);
    String logId = null;
    if (logRecord != null) {
      logId = logRecord.getId().toString();
    }

    return logId;
  }

  /**
   * Issues a fatal log message through a certain category. The message is also forwarded to the traces through the location specified. 
   * Uses Message ID concept.
   * 
   * @param location through this location the log message and optionally the stack trace of the throwable (if any) is forwarded to the traces
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param t if not null, the exception stack trace is formatted and appended at the end of the trace record as a message parameter
   * @param message the message text itself
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String logFatal(Location location, String msgId, String msg, Throwable t, String dcName, String csnComponent) {
    if (t == null) {
      return logFatal(location, msgId, msg, dcName, csnComponent);
    } else {
      // SimpleLogger.trace(int severity, Location location, String dcName, String csnComponent, String messageID, String message, Exception exc, Object... args)
      LogRecord logRecord = SimpleLogger.trace(Severity.FATAL, location, dcName, csnComponent, msgId, msg, t, new Object[0]);
      String logId = null;
      if (logRecord != null) {
        logId = logRecord.getId().toString();
      }

      // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
      SimpleLogger.log(Severity.FATAL, category, location, dcName, csnComponent, msgId, 
        msg + "For more details on the problem please check traces searching by logId: " + logId, new Object[0]);

      return logId;
    }
  }

  /**
   * Issues an error log message through a certain category. The message is also forwarded to the traces through the location specified.
   * Uses Message ID concept.
   * 
   * @param location through this location the log message is forwarded to the traces
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String logError(Location location, String msgId, String msg, String dcName, String csnComponent) {
    // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
    LogRecord logRecord = SimpleLogger.log(Severity.ERROR, category, location, dcName, csnComponent, msgId, msg, new Object[0]);
    String logId = null;
    if (logRecord != null) {
      logId = logRecord.getId().toString();
    }

    return logId;
  }

  /**
   * Issues an error log message through a certain category. The message is also forwarded to the traces through the location specified. 
   * Uses Message ID concept.
   * 
   * @param location through this location the log message and optionally the stack trace of the throwable (if any) is forwarded to the traces
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param t if not null, the exception stack trace is formatted and appended at the end of the trace record as a message parameter
   * @param message the message text itself
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String logError(Location location, String msgId, String msg, Throwable t, String dcName, String csnComponent) {
    if (t == null) {
      return logError(location, msgId, msg, dcName, csnComponent);
    } else {
      // SimpleLogger.trace(int severity, Location location, String dcName, String csnComponent, String messageID, String message, Exception exc, Object... args)
      LogRecord logRecord = SimpleLogger.trace(Severity.ERROR, location, dcName, csnComponent, msgId, msg, t, new Object[0]);
      String logId = null;
      if (logRecord != null) {
        logId = logRecord.getId().toString();
      }

      // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
      SimpleLogger.log(Severity.ERROR, category, location, dcName, csnComponent, msgId, 
        msg + "\r\nFor more details on the problem please check traces searching by logId: " + logId, new Object[0]);

      return logId;
    }
  }

  /**
   * Issues a warning log message through a certain category. The message is also forwarded to the traces through the location specified. 
   * Uses Message ID concept.
   * 
   * @param location through this location the log message is forwarded to the traces
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String logWarning(Location location, String msgId, String msg, String dcName, String csnComponent) {
    // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
    LogRecord logRecord = SimpleLogger.log(Severity.WARNING, category, location, dcName, csnComponent, msgId, msg, new Object[0]);
    String logId = null;
    if (logRecord != null) {
      logId = logRecord.getId().toString();
    }

    return logId;
  }

  /**
   * Issues a warning log message through a certain category. The message is also forwarded to the traces through the location specified. 
   * Uses Message ID concept.
   * 
   * @param location through this location the log message and optionally the stack trace of the throwable (if any) is forwarded to the traces 
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param t if not null, the exception stack trace is formatted and appended at the end of the trace record as a message parameter
   * @param message the message text itself
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String logWarning(Location location, String msgId, String msg, Throwable t, String dcName, String csnComponent) {
    if (t == null) {
      return logWarning(location, msgId, msg, dcName, csnComponent);
    } else {
      // SimpleLogger.trace(int severity, Location location, String dcName, String csnComponent, String messageID, String message, Exception exc, Object... args)
      LogRecord logRecord = SimpleLogger.trace(Severity.WARNING, location, dcName, csnComponent, msgId, msg, t, new Object[0]);
      String logId = null;
      if (logRecord != null) {
        logId = logRecord.getId().toString();
      }

      // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
      SimpleLogger.log(Severity.WARNING, category, location, dcName, csnComponent, msgId, 
        msg + "\r\nFor more details on the problem please check traces searching by logId: " + logId, new Object[0]);

      return logId;
    }
  }

  /**
   * Issues an info log message through a certain category. The message is also forwarded to the traces through the location specified. 
   * Uses Message ID concept.
   * 
   * @param location through this location the log message is forwarded to the traces
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public void logInfo(Location location, String msgId, String msg, String dcName, String csnComponent) {
    SimpleLogger.log(Severity.INFO, category, location, dcName, csnComponent, msgId, msg, new Object[0]);
  }

  //logs with arguments

  /**
   * Issues a fatal log message through a certain category. The message is also forwarded to the traces through the location specified.
   * Uses Message ID concept.
   * 
   * @param location through this location the log message is forwarded to the traces
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param args values to be placed at the end of the log entry as parameters.
   * Each of the message placeholders {0}, {1} will correspond to args[0], args[1], etc.
   * These placeholders will not be replaced in the plain text entry but will be replaced if logs are viewed with Log Viewer.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String logFatal(Location location, String msgId, String msg, Object[] args, String dcName, String csnComponent) {
    LogRecord logRecord = SimpleLogger.log(Severity.FATAL, category, location, dcName, csnComponent, msgId, msg, args);
    String logId = null;
    if (logRecord != null) {
      logId = logRecord.getId().toString();
    }

    return logId;
  }

  /**
   * Issues a fatal log message through a certain category. The message is also forwarded to the traces through the location specified.
   * Uses Message ID concept.
   * 
   * @param location through this location the log message and optionally the stack trace of the throwable (if any) is forwarded to the traces
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param t - if not null, the exception stack trace is formatted and appended at the end of the trace record as a message parameter
   * @param message the message text itself
   * @param args values to be placed at the end of the log entry as parameters.
   * Each of the message placeholders {0}, {1} will correspond to args[0], args[1], etc.
   * These placeholders will not be replaced in the plain text entry but will be replaced if logs are viewed with Log Viewer.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String logFatal(Location location, String msgId, String msg, Object[] args, Throwable t, String dcName, String csnComponent) {
    if (t == null) {
      return logFatal(location, msgId, msg, dcName, csnComponent);
    } else {
      LogRecord logRecord = SimpleLogger.trace(Severity.FATAL, location, dcName, csnComponent, msgId, msg, t, args);
      String logId = null;
      if (logRecord != null) {
        logId = logRecord.getId().toString();
      }

      // SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
      SimpleLogger.log(Severity.FATAL, category, location, dcName, csnComponent, msgId, 
        msg + "\r\nFor more details on the problem please check traces searching by logId: " + logId, args);

      return logId;
    }
  }

  /**
   * Issues an error log message through a certain category. The message is also forwarded to the traces through the location specified.
   * Uses Message ID concept.
   * 
   * @param location through this location the log message is forwarded to the traces
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param args values to be placed at the end of the log entry as parameters.
   * Each of the message placeholders {0}, {1} will correspond to args[0], args[1], etc.
   * These placeholders will not be replaced in the plain text entry but will be replaced if logs are viewed with Log Viewer.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String logError(Location location, String msgId, String msg, Object[] args, String dcName, String csnComponent) {
    LogRecord logRecord = SimpleLogger.log(Severity.ERROR, category, location, dcName, csnComponent, msgId, msg, args);
    String logId = null;
    if (logRecord != null) {
      logId = logRecord.getId().toString();
    }

    return logId;
  }

  /**
   * Issues an error log message through a certain category. The message is also forwarded to the traces through the location specified.
   * Uses Message ID concept.
   * @param location through this location the log message and optionally the stack trace of the throwable (if any) is forwarded to the traces
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param t - if not null, the exception stack trace is formatted and appended at the end of the trace record as a message parameter
   * @param message the message text itself
   * @param args values to be placed at the end of the log entry as parameters.
   * Each of the message placeholders {0}, {1} will correspond to args[0], args[1], etc.
   * These placeholders will not be replaced in the plain text entry but will be replaced if logs are viewed with Log Viewer.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String logError(Location location, String msgId, String msg, Object[] args, Throwable t, String dcName, String csnComponent) {
    if (t == null) {
      return logError(location, msgId, msg, dcName, csnComponent);
    } else {
      LogRecord logRecord = SimpleLogger.trace(Severity.ERROR, location, dcName, csnComponent, msgId, msg, t, args);
      String logId = null;
      if (logRecord != null) {
        logId = logRecord.getId().toString();
      }

      //SimpleLogger.log(int severity, Category category, Location location, String dcName, String csnComponent, String messageID, String message, Object... args)
      SimpleLogger.log(Severity.ERROR, category, location, dcName, csnComponent, msgId, 
        msg + "\r\nFor more details on the problem please check traces searching by logId: " + logId, args);

      return logId;
    }
  }

  /**
   * Issues a warning log message through a certain category. The message is also forwarded to the traces through the location specified.
   * Uses Message ID concept.
   * 
   * @param location through this location the log message is forwarded to the traces
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param args values to be placed at the end of the log entry as parameters.
   * Each of the message placeholders {0}, {1} will correspond to args[0], args[1], etc.
   * These placeholders will not be replaced in the plain text entry but will be replaced if logs are viewed with Log Viewer.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String logWarning(Location location, String msgId, String msg, Object[] args, String dcName, String csnComponent) {
    LogRecord logRecord = SimpleLogger.log(Severity.WARNING, category, location, dcName, csnComponent, msgId, msg, args);
    String logId = null;
    if (logRecord != null) {
      logId = logRecord.getId().toString();
    }

    return logId;
  }

  /**
   * Issues a warning log message through a certain category. The message is also forwarded to the traces through the location specified.
   * Uses Message ID concept.
   * 
   * @param location through this location the log message and optionally the stack trace of the throwable (if any) is forwarded to the traces
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param t - if not null, the exception stack trace is formatted and appended at the end of the trace record as a message parameter
   * @param message the message text itself
   * @param args values to be placed at the end of the log entry as parameters.
   * Each of the message placeholders {0}, {1} will correspond to args[0], args[1], etc.
   * These placeholders will not be replaced in the plain text entry but will be replaced if logs are viewed with Log Viewer.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   * @return String - the logId of the LogRecord object encapsulating the message that has been written. null if no message has been written.
   */
  public String logWarning(Location location, String msgId, String msg, Object[] args, Throwable t, String dcName, String csnComponent) {
    if (t == null) {
      return logWarning(location, msgId, msg, dcName, csnComponent);
    } else {
      LogRecord logRecord = SimpleLogger.trace(Severity.WARNING, location, dcName, csnComponent, msgId, msg, t, args);
      String logId = null;
      if (logRecord != null) {
        logId = logRecord.getId().toString();
      }

      SimpleLogger.log(Severity.WARNING, category, location, dcName, csnComponent, msgId, 
        msg + "\r\nFor more details on the problem please check traces searching by logId: " + logId, args);

      return logId;
    }
  }

  /**
   * Issues an info log message through a certain category. The message is also forwarded to the traces through the location specified.
   * Uses Message ID concept.
   * 
   * @param location through this location the log message is forwarded to the traces
   * @param msgID messageID to be assigned to this message. MessageID format is [prefix].[range][number] (e.g. ASJ.http.000135)
   * @param message the message text itself
   * @param args values to be placed at the end of the log entry as parameters.
   * Each of the message placeholders {0}, {1} will correspond to args[0], args[1], etc.
   * These placeholders will not be replaced in the plain text entry but will be replaced if logs are viewed with Log Viewer.
   * @param dcName deployment component name to be written in the trace entry. If you do not want to specify the DC name 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own DC name.
   * @param csnComponent CSN Component to be written in the trace entry. If you do not want to specify the CSN component 
   * you must use <code>NULL</code> thus the logging infrastructure will place in the log record your own CSN component.
   */
  public void logInfo(Location location, String msgId, String msg, Object[] args, String dcName, String csnComponent) {
    SimpleLogger.log(Severity.INFO, category, location, dcName, csnComponent, msgId, msg, args);
  }
}