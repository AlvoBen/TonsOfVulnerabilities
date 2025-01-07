/*
 * Copyright (c) 2000-2009 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server.rcm;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.sap.bc.proj.jstartup.fca.FCAConnection;
import com.sap.bc.proj.jstartup.fca.FCAException;

public class LocalFCAQueue implements FCAQueue {

  private HashMap<String, Vector<FCAConnection>> postponedFCAconnections = new HashMap<String, Vector<FCAConnection>>();
  private ReadWriteLock queueLock = new ReentrantReadWriteLock();
  private int currentQueueSize;
  
  private static final int MAX_QUEUE_SIZE = 20;
  
  public LocalFCAQueue() {
    currentQueueSize = 0;
  }
  
  public void add(FCAConnection conn, String key) throws FCAException {
    try{
      queueLock.writeLock().lock();
      if (currentQueueSize < MAX_QUEUE_SIZE) {
        currentQueueSize++;
      } else {
        throw new FCAException("FCAConnection queue overflow.");
      }
      Vector<FCAConnection> connections = postponedFCAconnections.get(key);
      if (connections != null) {
        connections.add(conn);
      } else {
        connections = new Vector<FCAConnection>();
        connections.add(conn);
        postponedFCAconnections.put(key, connections);
      }
    } finally {
      queueLock.writeLock().unlock();
    }
  }

  public FCAConnection poll(String key) {
    FCAConnection connection = null;
    try {
      queueLock.writeLock().lock();
      Vector<FCAConnection> requestQueue = postponedFCAconnections.get(key);
      if (requestQueue != null && !requestQueue.isEmpty()) {
        connection = requestQueue.firstElement();
        requestQueue.remove(connection);
        currentQueueSize--;
      }
      return connection;
    } finally {
      queueLock.writeLock().unlock();
    }
  }
}
