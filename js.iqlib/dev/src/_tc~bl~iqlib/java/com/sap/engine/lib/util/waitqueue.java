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

import java.util.Collection;

/**
 * Represents a first-in-first-out (FIFO) structure queue of objects.
 * The enqueue() method blocks the current thread with wait() if the
 * queue is full
 * The dequeue() and head() methods hang with wait() if the queue is
 * empty.<p>
 * <p/>
 * <b>Pooling</b>: see java doc of super class.<p>
 * <p/>
 * This class is thread safe, unlike its super class Queue, if you want
 * to invoke
 * methods ot super class you must do the synchronization yourself.<p>
 * <p/>
 * For example:
 * <p><blockquote><pre>
 * <p/>
 *   WaitQueue queue = new WaitQueue();
 * <p/>
 *   queue.enqueue("nick"); // thread safe method
 *   int index;
 *   synchronized (queue) {
 *     // here you can call all methods of LinkedListWrapped class
 *     index = queue.indexOfValue("nick");
 *   }
 * </pre></blockquote><p>
 *
 * @author Nick Angelov, Nikola Arnaudov, Krasimir Semerdzhiev, Nikolai Dimitrov
 * @version 6.30
 */
public class WaitQueue extends Queue { //$JL-CLONE$

  static final long serialVersionUID = 8536656333326965672L;
  /**
   * Count of threads waiting for dequeue.<p>
   */
  private transient int pushWaiting = 0;

  /**
   * Count of threads waiting for enqueue.<p>
   */
  private transient int popWaiting = 0;

  /**
   * Monitor for the pushing threads, that are waiting for free space
   */
  private transient Object pushMonitor = new Object();

  /**
   * Queue internal state that helps with the synchronization issues
   * over the queue and pushMonitor locks
   */
  private transient int id = 0;

  /**
   * It keeps the count of waiting and preparing to wait threads.
   */
  private transient int pushWillWait = 0;

  /**
   * Constructs an empty queue without size limit.<p>
   */
  public WaitQueue() {
    this(0);
  }

  /**
   * Constructs an empty queue with the specified limit of elements.<p>
   *
   * @param limit maximum elements in queue.
   */
  public WaitQueue(int limit) {
    super(limit);
    pushWillWait = 0;
    popWaiting = 0;
  }

  /**
   * Returns the object at the head of this queue without removing it
   * from the queue.<p>
   * <p/>
   * If the queue is empty current thread is either blocked until an
   * element is enqueued or a thread
   * is interrupted.<p>
   *
   * @return the element at the head of the queue and
   *         null if the current thread has been interrupted.
   */
  public synchronized Object head() {
    Object result;

    while (true) {
      result = getFirst();
      if (result != null) {
        break;
      } else {
        popWaiting++;
        try {
          this.wait();
        } catch (InterruptedException _) {
          break;
        } finally {
          popWaiting--;
        }
      }
    }

    return result;
  }

  /**
   * Returns the object at the top of this queue without removing it
   * from the queue.<p>
   * <p/>
   * If the queue is empty current thread is either blocked until an
   * element is enqueued or a thread
   * is interrupted or the time is elpased.<p>
   *
   * @param timeout maximum wait timeout.
   * @return the element at the top of the queue and
   *         null if the timeout elapsed.
   * @throws InterruptedException if a thread is interrupted.
   */
  public synchronized Object head(long timeout) throws
      InterruptedException {
    Object result;
    long startTime = System.currentTimeMillis();

    while (true) {
      result = getFirst();
      if (result != null) {
        break;
      } else {
        if (timeout > (System.currentTimeMillis() - startTime)) {
          popWaiting++;
          try {
            this.wait();
          } finally {
            popWaiting--;
          }
        } else {
          break;
        }
      }
    }

    return result;
  }

  /**
   * Removes the object at the head of this queue and returns it.<p>
   * <p/>
   * If the queue is empty current thread is either blocked until an
   * element is enqueued or a thread
   * is interrupted.<p>
   *
   * @return the element at the head of the queue and
   *         null if the current thread has been interrupted.
   */
  public synchronized Object dequeue() {
    Object result;

    while (true) {
      result = removeFirst();
      if (result != null) {
        if (pushWillWait > 0) {
          synchronized (pushMonitor) {
            if (pushWaiting > 0) {
              pushMonitor.notify();
            } else {
              id++;
            }
          }
        }
        break;
      } else {
        popWaiting++;
        try {
          this.wait();
        } catch (InterruptedException _) {
          break;
        } finally {
          popWaiting--;
        }
      }
    }

    return result;
  }

  /**
   * Removes the object at the head of this queue and returns it.<p>
   * <p/>
   * If the queue is empty current thread is either blocked until an
   * element is enqueued or a thread
   * is interrupted or the time is elpased.<p>
   *
   * @param timeout maximum wait timeout.
   * @return the element at the head of the queue and
   *         null if the timeout elapsed.
   * @throws InterruptedException if a thread is interrupted
   */
  public synchronized Object dequeue(long timeout) throws
      InterruptedException {
    long startTime = System.currentTimeMillis();
    Object result;

    while (true) {
      result = removeFirst();
      if (result != null) {
        if (pushWillWait > 0) {
          synchronized (pushMonitor) {
            if (pushWaiting > 0) {
              pushMonitor.notify();
            } else {
              id++;
            }
          }
        }
        break;
      } else {
        if (timeout > (System.currentTimeMillis() - startTime)) {
          popWaiting++;
          try {
            this.wait();
          } finally {
            popWaiting--;
          }
        } else {
          break;
        }
      }
    }

    return result;
  }

  /**
   * Inserts the specified element into this queue, if possible.  When
   * using queues that may impose insertion restrictions (for
   * example capacity bounds), method <tt>offer</tt> is generally
   * preferable to method {@link java.util.Collection#add}, which can fail to
   * insert an element only by throwing an exception.
   *
   * @param o the element to add.
   * @return <tt>true</tt> if it was possible to add the element to
   *         this queue, else <tt>false</tt>
   * @throws NullPointerException if the specified element is <tt>null</tt>
   */
  public boolean offer(Object o) {
    boolean result;

    synchronized (this) {
      if (result = (addLast(o) != null)) {
        this.notify();
      }
    }
    return result;
  }

  public synchronized Object poll() {
    Object result = removeFirst();
    if (result != null) {
      if (pushWillWait > 0) {
        synchronized (pushMonitor) {
          if (pushWaiting > 0) {
            pushMonitor.notify();
          } else {
            id++;
          }
        }
      }
    }
    return result;
  }

  public boolean enqueueNoWait(Object item) {
    boolean result = false;

    synchronized (this) {
      if (super.size() < popWaiting) {//be sure there is a thread to handle  the request
        result = addLast(item) != null;
        if (result) {
          this.notify();
        }
      }
    }

    return result;
  }

  /**
   * Enqueues an item at the tail of this queue.<p>
   * <p/>
   * If the queue is full current thread is either blocked until an
   * element is dequeued or a thread
   * is interrupted.<p>
   *
   * @param item the object to be inserted.
   * @return true if element is successfuly enqueued,
   *         false if the thread has been interrupted.
   */
  public boolean enqueue(Object item) {
    boolean result;
    boolean firstTime = true;
    boolean exit = false;
    int localId = 0;

    while (true) {
      synchronized (this) {
        if (!firstTime) {
          pushWillWait--;
        }

        result = addLast(item) != null;
        if (result) {
          if (popWaiting > 0) {
            this.notify();
          }
          break;
        } else {
          localId = this.id;
          pushWillWait++;
          firstTime = false;
        }
      }

      synchronized (pushMonitor) {
        if (localId == id) {
          try {
            pushWaiting++;
            pushMonitor.wait();
          } catch (InterruptedException e) {
            exit = true;
          } finally {
            pushWaiting--;
          }
        }
      }

      if (exit) {
        synchronized (this) {
          pushWillWait--;
        }
        break;
      }
    }

    return result;
  }

  /**
   * Enqueues an item at the tail of this queue.
   * <p/>
   * If the queue is full current thread is either blocked until an
   * element is dequeued or a thread
   * is interrupted or the time is elpased.
   *
   * @param item    the object to be inserted.
   * @param timeout max wait timeout
   * @return true if element is successfuly queued,
   *         false timeout has elapsed.
   * @throws InterruptedException if a thread is interrupted
   */
  public boolean enqueue(Object item, long timeout) throws
      InterruptedException {
    long startTime = System.currentTimeMillis();
    boolean result;
    boolean firstTime = true;
    boolean exit = false;
    InterruptedException localExc = null;
    int localId = 0;

    while (true) {
      synchronized (this) {
        if (!firstTime) {
          pushWillWait--;
        }

        result = addLast(item) != null;
        if (result) {
          if (popWaiting > 0) {
            this.notify();
          }
          break;
        } else {
          localId = this.id;
          pushWillWait++;
          firstTime = false;
        }
      }

      synchronized (pushMonitor) {
        if (timeout > (System.currentTimeMillis() - startTime)) {
          if (localId == id) {
            try {
              pushWaiting++;
              pushMonitor.wait();
            } catch (InterruptedException e) {
              localExc = e;
              exit = true;
            } finally {
              pushWaiting--;
            }
          }
        } else {
          exit = true;
        }
      }

      if (exit) {
        synchronized (this) {
          pushWillWait--;
        }
        if (localExc != null) {
          throw localExc;
        }
        break;
      }
    }

    return result;
  }

  public int getWaitingDequeueThreadsCount() {
    synchronized (this) {
      return popWaiting;
    }
  }

  public int getWaitingEnqueueThreadsCount() {
    synchronized (pushMonitor) {
      return pushWaiting;
    }
  }

  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

}

//public class WaitQueue extends WaitQueue2 {
//
//  public WaitQueue() {
//  }
//
//  public WaitQueue(int limit) {
//    super(limit);
//  }
//
////  public boolean enqueueNoWait(Object item) {
////    return enqueue(item);
////  }
//
//}
//
//
//class WaitQueue1 extends Queue {
//
//  /**
//   * Count of threads waiting for dequeue.<p>
//   */
//  private transient int waitPush = 0;
//
//  /**
//   * Count of threads waiting for enqueue.<p>
//   */
//  private transient int waitPop = 0;
//
//  /**
//   * Constructs an empty queue without size limit.<p>
//   *
//   */
//  public WaitQueue1() {
//    this(0);
//  }
//
//  /**
//   * Constructs an empty queue with the specified limit of elements.<p>
//   *
//   * @param   limit maximum elements in queue.
//   *
//   */
//  public WaitQueue1(int limit) {
//    super(limit);
//    waitPush = 0;
//    waitPop = 0;
//  }
//
////  /**
////   * Returns the object at the head of this queue without removing it from the queue.<p>
////   *
////   * If the queue is empty current thread is either blocked until an element is enqueued or a thread
////   * is interrupted.<p>
////   *
////   * @return   the element at the head of the queue and
////   *           null if the current thread has been interrupted.
////   */
////  public synchronized Object head() {
////    Object result;
////    while ((result = getFirst()) == null) {
////      waitPop++;
////      try {
////        wait();
////      } catch (Exception _) {
////        break;
////      } finally {
////        waitPop--;
////      }
////    }
////    return result;
////  }
////
////  /**
////   * Returns the object at the top of this queue without removing it from the queue.<p>
////   *
////   * If the queue is empty current thread is either blocked until an element is enqueued or a thread
////   * is interrupted or the time is elpased.<p>
////   *
////   * @param   timeout maximum wait timeout.
////   * @return   the element at the top of the queue and
////   *           null if the timeout elapsed.
////   * @exception   InterruptedException if a thread is interrupted.
////   */
////  public synchronized Object head(long timeout) throws InterruptedException {
////    Object result;
////    long lastTime = System.currentTimeMillis();
////    while ((result = getFirst()) == null && (timeout -= (System.currentTimeMillis() - lastTime)) > 0) {
////      waitPop++;
////      try {
////        lastTime = System.currentTimeMillis();
////        wait(timeout);
////      } finally {
////        waitPop--;
////      }
////    }
////
////    return result;
////  }
//
//  /**
//   * Removes the object at the head of this queue and returns it.<p>
//   *
//   * If the queue is empty current thread is either blocked until an element is enqueued or a thread
//   * is interrupted.<p>
//   *
//   * @return   the element at the head of the queue and
//   *           null if the current thread has been interrupted.
//   */
//  public synchronized Object dequeue() {
//    Object result;
//
//    while ((result = removeFirst()) == null) {
//      waitPop++;
//      try {
//        wait();
//      } catch (Exception _) {
//        break;
//      } finally {
//        waitPop--;
//      }
//    }
//
//    if (waitPush > 0) {
//      notify();
//    }
//
//    return result;
//  }
//
////  /**
////   * Removes the object at the head of this queue and returns it.<p>
////   *
////   * If the queue is empty current thread is either blocked until an element is enqueued or a thread
////   * is interrupted or the time is elpased.<p>
////   *
////   * @param   timeout maximum wait timeout.
////   * @return   the element at the head of the queue and
////   *           null if the timeout elapsed.
////   * @exception   InterruptedException  if a thread is interrupted
////   */
////  public synchronized Object dequeue(long timeout) throws InterruptedException {
////    Object result;
////
////    long lastTime = System.currentTimeMillis();
////    while ((result = removeFirst()) == null && (timeout -= (System.currentTimeMillis() - lastTime)) > 0) {
////      waitPop++;
////      try {
////        lastTime = System.currentTimeMillis();
////        wait(timeout);
////      } finally {
////        waitPop--;
////      }
////    }
////
////    if (waitPush > 0) {
////      notify();
////    }
////
////    return result;
////  }
//
//  /**
//   * Enqueues an item at the tail of this queue.<p>
//   *
//   * If the queue is full current thread is either blocked until an element is dequeued or a thread
//   * is interrupted.<p>
//   *
//   * @param   item the object to be inserted.
//   * @return    true if element is successfuly enqueued,
//   *             false if the thread has been interrupted.
//   */
//  public synchronized boolean enqueue(Object item) {
//    boolean result;
//
//    while (!(result = addLast(item) != null)) {
//      waitPush++;
//      try {
//        wait();
//      } catch (Exception _) {
//        break;
//      } finally {
//        waitPush--;
//      }
//    }
//
//    if (waitPop > 0) {
//      notify();
//    }
//
//    return result;
//  }
//
//  public boolean enqueueNoWait(Object item) {
//    boolean result = false;
//
//    synchronized (this) {
//      //todo ??????
////      if (waitPop > size()) {
//      result = addLast(item) != null;
//      if (result) {
//        this.notify();
//      }
////      }
//    }
//
//    return result;
//  }
//
//
////  /**
////   * Enqueues an item at the tail of this queue.
////   *
////   * If the queue is full current thread is either blocked until an element is dequeued or a thread
////   * is interrupted or the time is elpased.
////   *
////   * @param   item the object to be inserted.
////   * @param   timeout max wait timeout
////   * @return  true if element is successfuly queued,
////   *          false timeout has elapsed.
////   * @exception   InterruptedException  if a thread is interrupted
////   */
////  public synchronized boolean enqueue(Object item, long timeout) throws InterruptedException {
////    boolean result;
////    long lastTime = System.currentTimeMillis();
////    while (!(result = addLast(item) != null) && (timeout -= (System.currentTimeMillis() - lastTime)) > 0) {
////      waitPush++;
////      try {
////        lastTime = System.currentTimeMillis();
////        wait(timeout);
////      } finally {
////        waitPush--;
////      }
////    }
////
////    if (waitPop > 0) {
////      notify();
////    }
////    return result;
////  }
//
//  public synchronized int getWaitingDequeueThreadsCount() {
//    return waitPop;
//  }
//
//  public synchronized int getWaitingEnqueueThreadsCount() {
//    return waitPush;
//  }
//
//  public synchronized int size() {
//    return super.size();
//  }
//
//}
//
///**
// * Represents a first-in-first-out (FIFO) structure queue of objects.
// * The enqueue() method blocks the current thread with wait() if the
// queue is full
// * The dequeue() and head() methods hang with wait() if the queue is
// empty.<p>
// *
// * <b>Pooling</b>: see java doc of super class.<p>
// *
// * This class is thread safe, unlike its super class Queue, if you want
// to invoke
// * methods ot super class you must do the synchronization yourself.<p>
// *
// * For example:
// * <p><blockquote><pre>
// *
// *   WaitQueue queue = new WaitQueue();
// *
// *   queue.enqueue("nick"); // thread safe method
// *   int index;
// *   synchronized (queue) {
// *     // here you can call all methods of LinkedListWrapped class
// *     index = queue.indexOfValue("nick");
// *   }
// * </pre></blockquote><p>
// *
// * @author Nick Angelov, Nikola Arnaudov, Krasimir Semerdzhiev, Nikolai
// Dimitrov
// *
// * @version 6.30
// */
//class WaitQueue3 extends Inner {
//
//  /**
//   * Count of threads waiting for dequeue.<p>
//   */
//  private transient int pushWaiting = 0;
//
//  /**
//   * Count of threads waiting for enqueue.<p>
//   */
//  private transient int popWaiting = 0;
//
//  /**
//   * Monitor for the pushing threads, that are waiting for free space
//   */
//  private transient Object pushMonitor = new Object();
//
//  /**
//   * Queue internal state that helps with the synchronization issues
//   * over the queue and pushMonitor locks
//   */
//  private transient int id = 0;
//
//  /**
//   * It keeps the count of waiting and preparing to wait threads.
//   */
//  private transient int pushWillWait = 0;
//
//  /**
//   * Constructs an empty queue without size limit.<p>
//   *
//   */
//  public WaitQueue3() {
//    this(0);
//  }
//
//  /**
//   * Constructs an empty queue with the specified limit of elements.<p>
//   *
//   * @param   limit maximum elements in queue.
//   *
//   */
//  public WaitQueue3(int limit) {
//    super(limit);
//    pushWillWait = 0;
//    popWaiting = 0;
//  }
//
//  /**
//   * Returns the object at the head of this queue without removing it
//   from the queue.<p>
//   *
//   * If the queue is empty current thread is either blocked until an
//   element is enqueued or a thread
//   * is interrupted.<p>
//   *
//   * @return   the element at the head of the queue and
//   *           null if the current thread has been interrupted.
//   */
//  public synchronized Object head() {
//    Object result;
//
//    while (true) {
//      result = getFirst();
//      if (result != null) {
//        break;
//      } else {
//        popWaiting++;
//        try {
//          this.wait();
//        } catch (InterruptedException _) {
//          break;
//        } finally {
//          popWaiting--;
//        }
//      }
//    }
//
//    return result;
//  }
//
//  /**
//   * Returns the object at the top of this queue without removing it
//   from the queue.<p>
//   *
//   * If the queue is empty current thread is either blocked until an
//   element is enqueued or a thread
//   * is interrupted or the time is elpased.<p>
//   *
//   * @param   timeout maximum wait timeout.
//   * @return   the element at the top of the queue and
//   *           null if the timeout elapsed.
//   * @exception   InterruptedException if a thread is interrupted.
//   */
//  public synchronized Object head(long timeout) throws
//      InterruptedException {
//    Object result;
//    long startTime = System.currentTimeMillis();
//
//    while (true) {
//      result = getFirst();
//      if (result != null) {
//        break;
//      } else {
//        if (timeout > (System.currentTimeMillis() - startTime)) {
//          popWaiting++;
//          try {
//            this.wait();
//          } finally {
//            popWaiting--;
//          }
//        } else {
//          break;
//        }
//      }
//    }
//
//    return result;
//  }
//
//  /**
//   * Removes the object at the head of this queue and returns it.<p>
//   *
//   * If the queue is empty current thread is either blocked until an
//   element is enqueued or a thread
//   * is interrupted.<p>
//   *
//   * @return   the element at the head of the queue and
//   *           null if the current thread has been interrupted.
//   */
//  public synchronized Object dequeue() {
//    Object result;
//
//    while (true) {
//      result = removeFirst();
//      if (result != null) {
//        if (pushWillWait > 0) {
//          synchronized (pushMonitor) {
//            if (pushWaiting > 0) {
//              pushMonitor.notify();
//            } else {
//              id++;
//            }
//          }
//        }
//        break;
//      } else {
//        popWaiting++;
//        try {
//          this.wait();
//        } catch (InterruptedException _) {
//          break;
//        } finally {
//          popWaiting--;
//        }
//      }
//    }
//
//    return result;
//  }
//
//  /**
//   * Removes the object at the head of this queue and returns it.<p>
//   *
//   * If the queue is empty current thread is either blocked until an
//   element is enqueued or a thread
//   * is interrupted or the time is elpased.<p>
//   *
//   * @param   timeout maximum wait timeout.
//   * @return   the element at the head of the queue and
//   *           null if the timeout elapsed.
//   * @exception   InterruptedException  if a thread is interrupted
//   */
//  public synchronized Object dequeue(long timeout) throws
//      InterruptedException {
//    long startTime = System.currentTimeMillis();
//    Object result;
//
//    while (true) {
//      result = removeFirst();
//      if (result != null) {
//        if (pushWillWait > 0) {
//          synchronized (pushMonitor) {
//            if (pushWaiting > 0) {
//              pushMonitor.notify();
//            } else {
//              id++;
//            }
//          }
//        }
//        break;
//      } else {
//        if (timeout > (System.currentTimeMillis() - startTime)) {
//          popWaiting++;
//          try {
//            this.wait();
//          } finally {
//            popWaiting--;
//          }
//        } else {
//          break;
//        }
//      }
//    }
//
//    return result;
//  }
//
//  public boolean enqueueForThreadWithoutWait(Object item) {
//    boolean result = false;
//
//    synchronized (this) {
//      if (size() < popWaiting) {//be sure there is a thread to handle  the request
//        result = addLast(item) != null;
//        if (result) {
//          if (popWaiting > 0) {
//            this.notify();
//          }
//        }
//      }
//    }
//
//    return result;
//  }
//
//  /**
//   * Enqueues an item at the tail of this queue.<p>
//   *
//   * If the queue is full current thread is either blocked until an
//   element is dequeued or a thread
//   * is interrupted.<p>
//   *
//   * @param   item the object to be inserted.
//   * @return    true if element is successfuly enqueued,
//   *             false if the thread has been interrupted.
//   */
//  public boolean enqueue(Object item) {
//    boolean result;
//    boolean firstTime = true;
//    boolean exit = false;
//    int localId = 0;
//
//    while (true) {
//      synchronized (this) {
//        if (!firstTime) {
//          pushWillWait--;
//        }
//
//        result = addLast(item) != null;
//        if (result) {
//          if (popWaiting > 0) {
//            this.notify();
//          }
//          break;
//        } else {
//          localId = this.id;
//          pushWillWait++;
//          firstTime = false;
//        }
//      }
//
//      synchronized (pushMonitor) {
//        if (localId == id) {
//          try {
//            pushWaiting++;
//            pushMonitor.wait();
//          } catch (InterruptedException e) {
//            exit = true;
//          } finally {
//            pushWaiting--;
//          }
//        }
//      }
//
//      if (exit) {
//        synchronized (this) {
//          pushWillWait--;
//        }
//        break;
//      }
//    }
//
//    return result;
//  }
//
//  public boolean enqueueNoWait(Object item) {
//    boolean result = false;
//
//    synchronized (this) {
//      if (popWaiting > this.size) {
//        result = addLast(item) != null;
//        if (result) {
//          if (popWaiting > 0) {
//            this.notify();
//          }
//        }
//      }
//    }
//
//    return result;
//  }
//
//  /**
//   * Enqueues an item at the tail of this queue.
//   *
//   * If the queue is full current thread is either blocked until an
//   element is dequeued or a thread
//   * is interrupted or the time is elpased.
//   *
//   * @param   item the object to be inserted.
//   * @param   timeout max wait timeout
//   * @return  true if element is successfuly queued,
//   *          false timeout has elapsed.
//   * @exception   InterruptedException  if a thread is interrupted
//   */
//  public boolean enqueue(Object item, long timeout) throws
//      InterruptedException {
//    long startTime = System.currentTimeMillis();
//    boolean result;
//    boolean firstTime = true;
//    boolean exit = false;
//    InterruptedException localExc = null;
//    int localId = 0;
//
//    while (true) {
//      synchronized (this) {
//        if (!firstTime) {
//          pushWillWait--;
//        }
//
//        result = addLast(item) != null;
//        if (result) {
//          if (popWaiting > 0) {
//            this.notify();
//          }
//          break;
//        } else {
//          localId = this.id;
//          pushWillWait++;
//          firstTime = false;
//        }
//      }
//
//      synchronized (pushMonitor) {
//        if (timeout > (System.currentTimeMillis() - startTime)) {
//          if (localId == id) {
//            try {
//              pushWaiting++;
//              pushMonitor.wait();
//            } catch (InterruptedException e) {
//              localExc = e;
//              exit = true;
//            } finally {
//              pushWaiting--;
//            }
//          }
//        } else {
//          exit = true;
//        }
//      }
//
//      if (exit) {
//        synchronized (this) {
//          pushWillWait--;
//        }
//        if (localExc != null) {
//          throw localExc;
//        }
//        break;
//      }
//    }
//
//    return result;
//  }
//
//  public int getWaitingDequeueThreadsCount() {
//    synchronized (this) {
//      return popWaiting;
//    }
//  }
//
//  public int getWaitingEnqueueThreadsCount() {
//    synchronized (pushMonitor) {
//      return pushWaiting;
//    }
//  }
//
//  public int size() {
//    synchronized (this) {
//      return this.sizeInner();
//    }
//  }
////
////
////  ///////////////////////////////////////////////
////  //LinkedList
////  ///////////////////////////////////////////////
////
////  class Node {
////    Object data;
////    Node next;
////  }
////
////  Node head, tail;
////  int size = 0;
////  int limit = 0;
////
////  public Object getFirst() {
////    if (head == null) {
////      return null;
////    }
////
////    return head.data;
////  }
////
////
////  public Object removeFirst() {
////    if (head == null) {
////      return null;
////    }
////
////    Object result = head.data;
////
////    if (head.next == null) {
////      head = tail = null;
////    } else {
////      head = head.next;
////    }
////
////    size--;
////    return result;
////  }
////
////  public Object addLast(Object item) {
////    if (size == limit) {
////      return null;
////    }
////
////    Node n = new Node();
////    n.data = item;
////
////    if (tail == null) {
////      head = tail = n;
////    } else {
////      tail.next = n;
////      tail = tail.next;
////    }
////
////    size++;
////    return tail.data;
////  }
////
////  public int sizeInner() {
////    return size;
////  }
////
////  public int getLimit() {
////    return size;
////  }
////
////  public boolean setLimit(int newSize) {
////    if (newSize < size) {
////      return false;
////    } else {
////      limit = newSize;
////      return true;
////    }
////  }
//
//}
//
//
///*
// * Copyright (c) 2000 by SAP AG, Walldorf.,
// * url: http://www.sap.com
// * All rights reserved.
//  *
// * This software is the confidential and proprietary information
// * of SAP AG, Walldorf. You shall not disclose such Confidential
// * Information and shall use it only in accordance with the terms
// * of the license agreement you entered into with SAP.
// */
//
//class WaitQueue4 {
//
//  private transient int pushWaiting = 0;
//  private transient int popWaiting = 0;
//  private transient int limit = 0;
//  private Vector tasks = null;
//  private int size = 0;
//
//
//  public WaitQueue4() {
//    this(0);
//  }
//
//  public WaitQueue4(int limit) {
//    this.limit = limit;
//    tasks = new Vector(limit);
//  }
//
//  public Object dequeue() {
//    synchronized (this) {
//      while (true) {
//        if (size == 0) {
//          try {
//            popWaiting++;
//            this.wait();
//          } catch (InterruptedException e) {
//            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
//            //todo
//          }
//        } else {
//          Object result = tasks.remove(0);
//          size--;
//          if (pushWaiting > 0) {
//            notify();
//            pushWaiting--;
//          }
//          return result;
//        }
//      }
//    }
//  }
//
//  public boolean enqueue(Object item) {
//    synchronized (this) {
//      while (true) {
//        if (size == limit) {
//          try {
//            pushWaiting++;
//            this.wait();
//          } catch (InterruptedException e) {
//            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
//          }
//        } else {
//          boolean result = tasks.add(item);
//          if (result) {
//            size++;
//            if (popWaiting > 0) {
//              notify();
//              popWaiting--;
//            }
//            return result;
//          } else {
//            System.out.println("we have a queue problem ! ");
//          }
//        }
//      }
//    }
//  }
//
//  public boolean enqueueNoWait(Object item) {
//    synchronized (this) {
//      if (size == limit) {
//        return false;
//      } else {
//        if (popWaiting > size) {
//          boolean result = tasks.add(item);
//          if (result) {
//            size++;
//            if (popWaiting > 0) {
//              notify();
//              popWaiting--;
//            }
//            return result;
//          } else {
//            return false;
//          }
//        } else {
//          return false;
//        }
//      }
//    }
//  }
//
//  public int getWaitingDequeueThreadsCount() {
//    synchronized (this) {
//      return popWaiting;
//    }
//  }
//
//  public int getWaitingEnqueueThreadsCount() {
//    synchronized (this) {
//      return pushWaiting;
//    }
//  }
//
//  public int size() {
//    synchronized (this) {
//      return size;
//    }
//  }
//
//  public int getLimit() {
//    synchronized (this) {
//      return limit;
//    }
//  }
//
//  public void setLimit(int newLimit) {
//    synchronized (this) {
//      System.out.println("ATTENTIONNNNNNNNNNNNNNNN");
//      this.limit = newLimit;
//    }
//  }
//
//
//}



