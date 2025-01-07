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

/**
 * Implementation of the FIFO(first-in-first-out) structure.<p>
 *
 * WARNING: This class is not synchronized.<p>
 * If queue is used in multithreaded environment every method has to be
 * called in synchronized block.<p>
 *
 * <b>Pooling</b>: see java doc of super class.<p>
 *
 * Example for synchronization:
 * <p><blockquote><pre>
 *
 *   Queue queue = new Queue();
 *
 *   synchronized (queue) {
 *     queue.enqueue("nick");
 *   }
 *
 *   // some stuff
 *
 *   Object temp;
 *   synchronized (queue) {
 *     temp = queue.dequeue();
 *   }
 * </pre></blockquote><p>
 *
 * @author Nikola Arnaudov
 *
 * @version 1.0
 */
public class Queue extends LinkedList {

  static final long serialVersionUID = -5091721197580724670L;
  
  /**
   * Constructs an empty queue without size limit.<p>
   *
   */
  public Queue() {

  }

  /**
   * Constructs an empty queue with specified limit of elements.<p>
   *
   * @param   limit maximum elements in queue.
   */
  public Queue(int limit) {
    super(limit);
  }

  /**
   * Returns the element that has longest resided in queue.<p>
   *
   * @return     the element or
   *             null if the queue is empty.
   */
  public Object head() {
    return getFirst();
  }

  /**
   * Deletes the element that has longest resided in queue.<p>
   *
   * @return    the deleted element or
   *            null if the queue is empty.
   */
  public Object dequeue() {
    return removeFirst();
  }

  /**
   * Inserts element into queue.<p>
   *
   * @param   item the object to be inserted.
   * @return  true if operation is successful,
   *          false if there is no space in the queue.
   */
  public boolean enqueue(Object item) {
    return addLast(item) != null;
  }

}

