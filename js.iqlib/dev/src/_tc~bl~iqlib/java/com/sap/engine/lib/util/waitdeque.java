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
 * Implementation of Double Ended Queue (Deque).
 * The push() method blocks the current thread with wait() if the deque is full
 * The pop() and top() methods hang with wait() if the deque is empty.<p>
 *
 * This class is thread safe, unlike its super class Deque, if you want to invoke
 * methods ot super class you must do the synchronization yourself.<p>
 *
 * <b>Pooling</b>: see java doc of super class.<p>
 *
 * Example for synchronization:
 * <p><blockquote><pre>
 *
 *   WaitDeque deque = new WaitDeque();
 *
 *   deque.pushLeft("nick");
 *
 *   int index;
 *   synchronized (deque) {
 *     // here you can call all methods of Deque class
 *     index = deque.indexOfValue("nick");
 *   }
 * </pre></blockquote><p>
 *
 * @author Nikola Arnaudov, Krasimir Semerdzhiev
 *
 * @version 1.0
 */
public class WaitDeque extends Deque {

  static final long serialVersionUID = 542737391460054467L;
  /**
   * Count of threads waiting for pop.<p>
   */
  private transient int waitPush = 0;
  /**
   * Count of threads waiting for push.<p>
   */
  private transient int waitPop = 0;

  /**
   * Constructs an empty deque without size limit.<p>
   *
   */
  public WaitDeque() {

  }

  /**
   * Constructs an empty deque with specified limit of elements.<p>
   *
   * @param   limit the maximum number of  elements in deque.
   *
   */
  public WaitDeque(int limit) {
    super(limit);
  }

  /**
   * Inserts an element into left side of deque.<p>
   *
   * If the deque is full the current thread is either blocked until an element is poped or a thread
   * is interrupted.<p>
   *
   * @param   item the item to be inserted.
   * @return  true if element is successfuly pushed
   *          false if the thread has been interrupted
   */
  public synchronized boolean pushLeft(Object item) {
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
   * Inserts an element into left side of deque.<p>
   *
   * If the deque is full the current thread is either blocked until an element is poped or a thread
   * is interrupted or the time is elpased.<p>
   *
   * @param   item the item to be inserted.
   * @param   timeout maximum wait timeout.
   * @return  true if element is successfuly pushed,
   *          false if timeout has elapsed.
   * @exception   InterruptedException  if a thread is interrupted.
   */
  public synchronized boolean pushLeft(Object item, long timeout) throws InterruptedException {
    boolean result;
    long lastTime = System.currentTimeMillis();
    while (!(result = addFirst(item) != null && (timeout -= (System.currentTimeMillis() - lastTime)) > 0)) {
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
   * Deletes the element from left side of deque and returns it.<p>
   *
   * If the stack is empty the current thread is either blocked until an element is pushed or a thread
   * is interrupted.<p>
   *
   * @return   the element at the top of the deque and
   *           null if the current thread has been interrupted.
   */
  public synchronized Object popLeft() {
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
   * Deletes the element from left side of deque and returns it.<p>
   *
   * If the stack is empty the current thread is either blocked until an element is pushed or a thread
   * is interrupted or the time is elpased.<p>
   *
   * @param   timeout maximum wait timeout.
   * @return   the element at the top of the deque and
   *           null if the timeout elapsed
   * @exception   InterruptedException if a thread is interrupted.
   */
  public synchronized Object popLeft(long timeout) throws InterruptedException {
    Object result;
    long lastTime = System.currentTimeMillis();
    while ((result = removeFirst()) == null && (timeout -= (System.currentTimeMillis() - lastTime)) > 0) {
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
   * Returns the element which is on the left side in deque
   * without removing it from the deque.<p>
   *
   * If the stack is empty the current thread is either blocked
   * until an element is pushed or a thread is interrupted.<p>
   *
   * @return   the element at the top of the deque and
   *           null if the current thread has been interrupted.
   */
  public synchronized Object topLeft() {
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
   * Returns the element which is on the left side in deque
   * without removing it from the deque.<p>
   *
   * If the deque is empty the current thread is either
   * blocked until an element is pushed or a thread is interrupted or the time is elpased.<p>
   *
   * @param   timeout maximum wait timeout.
   * @return   the element at the top of the deque and
   *           null if the timeout elapsed.
   * @exception   InterruptedException if a thread is interrupted.
   */
  public synchronized Object topLeft(long timeout) throws InterruptedException {
    Object result;
    long lastTime = System.currentTimeMillis();
    while ((result = getFirst()) == null && (timeout -= (System.currentTimeMillis() - lastTime)) > 0) {
      waitPop++;
      try {
        lastTime = System.currentTimeMillis();
        wait(timeout);
      } finally {
        waitPop--;
      }
      return getFirst();
    }

    return result;
  }

  /**
   * Inserts an element into right side of deque.<p>
   *
   * If the deque is full the current thread is either
   * blocked until an element is poped or a thread is interrupted.
   *
   * @param   item the item to be inserted.
   * @return  true if element is successfuly pushed,
   *          false if the thread has been interrupted.
   */
  public synchronized boolean pushRight(Object item) {
    boolean result;

    while (!(result = addLast(item) != null)) {
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
   * Inserts an element into right side of deque.<p>
   *
   * If the deque is full the current thread is either blocked until an element is poped or a thread
   * is interrupted or the time is elpased.
   *
   * @param   item the item to be inserted.
   * @param   timeout maximum wait timeout.
   * @return  true if element is successfuly queued and
   *          false timeout has elapsed.
   * @exception   InterruptedException if a thread is interrupted.
   */
  public synchronized boolean pushRight(Object item, long timeout) throws InterruptedException {
    boolean result;
    long lastTime = System.currentTimeMillis();
    while (!(result = addLast(item) != null) && (timeout -= (System.currentTimeMillis() - lastTime)) > 0) {
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
   * Deletes the element from right side of deque and returns it.<p>
   *
   * If the deque is empty the current thread is either blocked until an element is pushed or a thread
   * is interrupted.<p>
   *
   * @return   the element at the top of the deque and
   *           null if the current thread has been interrupted.
   */
  public synchronized Object popRight() {
    Object result;

    while ((result = removeLast()) == null) {
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
   * Deletes the element from right side of deque and returns it.<p>
   *
   * If the stack is empty the current thread is either blocked until an element is pushed or a thread
   * is interrupted or the time is elpased.<p>
   *
   * @param   timeout maximum wait timeout.
   * @return   the element at the top of the deque or
   *           null if the timeout elapsed.
   * @exception   InterruptedException if thread is interrupted.
   */
  public synchronized Object popRight(long timeout) throws InterruptedException {
    Object result;
    long lastTime = System.currentTimeMillis();
    while ((result = removeLast()) == null && (timeout -= (System.currentTimeMillis() - lastTime)) > 0) {
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
   * Returns the element which is on the right side in deque without removing it from the deque.
   *
   * If the deque is empty the current thread is either blocked until an element is pushed or a thread
   * is interrupted.<p>
   *
   * @return   the element at the top of the deque and
   *           null if the current thread has been interrupted.
   */
  public synchronized Object topRight() {
    Object result;

    while ((result = getLast()) == null) {
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
   * Returns the element which is on the right side in deque without removing it from the deque.<p>
   *
   * If the deque is empty the current thread is either blocked until an element is pushed or a thread
   * is interrupted or the time is elpased.
   *
   * @param   timeout maximum wait timeout.
   * @return   the element at the top of the deque and
   *           null if the timeout elapsed.
   * @exception   InterruptedException if a thread is interrupted.
   */
  public synchronized Object topRight(long timeout) throws InterruptedException {
    Object result;
    long lastTime = System.currentTimeMillis();
    while ((result = getLast()) == null && (timeout -= (System.currentTimeMillis() - lastTime)) > 0) {
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

