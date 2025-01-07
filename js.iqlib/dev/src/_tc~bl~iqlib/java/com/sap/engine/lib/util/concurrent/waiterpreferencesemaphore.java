package com.sap.engine.lib.util.concurrent;

public final class WaiterPreferenceSemaphore extends Semaphore {

  /**
   * Create a Semaphore with the given initial number of permits.
   */
  public WaiterPreferenceSemaphore(long initial) {
    super(initial);
  }

  /**
   * Number of waiting threads
   */
  protected long waitsCount = 0;

  public void acquire() throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    synchronized (this) {
      /*
       Only take if there are more permits than threads waiting
       for permits. This prevents infinite overtaking.
       */
      if (permitsCount > waitsCount) {
        --permitsCount;
        return;
      } else {
        ++waitsCount;
        try {
          for (;;) {
            wait();

            if (permitsCount > 0) {
              --waitsCount;
              --permitsCount;
              return;
            }
          } 
        } catch (InterruptedException ex) {
          --waitsCount;
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
    synchronized (this) {
      if (permitsCount > waitsCount) {
        --permitsCount;
        return true;
      } else if (msecs <= 0) {
        return false;
      } else {
        ++waitsCount;
        long startTime = System.currentTimeMillis();
        long waitTime = msecs;
        try {
          for (;;) {
            wait(waitTime);

            if (permitsCount > 0) {
              --waitsCount;
              --permitsCount;
              return true;
            } else { // got a time-out or false-alarm notify
              waitTime = msecs - (System.currentTimeMillis() - startTime);

              if (waitTime <= 0) {
                --waitsCount;
                return false;
              }
            }
          } 
        } catch (InterruptedException ex) {
          --waitsCount;
          notify();
          throw ex;
        }
      }
    }
  }

  public synchronized void release() {
    ++permitsCount;
    notify();
  }

  /**
   * Release N permits
   */
  public synchronized void release(long n) {
    permitsCount += n;
    for (long i = 0; i < n; ++i) {
      notify(); 
    }
  }

}

