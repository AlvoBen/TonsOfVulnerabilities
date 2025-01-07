/*
 * Copyright (c) 2001 by In-Q-My Technologies GmbH,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of In-Q-My Technologies GmbH. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with In-Q-My.
 */
package com.sap.engine.lib.util;

import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * This class implements hashtable, which maps keys to values.
 * The idea is that for each key, which is of type Object, there are
 * multiple corresponding values, which themselves are also Objects.
 * That is why the class is called HashMapObjectMultiObject.
 *
 *
 * Initial capacity and load factor are parameteres of
 * HashMapObjectMultiObject instance that affect its performance.
 * Capacity parameter specifies the number of buckets in hash table.
 * Initial capacity is the capacity at the time the hash table
 * is created.  Hash table is open, i.e. in case of "hash
 * collision", a single bucket stores multiple entries, which must be searched
 * sequentially.  Load factor indicates number of entries that are
 * allowed in hash table before its capacity is automatically increased.
 * When the number of entries in the hashtable exceeds the product of the load
 * factor and the current capacity, the capacity is increased by calling the
 * rehash method.
 *
 * Generally, the default load factor (.75) offers a good tradeoff between
 * time and space costs.  Higher values decrease the space overhead but
 * increase the time cost to look up an entry (which is reflected in most
 * HashMapObjectMultiObject operations, including get and put).
 *
 * The initial capacity controls a tradeoff between wasted space and the
 * need for rehash operations, which are time-consuming.
 * Rehash operations will not occur if the initial
 * capacity is greater than the maximum number of entries the
 * HashMapObjectMultiObject will contain divided by its load factor. However,
 * setting the initial capacity too high can waste space.
 *
 * Creating HashMapObjectMultiObject with large capacity may be appropriate
 * if many entries are to be inserted into it.
 * Thus entries may be inserted more efficiently than if
 * automatic rehashing is performed when table resizing is necessary.
 *
 *
 * WARNING: This class is not synchronized. Also keep in mind that this
 *          hashtable could be quite memory consuming so use it only when
 *          there really will be enough values maped to each key, meaning
 *          that generally there won't be keys related to just 1 of 10 values.
 *
 * Note: The fastest way to traverse the hashtable is by the Enumerations
 *       returned by keys() and elements(..) methods.
 *
 * @author Nikola Arnaudov
 * @author George Manev
 * @version 4.0
 */
public class HashMapObjectMultiObject implements Cloneable, Serializable {

  static final long serialVersionUID = 1917336056378239313L;
  // krupka
  /**
   * Number of elements in data structure
   */
  protected int count;

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

  // end of krupka
  /**
   * Default number of values per key of the hashtable.
   */
  public static final int MULTI_FACTOR = 2;
  /**
   * Default load factor for the hashtable.
   */
  public static final float LOAD_FACTOR = 0.75f;
  /**
   * Default initial capacity for the hashtable.
   */
  public static final int INITIAL_CAPACITY = 13;
  /**
   * Default grow step (newSize = oldSize * growStep) for the hashtable.
   */
  public static final int GROW_STEP = 2;
  /**
   * Last element in the list.
   */
  protected static final int LAST = -1;
  /**
   * Grow step (newSize = oldSize * growStep).
   */
  protected int growStep;
  /**
   * Grow step in the simple number database.
   */
  protected int growSimpl;
  /**
   * Current loaf factor (0.0, 1.0].
   */
  protected float loadFactor;
  /**
   * Holder for hash function.
   */
  protected IntHashHolder hasher;
  /**
   * Index in the simple number database.
   */
  protected int simplIndex;
  /**
   * Limit for the hash table (limit = capacity * loadFactor).
   */
  protected int limit;
  /**
   * Capacity of the hash table i.e. the number of entries.
   */
  protected int capacity;
  /**
   * Number of values per key.
   */
  protected int multiFactor;
  /**
   * Pointer to list of free slots.
   */
  transient protected int nextFree;
  /**
   * Pointer to the values of the most recently seeked key.
   */
  transient protected int currentIndex;
  /**
   * The keys.
   */
  transient protected Object keys[];
  /**
   * The values.
   */
  transient protected Object elements[][];
  /**
   * Pointer to next slot in case of collision.
   */
  transient protected int nextPtr[];

  /**
   * Constructs a new, empty hashtable with the default capacity, grow step,
   * number of values per key and load factor which are 13, 2, .75, 2.
   */
  public HashMapObjectMultiObject() {
    this(INITIAL_CAPACITY, GROW_STEP, LOAD_FACTOR, MULTI_FACTOR, new IntHashHolderImpl());
  }

  /**
   * Constructs a new, empty hashtable with a specific capacity and default capacity grow step and load
   * factor. which are 2, .75, 2.<p>
   *
   * @param   initialCapacity the specified initial capacity of the hashtable.
   */
  public HashMapObjectMultiObject(int initialCapacity) {
    this(initialCapacity, GROW_STEP, LOAD_FACTOR, MULTI_FACTOR, new IntHashHolderImpl());
  }

  /**
   * Constructs a new, empty hashtable with the specified initial capacity and
   * number of values per key, and with the default grow step and load factor.
   *
   * @param   initialCapacity  initial capacity of hashtable.
   * @param   multiFactor  number of values per key of hashtable.
   */
  public HashMapObjectMultiObject(int initialCapacity, int multiFactor) {
    this(initialCapacity, GROW_STEP, LOAD_FACTOR, multiFactor, new IntHashHolderImpl());
  }

  /**
   * Constructs a new, empty hashtable with the specified initial
   * capacity, grow step, number of values per key and load factor.
   *
   * @param   initialCapacity  initial capacity of hashtable.
   * @param   growStep  grow step of hashtable.
   * @param   loadFactor   load factor of hashtable.
   * @param   multiFactor  number of values per key of hashtable.
   * @param   hasher  hash function.
   */
  public HashMapObjectMultiObject(int initialCapacity, int growStep, float loadFactor, int multiFactor, IntHashHolder hasher) {
    if (multiFactor < 1) {
      throw new IllegalArgumentException("The Number of Values per Key must be greater than zero !");
    }

    if ((loadFactor > 1.0f) || (loadFactor <= 0)) {
      throw new IllegalArgumentException("The Load Factor must be in the range (0.0,1.0] !");
    }

    if (growStep < 2) {
      throw new IllegalArgumentException("The Grow Step must be greater than one !");
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
    this.multiFactor = multiFactor;
    this.hasher = hasher;
    simplIndex = 0;
    init(initialCapacity);
  }

  /**
   * Returns an enumeration of the keys in this hashtable.
   *
   * While traversing enumeration the hash table must not be modified.
   *
   * @return  enumeration of the keys in this hashtable.
   */
  public Enumeration keys() {
    return new Enumeration() {

      private int i = 0;
      private int pos = nextPtr[i];
      private int counter = 0;

      public boolean hasMoreElements() {
        return counter < count;
      }

      public Object nextElement() {
        while (i < capacity) {
          if (pos != LAST) {
            Object result = keys[pos - capacity];
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
   * Returns an array of the keys in this hashtable.
   *
   * @return  array of the keys in this hashtable.
   */
  public Object[] getAllKeys() {
    // int index = 0;
    // Object[] result = new Object[count];
    // 
    // for (int i = 0; i < capacity; i++) {
    //   for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
    //     result[index++] = keys[pos - capacity];
    //   }
    // }
    // 
    // return result;
    return getAllKeys(new Object[count]);
  }

  /**
   * Returns an array of the keys in this hashtable.
   *
   * @return  array of the keys in this hashtable.
   */
  public Object[] getAllKeys(Object[] result) {
    int index = 0;
    
    if (result.length < count) {
      result = (Object[])Array.newInstance(result.getClass().getComponentType(), count);
    }
    
    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        result[index++] = keys[pos - capacity];
      }
    }
    
    return result;
  }

  /**
   * Returns enumeration of the specified range of values in this hashtable.
   * If the range argument is negative the enumeration returned will contain
   * all of the values in this hashtable in the following way : suppose that
   * num is the number of values per key in the hashtable. Then the first num
   * elements of the enumeration will be the values of the firs key, the second
   * num elements - the values of the second key and so forth.
   * Otherwise if range is 0 then only the first values of the keys in this
   * hashtable will be returned, if it is 1 then only the second values, and
   * so on. Finally if range is greater than the number of values per key - 1
   * an IllegalArgumentException is thrown.
   *
   * While traversing enumeration the hash table must not be modified.
   *
   * @param   range   a range of values to be returned.
   * @exception  IllegalArgumentException  if range argument is greater
   *             or equal to the number of values per key.
   * @return  enumeration of the specified range of values in this hashtable.
   */
  public Enumeration elements(final int range) {
    if (range >= multiFactor) {
      throw new IllegalArgumentException("The range must be less than " + multiFactor + " !");
    }

    if (range < 0) {
      return new Enumeration() {

        private int i = 0;
        private int j = 0;
        private int pos = nextPtr[i];
        private int index = pos - capacity;
        private int counter = 0;

        public boolean hasMoreElements() {
          return counter < count;
        }

        public Object nextElement() {
          while (i < capacity) {
            while (pos != LAST) {
              if (j < multiFactor) {
                return elements[index][j++];
              }

              j = 0;
              pos = nextPtr[pos];
              index = pos - capacity;
              counter++;
            }

            pos = nextPtr[++i];
            index = pos - capacity;
          }

          throw new NoSuchElementException();
        }

      };
    }

    return new Enumeration() {

      private int i = 0;
      private int pos = nextPtr[i];
      private int counter = 0;

      public boolean hasMoreElements() {
        return counter < count;
      }

      public Object nextElement() {
        while (i < capacity) {
          if (pos != LAST) {
            Object result = elements[pos - capacity][range];
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
   * Returns an array of the values in this hashtable.
   * let's suppose that we have the following statements:
   *   ...
   *   HashMapObjectMultiObject hash = new HashMapObjectMultiObject();
   *   // Put some values.
   *   Object [][] values = hash.getAllValues();
   *
   * Then values[i][0] will contain the first value for a specific key
   * whilst values[i][1] will contain the second value for that same key.
   * More precisely, values[i][j] will contain the (j+1)th value of the
   * (i+1)th key.
   *
   * @return  array of the values in this hashtable.
   */
  public Object[][] getAllValues() {
    int index = 0;
    Object[][] result = new Object[count][multiFactor];

    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        System.arraycopy(elements, pos - capacity, result, index++, 1);
      }
    }

    return result;
  }

  /**
   * Returns an array containing a range of the values in this hashtable.
   *
   * @exception  IndexOutOfBoundsException  if the range is negative or
   *             greater or equal to the number of values per key.
   * @return  array of the values in this hashtable.
   */
  public Object[] getAllValues(int range) {
    return getAllValues(new Object[count], range);
  }

  /**
   * Returns an array containing a range of the values in this hashtable.
   *
   * @exception  IndexOutOfBoundsException  if the range is negative or
   *             greater or equal to the number of values per key.
   * @return  array of the values in this hashtable.
   */
  public Object[] getAllValues(Object[] result, int range) {
    int index = 0;
    
    if (result.length < count) {
      result = (Object[])Array.newInstance(result.getClass().getComponentType(), count);
    }
    
    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        result[index++] = elements[pos - capacity][range];
      }
    }
    
    return result;
  }

  /**
   * Tests if some key is mapped to the specified value in this hashtable.
   * This operation is more expensive than the containsKey method.
   * If the range argument is negative then the entire hashtable is searched.
   * Otherwise if range is 0 then only the first values of the keys are
   * searched through, if it is 1 then only the second ones, and so forth.
   * Finally if range is greater than the number of values per key - 1 an
   * IndexOutOfBoundsException is thrown.
   *
   * Note that this method is identical in functionality to containsValue(...).
   *
   * @param      value   a value to be searched for.
   * @param      range   a range of elements to be searched.
   * @exception  IndexOutOfBoundsException  if range argument is greater
   *             or equal to the number of values per key.
   * @return     true if and only if some key maps to the value argument
   *             in the specified range of this hashtable as
   *             determined by the equals() method;
   *             false otherwise.
   */
  public boolean contains(Object value, int range) {
    if (range < 0) {
      for (int i = 0; i < capacity; i++) {
        for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
          int index = pos - capacity;

          for (int j = 0; j < multiFactor; j++) {
            if (equals(value, elements[index][j])) {
              return true;
            }
          }
        }
      }
    } else {
      for (int i = 0; i < capacity; i++) {
        for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
          if (equals(value, elements[pos - capacity][range])) {
            return true;
          }
        }
      }
    }

    return false;
  }

  /**
   * Tests if some key is mapped to the specified value in this hashtable.
   * This operation is more expensive than the containsKey method.
   * If the range argument is negative then the entire hashtable is searched.
   * Otherwise if range is 0 then only the first values of the keys are
   * searched through, if it is 1 then only the second ones, and so forth.
   * Finally if range is greater than the number of values per key - 1 an
   * IndexOutOfBoundsException is thrown.
   *
   * Note that this method is identical in functionality to contains() method.
   *
   * @param      value   a value to be searched for.
   * @param      range   a range of elements to be searched.
   * @exception  IndexOutOfBoundsException  if range argument is greater
   *             or equal to the number of values per key.
   * @return     true if and only if some key maps to the value argument
   *             in the specified range of this hashtable as
   *             determined by the equals() method;
   *             false otherwise.
   */
  public boolean containsValue(Object value, int range) {
    return contains(value, range);
  }

  /**
   * Tests if the specified key is a key in this hashtable. In contrast to the
   * seek(key) method if the hashtable contains such a key then a following call
   * to get(..) or modify(..) method will NOT accordingly return or modify
   * a value for this key.
   *
   * Note that this method is equivalent to the seek(key) method.
   *
   * @param   key   key to be searched for.
   * @return  true if and only if the specified key is a key in this
   *          hashtable, as determined by the equals() method;
   *          false otherwise.
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
   * Tests if the specified key is a key in this hashtable. If the hashtable
   * contains such a key then a following call to get(..) or modify(..) method
   * will accordingly return or modify a value for this key.
   *
   * Note that this method is equivalent to the containsKey(key) method.
   *
   * @param   key   key to be searched for.
   * @return  true if and only if the specified key is a key in this
   *          hashtable, as determined by the equals() method;
   *          false otherwise.
   */
  public boolean seek(Object key) {
    int pos = nextPtr[hasher.hash(key.hashCode()) % capacity];

    while (pos != LAST) {
      if (keys[pos - capacity].equals(key)) {
        currentIndex = pos - capacity;
        return true;
      }

      pos = nextPtr[pos];
    }

    return false;
  }

  /**
   * Clears this hashtable so that it contains no keys.
   */
  public void clear() {
    for (int i = 0; i < capacity; i++) {
      nextPtr[i] = LAST;
    }

    for (int i = capacity; i < nextPtr.length;) {
      nextPtr[i] = ++i;
    }

    for (int i = 0; i < limit; i++) {
      keys[i] = null;

      for (int j = 0; j < multiFactor; j++) {
        elements[i][j] = null;
      }
    }

    nextFree = capacity;
    currentIndex = -1;
    count = 0;
  }

  /**
   * Creates a shallow copy of this hashtable. The whole structure of the
   * hashtable is copied, but the keys and values are not cloned.
   * This is a relatively expensive operation.
   *
   * @return  a clone of the hashtable.
   */
  public Object clone() {
    try {
      HashMapObjectMultiObject result = (HashMapObjectMultiObject) super.clone();
      result.nextPtr = new int[nextPtr.length];
      result.keys = new Object[keys.length];
      result.elements = new Object[elements.length][multiFactor];
      System.arraycopy(nextPtr, 0, result.nextPtr, 0, nextPtr.length);
      System.arraycopy(keys, 0, result.keys, 0, keys.length);
      System.arraycopy(elements, 0, result.elements, 0, elements.length);
      return result;
    } catch (CloneNotSupportedException _) { //$JL-EXC$
      // never happend
      throw new InternalError();
    }
  }

  /**
   * Compares the specified Object with this hashtable for equality.
   *
   * @param   o specified object
   * @return true if the specified Object is equal to this hashtable.
   */
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof HashMapObjectMultiObject)) {
      return false;
    }

    HashMapObjectMultiObject t = (HashMapObjectMultiObject) o;

    if (t.count != count || t.multiFactor != multiFactor) {
      return false;
    }

    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        int index = pos - capacity;
        Object[] values = t.getAll(keys[index]);

        if (values == null) {
          return false;
        }

        for (int j = 0; j < multiFactor; j++) {
          if (!equals(elements[index][j], values[j])) {
            return false;
          }
        }

        //        if (!t.seek(keys[index])) {
        //          return false;
        //        }
        //        for (int j=0; j < multiFactor; j++) {
        //          if (!equals(elements[index][j], t.get(j))) { // s kolko e po-bavno ???
        //            return false;
        //          }
        //        }
      }
    }

    return true;
  }
  
  public int hashCode() {
	  int result = 17;
	  result = 37 * result + count;
	  result = 37 * result + multiFactor;
	  result = 37 * result + capacity;
	  result = 37 * result + arrayHashCode(nextPtr);
	  result = 37 * result + arrayHashCode(keys);
	  result = 37 * result + arrayHashCode(elements);
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
	
	private int arrayHashCode(Object[][] array) {
	  if (array == null) {
	    return 0;
	  }
	  int result = 17;
	  for (int i = 0; i < array.length; i++) {
	    for (int j = 0; j < array[i].length; j++) {
	      result = 37 * result + array[i][j].hashCode();
	    }
	        
	  }
	  return result;
	}

  /**
   * Equals method for internal use. Compares two objects for equality,
   * including the case when they both are null.
   *
   * @param  o1  first object to be compared.
   * @param  o2  second object to be compared.
   * @return true if the specified Objects are equal.
   */
  protected static final boolean equals(Object o1, Object o2) {
    if (o1 == null) {
      return o2 == null;
    }

    return o1.equals(o2);
  }

  /**
   * Reduces the size of hash table.
   *
   * Shrink factor shows how much elements can be added before rehash.
   * For example:
   *  If shrink factor is 1.0 then even one element can cause rehash.
   *  If shrink factor is 0.5 and hashtable has 100 elements,
   *  100 more elements can be put before rehash.
   *
   *  default shrinkFactor = 0.75
   */
  public void shrink() {
    shrink(LOAD_FACTOR);
  }

  /**
   * Reduces the size of hash table.
   * Shrink factor shows how much elements can be added before rehash.
   * For example:
   *  If shirink factor is 1.0 then even one element can cause rehash.
   *  If shrink factor is 0.5 and hashtable has 100 elements. You can put
   *  more 100 before rehash.
   *
   * @param   shrinkFactor  shrink factor;
   */
  public void shrink(float shrinkFactor) {
    if ((shrinkFactor <= 0.0f) || (shrinkFactor > 1.0f)) {
      throw new IllegalArgumentException("The Shrink Factor must be in the range (0.0,1.0] !");
    }

    int newCapacity = (int) (count / (loadFactor * shrinkFactor));
    long l = PrimeGenerator.getClosestPrime(newCapacity);
    simplIndex = (int) (l >> 32);
    newCapacity = (int) l;

    if (newCapacity < capacity) {
      int oldCapacity = capacity;
      int oldPtr[] = nextPtr;
      Object[] oldKeys = keys;
      Object[][] oldElements = elements;
      init(newCapacity);

      for (int i = 0; i < oldCapacity; i++) {
        for (int pos = oldPtr[i]; pos != LAST; pos = oldPtr[pos]) {
          int index = pos - oldCapacity;
          putQuick(oldKeys[index], oldElements[index]);
        }
      }
    }
  }

  /**
   * Maps the specified key to the specified values in this hashtable.
   * The key can NOT be null, nor can the values argument. Contrary,
   * the elements of the values argument, i.e. the particular values,
   * are allowed to be null.
   *
   * The values can be retrieved by calling the seek() method with a key that
   * is equal to the original key followed by call(s) to the get() method.
   *
   * @param      key     hashtable key.
   * @param      values  values that this key is to be mapped to.
   * @exception  NullPointerException  if one of the arguments is null.
   * @exception  IllegalArgumentException  if the number of elements in values
   *             argument is different from the number of values per key in this
   *             hashtable.
   */
  public void put(Object key, Object[] values) {
    if (values.length != multiFactor) {
      throw new IllegalArgumentException("The number of values per key must be : " + multiFactor);
    }

    if (count == limit) {
      rehash();
    }

    int pos = hasher.hash(key.hashCode()) % capacity;

    while (nextPtr[pos] != LAST) {
      pos = nextPtr[pos];

      if (keys[pos - capacity].equals(key)) {
        System.arraycopy(values, 0, elements[pos - capacity], 0, values.length);
        return;
      }
    }

    int index = nextFree - capacity;
    keys[index] = key;
    System.arraycopy(values, 0, elements[index], 0, values.length);
    nextPtr[pos] = nextFree;
    nextFree = nextPtr[nextFree];
    nextPtr[nextPtr[pos]] = LAST;
    count++;
  }

  /**
   * Put method for internal use. Does not perform checks for overflow
   * and illegal arguments.
   *
   * @param   key     hashtable key.
   * @param   values  values that this key is to be mapped to.
   */
  protected void putQuick(Object key, Object[] values) {
    int pos = hasher.hash(key.hashCode()) % capacity;

    while (nextPtr[pos] != LAST) {
      pos = nextPtr[pos];
    }

    int index = nextFree - capacity;
    keys[index] = key;
    System.arraycopy(values, 0, elements[index], 0, values.length);
    nextPtr[pos] = nextFree;
    nextFree = nextPtr[nextFree];
    nextPtr[nextPtr[pos]] = LAST;
    count++;
  }

  /**
   * If the specified key exists in this hashtable modifies its values.
   * The key can NOT be null, nor can the values argument. Contrary,
   * the elements of the values argument, i.e. the particular values,
   * are allowed to be null.
   *
   * If the specified key does NOT exist in this hashtable this method
   * does nothing but returning false.
   *
   * @param      key     hashtable key.
   * @param      values  values that this key is to be mapped to.
   * @exception  NullPointerException  if one of the arguments is null.
   * @exception  IllegalArgumentException  if the number of elements in values
   *             argument is different from the number of values per key in this
   *             hashtable.
   * @return     true if the values are modified (i.e. the key exists)
   *             or false otherwise.
   */
  public boolean modify(Object key, Object[] values) {
    if (values.length != multiFactor) {
      throw new IllegalArgumentException("The number of values per key must be : " + multiFactor);
    }
    
    int pos = hasher.hash(key.hashCode()) % capacity;
    
    while (nextPtr[pos] != LAST) {
      pos = nextPtr[pos];
      
      if (keys[pos - capacity].equals(key)) {
        System.arraycopy(values, 0, elements[pos - capacity], 0, values.length);
        return true;
      }
    }
    
    return false;
  }

  /**
   * Modifies a value to which the most recently seeked key is mapped in this
   * hashtable. A key is seeked by calling the seek(key) or containsKey(key)
   * method. If one of these methods returns true then a following call to
   * this method will modify a value for that key, according to the index
   * argument. If this argument is 0 the first value for that key will be
   * modified, if it is 1 - the second one, if it is i - the (i+1)th one.
   *
   * WARNING: The results of using this method without first calling a seeking
   *          method are unpredicted. Furthermore, the two calls MUST be some-
   *          how synchronized to ensure that the hashtable is not modified in
   *          the meantime.
   *
   * @param   value   specifies the new value to be applied.
   * @param   index   specifies which of the values for the most recently
   *                  seeked key to be replaced.
   * @exception  IndexOutOfBoundsException  if the index is negative or
   *             greater or equal to the number of values per key.
   * @return     a value which the most recently seeked key was mapped to
   *             prior to this call.
   */
  public Object modify(int index, Object value) {
    Object tmp = elements[currentIndex][index];
    elements[currentIndex][index] = value;
    return tmp;
  }

  /**
   * Returns a value to which the most recently seeked key is mapped in this
   * hashtable. A key is seeked by calling the seek(key) or containsKey(key)
   * method. If one of these methods returns true then a following call to
   * this method will return a value for that key, according to the index
   * argument. If this argument is 0 the first value for that key will be
   * returned, if it is 1 - the second one, if it is i - the (i+1)th one.
   *
   * WARNING: The results of using this method without first calling a seeking
   *          method are unpredicted. Furthermore, the two calls MUST be some-
   *          how synchronized to ensure that the hashtable is not modified.
   *
   * @param   index   specifies which of the values for the most recently
   *                  seeked key to be returned.
   * @exception  IndexOutOfBoundsException  if the index is negative or
   *             greater or equal to the number of values per key.
   * @return     a value which the most recently seeked key is mapped to in
   *             this hashtable.
   */
  public Object get(int index) {
    return elements[currentIndex][index];
  }

  /**
   * Returns an array containing all the values which the specified key is
   * mapped to in this hashtable or null if the hashtable does not contain
   * such a key.
   *
   * NOTE: This method does NOT imply a call to a seeking method, so it does
   *       NOT affect the get(..) and modify(..) methods.
   *
   * @param   key   specifies a key to get the values of.
   * @exception NullPointerException   if the specified key is null.
   * @return    an array containing all the values of the specified key or null.
   */
  public Object[] getAll(Object key) {
    int pos = nextPtr[hasher.hash(key.hashCode()) % capacity];

    while (pos != LAST) {
      if (keys[pos - capacity].equals(key)) {
        return elements[pos - capacity];
      }

      pos = nextPtr[pos];
    }

    return null;
  }

  /**
   * Removes the specified key and its corresponding values from this hashtable.
   * This method does nothing if the key is not in the hashtable.
   *
   * @param   key   the key that needs to be removed.
   * @return  true  if the hashtable has contained such key; false otherwise.
   */
  public boolean remove(Object key) {
    int prevPos = hasher.hash(key.hashCode()) % capacity;
    int pos = nextPtr[prevPos];

    while (pos != LAST) {
      int index = pos - capacity;

      if (keys[index].equals(key)) {
        keys[index] = null;

        for (int j = 0; j < multiFactor; j++) {
          elements[index][j] = null;
        }

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
   * Initializes this data structure for use.
   *
   * @param   initialCapacity  capacity.
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

    keys = new Object[limit];
    elements = new Object[limit][multiFactor];
    nextFree = capacity;
    currentIndex = -1;
    count = 0;
  }

  /**
   * Increases the capacity of this hashtable and internally reorganizes
   * it to accommodate and access its entries more efficiently.
   * This method is called automatically when the number of keys in the
   * hashtable exceeds the multiple of its capacity and load factor.
   */
  protected void rehash() {
    Object[] oldKeys = keys;
    Object[][] oldElements = elements;
    init(capacity * growStep);

    for (int i = 0; i < oldKeys.length; i++) {
      putQuick(oldKeys[i], oldElements[i]);
    }
  }

  /**
   * Returns a string representation of this HashMapObjectMultiObject object
   * in the form of a set of entries, enclosed in braces and separated
   * by the ASCII characters "; " (semicolon and space). Each
   * entry is rendered as the key, an equality sign '=', and the
   * associated elements separated by the ASCII character ',' (comma)
   * where the toString method is used to convert the key and values
   * to strings. Overrides the toString() method of Object.
   *
   * @return  a string representation of this hashtable.
   */
  public String toString() {
    int c = 0;
    int lim = multiFactor - 1;
    StringBuffer buf = new StringBuffer();
    buf.append('{');

    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        int index = pos - capacity;
        buf.append(keys[index]);
        buf.append('=');

        for (int j = 0; j < lim; j++) {
          buf.append(elements[index][j]);
          buf.append(',');
        }

        buf.append(elements[index][lim]);

        if (++c < count) {
          buf.append("; ");
        }
      }
    }

    buf.append('}');
    return buf.toString();
  }

  /**
   * Sets hash function
   *
   * @param   hasher  hash function.
   */
  public void setHasher(IntHashHolder hasher) {
    this.hasher = hasher;
  }

  // -----------------------------------------------------------
  // -------------- Some serialization magic -------------------
  // -----------------------------------------------------------
  /**
   * This method is used by java serializer.
   *
   * @param   stream output stream.
   * @exception   IOException if IO exception occur.
   */
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();

    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        int index = pos - capacity;

        for (int j = 0; j < multiFactor; j++) {
          stream.writeObject(elements[index][j]);
        }

        stream.writeObject(keys[index]);
      }
    }
  }

  /**
   * This method is used by java deserializer.
   *
   * @param   stream input stream
   * @exception   IOException if IO exception occur.
   * @exception   ClassNotFoundException if class not found.
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

    keys = new Object[limit];
    elements = new Object[limit][multiFactor];
    nextFree = capacity;
    currentIndex = -1;
    int size = count;
    count = 0;
    Object[] values = new Object[multiFactor];

    for (int i = 0; i < size; i++) {
      for (int j = 0; j < multiFactor; j++) {
        values[j] = stream.readObject();
      }

      putQuick(stream.readObject(), values);
    }
  }

}

