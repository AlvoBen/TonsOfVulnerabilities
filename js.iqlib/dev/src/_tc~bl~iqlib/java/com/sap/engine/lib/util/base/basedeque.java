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
 * Implementation of Double Ended Queue (BaseDeque).<p>
 *
 * WARNING: This class is not synchronized.<p>
 * If deque is used in multithreaded environment every method has to be
 * called in synchronized block.<p>
 *
 * Example for synchronization:
 * <p><blockquote><pre>
 *
 *   BaseDeque deque = new BaseDeque();
 *   LinearItem item = new LinearItemAdapter();
 *
 *   synchronized (deque) {
 *     deque.pushLeft(item);
 *   }
 *
 *   // some stuff
 *
 *   LinearItem temp;
 *   synchronized (deque) {
 *     temp = deque.popRight();
 *   }
 * </pre></blockquote><p>
 *
 * @author Nikola Arnaudov
 *
 * @version 1.0
 */
public class BaseDeque extends BaseDoublyLinkedList {
  static final long serialVersionUID = -6268876739810172618L;
  /**
   * Constructs an empty deque without size limit.<p>
   */
  public BaseDeque() {

  }

  /**
   * Constructs an empty deque with the specified limit of elements.<p>
   *
   * @param   limit maximum elements in deque.
   */
  public BaseDeque(int limit) {
    super(limit);
  }

  /**
   * Inserts an element on the left side of the deque.<p>
   *
   * @param   item the object to be inserted.
   * @return  true if operation is successful,
   *          false if there is no space in the deque.
   */
  public boolean pushLeft(LinearItem item) {
    return addFirstItem(item);
  }

  /**
   * Deletes the element from left side of deque.<p>
   *
   * @return     the deleted element or
   *             null if the deque is empty.
   */
  public LinearItem popLeft() {
    return removeFirstItem();
  }

  /**
   * Returns the element which is on the left side in deque.<p>
   *
   * @return     the element or
   *             null if the deque is empty.
   */
  public LinearItem topLeft() {
    return getFirstItem();
  }

  /**
   * Inserts element into right side of deque.<p>
   *
   * @param   item the object to be inserted.
   * @return  true if operation is successful,
   *          false if there is no space in the deque.
   */
  public boolean pushRight(LinearItem item) {
    return addLastItem(item);
  }

  /**
   * Deletes the element from right side of deque.<p>
   *
   * @return     the deleted element or
   *             null if the deque is empty.
   */
  public LinearItem popRight() {
    return removeLastItem();
  }

  /**
   * Returns the element which is on the right side in deque.<p>
   *
   * @return     the element or
   *             null if the deque is empty.
   */
  public LinearItem topRight() {
    return getLastItem();
  }

}

