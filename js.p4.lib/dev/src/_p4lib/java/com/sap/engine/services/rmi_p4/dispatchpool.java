package com.sap.engine.services.rmi_p4;

/**
 * @author Georgi Stanev
 * @version 7.0
 */
public class DispatchPool {

  public static final int NORMAL_SIZE = 5;
  public static final int MAX_SIZE = 50;
  public static final int INCREASE_CAPACITY = 5;
  private DispatchImpl[] pool;
  private int current;
  private boolean stopped = false;

  public DispatchPool() {
    pool = new DispatchImpl[NORMAL_SIZE];
    Thread th;

    for (current = 0; current < (NORMAL_SIZE); current++) {
      pool[current] = new DispatchImpl(this);
      th = new Thread(pool[current]);
      th.setDaemon(true);
      th.start();
    }
  }

  public synchronized DispatchImpl getDispatch() {
    if (current <= 0) {
      return null;
    } else {
      return pool[--current];
    }
  }

  protected synchronized boolean returnInPool(DispatchImpl cc) {
    if (stopped) {
        return false;
    } else {
        if (current >= pool.length) {
          if (pool.length < MAX_SIZE) {
            DispatchImpl[] newPool = new DispatchImpl[(MAX_SIZE <= INCREASE_CAPACITY + pool.length) ? MAX_SIZE : (INCREASE_CAPACITY + pool.length)];
            System.arraycopy(pool, 0, newPool, 0, pool.length);
            pool = newPool;
            newPool = null;
          } else {
            return false;
          }
        }

        pool[current++] = cc;
        return true;
        }
  }

  public synchronized void stopWork() {
    stopped = true;
    for (int i = 0; i < pool.length; i++) {
      if (pool[i] != null) {
        synchronized (pool[i]) {
          pool[i].running = false;
          pool[i].notifyAll();
        }
      }
    }
  }
}

