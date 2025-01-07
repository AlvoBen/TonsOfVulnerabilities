package com.sap.engine.services.rmi_p4.garbagecollector.finalize;

/**
 * Copyright: Copyright (c) 2003
 * Company: SAP AG - Sap Labs Bulgaria
 *
 * @author Ivan Atanassov
 * @version 7.1
 */
class FinalizeMessanger implements Runnable {
  private FinalizeInformer informer;
  boolean isWaitingMessage;
  private volatile long timeoutStartTime;
  private long timeout;
  private ThreadWrapper currentThread;

  public FinalizeMessanger(FinalizeInformer informer, int timeout) {
    this.informer = informer;
    this.timeout = timeout;

    init();
  }

  private void init() {
    this.isWaitingMessage = false;
    this.timeoutStartTime = 0;
    this.currentThread = new ThreadWrapper(this);
    currentThread.setDaemon(true);
    currentThread.start();
  }

  public void run() {
    while (((ThreadWrapper) Thread.currentThread()).isRunning()) {
      informer.doWork(this);
      setTimeoutStartTime(0);
    }
  }

  public synchronized void setTimeoutStartTime(long startTime) {
    this.timeoutStartTime = startTime;
  }

  public synchronized boolean isExpiredTimeout() {
    if (timeoutStartTime != 0) {
      return ((timeoutStartTime + timeout) < System.currentTimeMillis());
    } else {
      return false;
    }
  }

  public synchronized void startInNewThread() {
    currentThread.stopIt();

    init();  //initiates in new thread
  }


  public void stop() {
    currentThread.stopIt();
  }
}
