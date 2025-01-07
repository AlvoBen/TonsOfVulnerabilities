/*
 * Copyright (c) 2005 by SAP AG, Walldorf., http://www.sap.com All rights
 * reserved.
 * 
 * This software is the confidential and proprietary information of SAP AG,
 * Walldorf. You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered
 * into with SAP.
 * 
 * $Id$
 */
package com.sap.engine.lib.converter;

import java.io.Serializable;

/**
 * Container class for binding an exception to a filename and a severity
 * (WARNING or ERROR). Default severity is ERROR. Used during descriptor
 * conversion.
 * 
 * @author d037913
 */
public class FileNameExceptionPair implements Serializable {

  public static final int SEVERITY_ERROR = 1;
  public static final int SEVERITY_WARNING = 2;

  private String fileName;
  private Throwable throwable;
  private int severity = SEVERITY_ERROR;

  /**
   * Equivalent to
   * {@link #FileNameExceptionPair(Throwable, String, SEVERITY_ERROR)
   */
  public FileNameExceptionPair(Throwable throwable, String fileName) {
    this.fileName = fileName;
    this.throwable = throwable;
  }

  public FileNameExceptionPair(Throwable throwable, String fileName,
      int severity) {
    this.fileName = fileName;
    this.throwable = throwable;
    this.severity = severity;
  }

  public String getFileName() {
    return fileName;
  }

  public Throwable getThrowable() {
    return throwable;
  }

  public int getSeverity() {
    return severity;
  }

}