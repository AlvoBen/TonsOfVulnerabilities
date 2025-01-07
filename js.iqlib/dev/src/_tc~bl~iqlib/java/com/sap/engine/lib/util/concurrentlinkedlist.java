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
 * This class is thread safe implementation of LinkedList
 * @see com.sap.engine.lib.util.LinkedList
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentLinkedList extends LinkedList {

  static final long serialVersionUID = -1968188699384060661L;
  public ConcurrentLinkedList() {
    super();
  }

  public ConcurrentLinkedList(int param1) {
    super(param1);
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.add(int param1, Object param2)
   */
  public com.sap.engine.lib.util.NextItemPointer add(int param1, Object param2) {
    synchronized (this) {
      return super.add(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.addAfterPointer(com.sap.engine.lib.util.base.NextItem param1, Object param2)
   */
  public com.sap.engine.lib.util.NextItemPointer addAfterPointer(com.sap.engine.lib.util.base.NextItem param1, Object param2) {
    synchronized (this) {
      return super.addAfterPointer(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.addFirst(Object param1)
   */
  public com.sap.engine.lib.util.NextItemPointer addFirst(Object param1) {
    synchronized (this) {
      return super.addFirst(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.addLast(Object param1)
   */
  public com.sap.engine.lib.util.NextItemPointer addLast(Object param1) {
    synchronized (this) {
      return super.addLast(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.contains(Object param1)
   */
  public boolean contains(Object param1) {
    synchronized (this) {
      return super.contains(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.elementsIterator()
   */
  public com.sap.engine.lib.util.iterators.RootIterator elementsIterator() {
    synchronized (this) {
      return super.elementsIterator();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.get(Object param1)
   */
  public Object get(Object param1) {
    synchronized (this) {
      return super.get(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.indexOf(Object param1)
   */
  public int indexOf(Object param1) {
    synchronized (this) {
      return super.indexOf(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.iterChange(Object param1, com.sap.engine.lib.util.base.NextItem param2, com.sap.engine.lib.util.base.NextItem param3)
   */
  protected Object iterChange(Object param1, com.sap.engine.lib.util.base.NextItem param2, com.sap.engine.lib.util.base.NextItem param3) {
    synchronized (this) {
      return super.iterChange(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.lastIndexOf(Object param1)
   */
  public int lastIndexOf(Object param1) {
    synchronized (this) {
      return super.lastIndexOf(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.remove(Object param1)
   */
  public Object remove(Object param1) {
    synchronized (this) {
      return super.remove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.removeAfter(com.sap.engine.lib.util.base.NextItem param1)
   */
  public void removeAfter(com.sap.engine.lib.util.base.NextItem param1) {
    synchronized (this) {
      super.removeAfter(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.removeAll(Object param1)
   */
  public int removeAll(Object param1) {
    synchronized (this) {
      return super.removeAll(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.removeRange(com.sap.engine.lib.util.base.NextItem param1, com.sap.engine.lib.util.base.NextItem param2)
   */
  public void removeRange(com.sap.engine.lib.util.base.NextItem param1, com.sap.engine.lib.util.base.NextItem param2) {
    synchronized (this) {
      super.removeRange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.removeRange(int param1, int param2)
   */
  public boolean removeRange(int param1, int param2) {
    synchronized (this) {
      return super.removeRange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.removeSublist(int param1)
   */
  public boolean removeSublist(int param1) {
    synchronized (this) {
      return super.removeSublist(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.sublist(int param1)
   */
  public com.sap.engine.lib.util.base.BaseLinkedList sublist(int param1) {
    synchronized (this) {
      return super.sublist(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.sublist(int param1, int param2)
   */
  public com.sap.engine.lib.util.base.BaseLinkedList sublist(int param1, int param2) {
    synchronized (this) {
      return super.sublist(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.toArray()
   */
  public Object[] toArray() {
    synchronized (this) {
      return super.toArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.toPointerArray()
   */
  public com.sap.engine.lib.util.base.Pointer[] toPointerArray() {
    synchronized (this) {
      return super.toPointerArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.addAfter(com.sap.engine.lib.util.base.NextItem param1, com.sap.engine.lib.util.base.NextItem param2)
   */
  public boolean addAfter(com.sap.engine.lib.util.base.NextItem param1, com.sap.engine.lib.util.base.NextItem param2) {
    synchronized (this) {
      return super.addAfter(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.addFirstItem(com.sap.engine.lib.util.base.NextItem param1)
   */
  public boolean addFirstItem(com.sap.engine.lib.util.base.NextItem param1) {
    synchronized (this) {
      return super.addFirstItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.addItem(int param1, com.sap.engine.lib.util.base.NextItem param2)
   */
  public boolean addItem(int param1, com.sap.engine.lib.util.base.NextItem param2) {
    synchronized (this) {
      return super.addItem(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.addLastItem(com.sap.engine.lib.util.base.NextItem param1)
   */
  public boolean addLastItem(com.sap.engine.lib.util.base.NextItem param1) {
    synchronized (this) {
      return super.addLastItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.containsItem(com.sap.engine.lib.util.base.NextItem param1)
   */
  public boolean containsItem(com.sap.engine.lib.util.base.NextItem param1) {
    synchronized (this) {
      return super.containsItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.getFirstItem()
   */
  public com.sap.engine.lib.util.base.NextItem getFirstItem() {
    synchronized (this) {
      return super.getFirstItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.getItem(com.sap.engine.lib.util.base.NextItem param1)
   */
  public com.sap.engine.lib.util.base.NextItem getItem(com.sap.engine.lib.util.base.NextItem param1) {
    synchronized (this) {
      return super.getItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.getItem(int param1)
   */
  public com.sap.engine.lib.util.base.NextItem getItem(int param1) {
    synchronized (this) {
      return super.getItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.getLastItem()
   */
  public com.sap.engine.lib.util.base.NextItem getLastItem() {
    synchronized (this) {
      return super.getLastItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.getLimit()
   */
  public int getLimit() {
    synchronized (this) {
      return super.getLimit();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.indexOfItem(com.sap.engine.lib.util.base.NextItem param1)
   */
  public int indexOfItem(com.sap.engine.lib.util.base.NextItem param1) {
    synchronized (this) {
      return super.indexOfItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.isFull()
   */
  public boolean isFull() {
    synchronized (this) {
      return super.isFull();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.iterAdd(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2)
   */
  protected com.sap.engine.lib.util.base.NextItem iterAdd(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2) {
    synchronized (this) {
      return super.iterAdd(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.iterChange(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2)
   */
  protected com.sap.engine.lib.util.base.NextItem iterChange(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2) {
    synchronized (this) {
      return super.iterChange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.iterGet(com.sap.engine.lib.util.iterators.RootIterator param1)
   */
  protected com.sap.engine.lib.util.base.NextItem iterGet(com.sap.engine.lib.util.iterators.RootIterator param1) {
    synchronized (this) {
      return super.iterGet(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.iterInsert(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2)
   */
  protected com.sap.engine.lib.util.base.NextItem iterInsert(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2) {
    synchronized (this) {
      return super.iterInsert(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.iterNext(com.sap.engine.lib.util.iterators.RootIterator param1, int param2)
   */
  protected com.sap.engine.lib.util.base.NextItem iterNext(com.sap.engine.lib.util.iterators.RootIterator param1, int param2) {
    synchronized (this) {
      return super.iterNext(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.iterRemove(com.sap.engine.lib.util.iterators.RootIterator param1)
   */
  protected com.sap.engine.lib.util.base.NextItem iterRemove(com.sap.engine.lib.util.iterators.RootIterator param1) {
    synchronized (this) {
      return super.iterRemove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.iterSize(com.sap.engine.lib.util.iterators.RootIterator param1)
   */
  protected int iterSize(com.sap.engine.lib.util.iterators.RootIterator param1) {
    synchronized (this) {
      return super.iterSize(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.lastIndexOfItem(com.sap.engine.lib.util.base.NextItem param1)
   */
  public int lastIndexOfItem(com.sap.engine.lib.util.base.NextItem param1) {
    synchronized (this) {
      return super.lastIndexOfItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.removeAllItems(com.sap.engine.lib.util.base.NextItem param1)
   */
  public int removeAllItems(com.sap.engine.lib.util.base.NextItem param1) {
    synchronized (this) {
      return super.removeAllItems(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.removeFirstItem()
   */
  public com.sap.engine.lib.util.base.NextItem removeFirstItem() {
    synchronized (this) {
      return super.removeFirstItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.removeItem(com.sap.engine.lib.util.base.NextItem param1)
   */
  public com.sap.engine.lib.util.base.NextItem removeItem(com.sap.engine.lib.util.base.NextItem param1) {
    synchronized (this) {
      return super.removeItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.removeItem(int param1)
   */
  public com.sap.engine.lib.util.base.NextItem removeItem(int param1) {
    synchronized (this) {
      return super.removeItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.removeLastItem()
   */
  public com.sap.engine.lib.util.base.NextItem removeLastItem() {
    synchronized (this) {
      return super.removeLastItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.replaceAfter(com.sap.engine.lib.util.base.NextItem param1, com.sap.engine.lib.util.base.NextItem param2)
   */
  public com.sap.engine.lib.util.base.NextItem replaceAfter(com.sap.engine.lib.util.base.NextItem param1, com.sap.engine.lib.util.base.NextItem param2) {
    synchronized (this) {
      return super.replaceAfter(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.setItem(int param1, com.sap.engine.lib.util.base.NextItem param2)
   */
  public com.sap.engine.lib.util.base.NextItem setItem(int param1, com.sap.engine.lib.util.base.NextItem param2) {
    synchronized (this) {
      return super.setItem(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.setLimit(int param1)
   */
  public void setLimit(int param1) {
    synchronized (this) {
      super.setLimit(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.toItemArray()
   */
  public com.sap.engine.lib.util.base.NextItem[] toItemArray() {
    synchronized (this) {
      return super.toItemArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.LinkedList.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

