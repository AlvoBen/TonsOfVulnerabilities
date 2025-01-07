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

import com.sap.engine.lib.util.*;
import com.sap.engine.lib.util.iterators.*;

/**
 * Binary Search Tree
 * The advantage of trees is that elements can be enumerated sorted.
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
 *   BaseBinaryTree tree = new BaseBinaryTree();
 *   BinTreeItemAdapter item = new BinTreeItemAdapter();
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
public class BaseBinaryTree extends AbstractDataStructure {
  static final long serialVersionUID = -4702832679883873013L;
  /**
   *  root of the tree
   */
  protected BinTreeItem root;
  /**
   *  used by put() and remove()
   */
  protected transient BinTreeItem resultValue;
  /**
   *  used by put()
   */
  protected transient BinTreeItem recValue;
  /**
   *  used by put() and remove()
   */
  protected transient Comparable searchKey;

  /**
   * Creates new empty tree.
   *
   */
  public BaseBinaryTree() {

  }

  /**
   * Empties the tree.
   *
   * Note: This method do not clears pointers of removed elements.
   */
  public void clear() {
    count = 0;
    root = null;
  }

  /**
   * Compares a tree for equality with another object.
   * Trees are equal if and only if they have the same size
   * and contains elements with equal keys.
   *
   * @param   o  object that the current tree is compared to
   * @return  true if the tree is equal to the object; false otherwise
   */
  public boolean equals(Object o) {
    if (!(o instanceof BaseBinaryTree)) {
      return false;
    }

    BaseBinaryTree tmp = (BaseBinaryTree) o;

    if (tmp.count != count) {
      return false;
    }

    return equals(tmp.itemsIterator(), root);
  }
  
  public int hashCode() {
	  int result = 17;
	  result = 37 * result + count;
	  result = 37 * result + ((root == null) ? 0 : root.hashCode());  	  
	  return result;
	}
  	

  /**
   * Compares a tree for equality with another object.
   * Trees are equal if and only if they have the same size
   * and contains elements with equal keys.
   *
   * This method use stack and for optimization you can add
   * your own stack.
   *
   * @param   o  object that the current tree is compared to
   * @param   stack  user stack
   * @return  true if the tree is equal to the object; false otherwise
   */
  public boolean equals(Object o, Stack stack) {
    if (!(o instanceof BaseBinaryTree)) {
      return false;
    }

    BaseBinaryTree tmp = (BaseBinaryTree) o;

    if (tmp.count != count) {
      return false;
    }

    return equals(tmp.itemsIterator(stack), root);
  }

  /**
   * Clones the tree. All elements are cloned. Keys are not cloned.
   * If you want to keys to be cloned, you have to override the clone()
   * method of BinTreeItem.
   *
   * @return a clone of the tree
   */
  public Object clone() {
    BaseBinaryTree tree = (BaseBinaryTree) getNewInstance();
    clone(tree, root);
    return tree;
  }

  /**
   * Tests whether the tree contains specified element.
   *
   * @param   item  element that is searched for
   * @return  true if the list contains the element; false otherwise
   */
  public boolean contains(Comparable item) {
    return getItem(item) != null;
  }

  /**
   * Returns iterator of the components of this tree.
   * The returned Iterator object will generate all items in this tree
   * in ascending order.
   *
   * Note: perform() method is faster than this method cause this method
   *       uses stack.
   *
   * @return     iterator of the components of this tree
   */
  public ForwardIterator itemsIterator() {
    return new TreeIterator();
  }

  /**
   * Returns iterator of the components of this tree.
   * The returned Iterator object will generate all items in this tree
   * in ascending order.
   *
   * Note: perform() method is faster than this method cause this method
   *       uses stack.
   *
   * @param   stack  user stack
   * @return     iterator of the components of this tree
   */
  public ForwardIterator itemsIterator(Stack stack) {
    return new TreeIterator(stack);
  }

  /**
   * Returns iterator of the components of this tree.
   *
   * Note: perform() method is faster than this method cause this method
   *       uses stack.
   *
   * @param   ascending  the order ot elemets
   * @return     iterator of the components of this tree
   */
  public ForwardIterator itemsIterator(boolean ascending) {
    return ascending ? new TreeIterator() : new TreeIteratorDown();
  }

  /**
   * Returns iterator of the components of this tree.
   *
   * Note: perform() method is faster than this method cause this method
   *       uses stack.
   *
   * @param   ascending  the order ot elemets
   * @param   stack  user stack
   * @return     enumeration of the components of this tree
   */
  public ForwardIterator itemsIterator(boolean ascending, Stack stack) {
    return ascending ? new TreeIterator(stack) : new TreeIteratorDown(stack);
  }

  /**
   * Returns an array, containing the elements of the tree in
   * ascending order.
   *
   * @return   an array, containing the elements of the tree
   */
  public BinTreeItem[] toItemArray() {
    BinTreeItem[] result = new BinTreeItem[count];
    getAllNodesUp(result, root, 0);
    return result;
  }

  /**
   * Returns an array, containing the elements of the tree.
   *
   * @param   ascending  the order ot elemets
   * @return   an array, containing the elements of the tree
   */
  public BinTreeItem[] toItemArray(boolean ascending) {
    BinTreeItem[] result = new BinTreeItem[count];

    if (ascending) {
      getAllNodesUp(result, root, 0);
    } else {
      getAllNodesDown(result, root, 0);
    }

    return result;
  }

  /**
   * Returns a snapshot enumeraiton, containing the elements of the tree in
   * ascending order.
   *
   * @return   an enumeraiton, containing the elements of the tree
   */
  public SnapShotEnumeration itemsEnumeration() {
    return new ArrayEnumeration(toItemArray());
  }

  /**
   * Returns a snapshot enumeraiton, containing the elements of the tree.
   *
   * @param   ascending  the order ot elemets
   * @return   an enumeraiton, containing the elements of the tree
   */
  public SnapShotEnumeration itemsEnumeration(boolean ascending) {
    return new ArrayEnumeration(toItemArray(ascending));
  }

  /**
   * Perform some operations over all tree nodes in ascending order.
   *
   * @param   performer  operation performer
   */
  public void perform(TreePerformer performer) {
    performUp(performer, root);
  }

  /**
   * Perform some operations over all tree nodes.
   *
   * @param   ascending  the order ot elemets
   */
  public void perform(TreePerformer performer, boolean ascending) {
    if (ascending) {
      performUp(performer, root);
    } else {
      performDown(performer, root);
    }
  }

  /**
   * Puts the item in the tree.
   *
   * @param   item  new item
   * @return     item if item with the same kay exists in the tree,
   *             otherwise null.
   */
  public BinTreeItem putItem(BinTreeItem item) {
    recValue = item;
    searchKey = item.getKey();
    recValue.clearItem();
    root = insert(root);
    recValue = null;
    searchKey = null;

    if (resultValue != null) {
      resultValue.clearItem();
      BinTreeItem result = resultValue;
      resultValue = null;
      return result;
    }

    count++;
    return null;
  }

  /**
   * Search the tree for element with spicific key.
   *
   * @param   key  search key
   * @return  null if the tree do not contains the element with this key.
   */
  public BinTreeItem getItem(Comparable key) {
    BinTreeItem tmp = root;
    int cmp;

    for (;;) {
      if (tmp == null) {
        return null;
      }

      cmp = tmp.getKey().compareTo(key);

      if (cmp > 0) {
        tmp = tmp.getLeft();
      } else if (cmp < 0) {
        tmp = tmp.getRight();
      } else {
        return tmp;
      }
    } 
  }

  /**
   * Remove the element with spicific key from tree.
   *
   * @param   key  search key
   * @return  null if the tree do not contains the element with this key.
   */
  public BinTreeItem removeItem(Comparable key) {
    searchKey = key;
    root = delete(root);
    searchKey = null;

    if (resultValue != null) {
      count--;
      resultValue.clearItem();
      BinTreeItem result = resultValue;
      resultValue = null;
      return result;
    } else {
      return null;
    }
  }

  /**
   * Gets the element with min key in tree.
   *
   * @return  null if the tree is empty.
   */
  public BinTreeItem minItem() {
    if (root == null) {
      return null;
    }

    BinTreeItem tmp = root;

    while (tmp.getLeft() != null) {
      tmp = tmp.getLeft();
    }

    return tmp;
  }

  /**
   * Gets the element with max key in tree.
   *
   * @return  null if the tree is empty.
   */
  public BinTreeItem maxItem() {
    if (root == null) {
      return null;
    }

    BinTreeItem tmp = root;

    while (tmp.getRight() != null) {
      tmp = tmp.getRight();
    }

    return tmp;
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

    BinTreeItem parent = null;
    BinTreeItem tmp = root;

    while (tmp.getLeft() != null) {
      parent = tmp;
      tmp = tmp.getLeft();
    }

    if (parent == null) {
      root = root.getRight();
    } else {
      parent.setLeft(tmp.getRight());
    }

    count--;
    tmp.clearItem();
    return tmp;
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

    BinTreeItem parent = null;
    BinTreeItem tmp = root;

    while (tmp.getRight() != null) {
      parent = tmp;
      tmp = tmp.getRight();
    }

    if (parent == null) {
      root = root.getLeft();
    } else {
      parent.setRight(tmp.getLeft());
    }

    count--;
    tmp.clearItem();
    return tmp;
  }

  /**
   * Creates and returns a deep copy of this object.<p>
   *
   * @return  a clone of this instance.
   */
  public Object deepClone() {
    throw new NotSupportedException("In base structures all clone operation are deep clone.");
  }

  /**
   * Returns a snapshot enumeraiton, containing the elements of the datastructure.
   *
   * @return   an enumeraiton, containing the elements of the datastructure
   */
  public SnapShotEnumeration elementsEnumeration() {
    return new ArrayEnumeration(toItemArray());
  }

  /**
   * Returns iterator of the components of this datastructure.
   *
   * @return     iterator of the components of this datastructure
   */
  public RootIterator elementsIterator() {
    return new TreeIterator();
  }

  /**
   * Returns an array, containing the wrappers of the datastructure.
   *
   * @return   an array, containing the wrappers of the datastructure
   */
  public Pointer[] toPointerArray() {
    throw new NotSupportedException("In base structures there is no wrappers.");
  }

  /**
   * Returns an array, containing the elements of the datastructure.
   *
   * @return   an array, containing the elements of the datastructure
   */
  public Object[] toArray() {
    return toItemArray();
  }

  //************************************************************************
  //************************ protected methods *****************************
  //************************************************************************
  protected Object getNewInstance() {
    BaseBinaryTree tree = (BaseBinaryTree) super.clone();
    tree.root = null;
    tree.count = 0;
    return tree;
  }

  /**
   * Recurrent version of equlas() method.
   *
   * @param   e  enumeraion for comparison.
   * @param   current  start tree node
   * @return     true if e.nextElement() is equals to current.
   */
  protected boolean equals(ForwardIterator e, BinTreeItem current) {
    if (current == null) {
      return true;
    }

    if (!equals(e, current.getLeft())) {
      return false;
    }

    if (!current.equals(e.next())) {
      return false;
    }

    return equals(e, current.getRight());
  }

  /**
   * Copy tree nodes in array.
   *
   * @param   result  target array
   * @param   current  source node
   * @param   deep  start index of array
   * @return     end index of array
   */
  protected int getAllNodesDown(BinTreeItem[] result, BinTreeItem current, int deep) {
    if (current == null) {
      return deep;
    }

    deep = getAllNodesDown(result, current.getRight(), deep);
    result[deep++] = current;
    deep = getAllNodesDown(result, current.getLeft(), deep);
    return deep;
  }

  /**
   * Copy tree nodes in array.
   *
   * @param   result  target array
   * @param   current  source node
   * @param   deep  start index of array
   * @return     end index of array
   */
  protected int getAllNodesUp(BinTreeItem[] result, BinTreeItem current, int deep) {
    if (current == null) {
      return deep;
    }

    deep = getAllNodesUp(result, current.getLeft(), deep);
    result[deep++] = current;
    deep = getAllNodesUp(result, current.getRight(), deep);
    return deep;
  }

  /**
   * Perform operation on tree nodes.
   *
   * @param   performer  operation performer
   * @param   current  start tree node
   * @return     true if performer has canceled performing
   */
  protected boolean performUp(TreePerformer performer, BinTreeItem current) {
    if (current == null) {
      return false;
    }

    if (performUp(performer, current.getLeft())) {
      return true;
    }

    if (performer.perform(current)) {
      return true;
    }

    return performUp(performer, current.getRight());
  }

  /**
   * Perform operation on tree nodes.
   *
   * @param   performer  operation performer
   * @param   current  start tree node
   * @return     true if performer has canceled performing
   */
  protected boolean performDown(TreePerformer performer, BinTreeItem current) {
    if (current == null) {
      return false;
    }

    if (performDown(performer, current.getRight())) {
      return true;
    }

    if (performer.perform(current)) {
      return true;
    }

    return performDown(performer, current.getLeft());
  }

  /**
   * Inserts searchNode in node tree.
   *
   * @param   root  start node
   * @return     new subtree
   */
  protected BinTreeItem insert(BinTreeItem root) {
    if (root == null) {
      return recValue;
    }

    int cmp = root.getKey().compareTo(searchKey);

    if (cmp > 0) {
      root.setLeft(insert(root.getLeft()));
    } else if (cmp < 0) {
      root.setRight(insert(root.getRight()));
    } else {
      resultValue = root;
      recValue.setLeft(root.getLeft());
      recValue.setRight(root.getRight());
      return recValue;
    }

    return root;
  }

  /**
   * Deletes node with key equals to searchKey from tree.
   *
   * @param   root  start node
   * @return     new subtree
   */
  protected BinTreeItem delete(BinTreeItem root) {
    if (root == null) {
      return null;
    }

    int cmp = searchKey.compareTo(root.getKey());

    if (cmp < 0) {
      root.setLeft(delete(root.getLeft()));
    } else if (cmp > 0) {
      root.setRight(delete(root.getRight()));
    } else {
      resultValue = root;

      if (root.getLeft() == null) {
        return root.getRight();
      }

      if (root.getRight() == null) {
        return root.getLeft();
      }

      BinTreeItem parent = null;
      BinTreeItem tmp = root.getRight();

      while (tmp.getLeft() != null) {
        parent = tmp;
        tmp = tmp.getLeft();
      }

      tmp.setLeft(root.getLeft());

      if (parent != null) {
        parent.setLeft(tmp.getRight());
        tmp.setRight(root.getRight());
      }

      root = tmp;
    }

    return root;
  }

  /**
   * Copy nodes in new tree.
   *
   * @param   tree  new tree
   * @param   current  start node of this tree
   */
  protected void clone(BaseBinaryTree tree, BinTreeItem current) {
    if (current == null) {
      return;
    }

    tree.putItem((BinTreeItem) current.clone());
    clone(tree, current.getLeft());
    clone(tree, current.getRight());
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

    return depth + t + current + "\n" + getNode(depth + "    |", current.getLeft(), true) + getNode(depth + "    ", current.getRight(), false);
  }

  /**
   * Gets height ot tree.
   *
   * @return     height
   */
  public int height() {
    return height(root);
  }

  /**
   * Gets height ot tree.
   *
   * @param   current  start node
   * @return     height
   */
  protected int height(BinTreeItem current) {
    if (current == null) {
      return 0;
    }

    int hl = height(current.getLeft()) + 1;
    int hr = height(current.getRight()) + 1;
    return hl > hr ? hl : hr;
  }

  /**
   * Calculates size of tree.
   *
   * @return     size
   */
  public int size_() {
    return size_(root);
  }

  /**
   * Calculates size of tree.
   *
   * @param   current  start node
   * @return     size
   */
  protected int size_(BinTreeItem current) {
    if (current == null) {
      return 0;
    }

    return size_(current.getLeft()) + size_(current.getRight()) + 1;
  }

  /**
   * Check if this tree structure is binary search tree structure.
   *
   * @return     true if no problem
   */
  public boolean check() {
    if (root == null) {
      return true;
    }

    return check(root);
  }

  /**
   * Check if this tree structure is binary search tree structure.
   *
   * @param   current  start node.
   * @return     true if no problem
   */
  protected boolean check(BinTreeItem current) {
    if (current.getLeft() != null) {
      if (current.getKey().compareTo(current.getLeft().getKey()) <= 0) {
        return false;
      }

      if (!check(current.getLeft())) {
        return false;
      }
    }

    if (current.getRight() != null) {
      if (current.getKey().compareTo(current.getRight().getKey()) >= 0) {
        return false;
      }

      if (!check(current.getRight())) {
        return false;
      }
    }

    return true;
  }

  protected class TreeIterator extends TreeIteratorDown {
    
    static final long serialVersionUID = -7766057378584502390L;

    public TreeIterator() {
      this(new Stack());
    }

    public TreeIterator(Stack stack) {
      super(true);
      this.stack = stack;
      BinTreeItem tmp = root;

      while (tmp != null) {
        stack.push(tmp);
        tmp = tmp.getLeft();
      }
    }

    public Object next() {
      BinTreeItem node = (BinTreeItem) stack.pop();

      for (BinTreeItem t = node.getRight(); t != null; t = t.getLeft()) {
        stack.push(t);
      } 

      return node;
    }

  }

  protected class TreeIteratorDown implements ForwardIterator { //$JL-CLONE$
    
    static final long serialVersionUID = -571447147356773231L;

    protected Stack stack;
    protected BinTreeItem end;

    public TreeIteratorDown() {
      this(new Stack());
    }

    public TreeIteratorDown(Stack stack) {
      this.stack = stack;
      BinTreeItem tmp = root;

      while (tmp != null) {
        stack.push(tmp);
        tmp = tmp.getRight();
      }
    }

    protected TreeIteratorDown(boolean e) {

    }

    public Object add(Object o) {
      throw new IteratorException("Operation is not supported!");
    }

    public Object change(Object o) {
      throw new IteratorException("Operation is not supported!");
    }

    public Object remove() {
      throw new IteratorException("Operation is not supported!");
    }

    public Object insert(Object object) {
      throw new IteratorException("Operation is not supported!");
    }

    public Object get() {
      return stack.top();
    }

    public int size() {
      throw new IteratorException("Operation is not supported!");
    }

    public boolean isInsertable() {
      return false;
    }

    public boolean isRemoveable() {
      return false;
    }

    public boolean isChangeable() {
      return false;
    }

    public boolean isAddable() {
      return false;
    }

    public boolean isAtBegin() {
      return true;
    }

    public boolean isAtEnd() {
      return stack.top() == end;
    }

    public RootDataStructure getDataStructure() {
      return BaseBinaryTree.this;
    }

    public Object next() {
      BinTreeItem node = (BinTreeItem) stack.pop();

      for (BinTreeItem t = node.getLeft(); t != null; t = t.getRight()) {
        stack.push(t);
      } 

      return node;
    }

    public Object next(int n) {
      for (int i = 0; i < n; i++) {
        next();
      } 

      return next();
    }

    public void setStartFromIterator(RootIterator iterator) {
      stack = (Stack) ((TreeIteratorDown) iterator).stack.clone();
    }

    public void setEndFromIterator(RootIterator iterator) {
      end = (BinTreeItem) iterator.get();
    }

  }

}

