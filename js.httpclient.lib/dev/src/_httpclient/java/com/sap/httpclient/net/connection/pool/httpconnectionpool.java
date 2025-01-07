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
package com.sap.httpclient.net.connection.pool;

import com.sap.httpclient.net.connection.HttpConnection;
import com.sap.httpclient.net.connection.HttpConnectionManagerImpl;
import com.sap.httpclient.exception.ConnectionPoolTimeoutException;
import com.sap.httpclient.HostConfiguration;
import com.sap.tc.logging.Location;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Hashtable;

/**
 * This class represents a HTTP Connection pool
 *
 * @author Nikolai Neichev
 */
public class HttpConnectionPool {

  public final static Location LOG = Location.getLocation(HttpConnectionPool.class);

  // counts the total connections created for this connection manager
  private volatile int currentConnections;

  // counts the given connections count
  private volatile int totalUsedConnection;

  // the connection manager holding this pool
  private HttpConnectionManagerImpl manager;

  // will use the bigInts in the table for counting the connections to the key host
  private final Hashtable<HostConfiguration, Integer> connectionsCountPerHost = new Hashtable<HostConfiguration, Integer>();

  // monitor objects/counters for the waiting threads for connection on this key host
  private final HashMap<HostConfiguration, MonitorCounter> threadMonitorsPerHost = new HashMap<HostConfiguration, MonitorCounter>();

  // keeps all pooled connections
  private HashMap<HostConfiguration, HttpConnection> pooledConnections = new HashMap<HostConfiguration, HttpConnection>();

  /**
   * Constructor with specified maximum of alowed connections
   *
   * @param manager the connection manger
   */
  public HttpConnectionPool(HttpConnectionManagerImpl manager) {
    this.manager = manager;
    this.totalUsedConnection = 0;
    this.currentConnections = 0;
  }

  /**
   * Gets connection from the pool
   *
   * @param hostConfig the host configuration
   * @param timeout the timeout, <code>0</code> for infinity
   * @return the connection object
   * @throws ConnectionPoolTimeoutException if the specified timeout is exhausted
   */
  public HttpConnection getConnection(HostConfiguration hostConfig, long timeout) throws ConnectionPoolTimeoutException {
    MonitorCounter monitor;
    synchronized(threadMonitorsPerHost) {
      monitor = threadMonitorsPerHost.get(hostConfig);
      if (monitor == null) {
        monitor = new MonitorCounter(hostConfig);
        threadMonitorsPerHost.put(hostConfig, monitor);
        connectionsCountPerHost.put(hostConfig, 0);
      }
    }
    synchronized (monitor) {
      if ( (connectionsCountPerHost.get(hostConfig) == manager.getParams().getMaxConnectionsPerHost(hostConfig)) ||
           (totalUsedConnection == manager.getParams().getMaxTotalConnections()) ) { // no available connection, we should wait...
        monitor.increase();
        try {
          if (LOG.beDebug()) {
            LOG.debugT("Sleeping on monitor : " + monitor + " for " + timeout + " ms");
          }
          if (timeout == 0) { // wait until there is a free connection
            do {
              monitor.wait();
            } while ( (connectionsCountPerHost.get(hostConfig) == manager.getParams().getMaxConnectionsPerHost(hostConfig)) ||
                        (totalUsedConnection == manager.getParams().getMaxTotalConnections()) );
          } else {
            long checkTime = System.currentTimeMillis() + timeout;
            do {
              monitor.wait(timeout);
              timeout = checkTime - System.currentTimeMillis();
            } while ( ((connectionsCountPerHost.get(hostConfig) == manager.getParams().getMaxConnectionsPerHost(hostConfig)) ||
                        (totalUsedConnection == manager.getParams().getMaxTotalConnections())) && timeout > 0);
          }

        } catch(InterruptedException ie) {
          throw new ConnectionPoolTimeoutException("Wait for connection from pool interrupted", ie);
        } finally {
          monitor.decrease();
//          if (manager.isShutDown()) { // if the manager is eventually shut down while waiting
//            throw new IllegalStateException("Connection manager is shut down.");
//          }
        }
      }

      if (connectionsCountPerHost.get(hostConfig) == manager.getParams().getMaxConnectionsPerHost(hostConfig)) {
        throw new ConnectionPoolTimeoutException("No available connections for this host: " + hostConfig);
      }
      if (totalUsedConnection == manager.getParams().getMaxTotalConnections()) {
        throw new ConnectionPoolTimeoutException("No available connections.");
      }

      HttpConnection connection = pooledConnections.remove(hostConfig);
      if (connection == null) { // will use connection with diferent host config
        Iterator<HttpConnection> iterator = pooledConnections.values().iterator();
        if (!iterator.hasNext()) { // no connections in pool, will try to create
          if (currentConnections >= manager.getParams().getMaxTotalConnections()) { // bad luck, all connections are used
            throw new ConnectionPoolTimeoutException("The pool connections are exhausted.");
          } else { // we can create a connections
            connection = new HttpConnection(hostConfig);
            currentConnections++;
          }
        } else { // found a pooled connection
          connection = iterator.next();
          iterator.remove();
          connection.reInitConnection(hostConfig);
        }
      }
      connectionsCountPerHost.put(hostConfig, (connectionsCountPerHost.get(hostConfig) + 1));
      totalUsedConnection++;
      return connection;
    }
  }

  // notifies the first of the waiting monitors
  private void notifyOneMonitor() {
    for(MonitorCounter monitor : threadMonitorsPerHost.values()) { // awaking one sleeping threads
			if (monitor.hasWaitingThreads()) {
				synchronized (monitor) {
	        if (LOG.beDebug()) {
	          LOG.debugT("Notifying monitor : " + monitor);
	        }
	        monitor.notify();
	        return;
	      }
			}
		}
  }

  // notifies all the monitors
  private void notifyAllMonitors() {
    synchronized(threadMonitorsPerHost) {
      for(MonitorCounter monitor : threadMonitorsPerHost.values()) { // awaking all sleeping threads
        if (monitor.hasWaitingThreads()) {
          synchronized (monitor) {
            monitor.notifyAll();
          }
        }
      }
    }
  }

  /**
   * Returns the connection in pool
   * @param conn the connection
   */
  public void returnConnection(HttpConnection conn) {
    HostConfiguration hostConfig;
    if (manager.isShutDown()) { // manager is stopped, just close the connection
      conn.close();
      notifyAllMonitors();
      return;
    }
    if (pooledConnections.containsValue(conn)) {
      return; // already released
    }
    hostConfig = conn.getHostConfiguration();
    synchronized (connectionsCountPerHost) {
      Integer bigInt = connectionsCountPerHost.get(hostConfig);
      int connectionsToHost;
      if (bigInt == null) {
        connectionsToHost = 0;
      } else {
        connectionsToHost = --bigInt;
      }
      connectionsCountPerHost.put(hostConfig, connectionsToHost);
      pooledConnections.put(hostConfig, conn);
      totalUsedConnection--;
    }
    synchronized(threadMonitorsPerHost) {
      MonitorCounter monitor = threadMonitorsPerHost.get(hostConfig);
      if ( (monitor == null) || (monitor.getWaitingThreads() == 0) ) {
        notifyOneMonitor();
      } else {
        synchronized (monitor) {
          if (LOG.beDebug()) {
            LOG.debugT("Notifying monitor : " + monitor);
          }
          monitor.notify();
        }
      }
    }
  }

  /**
   * Gets all pooled connections count
   * @return the count
   */
  public int getPooledConnectionsCount() {
    return pooledConnections.size();
  }

  /**
   * Gets the pooled connections for specified hostConfiguration
   * @param hostConfiguration the specified configuration
   * @return 1 if there is a pooled connection, 0 if not
   */
  public int getPooledConnectionsCount(HostConfiguration hostConfiguration) {
    if (pooledConnections.containsKey(hostConfiguration)) {
      return 1;
    } else {
      return 0;
    }
  }

  /**
   * Removes all closed pooled connections.
   */
  public void removeClosedConnections() {
    Iterator iter = pooledConnections.values().iterator();
    while (iter.hasNext()) {
      HttpConnection conn = (HttpConnection) iter.next();
      if (!conn.isOpen()) {
        iter.remove();
        currentConnections--;
      }
    }
  }

  /**
   * Shuts the pool down. Clears all info and awakes all the waiting threads
   */
  public void shutdown() {
    notifyAllMonitors();
    connectionsCountPerHost.clear();
    pooledConnections.clear();
  }

}