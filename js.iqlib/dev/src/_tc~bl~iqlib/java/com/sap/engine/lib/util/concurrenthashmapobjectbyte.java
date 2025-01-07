/**
 * Title:        iQ-Lib
 * Description:  Data Structures & Algorithms Library
 * Copyright:    Copyright (c) 2000
 * Company:      SAP Labs Bulgaria LTD., Sofia, Bulgaria.
 * Url:          Http://www.saplabs.bg
 *               All rights reserved.
 *
 *               This software is the confidential and proprietary information
 *               of SAP AG International ("Confidential Information").
 *               You shall not disclose such  Confidential Information
 *               and shall use it only in accordance with the terms of
 *               the license agreement you entered into with SAP AG.
 */
package com.sap.engine.lib.util;

/**
 * This class is a thread safe implementation of HashMapObjectByte
 * @see com.sap.engine.lib.util.HashMapObjectByte
 *
 * @author Georgi Manev georgi.maneff@sap.com
 * @version 1.0
 */
public class ConcurrentHashMapObjectByte extends HashMapObjectByte {

  static final long serialVersionUID = -5735700657686597723L;
  
  public ConcurrentHashMapObjectByte() {
    super();
  }

  public ConcurrentHashMapObjectByte(int initialCapacity) {
    super(initialCapacity);
  }

  public ConcurrentHashMapObjectByte(int initialCapacity, int growStep, float loadFactor, IntHashHolder hasher) {
    super(initialCapacity, growStep, loadFactor, hasher);
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectByte.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectByte.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectByte.contains(byte value)
   */
  public boolean contains(byte value) {
    synchronized (this) {
      return super.contains(value);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectByte.elements()
   */
  public EnumerationByte elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectByte.equals(Object object)
   */
  public boolean equals(Object object) {
    synchronized (this) {
      return super.equals(object);
    }
  }
  
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectByte.get(Object key)
   */
  public byte get(Object key) {
    synchronized (this) {
      return super.get(key);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectByte.getAllKeys()
   */
  public Object[] getAllKeys() {
    synchronized (this) {
      return super.getAllKeys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectByte.getAllKeys(Object[], int)
   */
  public Object[] getAllKeys(Object[] result, int index) {
    synchronized (this) {
      return super.getAllKeys(result, index);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectByte.getAllValues()
   */
  public byte[] getAllValues() {
    synchronized (this) {
      return super.getAllValues();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectByte.getAllValues(byte[], int)
   */
  public byte[] getAllValues(byte[] result, int index) {
    synchronized (this) {
      return super.getAllValues(result, index);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectByte.keys()
   */
  public java.util.Enumeration keys() {
    synchronized (this) {
      return super.keys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectByte.put(Object, byte)
   */
  public boolean put(Object key, byte value) {
    synchronized (this) {
      return super.put(key, value);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectByte.remove(Object param1)
   */
  public boolean remove(Object param1) {
    synchronized (this) {
      return super.remove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectByte.setHasher(com.sap.engine.lib.util.IntHashHolder param1)
   */
  public void setHasher(com.sap.engine.lib.util.IntHashHolder param1) {
    synchronized (this) {
      super.setHasher(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectByte.shrink(float param1)
   */
  public void shrink(float param1) {
    synchronized (this) {
      super.shrink(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectByte.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectByte.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectByte.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

