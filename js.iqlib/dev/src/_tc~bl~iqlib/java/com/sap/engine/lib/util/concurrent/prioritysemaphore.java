package com.sap.engine.lib.util.concurrent;

public class PrioritySemaphore extends QueuedSemaphore {

  /**
   * Create a Semaphore with the given initial number of permits.
   * Using a seed of one makes the semaphore act as a mutual exclusion lock.
   * Negative seeds are also allowed, in which case no acquires will proceed
   * until the number of releases has pushed the number of permits past 0.
   */
  public PrioritySemaphore(long initialPermits) {
    super(new PriorityWaitQueue(), initialPermits);
  }

  protected static class PriorityWaitQueue extends WaitQueue {

    /**
     * An array of wait queues, one per priority
     */
    protected final FIFOSemaphore.FIFOWaitQueue[] cells = new FIFOSemaphore.FIFOWaitQueue[Thread.MAX_PRIORITY - Thread.MIN_PRIORITY + 1];
    /**
     * The index of the highest priority cell that may need to be signalled,
     * or -1 if none. Used to minimize array traversal.
     */
    protected int maxIndex_ = -1;

    protected PriorityWaitQueue() {
      for (int i = 0; i < cells.length; ++i) {
        cells[i] = new FIFOSemaphore.FIFOWaitQueue(); 
      }
    }

    protected void insert(WaitNode w) {
      int idx = Thread.currentThread().getPriority() - Thread.MIN_PRIORITY;
      cells[idx].insert(w);
      if (idx > maxIndex_) {
        maxIndex_ = idx;
      }
    }

    protected WaitNode extract() {
      for (;;) {
        int idx = maxIndex_;
        if (idx < 0) {
          return null;
        }
        WaitNode w = cells[idx].extract();
        if (w != null) {
          return w;
        } else {
          --maxIndex_;
        }
      } 
    }

  }

}

