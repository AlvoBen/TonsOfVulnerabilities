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

import com.sap.bc.proj.jstartup.fca.FCAConnection;
import com.sap.bc.proj.jstartup.fca.FCAException;
import com.sap.bc.proj.jstartup.fca.FCAServer;
import com.sap.engine.boot.SystemProperties;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.ServiceContext;

public class ICMFCAQueue implements FCAQueue {

  private FCAServer postponedFCAServer = null;
  private static final int keySize = 32;
  
  public ICMFCAQueue() {
    try {
      String queueName = SystemProperties.getProperty("SAPSYSTEMNAME") +
        "_" + SystemProperties.getProperty("SAPSYSTEM") + "_" + 
        ServiceContext.getServiceContext().getApplicationServiceContext()
        .getClusterContext().getClusterMonitor().getCurrentParticipant()
        .getClusterId() + "_HTTP_WAIT";
      postponedFCAServer = new FCAServer(queueName, keySize);
    } catch (Exception e) {
      Log.logFatal("ASJ.http.000391", "Could not initialize FCA server for postponed requests.", e, null, null, null);
      return;
    }
  }
  
  public void add(FCAConnection conn, String key) throws FCAException {
    conn.postpone(postponedFCAServer, getKey(key));
  }

  public FCAConnection poll(String key) throws FCAException {
    return postponedFCAServer.search(getKey(key));
  }
  
  private byte[] getKey(String key) {
    byte [] keyArr = new byte[keySize];
    byte [] urlKey = key.getBytes();
    if (urlKey.length > keySize) {
      System.arraycopy(urlKey, 0, keyArr, 0, keySize);
    } else {
      System.arraycopy(urlKey, 0, keyArr, 0, urlKey.length);
    }
    return keyArr;
  }

}
