/*
 * Created on 2005.2.9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.core.cache.impl.test.bench;

import com.sap.engine.core.cache.impl.CacheManagerImpl;
import com.sap.engine.core.cache.impl.test.BenchEntity;
import com.sap.engine.core.cache.impl.test.BenchResult;
import com.sap.util.cache.CacheRegion;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class BenchAdaptor implements BenchEntity {
  
  protected CacheRegion region = null;
  
  private Object sync = new Object();
  
  private volatile int counter = 0;
  
  private boolean benchWork;
  
  public void init(CacheRegion region) {
    this.region = region;
    counter = 0;
    benchWork = true;
  }

  public void run() {
    while (benchWork) {
      Thread.yield();
      singleOperation();
      counter++;
    }
  }
  
  public BenchResult execute(int period, int threads) {
    Thread[] spawns = new Thread[threads];
    for (int i = 0; i < threads; i++) {
      spawns[i] = new Thread(this);
      try {
        // artifitial wait - needed to avoid parallelism artefacts
        Thread.sleep((int)(1 + 10 * Math.random()));
      } catch (InterruptedException e) {
        CacheManagerImpl.traceT(e);
      }
      spawns[i].start();
    }
    long elapsedTime = System.currentTimeMillis();
    synchronized (sync) { try { Thread.sleep(period); } catch (InterruptedException e) {
        CacheManagerImpl.traceT(e);
      }
    }
    elapsedTime = System.currentTimeMillis() - elapsedTime;
    benchWork = false;
    synchronized (sync) { try { Thread.sleep(500); } catch (InterruptedException e) {
        CacheManagerImpl.traceT(e);
      }
    }
    BenchResult result = new BenchResult();
    result.putValue(getName(), (int)((long)counter * (long)getFactor() * (long)1000 / (long)elapsedTime));
    return result;
  }

  public void close() {
    region = null;
  }

  public abstract void singleOperation();
  
  public abstract String getName();
  
  public abstract int getFactor();
  
}
