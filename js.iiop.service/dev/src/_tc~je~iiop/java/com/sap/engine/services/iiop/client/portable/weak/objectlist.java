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
package com.sap.engine.services.iiop.client.portable.weak;

import java.lang.ref.WeakReference;

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

  private int count = 0;

  /**
   * Constructor.
   */
  public ObjectList() {
  }

  public ObjectList(WeakReference object) {
    root = new ListNode(object);
    last = root;
  }

  /**
   * Puts an object into the list.
   * @param object The object key.
   */
  public void put(WeakReference object) {
    if (root == null) {
      root = new ListNode(object);
      last = root;
    } else {
      last.next = new ListNode(object);
      last = last.next;
    }
    count++;
  }

  /**
   * Removes a value by specified object key.
   * @param object The object key.
   * @return True - if al is ok.
   */
  public boolean remove(Object object) {
    ListNode temp = root;
    ListNode prev = root;
    while (temp != null) {
      try {
        if (temp.getObject().equals(object)) {
          prev.next = temp.next;
          return true;
        }
      } catch (Exception ex) {//$JL-EXC$
      }
      prev = temp;
      temp = temp.next;
    }
    return false;
  }

  /**
   * Gets a position value by specified object key.
   * @param object The object key.
   * @return The position value.
   */
  public Object get(Object object) {
    ListNode temp = root;
    while (temp != null) {
      try {
        if (temp.getObject().equals(object)) {
          return object;
        }
      } catch (Exception ex) {//$JL-EXC$
      }
      temp = temp.next;
    }
    return null;
  }

  /**
   * Checks if the object key is already in the list.
   * @param object The object key.
   * @return True - if exists.
   */
  public boolean contains(Object object) {
    ListNode temp = root;
    while (temp != null) {
      try {
        if (temp.getObject().equals(object)) {
          return true;
        }
      } catch (Exception ex) { //$JL-EXC$        
      }
      temp = temp.next;
    }
    return false;
  }

  public WeakReference[] toArray() {
    WeakReference[] result = new WeakReference[count];
    int index = 0;
    ListNode temp = root;
    while (temp != null) {
      result[index] = temp.getObject();
      temp = temp.next;
    }
    return result;
  }

  public int size() {
    return count;
  }

}
