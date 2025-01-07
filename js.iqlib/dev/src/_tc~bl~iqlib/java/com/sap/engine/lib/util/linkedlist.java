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

import com.sap.engine.lib.util.base.BaseLinkedList;
import com.sap.engine.lib.util.base.NextItem;
import com.sap.engine.lib.util.base.Pointer;
import com.sap.engine.lib.util.iterators.ArrayEnumeration;
import com.sap.engine.lib.util.iterators.IteratorException;
import com.sap.engine.lib.util.iterators.RootIterator;
import com.sap.engine.lib.util.iterators.SnapShotEnumeration;

/**
 * Singly linked list for any kind ot objects.<p>
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
 *          Be careful when using methods of parent class BaseLinkedList.
 *          They do not use pool, for example clear(), do not put wrappers
 *          back to pool, but clearValues() do.<p>
 *
 * Example for pooling:
 * <p><blockquote><pre>
 *    LinkedList list = new LinkedList();
 *    PoolObject pool = new PoolObjectWithCreator(list, 20);
 *    list.setPool(pool);
 * </pre></blockquote><p>
 *
 * Example for synchronization:
 * <p><blockquote><pre>
 *   LinkedList list = new LinkedList();
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
public class LinkedList extends BaseLinkedList implements PoolInstanceCreator {

  static final long serialVersionUID = 6288632919565959502L;
  /**
   *  Pool for faster memory allocation.<p>
   */
  protected transient PoolObject pool;

  /**
   * Constructor of the class. Sets limit to 'no limit'.<p>
   *
   */
  public LinkedList() {
    this(0);
  }

  /**
   * Constructor of the class.<p>
   *
   * @param   limit the limit of list size. If limit is less than 0 then
   *            there is no size limit.
   */
  public LinkedList(int limit) {
    super(limit);
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
    return new NextItemPointer();
  }

  /**
   * Gets wrapper from pool or create new one if there is no pool.<p>
   *
   * @param   value wrapped value.
   * @return  a new wrapper of the structure.
   */
  protected NextItemPointer getNextItemPointer(Object value) {
    if (pool == null) {
      return new NextItemPointer(value);
    } else {
      NextItemPointer wrapper = (NextItemPointer) pool.getObject();
      wrapper.value = value;
      return wrapper;
    }
  }

  /**
   * Put back wrapper in pool if there is pool.<p>
   *
   * @param   wrapper the wrapper for release.
   */
  protected void releaseNextItemPointer(NextItemPointer wrapper) {
    if (pool != null) {
      wrapper.value = null;
      pool.releaseObject(wrapper);
    }
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
  public NextItemPointer add(int index, Object value) {
    if (value == null) {
      throw new NullPointerException();
    }

    if ((count >= limit) || (index > count) || (index < 0)) {
      return null;
    }

    NextItemPointer wrapper = getNextItemPointer(value);
    add_(index, wrapper);
    return wrapper;
  }

  /**
   * Adds an element at the head of the list.<p>
   *
   * @param   value the element to be added.
   * @return   a reference to a wrapper if inserting is successful, null
   *           if there is no space in the list.
   */
  public NextItemPointer addFirst(Object value) {
    if (value == null) {
      throw new NullPointerException();
    }

    if (count >= limit) {
      return null;
    }

    NextItemPointer wrapper = getNextItemPointer(value);
    addFirst_(wrapper);
    return wrapper;
  }

  /**
   * Adds an element at the tail of the list.<p>
   *
   * @param   value the element to be added.
   * @return   a reference to a wrapper if inserting is successful, null
   *           if there is no space in the list.
   */
  public NextItemPointer add(Object value) {
    return addLast(value);
  }

  /**
   * Adds an element at the tail of the list.<p>
   *
   * @param   value the element to be added.
   * @return   a reference to a wrapper if inserting is successful, null
   *           if there is no space in the list.
   */
  public NextItemPointer addLast(Object value) {
    if (value == null) {
      throw new NullPointerException();
    }

    if (count >= limit) {
      return null;
    }

    NextItemPointer wrapper = getNextItemPointer(value);
    addLast_(wrapper);
    return wrapper;
  }

  /**
   * Removes element on specified position.<p>
   *
   * @param   index the position of the element to be deleted.
   * @return   the deleted element.
   */
  public Object remove(int index) {
    NextItemPointer wrapper = (NextItemPointer) removeItem(index);

    if (wrapper == null) {
      return null;
    }

    Object result = wrapper.value;
    releaseNextItemPointer(wrapper);
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
    if (count == 0) {
      return null;
    }

    NextItem old = null;
    NextItem temp = first;

    for (int i = 0; i < count; i++) {
      if (((NextItemPointer) temp).value.equals(value)) {
        if (old != null) {
          old.setNext(temp.getNext());
        } else {
          first = temp.getNext();
        }

        if (temp == last) {
          last = old;
        }

        Object result = ((NextItemPointer) temp).value;
        temp.clearItem();
        count--;
        releaseNextItemPointer((NextItemPointer) temp);
        return result;
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
   * @param   value the element to be deleted.
   * @return  the count of deleted elements.
   */
  public int removeAll(Object value) {
    int deleted = 0;
    NextItem prev = null;
    NextItem next = null;

    for (NextItem temp = first; temp != null; temp = next) {
      next = temp.getNext();

      if (((NextItemPointer) temp).value.equals(value)) {
        deleted++;

        if (prev != null) {
          prev.setNext(next);
        } else {
          first = next;
        }

        temp.clearItem();
        releaseNextItemPointer((NextItemPointer) temp);
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
   * @return  the element that has been removed and null if the list is empty.
   */
  public Object removeFirst() {
    NextItemPointer wrapper = (NextItemPointer) removeFirstItem();

    if (wrapper == null) {
      return null;
    } else {
      Object result = wrapper.value;
      releaseNextItemPointer(wrapper);
      return result;
    }
  }

  /**
   * Removes the last element of the list.<p>
   *
   * @return  the element that has been removed and null if the list is empty.
   */
  public Object removeLast() {
    NextItemPointer wrapper = (NextItemPointer) removeLastItem();

    if (wrapper == null) {
      return null;
    } else {
      Object result = wrapper.value;
      releaseNextItemPointer(wrapper);
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

    NextItemPointer wrapper = (NextItemPointer) getItem(index);

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
    } else {
      clear_();
    }
  }

  /**
   * Empties the list.<p>
   *
   * Puts all wrappers in the pool.
   * It is slower than the clear() method, because clear() does not use the pool.
   *
   */
  private void clear_() {
    for (NextItem eraser = first; eraser != null;) {
      NextItem next = eraser.getNext();
      eraser.clearItem();
      ((NextItemPointer) eraser).value = null;
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
    LinkedList result = (LinkedList) getNewInstance();

    if (count == 0) {
      return result;
    }

    NextItem temp = first;
    NextItemPointer newFirst = (NextItemPointer) pool.getObject();
    newFirst.value = ((NextItemPointer) first).value;
    NextItem newItem = newFirst;

    for (int i = 1; i < count; i++) {
      temp = temp.getNext();
      NextItemPointer next = (NextItemPointer) pool.getObject();
      next.value = ((NextItemPointer) temp).value;
      newItem.setNext(next);
      newItem = next;
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

    for (int i = 0; i < count; i++) {
      if (((NextItemPointer) temp).value.equals(value)) {
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

    for (int i = 0; i < count; i++) {
      if (((NextItemPointer) temp).value.equals(value)) {
        return ((NextItemPointer) temp).value;
      } else {
        temp = temp.getNext();
      }
    } 

    return null;
  }

  /**
   * Retrieves the element at a specified position in the list.<p>
   *
   * @param   index the position whwre to get the element from.
   * @return   the element at position index.
   */
  public Object get(int index) {
    NextItemPointer result = (NextItemPointer) getItem(index);
    return result == null ? null : result.value;
  }

  /**
   * Retrieves the first element of the list.<p>
   *
   * @return   the first element of the list, or null if the list is empty.
   */
  public Object getFirst() {
    return first == null ? null : ((NextItemPointer) first).value;
  }

  /**
   * Retrieves the last element of the list.<p>
   *
   * @return   the last element of the list, or null if the list is empty.
   */
  public Object getLast() {
    return last == null ? null : ((NextItemPointer) last).value;
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

    for (int i = 0; i < count; i++) {
      if (((NextItemPointer) temp).value.equals(value)) {
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
    NextItem temp = first;
    int lastIndex = -1;

    for (int i = 0; i < count; i++) {
      if (((NextItemPointer) temp).value.equals(value)) {
        lastIndex = i;
      }

      temp = temp.getNext();
    } 

    return lastIndex;
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

    for (int i = 0; i < count; i++) {
      array[i] = ((NextItemPointer) temp).value;
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
  public BaseLinkedList sublist(int index) {
    if (pool == null) {
      return super.sublist(index);
    }

    return sublist_(index);
  }

  private BaseLinkedList sublist_(int index) {
    if (index <= 0) {
      return (LinkedList) clone_();
    }

    LinkedList result = (LinkedList) getNewInstance();

    if (index >= count) {
      result.init(null, null, 0);
      return result;
    }

    NextItem temp = first;

    for (int i = 0; i < index; i++) {
      temp = temp.getNext();
    } 

    NextItemPointer newFirst = (NextItemPointer) pool.getObject();
    newFirst.value = ((NextItemPointer) temp).value;
    NextItem newItem = newFirst;

    for (int i = index + 1; i < count; i++) {
      temp = temp.getNext();
      NextItemPointer next = (NextItemPointer) pool.getObject();
      next.value = ((NextItemPointer) temp).value;
      newItem.setNext(next);
      newItem = next;
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
  public BaseLinkedList sublist(int lowerBoundary, int upperBoundary) {
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

    LinkedList result = (LinkedList) getNewInstance();

    if (lowerBoundary > upperBoundary) {
      result.init(null, null, 0);
      return result;
    }

    NextItem temp = first;

    for (int i = 0; i < lowerBoundary; i++) {
      temp = temp.getNext();
    } 

    NextItemPointer newFirst = (NextItemPointer) pool.getObject();
    newFirst.value = ((NextItemPointer) temp).value;
    NextItem newItem = newFirst;

    for (int i = lowerBoundary; i < upperBoundary; i++) {
      temp = temp.getNext();
      newItem.setNext(getNextItemPointer(((NextItemPointer) temp).value));
      newItem = newItem.getNext();
    } 

    newItem.setNext(null);
    result.init(newFirst, newItem, upperBoundary - lowerBoundary + 1);
    return result;
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
      NextItem temp = first;

      for (int i = 1; i < index; i++, temp = temp.getNext()) {
        ;
      } 

      NextItem eraser = temp.getNext();
      temp.setNext(null);
      last = temp;
      count = index;

      while (eraser != null) {
        NextItem next = eraser.getNext();
        eraser.clearItem();
        ((NextItemPointer) eraser).value = null;
        pool.releaseObject(eraser);
        eraser = next;
      }
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

      for (int i = 0; i <= upperBoundary; i++, temp = next) {
        next = temp.getNext();
        temp.clearItem();
        ((NextItemPointer) temp).value = null;
        pool.releaseObject(temp);
      } 

      first = next;

      if (first == null) {
        last = null;
      }
    } else {
      NextItem old = first;

      for (int i = 1; i < lowerBoundary; i++) {
        old = old.getNext();
      } 

      NextItem temp = old.getNext();
      NextItem next = null;

      for (int i = 0; i <= dif; i++, temp = next) {
        next = temp.getNext();
        temp.clearItem();
        ((NextItemPointer) temp).value = null;
        pool.releaseObject(temp);
      } 

      old.setNext(next);

      if (next == null) {
        last = old;
      }
    }

    count -= (dif + 1);
    return true;
  }

  // -----------------------------------------------------------------------
  // -- Methods working with wrapper references which are already in list --
  // -----------------------------------------------------------------------
  /**
   * Inserts element in list after another element which is already in list.<p>
   *
   * @param   index reference to a wrapper in list.
   * @param   value a new item.
   * @return  reference to a wrapper if inserting is successful, null
   *             if there is no space in list
   */
  public NextItemPointer addAfterPointer(NextItem index, Object value) {
    if (count >= limit) {
      return null;
    }

    NextItemPointer item = getNextItemPointer(value);
    NextItem next = index.getNext();
    item.setNext(next);

    if (next == null) {
      last = item;
    }

    index.setNext(item);
    count++;
    return item;
  }

  /**
   * Removes element from list after spicific wrapper.<p>
   *
   * @param   index reference to a wrapper in list.
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
      releaseNextItemPointer((NextItemPointer) target);
      count--;
    }
  }

  /**
   * Removes elements from list.<p>
   *
   * @param   start start wrapper in list.
   * @param   end end wrapper in list.
   */
  public void removeRange(NextItem start, NextItem end) {
    if (pool == null) {
      super.removeRange(start, end);
      return;
    }

    int counter = 1;
    NextItem next = start.getNext();

    for (NextItem temp = next; temp != end; temp = next, counter++) {
      next = temp.getNext();
      temp.clearItem();
      ((NextItemPointer) temp).value = null;
      pool.releaseObject(temp);
    } 

    end = end.getNext();
    start.setNext(end);

    if (end == null) {
      last = start;
    }

    next.clearItem();
    releaseNextItemPointer((NextItemPointer) next);
    count -= counter;
  }

  public Pointer[] toPointerArray() {
    NextItemPointer array[] = new NextItemPointer[count];
    NextItem temp = first;

    for (int i = 0; i < count; i++) {
      array[i] = (NextItemPointer) temp;
      temp = temp.getNext();
    } 

    return array;
  }

  /**
   * Returns a ForwardIterator of the components of this list
   * The returned ForwardIterator object will generate all items in this list
   * and can be used to alternate values of the elements.<p>
   *
   * @return   RootIterator of the components of this list.
   */
  public RootIterator elementsIterator() {
    return new LinkedListIterator();
  }

  // ---------------------------------------------------------
  // ---------------- The Iterator methods -------------------
  // ---------------------------------------------------------
  protected Object iterChange(Object obj, NextItem pointer, NextItem end) {
    if (obj == null) {
      throw new IteratorException("An illegal attempt to change a value to null!");
    }

    if (pointer == end.getNext()) {
      throw new IteratorException("End of Iterator reached!");
    }

    NextItemPointer wrapper = (NextItemPointer) pointer;
    Object tmp = wrapper.value;
    wrapper.value = obj;
    return tmp;
  }

  /**
   * An implementation of ForwardIterator for the LinkedList.<p>
   */
  protected class LinkedListIterator extends BaseLinkedListIterator {
    
    static final long serialVersionUID = -4490437738661410641L;
    public Object get() {
      return ((NextItemPointer) super.get()).value;
    }

    public RootDataStructure getDataStructure() {
      return LinkedList.this;
    }

    public Object next() {
      return ((NextItemPointer) super.next()).value;
    }

    public Object next(int n) {
      return ((NextItemPointer) super.next(n)).value;
    }

    public void setStartFromIterator(RootIterator iterator) {
      LinkedListIterator lli = (LinkedListIterator) iterator;

      if (LinkedList.this != lli.getDataStructure()) {
        throw new IteratorException("An attempt to set start from an Iterator over a different LinkedList instance!");
      }

      NextItem newPointer = lli.pointer;

      if (newPointer == null) {
        throw new IteratorException("Illegal argument: pointer == null");
      }

      start = newPointer;
      pointer = start;
      beforePointer = lli.beforePointer;
    }

    public void setEndFromIterator(RootIterator iterator) {
      LinkedListIterator lli = (LinkedListIterator) iterator;

      if (LinkedList.this != lli.getDataStructure()) {
        throw new IteratorException("An attempt to set end from an Iterator over a different LinkedList instance!");
      }

      NextItem newPointer = lli.beforePointer;

      if (pointer == null) {
        throw new IteratorException("End of Iterator reached!");
      }

      if (newPointer == null) {
        throw new IteratorException("An attempt to set the end from an exhausted Iterator!");
      }

      end = newPointer;
    }

    public Object add(Object obj) {
      NextItemPointer wrapper = getNextItemPointer(obj);
      return ((NextItemPointer) super.add(wrapper)).value;
    }

    public Object change(Object obj) {
      return iterChange(obj, pointer, end);
    }

    public Object remove() {
      NextItemPointer tmp = (NextItemPointer) super.remove();
      Object res = tmp.value;
      releaseNextItemPointer(tmp);
      return res;
    }

    public Object insert(Object obj) {
      NextItemPointer wrapper = getNextItemPointer(obj);
      return ((NextItemPointer) super.insert(wrapper)).value;
    }

  }

}

