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
package com.sap.httpclient.net;

import com.sap.httpclient.HttpClientParameters;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * A factory for creating Sockets.
 * <p/>
 * <p>Both {@link java.lang.Object#equals(java.lang.Object) Object.equals()} and
 * {@link java.lang.Object#hashCode() Object.hashCode()} should be overridden appropriately.
 * Protocol socket factories are used to uniquely identify <code>Protocol</code>s and
 * <code>HostConfiguration</code>s, and <code>equals()</code> and <code>hashCode()</code> are
 * required for the correct operation of some connection managers.</p>
 *
 * @author Nikolai Neichev
 */
public interface ProtocolSocketFactory {

  /**
   * Gets a new socket connection to the specified host.
   *
   * @param host         the host name/IP
   * @param port         the port on the host
   * @param localAddress the local host name/IP to bind the socket to
   * @param localPort    the port on the local machine
   * @return Socket a new socket
   * @throws IOException          if an I/O error occurs while creating the socket
   */
  Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException;

  /**
   * Gets a new socket connection to the specified host.
   *
   * @param host         the host name/IP
   * @param port         the port on the host
   * @param localAddress the local host name/IP to bind the socket to
   * @param localPort    the port on the local machine
   * @param params       {@link com.sap.httpclient.Parameters Http connection parameters}
   * @return Socket a new socket
   * @throws IOException             if an I/O error occurs while creating the socket
   */
  Socket createSocket(String host, int port, InetAddress localAddress, int localPort, HttpClientParameters params)
          throws IOException;

  /**
   * Gets a new socket connection to the specified host.
   *
   * @param host the host name/IP
   * @param port the port on the host
   * @return Socket a new socket
   * @throws IOException          if an I/O error occurs while creating the socket
   */
  Socket createSocket(String host, int port) throws IOException;

}