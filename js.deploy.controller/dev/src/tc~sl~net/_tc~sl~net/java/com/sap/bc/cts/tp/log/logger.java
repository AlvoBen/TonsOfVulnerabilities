package com.sap.bc.cts.tp.log;

/**
 * An abstract logging framework. An instance of <code>Logger</code> 
 * provides methods for logging info, warning, error and fatal messages. 
 * 
 * @author Java Change Management May 14, 2004
 */
public abstract class Logger {
  private static Logger logger = new StdLogger();
  
  /**
   * Sets a <code>Logger</code> for logging all log messages.
   * 
   * @param logger a <code>Logger</code> for all log messages
   */
  public static void setLogger(Logger logger) {
    Logger.logger = logger;
    
    return;
  }
  
  /**
   * Gets a <code>Logger</code> for logging all log messages. Returns by default
   * a <code>Logger</code> that logs to standard output and standard error 
   * output.
   * 
   * @return a <code>Logger</code> for all log messages
   */  
  public static Logger getLogger() {
    return logger;
  }
  
  /**
   * Logs an info message.
   * 
   * @param message the message to be logged
   */  
  public abstract void info(String message);
  
  /**
   * Logs a warning message.
   * 
   * @param message a <code>String</code> containing the message to be logged
   */  
  public abstract void warning(String message);
  
  /**
   * Logs a warning message together with a <code>Throwable</code>.
   * 
   * @param message a <code>String</code> containing the message to be logged
   * @param throwable a <code>Throwable</code> to be logged
   */  
  public abstract void warning(String message, Throwable throwable);
  
  /**
   * Logs an error message.
   * 
   * @param message a <code>String</code> containing the message to be logged
   */  
  public abstract void error(String message);
  
  /**
   * Logs an error message together with a <code>Throwable</code>.
   * 
   * @param message a <code>String</code> containing the message to be logged
   * @param throwable a <code>Throwable</code> to be logged
   */  
  public abstract void error(String message, Throwable throwable);
  
  /**
   * Logs a fatal message.
   * 
   * @param message a <code>String</code> containing the message to be logged
   */  
  public abstract void fatal(String message);
  
  /**
   * Logs a fatal message together with a <code>Throwable</code>.
   * 
   * @param message a <code>String</code> containing the message to be logged
   * @param throwable a <code>Throwable</code> to be logged
   */  
  public abstract void fatal(String message, Throwable throwable);
}
