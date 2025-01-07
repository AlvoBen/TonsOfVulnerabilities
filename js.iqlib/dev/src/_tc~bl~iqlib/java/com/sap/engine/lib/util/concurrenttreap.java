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
 * This class is thread safe implementation of Treap
 * @see com.sap.engine.lib.util.Treap
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentTreap extends Treap {

  static final long serialVersionUID = -4969325157847254792L;
  public ConcurrentTreap() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.deepClone()
   */
  public Object deepClone() {
    synchronized (this) {
      return super.deepClone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.elements()
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.elements(boolean param1)
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator elements(boolean param1) {
    synchronized (this) {
      return super.elements(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.elementsIterator()
   */
  public com.sap.engine.lib.util.iterators.RootIterator elementsIterator() {
    synchronized (this) {
      return super.elementsIterator();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.getAllElements()
   */
  public Object[] getAllElements() {
    synchronized (this) {
      return super.getAllElements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.getAllElements(boolean param1)
   */
  public Object[] getAllElements(boolean param1) {
    synchronized (this) {
      return super.getAllElements(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.getAllKeys()
   */
  public Comparable[] getAllKeys() {
    synchronized (this) {
      return super.getAllKeys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.getAllKeys(boolean param1)
   */
  public Comparable[] getAllKeys(boolean param1) {
    synchronized (this) {
      return super.getAllKeys(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.keys()
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator keys() {
    synchronized (this) {
      return super.keys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.keys(boolean param1)
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator keys(boolean param1) {
    synchronized (this) {
      return super.keys(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.performOnElements(com.sap.engine.lib.util.TreePerformer param1)
   */
  public void performOnElements(com.sap.engine.lib.util.TreePerformer param1) {
    synchronized (this) {
      super.performOnElements(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.performOnElements(com.sap.engine.lib.util.TreePerformer param1, boolean param2)
   */
  public void performOnElements(com.sap.engine.lib.util.TreePerformer param1, boolean param2) {
    synchronized (this) {
      super.performOnElements(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.performOnKeys(com.sap.engine.lib.util.TreePerformer param1)
   */
  public void performOnKeys(com.sap.engine.lib.util.TreePerformer param1) {
    synchronized (this) {
      super.performOnKeys(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.performOnKeys(com.sap.engine.lib.util.TreePerformer param1, boolean param2)
   */
  public void performOnKeys(com.sap.engine.lib.util.TreePerformer param1, boolean param2) {
    synchronized (this) {
      super.performOnKeys(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.check()
   */
  public boolean check() {
    synchronized (this) {
      return super.check();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.equals(Object param1, com.sap.engine.lib.util.Stack param2)
   */
  public boolean equals(Object param1, com.sap.engine.lib.util.Stack param2) {
    synchronized (this) {
      return super.equals(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.getItem(Comparable param1)
   */
  public com.sap.engine.lib.util.base.BinTreeItem getItem(Comparable param1) {
    synchronized (this) {
      return super.getItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.height()
   */
  public int height() {
    synchronized (this) {
      return super.height();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.itemsIterator()
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator itemsIterator() {
    synchronized (this) {
      return super.itemsIterator();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.itemsIterator(boolean param1)
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator itemsIterator(boolean param1) {
    synchronized (this) {
      return super.itemsIterator(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.itemsIterator(boolean param1, com.sap.engine.lib.util.Stack param2)
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator itemsIterator(boolean param1, com.sap.engine.lib.util.Stack param2) {
    synchronized (this) {
      return super.itemsIterator(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.itemsIterator(com.sap.engine.lib.util.Stack param1)
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator itemsIterator(com.sap.engine.lib.util.Stack param1) {
    synchronized (this) {
      return super.itemsIterator(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.maxItem()
   */
  public com.sap.engine.lib.util.base.BinTreeItem maxItem() {
    synchronized (this) {
      return super.maxItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.minItem()
   */
  public com.sap.engine.lib.util.base.BinTreeItem minItem() {
    synchronized (this) {
      return super.minItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.perform(com.sap.engine.lib.util.TreePerformer param1)
   */
  public void perform(com.sap.engine.lib.util.TreePerformer param1) {
    synchronized (this) {
      super.perform(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.perform(com.sap.engine.lib.util.TreePerformer param1, boolean param2)
   */
  public void perform(com.sap.engine.lib.util.TreePerformer param1, boolean param2) {
    synchronized (this) {
      super.perform(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.putItem(com.sap.engine.lib.util.base.BinTreeItem param1)
   */
  public com.sap.engine.lib.util.base.BinTreeItem putItem(com.sap.engine.lib.util.base.BinTreeItem param1) {
    synchronized (this) {
      return super.putItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.removeItem(Comparable param1)
   */
  public com.sap.engine.lib.util.base.BinTreeItem removeItem(Comparable param1) {
    synchronized (this) {
      return super.removeItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.removeMaxItem()
   */
  public com.sap.engine.lib.util.base.BinTreeItem removeMaxItem() {
    synchronized (this) {
      return super.removeMaxItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.removeMinItem()
   */
  public com.sap.engine.lib.util.base.BinTreeItem removeMinItem() {
    synchronized (this) {
      return super.removeMinItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.size_()
   */
  public int size_() {
    synchronized (this) {
      return super.size_();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.toItemArray()
   */
  public com.sap.engine.lib.util.base.BinTreeItem[] toItemArray() {
    synchronized (this) {
      return super.toItemArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.toItemArray(boolean param1)
   */
  public com.sap.engine.lib.util.base.BinTreeItem[] toItemArray(boolean param1) {
    synchronized (this) {
      return super.toItemArray(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.Treap.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

