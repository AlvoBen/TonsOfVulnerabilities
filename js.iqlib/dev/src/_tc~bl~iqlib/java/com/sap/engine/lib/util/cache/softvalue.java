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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 * @author Iliyan Nenov, ilian.nenov@sap.com
 * @version SAP J2EE Engine 6.30
 */
public class SoftValue extends SoftReference {

  /**
   * If true then this object will never be moved to the
   * weak part of the caceh
   */
  protected boolean alwaysInWeak = false;

  /**
   * The key to this SoftReference object in the hash table
   * this is done for avoiding enumeration of the hash map and
   * linear searching in the collection. The result is fast search
   */
  protected Object key;

  /**
   * A reference to the owner of this SoftValue
   */
  protected SoftValueHashMap hashMap;

  /**
   * Constructs a SoftValue form a given object and a ReferenceQueue
   */
  public SoftValue(Object v, ReferenceQueue q, Object k, SoftValueHashMap hash, boolean _alwaysInWeak) {
    super(v, q);
    this.key = k;
    this.hashMap = hash;
    this.alwaysInWeak = _alwaysInWeak;
  }

}