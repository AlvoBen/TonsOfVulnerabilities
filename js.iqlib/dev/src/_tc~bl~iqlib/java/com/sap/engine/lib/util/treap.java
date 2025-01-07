package com.sap.engine.lib.util;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.sap.engine.lib.util.base.*;
import com.sap.engine.lib.util.iterators.ArrayEnumeration;
import com.sap.engine.lib.util.iterators.ForwardIterator;
import com.sap.engine.lib.util.iterators.RootIterator;
import com.sap.engine.lib.util.iterators.SnapShotEnumeration;

/**
 * Randomized Binary Search Tree. Most operation goes O(log(n)).
 * The advantage of trees is that elements can be enumerated sorted.
 *
 * WARNING: This class is not synchronized.
 *  If tree is used in multithreaded environment every method has to be
 *  called in synchronized block
 *
 * Pooling: This class can be connected to PoolObject easy.
 *          Then all wrapper are taken from pool in put methods,
 *          end put back in pool on remove methods.
 *          Be careful when using methods of parent class
 *          they do not use pool, for example clear() do not put wrappers
 *          back to pool.
 *
 * Example for pooling:
 *    Treap tree = new Treap();
 *    PoolObject pool = new PoolObjectWithCreator(tree, 20);
 *    tree.setPool(pool);
 *
 * Example for synchronization:
 *
 *   Treap tree = new Treap();
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
public class Treap extends BaseTreap implements PoolInstanceCreator { //$JL-CLONE$

  static final long serialVersionUID = 4283654118731396307L;
  /**
   *  Pool for faster memory allocation.
   */
  protected transient PoolObject pool;

  /**
   * Sets Object Pool for faster memory allocation.
   * if pool == null then pooling is thurned off.
   *
   * @param   pool  Object Pool
   */
  public void setPool(PoolObject pool) {
    this.pool = pool;
  }

  /**
   * Gets pool linked to this tree.
   *
   * @return     pool or null if there is no pool.
   */
  public PoolObject getPool() {
    return pool;
  }

  /**
   * Implementation of interface PoolInstanceCreator.
   *
   * @return     New instance
   */
  public Object newInstance() {
    return new Wrapper();
  }

  /**
   *  Gets wrapper from pool or create new one if there is no pool.
   *
   * @param   value  wrapped value
   * @return     new wrapper
   */
  protected Wrapper getWrapper(Comparable key, Object value) {
    if (pool == null) {
      return new Wrapper(key, value);
    } else {
      Wrapper wrapper = (Wrapper) pool.getObject();
      wrapper.setKey(key);
      wrapper.value = value;
      return wrapper;
    }
  }

  /**
   *  Put back wrapper in pool if there is pool.
   *
   * @param   wrapper  wrapper for release.
   */
  protected void releaseWrapper(Wrapper wrapper) {
    if (pool != null) {
      wrapper.setKey(null);
      wrapper.value = null;
      pool.releaseObject(wrapper);
    }
  }

  /**
   * Puts the value and key in the tree.
   *
   * @param   key  key
   * @param   value  value
   * @return     item if item with the same kay exists in the tree,
   *             otherwise null.
   */
  public Object put(Comparable key, Object value) {
    if (value == null) {
      throw new NullPointerException();
    }

    Wrapper result = (Wrapper) putItem(getWrapper(key, value));
    return result == null ? null : result.value;
  }

  /**
   * Search the tree for element with spicific key.
   *
   * @param   key  search key
   * @return  null if the tree do not contains the element with this key.
   */
  public Object get(Comparable key) {
    Wrapper result = (Wrapper) getItem(key);
    return result == null ? null : result.value;
  }

  /**
   * Remove the element with spicific key from tree.
   *
   * @param   key  search key
   * @return  null if the tree do not contains the element with this key.
   */
  public Object remove(Comparable key) {
    Wrapper wrapper = (Wrapper) removeItem(key);

    if (wrapper == null) {
      return null;
    }

    Object result = wrapper.value;
    releaseWrapper(wrapper);
    return result;
  }

  /**
   * Returns iterator of the elements of this tree.
   * The returned Iterator object will generate all items in this tree
   * in ascending order.
   *
   * Note: perform() method is faster than this method cause this method
   *       uses stack.
   *
   * @return     iterator of the components of this tree
   */
  public ForwardIterator elements() {
    return new TreeIterator();
  }

  /**
   * Returns iterator of the elements of this tree.
   *
   * Note: perform() method is faster than this method cause this method
   *       uses stack.
   *
   * @param   ascending  the order ot elemets
   * @return     iterator of the components of this tree
   */
  public ForwardIterator elements(boolean ascending) {
    return ascending ? (ForwardIterator) new TreeIterator() : new TreeIteratorDown();
  }

  /**
   * Returns iterator of the keys of this tree.
   * The returned Iterator object will generate all items in this tree
   * in ascending order.
   *
   * Note: perform() method is faster than this method cause this method
   *       uses stack.
   *
   * @return     iterator of the components of this tree
   */
  public ForwardIterator keys() {
    return new TreeKeysIterator();
  }

  /**
   * Returns iterator of the keys of this tree.
   *
   * Note: perform() method is faster than this method cause this method
   *       uses stack.
   *
   * @param   ascending  the order ot elemets
   * @return     iterator of the components of this tree
   */
  public ForwardIterator keys(boolean ascending) {
    return ascending ? (ForwardIterator) new TreeKeysIterator() : new TreeKeysIteratorDown();
  }

  /**
   * Returns an array, containing the elements of the tree in
   * ascending order.
   *
   * @return   an array, containing the elements of the tree
   */
  public Object[] getAllElements() {
    Object[] result = new Object[count];
    getAllValuesUp(result, root, 0);
    return result;
  }

  /**
   * Returns an array, containing the elements of the tree.
   *
   * @param   ascending  the order ot elemets
   * @return   an array, containing the elements of the tree
   */
  public Object[] getAllElements(boolean ascending) {
    Object[] result = new Object[count];

    if (ascending) {
      getAllValuesUp(result, root, 0);
    } else {
      getAllValuesDown(result, root, 0);
    }

    return result;
  }

  /**
   * Returns an array, containing the keys of the tree in
   * ascending order.
   *
   * @return   an array, containing the keys of the tree
   */
  public Comparable[] getAllKeys() {
    Comparable[] result = new Comparable[count];
    getAllKeysUp(result, root, 0);
    return result;
  }

  /**
   * Returns an array, containing the keys of the tree.
   *
   * @param   ascending  the order ot keys
   * @return   an array, containing the keys of the tree
   */
  public Comparable[] getAllKeys(boolean ascending) {
    Comparable[] result = new Comparable[count];

    if (ascending) {
      getAllKeysUp(result, root, 0);
    } else {
      getAllKeysDown(result, root, 0);
    }

    return result;
  }

  /**
   * Returns a snapshot enumeraiton, containing the elements of the tree in
   * ascending order.
   *
   * @return   an enumeraiton, containing the elements of the tree
   */
  public SnapShotEnumeration elementsEnumeration() {
    return new ArrayEnumeration(getAllElements());
  }

  /**
   * Returns a snapshot enumeraiton, containing the elements of the tree.
   *
   * @param   ascending  the order ot elemets
   * @return   an enumeraiton, containing the elements of the tree
   */
  public SnapShotEnumeration elementsEnumeration(boolean ascending) {
    return new ArrayEnumeration(getAllElements(ascending));
  }

  /**
   * Returns a snapshot enumeraiton, containing the keys of the tree in
   * ascending order.
   *
   * @return   an enumeraiton, containing the keys of the tree
   */
  public SnapShotEnumeration keysEnumeration() {
    return new ArrayEnumeration(getAllKeys());
  }

  /**
   * Returns a snapshot enumeraiton, containing the keys of the tree.
   *
   * @param   ascending  the order ot keys
   * @return   an enumeraiton, containing the keys of the tree
   */
  public SnapShotEnumeration keysEnumeration(boolean ascending) {
    return new ArrayEnumeration(getAllKeys(ascending));
  }

  /**
   * Perform some operations over all tree nodes in ascending order.
   *
   * @param   performer  operation performer
   */
  public void performOnElements(TreePerformer performer) {
    performOnValuesUp(performer, root);
  }

  /**
   * Perform some operations over all tree nodes.
   *
   * @param   ascending  the order ot elemets
   */
  public void performOnElements(TreePerformer performer, boolean ascending) {
    if (ascending) {
      performOnValuesUp(performer, root);
    } else {
      performOnValuesDown(performer, root);
    }
  }

  /**
   * Perform some operations over all tree keys in ascending order.
   *
   * @param   performer  operation performer
   */
  public void performOnKeys(TreePerformer performer) {
    performOnKeysUp(performer, root);
  }

  /**
   * Perform some operations over all tree keys.
   *
   * @param   performer  operation performer
   */
  public void performOnKeys(TreePerformer performer, boolean ascending) {
    if (ascending) {
      performOnKeysUp(performer, root);
    } else {
      performOnKeysDown(performer, root);
    }
  }

  /**
   * Gets the element with min key in tree.
   *
   * @return  null if the tree is empty.
   */
  public Object min() {
    Wrapper result = (Wrapper) minItem();
    return result == null ? null : result.value;
  }

  /**
   * Gets the element with max key in tree.
   *
   * @return  null if the tree is empty.
   */
  public Object max() {
    Wrapper result = (Wrapper) maxItem();
    return result == null ? null : result.value;
  }

  /**
   * Gets the min key in tree.
   *
   * @return  null if the tree is empty.
   */
  public Comparable minKey() {
    Wrapper result = (Wrapper) minItem();
    return result == null ? null : result.getKey();
  }

  /**
   * Gets the max key in tree.
   *
   * @return  null if the tree is empty.
   */
  public Comparable maxKey() {
    Wrapper result = (Wrapper) maxItem();
    return result == null ? null : result.getKey();
  }

  /**
   * Creates and returns a deep copy of this object.<p>
   *
   * @return  a clone of this instance.
   */
  public Object deepClone() {
    Treap tree = (Treap) getNewInstance();
    deepClone(tree, root);
    return tree;
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
    throw new NotSupportedException("Not Imeplemented.There is no reason to impement this method.");
  }

  /**
   * Returns an array, containing the elements of the datastructure.
   *
   * @return   an array, containing the elements of the datastructure
   */
  public Object[] toArray() {
    return getAllElements();
  }

  //************************************************************************
  //************************ protected methods *****************************
  //************************************************************************
  /**
   * Copy nodes in new tree.
   *
   * @param   tree  new tree
   * @param   current  start node of this tree
   */
  protected void deepClone(BaseBinaryTree tree, BinTreeItem current) {
    if (current == null) {
      return;
    }

    tree.putItem(getWrapper((Comparable) ((DeepCloneable) current.getKey()).deepClone(), ((DeepCloneable) ((Wrapper) current).value).deepClone()));
    clone(tree, current.getLeft());
    clone(tree, current.getRight());
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

    tree.putItem(getWrapper(current.getKey(), ((Wrapper) current).value));
    clone(tree, current.getLeft());
    clone(tree, current.getRight());
  }

  /**
   * Perform operation on tree nodes.
   *
   * @param   performer  operation performer
   * @param   current  start tree node
   * @return     true if performer has canceled performing
   */
  protected boolean performOnKeysUp(TreePerformer performer, BinTreeItem current) {
    if (current == null) {
      return false;
    }

    if (performOnKeysUp(performer, current.getLeft())) {
      return true;
    }

    if (performer.perform(((Wrapper) current).getKey())) {
      return true;
    }

    return performOnKeysUp(performer, current.getRight());
  }

  /**
   * Perform operation on tree nodes.
   *
   * @param   performer  operation performer
   * @param   current  start tree node
   * @return     true if performer has canceled performing
   */
  protected boolean performOnKeysDown(TreePerformer performer, BinTreeItem current) {
    if (current == null) {
      return false;
    }

    if (performOnKeysDown(performer, current.getRight())) {
      return true;
    }

    if (performer.perform(((Wrapper) current).getKey())) {
      return true;
    }

    return performOnKeysDown(performer, current.getLeft());
  }

  /**
   * Perform operation on tree nodes.
   *
   * @param   performer  operation performer
   * @param   current  start tree node
   * @return     true if performer has canceled performing
   */
  protected boolean performOnValuesUp(TreePerformer performer, BinTreeItem current) {
    if (current == null) {
      return false;
    }

    if (performOnValuesUp(performer, current.getLeft())) {
      return true;
    }

    if (performer.perform(((Wrapper) current).value)) {
      return true;
    }

    return performOnValuesUp(performer, current.getRight());
  }

  /**
   * Perform operation on tree nodes.
   *
   * @param   performer  operation performer
   * @param   current  start tree node
   * @return     true if performer has canceled performing
   */
  protected boolean performOnValuesDown(TreePerformer performer, BinTreeItem current) {
    if (current == null) {
      return false;
    }

    if (performOnValuesDown(performer, current.getRight())) {
      return true;
    }

    if (performer.perform(((Wrapper) current).value)) {
      return true;
    }

    return performOnValuesDown(performer, current.getLeft());
  }

  /**
   * Copy tree values in array.
   *
   * @param   result  target array
   * @param   current  source node
   * @param   deep  start index of array
   * @return     end index of array
   */
  protected int getAllValuesDown(Object[] result, BinTreeItem current, int deep) {
    if (current == null) {
      return deep;
    }

    deep = getAllValuesDown(result, current.getRight(), deep);
    result[deep++] = ((Wrapper) current).value;
    deep = getAllValuesDown(result, current.getLeft(), deep);
    return deep;
  }

  /**
   * Copy tree values in array.
   *
   * @param   result  target array
   * @param   current  source node
   * @param   deep  start index of array
   * @return     end index of array
   */
  protected int getAllValuesUp(Object[] result, BinTreeItem current, int deep) {
    if (current == null) {
      return deep;
    }

    deep = getAllValuesUp(result, current.getLeft(), deep);
    result[deep++] = ((Wrapper) current).value;
    deep = getAllValuesUp(result, current.getRight(), deep);
    return deep;
  }

  /**
   * Copy tree keys in array.
   *
   * @param   result  target array
   * @param   current  source node
   * @param   deep  start index of array
   * @return     end index of array
   */
  protected int getAllKeysDown(Comparable[] result, BinTreeItem current, int deep) {
    if (current == null) {
      return deep;
    }

    deep = getAllKeysDown(result, current.getRight(), deep);
    result[deep++] = ((Wrapper) current).getKey();
    deep = getAllKeysDown(result, current.getLeft(), deep);
    return deep;
  }

  /**
   * Copy tree keys in array.
   *
   * @param   result  target array
   * @param   current  source node
   * @param   deep  start index of array
   * @return     end index of array
   */
  protected int getAllKeysUp(Comparable[] result, BinTreeItem current, int deep) {
    if (current == null) {
      return deep;
    }

    deep = getAllKeysUp(result, current.getLeft(), deep);
    result[deep++] = ((Wrapper) current).getKey();
    deep = getAllKeysUp(result, current.getRight(), deep);
    return deep;
  }

  protected class TreeIterator extends BaseBinaryTree.TreeIterator {
    
    static final long serialVersionUID = -8631818606897020487L;

    public boolean isChangeable() {
      return true;
    }

    public Object change(Object o) {
      if (o == null) {
        throw new NullPointerException();
      }

      Object result = ((Wrapper) stack.top()).value;
      ((Wrapper) stack.top()).value = o;
      return result;
    }

    public Object next() {
      return ((Wrapper) super.next()).value;
    }

  }

  protected class TreeIteratorDown extends BaseBinaryTree.TreeIteratorDown {
    
    static final long serialVersionUID = 3072601618050055901L; 

    public boolean isChangeable() {
      return true;
    }

    public Object change(Object o) {
      if (o == null) {
        throw new NullPointerException();
      }

      Object result = ((Wrapper) stack.top()).value;
      ((Wrapper) stack.top()).value = o;
      return result;
    }

    public Object next() {
      return ((Wrapper) super.next()).value;
    }

  }

  protected class TreeKeysIterator extends BaseBinaryTree.TreeIterator {
    
    static final long serialVersionUID = -2375609933470373899L;

    public Object next() {
      return ((Wrapper) super.next()).getKey();
    }

  }

  protected class TreeKeysIteratorDown extends BaseBinaryTree.TreeIteratorDown {
    
    static final long serialVersionUID = 8137391657049413757L;

    public Object next() {
      return ((Wrapper) super.next()).getKey();
    }

  }

  static protected class Wrapper extends TreapItemAdapter { //$JL-CLONE$
    
    static final long serialVersionUID = 2454422849938661142L;

    protected Object value;

    public Wrapper() {

    }

    public Wrapper(Comparable key, Object value) {
      this.key = key;
      this.value = value;
    }

    public String toString() {
      return key + " = " + value;
    }

    public boolean equals(Object o) {
      if (!(o instanceof Wrapper)) {
        return false;
      }

      Wrapper tmp = (Wrapper) o;
      return value.equals(tmp.value) && (key.compareTo(tmp.key) == 0);
    }
    
    public int hashCode() {
	    int result = 17;
	    result = 37 * result + ((value ==  null) ? 0 : value.hashCode());
	    result = 37 * result + ((key == null) ? 0 : key.hashCode());
	    return result;
    }
    
    private void writeObject(ObjectOutputStream oos) throws NotSerializableException {
	    try {
	      oos.defaultWriteObject();
	    } catch (IOException ioex) {
	      throw new NotSerializableException("Cannot serialize class " + this.getClass().getName() + ". Error is " + ioex.toString());
	    }
	  }
	  
	  private void readObject(ObjectInputStream oos) throws NotSerializableException {
	    try {
	      oos.defaultReadObject();
	    } catch (IOException ioex) {
	      throw new NotSerializableException("Cannot deserialize class " + this.getClass().getName() + ". Error is " + ioex.toString());
	    } catch (ClassNotFoundException cnfe) {
	      throw new NotSerializableException("Cannot deserialize class " + this.getClass().getName() + ". Error is " + cnfe.toString());
	    }
	      
	  }

  }

}

