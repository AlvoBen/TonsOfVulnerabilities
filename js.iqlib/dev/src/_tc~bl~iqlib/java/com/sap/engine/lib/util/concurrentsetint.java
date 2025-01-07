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
 * This class is thread safe implementation of SetInt
 * @see com.sap.engine.lib.util.SetInt
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentSetInt extends SetInt {

  static final long serialVersionUID = 5859128487199647023L;
  public ConcurrentSetInt(int param1) {
    super(param1);
  }

  public ConcurrentSetInt(int param1, int param2, float param3, com.sap.engine.lib.util.IntHashHolder param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentSetInt() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetInt.add(int param1)
   */
  public boolean add(int param1) {
    synchronized (this) {
      return super.add(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetInt.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetInt.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetInt.contains(int param1)
   */
  public boolean contains(int param1) {
    synchronized (this) {
      return super.contains(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetInt.elements()
   */
  public com.sap.engine.lib.util.EnumerationInt elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetInt.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.SetInt.remove(int param1)
   */
  public boolean remove(int param1) {
    synchronized (this) {
      return super.remove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetInt.shrink(float param1)
   */
  public void shrink(float param1) {
    synchronized (this) {
      super.shrink(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetInt.toArray()
   */
  public int[] toArray() {
    synchronized (this) {
      return super.toArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetInt.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetInt.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetInt.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

