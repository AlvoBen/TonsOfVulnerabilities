package com.sap.engine.cache.test;

import com.sap.engine.cache.util.dump.DumpWriter;
import com.sap.engine.cache.util.dump.LogUtil;

/**
 * @author Petev, Petio, i024139
 */
public class ThreadsOnly {

  private ThreadsOnly() {
    TestRun[] runnables = new TestRun[2];
    for (int i = 0; i < runnables.length; i++) {
      runnables[i] = new TestRun(i, this);
    }
    for (int i = 0; i < runnables.length; i++) {
      new Thread(runnables[i]).start();
    }

    do {
      synchronized (this) {
        this.notify();
      }
    } while (TestRun.count > 0);
  }

  public static void main(String[] args) {
    new ThreadsOnly();
  }

  static class TestRun implements Runnable {

    private int id = -1;
    private Object sync = null;
    private static int count = 0;

    private TestRun(int id, Object sync) {
      this.id = id;
      this.sync = sync;
      count++;
    }

    public void run() {
      synchronized (sync) {
        try {
          do {
            sync.wait();
          } while (false);
        } catch (InterruptedException e) {
          LogUtil.logTInfo(e);
        }
      }
      DumpWriter.dump(id + " ok");
      count--;
    }

  }

}

