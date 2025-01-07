﻿package com.sap.engine.lib.util;

/**
 * Title:        iQ-Lib
 * Description:  Data Structures & Algorithms Library
 * Copyright:    Copyright (c) 2000
 * Company:      SAP Labs Bulgaria LTD., Sofia, Bulgaria.
 * Url:          Http://www.saplabs.bg
 *               All rights reserved.
 *
 *               This software is the confidential and proprietary information
 *               of SAP AG International ("Confidential Information").
 *               You shall not disclose such  Confidential Information
 *               and shall use it only in accordance with the terms of
 *               the license agreement you entered into with SAP AG.
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * This class implements Hashtable, which maps keys to values.<p>
 *
 *
 * Initial capacity and load factor are parameteres of the
 * hashtable instance that affect its performance.
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
 * Hashtable operations, including get and put).<p>
 *
 * The initial capacity controls a tradeoff between wasted space and the
 * need for rehash operations, which are time-consuming.
 * Rehash operations will not occur if the initial
 * capacity is greater than the maximum number of entries the
 * Hashtable will contain divided by its load factor. However,
 * setting the initial capacity too high can waste space.<p>
 *
 * Creating Hashtable with large capacity may be appropriate
 * if many entries are to be inserted into it.
 * Thus entries may be inserted more efficiently than if
 * automatic rehashing is performed when table resizingis necessary.<p>
 *
 *
 * WARNING: This class is not synchronized.<p>
 *
 * <b>Note</b>: The fastest way to traverse the hashtable is by Enumeration returned by
 *       elements() method.
 *
 * @author Georgi Manev georgi.maneff@sap.com
 *
 * @version 1.0
 *
 */
public class HashMapObjectByte extends PrimitiveTypeDataStructure {
  
  static final long serialVersionUID = 4178458027921802042L;
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
  protected static final int LAST = -1;
  /**
   * Static exception for better performance.<p>
   */
  protected static final NoSuchElementException noSuchElementException;
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
   * Values.<p>
   */
  transient protected byte elements[];
  /**
   * Pointer to next slot in case of collision.<p>
   */
  transient protected int nextPtr[];

  static {
    noSuchElementException = new NoSuchElementException("This is static exception, there is no stack trace available. It is thrown by get() method.");
  }

  /**
   * Constructs a new, empty hashtable with a default capacity grow step and load
   * factor. which are 13, 2, .75.<p>
   */
  public HashMapObjectByte() {
    this(INITIAL_CAPACITY, GROW_STEP, LOAD_FACTOR, new IntHashHolderImpl());
  }

  /**
   * Constructs a new, empty hashtable with a specific capacity and default capacity grow step and load
   * factor. which are 2, .75.<p>
   *
   * @param   initialCapacity the specified initial capacity of the hashtable.
   */
  public HashMapObjectByte(int initialCapacity) {
    this(initialCapacity, GROW_STEP, LOAD_FACTOR, new IntHashHolderImpl());
  }

  /**
   * Constructs a new, empty hashtable with specified initial
   * capacity, grow step and load factor.<p>
   *
   * @param   initialCapacity the specified initial capacity of the hashtable.
   * @param   growStep  grow step of the hashtable.
   * @param   loadFactor  load factor of hashtable.
   * @param   hasher a hash function.
   */
  public HashMapObjectByte(int initialCapacity, int growStep, float loadFactor, IntHashHolder hasher) {
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
   * Returns an enumeration of the keys in this hashtable.
   *
   * While traversing enumeration the hash table must not be modified.<p>
   *
   * @return  enumeration of the keys in this hashtable.
   */
  public Enumeration keys() {
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
   * Returns an array of the keys in this hashtable.<p>
   *
   * @return  array of the keys in this hashtable.
   */
  public Object[] getAllKeys() {
    return getAllKeys(new Object[count], 0);
  }

  /**
   * Returns an array of the keys in this hashtable. If there is enough room
   * for all the keys of this hashmap inside the result argument array starting
   * from the specified index, they are returned there in. Otherwise the keys
   * are returned inside a newly created array of the same runtime type
   * and both arguments are ignored.
   * <p>
   *
   * @param   result an array for storing the keys.
   * @param   index  offset in the result argument array.
   * @return  array of the keys in this hashtable.
   */
  public Object[] getAllKeys(Object[] result, int index) {
    if ((result.length - index) < count) {
      result = (Object[])java.lang.reflect.Array.newInstance(result.getClass().getComponentType(), count);
      index = 0;
    }
    
    for (int i = keys.length; --i >= 0; ) {
      if (keys[i] != null) {
        result[index++] = keys[i];
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
  public EnumerationByte elements() {
    return new EnumerationByte() {
      
      private int i = keys.length;
      private int counter = 0;
      
      public boolean hasMoreElements() {
        return counter < count;
      }
      
      public byte nextElement() {
        while (--i >= 0) {
          if (keys[i] != null) {
            counter++;
            return elements[i];
          }
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
  public byte[] getAllValues() {
    return getAllValues(new byte[count], 0);
  }

  /**
   * Returns an array of the values in this hashtable. If there is enough room
   * for all the values of this hashmap inside the result argument array starting
   * from the specified index, they are returned there in. Otherwise the values
   * are returned inside a newly created array and both arguments are ignored.
   * <p>
   *
   * @param   result an array for storing the values.
   * @param   index  offset in the result argument array.
   * @return  array of the values in this hashtable.
   */
  public byte[] getAllValues(byte[] result, int index) {
    if ((result.length - index) < count) {
      result = new byte[count];
      index = 0;
    }
    
    for (int i = keys.length; --i >= 0; ) {
      if (keys[i] != null) {
        result[index++] = elements[i];
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
  public boolean contains(byte value) {
    for (int i = capacity; --i >= 0; ) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        if (elements[pos - capacity] == value) {
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
  public boolean containsValue(byte value) {
    return contains(value);
  }

  /**
   * Tests if the specified key is a key in this hashtable.<p>
   *
   * @param   key a possible element.
   * @return  true if and only if the specified element is in this
   *          hashtable, false otherwise.
   */
  public boolean containsKey(Object key) {
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
   * Clears this hashtable so that it contains no keys.<p>
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
   * Creates a shallow copy of this hashtable. The whole structure of the
   * hashtable is copied, but the keys are not cloned.
   * This is a relatively expensive operation.<p>
   *
   * @return  a clone of the hashtable.
   */
  public Object clone() {
    HashMapObjectByte result = (HashMapObjectByte) super.clone();
    result.keys = new Object[keys.length];
    result.elements = new byte[elements.length];
    result.nextPtr = new int[nextPtr.length];
    System.arraycopy(nextPtr, 0, result.nextPtr, 0, nextPtr.length);
    System.arraycopy(keys, 0, result.keys, 0, keys.length);
    System.arraycopy(elements, 0, result.elements, 0, elements.length);
    return result;
  }

  /**
   * Compares the specified object with this set for equality.<p>
   *
   * @param  object a specified object.
   * @return true if the specified object is equal to this hashtable, false otherwise.
   */
  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }

    if (!(object instanceof HashMapObjectByte)) {
      return false;
    }

    HashMapObjectByte t = (HashMapObjectByte) object;

    if (t.count != count) {
      return false;
    }

    for (int i = 0; i < limit; i++) {
      if (keys[i] != null) {
        try {
          if (elements[i] != t.get(keys[i])) {
            return false;
          }
        } catch (NoSuchElementException _) {
          return false;
        }
      }
    }

    return true;
  }
  
  public int hashCode() {
	  int result = 17;
	  result = 37 * result + count;
	  result = 37 * result + limit;
	  result = 37 * result + arrayHashCode(keys);
	  result = 37 * result + arrayHashCode(elements);
	  return result;
	}
	
	private int arrayHashCode(byte[] array) {
	  if (array == null) {
	    return 0;
	  }
	  int result = 17;
	  for (int i = 0; i < array.length; i++) {
	    result = 37 * result + (int)array[i];
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
  public void shrink() {
    shrink(LOAD_FACTOR);
  }

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
      byte[] oldElements = elements;
      init(newCapacity);
      
      for (int i = oldKeys.length; --i >= 0; ) {
        if (oldKeys[i] != null) {
          putQuick(oldKeys[i], oldElements[i]);
        }
      }
    }
  }

  /**
   * Maps the specified key to the specified value in this hashtable.
   *
   * The value can be retrieved by calling the get() method with a key that
   * is equal to the original key.<p>
   *
   * @param   key hashtable key.
   * @param   value value that key is to be mapped to.
   * @return  true if key has been mapped in this hashtable (old value is replaced),
   *          false otherwise (new entry has created).
   */
  public boolean put(Object key, byte value) {
    if (count == limit) {
      rehash();
    }

    int pos = hasher.hash(key.hashCode()) % capacity;
    int index;

    while (nextPtr[pos] != LAST) {
      pos = nextPtr[pos];
      index = pos - capacity;

      if (keys[index].equals(key)) {
        elements[index] = value;
        return true;
      }
    }

    index = nextFree - capacity;
    nextPtr[pos] = nextFree;
    keys[index] = key;
    elements[index] = value;
    nextFree = nextPtr[nextFree];
    nextPtr[nextPtr[pos]] = LAST;
    count++;
    return false;
  }

  /**
   * Put method for internal use. Not  and does not perform check for
   * overflow.<p>
   *
   * @param   key hashtable key.
   * @param   value value that key is to be mapped to.
   */
  protected void putQuick(Object key, byte value) {
    int pos = hasher.hash(key.hashCode()) % capacity;
    int index;

    while (nextPtr[pos] != LAST) {
      pos = nextPtr[pos];
    }

    index = nextFree - capacity;
    nextPtr[pos] = nextFree;
    keys[index] = key;
    elements[index] = value;
    nextFree = nextPtr[nextFree];
    nextPtr[nextPtr[pos]] = LAST;
    count++;
  }

  /**
   * Returns the value to which the specified key is mapped in this hashtable.<p>
   *
   * @param   key hashtable key.
   * @return  the value to which the key is mapped in this hashtable.
   * @exception NoSuchElementException if there is no element with the specified key.
   */
  public byte get(Object key) {
    int index;
    int pos = nextPtr[hasher.hash(key.hashCode()) % capacity];

    while (pos != LAST) {
      index = pos - capacity;

      if (keys[index].equals(key)) {
        return elements[index];
      }

      pos = nextPtr[pos];
    }

    throw noSuchElementException;
  }

  /**
   * Removes the key (and its corresponding value) from this
   * hashtable. This method does nothing if the key is not in the hashtable.<p>
   *
   * @param   key the key that needs to be removed.
   * @return  true if key has been mapped in this hashtable,
   *          false otherwise.
   */
  public boolean remove(Object key) {
    int prevPos = hasher.hash(key.hashCode()) % capacity;
    int pos = nextPtr[prevPos];
    int index;

    while (pos != LAST) {
      index = pos - capacity;

      if (keys[index].equals(key)) {
        keys[index] = null;
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
   * @param   initialCapacity specified capacity.
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
    elements = new byte[limit];
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
  protected void rehash() {
    Object[] oldKeys = keys;
    byte[] oldElements = elements;
    init(capacity * growStep);
    
    for (int i = oldKeys.length; --i >= 0; ) {
      putQuick(oldKeys[i], oldElements[i]);
    }
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
  public String toString() {
    int c = 0;
    int index;
    StringBuffer buf = new StringBuffer(4 * count + 1);
    buf.append("{");

    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
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

  /**
   * Sets a hash function.
   *
   * @param   hasher hash function.
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
        stream.writeByte(elements[i]);
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

    for (int i = capacity; i < nextPtr.length; ) {
      nextPtr[i] = ++i;
    }

    keys = new Object[limit];
    elements = new byte[limit];
    nextFree = capacity;
    int size = count;
    count = 0;

    for (int i = size; --i >= 0; ) {
      putQuick(stream.readObject(), stream.readByte());
    }
  }

}

