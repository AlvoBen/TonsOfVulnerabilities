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
 * Implementation of LIFO structure.<p>
 *
 * WARNING: This class is not synchronized.<p>
 * If stack is used in multithreaded environment every method has to be
 * called in synchronized block.<p>
 *
 * Example for synchronization:
 * <p><blockquote><pre>
 *
 *   BaseStack stack = new BaseStack();
 *   NextItem item = new NextItemAdapter();
 *
 *   synchronized (stack) {
 *     stack.push(item);
 *   }
 *
 *   // some stuff
 *
 *   NextItem temp;
 *   synchronized (stack) {
 *     temp = stack.pop();
 *   }
 * </pre></blockquote><p>
 *
 * @author Nikola Arnaudov
 *
 * @version 1.0
 */
public class BaseStack extends BaseLinkedList {
  
  static final long serialVersionUID = -7385363867128668362L;

  /**
   * Constructs an empty stack without size limit.<p>
   *
   */
  public BaseStack() {

  }

  /**
   * Constructs an empty stack with specified limit of elements.<p>
   *
   * @param   limit maximum elements in stack.
   *
   */
  public BaseStack(int limit) {
    super(limit);
  }

  /**
   * Inserts element into stack.<p>
   *
   * @param   item the item to be inserted.
   * @return  true if operation is successful,
   *          false if there is no space in stack.
   */
  public boolean push(NextItem item) {
    return addFirstItem(item);
  }

  /**
   * Deletes the element that was most recently inserted in stack.<p>
   *
   * @return     the deleted element or
   *             null if the stack is empty.
   */
  public NextItem pop() {
    return removeFirstItem();
  }

  /**
   * Returns the element that was most recently inserted in stack.<p>
   *
   * @return     this element or
   *             null if the stack is empty.
   */
  public NextItem top() {
    return getFirstItem();
  }

}

