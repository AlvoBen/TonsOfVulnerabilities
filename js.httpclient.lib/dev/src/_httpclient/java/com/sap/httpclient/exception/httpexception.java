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

import java.io.IOException;

/**
 * Signals that an HTTP or HttpClient exception has occurred.
 *
 * @author Nikolai Neichev
 */
public class HttpException extends IOException {

  /**
   * The original Throwable representing the cause of this error
   */
  private final Throwable cause;

  /**
   * Creates a new HttpException with a <tt>null</tt> detail message.
   */
  public HttpException() {
    super();
    this.cause = null;
  }

  /**
   * Creates a new HttpException with the specified detail message.
   *
   * @param message the exception detail message
   */
  public HttpException(String message) {
    super(message);
    this.cause = null;
  }

  /**
   * Creates a new HttpException with the specified detail message and cause.
   *
   * @param message the exception detail message
   * @param cause   the <tt>Throwable</tt> that caused this exception, or <tt>null</tt>
   *                if the cause is unavailable, unknown, or not a <tt>Throwable</tt>
   */
  public HttpException(String message, Throwable cause) {
    super(message);
    this.cause = cause;
    super.initCause(cause);
  }

  /**
   * Return the <tt>Throwable</tt> that caused this exception, or <tt>null</tt>
   * if the cause is unavailable, unknown, or not a <tt>Throwable</tt>.
   *
   * @return the <tt>Throwable</tt> that caused this exception, or <tt>null</tt>
   *         if the cause is unavailable, unknown, or not a <tt>Throwable</tt>
   */
  public Throwable getCause() {
    return cause;
  }

}