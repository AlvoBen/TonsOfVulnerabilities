/*
 * BaseLogService.java Property of SAP AG, Walldorf (c) Copyright SAP AG,
 * Walldorf, 2003. All rights reserved.
 */
package com.sap.jms.util.logging;

import com.sap.jms.util.compat.PrintWriter;
import java.io.StringWriter;

import com.sap.exception.IBaseException;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

/**
 * Implementation of a log service.
 * @author Sabine Heider
 */
public class BaseLogService implements LogService {

  protected Category category;
  protected Location location;

  /**
   * Creates a new log service using the given Location and Category objects.
   * @param location The Location object to be used for logging and tracing
   * @param category The Category object to be used for tracing
   * @see com.sap.tc.logging.Location
   * @see com.sap.tc.logging.Category
   */
  protected BaseLogService(Location location, Category category) {
    this.location = location;
    this.category = category;
  }

  /**
   * @see com.sap.jms.util.logging.LogService#fatal(java.lang.String,
   *      java.lang.String)
   */
  public void fatal(String componentName, String msgText) {
    logText(FATAL, componentName, msgText);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#fatal(java.lang.String,
   *      java.lang.String, java.lang.Object)
   */
  public void fatal(String componentName, String msgText, Object arg1) {
    logText(FATAL, componentName, msgText, arg1);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#fatal(java.lang.String,
   *      java.lang.String, java.lang.Object, java.lang.Object)
   */
  public void fatal(String componentName, String msgText, Object arg1, Object arg2) {
    logText(FATAL, componentName, msgText, arg1, arg2);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#fatal(java.lang.String, java.lang.String, java.lang.Object[])
   */
  public void fatal(String componentName, String msgText, Object[] args) {
    logText(FATAL, componentName, msgText, args);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#error(java.lang.String, java.lang.String)
   */
  public void error(String componentName, String msgText) {
    logText(ERROR, componentName, msgText);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#error(java.lang.String,
   *      java.lang.String, java.lang.Object)
   */
  public void error(String componentName, String msgText, Object arg1) {
    logText(ERROR, componentName, msgText, arg1);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#error(java.lang.String,
   *      java.lang.String, java.lang.Object, java.lang.Object)
   */
  public void error(String componentName, String msgText, Object arg1, Object arg2) {
    logText(ERROR, componentName, msgText, arg1, arg2);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#error(java.lang.String, java.lang.String, java.lang.Object[])
   */
  public void error(String componentName, String msgText, Object[] args) {
    logText(ERROR, componentName, msgText, args);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#warning(java.lang.String, java.lang.String)
   */
  public void warning(String componentName, String msgText) {
    logText(WARNING, componentName, msgText);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#warning(java.lang.String,
   *      java.lang.String, java.lang.Object)
   */
  public void warning(String componentName, String msgText, Object arg1) {
    logText(WARNING, componentName, msgText, arg1);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#warning(java.lang.String,
   *      java.lang.String, java.lang.Object, java.lang.Object)
   */
  public void warning(String componentName, String msgText, Object arg1, Object arg2) {
    logText(WARNING, componentName, msgText, arg1, arg2);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#warning(java.lang.String, java.lang.String, java.lang.Object[])
   */
  public void warning(String componentName, String msgText, Object[] args) {
    logText(WARNING, componentName, msgText, args);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#info(java.lang.String, java.lang.String)
   */
  public void info(String componentName, String msgText) {
    logText(INFO, componentName, msgText);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#info(java.lang.String,
   *      java.lang.String, java.lang.Object)
   */
  public void info(String componentName, String msgText, Object arg1) {
    logText(INFO, componentName, msgText, arg1);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#info(java.lang.String,
   *      java.lang.String, java.lang.Object, java.lang.Object)
   */
  public void info(String componentName, String msgText, Object arg1, Object arg2) {
    logText(INFO, componentName, msgText, arg1, arg2);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#info(java.lang.String, java.lang.String, java.lang.Object[])
   */
  public void info(String componentName, String msgText, Object[] args) {
    logText(INFO, componentName, msgText, args);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#isLogged(int)
   */
  public boolean isLogged(int severity) {
    return category.beLogged(severity);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#path(java.lang.String, java.lang.String)
   */
  public void path(String componentName, String msgText) {
    traceText(PATH, componentName, msgText);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#path(java.lang.String,
   *      java.lang.String, java.lang.Object)
   */
  public void path(String componentName, String msgText, Object arg1) {
    traceText(PATH, componentName, msgText, arg1);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#path(java.lang.String,
   *      java.lang.String, java.lang.Object, java.lang.Object)
   */
  public void path(String componentName, String msgText, Object arg1, Object arg2) {
    traceText(PATH, componentName, msgText, arg1, arg2);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#path(java.lang.String, java.lang.String, long)
   */
  public void path(String componentName, String msgText, long arg1) {
    traceText(PATH, componentName, msgText, arg1);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#path(java.lang.String, java.lang.String, long, long)
   */
  public void path(String componentName, String msgText, long arg1, long arg2) {
    traceText(PATH, componentName, msgText, arg1, arg2);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#path(java.lang.String, java.lang.String, java.lang.Object[])
   */
  public void path(String componentName, String msgText, Object[] args) {
    traceText(PATH, componentName, msgText, args);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#debug(java.lang.String, java.lang.String)
   */
  public void debug(String componentName, String msgText) {
    traceText(DEBUG, componentName, msgText);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#debug(java.lang.String,
   *      java.lang.String, java.lang.Object)
   */
  public void debug(String componentName, String msgText, Object arg1) {
    traceText(DEBUG, componentName, msgText, arg1);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#debug(java.lang.String,
   *      java.lang.String, java.lang.Object, java.lang.Object)
   */
  public void debug(String componentName, String msgText, Object arg1, Object arg2) {
    traceText(DEBUG, componentName, msgText, arg1, arg2);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#debug(java.lang.String, java.lang.String, long)
   */
  public void debug(String componentName, String msgText, long arg1) {
    traceText(DEBUG, componentName, msgText, arg1);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#debug(java.lang.String, java.lang.String, long, long)
   */
  public void debug(String componentName, String msgText, long arg1, long arg2) {
    traceText(DEBUG, componentName, msgText, arg1, arg2);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#debug(java.lang.String, java.lang.String, java.lang.Object[])
   */
  public void debug(String componentName, String msgText, Object[] args) {
    traceText(DEBUG, componentName, msgText, args);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#exception(int, java.lang.String, java.lang.Throwable)
   */
  public void exception(int severity, String componentName, Throwable exc) {
    if (location.beLogged(severity)) {
	    String msgText;
	    if (exc instanceof IBaseException) {
	      IBaseException ex2 = (IBaseException)exc;
	      msgText = ex2.getNestedStackTraceString();
	    } else {
	      msgText = getExceptionMessage(exc);
	    } //if
	    traceText(severity, componentName, msgText);
    }
  }

  /**
   * @see com.sap.jms.util.logging.LogService#exception(java.lang.String, java.lang.Throwable)
   */
  public void exception(String componentName, Throwable exc) {
    exception(ERROR, componentName, exc);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#fatalTrace(java.lang.String,
   *      java.lang.String)
   */
  public void fatalTrace(String componentName, String msgText) {
    traceText(FATAL, componentName, msgText);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#fatalTrace(java.lang.String,
   *      java.lang.String, java.lang.Object)
   */
  public void fatalTrace(String componentName, String msgText, Object arg1) {
    traceText(FATAL, componentName, msgText, arg1);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#fatalTrace(java.lang.String,
   *      java.lang.String, java.lang.Object, java.lang.Object)
   */
  public void fatalTrace(String componentName, String msgText, Object arg1, Object arg2) {
    traceText(FATAL, componentName, msgText, arg1, arg2);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#fatalTrace(java.lang.String,
   *      java.lang.String, java.lang.Object[])
   */
  public void fatalTrace(String componentName, String msgText, Object[] args) {
    traceText(FATAL, componentName, msgText, args);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#errorTrace(java.lang.String,
   *      java.lang.String)
   */
  public void errorTrace(String componentName, String msgText) {
    traceText(ERROR, componentName, msgText);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#errorTrace(java.lang.String,
   *      java.lang.String, java.lang.Object)
   */
  public void errorTrace(String componentName, String msgText, Object arg1) {
    traceText(ERROR, componentName, msgText, arg1);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#errorTrace(java.lang.String,
   *      java.lang.String, java.lang.Object, java.lang.Object)
   */
  public void errorTrace(String componentName, String msgText, Object arg1, Object arg2) {
    traceText(ERROR, componentName, msgText, arg1, arg2);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#errorTrace(java.lang.String,
   *      java.lang.String, java.lang.Object[])
   */
  public void errorTrace(String componentName, String msgText, Object[] args) {
    traceText(ERROR, componentName, msgText, args);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#warningTrace(java.lang.String,
   *      java.lang.String)
   */
  public void warningTrace(String componentName, String msgText) {
    traceText(WARNING, componentName, msgText);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#warningTrace(java.lang.String,
   *      java.lang.String, java.lang.Object)
   */
  public void warningTrace(String componentName, String msgText, Object arg1) {
    traceText(WARNING, componentName, msgText, arg1);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#warningTrace(java.lang.String,
   *      java.lang.String, java.lang.Object, java.lang.Object)
   */
  public void warningTrace(String componentName, String msgText, Object arg1, Object arg2) {
    traceText(WARNING, componentName, msgText, arg1, arg2);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#warningTrace(java.lang.String,
   *      java.lang.String, java.lang.Object[])
   */
  public void warningTrace(String componentName, String msgText, Object[] args) {
    traceText(WARNING, componentName, msgText, args);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#infoTrace(java.lang.String,
   *      java.lang.String)
   */
  public void infoTrace(String componentName, String msgText) {
    traceText(INFO, componentName, msgText);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#infoTrace(java.lang.String,
   *      java.lang.String, java.lang.Object)
   */
  public void infoTrace(String componentName, String msgText, Object arg1) {
    traceText(INFO, componentName, msgText, arg1);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#infoTrace(java.lang.String,
   *      java.lang.String, java.lang.Object, java.lang.Object)
   */
  public void infoTrace(String componentName, String msgText, Object arg1, Object arg2) {
    traceText(INFO, componentName, msgText, arg1, arg2);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#infoTrace(java.lang.String,
   *      java.lang.String, java.lang.Object[])
   */
  public void infoTrace(String componentName, String msgText, Object[] args) {
    traceText(INFO, componentName, msgText, args);
  }

  /**
   * @see com.sap.jms.util.logging.LogService#isTraced(int)
   */
  public boolean isTraced(int severity) {
    return location.beLogged(severity);
  }

  /**
   * Sends a message of the specified severity to the system log controller.
   * @param severity Severity of the log message
   * @param componentName String specifying the source code area
   * @param msgText Message text
   */
  protected void logText(int severity, String componentName, String msgText) {
    if (componentName == null) {
      category.logT(severity, location, msgText);
    } else {
      category.logT(severity, location, componentName, msgText);
    }
  }

  /**
   * Sends a message of the specified severity to the system log controller.
   * @param severity Severity of the log message
   * @param componentName String specifying the source code area
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   */
  protected void logText(int severity, String componentName, String msgText, Object arg1) {
    if (category.beLogged(severity)) {
      Object[] args = new Object[] { arg1 };
      if (componentName == null) {
        category.logT(severity, location, msgText, args);
      } else {
        category.logT(severity, location, componentName, msgText, args);
      }
    }
  }

  /**
   * Sends a message of the specified severity to the system log controller.
   * @param severity Severity of the log message
   * @param componentName String specifying the source code area
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   * @param arg2 Second argument ({1} in msgText)
   */
  protected void logText(int severity, String componentName, String msgText, Object arg1,
      Object arg2) {
    if (category.beLogged(severity)) {
      Object[] args = new Object[] { arg1, arg2 };
      if (componentName == null) {
        category.logT(severity, location, msgText, args);
      } else {
        category.logT(severity, location, componentName, msgText, args);
      }
    }
  }

  /**
   * Sends a message of the specified severity with message arguments to the
   * system log controller.
   * @param severity Severity of the log message
   * @param componentName String specifying the source code area
   * @param msgText Message text
   * @param args Arguments ({0}, {1}, ..., {n} in msgText)
   */
  protected void logText(int severity, String componentName, String msgText, Object[] args) {
    if (componentName == null) {
      category.logT(severity, location, msgText, args);
    } else {
      category.logT(severity, location, componentName, msgText, args);
    }
  }

  /**
   * Sends a message of the specified severity to the trace controller.
   * @param severity Severity of the trace message
   * @param componentName String specifying the source code area
   * @param msgText Message text
   */
  protected void traceText(int severity, String componentName, String msgText) {
    if (componentName == null) {
      location.logT(severity, msgText);
    } else {
      location.logT(severity, componentName, msgText);
    }
  }

  /**
   * Sends a message of the specified severity to the trace controller.
   * @param severity Severity of the trace message
   * @param componentName String specifying the source code area
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   */
  protected void traceText(int severity, String componentName, String msgText, Object arg1) {
    if (location.beLogged(severity)) {
      Object[] args = new Object[] { arg1 };
      if (componentName == null) {
        location.logT(severity, msgText, args);
      } else {
        location.logT(severity, componentName, msgText, args);
      }
    }
  }

  /**
   * Sends a message of the specified severity to the trace controller.
   * @param severity Severity of the trace message
   * @param componentName String specifying the source code area
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   * @param arg2 Second argument ({1} in msgText)
   */
  protected void traceText(int severity, String componentName, String msgText, Object arg1,
      Object arg2) {
    if (location.beLogged(severity)) {
      Object[] args = new Object[] { arg1, arg2 };
      if (componentName == null) {
        location.logT(severity, msgText, args);
      } else {
        location.logT(severity, componentName, msgText, args);
      }
    }
  }

  /**
   * Sends a message of the specified severity to the trace controller.
   * @param severity Severity of the trace message
   * @param componentName String specifying the source code area
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   */
  protected void traceText(int severity, String componentName, String msgText, long arg1) {
    if (location.beLogged(severity)) {
      Object[] args = new Object[] { new Long(arg1) };
      if (componentName == null) {
        location.logT(severity, msgText, args);
      } else {
        location.logT(severity, componentName, msgText, args);
      }
    }
  }

  /**
   * Sends a message of the specified severity to the trace controller.
   * @param severity Severity of the trace message
   * @param componentName String specifying the source code area
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   * @param arg2 Second argument ({1} in msgText)
   */
  protected void traceText(int severity, String componentName, String msgText, long arg1,
      long arg2) {
    if (location.beLogged(severity)) {
      Object[] args = new Object[] { new Long(arg1), new Long(arg2) };
      if (componentName == null) {
        location.logT(severity, msgText, args);
      } else {
        location.logT(severity, componentName, msgText, args);
      }
    }
  }
  /**
   * Sends a message of the specified severity with message arguments to the
   * trace controller.
   * @param severity Severity of the trace message
   * @param componentName String specifying the source code area
   * @param msgText Message text
   * @param args Arguments ({0}, {1}, ..., {n} in msgText)
   */
  protected void traceText(int severity, String componentName, String msgText, Object[] args) {
    if (componentName == null) {
      location.logT(severity, msgText, args);
    } else {
      location.logT(severity, componentName, msgText, args);
    }
  }

  /**
   * Creates the message text for an exception to be logged.
   * @param exc Exception to be logged
   * @return String Message text
   */
  protected String getExceptionMessage(Throwable exc) {
    StringWriter writer = new StringWriter();
    exc.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }

  /**
   * Returns the location object used by the log service.
   * @return The location object
   */
  public Location getLocation() {
    return location;
  }
  
  /**
   * Returns the category object used by the log service.
   * @return The category object
   */
  public Category getCategory() {
    return category;
  }
  
  //-------------------------------------------------------------------
  // Deprecated log and trace methods
  //-------------------------------------------------------------------
  /**
   * Sends an exception to the system or application log controller as a message
   * of the specified severity. The exception is printed using its
   * <code>printStackTrace()</code> method.
   * @param severity Severity of the log message
   * @param componentName String specifying the source code area (relative to
   *          com.sap.jms)
   * @param exc Exception to be logged
   */
  public void exceptionLog(int severity, String componentName, Throwable exc) {
    String msgText;
    if (exc instanceof IBaseException) {
      IBaseException ex2 = (IBaseException)exc;
      msgText = ex2.getNestedStackTraceString();
    } else {
      msgText = getExceptionMessage(exc);
    } //if
    logText(severity, componentName, msgText);
  }

  /**
   * Sends an exception to the system or application log controller as an error
   * message. The exception is printed using its <code>printStackTrace()</code>
   * method.
   * @param componentName String specifying the source code area (relative to
   *          com.sap.jms)
   * @param exc Exception to be logged
   */
  public void exceptionLog(String componentName, Throwable exc) {
    exception(ERROR, componentName, exc);
  }

}