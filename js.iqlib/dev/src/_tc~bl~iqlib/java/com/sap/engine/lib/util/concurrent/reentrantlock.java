package com.sap.engine.lib.util.concurrent;

public class ReentrantLock implements Sync {

  protected Thread ownerThread = null;
  protected long holdsCount = 0;

  public void acquire() throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    Thread caller = Thread.currentThread();
    synchronized (this) {
      if (caller == ownerThread) {
        ++holdsCount;
      } else {
        try {
          while (ownerThread != null) {
            wait();
          }
          ownerThread = caller;
          holdsCount = 1;
        } catch (InterruptedException ex) {
          notify();
          throw ex;
        }
      }
    }
  }

  public boolean attempt(long msecs) throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    Thread caller = Thread.currentThread();
    synchronized (this) {
      if (caller == ownerThread) {
        ++holdsCount;
        return true;
      } else if (ownerThread == null) {
        ownerThread = caller;
        holdsCount = 1;
        return true;
      } else if (msecs <= 0) {
        return false;
      } else {
        long waitTime = msecs;
        long start = System.currentTimeMillis();
        try {
          for (;;) {
            wait(waitTime);

            if (caller == ownerThread) {
              ++holdsCount;
              return true;
            } else if (ownerThread == null) {
              ownerThread = caller;
              holdsCount = 1;
              return true;
            } else {
              waitTime = msecs - (System.currentTimeMillis() - start);
              if (waitTime <= 0) {
                return false;
              }
            }
          } 
        } catch (InterruptedException ex) {
          notify();
          throw ex;
        }
      }
    }
  }

  /**
   * Release the lock.
   * @exception Error thrown if not current owner of lock
   */
  public synchronized void release() {
    if (Thread.currentThread() != ownerThread) { //$JL-EXC$
      throw new Error("Illegal Lock usage");
    }

    if (--holdsCount == 0) {
      ownerThread = null;
      notify();
    }
  }

  /**
   * Release the lock N times. release(n) is
   * equivalent in effect to:
   * <pre>
   *   for (int i = 0; i < n; ++i) release();
   * </pre>
   * <p>
   * @exception Error thrown if not current owner of lock
   * or has fewer than N holds on the lock
   */
  public synchronized void release(long n) {
    if (Thread.currentThread() != ownerThread || n > holdsCount) { //$JL-EXC$
      throw new Error("Illegal Lock usage");
    }
    holdsCount -= n;

    if (holdsCount == 0) {
      ownerThread = null;
      notify();
    }
  }

  /**
   * Return the number of unreleased acquires performed
   * by the current thread.
   * Returns zero if current thread does not hold lock.
   */
  public synchronized long holds() {
    if (Thread.currentThread() != ownerThread) {
      return 0;
    }
    return holdsCount;
  }

}

