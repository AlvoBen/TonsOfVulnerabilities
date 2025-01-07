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
package com.sap.engine.lib.util;

import com.sap.engine.lib.util.base.*;
import com.sap.engine.lib.util.iterators.ArrayEnumeration;
import com.sap.engine.lib.util.iterators.IteratorException;
import com.sap.engine.lib.util.iterators.RootIterator;
import com.sap.engine.lib.util.iterators.SnapShotEnumeration;

/**
 * Doubly linked List for any kind of objects.<p>
 *
 * WARNING: This class is not synchronized.<p>
 * If list is used in multithreaded environment every method has to be
 * called in synchronized block.<p>
 *
 * <b>Note</b>: The fastest way to traverse the list is by Enumeration.
 *
 * <b>Pooling</b>: This class can be connected to PoolObject easyly.
 *          Then all wrapper are taken from pool in put methods,
 *          end put back in pool on remove methods.
 *          Be careful when using methods of parent class BaseDoublyLinkedList.
 *          They do not use pool, for example clear(), do not put wrappers
 *          back to pool, but clearValues() do.<p>
 *
 * Example for pooling:<p>
 * <p><blockquote><pre>
 *    DoublyLinkedList list = new DoublyLinkedList();
 *    PoolObject pool = new PoolObjectWithCreator(list, 20);
 *    list.setPool(pool);
 * </pre></blockquote><p>
 *
 * Example for synchronization:
 * <p><blockquote><pre>
 *   DoublyLinkedList list = new DoublyLinkedList();
 *
 *   synchronized (list) {
 *     list.addFirstValue("nick");
 *   }
 *
 *   // some stuff
 *
 *   int index;
 *   synchronized (list) {
 *     index = list.indexOfValue("nick");
 *   }
 * </pre></blockquote><p>
 *
 * @author Nikola Arnaudov, Andrei Gatev
 *
 * @version 1.0
 */
public class DoublyLinkedList extends BaseDoublyLinkedList implements PoolInstanceCreator {

  static final long serialVersionUID = 9124261124979920956L;
  /**
   *  Used from removeValue method.<p>
   */
  protected LinearItemPointer tmp;
  /**
   *  Pool for faster memory allocation.<p>
   */
  protected transient PoolObject pool;

  /**
   * Constructor of the class. Sets limit to 'no limit'.<p>
   *
   */
  public DoublyLinkedList() {
    this(0);
  }

  /**
   * Constructor of the class.<p>
   *
   * @param   limit the limit of list size. If limit is less than 0 then
   *            there is no size limit.
   */
  public DoublyLinkedList(int limit) {
    super(limit);
    tmp = getLinearItemPointer(null);
  }

  /**
   * Sets an object pool for the list for faster memory allocation.
   * if pool == null then pooling is thurned off.<p>
   *
   * @param   pool the object pool.
   */
  public void setPool(PoolObject pool) {
    this.pool = pool;
  }

  /**
   * Gets pool linked to this list.<p>
   *
   * @return   the pool or null if there is no pool.
   */
  public PoolObject getPool() {
    return pool;
  }

  /**
   * Implementation of interface PoolInstanceCreator.<p>
   *
   * @return    a new instance of a wrapper of this list.
   */
  public Object newInstance() {
    return new LinearItemPointer();
  }

  /**
   * Gets wrapper from pool or create new one if there is no pool.<p>
   *
   * @param   value wrapped value.
   * @return  a new wrapper of the structure.
   */
  protected LinearItemPointer getLinearItemPointer(Object value) {
    if (pool == null) {
      return new LinearItemPointer(value);
    } else {
      LinearItemPointer wrapper = (LinearItemPointer)pool.getObject();
      wrapper.value = value;
      return wrapper;
    }
  }

  /**
   * Put back wrapper in pool if there is pool.<p>
   *
   * @param   wrapper the wrapper for release.
   */
  protected void releaseLinearItemPointer(LinearItemPointer wrapper) {
    if (pool != null) {
      wrapper.value = null;
      pool.releaseObject(wrapper);
    }
  }

  /**
   * Adds an element at the head of the list.<p>
   *
   * @param   value the element to be added.
   * @return   a reference to a wrapper if inserting is successful, null
   *           if there is no space in the list.
   */
  public LinearItemPointer addFirst(Object value) {
    if (value == null) {
      throw new NullPointerException();
    }

    if (count >= limit) {
      return null;
    }

    LinearItem result = getLinearItemPointer(value);
    addFirst_(result);
    return (LinearItemPointer)result;
  }

  /**
   * Adds an element at the tail of the list.<p>
   *
   * @param   value the element to be added.
   * @return   a reference to a wrapper if inserting is successful, null
   *           if there is no space in the list.
   */
  public LinearItemPointer add(Object value) {
    return addLast(value);
  }

  /**
   * Adds an element at the tail of the list.<p>
   *
   * @param   value the element to be added.
   * @return   a reference to a wrapper if inserting is successful, null
   *           if there is no space in the list.
   */
  public LinearItemPointer addLast(Object value) {
    if (value == null) {
      throw new NullPointerException();
    }

    if (count >= limit) {
      return null;
    }

    LinearItemPointer result = getLinearItemPointer(value);
    addLast_(result);
    return result;
  }

  /**
   * Adds an element at the specified position in the list. If the
   * position index is greater than the current count of the elements or the
   * current count is equal to the limit, does nothing.<p>
   *
   * @param   index the position, where the new element must be inserted.
   * @param   value the new element.
   * @return   a reference to the wrapper if the list has changed (i.e. the new element was successfully inserted),
   *           null otherwise.
   */
  public LinearItemPointer add(int index, Object value) {
    if (value == null) {
      throw new NullPointerException();
    }

    LinearItemPointer result = getLinearItemPointer(value);

    if (addItem(index, result)) {
      return result;
    }

    releaseLinearItemPointer(result);
    return null;
  }

  /**
   * Removes element on specified position.<p>
   *
   * @param   index the position of the element to be deleted.
   * @return   the deleted element.
   */
  public Object remove(int index) {
    LinearItemPointer wrapper = (LinearItemPointer)removeItem(index);

    if (wrapper == null) {
      return null;
    }

    Object result = wrapper.value;
    releaseLinearItemPointer(wrapper);
    return result;
  }

  /**
   * Removes the first occurence of specified value in the list (i.e. the first
   * element that, compared by the equals()  method returns true).<p>
   *
   * @param   value the element to be deleted.
   * @return   the removed element or null if the element is not in list.
   */
  public Object remove(Object value) {
    tmp.value = value;
    LinearItemPointer wrapper = (LinearItemPointer)removeItem(tmp);
    tmp.value = null;

    if (wrapper == null) {
      return null;
    }

    Object result = wrapper.value;
    releaseLinearItemPointer(wrapper);
    return result;
  }

  /**
   * Removes all the elements, equal to the specified one (comparison is made
   * by the equals() method).<p>
   *
   * @param   value the element to be deleted.
   * @return  the count of deleted elements.
   */
  public int removeAll(Object value) {
    int deleted = 0;
    LinearItem prev = null;
    LinearItem start = null;

    for(NextItem temp = first; temp != null; temp = temp.getNext()) {
      if (((LinearItemPointer)temp).value.equals(value)) {
        deleted++;

        if (start == null) {
          start = (LinearItem)temp;
        }
      } else if (start != null) {
        prev = (LinearItem)start.getPrev();

        if (prev == null) {
          first = (LinearItem)temp;
          first.setPrev(null);
        } else {
          prev.setNext(temp);
          ((LinearItem)temp).setPrev(prev);
        }

        for(NextItem eraser = start; eraser != temp;) {
          NextItem next = eraser.getNext();
          eraser.clearItem();
          releaseLinearItemPointer((LinearItemPointer)eraser);
          eraser = next;
        }

        start = null;
      }
    }

    if (start != null) {
      last = (LinearItem)start.getPrev();

      if (last != null) {
        last.setNext(null);
      } else {
        first = null;
      }

      for(NextItem eraser = start; eraser != null;) {
        NextItem next = eraser.getNext();
        eraser.clearItem();
        releaseLinearItemPointer((LinearItemPointer)eraser);
        eraser = next;
      }
    }

    count -= deleted;
    return deleted;
  }

  /**
   * Removes the first element of the list.<p>
   *
   * @return  the element that has been removed and null if the list is empty.
   */
  public Object removeFirst() {
    LinearItemPointer wrapper = (LinearItemPointer)removeFirstItem();

    if (wrapper == null) {
      return null;
    } else {
      Object result = wrapper.value;
      releaseLinearItemPointer(wrapper);
      return result;
    }
  }

  /**
   * Removes the last element of the list.<p>
   *
   * @return  the element that has been removed and null if the list is empty.
   */
  public Object removeLast() {
    LinearItemPointer wrapper = (LinearItemPointer)removeLastItem();

    if (wrapper == null) {
      return null;
    } else {
      Object result = wrapper.value;
      releaseLinearItemPointer(wrapper);
      return result;
    }
  }

  /**
   * Replaces the element at a specified position with a given new element.<p>
   *
   * @param   index the position of the element to be changed.
   * @param   value the new element of the list.
   * @return  the old element of the list, that has been replaced.
   */
  public Object set(int index, Object value) {
    if (value == null) {
      throw new NullPointerException();
    }

    LinearItemPointer wrapper = (LinearItemPointer)getItem_(index);

    if (wrapper == null) {
      return null;
    }

    Object result = wrapper.value;
    wrapper.value = value;
    return result;
  }

  /**
   * Empties the list.<p>
   */
  public void clear() {
    if (pool == null) {
      super.clear();
      return;
    }

    clear_();
  }

  /**
   * Empties the list.<p>
   *
   * Puts all wrappers in the pool.
   * It is slower than the clear() method, because clear() does not use the pool.
   *
   */
  private void clear_() {
    for(NextItem eraser = first; eraser != null;) {
      NextItem next = eraser.getNext();
      eraser.clearItem();
      ((LinearItemPointer)eraser).value = null;
      pool.releaseObject(eraser);
      eraser = next;
    }

    super.clear();
  }

  /**
   * Creates shallow copy of the list.
   * Pool is not cloned. New list has not pool.<p>
   *
   * @return a clone of the list.
   */
  public Object clone() {
    if (pool == null) {
      return super.clone();
    }

    return clone_();
  }

  private Object clone_() {
    DoublyLinkedList result = (DoublyLinkedList)getNewInstance();

    if (count == 0) {
      return result;
    }

    LinearItem temp = null;
    NextItem index = first;
    LinearItemPointer newFirst = (LinearItemPointer)pool.getObject();
    newFirst.value = ((LinearItemPointer)first).value;
    LinearItem newItem = newFirst;
    newFirst.setPrev(null);

    for(int i = 1; i < count; i++) {
      index = index.getNext();
      temp = (LinearItem)pool.getObject();
      ((LinearItemPointer)temp).value = ((LinearItemPointer)index).value;
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
   * Tests whether the list contains specified element.<p>
   *
   * @param   value the element that is searched for.
   * @return  true if the list contains the element, false otherwise.
   */
  public boolean contains(Object value) {
    NextItem temp = first;

    for(int i = 0; i < count; i++) {
      if (((LinearItemPointer)temp).value.equals(value)) {
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
   * @param   value the element that is searched for.
   * @return  null if the list does not contains the element.
   */
  public Object get(Object value) {
    NextItem temp = first;

    for(int i = 0; i < count; i++) {
      if (((LinearItemPointer)temp).value.equals(value)) {
        return ((LinearItemPointer)temp).value;
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
  public Object get(int index) {
    LinearItemPointer result = (LinearItemPointer)getItem(index);
    return result == null ? null : result.value;
  }

  /**
   * Retrieves the first element of the list.<p>
   *
   * @return   the first element of the list, or null if the list is empty.
   */
  public Object getFirst() {
    return first == null ? null : ((LinearItemPointer)first).value;
  }

  /**
   * Retrieves the last element of the list.<p>
   *
   * @return   the last element of the list, or null if the list is empty.
   */
  public Object getLast() {
    return last == null ? null : ((LinearItemPointer)last).value;
  }

  /**
   * Retrieves the position of the first occurence of an element.<p>
   *
   * @param   value the element that is searched for.
   * @return   the position of the first occurence of the element
   *           or -1 if there is no such element.
   */
  public int indexOf(Object value) {
    NextItem temp = first;

    for(int i = 0; i < count; i++) {
      if (((LinearItemPointer)temp).value.equals(value)) {
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
   * @param   value the element that is searched for.
   * @return   the position of the last occurence of the element.
   */
  public int lastIndexOf(Object value) {
    PrevItem temp = last;

    for(int i = count - 1; i >= 0; i--) {
      if (((LinearItemPointer)temp).value.equals(value)) {
        return i;
      }

      temp = temp.getPrev();
    }

    return -1;
  }

  /**
   * Returns an array, containing the elements of the list in the
   * order they appear in the list.<p>
   *
   * @return   an array, containing the elements of the list.
   */
  public Object[] toArray() {
    Object[] array = new Object[count];
    NextItem temp = first;

    for(int i = 0; i < count; i++) {
      array[i] = ((LinearItemPointer)temp).value;
      temp = temp.getNext();
    }

    return array;
  }

  /**
   * Returns a sublist of the list from a given index ot end inclusive.
   * If the index parameter is negative, assumes 0. If the index exceeds the count of the
   * elements in the list returns an empty list.<p>
   *
   * <b>Note</b>: The sublist is shallow copy of list.
   *       The sublist has not pool.<p>
   *
   * @param   index the beginning index of the sublist
   * @return   a sublist of the list from the given index inclusive.
   */
  public BaseDoublyLinkedList sublist(int index) {
    if (pool == null) {
      return super.sublist(index);
    }

    return sublist_(index);
  }

  private BaseDoublyLinkedList sublist_(int index) {
    if (index <= 0) {
      return (DoublyLinkedList)clone_();
    }

    DoublyLinkedList result = (DoublyLinkedList)getNewInstance();

    if (index >= count) {
      result.init(null, null, 0);
      return result;
    }

    NextItem counter = getItem_(index);
    LinearItem temp = null;
    LinearItemPointer newFirst = (LinearItemPointer)pool.getObject();
    newFirst.value = ((LinearItemPointer)counter).value;
    LinearItem newItem = newFirst;
    newFirst.setPrev(null);

    for(int i = index + 1; i < count; i++) {
      counter = counter.getNext();
      temp = (LinearItemPointer)pool.getObject();
      ((LinearItemPointer)temp).value = ((LinearItemPointer)counter).value;
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
   * <b>Note</b: The sublist is shallow copy of list.
   *       The sublist has not pool.<p>
   *
   * @param   lowerBoundary the position in the list, which is lower boundary for the sublist.
   * @param   upperBoundary  position in the list, which is upper boundary for the sublist.
   * @return  a sublist of the list from the given lower boundary to the given upper boundary.
   */
  public BaseDoublyLinkedList sublist(int lowerBoundary, int upperBoundary) {
    if (pool == null) {
      return super.sublist(lowerBoundary, upperBoundary);
    }

    if (upperBoundary >= count) {
      upperBoundary = count - 1;
    }

    if (lowerBoundary < 0) {
      lowerBoundary = 0;
    }

    if (upperBoundary == count - 1) {
      return sublist_(lowerBoundary);
    }

    DoublyLinkedList result = (DoublyLinkedList)getNewInstance();

    if (lowerBoundary > upperBoundary) {
      return new DoublyLinkedList(limit);
    }

    NextItem counter = getItem_(lowerBoundary);
    LinearItem temp = null;
    LinearItemPointer newFirst = (LinearItemPointer)pool.getObject();
    newFirst.value = ((LinearItemPointer)counter).value;
    LinearItem newItem = newFirst;
    newFirst.setPrev(null);

    for(int i = lowerBoundary; i < upperBoundary; i++) {
      counter = counter.getNext();
      temp = (LinearItemPointer)pool.getObject();
      ((LinearItemPointer)temp).value = ((LinearItemPointer)counter).value;
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
   * @param   index the position, from which on all elements are deleted.
   * @return  true if the list has changed (i.e. at least one element was deleted), false otherwise
   */
  public boolean removeSublist(int index) {
    if (pool == null) {
      return super.removeSublist(index);
    }

    if ((index >= count) || (count == 0)) {
      return false;
    }

    if (index <= 0) {
      clear_();
    } else {
      LinearItem temp = getItem_(index);
      LinearItem prev = (LinearItem)temp.getPrev();

      while (temp != null) {
        NextItem next = temp.getNext();
        temp.clearItem();
        ((LinearItemPointer)temp).value = null;
        pool.releaseObject(temp);
        temp = (LinearItem)next;
      }

      prev.setNext(null);
      last = prev;
      count = index;
    }

    return true;
  }

  /**
   * Removes all the elements from a given position to another inclusive.
   * If the lower boundary is negative, assumes 0. If the upper boundary is greater
   * than the position of the last element of the list, assumes the last position in the list.<p>
   *
   * @param   lowerBoundary the position in the list, which is lower boundary for the sublist.
   * @param   upperBoundary  position in the list, which is upper boundary for the sublist.
   * @return  true if the list has changed (i.e. at least one element was deleted), false otherwise
   */
  public boolean removeRange(int lowerBoundary, int upperBoundary) {
    if (pool == null) {
      return super.removeRange(lowerBoundary, upperBoundary);
    }

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
      NextItem next = null;

      for(int i = 0; i <= upperBoundary; i++, temp = next) {
        next = temp.getNext();
        temp.clearItem();
        ((LinearItemPointer)temp).value = null;
        pool.releaseObject(temp);
      }

      first = (LinearItem)next;

      if (first == null) {
        last = null;
      } else {
        first.setPrev(null);
      }
    } else {
      NextItem temp = getItem_(lowerBoundary);
      NextItem old = (NextItem)((PrevItem)temp).getPrev();
      NextItem next = null;

      for(int i = 0; i <= dif; i++, temp = next) {
        next = temp.getNext();
        temp.clearItem();
        ((LinearItemPointer)temp).value = null;
        pool.releaseObject(temp);
      }

      old.setNext(temp);

      if (temp == null) {
        last = (LinearItem)old;
      } else {
        ((LinearItem)temp).setPrev((LinearItem)old);
      }
    }

    count -= (dif + 1);
    return true;
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

  // -----------------------------------------------------------------------
  // -- Methods working with wrapper references which are already in list --
  // -----------------------------------------------------------------------
  /**
   * Inserts element in list before another element which is already in list.<p>
   *
   * @param   index reference to a wrapper in list.
   * @param   value a new item.
   * @return  reference to a wrapper if inserting is successful, null
   *             if there is no space in list
   */
  public LinearItemPointer addBeforePointer(LinearItem index, Object value) {
    if (count >= limit) {
      return null;
    }

    LinearItem item = getLinearItemPointer(value);
    LinearItem prev = (LinearItem)index.getPrev();
    item.setPrev(prev);

    if (prev == null) {
      first = item;
    } else {
      prev.setNext(item);
    }

    item.setNext(index);
    index.setPrev(item);
    count++;
    return (LinearItemPointer)item;
  }

  /**
   * Inserts element in list after another element which is already in list.<p>
   *
   * @param   index reference to a wrapper in list.
   * @param   value a new item.
   * @return  reference to a wrapper if inserting is successful, null
   *             if there is no space in list
   */
  public LinearItemPointer addAfterPointer(LinearItem index, Object value) {
    if (count >= limit) {
      return null;
    }

    LinearItem item = getLinearItemPointer(value);
    LinearItem next = (LinearItem)index.getNext();
    item.setNext(next);

    if (next == null) {
      last = item;
    } else {
      next.setPrev(item);
    }

    item.setPrev(index);
    index.setNext(item);
    count++;
    return (LinearItemPointer)item;
  }

  /**
   * Removes element from list.<p>
   *
   * @param   item a wrapper to an element.
   */
  public void removeElement(LinearItem item) {
    super.removeElement(item);
    releaseLinearItemPointer((LinearItemPointer)item);
  }

  /**
   * Removes elements from list.<p>
   *
   * @param   start start wrapper in list.
   * @param   end end wrapper in list.
   */
  public void removeRange(LinearItem start, LinearItem end) {
    if (pool == null) {
      super.removeRange(start, end);
      return;
    }

    int counter = 0;
    LinearItem prev = (LinearItem)start.getPrev();
    LinearItem next = (LinearItem)end.getNext();

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

    NextItem temp = start;
    end = (LinearItem)end.getNext();

    do {
      NextItem nextItem = temp.getNext();
      counter++;
      temp.clearItem();
      ((LinearItemPointer)temp).value = null;
      pool.releaseObject(temp);
      temp = nextItem;
    } while (temp != end);

    count -= counter;
  }

  public Pointer[] toPointerArray() {
    LinearItemPointer array[] = new LinearItemPointer[count];
    NextItem temp = first;

    for(int i = 0; i < count; i++) {
      array[i] = (LinearItemPointer)temp;
      temp = temp.getNext();
    }

    return array;
  }

  /**
   * Returns a BidirectionalIterator of the components of this list
   * The returned BidirectionalIterator object will generate all items in this list
   * and can be used to alternate values of the elements.<p>
   *
   * @return   RootIterator of the components of this list.
   */
  public RootIterator elementsIterator() {
    return new DoublyLinkedListIterator();
  }

  // ---------------------------------------------------------
  // ---------------- The Iterator methods -------------------
  // ---------------------------------------------------------
  protected Object iterChange(Object obj, LinearItem pointer, LinearItem end) {
    if (obj == null) {
      throw new IteratorException("An illegal attempt to change a value to null!");
    }

    if (pointer == end.getNext()) {
      throw new IteratorException("End of Iterator reached!");
    }

    LinearItemPointer wrapper = (LinearItemPointer)pointer;
    Object tmp = wrapper.value;
    wrapper.value = obj;
    return tmp;
  }

  /**
   * An implementation of BidirectionalIterator for the DoublyLinkedList.
   */
  protected class DoublyLinkedListIterator extends BaseDoublyLinkedListIterator {
    
    static final long serialVersionUID = 8062271341782585952L;

    public Object get() {
      return ((LinearItemPointer)super.get()).value;
    }

    public RootDataStructure getDataStructure() {
      return DoublyLinkedList.this;
    }

    public Object next() {
      return ((LinearItemPointer)super.next()).value;
    }

    public Object next(int n) {
      return ((LinearItemPointer)super.next(n)).value;
    }

    public void setStartFromIterator(RootIterator iterator) {
      DoublyLinkedListIterator dlli = (DoublyLinkedListIterator)iterator;

      if (DoublyLinkedList.this != dlli.getDataStructure()) {
        throw new IteratorException("An attempt to set start from an Iterator over a different DoublyLinkerList instance!");
      }

      LinearItem newPointer = dlli.pointer;

      if (newPointer == null) {
        throw new IteratorException("Illegal argument: pointer == null");
      }

      start = newPointer;
      pointer = start;
    }

    public void setEndFromIterator(RootIterator iterator) {
      DoublyLinkedListIterator dlli = (DoublyLinkedListIterator)iterator;

      if (DoublyLinkedList.this != dlli.getDataStructure()) {
        throw new IteratorException("An attempt to set end from an Iterator over a different DoublyLinkerList instance!");
      }

      LinearItem newPointer = (LinearItem)dlli.pointer.getPrev();

      if (pointer == null) {
        throw new IteratorException("End of Iterator reached!");
      }

      if (newPointer == null) {
        throw new IteratorException("An attempt to set the end from an exhausted Iterator!");
      }

      end = newPointer;
    }

    public Object add(Object obj) {
      return ((LinearItemPointer)super.add(getLinearItemPointer(obj))).value;
    }

    public Object change(Object obj) {
      return iterChange(obj, pointer, end);
    }

    public Object remove() {
      LinearItemPointer tmp = (LinearItemPointer)super.remove();
      Object res = tmp.value;
      releaseLinearItemPointer(tmp);
      return res;
    }

    public Object insert(Object obj) {
      return ((LinearItemPointer)super.insert(obj)).value;
    }

    public Object prev() {
      return ((LinearItemPointer)super.prev()).value;
    }

    public Object prev(int n) {
      return ((LinearItemPointer)super.prev(n)).value;
    }

  }

}

