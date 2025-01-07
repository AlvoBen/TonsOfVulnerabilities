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
 * Implementation of Double Ended Queue (Deque).<p>
 *
 * WARNING: This class is not synchronized.<p>
 * If deque is used in multithreaded environment every method has to be
 * called in synchronized block.<p>
 *
 * <b>Pooling</b>: see java doc of super class.<p>
 *
 * Example for synchronization:
 * <p><blockquote><pre>
 *
 *   Deque deque = new Deque();
 *
 *   synchronized (deque) {
 *     deque.pushLeft("nick");
 *   }
 *
 *   // some stuff
 *
 *   Object temp;
 *   synchronized (deque) {
 *     temp = deque.popRight();
 *   }
 * </pre></blockquote><p>
 *
 * @author Nikola Arnaudov
 *
 * @version 1.0
 */
public class Deque extends DoublyLinkedList {

  static final long serialVersionUID = -483082801248884002L;
  /**
   * Constructs an empty deque without size limit.<p>
   *
   */
  public Deque() {

  }

  /**
   * Constructs an empty deque with specified limit of elements.<p>
   *
   * @param   limit maximum elements in queue.
   *
   */
  public Deque(int limit) {
    super(limit);
  }

  /**
   * Inserts element into left side of deque.<p>
   *
   * @param   item the object to be inserted.
   * @return  true if operation is successful,
   *          false if there is no space in the deque.
   */
  public boolean pushLeft(Object item) {
    return addFirst(item) != null;
  }

  /**
   * Deletes the element from left side of deque.<p>
   *
   * @return     the deleted element or
   *             null if the deque is empty.
   */
  public Object popLeft() {
    return removeFirst();
  }

  /**
   * Returns the element which is on the left side in deque.<p>
   *
   * @return     the element or
   *             null if the deque is empty.
   */
  public Object topLeft() {
    return getFirst();
  }

  /**
   * Inserts element into right side of deque.<p>
   *
   * @param   item the object to be inserted.
   * @return  true if operation is successful,
   *          false if there is no space in the deque.
   */
  public boolean pushRight(Object item) {
    return addLast(item) != null;
  }

  /**
   * Deletes the element from right side of deque.<p>
   *
   * @return     the deleted element or
   *             null if the deque is empty.
   */
  public Object popRight() {
    return removeLast();
  }

  /**
   * Returns the element which is on the right side in deque.<p>
   *
   * @return     the element or
   *             null if the deque is empty.
   */
  public Object topRight() {
    return getLast();
  }

}

