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
 * This class represents the output stream cashe, it is a hashtable implementstion.
 *
 * @author Nikolai Neichev
 * @version 4.0
 */
public class StreamCache {

  // default hash index
  private static int hashIndex = 101;

  // the hash lists
  private ObjectList[] cacheStore;

  /**
   * Default constructor.
   */
  public StreamCache() {
    cacheStore = new ObjectList[hashIndex];
  }

  /**
   * Puts an object into the hashtable.
   * @param object The object key.
   * @param position The position value.
   */
  public void put(Object object, int position) {
    int id = System.identityHashCode(object);
    int index = Math.abs(id) % hashIndex;
//         int index = id % hashIndex;
    if (cacheStore[index] != null) {
      cacheStore[index].put(object, id, position);
    } else {
      cacheStore[index] = new ObjectList(object, id, position);
    }
  }

  /**
   * Gets a position value by specified object key.
   * @param object The object key.
   * @return The position value.
   */
  public int get(Object object) {
    int id = System.identityHashCode(object);
    int index = Math.abs(id) % hashIndex;
//    int index = id % hashIndex;
    int res = -1;
    if (cacheStore[index] != null) {
      res = cacheStore[index].get(object, id);
    }
    return res;
  }

  /**
   * Checks if the object key is already in the hashtable.
   * @param object The object key.
   * @return True - if exists.
   */
  public boolean contains(Object object) {
    int id = System.identityHashCode(object);
    int index = Math.abs(id) % hashIndex;
//    int index = id % hashIndex;
    boolean res = false;
    if (cacheStore[index] != null) {
      res = cacheStore[index].contains(object, id);
    }
    return res;
  }

}
