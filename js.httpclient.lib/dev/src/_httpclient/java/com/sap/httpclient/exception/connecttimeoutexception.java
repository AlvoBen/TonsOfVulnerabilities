/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.httpclient.exception;

import java.io.InterruptedIOException;

/**
 * A timeout while connecting to an HTTP server or waiting for an available connection from the HttpConnectionManager.
 *
 * @author Nikolai Neichev
 */
public class ConnectTimeoutException extends InterruptedIOException {

  /**
   * Creates a ConnectTimeoutException with a <tt>null</tt> detail message.
   */
  public ConnectTimeoutException() {
    super();
  }

  /**
   * Creates a ConnectTimeoutException with the specified detail message.
   *
   * @param message The exception detail message
   */
  public ConnectTimeoutException(String message) {
    super(message);
  }

  /**
   * Creates a new ConnectTimeoutException with the specified detail message and cause.
   *
   * @param message the exception detail message
   * @param cause   the <tt>Throwable</tt> that caused this exception, or <tt>null</tt>
   *                if the cause is unavailable, unknown, or not a <tt>Throwable</tt>
   */
  public ConnectTimeoutException(String message, Throwable cause) {
    super(message);
    super.initCause(cause);
  }

}