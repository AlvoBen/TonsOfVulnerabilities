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
 * Signals a circular redirect
 *
 * @author Nikolai Neichev
 */
public class CircularRedirectException extends RedirectException {

  /**
   * Creates a new CircularRedirectException with a <tt>null</tt> detail message.
   */
  public CircularRedirectException() {
    super();
  }

  /**
   * Creates a new CircularRedirectException with the specified detail message.
   *
   * @param message The exception detail message
   */
  public CircularRedirectException(String message) {
    super(message);
  }

  /**
   * Creates a new CircularRedirectException with the specified detail message and cause.
   *
   * @param message the exception detail message
   * @param cause   the <tt>Throwable</tt> that caused this exception, or <tt>null</tt>
   *                if the cause is unavailable, unknown, or not a <tt>Throwable</tt>
   */
  public CircularRedirectException(String message, Throwable cause) {
    super(message, cause);
  }
}