package com.sap.engine.lib.util.concurrent;

/**
 * Standard mutex structure with the additional functionality, for a certain
 * thread to acquire the lock with priority, making sure it'll go before the
 * others waiting for it.
 *
 * @author Krasimir Semerdzhiev (krasimir.semerdzhiev@sap.com)
 * @version 6.40
 */
public class PriorityMutex extends Mutex  {

 /*
  * Monitor for the priority threads to synchronize on. All the others will synchronize on the
  * objects' local monitor.
  */
  Object priorityMonitor = new Object();

 /*
  * Counter for waitning priority threads.
  */
  protected int priorityWaiting = 0;


/**
 * Acquire method for threads with high priority - they're guaranteed that once the
 * mutex is freed - they'll get it before all the others waiting (if any).
 *
 * @throws InterruptedException thrown if the thread is interrupted while waiting to lock.
 */
  public void acquireWithPriority() throws InterruptedException {
	if (Thread.interrupted()) {
	  throw new InterruptedException();
	}
	synchronized (priorityMonitor) {
	  priorityWaiting++;
	  for (;;) {
		synchronized(this) {
		  if (!isInUse) {
			isInUse = true;
			priorityWaiting--;
			return;
		  }
		}
		try {
		  priorityMonitor.wait();
		} catch (InterruptedException e) {
		  priorityWaiting--;
		  priorityMonitor.notify();
		  throw e;
		}
	  }
	}
  }

/**
 * Release method is overriden, to ensure the priority threads will get the lock first.
 *
 */
  public void release() {
	synchronized (priorityMonitor) {
	  if (priorityWaiting > 0) {
		synchronized (this) {
		  isInUse = false;
		}
		priorityMonitor.notify();
	  } else {
		super.release();
	  }
	}
  }

}