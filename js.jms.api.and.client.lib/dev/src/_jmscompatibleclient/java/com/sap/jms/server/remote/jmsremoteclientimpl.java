/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.jms.server.remote;

import java.rmi.RemoteException;
import com.sap.jms.client.connection.*;

/**
 * @author Desislav Bantchovski
 * @version 7.10 
 */

public class JMSRemoteClientImpl implements JMSRemoteClient { 

  RemoteAdapter adapter = null;

  public JMSRemoteClientImpl(RemoteAdapter adapter) throws RemoteException {
  	this.adapter = adapter;
  }

  public void receive(byte[] request, int offset, int length) throws RemoteException {
  	adapter.receive(request, offset, length);
  }
  
  public void closedConnection() {
  	adapter.unreferenced();
  }
}
