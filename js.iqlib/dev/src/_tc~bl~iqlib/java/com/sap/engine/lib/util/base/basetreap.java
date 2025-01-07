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

import java.util.Random;

/**
 * Randomized Binary Search Tree. Most operation goes O(log(n)).
 * The advantage of trees is that elements can be enumerate sorted.
 *
 * WARNING: This class is not synchronized.
 *  If tree is used in multithreaded environment every method has to be
 *  called in synchronized block
 *
 * Pooling: Generally list assigns null value to pointers of deleted elements.
 *          exceptions are methods, for which it is explicitly specified.
 *
 * Example for synchronization:
 *
 *   BaseTreap tree = new BaseTreap();
 *   TreapItemAdapter item = new TreapItemAdapter();
 *   item.setKey("nick");
 *
 *   synchronized (tree) {
 *     tree.put(item);
 *   }
 *
 *   // some stuff
 *
 *   synchronized (tree) {
 *     tree.remove("nick");
 *   }
 *
 * @author Nikola Arnaudov
 *
 * @version 1.0.3
 */
public class BaseTreap extends BaseBinaryTree {
  
  static final long serialVersionUID = -2004607637938523700L;

  /**
   *  Random generator for creation of randomized tree.
   */
  protected Random random;

  /**
   * Creates new empty tree.
   *
   */
  public BaseTreap() {
    random = new Random();
  }

  /**
   * Clones the tree. All elements are cloned. Keys are not cloned.
   * If you want to keys to be cloned, you have to override the clone()
   * method of BinTreeItem.
   *
   * @return a clone of the tree
   */
  public Object clone() {
    BaseTreap tree = (BaseTreap) getNewInstance();
    tree.random = new Random();
    clone(tree, root);
    return tree;
  }

  //************************************************************************
  //************************ protected methods *****************************
  //************************************************************************
  /**
   * Inserts searchNode in node tree.
   *
   * @param   current  start node
   * @return     new subtree
   */
  protected BinTreeItem insert(BinTreeItem current) {
    if (current == null) {
      ((TreapItem) recValue).setPriority(random.nextInt());
      return recValue;
    }

    int cmp = searchKey.compareTo(current.getKey());

    if (cmp < 0) {
      current.setLeft(insert(current.getLeft()));

      if (((TreapItem) current).getPriority() > ((TreapItem) current.getLeft()).getPriority()) {
        current = rotateRight(current);
      }
    } else if (cmp > 0) {
      current.setRight(insert(current.getRight()));

      if (((TreapItem) current).getPriority() > ((TreapItem) current.getRight()).getPriority()) {
        current = rotateLeft(current);
      }
    } else {
      resultValue = current;
      recValue.setLeft(current.getLeft());
      recValue.setRight(current.getRight());
      return recValue;
    }

    return current;
  }

  /**
   * Deletes node with key equals to searchKey from tree.
   *
   * @param   current  start node
   * @return     new subtree
   */
  protected BinTreeItem delete(BinTreeItem current) {
    if (current == null) {
      return null;
    }

    int cmp = searchKey.compareTo(current.getKey());

    if (cmp < 0) {
      current.setLeft(delete(current.getLeft()));
    } else if (cmp > 0) {
      current.setRight(delete(current.getRight()));
    } else {
      resultValue = current;
      current = delete_(current);
    }

    return current;
  }

  /**
   * Deletes node.
   *
   * @param   node  node for deletion
   * @return     new subtree
   */
  protected BinTreeItem delete_(BinTreeItem node) {
    BinTreeItem tmp;

    if (node.getLeft() == null) {
      return node.getRight();
    }

    if (node.getRight() == null) {
      return node.getLeft();
    }

    if (((TreapItem) node.getLeft()).getPriority() < ((TreapItem) node.getRight()).getPriority()) {
      tmp = rotateRight(node);
      tmp.setRight(delete_(node));
    } else {
      tmp = rotateLeft(node);
      tmp.setLeft(delete_(node));
    }

    return tmp;
  }

  /**
   * Rotate left tree node.
   *
   * @param   node  node for rotation
   * @return     new subtree
   */
  protected BinTreeItem rotateLeft(BinTreeItem node) {
    BinTreeItem tmp = node.getRight();
    node.setRight(tmp.getLeft());
    tmp.setLeft(node);
    return tmp;
  }

  /**
   * Rotate right tree node.
   *
   * @param   node  node for rotation
   * @return     new subtree
   */
  protected BinTreeItem rotateRight(BinTreeItem node) {
    BinTreeItem tmp = node.getLeft();
    node.setLeft(tmp.getRight());
    tmp.setRight(node);
    return tmp;
  }

  //***********************************************************************
  //************************** Debug methods ******************************
  //***********************************************************************
  /**
   * Returns a string representation of the tree.
   *
   * @return     a string representation of the tree
   */
  public String toString() {
    return getNode("", root, true);
  }

  /**
   * Returns a string representation of the tree node.
   *
   * @param   depth  depth of tree
   * @param   current  start node
   * @param   isLeft  is left leaf
   * @return     string representation of the tree node
   */
  protected String getNode(String depth, BinTreeItem current, boolean isLeft) {
    String t = isLeft ? "--- " : "R--- ";

    if (current == null) {
      return depth + t + null + "\n";
    }

    return depth + t + current.getKey() + " : " + ((TreapItem) current).getPriority() + "\n" + getNode(depth + "    |", current.getLeft(), true) + getNode(depth + "    ", current.getRight(), false);
  }

}

