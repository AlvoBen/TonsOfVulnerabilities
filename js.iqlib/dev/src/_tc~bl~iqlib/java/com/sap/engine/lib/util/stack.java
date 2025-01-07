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
 * Implementation of LIFO structure.<p>
 *
 * WARNING: This class is not synchronized.<p>
 *
 *  If a stack is used in multithreaded environment every method has to be
 *  called in synchronized block.
 *
 * <b>Pooling</b>: see java doc of super class.<p>
 *
 * Example for synchronization:
 * <p><blockquote><pre>
 *
 *   Stack stack = new Stack();
 *
 *   synchronized (stack) {
 *     stack.push("nick");
 *   }
 *
 *   // some stuff
 *
 *   Object temp;
 *   synchronized (stack) {
 *     temp = stack.pop();
 *   }
 * </pre></blockquote><p>
 *
 * @author Nikola Arnaudov
 *
 * @version 1.0
 */
public class Stack extends LinkedList {


  static final long serialVersionUID = 3946878507853474512L;
  
  /**
   * Constructs an empty stack without size limit.<p>
   *
   */
  public Stack() {

  }

  /**
   * Constructs an empty stack with specified limit of elements.<p>
   *
   * @param   limit maximum elements in stack.
   *
   */
  public Stack(int limit) {
    super(limit);
  }

  /**
   * Inserts element into stack.<p>
   *
   * @param   item the object to be inserted.
   * @return  true if operation is successful,
   *          false if there is no space in stack.
   */
  public boolean push(Object item) {
    return addFirst(item) != null;
  }

  /**
   * Deletes the element that was most recently inserted in stack.<p>
   *
   * @return     the deleted element or
   *             null if the stack is empty.
   */
  public Object pop() {
    return removeFirst();
  }

  /**
   * Returns the element that was most recently inserted in stack.<p>
   *
   * @return     this element or
   *             null if the stack is empty.
   */
  public Object top() {
    return getFirst();
  }

}

