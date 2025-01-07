/**
 * Copyright (c) 2008 by SAP Labs Bulgaria,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * 
 * Created on Sep 29, 2008 by I030797
 *   
 */
 
package com.sap.engine.services.security.login;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;


/**
 * @author I030797
 *
 */
public class WeakReferenceKey<T> extends WeakReference<T> {
  
  private int hash;
  
  /**
   * @param referent
   */
  public WeakReferenceKey(T referent) {
    super(referent);
    hash = referent.hashCode();
  }

  /**
   * @param referent
   * @param q
   */
  public WeakReferenceKey(T referent, ReferenceQueue<? super T> q) {
    super(referent, q);
    hash = referent.hashCode();
  }

  public int hashCode() {
    Object referent = get();
    
    if (referent != null) {
      return get().hashCode();
    }
    
    return hash;
  }
  
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }
    
    if (!(other instanceof WeakReferenceKey)) {
      return false;
    }
    
    T thisRef = get();
    WeakReferenceKey<T> that = (WeakReferenceKey<T>) other;
    T thatRef = that.get();
    
    if (thisRef == null || thatRef == null) {
      return hash == that.hashCode();
    }
    
    return thisRef.equals(thatRef);
  }
}
