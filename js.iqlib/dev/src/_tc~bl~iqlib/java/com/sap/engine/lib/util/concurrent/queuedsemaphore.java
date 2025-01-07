package com.sap.engine.lib.util.concurrent;

public abstract class QueuedSemaphore extends Semaphore {

  protected final WaitQueue waitQueue;

  QueuedSemaphore(WaitQueue q, long initialPermits) {
    super(initialPermits);
    waitQueue = q;
  }

  public void acquire() throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    if (precheck()) {
      return;
    }
    WaitQueue.WaitNode w = new WaitQueue.WaitNode();
    w.doWait(this);
  }

  public boolean attempt(long msecs) throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    if (precheck()) {
      return true;
    }
    if (msecs <= 0) {
      return false;
    }
    WaitQueue.WaitNode w = new WaitQueue.WaitNode();
    return w.doTimedWait(this, msecs);
  }

  protected synchronized boolean precheck() {
    boolean pass = (permitsCount > 0);
    if (pass) {
      --permitsCount;
    }
    return pass;
  }

  protected synchronized boolean recheck(WaitQueue.WaitNode w) {
    boolean pass = (permitsCount > 0);
    if (pass) {
      --permitsCount;
    } else {
      waitQueue.insert(w);
    }
    return pass;
  }

  protected synchronized WaitQueue.WaitNode getSignallee() {
    WaitQueue.WaitNode w = waitQueue.extract();
    if (w == null) {
      ++permitsCount; // if none, inc permits for new arrivals

    }
    return w;
  }

  public void release() {
    for (;;) {
      WaitQueue.WaitNode w = getSignallee();
      if (w == null) {
        return; // no one to signal

      }
      if (w.signal()) {
        return; // notify if still waiting, else skip

      }
    } 
  }

  /**
   * Release N permits
   */
  public void release(long n) {
    if (n < 0) {
      throw new IllegalArgumentException("Negative argument");
    }
    for (long i = 0; i < n; ++i) {
      release(); 
    }
  }

  /**
   * Base class for internal queue classes for semaphores, etc.
   * Relies on subclasses to actually implement queue mechanics
   */
  protected static abstract class WaitQueue {

    protected abstract void insert(WaitNode w); // assumed not to block

    protected abstract WaitNode extract(); // should return null if empty

    protected static class WaitNode {

      boolean waiting = true;
      WaitNode next = null;

      protected synchronized boolean signal() {
        boolean signalled = waiting;

        if (signalled) {
          waiting = false;
          notify();
        }

        return signalled;
      }

      protected synchronized boolean doTimedWait(QueuedSemaphore sem, long msecs) throws InterruptedException {
        if (sem.recheck(this) || !waiting) {
          return true;
        } else if (msecs <= 0) {
          waiting = false;
          return false;
        } else {
          long waitTime = msecs;
          long start = System.currentTimeMillis();
          try {
            for (;;) {
              wait(waitTime);

              if (!waiting) // definitely signalled
              {
                return true;
              } else {
                waitTime = msecs - (System.currentTimeMillis() - start);

                if (waitTime <= 0) { //  timed out
                  waiting = false;
                  return false;
                }
              }
            } 
          } catch (InterruptedException ex) {
            if (waiting) { // no notification
              waiting = false; // invalidate for the signaller
              throw ex;
            } else { // thread was interrupted after it was notified
              Thread.currentThread().interrupt();
              return true;
            }
          }
        }
      }

      protected synchronized void doWait(QueuedSemaphore sem) throws InterruptedException {
        if (!sem.recheck(this)) {
          try {
            while (waiting) {
              wait();
            }
          } catch (InterruptedException ex) {
            if (waiting) { // no notification
              waiting = false; // invalidate for the signaller
              throw ex;
            } else { // thread was interrupted after it was notified
              Thread.currentThread().interrupt();
              return;
            }
          }
        }
      }

    }

  }

}

