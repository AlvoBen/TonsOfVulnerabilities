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
 * This class is thread safe implementation of HashMapIntInt
 * @see com.sap.engine.lib.util.HashMapIntInt
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentHashMapIntInt extends HashMapIntInt {

  static final long serialVersionUID = -3600779224768831619L;
  
  public ConcurrentHashMapIntInt(int param1) {
    super(param1);
  }

  public ConcurrentHashMapIntInt(int param1, int param2, float param3, com.sap.engine.lib.util.IntHashHolder param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentHashMapIntInt() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntInt.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntInt.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntInt.contains(int param1)
   */
  public boolean contains(int param1) {
    synchronized (this) {
      return super.contains(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntInt.elements()
   */
  public com.sap.engine.lib.util.EnumerationInt elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntInt.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntInt.get(int param1)
   */
  public int get(int param1) {
    synchronized (this) {
      return super.get(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntInt.getAllKeys()
   */
  public int[] getAllKeys() {
    synchronized (this) {
      return super.getAllKeys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntInt.getAllValues()
   */
  public int[] getAllValues() {
    synchronized (this) {
      return super.getAllValues();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntInt.keys()
   */
  public com.sap.engine.lib.util.EnumerationInt keys() {
    synchronized (this) {
      return super.keys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntInt.put(int param1, int param2)
   */
  public boolean put(int param1, int param2) {
    synchronized (this) {
      return super.put(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntInt.remove(int param1)
   */
  public boolean remove(int param1) {
    synchronized (this) {
      return super.remove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntInt.setHasher(com.sap.engine.lib.util.IntHashHolder param1)
   */
  public void setHasher(com.sap.engine.lib.util.IntHashHolder param1) {
    synchronized (this) {
      super.setHasher(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntInt.shrink(float param1)
   */
  public void shrink(float param1) {
    synchronized (this) {
      super.shrink(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntInt.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntInt.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntInt.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

