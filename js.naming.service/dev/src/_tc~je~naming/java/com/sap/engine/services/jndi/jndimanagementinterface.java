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
package com.sap.engine.services.jndi;

import com.sap.engine.frame.state.ManagementInterface;

import java.rmi.*;

/**
 * Runtime interface of the JNDI
 *
 * @author Petio Petev
 * @version 4.00
 */
public interface JNDIManagementInterface extends ManagementInterface, Remote {

  /**
   * This method has obsolete functionality. Do not use it.
   * Returns all the permissions defined for jndi operations.
   *
   * @return Object representing all the permissions defined for jndi operations.
   * @throws RemoteException
   * @deprecated For proprietary use only. Will be removed after removing the Visual Admin tool
   */
  public Object getPermissions() throws RemoteException;

  /**
   * This method has obsolete functionality. Do not use it.
   * Returns all the permissions defined for a certain permission name.
   *
   * @param permissionName the name of the permission
   * @return Object representing the permissions defined for a specified name
   * @throws RemoteException
   * @deprecated For proprietary use only. Will be removed after removing the Visual Admin tool
   */
  public Object getPermissions(String permissionName) throws RemoteException;

  /**
   * This method has obsolete functionality. Do not use it.
   * Creates permission with specified name for a specified user or group.
   *
   * @param userName the name of the user/group
   * @param permissionName the name of the permission
   * @param isGroup true if the name corresponds to a group, false if case of user
   * @throws RemoteException
   * @deprecated For proprietary use only. Will be removed after removing the Visual Admin tool
   */
  public void addPermission(String userName, String permissionName, boolean isGroup) throws RemoteException;

  /**
   * This method has obsolete functionality. Do not use it.
   * Deletes permission with specified name for a specified user or group.
   *
   * @param userName the name of the user/group
   * @param permissionName the name of the permission
   * @param isGroup true if the name corresponds to a group, false if case of user
   * @throws RemoteException
   * @deprecated For proprietary use only. Will be removed after removing the Visual Admin tool
   */
  public void removePermission(String userName, String permissionName, boolean isGroup) throws RemoteException;

  /**
   * This method has obsolete functionality. Do not use it.
   * Returns a tree object representing the naming tree structure. Used by Visual Admin Tool for jndi tree
   * visualization.
   *
   * @return DefaultMutableTreeNode representing the jndi bindings
   * @throws RemoteException
   * @deprecated For proprietary use only. Will be removed after removing the Visual Admin tool
   */
  public Object listAllBindings() throws java.rmi.RemoteException;

  /**
   * This method has obsolete functionality. Do not use it.
   * Currently returns null.
   *
   * @return null
   * @throws RemoteException
   * @deprecated For proprietary use only. Will be removed after removing the Visual Admin tool
   */
  public Object getUsersTree() throws RemoteException;

//Monitoring methods ===========================================================================================//

  /**
   * Shows the size of the naming system byte array cache in KB.
   * All the serializable object bindings in the naming system are kept in a repository as byte arrays.
   * When a lookup of a serializable object is performed the byte array representing the object is put in
   * the naming cache so that the next lookup with this name will take the object directly from the cache.
   * The value is actualized every time the cache is affected by a naming operation.
   *
   * @return integer value representing the size of the naming system byte array cache in KB.
   */
  public int getByteArrayCacheSize();

  /**
   * Shows the number of objects bound in the naming system at the present time.
   * The number of objects is formed from the number of the locally bound objects on a certain server process
   * and the number of all the global object bindings in the cluster.
   * The value is actualized on every bind/rebind/unbind operation in the naming system.
   *
   * @return integer value representing the number of objects bound in the naming system at the present time.
   */
  public int getBoundObjectsCount();
}

