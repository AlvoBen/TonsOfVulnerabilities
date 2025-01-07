/*
 * ...Copyright...
 */
package com.sap.engine.services.security.remoteimpl.resource;

import com.sap.engine.interfaces.security.resource.ResourceHandle;
import com.sap.engine.interfaces.security.resource.ResourceAccessControlHandle;
import com.sap.engine.interfaces.security.ResourceContext;
import com.sap.engine.services.security.remote.resource.RemoteResourceHandle;
import com.sap.engine.services.security.remote.resource.RemoteResourceAccessControlHandle;
import com.sap.engine.services.security.remote.resource.RemoteSecurityResources;

import javax.rmi.PortableRemoteObject;
import java.rmi.RemoteException;
/**
 *  The context is targeted at managing resources defined in the server.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 */
public class RemoteSecurityResourcesImpl extends PortableRemoteObject implements RemoteSecurityResources {

  private ResourceContext resources = null;

  public RemoteSecurityResourcesImpl(ResourceContext resources) throws RemoteException {
    this.resources = resources;
  }

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
  public void checkPermission(String actionId, String resourceId, String instanceId) throws RemoteException, SecurityException {
    resources.checkPermission(actionId, resourceId, instanceId);
  }


  /**
   *  Creates a new resource with the given alias.
   *
   * @param  alias  the alias of the resource.
   *
   *
   * @exception SecurityException  thrown if the caller is not allowed to create resources.
   */
  public void createResource(String alias) throws RemoteException, SecurityException {
    resources.createResource(alias);
  }


  /**
   *  Destroys a resource registered with the given alias.
   *
   * @param  alias  the alias of the resource.
   *
   * @exception SecurityException  thrown if the caller is not allowed to destroy this resource.
   */
  public void destroyResource(String alias) throws RemoteException, SecurityException {
    resources.destroyResource(alias);
  }


  /**
   *  Returns a handle to the resource with the given name.
   *
   * @param  alias  the name of the resource as registered with the resource context.
   *
   * @return  a handle to the resource.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public RemoteResourceHandle getResourceHandle(String alias) throws RemoteException, SecurityException {
    ResourceHandle handle = resources.getResourceHandle(alias);
    if (handle == null) {
      throw new RemoteException("Cannot create a remote resource handle - the resource with a name [" + alias + "] not found.");
    }
    return new RemoteResourceHandleImpl(handle);
  }

  /**
   *  Returns an access control handle to the resource with the given name.
   *
   * @param  alias  the name of the resource as registered with the resource context.
   *
   * @return  a handle to the resource.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public RemoteResourceAccessControlHandle getResourceAccessControlHandle(String alias) throws RemoteException, SecurityException {
    ResourceAccessControlHandle handle = resources.getResourceAccessControlHandle(alias);
    if (handle == null) {
      throw new RemoteException("Cannot create a remote resource handle - the resource with a name [" + alias + "] not found.");
    }
    return new RemoteResourceAccessControlHandleImpl(handle);
  }

  /**
   *  Renames the resource.
   *
   * @param  alias     the name of the resource as registered with the resource context.
   * @param  newAlias  the new alias of the resource
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public void renameResource(String alias, String newAlias) throws RemoteException, SecurityException {
    resources.renameResource(alias, newAlias);
  }


  /**
   *  Enumerates the aliases of all resources.
   *
   * @return array of resource aliases.
   *
   * @exception   SecurityException thrown if the caller is denied.
   */
  public String[] getResourceAliases() throws RemoteException, SecurityException {
    return resources.getResourceAliases();
  }

}

