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
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * This class implements hashtable, which maps keys to values.<p>
 *
 *
 * Initial capacity and load factor are parameteres of
 * HashMapObjectObject instance that affect its performance.
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
 * HashMapObjectObject operations, including get and put).<p>
 *
 * The initial capacity controls a tradeoff between wasted space and the
 * need for rehash operations, which are time-consuming.
 * Rehash operations will not occur if the initial
 * capacity is greater than the maximum number of entries the
 * HashMapObjectObject will contain divided by its load factor. However,
 * setting the initial capacity too high can waste space.<p>
 *
 * Creating HashMapObjectObject with large capacity may be appropriate
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
 * @author Nikola Arnaudov, Andrei Gatev
 *
 * @version 1.0
 *
 */
public class HashMapObjectObject extends AbstractDataStructure {

  static final long serialVersionUID = 5534319535730135595L;
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
  transient protected Object elements[];
  /**
   * Pointer to next slot in case of collision.<p>
   */
  transient protected int nextPtr[];

  /**
   * Constructs a new, empty hashtable with a default capacity grow step and load
   * factor. which are 13, 2, .75.<p>
   */
  public HashMapObjectObject() {
    this(INITIAL_CAPACITY, GROW_STEP, LOAD_FACTOR, new IntHashHolderImpl());
  }

  /**
   * Constructs a new, empty hashtable with a specific capacity and default capacity grow step and load
   * factor. which are 2, .75.<p>
   *
   * @param   initialCapacity the specified initial capacity of the hashtable.
   */
  public HashMapObjectObject(int initialCapacity) {
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
  public HashMapObjectObject(int initialCapacity, int growStep, float loadFactor, IntHashHolder hasher) {
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
   * Returns an array of the keys in this hashtable.<p>
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
      result = (Object[])java.lang.reflect.Array.newInstance(result.getClass().getComponentType(), count);
    }
    
    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
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
  public Object[] getAllValues() {
    // int index = 0;
    // Object[] result = new Object[count];
    // 
    // for (int i = 0; i < capacity; i++) {
    //   for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
    //     result[index++] = elements[pos - capacity];
    //   }
    // }
    // 
    // return result;
    return getAllValues(new Object[count]);
  }

  /**
   * Returns an array of the keys in this hashtable.
   *
   * @return  array of the keys in this hashtable.
   */
  public Object[] getAllValues(Object[] result) {
    int index = 0;
    
    if (result.length < count) {
      result = (Object[])java.lang.reflect.Array.newInstance(result.getClass().getComponentType(), count);
    }
    
    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
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
  public boolean contains(Object value) {
    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
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
  public boolean containsKey(Object key) {
    return get(key) != null;
  }

  /**
   * Clears this hashtable so that it contains no keys.<p>
   */
  public void clear() {
    for (int i = 0; i < capacity; i++) {
      nextPtr[i] = LAST;
    } 

    for (int i = capacity; i < nextPtr.length;) {
      nextPtr[i] = ++i;
    } 

    for (int i = 0; i < elements.length; i++) {
      keys[i] = null;
      elements[i] = null;
    } 

    nextFree = capacity;
    count = 0;
  }

  /**
   * Creates a shallow copy of this hashtable. The whole structure of the
   * hashtable is copied, but the keys and values are not cloned.
   * This is a relatively expensive operation.<p>
   *
   * @return  a clone of the hashtable.
   */
  public Object clone() {
    HashMapObjectObject result = (HashMapObjectObject) super.clone();
    result.keys = new Object[keys.length];
    result.elements = new Object[elements.length];
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

    if (!(object instanceof HashMapObjectObject)) {
      return false;
    }

    HashMapObjectObject t = (HashMapObjectObject) object;

    if (t.count != count) {
      return false;
    }

    int index;
    Object temp;

    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
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
      int oldCapacity = capacity;
      int[] oldPtr = nextPtr;
      Object[] oldKeys = keys;
      Object[] oldElements = elements;
      init(newCapacity);
      int index;

      for (int i = 0; i < oldCapacity; i++) {
        for (int pos = oldPtr[i]; pos != LAST; pos = oldPtr[pos]) {
          index = pos - oldCapacity;
          putQuick(oldKeys[index], oldElements[index]);
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
   * @return  the previous value of the specified key in this hashtable,
   *             or null if it did not have one.
   */
  public Object put(Object key, Object value) {
    if (value == null) {
      throw new NullPointerException("Value can not be null.");
    }

    if (count == limit) {
      rehash();
    }

    int pos = hasher.hash(key.hashCode()) % capacity;
    int index;

    while (nextPtr[pos] != LAST) {
      pos = nextPtr[pos];
      index = pos - capacity;

      if (keys[index].equals(key)) {
        Object temp = elements[index];
        elements[index] = value;
        return temp;
      }
    }

    index = nextFree - capacity;
    nextPtr[pos] = nextFree;
    keys[index] = key;
    elements[index] = value;
    nextFree = nextPtr[nextFree];
    nextPtr[nextPtr[pos]] = LAST;
    count++;
    return null;
  }

  /**
   * Put method for internal use. Not  and does not perform check for
   * overflow.<p>
   *
   * @param   key hashtable key.
   * @param   value value that key is to be mapped to.
   */
  protected void putQuick(Object key, Object value) {
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
   * @return  the value to which the key is mapped in this hashtable,
   *          null if the key is not mapped to any value in this hashtable.
   */
  public Object get(Object key) {
    int index;
    int pos = nextPtr[hasher.hash(key.hashCode()) % capacity];

    while (pos != LAST) {
      index = pos - capacity;

      if (keys[index].equals(key)) {
        return elements[index];
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
  public Object remove(Object key) {
    int prevPos = hasher.hash(key.hashCode()) % capacity;
    int pos = nextPtr[prevPos];
    int index;

    while (pos != LAST) {
      index = pos - capacity;

      if (keys[index].equals(key)) {
        Object result = elements[index];
        keys[index] = null;
        elements[index] = null;
        nextPtr[prevPos] = nextPtr[pos];
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
    elements = new Object[limit];
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
    Object[] oldElements = elements;
    init(capacity * growStep);

    for (int i = 0; i < oldKeys.length; i++) {
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
    StringBuffer buf = new StringBuffer();
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
   * Sets a hash function.<p
   *
   * @param   hasher a hash function.
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
    int index;

    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        index = pos - capacity;
        stream.writeObject(keys[index]);
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
    nextPtr = new int[capacity + limit];

    for (int i = 0; i < capacity; i++) {
      nextPtr[i] = LAST;
    } 

    for (int i = capacity; i < nextPtr.length;) {
      nextPtr[i] = ++i;
    } 

    keys = new Object[limit];
    elements = new Object[limit];
    nextFree = capacity;
    int size = count;
    count = 0;

    for (int i = 0; i < size; i++) {
      putQuick(stream.readObject(), stream.readObject());
    } 
  }

  /**
   * Returns ForwardIterator implementation over the keys of this HashMap.<p>
   *
   * @return RootIterator over the keys.
   */
  public RootIterator keysIterator() {
    return new HashMapObjectObjectKeysIterator();
  }

  /**>
   * Returns ForwardIterator implementation over the values of this HashMap.<p>
   *
   * @return RootIterator over the values.
   */
  public RootIterator elementsIterator() {
    return new HashMapObjectObjectElementsIterator();
  }

  /**
   * Returns enumeration of the components of this list.
   * The returned SnapShotEnumeration object will generate
   * all items in this list. Changes to this object will not
   * affect the underlying data structure.<p>
   *
   * @return     enumeration of the components of this list.
   */
  public SnapShotEnumeration elementsEnumeration() {
    return new ArrayEnumeration(toArray());
  }

  public Object[] toArray() {
    return getAllValues();
  }

  public Pointer[] toPointerArray() {
    throw new NotSupportedException("Use toArray() method instead!");
  }

  /**
   * Creates a deep copy of this HashMap. The whole structure of the
   * HashMap is copied and the elements are cloned.
   * This is a relatively expensive operation.<p>
   *
   * @return  a deep clone of the HashMap.
   */
  public Object deepClone() {
    HashMapObjectObject result = (HashMapObjectObject) super.clone();
    result.keys = new Object[keys.length];
    result.elements = new Object[elements.length];
    result.nextPtr = new int[nextPtr.length];

    for (int i = 0; i < keys.length; i++) {
      result.keys[i] = ((DeepCloneable) keys[i]).deepClone();
      result.elements[i] = ((DeepCloneable) elements[i]).deepClone();
    } 

    System.arraycopy(nextPtr, 0, result.nextPtr, 0, nextPtr.length);
    return result;
  }

  // ---------------------------------------------------------
  // ---------------- The Iterator methods -------------------
  // ---------------------------------------------------------
  protected Object iterGet(RootIterator rootIter, boolean keysOrElems) {
    if (keysOrElems) {
      HashMapObjectObjectKeysIterator iter = (HashMapObjectObjectKeysIterator) rootIter;

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
    } else {
      HashMapObjectObjectElementsIterator iter = (HashMapObjectObjectElementsIterator) rootIter;

      if (iter.counter >= iter.end) {
        throw new IteratorException("End of Iterator reached!");
      }

      int tmpI = iter.i;
      int tmpPos = iter.pos;

      while (tmpI < capacity) {
        if (tmpPos != LAST) {
          return elements[tmpPos - capacity];
        }

        tmpPos = nextPtr[++tmpI];
      }

      throw new IteratorException("End of Iterator reached!");
    }
  }

  protected Object iterNext(RootIterator rootIter, int offset, boolean keysOrElems) {
    if (keysOrElems) {
      HashMapObjectObjectKeysIterator iter = (HashMapObjectObjectKeysIterator) rootIter;

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
    } else {
      HashMapObjectObjectElementsIterator iter = (HashMapObjectObjectElementsIterator) rootIter;

      if (offset < 0) {
        throw new IteratorException("Invalid argument: " + offset + " < 0");
      }

      int newCounter = iter.counter + offset;

      if (newCounter >= iter.end) {
        throw new IteratorException("End of Iterator reached!");
      }

      while (iter.i < capacity) {
        while (iter.pos != LAST) {
          Object result = elements[iter.pos - capacity];
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
  }

  protected Object iterRemove(RootIterator rootIter, boolean keysOrElems) {
    if (keysOrElems) {
      HashMapObjectObjectKeysIterator iter = (HashMapObjectObjectKeysIterator) rootIter;

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
          elements[index] = null;
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
    } else {
      HashMapObjectObjectElementsIterator iter = (HashMapObjectObjectElementsIterator) rootIter;

      if (count == 0) {
        throw new IteratorException("Iterator is empty!");
      }

      if (iter.counter >= iter.end) {
        throw new IteratorException("End of Iterator reached!");
      }

      while (iter.i < capacity) {
        if (iter.pos != LAST) {
          int index = iter.pos - capacity;
          Object result = elements[index];
          keys[index] = null;
          elements[index] = null;
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
  }

  protected Object iterChange(RootIterator rootIter, Object obj) {
    HashMapObjectObjectElementsIterator iter = (HashMapObjectObjectElementsIterator) rootIter;

    if (obj == null) {
      throw new IteratorException("An illegal attempt to change a value to null!");
    }

    if (count == 0) {
      throw new IteratorException("Iterator is empty!");
    }

    if (iter.counter >= iter.end) {
      throw new IteratorException("End of Iterator reached!");
    }

    int tmpI = iter.i;
    int tmpPos = iter.pos;

    while (tmpI < capacity) {
      if (tmpPos != LAST) {
        int index = tmpPos - capacity;
        Object result = elements[index];
        elements[index] = obj;
        return result;
      }

      tmpPos = nextPtr[++tmpI];
    }

    throw new IteratorException("End of Iterator reached!");
  }

  /**
   * An implementation of ForwardIterator for the keys of this HashMap.
   */
  protected class HashMapObjectObjectKeysIterator implements ForwardIterator { //$JL-CLONE$

    static final long serialVersionUID = -6641255774999469401L;
    private int i = 0;
    private int prevPos = i;
    private int pos = nextPtr[i];
    private int start = 0;
    private int end = count;
    private int counter = start;

    public Object get() {
      return iterGet(this, true);
    }

    public boolean isAtBegin() {
      return (counter == start);
    }

    public boolean isAtEnd() {
      return (counter == end);
    }

    public RootDataStructure getDataStructure() {
      return HashMapObjectObject.this;
    }

    public Object next() {
      return iterNext(this, 0, true);
    }

    public Object next(int n) {
      return iterNext(this, n, true);
    }

    public void setStartFromIterator(RootIterator iterator) {
      HashMapObjectObjectKeysIterator hmooki = (HashMapObjectObjectKeysIterator) iterator;

      if (HashMapObjectObject.this != hmooki.getDataStructure()) {
        throw new IteratorException("An attempt to set start from an Iterator over a different HashMapObjectObject instance!");
      }

      int newCounter = hmooki.counter;
      int newPos = hmooki.pos;
      int newPrevPos = hmooki.prevPos;
      int newI = hmooki.i;

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
      HashMapObjectObjectKeysIterator hmooki = (HashMapObjectObjectKeysIterator) iterator;

      if (HashMapObjectObject.this != hmooki.getDataStructure()) {
        throw new IteratorException("An attempt to set end from an Iterator over a different HashMapObjectObject instance!");
      }

      int newCounter = hmooki.counter;

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
      return iterRemove(this, true);
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

  /**
   * An implementation of ForwardIterator for the values of this HashMap.<p>
   */
  protected class HashMapObjectObjectElementsIterator implements ForwardIterator { //$JL-CLONE$

    static final long serialVersionUID = -2970755344749607355L;
    private int i = 0;
    private int prevPos = i;
    private int pos = nextPtr[i];
    private int start = 0;
    private int end = count;
    private int counter = start;

    public Object get() {
      return iterGet(this, false);
    }

    public boolean isAtBegin() {
      return (counter == start);
    }

    public boolean isAtEnd() {
      return (counter == end);
    }

    public RootDataStructure getDataStructure() {
      return HashMapObjectObject.this;
    }

    public Object next() {
      return iterNext(this, 0, false);
    }

    public Object next(int n) {
      return iterNext(this, n, false);
    }

    public void setStartFromIterator(RootIterator iterator) {
      HashMapObjectObjectElementsIterator hmooei = (HashMapObjectObjectElementsIterator) iterator;

      if (HashMapObjectObject.this != hmooei.getDataStructure()) {
        throw new IteratorException("An attempt to set start from an Iterator over a different HashMapObjectObject instance!");
      }

      int newCounter = hmooei.counter;
      int newPrevPos = hmooei.prevPos;
      int newPos = hmooei.pos;
      int newI = hmooei.i;

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
      HashMapObjectObjectElementsIterator hmooei = (HashMapObjectObjectElementsIterator) iterator;

      if (HashMapObjectObject.this != hmooei.getDataStructure()) {
        throw new IteratorException("An attempt to set end from an Iterator over a different HashMapObjectObject instance!");
      }

      int newCounter = hmooei.counter;

      if (newCounter < counter) {
        throw new IteratorException(" end ahead of the current position of the Iterator: " + newCounter + " < " + counter);
      }

      end = newCounter;
    }

    public Object add(Object obj) {
      throw new IteratorException("Operation is not supported!");
    }

    public Object change(Object obj) {
      return iterChange(this, obj);
    }

    public Object remove() {
      return iterRemove(this, false);
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
      return true;
    }

    public boolean isAddable() {
      return false;
    }

  }

}

