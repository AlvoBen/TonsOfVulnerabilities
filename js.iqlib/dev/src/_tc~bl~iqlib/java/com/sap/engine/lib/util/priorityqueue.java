/*
 * Copyright (c) 2003 by SAP AG.,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP AG..
 */
package com.sap.engine.lib.util;

import java.io.IOException;
import java.io.Serializable;

/**
 * This class is a representation of the Priority Queue data structure.
 * This structure is similar to the standard FIFO Queue , except that
 * each of its elements has its specific priority and the next element
 * to be pulled out of the queue is the one with the highest priority.
 *
 * @author George Manev, Krasimir Semerdzhiev
 * @version 6.30
 */
public class PriorityQueue implements Serializable {
  
  static final long serialVersionUID = 4527609128312155841L;
  
  private transient long[] priorities;
  private transient Object[] items;
  private int itemsCounter;
  private int MAX_SIZE;
  private byte trigger;

  /**
   * Creates a new empty PriorityQueue using the defaults.
   * This means that the Queue will have an initial capacity for 10 items
   * and will be able to contain up to 2,147,483,647 items total.
   */
  public PriorityQueue() {
    this(-1, 2147483647);
  }

  /**
   * Creates a new empty PriorityQueue having the specified initial capacity.
   * The Queue will be able to grow for up to 2,147,483,647 items total.
   * If the capacity argument is negative than the default of 10 will be used.
   *
   * @param capacity  Initial capacity for the Queue.
   */
  public PriorityQueue(int capacity) {
    this(capacity, 2147483647);
  }

  /**
   * Creates a new empty PriorityQueue having the specified initial capacity.
   * The Queue will be able to grow for up to maxSize items or 2,147,483,647
   * in case the maxSize is negative. If the capacity argument is negative
   * than the less of 10 and maxSize will be used. An IllegalArgumentException
   * is thrown if capacity is greater than maxSize or maxSize is equal to zero.
   *
   * @param maxSize  The maximum number of items that this queue can contain.
   * @param capacity  Initial capacity for the Queue.
   * @exception IllegalArgumentException  If capacity is greater than maxSize
   *                                      or maxSize is 0.
   */
  public PriorityQueue(int capacity, int maxSize) throws IllegalArgumentException {
    if (maxSize < 0) {
      MAX_SIZE = 2147483647;
    } else {
      MAX_SIZE = maxSize;
    }
    if ((MAX_SIZE < capacity) || (MAX_SIZE == 0)) {
      throw new IllegalArgumentException();
    }
    if (capacity < 0) {
      if (MAX_SIZE < 10) {
        capacity = MAX_SIZE;
      } else {
        capacity = 10;
      }
    }
    trigger = 0;
    itemsCounter = 0;
    priorities = new long[capacity];
    items = new Object[capacity];
  }

  /**
   * Returns a reference to this element in the queue that will be executed
   * next i.e. the one with the currently highest priority, or null if the queue
   * is empty.
   *
   * @return  a reference to the element with the highest priority.
   */
  public Object inspect() {
    if (isEmpty()) {
      return null;
    }
    return items[0];
  }

  /**
   * Used to determine if the queue is currently empty.
   *
   * @return  true if the queue is empty and false otherwise.
   */
  public boolean isEmpty() {
    return itemsCounter == 0;
  }

  /**
   * Used to determine if the queue is currently full, in the meaning
   * that the queueu has already reached its maximum allowed size.
   *
   * @return  true if the queue is full and false otherwise.
   */
  public boolean isFull() {
    return itemsCounter == MAX_SIZE;
  }

  /**
   * Returns the next element waiting for execution and removes it from the Queue.
   *
   * @return  The element with the highest priority in the queue.
   * @exception IllegalStateException  If there aren't any items in the Queue.
   */
  public synchronized Object serve() throws IllegalStateException {
    if (isEmpty()) {
      throw new IllegalStateException("The Queue is empty.");
    }
    Object theHead = items[0];
    itemsCounter--;
    int hole = 0;
    int child = 2;
    while (child < itemsCounter) {
      if (priorities[child] < priorities[--child]) {
        child++;
      }
      if (priorities[itemsCounter] > priorities[child]) {
        items[hole] = items[child];
        priorities[hole] = priorities[child];
        hole = child++;
        child *= 2;
      } else {
        break;
      }
    }
    if (child == itemsCounter) {
      if (priorities[itemsCounter] > priorities[--child]) {
        items[hole] = items[child];
        priorities[hole] = priorities[child];
        hole = child;
      }
    }
    items[hole] = items[itemsCounter];
    priorities[hole] = priorities[itemsCounter];
    items[itemsCounter] = null;
    trigger++;
    trigger %= 5;
    if (trigger == 0) {
      try {
        setLength(itemsCounter);
      } catch (IOException ioe) {
        // Resize failed. Memory is wasted, but data is OK. $JL-EXC$
      }
    }
    return theHead;
  }

  /**
   * Adds a new element in the queue, according ot its priority. If the queue
   * has already reached its maximum allowed size an IllegalStateException is thrown.
   * Else if the queue has reached its current capacity and for some reason can NOT
   * be enlarged then an IOException is thrown.In this case the queue DO RETAINS its
   * consistency and usability.
   *
   * @param  value   The new element to be enqueued.
   * @param  priority   The priority of this element.
   * @exception  IOException   If the enqueue process of the new element has FAILED.
   * @exception  IllegalStateException   If the Queue has reached its maximum allowed size.
   */
  public synchronized boolean enqueue(long priority, Object value) throws IllegalStateException, IOException {
    if (isFull()) {
      throw new IllegalStateException("The Queue is full.");
    }
    setLength(itemsCounter + 1);
    int hole = itemsCounter++;
    int parent = itemsCounter / 2;
    while (parent > 0) {
      if (priority < priorities[--parent]) {
        items[hole] = items[parent];
        priorities[hole] = priorities[parent];
        hole = parent++;
        parent /= 2;
      } else {
        break;
      }
    }
    items[hole] = value;
    priorities[hole] = priority;
    return hole == 0;
  }

  /**
   * Resizes the Queue as appropriate.
   */
  private synchronized void setLength(int newLength) throws IOException {
    try {
      if (newLength < (priorities.length / 2)) {
        long[] newPriorities = new long[newLength];
        Object[] newItems = new Object[newLength];
        System.arraycopy(priorities, 0, newPriorities, 0, newLength);
        System.arraycopy(items, 0, newItems, 0, newLength);
        priorities = newPriorities;
        items = newItems;
      }
      if (newLength > priorities.length) {
        int newCapacity = priorities.length;
        newCapacity *= 1.75;
        if (newLength > newCapacity) {
          newCapacity = newLength;
        }
        if (newCapacity > MAX_SIZE) {
          newCapacity = MAX_SIZE;
        }
        long[] newPriorities = new long[newCapacity];
        Object[] newItems = new Object[newCapacity];
        System.arraycopy(priorities, 0, newPriorities, 0, priorities.length);
        System.arraycopy(items, 0, newItems, 0, priorities.length);
        priorities = newPriorities;
        items = newItems;
      }
    } catch (Exception e) {
      throw new IOException(e.getMessage());
    }
  }

  /**
   * Serializes this PriorityQueue instance.
   */
  private synchronized void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
    s.defaultWriteObject();
    // Write out queue length
    s.writeInt(priorities.length);
    // Write out all elements in the proper order.
    for (int i=0; i<itemsCounter; i++) {
      s.writeLong(priorities[i]);
      s.writeObject(items[i]);
    }
  }

  /**
   * Deserializes this PriorityQueue instance.
   */
  private synchronized void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
    s.defaultReadObject();
    // Read in queue length and allocate space
    int arrayLength = s.readInt();
    items = new Object[arrayLength];
    priorities = new long[arrayLength];
    // Read in all elements in the proper order.
    for (int i=0; i<itemsCounter; i++) {
      priorities[i] = s.readLong();
      items[i] = s.readObject();
    }
  }

}
