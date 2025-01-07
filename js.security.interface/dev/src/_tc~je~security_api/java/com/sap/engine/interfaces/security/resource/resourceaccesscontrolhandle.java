/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security.resource;

import java.security.Permission;

/**
 *  Access control for a registered security sensitive resource.
 *
 * @author  Jako Blagoev
 * @author  Stephan Zlatarev
 * @version 6.30
 *
 * @deprecated Resource context is deprecated since NW04 AS Java. Use UME API instead.
 * @see com.sap.engine.interfaces.security.ResourceContext
 */
public interface ResourceAccessControlHandle {

  /**
   *  Tests if the user initiating the call is authorized to use the specified instance
   * of the resource through the given action.
   *  The call returns silently if the caller has permission to use the instance and throws
   * a security exception otherwise.
   *
   * @param  actionAlias   the name of the action.
   * @param  instanceAlias the name of the instance.
   *
   * @exception SecurityException  thrown if the caller is denied access to the instance.
   */
  public void checkPermission(String actionAlias, String instanceAlias) throws SecurityException;


  /**
   *  Denies access of the security role to the specified instance of the resource
   * through the given action.
   *
   * @param  roleAlias      the name of the security role.
   * @param  actionAlias    the name of the action as registered with the resource context.
   * @param  instanceAlias  the name of the instance of the resource as registered with the resource context.
   *
   * @exception  SecurityException  thrown if the caller does not have permissions to change restrictions.
   */
  public void denySecurityRole(String roleAlias, String actionAlias, String instanceAlias) throws SecurityException;


  /**
   *  Returns the groups directly given access to the specified instance of the resource
   * through the given action.
   *  No inheritance is taken in consideration.
   *
   * @param  actionAlias    the name of the action as registered with the resource context.
   * @param  instanceAlias  the name of the instance of the resource as registered with the resource context.
   *
   * @return  an array of security roles' names.
   *
   * @exception  SecurityException  thrown if the caller does not have permissions to view restrictions.
   */
  public String[] listGrantedSecurityRoles(String actionAlias, String instanceAlias) throws SecurityException;


  /**
   *  Returns the groups directly denied of access to the specified instance of the resource
   * through the given action.
   *  No inheritance is taken in consideration.
   *
   * @param  actionAlias    the name of the action as registered with the resource context.
   * @param  instanceAlias  the name of the instance of the resource as registered with the resource context.
   *
   * @return  an array of group names.
   *
   * @exception  SecurityException  thrown if the caller does not have permissions to view restrictions.
   */
  public String[] listDeniedSecurityRoles(String actionAlias, String instanceAlias) throws SecurityException;


  /**
   *  Returns an instance of <code>java.security.Permission</code> that can be used to
   * test for authorization to access the specified instance through the specified action.
   *
   * @param  actionAlias   the name of the action as registered with the resource context.
   * @param  instanceAlias the name of the instance as registered with the resource context.
   *
   * @return  permission instance.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public Permission getPermission(String actionAlias, String instanceAlias) throws SecurityException;


  /**
   *  Grants the security role with access to the specified instance of the resource
   * through the given action.
   *
   * @param  roleAlias      the name of the security role.
   * @param  actionAlias    the name of the action as registered with the resource context.
   * @param  instanceAlias  the name of the instance of the resource as registered with the resource context.
   *
   * @exception  SecurityException  thrown if the caller does not have permissions to change restrictions.
   */
  public void grantSecurityRole(String roleAlias, String actionAlias, String instanceAlias) throws SecurityException;


  /**
   *  Clears any access settings for the security role to the specified instance of the resource
   * through the given action.
   *
   * @param  roleAlias      the name of the security role.
   * @param  actionAlias    the name of the action as registered with the resource context.
   * @param  instanceAlias  the name of the instance of the resource as registered with the resource context.
   *
   * @exception  SecurityException  thrown if the caller does not have permissions to change restrictions.
   */
  public void clearSecurityRole(String roleAlias, String actionAlias, String instanceAlias) throws SecurityException;

}

