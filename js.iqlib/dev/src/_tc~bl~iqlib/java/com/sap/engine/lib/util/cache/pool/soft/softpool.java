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

package com.sap.engine.lib.util.cache.pool.soft;

import com.sap.engine.lib.util.PoolInstanceCreator;
import com.sap.engine.lib.util.PoolObjectWithCreator;

import java.lang.ref.ReferenceQueue;

/**
 * This is an implementation of object pool with weak part
 * The weak part consist of SoftReferences
 *
 * @author Iliyan Nenov, ilian.nenov@sap.com
 * @version SAP J2EE Engine 6.30
 */
public class SoftPool implements Runnable {

  protected static ReferenceQueue queue = new ReferenceQueue();
  private static Pool weakPool = new Pool();
  private PoolInstanceCreator objFactory = null;
  private Thread thread = null;
  private boolean interrupted = false;

  private int count = 0;

  private PoolObjectWithCreator stable = null;
  private int stableSize = 0;

  public SoftPool(int _stableSize, PoolInstanceCreator _objFactory) {
    stableSize = _stableSize;
    objFactory = _objFactory;
    stable = new PoolObjectWithCreator(objFactory);
  }

  public Object get() {

    if (count == 0) {
      boolean flag = true;
      while (flag && count <= stableSize) {
        PoolItem item = weakPool.get();
        if (item == null) {
          break;
        }
        Object result = item.get();
        stable.releaseObject(result);
        flag = weakPool.remove(item);
        count++;
      }

      if (count == 0) {
        return objFactory.newInstance();
      }
    }

    return stable.getObject();
  }

  public void put(Object obj) {
    if (count >= stableSize) { // stable is full
      weakPool.put(new PoolItem(obj));
    }

    stable.releaseObject(obj);
    count++;
  }

  public void stop() {
    interrupted = true;
    stable.freeMemory();
    thread.interrupt();
  }

  public void run() {
    thread = Thread.currentThread();
    Thread.currentThread().setName("SAP J2EE Engine|SoftPool GarbageCollector Daemon");
    while (true) {
      if (interrupted) {
        stable.freeMemory();
        weakPool = null;
        break;
      }

      try {
        PoolItem removed = (PoolItem) queue.remove();
        weakPool.remove(removed);
        count--;
      } catch (InterruptedException inEx) {
        interrupted = true;
//        inEx.printStackTrace();
      }
    }
  }

}