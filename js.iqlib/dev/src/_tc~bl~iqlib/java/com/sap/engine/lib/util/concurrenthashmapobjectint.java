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
 * This class is thread safe implementation of HashMapObjectInt
 * @see com.sap.engine.lib.util.HashMapObjectInt
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentHashMapObjectInt extends HashMapObjectInt {

  static final long serialVersionUID = 1418280821256756927L;
  
  public ConcurrentHashMapObjectInt(int param1) {
    super(param1);
  }

  public ConcurrentHashMapObjectInt(int param1, int param2, float param3, com.sap.engine.lib.util.IntHashHolder param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentHashMapObjectInt() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectInt.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectInt.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectInt.contains(int param1)
   */
  public boolean contains(int param1) {
    synchronized (this) {
      return super.contains(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectInt.elements()
   */
  public com.sap.engine.lib.util.EnumerationInt elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectInt.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectInt.get(Object param1)
   */
  public int get(Object param1) {
    synchronized (this) {
      return super.get(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectInt.getAllKeys()
   */
  public Object[] getAllKeys() {
    synchronized (this) {
      return super.getAllKeys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectInt.getAllKeys(Object[], int)
   */
  public Object[] getAllKeys(Object[] result, int index) {
    synchronized (this) {
      return super.getAllKeys(result, index);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectInt.getAllValues()
   */
  public int[] getAllValues() {
    synchronized (this) {
      return super.getAllValues();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectInt.getAllValues(int[], int)
   */
  public int[] getAllValues(int[] result, int index) {
    synchronized (this) {
      return super.getAllValues(result, index);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectInt.keys()
   */
  public java.util.Enumeration keys() {
    synchronized (this) {
      return super.keys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectInt.put(Object param1, int param2)
   */
  public boolean put(Object param1, int param2) {
    synchronized (this) {
      return super.put(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectInt.remove(Object param1)
   */
  public boolean remove(Object param1) {
    synchronized (this) {
      return super.remove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectInt.setHasher(com.sap.engine.lib.util.IntHashHolder param1)
   */
  public void setHasher(com.sap.engine.lib.util.IntHashHolder param1) {
    synchronized (this) {
      super.setHasher(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectInt.shrink(float param1)
   */
  public void shrink(float param1) {
    synchronized (this) {
      super.shrink(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectInt.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectInt.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectInt.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

