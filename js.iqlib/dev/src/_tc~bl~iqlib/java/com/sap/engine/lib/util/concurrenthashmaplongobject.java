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
 * This class is thread safe implementation of HashMapLongObject
 * @see com.sap.engine.lib.util.HashMapLongObject
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentHashMapLongObject extends HashMapLongObject {
  
  static final long serialVersionUID = 5665289179820746831L;
  
  public ConcurrentHashMapLongObject(int param1) {
    super(param1);
  }

  public ConcurrentHashMapLongObject(int param1, int param2, float param3, com.sap.engine.lib.util.LongHashHolder param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentHashMapLongObject() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongObject.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongObject.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongObject.contains(Object param1)
   */
  public boolean contains(Object param1) {
    synchronized (this) {
      return super.contains(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongObject.elements()
   */
  public java.util.Enumeration elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongObject.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongObject.get(long param1)
   */
  public Object get(long param1) {
    synchronized (this) {
      return super.get(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongObject.getAllKeys()
   */
  public long[] getAllKeys() {
    synchronized (this) {
      return super.getAllKeys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongObject.getAllValues()
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
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongObject.keys()
   */
  public com.sap.engine.lib.util.EnumerationLong keys() {
    synchronized (this) {
      return super.keys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongObject.put(long param1, Object param2)
   */
  public Object put(long param1, Object param2) {
    synchronized (this) {
      return super.put(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongObject.remove(long param1)
   */
  public Object remove(long param1) {
    synchronized (this) {
      return super.remove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongObject.setHasher(com.sap.engine.lib.util.LongHashHolder param1)
   */
  public void setHasher(com.sap.engine.lib.util.LongHashHolder param1) {
    synchronized (this) {
      super.setHasher(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongObject.shrink(float param1)
   */
  public void shrink(float param1) {
    synchronized (this) {
      super.shrink(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongObject.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongObject.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapLongObject.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

