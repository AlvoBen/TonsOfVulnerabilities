/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.lib.util.cache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

/**
 * @author Iliyan Nenov, ilian.nenov@sap.com
 * @version SAP J2EE Engine 6.30
 */
public class CacheGroup extends ReferenceQueue implements Runnable {

  private Thread thread = null;

  /* All garbage collected objects are put in this queue */
  protected ReferenceQueue queue = new ReferenceQueue();

  /* If true the thread must be stopped else the thread is running*/
  protected boolean stopFlag = false;

  /**
   * Overrides poll() method from the ReferenceQueue.
   * Ths method is a proxy to the private ReferenceQueue in the class
   */
  public Reference poll() {
    return queue.poll();
  }

  /**
   * Overides remove() method from the ReferenceQueue
   * Ths method is a proxy to the private ReferenceQueue in the class
   */
  public Reference remove() throws InterruptedException {
    return queue.remove();
  }

  /**
   * Overides remove(long timeout) method from the ReferenceQueue
   * Ths method is a proxy to the private ReferenceQueue in the class
   */
  public Reference remove(long timeout)  throws InterruptedException {
    return queue.remove(timeout);
  }

  /**
   * Stops this cache group. This method should be invoked when you don't
   * intend to use the cache anymore. if a caceh operation is performed
   * after the stopping of the group then the cache system will throw
   * CacheGroupException
   */
  public void stop() {
    stopFlag = true;
    queue = null;
    if (thread != null) {
      thread.interrupt();
    }
  }

  /**
   * One thread for the cache group
   */
  public void run() {
    thread = Thread.currentThread();
    thread.setContextClassLoader(this.getClass().getClassLoader());
    while (!stopFlag) {
      try {
        SoftValue value = (SoftValue) queue.remove();
        synchronized (value.hashMap) {
          Object removingKey = value.key;
          SoftValue getted = (SoftValue)value.hashMap.get(removingKey); // NullPointer: remove is invoked before this thread is notified
          if (getted != null && getted.get() == null) { // simulate sinchronization with the cache
            if (value.hashMap.cacheListener != null) {
              value.hashMap.cacheListener.removedByGarbageCollector(removingKey);
            }
            value.hashMap.remove(removingKey); // in fact acts as synchronization
          }
        }
      } catch (InterruptedException inEx) {
        stopFlag=true;
          // Not very nice of you but I'll tolerate this
      }
    }
  }

}