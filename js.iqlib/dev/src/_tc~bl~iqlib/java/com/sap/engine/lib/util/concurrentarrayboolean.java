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
 * This class is thread safe implementation of ArrayBoolean
 * @see com.sap.engine.lib.util.ArrayBoolean
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentArrayBoolean extends ArrayBoolean {

  static final long serialVersionUID = -5884689961906277540L;

  public ConcurrentArrayBoolean(int param1, int param2) {
    super(param1, param2);
  }

  public ConcurrentArrayBoolean() {
    super();
  }

  public ConcurrentArrayBoolean(boolean[] param1, int param2, int param3, int param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentArrayBoolean(boolean[] param1) {
    super(param1);
  }

  public ConcurrentArrayBoolean(com.sap.engine.lib.util.ArrayBoolean param1, int param2, int param3, int param4) {
    super(param1, param2, param3, param4);
  }

  public ConcurrentArrayBoolean(com.sap.engine.lib.util.ArrayBoolean param1) {
    super(param1);
  }

  public ConcurrentArrayBoolean(int param1) {
    super(param1);
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.add(boolean param1)
   */
  public void add(boolean param1) {
    synchronized (this) {
      super.add(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.addAll(boolean[] param1, int param2, int param3)
   */
  public void addAll(boolean[] param1, int param2, int param3) {
    synchronized (this) {
      super.addAll(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.addAll(com.sap.engine.lib.util.ArrayBoolean param1, int param2, int param3)
   */
  public void addAll(com.sap.engine.lib.util.ArrayBoolean param1, int param2, int param3) {
    synchronized (this) {
      super.addAll(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.addAll(int param1, boolean[] param2, int param3, int param4)
   */
  public void addAll(int param1, boolean[] param2, int param3, int param4) {
    synchronized (this) {
      super.addAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.addAll(int param1, com.sap.engine.lib.util.ArrayBoolean param2, int param3, int param4)
   */
  public void addAll(int param1, com.sap.engine.lib.util.ArrayBoolean param2, int param3, int param4) {
    synchronized (this) {
      super.addAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.addElement(boolean param1)
   */
  public void addElement(boolean param1) {
    synchronized (this) {
      super.addElement(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.capacity()
   */
  public int capacity() {
    synchronized (this) {
      return super.capacity();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.copyInto(boolean[] param1)
   */
  public void copyInto(boolean[] param1) {
    synchronized (this) {
      super.copyInto(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.copyInto(int param1, boolean[] param2, int param3, int param4)
   */
  public void copyInto(int param1, boolean[] param2, int param3, int param4) {
    synchronized (this) {
      super.copyInto(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.elementAt(int param1)
   */
  public boolean elementAt(int param1) {
    synchronized (this) {
      return super.elementAt(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.elements()
   */
  public com.sap.engine.lib.util.EnumerationBoolean elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.equals(Object param1)
   */
  public boolean equals(Object param1) {
    synchronized (this) {
      return super.equals(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.equals_(com.sap.engine.lib.util.ArrayBoolean param1)
   */
  protected boolean equals_(com.sap.engine.lib.util.ArrayBoolean param1) {
    synchronized (this) {
      return super.equals_(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.firstElement()
   */
  public boolean firstElement() {
    synchronized (this) {
      return super.firstElement();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.get(int param1)
   */
  public boolean get(int param1) {
    synchronized (this) {
      return super.get(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.hashCode()
   */
  public int hashCode() {
    synchronized (this) {
      return super.hashCode();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.indexOf(boolean param1, int param2)
   */
  public int indexOf(boolean param1, int param2) {
    synchronized (this) {
      return super.indexOf(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.insertElementAt(boolean param1, int param2)
   */
  public void insertElementAt(boolean param1, int param2) {
    synchronized (this) {
      super.insertElementAt(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.lastElement()
   */
  public boolean lastElement() {
    synchronized (this) {
      return super.lastElement();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.lastIndexOf(boolean param1, int param2)
   */
  public int lastIndexOf(boolean param1, int param2) {
    synchronized (this) {
      return super.lastIndexOf(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.removeAllElements()
   */
  public void removeAllElements() {
    synchronized (this) {
      super.removeAllElements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.removeAt(int param1)
   */
  public boolean removeAt(int param1) {
    synchronized (this) {
      return super.removeAt(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.removeElement(boolean param1)
   */
  public boolean removeElement(boolean param1) {
    synchronized (this) {
      return super.removeElement(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.removeElementAt(int param1)
   */
  public void removeElementAt(int param1) {
    synchronized (this) {
      super.removeElementAt(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.removeLastElement()
   */
  public boolean removeLastElement() {
    synchronized (this) {
      return super.removeLastElement();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.removeRange(int param1, int param2)
   */
  public void removeRange(int param1, int param2) {
    synchronized (this) {
      super.removeRange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.set(int param1, boolean param2)
   */
  public boolean set(int param1, boolean param2) {
    synchronized (this) {
      return super.set(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.setAll(int param1, boolean[] param2, int param3, int param4)
   */
  public void setAll(int param1, boolean[] param2, int param3, int param4) {
    synchronized (this) {
      super.setAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.setAll(int param1, com.sap.engine.lib.util.ArrayBoolean param2, int param3, int param4)
   */
  public void setAll(int param1, com.sap.engine.lib.util.ArrayBoolean param2, int param3, int param4) {
    synchronized (this) {
      super.setAll(param1, param2, param3, param4);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.setElementAt(boolean param1, int param2)
   */
  public void setElementAt(boolean param1, int param2) {
    synchronized (this) {
      super.setElementAt(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.setSize(int param1)
   */
  public void setSize(int param1) {
    synchronized (this) {
      super.setSize(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.toArray()
   */
  public boolean[] toArray() {
    synchronized (this) {
      return super.toArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.trimToSize()
   */
  public void trimToSize() {
    synchronized (this) {
      super.trimToSize();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.ArrayBoolean.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

