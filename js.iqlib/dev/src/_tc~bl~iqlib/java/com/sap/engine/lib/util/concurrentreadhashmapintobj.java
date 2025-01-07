/*
 * Copyright (c) 2002 by SAP Labs Sofia AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.lib.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * This class implements hashtable, which maps keys to values.<p>
 *
 *
 * Initial capacity and load factor are parameteres of
 * ConcurrentReadHashMap instance that affect its performance.
 * Capacity parameter specifies the number of buckets in hash table.
 * Initial capacity is the capacity at the time the hash table
 * is created.  Hash table is open, i.e. in case of "hash
 * collision", a single bucket stores multiple entries, which must be searched
 * sequentially.  Load factor indicates number of entries that are
 * allowed in hash table before its capacity is automatically increased.
 * When the number of entries in the hashtable exceeds the product of the load
 * factor and the current capacity, the capacity is increased by calling the
 * rehash method.<p>
 *
 * Generally, the default load factor (.75) offers a good tradeoff between
 * time and space costs.  Higher values decrease the space overhead but
 * increase the time cost to look up an entry (which is reflected in most
 * ConcurrentReadHashMap operations, including get and put).<p>
 *
 * The initial capacity controls a tradeoff between wasted space and the
 * need for rehash operations, which are time-consuming.
 * Rehash operations will not occur if the initial
 * capacity is greater than the maximum number of entries the
 * ConcurrentReadHashMap will contain divided by its load factor. However,
 * setting the initial capacity too high can waste space.<p>
 *
 * Creating ConcurrentReadHashMap with large capacity may be appropriate
 * if many entries are to be inserted into it.
 * Thus entries may be inserted more efficiently than if
 * automatic rehashing is performed when table resizing is necessary.<p>
 *
 *
 * WARNING: This class is not synchronized.<p>
 *
 * <b>Note</b>: The fastest way to traverse the hashtable is by Enumeration returned by
 *       the elements() method.<p>
 *
 * @author Nikola Arnaudov
 *
 * @version 1.0
 *
 */
public class ConcurrentReadHashMapIntObj implements Cloneable, Serializable {

  static final long serialVersionUID = -7671536490292739304L;
  /**
   * Default load factor for the hashtable.<p>
   */
  public static final float LOAD_FACTOR = 0.75f;
  /**
   * Default initial capacity for the hashtable.<p>
   */
  public static final int INITIAL_CAPACITY = 13;
  /**
   * Default grow step (newSize = oldSize * growStep) for the hashtable.<p>
   */
  public static final int GROW_STEP = 2;
  /**
   * Last element in the list.<p>
   */
  private static final int LAST = -1;
  /**
   * Grow step (newSize = oldSize * growStep).<p>
   */
  private int growStep;
  /**
   * Grow step in the simple number database.<p>
   */
  private int growSimpl;
  /**
   * Current loaf factor (0.0, 1.0].<p>
   */
  private float loadFactor;
  /**
   * Index in the simple number database.<p>
   */
  private int simplIndex;
  /**
   * Limit for the hash table (limit = capacity * loadFactor).<p>
   */
  private int limit;
  /**
   * Capacity of the hash table.<p>
   */
  private int capacity;
  /**
   * Number of elements in data structure
   */
  private int count;
  /**
   * Pointer to list of free slots.<p>
   */
  transient private int nextFree;


  private static class DataHolder {
    /**
     * Keys.<p>
     */
    public int keys[];
    /**
     * Values.<p>
     */
    public Object elements[];
    /**
     * Pointer to next slot in case of collision.<p>
     */
    public int nextPtr[];
    /**
     * Capacity of the hash table.<p>
     */
    public int capacity;
  }

  transient private DataHolder holder;

  /**
   * Constructs a new, empty hashtable with a default capacity grow step and load
   * factor. which are 13, 2, .75.<p>
   */
  public ConcurrentReadHashMapIntObj() {
    this(INITIAL_CAPACITY, GROW_STEP, LOAD_FACTOR);
  }

  /**
   * Constructs a new, empty hashtable with a specific capacity and default capacity grow step and load
   * factor. which are 2, .75.<p>
   *
   * @param   initialCapacity the specified initial capacity of the hashtable.
   */
  public ConcurrentReadHashMapIntObj(int initialCapacity) {
    this(initialCapacity, GROW_STEP, LOAD_FACTOR);
  }

  /**
   * Constructs a new, empty hashtable with specified initial
   * capacity, grow step and load factor.<p>
   *
   * @param   initialCapacity the specified initial capacity of the hashtable.
   * @param   growStep  grow step of the hashtable.
   * @param   loadFactor  load factor of hashtable.
   */
  public ConcurrentReadHashMapIntObj(int initialCapacity, int growStep, float loadFactor) {
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
    simplIndex = 0;

    holder = new DataHolder();

    init(holder, initialCapacity);
  }


  /**
   * Returns an enumeration of the keys in this hashtable.
   *
   * While traversing enumeration the hash table must not be modified.<p>
   *
   * @return  enumeration of the keys in this hashtable.
   */
  public EnumerationInt keys() {

    return new EnumerationInt() {

      int keys[] = holder.keys;
      int nextPtr[] = holder.nextPtr;

      private int i = 0;
      private int pos = nextPtr[i];
      private int counter = 0;

      public boolean hasMoreElements() {
        return counter < count;
      }

      public int nextElement() {
        while (i < capacity) {
          if (pos != LAST) {
            int result = keys[pos - capacity];
            pos = nextPtr[pos];
            counter++;
            return result;
          }

          pos = nextPtr[++i];
        }

        throw new NoSuchElementException();
      }

    };
  }

  /**
   * Returns an array of the keys in this hashtable.<p>
   *
   * @return  array of the keys in this hashtable.
   */
  public synchronized int[] getAllKeys() {

    int keys[] = holder.keys;
    int nextPtr[] = holder.nextPtr;

    int index = 0;
    int[] result = new int[count];

    for(int i = 0; i < capacity; i++) {
      for(int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        result[index++] = keys[pos - capacity];
      }
    }

    return result;
  }

  /**
   * Returns enumeration of the values in this hashtable.
   *
   * While traversing enumeration the hash table must not be modified.<p>
   *
   * @return  enumeration of the values in this hashtable.
   */
  public Enumeration elements() {
    return new Enumeration() {

      Object elements[] = holder.elements;
      int nextPtr[] = holder.nextPtr;

      private int i = 0;
      private int pos = nextPtr[i];
      private int counter = 0;

      public boolean hasMoreElements() {
        return counter < count;
      }

      public Object nextElement() {
        while (i < capacity) {
          if (pos != LAST) {
            Object result = elements[pos - capacity];
            pos = nextPtr[pos];
            counter++;
            return result;
          }

          pos = nextPtr[++i];
        }

        throw new NoSuchElementException();
      }

    };
  }

  /**
   * Returns an array of the values in this hashtable.<p>
   *
   * @return  array of the values in this hashtable.
   */
  public synchronized Object[] getAllValues() {

    Object elements[] = holder.elements;
    int nextPtr[] = holder.nextPtr;

    int index = 0;
    Object[] result = new Object[count];

    for(int i = 0; i < capacity; i++) {
      for(int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        result[index++] = elements[pos - capacity];
      }
    }

    return result;
  }

  /**
   * Tests if some key is mapped to the specified value in this hashtable.
   * This operation is more expensive than the containsKey method.
   *
   * Note that this method is identical in functionality to containsValue.<p>
   *
   * @param     value a value to search for.
   * @return    true if and only if some key maps to the
   *            value argument in this hashtable, 
   *            false otherwise.
   */
  public synchronized boolean contains(Object value) {

    Object elements[] = holder.elements;
    int nextPtr[] = holder.nextPtr;

    for(int i = 0; i < capacity; i++) {
      for(int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        if (elements[pos - capacity].equals(value)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Tests if some key is mapped to the specified value in this hashtable.
   * This operation is more expensive than the containsKey method.
   *
   * Note that this method is identical in functionality to contains() method.<p>
   *
   * @param      value a value to search for.
   * @return     true if and only if some key maps to the
   *             value argument in this hashtable, 
   *             false otherwise.
   */
  public boolean containsValue(Object value) {
    return contains(value);
  }

  /**
   * Tests if the specified key is a key in this hashtable.<p>
   *
   * @param   key a possible element.
   * @return  true if and only if the specified element is in this
   *          hashtable, false otherwise.
   */
  public boolean containsKey(int key) {
    return get(key) != null;
  }

  /**
   * Clears this hashtable so that it contains no keys.<p>
   */
  public synchronized void clear() {

    DataHolder active = holder;

    int[] keys = new int[active.keys.length];
    Object[] elements = new Object[active.elements.length];
    int[] nextPtr = new int[active.nextPtr.length];

    DataHolder newHolder = new DataHolder();
    newHolder.keys = keys;
    newHolder.elements = elements;
    newHolder.nextPtr = nextPtr;
    newHolder.capacity = capacity;


    for(int i = 0; i < capacity; i++) {
      nextPtr[i] = LAST;
    }
    
    for(int i = capacity; i < nextPtr.length;) {
      nextPtr[i] = ++i;
    }
    
    for(int i = 0; i < elements.length; i++) {
//      keys[i] = 0; ///!!!!! TOWA NE ZNAM TAKA LI TRIABWA DA E....
      elements[i] = null;
    }

    nextFree = capacity;
    count = 0;

    holder = newHolder;

  }

  /**
   * Creates a shallow copy of this hashtable. The whole structure of the
   * hashtable is copied, but the keys and values are not cloned.
   * This is a relatively expensive operation.<p>
   *
   * @return  a clone of the hashtable.
   */
  public synchronized Object clone() throws CloneNotSupportedException {

    DataHolder active = holder;
    int keys[] = active.keys;
    Object elements[] = active.elements;
    int nextPtr[] = active.nextPtr;

    ConcurrentReadHashMapIntObj result = null;
    result = (ConcurrentReadHashMapIntObj)super.clone();
    int rKeys[] = new int[keys.length];
    Object rElements[] = new Object[elements.length];
    int rNextPtr[] = new int[nextPtr.length];

    DataHolder newHolder = new DataHolder();
    newHolder.keys = rKeys;
    newHolder.elements = rElements;
    newHolder.nextPtr = rNextPtr;
    result.holder = newHolder;

    System.arraycopy(nextPtr, 0, rNextPtr, 0, nextPtr.length);
    System.arraycopy(keys, 0, rKeys, 0, keys.length);
    System.arraycopy(elements, 0, rElements, 0, elements.length);

    return result;
  }

  /**
   * Compares the specified object with this set for equality.<p>
   *
   * @param  object a specified object.
   * @return true if the specified object is equal to this hashtable, false otherwise.
   */
  public synchronized boolean equals(Object object) {
    if (object == this) {
      return true;
    }

    if (!(object instanceof ConcurrentReadHashMapIntObj)) {
      return false;
    }

    ConcurrentReadHashMapIntObj t = (ConcurrentReadHashMapIntObj)object;

    if (t.count != count) {
      return false;
    }

    int keys[] = holder.keys;
    Object elements[] = holder.elements;
    int nextPtr[] = holder.nextPtr;

    int index;
    Object temp;

    for(int i = 0; i < capacity; i++) {
      for(int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        index = pos - capacity;
        temp = t.get(keys[index]);

        if (temp == null) {
          return false;
        }

        if (!elements[index].equals(temp)) {
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
	  result = 37 * result + ((holder == null) ? 0 : holder.hashCode());
	  return result;
	}

  /**
   * Reduces the size of hash table.
   * Shrink factor shows how much elements can be added before rehash.<p>
   *
   * For example:<p>
   *  If shrink factor is 1.0 then even one element can cause rehash.
   *  If shrink factor is 0.5 and set has 100 elements,
   *  100 more elements can be put before rehash.<p>
   *
   *  default shrinkFactor = 0.75.<p>
   */
//  public void shrink() {
//    shrink(LOAD_FACTOR);
//  }

  /**
   * Reduces the size of hashtable.
   * Shrink factor shows how much elements can be added before rehash.<p>
   *
   * For example:<p>
   *  If shrink factor is 1.0 then even one element can cause rehash.
   *  If shrink factor is 0.5 and set has 100 elements,
   *  100 more elements can be put before rehash.<p>
   *
   * @param   shrinkFactor the shrink factor.
   */
//  public void shrink(float shrinkFactor) {
//    if ((shrinkFactor <= 0.0f) || (shrinkFactor > 1.0)) {
//      throw new IllegalArgumentException("Shrink Factor = " + shrinkFactor);
//    }
//
//    int newCapacity = (int) (count / (loadFactor * shrinkFactor));
//    long l = getClosestPrime(newCapacity);
//    simplIndex = (int) (l >> 32);
//    newCapacity = (int) l;
//
//    if (newCapacity < capacity) {
//      int oldCapacity = capacity;
//      int[] oldPtr = nextPtr;
//      Object[] oldKeys = keys;
//      Object[] oldElements = elements;
//      init(newCapacity);
//      int index;
//
//      for (int i = 0; i < oldCapacity; i++) {
//        for (int pos = oldPtr[i]; pos != LAST; pos = oldPtr[pos]) {
//          index = pos - oldCapacity;
//          putQuick(oldKeys[index], oldElements[index]);
//        } 
//      } 
//    }
//  }

  /**
   * Maps the specified key to the specified value in this hashtable.
   *
   * The value can be retrieved by calling the get() method with a key that
   * is equal to the original key.<p>
   *
   * @param   key hashtable key.
   * @param   value value that key is to be mapped to.
   * @return  the previous value of the specified key in this hashtable,
   *             or null if it did not have one.
   */
  public synchronized Object put(int key, Object value) {
    if (value == null) {
      throw new NullPointerException("Value can not be null.");
    }

    if (count == limit) {
      rehash();
    }

    int keys[] = holder.keys;
    Object elements[] = holder.elements;
    int nextPtr[] = holder.nextPtr;
    int pos = hash(key) % capacity;
    int index;

    while (nextPtr[pos] != LAST) {
      pos = nextPtr[pos];
      index = pos - capacity;

      if (keys[index] == key) {
        Object temp = elements[index];
        elements[index] = value;
        return temp;
      }
    }

    int newSlot = nextFree;

    int newIndex = newSlot - capacity;
    keys[newIndex] = key;
    elements[newIndex] = value;
    nextFree = nextPtr[nextFree];
    nextPtr[newSlot] = LAST;
    nextPtr[pos] = newSlot;
    count++;
    return null;
  }

  public synchronized Object put(int key, Object value, int hash) {
    if (value == null) {
      throw new NullPointerException("Value can not be null.");
    }
    
    if (count == limit) {
      rehash();
    }
    
    int keys[] = holder.keys;
    Object elements[] = holder.elements;
    int nextPtr[] = holder.nextPtr;
    
    int pos = hash % capacity;
    int index;
    
    while (nextPtr[pos] != LAST) {
      pos = nextPtr[pos];
      index = pos - capacity;

      if (keys[index] == key) {
        Object temp = elements[index];
        elements[index] = value;
        return temp;
      }
    }

    int newSlot = nextFree;

    int newIndex = newSlot - capacity;
    keys[newIndex] = key;
    elements[newIndex] = value;
    nextFree = nextPtr[nextFree];
    nextPtr[newSlot] = LAST;
    nextPtr[pos] = newSlot;
    count++;
    return null;
  }

  /**
   * Put method for internal use. Not  and does not perform check for
   * overflow.<p>
   *
   * @param   key hashtable key.
   * @param   value value that key is to be mapped to.
   * @return  the previous value of the specified key in this hashtable,
   *             or null if it did not have one.
   */
//  private void putQuick(Object key, Object value) {
//    int pos = hash(key.hashCode()) % capacity;
//    int index;
//
//    while (nextPtr[pos] != LAST) {
//      pos = nextPtr[pos];
//    }
//
//    index = nextFree - capacity;
//    nextPtr[pos] = nextFree;
//    keys[index] = key;
//    elements[index] = value;
//    nextFree = nextPtr[nextFree];
//    nextPtr[nextPtr[pos]] = LAST;
//    count++;
//  }

  /**
   * Returns the value to which the specified key is mapped in this hashtable.<p>
   *
   * @param   key hashtable key.
   * @return  the value to which the key is mapped in this hashtable,
   *          null if the key is not mapped to any value in this hashtable.
   */
  public Object get(int key) {

    DataHolder active = holder;
    int keys[] = active.keys;
    int nextPtr[] = active.nextPtr;
    int cap = active.capacity;

    int index;
    int pos = nextPtr[hash(key) % cap];

    while (pos != LAST) {
      index = pos - cap;

      if (keys[index] == key) {
        return active.elements[index];
      }

      pos = nextPtr[pos];
    }

    return null;
  }

  public Object get(int key, int hash) {

    DataHolder active = holder;
    int keys[] = active.keys;
    int nextPtr[] = active.nextPtr;
    int cap = active.capacity;

    int index;
    int pos = nextPtr[hash % cap];

    while (pos != LAST) {
      index = pos - cap;

      if (keys[index] == key) {
        return active.elements[index];
      }

      pos = nextPtr[pos];
    }

    return null;
  }

  /**
   * Removes the key (and its corresponding value) from this
   * hashtable. This method does nothing if the key is not in the hashtable.<p>
   *
   * @param   key the key that needs to be removed.
   * @return  the previous value of the specified key in this hashtable,
   *             or null if it did not have one.
   */
  public synchronized Object remove(int key) {

    int keys[] = holder.keys;
    Object elements[] = holder.elements;
    int nextPtr[] = holder.nextPtr;
    int prevPos = hash(key) % capacity;
    int pos = nextPtr[prevPos];
    int index;

    while (pos != LAST) {
      index = pos - capacity;

      if (keys[index] == key) {
        nextPtr[prevPos] = nextPtr[pos];
        Object result = elements[index];
        elements[index] = null;
        nextPtr[pos] = nextFree;
        nextFree = pos;
        count--;
        return result;
      }

      prevPos = pos;
      pos = nextPtr[pos];
    }

    return null;
  }


  public synchronized Object remove(int key, int hash) {

    int keys[] = holder.keys;
    Object elements[] = holder.elements;
    int nextPtr[] = holder.nextPtr;
    int prevPos = hash % capacity;
    int pos = nextPtr[prevPos];
    int index;

    while (pos != LAST) {
      index = pos - capacity;

      if (keys[index] == key) {
        nextPtr[prevPos] = nextPtr[pos];
        Object result = elements[index];
        elements[index] = null;
        nextPtr[pos] = nextFree;
        nextFree = pos;
        count--;
        return result;
      }

      prevPos = pos;
      pos = nextPtr[pos];
    }

    return null;
  }

  /**
   * Initial data structure for use.<p>
   *
   * @param   initialCapacity specified capacity.
   */
  private void init(DataHolder initHolder, int initialCapacity) {

    if (growStep > 17) {
      this.capacity = (int)getClosestPrime(initialCapacity);
    } else {
      long l = getClosestPrime(initialCapacity, simplIndex);
      simplIndex = (int)(l >> 32) + growSimpl;
      this.capacity = (int)l;
    }

    limit = (int)(capacity * loadFactor);
    initHolder.nextPtr = new int[capacity + limit];

    for(int i = 0; i < capacity; i++) {
      initHolder.nextPtr[i] = LAST;
    }

    for(int i = capacity; i < initHolder.nextPtr.length;) {
      initHolder.nextPtr[i] = ++i;
    }

    initHolder.keys = new int[limit];
    initHolder.elements = new Object[limit];
    initHolder.capacity = capacity;
    nextFree = capacity;
    count = 0;
  }

  /**
   * Increases the capacity of this hashtable and internally reorganizes
   * it to accommodate and access its entries more efficiently.
   * This method is called automatically when the
   * number of keys in the hashtable exceeds this hashtable capacity
   * and load factor.<p>
   */
  private void rehash() {

    DataHolder newHolder = new DataHolder();

    init(newHolder, capacity * growStep);

    int keys[] = newHolder.keys;
    Object elements[] = newHolder.elements;
    int nextPtr[] = newHolder.nextPtr;

    int oldKeys[] = holder.keys;
    Object oldElements[] = holder.elements;


    for(int i = 0; i < oldKeys.length; i++) {

      int pos = hash(oldKeys[i]) % capacity;

      int newSlot = nextFree;
      int newIndex = nextFree - capacity;

      nextFree = nextPtr[nextFree];

      nextPtr[newSlot] = nextPtr[pos];
      nextPtr[pos] = newSlot;

      keys[newIndex] = oldKeys[i];
      elements[newIndex] = oldElements[i];

      count++;
    }
    holder = newHolder;
  }

  /**
   * Returns a string representation of this hashtable object
   * in the form of a set of entries, enclosed in braces and separated
   * by the ASCII characters ,  (comma and space). Each
   * entry is rendered as the key, an equality sign =, and the
   * associated element, where the toString method is used to
   * convert the key and element to strings. Overrides the
   * toString method of java.lang.Object.<p>
   *
   * @return  a string representation of this hashtable.
   */
  public synchronized String toString() {

    int keys[] = holder.keys;
    Object elements[] = holder.elements;
    int nextPtr[] = holder.nextPtr;

    int c = 0;
    int index;
    StringBuffer buf = new StringBuffer();
    buf.append("{");

    for(int i = 0; i < capacity; i++) {
      for(int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        index = pos - capacity;
        buf.append(keys[index] + "=" + elements[index]);

        if (++c < count) {
          buf.append(", ");
        }
      }
    }

    buf.append("}");
    return buf.toString();
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
  private synchronized void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    int index;

    int keys[] = holder.keys;
    Object elements[] = holder.elements;
    int nextPtr[] = holder.nextPtr;

    for(int i = 0; i < capacity; i++) {
      for(int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        index = pos - capacity;
        stream.writeInt(keys[index]);
        stream.writeObject(elements[index]);
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

    int[] nextPtr = new int[capacity + limit];

    for(int i = 0; i < capacity; i++) {
      nextPtr[i] = LAST;
    }

    for(int i = capacity; i < nextPtr.length;) {
      nextPtr[i] = ++i;
    }

    int[] keys = new int[limit];
    Object[] elements = new Object[limit];
    nextFree = capacity;
    int size = count;
    count = 0;

    for(int i = 0; i < size; i++) {

      int key = stream.readInt();
      Object element = stream.readObject();

      int pos = hash(key) % capacity;

      int newSlot = nextFree;
      int newIndex = nextFree - capacity;

      nextFree = nextPtr[nextFree];

      nextPtr[newSlot] = nextPtr[pos];
      nextPtr[pos] = newSlot;

      keys[newIndex] = key;
      elements[newIndex] = element;

      count++;
    }

    holder = new DataHolder();
    holder.capacity = capacity;
    holder.nextPtr = nextPtr;
    holder.keys = keys;
    holder.elements = elements;

  }

  public Object[] toArray() {
    return getAllValues();
  }

  /**
   * Retrieves the count of the elements in the structure.<p>
   *
   * @return   the count of the elements in the structure
   */
  public int size() {
    return count;
  }

  /**
   * Checks if the structure is empty.<p>
   *
   * @return true if the structure has no elements
   * and false otherwise
   */
  public boolean isEmpty() {
    return count == 0;
  }

  final static private int hash(int key) {
    return key & 0x7fffffff;
  }

  /**
   * Simple numbers.<p>
   */
  static final private int[] primes =
      {13, 17, 19, 23, 29, 31, 37, 43, 53, 61, 73, 89, 107, 127, 149, 179,
       223, 257, 307, 367, 439, 523, 631, 757, 907, 1087, 1301, 1559, 1871,
       2243, 2689, 3229, 3877, 4649, 5581, 6689, 8039, 9631, 11579, 13873,
       16649, 19973, 23971, 28753, 34511, 41411, 49697, 59621, 71549, 85853,
       103043, 123631, 148361, 178021, 213623, 256349, 307627, 369137, 442961,
       531569, 637873, 765437, 918529, 1102237, 1322669, 1587221, 1904647,
       2285581, 2742689, 3291221, 3949469, 4739363, 5687237, 6824669, 8189603,
       9827537, 11793031, 14151629, 16981957, 20378357, 24454013, 29344823,
       35213777, 42256531, 50707837, 60849407, 73019327, 87623147, 105147773,
       126177323, 151412791, 181695341, 218034407, 261641287, 313969543,
       376763459, 452116163, 542539391, 651047261, 781256711, 937508041,
       1125009637, 1350011569, 1620013909, 1944016661, 2147483647};

  /**
   * Gets closest simple number bigger than key.
   * Performs binary search in db.<p>
   *
   * Usage:
   * <p><blockquote><pre>
   *    long l = SimplesGenerator.getClosestSimple(20);
   *    int index = (int)(l >> 32); // index = 3;
   *    int simple = (int)l;        // simple = 23
   * </pre></blockquote><p>
   *
   * @param   key the base number.
   * @return  two ints packed in long:
   *          high int is position i db array,
   *          low int is the closest simple number bigger than key.
   */
  private static final long getClosestPrime(int key) {
    int low = 0;
    int high = primes.length - 1;

    while (low <= high) {
      int mid = (low + high) >>> 1;
      int midVal = primes[mid];

      if (midVal < key) {
        low = mid + 1;
      } else if (midVal > key) {
        high = mid - 1;
      } else {
        return ((long)mid << 32) | primes[mid];
      }
    }

    return ((long)low << 32) | primes[low];
  }

  /**
   * Gets closest simple number bigger than key.
   * Performs sequential search in db starting from specified possition.<p>
   *
   * Usage:
   * <p><blockquote><pre>
   *    long l = SimplesGenerator.getClosestSimple(20, 4);
   *    int index = (int)(l >> 32); // index = 4;
   *    int simple = (int)l;        // simple = 29
   * </pre></blockquote><p>
   *
   * @param   key the base number.
   * @param   startPos the start position in db.
   * @return  two ints packed in long:
   *          high int is position i db array,
   *          low int is the closest simple number bigger than key.
   */
  private static final long getClosestPrime(int key, int startPos) {
    int i = (startPos < primes.length) ? startPos : primes.length;
    for(; primes[i] < key; i++) ;
    return ((long)i << 32) | primes[i];
  }

}
