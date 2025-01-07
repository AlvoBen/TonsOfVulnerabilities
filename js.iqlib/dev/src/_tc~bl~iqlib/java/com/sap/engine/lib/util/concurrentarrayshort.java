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
 * This class is thread safe implementation of ArrayShort
 * @see com.sap.engine.lib.util.ArrayShort
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentArrayShort extends ArrayShort {

  static final long serialVersionUID = -2345587234545692804L;

  public ConcurrentArrayShort() {
    super();
  }

  public ConcurrentArrayShort(short[] param1, int param2, int param3, int param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentArrayShort(short[] param1) {
    super(param1);
  }

  public ConcurrentArrayShort(int param1) {
    super(param1);
  }

  public ConcurrentArrayShort(com.sap.engine.lib.util.ArrayShort param1, int param2, int param3, int param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentArrayShort(com.sap.engine.lib.util.ArrayShort param1) {
    super(param1);
  }

  public ConcurrentArrayShort(int param1, int param2) {
    super(param1, param2);
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.add(short param1)
   */
  public void add(short param1) {
    synchronized (this) {
      super.add(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.addAll(com.sap.engine.lib.util.ArrayShort param1, int param2, int param3)
   */
  public void addAll(com.sap.engine.lib.util.ArrayShort param1, int param2, int param3) {
    synchronized (this) {
      super.addAll(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.addAll(int param1, com.sap.engine.lib.util.ArrayShort param2, int param3, int param4)
   */
  public void addAll(int param1, com.sap.engine.lib.util.ArrayShort param2, int param3, int param4) {
    synchronized (this) {
      super.addAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.addAll(int param1, short[] param2, int param3, int param4)
   */
  public void addAll(int param1, short[] param2, int param3, int param4) {
    synchronized (this) {
      super.addAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.addAll(short[] param1, int param2, int param3)
   */
  public void addAll(short[] param1, int param2, int param3) {
    synchronized (this) {
      super.addAll(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.addElement(short param1)
   */
  public void addElement(short param1) {
    synchronized (this) {
      super.addElement(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.binarySearch(short param1)
   */
  public int binarySearch(short param1) {
    synchronized (this) {
      return super.binarySearch(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.binarySearch(short param1, boolean param2)
   */
  public int binarySearch(short param1, boolean param2) {
    synchronized (this) {
      return super.binarySearch(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.binarySearch(short param1, int param2, int param3)
   */
  public int binarySearch(short param1, int param2, int param3) {
    synchronized (this) {
      return super.binarySearch(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.binarySearch(short param1, int param2, int param3, boolean param4)
   */
  public int binarySearch(short param1, int param2, int param3, boolean param4) {
    synchronized (this) {
      return super.binarySearch(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.capacity()
   */
  public int capacity() {
    synchronized (this) {
      return super.capacity();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.copyInto(int param1, short[] param2, int param3, int param4)
   */
  public void copyInto(int param1, short[] param2, int param3, int param4) {
    synchronized (this) {
      super.copyInto(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.copyInto(short[] param1)
   */
  public void copyInto(short[] param1) {
    synchronized (this) {
      super.copyInto(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.elementAt(int param1)
   */
  public short elementAt(int param1) {
    synchronized (this) {
      return super.elementAt(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.elements()
   */
  public com.sap.engine.lib.util.EnumerationShort elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.equals(Object param1)
   */
  public boolean equals(Object param1) {
    synchronized (this) {
      return super.equals(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.equals_(com.sap.engine.lib.util.ArrayShort param1)
   */
  protected boolean equals_(com.sap.engine.lib.util.ArrayShort param1) {
    synchronized (this) {
      return super.equals_(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.firstElement()
   */
  public short firstElement() {
    synchronized (this) {
      return super.firstElement();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.get(int param1)
   */
  public short get(int param1) {
    synchronized (this) {
      return super.get(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.hashCode()
   */
  public int hashCode() {
    synchronized (this) {
      return super.hashCode();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.indexOf(short param1, int param2)
   */
  public int indexOf(short param1, int param2) {
    synchronized (this) {
      return super.indexOf(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.insertElementAt(short param1, int param2)
   */
  public void insertElementAt(short param1, int param2) {
    synchronized (this) {
      super.insertElementAt(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.lastElement()
   */
  public short lastElement() {
    synchronized (this) {
      return super.lastElement();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.lastIndexOf(short param1, int param2)
   */
  public int lastIndexOf(short param1, int param2) {
    synchronized (this) {
      return super.lastIndexOf(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.removeAllElements()
   */
  public void removeAllElements() {
    synchronized (this) {
      super.removeAllElements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.removeAt(int param1)
   */
  public short removeAt(int param1) {
    synchronized (this) {
      return super.removeAt(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.removeElement(short param1)
   */
  public boolean removeElement(short param1) {
    synchronized (this) {
      return super.removeElement(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.removeElementAt(int param1)
   */
  public void removeElementAt(int param1) {
    synchronized (this) {
      super.removeElementAt(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.removeLastElement()
   */
  public short removeLastElement() {
    synchronized (this) {
      return super.removeLastElement();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.removeRange(int param1, int param2)
   */
  public void removeRange(int param1, int param2) {
    synchronized (this) {
      super.removeRange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.set(int param1, short param2)
   */
  public short set(int param1, short param2) {
    synchronized (this) {
      return super.set(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.setAll(int param1, com.sap.engine.lib.util.ArrayShort param2, int param3, int param4)
   */
  public void setAll(int param1, com.sap.engine.lib.util.ArrayShort param2, int param3, int param4) {
    synchronized (this) {
      super.setAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.setAll(int param1, short[] param2, int param3, int param4)
   */
  public void setAll(int param1, short[] param2, int param3, int param4) {
    synchronized (this) {
      super.setAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.setElementAt(short param1, int param2)
   */
  public void setElementAt(short param1, int param2) {
    synchronized (this) {
      super.setElementAt(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.setSize(int param1)
   */
  public void setSize(int param1) {
    synchronized (this) {
      super.setSize(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.sort()
   */
  public void sort() {
    synchronized (this) {
      super.sort();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.sort(boolean param1)
   */
  public void sort(boolean param1) {
    synchronized (this) {
      super.sort(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.sort(int param1, int param2)
   */
  public void sort(int param1, int param2) {
    synchronized (this) {
      super.sort(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.sort(int param1, int param2, boolean param3)
   */
  public void sort(int param1, int param2, boolean param3) {
    synchronized (this) {
      super.sort(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.toArray()
   */
  public short[] toArray() {
    synchronized (this) {
      return super.toArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.trimToSize()
   */
  public void trimToSize() {
    synchronized (this) {
      super.trimToSize();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayShort.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

