package com.sap.engine.lib.util.concurrent;

public class Latch implements Sync {

  protected boolean isLatched = false;

  /*
   This could use double-check, but doesn't.
   If the latch is being used as an indicator of
   the presence or state of an object, the user would
   not necessarily get the memory barrier that comes with synch
   that would be needed to correctly use that object. This
   would lead to errors that users would be very hard to track down. So, to
   be conservative, we always use synch.
   */
  public void acquire() throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    synchronized (this) {
      while (!isLatched) {
        wait();
      }
    }
  }

  public boolean attempt(long msecs) throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    synchronized (this) {
      if (isLatched) {
        return true;
      } else if (msecs <= 0) {
        return false;
      } else {
        long waitTime = msecs;
        long start = System.currentTimeMillis();

        for (;;) {
          wait(waitTime);

          if (isLatched) {
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

  /** Enable all current and future acquires to pass  */
  public synchronized void release() {
    isLatched = true;
    notifyAll();
  }

}

