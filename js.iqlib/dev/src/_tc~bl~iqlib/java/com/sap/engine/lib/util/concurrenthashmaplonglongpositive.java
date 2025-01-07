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
package com.sap.engine.lib.util;

/**
 * This class is thread safe implementation of HashMapLongLongPositive
 * @see com.sap.engine.lib.util.HashMapLongLongPositive
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentHashMapLongLongPositive extends HashMapLongLongPositive {

  static final long serialVersionUID = 4399095179795279311L;
  
  public ConcurrentHashMapLongLongPositive(int param1) {
    super(param1);
  }

  public ConcurrentHashMapLongLongPositive(int param1, int param2, float param3, com.sap.engine.lib.util.LongHashHolder param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentHashMapLongLongPositive() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongLongPositive.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongLongPositive.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongLongPositive.contains(long param1)
   */
  public boolean contains(long param1) {
    synchronized (this) {
      return super.contains(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongLongPositive.elements()
   */
  public com.sap.engine.lib.util.EnumerationLong elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongLongPositive.equals(Object param1)
   */
  public boolean equals(Object param1) {
    synchronized (this) {
      return super.equals(param1);
    }
  }
  
  
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongLongPositive.get(long param1)
   */
  public long get(long param1) {
    synchronized (this) {
      return super.get(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongLongPositive.getAllKeys()
   */
  public long[] getAllKeys() {
    synchronized (this) {
      return super.getAllKeys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongLongPositive.getAllValues()
   */
  public long[] getAllValues() {
    synchronized (this) {
      return super.getAllValues();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongLongPositive.keys()
   */
  public com.sap.engine.lib.util.EnumerationLong keys() {
    synchronized (this) {
      return super.keys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongLongPositive.put(long param1, long param2)
   */
  public long put(long param1, long param2) {
    synchronized (this) {
      return super.put(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongLongPositive.remove(long param1)
   */
  public long remove(long param1) {
    synchronized (this) {
      return super.remove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongLongPositive.setHasher(com.sap.engine.lib.util.LongHashHolder param1)
   */
  public void setHasher(com.sap.engine.lib.util.LongHashHolder param1) {
    synchronized (this) {
      super.setHasher(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongLongPositive.shrink(float param1)
   */
  public void shrink(float param1) {
    synchronized (this) {
      super.shrink(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongLongPositive.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongLongPositive.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongLongPositive.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

