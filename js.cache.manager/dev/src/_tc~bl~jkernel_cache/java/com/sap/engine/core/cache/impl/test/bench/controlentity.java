/*
 * Created on 2005.2.9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.core.cache.impl.test.bench;

import com.sap.engine.core.cache.impl.CacheManagerImpl;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ControlEntity extends BenchAdaptor {

  public void singleOperation() {
    synchronized (this) {
      try {
        for (int i = 0; i < 10; i++) wait(10);
      } catch (InterruptedException e) {
        CacheManagerImpl.traceT(e);
      }
    }
  }

  public String getName() {
    return "Wait(10) / Sec";
  }

  public int getFactor() {
    return 10;
  }
  
}
