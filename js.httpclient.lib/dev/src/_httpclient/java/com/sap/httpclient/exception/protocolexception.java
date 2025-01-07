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
 * Signals that an HTTP net violation has occurred.  For example, HttpClient
 * detected a malformed status line or headers, a missing message body, etc.
 *
 * @author Nikolai Neichev
 */
public class ProtocolException extends HttpException {

  /**
   * Creates a new ProtocolException with a <tt>null</tt> detail message.
   */
  public ProtocolException() {
    super();
  }

  /**
   * Creates a new ProtocolException with the specified detail message.
   *
   * @param message The exception detail message
   */
  public ProtocolException(String message) {
    super(message);
  }

  /**
   * Creates a new ProtocolException with the specified detail message and cause.
   *
   * @param message the exception detail message
   * @param cause   the <tt>Throwable</tt> that caused this exception, or <tt>null</tt>
   *                if the cause is unavailable, unknown, or not a <tt>Throwable</tt>
   */
  public ProtocolException(String message, Throwable cause) {
    super(message, cause);
  }
}