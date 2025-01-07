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
 * This class represents the output stream cashe, it is a hashtable implementstion.
 *
 * @author Nikolai Neichev
 * @version 4.0
 */
public class WeakStorrage {

  // default hash index
  private static int hashIndex = 11;

  // the hash lists
  private ObjectList[] cacheStore;

  /**
   * Default constructor.
   */
  public WeakStorrage() {
    init();
  }

  /**
   * Constructor.
   * @param hashIndex New hash index value.
   */
  public WeakStorrage(int hashIndex) {
    if (hashIndex > 0) {
      this.hashIndex = hashIndex;
    } else {
      this.hashIndex = 1;
    }
    init();
  }

  private void init() {
    cacheStore = new ObjectList[hashIndex];
  }

  /**
   * Puts an object into the hashtable.
   * @param object The object key.
   */
  public void put(WeakReference object) {
    int id = Math.abs(System.identityHashCode(object));
    int index = id % hashIndex;
    if (cacheStore[index] != null) {
      cacheStore[index].put(object);
    } else {
      cacheStore[index] = new ObjectList(object);
    }
  }

  /**
   * Removes an object into the hashtable.
   * @param object The object key.
   */
  public void remove(WeakReference object) {
    int id = Math.abs(System.identityHashCode(object));
    int index = id % hashIndex;
    if (cacheStore[index] != null) {
      cacheStore[index].remove(object);
    }
  }

  /**
   * Checks if the object key is already in the hashtable.
   * @param object The object key.
   * @return True - if exists.
   */
  public boolean contains(Object object) {
    int id = Math.abs(System.identityHashCode(object));
    int index = id % hashIndex;
    boolean res = false;
    if (cacheStore[index] != null) {
      res = cacheStore[index].contains(object);
    }
    return res;
  }

  public WeakReference[] toArray() {
    int all = 0;
    for (int i = 0; i < cacheStore.length; i++) {
      all += cacheStore[i].size();
    }
    WeakReference[] result = new WeakReference[all];
    int start = 0;
    for (int i = 0; i < cacheStore.length; i++) {
      System.arraycopy(cacheStore[i].toArray(), 0, result, start, cacheStore[i].size());
      start += cacheStore[i].size();
    }
    return result;
  }

}
