package com.sap.engine.lib.util.concurrent;

public class NullSync implements Sync {

  public synchronized void acquire() throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
  }

  public synchronized boolean attempt(long msecs) throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    return true;
  }

  public synchronized void release() {

  }

}

