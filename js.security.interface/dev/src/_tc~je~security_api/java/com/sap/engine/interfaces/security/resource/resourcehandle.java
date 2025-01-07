/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security.resource;

/**
 *  A handle for a resource registered with the resource context.
 *
 * @author  Jako Blagoev
 * @author  Stephan Zlatarev
 * @version 6.30
 *
 * @deprecated Resource context is deprecated since NW04 AS Java. Use UME API instead.
 * @see com.sap.engine.interfaces.security.ResourceContext
 */
public interface ResourceHandle {


  /**
   *  Use this constant when no specific instance or action is targeted.
   *
   *  Value is "ALL".
   */
  public final static String ALL = "ALL";


  /**
   *  Creates an action to the resource with the specified alias.
   *
   * @param  action  the name of the action.
   *
   * @exception SecurityException  thrown if the caller is denied access to the action.
   */
  public void createAction(String action) throws SecurityException;


  /**
   *  Creates an instance of the resource with the specified alias.
   *
   * @param  instanceAlias  the name of the instance ( this is optional and may be null ).
   *
   * @exception SecurityException  thrown if the caller is denied access to the instance.
   */
  public void createInstance(String instanceAlias) throws SecurityException;


  /**
   *  Closes the handle.
   */
  public void free();


  /**
   *  Retrieves all actions.
   *
   * @return array of action names.
   */
  public String[] getActions();


  /**
   *  Returns the alias of the resource.
   *
   * @return  the name of the resource.
   */
  public String getAlias();


  /**
   *  Retrieves all children of the instance.
   *
   * @param   instanceAlias  alias of the instance.
   *
   * @return  array of identifiers of all instances.
   */
  public String[] getChildren(String instanceAlias);

  /**
   *  Retrieves all instances.
   *
   * @return array of instance names.
   */
  public String[] getInstances();


  /**
   *  Retrieves the name of the parent instance.
   *
   * @param   instanceAlias  alias of the instance.
   *
   * @return int
   */
  public String getParent(String instanceAlias);


  /**
   * Groups instance with the specified parent instance.
   *
   * @param  instanceAlias    the alias of the instance as registered with the resource handle.
   * @param  parentInstanceId the alias of the instance as registered with the resource handle.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public void groupInstance(String instanceAlias, String parentInstanceId) throws SecurityException;


  /**
   *  Registers a listener for operations concerning this resource.
   * See <code>ResourceModificationListener</code> for more information on modifiers.
   *
   * @param  listener  the listener instance.
   * @param  modifiers specifying accepted notifications.
   */
  public void registerListener(ResourceModificationListener listener, int modifiers) throws SecurityException;


  /**
   *  Removes a registered action to the resource.
   *
   * @param  action  the alias of the action.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public void removeAction(String action) throws SecurityException;


  /**
   *  Removes a registered instance of the resource.
   *
   * @param  instance the alias of the instance.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public void removeInstance(String instance) throws SecurityException;


  /**
   *  Renames the resource.
   *
   * @param  newAlias  the new alias of the resource
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public void renameResource(String newAlias) throws SecurityException;


  /**
   *  Returns a printable string representation of the resource.
   *
   * @return  a printable string.
   */
  public String toString();


  /**
   * Ungroups instance from the parent instance.
   *
   * @param  instanceAlias  the alias of the resource as registered with the resource context.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public void ungroupInstance(String instanceAlias) throws SecurityException;


  /**
   *  Unregisters the listener. It will be no longer notified of events.
   *
   * @param  listener  the listener to unregister.
   */
  public void unregisterListener(ResourceModificationListener listener) throws SecurityException;

}