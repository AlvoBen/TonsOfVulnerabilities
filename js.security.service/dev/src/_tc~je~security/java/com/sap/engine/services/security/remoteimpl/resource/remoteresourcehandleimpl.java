/*
 * ...Copyright...
 */
package com.sap.engine.services.security.remoteimpl.resource;

import com.sap.engine.interfaces.security.resource.ResourceModificationListener;
import com.sap.engine.interfaces.security.resource.ResourceHandle;
import com.sap.engine.services.security.remote.resource.RemoteResourceHandle;

import javax.rmi.PortableRemoteObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *  A handle for a resource registered with the resource context.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 */
public class RemoteResourceHandleImpl extends PortableRemoteObject implements RemoteResourceHandle {

  private ResourceHandle handle = null;

  public RemoteResourceHandleImpl(ResourceHandle handle) throws RemoteException {
    this.handle = handle;
  }

  /**
   *  Creates an instance of the resource with the specified alias.
   *
   * @param  alias  the name of the instance ( this is optional and may be null ).
   *
   * @return  the unique ( within the resource ) identifier of the instance.
   *
   * @exception SecurityException  thrown if the caller is denied access to the instance.
   */
  public void createInstance(String instance) throws RemoteException, SecurityException {
    handle.createInstance(instance);
  }


  /**
   *  Creates an action to the resource with the specified alias.
   *
   * @param  action  the name of the action.
   *
   * @return  the unique ( within the resource ) identifier of the action.
   *
   * @exception SecurityException  thrown if the caller is denied access to the action.
   */
  public void createAction(String action) throws RemoteException, SecurityException {
    handle.createAction(action);
  }


  /**
   *  Returns the alias of the resource.
   *
   * @return  the name of the resource.
   */
  public String getAlias() throws RemoteException {
    return handle.getAlias();
  }


  /**
   *  Removes a registered instance of the resource.
   *
   * @param  instance the alias of the instance.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public void removeInstance(String instance) throws RemoteException, SecurityException {
    handle.removeInstance(instance);
  }


  /**
   *  Removes a registered action to the resource.
   *
   * @param  action  the alias of the action.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public void removeAction(String action) throws RemoteException, SecurityException {
    handle.removeAction(action);
  }


  /**
   *  Renames the resource.
   *
   * @param  newAlias  the new alias of the resource
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public void renameResource(String newAlias) throws RemoteException, SecurityException {
    handle.renameResource(newAlias);
  }


  /**
   * Groups instance with the specified parent instance.
   *
   * @param  instanceId       the alias of the instance as registered with the resource handle.
   * @param  parentInstanceId the alias of the instance as registered with the resource handle.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public void groupInstance(String instanceId, String parentInstanceId) throws RemoteException, SecurityException {
    handle.groupInstance(instanceId, parentInstanceId);
  }


  /**
   * Ungroups instance from the parent instance.
   *
   * @param  instanceId  the alias of the resource as registered with the resource context.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public void ungroupInstance(String instanceId) throws RemoteException, SecurityException {
    handle.ungroupInstance(instanceId);
  }


  /**
   *  Retrieves the name of the parent instance.
   *
   * @param   alias  id of the parent.
   *
   * @return int
   */
  public String getParent(String instanceId) throws RemoteException {
    return handle.getParent(instanceId);
  }


  /**
   *  Retrieves all children of the instance.
   *
   * @param   instanceId  id of the instance.
   * @return  array of identifiers of all instances.
   */
  public String[] getChildren(String instanceId) throws RemoteException {
    return handle.getChildren(instanceId);
  }


  /**
   *  Retrieves all actions.
   *
   * @return array of action names.
   */
  public String[] getActions() throws RemoteException {
    return handle.getActions();
  }

}

