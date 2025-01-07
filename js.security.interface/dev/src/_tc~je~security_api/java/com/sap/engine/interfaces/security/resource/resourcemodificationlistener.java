/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security.resource;

/**
 *  Resource listeners are informed of changes concerning the resource they
 * listen on.
 *  The modifiers are used when a listener is registered to specify that
 * it wishes to be notified when certain events occur. Typical use is for an
 * application that has registered a resource and needs to do additional
 * configuration of externally added or modified instances.
 *  To use more than one modifier use bit-wise OR. For example:
 *  <code>INFORM_ON_ADD | INFORM_ON_MODIFY | INFORM_ON_REMOVE<code>
 *
 * @author  Jako Blagoev
 * @author  Stephan Zlatarev
 * @version 6.30
 *
 * @deprecated Resource context is deprecated since NW04 AS Java. Use UME API instead.
 * @see com.sap.engine.interfaces.security.ResourceContext
 * @see com.sap.engine.interfaces.security.resource.ResourceHandle
 */
public interface ResourceModificationListener {


  /**
   *  The modifier is used when a listener is registered to specify that
   * it wishes to be notified when an action is added.
   */
  public final static int INFORM_ON_ACTION_ADD = 0x01;


  /**
   *  The modifier is used when a listener is registered to specify that
   * it wishes to be notified when an action is removed.
   *  To use more than one modifier use bit-wise OR.
   */
  public final static int INFORM_ON_ACTION_REMOVE = 0x02;


  /**
   *  The modifier is used when a listener is registered to specify that
   * it wishes to be notified when an instance is added.
   */
  public final static int INFORM_ON_INSTANCE_ADD = 0x04;


  /**
   *  The modifier is used when a listener is registered to specify that
   * it wishes to be notified when an instance is modified.
   *  To use more than one modifier use bit-wise OR.
   */
  public final static int INFORM_ON_INSTANCE_MODIFY = 0x08;


  /**
   *  The modifier is used when a listener is registered to specify that
   * it wishes to be notified when an instance is removed.
   *  To use more than one modifier use bit-wise OR.
   */
  public final static int INFORM_ON_INSTANCE_REMOVE = 0x10;


  /**
   *  The modifier is used when a listener is registered to specify that
   * it wishes to be notified when a permission is granted or denied to this resource.
   *  To use more than one modifier use bit-wise OR.
   */
  public final static int INFORM_ON_PERMISSION_CHANGED = 0x20;


  /**
   *  Invoked when a new action is defined for the resource.
   *
   * @param  alias  the name of the action that is added.
   */
  public void onActionAdded(String alias);


  /**
   *  Invoked when an action is removed from the resource.
   *
   * @param  alias  the name of the action.
   */
  public void onActionRemoved(String alias);


  /**
   *  Invoked when a new instance is defined for the resource.
   *
   * @param  alias  the name of the instance that is added.
   */
  public void onInstanceAdded(String alias);


  /**
   *  Invoked when a instance defined for the resource is grouped.
   *
   * @param  instanceId the name of the instance.
   * @param  parentId   the name of the parent.
   */
  public void onInstanceGrouped(String instanceId, String parentId);


  /**
   *  Invoked when a instance defined for the resource is ungrouped.
   *
   * @param  instanceId the name of the instance.
   */
  public void onInstanceUngrouped(String instanceId);


  /**
   *  Invoked when a instance is removed from the resource.
   *
   * @param  alias  the name of the instance.
   */
  public void onInstanceRemoved(String alias);


  /**
   *  Invoked when the resource is modified.
   */
  public void onResourceModified();


  /**
   *  Invoked when the resource is removed.
   */
  public void onResourceRemoved();


  /**
   *  Invoked when a permission is granted.
   */
  public void onPermissionGranted(String roleId, String actionId, String instanceId);


  /**
   *  Invoked when a permission is denied.
   */
  public void onPermissionDenied(String roleId, String actionId, String instanceId);


  /**
   *  Invoked when a permission is cleared.
   */
  public void onPermissionCleared(String roleId, String actionId, String instanceId);

}

