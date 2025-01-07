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
 * This class is thread safe implementation of HashMapObjectLongPositive
 * @see com.sap.engine.lib.util.HashMapObjectLongPositive
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentHashMapObjectLongPositive extends HashMapObjectLongPositive {
  static final long serialVersionUID = 1335015773400778496L;
  
  public ConcurrentHashMapObjectLongPositive(int param1) {
    super(param1);
  }

  public ConcurrentHashMapObjectLongPositive(int param1, int param2, float param3, com.sap.engine.lib.util.IntHashHolder param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentHashMapObjectLongPositive() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectLongPositive.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectLongPositive.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectLongPositive.contains(long param1)
   */
  public boolean contains(long param1) {
    synchronized (this) {
      return super.contains(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectLongPositive.elements()
   */
  public com.sap.engine.lib.util.EnumerationLong elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectLongPositive.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectLongPositive.get(Object param1)
   */
  public long get(Object param1) {
    synchronized (this) {
      return super.get(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectLongPositive.getAllKeys()
   */
  public Object[] getAllKeys() {
    synchronized (this) {
      return super.getAllKeys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectLongPositive.getAllValues()
   */
  public long[] getAllValues() {
    synchronized (this) {
      return super.getAllValues();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectLongPositive.keys()
   */
  public java.util.Enumeration keys() {
    synchronized (this) {
      return super.keys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectLongPositive.put(Object param1, long param2)
   */
  public long put(Object param1, long param2) {
    synchronized (this) {
      return super.put(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectLongPositive.remove(Object param1)
   */
  public long remove(Object param1) {
    synchronized (this) {
      return super.remove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectLongPositive.setHasher(com.sap.engine.lib.util.IntHashHolder param1)
   */
  public void setHasher(com.sap.engine.lib.util.IntHashHolder param1) {
    synchronized (this) {
      super.setHasher(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectLongPositive.shrink(float param1)
   */
  public void shrink(float param1) {
    synchronized (this) {
      super.shrink(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectLongPositive.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectLongPositive.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectLongPositive.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

