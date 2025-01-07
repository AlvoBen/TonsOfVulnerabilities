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
 * This class represents a hashtable list.
 *
 * @author Nikolai Neichev
 * @version 4.0
 */
public class ObjectList {

  // the list root
  private ListNode root;
  // the last element
  private ListNode last;


  public ObjectList(Object object, int identityHashCode, int position) {
    root = new ListNode(object, identityHashCode, position);
    last = root;
  }

  /**
   * Puts an object into the list.
   * @param object The object key.
   * @param position The position value.
   */
  public void put(Object object, int identityHashCode, int position) {
    if (root == null) {
      root = new ListNode(object, identityHashCode, position);
      last = root;
    } else {
      last.next = new ListNode(object, identityHashCode, position);
      last = last.next;
    }
  }

  /**
   * Gets a position value by specified object key.
   * @param object The object key.
   * @return The position value.
   */
  public int get(Object object, int identityHashCode) {
    ListNode temp = root;
    while (temp != null) {
      try {
        if ((temp.getIdentityHashCode() == identityHashCode) && temp.getObject().equals(object)) {
          return temp.getPosition();
        }
      } catch (Exception ex) {
        //$JL-EXC$ object's equals impementation error
      }
      temp = temp.next;
    }
    return -1;
  }

  /**
   * Checks if the object key is already in the list.
   * @param object The object key.
   * @return True - if exists.
   */
  public boolean contains(Object object, int identityHashCode) {
    ListNode temp = root;
    while (temp != null) {
      try {
        if ((temp.getIdentityHashCode() == identityHashCode ) && temp.getObject().equals(object)) {
          return true;
        }
      } catch (Exception ex) {
        //$JL-EXC$ object's equals impementation error
      }
      temp = temp.next;
    }
    return false;
  }

}
