/*
 * LogService.java 
 * Property of SAP AG, Walldorf 
 * (c) Copyright SAP AG, Walldorf, 2003. 
 * All rights reserved.
 */
package com.sap.jms.util.logging;

import com.sap.tc.logging.Severity;

/**
 * <p>
 * The log service encapsulates all methods related with the writing of log and
 * trace messages.
 * </p><p>
 * The interface has two types of methods: Methods writing to the log (i.e.
 * system or application log), and such writing to a trace.
 * </p><p>
 * The <b>log </b> is intended to contain only such messages that are meaningful
 * to a system or application administrator, who typically has no deeper
 * knowledge of the JMS provider implementation. The severity of a log message
 * can be FATAL, ERROR, WARNING, or INFO.
 * </p><p>
 * The <b>trace </b> however contains messages helping the JMS developers or
 * support people to debug a problem. These messages are generally more detailed
 * and require a deeper knowledge of the implementation details to be of any
 * use. Trace messages are usually of severity PATH or DEBUG. Other severities
 * should only be used in combination with a log message to provide further
 * information, such as the stack trace of an exception, for example. Note that
 * a copy of the log messages is sent to the trace as well in order to avoid
 * juggling with the different files in case a trace has to be analyzed.
 * </p><p>
 * The methods for sending log or trace messages have variants with and without
 * object arguments. To use the message arguments, the message text has to be
 * prepared with placeholders of the form <code>"{</code> <i>&lt;number&gt;
 * </i> <code>}"</code>. The placeholders are replaced with the result of the
 * <code>toString()</code> method of the object with the same number. Note
 * that to output <code>"{"</code> or <code>"}"</code> you have to put them
 * into single quotes, that is write <code>"'{'"</code> or <code>"'}'"</code>.
 * To print single quotes, double them in your message text.
 * </p><p>
 * On creation of the log service object you define the source code location
 * that is logged together with the log or trace message (e.g. com.sap.jms). If
 * you want to further specify the source code location, you can pass a
 * component name, which is then appended to the default location output If you
 * don't need this feature, you should specify <code>null</code> as the
 * component name instead.
 * </p><p>
 * Some of the methods take a first argument that specifies the severity of the
 * message. Use the constants defined in this interface.
 * </p>
 * @author Sabine Heider
 */
public interface LogService {

  /**
   * Severity of a fatal error message.
   */
  public static int FATAL = Severity.FATAL;

  /**
   * Severity of an error message.
   */
  public static int ERROR = Severity.ERROR;

  /**
   * Severity of a warning message.
   */
  public static int WARNING = Severity.WARNING;

  /**
   * Severity of an informational message.
   */
  public static int INFO = Severity.INFO;

  /**
   * Severity of a path message, that is a message indicating the execution
   * flow.
   */
  public static int PATH = Severity.PATH;

  /**
   * Severity of a debug message.
   */
  public static int DEBUG = Severity.DEBUG;

  //-------------------------------------------------------------------
  // Methods to write log entries
  //-------------------------------------------------------------------

  /**
   * Sends a message of severity FATAL to the system or application log
   * controller.
   * <p>
   * <b>Note: </b> This is a log message, so use a meaningful message text!
   * </p>
   * @param componentName String specifying the source code area (relative to
   *          the location used for constructing the LogService object)
   * @param msgText Message text
   */
  public void fatal(String componentName, String msgText);

  /**
   * Sends a message of severity FATAL to the system or application log
   * controller.
   * <p>
   * <b>Note: </b> This is a log message, so use a meaningful message text!
   * </p>
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   */
  public void fatal(String componentName, String msgText, Object arg1);

  /**
   * Sends a message of severity FATAL to the system or application log
   * controller.
   * <p>
   * <b>Note: </b> This is a log message, so use a meaningful message text!
   * </p>
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   * @param arg2 Second argument ({1} in msgText)
   */
  public void fatal(String componentName, String msgText, Object arg1, Object arg2);

  /**
   * Sends a message of severity FATAL with message arguments to the system or
   * application log controller.
   * <p>
   * <b>Note: </b> This is a log message, so use a meaningful message text!
   * </p>
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param args Arguments ({0}, {1}, ..., {n} in msgText)
   */
  public void fatal(String componentName, String msgText, Object[] args);

  /**
   * Sends a message of severity ERROR to the system or application log
   * controller.
   * <p>
   * <b>Note: </b> This is a log message, so use a meaningful message text!
   * </p>
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   */
  public void error(String componentName, String msgText);

  /**
   * Sends a message of severity ERROR to the system or application log
   * controller.
   * <p>
   * <b>Note: </b> This is a log message, so use a meaningful message text!
   * </p>
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   */
  public void error(String componentName, String msgText, Object arg1);

  /**
   * Sends a message of severity ERROR to the system or application log
   * controller.
   * <p>
   * <b>Note: </b> This is a log message, so use a meaningful message text!
   * </p>
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   * @param arg2 Second argument ({1} in msgText)
   */
  public void error(String componentName, String msgText, Object arg1, Object arg2);

  /**
   * Sends a message of severity ERROR with message arguments to the system or
   * application log controller.
   * <p>
   * <b>Note: </b> This is a log message, so use a meaningful message text!
   * </p>
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param args Arguments ({0}, {1}, ..., {n} in msgText)
   */
  public void error(String componentName, String msgText, Object[] args);

  /**
   * Sends a message of severity WARNING to the system or application log
   * controller.
   * <p>
   * <b>Note: </b> This is a log message, so use a meaningful message text!
   * </p>
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   */
  public void warning(String componentName, String msgText);

  /**
   * Sends a message of severity WARNING to the system or application log
   * controller.
   * <p>
   * <b>Note: </b> This is a log message, so use a meaningful message text!
   * </p>
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   */
  public void warning(String componentName, String msgText, Object arg1);

  /**
   * Sends a message of severity WARNING to the system or application log
   * controller.
   * <p>
   * <b>Note: </b> This is a log message, so use a meaningful message text!
   * </p>
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   * @param arg2 Second argument ({1} in msgText)
   */
  public void warning(String componentName, String msgText, Object arg1, Object arg2);

  /**
   * Sends a message of severity WARNING with message arguments to the system or
   * application log controller.
   * <p>
   * <b>Note: </b> This is a log message, so use a meaningful message text!
   * </p>
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param args Arguments ({0}, {1}, ..., {n} in msgText)
   */
  public void warning(String componentName, String msgText, Object[] args);

  /**
   * Sends a message of severity INFO to the system or application log
   * controller.
   * <p>
   * <b>Note: </b> This is a log message, so use a meaningful message text!
   * </p>
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   */
  public void info(String componentName, String msgText);

  /**
   * Sends a message of severity INFO to the system or application log
   * controller.
   * <p>
   * <b>Note: </b> This is a log message, so use a meaningful message text!
   * </p>
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   */
  public void info(String componentName, String msgText, Object arg1);

  /**
   * Sends a message of severity INFO to the system or application log
   * controller.
   * <p>
   * <b>Note: </b> This is a log message, so use a meaningful message text!
   * </p>
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   * @param arg2 Second argument ({1} in msgText)
   */
  public void info(String componentName, String msgText, Object arg1, Object arg2);

  /**
   * Sends a message of severity INFO with message arguments to the system or
   * application log controller.
   * <p>
   * <b>Note: </b> This is a log message, so use a meaningful message text!
   * </p>
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param args Arguments ({0}, {1}, ..., {n} in msgText)
   */
  public void info(String componentName, String msgText, Object[] args);

  /**
   * Checks whether a message of the specified severity would be logged.
   * @param severity Severity of the log message
   * @return boolean <code>true</code> if the message would be logged,
   *         <code>false</code> otherwise
   */
  public boolean isLogged(int severity);

  //-------------------------------------------------------------------
  // Methods to write trace entries
  //-------------------------------------------------------------------

  /**
   * Sends a message of severity PATH to the trace controller.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   */
  public void path(String componentName, String msgText);

  /**
   * Sends a message of severity PATH to the trace controller.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   */
  public void path(String componentName, String msgText, Object arg1);

  /**
   * Sends a message of severity PATH to the trace controller.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   * @param arg2 Second argument ({1} in msgText)
   */
  public void path(String componentName, String msgText, Object arg1, Object arg2);

  /**
   * Sends a message of severity PATH to the trace controller.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   */
  public void path(String componentName, String msgText, long arg1);

  /**
   * Sends a message of severity PATH to the trace controller.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   * @param arg2 Second argument ({1} in msgText)
   */
  public void path(String componentName, String msgText, long arg1, long arg2);

  /**
   * Sends a message of severity PATH with message arguments to the trace
   * controller. It is usually a good idea to check first with
   * {@link #isTraced(int)} whether messages of severity PATH are traced at all.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param args Arguments ({0}, {1}, ..., {n} in msgText)
   */
  public void path(String componentName, String msgText, Object[] args);

  /**
   * Sends a message of severity DEBUG to the trace controller.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   */
  public void debug(String componentName, String msgText);

  /**
   * Sends a message of severity DEBUG to the trace controller.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   */
  public void debug(String componentName, String msgText, Object arg1);

  /**
   * Sends a message of severity DEBUG to the trace controller.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   * @param arg2 Second argument ({1} in msgText)
   */
  public void debug(String componentName, String msgText, Object arg1, Object arg2);

  /**
   * Sends a message of severity DEBUG to the trace controller.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   */
  public void debug(String componentName, String msgText, long arg1);

  /**
   * Sends a message of severity DEBUG to the trace controller.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   * @param arg2 Second argument ({1} in msgText)
   */
  public void debug(String componentName, String msgText, long arg1, long arg2);

  /**
   * Sends a message of severity DEBUG with message arguments to the trace
   * controller. It is usually a good idea to check first with
   * {@link #isTraced(int)} whether messages of severity DEBUG are traced at
   * all.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param args Arguments as object references
   */
  public void debug(String componentName, String msgText, Object[] args);

  /**
   * Sends a message of severity FATAL to the trace controller. This method
   * should ONLY be used in combination with a FATAL log message in order to
   * provide additional context information.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   */
  public void fatalTrace(String componentName, String msgText);

  /**
   * Sends a message of severity FATAL to the trace controller. This method
   * should ONLY be used in combination with a FATAL log message in order to
   * provide additional context information.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   */
  public void fatalTrace(String componentName, String msgText, Object arg1);

  /**
   * Sends a message of severity FATAL to the trace controller. This method
   * should ONLY be used in combination with a FATAL log message in order to
   * provide additional context information.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   * @param arg2 Second argument ({1} in msgText)
   */
  public void fatalTrace(String componentName, String msgText, Object arg1, Object arg2);

  /**
   * Sends a message of severity FATAL to the trace controller. This method
   * should ONLY be used in combination with a FATAL log message in order to
   * provide additional context information.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param args Arguments ({0}, {1}, ..., {n} in msgText)
   * @see #isTraced(int)
   */
  public void fatalTrace(String componentName, String msgText, Object[] args);

  /**
   * Sends a message of severity ERROR to the trace controller. This method
   * should ONLY be used in combination with an ERROR log message in order to
   * provide additional context information.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   */
  public void errorTrace(String componentName, String msgText);

  /**
   * Sends a message of severity ERROR to the trace controller. This method
   * should ONLY be used in combination with an ERROR log message in order to
   * provide additional context information.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   */
  public void errorTrace(String componentName, String msgText, Object arg1);

  /**
   * Sends a message of severity ERROR to the trace controller. This method
   * should ONLY be used in combination with an ERROR log message in order to
   * provide additional context information.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   * @param arg2 Second argument ({1} in msgText)
   */
  public void errorTrace(String componentName, String msgText, Object arg1, Object arg2);

  /**
   * Sends a message of severity ERROR to the trace controller. This method
   * should ONLY be used in combination with an ERROR log message in order to
   * provide additional context information.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param args Arguments ({0}, {1}, ..., {n} in msgText)
   * @see #isTraced(int)
   */
  public void errorTrace(String componentName, String msgText, Object[] args);

  /**
   * Sends a message of severity WARNING to the trace controller. This method
   * should ONLY be used in combination with a WARNING log message in order to
   * provide additional context information.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   */
  public void warningTrace(String componentName, String msgText);

  /**
   * Sends a message of severity WARNING to the trace controller. This method
   * should ONLY be used in combination with a WARNING log message in order to
   * provide additional context information.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   */
  public void warningTrace(String componentName, String msgText, Object arg1);

  /**
   * Sends a message of severity WARNING to the trace controller. This method
   * should ONLY be used in combination with a WARNING log message in order to
   * provide additional context information.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   * @param arg2 Second argument ({1} in msgText)
   */
  public void warningTrace(String componentName, String msgText, Object arg1, Object arg2);

  /**
   * Sends a message of severity WARNING to the trace controller. This method
   * should ONLY be used in combination with a WARNING log message in order to
   * provide additional context information.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param args Arguments ({0}, {1}, ..., {n} in msgText)
   * @see #isTraced(int)
   */
  public void warningTrace(String componentName, String msgText, Object[] args);

  /**
   * Sends a message of severity INFO to the trace controller. This method
   * should ONLY be used in combination with a INFO log message in order to
   * provide additional context information.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   */
  public void infoTrace(String componentName, String msgText);

  /**
   * Sends a message of severity INFO to the trace controller. This method
   * should ONLY be used in combination with a INFO log message in order to
   * provide additional context information.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   */
  public void infoTrace(String componentName, String msgText, Object arg1);

  /**
   * Sends a message of severity INFO to the trace controller. This method
   * should ONLY be used in combination with a INFO log message in order to
   * provide additional context information.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param arg1 First argument ({0} in msgText)
   * @param arg2 Second argument ({1} in msgText)
   */
  public void infoTrace(String componentName, String msgText, Object arg1, Object arg2);

  /**
   * Sends a message of severity INFO to the trace controller. This method
   * should ONLY be used in combination with a INFO log message in order to
   * provide additional context information.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param msgText Message text
   * @param args Arguments ({0}, {1}, ..., {n} in msgText)
   * @see #isTraced(int)
   */
  public void infoTrace(String componentName, String msgText, Object[] args);

  /**
   * Sends an exception to the trace controller as a message of severity ERROR.
   * This method should ONLY be used in combination with a log message of
   * severity ERROR in order to provide additional context information.
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param exc Exception to be traced
   */
  public void exception(String componentName, Throwable exc);

  /**
   * Sends an exception to the trace controller as a message of the specified
   * severity. Severities FATAL, ERROR, WARNING, or INFO should ONLY be used in
   * combination with a log message of the same severity in order to provide
   * additional context information.
   * @param severity Severity of the trace message
   * @param componentName String specifying the source code area (relative to
   *          the base location of the LogService object)
   * @param exc Exception to be traced
   */
  public void exception(int severity, String componentName, Throwable exc);

  /**
   * Checks whether a message of the specified severity would be traced.
   * @param severity Severity of the traced message
   * @return boolean <code>true</code> if the message would be traced,
   *         <code>false</code> otherwise
   */
  public boolean isTraced(int severity);

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
   * @deprecated Log messages should not contain stack traces. Write a message
   *             that is meaningful to an administrator. A stack trace may be
   *             provided additionally as a <b>trace </b> message using the
   *             {@link #exception(int, String, Throwable)}method.
   */
  public void exceptionLog(int severity, String componentName, Throwable exc);

  /**
   * Sends an exception to the system or application log controller as an error
   * message. The exception is printed using its <code>printStackTrace()</code>
   * method.
   * @param componentName String specifying the source code area (relative to
   *          com.sap.jms)
   * @param exc Exception to be logged
   * @deprecated Log messages should not contain stack traces. Write a message
   *             that is meaningful to an administrator. A stack trace may be
   *             provided additionally as a <b>trace </b> message using the
   *             {@link #exception(String, Throwable)}method.
   */
  public void exceptionLog(String componentName, Throwable exc);

}