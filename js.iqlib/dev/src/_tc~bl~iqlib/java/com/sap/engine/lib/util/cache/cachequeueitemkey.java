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
 * Class representing the key of each cache item so the item
 * could be stored it in a hash table.
 *
 * @author Iliyan Nenov, ilian.nenov@sap.com
 * @version SAP J2EE Engine 6.30
 */
public class CacheQueueItemKey {

  /**
   * The Key could be anything
   */
  public Object key;

  /**
   * Constructor
   */
  public CacheQueueItemKey(Object _key) {
    key = _key;
  }

  /**
   * The keys are equal iff the objects are the same
   */
  public boolean equals(Object _key) {
    return key.equals(_key);
  }

  /**
   * Hash code from Object class
   */
  public int hashCode() {
    return key.hashCode();
  }

  /**
   * String representation of the key
   */
  public String toString() {
    return "CacheQueueItem | Key [" + key.toString() + "]";
  }

}