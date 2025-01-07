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
 * This class is thread safe implementation of AVLTree
 * @see com.sap.engine.lib.util.AVLTree
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentAVLTree extends AVLTree {

  static final long serialVersionUID = 5950465030739794018L;

  public ConcurrentAVLTree() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.deepClone()
   */
  public Object deepClone() {
    synchronized (this) {
      return super.deepClone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.elements()
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator elements() {
    synchronized (this) {
      return super.elements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.elements(boolean param1)
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator elements(boolean param1) {
    synchronized (this) {
      return super.elements(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.elementsIterator()
   */
  public com.sap.engine.lib.util.iterators.RootIterator elementsIterator() {
    synchronized (this) {
      return super.elementsIterator();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.getAllElements()
   */
  public Object[] getAllElements() {
    synchronized (this) {
      return super.getAllElements();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.getAllElements(boolean param1)
   */
  public Object[] getAllElements(boolean param1) {
    synchronized (this) {
      return super.getAllElements(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.getAllKeys()
   */
  public Comparable[] getAllKeys() {
    synchronized (this) {
      return super.getAllKeys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.getAllKeys(boolean param1)
   */
  public Comparable[] getAllKeys(boolean param1) {
    synchronized (this) {
      return super.getAllKeys(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.keys()
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator keys() {
    synchronized (this) {
      return super.keys();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.keys(boolean param1)
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator keys(boolean param1) {
    synchronized (this) {
      return super.keys(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.performOnElements(com.sap.engine.lib.util.TreePerformer param1)
   */
  public void performOnElements(com.sap.engine.lib.util.TreePerformer param1) {
    synchronized (this) {
      super.performOnElements(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.performOnElements(com.sap.engine.lib.util.TreePerformer param1, boolean param2)
   */
  public void performOnElements(com.sap.engine.lib.util.TreePerformer param1, boolean param2) {
    synchronized (this) {
      super.performOnElements(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.performOnKeys(com.sap.engine.lib.util.TreePerformer param1)
   */
  public void performOnKeys(com.sap.engine.lib.util.TreePerformer param1) {
    synchronized (this) {
      super.performOnKeys(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.performOnKeys(com.sap.engine.lib.util.TreePerformer param1, boolean param2)
   */
  public void performOnKeys(com.sap.engine.lib.util.TreePerformer param1, boolean param2) {
    synchronized (this) {
      super.performOnKeys(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.checkBal()
   */
  public void checkBal() {
    synchronized (this) {
      super.checkBal();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.removeMaxItem()
   */
  public com.sap.engine.lib.util.base.BinTreeItem removeMaxItem() {
    synchronized (this) {
      return super.removeMaxItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.removeMinItem()
   */
  public com.sap.engine.lib.util.base.BinTreeItem removeMinItem() {
    synchronized (this) {
      return super.removeMinItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.check()
   */
  public boolean check() {
    synchronized (this) {
      return super.check();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.equals(Object param1, com.sap.engine.lib.util.Stack param2)
   */
  public boolean equals(Object param1, com.sap.engine.lib.util.Stack param2) {
    synchronized (this) {
      return super.equals(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.getItem(Comparable param1)
   */
  public com.sap.engine.lib.util.base.BinTreeItem getItem(Comparable param1) {
    synchronized (this) {
      return super.getItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.height()
   */
  public int height() {
    synchronized (this) {
      return super.height();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.itemsIterator()
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator itemsIterator() {
    synchronized (this) {
      return super.itemsIterator();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.itemsIterator(boolean param1)
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator itemsIterator(boolean param1) {
    synchronized (this) {
      return super.itemsIterator(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.itemsIterator(boolean param1, com.sap.engine.lib.util.Stack param2)
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator itemsIterator(boolean param1, com.sap.engine.lib.util.Stack param2) {
    synchronized (this) {
      return super.itemsIterator(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.itemsIterator(com.sap.engine.lib.util.Stack param1)
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator itemsIterator(com.sap.engine.lib.util.Stack param1) {
    synchronized (this) {
      return super.itemsIterator(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.maxItem()
   */
  public com.sap.engine.lib.util.base.BinTreeItem maxItem() {
    synchronized (this) {
      return super.maxItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.minItem()
   */
  public com.sap.engine.lib.util.base.BinTreeItem minItem() {
    synchronized (this) {
      return super.minItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.perform(com.sap.engine.lib.util.TreePerformer param1)
   */
  public void perform(com.sap.engine.lib.util.TreePerformer param1) {
    synchronized (this) {
      super.perform(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.perform(com.sap.engine.lib.util.TreePerformer param1, boolean param2)
   */
  public void perform(com.sap.engine.lib.util.TreePerformer param1, boolean param2) {
    synchronized (this) {
      super.perform(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.putItem(com.sap.engine.lib.util.base.BinTreeItem param1)
   */
  public com.sap.engine.lib.util.base.BinTreeItem putItem(com.sap.engine.lib.util.base.BinTreeItem param1) {
    synchronized (this) {
      return super.putItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.removeItem(Comparable param1)
   */
  public com.sap.engine.lib.util.base.BinTreeItem removeItem(Comparable param1) {
    synchronized (this) {
      return super.removeItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.size_()
   */
  public int size_() {
    synchronized (this) {
      return super.size_();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.toItemArray()
   */
  public com.sap.engine.lib.util.base.BinTreeItem[] toItemArray() {
    synchronized (this) {
      return super.toItemArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.toItemArray(boolean param1)
   */
  public com.sap.engine.lib.util.base.BinTreeItem[] toItemArray(boolean param1) {
    synchronized (this) {
      return super.toItemArray(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.AVLTree.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

