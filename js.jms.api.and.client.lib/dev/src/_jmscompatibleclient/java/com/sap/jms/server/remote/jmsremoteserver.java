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

import java.rmi.Remote;

/**  
 * @author Desislav Bantchovski
 * @version 7.10 
 */

import java.rmi.RemoteException;
import com.sap.jms.util.compat.rmi_p4.interfaces.P4Notification;

public interface JMSRemoteServer extends Remote, P4Notification { 

  /**
   * Receives the client request and returns the provider reposnse.
   * @param connectionId connectionId of the Connection
   * @param request packet from the client
   * @param offset packet offset - used to create a Packet.
   * @param length packet length - used to create a Packet.
   * @return the provider response of the incomming packet. 
   * @throws RemoteException
   */	
	public byte[] dispatchRequest(long connectionId, byte[] request, int offset, int length) throws RemoteException;
  
  /**
   * Adds a callback for some particular connectionId.
   * Asociates some connectionId with a client callback.
   * This callback will be used to send a packets to this connection.
   * @param connectionId connectionId of the connection.
   * @param client callback interface to this connection.
   */
 
	public JMSRemoteServer handshake(long connectionId, JMSRemoteClient client) throws RemoteException;

}
