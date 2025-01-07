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
 * This class is thread safe implementation of HashMapIntBoolean
 * @see com.sap.engine.lib.util.HashMapIntBoolean
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentHashMapIntBoolean extends HashMapIntBoolean {

  static final long serialVersionUID = 4379396485267229915L;
  
  public ConcurrentHashMapIntBoolean(int param1) {
    super(param1);
  }

  public ConcurrentHashMapIntBoolean(int param1, int param2, float param3, IntHashHolder param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentHashMapIntBoolean() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntBoolean.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntBoolean.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntBoolean.contains(int param1)
   */
  public boolean contains(boolean param1) {
    synchronized (this) {
      return super.contains(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntBoolean.elements()
   */
  public EnumerationBoolean elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntBoolean.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntBoolean.get(int param1)
   */
  public boolean get(int param1) {
    synchronized (this) {
      return super.get(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntBoolean.getAllKeys()
   */
  public int[] getAllKeys() {
    synchronized (this) {
      return super.getAllKeys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntBoolean.getAllValues()
   */
  public boolean[] getAllValues() {
    synchronized (this) {
      return super.getAllValues();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntBoolean.keys()
   */
  public EnumerationInt keys() {
    synchronized (this) {
      return super.keys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntBoolean.put(int param1, int param2)
   */
  public boolean put(int param1, boolean param2) {
    synchronized (this) {
      return super.put(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntBoolean.remove(int param1)
   */
  public boolean remove(int param1) {
    synchronized (this) {
      return super.remove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntBoolean.setHasher(com.sap.engine.lib.util.IntHashHolder param1)
   */
  public void setHasher(IntHashHolder param1) {
    synchronized (this) {
      super.setHasher(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntBoolean.shrink(float param1)
   */
  public void shrink(float param1) {
    synchronized (this) {
      super.shrink(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntBoolean.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntBoolean.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapIntBoolean.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

