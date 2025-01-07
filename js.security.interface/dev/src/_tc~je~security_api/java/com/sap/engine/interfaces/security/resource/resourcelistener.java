/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security.resource;

/**
 *  Resource listeners are informed of changes concerning resources registered
 * in the resource manager.
 *
 * @author  Jako Blagoev
 * @author  Stephan Zlatarev
 * @version 6.30
 *
 * @deprecated Resource context is deprecated since NW04 AS Java. Use UME API instead.
 * @see com.sap.engine.interfaces.security.ResourceContext
 * @see com.sap.engine.interfaces.security.resource.ResourceHandle
 */
public interface ResourceListener {

  /**
   *  The modifier is used when a listener is registered to specify that
   * it wishes to be notified when a resource is added.
   *  To use more than one modifier use bit-wise OR. For example, to listen
   * for all notifications use:
   *  <code>INFORM_ON_ADD | INFORM_ON_MODIFY | INFORM_ON_REMOVE<code>
   */
  public final static int INFORM_ON_ADD = 0x01;


  /**
   *  The modifier is used when a listener is registered to specify that
   * it wishes to be notified when a resource is modified.
   *  To use more than one modifier use bit-wise OR.
   */
  public final static int INFORM_ON_MODIFY = 0x02;


  /**
   *  The modifier is used when a listener is registered to specify that
   * it wishes to be notified when a resource is removed.
   *  To use more than one modifier use bit-wise OR.
   */
  public final static int INFORM_ON_REMOVE = 0x04;


  /**
   *  Invoked when a new resource is registered.
   *
   * @param  handle  a handle to the new resource.
   */
  public void onResourceAdded(ResourceHandle handle);


  /**
   *  Invoked when a resource is modified.
   *
   * @param  handle  a handle to the resource.
   */
  public void onResourceModified(ResourceHandle handle);


  /**
   *  Invoked when a resource is removed.
   *
   * @param  alias  the name of the resource.
   */
  public void onResourceRemoved(String alias);

}