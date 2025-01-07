package com.sap.engine.lib.util.concurrent;

public class Mutex implements Sync {

  /** The lock status  */
  protected boolean isInUse = false;

  public void acquire() throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    synchronized (this) {
      try {
        while (isInUse) {
          wait();
        }
        isInUse = true;
      } catch (InterruptedException ex) {
        notify();
        throw ex;
      }
    }
  }

  public synchronized void release() {
    isInUse = false;
    notify();
  }

  public boolean attempt(long msecs) throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    synchronized (this) {
      if (!isInUse) {
        isInUse = true;
        return true;
      } else if (msecs <= 0) {
        return false;
      } else {
        long waitTime = msecs;
        long start = System.currentTimeMillis();
        try {
          for (;;) {
            wait(waitTime);

            if (!isInUse) {
              isInUse = true;
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

}

