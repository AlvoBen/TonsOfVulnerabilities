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
 * Signals a failure in authentication process
 *
 * @author Nikolai Neichev
 */
public class AuthenticationException extends ProtocolException {

  /**
   * Creates a new AuthenticationException with a <tt>null</tt> detail message.
   */
  public AuthenticationException() {
    super();
  }

  /**
   * Creates a new AuthenticationException with the specified message.
   *
   * @param message the exception detail message
   */
  public AuthenticationException(String message) {
    super(message);
  }

  /**
   * Creates a new AuthenticationException with the specified detail message and cause.
   *
   * @param message the exception detail message
   * @param cause   the <tt>Throwable</tt> that caused this exception, or <tt>null</tt>
   *                if the cause is unavailable, unknown, or not a <tt>Throwable</tt>
   */
  public AuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }

}