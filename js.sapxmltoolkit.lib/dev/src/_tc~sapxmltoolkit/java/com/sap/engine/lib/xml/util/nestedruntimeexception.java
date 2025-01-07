package com.sap.engine.lib.xml.util;

import java.io.*;

import com.sap.engine.lib.log.LogWriter;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      September 2001
 */
public class NestedRuntimeException extends RuntimeException {

  private Throwable cause;

  public NestedRuntimeException(Throwable cause) {
    super();
    this.cause = cause;
  }

  public NestedRuntimeException(String message) {
    super(message);
  }

  public NestedRuntimeException(Throwable cause, String message) {
    super(message);
    this.cause = cause;
  }

  public NestedRuntimeException(String message, Throwable cause) {
    super(message);
    this.cause = cause;
  }

  public final Throwable getCause() {
    return cause;
  }

  public final void setCause(Throwable cause) {
    this.cause = cause;
  }

  public String toString() {
    return(LogWriter.createExceptionRepresentation(this));
  }
}

