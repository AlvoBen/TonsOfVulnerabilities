package com.sap.engine.cache.core;

import com.sap.util.cache.exception.CacheException;

/**
 * @author Petev, Petio, i024139
 */
public interface LockerHook {

  public void execute(String name, Runnable runnable) throws CacheException;

}
