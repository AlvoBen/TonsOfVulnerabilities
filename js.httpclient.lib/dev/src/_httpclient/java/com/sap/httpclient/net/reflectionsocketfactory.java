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

import com.sap.httpclient.exception.ConnectTimeoutException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This helper class uses refelction in order to execute Socket methods
 * available in Java 1.4 and above
 *
 * @author Nikolai Neichev
 */
public final class ReflectionSocketFactory {

  private static boolean REFLECTION_FAILED = false;

  private static Constructor INETSOCKETADDRESS_CONSTRUCTOR = null;
  private static Method SOCKETCONNECT_METHOD = null;
  private static Method SOCKETBIND_METHOD = null;
  private static Class SOCKETTIMEOUTEXCEPTION_CLASS = null;

  private ReflectionSocketFactory() {
    super();
  }

  /**
   * This method attempts to execute Socket method available since Java 1.4
   * using reflection. If the methods are not available or could not be executed
   * <tt>null</tt> is returned
   *
   * @param socketfactoryName name of the socket factory class
   * @param host              the host name/IP
   * @param port              the port on the host
   * @param localAddress      the local host name/IP to bind the socket to
   * @param localPort         the port on the local machine
   * @param timeout           the timeout value to be used in milliseconds. If the socket cannot be
   *                          completed within the specified time limit, it will be abandoned
   * @return a connected Socket
   * @throws IOException             if an I/O error occurs while creating the socket
   */
  public static Socket createSocket(final String socketfactoryName,
                                    final String host,
                                    final int port,
                                    final InetAddress localAddress,
                                    final int localPort,
                                    int timeout)
          throws IOException {

    if (REFLECTION_FAILED) {
      //This is known to have failed before. Do not try it again
      return null;
    }
    // This code uses reflection to essentially do the following:
    //
    //  SocketFactory socketFactory = Class.forName(socketfactoryName).getDefault();
    //  Socket socket = socketFactory.createSocket();
    //  SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
    //  SocketAddress remoteaddr = new InetSocketAddress(host, port);
    //  socket.bind(localaddr);
    //  socket.connect(remoteaddr, timeout);
    //  return socket;
    try {
      Class socketfactoryClass = Class.forName(socketfactoryName);
      Method method = socketfactoryClass.getMethod("getDefault");
      Object socketfactory = method.invoke(null);
      method = socketfactoryClass.getMethod("createSocket");
      Socket socket = (Socket) method.invoke(socketfactory);
      if (INETSOCKETADDRESS_CONSTRUCTOR == null) {
        Class addressClass = Class.forName("java.net.InetSocketAddress");
        INETSOCKETADDRESS_CONSTRUCTOR = addressClass.getConstructor(InetAddress.class, Integer.TYPE);
      }
      Object remoteaddr = INETSOCKETADDRESS_CONSTRUCTOR.newInstance(InetAddress.getByName(host), port);
      Object localaddr = INETSOCKETADDRESS_CONSTRUCTOR.newInstance(localAddress, localPort);
      if (SOCKETCONNECT_METHOD == null) {
        SOCKETCONNECT_METHOD = Socket.class.getMethod("connect", Class.forName("java.net.SocketAddress"), Integer.TYPE);
      }
      if (SOCKETBIND_METHOD == null) {
        SOCKETBIND_METHOD = Socket.class.getMethod("bind", Class.forName("java.net.SocketAddress"));
      }
      SOCKETBIND_METHOD.invoke(socket, localaddr);
      SOCKETCONNECT_METHOD.invoke(socket, remoteaddr, timeout);
      return socket;
    } catch (InvocationTargetException e) {
      Throwable cause = e.getTargetException();
      if (SOCKETTIMEOUTEXCEPTION_CLASS == null) {
        try {
          SOCKETTIMEOUTEXCEPTION_CLASS = Class.forName("java.net.SocketTimeoutException");
        } catch (ClassNotFoundException ex) {
          // At this point this should never happen. Really.
          REFLECTION_FAILED = true;
          return null;
        }
      }
      if (SOCKETTIMEOUTEXCEPTION_CLASS.isInstance(cause)) {
        throw new ConnectTimeoutException("The host did not accept the connection within timeout of "
                + timeout + " ms", cause);
      }
      if (cause instanceof IOException) {
        throw (IOException) cause;
      }
      return null;
    } catch (Exception e) {
      REFLECTION_FAILED = true;
      return null;
    }
  }
}