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
package com.sap.httpclient.http.methods;

import com.sap.httpclient.net.connection.HttpConnection;
import com.sap.httpclient.HttpMethod;
import com.sap.httpclient.HttpMethodImpl;
import com.sap.httpclient.HttpState;
import com.sap.httpclient.exception.HttpException;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.utils.dump.Dump;
import com.sap.tc.logging.Location;

import java.io.IOException;

/**
 * Establishes a tunneled HTTP connection via the CONNECT method.
 *
 * @author Nikolai Neichev
 */
public class CONNECT extends HttpMethodImpl {

  private static final Location LOG = Location.getLocation(CONNECT.class);

  /**
   * Create a connect method.
   */
  public CONNECT() {
  }

  /**
   * Returns <tt>"CONNECT"</tt>.
   *
   * @return <tt>"CONNECT"</tt>
   */
  public String getName() {
    return HttpMethod.METHOD_CONNECT;
  }

  /**
   * Adds the cookie request header.
   *
   * @param state current state of http requests
   * @param conn  the connection to use for I/O
   * @throws IOException   when errors occur reading or writing to/from the connection
   * @throws HttpException when a recoverable error occurs
   */
  protected void addCookieRequestHeader(HttpState state, HttpConnection conn) throws IOException {
    // this method does nothing. <tt>CONNECT</tt> request doesn't have cookies
  }



  /**
   * Adds the request headers to the specified {@link HttpConnection}.
   * Adds <tt>User-Agent</tt>, <tt>Host</tt>, and <tt>Proxy-Authorization</tt> headers, when appropriate.
   *
   * @param state the client state
   * @param conn  the {@link HttpConnection} the headers will eventually be written to
   * @throws IOException   when an error occurs writing the request
   * @throws HttpException when a HTTP net error occurs
   */
  protected void addRequestHeaders(HttpState state, HttpConnection conn) throws IOException {
    addUserAgentRequestHeader(state, conn);
    addHostRequestHeader(state, conn);
    addProxyConnectionHeader(state, conn);
  }

  /**
   * Execute this method and create a tunneled HttpConnection.
   *
   * @param state the current http state
   * @param conn  the connection to write to
   * @return the http status code from execution
   * @throws HttpException when an error occurs writing the headers
   * @throws IOException   when an error occurs writing the headers
   */
  public int execute(HttpState state, HttpConnection conn) throws IOException {
    int code = super.execute(state, conn);
    if (LOG.beDebug()) {
      LOG.debugT("CONNECT status code " + code);
    }
    return code;
  }

  /**
   * Special Connect request.
   *
   * @param state the current http state
   * @param conn  the connection to write to
   * @throws IOException   when an error occurs writing the request
   * @throws HttpException when an error occurs writing the request
   */
  protected void writeRequestLine(HttpState state, HttpConnection conn) throws IOException {
    int port = conn.getPort();
    if (port == -1) {
      port = conn.getProtocol().getDefaultPort();
    }
    StringBuilder buffer = new StringBuilder();
    buffer.append(getName());
    buffer.append(' ');
    buffer.append(conn.getHost());
    if (port > -1) {
      buffer.append(':');
      buffer.append(port);
    }
    buffer.append(" ");
    buffer.append(getEffectiveVersion());
    String line = buffer.toString();
    conn.printLine(line, getParams().getHttpElementCharset());
    if (Dump.HEADER_DUMP.enabled() || Dump.DEBUG) {
      Dump.HEADER_DUMP.outgoing(line);
    }
  }

  /**
   * Checks wether to close the connection
   *
   * @return <code>true</code> if the status code is anything other than SC_OK, <code>false</code> otherwise.
   */
  protected boolean shouldCloseConnection(HttpConnection conn) {
    if (getStatusCode() == HttpStatus.SC_OK) {
      Header connectionHeader = null;
      if (!conn.isTransparent()) {
        connectionHeader = getResponseHeader("proxy-connection");
      }
      if (connectionHeader == null) {
        connectionHeader = getResponseHeader("connection");
      }
      if (connectionHeader != null) {
        if (connectionHeader.getValue().equalsIgnoreCase("close")) {
          if (LOG.beWarning()) {
            LOG.warningT("Invalid header : '" + connectionHeader.toText()
                    + "' response is: " + getStatusLine().toString());
          }
        }
      }
      return false;
    } else {
      return super.shouldCloseConnection(conn);
    }
  }

}