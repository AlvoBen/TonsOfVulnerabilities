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
 * AVL Search Tree. Most operation goes O(log(n)).
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
 *   BaseAVLTree tree = new BaseAVLTree();
 *   AVLItemAdapter item = new AVLItemAdapter();
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
public class BaseAVLTree extends BaseBinaryTree { //$JL-CLONE$
  
  static final long serialVersionUID = -7870074126624455035L;

  /**
   *  used by put(), remove(), deleteMin and deleteMax
   */
  protected transient boolean isModified;
  /**
   *  used by delete_()
   */
  protected transient BinTreeItem tmpValue;

  /**
   * Creates new empty tree.
   *
   */
  public BaseAVLTree() {

  }

  /**
   * Gets and remove the element with min key in tree.
   *
   * @return  null if the tree is empty.
   */
  public BinTreeItem removeMinItem() {
    if (root == null) {
      return null;
    }

    root = deleteMin(root);
    count--;
    resultValue.clearItem();
    BinTreeItem result = resultValue;
    resultValue = null;
    return result;
  }

  /**
   * Gets and remove the element with max key in tree.
   *
   * @return  null if the tree is empty.
   */
  public BinTreeItem removeMaxItem() {
    if (root == null) {
      return null;
    }

    root = deleteMax(root);
    count--;
    resultValue.clearItem();
    BinTreeItem result = resultValue;
    resultValue = null;
    return result;
  }

  //************************************************************************
  //************************ protected methods *****************************
  //************************************************************************
  /**
   * Gets and remove the element with min key in tree.
   *
   * @param   current  start tree node
   * @return  new subtree.
   */
  protected BinTreeItem deleteMin(BinTreeItem current) {
    if (current.getLeft() != null) {
      current.setLeft(deleteMin(current.getLeft()));

      if (isModified) {
        current = rotateLeft(current);
      }
    } else {
      resultValue = current;
      isModified = true;
      return current.getRight();
    }

    return current;
  }

  /**
   * Gets and remove the element with max key in tree.
   *
   * @param   current  start tree node
   * @return  new subtree.
   */
  protected BinTreeItem deleteMax(BinTreeItem current) {
    if (current.getRight() != null) {
      current.setRight(deleteMax(current.getRight()));

      if (isModified) {
        current = rotateRight(current);
      }
    } else {
      resultValue = current;
      isModified = true;
      return current.getLeft();
    }

    return current;
  }

  /**
   * Inserts searchNode in node tree.
   *
   * @param   current  start node
   * @return     new subtree
   */
  protected BinTreeItem insert(BinTreeItem current) {
    if (current == null) {
      isModified = true;
      ((AVLItem) recValue).setBalance(0);
      return recValue;
    }

    int cmp = searchKey.compareTo(current.getKey());

    if (cmp < 0) {
      current.setLeft(insert(current.getLeft()));

      if (isModified) {
        switch (((AVLItem) current).getBalance()) {
          case 1: {
            ((AVLItem) current).setBalance(0);
            isModified = false;
            break;
          }
          case 0: {
            ((AVLItem) current).setBalance(-1);
            break;
          }
          case -1: {
            BinTreeItem tmp1 = current.getLeft();

            if (((AVLItem) tmp1).getBalance() == -1) {
              current.setLeft(tmp1.getRight());
              tmp1.setRight(current);
              ((AVLItem) current).setBalance(0);
              current = tmp1;
            } else {
              BinTreeItem tmp2 = tmp1.getRight();
              tmp1.setRight(tmp2.getLeft());
              tmp2.setLeft(tmp1);
              current.setLeft(tmp2.getRight());
              tmp2.setRight(current);
              int bal2 = ((AVLItem) tmp2).getBalance();
              ((AVLItem) current).setBalance(bal2 == -1 ? 1 : 0);
              ((AVLItem) tmp1).setBalance(bal2 == 1 ? -1 : 0);
              current = tmp2;
            }

            ((AVLItem) current).setBalance(0);
            isModified = false;
          }
        }
      }
    } else if (cmp > 0) {
      current.setRight(insert(current.getRight()));

      if (isModified) {
        switch (((AVLItem) current).getBalance()) {
          case -1: {
            ((AVLItem) current).setBalance(0);
            isModified = false;
            break;
          }
          case 0: {
            ((AVLItem) current).setBalance(1);
            break;
          }
          case 1: {
            BinTreeItem tmp1 = current.getRight();

            if (((AVLItem) tmp1).getBalance() == 1) {
              current.setRight(tmp1.getLeft());
              tmp1.setLeft(current);
              ((AVLItem) current).setBalance(0);
              current = tmp1;
            } else {
              BinTreeItem tmp2 = tmp1.getLeft();
              tmp1.setLeft(tmp2.getRight());
              tmp2.setRight(tmp1);
              current.setRight(tmp2.getLeft());
              tmp2.setLeft(current);
              int bal2 = ((AVLItem) tmp2).getBalance();
              ((AVLItem) current).setBalance(bal2 == 1 ? -1 : 0);
              ((AVLItem) tmp1).setBalance(bal2 == -1 ? 1 : 0);
              current = tmp2;
            }

            ((AVLItem) current).setBalance(0);
            isModified = false;
          }
        }
      }
    } else {
      isModified = false;
      resultValue = current;
      recValue.setLeft(current.getLeft());
      recValue.setRight(current.getRight());
      ((AVLItem) recValue).setBalance(((AVLItem) current).getBalance());
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
      isModified = false;
      return null;
    }

    int cmp = searchKey.compareTo(current.getKey());

    if (cmp < 0) {
      current.setLeft(delete(current.getLeft()));

      if (isModified) {
        current = rotateLeft(current);
      }
    } else if (cmp > 0) {
      current.setRight(delete(current.getRight()));

      if (isModified) {
        current = rotateRight(current);
      }
    } else {
      resultValue = current;

      if (current.getRight() == null) {
        isModified = true;
        return current.getLeft();
      }

      if (current.getLeft() == null) {
        isModified = true;
        return current.getRight();
      }

      current = delete_(current.getLeft());
      tmpValue.setLeft(current);
      tmpValue.setRight(resultValue.getRight());
      ((AVLItem) tmpValue).setBalance(((AVLItem) resultValue).getBalance());
      current = isModified ? rotateLeft(tmpValue) : tmpValue;
      tmpValue = null;
    }

    return current;
  }

  /**
   * Deletes node.
   *
   * @param   current  node for deletion
   * @return     new subtree
   */
  protected BinTreeItem delete_(BinTreeItem current) {
    if (current.getRight() != null) {
      current.setRight(delete_(current.getRight()));

      if (isModified) {
        current = rotateRight(current);
      }
    } else {
      isModified = true;
      tmpValue = current;
      return current.getLeft();
    }

    return current;
  }

  /**
   * Rotate left tree node.
   *
   * @param   current  node for rotation
   * @return     new subtree
   */
  protected BinTreeItem rotateLeft(BinTreeItem current) {
    switch (((AVLItem) current).getBalance()) {
      case -1: {
        ((AVLItem) current).setBalance(0);
        break;
      }
      case 0: {
        ((AVLItem) current).setBalance(1);
        isModified = false;
        break;
      }
      case 1: {
        BinTreeItem tmp1 = current.getRight();
        int bal1 = ((AVLItem) tmp1).getBalance();

        if (bal1 >= 0) {
          current.setRight(tmp1.getLeft());
          tmp1.setLeft(current);

          if (bal1 == 0) {
            ((AVLItem) current).setBalance(1);
            ((AVLItem) tmp1).setBalance(-1);
            isModified = false;
          } else {
            ((AVLItem) current).setBalance(0);
            ((AVLItem) tmp1).setBalance(0);
          }

          current = tmp1;
        } else {
          BinTreeItem tmp2 = tmp1.getLeft();
          tmp1.setLeft(tmp2.getRight());
          tmp2.setRight(tmp1);
          current.setRight(tmp2.getLeft());
          tmp2.setLeft(current);
          int bal2 = ((AVLItem) tmp2).getBalance();
          ((AVLItem) current).setBalance(bal2 == 1 ? -1 : 0);
          ((AVLItem) tmp1).setBalance(bal2 == -1 ? 1 : 0);
          ((AVLItem) tmp2).setBalance(0);
          current = tmp2;
        }
      }
    }

    return current;
  }

  /**
   * Rotate right tree node.
   *
   * @param   current  node for rotation
   * @return     new subtree
   */
  protected BinTreeItem rotateRight(BinTreeItem current) {
    switch (((AVLItem) current).getBalance()) {
      case 1: {
        ((AVLItem) current).setBalance(0);
        break;
      }
      case 0: {
        ((AVLItem) current).setBalance(-1);
        isModified = false;
        break;
      }
      case -1: {
        BinTreeItem tmp1 = current.getLeft();
        int bal1 = ((AVLItem) tmp1).getBalance();

        if (bal1 <= 0) {
          current.setLeft(tmp1.getRight());
          tmp1.setRight(current);

          if (bal1 == 0) {
            ((AVLItem) current).setBalance(-1);
            ((AVLItem) tmp1).setBalance(1);
            isModified = false;
          } else {
            ((AVLItem) current).setBalance(0);
            ((AVLItem) tmp1).setBalance(0);
          }

          current = tmp1;
        } else {
          BinTreeItem tmp2 = tmp1.getRight();
          tmp1.setRight(tmp2.getLeft());
          tmp2.setLeft(tmp1);
          current.setLeft(tmp2.getRight());
          tmp2.setRight(current);
          int bal2 = ((AVLItem) tmp2).getBalance();
          ((AVLItem) current).setBalance(bal2 == -1 ? 1 : 0);
          ((AVLItem) tmp1).setBalance(bal2 == 1 ? -1 : 0);
          ((AVLItem) tmp2).setBalance(0);
          current = tmp2;
        }
      }
    }

    return current;
  }

  //***********************************************************************
  //************************** Debug methods ******************************
  //***********************************************************************
  /**
   * Check if this tree structure is AVL balanced tree structure.
   *
   */
  public void checkBal() {
    checkBal(root);
  }

  /**
   * Check if this tree structure is AVL balanced tree structure.
   *
   * @param   current  start node.
   * @return     balance of node
   */
  protected int checkBal(BinTreeItem current) {
    if (current == null) {
      return -1;
    }

    int hl = checkBal(current.getLeft());
    int hr = checkBal(current.getRight());
    int bal = hr - hl;

    if (((AVLItem) current).getBalance() != bal) {
      throw new RuntimeException(current + "     Clalculated Balance = : " + bal);
    }

    return Math.max(hr, hl) + 1;
  }

}

