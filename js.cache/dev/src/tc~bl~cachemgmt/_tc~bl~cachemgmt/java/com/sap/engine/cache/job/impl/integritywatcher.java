/*
 * Created on 2004.9.15
 *
 */
package com.sap.engine.cache.job.impl;

import com.sap.engine.cache.util.dump.LogUtil;

/**
 * @author petio-p
 *
 */
public class IntegrityWatcher implements Runnable {

  private static final String THREAD_NAME = "Timeout Service Integrity Watcher Thread";
  // wait time 1 sec
  private static final long INTERVAL = 1000;
  // max expected wait time 1,5 sec
  private static final long EXPECTED = 1500;

  // run method work flag
  private boolean work = false;
  // queue hold TimeoutNode objects
  private PriorityQueue queue = null;
  // system time before wait
  private long beforeWaitTime = 0;
  // holds delta accumulation
  private long deltaAccumulation = 0;
  // integrity thread
  private Thread integrityThread = null;

  IntegrityWatcher(PriorityQueue queue) {
    this.queue = queue;
    integrityThread = new Thread(this);
    integrityThread.setDaemon(true);
    integrityThread.start();
    ensureThreadStart();
  }

  public void run() {
    integrityThread = Thread.currentThread();
    int priority = integrityThread.getPriority();
    String name = integrityThread.getName();
    try {
      // max priority is needed for exact delta time calculation
      integrityThread.setPriority(Thread.MAX_PRIORITY);
      integrityThread.setName(THREAD_NAME);
      synchronized (this) {
        beforeWaitTime = System.currentTimeMillis();
        work = true;
        this.notify();
        while (work) {
          try {
            this.wait(INTERVAL);
          } catch (InterruptedException e) {
            LogUtil.logTInfo(e);
            //Excluding this catch block from JLin $JC-EXC$ - performance reasons
            //Please do not remove this comment!
            continue;
          } finally {
            beforeWaitTime = accumulateDelta();
          }
        }
      }
    } finally {
      integrityThread.setPriority(priority);
      integrityThread.setName(name);
    }
  }

  /**
   * Returns current time and recalculate all TimeoutNode objects in queue
   * according deltaAccumulation. This method is used instead System.currentTimeMillis()
   * and the returned time is consistent in interval [-EXPECTED, EXPECTED].
   * This method can change all TimeoutNode.nextCallTime values and it must not be
   * used directly in expressions containing nextCallTime!
   *
   * @return current time
   */
  final synchronized long getCurrentTimeMillis() {
    long currentTime = accumulateDelta();
    if (deltaAccumulation != 0) {
//        System.out.println("Recalculating queue with delta = " + deltaAccumulation);
      queue.recalculateIntervals(deltaAccumulation);
      // reset deltaAccumulation and beforeWaitTime
      deltaAccumulation = 0;
      beforeWaitTime = currentTime;
    }
    return currentTime;
  }

  // stop integrity thread
  final void stop() {
    synchronized (this) {
      work = false;
      integrityThread.interrupt();
    }
  }

  // block calling thread until integrityThread is initialized
  private final void ensureThreadStart() {
    synchronized (this) {
      while (!work) {
        try {
          this.wait();
        } catch (InterruptedException e) { //Excluding this catch block from JLin $JC-EXC$ - performance reasons
          LogUtil.logT(e);
          //Please do not remove this comment!
          continue;
        }
      }
    }
  }

  // return current time, accumulate delta if needed
  private final long accumulateDelta() {
    long currentTime = System.currentTimeMillis();
    long delta = currentTime - beforeWaitTime;
    if (((delta < 0) ? -delta : delta) > EXPECTED) {
      deltaAccumulation += delta;
    }
    return currentTime;
  }

}
