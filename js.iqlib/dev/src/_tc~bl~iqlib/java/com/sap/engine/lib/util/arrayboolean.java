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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.NoSuchElementException;

/**
 * The ArrayBoolean class implements a growable array of
 * booleans. Like an array, it contains components that can be
 * accessed using an integer index. However, the size of a
 * ArrayBoolean can grow or shrink as needed to accommodate
 * adding and removing items after the ArrayBoolean has been created.<p>
 *
 * The functionality of this class is similar to the functionality of the standard
 * java.util.Array class , especially as storage management is concerned.
 * Each array tries to optimize storage management by maintaining a
 * capacity and a capacityIncrement. The capacity is always at least
 * as large as the array size; it is usually larger because as
 * components are added to the array, the array's storage increases
 * in chunks the size of capacityIncrement. An application can increase the
 * capacity of a array before inserting a large number of components;
 * this reduces the amount of incremental reallocation.<p>
 *
 * WARNING: This class is not synchronized.<p>
 * <b>Note</b>: The fastest way to traverse the set is by an Enumeration returned by the
 *       elements() method.
 *
 * @author Nikola Arnaudov, George Manev, Andrei Gatev
 * @version 1.0
 */
public class ArrayBoolean extends PrimitiveTypeDataStructure {
  
  static final long serialVersionUID = 3365209572211181800L;  

  /**
   * The array buffer into which the elements of the ArrayBoolean are stored.
   * The capacity of the ArrayBoolean is the length of this array buffer.<p>
   */
  protected transient boolean elementData[];
  /**
   * The amount by which the capacity of the array is automatically incremented when its
   * size becomes greater than its capacity. If the capacity increment is 0, the capacity
   * of the array is doubled each time it needs to grow.<p>
   */
  protected int capacityIncrement;

  /**
   * Constructs an empty ArrayBoolean with the specified initial capacity and capacity increment.<p>
   *
   * @param  initialCapacity the initial capacity of the array.
   * @param  capacityIncrement the amount by which the capacity is increased when the array overflows.
   * @exception IllegalArgumentException if the specified initial capacity is negative.
   */
  public ArrayBoolean(int initialCapacity, int capacityIncrement) {
    if (initialCapacity < 0) {
      throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
    }

    elementData = new boolean[initialCapacity];
    this.capacityIncrement = capacityIncrement;
  }

  /**
   * Constructs an empty ArrayBoolean with the specified initial capacity
   * and with its capacity increment equal to zero.<p>
   *
   * @param   initialCapacity the initial capacity of the array.
   * @exception  IllegalArgumentException if the specified initial capacity is negative.
   */
  public ArrayBoolean(int initialCapacity) {
    this(initialCapacity, 0);
  }

  /**
   * Constructs an empty ArrayBoolean so that
   * its internal data array has the size of 10
   * and its standard capacity increment is zero.<p>
   */
  public ArrayBoolean() {
    this(10);
  }

  /**
   * Constructs an ArrayBoolean from elements of the
   * specified boolean array.<p>
   *
   * @param   array the boolean array which contains the elements.
   * @param   offset the elements are taken from this position of array.
   * @param   length the count of elements which will be added.
   * @param   capacityIncrement the amount by which the capacity
   *                               is increased when the array overflows.
   */
  public ArrayBoolean(boolean[] array, int offset, int length, int capacityIncrement) {
    elementData = new boolean[length];
    System.arraycopy(array, offset, elementData, 0, length);
    this.capacityIncrement = capacityIncrement;
    count = length;
  }

  /**
   * Constructs an ArrayBoolean from elements of the specified boolean array.
   * Capacity increment is zero.<p>
   */
  public ArrayBoolean(boolean[] array) {
    this(array, 0, array.length, 0);
  }

  /**
   * Constructs an ArrayBoolean from elements of the specified ArrayBoolean.<p>
   *
   * @param   arrayBoolean the ArrayBoolean which contains the elements.
   * @param   offset the elements are taken from this position of array.
   * @param   length the count of elements which will be added.
   * @param   capacityIncrement the amount by which the capacity is increased
   *                              when the array overflows.
   */
  public ArrayBoolean(ArrayBoolean arrayBoolean, int offset, int length, int capacityIncrement) {
    this(length, capacityIncrement);
    arrayBoolean.copyInto(offset, elementData, 0, length);
    count = length;
  }

  /**
   * Constructs an ArrayBoolean from the elements of the specified ArrayBoolean, array.
   * Capacity increment is zero.<p>
   */
  public ArrayBoolean(ArrayBoolean arrayBoolean) {
    elementData = arrayBoolean.toArray();
    count = elementData.length;
  }

  /**
   * Copies the components of this ArrayBoolean into the specified array. The item at index k
   * in this array is copied into component k of anArray[]. The array must be big enough
   * to hold all the elements in this ArrayBoolean, else an ArrayIndexOutOfBoundsException is thrown.<p>
   *
   * @param   anArray the array into which the components are copied.
   */
  public void copyInto(boolean anArray[]) {
    System.arraycopy(elementData, 0, anArray, 0, count);
  }

  /**
   * Gets range of elements starting at the specified position in
   * this ArrayBoolean in specified boolean array.
   * If the length field is greater than count of remaining
   * elements in this ArrayBoolean an appropriate exception is thrown.<p>
   *
   * @param  index the starting position for getting.
   * @param  array the destionation array.
   * @param  offset  new elements are set from this position of array.
   * @param  length  the count of elements which will be set.
   */
  public void copyInto(int index, boolean[] array, int offset, int length) {
    if (index + length > count) {
      throw new ArrayIndexOutOfBoundsException("(index + length)=" + (index + length) + " > size=" + count);
    }

    System.arraycopy(elementData, index, array, offset, length);
  }

  /**
   * Trims the capacity of this array to be the array's current size. If the capacity
   * of this array is larger than its current size, then the capacity is changed to
   * equal the size by replacing its internal data array, kept in the field elementData,
   * with a smaller one. An application can use this operation to minimize the storage of
   * an ArrayBoolean.<p>
   */
  public void trimToSize() {
    int oldCapacity = elementData.length;

    if (count < oldCapacity) {
      boolean newData[] = new boolean[count];
      System.arraycopy(elementData, 0, newData, 0, count);
      elementData = newData;
    }
  }

  /**
   * Increases the capacity of this ArrayBoolean, if necessary, to ensure that it can hold
   * at least the number of components specified by the minimum capacity argument. If the
   * current capacity of this ArrayBoolean is less than minCapacity, then its capacity is
   * increased by replacing its internal data array, kept in the field elementData, with
   * a larger one. The size of the new data array will be the old size plus
   * capacityIncrement, unless the value of capacityIncrement is nonpositive, in which
   * case the new capacity will be twice the old capacity; but if this new size is still
   * smaller than minCapacity, then the new capacity will be minCapacity.<p>
   *
   * @param   minCapacity the desired minimum capacity.
   */
  protected void ensureCapacity(int minCapacity) {
    int capacity = elementData.length;

    if (minCapacity > capacity) {
      capacity = (capacityIncrement > 0) ? (capacity + capacityIncrement) : (capacity << 1);

      if (capacity < minCapacity) {
        capacity = minCapacity;
      }

      boolean newData[] = new boolean[capacity];
      System.arraycopy(elementData, 0, newData, 0, count);
      elementData = newData;
    }
  }

  /**
   * Sets the size of this ArrayBoolean. If the new size is less than the current size,
   * all components at index newSize and greater are discarded.<p>
   *
   * @param   newSize the new size of this ArrayBoolean.
   * @throws  IllegalArgumentException if the new size is negative.
   */
  public void setSize(int newSize) {
    if (newSize < 0) {
      throw new IllegalArgumentException("Illegal newSize: " + newSize);
    }

    if (newSize > count) {
      ensureCapacity(newSize);
    } 
    count = newSize;
  }

  /**
   * Returns the current capacity of this ArrayBoolean.<p>
   *
   * @return  the current capacity (the length of its internal data arary, kept in the
   *          field elementData of this ArrayBoolean)
   */
  public int capacity() {
    return elementData.length;
  }

  /**
   * Tests if the specified boolean is a component in this array.<p>
   *
   * @param   elem a boolean value.
   * @return  true if the specified boolean value is equal to a component in this array
   *          and false otherwise.
   */
  public boolean contains(boolean elem) {
    return indexOf(elem, 0) >= 0;
  }

  /**
   * Searches for the first occurence of the given argument.<p>
   *
   * @param   elem a boolean value.
   * @return  the index of the first occurrence of the argument in this array, that is,
   *          the smallest value k such that elem is equal to elementData[k];
   *          returns -1 if the boolean is not found.
   */
  public int indexOf(boolean elem) {
    return indexOf(elem, 0);
  }

  /**
   * Searches for the first occurence of the given element after the specified index.<p>
   *
   * @param   elem a boolean value.
   * @param   index the index to start searching from.
   * @return  the index of the first occurrence of the boolean value in this array
   *          at position index or later in the array, that is the smallest value k,
   *          such that elem is equal to elementData[k] and k>=index;
   *          returns -1 if the boolean is not found.
   */
  public int indexOf(boolean elem, int index) {
    for (int i = index; i < count; i++) {
      if (elem == elementData[i]) {
        return i;
      }
    } 

    return -1;
  }

  /**
   * Returns the index of the last occurrence of the specified boolean value in this array.<p>
   *
   * @param   elem the desired boolean value.
   * @return  the index of the last occurrence of the specified boolean value in this array,
   *          that is, the largest value k such that elem is equal to elementData[k];
   *          returns -1 if the boolean is not found.
   */
  public int lastIndexOf(boolean elem) {
    return lastIndexOf(elem, count - 1);
  }

  /**
   * Searches backwards for the specified boolean value, starting from the specified index,
   * and returns an index to it.<p>
   *
   * @param  elem the desired boolean value.
   * @param  index the index to start searching from.
   * @return the index of the last occurrence of the specified boolean value in this array
   *         at position less than index in the array, that is, the largest value k such
   *         that elem is equal to elementData[k] and k <= index;
   *         returns -1 if the boolean is not found.
   */
  public int lastIndexOf(boolean elem, int index) {
    for (int i = index; i >= 0; i--) {
      if (elem == elementData[i]) {
        return i;
      }
    } 

    return -1;
  }

  /**
   * Returns the component at the specified index.<p>
   *
   * @param      index an index into this array.
   * @return     the boolean value at the specified index.
   * @exception  ArrayIndexOutOfBoundsException if the index is negative or not less
   *             than the current size of this ArrayBoolean object.
   */
  public boolean elementAt(int index) {
    if (index >= count) {
      throw new ArrayIndexOutOfBoundsException(index + " >= " + count);
    }

    return elementData[index];
  }

  /**
   * Returns the first component (the item at index 0) of this array.<p>
   *
   * @return     the first component of this array.
   * @exception  NoSuchElementException if this array has no components.
   */
  public boolean firstElement() {
    if (count == 0) {
      throw new NoSuchElementException();
    }

    return elementData[0];
  }

  /**
   * Returns the last component of the array.<p>
   *
   * @return  the last component of the array i.e. the component at index size() - 1.
   * @exception  NoSuchElementException if this array is empty.
   */
  public boolean lastElement() {
    if (count == 0) {
      throw new NoSuchElementException();
    }

    return elementData[count - 1];
  }

  /**
   * Sets the element at the specified index of this array to be the specified boolean
   * value. The previous component at that position is discarded. The index parameter must be
   * greater than or equal to 0 and less than the current size of the array.<p>
   *
   * @param      value a boolean value to be set.
   * @param      index the specified index.
   * @exception  ArrayIndexOutOfBoundsException if the index was invalid.
   */
  public void setElementAt(boolean value, int index) {
    if (index >= count) {
      throw new ArrayIndexOutOfBoundsException(index + " >= " + count);
    }

    elementData[index] = value;
  }

  /**
   * Deletes the component at the specified index. Each component in this array with an
   * index greater or equal to the specified index is shifted downward to have an index
   * one smaller than the value it had previously. The size of this array is decreased
   * by 1. The index parameter must be a value greater than or equal to 0 and less than the current
   * size of the array.<p>
   *
   * @param      index the index of the boolean value to remove.
   * @exception  ArrayIndexOutOfBoundsException f the index was invalid.
   */
  public void removeElementAt(int index) {
    if (index >= count) {
      throw new ArrayIndexOutOfBoundsException(index + " >= " + count);
    } else if (index < 0) {
      throw new ArrayIndexOutOfBoundsException(index + " < " + 0);
    }

    int j = count - index - 1;

    if (j > 0) {
      System.arraycopy(elementData, index + 1, elementData, index, j);
    }

    count--;
  }

  /**
   * Deletes the last component in the array.<p>
   *
   * @return     the deleted element.
   * @exception  NoSuchElementException if the array is empty.
   */
  public boolean removeLastElement() {
    if (count == 0) {
      throw new NoSuchElementException();
    }

    return elementData[--count];
  }

  /**
   * Inserts the specified boolean value as a component in this array at the specified
   * index. Each component in this array with an index greater or equal to the specified
   * index is shifted upward to have an index one greater than the value it had previously.
   * The index parameter must be a value greater than or equal to 0 and less than or equal to the
   * current size of the array. (If the index is equal to the current size of the array,
   * the new element is appended to the array.)<p>
   *
   * @param      value the element to be inserted.
   * @param      index the position at which to insert the new component.
   * @exception  ArrayIndexOutOfBoundsException if the index is invalid.
   */
  public void insertElementAt(boolean value, int index) {
    if (index > count) {
      throw new ArrayIndexOutOfBoundsException(index + " > " + count);
    }

    ensureCapacity(count + 1);
    System.arraycopy(elementData, index, elementData, index + 1, count - index);
    elementData[index] = value;
    count++;
  }

  /**
   * Adds the specified component to the end of this array, increasing its size by one.
   * The capacity of this array is increased if its size becomes greater than its
   * capacity.<p>
   *
   * @param   value a boolean value to be added.
   */
  public void addElement(boolean value) {
    ensureCapacity(count + 1);
    elementData[count++] = value;
  }

  /**
   * Removes the first (lowest-indexed) occurrence of the argument from this array. If
   * the boolean value is found in this array, each component in the array with an index
   * greater or equal to the boolean's index is shifted downward to have an index one
   * smaller than the value it had previously.<p>
   *
   * @param   value the boolean value to be removed.
   * @return  true if the argument was a component of this array, false otherwise.
   */
  public boolean removeElement(boolean value) {
    for (int i = 0; i < count; i++) {
      if (elementData[i] == value) {
        int j = count - i - 1;

        if (j > 0) {
          System.arraycopy(elementData, i + 1, elementData, i, j);
        }

        count--;
        return true;
      }
    } 

    return false;
  }

  /**
   * Removes all components from this array and sets its size to zero.<p>
   */
  public void removeAllElements() {
    count = 0;
  }

  /**
   * Returns a clone of this array. The copy will contain a reference to a clone of the
   * internal data array, not a reference to the original internal data array of this
   * ArrayBoolean object.<p>
   *
   * @return  a clone of this array.
   */
  public Object clone() {
    ArrayBoolean v = (ArrayBoolean) super.clone();
    v.elementData = new boolean[count];
    System.arraycopy(elementData, 0, v.elementData, 0, count);
    return v;
  }

  /**
   * Returns an array containing all of the elements in this ArrayBoolean object
   * in the correct order.<p>
   *
   * @return  array of the elements in this ArrayBoolean object.
   */
  public boolean[] toArray() {
    boolean[] result = new boolean[count];
    System.arraycopy(elementData, 0, result, 0, count);
    return result;
  }

  /**
   * Returns the element at the specified position in this ArrayBoolean object.<p>
   *
   * @param  index the index of the element to be returned.
   * @return  the element in the specified position of this array.
   * @exception ArrayIndexOutOfBoundsException if index is out of range.
   */
  public boolean get(int index) {
    if (index >= count) {
      throw new ArrayIndexOutOfBoundsException(index + " >= " + count);
    }

    return elementData[index];
  }

  /**
   * Replaces the element at the specified position in this ArrayBoolean
   * with the specified element.<p>
   *
   * @param  index the index of the element to be replaced.
   * @param  element the element to be stored at the specified position.
   * @return  the element which was previously at the specified position.
   * @exception ArrayIndexOutOfBoundsException if index is out of range.
   */
  public boolean set(int index, boolean element) {
    if (index >= count) {
      throw new ArrayIndexOutOfBoundsException(index);
    }

    boolean oldValue = elementData[index];
    elementData[index] = element;
    return oldValue;
  }

  /**
   * Appends the specified element to the end of this ArrayBoolean.<p>
   *
   * @param  value the element to be appended to this ArrayBoolean.
   */
  public void add(boolean value) {
    ensureCapacity(count + 1);
    elementData[count++] = value;
  }

  /**
   * Removes the element at the specified position in this ArrayBoolean. Shifts any
   * subsequent elements to the left (subtracts one from their indices). Returns the
   * element that was removed from the ArrayBoolean.
   *
   * @param index index of the element to be removed
   * @return the element witch was removed
   * @exception ArrayIndexOutOfBoundsException index out of range
   */
  public boolean remove(int index) {
    return removeAt(index);
  }

  /**
   * Removes the first occurrence of the specified element in this ArrayBoolean.
   * If the array does not contain the element, it remains unchanged.<p>
   *
   * @param  value element to be removed from this ArrayBoolean, if present.
   * @return true if the ArrayBoolean contained the specified element, false otherwise.
   */
  public boolean remove(boolean value) {
    return removeElement(value);
  }

  /**
   * Inserts the specified element at the specified position in this ArrayBoolean.
   * Shifts the element currently at that position (if any) and any subsequent elements
   * to the right (i.e. adds one to their indices).<p>
   *
   * @param index the index at which the specified element is to be inserted.
   * @param element the element to be inserted.
   * @exception ArrayIndexOutOfBoundsException if index is out of range.
   */
  public void add(int index, boolean element) {
    insertElementAt(element, index);
  }

  /**
   * Removes the element at the specified position in this ArrayBoolean. Shifts any
   * subsequent elements to the left (subtracts one from their indices). Returns the
   * element that was removed from the ArrayBoolean.<p>
   *
   * @param index the index of the element to be removed.
   * @return the element which was removed.
   * @exception ArrayIndexOutOfBoundsException if index is out of range.
   */
  public boolean removeAt(int index) {
    if (index >= count) {
      throw new ArrayIndexOutOfBoundsException(index + " >= " + count);
    } else if (index < 0) {
      throw new ArrayIndexOutOfBoundsException(index + " < " + 0);
    }

    boolean oldValue = elementData[index];
    int j = count - index - 1;

    if (j > 0) {
      System.arraycopy(elementData, index + 1, elementData, index, j);
    }

    count--;
    return oldValue;
  }

  /**
   * Removes all of the elements from this ArrayBoolean object.
   * The ArrayBoolean will be empty after this call returns (unless it throws an exception).<p>
   */
  public void clear() {
    removeAllElements();
  }

  /**
   * Returns the hash code value for this ArrayBoolean object.
   *
   * @return  the hash code value for this ArrayBoolean.
   */
  public int hashCode() {
    int theCode = 0;

    for (int i = 0; i < count; i++) {
      theCode += i;

      if (elementData[i]) {
        theCode ^= 1;
      }
    } 

    return theCode;
  }

  /**
   * Returns a string representation of this ArrayBoolean, containing the String
   * representation of each of its elements.<p>
   *
   * @return  string representation of this ArrayBoolean.
   */
  public String toString() {
    StringBuffer s = new StringBuffer(super.toString());
    s.append("\n[ size = " + count + "; capacityIncrement = " + capacityIncrement + "; capacity = " + elementData.length + " ]\n the elements are: [ ");

    for (int i = 0; i < count - 1; i++) {
      s.append(elementData[i] + ", ");
    } 

    if (count != 0) {
      s.append(elementData[count - 1]);
    }

    s.append(" ]");
    return s.toString();
  }

  /**
   * Compares this ArrayBoolean for equality with the specified object. They are equal
   * if and only if the specified object is also an instance of ArrayBoolean, they both
   * have the same size, and for each position the elements that they contain at this
   * position are equal.<p>
   *
   * @param  arrayBoolean the object that this ArrayBoolean is compared to.
   * @return  true if the object is equal to this ArrayBoolean, false otherwise.
   */
  public boolean equals(Object arrayBoolean) {
    if (!(arrayBoolean instanceof ArrayBoolean)) {
      return false;
    }

    return ((ArrayBoolean) arrayBoolean).equals_(this);
  }

  /**
   * Compares this ArrayBoolean for equality with the specified object. They are equal
   * if and only if the specified object is also an instance of ArrayBoolean, they both
   * have the same size, and for each position the elements that they contain at this
   * position are equal.<p>
   *
   * @param  arrayBoolean ArrayBoolean that is compared to.
   * @return  true if the object is equal to this ArrayBoolean, false otherwise.
   */
  protected boolean equals_(ArrayBoolean arrayBoolean) {
    if (count != arrayBoolean.count) {
      return false;
    }

    for (int i = 0; i < count; i++) {
      if (elementData[i] != arrayBoolean.elementData[i]) {
        return false;
      }
    } 

    return true;
  }

  /**
   * Returns an enumeration of the components of this array. The returned
   * enumeration object will be an instance of the EnumerationBoolean class.
   * It will generate all the items in this array in the following manner:<p>
   * The first item generated is the item at index 0 in this ArrayBoolean,
   * then the item at index - 1, and so on.<p>
   *
   * @return  an enumeration of the components of this array.
   */
  public EnumerationBoolean elements() {
    return new EnumerationBoolean() {

      private int counter = 0;

      public boolean hasMoreElements() {
        return counter < count;
      }

      public boolean nextElement() {
        return elementData[counter++];
      }

    };
  }

  /**
   * Removes from this array all of the elements whose index is between
   * fromIndex, inclusive and toIndex, exclusive. Shifts any succeeding
   * elements to the left (i.e. reduces their index). This call shortens
   * the ArrayBoolean by (toIndex - fromIndex) number of elements.<p>
   * (If toIndex==fromIndex, this operation has no effect.)<p>
   *
   * @param fromIndex index of the first element to be removed.
   * @param toIndex the index after the one of the last element to be removed.
   */
  public void removeRange(int fromIndex, int toIndex) {
    if (fromIndex > toIndex) {
      throw new IllegalArgumentException(fromIndex + " >= " + toIndex);
    }

    if (toIndex > count) {
      throw new ArrayIndexOutOfBoundsException(toIndex + " > " + count);
    }

    System.arraycopy(elementData, toIndex, elementData, fromIndex, count - toIndex);
    count -= (toIndex - fromIndex);
  }

  /**
   * Replaces a range of elements starting at the specified position in
   * this ArrayBoolean with the elements from specified ArrayBoolean array.
   * If the length of array is greater than the count of remaining
   * elements in this ArrayBoolean an appropriate exception is thrown.<p>
   *
   * @param  index the starting position for replacement.
   * @param  arrayBoolean the source ArrayBoolean which contains the elements for replacement.
   */
  public void setAll(int index, ArrayBoolean arrayBoolean) {
    setAll(index, arrayBoolean, 0, arrayBoolean.size());
  }

  /**
   * Replaces a range of elements starting at the specified position in
   * this ArrayBoolean with the elements from specified ArrayBoolean array.
   * If the length field is greater than the count of remaining
   * elements in this ArrayBoolean an appropriate exception is thrown.<p>
   *
   * @param  index the starting position for replacement.
   * @param  arrayBoolean the source ArrayBoolean which contains the elements for replacement.
   * @param  offset new elements are taken from this position of array.
   * @param  length  the count of elements which will be replaced.
   */
  public void setAll(int index, ArrayBoolean arrayBoolean, int offset, int length) {
    if (index + length > count) {
      throw new ArrayIndexOutOfBoundsException("(index + length)=" + (index + length) + " > size=" + count);
    }

    arrayBoolean.copyInto(offset, elementData, index, length);
  }

  /**
   * Replaces a range of elements starting at the specified position in
   * this ArrayBoolean with the elements from specified boolean array.
   * If the length of array is greater than count of remaining
   * elements in this ArrayBoolean an appropriate exception is thrown.<p>
   *
   * @param  index the starting position for replacement.
   * @param  array the source boolean[] which contains the elements for replacement.
   */
  public void setAll(int index, boolean[] array) {
    setAll(index, array, 0, array.length);
  }

  /**
   * Replaces a range of elements starting at the specified position in
   * this ArrayBoolean with the elements from specified boolean array.
   * If the length field is greater than count of remaining
   * elements in this ArrayBoolean an appropriate exception is thrown.<p>
   *
   * @param  index the starting position for replacement.
   * @param  array the source array which contains the elements for replacement.
   * @param  offset new elements are taken from this position of array.
   * @param  length the count of elements which will be replaced.
   */
  public void setAll(int index, boolean[] array, int offset, int length) {
    if (index + length > count) {
      throw new ArrayIndexOutOfBoundsException("(index + length)=" + (index + length) + " > size=" + count);
    }

    System.arraycopy(array, offset, elementData, index, length);
  }

  /**
   * Adds all elements from specified ArrayBoolean at the end of this ArrayBoolean.<p>
   *
   * @param  arrayBoolean the ArrayBoolean which contains new elements.
   */
  public void addAll(ArrayBoolean arrayBoolean) {
    addAll(arrayBoolean, 0, arrayBoolean.size());
  }

  /**
   * Adds a specified range of elements from ArrayBoolean array at the end
   * of this ArrayBoolean.<p>
   *
   * @param  arrayBoolean the ArrayBoolean which contains new elements.
   * @param  offset new elements are taken from this position of array.
   * @param  length the count of elements which will be replaced.
   */
  public void addAll(ArrayBoolean arrayBoolean, int offset, int length) {
    ensureCapacity(count + length);
    arrayBoolean.copyInto(offset, elementData, count, length);
    count += length;
  }

  /**
   * Adds all the elements from specified boolean array at the end of this ArrayBoolean.<p>
   *
   * @param  array the byte{} which contains new elements.
   */
  public void addAll(boolean[] array) {
    addAll(array, 0, array.length);
  }

  /**
   * Adds a specified range of elements from boolean array at the end
   * of this ArrayBoolean.<p>
   *
   * @param  array the boolean array which contains new elements.
   * @param  offset new elements are taken from this position of array.
   * @param  length the count of elements which will be replaced.
   */
  public void addAll(boolean[] array, int offset, int length) {
    ensureCapacity(count + length);
    System.arraycopy(array, offset, elementData, count, length);
    count += length;
  }

  /**
   * Adds all the elements from specified ArrayBoolean at the specified position
   * of this ArrayBoolean.<p>
   */
  public void addAll(int index, ArrayBoolean arrayBoolean) {
    addAll(index, arrayBoolean, 0, arrayBoolean.size());
  }

  /**
   * Adds specified range of elements from ArrayBoolean, array at the
   * specified position of this ArrayBoolean.<p>
   *
   * @param  index the starting position for adding.
   * @param  arrayBoolean the ArrayBoolean which contains new elements.
   * @param  offset new elements are taken from this position of array.
   * @param  length the count of elements which will be replaced.
   */
  public void addAll(int index, ArrayBoolean arrayBoolean, int offset, int length) {
    if (index > count) {
      throw new ArrayIndexOutOfBoundsException("index=" + index + " > size=" + count);
    }

    ensureCapacity(count + length);
    System.arraycopy(elementData, index, elementData, index + length, count - index);
    arrayBoolean.copyInto(offset, elementData, index, length);
    count += length;
  }

  /**
   * Adds all elements from specified boolean array at the specified position
   * of this ArrayBoolean.<p>
   *
   * @param  index the starting position for adding.
   * @param  array the boolean[] which contains new elements.
   */
  public void addAll(int index, boolean[] array) {
    addAll(index, array, 0, array.length);
  }

  /**
   * Adds specified range of elements from boolean array at the specified position
   * of this ArrayBoolean.<p>
   *
   * @param  index the starting position for adding.
   * @param  array he boolean array which contains new elements.
   * @param  offset new elements are taken from this position of array.
   * @param  length the count of elements which will be replaced.
   */
  public void addAll(int index, boolean[] array, int offset, int length) {
    if (index > count) {
      throw new ArrayIndexOutOfBoundsException("index=" + index + " > size=" + count);
    }

    ensureCapacity(count + length);
    System.arraycopy(elementData, index, elementData, index + length, count - index);
    System.arraycopy(array, offset, elementData, index, length);
    count += length;
  }

  // -----------------------------------------------------------
  // -------------- Some serialization magic -------------------
  // -----------------------------------------------------------
  /**
   * This method is used by Java serializer.<p>
   *
   * @param  stream an output stream to write this object to.
   * @exception  IOException  if an IO error occurs.
   */
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    stream.writeInt(elementData.length);

    for (int i = 0; i < count; i++) {
      stream.writeBoolean(elementData[i]);
    } 
  }

  /**
   * This method is used by Java serializer.<p>
   *
   * @param  stream an input stream to read this object from.
   * @exception  IOException if an IO error occurs.
   * @exception  ClassNotFoundException if the class file of this Class was not found.
   */
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    elementData = new boolean[stream.readInt()];

    for (int i = 0; i < count; i++) {
      elementData[i] = stream.readBoolean();
    } 
  }

}

