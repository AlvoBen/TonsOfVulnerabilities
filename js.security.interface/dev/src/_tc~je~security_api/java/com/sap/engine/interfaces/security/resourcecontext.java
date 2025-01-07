/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security;

import java.security.Permission;

import com.sap.engine.interfaces.security.resource.ResourceHandle;
import com.sap.engine.interfaces.security.resource.ResourceAccessControlHandle;
import com.sap.engine.interfaces.security.resource.ResourceListener;

/**
 *  The context is targeted at managing resources defined in the server.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 *
 * @deprecated Resource context is deprecated since NW04 AS Java. Use UME API instead.
 * @see com.sap.engine.interfaces.security.AuthorizationContext
 * @see com.sap.engine.interfaces.security.resource.ResourceAccessControlHandle
 * @see com.sap.engine.interfaces.security.resource.ResourceHandle
 * @see com.sap.engine.interfaces.security.resource.ResourceListener
 */
public interface ResourceContext {

  /**
   *  Tests if the user initiating the call is authorized to use the specified instance
   * of the resource through the given action.
   *  The call returns silently if the caller has permission to use the instance and throws
   * a security exception otherwise.
   *
   * @param  actionId   the alias of the action.
   * @param  resourceId the alias of the resource.
   * @param  instanceId the alias of the instance.
   *
   * @exception SecurityException  thrown if the caller is denied access to the instance.
   */
  public void checkPermission(String actionId, String resourceId, String instanceId) throws SecurityException;


  /**
   *  Creates a new resource with the given alias.
   *
   * @param  alias  the alias of the resource.
   *
   * @exception SecurityException  thrown if the caller is not allowed to create resources.
   */
  public void createResource(String alias) throws SecurityException;


  /**
   *  Destroys a resource registered with the given alias.
   *
   * @param  alias  the alias of the resource.
   *
   * @exception SecurityException  thrown if the caller is not allowed to destroy this resource.
   */
  public void destroyResource(String alias) throws SecurityException;


  /**
   *  Returns an instance of <code>java.security.Permission</code> that can be used to
   * test for authorization to access the specified instance through the specified action.
   *
   * @param  actionId   the alias of the action.
   * @param  resourceId the alias of the resource.
   * @param  instanceId the alias of the instance.
   *
   * @return  permission instance.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public Permission getPermission(String actionId, String resourceId, String instanceId) throws SecurityException;


  /**
   *  Returns a handle to the resource with the given name.
   *
   * @param  alias  the name of the resource as registered with the resource context.
   *
   * @return  a handle to the resource.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public ResourceHandle getResourceHandle(String alias) throws SecurityException;

  /**
   *  Returns an access control handle to the resource with the given name.
   *
   * @param  alias  the name of the resource as registered with the resource context.
   *
   * @return  a handle to the resource.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public ResourceAccessControlHandle getResourceAccessControlHandle(String alias) throws SecurityException;

  /**
   *  Renames the resource.
   *
   * @param  alias     the name of the resource as registered with the resource context.
   * @param  newAlias  the new alias of the resource
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public void renameResource(String alias, String newAlias) throws SecurityException;


  /**
   *  Enumerates the aliases of all resources.
   *
   * @return array of resource aliases.
   *
   * @exception   SecurityException thrown if the caller is denied.
   */
  public String[] getResourceAliases() throws SecurityException;


  /**
   *  Registers a ResourceListener instance.
   *
   * @param   listener  listener of event of this resource.
   * @param   modifiers  modifier for the desired events
   *
   * @see com.sap.engine.interfaces.security.resource.ResourceListener
   */
  public void registerListener(ResourceListener listener, int modifiers);


  /**
   *  Unregisters a ResourceListener instance.
   *
   * @param   listener  listener of event of this resource.
   */
  public void unregisterListener(ResourceListener listener);

}

