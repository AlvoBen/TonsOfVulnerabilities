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
 * This class is thread safe implementation of HashMapIntObject
 * @see com.sap.engine.lib.util.HashMapIntObject
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentHashMapIntObject extends HashMapIntObject {

  static final long serialVersionUID = -1587696595322388709L;
  
  public ConcurrentHashMapIntObject(int param1) {
    super(param1);
  }

  public ConcurrentHashMapIntObject(int param1, int param2, float param3, com.sap.engine.lib.util.IntHashHolder param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentHashMapIntObject() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.contains(Object param1)
   */
  public boolean contains(Object param1) {
    synchronized (this) {
      return super.contains(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.elements()
   */
  public java.util.Enumeration elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.get(int param1)
   */
  public Object get(int param1) {
    synchronized (this) {
      return super.get(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.getAllKeys()
   */
  public int[] getAllKeys() {
    synchronized (this) {
      return super.getAllKeys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.getAllKeys(int[])
   */
  public int[] getAllKeys(int[] i) {
    synchronized (this) {
      return super.getAllKeys(i);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.getAllValues()
   */
  public Object[] getAllValues() {
    synchronized (this) {
      return super.getAllValues();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.getAllValues(Object[])
   */
  public Object[] getAllValues(Object[] o) {
    synchronized (this) {
      return super.getAllValues(o);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.getAllValues(Object[])
   */
  public void copyAllValues(java.lang.Object[] o) {
    synchronized (this) {
      super.getAllValues(o);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.keys()
   */
  public com.sap.engine.lib.util.EnumerationInt keys() {
    synchronized (this) {
      return super.keys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.put(int param1, Object param2)
   */
  public Object put(int param1, Object param2) {
    synchronized (this) {
      return super.put(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.remove(int param1)
   */
  public Object remove(int param1) {
    synchronized (this) {
      return super.remove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.setHasher(com.sap.engine.lib.util.IntHashHolder param1)
   */
  public void setHasher(com.sap.engine.lib.util.IntHashHolder param1) {
    synchronized (this) {
      super.setHasher(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.shrink(float param1)
   */
  public void shrink(float param1) {
    synchronized (this) {
      super.shrink(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntObject.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

