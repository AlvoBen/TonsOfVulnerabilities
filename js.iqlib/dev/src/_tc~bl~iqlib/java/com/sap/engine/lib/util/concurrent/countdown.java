package com.sap.engine.lib.util.concurrent;

/**
 * Countdown structure. Can be used to arrange thread meeting at a certain point.
 */
public class CountDown implements Sync {

  protected final int initialCount;
  protected int countToRelease;

  /**
   * Create a new CountDown with given count value
   */
  public CountDown(int count) {
    countToRelease = initialCount = count;
  }

  /*
   This could use double-check, but doesn't out of concern
   for surprising effects on user programs stemming
   from lack of memory barriers with lack of synch.
   */
  public void acquire() throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    synchronized (this) {
      while (countToRelease > 0) {
        wait();
      }
    }
  }

  public boolean attempt(long msecs) throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    synchronized (this) {
      if (countToRelease <= 0) {
        return true;
      } else if (msecs <= 0) {
        return false;
      } else {
        long waitTime = msecs;
        long start = System.currentTimeMillis();

        for (;;) {
          wait(waitTime);

          if (countToRelease <= 0) {
            return true;
          } else {
            waitTime = msecs - (System.currentTimeMillis() - start);
            if (waitTime <= 0) {
              return false;
            }
          }
        } 
      }
    }
  }

  /**
   * Decrement the count.
   * After the initialCount'th release, all current and future
   * acquires will pass
   */
  public synchronized void release() {
    if (--countToRelease == 0) {
      notifyAll();
    }
  }

  /**
   * Return the initial count value
   */
  public int initialCount() {
    return initialCount;
  }

  /**
   * Return the current count value.
   * This is just a snapshot value, that may change immediately
   * after returning.
   */
  public synchronized int currentCount() {
    return countToRelease;
  }

}

