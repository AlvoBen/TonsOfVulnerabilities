/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ZoneManagementInterface extends Remote {
  public abstract Zone[] getAllZones() throws RemoteException;

  public abstract Zone getZone(String s) throws RemoteException;

  public abstract void registerZone(String s) throws IllegalArgumentException, ConfigurationException, RemoteException;

  public abstract void unregisterZone(String s) throws ConfigurationException, RemoteException;
}