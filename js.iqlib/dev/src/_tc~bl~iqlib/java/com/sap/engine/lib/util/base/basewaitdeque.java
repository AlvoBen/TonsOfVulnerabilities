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
 * Implementation of Double Ended Queue (Deque).
 * The push() method blocks the current thread with wait() if the deque is full
 * The pop() and top() methods hang with wait() if the deque is empty.<p>
 *
 * This class is thread safe, unlike its super class BaseDeque, if you want to invoke
 * methods ot super class you must do the synchronization yourself.<p>
 *
 *
 * Example for synchronization:
 * <p><blockquote><pre>
 *
 *   BaseWaitDeque deque = new BaseWaitDeque();
 *   LinearItem item = new LinearItemAdapter();
 *
 *   deque.pushLeft(item);
 *
 *   int index;
 *   synchronized (deque) {
 *     // here you can call all methods of DoublyLinkedList class
 *     index = deque.indexOf(item);
 *   }
 * </pre></blockquote><p>
 *
 * @author Nikola Arnaudov
 *
 * @version 1.0
 */
public class BaseWaitDeque extends BaseDeque {
  
  static final long serialVersionUID = -5313186376020768181L;
  /**
   * Count of threads waiting for push and pop.<p>
   */
  private transient int waitPop = 0;
  private transient int waitPush = 0;

  /**
   * Constructs an empty deque without size limit.<p>
   */
  public BaseWaitDeque() {

  }

  /**
   * Constructs an empty deque with specified limit of elements.<p>
   *
   * @param   limit the maximum number of  elements in deque.
   *
   */
  public BaseWaitDeque(int limit) {
    super(limit);
  }

  /**
   * Inserts element into left side of deque.<p>
   *
   * If the deque is full current thread is either blocked until element is poped or thread
   * is interrupted.<p>
   *
   * @param   item the item to be inserted.
   * @return  true if element is successfuly pushed
   *          false if the thread has been interrupted.
   */
  public synchronized boolean pushLeft(LinearItem item) {
    boolean result;

    while (!(result = addFirstItem(item))) {
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
  public synchronized boolean pushLeft(LinearItem item, long timeout) throws InterruptedException {
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
   * Deletes the element from left side of deque and returns it.<p>
   *
   * If the stack is empty the current thread is either blocked until an element is pushed or a thread
   * is interrupted.<p>
   *
   * @return   the element at the top of the deque and
   *           null if the current thread has been interrupted.
   */
  public synchronized LinearItem popLeft() {
    LinearItem result;

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
  public synchronized LinearItem popLeft(long timeout) throws InterruptedException {
    LinearItem result;

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
   * Returns the element which is on the left side in deque
   * without removing it from the deque.<p>
   *
   * If the stack is empty the current thread is either blocked
   * until an element is pushed or a thread is interrupted.<p>
   *
   * @return   the element at the top of the deque and
   *           null if the current thread has been interrupted.
   */
  public synchronized LinearItem topLeft() {
    LinearItem result;

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
  public synchronized LinearItem topLeft(long timeout) throws InterruptedException {
    LinearItem result;

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
  public synchronized boolean pushRight(LinearItem item) {
    boolean result;

    while (!(result = addLastItem(item))) {
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
  public synchronized boolean pushRight(LinearItem item, long timeout) throws InterruptedException {
    boolean result;

    while (!(result = addLastItem(item))) {
      waitPop++;
      try {
        wait(timeout);
      } finally {
        waitPop--;
      }
      result = addLastItem(item);
      break;
    }

    if (waitPush > 0) {
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
  public synchronized LinearItem popRight() {
    LinearItem result;

    while ((result = removeLastItem()) == null) {
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
  public synchronized LinearItem popRight(long timeout) throws InterruptedException {
    LinearItem result;

    while ((result = removeLastItem()) == null) {
      waitPush++;
      try {
        wait(timeout);
      } finally {
        waitPush--;
      }
      result = removeLastItem();
      break;
    }

    if (waitPop > 0) {
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
  public synchronized LinearItem topRight() {
    LinearItem result;

    while ((result = getLastItem()) == null) {
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
  public synchronized LinearItem topRight(long timeout) throws InterruptedException {
    LinearItem result;

    while ((result = getLastItem()) == null) {
      waitPush++;
      try {
        wait(timeout);
      } finally {
        waitPush--;
      }
      return getLastItem();
    }

    return result;
  }

}

