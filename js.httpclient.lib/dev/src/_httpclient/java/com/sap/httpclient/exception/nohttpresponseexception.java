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
 * <p/>
 * Signals that the target server failed to respond with a valid HTTP response.
 * </p>
 *
 * @author Nikolai Neichev
 */
public class NoHttpResponseException extends IOException {

  /**
   * Creates a new NoHttpResponseException with a <tt>null</tt> detail message.
   */
  public NoHttpResponseException() {
    super();
  }

  /**
   * Creates a new NoHttpResponseException with the specified detail message.
   *
   * @param message exception message
   */
  public NoHttpResponseException(String message) {
    super(message);
  }

  /**
   * Creates a new NoHttpResponseException with the specified detail message and cause.
   *
   * @param message the exception detail message
   * @param cause   the <tt>Throwable</tt> that caused this exception, or <tt>null</tt>
   *                if the cause is unavailable, unknown, or not a <tt>Throwable</tt>
   */
  public NoHttpResponseException(String message, Throwable cause) {
    super(message);
    super.initCause(cause);
  }
}