package com.sap.httpclient;

import java.io.IOException;
import java.io.InputStream;

import com.sap.httpclient.exception.ConnectionPoolTimeoutException;
import com.sap.httpclient.net.connection.HttpConnectionManager;
import com.sap.httpclient.net.connection.HttpConnection;

public class NoHostHttpConnectionManager implements HttpConnectionManager {

  private HttpConnection connection;

  private boolean connectionReleased = false;

  private HttpClientParameters params = new HttpClientParameters();

  public NoHostHttpConnectionManager() {
    super();
  }

  /**
   * This method currently does nothing.
   */
  public void closeIdleConnections(long idleTimeout) {
  }

  /**
	 * Check if the connection is released
	 *
   * @return true if connection is released
   */
  public boolean isConnectionReleased() {
    return connectionReleased;
  }

  /**
	 * Sets the connection
   * @param connection the connection to set
   */
  public void setConnection(HttpConnection connection) {
    this.connection = connection;
    connection.setHttpConnectionManager(this);
    connection.getParams().setDefaults(this.params);
  }

  public HttpConnection getConnection(HostConfiguration hostConfiguration) {
    // make sure the host and proxy are correct for this connection
    // close it and set the values if they are not
    if (!hostConfiguration.hostEquals(connection) || !hostConfiguration.proxyEquals(connection)) {
      if (connection.isOpen()) {
        connection.close();
      }
      connection.setHost(hostConfiguration.getHost());
      connection.setPort(hostConfiguration.getPort());
      connection.setProtocol(hostConfiguration.getProtocol());
      connection.setLocalAddress(hostConfiguration.getLocalAddress());
      connection.setProxyHost(hostConfiguration.getProxyHost());
      connection.setProxyPort(hostConfiguration.getProxyPort());
    } else {
      finishLastResponse(connection);
    }
    connectionReleased = false;
    return connection;
  }

  public HttpConnection getConnectionWithTimeout(HostConfiguration hostConfiguration, long timeout)
          throws ConnectionPoolTimeoutException {
    return getConnection(hostConfiguration);
  }

  public void releaseConnection(HttpConnection conn) {
    if (conn != connection) {
      throw new IllegalStateException("Unexpected close on a different connection.");
    }
    connectionReleased = true;
    finishLastResponse(connection);
  }

  /**
   * Since the same connection is about to be reused, make sure the
   * previous request was completely processed, and if not
   * consume it now.
   *
   * @param conn The connection
   */
  static void finishLastResponse(HttpConnection conn) {
    InputStream lastResponse = conn.getLastResponseInputStream();
    if (lastResponse != null) {
      conn.setLastResponseInputStream(null);
      try {
        lastResponse.close();
      } catch (IOException ioe) {
        // $JL-EXC$
        conn.close();
      }
    }
  }

  public HttpClientParameters getParams() {
    return this.params;
  }

  public void setParams(final HttpClientParameters params) {
    if (params == null) {
      throw new IllegalArgumentException("Parameters may not be null");
    }
    this.params = params;
  }

}