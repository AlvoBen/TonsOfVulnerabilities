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
 * The ArrayChar class implements a growable array of
 * chars. Like an array, it contains components that can be
 * accessed using an integer index. However, the size of a
 * ArrayChar can grow or shrink as needed to accommodate
 * adding and removing items after the ArrayChar has been created.<p>
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
 * @version 4.0
 */
public class ArrayChar extends PrimitiveTypeDataStructure {

  static final long serialVersionUID = -5223804234351663028L;

  /**
   * The array buffer into which the elements of the ArrayChar are stored.
   * The capacity of the ArrayChar is the length of this array buffer.<p>
   */
  protected transient char elementData[];
  /**
   * The amount by which the capacity of the array is automatically incremented when its
   * size becomes greater than its capacity. If the capacity increment is 0, the capacity
   * of the array is doubled each time it needs to grow.<p>
   */
  protected int capacityIncrement;

  /**
   * Constructs an empty ArrayChar with the specified initial capacity and capacity increment.<p>
   *
   * @param   initialCapacity the initial capacity of the array.
   * @param   capacityIncrement the amount by which the capacity is increased when the array overflows.
   * @exception IllegalArgumentException if the specified initial capacity is negative.
   */
  public ArrayChar(int initialCapacity, int capacityIncrement) {
    if (initialCapacity < 0) {
      throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
    }

    elementData = new char[initialCapacity];
    this.capacityIncrement = capacityIncrement;
  }

  /**
   * Constructs an empty ArrayChar with the specified initial capacity
   * and with its capacity increment equal to zero.<p>
   *
   * @param   initialCapacity the initial capacity of the array.
   * @exception IllegalArgumentException if the specified initial capacity is negative.
   */
  public ArrayChar(int initialCapacity) {
    this(initialCapacity, 0);
  }

  /**
   * Constructs an empty ArrayChar so that its internal data array has size 10
   * its standard capacity increment is zero and with no capacity limit.<p>
   */
  public ArrayChar() {
    this(10);
  }

  /**
   * Constructs an ArrayChar from elements of the specified char array.<p>
   *
   * @param   array the char array which contains the elements.
   * @param   offset the elements are taken from this position of array.
   * @param   length   the count of elements which will be added.
   * @param   capacityIncrement the amount by which the capacity is increased
   *                              when the array overflows.
   */
  public ArrayChar(char[] array, int offset, int length, int capacityIncrement) {
    elementData = new char[length];
    System.arraycopy(array, offset, elementData, 0, length);
    this.capacityIncrement = capacityIncrement;
    count = length;
  }

  /**
   * Constructs an ArrayChar from elements of the specified char array.
   * Capacity increment is zero.<p>
   *
   * @param   array the char array which contains the elements.
   */
  public ArrayChar(char[] array) {
    this(array, 0, array.length, 0);
  }

  /**
   * Constructs an ArrayChar from elements of the specified ArrayChar.<p>
   *
   * @param   arrayChar the ArrayChar which contains the elements.
   * @param   offset the elements are taken from this position of arrayChar.
   * @param   length   the count of elements which will be added.
   * @param   capacityIncrement the amount by which the capacity is increased
   *                              when the array overflows
   */
  public ArrayChar(ArrayChar arrayChar, int offset, int length, int capacityIncrement) {
    this(length, capacityIncrement);
    arrayChar.copyInto(offset, elementData, 0, length);
    count = length;
  }

  /**
   * Constructs an ArrayChar from elements of the specified ArrayChar.
   * Capacity increment is zero.<p>
   *
   * @param   arrayChar the char array which contains the elements.
   */
  public ArrayChar(ArrayChar arrayChar) {
    elementData = arrayChar.toArray();
    count = elementData.length;
  }

  /**
   * Copies the components of this ArrayChar into the specified array. The item at index k
   * in this array is copied into component k of anArray[]. The array must be big enough
   * to hold all the elements in this ArrayChar, else an IndexOutOfBoundsException is thrown.<p>
   *
   * @param   anArray the array into which the components get copied.
   */
  public void copyInto(char anArray[]) {
    System.arraycopy(elementData, 0, anArray, 0, count);
  }

  /**
   * Gets range of elements starting at the specified position in
   * this ArrayChar in specified char array.
   * If the length field is greater than count of remaining
   * elements in this ArrayChar an appropriate exception is thrown.<p>
   *
   * @param  index the starting position for getting.
   * @param  array the destionation array.
   * @param  offset  new elements are set from this position of array.
   * @param  length  the count of elements which will be set.
   */
  public void copyInto(int index, char[] array, int offset, int length) {
    if (index + length > count) {
      throw new ArrayIndexOutOfBoundsException("(index + length)=" + (index + length) + " > size=" + count);
    }

    System.arraycopy(elementData, index, array, offset, length);
  }

  /**
   * Trims the capacity of this array to be the array's current size. If the capacity
   * of this array is larger than its current size, then the capacity is changed to
   * equal the size by replacing its internal data array, kept in the field element	Data,
   * with a smaller one. An application can use this operation to minimize the storage of
   * an ArrayChar.<p
   */
  public void trimToSize() {
    int oldCapacity = elementData.length;

    if (count < oldCapacity) {
      char newData[] = new char[count];
      System.arraycopy(elementData, 0, newData, 0, count);
      elementData = newData;
    }
  }

  /**
   * Increases the capacity of this ArrayChar, if necessary, to ensure that it can hold
   * at least the number of components specified by the minimum capacity argument. If the
   * current capacity of this ArrayChar is less than minCapacity, then its capacity is
   * increased by replacing its internal data array, kept in the field elementData, with
   * a larger one. The size of the new data array will be the old size plus
   * capacityIncrement, unless the value of capacityIncrement is nonpositive, in which
   * case the new capacity will be twice the old capacity; but if this new size is still
   * smaller than minCapacity, then the new capacity will be minCapacity.
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

      char newData[] = new char[capacity];
      System.arraycopy(elementData, 0, newData, 0, count);
      elementData = newData;
    }
  }

  /**
   * Sets the size of this ArrayChar. If the new size is less than the current size,
   * all components at index newSize and greater are discarded.<p>
   *
   * @param   newSize the new size of this ArrayChar.
   * @throws  IllegalArgumentException if new size is negative.
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
   * Returns the current capacity of this ArrayChar.<p>
   *
   * @return  the current capacity (the length of its internal data array, kept in the
   *          field elementData of this ArrayChar).
   */
  public int capacity() {
    return elementData.length;
  }

  /**
   * Tests if the specified char is a component in this array.<p>
   *
   * @param   elem a char value.
   * @return  true if the specified char value is equal to a component in this array
   *          and false otherwise.
   */
  public boolean contains(char elem) {
    return indexOf(elem, 0) >= 0;
  }

  /**
   * Searches for the first occurence of the given argument.<p>
   *
   * @param   elem a char value.
   * @return  the index of the first occurrence of the argument in this array, that is,
   *          the smallest value k such that elem is equal to elementData[k];
   *          returns -1 if the char is not found.
   */
  public int indexOf(char elem) {
    return indexOf(elem, 0);
  }

  /**
   * Searches for the first occurence of the given element after the specified index.<p>
   *
   * @param   elem a char value.
   * @param   index the index to start searching from.
   * @return  the index of the first occurrence of the char value in this array
   *          at position index or later in the array, that is the smallest value k,
   *          such that elem is equal to elementData[k] and k is greater than index;
   *          returns -1 if the char is not found
   */
  public int indexOf(char elem, int index) {
    for (int i = index; i < count; i++) {
      if (elem == elementData[i]) {
        return i;
      }
    } 

    return -1;
  }

  /**
   * Returns the index of the last occurrence of the specified char value in this array.<p>
   *
   * @param   elem the desired char value.
   * @return  the index of the last occurrence of the specified char value in this array,
   *          that is, the largest value k such that elem is equal to elementData[k];
   *          returns -1 if the char is not found.
   */
  public int lastIndexOf(char elem) {
    return lastIndexOf(elem, count - 1);
  }

  /**
   * Searches backwards for the specified char value, starting from the specified index,
   * and returns an index to it.<p>
   *
   * @param  elem the desired char value.
   * @param  index the index to start searching from.
   * @return the index of the last occurrence of the specified char value in this array
   *         at position less than index in the array, that is, the largest value k such
   *         that elem is equal to elementData[k] and k is greater than index;
   *         returns -1 if the char is not found
   */
  public int lastIndexOf(char elem, int index) {
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
   * @return     the char value at the specified index.
   * @exception  ArrayIndexOutOfBoundsException if the index is negative or not less
   *             than the current size of this ArrayChar object.
   */
  public char elementAt(int index) {
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
  public char firstElement() {
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
  public char lastElement() {
    if (count == 0) {
      throw new NoSuchElementException();
    }

    return elementData[count - 1];
  }

  /**
   * Sets the element at the specified index of this array to be the specified char
   * value. The previous component at that position is discarded. The index must be
   * greater than or equal to 0 and less than the current size of the array.<p>
   *
   * @param      value> a char value to be set.
   * @param      index the specified index.
   * @exception  ArrayIndexOutOfBoundsException if the index was invalid.
   */
  public void setElementAt(char value, int index) {
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
   * @param      index the index of the char value to remove.
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

    count--;
  }

  /**
   * Deletes the last component in the array.<p>
   *
   * @return     the deleted element.
   * @exception  NoSuchElementException if the array is empty.
   */
  public char removeLastElement() {
    if (count == 0) {
      throw new NoSuchElementException();
    }

    return elementData[--count];
  }

  /**
   * Inserts the specified char value as a component in this array at the specified
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
  public void insertElementAt(char value, int index) {
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
   * @param   value a char value to be added.
   */
  public void addElement(char value) {
    ensureCapacity(count + 1);
    elementData[count++] = value;
  }

  /**
   * Removes all components from this array and sets its size to zero.<p>
   */
  public void removeAllElements() {
    count = 0;
  }

  /**
   * Returns the element at the specified position in this ArrayChar object.<p>
   *
   * @param  index the index of the element to be returned.
   * @return  the element in the specified position of this array.
   * @exception ArrayIndexOutOfBoundsException if index is out of range.
   */
  public char get(int index) {
    if (index >= count) {
      throw new ArrayIndexOutOfBoundsException(index + " >= " + count);
    }

    return elementData[index];
  }

  /**
   * Replaces the element at the specified position in this ArrayChar
   * with the specified element.<\u00ef>
   *
   * @param  index the index of the element to be replaced.
   * @param  element the element to be stored at the specified position.
   * @return the element which was previously at the specified position.
   * @exception ArrayIndexOutOfBoundsException if index is out of range.
   */
  public char set(int index, char element) {
    if (index >= count) {
      throw new ArrayIndexOutOfBoundsException(index);
    }

    char oldValue = elementData[index];
    elementData[index] = element;
    return oldValue;
  }

  /**
   * Removes the element at the specified position in this ArrayChar. Shifts any
   * subsequent elements to the left (subtracts one from their indices). Returns the
   * element that was removed from the ArrayChar.
   *
   * @param index index of the element to be removed
   * @return the element witch was removed
   * @exception ArrayIndexOutOfBoundsException index out of range
   */
  public char remove(int index) {
    return removeAt(index);
  }

  /**
   * Removes the first occurrence of the specified element in this ArrayChar.
   * If the array does not contain the element, it remains unchanged.<p>
   *
   * @param  value element to be removed from this ArrayChar, if present.
   * @return the element that was removed.
   */
  public boolean remove(char value) {
    return removeElement(value);
  }

  /**
   * Inserts the specified element at the specified position in this ArrayChar.
   * Shifts the element currently at that position (if any) and any subsequent elements
   * to the right (i.e. adds one to their indices).<p>
   *
   * @param index the index at which the specified element is to be inserted.
   * @param element the element to be inserted.
   * @exception ArrayIndexOutOfBoundsException if index is out of range.
   */
  public void add(int index, char element) {
    insertElementAt(element, index);
  }

  /**
   * Removes the element at the specified position in this ArrayChar. Shifts any
   * subsequent elements to the left (subtracts one from their indices). Returns the
   * element that was removed from the ArrayChar.<p>
   *
   * @param  index the index of the element to be removed.
   * @return the element witch was removed.
   * @exception ArrayIndexOutOfBoundsException if index is out of range.
   */
  public char removeAt(int index) {
    if (index >= count) {
      throw new ArrayIndexOutOfBoundsException(index + " >= " + count);
    } else if (index < 0) {
      throw new ArrayIndexOutOfBoundsException(index + " < " + 0);
    }

    char oldValue = elementData[index];
    int j = count - index - 1;

    if (j > 0) {
      System.arraycopy(elementData, index + 1, elementData, index, j);
    }

    count--;
    return oldValue;
  }

  /**
   * Returns the hash code value for this ArrayChar object.<p>
   *
   * @return  the hash code value for this ArrayChar.
   */
  public int hashCode() {
    int theCode = 0;

    for (int i = 0; i < count; i++) {
      theCode += i;
      theCode ^= elementData[i];
    } 

    return theCode;
  }

  /**
   * Removes from this array all of the elements whose index is between
   * fromIndex, inclusive and toIndex, exclusive. Shifts any succeeding
   * elements to the left (i.e. reduces their index). This call shortens
   * the ArrayChar by (toIndex - fromIndex) number of elements.
   * (If toIndex==fromIndex, this operation has no effect.)
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
   * Sorts the array of chars into ascending numerical order.
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
   * Sorts the array of chars into ascending or descending numerical order,
   * according to the specified boolean flag. If the flag is true this ArrayChar
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
   * Sorts the specified sub-array of chars into ascending order.<p>
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
   * Sorts the specified sub-array of chars into ascending or descending
   * numerical order, according to the specified boolean flag. If the flag is true
   * this ArrayChar is sorted into descending order, else into ascending.<p>
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
   * Sorts the specified sub-array of chars into descending order.<p>
   *
   * @param off the starting offset.
   * @param len the number of chars to be sorted.
   */
  private void qsortDesc(int off, int len) {
    // Insertion sort on smallest arrays
    if (len < 7) {
      for (int i = off; i < len + off; i++) {
        for (int j = i; j > off && elementData[j - 1] < elementData[j]; j--) {
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

    char v = elementData[m];
    // Establish Invariant: v* (<v)* (>v)* v*
    int a = off;
    int b = a;
    int c = off + len - 1;
    int d = c;

    while (true) {
      while (b <= c && elementData[b] >= v) {
        if (elementData[b] == v) {
          swap(a++, b);
        }

        b++;
      }

      while (c >= b && elementData[c] <= v) {
        if (elementData[c] == v) {
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
   * Sorts the specified sub-array of chars into ascending order.<p>
   *
   * @param off the starting offset.
   * @param len the number of chars to be sorted.
   */
  private void qsort(int off, int len) {
    // Insertion sort on smallest arrays
    if (len < 7) {
      for (int i = off; i < len + off; i++) {
        for (int j = i; j > off && elementData[j - 1] > elementData[j]; j--) {
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

    char v = elementData[m];
    // Establish Invariant: v* (<v)* (>v)* v*
    int a = off;
    int b = a;
    int c = off + len - 1;
    int d = c;

    while (true) {
      while (b <= c && elementData[b] <= v) {
        if (elementData[b] == v) {
          swap(a++, b);
        }

        b++;
      }

      while (c >= b && elementData[c] >= v) {
        if (elementData[c] == v) {
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
   * Swaps elementData[a] with elementData[b].<p>
   *
   * @param a first element index.
   * @param b second element index.
   */
  private void swap(int a, int b) {
    char temp = elementData[a];
    elementData[a] = elementData[b];
    elementData[b] = temp;
  }

  /**
   * Swaps elementData[a .. (a+n-1)] with elementData[b .. (b+n-1)].<p
   *
   * @param a first element index.
   * @param b second element index.
   * @param n the  number of elements to be swapped.
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
   * @param b second element index.
   * @param c third element index.
   * @return the index of the median of the three indexed elements.
   */
  private int med3(int a, int b, int c) {
    return (elementData[a] < elementData[b] ? (elementData[b] < elementData[c] ? b : elementData[a] < elementData[c] ? c : a) : (elementData[b] > elementData[c] ? b : elementData[a] > elementData[c] ? c : a));
  }

  /**
   * Searches this array of chars for the specified value using the
   * binary search algorithm. The array must be sorted into ASCENDING order
   * (as by the sort() method, above) prior to making this call. If it is NOT,
   * the results are undefined. If the array contains multiple elements
   * with the specified value, there is no guarantee which one will be found.<p>
   *
   * @param  key the value to be searched for.
   * @return index of the search key, if it is contained in the array;
   *         otherwise, (-(insertion point) - 1). The insertion point
   *         is defined as the point at which the key would be inserted
   *         into the ArrayChar, i.e. the index of the first element
   *         greater than the key, or the size of this ArrayChar object,
   *         if all elements in the array are less than the specified key.
   *         Note that this guarantees that the returned value will be rgeater than 0
   *         if and only if the key is found.
   */
  public int binarySearch(char key) {
    int low = 0;
    int high = count - 1;

    while (low <= high) {
      int mid = (low + high) >>> 1;
      char midVal = elementData[mid];

      if (midVal < key) {
        low = mid + 1;
      } else if (midVal > key) {
        high = mid - 1;
      } else {
        return mid;
      }
    }

    return -(low + 1);
  }

  /**
   * Searches this array of chars for the specified value using the
   * binary search algorithm. The array must be sorted into ASCENDING or
   * DESCENDING order (as by the sort(boolean descending) method, above)
   * prior to making this call. If it is NOT, the results are undefined.
   * If the array contains multiple elements with the specified value,
   * there is no guarantee which one will be found.<p>
   *
   * @param  key the value to be searched for.
   * @param  descending specifies whether the array is sorted into
   *                     DESCENDING or into ASCENDING order.
   * @return index of the search key, if it is contained in the array;
   *         otherwise, (-(insertion point) - 1). The insertion point
   *         is defined as the point at which the key should be inserted
   *         into the ArrayChar in order for it to stay sorted.
   *         Note that this guarantees that the returned value will be regater than 0
   *         if and only if the key is found.
   */
  public int binarySearch(char key, boolean descending) {
    int low = 0;
    int high = count - 1;

    if (descending) {
      while (low <= high) {
        int mid = (low + high) >>> 1;
        char midVal = elementData[mid];

        if (midVal > key) {
          low = mid + 1;
        } else if (midVal < key) {
          high = mid - 1;
        } else {
          return mid;
        }
      }
    } else {
      while (low <= high) {
        int mid = (low + high) >>> 1;
        char midVal = elementData[mid];

        if (midVal < key) {
          low = mid + 1;
        } else if (midVal > key) {
          high = mid - 1;
        } else {
          return mid;
        }
      }
    }

    return -(low + 1);
  }

  /**
   * Searches the specified subarray of chars for the specified value
   * using the binary search algorithm. The array must be sorted into ASCENDING
   * order (as by the sort() method) prior to making this call. If it is NOT,
   * the results are undefined. If the subarray contains multiple elements
   * with the specified value, there is no guarantee which one will be found.<p>
   *
   * @param  key the value to be searched for.
   * @param  low the index of the first element (inclusive) of the subarray.
   * @param  high the index of the last element (inclusive) of the subarray.
   * @return index of the search key, if it is contained in the subarray;
   *         otherwise, (-(insertion point) - 1). The insertion point
   *         is defined as the point at which the key would be inserted
   *         into the given subarray of this ArrayChar.
   *         Note that this guarantees that the returned value will be greater than 0
   *         if and only if the key is found.
   */
  public int binarySearch(char key, int low, int high) {
    // Checks for illegal arguments.
    rangeCheck(low, high + 1);

    while (low <= high) {
      int mid = (low + high) >>> 1;
      char midVal = elementData[mid];

      if (midVal < key) {
        low = mid + 1;
      } else if (midVal > key) {
        high = mid - 1;
      } else {
        return mid;
      }
    }

    return -(low + 1);
  }

  /**
   * Searches the given subarray of chars for the specified value using
   * the binary search algorithm. The sub-array must be sorted into ASCENDING
   * or DESCENDING order (as by the sort(int .., int .., boolean ..) method)
   * prior to making this call. If it is NOT, the results are undefined.
   * If the subarray contains multiple elements with the specified value,
   * there is no guarantee which one will be found.<p>
   *
   * @param  key the value to be searched for.
   * @param  low the index of the first element (inclusive) of the subarray.
   * @param  high the index of the last element (inclusive) of the subarray.
   * @param  descending specifies whether the subarray is sorted into
   *                     DESCENDING or into ASCENDING order.
   * @return index of the search key, if it is contained in the subarray;
   *         otherwise, (-(insertion point) - 1). The insertion point
   *         is defined as the point at which the key should be inserted
   *         into that part of the ArrayChar in order for it to stay sorted.
   *         Note that this guarantees that the returned value will be greater than 0
   *         if and only if the key is found.
   */
  public int binarySearch(char key, int low, int high, boolean descending) {
    // Checks for illegal arguments.
    rangeCheck(low, high + 1);

    if (descending) {
      while (low <= high) {
        int mid = (low + high) >>> 1;
        char midVal = elementData[mid];

        if (midVal > key) {
          low = mid + 1;
        } else if (midVal < key) {
          high = mid - 1;
        } else {
          return mid;
        }
      }
    } else {
      while (low <= high) {
        int mid = (low + high) >>> 1;
        char midVal = elementData[mid];

        if (midVal < key) {
          low = mid + 1;
        } else if (midVal > key) {
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
   * this ArrayChar with the elements from specified ArrayChar v.
   * If the length of arrayChar is greater than count of remaining
   * elements in this ArrayChar an appropriate exception is thrown.<p>
   *
   * @param  index the starting position for replacement.
   * @param  arrayChar the source ArrayChar which contains the elements for replacement.
   */
  public void setAll(int index, ArrayChar arrayChar) {
    setAll(index, arrayChar, 0, arrayChar.size());
  }

  /**
   * Replaces range of elements starting at the specified position in
   * this ArrayChar with the elements from specified ArrayChar, arrayChar.
   * If the length field is greater than count of remaining
   * elements in this ArrayChar an appropriate exception is thrown.<p>
   *
   * @param  index the starting position for replacement.
   * @param  arrayChar the source ArrayChar which contains the elements for replacement.
   * @param  offset new elements are taken from this position of arrayChar.
   * @param  length the count of elements which will be replaced.
   */
  public void setAll(int index, ArrayChar arrayChar, int offset, int length) {
    if (index + length > count) {
      throw new ArrayIndexOutOfBoundsException("(index + length)=" + (index + length) + " > size=" + count);
    }

    arrayChar.copyInto(offset, elementData, index, length);
  }

  /**
   * Replaces range of elements starting at the specified position in
   * this ArrayChar with the elements from specified char array.
   * If the length of array is greater than count of remaining
   * elements in this ArrayChar an appropriate exception is thrown.<p>
   *
   * @param  index the starting position for replacement.
   * @param  array the source array which contains the elements for replacement.
   */
  public void setAll(int index, char[] array) {
    setAll(index, array, 0, array.length);
  }

  /**
   * Replaces range of elements starting at the specified position in
   * this ArrayChar with the elements from specified char array.
   * If the length field is greater than count of remaining
   * elements in this ArrayChar an appropriate exception is thrown.
   *
   * @param  index the starting position for replacement.
   * @param  array the source array which contains the elements for replacement.
   * @param  offset  new elements are taken from this position of array.
   * @param  length  the count of elements which will be replaced.
   */
  public void setAll(int index, char[] array, int offset, int length) {
    if (index + length > count) {
      throw new ArrayIndexOutOfBoundsException("(index + length)=" + (index + length) + " > size=" + count);
    }

    System.arraycopy(array, offset, elementData, index, length);
  }

  /**
   * Adds all elements from specified ArrayChar at the end of this ArrayChar.<p>
   *
   * @param  arrayChar the source ArrayChar which contains the elements for replacement.
   */
  public void addAll(ArrayChar arrayChar) {
    addAll(arrayChar, 0, arrayChar.size());
  }

  /**
   * Adds specified range of elements from ArrayChar v at the end
   * of this ArrayChar.<p>
   *
   * @param  arrayChar the source ArrayChar which contains the elements for replacement.
   * @param  offset  new elements are taken from this position of array.
   * @param  length  the count of elements which will be replaced.
   */
  public void addAll(ArrayChar arrayChar, int offset, int length) {
    ensureCapacity(count + length);
    arrayChar.copyInto(offset, elementData, count, length);
    count += length;
  }

  /**
   * Adds all elements from specified char array at the end of this ArrayChar.<p>
   *
   * @param  array the char array which contains new elements for adding.
   */
  public void addAll(char[] array) {
    addAll(array, 0, array.length);
  }

  /**
   * Adds specified range of elements from char array at the end
   * of this ArrayChar.
   *
   * @param  array the char array which contains new elements for adding.
   * @param  offset  new elements are taken from this position of array.
   * @param  length  the count of elements which will be replaced.
   */
  public void addAll(char[] array, int offset, int length) {
    ensureCapacity(count + length);
    System.arraycopy(array, offset, elementData, count, length);
    count += length;
  }

  /**
   * Adds all elements from specified ArrayChar at the specified position
   * of this ArrayChar.<p>
   *
   * @param  index thethe posittion to start from.
   * @param  arrayChar the source ArrayChar which contains the elements for replacement.
   */
  public void addAll(int index, ArrayChar arrayChar) {
    addAll(index, arrayChar, 0, arrayChar.size());
  }

  /**
   * Adds specified range of elements from ArrayChar arrayChar at the
   * specified position of this ArrayChar.
   *
   * @param  index the starting position for adding.
   * @param  arrayChar the ArrayChar which contains new elements.
   * @param  offset new elements are taken from this position of arrayChar.
   * @param  length the count of elements which will be replaced.
   */
  public void addAll(int index, ArrayChar arrayChar, int offset, int length) {
    if (index > count) {
      throw new ArrayIndexOutOfBoundsException("index=" + index + " > size=" + count);
    }

    ensureCapacity(count + length);
    System.arraycopy(elementData, index, elementData, index + length, count - index);
    arrayChar.copyInto(offset, elementData, index, length);
    count += length;
  }

  /**
   * Adds all elements from specified char array at the specified position
   * of this ArrayChar.<p>
   *
   * @param  index the starting position for adding.
   * @param  array the char array which contains new elements.
   */
  public void addAll(int index, char[] array) {
    addAll(index, array, 0, array.length);
  }

  /**
   * Adds specified range of elements from char array at the specified position
   * of this ArrayChar.<p>
   *
   * @param  index  the starting position for adding.
   * @param  array  the char array which contains new elements.
   * @param  offset new elements are taken from this position of array.
   * @param  length the count of elements which will be replaced.
   */
  public void addAll(int index, char[] array, int offset, int length) {
    if (index > count) {
      throw new ArrayIndexOutOfBoundsException("index=" + index + " > size=" + count);
    }

    ensureCapacity(count + length);
    System.arraycopy(elementData, index, elementData, index + length, count - index);
    System.arraycopy(array, offset, elementData, index, length);
    count += length;
  }

  /**
   * Removes all of the elements from this ArrayChar object.
   * The ArrayChar will be empty after this call returns (unless it throws an exception).<p>
   */
  public void clear() {
    removeAllElements();
  }

  /**
   * Returns a string representation of this ArrayChar, containing the string
   * representation of each of its elements.<p
   *
   * @return  a string representation of this ArrayChar.
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
   * Compares this ArrayChar for equality with the specified object. They are equal
   * if and only if the specified object is also an instance of ArrayChar, they both
   * have the same size, and for each position the elements that they contain at this
   * position are equal.<p>
   *
   * @param  arrayChar the object that this ArrayChar is compared to.
   * @return  true if the object is equal to this ArrayChar, false otherwise.
   */
  public boolean equals(Object arrayChar) {
    if (!(arrayChar instanceof ArrayChar)) {
      return false;
    }

    return ((ArrayChar) arrayChar).equals_(this);
  }

  /**
   * Compares this ArrayChar for equality with the specified object. They are equal
   * if and only if the specified object is also an instance of ArrayChar, they both
   * have the same size, and for each position the elements that they contain at this
   * position are equal.<p>
   *
   * @param  arrayChar ArrayChar that is compared to.
   * @return  true if the object is equal to this ArrayChar, false otherwise.
   */
  protected boolean equals_(ArrayChar arrayChar) {
    if (count != arrayChar.count) {
      return false;
    }

    for (int i = 0; i < count; i++) {
      if (elementData[i] != arrayChar.elementData[i]) {
        return false;
      }
    } 

    return true;
  }

  /**
   * Returns an enumeration of the components of this array. The returned
   * enumeration object will be an instance of the EnumerationChar class.
   * It will generate all the items in this array in the following manner:<p>
   * The first item generated is the item at index 0 in this ArrayChar,
   * then the item at index 1, and so on.<p>
   *
   * @return  an enumeration of the components of this array.
   */
  public EnumerationChar elements() {
    return new EnumerationChar() {

      private int counter = 0;

      public boolean hasMoreElements() {
        return counter < count;
      }

      public char nextElement() {
        return elementData[counter++];
      }

    };
  }

  /**
   * Returns a clone of this array. The copy will contain a reference to a clone of the
   * internal data array, not a reference to the original internal data array of this
   * ArrayChar object.<p>
   *
   * @return  a clone of this array.
   */
  public Object clone() {
    ArrayChar v = (ArrayChar) super.clone();
    v.elementData = new char[count];
    System.arraycopy(elementData, 0, v.elementData, 0, count);
    return v;
  }

  /**
   * Returns an array containing all of the elements in this ArrayChar object
   * in the correct order.<p>
   *
   * @return  array containing the elements in this ArrayChar object.
   */
  public char[] toArray() {
    char[] result = new char[count];
    System.arraycopy(elementData, 0, result, 0, count);
    return result;
  }

  /**
   * Appends the specified element to the end of this ArrayChar.<p>
   *
   * @param  value element to be appended to this ArrayChar
   */
  public void add(char value) {
    ensureCapacity(count + 1);
    elementData[count++] = value;
  }

  /**
   * Removes the first (lowest-indexed) occurrence of the argument from this array. If
   * the char value is found in this array, each component in the array with an index
   * greater or equal to the char's index is shifted downward to have an index one
   * smaller than the value it had previously.<p>
   *
   * @param   value the char value to be removed.
   * @return  true if the argument was a component of this array, false otherwise.
   */
  public boolean removeElement(char value) {
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

  // -----------------------------------------------------------
  // -------------- Some serialization magic -------------------
  // -----------------------------------------------------------
  /**
   * This method is used by Java serializer.<p>
   *
   * @param  stream an output stream to write this object to.
   * @exception  IOException if an IO error occurs.
   */
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    stream.writeInt(elementData.length);

    for (int i = 0; i < count; i++) {
      stream.writeChar(elementData[i]);
    } 
  }

  /**
   * This method is used by Java deserializer.<p
   *
   * @param  stream an input stream to read this object from.
   * @exception  IOException if an IO error occurs.
   * @exception  ClassNotFoundException if the class file of this Class was not found.
   */
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    elementData = new char[stream.readInt()];

    for (int i = 0; i < count; i++) {
      elementData[i] = stream.readChar();
    } 
  }

}

