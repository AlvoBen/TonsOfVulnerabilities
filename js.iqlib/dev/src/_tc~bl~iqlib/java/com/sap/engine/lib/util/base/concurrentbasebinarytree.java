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
 * This class is thread safe implementation of BaseBinaryTree
 * @see com.sap.engine.lib.util.base.BaseBinaryTree
 *
 * @author Vassil Popovski vassil.popovski@sap.com
 * @version 4.0
 */
public class ConcurrentBaseBinaryTree extends BaseBinaryTree {
  static final long serialVersionUID = 2632991583077182440L;
  public ConcurrentBaseBinaryTree() {
    super();
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.check()
   */
  public boolean check() {
    synchronized (this) {
      return super.check();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.clear()
   */
  public void clear() {
    synchronized (this) {
      super.clear();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.clone()
   */
  public Object clone() {
    synchronized (this) {
      return super.clone();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.elementsIterator()
   */
  public com.sap.engine.lib.util.iterators.RootIterator elementsIterator() {
    synchronized (this) {
      return super.elementsIterator();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.equals(Object param1)
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
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.equals(Object param1, com.sap.engine.lib.util.Stack param2)
   */
  public boolean equals(Object param1, com.sap.engine.lib.util.Stack param2) {
    synchronized (this) {
      return super.equals(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.getItem(Comparable param1)
   */
  public com.sap.engine.lib.util.base.BinTreeItem getItem(Comparable param1) {
    synchronized (this) {
      return super.getItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.height()
   */
  public int height() {
    synchronized (this) {
      return super.height();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.itemsIterator()
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator itemsIterator() {
    synchronized (this) {
      return super.itemsIterator();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.itemsIterator(boolean param1)
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator itemsIterator(boolean param1) {
    synchronized (this) {
      return super.itemsIterator(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.itemsIterator(boolean param1, com.sap.engine.lib.util.Stack param2)
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator itemsIterator(boolean param1, com.sap.engine.lib.util.Stack param2) {
    synchronized (this) {
      return super.itemsIterator(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.itemsIterator(com.sap.engine.lib.util.Stack param1)
   */
  public com.sap.engine.lib.util.iterators.ForwardIterator itemsIterator(com.sap.engine.lib.util.Stack param1) {
    synchronized (this) {
      return super.itemsIterator(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.maxItem()
   */
  public com.sap.engine.lib.util.base.BinTreeItem maxItem() {
    synchronized (this) {
      return super.maxItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.minItem()
   */
  public com.sap.engine.lib.util.base.BinTreeItem minItem() {
    synchronized (this) {
      return super.minItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.perform(com.sap.engine.lib.util.TreePerformer param1)
   */
  public void perform(com.sap.engine.lib.util.TreePerformer param1) {
    synchronized (this) {
      super.perform(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.perform(com.sap.engine.lib.util.TreePerformer param1, boolean param2)
   */
  public void perform(com.sap.engine.lib.util.TreePerformer param1, boolean param2) {
    synchronized (this) {
      super.perform(param1, param2);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.putItem(com.sap.engine.lib.util.base.BinTreeItem param1)
   */
  public com.sap.engine.lib.util.base.BinTreeItem putItem(com.sap.engine.lib.util.base.BinTreeItem param1) {
    synchronized (this) {
      return super.putItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.removeItem(Comparable param1)
   */
  public com.sap.engine.lib.util.base.BinTreeItem removeItem(Comparable param1) {
    synchronized (this) {
      return super.removeItem(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.removeMaxItem()
   */
  public com.sap.engine.lib.util.base.BinTreeItem removeMaxItem() {
    synchronized (this) {
      return super.removeMaxItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.removeMinItem()
   */
  public com.sap.engine.lib.util.base.BinTreeItem removeMinItem() {
    synchronized (this) {
      return super.removeMinItem();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.size_()
   */
  public int size_() {
    synchronized (this) {
      return super.size_();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.toItemArray()
   */
  public com.sap.engine.lib.util.base.BinTreeItem[] toItemArray() {
    synchronized (this) {
      return super.toItemArray();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.toItemArray(boolean param1)
   */
  public com.sap.engine.lib.util.base.BinTreeItem[] toItemArray(boolean param1) {
    synchronized (this) {
      return super.toItemArray(param1);
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.toString()
   */
  public String toString() {
    synchronized (this) {
      return super.toString();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.isEmpty()
   */
  public boolean isEmpty() {
    synchronized (this) {
      return super.isEmpty();
    }
  }

  /**
   * This is thread safe implementation of com.sap.engine.lib.util.base.BaseBinaryTree.size()
   */
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

