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

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP_REQUEST;

import com.sap.bc.proj.jstartup.fca.FCAConnection;
import com.sap.bc.proj.jstartup.fca.FCAException;
import com.sap.engine.services.httpserver.server.rcm.ThrResourceManager;
import com.sap.engine.services.httpserver.server.rcm.WebConsumer;

public class PostponedQueue {
	
	private ThrResourceManager manager = null;
	//Queue queue;
  //private FCAQueue queue = new LocalFCAQueue(); 
  private FCAQueue queue = new ICMFCAQueue(); 
	
  public PostponedQueue(ThrResourceManager manager) {
    this.manager = manager;
  }
  
	public synchronized FCAConnection poll(String key) throws FCAException {
    FCAConnection conn = queue.poll(key);
    if (conn != null) {
      if (LOCATION_HTTP_REQUEST.beDebug()) {
        String msg = "[PostponedQueue.poll(" + key + ")] returned conn[" + conn.getRequestPath() + "]";
        LOCATION_HTTP_REQUEST.debugT(msg);
      }
    } else {
      if (LOCATION_HTTP_REQUEST.beDebug()) {
        String msg = "[PostponedQueue.poll(" + key + ")] returned conn[null]";
        LOCATION_HTTP_REQUEST.debugT(msg);
      }      
    }
    return conn;
	}
	
	public synchronized void add(FCAConnection conn, String key) throws FCAException {
    if (LOCATION_HTTP_REQUEST.beDebug()) {
      String msg = "[PostponedQueue.add(" + conn.getRequestPath() + ", " + key + ")]";
      LOCATION_HTTP_REQUEST.debugT(msg);
    }    
    queue.add(conn, key);
	}
	
	public synchronized boolean postpone(FCAConnection conn, WebConsumer consumer) throws FCAException {
		if (manager.consume(consumer)) {
      if (LOCATION_HTTP_REQUEST.beDebug()) {
        String msg = "[PostponedQueue.postpone(" + conn.getRequestPath() + ", " + consumer.getId() + ")]: false";
        LOCATION_HTTP_REQUEST.debugT(msg);
      } 
			return false;
		} else {
      if (LOCATION_HTTP_REQUEST.beDebug()) {
        String msg = "[PostponedQueue.postpone(" + conn.getRequestPath() + ", " + consumer.getId() + ")]: true";
        LOCATION_HTTP_REQUEST.debugT(msg);
      } 
			queue.add(conn, consumer.getId());
			return true;
		}
	}

	
	public synchronized FCAConnection get(WebConsumer consumer) throws FCAException {
		FCAConnection conn = null;
		if (manager.consume(consumer)) {
			conn = poll(consumer.getId());
			if (conn == null) {
				manager.release(consumer);
			}
		}
    if (conn != null) {
      if (LOCATION_HTTP_REQUEST.beDebug()) {
        String msg = "[PostponedQueue.get(" + consumer.getId() + ")] returned conn[" + conn.getRequestPath() + "]";
        LOCATION_HTTP_REQUEST.debugT(msg);
      } 
    } else {
      if (LOCATION_HTTP_REQUEST.beDebug()) {
        String msg = "[PostponedQueue.get(" + consumer.getId() + ")] returned conn[null]";
        LOCATION_HTTP_REQUEST.debugT(msg);
      } 
    }
		return conn;		
	}
	
}
