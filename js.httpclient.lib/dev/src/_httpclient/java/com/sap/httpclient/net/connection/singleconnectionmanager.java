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

import com.sap.tc.logging.Location;
import com.sap.httpclient.HttpClientParameters;
import com.sap.httpclient.HostConfiguration;

import java.io.IOException;
import java.io.InputStream;

/**
 * A connection manager that provides access to a single HttpConnection.
 *
 * @author Nikolai Neichev
 */
public class SingleConnectionManager implements HttpConnectionManager {

  private static final Location LOG = Location.getLocation(SingleConnectionManager.class);

  /**
   * The http connection
   */
  protected HttpConnection httpConnection;

  /**
   * Collection of parameters associated with this connection manager.
   */
  private HttpClientParameters params = new HttpClientParameters();

  /**
   * The time the connection was made idle.
   */
  private long idleStartTime = Long.MAX_VALUE;

  /**
   * Used to test if {@link #httpConnection} is currently in use
   * (i.e. checked out).  This is only used as a sanity check to help
   * debug cases where this connection manager is being used incorrectly.
   * It will not be used to enforce thread safety.
   */
  private volatile boolean inUse = false;

  /**
   * Since the same connection is about to be reused, make sure the
   * previous request was completely processed, and if not consume it now.
   *
   * @param conn The connection
   */
  static void finishLastResponse(HttpConnection conn) {
    InputStream lastResponse = conn.getLastResponseInputStream();
    if (lastResponse != null) {
      conn.setLastResponseInputStream(null);
      try {
        lastResponse.close();
      } catch (IOException ioe) { // $JL-EXC$
        // close to force reconnect.
        conn.close();
      }
    }
  }

  public SingleConnectionManager() {
  }

  public HttpConnection getConnection(HostConfiguration hostConfiguration) {
    return getConnectionWithTimeout(hostConfiguration, -1);
  }

  public HttpConnection getConnectionWithTimeout(HostConfiguration hostConfiguration, long timeout) {
    if (httpConnection == null) {
      httpConnection = new HttpConnection(hostConfiguration);
      httpConnection.setHttpConnectionManager(this);
      httpConnection.getParams().setDefaults(this.params);
    } else {
      // make sure the host and proxy are correct for this connection
      // close it and set the values if they are not
      if (!hostConfiguration.hostEquals(httpConnection) || !hostConfiguration.proxyEquals(httpConnection)) {
        if (httpConnection.isOpen()) {
          httpConnection.close();
        }
        httpConnection.setHost(hostConfiguration.getHost());
        httpConnection.setPort(hostConfiguration.getPort());
        httpConnection.setProtocol(hostConfiguration.getProtocol());
        httpConnection.setLocalAddress(hostConfiguration.getLocalAddress());
        httpConnection.setProxyHost(hostConfiguration.getProxyHost());
        httpConnection.setProxyPort(hostConfiguration.getProxyPort());
      } else {
        finishLastResponse(httpConnection);
      }
    }
    // remove the connection from the timeout handler
    idleStartTime = Long.MAX_VALUE;
    if (inUse) LOG.warningT("SingleConectionManager is badly used.");
    inUse = true;
    return httpConnection;
  }

  public void releaseConnection(HttpConnection conn) {
    if (conn != httpConnection) {
      throw new IllegalStateException("Unexpected release of an unknown connection.");
    }
    finishLastResponse(httpConnection);
    inUse = false;
    // track the time the connection was made idle
    idleStartTime = System.currentTimeMillis();
  }

  /**
   * Returns {@link HttpClientParameters parameters} associated
   * with this connection manager.
   */
  public HttpClientParameters getParams() {
    return this.params;
  }

  /**
   * Assigns {@link HttpClientParameters parameters} for this
   * connection manager.
   */
  public void setParams(final HttpClientParameters params) {
    if (params == null) {
      throw new IllegalArgumentException("Parameters is null");
    }
    this.params = params;
  }

  /**
   * Close the idle connections
   * @param idleTimeout the close timeout
   */
  public void closeIdleConnections(long idleTimeout) {
    long maxIdleTime = System.currentTimeMillis() - idleTimeout;
    if (idleStartTime <= maxIdleTime) {
      httpConnection.close();
    }
  }
}