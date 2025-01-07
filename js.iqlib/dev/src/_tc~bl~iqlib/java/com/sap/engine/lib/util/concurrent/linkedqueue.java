package com.sap.engine.lib.util.concurrent;

public class LinkedQueue implements Channel {

  /**
   * Dummy header node of list. The first actual node, if it exists, is always
   * at headNode.next. After each take, the old first node becomes the head.
   */
  protected LinkedNode headNode;
  /**
   * Helper monitor for managing access to last node.
   */
  protected final Object putLockObject = new Object();
  /**
   * The last node of list. Put() appends to list, so modifies lastNode
   */
  protected LinkedNode lastNode;
  /**
   * The number of threads waiting for a take.
   * Notifications are provided in put only if greater than zero.
   * The bookkeeping is worth it here since in reasonably balanced
   * usages, the notifications will hardly ever be necessary, so
   * the call overhead to notify can be eliminated.
   */
  protected int waitingForTake_ = 0;

  public LinkedQueue() {
    headNode = new LinkedNode(null);
    lastNode = headNode;
  }

  /** Main mechanics for put/offer  */
  protected void insert(Object x) {
    synchronized (putLockObject) {
      LinkedNode p = new LinkedNode(x);
      synchronized (lastNode) {
        lastNode.next = p;
        lastNode = p;
      }
      if (waitingForTake_ > 0) {
        putLockObject.notify();
      }
    }
  }

  /** Main mechanics for take/poll  */
  protected synchronized Object extract() {
    synchronized (headNode) {
      Object x = null;
      LinkedNode first = headNode.next;

      if (first != null) {
        x = first.value;
        first.value = null;
        headNode = first;
      }

      return x;
    }
  }

  public void put(Object x) throws InterruptedException {
    if (x == null) {
      throw new IllegalArgumentException();
    }
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    insert(x);
  }

  public boolean offer(Object x, long msecs) throws InterruptedException {
    if (x == null) {
      throw new IllegalArgumentException();
    }
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    insert(x);
    return true;
  }

  public Object take() throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    // try to extract. If fail, then enter wait-based retry loop
    Object x = extract();

    if (x != null) {
      return x;
    } else {
      synchronized (putLockObject) {
        try {
          ++waitingForTake_;

          for (;;) {
            x = extract();

            if (x != null) {
              --waitingForTake_;
              return x;
            } else {
              putLockObject.wait();
            }
          } 
        } catch (InterruptedException ex) {
          --waitingForTake_;
          putLockObject.notify();
          throw ex;
        }
      }
    }
  }

  public Object peek() {
    synchronized (headNode) {
      LinkedNode first = headNode.next;
      if (first != null) {
        return first.value;
      } else {
        return null;
      }
    }
  }

  public boolean isEmpty() {
    synchronized (headNode) {
      return headNode.next == null;
    }
  }

  public Object poll(long msecs) throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    Object x = extract();

    if (x != null) {
      return x;
    } else {
      synchronized (putLockObject) {
        try {
          long waitTime = msecs;
          long start = (msecs <= 0) ? 0 : System.currentTimeMillis();
          ++waitingForTake_;

          for (;;) {
            x = extract();

            if (x != null || waitTime <= 0) {
              --waitingForTake_;
              return x;
            } else {
              putLockObject.wait(waitTime);
              waitTime = msecs - (System.currentTimeMillis() - start);
            }
          } 
        } catch (InterruptedException ex) {
          --waitingForTake_;
          putLockObject.notify();
          throw ex;
        }
      }
    }
  }

}

