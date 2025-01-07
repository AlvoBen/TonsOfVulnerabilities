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
 * This class is thread safe implementation of SetLong
 * @see com.sap.engine.lib.util.SetLong
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentSetLong extends SetLong {

  static final long serialVersionUID = -2747267991589566023L;
  
  public ConcurrentSetLong(int param1) {
    super(param1);
  }

  public ConcurrentSetLong(int param1, int param2, float param3, com.sap.engine.lib.util.LongHashHolder param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentSetLong() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetLong.add(long param1)
   */
  public boolean add(long param1) {
    synchronized (this) {
      return super.add(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetLong.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetLong.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetLong.contains(long param1)
   */
  public boolean contains(long param1) {
    synchronized (this) {
      return super.contains(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetLong.elements()
   */
  public com.sap.engine.lib.util.EnumerationLong elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetLong.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.SetLong.remove(long param1)
   */
  public boolean remove(long param1) {
    synchronized (this) {
      return super.remove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetLong.shrink(float param1)
   */
  public void shrink(float param1) {
    synchronized (this) {
      super.shrink(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetLong.toArray()
   */
  public long[] toArray() {
    synchronized (this) {
      return super.toArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetLong.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetLong.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.SetLong.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

