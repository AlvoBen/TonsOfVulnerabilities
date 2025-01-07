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
 * This class is thread safe implementation of BaseLinkedList
 * @see com.sap.engine.lib.util.base.BaseLinkedList
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentBaseLinkedList extends BaseLinkedList {
  static final long serialVersionUID = -4181176690619434676L;
  
  public ConcurrentBaseLinkedList(int param1) {
    super(param1);
  }

  public ConcurrentBaseLinkedList() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.addAfter(com.sap.engine.lib.util.base.NextItem param1, com.sap.engine.lib.util.base.NextItem param2)
   */
  public boolean addAfter(com.sap.engine.lib.util.base.NextItem param1, com.sap.engine.lib.util.base.NextItem param2) {
    synchronized (this) {
      return super.addAfter(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.addFirstItem(com.sap.engine.lib.util.base.NextItem param1)
   */
  public boolean addFirstItem(com.sap.engine.lib.util.base.NextItem param1) {
    synchronized (this) {
      return super.addFirstItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.addItem(int param1, com.sap.engine.lib.util.base.NextItem param2)
   */
  public boolean addItem(int param1, com.sap.engine.lib.util.base.NextItem param2) {
    synchronized (this) {
      return super.addItem(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.addLastItem(com.sap.engine.lib.util.base.NextItem param1)
   */
  public boolean addLastItem(com.sap.engine.lib.util.base.NextItem param1) {
    synchronized (this) {
      return super.addLastItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.containsItem(com.sap.engine.lib.util.base.NextItem param1)
   */
  public boolean containsItem(com.sap.engine.lib.util.base.NextItem param1) {
    synchronized (this) {
      return super.containsItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.elementsIterator()
   */
  public com.sap.engine.lib.util.iterators.RootIterator elementsIterator() {
    synchronized (this) {
      return super.elementsIterator();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.getFirstItem()
   */
  public com.sap.engine.lib.util.base.NextItem getFirstItem() {
    synchronized (this) {
      return super.getFirstItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.getItem(com.sap.engine.lib.util.base.NextItem param1)
   */
  public com.sap.engine.lib.util.base.NextItem getItem(com.sap.engine.lib.util.base.NextItem param1) {
    synchronized (this) {
      return super.getItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.getItem(int param1)
   */
  public com.sap.engine.lib.util.base.NextItem getItem(int param1) {
    synchronized (this) {
      return super.getItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.getLastItem()
   */
  public com.sap.engine.lib.util.base.NextItem getLastItem() {
    synchronized (this) {
      return super.getLastItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.getLimit()
   */
  public int getLimit() {
    synchronized (this) {
      return super.getLimit();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.indexOfItem(com.sap.engine.lib.util.base.NextItem param1)
   */
  public int indexOfItem(com.sap.engine.lib.util.base.NextItem param1) {
    synchronized (this) {
      return super.indexOfItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.isFull()
   */
  public boolean isFull() {
    synchronized (this) {
      return super.isFull();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.iterAdd(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2)
   */
  protected com.sap.engine.lib.util.base.NextItem iterAdd(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2) {
    synchronized (this) {
      return super.iterAdd(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.iterChange(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2)
   */
  protected com.sap.engine.lib.util.base.NextItem iterChange(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2) {
    synchronized (this) {
      return super.iterChange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.iterGet(com.sap.engine.lib.util.iterators.RootIterator param1)
   */
  protected com.sap.engine.lib.util.base.NextItem iterGet(com.sap.engine.lib.util.iterators.RootIterator param1) {
    synchronized (this) {
      return super.iterGet(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.iterInsert(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2)
   */
  protected com.sap.engine.lib.util.base.NextItem iterInsert(Object param1, com.sap.engine.lib.util.iterators.RootIterator param2) {
    synchronized (this) {
      return super.iterInsert(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.iterNext(com.sap.engine.lib.util.iterators.RootIterator param1, int param2)
   */
  protected com.sap.engine.lib.util.base.NextItem iterNext(com.sap.engine.lib.util.iterators.RootIterator param1, int param2) {
    synchronized (this) {
      return super.iterNext(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.iterRemove(com.sap.engine.lib.util.iterators.RootIterator param1)
   */
  protected com.sap.engine.lib.util.base.NextItem iterRemove(com.sap.engine.lib.util.iterators.RootIterator param1) {
    synchronized (this) {
      return super.iterRemove(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.iterSize(com.sap.engine.lib.util.iterators.RootIterator param1)
   */
  protected int iterSize(com.sap.engine.lib.util.iterators.RootIterator param1) {
    synchronized (this) {
      return super.iterSize(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.lastIndexOfItem(com.sap.engine.lib.util.base.NextItem param1)
   */
  public int lastIndexOfItem(com.sap.engine.lib.util.base.NextItem param1) {
    synchronized (this) {
      return super.lastIndexOfItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.removeAfter(com.sap.engine.lib.util.base.NextItem param1)
   */
  public void removeAfter(com.sap.engine.lib.util.base.NextItem param1) {
    synchronized (this) {
      super.removeAfter(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.removeAllItems(com.sap.engine.lib.util.base.NextItem param1)
   */
  public int removeAllItems(com.sap.engine.lib.util.base.NextItem param1) {
    synchronized (this) {
      return super.removeAllItems(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.removeFirstItem()
   */
  public com.sap.engine.lib.util.base.NextItem removeFirstItem() {
    synchronized (this) {
      return super.removeFirstItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.removeItem(com.sap.engine.lib.util.base.NextItem param1)
   */
  public com.sap.engine.lib.util.base.NextItem removeItem(com.sap.engine.lib.util.base.NextItem param1) {
    synchronized (this) {
      return super.removeItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.removeItem(int param1)
   */
  public com.sap.engine.lib.util.base.NextItem removeItem(int param1) {
    synchronized (this) {
      return super.removeItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.removeLastItem()
   */
  public com.sap.engine.lib.util.base.NextItem removeLastItem() {
    synchronized (this) {
      return super.removeLastItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.removeRange(com.sap.engine.lib.util.base.NextItem param1, com.sap.engine.lib.util.base.NextItem param2)
   */
  public void removeRange(com.sap.engine.lib.util.base.NextItem param1, com.sap.engine.lib.util.base.NextItem param2) {
    synchronized (this) {
      super.removeRange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.removeRange(int param1, int param2)
   */
  public boolean removeRange(int param1, int param2) {
    synchronized (this) {
      return super.removeRange(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.removeSublist(int param1)
   */
  public boolean removeSublist(int param1) {
    synchronized (this) {
      return super.removeSublist(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.replaceAfter(com.sap.engine.lib.util.base.NextItem param1, com.sap.engine.lib.util.base.NextItem param2)
   */
  public com.sap.engine.lib.util.base.NextItem replaceAfter(com.sap.engine.lib.util.base.NextItem param1, com.sap.engine.lib.util.base.NextItem param2) {
    synchronized (this) {
      return super.replaceAfter(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.setItem(int param1, com.sap.engine.lib.util.base.NextItem param2)
   */
  public com.sap.engine.lib.util.base.NextItem setItem(int param1, com.sap.engine.lib.util.base.NextItem param2) {
    synchronized (this) {
      return super.setItem(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.setLimit(int param1)
   */
  public void setLimit(int param1) {
    synchronized (this) {
      super.setLimit(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.sublist(int param1)
   */
  public com.sap.engine.lib.util.base.BaseLinkedList sublist(int param1) {
    synchronized (this) {
      return super.sublist(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.sublist(int param1, int param2)
   */
  public com.sap.engine.lib.util.base.BaseLinkedList sublist(int param1, int param2) {
    synchronized (this) {
      return super.sublist(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.toItemArray()
   */
  public com.sap.engine.lib.util.base.NextItem[] toItemArray() {
    synchronized (this) {
      return super.toItemArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseLinkedList.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

