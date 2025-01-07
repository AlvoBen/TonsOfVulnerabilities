/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.persistent;

import com.sap.engine.interfaces.cross.UnsatisfiedReference;
import java.io.Serializable;

public class UnsatisfiedReferenceImpl implements UnsatisfiedReference, Serializable {

  private byte[] bytes;
  static final long serialVersionUID = -2841452124820023561L;
  private String rootCause = null;
  private String stackTrace = null;
  
  private transient Throwable causedByError = null;

  public UnsatisfiedReferenceImpl(byte[] bytes) {
    this.bytes = bytes;
  }

  public UnsatisfiedReferenceImpl(byte[] bytes, String stackTrace, String exception, Throwable causedByError) {
    this.bytes = bytes;
    this.stackTrace = stackTrace;
    this.rootCause = exception;
    this.causedByError = causedByError;
  }

  public byte[] getBytes() {
    return bytes;
  }
  
  public Throwable getCausedByException() {
  	return causedByError;
  }

  public String getRootCause() {
    return rootCause != null ? rootCause : "N/A";
  }

  public String printThrowable() {
    return stackTrace != null ? stackTrace : "N/A";
  }

  public String getStackTrace() {
    return stackTrace != null ? stackTrace : "N/A";
  }

  public String toString() {
    return getClass().getName() + "@" + Integer.toHexString(hashCode()) + "\nReturned because an Exception occured in deserialization process: [" + getRootCause() + "]";
  }
}

