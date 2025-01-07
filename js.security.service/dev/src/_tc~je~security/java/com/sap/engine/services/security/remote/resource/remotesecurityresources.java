/*
 * ...Copyright...
 */
package com.sap.engine.services.security.remote.resource;

import com.sap.engine.interfaces.security.resource.ResourceHandle;
import com.sap.engine.interfaces.security.resource.ResourceAccessControlHandle;
import com.sap.engine.interfaces.security.resource.ResourceListener;

import java.rmi.RemoteException;
import java.rmi.Remote;

/**
 *  The context is targeted at managing resources defined in the server.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 */
public interface RemoteSecurityResources extends Remote {

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
  public void checkPermission(String actionId, String resourceId, String instanceId) throws RemoteException, SecurityException;


  /**
   *  Creates a new resource with the given alias.
   *
   * @param  alias  the alias of the resource.
   *
   * @return  the unique identifier of the newly-created resource.
   *
   * @exception SecurityException  thrown if the caller is not allowed to create resources.
   */
  public void createResource(String alias) throws RemoteException, SecurityException;


  /**
   *  Destroys a resource registered with the given alias.
   *
   * @param  alias  the alias of the resource.
   *
   * @exception SecurityException  thrown if the caller is not allowed to destroy this resource.
   */
  public void destroyResource(String alias) throws RemoteException, SecurityException;


  /**
   *  Returns a handle to the resource with the given name.
   *
   * @param  alias  the name of the resource as registered with the resource context.
   *
   * @return  a handle to the resource.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public RemoteResourceHandle getResourceHandle(String alias) throws RemoteException, SecurityException;

  /**
   *  Returns an access control handle to the resource with the given name.
   *
   * @param  alias  the name of the resource as registered with the resource context.
   *
   * @return  a handle to the resource.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public RemoteResourceAccessControlHandle getResourceAccessControlHandle(String alias) throws RemoteException, SecurityException;

  /**
   *  Renames the resource.
   *
   * @param  alias     the name of the resource as registered with the resource context.
   * @param  newAlias  the new alias of the resource
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public void renameResource(String alias, String newAlias) throws RemoteException, SecurityException;


  /**
   *  Enumerates the aliases of all resources.
   *
   * @return array of resource aliases.
   *
   * @exception   SecurityException thrown if the caller is denied.
   */
  public String[] getResourceAliases() throws RemoteException, SecurityException;

}

