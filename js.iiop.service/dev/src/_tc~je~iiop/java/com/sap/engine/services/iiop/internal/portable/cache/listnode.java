/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.internal.portable.cache;

/**
 * This class represents a hashtable list node.
 *
 * @author Nikolai Neichev
 * @version 4.0
 */
public class ListNode {

  // the next node
  protected ListNode next;
  // the object key
  private Object object;
  // the position value
  private int position;

  private int identityHashCode;

  /**
   * Constructor.
   * @param object The object key.
   * @param position The position value.
   */
  public ListNode(Object object, int identityHashCode, int position) {
    this.object = object;
    this.identityHashCode = identityHashCode;
    this.position = position;
  }

  /**
   * Getter method.
   * @return The object key.
   */
  public Object getObject() {
    return object;
  }

  public int getIdentityHashCode() {
    return identityHashCode;
  }

  /**
   * Getter method
   * @return The position value.
   */
  public int getPosition() {
    return position;
  }

}
