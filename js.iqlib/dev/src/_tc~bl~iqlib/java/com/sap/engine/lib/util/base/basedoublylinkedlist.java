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
 * Doubly linked List of LinearItems.<p>
 *
 * WARNING: This class is not synchronized.<p>
 * If list is used in multithreaded environment every method has to be
 * called in synchronized block.<p>
 *
 * <b>Note</b>: The fastest way to traverse the list is by Iterator.<p>
 *
 * <b>Pooling</b>: Generally list assigns null value to pointers of deleted elements,
 *          exceptions are methods, for which it is explicitly specified.<p>
 *
 * Example for synchronization:
 * <p><blockquote><pre>
 *
 *   BaseDoublyLinkedList list = new BaseDoublyLinkedList();
 *   LinearItem item = new LinearItemAdapter();
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
 * @author Nikola Arnaudov, Andrei Gatev
 *
 * @version 1.0
 */
public class BaseDoublyLinkedList extends AbstractDataStructure {
  static final long serialVersionUID = 3640819823862105131L;
  /**
   * The first element in the list.<p>
   */
  protected LinearItem first = null;
  /**
   * The last element in the list.<p>
   */
  protected LinearItem last = null;
  /**
   * The upper limit of list size.<p>
   */
  protected int limit;

  /**
   * Constructor of the class. Sets limit to 'no limit'.<p>
   */
  public BaseDoublyLinkedList() {
    this(0);
  }

  /**
   * Constructor of the class.<p>
   *
   * @param   limit the limit of list size. If limit is less than 0 then
   *            there is no size limit.
   */
  public BaseDoublyLinkedList(int limit) {
    setLimit(limit);
  }

  /**
   * Adds an element at the head of the list.<p>
   *
   * @param   item the element to be added.
   * @return   true if inserting is successful, false if there is no space in list.
   */
  public boolean addFirstItem(LinearItem item) {
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
  protected void addFirst_(LinearItem item) {
    item.setPrev(null);
    item.setNext(first);

    if (count == 0) {
      last = item;
    } else {
      first.setPrev(item);
    }

    first = item;
    count++;
  }

  /**
   * Adds an element at the tail of the list.<p>
   *
   * @param   item the element to be added.
   * @return  true if inserting is successful, false if there is no space in list.
   */
  public boolean addItem(LinearItem item) {
    return addLastItem(item);
  }

  /**
   * Adds an element at the tail of the list.<p>
   *
   * @param   item the element to be added.
   * @return  true if inserting is successful, false if there is no space in list.
   */
  public boolean addLastItem(LinearItem item) {
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
  protected void addLast_(LinearItem item) {
    item.setPrev(last);
    item.setNext(null);

    if (count == 0) {
      first = item;
    } else {
      last.setNext(item);
    }

    last = item;
    count++;
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
  public boolean addItem(int index, LinearItem item) {
    if ((index > count) || (index < 0) || (count >= limit)) {
      return false;
    }

    if (index == 0) {
      addFirst_(item);
      return true;
    }

    if (index == count) {
      addLast_(item);
      return true;
    }

    LinearItem prev = getItem_(index - 1);
    LinearItem next = (LinearItem) prev.getNext();
    item.setPrev(prev);
    item.setNext(next);
    prev.setNext(item);
    next.setPrev(item);
    count++;
    return true;
  }

  /**
   * Removes an element on the specified position.<p>
   *
   * @param   index the position of element to be deleted.
   * @return  the deleted element.
   */
  public LinearItem removeItem(int index) {
    if (index == 0) {
      if (count == 0) {
        return null;
      }

      LinearItem result = first;
      first = (LinearItem) first.getNext();
      count--;

      if (count == 0) {
        last = null;
      } else {
        first.setPrev(null);
      }

      result.clearItem();
      return result;
    }

    if (index == count - 1) {
      if (count == 0) {
        return null;
      }

      LinearItem result = last;
      last = (LinearItem) last.getPrev();
      count--;
      last.setNext(null);
      result.clearItem();
      return result;
    }

    LinearItem item = getItem_(index);

    if (item == null) {
      return null;
    }

    LinearItem prev = (LinearItem) item.getPrev();
    LinearItem next = (LinearItem) item.getNext();
    prev.setNext(next);
    next.setPrev(prev);
    count--;
    item.clearItem();
    return item;
  }

  /**
   * Removes the first occurence of specified item in the list (i.e. the first
   * element that compared by the equals() method returns true).
   *
   * @param   item the element to be deleted.
   * @return  the removed element or null if the element is not in list.
   */
  public LinearItem removeItem(LinearItem item) {
    if (count == 0) {
      return null;
    }

    NextItem temp = first;

    for (int i = 0; i < count; i++) {
      if (temp.equals(item)) {
        LinearItem result = (LinearItem) temp;
        LinearItem prev = (LinearItem) result.getPrev();
        LinearItem next = (LinearItem) result.getNext();

        if (prev == null) {
          first = next;
        } else {
          prev.setNext(next);
        }

        if (next == null) {
          last = prev;
        } else {
          next.setPrev(prev);
        }

        result.clearItem();
        count--;
        return result;
      } else {
        temp = temp.getNext();
      }
    } 

    return null;
  }

  /**
   * Removes all the elements, equal to the specified one (comparison is made
   * by the equals() method).<p>
   *
   * <b>Note</b>: This method do not clears pointers of removed elements.<p>
   *
   * @param   item the element to be deleted.
   * @return   the count of deleted elements.
   */
  public int removeAllItems(LinearItem item) {
    int deleted = 0;
    LinearItem prev = null;
    LinearItem start = null;

    for (NextItem temp = first; temp != null; temp = temp.getNext()) {
      if (temp.equals(item)) {
        deleted++;

        if (start == null) {
          start = (LinearItem) temp;
        }
      } else if (start != null) {
        prev = (LinearItem) start.getPrev();

        if (prev == null) {
          first = (LinearItem) temp;
          first.setPrev(null);
        } else {
          prev.setNext(temp);
          ((LinearItem) temp).setPrev(prev);
        }

        start = null;
      }
    } 

    if (start != null) {
      last = (LinearItem) start.getPrev();

      if (last != null) {
        last.setNext(null);
      } else {
        first = null;
      }
    }

    count -= deleted;
    return deleted;
  }

  /**
   * Removes the first element of the list.
   *
   * @return  the element that has been removed; null if the list is empty
   */
  public LinearItem removeFirstItem() {
    if (count == 0) {
      return null;
    }

    LinearItem result = first;
    first = (LinearItem) first.getNext();
    count--;

    if (count == 0) {
      last = null;
    } else {
      first.setPrev(null);
    }

    result.clearItem();
    return result;
  }

  /**
   * Removes the first element of the list.<p>
   *
   * @return  the element that has been removed, null if the list is empty.
   */
  public LinearItem removeLastItem() {
    if (count == 0) {
      return null;
    }

    LinearItem result = last;
    last = (LinearItem) last.getPrev();
    count--;

    if (count == 0) {
      first = null;
    } else {
      last.setNext(null);
    }

    result.clearItem();
    return result;
  }

  /**
   * Replaces the element at the specified position with a given new element.<p>
   *
   * @param   index the position to be changed.
   * @param   item the new element of the list.
   * @return   the old element of the list, that has been replaced.
   */
  public LinearItem setItem(int index, LinearItem item) {
    LinearItem result = getItem_(index);

    if (result == null) {
      return null;
    }

    LinearItem prev = (LinearItem) result.getPrev();
    LinearItem next = (LinearItem) result.getNext();
    item.setPrev(prev);
    item.setNext(next);

    if (index == 0) {
      first = item;
    } else {
      prev.setNext(item);
    }

    if (index == count - 1) {
      last = item;
    } else {
      next.setPrev(item);
    }

    result.clearItem();
    return result;
  }

  /**
   * Empties the list.<p>
   *
   * <b>Note</b>: This method do not clears pointers of removed elements.<p>
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
    BaseDoublyLinkedList result = (BaseDoublyLinkedList) super.clone();

    if (count == 0) {
      return result;
    }

    LinearItem temp = null;
    NextItem index = first;
    LinearItem newFirst = (LinearItem) first.clone();
    LinearItem newItem = newFirst;
    newFirst.setPrev(null);

    for (int i = 1; i < count; i++) {
      index = index.getNext();
      temp = (LinearItem) index.clone();
      newItem.setNext(temp);
      temp.setPrev(newItem);
      newItem = temp;
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
    if (!(obj instanceof BaseDoublyLinkedList)) {
      return false;
    }

    if (count != ((BaseDoublyLinkedList) obj).count) {
      return false;
    }

    if (count == 0) {
      return true;
    }

    NextItem currentTemp = first;
    NextItem checkedTemp = ((BaseDoublyLinkedList) obj).first;

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
    result = 37 * result + ((first == null) ? 0 : first.hashCode()); 
    return result;
  }


  /**
   * Tests whether the list contains specified element.<p>
   *
   * @param   item the element that is searched for.
   * @return  true if the list contains the element, false otherwise.
   */
  public boolean containsItem(LinearItem item) {
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
  public LinearItem getItem(LinearItem item) {
    NextItem temp = first;

    for (int i = 0; i < count; i++) {
      if (temp.equals(item)) {
        return (LinearItem) temp;
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
  public LinearItem getItem(int index) {
    return getItem_(index);
  }

  protected LinearItem getItem_(int index) {
    if ((index >= count) || (index < 0)) {
      return null;
    }

    LinearItem result;

    if (index < (count / 2)) {
      NextItem temp = first;

      for (int i = 0; i < index; i++) {
        temp = temp.getNext();
      } 

      result = (LinearItem) temp;
    } else {
      PrevItem temp = last;

      for (int i = count - 1; i > index; i--) {
        temp = temp.getPrev();
      } 

      result = (LinearItem) temp;
    }

    return result;
  }

  /**
   * Retrieves the first element of the list.<p>
   *
   * @return   the first element of the list, or null if the list is empty.
   */
  public LinearItem getFirstItem() {
    return first;
  }

  /**
   * Retrieves the last element of the list.<p>
   *
   * @return   the last element of the list, or null if the list is empty.
   */
  public LinearItem getLastItem() {
    return last;
  }

  /**
   * Retrieves the position of the first occurence of an element.<p>
   *
   * @param   item the element that is searched for.
   * @return   the position of the first occurence of the element
   *           or -1 if there is no such element.
   */
  public int indexOfItem(LinearItem item) {
    NextItem temp = first;

    for (int i = 0; i < count; i++) {
      if (temp.equals(item)) {
        return i;
      }

      temp = temp.getNext();
    } 

    return -1;
  }

  /**
   * Retireves the position of the last occurence of an element.<p>
   *
   * @param   item the element that is searched for.
   * @return   the position of the last occurence of the element.
   */
  public int lastIndexOfItem(LinearItem item) {
    PrevItem temp = last;

    for (int i = count - 1; i >= 0; i--) {
      if (temp.equals(item)) {
        return i;
      }

      temp = temp.getPrev();
    } 

    return -1;
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
  public LinearItem[] toItemArray() {
    LinearItem[] array = new LinearItem[count];
    LinearItem temp = first;

    for (int i = 0; i < count; i++) {
      array[i] = temp;
      temp = (LinearItem)temp.getNext();
    } 

    return array;
  }

  protected void init(LinearItem first, LinearItem last, int count) {
    this.first = first;
    this.last = last;
    this.count = count;
  }

  /**
   * Returns a sublist of the list from a given index ot end inclusive.
   * If the index is negative, assumes 0. If the index exceeds the count of the
   * elements in the list returns an empty list.<p>
   *
   * <b>Note</b>: the sublist is cloned from list.
   *
   * @param   index the beginning index of the sublist
   * @return   a sublist of the list from the given index inclusive.
   */
  public BaseDoublyLinkedList sublist(int index) {
    return sublist_(index);
  }

  private BaseDoublyLinkedList sublist_(int index) {
    if (index <= 0) {
      return (BaseDoublyLinkedList) clone_();
    }

    BaseDoublyLinkedList result = (BaseDoublyLinkedList) super.clone();

    if (index >= count) {
      result.init(null, null, 0);
      return result;
    }

    NextItem counter = getItem_(index);
    LinearItem temp = null;
    LinearItem newFirst = (LinearItem) counter.clone();
    LinearItem newItem = newFirst;
    newFirst.setPrev(null);

    for (int i = index + 1; i < count; i++) {
      counter = counter.getNext();
      temp = (LinearItem) counter.clone();
      newItem.setNext(temp);
      temp.setPrev(newItem);
      newItem = temp;
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
  public BaseDoublyLinkedList sublist(int lowerBoundary, int upperBoundary) {
    if (upperBoundary >= count) {
      upperBoundary = count - 1;
    }

    if (lowerBoundary < 0) {
      lowerBoundary = 0;
    }

    if (upperBoundary == count - 1) {
      return sublist_(lowerBoundary);
    }

    BaseDoublyLinkedList result = (BaseDoublyLinkedList) super.clone();

    if (lowerBoundary > upperBoundary) {
      result.init(null, null, 0);
      return result;
    }

    NextItem counter = getItem_(lowerBoundary);
    LinearItem temp = null;
    LinearItem newFirst = (LinearItem) counter.clone();
    LinearItem newItem = newFirst;
    newFirst.setPrev(null);

    for (int i = lowerBoundary; i < upperBoundary; i++) {
      counter = counter.getNext();
      temp = (LinearItem) counter.clone();
      newItem.setNext(temp);
      temp.setPrev(newItem);
      newItem = temp;
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
      LinearItem temp = (LinearItem) getItem_(index).getPrev();
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

    int dif = upperBoundary - lowerBoundary + 1;
    int s1 = upperBoundary + 1; // strategy s1 left + mid
    int s2 = count - lowerBoundary; // strategy s2 right + mid
    int s3 = count - dif; // strategy s3 left + right

    if (s1 <= s2 && s1 <= s3) {
      if (lowerBoundary == 0) {
        NextItem right = first;

        for (int i = 0; i < dif; i++, right = right.getNext()) {
          ;
        } 

        ((LinearItem) right).setPrev(null);
        first = (LinearItem) right;
      } else {
        NextItem left = first;

        for (int i = 1; i < lowerBoundary; i++, left = left.getNext()) {
          ;
        } 

        NextItem right = left;

        for (int i = 0; i <= dif; i++, right = right.getNext()) {
          ;
        } 

        left.setNext(right);
        ((LinearItem) right).setPrev((PrevItem) left);
      }
    } else if (s2 <= s1 && s2 <= s3) {
      if (upperBoundary + 1 == count) {
        PrevItem left = last;

        for (int i = 0; i < dif; i++, left = left.getPrev()) {
          ;
        } 

        ((LinearItem) left).setNext(null);
        last = (LinearItem) left;
      } else {
        PrevItem right = last;

        for (int i = count - 2; i > upperBoundary; i--, right = right.getPrev()) {
          ;
        } 

        PrevItem left = right;

        for (int i = 0; i <= dif; i++, left = left.getPrev()) {
          ;
        } 

        ((LinearItem) left).setNext((LinearItem) right);
        right.setPrev(left);
      }
    } else {
      if (lowerBoundary == 0) {
        if (upperBoundary + 1 == count) {
          first = null;
          last = null;
        } else {
          PrevItem right = last;

          for (int i = count - 2; i > upperBoundary; i--, right = right.getPrev()) {
            ;
          } 

          right.setPrev(null);
          first = (LinearItem) right;
        }
      } else {
        if (upperBoundary + 1 == count) {
          NextItem left = first;

          for (int i = 1; i < lowerBoundary; i++, left = left.getNext()) {
            ;
          } 

          left.setNext(null);
          last = (LinearItem) left;
        } else {
          PrevItem right = last;

          for (int i = count - 2; i > upperBoundary; i--, right = right.getPrev()) {
            ;
          } 

          NextItem left = first;

          for (int i = 1; i < lowerBoundary; i++, left = left.getNext()) {
            ;
          } 

          left.setNext((NextItem) right);
          right.setPrev((PrevItem) left);
        }
      }
    }

    count -= dif;
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

    s.append("]\n[");

    for (PrevItem temp = last; temp != null; temp = temp.getPrev()) {
      s.append(temp + ", ");
    } 

    s.append("]");
    return s.toString();
  }

  /**
   * Returns an enumeration of the components of this list.
   * The returned Enumeration object will generate all items in this list.<p>
   *
   * @return   an enumeration of the components of this list.
   */
  public SnapShotEnumeration itemsEnumeration() {
    return new ArrayEnumeration(toItemArray());
  }

  /**
   * Gets the size limit of the list.<p
   *
   * @return   the size limit.
   */
  public int getLimit() {
    return this.limit;
  }

  /**
   * Sets the size limit of the list.
   * If the limit is smaller than current size of the list, the list is not truncated.
   * Just you can not add more elements.<p>
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
   * @param   index reference to a wrapper in list.
   * @param   item a new item.
   * @return  true if inserting is successful, false if there is no space in list.
   */
  public boolean addBefore(LinearItem index, LinearItem item) {
    if (count >= limit) {
      return false;
    }

    LinearItem prev = (LinearItem) index.getPrev();
    item.setPrev(prev);

    if (prev == null) {
      first = item;
    } else {
      prev.setNext(item);
    }

    item.setNext(index);
    index.setPrev(item);
    count++;
    return true;
  }

  /**
   * Inserts element in list after another element which is already in list.<p>
   *
   * WARNING: Index instance itself should be in list and not another instance
   *          that is equal to the index instance.<p>
   *
   * @param   index reference to a wrapper in list.
   * @param   item a new item.
   * @return  true if inserting is successful, false if there is no space in list.
   */
  public boolean addAfter(LinearItem index, LinearItem item) {
    if (count >= limit) {
      return false;
    }

    LinearItem next = (LinearItem) index.getNext();
    item.setNext(next);

    if (next == null) {
      last = item;
    } else {
      next.setPrev(item);
    }

    item.setPrev(index);
    index.setNext(item);
    count++;
    return true;
  }

  /**
   * Removes element from list.<p>
   *
   * WARNING: Item instance itself should be in list and not another instance
   *          that is equal to the item instance.<p>
   *
   * @param   item an item in the list.
   */
  public void removeElement(LinearItem item) {
    LinearItem prev = (LinearItem) item.getPrev();
    LinearItem next = (LinearItem) item.getNext();

    if (prev == null) {
      first = next;
    } else {
      prev.setNext(next);
    }

    if (next == null) {
      last = prev;
    } else {
      next.setPrev(prev);
    }

    item.clearItem();
    count--;
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
  public void removeRange(LinearItem start, LinearItem end) {
    int counter = 1;

    for (NextItem temp = start; temp != end; temp = temp.getNext(), counter++) {
      ;
    } 

    LinearItem prev = (LinearItem) start.getPrev();
    LinearItem next = (LinearItem) end.getNext();

    if (prev == null) {
      first = next;
    } else {
      prev.setNext(next);
    }

    if (next == null) {
      last = prev;
    } else {
      next.setPrev(prev);
    }

    count -= counter;
  }

  /**
   * Replaces element in list with other element which is already in list.<p>
   *
   * WARNING: oldItem instance itself should be in list and not another instance
   *          that is equal to the oldItem instance.<p>
   *
   * @param   oldItem the item in the list to replace.
   * @param   newItem the new item item.
   */
  public void replace(LinearItem oldItem, LinearItem newItem) {
    LinearItem prev = (LinearItem) oldItem.getPrev();
    LinearItem next = (LinearItem) oldItem.getNext();
    newItem.setNext(next);
    newItem.setPrev(prev);

    if (prev == null) {
      first = newItem;
    } else {
      prev.setNext(newItem);
    }

    if (next == null) {
      last = newItem;
    } else {
      next.setPrev(newItem);
    }

    oldItem.clearItem();
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
   * Returns a BidirectionalIterator of the components of this list
   * The returned BidirectionalIterator object will generate all items in this list
   * and can be used to alternate values of the elements.<p>
   *
   * @return   RootIterator of the components of this list.
   */
  public RootIterator elementsIterator() {
    return new BaseDoublyLinkedListIterator();
  }

  // ---------------------------------------------------------
  // ---------------- The Iterator methods -------------------
  // ---------------------------------------------------------
  protected LinearItem iterGet(RootIterator rootIter) {
    BaseDoublyLinkedListIterator iter = (BaseDoublyLinkedListIterator) rootIter;

    if (iter.pointer == iter.end.getNext()) {
      throw new IteratorException("End of Iterator reached!");
    }

    return iter.pointer;
  }

  protected LinearItem iterNext(RootIterator rootIter, int offset) {
    BaseDoublyLinkedListIterator iter = (BaseDoublyLinkedListIterator) rootIter;
    LinearItem endNext = (LinearItem) iter.end.getNext();

    if (offset < 0) {
      throw new IteratorException("Invalid argument: " + offset + " < 0");
    }

    if (iter.pointer == endNext) {
      throw new IteratorException("End of Iterator reached!");
    }

    for (int i = 0; i < offset; i++, iter.pointer = (LinearItem) iter.pointer.getNext()) {
      if (iter.pointer == endNext) {
        throw new IteratorException("Specified position exceeds the end of Iterator!");
      }
    } 

    LinearItem res = iter.pointer;
    iter.pointer = (LinearItem) iter.pointer.getNext();
    return res;
  }

  protected LinearItem iterAdd(Object obj, RootIterator rootIter) {
    BaseDoublyLinkedListIterator iter = (BaseDoublyLinkedListIterator) rootIter;

    if (obj == null) {
      throw new IteratorException("An illegal attempt to add null element!");
    } else if (count >= limit) {
      throw new IteratorException("The limit ot LinkedList exceeded!");
    }

    LinearItem item = (LinearItem) obj;
    item.setPrev(iter.end);

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

  protected LinearItem iterChange(Object obj, RootIterator rootIter) {
    BaseDoublyLinkedListIterator iter = (BaseDoublyLinkedListIterator) rootIter;

    if (obj == null) {
      throw new IteratorException("An illegal attempt to change an element to null!");
    }

    if (iter.pointer == iter.end.getNext()) {
      throw new IteratorException("End of Iterator reached!");
    }

    LinearItem item = (LinearItem) obj;
    LinearItem old = iter.pointer;
    item.setNext(iter.pointer.getNext());
    item.setPrev(iter.pointer.getPrev());
    iter.pointer = item;
    return old;
  }

  protected LinearItem iterRemove(RootIterator rootIter) {
    BaseDoublyLinkedListIterator iter = (BaseDoublyLinkedListIterator) rootIter;

    if (iter.pointer == iter.end.getNext()) {
      throw new IteratorException("End of Iterator reached!");
    }

    if (count == 0) {
      throw new IteratorException("Iterator is empty!");
    }

    LinearItem temp = iter.pointer;
    LinearItem prev = (LinearItem) iter.pointer.getPrev();
    iter.pointer = (LinearItem) iter.pointer.getNext();

    if (iter.pointer != null) {
      iter.pointer.setPrev(prev);
    }

    if (temp == iter.start) {
      if (iter.start == first) {
        first = iter.pointer;
      } else {
        prev.setNext(iter.pointer);
      }

      iter.start = temp == iter.end ? null : iter.pointer;

      if (--count == 0) {
        last = null;
        iter.end = last;
      }
    } else {
      prev.setNext(iter.pointer);
      count--;
    }

    if (iter.end == temp) {
      boolean isLast = (iter.end == last);
      iter.end = prev;

      if (isLast) {
        last = iter.end;
      }
    }

    temp.clearItem();
    return temp;
  }

  protected LinearItem iterInsert(Object obj, RootIterator rootIter) {
    BaseDoublyLinkedListIterator iter = (BaseDoublyLinkedListIterator) rootIter;

    if (obj == null) {
      throw new IteratorException("An illegal attempt to add null element!");
    } else if (count >= limit) {
      throw new IteratorException("The limit ot LinkedList exceeded!");
    }

    LinearItem item = (LinearItem) obj;
    LinearItem prev = (iter.pointer == null ? null : (LinearItem) iter.pointer.getPrev());
    item.setPrev(prev);
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

    if (prev != null) {
      prev.setNext(item);
    }

    iter.pointer = item;
    count++;
    return item;
  }

  protected LinearItem iterPrev(RootIterator rootIter, int offset) {
    BaseDoublyLinkedListIterator iter = (BaseDoublyLinkedListIterator) rootIter;

    if (offset < 0) {
      throw new IteratorException("Invalid argument: " + offset + " < 0");
    }

    if (iter.pointer == iter.start) {
      throw new IteratorException("Beginning of Iterator reached!");
    }

    for (int i = 0; i < offset; i++, iter.pointer = (LinearItem) iter.pointer.getPrev()) {
      if (iter.pointer == iter.start) {
        throw new IteratorException("Specified position is underneath the start of Iterator!");
      }
    } 

    iter.pointer = (LinearItem) iter.pointer.getPrev();
    return iter.pointer;
  }

  protected int iterSize(RootIterator rootIter) {
    BaseDoublyLinkedListIterator iter = (BaseDoublyLinkedListIterator) rootIter;
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
   * An implementation of BidirectionalIterator for the BaseDoublyLinkedList.
   */
  protected class BaseDoublyLinkedListIterator implements BidirectionalIterator { //$JL-CLONE$
    
    static final long serialVersionUID = 3640819823862105131L;

    protected LinearItem start = first;
    protected LinearItem end = last;
    protected LinearItem pointer = start;

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
      return BaseDoublyLinkedList.this;
    }

    public Object next() {
      return iterNext(this, 0);
    }

    public Object next(int n) {
      return iterNext(this, n);
    }

    public void setStartFromIterator(RootIterator iterator) {
      BaseDoublyLinkedListIterator bdlli = (BaseDoublyLinkedListIterator) iterator;

      if (BaseDoublyLinkedList.this != bdlli.getDataStructure()) {
        throw new IteratorException("An attempt to set start from an Iterator over a different DoublyLinkerList instance!");
      }

      LinearItem newPointer = bdlli.pointer;

      if (newPointer == null) {
        throw new IteratorException("Illegal argument: pointer == null");
      }

      start = newPointer;
      pointer = start;
    }

    public void setEndFromIterator(RootIterator iterator) {
      BaseDoublyLinkedListIterator bdlli = (BaseDoublyLinkedListIterator) iterator;

      if (BaseDoublyLinkedList.this != bdlli.getDataStructure()) {
        throw new IteratorException("An attempt to set end from an Iterator over a different DoublyLinkerList instance!");
      }

      LinearItem newPointer = (LinearItem) bdlli.pointer.getPrev();

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

    public Object prev() {
      return iterPrev(this, 0);
    }

    public Object prev(int n) {
      return iterPrev(this, n);
    }

  }

}

