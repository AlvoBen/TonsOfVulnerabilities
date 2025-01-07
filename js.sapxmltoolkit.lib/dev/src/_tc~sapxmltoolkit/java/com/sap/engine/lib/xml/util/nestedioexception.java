/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.lib.xml.util;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.sap.engine.lib.log.LogWriter;

/**
 * @author Alexander Zubev (alexander.zubev@sap.com)
 */
public class NestedIOException extends IOException {
  
  private Throwable cause;

  public NestedIOException() {
  }

  public NestedIOException(Throwable cause) {
    super();
    this.cause = cause;
  }

  public NestedIOException(String message) {
    super(message);
  }

  public NestedIOException(Throwable cause, String message) {
    super(message);
    this.cause = cause;
  }

  public NestedIOException(String message, Throwable cause) {
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
