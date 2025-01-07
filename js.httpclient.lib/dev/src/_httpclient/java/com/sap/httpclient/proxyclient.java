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

import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.http.methods.CONNECT;
import com.sap.httpclient.net.connection.HttpConnectionManager;
import com.sap.httpclient.net.connection.HttpConnection;
import java.io.IOException;
import java.net.Socket;


/**
 * A client that provides {@link java.net.Socket sockets} for communicating through HTTP proxies
 * via the HTTP CONNECT method.  This is primarily needed for non-HTTP protocols that wish to
 * communicate via an HTTP proxy.
 * 
 * @deprecated will be removed
 * @author Nikolai Neichev
 */
public class ProxyClient {

  /**
   * The {@link HttpState HTTP state} associated with this ProxyClient.
   */
  private HttpState state = new HttpState();

  /**
   * The {@link HttpClientParameters collection of parameters} associated with this ProxyClient.
   */
  private HttpClientParameters params = null;

  /**
   * The {@link HostConfiguration host configuration} associated with the ProxyClient
   */
  private HostConfiguration hostConfiguration = new HostConfiguration();

  /**
   * Creates an instance of ProxyClient using default {@link HttpClientParameters parameter set}.
   */
  public ProxyClient() {
    this(new HttpClientParameters());
  }

  /**
   * Creates an instance of ProxyClient using the specified {@link HttpClientParameters parameter set}.
   *
   * @param params The {@link HttpClientParameters parameters} to use.
   */
  public ProxyClient(HttpClientParameters params) {
    super();
    if (params == null) {
      throw new IllegalArgumentException("Params is null");
    }
    this.params = params;
  }

  /**
   * Returns {@link HttpState HTTP state} associated with the ProxyClient.
   *
   * @return the shared client state
   */
  public synchronized HttpState getState() {
    return state;
  }

  /**
   * Assigns {@link HttpState HTTP state} for the ProxyClient.
   *
   * @param state the new {@link HttpState HTTP state} for the client
   */
  public synchronized void setState(HttpState state) {
    this.state = state;
  }

  /**
   * Returns the {@link HostConfiguration host configuration} associated with the ProxyClient.
   *
   * @return A {@link HostConfiguration host configuration}
   */
  public synchronized HostConfiguration getHostConfiguration() {
    return hostConfiguration;
  }

  /**
   * Assigns the {@link HostConfiguration host configuration} to use with the ProxyClient.
   *
   * @param hostConfiguration The {@link HostConfiguration host configuration} to set
   */
  public synchronized void setHostConfiguration(HostConfiguration hostConfiguration) {
    this.hostConfiguration = hostConfiguration;
  }

  /**
   * Returns {@link HttpClientParameters HTTP net parameters} associated with this ProxyClient.
	 *
	 * @return the params
	 */
  public synchronized HttpClientParameters getParams() {
    return this.params;
  }

  /**
   * Assigns {@link HttpClientParameters HTTP net parameters} for this ProxyClient.
	 *
	 * @param params the params
	 */
  public synchronized void setParams(final HttpClientParameters params) {
    if (params == null) {
      throw new IllegalArgumentException("Parameters is null");
    }
    this.params = params;
  }

  /**
   * Creates a socket that is connected, via the HTTP CONNECT method, to a proxy.
   *
   * @return the connect response
   * @throws IOException if an I/O error occurs
   */
  public ConnectResponse connect() throws IOException {
    if (getHostConfiguration().getProxyHost() == null) {
      throw new IllegalStateException("proxy host not set");
    }
    if (getHostConfiguration().getHost() == null) {
      throw new IllegalStateException("host not set");
    }
    CONNECT method = new CONNECT();
    method.getParams().setDefaults(getParams());
    ProxyConnectionManager connectionManager = new ProxyConnectionManager();
    connectionManager.setConnectionParams(getParams());
    HttpMethodProcessor processor = new HttpMethodProcessor(connectionManager,
            getHostConfiguration(),
            getParams(),
            getState());

    processor.executeMethod(method);
    ConnectResponse response = new ConnectResponse();
    response.setConnectMethod(method);
    if (method.getStatusCode() == HttpStatus.SC_OK) { // is connection successful
      response.setSocket(connectionManager.getConnection().getSocket());
    } else {
      response.getConnectMethod().getResponseBodyAsString(); // read the response, because the stream will be closed
      connectionManager.getConnection().close();
    }
    return response;
  }

  /**
   * Contains the method used to execute the connect along with the created socket.
   */
  public static class ConnectResponse {

    private CONNECT connectMethod;

    private Socket socket;

    private ConnectResponse() {
    }

    /**
     * Gets the method that was used to execute the connect.  This method is useful for
     * analyzing the proxy's response when a connect fails.
     *
     * @return the connectMethod.
     */
    public CONNECT getConnectMethod() {
      return connectMethod;
    }

    /**
     * @param connectMethod The connectMethod to set.
     */
    private void setConnectMethod(CONNECT connectMethod) {
      this.connectMethod = connectMethod;
    }

    /**
     * Gets the socket connected and authenticated (if appropriate) to the configured
     * HTTP proxy, or <code>null</code> if a connection could not be made.  It is the
     * responsibility of the user to close this socket when it is no longer needed.
     *
     * @return the socket.
     */
    public Socket getSocket() {
      return socket;
    }

    /**
     * @param socket The socket to set.
     */
    private void setSocket(Socket socket) {
      this.socket = socket;
    }
  }

  /**
   * A connection manager that creates a single connection.  Meant to be used only once.
   */
  static class ProxyConnectionManager implements HttpConnectionManager {

    private HttpConnection httpConnection;

    private Parameters connectionParams;

    public void closeIdleConnections(long idleTimeout) {
    }

    public HttpConnection getConnection() {
      return httpConnection;
    }

    public void setConnectionParams(Parameters httpParams) {
      this.connectionParams = httpParams;
    }

    public HttpConnection getConnectionWithTimeout(HostConfiguration hostConfiguration, long timeout) {
      httpConnection = new HttpConnection(hostConfiguration);
      httpConnection.setHttpConnectionManager(this);
      httpConnection.getParams().setDefaults(connectionParams);
      return httpConnection;
    }

    public HttpConnection getConnection(HostConfiguration hostConfiguration) {
      return getConnectionWithTimeout(hostConfiguration, -1);
    }

    public void releaseConnection(HttpConnection conn) {
    }

    public HttpClientParameters getParams() {
      return null;
    }

    public void setParams(HttpClientParameters params) {
    }
  }
}