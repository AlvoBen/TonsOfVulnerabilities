/*
 * Created on 2005.2.10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.core.cache.impl.test.bench;

import com.sap.engine.core.cache.impl.test.BenchEntity;
import com.sap.engine.core.cache.impl.test.BenchResult;
import com.sap.util.cache.CacheRegion;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HashtableCollectiveEntity implements BenchEntity {

  private CacheRegion region;
  private int period;
  private int threads;

  public void init(CacheRegion region) {
    this.region = region;
  }

  public synchronized BenchResult execute(int period, int threads) {
    BenchResult result = new BenchResult();
    this.period = period;
    this.threads = threads;
    BenchResult singleResult = null;

    // control
    singleResult = singleEntity(new ControlEntity());
    int control = singleResult.getSingleValue();
    result.addAll(singleResult);
    
    // hashtable get
    singleResult = singleEntity(new HashtableGetEntity());
    int hashtableGet = singleResult.getSingleValue();
    result.addAll(singleResult);
    
    // hashtable put
    singleResult = singleEntity(new HashtablePutEntity());
    int hashtablePut = singleResult.getSingleValue();
    result.addAll(singleResult);
    
    // hashtable put + remove
    singleResult = singleEntity(new HashtablePutRemoveEntity());
    int hashtablePutRemove = singleResult.getSingleValue();
    result.addAll(singleResult);
    
    // hashtable remove - this is calculated using put and put + remove velocities
    int hashtableRemove = (int) (((long)hashtablePutRemove * (long)hashtablePut) / ((long)hashtablePut - (long)hashtablePutRemove));
    result.putValue("Hashtable Remove Operations / Sec", hashtableRemove);
    
    return result;
  }
  
  private BenchResult singleEntity(BenchEntity entity) {
    entity.init(region);
    BenchResult singleResult = entity.execute(period, threads);
    entity.close();
    return singleResult;
  }

  public void close() {
  }

  public void run() {
  }

}
