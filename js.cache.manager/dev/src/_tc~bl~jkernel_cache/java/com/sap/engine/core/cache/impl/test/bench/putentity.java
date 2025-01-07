/*
 * Created on 2005.2.9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.core.cache.impl.test.bench;

import com.sap.engine.core.cache.impl.CacheManagerImpl;
import com.sap.util.cache.exception.CacheException;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PutEntity extends FacadeBenchAdaptor {

  public void singleOperation() {
    for (int i = 0; i < factor; i++) {
      try {
        facade.put(Names.key[i], CommonPool.cachedObjectArray[i]);
      } catch (CacheException e) {
        CacheManagerImpl.traceT(e);
      }
    }
  }

  public String getName() {
    return ("Put Operations / Sec");
  }

}
