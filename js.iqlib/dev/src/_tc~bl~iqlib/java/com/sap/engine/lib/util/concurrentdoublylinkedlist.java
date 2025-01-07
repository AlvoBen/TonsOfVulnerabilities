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
 * This class is thread safe implementation of DoublyLinkedList
 * @see com.sap.engine.lib.util.DoublyLinkedList
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentDoublyLinkedList extends DoublyLinkedList {

  static final long serialVersionUID = 6636933688306955254L;

  public ConcurrentDoublyLinkedList(int param1) {
    super(param1);
  }

  public ConcurrentDoublyLinkedList() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.addAfterPointer(com.sap.engine.lib.util.base.LinearItem param1, Object param2)
   */
  public com.sap.engine.lib.util.LinearItemPointer addAfterPointer(com.sap.engine.lib.util.base.LinearItem param1, Object param2) {
    synchronized (this) {
      return super.addAfterPointer(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.addBeforePointer(com.sap.engine.lib.util.base.LinearItem param1, Object param2)
   */
  public com.sap.engine.lib.util.LinearItemPointer addBeforePointer(com.sap.engine.lib.util.base.LinearItem param1, Object param2) {
    synchronized (this) {
      return super.addBeforePointer(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.addFirst(Object param1)
   */
  public com.sap.engine.lib.util.LinearItemPointer addFirst(Object param1) {
    synchronized (this) {
      return super.addFirst(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.addLast(Object param1)
   */
  public com.sap.engine.lib.util.LinearItemPointer addLast(Object param1) {
    synchronized (this) {
      return super.addLast(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.contains(Object param1)
   */
  public boolean contains(Object param1) {
    synchronized (this) {
      return super.contains(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.elementsIterator()
   */
  public com.sap.engine.lib.util.iterators.RootIterator elementsIterator() {
    synchronized (this) {
      return super.elementsIterator();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.get(Object param1)
   */
  public Object get(Object param1) {
    synchronized (this) {
      return super.get(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.indexOf(Object param1)
   */
  public int indexOf(Object param1) {
    synchronized (this) {
      return super.indexOf(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.iterChange(Object param1, com.sap.engine.lib.util.base.LinearItem param2, com.sap.engine.lib.util.base.LinearItem param3)
   */
  protected Object iterChange(Object param1, com.sap.engine.lib.util.base.LinearItem param2, com.sap.engine.lib.util.base.LinearItem param3) {
    synchronized (this) {
      return super.iterChange(param1, param2, param3);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.lastIndexOf(Object param1)
   */
  public int lastIndexOf(Object param1) {
    synchronized (this) {
      return super.lastIndexOf(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.removeAll(Object param1)
   */
  public int removeAll(Object param1) {
    synchronized (this) {
      return super.removeAll(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.removeRange(com.sap.engine.lib.util.base.LinearItem param1, com.sap.engine.lib.util.base.LinearItem param2)
   */
  public void removeRange(com.sap.engine.lib.util.base.LinearItem param1, com.sap.engine.lib.util.base.LinearItem param2) {
    synchronized (this) {
      super.removeRange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.removeRange(int param1, int param2)
   */
  public boolean removeRange(int param1, int param2) {
    synchronized (this) {
      return super.removeRange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.removeSublist(int param1)
   */
  public boolean removeSublist(int param1) {
    synchronized (this) {
      return super.removeSublist(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.set(int param1, Object param2)
   */
  public Object set(int param1, Object param2) {
    synchronized (this) {
      return super.set(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.sublist(int param1)
   */
  public com.sap.engine.lib.util.base.BaseDoublyLinkedList sublist(int param1) {
    synchronized (this) {
      return super.sublist(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.sublist(int param1, int param2)
   */
  public com.sap.engine.lib.util.base.BaseDoublyLinkedList sublist(int param1, int param2) {
    synchronized (this) {
      return super.sublist(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.toArray()
   */
  public Object[] toArray() {
    synchronized (this) {
      return super.toArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.toPointerArray()
   */
  public com.sap.engine.lib.util.base.Pointer[] toPointerArray() {
    synchronized (this) {
      return super.toPointerArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.addAfter(com.sap.engine.lib.util.base.LinearItem param1, com.sap.engine.lib.util.base.LinearItem param2)
   */
  public boolean addAfter(com.sap.engine.lib.util.base.LinearItem param1, com.sap.engine.lib.util.base.LinearItem param2) {
    synchronized (this) {
      return super.addAfter(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.addBefore(com.sap.engine.lib.util.base.LinearItem param1, com.sap.engine.lib.util.base.LinearItem param2)
   */
  public boolean addBefore(com.sap.engine.lib.util.base.LinearItem param1, com.sap.engine.lib.util.base.LinearItem param2) {
    synchronized (this) {
      return super.addBefore(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.addFirstItem(com.sap.engine.lib.util.base.LinearItem param1)
   */
  public boolean addFirstItem(com.sap.engine.lib.util.base.LinearItem param1) {
    synchronized (this) {
      return super.addFirstItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.addItem(int param1, com.sap.engine.lib.util.base.LinearItem param2)
   */
  public boolean addItem(int param1, com.sap.engine.lib.util.base.LinearItem param2) {
    synchronized (this) {
      return super.addItem(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.addLastItem(com.sap.engine.lib.util.base.LinearItem param1)
   */
  public boolean addLastItem(com.sap.engine.lib.util.base.LinearItem param1) {
    synchronized (this) {
      return super.addLastItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.containsItem(com.sap.engine.lib.util.base.LinearItem param1)
   */
  public boolean containsItem(com.sap.engine.lib.util.base.LinearItem param1) {
    synchronized (this) {
      return super.containsItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.getFirstItem()
   */
  public com.sap.engine.lib.util.base.LinearItem getFirstItem() {
    synchronized (this) {
      return super.getFirstItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.getItem(com.sap.engine.lib.util.base.LinearItem param1)
   */
  public com.sap.engine.lib.util.base.LinearItem getItem(com.sap.engine.lib.util.base.LinearItem param1) {
    synchronized (this) {
      return super.getItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.getItem(int param1)
   */
  public com.sap.engine.lib.util.base.LinearItem getItem(int param1) {
    synchronized (this) {
      return super.getItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.getLastItem()
   */
  public com.sap.engine.lib.util.base.LinearItem getLastItem() {
    synchronized (this) {
      return super.getLastItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.getLimit()
   */
  public int getLimit() {
    synchronized (this) {
      return super.getLimit();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.indexOfItem(com.sap.engine.lib.util.base.LinearItem param1)
   */
  public int indexOfItem(com.sap.engine.lib.util.base.LinearItem param1) {
    synchronized (this) {
      return super.indexOfItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.isFull()
   */
  public boolean isFull() {
    synchronized (this) {
      return super.isFull();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.iterAdd(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2)
   */
  protected com.sap.engine.lib.util.base.LinearItem iterAdd(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2) {
    synchronized (this) {
      return super.iterAdd(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.iterChange(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2)
   */
  protected com.sap.engine.lib.util.base.LinearItem iterChange(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2) {
    synchronized (this) {
      return super.iterChange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.iterGet(com.sap.engine.lib.util.iterators.RootIterator param1)
   */
  protected com.sap.engine.lib.util.base.LinearItem iterGet(com.sap.engine.lib.util.iterators.RootIterator param1) {
    synchronized (this) {
      return super.iterGet(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.iterInsert(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2)
   */
  protected com.sap.engine.lib.util.base.LinearItem iterInsert(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2) {
    synchronized (this) {
      return super.iterInsert(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.iterNext(com.sap.engine.lib.util.iterators.RootIterator param1, int param2)
   */
  protected com.sap.engine.lib.util.base.LinearItem iterNext(com.sap.engine.lib.util.iterators.RootIterator param1, int param2) {
    synchronized (this) {
      return super.iterNext(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.iterPrev(com.sap.engine.lib.util.iterators.RootIterator param1, int param2)
   */
  protected com.sap.engine.lib.util.base.LinearItem iterPrev(com.sap.engine.lib.util.iterators.RootIterator param1, int param2) {
    synchronized (this) {
      return super.iterPrev(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.iterRemove(com.sap.engine.lib.util.iterators.RootIterator param1)
   */
  protected com.sap.engine.lib.util.base.LinearItem iterRemove(com.sap.engine.lib.util.iterators.RootIterator param1) {
    synchronized (this) {
      return super.iterRemove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.iterSize(com.sap.engine.lib.util.iterators.RootIterator param1)
   */
  protected int iterSize(com.sap.engine.lib.util.iterators.RootIterator param1) {
    synchronized (this) {
      return super.iterSize(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.lastIndexOfItem(com.sap.engine.lib.util.base.LinearItem param1)
   */
  public int lastIndexOfItem(com.sap.engine.lib.util.base.LinearItem param1) {
    synchronized (this) {
      return super.lastIndexOfItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.removeAllItems(com.sap.engine.lib.util.base.LinearItem param1)
   */
  public int removeAllItems(com.sap.engine.lib.util.base.LinearItem param1) {
    synchronized (this) {
      return super.removeAllItems(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.removeFirstItem()
   */
  public com.sap.engine.lib.util.base.LinearItem removeFirstItem() {
    synchronized (this) {
      return super.removeFirstItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.removeItem(com.sap.engine.lib.util.base.LinearItem param1)
   */
  public com.sap.engine.lib.util.base.LinearItem removeItem(com.sap.engine.lib.util.base.LinearItem param1) {
    synchronized (this) {
      return super.removeItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.removeItem(int param1)
   */
  public com.sap.engine.lib.util.base.LinearItem removeItem(int param1) {
    synchronized (this) {
      return super.removeItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.removeLastItem()
   */
  public com.sap.engine.lib.util.base.LinearItem removeLastItem() {
    synchronized (this) {
      return super.removeLastItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.replace(com.sap.engine.lib.util.base.LinearItem param1, com.sap.engine.lib.util.base.LinearItem param2)
   */
  public void replace(com.sap.engine.lib.util.base.LinearItem param1, com.sap.engine.lib.util.base.LinearItem param2) {
    synchronized (this) {
      super.replace(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.setItem(int param1, com.sap.engine.lib.util.base.LinearItem param2)
   */
  public com.sap.engine.lib.util.base.LinearItem setItem(int param1, com.sap.engine.lib.util.base.LinearItem param2) {
    synchronized (this) {
      return super.setItem(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.setLimit(int param1)
   */
  public void setLimit(int param1) {
    synchronized (this) {
      super.setLimit(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.toItemArray()
   */
  public com.sap.engine.lib.util.base.LinearItem[] toItemArray() {
    synchronized (this) {
      return super.toItemArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.DoublyLinkedList.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

