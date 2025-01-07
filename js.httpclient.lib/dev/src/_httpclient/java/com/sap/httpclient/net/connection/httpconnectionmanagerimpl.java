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

import com.sap.httpclient.HostConfiguration;
import com.sap.httpclient.HttpClientParameters;
import com.sap.httpclient.utils.IdleConnectionStorrage;
import com.sap.httpclient.net.connection.pool.HttpConnectionPool;
import com.sap.httpclient.exception.ConnectionPoolTimeoutException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.io.InputStream;
import java.io.IOException;
import java.util.WeakHashMap;

/**
 * The default implementation of the HttpConnectionManager.
 *
 * @author Nikolai Neichev
 */
public class HttpConnectionManagerImpl implements HttpConnectionManager {

  private static final Location LOG = Location.getLocation(HttpConnectionManagerImpl.class);

  // the current parameters
  private HttpClientParameters params = null;

  // the connection pool
  private HttpConnectionPool connectionPool = new HttpConnectionPool(this);

  // the idle connceiton store
  private IdleConnectionStorrage idleConnectionStorrage = new IdleConnectionStorrage();

  // shutdown flag
  private boolean shutdown = false;

  /**
   * Holds references to all active instances of this class.
   */
  private static final WeakHashMap<HttpConnectionManagerImpl, Object> CONNECTION_MANAGERS = new WeakHashMap<HttpConnectionManagerImpl, Object>();

  public HttpConnectionManagerImpl() {
    synchronized (CONNECTION_MANAGERS) {
      CONNECTION_MANAGERS.put(this, null);
    }
  }

  /**
   * Provides connection for this host configuration. If connection is not available, sleeps until there is.
   *
   * @param hostConfiguration the host configuration
   * @return the Http connection
   */
  public HttpConnection getConnection(HostConfiguration hostConfiguration) {
    while (true) {
      try {
        return getConnectionWithTimeout(hostConfiguration, 0);
      } catch (ConnectionPoolTimeoutException cpe) {
        // $JL-EXC$
        LOG.traceThrowableT(Severity.WARNING, "Connection pool pxception, will try again...", cpe);
      }
    }
  }

  /**
   * Provides connection for this host configuration for specified timeout.
   *
   * @param hostConfiguration the host configuration
   * @param timeout the timeout
   * @return the Http connection
   * @throws ConnectionPoolTimeoutException if there is no available connections after the timeout
   */
  public HttpConnection getConnectionWithTimeout(HostConfiguration hostConfiguration, long timeout)
          throws ConnectionPoolTimeoutException {

    if (hostConfiguration == null) {
      throw new IllegalArgumentException("hostConfiguration is null");
    }
    if (LOG.beDebug()) {
      LOG.debugT("Get connection with hostConfiguration: " + hostConfiguration + ", timeout: " + timeout);
    }
    if (shutdown) {
      throw new IllegalStateException("Connection Manager is shut down.");
    }

    HttpConnection connection = connectionPool.getConnection(hostConfiguration, timeout);
    connection.setHttpConnectionManager(this);
    idleConnectionStorrage.remove(connection);
    return connection;
  }

  /**
   * Gets the total number of pooled connections.
   *
   * @return the total number of pooled connections
   */
  public int getConnectionsInPool() {
    return connectionPool.getPooledConnectionsCount();
  }

  /**
   * Gets the total number of pooled connections for specified host configuration.
   *
	 * @param hostConfiguration the host configuration
   * @return the total number of pooled connections
   */
  public int getConnectionsInPool(HostConfiguration hostConfiguration) {
    return connectionPool.getPooledConnectionsCount(hostConfiguration);
  }

  /**
   * Removes all closed pooled connections.
   */
  public void removeClosedConnections() {
    connectionPool.removeClosedConnections();
  }

  /**
   * Releases the connection
   * @param conn the connection to release
   */
  public void releaseConnection(HttpConnection conn) {
    finishLastResponse(conn); // read the last response, if not yet read.
    connectionPool.returnConnection(conn);
    idleConnectionStorrage.add(conn);
  }

  /**
   * Closes all connections idle for more than the specified time
   * @param idleTimeout the time in milliseconds
   */
  public void closeIdleConnections(long idleTimeout) {
    idleConnectionStorrage.closeIdleConnections(idleTimeout);
  }

  /**
   * Since the same connection is about to be reused, make sure the
   * previous request was completely processed, and if not consume it now.
   *
   * @param conn The connection
   */
  private void finishLastResponse(HttpConnection conn) {
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

  /**
   * Gets the manager parameters
   * @return the parameters
   */
  public HttpClientParameters getParams() {
    if (params == null) {
      params = new HttpClientParameters();
    }
    return params;
  }

  /**
   * Sets the manager parameters
   * @param params the parameters
   */
  public void setParams(HttpClientParameters params) {
    this.params = params;
  }

  /**
   * Shuts down the manager
   */
  public void shutdown() {
    shutdown = true;
    connectionPool.shutdown();
    idleConnectionStorrage.closeIdleConnections(0);
    idleConnectionStorrage.removeAll();
  }

  /**
   * Shuts down and cleans up resources used by all instances of HttpConnectionManagerImpl.
   */
  public static void shutdownAll() {
    // shutdown all connection managers
    synchronized (CONNECTION_MANAGERS) {
      for (HttpConnectionManagerImpl manager : CONNECTION_MANAGERS.keySet()) {
        manager.shutdown();
      }
      CONNECTION_MANAGERS.clear();
    }
  }

  /**
   * Checks if the manager is shut down
   * @return <code>true</code> if is shutdown, <code>false</code> otherwise
   */
  public boolean isShutDown() {
    return shutdown;
  }

}
