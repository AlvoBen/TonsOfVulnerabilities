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

public interface Zone extends Remote {
  public abstract String getZoneName() throws RemoteException;

  /**
   *
   * @return
   * @throws RemoteException
   *
   * @deprecated  use int[] getInstances()
   */
  public abstract int[] getServers() throws RemoteException;
  
	public abstract int[] getInstances() throws RemoteException;

  public abstract String[] getAliases() throws RemoteException;

  public abstract String[] getExactAliases() throws RemoteException;

  /**
   *
   * @param i
   * @throws ConfigurationException
   * @throws RemoteException
   *
   * @deprecated  use addInstance(int i)
   */
  public abstract void addServer(int i) throws ConfigurationException, RemoteException;

  public abstract void addInstance(int i) throws ConfigurationException, RemoteException;

  /**
   *
   * @param i
   * @throws ConfigurationException
   * @throws RemoteException
   *
   * @deprecated  use removeInstance(int i)
   */
  public abstract void removeServer(int i) throws ConfigurationException, RemoteException;

  public abstract void removeInstance(int i) throws ConfigurationException, RemoteException;

  /**
   *
   * @param ai
   * @throws ConfigurationException
   * @throws RemoteException
   *
   * @deprecated  use addInstances(int ai[])
   */
  public abstract void addServers(int ai[]) throws ConfigurationException, RemoteException;

  public abstract void addInstances(int ai[]) throws ConfigurationException, RemoteException;

  /**
   *
   * @throws ConfigurationException
   * @throws RemoteException
   *
   * @deprecated  use clearAllInstances()
   */
  public abstract void clearAllServers() throws ConfigurationException, RemoteException;

	public abstract void clearAllInstances() throws ConfigurationException, RemoteException;
	
  public abstract void addAlias(String s) throws ConfigurationException, RemoteException;

  public abstract void removeAlias(String s) throws ConfigurationException, RemoteException;

  public abstract void addAliases(String as[]) throws ConfigurationException, RemoteException;

  public abstract void addExactAlias(String s) throws ConfigurationException, RemoteException;

  public abstract void removeExactAlias(String s) throws ConfigurationException, RemoteException;

  public abstract void addExactAliases(String as[]) throws ConfigurationException, RemoteException;

  public abstract void clearAllAliases() throws ConfigurationException, RemoteException;
}
