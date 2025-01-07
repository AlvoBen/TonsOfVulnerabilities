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
 * Signals that an error has occurred.
 *
 * @author Nikolai Neichev
 */
public class HttpClientError extends Error {   //$JL-EXC$

  /**
   * Creates a new HttpClientError with a <tt>null</tt> detail message.
   */
  public HttpClientError() {
    this(null);
  }

  /**
   * Creates a new HttpClientError with the specified detail message.
   *
   * @param message The error message
   */
  public HttpClientError(String message) {
    super(message);
  }

}