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
package com.sap.httpclient;

import com.sap.httpclient.exception.NoHttpResponseException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;

/**
 * Used for checking wether to retry a method or not.
 *
 * @author Nikolai Neichev
 */
public class HttpMethodRetryHandler  {

  private static Class SSL_HANDSHAKE_EXCEPTION = null;

  static {
    try {
      SSL_HANDSHAKE_EXCEPTION = Class.forName("javax.net.ssl.SSLHandshakeException");
    } catch (ClassNotFoundException cnfe) {
      // $JL-EXC$
    }
  }

  private int maxRetryCount;

  /**
   * Whether or not methods that have successfully sent their requests will be retried
   */
  private boolean retrySentRequests;

  /**
   * Creates a new HttpMethodRetryHandler.
   *
   * @param retryCount              the number of times a method will be retried
   * @param requestSentRetryEnabled if true, methods that have successfully sent their request will be retried
   */
  public HttpMethodRetryHandler(int retryCount, boolean requestSentRetryEnabled) {
    super();
    this.maxRetryCount = retryCount;
    this.retrySentRequests = requestSentRetryEnabled;
  }

  /**
   * Creates a handler that retries up to 3 times and
   * does not retry methods that have successfully sent their requests.
   */
  public HttpMethodRetryHandler() {
    this(3, false);
  }

  /**
   * Used <code>retryCount</code> and <code>requestSentRetryEnabled</code> to determine
   * if the specified method should be retried.
   */
  /**
   * Determines whether the specified method should be retried
   *
   * @param method the specified method
   * @param exception the exception that failed the original request
   * @param executionCount reopeat count of the current execution
   * @return <tt>true</tt> if the method should be retried, <tt>false</tt> if not
   */
  public boolean retryMethod(final HttpMethod method, final IOException exception, int executionCount) {
    if (method == null) {
      throw new IllegalArgumentException("HTTP method is null");
    }
    if (exception == null) {
      throw new IllegalArgumentException("Exception parameter is null");
    }
    if (method.isAborted()) { // aborted by user
      return false;
    }
    if (executionCount > this.maxRetryCount) { // max retry count reached
      return false;
    }
    if (exception instanceof NoHttpResponseException) { // the server dropped the connection, will retry
      return true;
    }
    if (exception instanceof InterruptedIOException) { // request timed out, won't retry
      return false;
    }
    if (exception instanceof UnknownHostException) { // unknown host , won't retry
      return false;
    }
    if (exception instanceof NoRouteToHostException) { // host is unreachable, won't retry
      return false;
    }
    if (SSL_HANDSHAKE_EXCEPTION != null && SSL_HANDSHAKE_EXCEPTION.isInstance(exception)) { // SSL handshake error
      return false;
    }
    if (!method.isRequestSent()) { // method is not sent yet, will "retry"
      return true;
    } else if (this.retrySentRequests) { // retry successfully sent method
      return true;
    }
    return false; // do not retry by default
  }

  /**
   * Gets the retry sent requests flag
   *
   * @return <code>true</code> if this handler will retry already sent methods, <code>false</code> otherwise
   */
  public boolean isRetrySentRequests() {
    return retrySentRequests;
  }

  /**
   * Gets the max retry count setting
   *
   * @return the maximum number of times a method will be retried
   */
  public int getMaxRetryCount() {
    return maxRetryCount;
  }
}