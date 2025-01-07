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

/**
 * Implementation of FIFO structure.<p>
 *
 * WARNING: This class is not synchronized.<p>
 * If queue is used in multithreaded environment every method has to be
 * called in synchronized block.<p>
 *
 * Example for synchronization:
 * <p><blockquote><pre>
 *
 *   BaseQueue queue = new BaseQueue();
 *   NextItem item = new NextItemAdapter();
 *
 *   synchronized (queue) {
 *     queue.enqueue(item);
 *   }
 *
 *   // some stuff
 *
 *   NextItem temp;
 *   synchronized (queue) {
 *     temp = queue.dequeue();
 *   }
 * </pre></blockquote><p>
 *
 * @author Nikola Arnaudov
 *
 * @version 1.0
 */
public class BaseQueue extends BaseLinkedList {
  
  static final long serialVersionUID = 1337041486033848798L;
  /**
   * Constructs an empty queue without size limit.<p>
   *
   */
  public BaseQueue() {

  }

  /**
   * Constructs an empty queue with specified limit of elements.<p>
   *
   * @param   limit maximum elements in queue.
   */
  public BaseQueue(int limit) {
    super(limit);
  }

  /**
   * Returns the element that has longest resided in queue.<p>
   *
   * @return     the element or
   *             null if the queue is empty.
   */
  public NextItem head() {
    return getFirstItem();
  }

  /**
   * Deletes the element that has longest resided in queue.<p>
   *
   * @return     the element or
   *             null if the queue is empty.
   */
  public NextItem dequeue() {
    return removeFirstItem();
  }

  /**
   * Inserts element into the queue.<p>
   *
   * @param   item the object to be inserted.
   * @return  true if operation is successful,
   *          false if there is no space in the queue.
   */
  public boolean enqueue(NextItem item) {
    return addLastItem(item);
  }

}

