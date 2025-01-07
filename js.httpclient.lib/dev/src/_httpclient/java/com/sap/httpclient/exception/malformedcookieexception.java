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
 * Signals that a cookie is in some way invalid or illegal in a specified
 * context
 *
 * @author Nikolai Neichev
 */
public class MalformedCookieException extends ProtocolException {

  /**
   * Creates a new MalformedCookieException with a <tt>null</tt> detail message.
   */
  public MalformedCookieException() {
    super();
  }

  /**
   * Creates a new MalformedCookieException with a specified message string.
   *
   * @param message The exception detail message
   */
  public MalformedCookieException(String message) {
    super(message);
  }

  /**
   * Creates a new MalformedCookieException with the specified detail message and cause.
   *
   * @param message the exception detail message
   * @param cause   the <tt>Throwable</tt> that caused this exception, or <tt>null</tt>
   *                if the cause is unavailable, unknown, or not a <tt>Throwable</tt>
   */
  public MalformedCookieException(String message, Throwable cause) {
    super(message, cause);
  }
}