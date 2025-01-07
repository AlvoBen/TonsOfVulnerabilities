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
 * This class is thread safe implementation of HashMapLongInt
 * @see com.sap.engine.lib.util.HashMapLongInt
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentHashMapLongInt extends HashMapLongInt {

  static final long serialVersionUID = -5163940165942805348L;
  
  public ConcurrentHashMapLongInt(int param1) {
    super(param1);
  }

  public ConcurrentHashMapLongInt(int param1, int param2, float param3, com.sap.engine.lib.util.LongHashHolder param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentHashMapLongInt() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongInt.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongInt.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongInt.contains(int param1)
   */
  public boolean contains(int param1) {
    synchronized (this) {
      return super.contains(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongInt.elements()
   */
  public com.sap.engine.lib.util.EnumerationInt elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongInt.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongInt.get(long param1)
   */
  public int get(long param1) {
    synchronized (this) {
      return super.get(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongInt.getAllKeys()
   */
  public long[] getAllKeys() {
    synchronized (this) {
      return super.getAllKeys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongInt.getAllValues()
   */
  public int[] getAllValues() {
    synchronized (this) {
      return super.getAllValues();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongInt.keys()
   */
  public com.sap.engine.lib.util.EnumerationLong keys() {
    synchronized (this) {
      return super.keys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongInt.put(long param1, int param2)
   */
  public boolean put(long param1, int param2) {
    synchronized (this) {
      return super.put(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongInt.remove(long param1)
   */
  public boolean remove(long param1) {
    synchronized (this) {
      return super.remove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongInt.setHasher(com.sap.engine.lib.util.LongHashHolder param1)
   */
  public void setHasher(com.sap.engine.lib.util.LongHashHolder param1) {
    synchronized (this) {
      super.setHasher(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongInt.shrink(float param1)
   */
  public void shrink(float param1) {
    synchronized (this) {
      super.shrink(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongInt.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongInt.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongInt.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

