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
 * This class is thread safe implementation of Set
 * @see com.sap.engine.lib.util.Set
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentSet extends Set {

  static final long serialVersionUID = 2122013456453166651L;
  public ConcurrentSet(int param1) {
    super(param1);
  }

  public ConcurrentSet(int param1, int param2, float param3, com.sap.engine.lib.util.IntHashHolder param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentSet() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Set.add(Object param1)
   */
  public boolean add(Object param1) {
    synchronized (this) {
      return super.add(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Set.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Set.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetInt.elements()
   */
  public java.util.Enumeration elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Set.contains(Object param1)
   */
  public boolean contains(Object param1) {
    synchronized (this) {
      return super.contains(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Set.deepClone()
   */
  public Object deepClone() {
    synchronized (this) {
      return super.deepClone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Set.elementsIterator()
   */
  public com.sap.engine.lib.util.iterators.RootIterator elementsIterator() {
    synchronized (this) {
      return super.elementsIterator();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Set.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.Set.iterGet(com.sap.engine.lib.util.iterators.RootIterator param1)
   */
  protected Object iterGet(com.sap.engine.lib.util.iterators.RootIterator param1) {
    synchronized (this) {
      return super.iterGet(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Set.iterNext(com.sap.engine.lib.util.iterators.RootIterator param1, int param2)
   */
  protected Object iterNext(com.sap.engine.lib.util.iterators.RootIterator param1, int param2) {
    synchronized (this) {
      return super.iterNext(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Set.iterRemove(com.sap.engine.lib.util.iterators.RootIterator param1)
   */
  protected Object iterRemove(com.sap.engine.lib.util.iterators.RootIterator param1) {
    synchronized (this) {
      return super.iterRemove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Set.remove(Object param1)
   */
  public boolean remove(Object param1) {
    synchronized (this) {
      return super.remove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Set.shrink(float param1)
   */
  public void shrink(float param1) {
    synchronized (this) {
      super.shrink(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Set.toArray()
   */
  public Object[] toArray() {
    synchronized (this) {
      return super.toArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Set.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Set.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Set.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

