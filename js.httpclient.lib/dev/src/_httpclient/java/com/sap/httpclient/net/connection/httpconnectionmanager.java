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
package com.sap.httpclient.net.connection;

import com.sap.httpclient.exception.ConnectionPoolTimeoutException;
import com.sap.httpclient.HostConfiguration;
import com.sap.httpclient.HttpClientParameters;

/**
 * An interface for classes that manage HttpConnections.
 *
 * @author Nikolai Neichev
 */
public interface HttpConnectionManager {

  /**
   * The default maximum number of connections allowed per host
   */
  public static final int DEFAULT_MAX_HOST_CONNECTIONS = 2;   // RFC 2616 sec 8.1.4

  /**
   * The default maximum number of connections allowed overall
   */
  public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;

  /**
   * Gets an HttpConnection for a specified host configuration. If a connection is
   * not available this method will block until one is.
   * <p/>
   * The connection manager should be registered with any HttpConnection that
   * is created.
   *
   * @param hostConfiguration the host configuration to use to configure the
   *                          connection
   * @return an HttpConnection for the specified configuration
   */
  HttpConnection getConnection(HostConfiguration hostConfiguration);

  /**
   * Gets an HttpConnection for a specified host configuration. If a connection is
   * not available, this method will block for at most the specified number of
   * milliseconds or until a connection becomes available.
   * <p/>
   * The connection manager should be registered with any HttpConnection that
   * is created.
   *
   * @param hostConfiguration the host configuration to use to configure the
   *                          connection
   * @param timeout           - the time (in milliseconds) to wait for a connection to
   *                          become available, 0 to specify an infinite timeout
   * @return an HttpConnection for the specified configuraiton
   * @throws com.sap.httpclient.exception.ConnectionPoolTimeoutException if no connection becomes available before the
   *                                        timeout expires
   */
  HttpConnection getConnectionWithTimeout(HostConfiguration hostConfiguration, long timeout)
          throws ConnectionPoolTimeoutException;

  /**
   * Releases the specified HttpConnection for use by other requests.
   *
   * @param conn - The HttpConnection to make available.
   */
  void releaseConnection(HttpConnection conn);

  /**
   * Closes connections that have been idle for at least the specified amount of time.  Only
   * connections that are currently owned, not checked out, are subject to idle timeouts.
   *
   * @param idleTimeout the minimum idle time, in milliseconds, for connections to be closed
   */
  void closeIdleConnections(long idleTimeout);

  /**
   * Returns {@link com.sap.httpclient.HttpClientParameters parameters} associated
   * with this connection manager.
	 *
	 * @return the params of the manager
	 */
  HttpClientParameters getParams();

  /**
   * Assigns {@link HttpClientParameters parameters} for this
   * connection manager.
	 *
	 * @param params the params to set
	 */
  void setParams(final HttpClientParameters params);
}