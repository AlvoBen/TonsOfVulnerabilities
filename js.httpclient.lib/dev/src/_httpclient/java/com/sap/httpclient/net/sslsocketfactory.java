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
 * A SecureSocketFactory that uses JSSE to create sockets.
 *
 * @author Nikolai Neichev
 */
public class SSLSocketFactory implements SecureSocketFactory {

  /**
   * The factory singleton.
   */
  private static final SSLSocketFactory factory = new SSLSocketFactory();

  /**
   * Gets an singleton instance of the SSLSocketFactory.
   *
   * @return a SSLSocketFactory
   */
  static SSLSocketFactory getSocketFactory() {
    return factory;
  }

  /**
   * Constructor for SSLSocketFactory.
   */
  public SSLSocketFactory() {
    super();
  }

  public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) throws IOException{
    return javax.net.ssl.SSLSocketFactory.getDefault().createSocket(host, port, clientHost, clientPort);
  }

  /**
   * Attempts to get a new socket connection to the specified host within the specified time limit.
   * <p/>
   * This method employs several techniques to circumvent the limitations of older JREs that
   * do not support connect timeout. When running in JRE 1.4 or above reflection is used to
   * call Socket#connect(SocketAddress endpoint, int timeout) method. When executing in older
   * JREs a controller thread is executed. The controller thread attempts to create a new socket
   * within the specified limit of time. If socket constructor does not return until the timeout
   * expires, the controller terminates and throws an {@link com.sap.httpclient.exception.ConnectTimeoutException}
   * </p>
   *
   * @param host         the host name/IP
   * @param port         the port on the host
   * @param localAddress the local host name/IP to bind the socket to
   * @param localPort    the port on the local machine
   * @param params       {@link com.sap.httpclient.Parameters Http connection parameters}
   * @return Socket a new socket
   * @throws IOException          if an I/O error occurs while creating the socket
   */
  public Socket createSocket(final String host, final int port, final InetAddress localAddress,
                             final int localPort, final HttpClientParameters params) throws IOException {

    if (params == null) {
      throw new IllegalArgumentException("Parameters is null");
    }
    int timeout = params.getConnectionTimeout();
    if (timeout == 0) {
      return createSocket(host, port, localAddress, localPort);
    } else {
      // To be eventually deprecated when migrated to Java 1.4 or above
      Socket socket = ReflectionSocketFactory.createSocket("javax.net.ssl.SSLSocketFactory",
                                                            host,
                                                            port,
                                                            localAddress,
                                                            localPort,
                                                            timeout);

      if (socket == null) {
        socket = TimeoutSocketFactory.createSocket(this, host, port, localAddress, localPort, timeout);
      }
      return socket;
    }
  }

  public Socket createSocket(String host, int port) throws IOException {
    return javax.net.ssl.SSLSocketFactory.getDefault().createSocket(host, port);
  }

  public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
    return ((javax.net.ssl.SSLSocketFactory) javax.net.ssl.SSLSocketFactory.getDefault()).createSocket(socket, host, port, autoClose);
  }

  /**
   * All instances of SSLSocketFactory are the same.
   */
  public boolean equals(Object obj) {
    return ((obj != null) && obj.getClass().equals(SSLSocketFactory.class));
  }

  /**
   * All instances of SSLSocketFactory have the same hash code.
   */
  public int hashCode() {
    return SSLSocketFactory.class.hashCode();
  }

}