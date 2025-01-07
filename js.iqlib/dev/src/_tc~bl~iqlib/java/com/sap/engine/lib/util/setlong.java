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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
 *       elements() method.
 *
 * @author Nikola Arnaudov
 * @version 1.0
 *
 */
public class SetLong extends PrimitiveTypeDataStructure {

  static final long serialVersionUID = -7045169532835870108L;
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
  protected LongHashHolder hasher;
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
  transient protected long keys[];
  /**
   * Pointer to next slot in case of collision.<p>
   */
  transient protected int nextPtr[];

  /**
   * Constructs a new, empty set with a default capacity grow step and load
   * factor. which are 13, 2, .75.<p>
   */
  public SetLong() {
    this(INITIAL_CAPACITY, GROW_STEP, LOAD_FACTOR, new LongHashHolderImpl());
  }

  /**
   * Constructs a new, empty hashtable with a specific capacity and default capacity grow step and load
   * factor. which are 2, .75.<p>
   *
   * @param   initialCapacity the specified initial capacity of the hashtable.
   */
  public SetLong(int initialCapacity) {
    this(initialCapacity, GROW_STEP, LOAD_FACTOR, new LongHashHolderImpl());
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
  public SetLong(int initialCapacity, int growStep, float loadFactor, LongHashHolder hasher) {
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
   * Returns an enumeration of the elements in this set.<p>
   *
   * While traversing enumeration the set must not be modified.<p>
   *
   * @return  enumeration of the keys in this set.
   */
  public EnumerationLong elements() {
    return new EnumerationLong() {

      private int i = 0;
      private int pos = nextPtr[i];
      private int counter = 0;

      public boolean hasMoreElements() {
        return counter < count;
      }

      public long nextElement() {
        while (i < capacity) {
          if (pos != LAST) {
            long result = keys[pos - capacity];
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
   * Returns an array of the elements in this set.<p>
   *
   * @return  array of the elements in this set.
   */
  public long[] toArray() {
    int index = 0;
    long[] result = new long[count];

    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        result[index++] = keys[pos - capacity];
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
  public boolean contains(long key) {
    int pos = nextPtr[hasher.hash(key) % capacity];

    while (pos != LAST) {
      if (keys[pos - capacity] == key) {
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
    for (int i = 0; i < capacity; i++) {
      nextPtr[i] = LAST;
    } 

    for (int i = capacity; i < nextPtr.length;) {
      nextPtr[i] = ++i;
    } 

    nextFree = capacity;
    count = 0;
  }

  /**
   * Creates a shallow copy of this set. The whole structure of the
   * set is copied. This is a relatively expensive operation.<p>
   *
   * @return  a clone of the set.
   */
  public Object clone() {
    SetLong result = (SetLong) super.clone();
    result.keys = new long[keys.length];
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

    if (!(object instanceof SetLong)) {
      return false;
    }

    SetLong t = (SetLong) object;

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
	
	private int arrayHashCode(long[] array) {
	  if (array == null) {
	    return 0;
	  }
	  int result = 17;
	  for (int i = 0; i < array.length; i++) {
	    result = 37 * result + (int)(array[i] ^ (array[i] >>> 32));
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
      int oldCapacity = capacity;
      int[] oldPtr = nextPtr;
      long[] oldKeys = keys;
      init(newCapacity);

      for (int i = 0; i < oldCapacity; i++) {
        for (int pos = oldPtr[i]; pos != LAST; pos = oldPtr[pos]) {
          putQuick(oldKeys[pos - oldCapacity]);
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
  public boolean add(long key) {
    if (count == limit) {
      rehash();
    }

    int pos = hasher.hash(key) % capacity;

    while (nextPtr[pos] != LAST) {
      pos = nextPtr[pos];

      if (keys[pos - capacity] == key) {
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
   * Put method for internal use. Not  and does not perform check for
   * overflow.<p>
   *
   * @param   key an element of the set.
   */
  protected void putQuick(long key) {
    int pos = hasher.hash(key) % capacity;

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
  public boolean remove(long key) {
    int prevPos = hasher.hash(key) % capacity;
    int pos = nextPtr[prevPos];

    while (pos != LAST) {
      if (keys[pos - capacity] == key) {
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

    for (int i = 0; i < capacity; i++) {
      nextPtr[i] = LAST;
    } 

    for (int i = capacity; i < nextPtr.length;) {
      nextPtr[i] = ++i;
    } 

    keys = new long[limit];
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
    long[] oldKeys = keys;
    init(capacity * growStep);

    for (int i = 0; i < oldKeys.length; i++) {
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
    StringBuffer buf = new StringBuffer();
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
  public void setHasher(LongHashHolder hasher) {
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

    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        stream.writeLong(keys[pos - capacity]);
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

    for (int i = 0; i < capacity; i++) {
      nextPtr[i] = LAST;
    } 

    for (int i = capacity; i < nextPtr.length;) {
      nextPtr[i] = ++i;
    } 

    keys = new long[limit];
    nextFree = capacity;
    int size = count;
    count = 0;

    for (int i = 0; i < size; i++) {
      putQuick(stream.readLong());
    } 
  }

}

