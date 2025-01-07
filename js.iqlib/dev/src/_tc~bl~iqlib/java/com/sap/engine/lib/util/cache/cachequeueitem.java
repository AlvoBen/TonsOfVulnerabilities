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

package com.sap.engine.lib.util.cache;

/**
 * Class representing an item in the LRU cache
 *
 * @author Iliyan Nenov, ilian.nenov@sap.com
 * @version SAP J2EE Engine 6.30
 */
public class CacheQueueItem {

  /*
   * item is not cached
   */
  public final static int NOT_CACHED = -1;
  /*
   * item is cached data
   */
  public final static int CACHED_DATA = 2;
  /*
   * current item type
   */
  protected short cacheType = NOT_CACHED;

  /*
   * A key used for mapping this item.
   */
  Object key = null;

  /*
   * previous element from double-linked list
   */
  CacheQueueItem prev = null;

  /*
   * next element from double-linked list
   */
  CacheQueueItem next = null;

  /*
   * The cached Object of this CacheQueueItem
   */
  Object value = null;

  /**
   * Default constructor
   */
  public CacheQueueItem() {
  }

  /**
   * Constructor
   */
  public CacheQueueItem(CacheQueueItem _prev, CacheQueueItem _next, Object _value) {
    this.prev = _prev;
    this.next = _next;
    this.value = _value;
  }

//  /**
//   * Releases info object. The Item is pooled.
//   * (there is an idea for pooling the Items for now there is no pooling)
//   */
//  public void release() {
//    // for pool
//    this.prev = null;
//    this.next = null;
//  }

//  /**
//   * Sets variable
//   */
//  public void reuse(Object _key, Object _value) {
//    this.key = new CacheQueueItemKey(_key);
//    this.value = _value;
//  }

}