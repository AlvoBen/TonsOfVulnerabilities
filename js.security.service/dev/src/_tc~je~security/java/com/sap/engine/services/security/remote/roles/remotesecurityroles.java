/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.remote.roles;

import com.sap.engine.interfaces.security.SecurityRole;

import java.rmi.RemoteException;
import java.rmi.Remote;

/**
 * Remote context for managing with security roles.
 *
 * @author  Svetlana Stancheva
 * @version 6.30
 */
public interface RemoteSecurityRoles extends Remote {

  /**
   *  Adds a security role for the application.
   *
   * @param  securityRole  the name of the security role.
   *
   * @return  the new security role
   */
  public SecurityRole addSecurityRole(String securityRole) throws RemoteException, SecurityException;
  
  /**
   *  Adds a security role for the application.
   *
   * @param  roleName             the name of the new security role.
   * @param  policyConfiguration  the policy configuration that contain the security role.
   * @param  securityRole         the name of the security role in the given policy configuration.
   *
   * @return  the new security role
   */
  public SecurityRole addSecurityRoleReference(String roleName, String policyConfiguration, String securityRole) throws RemoteException, SecurityException;

  /**
   *  Returns the security role with the given name
   *
   * @return  a security role instance.
   */
  public SecurityRole getSecurityRole(String securityRole) throws RemoteException, SecurityException;


  /**
   *  Lists all registered security roles of this component.
   *
   * @return  list of the registered security roles.
   */
  public SecurityRole[] listSecurityRoles() throws RemoteException, SecurityException;


  /**
   *  Lists all registered security roles for the specified group.
   *
   * @param  groupName  group name
   *
   * @return  list of the registered security roles.
   */
  public SecurityRole[] listSecurityRolesOfGroup(String groupName) throws RemoteException, SecurityException;


  /**
   *  Lists all registered security roles for the specified user.
   *
   * @param  userName  user name
   *
   * @return  list of the registered security roles.
   */
  public SecurityRole[] listSecurityRolesOfUser(String userName) throws RemoteException, SecurityException;


  /**
   *  Modifies a security role.
   *
   * @param  securityRole  the modified security role.
   */
  public void modifySecurityRole(SecurityRole securityRole) throws RemoteException, SecurityException;

  /**
   *  Removes a security role for the component.
   *
   * @param  securityRole  the security role.
   */
  public void removeSecurityRole(String securityRole) throws RemoteException, SecurityException;
}

