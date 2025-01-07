package com.sap.engine.lib.util.concurrent;

public class TimeoutSync implements Sync {

  protected final Sync syncGuard; // the adapted sync
  protected final long timeout; // timeout value

  /**
   * Create a TimeoutSync using the given Sync object, and
   * using the given timeout value for all calls to acquire.
   */
  public TimeoutSync(Sync sync, long time) {
    syncGuard = sync;
    timeout = time;
  }

  public void acquire() throws InterruptedException {
    if (!syncGuard.attempt(timeout)) {
      throw new TimeoutException(timeout);
    }
  }

  public boolean attempt(long msecs) throws InterruptedException {
    return syncGuard.attempt(msecs);
  }

  public void release() {
    syncGuard.release();
  }

}

