package com.sap.engine.core.cache.pp;

import com.sap.engine.core.Manager;
import com.sap.engine.frame.core.cache.CacheContext;

/**
 * @author Petev, Petio, i024139
 */
public interface CacheManager extends Manager {

  public CacheContext getCacheContext();

}
