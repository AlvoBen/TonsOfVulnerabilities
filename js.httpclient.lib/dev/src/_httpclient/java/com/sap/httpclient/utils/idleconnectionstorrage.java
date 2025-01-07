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

import com.sap.httpclient.net.connection.HttpConnection;
import com.sap.tc.logging.Location;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A helper class for connection managers to track idle connections.
 *
 * @author Nikolai Neichev
 */
public class IdleConnectionStorrage {

  private static final Location LOG = Location.getLocation(IdleConnectionStorrage.class);

  /**
   * Holds connections and their add time.
   */
  private Map<HttpConnection, Long> connectionToAdded = new HashMap<HttpConnection, Long>();

  /**
   * Default constructor
   */
  public IdleConnectionStorrage() {
    super();
  }

  /**
   * Add a connection.
   *
   * @param connection the connection to add
   */
  public void add(HttpConnection connection) {
    Long timeAdded = System.currentTimeMillis();
    if (LOG.beDebug()) {
      LOG.debugT("Adding connection at: " + timeAdded);
    }
    connectionToAdded.put(connection, timeAdded);
  }

  /**
   * Removes the specified connection.
   *
   * @param connection the connection to remove
   */
  public void remove(HttpConnection connection) {
    connectionToAdded.remove(connection);
  }

  /**
   * Removes all connections.
   */
  public void removeAll() {
    this.connectionToAdded.clear();
  }

  /**
   * Closes all connections that have been idle for more than the specified time.
   *
   * @param idleTime the idle time in milliseconds
   */
  public void closeIdleConnections(long idleTime) {
    // the latest time for which connections will be closed
    long connectionAge = System.currentTimeMillis() - idleTime;
    if (LOG.beDebug()) {
      LOG.debugT("Checking for connections older than : " + connectionAge + " ms");
    }
    Iterator<HttpConnection> connectionIter = connectionToAdded.keySet().iterator();
    while (connectionIter.hasNext()) {
      HttpConnection conn = connectionIter.next();
      Long connectionTime = connectionToAdded.get(conn);
      if (connectionTime <= connectionAge) {
        if (LOG.beDebug()) {
          LOG.debugT("Closing connection, connection time: " + connectionTime);
        }
        connectionIter.remove();
        conn.close();
      }
    }
  }
}