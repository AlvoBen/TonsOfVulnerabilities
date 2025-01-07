package com.sap.engine.lib.util.concurrent;

public class LayeredSync implements Sync {

  protected final Sync outerSync;
  protected final Sync innerSync;

  /**
   * Create a LayeredSync managing the given outer and inner Sync
   * objects
   */
  public LayeredSync(Sync outer, Sync inner) {
    outerSync = outer;
    innerSync = inner;
  }

  public void acquire() throws InterruptedException {
    outerSync.acquire();
    try {
      innerSync.acquire();
    } catch (InterruptedException ex) {
      outerSync.release();
      throw ex;
    }
  }

  public boolean attempt(long msecs) throws InterruptedException {
    long start = (msecs <= 0) ? 0 : System.currentTimeMillis();
    long waitTime = msecs;

    if (outerSync.attempt(waitTime)) {
      try {
        if (msecs > 0) {
          waitTime = msecs - (System.currentTimeMillis() - start);
        }

        if (innerSync.attempt(waitTime)) {
          return true;
        } else {
          outerSync.release();
          return false;
        }
      } catch (InterruptedException ex) {
        outerSync.release();
        throw ex;
      }
    } else {
      return false;
    }
  }

  public void release() {
    innerSync.release();
    outerSync.release();
  }

}

