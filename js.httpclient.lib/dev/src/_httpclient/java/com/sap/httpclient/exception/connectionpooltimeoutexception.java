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

/**
 * A timeout while connecting waiting for an available connection from the HttpConnectionManager.
 *
 * @author Nikolai Neichev
 */
public class ConnectionPoolTimeoutException extends ConnectTimeoutException {

  /**
   * Creates a ConnectTimeoutException with a <tt>null</tt> detail message.
   */
  public ConnectionPoolTimeoutException() {
    super();
  }

  /**
   * Creates a ConnectTimeoutException with the specified detail message.
   *
   * @param message The exception detail message
   */
  public ConnectionPoolTimeoutException(String message) {
    super(message);
  }

  /**
   * Creates a new ConnectTimeoutException with the specified detail message and cause.
   *
   * @param message the exception detail message
   * @param cause   the <tt>Throwable</tt> that caused this exception, or <tt>null</tt>
   *                if the cause is unavailable, unknown, or not a <tt>Throwable</tt>
   */
  public ConnectionPoolTimeoutException(String message, Throwable cause) {
    super(message, cause);
  }

}