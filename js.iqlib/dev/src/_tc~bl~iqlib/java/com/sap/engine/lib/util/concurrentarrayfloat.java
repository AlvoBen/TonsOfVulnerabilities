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
 * This class is thread safe implementation of ArrayFloat
 * @see com.sap.engine.lib.util.ArrayFloat
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentArrayFloat extends ArrayFloat {

  static final long serialVersionUID = -7535070368025246893L;

  public ConcurrentArrayFloat() {
    super();
  }

  public ConcurrentArrayFloat(float[] param1, int param2, int param3, int param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentArrayFloat(float[] param1) {
    super(param1);
  }

  public ConcurrentArrayFloat(com.sap.engine.lib.util.ArrayFloat param1, int param2, int param3, int param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentArrayFloat(com.sap.engine.lib.util.ArrayFloat param1) {
    super(param1);
  }

  public ConcurrentArrayFloat(int param1) {
    super(param1);
  }

  public ConcurrentArrayFloat(int param1, int param2) {
    super(param1, param2);
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.add(float param1)
   */
  public void add(float param1) {
    synchronized (this) {
      super.add(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.addAll(com.sap.engine.lib.util.ArrayFloat param1, int param2, int param3)
   */
  public void addAll(com.sap.engine.lib.util.ArrayFloat param1, int param2, int param3) {
    synchronized (this) {
      super.addAll(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.addAll(float[] param1, int param2, int param3)
   */
  public void addAll(float[] param1, int param2, int param3) {
    synchronized (this) {
      super.addAll(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.addAll(int param1, com.sap.engine.lib.util.ArrayFloat param2, int param3, int param4)
   */
  public void addAll(int param1, com.sap.engine.lib.util.ArrayFloat param2, int param3, int param4) {
    synchronized (this) {
      super.addAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.addAll(int param1, float[] param2, int param3, int param4)
   */
  public void addAll(int param1, float[] param2, int param3, int param4) {
    synchronized (this) {
      super.addAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.addElement(float param1)
   */
  public void addElement(float param1) {
    synchronized (this) {
      super.addElement(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.binarySearch(float param1)
   */
  public int binarySearch(float param1) {
    synchronized (this) {
      return super.binarySearch(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.binarySearch(float param1, boolean param2)
   */
  public int binarySearch(float param1, boolean param2) {
    synchronized (this) {
      return super.binarySearch(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.binarySearch(float param1, int param2, int param3)
   */
  public int binarySearch(float param1, int param2, int param3) {
    synchronized (this) {
      return super.binarySearch(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.binarySearch(float param1, int param2, int param3, boolean param4)
   */
  public int binarySearch(float param1, int param2, int param3, boolean param4) {
    synchronized (this) {
      return super.binarySearch(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.capacity()
   */
  public int capacity() {
    synchronized (this) {
      return super.capacity();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.copyInto(float[] param1)
   */
  public void copyInto(float[] param1) {
    synchronized (this) {
      super.copyInto(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.copyInto(int param1, float[] param2, int param3, int param4)
   */
  public void copyInto(int param1, float[] param2, int param3, int param4) {
    synchronized (this) {
      super.copyInto(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.elementAt(int param1)
   */
  public float elementAt(int param1) {
    synchronized (this) {
      return super.elementAt(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.elements()
   */
  public com.sap.engine.lib.util.EnumerationFloat elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.equals(Object param1)
   */
  public boolean equals(Object param1) {
    synchronized (this) {
      return super.equals(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.equals_(com.sap.engine.lib.util.ArrayFloat param1)
   */
  protected boolean equals_(com.sap.engine.lib.util.ArrayFloat param1) {
    synchronized (this) {
      return super.equals_(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.firstElement()
   */
  public float firstElement() {
    synchronized (this) {
      return super.firstElement();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.get(int param1)
   */
  public float get(int param1) {
    synchronized (this) {
      return super.get(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.hashCode()
   */
  public int hashCode() {
    synchronized (this) {
      return super.hashCode();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.indexOf(float param1, int param2)
   */
  public int indexOf(float param1, int param2) {
    synchronized (this) {
      return super.indexOf(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.insertElementAt(float param1, int param2)
   */
  public void insertElementAt(float param1, int param2) {
    synchronized (this) {
      super.insertElementAt(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.lastElement()
   */
  public float lastElement() {
    synchronized (this) {
      return super.lastElement();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.lastIndexOf(float param1, int param2)
   */
  public int lastIndexOf(float param1, int param2) {
    synchronized (this) {
      return super.lastIndexOf(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.removeAllElements()
   */
  public void removeAllElements() {
    synchronized (this) {
      super.removeAllElements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.removeAt(int param1)
   */
  public float removeAt(int param1) {
    synchronized (this) {
      return super.removeAt(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.removeElement(float param1)
   */
  public boolean removeElement(float param1) {
    synchronized (this) {
      return super.removeElement(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.removeElementAt(int param1)
   */
  public void removeElementAt(int param1) {
    synchronized (this) {
      super.removeElementAt(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.removeLastElement()
   */
  public float removeLastElement() {
    synchronized (this) {
      return super.removeLastElement();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.removeRange(int param1, int param2)
   */
  public void removeRange(int param1, int param2) {
    synchronized (this) {
      super.removeRange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.set(int param1, float param2)
   */
  public float set(int param1, float param2) {
    synchronized (this) {
      return super.set(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.setAll(int param1, com.sap.engine.lib.util.ArrayFloat param2, int param3, int param4)
   */
  public void setAll(int param1, com.sap.engine.lib.util.ArrayFloat param2, int param3, int param4) {
    synchronized (this) {
      super.setAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.setAll(int param1, float[] param2, int param3, int param4)
   */
  public void setAll(int param1, float[] param2, int param3, int param4) {
    synchronized (this) {
      super.setAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.setElementAt(float param1, int param2)
   */
  public void setElementAt(float param1, int param2) {
    synchronized (this) {
      super.setElementAt(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.setSize(int param1)
   */
  public void setSize(int param1) {
    synchronized (this) {
      super.setSize(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.sort()
   */
  public void sort() {
    synchronized (this) {
      super.sort();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.sort(boolean param1)
   */
  public void sort(boolean param1) {
    synchronized (this) {
      super.sort(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.sort(int param1, int param2)
   */
  public void sort(int param1, int param2) {
    synchronized (this) {
      super.sort(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.sort(int param1, int param2, boolean param3)
   */
  public void sort(int param1, int param2, boolean param3) {
    synchronized (this) {
      super.sort(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.toArray()
   */
  public float[] toArray() {
    synchronized (this) {
      return super.toArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.trimToSize()
   */
  public void trimToSize() {
    synchronized (this) {
      super.trimToSize();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayFloat.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

