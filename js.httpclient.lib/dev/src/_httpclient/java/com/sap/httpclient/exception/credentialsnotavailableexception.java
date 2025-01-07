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
 * Authentication credentials required to respond to a authentication
 * challenge are not available
 *
 * @author Nikolai Neichev
 */
public class CredentialsNotAvailableException extends AuthenticationException {
  /**
   * Creates a new CredentialsNotAvailableException with a <tt>null</tt> detail message.
   */
  public CredentialsNotAvailableException() {
    this(null);
  }

  /**
   * Creates a new CredentialsNotAvailableException with the specified message.
   *
   * @param message the exception detail message
   */
  public CredentialsNotAvailableException(String message) {
    this(message, null);
  }

  /**
   * Creates a new CredentialsNotAvailableException with the specified detail message and cause.
   *
   * @param message the exception detail message
   * @param cause   the <tt>Throwable</tt> that caused this exception, or <tt>null</tt>
   *                if the cause is unavailable, unknown, or not a <tt>Throwable</tt>
   */
  public CredentialsNotAvailableException(String message, Throwable cause) {
    super(message, cause);
  }
}