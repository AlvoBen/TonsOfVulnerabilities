/*
 * Created on 2005.2.9
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
public class CollectiveEntity implements BenchEntity {
  
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

    // get
    singleResult = singleEntity(new GetEntity());
    int get = singleResult.getSingleValue();
    result.addAll(singleResult);
    
    // put
    singleResult = singleEntity(new PutEntity());
    int put = singleResult.getSingleValue();
    result.addAll(singleResult);
    
    // put + remove
    singleResult = singleEntity(new PutRemoveEntity());
    int putRemove = singleResult.getSingleValue();
    result.addAll(singleResult);
    
    // put + invalidate
    singleResult = singleEntity(new PutInvalidateEntity());
    int putInvalidate = singleResult.getSingleValue();
    result.addAll(singleResult);
    
    // remove - this is calculated using put and put + remove velocities
    int remove = (int) (((double)putRemove * (double)put) / ((double)put - (double)putRemove));
    result.putValue("Remove Operations / Sec", remove);
    
    // remove - this is calculated using put and put + remove velocities
    int invalidate = (int) (((double)putInvalidate * (double)put) / ((double)put - (double)putInvalidate));
    result.putValue("Invalidate Operations / Sec", invalidate);
    
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
