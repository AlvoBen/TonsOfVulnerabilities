/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.lib.util.cache.pool.weak;

/**
 *
 *
 * @author Iliyan Nenov, ilian.nenov@sap.com
 * @version SAP J2EE Engine 6.30
 */
public class Pool {

  /**
   * index of queue
   */
  PoolItem index = null;

  /**
   * Puts element in queue.
   *
   * @param   item  element to be put
   */
  public synchronized void put(PoolItem item) {
    if (index == null) {
      item.prev = item;
      item.next = item;
      index = item;
    } else {
      item.next = index.next;
      item.prev = index;
      item.next.prev = item;
      index.next = item;
    }
  }

  /**
   * Gets element from queue.
   *
   * @return
   */
  public synchronized PoolItem get() {
    PoolItem result = null;

    if (index != null) {
      result = index;

      if (index.next == index) {
        index = null;
      } else {
        index.prev.next = index.next;
        index.next.prev = index.prev;
        index = index.prev;
      }
    }

    return result;
  }

  /**
   * Removes element from queue.
   *
   * @param item  element to be removed
   */
  public synchronized boolean remove(PoolItem item) {
    if (item == index) {
      index = index.prev;
      return true;
    }

    if (index.next == index) {
      index = null;
      return false;
    } else {
      item.prev.next = item.next;
      item.next.prev = item.prev;
    }
    return true;
  }

}