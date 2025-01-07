package com.sap.bc.cts.tp.log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * An implementation of the abstract <code>Logger</code> class. Logs to 
 * <code>System.out</code> and <code>System.err</code>.
 * 
 * @author Java Change Management May 14, 2004
 */
final class StdLogger extends Logger {
  public void info(String message) {
    System.out.println(message);
  }

  public void warning(String message) {
    System.err.println(message);
  }

  public void warning(String message, Throwable throwable) {
    warning(message + ": " + throwable);
    warning(stackTraceToString(throwable));
  }

  public void error(String message) {
    System.err.println(message);
  }

  public void error(String message, Throwable throwable) {
    error(message + ": " + throwable);
    error(stackTraceToString(throwable));
  }

  public void fatal(String message) {
    System.err.println(message);
  }

  public void fatal(String message, Throwable throwable) {
    fatal(message + ": " + throwable);
    fatal(stackTraceToString(throwable));
  }

  private String stackTraceToString(Throwable throwable) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    throwable.printStackTrace(pw);
    sw.flush();
    return sw.toString();
  }
}
