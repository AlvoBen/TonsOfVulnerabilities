package com.sap.engine.lib.util.concurrent;

public class FIFOSemaphore extends QueuedSemaphore {

  /**
   * Create a Semaphore with the given initial number of permits.
   * Using a seed of one makes the semaphore act as a mutual exclusion lock.
   * Negative seeds are also allowed, in which case no acquires will proceed
   * until the number of releases has pushed the number of permits past 0.
   */
  public FIFOSemaphore(long initialPermits) {
    super(new FIFOWaitQueue(), initialPermits);
  }

  /**
   * Simple linked list queue used in FIFOSemaphore.
   * Methods are not synchronized; they depend on synch of callers
   */
  protected static class FIFOWaitQueue extends WaitQueue {

    protected WaitNode headNode = null;
    protected WaitNode tailNode = null;

    protected void insert(WaitNode w) {
      if (tailNode == null) {
        headNode = tailNode = w;
      } else {
        tailNode.next = w;
        tailNode = w;
      }
    }

    protected WaitNode extract() {
      if (headNode == null) {
        return null;
      } else {
        WaitNode w = headNode;
        headNode = w.next;
        if (headNode == null) {
          tailNode = null;
        }
        w.next = null;
        return w;
      }
    }

  }

}

