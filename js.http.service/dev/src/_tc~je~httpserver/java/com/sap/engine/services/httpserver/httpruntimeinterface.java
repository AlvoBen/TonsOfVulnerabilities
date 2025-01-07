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

/*
 *
 * @author Maria Jurova
 * @version 6.30
 */
import com.sap.engine.frame.state.ManagementInterface;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.services.httpserver.exceptions.IllegalHostArgumentsException;

import java.rmi.*;

/**
 * This class is used for runtime administration of the Http service.
 *
 */
public interface HttpRuntimeInterface extends ServerMonitoring, ManagementInterface, Remote {

  /**
   * Returns description of the all virtual hosts on the server.
   *
   * @return     description of the all virtual hosts on the server
   * @deprecated
   */
  public HostPropertiesRuntimeInterface[] getAllHostsTemp() throws java.rmi.RemoteException;

  /**
   * Returns description of the all virtual hosts on the server.
   *
   * @return     description of the all virtual hosts on the server
   */
  public HostPropertiesRuntimeInterface[] getAllHosts() throws java.rmi.RemoteException;

  /**
   * Creates new virtual host with name <code>hostName</code>
   *
   * @param      hostName   the name of the host that will be crated
   * @return     description of the new virtual host if is successfully created
   * @throws     IllegalHostArgumentsException if a host with such name already exists or some error occures
   */
  public HostPropertiesRuntimeInterface createHost(String hostName) throws IllegalHostArgumentsException, ConfigurationException, java.rmi.RemoteException;

  /**
   * Removes virtual host with name <code>hostName</code>.
   * If host with such name does not exists returns with no exception.
   *
   * @param      hostName   the name of the host that will be removed
   * @throws     IllegalHostArgumentsException if trying to remove the default host or some error occures
   */
  public void removeHost(String hostName) throws IllegalHostArgumentsException, java.rmi.RemoteException;

  /**
   * Clears the cache of the http service.
   * The local cache of the http service running on this node is cleared as well as the cache of the ICM of the current box.
   */
  public void clearCache() throws java.rmi.RemoteException;

  /**
   * Clears the cache of the http service of a specified virtual host only.
   * The local cache of the http service running on this node is cleared as well as the cache of the ICM of the current box.
   *
   * @param   hostName  name of the host which cache will be cleared
   */
  public void clearCache(String hostName) throws java.rmi.RemoteException;

  /**
   * Clears the cache of the http service .
   * The local cache of the http service running on this node is cleared as well as
   * all entries from the cache of the ICM on the current box that belong to a specified http alias or web application.
   *
   * @param alias   Http alias or web applications which cached entries will be cleared
   */
  public void clearCacheByAlias(String alias) throws java.rmi.RemoteException;

  public ZoneManagementInterface getZoneManagementInterface() throws RemoteException;
}

