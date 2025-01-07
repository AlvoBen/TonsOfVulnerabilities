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
 * This class is thread safe implementation of HashMapObjectObject
 * @see com.sap.engine.lib.util.HashMapObjectObject
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentHashMapObjectObject extends HashMapObjectObject {

  static final long serialVersionUID = -2187473329234122472L;
  public ConcurrentHashMapObjectObject(int param1) {
    super(param1);
  }

  public ConcurrentHashMapObjectObject(int param1, int param2, float param3, com.sap.engine.lib.util.IntHashHolder param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentHashMapObjectObject() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.contains(Object param1)
   */
  public boolean contains(Object param1) {
    synchronized (this) {
      return super.contains(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.deepClone()
   */
  public Object deepClone() {
    synchronized (this) {
      return super.deepClone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.elements()
   */
  public java.util.Enumeration elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.elementsIterator()
   */
  public com.sap.engine.lib.util.iterators.RootIterator elementsIterator() {
    synchronized (this) {
      return super.elementsIterator();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.get(Object param1)
   */
  public Object get(Object param1) {
    synchronized (this) {
      return super.get(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.getAllKeys()
   */
  public Object[] getAllKeys() {
    synchronized (this) {
      return super.getAllKeys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.getAllKeys(Object[])
   */
  public Object[] getAllKeys(Object[] o) {
    synchronized (this) {
      return super.getAllKeys(o);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.getAllValues()
   */
  public Object[] getAllValues() {
    synchronized (this) {
      return super.getAllValues();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.getAllValues(Object[])
   */
  public Object[] getAllValues(Object[] o) {
    synchronized (this) {
      return super.getAllValues(o);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.iterChange(com.sap.engine.lib.util.iterators.RootIterator param1, Object param2)
   */
  protected Object iterChange(com.sap.engine.lib.util.iterators.RootIterator param1, Object param2) {
    synchronized (this) {
      return super.iterChange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.iterGet(com.sap.engine.lib.util.iterators.RootIterator param1, boolean param2)
   */
  protected Object iterGet(com.sap.engine.lib.util.iterators.RootIterator param1, boolean param2) {
    synchronized (this) {
      return super.iterGet(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.iterNext(com.sap.engine.lib.util.iterators.RootIterator param1, int param2, boolean param3)
   */
  protected Object iterNext(com.sap.engine.lib.util.iterators.RootIterator param1, int param2, boolean param3) {
    synchronized (this) {
      return super.iterNext(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.iterRemove(com.sap.engine.lib.util.iterators.RootIterator param1, boolean param2)
   */
  protected Object iterRemove(com.sap.engine.lib.util.iterators.RootIterator param1, boolean param2) {
    synchronized (this) {
      return super.iterRemove(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.keys()
   */
  public java.util.Enumeration keys() {
    synchronized (this) {
      return super.keys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.keysIterator()
   */
  public com.sap.engine.lib.util.iterators.RootIterator keysIterator() {
    synchronized (this) {
      return super.keysIterator();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.put(Object param1, Object param2)
   */
  public Object put(Object param1, Object param2) {
    synchronized (this) {
      return super.put(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.remove(Object param1)
   */
  public Object remove(Object param1) {
    synchronized (this) {
      return super.remove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.setHasher(com.sap.engine.lib.util.IntHashHolder param1)
   */
  public void setHasher(com.sap.engine.lib.util.IntHashHolder param1) {
    synchronized (this) {
      super.setHasher(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.shrink(float param1)
   */
  public void shrink(float param1) {
    synchronized (this) {
      super.shrink(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.HashMapObjectObject.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

