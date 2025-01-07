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
 * This class is thread safe implementation of HashMapLongIntPositive
 * @see com.sap.engine.lib.util.HashMapLongIntPositive
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentHashMapLongIntPositive extends HashMapLongIntPositive {

  static final long serialVersionUID = 3409979212835236518L;
  
  public ConcurrentHashMapLongIntPositive(int param1) {
    super(param1);
  }

  public ConcurrentHashMapLongIntPositive(int param1, int param2, float param3, com.sap.engine.lib.util.LongHashHolder param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentHashMapLongIntPositive() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongIntPositive.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongIntPositive.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongIntPositive.contains(int param1)
   */
  public boolean contains(int param1) {
    synchronized (this) {
      return super.contains(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongIntPositive.elements()
   */
  public com.sap.engine.lib.util.EnumerationInt elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongIntPositive.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongIntPositive.get(long param1)
   */
  public int get(long param1) {
    synchronized (this) {
      return super.get(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongIntPositive.getAllKeys()
   */
  public long[] getAllKeys() {
    synchronized (this) {
      return super.getAllKeys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongIntPositive.getAllValues()
   */
  public int[] getAllValues() {
    synchronized (this) {
      return super.getAllValues();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongIntPositive.keys()
   */
  public com.sap.engine.lib.util.EnumerationLong keys() {
    synchronized (this) {
      return super.keys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongIntPositive.put(long param1, int param2)
   */
  public int put(long param1, int param2) {
    synchronized (this) {
      return super.put(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongIntPositive.remove(long param1)
   */
  public int remove(long param1) {
    synchronized (this) {
      return super.remove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongIntPositive.setHasher(com.sap.engine.lib.util.LongHashHolder param1)
   */
  public void setHasher(com.sap.engine.lib.util.LongHashHolder param1) {
    synchronized (this) {
      super.setHasher(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongIntPositive.shrink(float param1)
   */
  public void shrink(float param1) {
    synchronized (this) {
      super.shrink(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongIntPositive.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongIntPositive.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongIntPositive.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

