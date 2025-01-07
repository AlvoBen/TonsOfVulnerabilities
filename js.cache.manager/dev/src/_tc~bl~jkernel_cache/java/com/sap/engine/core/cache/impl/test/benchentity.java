/*
 * Created on 2005.2.9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.core.cache.impl.test;

import com.sap.util.cache.CacheRegion;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface BenchEntity extends Runnable {
  
  public void init(CacheRegion region);
  
  public BenchResult execute(int period, int threads);
  
  public void close();

}
