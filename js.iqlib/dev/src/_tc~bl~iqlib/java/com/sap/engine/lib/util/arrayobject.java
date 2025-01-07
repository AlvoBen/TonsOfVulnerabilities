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

import com.sap.engine.lib.util.base.Pointer;
import com.sap.engine.lib.util.iterators.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.NoSuchElementException;

/**
 * The ArrayObject class implements a growable array of
 * Objects. Like an array, it contains components that can be
 * accessed using an integer index. However, the size of a
 * ArrayObject can grow or shrink as needed to accommodate
 * adding and removing items after the ArrayObject has been created.<p>
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
 *
 * <b>Note</b>: The fastest way to traverse the set is by Enumeration returned by
 *       elements() method.<p>
 *
 * @author Nikola Arnaudov, George Manev, Andrei Gatev
 * @version 1.0
 */
public class ArrayObject extends AbstractDataStructure {

  static final long serialVersionUID = -5433094647863795927L;
  /**
   * The array buffer into which the elements of the ArrayObject are stored.
   * The capacity of the ArrayObject is the length of this array buffer.<p>
   */
  protected transient Object elementData[];
  /**
   * The amount by which the capacity of the array is automatically incremented when its
   * size becomes greater than its capacity. If the capacity increment is 0, the capacity
   * of the array is doubled each time it needs to grow.<p>
   */
  protected int capacityIncrement;

  /**
   * Constructs an empty ArrayObject with the specified initial capacity and capacity increment.<p>
   *
   * @param   initialCapacity the initial capacity of the array.
   * @param   capacityIncrement the amount by which the capacity is increased when the array overflows.
   * @exception IllegalArgumentException if the specified initial capacity is negative.
   */
  public ArrayObject(int initialCapacity, int capacityIncrement) {
    if (initialCapacity < 0) {
      throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
    }

    elementData = new Object[initialCapacity];
    this.capacityIncrement = capacityIncrement;
  }

  /**
   * Constructs an empty ArrayObject with the specified initial capacity
   * and with its capacity increment equal to zero.<p>
   *
   * @param   initialCapacity the initial capacity of the array.
   * @exception IllegalArgumentException if the specified initial capacity is negative.
   */
  public ArrayObject(int initialCapacity) {
    this(initialCapacity, 0);
  }

  /**
   * Constructs an empty ArrayObject so that its internal data array has size 10
   * and its standard capacity increment is zero.<p>
   */
  public ArrayObject() {
    this(10);
  }

  /**
   * Constructs an ArrayObject from elements of the specified Object array.<p>
   *
   * @param   array the Object array which contains the elements.
   * @param   offset the elements are taken from this position of array.
   * @param   length the count of elements which will be added.
   * @param   capacityIncrement the amount by which the capacity is increased
   *                              when the array overflows.
   */
  public ArrayObject(Object[] array, int offset, int length, int capacityIncrement) {
    elementData = new Object[length];
    System.arraycopy(array, offset, elementData, 0, length);
    this.capacityIncrement = capacityIncrement;
    count = length;
  }

  /**
   * Constructs an ArrayObject from elements of the specified Object array.
   * Capacity increment is zero.<p>
   *
   * @param   array the Object array which contains the elements.
   */
  public ArrayObject(Object[] array) {
    this(array, 0, array.length, 0);
  }

  /**
   * Constructs an ArrayObject from elements of the specified ArrayObject.<p>
   *
   * @param   arrayObject the ArrayObject which contains the elements.
   * @param   offset the elements are taken from this position of arrayObject.
   * @param   length the count of elements which will be added.
   * @param   capacityIncrement the amount by which the capacity is increased
   *                              when the array overflows.
   */
  public ArrayObject(ArrayObject arrayObject, int offset, int length, int capacityIncrement) {
    this(length, capacityIncrement);
    arrayObject.copyInto(offset, elementData, 0, length);
    count = length;
  }

  /**
   * Constructs an ArrayObject from elements of the specified ArrayObject.
   * Capacity increment is zero.<p>
   *
   * @param   arrayObject the ArrayObject which contains the elements.
   */
  public ArrayObject(ArrayObject arrayObject) {
    elementData = arrayObject.toArray();
    count = elementData.length;
  }

  /**
   * Copies the components of this ArrayObject into the specified array. The item at index k
   * in this array is copied into component k of anArray[]. The array must be big enough
   * to hold all the elements in this ArrayObject, else an IndexOutOfBoundsException is thrown.<p>
   *
   * @param   anArray the array into which the components get copied.
   */
  public void copyInto(Object anArray[]) {
    System.arraycopy(elementData, 0, anArray, 0, count);
  }

  /**
   * Gets range of elements starting at the specified position in
   * this ArrayObject in specified Object array.
   * If the length field is greater than count of remaining
   * elements in this ArrayObject an appropriate exception is thrown.<p>
   *
   * @param  index the starting position for getting.
   * @param  array the destionation array.
   * @param  offset  new elements are set from this position of array.
   * @param  length  the count of elements which will be set.
   */
  public void copyInto(int index, Object[] array, int offset, int length) {
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
   * an ArrayObject.<p>
   */
  public void trimToSize() {
    int oldCapacity = elementData.length;

    if (count < oldCapacity) {
      Object newData[] = new Object[count];
      System.arraycopy(elementData, 0, newData, 0, count);
      elementData = newData;
    }
  }

  /**
   * Increases the capacity of this ArrayObject, if necessary, to ensure that it can hold
   * at least the number of components specified by the minimum capacity argument. If the
   * current capacity of this ArrayObject is less than minCapacity, then its capacity is
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

      Object newData[] = new Object[capacity];
      System.arraycopy(elementData, 0, newData, 0, count);
      elementData = newData;
    }
  }

  /**
   * Sets the size of this ArrayObject. If the new size is less than the current size,
   * all components at index newSize and greater are discarded.<p>
   *
   * @param   newSize the new size of this ArrayObject.
   * @throws  IllegalArgumentException if new size is negative.
   */
  public void setSize(int newSize) {
    if (newSize < 0) {
      throw new IllegalArgumentException("Illegal newSize: " + newSize);
    }

    if (newSize > count) {
      ensureCapacity(newSize);
    } else {
      for (int i = newSize; i < count; i++) {
        elementData[i] = null;
      } 
    }
    count = newSize;
  }

  /**
   * Returns the current capacity of this ArrayObject.<p>
   *
   * @return  the current capacity (the length of its internal data arary, kept in the
   *          field elementData of this ArrayObject)
   */
  public int capacity() {
    return elementData.length;
  }

  /**
   * Tests if the specified Object is a component in this array.<p>
   *
   * @param   elem an Object value.
   * @return  true if the specified Object value is equal to a component in this array
   *          and false otherwise.
   */
  public boolean contains(Object elem) {
    return indexOf(elem, 0) >= 0;
  }

  /**
   * Searches for the first occurence of the given argument.<p>
   *
   * @param   elem an Object value.
   * @return  the index of the first occurrence of the argument in this array, that is,
   *          the smallest value k such that elem is equal to elementData[k];
   *          returns -1 if the Object is not found.
   */
  public int indexOf(Object elem) {
    return indexOf(elem, 0);
  }

  /**
   * Searches for the first occurence of the given element after the specified index.<p>
   *
   * @param   elem an Object value.
   * @param   index the index to start searching from.
   * @return  the index of the first occurrence of the Object value in this array
   *          at position index or later in the array, that is the smallest value k,
   *          such that elem is equal to elementData[k] and k is greater than index;
   *          returns -1 if the Object is not found.
   */
  public int indexOf(Object elem, int index) {
    for (int i = index; i < count; i++) {
      if (elem == null ? elementData[i] == null : elem.equals(elementData[i])) {
        return i;
      }
    } 

    return -1;
  }

  /**
   * Returns the index of the last occurrence of the specified Object value in this array.<p>
   *
   * @param   elem an Object value.
   * @return  the index of the last occurrence of the specified Object value in this array,
   *          that is, the largest value k such that elem is equal to elementData[k];
   *          returns -1 if the Object is not found.
   */
  public int lastIndexOf(Object elem) {
    return lastIndexOf(elem, count - 1);
  }

  /**
   * Searches backwards for the specified Object value, starting from the specified index,
   * and returns an index to it.<p>
   *
   * @param  elem an Object value.
   * @param  index the index to start searching from.
   * @return the index of the last occurrence of the specified Object value in this array
   *         at position less than index in the array, that is, the largest value k such
   *         that elem is equal to elementData[k] and k is greater than index;
   *         returns -1 if the Object is not found.
   */
  public int lastIndexOf(Object elem, int index) {
    for (int i = index; i >= 0; i--) {
      if (elem == null ? elementData[i] == null : elem.equals(elementData[i])) {
        return i;
      }
    } 

    return -1;
  }

  /**
   * Returns the component at the specified index.<p>
   *
   * @param      index an index into this array.
   * @return     the Object value at the specified index.
   * @exception  ArrayIndexOutOfBoundsException if the index is negative or not less
   *             than the current size of this ArrayObject object.
   */
  public Object elementAt(int index) {
    if (index >= count) {
      throw new ArrayIndexOutOfBoundsException(index + " >= " + count);
    }

    return elementData[index];
  }

  /**
   * Returns the first component (the item at index 0) of this array.<p>
   *
   * @return     the first component of this array.
   * @exception  NoSuchElementException  if this array has no components.
   */
  public Object firstElement() {
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
  public Object lastElement() {
    if (count == 0) {
      throw new NoSuchElementException();
    }

    return elementData[count - 1];
  }

  /**
   * Sets the element at the specified index of this array to be the specified Object
   * value. The previous component at that position is discarded. The index must be
   * greater than or equal to 0 and less than the current size of the array.<p>
   *
   * @param      value an Object value to be set.
   * @param      index the specified index.
   * @exception  ArrayIndexOutOfBoundsException if the index was invalid.
   */
  public void setElementAt(Object value, int index) {
    if (index >= count) {
      throw new ArrayIndexOutOfBoundsException(index + " >= " + count);
    }

    elementData[index] = value;
  }

  /**
   * Deletes the component at the specified index. Each component in this array with an
   * index greater or equal to the specified index is shifted downward to have an index
   * one smaller than the value it had previously. The size of this array is decreased
   * by 1. The index must be a value greater than or equal to 0 and less than the current
   * size of the array.<p>
   *
   * @param      index the index of the Object value to remove.
   * @exception  ArrayIndexOutOfBoundsException if the index was invalid.
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

    elementData[--count] = null;
  }

  /**
   * Deletes the last component in the array.<p>
   *
   * @return     the deleted element.
   * @exception  NoSuchElementException if the array is empty.
   */
  public Object removeLastElement() {
    if (count == 0) {
      throw new NoSuchElementException();
    }

    Object last = elementData[--count];
    elementData[count] = null;
    return last;
  }

  /**
   * Inserts the specified Object value as a component in this array at the specified
   * index. Each component in this array with an index greater or equal to the specified
   * index is shifted upward to have an index one greater than the value it had previously.
   * The index must be a value greater than or equal to 0 and less than or equal to the
   * current size of the array. (If the index is equal to the current size of the array,
   * the new element is appended to the array.)<p>
   *
   * @param      value the element to be inserted.
   * @param      index the position at which to insert the new component.
   * @exception  ArrayIndexOutOfBoundsException if the index is invalid.
   */
  public void insertElementAt(Object value, int index) {
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
   * @param   value an Object value to be added.
   */
  public void addElement(Object value) {
    ensureCapacity(count + 1);
    elementData[count++] = value;
  }

  /**
   * Removes the first (lowest-indexed) occurrence of the argument from this array. If
   * the Object value is found in this array, each component in the array with an index
   * greater or equal to the Object's index is shifted downward to have an index one
   * smaller than the value it had previously.<p>
   *
   * @param   value the Object value to be removed.
   * @return  true if the argument was a component of this array, false otherwise.
   */
  public boolean removeElement(Object value) {
    int i = indexOf(value);

    if (i >= 0) {
      removeElementAt(i);
      return true;
    }

    return false;
  }

  /**
   * Removes all components from this array and sets its size to zero.<p>
   */
  public void removeAllElements() {
    /*
     * One may also use memory consuming but probably faster realization
     * than by the means of a for(.., .., ..) cycle, like the following:
     *
     * elementData = new Object[elementData.length];
     */
    for (int i = 0; i < count; i++) {
      elementData[i] = null;
    } 

    count = 0;
  }

  /**
   * Returns a clone of this array. The copy will contain a reference to a clone of the
   * internal data array, not a reference to the original internal data array of this
   * ArrayObject object.<p>
   *
   * @return  a clone of this array.
   */
  public Object clone() {
    ArrayObject v = (ArrayObject) super.clone();
    v.elementData = new Object[count];
    System.arraycopy(elementData, 0, v.elementData, 0, count);
    return v;
  }

  /**
   * Returns a clone of this array. The copy will contain a reference to a clone of the
   * internal data array, not a reference to the original internal data array of this
   * ArrayObject object.<p>
   *
   * @return  a clone of this array.
   */
  public Object deepClone() {
    ArrayObject v = (ArrayObject) super.clone();
    v.elementData = new Object[count];

    for (int i = 0; i < count; i++) {
      v.elementData[i] = ((DeepCloneable) elementData[i]).deepClone();
    } 

    return v;
  }

  /**
   * Returns an array containing all of the elements in this ArrayObject object
   * in the correct order.<p>
   *
   * @return  array of the elements in this ArrayObject object.
   */
  public Object[] toArray() {
    Object[] result = new Object[count];
    System.arraycopy(elementData, 0, result, 0, count);
    return result;
  }

  /**
   * Returns an array containing all of the elements in this ArrayObject object.
   * if the passed Object[] is big enough - it's used from offset 0, if not - a new one is
   * created and returned.
   *
   * @param result the array to store the elements in, if it's bit enough
   *
   * @return  array of the elements in this ArrayObject object.
   */
  public Object[] toArray(Object[] result) {

    if (result.length < count) {
      result = (Object[])java.lang.reflect.Array.newInstance(result.getClass().getComponentType(), count);
    }

    System.arraycopy(elementData, 0, result, 0, count);
    return result;
  }


  /**
   * Returns the element at the specified position in this ArrayObject object.
   *
   * @param  index the index of the element to be returned.
   * @return  the element in the specified position of this array.
   * @exception ArrayIndexOutOfBoundsException if index is out of range.
   */
  public Object get(int index) {
    if (index >= count) {
      throw new ArrayIndexOutOfBoundsException(index + " >= " + count);
    }

    return elementData[index];
  }

  /**
   * Replaces the element at the specified position in this ArrayObject
   * with the specified element.<p>
   *
   * @param  index the index of the element to be replaced.
   * @param  element the element to be stored at the specified position.
   * @return the element which was previously at the specified position.
   * @exception ArrayIndexOutOfBoundsException if index is out of range.
   */
  public Object set(int index, Object element) {
    if (index >= count) {
      throw new ArrayIndexOutOfBoundsException(index + " >= " + count);
    }

    Object oldValue = elementData[index];
    elementData[index] = element;
    return oldValue;
  }

  /**
   * Appends the specified element to the end of this ArrayObject.<p>
   *
   * @param value the element to be appended to this ArrayObject
   */
  public void add(Object value) {
    ensureCapacity(count + 1);
    elementData[count++] = value;
  }

  /**
   * Removes the element at the specified position in this ArrayObject. Shifts any
   * subsequent elements to the left (subtracts one from their indices). Returns the
   * element that was removed from the ArrayObject.
   *
   * @param index index of the element to be removed
   * @return the element witch was removed
   * @exception ArrayIndexOutOfBoundsException index out of range
   */
  public Object remove(int index) {
    return removeAt(index);
  }

  /**
   * Removes the first occurrence of the specified element in this ArrayObject.
   * If the array does not contain the element, it remains unchanged.<p>
   *
   * @param  value element to be removed from this ArrayObject, if present.
   * @return true if the ArrayObject contained the specified element, false otherwise.
   */
  public boolean remove(Object value) {
    return removeElement(value);
  }

  /**
   * Inserts the specified element at the specified position in this ArrayObject.
   * Shifts the element currently at that position (if any) and any subsequent elements
   * to the right (i.e. adds one to their indices).<p>
   *
   * @param index index at which the specified element is to be inserted.
   * @param element element to be inserted.
   * @exception ArrayIndexOutOfBoundsException if index is out of range.
   */
  public void add(int index, Object element) {
    insertElementAt(element, index);
  }

  /**
   * Removes the element at the specified position in this ArrayObject. Shifts any
   * subsequent elements to the left (subtracts one from their indices). Returns the
   * element that was removed from the ArrayObject.<p>
   *
   * @param index the index of the element to be removed.
   * @return the element witch was removed.
   * @exception ArrayIndexOutOfBoundsException if index is out of range.
   */
  public Object removeAt(int index) {
    if (index >= count) {
      throw new ArrayIndexOutOfBoundsException(index + " >= " + count);
    } else if (index < 0) {
      throw new ArrayIndexOutOfBoundsException(index + " < " + 0);
    }

    Object oldValue = elementData[index];
    int j = count - index - 1;

    if (j > 0) {
      System.arraycopy(elementData, index + 1, elementData, index, j);
    }

    elementData[--count] = null;
    return oldValue;
  }

  /**
   * Removes all of the elements from this ArrayObject object.
   * The ArrayObject will be empty after this call returns (unless it throws an exception).<p>
   */
  public void clear() {
    removeAllElements();
  }

  /**
   * Returns the hash code value for this ArrayObject object.<p>
   *
   * @return  the hash code value for this ArrayObject.
   */
  public int hashCode() {
    int theCode = 0;

    for (int i = 0; i < count; i++) {
      theCode += i;

      if (elementData[i] != null) {
        theCode ^= elementData[i].hashCode();
      }
    } 

    return theCode;
  }

  /**
   * Returns a string representation of this ArrayObject, containing the string
   * representation of each of its elements.<p>
   *
   * @return  string representation of this ArrayObject.
   */
  public String toString() {
    StringBuffer s = new StringBuffer(super.toString());
    s.append("\n[ size = " + count + "; capacityIncrement = " + capacityIncrement + "; capacity = " + elementData.length + " ]\n the elements are: [ ");

    for (int i = 0; i < count - 1; i++) {
      //It is also posible to append the value of elementData[i].toString()...
      s.append(elementData[i] + ", ");
    } 

    if (count != 0) {
      //It is also posible to append the value of elementData[count - 1].toString()...
      s.append(elementData[count - 1]);
    }

    s.append(" ]");
    return s.toString();
  }

  /**
   * Compares this ArrayObject for equality with the specified ArrayObject. They are equal
   * if and only if the specified object is also an instance of ArrayObject, they both
   * have the same size, and for each position the elements that they contain at this
   * position are equal.<p>
   *
   * @param  arrayObject the object that this ArrayObject is compared to.
   * @return  true if the object is equal to this ArrayObject, false otherwise.
   */
  public boolean equals(Object arrayObject) {
    if (!(arrayObject instanceof ArrayObject)) {
      return false;
    }

    return ((ArrayObject) arrayObject).equals_(this);
  }

  protected boolean equals_(ArrayObject arrayObject) {
    if (count != arrayObject.count) {
      return false;
    }

    for (int i = 0; i < count; i++) {
      if (elementData[i] == null ? arrayObject.elementData[i] != null : !(elementData[i].equals(arrayObject.elementData[i]))) {
        return false;
      }
    } 

    return true;
  }

  /**
   * Returns enumeration of the components of this list.
   * The returned SnapShotEnumeration object will generate
   * all items in this list. Changes to this object will not
   * affect the underlying data structure.
   *
   * @return     enumeration of the components of this list
   */
  public SnapShotEnumeration elementsEnumeration() {
    return new ArrayEnumeration(toArray());
  }

  /**
   * Removes from this array all of the elements whose index is between
   * fromIndex, inclusive and toIndex, exclusive. Shifts any succeeding
   * elements to the left (i.e. reduces their index). This call shortens
   * the ArrayObject by (toIndex - fromIndex) number of elements.
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
    int countOfElemToRemove = toIndex - fromIndex;

    for (int i = count - countOfElemToRemove; i < count; i++) {
      elementData[i] = null;
    } 

    count -= countOfElemToRemove;
  }

  /**
   * Sorts the array of Objects into ascending numerical order.
   * The sorting algorithm is a tuned quicksort, adapted from Jon
   * L. Bentley and M. Douglas McIlroy's "Engineering a Sort Function",
   * Software-Practice and Experience, Vol. 23(11) P. 1249-1265 (November
   * 1993). This algorithm offers n*log(n) performance on many data sets
   * that cause other quicksorts to degrade to quadratic performance.<p>
   */
  public void sort() {
    qsort(0, count);
  }

  /**
   * Sorts the array of Objects into ascending or descending numerical order,
   * according to the specified boolean flag. If the flag is true this ArrayObject
   * is sorted into descending order, else into ascending.
   * The sorting algorithm is a tuned quicksort, adapted from Jon
   * L. Bentley and M. Douglas McIlroy's "Engineering a Sort Function",
   * Software-Practice and Experience, Vol. 23(11) P. 1249-1265 (November
   * 1993). This algorithm offers n*log(n) performance on many data sets
   * that cause other quicksorts to degrade to quadratic performance.
   *
   * @param  descending specifies the order which to sort the array into.
   */
  public void sort(boolean descending) {
    if (descending) {
      qsortDesc(0, count);
      return;
    }

    qsort(0, count);
  }

  /**
   * Sorts the specified sub-array of Objects into ascending order.<p>
   *
   * @param fromIndex the index of the first element (inclusive) to be sorted.
   * @param toIndex the index of the last element (exclusive) to be sorted.
   * @throws IllegalArgumentException if fromIndex &gt; toIndex
   * @throws ArrayIndexOutOfBoundsException if fromIndex &lt; 0 or
   *         toIndex &gt; the current length of the array
   */
  public void sort(int fromIndex, int toIndex) {
    // Checks for illegal arguments.
    rangeCheck(fromIndex, toIndex);
    qsort(fromIndex, toIndex - fromIndex);
  }

  /**
   * Sorts the specified sub-array of Objects into ascending or descending
   * numerical order, according to the specified boolean flag. If the flag is true
   * this ArrayObject is sorted into descending order, else into ascending.<p>
   *
   * @param fromIndex the index of the first element (inclusive) to be sorted.
   * @param toIndex the index of the last element (exclusive) to be sorted.
   * @param descending specifies the order which to sort the array into.
   * @throws IllegalArgumentException if fromIndex &gt; toIndex
   * @throws ArrayIndexOutOfBoundsException if fromIndex &lt; 0 or
   *         toIndex &gt; the current length of the array
   */
  public void sort(int fromIndex, int toIndex, boolean descending) {
    // Checks for illegal arguments.
    rangeCheck(fromIndex, toIndex);

    if (descending) {
      qsortDesc(fromIndex, toIndex - fromIndex);
    } else {
      qsort(fromIndex, toIndex - fromIndex);
    }
  }

  /**
   * Sorts the specified sub-array of Objects into descending order.<p>
   *
   * @param off starting offset.
   * @param len the number of Objects to be sorted.
   */
  private void qsortDesc(int off, int len) {
    // Insertion sort on smallest arrays
    if (len < 7) {
      for (int i = off; i < len + off; i++) {
        for (int j = i; j > off && (((Comparable) elementData[j - 1]).compareTo(elementData[j]) < 0)
        /*elementData[j - 1] < elementData[j]*/
        ; j--) {
          swap(j, j - 1);
        } 
      } 

      return;
    }

    // Choose a partition element, v
    int m = off + (len >>> 1); // Small arrays, middle element

    if (len > 7) {
      int l = off;
      int n = off + len - 1;

      if (len > 40) { // Big arrays, pseudomedian of 9
        int s = len >>> 3;
        l = med3(l, l + s, l + 2 * s);
        m = med3(m - s, m, m + s);
        n = med3(n - 2 * s, n - s, n);
      }

      m = med3(l, m, n); // Mid-size, med of 3
    }

    Object v = elementData[m];
    // Establish Invariant: v* (<v)* (>v)* v*
    int a = off;
    int b = a;
    int c = off + len - 1;
    int d = c;

    while (true) {
      while (b <= c && (((Comparable) elementData[b]).compareTo(v) >= 0)
      /*elementData[b] >= v*/
      ) {
        if (((Comparable) elementData[b]).compareTo(v) == 0
        /*elementData[b] == v*/
        ) {
          swap(a++, b);
        }

        b++;
      }

      while (c >= b && (((Comparable) elementData[c]).compareTo(v) <= 0)
      /*elementData[c] <= v*/
      ) {
        if (((Comparable) elementData[c]).compareTo(v) == 0
        /*elementData[c] == v*/
        ) {
          swap(c, d--);
        }

        c--;
      }

      if (b > c) {
        break;
      }

      swap(b++, c--);
    }

    // Swap partition elements back to middle
    int n = off + len;
    int s;
    s = Math.min(a - off, b - a);
    vecSwap(off, b - s, s);
    s = Math.min(d - c, n - d - 1);
    vecSwap(b, n - s, s);

    // Recursively sort non-partition-elements
    if ((s = b - a) > 1) {
      qsortDesc(off, s);
    }

    if ((s = d - c) > 1) {
      qsortDesc(n - s, s);
    }
  }

  /**
   * Sorts the specified sub-array of Objects into ascending order.<p>
   *
   * @param off starting offset.
   * @param len the number of Objects to be sorted.
   */
  private void qsort(int off, int len) {
    // Insertion sort on smallest arrays
    if (len < 7) {
      for (int i = off; i < len + off; i++) {
        for (int j = i; j > off && (((Comparable) elementData[j - 1]).compareTo(elementData[j]) > 0)
        /*elementData[j - 1] > elementData[j]*/
        ; j--) {
          swap(j, j - 1);
        } 
      } 

      return;
    }

    // Choose a partition element, v
    int m = off + (len >>> 1); // Small arrays, middle element

    if (len > 7) {
      int l = off;
      int n = off + len - 1;

      if (len > 40) { // Big arrays, pseudomedian of 9
        int s = len >>> 3;
        l = med3(l, l + s, l + 2 * s);
        m = med3(m - s, m, m + s);
        n = med3(n - 2 * s, n - s, n);
      }

      m = med3(l, m, n); // Mid-size, med of 3
    }

    Object v = elementData[m];
    // Establish Invariant: v* (<v)* (>v)* v*
    int a = off;
    int b = a;
    int c = off + len - 1;
    int d = c;

    while (true) {
      while (b <= c && (((Comparable) elementData[b]).compareTo(v) <= 0)
      /*elementData[b] <= v*/
      ) {
        if (((Comparable) elementData[b]).compareTo(v) == 0
        /*elementData[b] == v*/
        ) {
          swap(a++, b);
        }

        b++;
      }

      while (c >= b && (((Comparable) elementData[c]).compareTo(v) >= 0)
      /*elementData[c] >= v*/
      ) {
        if (((Comparable) elementData[c]).compareTo(v) == 0
        /*elementData[c] == v*/
        ) {
          swap(c, d--);
        }

        c--;
      }

      if (b > c) {
        break;
      }

      swap(b++, c--);
    }

    // Swap partition elements back to middle
    int n = off + len;
    int s;
    s = Math.min(a - off, b - a);
    vecSwap(off, b - s, s);
    s = Math.min(d - c, n - d - 1);
    vecSwap(b, n - s, s);

    // Recursively sort non-partition-elements
    if ((s = b - a) > 1) {
      qsort(off, s);
    }

    if ((s = d - c) > 1) {
      qsort(n - s, s);
    }
  }

  /**
   * Swaps elementData[a] with elementData[b].
   *
   * @param a first element index.
   * @param b second element index.
   */
  private void swap(int a, int b) {
    Object temp = elementData[a];
    elementData[a] = elementData[b];
    elementData[b] = temp;
  }

  /**
   * Swaps elementData[a .. (a+n-1)] with elementData[b .. (b+n-1)].
   *
   * @param a first element index.
   * @param  b second element index.
   * @param n the number of elements to be swapped.
   */
  private void vecSwap(int a, int b, int n) {
    for (int i = 0; i < n; i++, a++, b++) {
      swap(a, b);
    } 
  }

  /**
   * Returns the index of the median of the three indexed elements.
   *
   * @param a first element index.
   * @param  b second element index.
   * @param c third element index.
   * @return the index of the median of the three indexed elements.
   */
  private int med3(int a, int b, int c) {
    return (((Comparable) elementData[a]).compareTo(elementData[b]) < 0 
    /*elementData[a] < elementData[b]*/
    ? (((Comparable) elementData[b]).compareTo(elementData[c]) < 0 
    /*elementData[b] < elementData[c]*/
    ? b : ((Comparable) elementData[a]).compareTo(elementData[c]) < 0 
    /*elementData[a] < elementData[c]*/
    ? c : a) : (((Comparable) elementData[b]).compareTo(elementData[c]) > 0 
    /*elementData[b] > elementData[c]*/
    ? b : ((Comparable) elementData[a]).compareTo(elementData[c]) > 0 
    /*elementData[a] > elementData[c]*/
    ? c : a));
  }

  /**
   * Searches this array of Objects for the specified value using the
   * binary search algorithm. The array must be sorted into ASCENDING order
   * (as by the sort() method, above) prior to making this call. If it is NOT,
   * the results are undefined. If the array contains multiple elements
   * with the specified value, there is no guarantee which one will be found.<p>
   *
   * @param  key the value to be searched for.
   * @return index of the search key, if it is contained in the array;
   *         otherwise, (-(insertion point) - 1). The insertion point
   *         is defined as the point at which the key would be inserted
   *         into the ArrayObject, i.e. the index of the first element
   *         greater than the key, or the size of this ArrayObject object,
   *         if all elements in the array are less than the specified key.
   *         Note that this guarantees that the returned value will be greater than 0
   *         if and only if the key is found.
   */
  public int binarySearch(Object key) {
    int low = 0;
    int high = count - 1;

    while (low <= high) {
      int mid = (low + high) >>> 1;
      Object midVal = elementData[mid];

      if (((Comparable) midVal).compareTo(key) < 0
      /*midVal < key*/
      ) {
        low = mid + 1;
      } else if (((Comparable) midVal).compareTo(key) > 0
      /*midVal > key*/
      ) {
        high = mid - 1;
      } else {
        return mid;
      }
    }

    return -(low + 1);
  }

  /**
   * Searches this array of Objects for the specified value using the
   * binary search algorithm. The array must be sorted into ASCENDING or
   * DESCENDING order (as by the sort(boolean descending) method, above)
   * prior to making this call. If it is NOT, the results are undefined.
   * If the array contains multiple elements with the specified value,
   * there is no guarantee which one will be found.<p>
   *
   * @param  key the value to be searched for.
   * @param  descending  specifies whether the array is sorted into
   *                     DESCENDING or into ASCENDING order.
   * @return index of the search key, if it is contained in the array;
   *         otherwise, (-(insertion point) - 1). The insertion point
   *         is defined as the point at which the key should be inserted
   *         into the ArrayObject in order for it to stay sorted.
   *         Note that this guarantees that the returned value will be greater than 0
   *         if and only if the key is found.
   */
  public int binarySearch(Object key, boolean descending) {
    int low = 0;
    int high = count - 1;

    if (descending) {
      while (low <= high) {
        int mid = (low + high) >>> 1;
        Object midVal = elementData[mid];

        if (((Comparable) midVal).compareTo(key) > 0
        /*midVal > key*/
        ) {
          low = mid + 1;
        } else if (((Comparable) midVal).compareTo(key) < 0
        /*midVal < key*/
        ) {
          high = mid - 1;
        } else {
          return mid;
        }
      }
    } else {
      while (low <= high) {
        int mid = (low + high) >>> 1;
        Object midVal = elementData[mid];

        if (((Comparable) midVal).compareTo(key) < 0
        /*midVal < key*/
        ) {
          low = mid + 1;
        } else if (((Comparable) midVal).compareTo(key) > 0
        /*midVal > key*/
        ) {
          high = mid - 1;
        } else {
          return mid;
        }
      }
    }

    return -(low + 1);
  }

  /**
   * Searches the specified subarray of Objects for the specified value
   * using the binary search algorithm. The array must be sorted into ASCENDING
   * order (as by the sort() method) prior to making this call. If it is NOT,
   * the results are undefined. If the subarray contains multiple elements
   * with the specified value, there is no guarantee which one will be found.<p>
   *
   * @param  key the value to be searched for.
   * @param  low the index of the first element (inclusive) of the subarray.
   * @param  high  the index of the last element (inclusive) of the subarray.
   * @return index of the search key, if it is contained in the subarray;
   *         otherwise, (-(insertion point) - 1). The insertion point
   *         is defined as the point at which the key would be inserted
   *         into the given subarray of this ArrayObject.
   *         Note that this guarantees that the returned value will be greater than 0
   *         if and only if the key is found.
   */
  public int binarySearch(Object key, int low, int high) {
    // Checks for illegal arguments.
    rangeCheck(low, high + 1);

    while (low <= high) {
      int mid = (low + high) >>> 1;
      Object midVal = elementData[mid];

      if (((Comparable) midVal).compareTo(key) < 0
      /*midVal < key*/
      ) {
        low = mid + 1;
      } else if (((Comparable) midVal).compareTo(key) > 0
      /*midVal > key*/
      ) {
        high = mid - 1;
      } else {
        return mid;
      }
    }

    return -(low + 1);
  }

  /**
   * Searches the given subarray of Objects for the specified value using
   * the binary search algorithm. The subarray must be sorted into ASCENDING
   * or DESCENDING order (as by the sort(int .., int .., boolean ..) method)
   * prior to making this call. If it is NOT, the results are undefined.
   * If the subarray contains multiple elements with the specified value,
   * there is no guarantee which one will be found.
   *
   * @param  key the value to be searched for.
   * @param  low the index of the first element (inclusive) of the subarray.
   * @param  high  the index of the last element (inclusive) of the subarray.
   * @param  descending  specifies whether the subarray is sorted into
   *                     DESCENDING or into ASCENDING order.
   * @return index of the search key, if it is contained in the subarray;
   *         otherwise, (-(insertion point) - 1). The insertion point
   *         is defined as the point at which the key should be inserted
   *         into that part of the ArrayObject in order for it to stay sorted.
   *         Note that this guarantees that the returned value will be greater than 0
   *         if and only if the key is found.
   */
  public int binarySearch(Object key, int low, int high, boolean descending) {
    // Checks for illegal arguments.
    rangeCheck(low, high + 1);

    if (descending) {
      while (low <= high) {
        int mid = (low + high) >>> 1;
        Object midVal = elementData[mid];

        if (((Comparable) midVal).compareTo(key) > 0
        /*midVal > key*/
        ) {
          low = mid + 1;
        } else if (((Comparable) midVal).compareTo(key) < 0
        /*midVal < key*/
        ) {
          high = mid - 1;
        } else {
          return mid;
        }
      }
    } else {
      while (low <= high) {
        int mid = (low + high) >>> 1;
        Object midVal = elementData[mid];

        if (((Comparable) midVal).compareTo(key) < 0
        /*midVal < key*/
        ) {
          low = mid + 1;
        } else if (((Comparable) midVal).compareTo(key) > 0
        /*midVal > key*/
        ) {
          high = mid - 1;
        } else {
          return mid;
        }
      }
    }

    return -(low + 1);
  }

  /**
   * Check that fromIndex and toIndex are in range, and throw an
   * appropriate exception if they aren't.<p>
   *
   * @param fromIndex the first element index.
   * @param toIndex the second element index.
   */
  private void rangeCheck(int fromIndex, int toIndex) {
    if (fromIndex > toIndex) {
      throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
    }
    if (fromIndex < 0) {
      throw new ArrayIndexOutOfBoundsException(fromIndex);
    }
    if (toIndex > count) {
      throw new ArrayIndexOutOfBoundsException(toIndex);
    }
  }

  /**
   * Replaces range of elements starting at the specified position in
   * this ArrayObject with the elements from specified ArrayObject v.
   * If the length of arrayObject is greater than count of remaining
   * elements in this ArrayObject an appropriate exception is thrown.<p>
   *
   * @param  index the starting position for setting.
   * @param  arrayObject the source ArrayObject which contains the elements for setting.
   */
  public void setAll(int index, ArrayObject arrayObject) {
    setAll(index, arrayObject, 0, arrayObject.size());
  }

  /**
   * Replaces range of elements starting at the specified position in
   * this ArrayObject with the elements from specified ArrayObject v.
   * If the length field is greater than count of remaining
   * elements in this ArrayObject an appropriate exception is thrown.<p>
   *
   * @param  index the starting position for setting.
   * @param  arrayObject the source ArrayObject which contains the elements for setting.
   * @param  offset new elements are taken from this position of arrayObject.
   * @param  length the count of elements which will be set.
   */
  public void setAll(int index, ArrayObject arrayObject, int offset, int length) {
    if (index + length > count) {
      throw new ArrayIndexOutOfBoundsException("(index + length)=" + (index + length) + " > size=" + count);
    }

    arrayObject.copyInto(offset, elementData, index, length);
  }

  /**
   * Replaces range of elements starting at the specified position in
   * this ArrayObject with the elements from specified Object array.
   * If the length of array is greater than count of remaining
   * elements in this ArrayObject an appropriate exception is thrown.<p>
   *
   * @param  index the starting position for setting.
   * @param  array the source array which contains the elements for settimg.
   */
  public void setAll(int index, Object[] array) {
    setAll(index, array, 0, array.length);
  }

  /**
   * Replaces range of elements starting at the specified position in
   * this ArrayObject with the elements from specified Object array.
   * If the length field is greater than count of remaining
   * elements in this ArrayObject an appropriate exception is thrown.<p>
   *
   * @param  index the starting position for setting.
   * @param  array the source array which contains the elements for setting.
   * @param  offset  new elements are taken from this position of array.
   * @param  length  the count of elements which will be set.
   */
  public void setAll(int index, Object[] array, int offset, int length) {
    if (index + length > count) {
      throw new ArrayIndexOutOfBoundsException("(index + length)=" + (index + length) + " > size=" + count);
    }

    System.arraycopy(array, offset, elementData, index, length);
  }

  /**
   * Adds all elements from specified ArrayObject at the end of this ArrayObject.<p>
   *
   * @param  arrayObject the source ArrayObject which contains the elements for adding.
   */
  public void addAll(ArrayObject arrayObject) {
    addAll(arrayObject, 0, arrayObject.size());
  }

  /**
   * Adds specified range of elements from ArrayObject v at the end
   * of this ArrayObject.<p>
   *
   * @param  arrayObject the source ArrayObject which contains the elements for adding.
   * @param  offset  new elements are taken from this position of array.
   * @param  length  the count of elements which will be added.
   */
  public void addAll(ArrayObject arrayObject, int offset, int length) {
    ensureCapacity(count + length);
    arrayObject.copyInto(offset, elementData, count, length);
    count += length;
  }

  /**
   * Adds all elements from specified Object array at the end of this ArrayObject.<p>
   *
   * @param  array the Object array which contains new elements for adding.
   */
  public void addAll(Object[] array) {
    addAll(array, 0, array.length);
  }

  /**
   * Adds specified range of elements from Object array at the end
   * of this ArrayObject.<p>
   *
   * @param  array the Object array which contains new elements for adding.
   * @param  offset  new elements are taken from this position of array.
   * @param  length  the count of elements which will be added.
   */
  public void addAll(Object[] array, int offset, int length) {
    ensureCapacity(count + length);
    System.arraycopy(array, offset, elementData, count, length);
    count += length;
  }

  /**
   * Adds all elements from specified ArrayObject at the specified position
   * of this ArrayObject.<p>
   *
   * @param  index the posittion to start from.
   * @param  arrayObject the source ArrayObject which contains the elements for adding.
   */
  public void addAll(int index, ArrayObject arrayObject) {
    addAll(index, arrayObject, 0, arrayObject.size());
  }

  /**
   * Adds specified range of elements from ArrayObject v at the
   * specified position of this ArrayObject.<p>
   *
   * @param  index the posittion to start from.
   * @param  arrayObject the source ArrayObject which contains the elements for adding.
   * @param  offset  new elements are taken from this position of array.
   * @param  length  the count of elements which will be added.
   */
  public void addAll(int index, ArrayObject arrayObject, int offset, int length) {
    if (index > count) {
      throw new ArrayIndexOutOfBoundsException("index=" + index + " > size=" + count);
    }

    ensureCapacity(count + length);
    System.arraycopy(elementData, index, elementData, index + length, count - index);
    arrayObject.copyInto(offset, elementData, index, length);
    count += length;
  }

  /**
   * Adds all elements from specified Object array at the specified position
   * of this ArrayObject.<p>
   *
   * @param  index the starting position for adding.
   * @param  array the source array which contains the elements for adding.
   */
  public void addAll(int index, Object[] array) {
    addAll(index, array, 0, array.length);
  }

  /**
   * Adds specified range of elements from Object array at the specified position
   * of this ArrayObject.<p>
   *
   * @param  index the starting position for adding.
   * @param  array the source array which contains the elements for adding.
   * @param  offset  new elements are taken from this position of array.
   * @param  length  the count of elements which will be added.
   */
  public void addAll(int index, Object[] array, int offset, int length) {
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
   * This method is used by java serializer.<p>
   *
   * @param  stream an output stream to write this object to.
   * @exception  IOException if an IO error occurs.
   */
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    stream.writeInt(elementData.length);

    for (int i = 0; i < count; i++) {
      stream.writeObject(elementData[i]);
    } 
  }

  /**
   * This method is used by java deserializer.<p>
   *
   * @param  stream an input stream to read this object from.
   * @exception  IOException if an IO error occurs.
   * @exception  ClassNotFoundException  if the class file of this Class was not found.
   */
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    elementData = new Object[stream.readInt()];

    for (int i = 0; i < count; i++) {
      elementData[i] = stream.readObject();
    } 
  }

  /**
   * Returns a RandomAccessIterator over this ArrayObject.
   * It will generate all the items in this array in the following manner:<p>
   * The first item generated is the item at index 0 in this ArrayObject,
   * then the item at index 1, and so on.<p>
   * You can use the iterator to change the elements of this ArrayObject.<p>
   *
   * @return  RootIterator of the components of this array.
   */
  public RootIterator elementsIterator() {
    return new ArrayObjectIterator();
  }

  public Pointer[] toPointerArray() {
    throw new NotSupportedException("Use toArray() method instead!");
  }

  // ---------------------------------------------------------
  // ---------------- The Iterator methods -------------------
  // ---------------------------------------------------------
  protected Object iterGet(int counter) {
    return elementData[counter];
  }

  protected Object iterAdd(Object obj, int pos) {
    ensureCapacity(count + 1);
    System.arraycopy(elementData, pos, elementData, pos + 1, count - pos);
    elementData[pos] = obj;
    count++;
    return elementData[pos];
  }

  protected Object iterChange(Object obj, int counter) {
    Object old = elementData[counter];
    elementData[counter] = obj;
    return old;
  }

  protected Object iterRemove(int counter) {
    Object old = elementData[counter];
    int j = count - counter - 1;

    if (j > 0) {
      System.arraycopy(elementData, counter + 1, elementData, counter, j);
    }

    elementData[--count] = null;
    return old;
  }

  /**
   * An implementation of RandomAccessIterator for the ArrayObject
   */
  protected class ArrayObjectIterator implements RandomAccessIterator { //$JL-CLONE$
    
    static final long serialVersionUID = 8333597504350342857L;

    private int start = 0;
    private int end = count;
    private int counter = start;

    public Object get() {
      if (counter >= end) {
        throw new IteratorException("End of Iterator reached: " + counter + " >= " + end);
      }

      return iterGet(counter);
    }

    public boolean isAtBegin() {
      return counter == start;
    }

    public boolean isAtEnd() {
      return counter == end;
    }

    public RootDataStructure getDataStructure() {
      return ArrayObject.this;
    }

    public Object next() {
      if (counter >= end) {
        throw new IteratorException("End of Iterator reached: " + counter + " >= " + end);
      }

      return iterGet(counter++);
    }

    public Object next(int n) {
      int tmp = counter + n;

      if (tmp >= end) {
        throw new IteratorException("Specified position exceeds the end of Iterator: " + tmp + " >= " + end);
      } else if (tmp < start) {
        throw new IteratorException("Specified position is underneath the start of Iterator: " + tmp + " < " + start);
      }

      counter = tmp;
      return iterGet(counter++);
    }

    public void setStartFromIterator(RootIterator iterator) {
      ArrayObjectIterator aoi = (ArrayObjectIterator) iterator;

      if (ArrayObject.this != aoi.getDataStructure()) {
        throw new IteratorException("An attempt to set start from an Iterator over a different ArrayObject instance!");
      }

      int tmp = aoi.counter;

      if (tmp > end) {
        throw new IteratorException("An attempt to set the start behind the end of the Iterator: " + tmp + " > " + end);
      }

      start = tmp;
      counter = start;
    }

    public void setEndFromIterator(RootIterator iterator) {
      ArrayObjectIterator aoi = (ArrayObjectIterator) iterator;

      if (ArrayObject.this != aoi.getDataStructure()) {
        throw new IteratorException("An attempt to set end from an Iterator over a different ArrayObject instance!");
      }

      int tmp = aoi.counter;

      if (tmp < counter) {
        throw new IteratorException("An attempt to set the end ahead of the current position of the Iterator: " + tmp + " < " + counter);
      }

      end = tmp;
    }

    public Object add(Object obj) {
      return iterAdd(obj, end++);
    }

    public Object change(Object obj) {
      if (counter >= end) {
        throw new IteratorException("End of Iterator reached!");
      }

      return iterChange(obj, counter);
    }

    public Object remove() {
      if (counter >= end) {
        throw new IteratorException("End of Iterator reached!");
      }

      end--;
      return iterRemove(counter);
    }

    public Object insert(Object obj) {
      end++;
      return iterAdd(obj, counter);
    }

    public int size() {
      return end - start;
    }

    public boolean isInsertable() {
      return true;
    }

    public boolean isRemoveable() {
      return true;
    }

    public boolean isChangeable() {
      return true;
    }

    public boolean isAddable() {
      return true;
    }

    public Object prev() {
      if (counter <= start) {
        throw new IteratorException("Beginning of Iterator reached: " + counter + " <= " + start);
      }

      return iterGet(--counter);
    }

    public Object prev(int n) {
      int tmp = counter - n;

      if (tmp > end) {
        throw new IteratorException("Specified position exceeds the end of Iterator: " + tmp + " > " + end);
      } else if (tmp <= start) {
        throw new IteratorException("Specified position is underneath the start of Iterator: " + tmp + " <= " + start);
      }

      counter = tmp;
      return iterGet(--counter);
    }

    public int currentPosition() {
      return counter;
    }

    public Object jumpTo(int n) {
      if (n > end) {
        throw new IteratorException("Specified position exceeds the end of Iterator: " + n + " > " + end);
      } else if (n < start) {
        throw new IteratorException("Specified position is underneath the start of Iterator: " + n + " < " + start);
      }

      counter = n;
      return iterGet(counter);
    }

  }

}

