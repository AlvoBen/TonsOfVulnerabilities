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
import java.rmi.RemoteException;
import com.sap.jms.util.compat.rmi_p4.interfaces.P4Notification;

/**
 * @author Desislav Bantchovski
 * @version 7.10 
 */

public interface JMSRemoteClient extends Remote , P4Notification { 
  public void receive(byte[] request, int offset, int length) throws RemoteException;
}
