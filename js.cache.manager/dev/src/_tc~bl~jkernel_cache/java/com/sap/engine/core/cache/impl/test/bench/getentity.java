/*
 * Created on 2005.2.9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.core.cache.impl.test.bench;

import com.sap.engine.core.cache.impl.CacheManagerImpl;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.exception.CacheException;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GetEntity extends FacadeBenchAdaptor {
  
  private void populate() {
    for (int i = 0; i < factor; i++) {
      try {
        facade.put(Names.key[i], CommonPool.cachedObjectArray[i]);
      } catch (CacheException e) {
        CacheManagerImpl.traceT(e);
      }
    }
  }
  
  private void cleanup() {
    for (int i = 0; i < factor; i++) {
      facade.remove(Names.key[i]);
    }
  }
  
  public void init(CacheRegion region) {
    super.init(region);
    populate();
  }
  
  public void close() {
    cleanup();
    super.close();
  }
  
  public void singleOperation() {
    for (int i = 0; i < factor; i++) {
      facade.get(Names.key[i]);
    }
  }
  
  public String getName() {
    return "Get Operations / Sec";
  }
  
}
