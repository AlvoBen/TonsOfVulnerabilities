/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.util;

import com.sap.engine.lib.util.base.Pointer;
import com.sap.engine.lib.util.iterators.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * This class implements Set using hashtable structure.<p>
 *
 *
 * Initial capacity and load factor are the parameteres of Set instance
 * that affect its performance.Capacity parameter specifies the number
 * of buckets in set. Initial capacity is the capacity at the time the set
 * is created. Set is open, i.e. in case of "hash collision", a single
 * bucket stores multiple entries, which must be searched sequentially.
 * Load factor indicates number of entries that are allowed in hash table
 * before its capacity is automatically increased. When the number of
 * entries in the set exceeds the product of the load factor and the
 * current capacity, the capacity is increased by calling the rehash method.<p>
 *
 * Generally, the default load factor (.75) offers a good tradeoff between
 * time and space costs.  Higher values decrease the space overhead but
 * increase the time cost to look up an entry (which is reflected in most
 * Set operations, including add, contains and remove).<p>
 *
 * The initial capacity controls a tradeoff between wasted space and the
 * need for rehash operations, which are time-consuming. Rehash operations
 * will not occur if the initial capacity is greater than the maximum number
 * of entries the Set will contain divided by its load factor. However,
 * setting the initial capacity too high can waste space.<p>
 *
 * Creating Set with large capacity may be appropriate if many entries are
 * to be inserted into it. Thus entries may be inserted more efficiently
 * than if automatic rehashing is performed when table resizing is necessary.<p>
 *
 *
 * WARNING: This class is not synchronized.<p>
 *
 * <b>Note</b>: The fastest way to traverse the set is by Enumeration returned by the
 *       elements() method.<p>
 *
 * @author Nikola Arnaudov
 * @version 1.0
 *
 */
public class Set extends AbstractDataStructure {

  static final long serialVersionUID = 6323261425323123690L;
  /**
   * Default load factor for the set.<p>
   */
  public static final float LOAD_FACTOR = 0.75f;
  /**
   * Default initial capacity for the set.<p>
   */
  public static final int INITIAL_CAPACITY = 13;
  /**
   * Default grow step (newSize = oldSize * growStep) for the set.<p>
   */
  public static final int GROW_STEP = 2;
  /**
   * Last element in the list.<p>
   */
  protected static final int LAST = -1;
  /**
   * Grow step (newSize = oldSize * growStep).<p>
   */
  protected int growStep;
  /**
   * Grow step in the simple number database.<p>
   */
  protected int growSimpl;
  /**
   * Current loaf factor (0.0, 1.0].<p>
   */
  protected float loadFactor;
  /**
   * Holder for hash function.<p>
   */
  protected IntHashHolder hasher;
  /**
   * Index in the simple number database.<p>
   */
  protected int simplIndex;
  /**
   * Limit for the hash table (limit = capacity * loadFactor).<p>
   */
  protected int limit;
  /**
   * Capacity of the hash table.<p>
   */
  protected int capacity;
  /**
   * Pointer to list of free slots.<p>
   */
  transient protected int nextFree;
  /**
   * Keys.<p>
   */
  transient protected Object keys[];
  /**
   * Pointer to next slot in case of collision.<p>
   */
  transient protected int nextPtr[];

  /**
   * Constructs a new, empty set with a default capacity grow step and load
   * factor. which are 13, 2, .75.<p>
   */
  public Set() {
    this(INITIAL_CAPACITY, GROW_STEP, LOAD_FACTOR, new IntHashHolderImpl());
  }

  /**
   * Constructs a new, empty hashtable with a specific capacity and default capacity grow step and load
   * factor. which are 2, .75.<p>
   *
   * @param   initialCapacity the specified initial capacity of the hashtable.
   */
  public Set(int initialCapacity) {
    this(initialCapacity, GROW_STEP, LOAD_FACTOR, new IntHashHolderImpl());
  }

  /**
   * Constructs a new, empty set with specified initial
   * capacity, grow step and load factor.<p>
   *
   * @param   initialCapacity the specified initial capacity of the set.
   * @param   growStep  grow step of the set.
   * @param   loadFactor  load factor of set.
   * @param   hasher a hash function.
   */
  public Set(int initialCapacity, int growStep, float loadFactor, IntHashHolder hasher) {
    if ((loadFactor > 1.0) || (loadFactor <= 0)) {
      throw new IllegalArgumentException("Load Factor = " + loadFactor);
    }

    if (growStep <= 1) {
      throw new IllegalArgumentException("Grow step = " + growStep);
    }

    this.growStep = growStep;

    if (growStep == 2) {
      growSimpl = 4;
    } else if (growStep < 10) {
      growSimpl = growStep + 4;
    } else {
      growSimpl = 13;
    }

    this.loadFactor = loadFactor;
    this.hasher = hasher;
    simplIndex = 0;
    init(initialCapacity);
  }

  /**
   * Returns an enumeration of the elements in this set.
   *
   * While traversing enumeration the set must not be modified.<p>
   *
   * @return  enumeration of the keys in this set.
   */
  public Enumeration elements() {
    return new Enumeration() {
      
      private int i = keys.length;
      private int counter = 0;
      
      public boolean hasMoreElements() {
        return counter < count;
      }
      
      public Object nextElement() {
        while (--i >= 0) {
          if (keys[i] != null) {
            counter++;
            return keys[i];
          }
        }
        
        throw new NoSuchElementException();
      }
    
    };
  }

  /**
   * Returns enumeration of the components of this list.
   * The returned SnapShotEnumeration object will generate
   * all items in this list. Changes to this object will not
   * affect the underlying data structure.<p>
   *
   * @return   an enumeration of the components of this list.
   */
  public SnapShotEnumeration elementsEnumeration() {
    return new ArrayEnumeration(toArray());
  }

  /**
   * Returns an array of the elements in this set.<p>
   *
   * @return  array of the elements in this set.
   */
  public Object[] toArray() {
    return toArray(new Object[count]);
  }

  /**
   * Returns an array containing all of the elements in this set
   * whose runtime type is that of the specified array.<p>
   *
   * @param      result  an array of a specific runtime type.
   * @exception  ArrayStoreException  if the runtime type of the specified array
   *             is not a supertype of the runtime type of every element in this set.
   * @return     array of the elements in this set.
   */
  public Object[] toArray(Object[] result) {
    int index = 0;
    
    if (result.length < count) {
      result = (Object[])Array.newInstance(result.getClass().getComponentType(), count);
    }
    
    for (int i = keys.length; --i >= 0; ) {
      if (keys[i] != null) {
        result[index++] = keys[i];
      }
    }
    
    return result;
  }

  /**
   * Tests if the specified element is in this set.<p>
   *
   * @param   key a possible element.
   * @return  true if and only if the specified element is in this
   *          set, false otherwise.
   */
  public boolean contains(Object key) {
    int pos = nextPtr[hasher.hash(key.hashCode()) % capacity];

    while (pos != LAST) {
      if (keys[pos - capacity].equals(key)) {
        return true;
      }

      pos = nextPtr[pos];
    }

    return false;
  }

  /**
   * Clears this set so that it contains no elements.<p>
   */
  public void clear() {
    for (int i = keys.length; --i >= 0; ) {
      nextPtr[i] = LAST;
      keys[i] = null;
    }
    
    for (int i = keys.length; i < capacity; i++) {
      nextPtr[i] = LAST;
    }
    
    for (int i = capacity; i < nextPtr.length; ) {
      nextPtr[i] = ++i;
    }
    
    nextFree = capacity;
    count = 0;
  }

  /**
   * Creates a shallow copy of this set. The whole structure of the
   * set is copied, but the elements are not cloned.
   * This is a relatively expensive operation.<p>
   *
   * @return  a clone of the set.
   */
  public Object clone() {
    Set result = (Set) super.clone();
    result.keys = new Object[keys.length];
    result.nextPtr = new int[nextPtr.length];
    System.arraycopy(nextPtr, 0, result.nextPtr, 0, nextPtr.length);
    System.arraycopy(keys, 0, result.keys, 0, keys.length);
    return result;
  }

  /**
   * Compares the specified object with this set for equality.<p>
   *
   * @param  object a specified object.
   * @return true if the specified object is equal to this set, false otherwise.
   */
  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }

    if (!(object instanceof Set)) {
      return false;
    }

    Set t = (Set) object;

    if (t.count != count) {
      return false;
    }

    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        if (!t.contains(keys[pos - capacity])) {
          return false;
        }
      } 
    } 

    return true;
  }
  
  
  public int hashCode() {
	  int result = 17;
	  result = 37 * result + count;
	  result = 37 * result + capacity;
	  result = 37 * result + arrayHashCode(nextPtr);
	  result = 37 * result + arrayHashCode(keys);
	  return result;
	}
	
	private int arrayHashCode(int[] array) {
	  if (array == null) {
	    return 0;
	  }
	  int result = 17;
	  for (int i = 0; i < array.length; i++) {
	    result = 37 * result + array[i];
	  }  
	  return result;
	}
	
	private int arrayHashCode(Object[] array) {
	  if (array == null) {
	    return 0;
	  }
	  int result = 17;
	  for (int i = 0; i < array.length; i++) {
	    result = 37 * result + array[i].hashCode();
	  }
	  return result;
	}

  /**
   * Reduces the size of set.
   * Shrink factor shows how much elements can be added before rehash.<p>
   *
   * For example:<p>
   *  If shrink factor is 1.0 then even one element can cause rehash.
   *  If shrink factor is 0.5 and set has 100 elements,
   *  100 more elements can be put before rehash.<p>
   *
   *  default shrinkFactor = 0.75.<p>
   */
  public void shrink() {
    shrink(LOAD_FACTOR);
  }

  /**
   * Reduces the size of set.
   * Shrink factor shows how much elements can be added before rehash.<p>
   *
   * For example:<p>
   *  If shirink factor is 1.0 then even one element can cause rehash.
   *  If shrink factor is 0.5 and set has 100 elements. You can put
   *  more 100 before rehash.<p>
   *
   * @param   shrinkFactor the shrink factor.
   */
  public void shrink(float shrinkFactor) {
    if ((shrinkFactor <= 0.0f) || (shrinkFactor > 1.0)) {
      throw new IllegalArgumentException("Shrink Factor = " + shrinkFactor);
    }
    
    int newCapacity = (int) (count / (loadFactor * shrinkFactor));
    long l = PrimeGenerator.getClosestPrime(newCapacity);
    simplIndex = (int) (l >> 32);
    newCapacity = (int) l;
    
    if (newCapacity < capacity) {
      Object[] oldKeys = keys;
      init(newCapacity);
      
      for (int i = oldKeys.length; --i >= 0; ) {
        if (oldKeys[i] != null) {
          putQuick(oldKeys[i]);
        }
      }
    }
  }

  /**
   * Add the specified element in this set.<p>
   *
   * @param   key an element of the set.
   * @return  true if the element is added in the set,
   *          false otherwise.
   */
  public boolean add(Object key) {
    if (count == limit) {
      rehash();
    }

    int pos = hasher.hash(key.hashCode()) % capacity;

    while (nextPtr[pos] != LAST) {
      pos = nextPtr[pos];

      if (keys[pos - capacity].equals(key)) {
        return false;
      }
    }

    nextPtr[pos] = nextFree;
    keys[nextFree - capacity] = key;
    nextFree = nextPtr[nextFree];
    nextPtr[nextPtr[pos]] = LAST;
    count++;
    return true;
  }

  /**
   * Adds the specified elements to this set. None of them can be <code>null</code>.
   * Otherwise a NullPointerException is thrown and the state of this set is left
   * unknown.<p>
   *
   * @param   elements  the elements to be included into the set.
   * @return  <code>true</code> if at least one element is added to the set,
   *          <code>false</code> otherwise.
   */
  public boolean addAll(Object[] elements) {
    int oldCount = count;
//    int overhead = elements.length - (limit - count);
//    
//    if (overhead > 0) {
//      Object[] oldKeys = keys;
//      init(capacity * growStep + overhead);
//      
//      for (int i = oldKeys.length; --i >= 0; ) {
//        if (oldKeys[i] != null) {
//          putQuick(oldKeys[i]);
//        }
//      }
//    }
    
    Elements:
    for (int i = elements.length; --i >= 0; ) {
      // To try another implementation comment the following
      // "if statement" and uncomment the overhead part.
      if (count == limit) {
        rehash();
      }
      
      int pos = hasher.hash(elements[i].hashCode()) % capacity;
      
      while (nextPtr[pos] != LAST) {
        pos = nextPtr[pos];
        
        if (keys[pos - capacity].equals(elements[i])) {
          continue Elements;
        }
      }
      
      nextPtr[pos] = nextFree;
      keys[nextFree - capacity] = elements[i];
      nextFree = nextPtr[nextFree];
      nextPtr[nextPtr[pos]] = LAST;
      count++;
    }
    
    return oldCount != count;
  }

  /**
   * Put method for internal use. Not  and does not perform check for
   * overflow.
   *
   * @param   key an element of the set.
   */
  protected void putQuick(Object key) {
    int pos = hasher.hash(key.hashCode()) % capacity;

    while (nextPtr[pos] != LAST) {
      pos = nextPtr[pos];
    }

    nextPtr[pos] = nextFree;
    keys[nextFree - capacity] = key;
    nextFree = nextPtr[nextFree];
    nextPtr[nextPtr[pos]] = LAST;
    count++;
  }

  /**
   * Removes the element from this set. This method does nothing
   * if the element is not in the set.<p>
   *
   * @param   key the element that needs to be removed.
   * @return  true if key has been removed,
   *          false otherwise (no such element).
   */
  public boolean remove(Object key) {
    int prevPos = hasher.hash(key.hashCode()) % capacity;
    int pos = nextPtr[prevPos];

    while (pos != LAST) {
      if (keys[pos - capacity].equals(key)) {
        keys[pos - capacity] = null;
        nextPtr[prevPos] = nextPtr[pos];
        nextPtr[pos] = nextFree;
        nextFree = pos;
        count--;
        return true;
      }

      prevPos = pos;
      pos = nextPtr[pos];
    }

    return false;
  }

  /**
   * Initial data structure for use.<p>
   *
   * @param   initialCapacity  the specified initial capacity.
   */
  protected void init(int initialCapacity) {
    if (growStep > 17) {
      this.capacity = (int) PrimeGenerator.getClosestPrime(initialCapacity);
    } else {
      long l = PrimeGenerator.getClosestPrime(initialCapacity, simplIndex);
      simplIndex = (int) (l >> 32) + growSimpl;
      this.capacity = (int) l;
    }

    limit = (int) (capacity * loadFactor);
    nextPtr = new int[capacity + limit];

    for (int i = capacity; --i >= 0; ) {
      nextPtr[i] = LAST;
    } 

    for (int i = capacity; i < nextPtr.length;) {
      nextPtr[i] = ++i;
    } 

    keys = new Object[limit];
    nextFree = capacity;
    count = 0;
  }

  /**
   * Increases the capacity of this set and internally reorganizes
   * it to accommodate and access its entries more efficiently.
   * This method is called automatically when the
   * number of keys in the set exceeds this set capacity
   * and load factor.<p>
   */
  protected void rehash() {
    Object[] oldKeys = keys;
    init(capacity * growStep);
    
    for (int i = oldKeys.length; --i >= 0; ) {
      putQuick(oldKeys[i]);
    }
  }

  /**
   * Returns a string representation of this set.<p>
   *
   * @return  a string representation of this set.
   */
  public String toString() {
    int c = 0;
    StringBuffer buf = new StringBuffer(2 * count + 1);
    buf.append("{");

    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        buf.append(keys[pos - capacity]);

        if (++c < count) {
          buf.append(", ");
        }
      } 
    } 

    buf.append("}");
    return buf.toString();
  }

  /**
   * Sets a hash function.<p>
   *
   * @param   hasher the hash function to be set.
   */
  public void setHasher(IntHashHolder hasher) {
    this.hasher = hasher;
  }

  // -----------------------------------------------------------
  // -------------- Some serialization magic -------------------
  // -----------------------------------------------------------
  /**
   * This method is used by Java serializer.<p>
   *
   * @param   stream an output stream.
   * @exception   IOException if an IO exception occur.
   */
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    
    for (int i = keys.length; --i >= 0; ) {
      if (keys[i] != null) {
        stream.writeObject(keys[i]);
      }
    }
  }

  /**
   * This method is used by Java serializer.<p>
   *
   * @param   stream an input stream.
   * @exception   IOException if an IO exception occur.
   * @exception   ClassNotFoundException if the class not found.
   */
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    nextPtr = new int[capacity + limit];

    for (int i = capacity; --i >= 0; ) {
      nextPtr[i] = LAST;
    } 

    for (int i = capacity; i < nextPtr.length;) {
      nextPtr[i] = ++i;
    } 

    keys = new Object[limit];
    nextFree = capacity;
    int size = count;
    count = 0;

    for (int i = size; --i >= 0; ) {
      putQuick(stream.readObject());
    } 
  }

  public Pointer[] toPointerArray() {
    throw new NotSupportedException("Use toArray() method instead!");
  }

  /**
   * Creates a deep copy of this set. The whole structure of the
   * set is copied and the elements are cloned.
   * This is a relatively expensive operation.<p>
   *
   * @return  a deep clone of the set.
   */
  public Object deepClone() {
    Set result = (Set) super.clone();
    result.keys = new Object[keys.length];
    result.nextPtr = new int[nextPtr.length];

    for (int i = 0; i < keys.length; i++) {
      result.keys[i] = ((DeepCloneable) keys[i]).deepClone();
    } 

    System.arraycopy(nextPtr, 0, result.nextPtr, 0, nextPtr.length);
    return result;
  }

  /**
   * Returns a ForwardIterator implementation over the elements of this Set.
   *
   * @return a RootIterator over the elements.
   */
  public RootIterator elementsIterator() {
    return new SetIterator();
  }

  // ---------------------------------------------------------
  // ---------------- The Iterator methods -------------------
  // ---------------------------------------------------------
  protected Object iterGet(RootIterator rootIter) {
    SetIterator iter = (SetIterator) rootIter;

    if (iter.counter >= iter.end) {
      throw new IteratorException("End of Iterator reached!");
    }

    int tmpI = iter.i;
    int tmpPos = iter.pos;

    while (tmpI < capacity) {
      if (tmpPos != LAST) {
        return keys[tmpPos - capacity];
      }

      tmpPos = nextPtr[++tmpI];
    }

    throw new IteratorException("End of Iterator reached!");
  }

  protected Object iterNext(RootIterator rootIter, int offset) {
    SetIterator iter = (SetIterator) rootIter;

    if (offset < 0) {
      throw new IteratorException("Invalid argument: " + offset + " < 0");
    }

    int newCounter = iter.counter + offset;

    if (newCounter >= iter.end) {
      throw new IteratorException("End of Iterator reached!");
    }

    while (iter.i < capacity) {
      while (iter.pos != LAST) {
        Object result = keys[iter.pos - capacity];
        iter.prevPos = iter.pos;
        iter.pos = nextPtr[iter.pos];

        if (iter.counter++ == newCounter) {
          return result;
        }
      }

      iter.i++;
      iter.prevPos = iter.i;
      iter.pos = nextPtr[iter.i];
    }

    throw new IteratorException("End of Iterator reached!");
  }

  protected Object iterRemove(RootIterator rootIter) {
    SetIterator iter = (SetIterator) rootIter;

    if (count == 0) {
      throw new IteratorException("Iterator is empty!");
    }

    if (iter.counter >= iter.end) {
      throw new IteratorException("End of Iterator reached!");
    }

    while (iter.i < capacity) {
      if (iter.pos != LAST) {
        int index = iter.pos - capacity;
        Object result = keys[index];
        keys[index] = null;
        nextPtr[iter.prevPos] = nextPtr[iter.pos];
        nextPtr[iter.pos] = nextFree;
        nextFree = iter.pos;
        iter.pos = nextPtr[iter.prevPos];
        count--;
        iter.counter++;
        return result;
      }

      iter.i++;
      iter.prevPos = iter.i;
      iter.pos = nextPtr[iter.i];
    }

    throw new IteratorException("End of Iterator reached!");
  }

  /**
   * An implementation of ForwardIterator for the Set.
   */
  protected class SetIterator implements ForwardIterator { //$JL-CLONE$

    static final long serialVersionUID = 8802670333440859083L;
    
    private int i = 0;
    private int prevPos = i;
    private int pos = nextPtr[i];
    private int start = 0;
    private int end = count;
    private int counter = start;

    public Object get() {
      return iterGet(this);
    }

    public boolean isAtBegin() {
      return (counter == start);
    }

    public boolean isAtEnd() {
      return (counter == end);
    }

    public RootDataStructure getDataStructure() {
      return Set.this;
    }

    public Object next() {
      return iterNext(this, 0);
    }

    public Object next(int n) {
      return iterNext(this, n);
    }

    public void setStartFromIterator(RootIterator iterator) {
      SetIterator si = (SetIterator) iterator;

      if (Set.this != si.getDataStructure()) {
        throw new IteratorException("An attempt to set start from an Iterator over a different Set instance!");
      }

      int newCounter = si.counter;
      int newPos = si.pos;
      int newPrevPos = si.prevPos;
      int newI = si.i;

      if (newCounter >= end) {
        throw new IteratorException("An attempt to set the start behind the end of the Iterator: " + newCounter + " >= " + end);
      }

      start = newCounter;
      counter = start;
      prevPos = newPrevPos;
      pos = newPos;
      i = newI;
    }

    public void setEndFromIterator(RootIterator iterator) {
      SetIterator si = (SetIterator) iterator;

      if (Set.this != si.getDataStructure()) {
        throw new IteratorException("An attempt to set end from an Iterator over a different Set instance!");
      }

      int newCounter = si.counter;

      if (newCounter < counter) {
        throw new IteratorException(" end ahead of the current position of the Iterator: " + newCounter + " < " + counter);
      }

      end = newCounter;
    }

    public Object add(Object obj) {
      throw new IteratorException("Operation is not supported!");
    }

    public Object change(Object obj) {
      throw new IteratorException("Operation is not supported!");
    }

    public Object remove() {
      return iterRemove(this);
    }

    public Object insert(Object obj) {
      throw new IteratorException("Operation is not supported!");
    }

    public int size() {
      return end - start;
    }

    public boolean isInsertable() {
      return false;
    }

    public boolean isRemoveable() {
      return true;
    }

    public boolean isChangeable() {
      return false;
    }

    public boolean isAddable() {
      return false;
    }

  }

}

