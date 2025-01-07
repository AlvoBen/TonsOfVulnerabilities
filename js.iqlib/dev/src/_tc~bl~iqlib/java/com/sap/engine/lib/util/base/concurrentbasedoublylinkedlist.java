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
package com.sap.engine.lib.util.base;

/**
 * This class is thread safe implementation of BaseDoublyLinkedList
 * @see com.sap.engine.lib.util.base.BaseDoublyLinkedList
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentBaseDoublyLinkedList extends BaseDoublyLinkedList {
  static final long serialVersionUID = -8138397960297246468L;
  public ConcurrentBaseDoublyLinkedList(int param1) {
    super(param1);
  }

  public ConcurrentBaseDoublyLinkedList() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.addAfter(com.sap.engine.lib.util.base.LinearItem param1, com.sap.engine.lib.util.base.LinearItem param2)
   */
  public boolean addAfter(com.sap.engine.lib.util.base.LinearItem param1, com.sap.engine.lib.util.base.LinearItem param2) {
    synchronized (this) {
      return super.addAfter(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.addBefore(com.sap.engine.lib.util.base.LinearItem param1, com.sap.engine.lib.util.base.LinearItem param2)
   */
  public boolean addBefore(com.sap.engine.lib.util.base.LinearItem param1, com.sap.engine.lib.util.base.LinearItem param2) {
    synchronized (this) {
      return super.addBefore(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.addFirstItem(com.sap.engine.lib.util.base.LinearItem param1)
   */
  public boolean addFirstItem(com.sap.engine.lib.util.base.LinearItem param1) {
    synchronized (this) {
      return super.addFirstItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.addItem(int param1, com.sap.engine.lib.util.base.LinearItem param2)
   */
  public boolean addItem(int param1, com.sap.engine.lib.util.base.LinearItem param2) {
    synchronized (this) {
      return super.addItem(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.addLastItem(com.sap.engine.lib.util.base.LinearItem param1)
   */
  public boolean addLastItem(com.sap.engine.lib.util.base.LinearItem param1) {
    synchronized (this) {
      return super.addLastItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.containsItem(com.sap.engine.lib.util.base.LinearItem param1)
   */
  public boolean containsItem(com.sap.engine.lib.util.base.LinearItem param1) {
    synchronized (this) {
      return super.containsItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.elementsIterator()
   */
  public com.sap.engine.lib.util.iterators.RootIterator elementsIterator() {
    synchronized (this) {
      return super.elementsIterator();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.getFirstItem()
   */
  public com.sap.engine.lib.util.base.LinearItem getFirstItem() {
    synchronized (this) {
      return super.getFirstItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.getItem(com.sap.engine.lib.util.base.LinearItem param1)
   */
  public com.sap.engine.lib.util.base.LinearItem getItem(com.sap.engine.lib.util.base.LinearItem param1) {
    synchronized (this) {
      return super.getItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.getItem(int param1)
   */
  public com.sap.engine.lib.util.base.LinearItem getItem(int param1) {
    synchronized (this) {
      return super.getItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.getLastItem()
   */
  public com.sap.engine.lib.util.base.LinearItem getLastItem() {
    synchronized (this) {
      return super.getLastItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.getLimit()
   */
  public int getLimit() {
    synchronized (this) {
      return super.getLimit();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.indexOfItem(com.sap.engine.lib.util.base.LinearItem param1)
   */
  public int indexOfItem(com.sap.engine.lib.util.base.LinearItem param1) {
    synchronized (this) {
      return super.indexOfItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.isFull()
   */
  public boolean isFull() {
    synchronized (this) {
      return super.isFull();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.iterAdd(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2)
   */
  protected com.sap.engine.lib.util.base.LinearItem iterAdd(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2) {
    synchronized (this) {
      return super.iterAdd(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.iterChange(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2)
   */
  protected com.sap.engine.lib.util.base.LinearItem iterChange(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2) {
    synchronized (this) {
      return super.iterChange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.iterGet(com.sap.engine.lib.util.iterators.RootIterator param1)
   */
  protected com.sap.engine.lib.util.base.LinearItem iterGet(com.sap.engine.lib.util.iterators.RootIterator param1) {
    synchronized (this) {
      return super.iterGet(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.iterInsert(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2)
   */
  protected com.sap.engine.lib.util.base.LinearItem iterInsert(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2) {
    synchronized (this) {
      return super.iterInsert(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.iterNext(com.sap.engine.lib.util.iterators.RootIterator param1, int param2)
   */
  protected com.sap.engine.lib.util.base.LinearItem iterNext(com.sap.engine.lib.util.iterators.RootIterator param1, int param2) {
    synchronized (this) {
      return super.iterNext(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.iterPrev(com.sap.engine.lib.util.iterators.RootIterator param1, int param2)
   */
  protected com.sap.engine.lib.util.base.LinearItem iterPrev(com.sap.engine.lib.util.iterators.RootIterator param1, int param2) {
    synchronized (this) {
      return super.iterPrev(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.iterRemove(com.sap.engine.lib.util.iterators.RootIterator param1)
   */
  protected com.sap.engine.lib.util.base.LinearItem iterRemove(com.sap.engine.lib.util.iterators.RootIterator param1) {
    synchronized (this) {
      return super.iterRemove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.iterSize(com.sap.engine.lib.util.iterators.RootIterator param1)
   */
  protected int iterSize(com.sap.engine.lib.util.iterators.RootIterator param1) {
    synchronized (this) {
      return super.iterSize(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.lastIndexOfItem(com.sap.engine.lib.util.base.LinearItem param1)
   */
  public int lastIndexOfItem(com.sap.engine.lib.util.base.LinearItem param1) {
    synchronized (this) {
      return super.lastIndexOfItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.removeAllItems(com.sap.engine.lib.util.base.LinearItem param1)
   */
  public int removeAllItems(com.sap.engine.lib.util.base.LinearItem param1) {
    synchronized (this) {
      return super.removeAllItems(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.removeElement(com.sap.engine.lib.util.base.LinearItem param1)
   */
  public void removeElement(com.sap.engine.lib.util.base.LinearItem param1) {
    synchronized (this) {
      super.removeElement(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.removeFirstItem()
   */
  public com.sap.engine.lib.util.base.LinearItem removeFirstItem() {
    synchronized (this) {
      return super.removeFirstItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.removeItem(com.sap.engine.lib.util.base.LinearItem param1)
   */
  public com.sap.engine.lib.util.base.LinearItem removeItem(com.sap.engine.lib.util.base.LinearItem param1) {
    synchronized (this) {
      return super.removeItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.removeItem(int param1)
   */
  public com.sap.engine.lib.util.base.LinearItem removeItem(int param1) {
    synchronized (this) {
      return super.removeItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.removeLastItem()
   */
  public com.sap.engine.lib.util.base.LinearItem removeLastItem() {
    synchronized (this) {
      return super.removeLastItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.removeRange(com.sap.engine.lib.util.base.LinearItem param1, com.sap.engine.lib.util.base.LinearItem param2)
   */
  public void removeRange(com.sap.engine.lib.util.base.LinearItem param1, com.sap.engine.lib.util.base.LinearItem param2) {
    synchronized (this) {
      super.removeRange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.removeRange(int param1, int param2)
   */
  public boolean removeRange(int param1, int param2) {
    synchronized (this) {
      return super.removeRange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.removeSublist(int param1)
   */
  public boolean removeSublist(int param1) {
    synchronized (this) {
      return super.removeSublist(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.replace(com.sap.engine.lib.util.base.LinearItem param1, com.sap.engine.lib.util.base.LinearItem param2)
   */
  public void replace(com.sap.engine.lib.util.base.LinearItem param1, com.sap.engine.lib.util.base.LinearItem param2) {
    synchronized (this) {
      super.replace(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.setItem(int param1, com.sap.engine.lib.util.base.LinearItem param2)
   */
  public com.sap.engine.lib.util.base.LinearItem setItem(int param1, com.sap.engine.lib.util.base.LinearItem param2) {
    synchronized (this) {
      return super.setItem(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.setLimit(int param1)
   */
  public void setLimit(int param1) {
    synchronized (this) {
      super.setLimit(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.sublist(int param1)
   */
  public com.sap.engine.lib.util.base.BaseDoublyLinkedList sublist(int param1) {
    synchronized (this) {
      return super.sublist(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.sublist(int param1, int param2)
   */
  public com.sap.engine.lib.util.base.BaseDoublyLinkedList sublist(int param1, int param2) {
    synchronized (this) {
      return super.sublist(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.toItemArray()
   */
  public com.sap.engine.lib.util.base.LinearItem[] toItemArray() {
    synchronized (this) {
      return super.toItemArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseDoublyLinkedList.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

