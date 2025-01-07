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

package com.sap.engine.lib.util.cache;

/**
 * This class is an implementation of a priority queue.
 * The used algorithm is LRU.
 *
 * @author Iliyan Nenov, ilian.nenov@sap.com
 * @version SAP J2EE Engine 6.30
 */

public class CacheQueueLRU implements CacheQueue {

  /**
   * Default queue minimal size
   */
  protected final static int MIN_SIZE_DEFAULT = 10;
  /**
   * Default queue maximal size
   */
  protected final static int MAX_SIZE_DEFAULT = 100;
  /**
   * Maximal size of the queue
   */
  protected int maxSize;
  /**
   * Minimal size of the queue
   */
  protected int minSize;
  /**
   * Current size of the queue
   */
  protected int size;
  /**
   * First node of the queue
   */
  protected CacheQueueItem first;
  /**
   * Last node of the queue
   */
  protected CacheQueueItem last;

  /**
   * Constructor
   */
  public CacheQueueLRU() {
    this(MIN_SIZE_DEFAULT, MAX_SIZE_DEFAULT);
  }

  /**
   * Constructor
   *
   * @param   maxSize Maximal queue size
   */
  public CacheQueueLRU(int maxSize) {
    this(MIN_SIZE_DEFAULT, maxSize);
  }

  /**
   * Constructor
   *
   * @param   minSize Minimal queue size
   * @param   maxSize Maximal queue size
   */
  public CacheQueueLRU(int minSize, int maxSize) {
    this.minSize = minSize;
    this.maxSize = maxSize;
    size = 0;
    first = null;
    last = null;
  }

  /**
   * Update the access frequency of an item and move it up if necessary
   *
   * @param node The node to be updated
   */
  public void update(CacheQueueItem node) {
    if (node != first) {
      if (last == node) {
        first = last;
        last = last.prev;
      } else {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.next = first;
        node.prev = last;
        first.prev = node;
        last.next = node;
        first = node;
      }
    }
  }

  /**
   * Add a new item into the queue
   * Replace last if Queue Size exceeded
   *
   * @param node The node to be added
   */
  public CacheQueueItem add(CacheQueueItem node) {
    CacheQueueItem removed = null;

    if (size == 0) {
      size = 1;
      node.prev = node;
      node.next = node;
      first = node;
      last = node;
    } else {
      if (size < maxSize) {
        size++;
        node.next = first;
        node.prev = last;
        last.next = node;
        first.prev = node;
        first = node;
      } else {
        //adding before first
        node.next = first;
        node.prev = last.prev;
        last.prev.next = node;
        first.prev = node;
        first = node;
        //and deleting last
        removed = last;
        last = first.prev;
        removed.next = null;  // cut removed from the  LRU queue
        removed.prev = null;  // cut removed from the  LRU queue
      }
    }
    return removed;
  }

  /**
   *
   * @return whether this queue is full or not
   */
  public boolean isFull() {
    return (size >= maxSize);
  }

  /**
   * Removes an item in the queue
   *
   * @param node The node to be removed
   */
  public CacheQueueItem remove(CacheQueueItem node) {
    CacheQueueItem snode = node;
    if (size > 1) {
      size--;
      if (first == node) {
        first = node.next;
        last.next = first;
        first.prev = last;
//        node.prev.next = node.next;
//        node.next.prev = node.prev;
      } else if (last == node) {
        last = node.prev;
        first.prev = last;
        last.next = first;
//        node.prev.next = node.next;
//        node.next.prev = node.prev;
      } else {
        node.prev.next = node.next;
        node.next.prev = node.prev;
      }
      node.next = null;
      node.prev = null;
    } else if (size == 1) {
      size--;
      first = null;
      last = null;
    }
    return snode;
  }

  /**
   * Removes the last item of the queue
   */
  public CacheQueueItem removeLast() {
    return remove(last);
  }

  /**
   * Prints out to the screen the contents of the queue in first->last order
   */
  public void print() {
    CacheQueueItem temp;
//    System.out.print("QUEUE ");
//    System.out.print("(size:" + size + ")");
//    int i = 1;
//    System.out.println("0 element = " + first);
    if (first != null) {
      temp = first.next;
//      System.out.println("1 element = " + temp);
      while (temp != last) {
//        i++;
//        System.out.println(i + "element = " + temp);
        temp = temp.next;
      }
    } else {
//      System.out.println("Queue empty.");
    }
  }

  /**
   * Get max Size of queue
   *
   * return maxSize
   */
  public int getMaxSize() {
    return maxSize;
  }

  /**
   * Get min Size of queue
   *
   * return minSize
   */
  public int getMinSize() {
    return minSize;
  }

  /**
   * Get size of queue
   *
   * return size
   */
  public int getSize() {
    return size;
  }

  /**
   * Empties this queue
   */
  public void clear() {
    size = 0;
    first = null;
    last = null;
  }

}