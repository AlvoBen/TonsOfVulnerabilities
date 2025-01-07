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
 * This class is thread safe implementation of ArrayObject
 * @see com.sap.engine.lib.util.ArrayObject
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentArrayObject extends ArrayObject {

  static final long serialVersionUID = -2692072743599767581L;

  public ConcurrentArrayObject(com.sap.engine.lib.util.ArrayObject param1, int param2, int param3, int param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentArrayObject() {
    super();
  }

  public ConcurrentArrayObject(Object[] param1, int param2, int param3, int param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentArrayObject(Object[] param1) {
    super(param1);
  }

  public ConcurrentArrayObject(com.sap.engine.lib.util.ArrayObject param1) {
    super(param1);
  }

  public ConcurrentArrayObject(int param1) {
    super(param1);
  }

  public ConcurrentArrayObject(int param1, int param2) {
    super(param1, param2);
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.add(Object param1)
   */
  public void add(Object param1) {
    synchronized (this) {
      super.add(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.addAll(com.sap.engine.lib.util.ArrayObject param1, int param2, int param3)
   */
  public void addAll(com.sap.engine.lib.util.ArrayObject param1, int param2, int param3) {
    synchronized (this) {
      super.addAll(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.addAll(int param1, com.sap.engine.lib.util.ArrayObject param2, int param3, int param4)
   */
  public void addAll(int param1, com.sap.engine.lib.util.ArrayObject param2, int param3, int param4) {
    synchronized (this) {
      super.addAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.addAll(int param1, Object[] param2, int param3, int param4)
   */
  public void addAll(int param1, Object[] param2, int param3, int param4) {
    synchronized (this) {
      super.addAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.addAll(Object[] param1, int param2, int param3)
   */
  public void addAll(Object[] param1, int param2, int param3) {
    synchronized (this) {
      super.addAll(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.addElement(Object param1)
   */
  public void addElement(Object param1) {
    synchronized (this) {
      super.addElement(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.binarySearch(Object param1)
   */
  public int binarySearch(Object param1) {
    synchronized (this) {
      return super.binarySearch(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.binarySearch(Object param1, boolean param2)
   */
  public int binarySearch(Object param1, boolean param2) {
    synchronized (this) {
      return super.binarySearch(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.binarySearch(Object param1, int param2, int param3)
   */
  public int binarySearch(Object param1, int param2, int param3) {
    synchronized (this) {
      return super.binarySearch(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.binarySearch(Object param1, int param2, int param3, boolean param4)
   */
  public int binarySearch(Object param1, int param2, int param3, boolean param4) {
    synchronized (this) {
      return super.binarySearch(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.capacity()
   */
  public int capacity() {
    synchronized (this) {
      return super.capacity();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.copyInto(int param1, Object[] param2, int param3, int param4)
   */
  public void copyInto(int param1, Object[] param2, int param3, int param4) {
    synchronized (this) {
      super.copyInto(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.copyInto(Object[] param1)
   */
  public void copyInto(Object[] param1) {
    synchronized (this) {
      super.copyInto(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.deepClone()
   */
  public Object deepClone() {
    synchronized (this) {
      return super.deepClone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.elementAt(int param1)
   */
  public Object elementAt(int param1) {
    synchronized (this) {
      return super.elementAt(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.elementsIterator()
   */
  public com.sap.engine.lib.util.iterators.RootIterator elementsIterator() {
    synchronized (this) {
      return super.elementsIterator();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.equals(Object param1)
   */
  public boolean equals(Object param1) {
    synchronized (this) {
      return super.equals(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.equals_(com.sap.engine.lib.util.ArrayObject param1)
   */
  protected boolean equals_(com.sap.engine.lib.util.ArrayObject param1) {
    synchronized (this) {
      return super.equals_(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.firstElement()
   */
  public Object firstElement() {
    synchronized (this) {
      return super.firstElement();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.get(int param1)
   */
  public Object get(int param1) {
    synchronized (this) {
      return super.get(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.hashCode()
   */
  public int hashCode() {
    synchronized (this) {
      return super.hashCode();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.indexOf(Object param1, int param2)
   */
  public int indexOf(Object param1, int param2) {
    synchronized (this) {
      return super.indexOf(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.insertElementAt(Object param1, int param2)
   */
  public void insertElementAt(Object param1, int param2) {
    synchronized (this) {
      super.insertElementAt(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.iterAdd(Object param1, int param2)
   */
  protected Object iterAdd(Object param1, int param2) {
    synchronized (this) {
      return super.iterAdd(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.iterChange(Object param1, int param2)
   */
  protected Object iterChange(Object param1, int param2) {
    synchronized (this) {
      return super.iterChange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.iterGet(int param1)
   */
  protected Object iterGet(int param1) {
    synchronized (this) {
      return super.iterGet(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.iterRemove(int param1)
   */
  protected Object iterRemove(int param1) {
    synchronized (this) {
      return super.iterRemove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.lastElement()
   */
  public Object lastElement() {
    synchronized (this) {
      return super.lastElement();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.lastIndexOf(Object param1, int param2)
   */
  public int lastIndexOf(Object param1, int param2) {
    synchronized (this) {
      return super.lastIndexOf(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.removeAllElements()
   */
  public void removeAllElements() {
    synchronized (this) {
      super.removeAllElements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.removeAt(int param1)
   */
  public Object removeAt(int param1) {
    synchronized (this) {
      return super.removeAt(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.removeElement(Object param1)
   */
  public boolean removeElement(Object param1) {
    synchronized (this) {
      return super.removeElement(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.removeElementAt(int param1)
   */
  public void removeElementAt(int param1) {
    synchronized (this) {
      super.removeElementAt(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.removeLastElement()
   */
  public Object removeLastElement() {
    synchronized (this) {
      return super.removeLastElement();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.removeRange(int param1, int param2)
   */
  public void removeRange(int param1, int param2) {
    synchronized (this) {
      super.removeRange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.set(int param1, Object param2)
   */
  public Object set(int param1, Object param2) {
    synchronized (this) {
      return super.set(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.setAll(int param1, com.sap.engine.lib.util.ArrayObject param2, int param3, int param4)
   */
  public void setAll(int param1, com.sap.engine.lib.util.ArrayObject param2, int param3, int param4) {
    synchronized (this) {
      super.setAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.setAll(int param1, Object[] param2, int param3, int param4)
   */
  public void setAll(int param1, Object[] param2, int param3, int param4) {
    synchronized (this) {
      super.setAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.setElementAt(Object param1, int param2)
   */
  public void setElementAt(Object param1, int param2) {
    synchronized (this) {
      super.setElementAt(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.setSize(int param1)
   */
  public void setSize(int param1) {
    synchronized (this) {
      super.setSize(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.sort()
   */
  public void sort() {
    synchronized (this) {
      super.sort();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.sort(boolean param1)
   */
  public void sort(boolean param1) {
    synchronized (this) {
      super.sort(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.sort(int param1, int param2)
   */
  public void sort(int param1, int param2) {
    synchronized (this) {
      super.sort(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.sort(int param1, int param2, boolean param3)
   */
  public void sort(int param1, int param2, boolean param3) {
    synchronized (this) {
      super.sort(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.toArray()
   */
  public Object[] toArray() {
    synchronized (this) {
      return super.toArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.trimToSize()
   */
  public void trimToSize() {
    synchronized (this) {
      super.trimToSize();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayObject.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

