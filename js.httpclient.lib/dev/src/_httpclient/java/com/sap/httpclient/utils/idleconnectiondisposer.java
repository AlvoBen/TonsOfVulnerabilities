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
package com.sap.httpclient.utils;

import com.sap.httpclient.net.connection.HttpConnectionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for periodically closing idle connections.
 *
 * @author Nikolai Neichev
 */
public class IdleConnectionDisposer extends Thread {

  private List<HttpConnectionManager> connectionManagers = new ArrayList<HttpConnectionManager>();

  private boolean shutdown = false;

  private long timeoutInterval = 1000;

  private long connectionTimeout = 3000;

  public IdleConnectionDisposer() {
    setDaemon(true);
  }

  /**
   * Registers a connection manager.
   *
   * @param connectionManager The connection manager to add
   */
  public synchronized void registerConnectionManager(HttpConnectionManager connectionManager) {
    if (shutdown) {
      throw new IllegalStateException("IdleConnectionDisposer has been shutdown");
    }
    this.connectionManagers.add(connectionManager);
  }

  /**
   * Unregisters the connection manager.
   *
   * @param connectionManager The connection manager to unregister
   */
  public synchronized void unregisterConnectionManager(HttpConnectionManager connectionManager) {
    if (shutdown) {
      throw new IllegalStateException("IdleConnectionDisposer has been shutdown");
    }
    this.connectionManagers.remove(connectionManager);
  }

  /**
   * Closes idle connections.
   */
  public synchronized void run() {
    while (!shutdown) {
      for (HttpConnectionManager connectionManager : connectionManagers) {
        connectionManager.closeIdleConnections(connectionTimeout);
      }
      try {
        this.wait(timeoutInterval);
      } catch (InterruptedException e) {
        // $JL-EXC$
      }
    }
    // shutdown
    this.connectionManagers.clear();
  }

  /**
   * Shuts down the disposer
   */
  public synchronized void shutdown() {
    this.shutdown = true;
    this.notifyAll();
  }

  /**
   * Sets the timeout value.
   *
   * @param connectionTimeout The connection timeout in milliseconds
   */
  public synchronized void setConnectionTimeout(long connectionTimeout) {
    if (shutdown) {
      throw new IllegalStateException("IdleConnectionDisposer has been shutdown");
    }
    this.connectionTimeout = connectionTimeout;
  }

  /**
   * Sets the interval used by the disposer between closing idle connections.
   *
   * @param timeoutInterval The timeout interval in milliseconds
   */
  public synchronized void setTimeoutInterval(long timeoutInterval) {
    if (shutdown) {
      throw new IllegalStateException("IdleConnectionDisposer has been shutdown");
    }
    this.timeoutInterval = timeoutInterval;
  }

}