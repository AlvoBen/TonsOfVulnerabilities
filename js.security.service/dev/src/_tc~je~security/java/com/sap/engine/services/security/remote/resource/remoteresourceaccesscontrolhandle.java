/*
 * ...Copyright...
 */
package com.sap.engine.services.security.remote.resource;

import java.security.Permission;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *  Access control for a registered security sensitive resource.
 *
 * @see com.sap.engine.frame.container.security.ResourceContext
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 */
public interface RemoteResourceAccessControlHandle extends Remote {

  /**
   *  Tests if the user initiating the call is authorized to use the specified instance
   * of the resource through the given action.
   *  The call returns silently if the caller has permission to use the instance and throws
   * a security exception otherwise.
   *
   * @param  actionId   the name of the action.
   * @param  instanceId the name of the instance.
   *
   * @exception SecurityException  thrown if the caller is denied access to the instance.
   */
  public void checkPermission(String actionId, String instanceId) throws RemoteException, SecurityException;


  /**
   *  Denies access of the group to the specified instance of the resource
   * through the given action.
   *
   * @param  role        the name of the security role.
   * @param  actionId    the name of the action as registered with the resource context.
   * @param  instanceId  the name of the instance of the resource as registered with the resource context.
   *
   * @exception  SecurityException  thrown if the caller does not have permissions to change restrictions.
   */
  public void denySecurityRole(String role, String actionId, String instanceId) throws RemoteException, SecurityException;


  /**
   *  Returns the groups directly given access to the specified instance of the resource
   * through the given action.
   *  No inheritance is taken in consideration.
   *
   * @param  actionId    the name of the action as registered with the resource context.
   * @param  instanceId  the name of the instance of the resource as registered with the resource context.
   *
   * @return  an array of security roles' names.
   *
   * @exception  SecurityException  thrown if the caller does not have permissions to view restrictions.
   */
  public String[] listGrantedSecurityRoles(String actionId, String instanceId) throws RemoteException, SecurityException;


  /**
   *  Returns the groups directly denied of access to the specified instance of the resource
   * through the given action.
   *  No inheritance is taken in consideration.
   *
   * @param  actionId    the name of the action as registered with the resource context.
   * @param  instanceId  the name of the instance of the resource as registered with the resource context.
   *
   * @return  an array of group names.
   *
   * @exception  SecurityException  thrown if the caller does not have permissions to view restrictions.
   */
  public String[] listDeniedSecurityRoles(String actionId, String instanceId) throws RemoteException, SecurityException;


  /**
   *  Grants the group with access to the specified instance of the resource
   * through the given action.
   *
   * @param  role        the name of the group.
   * @param  actionId    the name of the action as registered with the resource context.
   * @param  instanceId  the name of the instance of the resource as registered with the resource context.
   *
   * @exception  SecurityException  thrown if the caller does not have permissions to change restrictions.
   */
  public void grantSecurityRole(String role, String actionId, String instanceId) throws RemoteException, SecurityException;


  /**
   *  Clears any access settings for the group to the specified instance of the resource
   * through the given action.
   *
   * @param  groupName   the name of the group.
   * @param  actionId    the name of the action as registered with the resource context.
   * @param  instanceId  the name of the instance of the resource as registered with the resource context.
   *
   * @exception  SecurityException  thrown if the caller does not have permissions to change restrictions.
   */
  public void clearSecurityRole(String role, String actionId, String instanceId) throws RemoteException, SecurityException;

}

