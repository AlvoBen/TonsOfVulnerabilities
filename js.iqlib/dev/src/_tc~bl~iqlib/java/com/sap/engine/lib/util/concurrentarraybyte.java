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
 * This class is thread safe implementation of ArrayByte
 * @see com.sap.engine.lib.util.ArrayByte
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentArrayByte extends ArrayByte {

  static final long serialVersionUID = -1283678949048807930L;

  public ConcurrentArrayByte() {
    super();
  }

  public ConcurrentArrayByte(com.sap.engine.lib.util.ArrayByte param1, int param2, int param3, int param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentArrayByte(byte[] param1) {
    super(param1);
  }

  public ConcurrentArrayByte(int param1) {
    super(param1);
  }

  public ConcurrentArrayByte(byte[] param1, int param2, int param3, int param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentArrayByte(com.sap.engine.lib.util.ArrayByte param1) {
    super(param1);
  }

  public ConcurrentArrayByte(int param1, int param2) {
    super(param1, param2);
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.add(byte param1)
   */
  public void add(byte param1) {
    synchronized (this) {
      super.add(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.addAll(byte[] param1, int param2, int param3)
   */
  public void addAll(byte[] param1, int param2, int param3) {
    synchronized (this) {
      super.addAll(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.addAll(com.sap.engine.lib.util.ArrayByte param1, int param2, int param3)
   */
  public void addAll(com.sap.engine.lib.util.ArrayByte param1, int param2, int param3) {
    synchronized (this) {
      super.addAll(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.addAll(int param1, byte[] param2, int param3, int param4)
   */
  public void addAll(int param1, byte[] param2, int param3, int param4) {
    synchronized (this) {
      super.addAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.addAll(int param1, com.sap.engine.lib.util.ArrayByte param2, int param3, int param4)
   */
  public void addAll(int param1, com.sap.engine.lib.util.ArrayByte param2, int param3, int param4) {
    synchronized (this) {
      super.addAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.addElement(byte param1)
   */
  public void addElement(byte param1) {
    synchronized (this) {
      super.addElement(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.binarySearch(byte param1)
   */
  public int binarySearch(byte param1) {
    synchronized (this) {
      return super.binarySearch(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.binarySearch(byte param1, boolean param2)
   */
  public int binarySearch(byte param1, boolean param2) {
    synchronized (this) {
      return super.binarySearch(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.binarySearch(byte param1, int param2, int param3)
   */
  public int binarySearch(byte param1, int param2, int param3) {
    synchronized (this) {
      return super.binarySearch(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.binarySearch(byte param1, int param2, int param3, boolean param4)
   */
  public int binarySearch(byte param1, int param2, int param3, boolean param4) {
    synchronized (this) {
      return super.binarySearch(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.capacity()
   */
  public int capacity() {
    synchronized (this) {
      return super.capacity();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.copyInto(byte[] param1)
   */
  public void copyInto(byte[] param1) {
    synchronized (this) {
      super.copyInto(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.copyInto(int param1, byte[] param2, int param3, int param4)
   */
  public void copyInto(int param1, byte[] param2, int param3, int param4) {
    synchronized (this) {
      super.copyInto(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.elementAt(int param1)
   */
  public byte elementAt(int param1) {
    synchronized (this) {
      return super.elementAt(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.elements()
   */
  public com.sap.engine.lib.util.EnumerationByte elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.equals(Object param1)
   */
  public boolean equals(Object param1) {
    synchronized (this) {
      return super.equals(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.equals_(com.sap.engine.lib.util.ArrayByte param1)
   */
  protected boolean equals_(com.sap.engine.lib.util.ArrayByte param1) {
    synchronized (this) {
      return super.equals_(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.firstElement()
   */
  public byte firstElement() {
    synchronized (this) {
      return super.firstElement();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.get(int param1)
   */
  public byte get(int param1) {
    synchronized (this) {
      return super.get(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.hashCode()
   */
  public int hashCode() {
    synchronized (this) {
      return super.hashCode();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.indexOf(byte param1, int param2)
   */
  public int indexOf(byte param1, int param2) {
    synchronized (this) {
      return super.indexOf(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.insertElementAt(byte param1, int param2)
   */
  public void insertElementAt(byte param1, int param2) {
    synchronized (this) {
      super.insertElementAt(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.lastElement()
   */
  public byte lastElement() {
    synchronized (this) {
      return super.lastElement();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.lastIndexOf(byte param1, int param2)
   */
  public int lastIndexOf(byte param1, int param2) {
    synchronized (this) {
      return super.lastIndexOf(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.removeAllElements()
   */
  public void removeAllElements() {
    synchronized (this) {
      super.removeAllElements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.removeAt(int param1)
   */
  public byte removeAt(int param1) {
    synchronized (this) {
      return super.removeAt(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.removeElement(byte param1)
   */
  public boolean removeElement(byte param1) {
    synchronized (this) {
      return super.removeElement(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.removeElementAt(int param1)
   */
  public void removeElementAt(int param1) {
    synchronized (this) {
      super.removeElementAt(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.removeLastElement()
   */
  public byte removeLastElement() {
    synchronized (this) {
      return super.removeLastElement();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.removeRange(int param1, int param2)
   */
  public void removeRange(int param1, int param2) {
    synchronized (this) {
      super.removeRange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.set(int param1, byte param2)
   */
  public byte set(int param1, byte param2) {
    synchronized (this) {
      return super.set(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.setAll(int param1, byte[] param2, int param3, int param4)
   */
  public void setAll(int param1, byte[] param2, int param3, int param4) {
    synchronized (this) {
      super.setAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.setAll(int param1, com.sap.engine.lib.util.ArrayByte param2, int param3, int param4)
   */
  public void setAll(int param1, com.sap.engine.lib.util.ArrayByte param2, int param3, int param4) {
    synchronized (this) {
      super.setAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.setElementAt(byte param1, int param2)
   */
  public void setElementAt(byte param1, int param2) {
    synchronized (this) {
      super.setElementAt(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.setSize(int param1)
   */
  public void setSize(int param1) {
    synchronized (this) {
      super.setSize(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.sort()
   */
  public void sort() {
    synchronized (this) {
      super.sort();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.sort(boolean param1)
   */
  public void sort(boolean param1) {
    synchronized (this) {
      super.sort(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.sort(int param1, int param2)
   */
  public void sort(int param1, int param2) {
    synchronized (this) {
      super.sort(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.sort(int param1, int param2, boolean param3)
   */
  public void sort(int param1, int param2, boolean param3) {
    synchronized (this) {
      super.sort(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.toArray()
   */
  public byte[] toArray() {
    synchronized (this) {
      return super.toArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.trimToSize()
   */
  public void trimToSize() {
    synchronized (this) {
      super.trimToSize();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayByte.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

