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
 * Represents a last-in-first-out (LIFO) structure stack of objects.
 * The push() method blocks the current thread with wait() if the stack is full
 * The pop() and top() methods hang with wait() if the stack is empty.<p>
 *
 * <b>Pooling</b>: see java doc of super class.<p>
 *
 * This class is thread safe, unlike its super class Stack, if you want to invoke
 * methods ot super class you must do the synchronization yourself.<p>
 *
 * For example:
 * <p><blockquote><pre>
 *
 *   WaitStack stack = new WaitStack();
 *
 *   stack.push("nick"); // thread safe method
 *   int index;
 *   synchronized (stack) {
 *     // here you can call all methods of LinkedListWrapped class
 *     index = stack.indexOfValue("nick");
 *   }
 * </pre></blockquote><p>
 *
 * @author Andrei Gateff, Nikola Arnaudov, Krasimir Semerdzhiev
 *
 * @version 1.0
 */
public class WaitStack extends Stack {

  static final long serialVersionUID = -2634515914420145207L;
  /**
   * Count of threads waiting for pop.<p>
   */
  private transient int waitPush = 0;
  /**
   * Count of threads waiting for push.<p>
   */
  private transient int waitPop = 0;

  /**
   * Constructs an empty stack without size limit.<p>
   *
   */
  public WaitStack() {
    this(0);
  }

  /**
   * Constructs an empty stack with specified limit of elements.<p>
   *
   * @param   limit maximum elements in stack.
   *
   */
  public WaitStack(int limit) {
    super(limit);
    waitPush = 0;
    waitPop = 0;
  }

  /**
   * Pushes an item at the top of this stack
   *
   * If the stack is full current thread is either blocked until an element is poped or a thread
   * is interrupted
   *
   * @param   item the object to be inserted.
   * @return  true if the element is successfuly pushed,
   *          false if the thread has been interrupted.
   */
  public synchronized boolean push(Object item) {
    boolean result;

    while (!(result = addFirst(item) != null)) {
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
   * Pushes an item at the top of this stack.<p>
   *
   * If the stack is full current thread is either blocked until an element is poped or a thread
   * is interrupted or the time is elpased.<p>
   *
   * @param   item the object to be inserted.
   * @param   timeout maximum wait timeout.
   * @return  true if the element is successfuly pushed,
   *          false timeout has elapsed.
   * @exception   InterruptedException if a thread is interrupted.
   */
  public synchronized boolean push(Object item, long timeout) throws InterruptedException {
    boolean result;
    long lastTime = System.currentTimeMillis();
    while (!(result = addFirst(item) != null) && (timeout -= (System.currentTimeMillis() - lastTime)) > 0) {
      waitPush++;
      try {
        lastTime = System.currentTimeMillis();
        wait(timeout);
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
   * is interrupted.<p>
   *
   * @return   the element at the top of the stack and
   *           null if the current thread has been interrupted.
   */
  public synchronized Object pop() {
    Object result;

    while ((result = removeFirst()) == null) {
      waitPop++;
      try {
        wait();
      } catch (Exception _) {
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
   * Pops the object at the top of this stack and returns it.<p>
   *
   * If the stack is empty current thread is either blocked until an element is pushed or a thread
   * is interrupted or the time is elpased.<t>
   *
   * @param   timeout maximum wait timeout.
   * @return   the element at the top of the stack and
   *           null if the timeout elapsed.
   * @exception   InterruptedException  if a thread is interrupted.
   */
  public synchronized Object pop(long timeout) throws InterruptedException {
    Object result;
    long lastTime = System.currentTimeMillis();
    while ((result = removeFirst()) == null  && (timeout -= (System.currentTimeMillis() - lastTime)) > 0) {
      waitPop++;
      try {
        lastTime = System.currentTimeMillis();
        wait(timeout);
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
   * Returns the object at the top of this stack without removing it from the stack.<p>
   *
   * If the stack is empty current thread is either blocked until an element is pushed or a thread
   * is interrupted.<p>
   *
   * @return   the element at the top of the stack and
   *           null if the current thread has been interrupted.
   */
  public synchronized Object top() {
    Object result;

    while ((result = getFirst()) == null) {
      waitPop++;
      try {
        wait();
      } catch (Exception _) {
        break;
      } finally {
        waitPop--;
      }
    }

    return result;
  }

  /**
   * Returns the object at the top of this stack without removing it from the stack.<p>
   *
   * If the stack is empty current thread is either blocked until an element is pushed or a thread
   * is interrupted or the time is elpased.<p
   *
   * @param   timeout maximum wait timeout.
   * @return   the element at the top of the stack and
   *           null if the timeout elapsed.
   * @exception   InterruptedException if a thread is interrupted.
   */
  public synchronized Object top(long timeout) throws InterruptedException {
    Object result;
    long lastTime = System.currentTimeMillis();
    while ((result = getFirst()) == null  && (timeout -= (System.currentTimeMillis() - lastTime)) > 0) {
      waitPop++;
      try {
        lastTime = System.currentTimeMillis();
        wait(timeout);
      } finally {
        waitPop--;
      }
    }

    return result;
  }

}

