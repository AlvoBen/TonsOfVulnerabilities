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
import com.sap.httpclient.exception.TimeoutException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This helper class is intedned to help work around the limitation of older Java versions
 * (older than 1.4) that prevents from specifying a connection timeout when creating a
 * socket. This factory executes a controller thread overssing the process of socket
 * initialisation. If the socket constructor cannot be created within the specified time
 * limit, the controller terminates and throws an {@link ConnectTimeoutException}
 *
 * @author Nikolai Neichev
 */
public final class TimeoutSocketFactory {

  private TimeoutSocketFactory() {
    super();
  }

  /**
   * This method spawns a controller thread overseeing the process of socket
   * initialisation. If the socket constructor cannot be created within the specified time
   * limit, the controller terminates and throws an {@link ConnectTimeoutException}
   *
	 * @param socketfactory the socket provider factory
   * @param host         the host name/IP
   * @param port         the port on the host
   * @param localAddress the local host name/IP to bind the socket to
   * @param localPort    the port on the local machine
   * @param timeout      the timeout value to be used in milliseconds. If the socket cannot be
   *                     completed within the specified time limit, it will be abandoned
   * @return a connected Socket
   * @throws IOException             if an I/O error occurs while creating the socket
   */
  public static Socket createSocket(final ProtocolSocketFactory socketfactory,
                                    final String host,
                                    final int port,
                                    final InetAddress localAddress,
                                    final int localPort,
                                    int timeout)
          throws IOException {

    SocketTask task = new SocketTask() {
      public void doit() throws IOException {
        setSocket(socketfactory.createSocket(host, port, localAddress, localPort));
      }
    };
    try {
      execute(task, timeout);
    } catch (TimeoutException e) {
      throw new ConnectTimeoutException("The host did not accept the connection within timeout of "
              + timeout + " ms");
    }
    Socket socket = task.getSocket();
    if (task.exception != null) {
      throw task.exception;
    }
    return socket;
  }

  /**
   * Creates a socket using the specified SocketTask in a specified timeout
   * @param task The specified SocketTask
   * @param timeout The specified timeout
   * @return The new socket
   * @throws IOException             if an I/O error occurs while creating the socket
   */
  public static Socket createSocket(final SocketTask task, int timeout)
          throws IOException {
    try {
      execute(task, timeout);
    } catch (TimeoutException e) { // $JL-EXC$
      throw new ConnectTimeoutException("Connection not accepted within " + timeout + " ms");
    }
    Socket socket = task.getSocket();
    if (task.exception != null) {
      throw task.exception;
    }
    return socket;
  }

  /**
   * Executes <code>task</code>. Waits for <code>timeout</code>
   * milliseconds for the task to end and returns. If the task does not return
   * in time, the thread is interrupted and an Exception is thrown.
   * The caller should override the Thread.interrupt() method to something that
   * quickly makes the thread die or use Thread.isInterrupted().
   *
   * @param task    The thread to execute
   * @param timeout The timeout in milliseconds. 0 means to wait forever.
   * @throws com.sap.httpclient.exception.TimeoutException if the timeout passes and the thread does not return.
   */
  public static void execute(Runnable task, long timeout) throws TimeoutException {
    Thread t = new Thread(task, "Timeouts if delayed more than " + timeout + " ms");
    t.setDaemon(true);
    t.start();
    try {
      t.join(timeout);
    } catch (InterruptedException e) {
      // $JL-EXC$
    }
    if (t.isAlive()) {
      t.interrupt();
      throw new TimeoutException();
    }
  }

  /**
   * Helper class for wrapping socket based tasks.
   */
  public static abstract class SocketTask implements Runnable {
    /**
     * The socket
     */
    private Socket socket;
    /**
     * The exception
     */
    private IOException exception;

    /**
     * Set the socket.
     *
     * @param newSocket The new socket.
     */
    protected void setSocket(final Socket newSocket) {
      socket = newSocket;
    }

    /**
     * Return the socket.
     *
     * @return Socket The socket.
     */
    protected Socket getSocket() {
      return socket;
    }

    /**
     * Perform the logic.
     *
     * @throws IOException If an IO problem occurs
     */
    public abstract void doit() throws IOException;

    /**
     * Execute the logic in this object and keep track of any exceptions.
     */
    public void run() {
      try {
        doit();
      } catch (IOException e) {
        exception = e;
      }
    }
  }
}