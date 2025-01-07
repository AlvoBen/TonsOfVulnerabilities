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
 * This class is thread safe implementation of ArrayDouble
 * @see com.sap.engine.lib.util.ArrayDouble
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentArrayDouble extends ArrayDouble {

  static final long serialVersionUID = -1792441659188808225L;

  public ConcurrentArrayDouble() {
    super();
  }

  public ConcurrentArrayDouble(double[] param1, int param2, int param3, int param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentArrayDouble(double[] param1) {
    super(param1);
  }

  public ConcurrentArrayDouble(com.sap.engine.lib.util.ArrayDouble param1, int param2, int param3, int param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentArrayDouble(com.sap.engine.lib.util.ArrayDouble param1) {
    super(param1);
  }

  public ConcurrentArrayDouble(int param1) {
    super(param1);
  }

  public ConcurrentArrayDouble(int param1, int param2) {
    super(param1, param2);
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.add(double param1)
   */
  public void add(double param1) {
    synchronized (this) {
      super.add(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.addAll(com.sap.engine.lib.util.ArrayDouble param1, int param2, int param3)
   */
  public void addAll(com.sap.engine.lib.util.ArrayDouble param1, int param2, int param3) {
    synchronized (this) {
      super.addAll(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.addAll(double[] param1, int param2, int param3)
   */
  public void addAll(double[] param1, int param2, int param3) {
    synchronized (this) {
      super.addAll(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.addAll(int param1, com.sap.engine.lib.util.ArrayDouble param2, int param3, int param4)
   */
  public void addAll(int param1, com.sap.engine.lib.util.ArrayDouble param2, int param3, int param4) {
    synchronized (this) {
      super.addAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.addAll(int param1, double[] param2, int param3, int param4)
   */
  public void addAll(int param1, double[] param2, int param3, int param4) {
    synchronized (this) {
      super.addAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.addElement(double param1)
   */
  public void addElement(double param1) {
    synchronized (this) {
      super.addElement(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.binarySearch(double param1)
   */
  public int binarySearch(double param1) {
    synchronized (this) {
      return super.binarySearch(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.binarySearch(double param1, boolean param2)
   */
  public int binarySearch(double param1, boolean param2) {
    synchronized (this) {
      return super.binarySearch(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.binarySearch(double param1, int param2, int param3)
   */
  public int binarySearch(double param1, int param2, int param3) {
    synchronized (this) {
      return super.binarySearch(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.binarySearch(double param1, int param2, int param3, boolean param4)
   */
  public int binarySearch(double param1, int param2, int param3, boolean param4) {
    synchronized (this) {
      return super.binarySearch(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.capacity()
   */
  public int capacity() {
    synchronized (this) {
      return super.capacity();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.copyInto(double[] param1)
   */
  public void copyInto(double[] param1) {
    synchronized (this) {
      super.copyInto(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.copyInto(int param1, double[] param2, int param3, int param4)
   */
  public void copyInto(int param1, double[] param2, int param3, int param4) {
    synchronized (this) {
      super.copyInto(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.elementAt(int param1)
   */
  public double elementAt(int param1) {
    synchronized (this) {
      return super.elementAt(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.elements()
   */
  public com.sap.engine.lib.util.EnumerationDouble elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.equals(Object param1)
   */
  public boolean equals(Object param1) {
    synchronized (this) {
      return super.equals(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.equals_(com.sap.engine.lib.util.ArrayDouble param1)
   */
  protected boolean equals_(com.sap.engine.lib.util.ArrayDouble param1) {
    synchronized (this) {
      return super.equals_(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.firstElement()
   */
  public double firstElement() {
    synchronized (this) {
      return super.firstElement();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.get(int param1)
   */
  public double get(int param1) {
    synchronized (this) {
      return super.get(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.hashCode()
   */
  public int hashCode() {
    synchronized (this) {
      return super.hashCode();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.indexOf(double param1, int param2)
   */
  public int indexOf(double param1, int param2) {
    synchronized (this) {
      return super.indexOf(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.insertElementAt(double param1, int param2)
   */
  public void insertElementAt(double param1, int param2) {
    synchronized (this) {
      super.insertElementAt(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.lastElement()
   */
  public double lastElement() {
    synchronized (this) {
      return super.lastElement();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.lastIndexOf(double param1, int param2)
   */
  public int lastIndexOf(double param1, int param2) {
    synchronized (this) {
      return super.lastIndexOf(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.removeAllElements()
   */
  public void removeAllElements() {
    synchronized (this) {
      super.removeAllElements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.removeAt(int param1)
   */
  public double removeAt(int param1) {
    synchronized (this) {
      return super.removeAt(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.removeElement(double param1)
   */
  public boolean removeElement(double param1) {
    synchronized (this) {
      return super.removeElement(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.removeElementAt(int param1)
   */
  public void removeElementAt(int param1) {
    synchronized (this) {
      super.removeElementAt(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.removeLastElement()
   */
  public double removeLastElement() {
    synchronized (this) {
      return super.removeLastElement();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.removeRange(int param1, int param2)
   */
  public void removeRange(int param1, int param2) {
    synchronized (this) {
      super.removeRange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.set(int param1, double param2)
   */
  public double set(int param1, double param2) {
    synchronized (this) {
      return super.set(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.setAll(int param1, com.sap.engine.lib.util.ArrayDouble param2, int param3, int param4)
   */
  public void setAll(int param1, com.sap.engine.lib.util.ArrayDouble param2, int param3, int param4) {
    synchronized (this) {
      super.setAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.setAll(int param1, double[] param2, int param3, int param4)
   */
  public void setAll(int param1, double[] param2, int param3, int param4) {
    synchronized (this) {
      super.setAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.setElementAt(double param1, int param2)
   */
  public void setElementAt(double param1, int param2) {
    synchronized (this) {
      super.setElementAt(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.setSize(int param1)
   */
  public void setSize(int param1) {
    synchronized (this) {
      super.setSize(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.sort()
   */
  public void sort() {
    synchronized (this) {
      super.sort();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.sort(boolean param1)
   */
  public void sort(boolean param1) {
    synchronized (this) {
      super.sort(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.sort(int param1, int param2)
   */
  public void sort(int param1, int param2) {
    synchronized (this) {
      super.sort(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.sort(int param1, int param2, boolean param3)
   */
  public void sort(int param1, int param2, boolean param3) {
    synchronized (this) {
      super.sort(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.toArray()
   */
  public double[] toArray() {
    synchronized (this) {
      return super.toArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.trimToSize()
   */
  public void trimToSize() {
    synchronized (this) {
      super.trimToSize();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayDouble.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

