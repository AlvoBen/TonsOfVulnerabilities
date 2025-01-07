/*
 * Copyright (c) 2005 by SAP AG, Walldorf., http://www.sap.com All rights
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
import java.io.StringWriter;

/**
 * Wrapper exception for the various exceptions being thrown during J2EE
 * descriptor conversion. It takes several wrapped exceptions as argument in
 * order to be able to go on as far as possible during conversion before
 * throwing the ConversionException.
 * 
 * @author d037913
 */
public class ConversionException extends Exception {

  private FileNameExceptionPair[] fileExcPairs;
  private String message;

  public ConversionException(FileNameExceptionPair[] fileExcPairs) {
    this.fileExcPairs = fileExcPairs;
    // construct message out of nested messages 
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    pw.print("ConversionException contains ");
    if (fileExcPairs == null || fileExcPairs.length == 0) {
      pw.print("no nested exceptions.");
    } else {
      pw.print(String.valueOf(fileExcPairs.length));
      pw.println(" nested exception(s):");
      for (int i = 0; i < fileExcPairs.length; i++) {
        pw.print(fileExcPairs[i].getFileName());
        pw.print(": ");
        pw.println(fileExcPairs[i].getThrowable());
      }
    }
    pw.flush();
    this.message = sw.toString();
  }

  public String getMessage() {
    return message;
  }

  public FileNameExceptionPair[] getFileExceptionPairs() {
    return fileExcPairs;
  }

  // Override stack trace methods to show original cause:
  public void printStackTrace() {
    printStackTrace(System.err);// $JL-SYS_OUT_ERR$
  }

  public void printStackTrace(PrintStream ps) {
    synchronized (ps) {
      super.printStackTrace(ps);
      if (fileExcPairs != null && fileExcPairs.length > 0) {
        ps.println("--- Causing Exception(s) ---");
        for (int i = 0; i < fileExcPairs.length; i++) {
          Throwable t = fileExcPairs[i].getThrowable();
          ps.println((t == null ? "null" : t.toString())
              + " for file: \""
              + fileExcPairs[i].getFileName() + "\", severity "
              + fileExcPairs[i].getSeverity() + ":");
          if (t != null) {
            t.printStackTrace(ps);
          }
        }
      }
    }
  }

  public void printStackTrace(PrintWriter pw) {
    synchronized (pw) {
      super.printStackTrace(pw);
      if (fileExcPairs != null && fileExcPairs.length > 0) {
        pw.println("--- Causing Exception(s) ---");
        for (int i = 0; i < fileExcPairs.length; i++) {
          Throwable t = fileExcPairs[i].getThrowable();
          pw.println((t == null ? "null" : t.toString())
              + " for file: \""
              + fileExcPairs[i].getFileName() + "\", severity "
              + fileExcPairs[i].getSeverity() + ":");
          if (t != null) {
            t.printStackTrace(pw);
          }
        }
      }
    }
  }

}