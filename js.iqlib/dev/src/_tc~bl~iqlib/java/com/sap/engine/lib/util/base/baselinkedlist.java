/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.util.base;

import com.sap.engine.lib.util.AbstractDataStructure;
import com.sap.engine.lib.util.NotSupportedException;
import com.sap.engine.lib.util.RootDataStructure;
import com.sap.engine.lib.util.iterators.*;

/**
 * Singly linked List.<p>
 *
 * WARNING: This class is not synchronized.
 * If list is used in multithreaded environment every method has to be
 * called in synchronized block.<p>
 *
 * <b>Note</b>: The fastest way to traverse the list is by Iterator.
 *
 * <b>Pooling</b>: Generally list assigns null value to pointers of deleted elements,
 *          exceptions are methods, for which it is explicitly specified.<p>
 *
 * Example for synchronization:
 * <p><blockquote><pre>
 *
 *   BaseLinkedList list = new BaseLinkedList();
 *   NextItem item = new NextItemAdapter();
 *
 *   synchronized (list) {
 *     list.addFirst(item);
 *   }
 *
 *   // some stuff
 *
 *   int index;
 *   synchronized (list) {
 *     index = list.indexOf(item);
 *   }
 * </pre></blockquote><p>
 *
 *
 * @author Nikola Arnaudov, Andrei Gatev
 *
 * @version 1.0
 */
public class BaseLinkedList extends AbstractDataStructure {
  static final long serialVersionUID = -6323275689159007036L;
  /**
   * The first element in the list.<p>
   */
  protected NextItem first = null;
  /**
   * The last element in the list.<p>
   */
  protected NextItem last = null;
  /**
   * The upper limit of list size.<p>
   */
  protected int limit;

  /**
   * Constructor of the class. Sets limit to 'no limit'.<p>
   *
   */
  public BaseLinkedList() {
    this(0);
  }

  /**
   * Constructor of the class.<p>
   *
   * @param   limit the limit of list size. If limit is less than 0 then
   *            there is no size limit.
   */
  public BaseLinkedList(int limit) {
    setLimit(limit);
  }

  /**
   * Adds an element at the specified position in the list. If the
   * position index is greater than the current count of the elements or the
   * current count is equal to the limit, does nothing.<p>
   *
   * @param   index the position, where the new element must be inserted.
   * @param   item a new element.
   * @return   true if the list has changed (i.e. the new element was successfully inserted),
   *           false otherwise.
   */
  public boolean addItem(int index, NextItem item) {
    if ((count >= limit) || (index > count) || (index < 0)) {
      return false;
    }

    add_(index, item);
    return true;
  }

  /**
   * Adds an element at the specified position in the list.<p>
   *
   * @param   index the position, where the new element must be inserted.
   * @param   item a new element.
   */
  protected void add_(int index, NextItem item) {
    if (index == 0) {
      item.setNext(first);
      first = item;

      if (last == null) {
        last = first;
      }
    } else if (index == count) {
      last.setNext(item);
      item.setNext(null);
      last = item;
    } else {
      NextItem temp = first;

      for (int i = 1; i < index; i++) {
        temp = temp.getNext();
      } 

      item.setNext(temp.getNext());
      temp.setNext(item);
    }

    count++;
  }

  /**
   * Adds an element at the head of the list.<p>
   *
   * @param   item the element to be added.
   * @return   true if inserting is successful, false if there is no space in list.
   */
  public boolean addFirstItem(NextItem item) {
    if (count >= limit) {
      return false;
    }

    addFirst_(item);
    return true;
  }

  /**
   * Adds an element at the head of the list.<p>
   *
   * @param   item the element to be added.
   */
  protected void addFirst_(NextItem item) {
    item.setNext(first);
    first = item;

    if (last == null) {
      last = first;
    }

    count++;
  }

  /**
   * Adds an element at the tail of the list.<p>
   *
   * @param   item the element to be added.
   * @return   true if inserting is successful, false if there is no space in list.
   */
  public boolean addItem(NextItem item) {
    return addLastItem(item);
  }

  /**
   * Adds an element at the tail of the list.<p>
   *
   * @param   item the element to be added.
   * @return   true if inserting is successful, false if there is no space in list.
   */
  public boolean addLastItem(NextItem item) {
    if (count >= limit) {
      return false;
    }

    addLast_(item);
    return true;
  }

  /**
   * Adds an element at the tail of the list.<p>
   *
   * @param   item the element to be added.
   */
  protected void addLast_(NextItem item) {
    if (count == 0) {
      item.setNext(null);
      first = item;
      last = item;
    } else {
      last.setNext(item);
      item.setNext(null);
      last = item;
    }

    count++;
  }

  /**
   * Removes element on specified position.<p>
   *
   * @param   index the position of element to be deleted.
   * @return  the deleted element.
   */
  public NextItem removeItem(int index) {
    if ((index >= count) || (index < 0)) {
      return null;
    }

    if (index == 0) {
      NextItem temp = first;
      first = first.getNext();
      count--;

      if (count == 0) {
        last = null;
      }

      temp.clearItem();
      return temp;
    }

    NextItem temp = first;

    for (int i = 1; i < index; i++) {
      temp = temp.getNext();
    } 

    NextItem res = temp.getNext();
    temp.setNext(res.getNext());
    count--;

    if (res == last) {
      last = temp;
    }

    res.clearItem();
    return res;
  }

  /**
   * Removes the first occurence of specified item in the list (i.e. the first
   * element that compared by the equals() method returns true).
   *
   * @param   item the element to be deleted.
   * @return  the removed element or null if the element is not in list.
   */
  public NextItem removeItem(NextItem item) {
    if (count == 0) {
      return null;
    }

    NextItem old = null;
    NextItem temp = first;

    for (int i = 0; i < count; i++) {
      if (temp.equals(item)) {
        if (old != null) {
          old.setNext(temp.getNext());
        } else {
          first = temp.getNext();
        }

        if (temp == last) {
          last = old;
        }

        temp.clearItem();
        count--;
        return temp;
      } else {
        old = temp;
        temp = temp.getNext();
      }
    } 

    return null;
  }

  /**
   * Removes all the elements, equal to the specified one (comparison is made
   * by the equals() method).<p>
   *
   * @param   item the element to be deleted.
   * @return   the count of deleted elements.
   */
  public int removeAllItems(NextItem item) {
    int deleted = 0;
    NextItem prev = null;
    NextItem next = null;

    for (NextItem temp = first; temp != null; temp = next) {
      next = temp.getNext();

      if (temp.equals(item)) {
        deleted++;

        if (prev != null) {
          prev.setNext(next);
        } else {
          first = next;
        }

        temp.clearItem();
      } else {
        prev = temp;
      }
    } 

    last = prev;
    count -= deleted;
    return deleted;
  }

  /**
   * Removes the first element of the list.<p>
   *
   * @return  the element that has been removed, null if the list is empty.
   */
  public NextItem removeFirstItem() {
    if (count == 0) {
      return null;
    }

    NextItem temp = first;
    first = first.getNext();
    count--;

    if (count == 0) {
      last = null;
    }

    temp.clearItem();
    return temp;
  }

  /**
   * Removes the last element of the list.<p>
   *
   * @return  the element that has been removed, null if the list is empty.
   */
  public NextItem removeLastItem() {
    if (count == 0) {
      return null;
    }

    NextItem tmp = first;

    if (count == 1) {
      first = null;
      last = null;
      tmp.clearItem();
      count--;
      return tmp;
    }

    for (int i = 2; i < count; i++) {
      tmp = tmp.getNext();
    } 

    last = tmp;
    tmp = tmp.getNext();
    last.setNext(null);
    tmp.clearItem();
    count--;
    return tmp;
  }

  /**
   * Replaces the element at a specified position with a given new element.<p>
   *
   * @param   index the position to be changed.
   * @param   item the new element of the list.
   * @return   the old element of the list, that has been replaced.
   */
  public NextItem setItem(int index, NextItem item) {
    if ((index >= count) || (index < 0)) {
      return null;
    }

    NextItem result;

    if (index == 0) {
      item.setNext(first.getNext());
      result = first;
      first = item;
    } else {
      NextItem temp = first;

      for (int i = 1; i < index; i++) {
        temp = temp.getNext();
      } 

      result = temp.getNext();
      item.setNext(result.getNext());
      temp.setNext(item);
    }

    if (last == result) {
      last = item;
    }

    result.clearItem();
    return result;
  }

  /**
   * Empties the list.<p>
   *
   * <b>Note</b>: This method do not clears pointers of removed elements.<p>
   *
   */
  public void clear() {
    first = null;
    last = null;
    count = 0;
  }

  /**
   * Clones the list. All elements are cloned.<p>
   *
   * @return a clone of the list.
   */
  public Object clone() {
    return clone_();
  }

  private Object clone_() {
    BaseLinkedList result = (BaseLinkedList) super.clone();

    if (count == 0) {
      return result;
    }

    NextItem temp = first;
    NextItem newFirst = (NextItem) first.clone();
    NextItem newItem = newFirst;

    for (int i = 1; i < count; i++) {
      temp = temp.getNext();
      newItem.setNext((NextItem) temp.clone());
      newItem = newItem.getNext();
    } 

    newItem.setNext(null);
    result.first = newFirst;
    result.last = newItem;
    return result;
  }

  /**
   * Compares a list for equality with another object.
   * Lists are equal if and only if they have the same size
   * and for each position the elements at this position in each list
   * are equal.<p>
   *
   * @param   obj the object that the current list is compared to.
   * @return true if the list is equal to the object, false otherwise.
   */
  public boolean equals(Object obj) {
    if (!(obj instanceof BaseLinkedList)) {
      return false;
    }

    if (count != ((BaseLinkedList) obj).count) {
      return false;
    }

    if (count == 0) {
      return true;
    }

    NextItem currentTemp = first;
    NextItem checkedTemp = ((BaseLinkedList) obj).first;

    for (int i = 0; i < count; i++) {
      if (currentTemp.equals(checkedTemp)) {
        currentTemp = currentTemp.getNext();
        checkedTemp = checkedTemp.getNext();
      } else {
        return false;
      }
    } 

    return true;
  }
  
  public int hashCode() {
    int result = 17;
    result = 37 * result + count;
    result = 37 * result + ((first == null)? 0 : first.hashCode());
    return result;
  }

  /**
   * Tests whether the list contains specified element.<p>
   *
   * @param   item the element that is searched for.
   * @return  true if the list contains the element, false otherwise.
   */
  public boolean containsItem(NextItem item) {
    NextItem temp = first;

    for (int i = 0; i < count; i++) {
      if (temp.equals(item)) {
        return true;
      } else {
        temp = temp.getNext();
      }
    } 

    return false;
  }

  /**
   * Retrieves the first occurence of specified item in the list (i.e. the first
   * element that, compared by the equals() method, returns true).<p>
   *
   * @param   item the element that is searched for.
   * @return  null if the list does not contains the element.
   */
  public NextItem getItem(NextItem item) {
    NextItem temp = first;

    for (int i = 0; i < count; i++) {
      if (temp.equals(item)) {
        return temp;
      } else {
        temp = temp.getNext();
      }
    } 

    return null;
  }

  /**
   * Retrieves the element at a specified position in the list.<p>
   *
   * @param   index the position where to get the element from.
   * @return   the element at position index.
   */
  public NextItem getItem(int index) {
    if ((index >= count) || (index < 0)) {
      return null;
    }

    NextItem temp = first;

    for (int i = 0; i < index; i++) {
      temp = temp.getNext();
    } 

    return temp;
  }

  /**
   * Retrieves the first element of the list.<p>
   *
   * @return   the first element of the list.
   */
  public NextItem getFirstItem() {
    return first;
  }

  /**
   * Retrieves the last element of the list.<p>
   *
   * @return   the last element of the list.
   */
  public NextItem getLastItem() {
    return last;
  }

  /**
   * Retrieves the position of the first occurence of an element.<p>
   *
   * @param   item the element that is searched for.
   * @return   the position of the first occurence of the element
   *           or -1 if there is no such element.
   */
  public int indexOfItem(NextItem item) {
    NextItem temp = first;

    for (int i = 0; i < count; i++) {
      if (temp.equals(item)) {
        return i;
      } else {
        temp = temp.getNext();
      }
    } 

    return -1;
  }

  /**
   * Retireves the position of the last occurence of an element.<p>
   *
   * @param   item the element that is searched for.
   * @return   the position of the last occurence of the element.
   */
  public int lastIndexOfItem(NextItem item) {
    NextItem temp = first;
    int lastIndex = -1;

    for (int i = 0; i < count; i++) {
      if (temp.equals(item)) {
        lastIndex = i;
      }

      temp = temp.getNext();
    } 

    return lastIndex;
  }

  /**
   * Tests whether the list is full (i.e. the limit of element count is reached).<p>
   *
   * @return  true if the list is full, false otherwise.
   */
  public boolean isFull() {
    return (count >= limit);
  }

  /**
   * Returns an array, containing the elements of the list in the
   * order they appear in the list.<p>
   *
   * @return   an array, containing the elements of the list.
   */
  public NextItem[] toItemArray() {
    NextItem[] array = new NextItem[count];
    NextItem temp = first;

    for (int i = 0; i < count; i++) {
      array[i] = temp;
      temp = temp.getNext();
    } 

    return array;
  }

  protected void init(NextItem first, NextItem last, int count) {
    this.first = first;
    this.last = last;
    this.count = count;
  }

  /**
   * Returns a sublist of the list from a given to end index inclusive.
   * If the index is negative, assumes 0. If the index exceeds the count of the
   * elements in the list returns an empty list.<p>
   *
   * <b>Note</b>: the sublist is cloned from list.<p>
   *
   * @param   index the beginning index of the sublist
   * @return   a sublist of the list from the given index inclusive.
   */
  public BaseLinkedList sublist(int index) {
    return sublist_(index);
  }

  private BaseLinkedList sublist_(int index) {
    if (index <= 0) {
      return (BaseLinkedList) clone_();
    }

    BaseLinkedList result = (BaseLinkedList) super.clone();

    if (index >= count) {
      result.init(null, null, 0);
      return result;
    }

    NextItem temp = first;

    for (int i = 0; i < index; i++) {
      temp = temp.getNext();
    } 

    NextItem newFirst = (NextItem) temp.clone();
    NextItem newItem = newFirst;

    for (int i = index + 1; i < count; i++) {
      temp = temp.getNext();
      newItem.setNext((NextItem) temp.clone());
      newItem = newItem.getNext();
    } 

    newItem.setNext(null);
    result.init(newFirst, newItem, count - index);
    return result;
  }

  /**
   * Returns a sublist of the list from a given lower boundary to a given upper boundary
   * inclusive. If the lower boundary is negative, assumes 0. If the upper boundary is greater
   * than the position of the last element of the list, assumes the last position in the list.<p>
   *
   * <b>Note</b>: The sublist is shallow copy of list.
   *       The sublist has not pool.<p>
   *
   * @param   lowerBoundary the position in the list, which is lower boundary for the sublist.
   * @param   upperBoundary  position in the list, which is upper boundary for the sublist.
   * @return  a sublist of the list from the given lower boundary to the given upper boundary.
   */
  public BaseLinkedList sublist(int lowerBoundary, int upperBoundary) {
    if (upperBoundary >= count) {
      upperBoundary = count - 1;
    }

    if (lowerBoundary < 0) {
      lowerBoundary = 0;
    }

    if (upperBoundary == count - 1) {
      return sublist_(lowerBoundary);
    }

    BaseLinkedList result = (BaseLinkedList) super.clone();

    if (lowerBoundary > upperBoundary) {
      result.init(null, null, 0);
      return result;
    }

    NextItem temp = first;

    for (int i = 0; i < lowerBoundary; i++) {
      temp = temp.getNext();
    } 

    NextItem newFirst = (NextItem) temp.clone();
    NextItem newItem = newFirst;

    for (int i = lowerBoundary; i < upperBoundary; i++) {
      temp = temp.getNext();
      newItem.setNext((NextItem) temp.clone());
      newItem = newItem.getNext();
    } 

    newItem.setNext(null);
    result.init(newFirst, newItem, upperBoundary - lowerBoundary + 1);
    return result;
  }

  /**
   * Removes all elements from a given position to the end of the list inclusive.
   * If the given position is negative, assumes 0.<p>
   *
   * <b>Note</b>: This method do not clears pointers of removed elements.<p>
   *
   * @param   index the position, from which on all elements are deleted.
   * @return  true if the list has changed (i.e. at least one element was deleted), false otherwise
   */
  public boolean removeSublist(int index) {
    if ((index >= count) || (count == 0)) {
      return false;
    }

    if (index <= 0) {
      first = null;
      last = null;
      count = 0;
    } else {
      NextItem temp = first;

      for (int i = 1; i < index; i++) {
        temp = temp.getNext();
      } 

      temp.setNext(null);
      last = temp;
      count = index;
    }

    return true;
  }

  /**
   * Removes all the elements from a given position to another inclusive.
   * If the lower boundary is negative, assumes 0. If the upper boundary is greater
   * than the position of the last element of the list, assumes the last position in the list.<p>
   *
   * <b>Note</b>: This method do not clears pointers of removed elements.<p
   *
   * @param   lowerBoundary the position in the list, which is lower boundary for the sublist.
   * @param   upperBoundary  position in the list, which is upper boundary for the sublist.
   * @return  true if the list has changed (i.e. at least one element was deleted), false otherwise
   */
  public boolean removeRange(int lowerBoundary, int upperBoundary) {
    if (upperBoundary >= count) {
      upperBoundary = count - 1;
    }

    if (lowerBoundary < 0) {
      lowerBoundary = 0;
    }

    if ((lowerBoundary > upperBoundary) || (lowerBoundary >= count)) {
      return false;
    }

    int dif = upperBoundary - lowerBoundary;

    if (lowerBoundary == 0) {
      NextItem temp = first;

      for (int i = 0; i < upperBoundary; i++) {
        temp = temp.getNext();
      } 

      first = temp.getNext();

      if (first == null) {
        last = null;
      }
    } else {
      NextItem old = first;

      for (int i = 1; i < lowerBoundary; i++) {
        old = old.getNext();
      } 

      NextItem temp = old.getNext();

      for (int i = 0; i <= dif; i++) {
        temp = temp.getNext();
      } 

      old.setNext(temp);

      if (temp == null) {
        last = old;
      }
    }

    count -= (dif + 1);
    return true;
  }

  /**
   * Returns a string representation of the list.<p>
   *
   * @return  a string representation of the list.
   */
  public String toString() {
    StringBuffer s = new StringBuffer(super.toString());
    s.append(" [Size = " + count + "; First = " + first + "; Last = " + last + "; Limit = " + limit + "]\n[");

    for (NextItem temp = first; temp != null; temp = temp.getNext()) {
      s.append(temp + ", ");
    } 

    s.append("]");
    return s.toString();
  }

  /**
   * Returns enumeration of the components of this list.
   * The returned Enumeration object will generate all items in this list.
   *
   * @return an enumeration of the components of this list.
   */
  public SnapShotEnumeration itemsEnumeration() {
    return new ArrayEnumeration(toItemArray());
  }

  /**
   * Gets the size limit of the list.<p>
   *
   * @return   the size limit.
   */
  public int getLimit() {
    return this.limit;
  }

  /**
   * Sets the size limit of the list.
   * If the limit is smaller than current size of the list, the list is not truncated.
   * Just you can not add more elements.<p
   *
   * @param   limit the new size limit.
   */
  public void setLimit(int limit) {
    this.limit = limit <= 0 ? Integer.MAX_VALUE : limit;
  }

  protected Object getNewInstance() {
    return super.clone();
  }

  // --------------------------------------------------------------
  // -- Methods working with instnaces which are already in list --
  // --------------------------------------------------------------
  /**
   * Inserts element in list before another element which is already in list.<p>
   *
   * WARNING: Index instance itself should be in list and not another instance
   *          that is equal to the index instance.<p>
   *
   * @param   index an item in list.
   * @param   item a new item.
   * @return  true if inserting is successful, false if there is no space in list.
   */
  public boolean addAfter(NextItem index, NextItem item) {
    if (count >= limit) {
      return false;
    }

    NextItem next = index.getNext();
    item.setNext(next);

    if (next == null) {
      last = item;
    }

    index.setNext(item);
    count++;
    return true;
  }

  /**
   * Removes element from list after another element which is already in list.<p>
   *
   * WARNING: Index instance itself should be in list and not another instance
   *          that is equals() to the index instance.<p>
   *
   * @param   index an item in the list.
   */
  public void removeAfter(NextItem index) {
    NextItem target = index.getNext();

    if (target != null) {
      NextItem next = target.getNext();
      index.setNext(next);

      if (next == null) {
        last = index;
      }

      target.clearItem();
      count--;
    }
  }

  /**
   * Removes elements from list.<p>
   *
   * WARNING: Start instance itself should be in list and not another instance
   *          that is equal to the start instance.
   *          End instance itself should be in list and not another instance
   *          that is equal to the end instance.<p>
   *
   * <b>Note</b>: This method do not clears pointers of removed elements.<p>
   *
   * @param   start start index in list.
   * @param   end end index in list.
   */
  public void removeRange(NextItem start, NextItem end) {
    int counter = 0;

    for (NextItem temp = start; temp != end; temp = temp.getNext(), counter++) {
      ;
    } 

    NextItem next = end.getNext();
    start.setNext(next);

    if (next == null) {
      last = start;
    }

    count -= counter;
  }

  /**
   * Replaces element after after other element which is already in list.<p>
   *
   * WARNING: Index instance itself should be in list and not another instance
   *          that is equals() to the index instance.<p>
   *
   * @param   index an item in list.
   * @param   newItem a new item.
   * @return  replaced element, null if there is no such element.
   */
  public NextItem replaceAfter(NextItem index, NextItem newItem) {
    NextItem target = index.getNext();

    if (target != null) {
      NextItem next = target.getNext();

      if (next == null) {
        last = newItem;
      }

      newItem.setNext(next);
      index.setNext(newItem);
      target.clearItem();
    }

    return target;
  }

  /**
   * Returns enumeration of the components of this list.
   * The returned SnapShotEnumeration object will generate
   * all items in this list. Changes to this object will not
   * affect the underlying data structure.<p>
   *
   * @return  an enumeration of the components of this list.
   */
  public SnapShotEnumeration elementsEnumeration() {
    return new ArrayEnumeration(toArray());
  }

  public Pointer[] toPointerArray() {
    throw new NotSupportedException("Use toArray() method instead!");
  }

  public Object deepClone() {
    throw new NotSupportedException("Use clone() method instead!");
  }

  public Object[] toArray() {
    return toItemArray();
  }

  /**
   * Returns a ForwardIterator of the components of this list
   * The returned ForwardIterator object will generate all items in this list
   * and can be used to alternate values of the elements.<p>
   *
   * @return   RootIterator of the components of this list.
   */
  public RootIterator elementsIterator() {
    return new BaseLinkedListIterator();
  }

  // ---------------------------------------------------------
  // ---------------- The Iterator methods -------------------
  // ---------------------------------------------------------
  protected NextItem iterGet(RootIterator rootIter) {
    BaseLinkedListIterator iter = (BaseLinkedListIterator) rootIter;

    if (iter.pointer == iter.end.getNext()) {
      throw new IteratorException("End of Iterator reached!");
    }

    return iter.pointer;
  }

  protected NextItem iterNext(RootIterator rootIter, int offset) {
    BaseLinkedListIterator iter = (BaseLinkedListIterator) rootIter;
    NextItem endNext = iter.end.getNext();

    if (offset < 0) {
      throw new IteratorException("Invalid argument: " + offset + " < 0");
    }

    if (iter.pointer == endNext) {
      throw new IteratorException("End of Iterator reached!");
    }

    for (int i = 0; i < offset; i++, iter.pointer = iter.pointer.getNext()) {
      if (iter.pointer == endNext) {
        throw new IteratorException("Specified position exceeds the end of Iterator!");
      }
    } 

    iter.beforePointer = iter.pointer;
    iter.pointer = iter.pointer.getNext();
    return iter.beforePointer;
  }

  protected NextItem iterAdd(Object obj, RootIterator rootIter) {
    BaseLinkedListIterator iter = (BaseLinkedListIterator) rootIter;

    if (obj == null) {
      throw new IteratorException("An illegal attempt to add null element!");
    } else if (count >= limit) {
      throw new IteratorException("The limit ot LinkedList exceeded!");
    }

    NextItem item = (NextItem) obj;

    if (count == 0) {
      item.setNext(null);
      first = item;
      iter.start = item;
      last = item;
    } else {
      if (iter.end == last) {
        last = item;
      }

      item.setNext(iter.end.getNext());
      iter.end.setNext(item);
    }

    count++;
    iter.end = item;
    return item;
  }

  protected NextItem iterChange(Object obj, RootIterator rootIter) {
    BaseLinkedListIterator iter = (BaseLinkedListIterator) rootIter;

    if (obj == null) {
      throw new IteratorException("An illegal attempt to change an element to null!");
    }

    if (iter.pointer == iter.end.getNext()) {
      throw new IteratorException("End of Iterator reached!");
    }

    NextItem item = (NextItem) obj;
    NextItem old = iter.pointer;
    item.setNext(iter.pointer.getNext());
    iter.pointer = item;
    iter.beforePointer.setNext(iter.pointer);
    return old;
  }

  protected NextItem iterRemove(RootIterator rootIter) {
    BaseLinkedListIterator iter = (BaseLinkedListIterator) rootIter;

    if (iter.pointer == iter.end.getNext()) {
      throw new IteratorException("End of Iterator reached!");
    }

    if (count == 0) {
      throw new IteratorException("Iterator is empty!");
    }

    NextItem temp = iter.pointer;
    iter.pointer = iter.pointer.getNext();

    if (temp == iter.start) {
      if (iter.start == first) {
        first = iter.pointer;
      } else {
        iter.beforePointer.setNext(iter.pointer);
      }

      iter.start = temp == iter.end ? null : iter.pointer;

      if (--count == 0) {
        last = null;
        iter.end = last;
      }
    } else {
      iter.beforePointer.setNext(iter.pointer);
      count--;
    }

    if (iter.end == temp) {
      boolean isLast = (iter.end == last);
      iter.end = iter.beforePointer;

      if (isLast) {
        last = iter.end;
      }
    }

    temp.clearItem();
    return temp;
  }

  protected NextItem iterInsert(Object obj, RootIterator rootIter) {
    BaseLinkedListIterator iter = (BaseLinkedListIterator) rootIter;

    if (obj == null) {
      throw new IteratorException("An illegal attempt to add null element!");
    } else if (count >= limit) {
      throw new IteratorException("The limit ot LinkedList exceeded!");
    }

    NextItem item = (NextItem) obj;
    item.setNext(iter.pointer);

    if (count == 0) {
      first = item;
      iter.start = item;
      last = item;
      iter.end = item;
    } else {
      if (iter.pointer == iter.start) {
        if (iter.start == first) {
          first = item;
        }

        iter.start = item;
      } else if (iter.pointer == iter.end.getNext()) {
        if (iter.end == last) {
          last = item;
        }

        iter.end = item;
      }
    }

    if (iter.beforePointer != null) {
      iter.beforePointer.setNext(item);
    }

    iter.pointer = item;
    count++;
    return item;
  }

  protected int iterSize(RootIterator rootIter) {
    BaseLinkedListIterator iter = (BaseLinkedListIterator) rootIter;
    NextItem endNext = iter.end.getNext();
    int counter = 0;

    if (iter.start == null || iter.start == iter.end) {
      return 0;
    }

    for (NextItem ptr = iter.start; ptr != endNext; ptr = ptr.getNext()) {
      if (ptr == null) {
        throw new IteratorException("Start of Iterator was set after its end!");
      }

      counter++;
    } 

    return counter;
  }

  /**
   * An implementation of ForwardIterator for the BaseLinkedList.<p>
   */
  protected class BaseLinkedListIterator implements ForwardIterator { //$JL-CLONE$
    
    static final long serialVersionUID = -6323275689159007036L;

    protected NextItem start = first;
    protected NextItem end = last;
    protected NextItem pointer = start;
    protected NextItem beforePointer = null;

    public Object get() {
      return iterGet(this);
    }

    public boolean isAtBegin() {
      return start == null ? true : pointer == start;
    }

    public boolean isAtEnd() {
      return end == null ? true : pointer == end.getNext();
    }

    public RootDataStructure getDataStructure() {
      return BaseLinkedList.this;
    }

    public Object next() {
      return iterNext(this, 0);
    }

    public Object next(int n) {
      return iterNext(this, n);
    }

    public void setStartFromIterator(RootIterator iterator) {
      BaseLinkedListIterator blli = (BaseLinkedListIterator) iterator;

      if (BaseLinkedList.this != blli.getDataStructure()) {
        throw new IteratorException("An attempt to set start from an Iterator over a different LinkedList instance!");
      }

      NextItem newPointer = blli.pointer;

      if (newPointer == null) {
        throw new IteratorException("Illegal argument: pointer == null");
      }

      start = newPointer;
      pointer = start;
      beforePointer = blli.beforePointer;
    }

    public void setEndFromIterator(RootIterator iterator) {
      BaseLinkedListIterator blli = (BaseLinkedListIterator) iterator;

      if (BaseLinkedList.this != blli.getDataStructure()) {
        throw new IteratorException("An attempt to set end from an Iterator over a different LinkedList instance!");
      }

      NextItem newPointer = blli.beforePointer;

      if (pointer == null) {
        throw new IteratorException("End of Iterator reached!");
      }

      if (newPointer == null) {
        throw new IteratorException("An attempt to set the end from an exhausted Iterator!");
      }

      end = newPointer;
    }

    public Object add(Object obj) {
      return iterAdd(obj, this);
    }

    public Object change(Object obj) {
      return iterChange(obj, this);
    }

    public Object remove() {
      return iterRemove(this);
    }

    public Object insert(Object obj) {
      return iterInsert(obj, this);
    }

    public int size() {
      return iterSize(this);
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

  }

}

