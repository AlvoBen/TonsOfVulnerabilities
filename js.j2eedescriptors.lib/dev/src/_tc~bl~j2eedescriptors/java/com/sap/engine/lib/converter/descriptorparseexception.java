/*
 * Copyright (c) 2004 by SAP AG, Walldorf., http://www.sap.com All rights
 * reserved.
 * 
 * This software is the confidential and proprietary information of SAP AG,
 * Walldorf. You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered
 * into with SAP.
 */
package com.sap.engine.lib.converter;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Wrapper exception for the various exceptions being thrown during J2EE
 * descriptor parsing and converting.
 * 
 * @author d037913
 */
public class DescriptorParseException extends Exception {

  private Throwable cause;

  public DescriptorParseException(String message) {
    super(message);
  }

  public DescriptorParseException(Throwable cause) {
    super(cause);
    this.cause = cause;
  }

  public DescriptorParseException(String message, Throwable cause) {
    super(message, cause);
    this.cause = cause;
  }

  // Override stack trace methods to show original cause:
  public void printStackTrace() {
    printStackTrace(System.err);//$JL-SYS_OUT_ERR$
  }

  public void printStackTrace(PrintStream ps) {
    synchronized (ps) {
      super.printStackTrace(ps);
      if (cause != null) {
        ps.println("--- Nested Exception ---");
        cause.printStackTrace(ps);
      }
    }
  }

  public void printStackTrace(PrintWriter pw) {
    synchronized (pw) {
      super.printStackTrace(pw);
      if (cause != null) {
        pw.println("--- Nested Exception ---");
        cause.printStackTrace(pw);
      }
    }
  }

}