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
 * Represents a first-in-first-out (FIFO) structure queue of objects.
 * The enqueue() method blocks the current thread with wait() if the queue is full
 * The dequeue() and head() methods hang with wait() if the queue is empty.<p>
 *
 * This class is thread safe, unlike its super class BaseQueue, if you want to invoke
 * methods ot super class you must do the synchronization yourself.<p>
 *
 * For example:
 * <p><blockquote><pre>
 *
 *   BaseWaitQueue queue = new BaseWaitQueue();
 *   NextItem item = new NextItemAdapter();
 *
 *   queue.enqueue(item); // thread safe method
 *   int index;
 *   synchronized (queue) {
 *     // here you can call all methods of LinkedList class
 *     index = queue.indexOf(item);
 *   }
 * </pre></blockquote><p>
 *
 * @author Nick Angelov, Nikola Arnaudov
 *
 * @version 1.0
 */
public class BaseWaitQueue extends BaseQueue {
  static final long serialVersionUID = -3134057123629367017L;
  /**
   * Count of threads waiting for enqueue and dequeue.<p>
   */
  private transient int waitPop = 0;
  private transient int waitPush = 0;

  /**
   * Constructs an empty queue without size limit.<p>
   *
   */
  public BaseWaitQueue() {
    this(0);
  }

  /**
   * Constructs an empty queue with the specified limit of elements.<p>
   *
   * @param   limit maximum elements in queue.
   */
  public BaseWaitQueue(int limit) {
    super(limit);
    waitPop = 0;
    waitPush = 0;
  }

  /**
   * Returns the object at the head of this queue without removing it from the queue.<p>
   *
   * If the queue is empty current thread is either blocked until an element is enqueued or a thread
   * is interrupted.<p>
   *
   * @return   the element at the head of the queue and
   *           null if the current thread has been interrupted.
   */
  public synchronized NextItem head() {
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
   * Returns the object at the top of this queue without removing it from the queue.<p>
   *
   * If the queue is empty current thread is either blocked until an element is enqueued or a thread
   * is interrupted or the time is elpased.<p>
   *
   * @param   timeout maximum wait timeout.
   * @return   the element at the top of the queue and
   *           null if the timeout elapsed.
   * @exception   InterruptedException if a thread is interrupted.
   */
  public synchronized NextItem head(long timeout) throws InterruptedException {
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

  /**
   * Removes the object at the head of this queue and returns it.<p>
   *
   * If the queue is empty current thread is either blocked until an element is enqueued or a thread
   * is interrupted.<p>
   *
   * @return   the element at the head of the queue and
   *           null if the current thread has been interrupted.
   */
  public synchronized NextItem dequeue() {
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
   * Removes the object at the head of this queue and returns it.<p>
   *
   * If the queue is empty current thread is either blocked until an element is enqueued or a thread
   * is interrupted or the time is elpased.<p>
   *
   * @param   timeout maximum wait timeout.
   * @return   the element at the head of the queue and
   *           null if the timeout elapsed.
   * @exception   InterruptedException  if a thread is interrupted
   */
  public synchronized NextItem dequeue(long timeout) throws InterruptedException {
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
   * Enqueues an item at the tail of this queue.<p>
   *
   * If the queue is full current thread is either blocked until an element is dequeued or a thread
   * is interrupted.<p>
   *
   * @param   item the item to be inserted.
   * @return    true if element is successfuly enqueued,
   *             false if the thread has been interrupted.
   */
  public synchronized boolean enqueue(NextItem item) {
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
   * Enqueues an item at the tail of this queue.
   *
   * If the queue is full current thread is either blocked until an element is dequeued or a thread
   * is interrupted or the time is elpased.<p>
   *
   * @param   item the item to be inserted.
   * @param   timeout max wait timeout
   * @return  true if element is successfuly queued,
   *          false timeout has elapsed.
   * @exception   InterruptedException  if a thread is interrupted
   */
  public synchronized boolean enqueue(NextItem item, long timeout) throws InterruptedException {
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

}

