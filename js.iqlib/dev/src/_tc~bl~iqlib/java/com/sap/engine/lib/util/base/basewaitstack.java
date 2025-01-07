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
 * Represents a last-in-first-out (LIFO) structure stack of objects.
 * The push() method blocks the current thread with wait() if the stack is full
 * The pop() and top() methods hang with wait() if the stack is empty.<p>
 *
 * This class is thread safe, unlike its super class Stack, if you want to invoke
 * methods ot super class you must do the synchronization yourself.<p>
 *
 * For example:
 * <p><blockquote><pre>
 *
 *   BaseWaitStack stack = new BaseWaitStack();
 *   NextItem item = new NextItemAdapter();
 *
 *   stack.push(item); // thread safe method
 *   int index;
 *   synchronized (stack) {
 *     // here you can call all methods of LinkedList class
 *     index = stack.indexOf(item);
 *   }
 * </pre></blockquote><p>
 *
 * @author Andrei Gatev, Nikola Arnaudov
 *
 * @version 1.0
 */
public class BaseWaitStack extends BaseStack {
  static final long serialVersionUID = 7418461441113073914L;
  /**
   * Count of threads waiting for push and pop.<p>
   */
  private transient int waitPop = 0;
  private transient int waitPush = 0;

  /**
   * Constructs an empty stack without size limit.<p>
   */
  public BaseWaitStack() {
    this(0);
  }

  /**
   * Constructs an empty stack with specified limit of elements.<p>
   *
   * @param   limit maximum elements in stack.
   */
  public BaseWaitStack(int limit) {
    super(limit);
    waitPop = 0;
    waitPush = 0;
  }

  /**
   * Pushes an item at the top of this stack
   *
   * If the stack is full current thread is either blocked until an element is poped or a thread
   * is interrupted.<p>
   *
   * @param   item the item to be inserted.
   * @return  true if the element is successfuly pushed,
   *          false if the thread has been interrupted.
   */
  public synchronized boolean push(NextItem item) {
    boolean result;

    while (!(result = addFirstItem(item))) {
      waitPop++;
      try {
        wait();
      } catch (InterruptedException _) {
        break;
      } finally {
        waitPop--;
      }
    }

    if (waitPush > 0) {
      notifyAll();
    }

    return result;
  }

  /**
   * Pushes an item at the top of this stack.<p>
   *
   * If the stack is full current thread is either blocked until an element is poped or a thread
   * is interrupted or the time is elpased.<p>
   *
   * @param   item the item to be inserted.
   * @param   timeout maximum wait timeout.
   * @return  true if the element is successfuly pushed,
   *          false timeout has elapsed.
   * @exception   InterruptedException if a thread is interrupted.
   */
  public synchronized boolean push(NextItem item, long timeout) throws InterruptedException {
    boolean result;

    while (!(result = addFirstItem(item))) {
      waitPop++;
      try {
        wait(timeout);
      } finally {
        waitPop--;
      }
      result = addFirstItem(item);
      break;
    }

    if (waitPush > 0) {
      notifyAll();
    }

    return result;
  }

  /**
   * Pops the object at the top of this stack and returns it.<p>
   *
   * If the stack is empty current thread is either blocked until an element is pushed or a thread
   * is interrupted.<p>
   *
   * @return   the element at the top of the stack and
   *           null if the current thread has been interrupted.
   */
  public synchronized NextItem pop() {
    NextItem result;

    while ((result = removeFirstItem()) == null) {
      waitPush++;
      try {
        wait();
      } catch (Exception _) {
        break;
      } finally {
        waitPush--;
      }
    }

    if (waitPop > 0) {
      notifyAll();
    }

    return result;
  }

  /**
   * Pops the object at the top of this stack and returns it.<p>
   *
   * If the stack is empty current thread is either blocked until an element is pushed or a thread
   * is interrupted or the time is elpased.<p>
   *
   * @param   timeout maximum wait timeout.
   * @return   the element at the top of the stack and
   *           null if the timeout elapsed.
   * @exception   InterruptedException  if a thread is interrupted.
   */
  public synchronized NextItem pop(long timeout) throws InterruptedException {
    NextItem result;

    while ((result = removeFirstItem()) == null) {
      waitPush++;
      try {
        wait(timeout);
      } finally {
        waitPush--;
      }
      result = removeFirstItem();
      break;
    }

    if (waitPop > 0) {
      notifyAll();
    }

    return result;
  }

  /**
   * Returns the object at the top of this stack without removing it from the stack.<p>
   *
   * If the stack is empty current thread is either blocked until an element is pushed or a thread
   * is interrupted.<p>
   *
   * @return   the element at the top of the stack and
   *           null if the current thread has been interrupted.
   */
  public synchronized NextItem top() {
    NextItem result;

    while ((result = getFirstItem()) == null) {
      waitPush++;
      try {
        wait();
      } catch (Exception _) {
        break;
      } finally {
        waitPush--;
      }
    }

    return result;
  }

  /**
   * Returns the object at the top of this stack without removing it from the stack.<p>
   *
   * If the stack is empty current thread is either blocked until an element is pushed or a thread
   * is interrupted or the time is elpased.<p>
   *
   * @param   timeout maximum wait timeout.
   * @return   the element at the top of the stack and
   *           null if the timeout elapsed.
   * @exception   InterruptedException if a thread is interrupted.
   */
  public synchronized NextItem top(long timeout) throws InterruptedException {
    NextItem result;

    while ((result = getFirstItem()) == null) {
      waitPush++;
      try {
        wait(timeout);
      } finally {
        waitPush--;
      }
      return getFirstItem();
    }

    return result;
  }

}

