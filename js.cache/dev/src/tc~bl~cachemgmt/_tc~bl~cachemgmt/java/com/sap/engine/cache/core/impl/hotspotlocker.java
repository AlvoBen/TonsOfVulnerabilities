/*
 * Created on 2004.9.13
 *
 */
package com.sap.engine.cache.core.impl;

import java.util.HashMap;

import com.sap.engine.cache.core.LockerHook;
import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.HolderRuntimeException;

class HotspotLocker implements LockerHook {

  HashMap locks = new HashMap();

  public void execute(String name, Runnable runnable) throws CacheException {
    Object lock;
    synchronized (locks) {
      lock = locks.get(name);
      if (locks.get(name) == null) {  //todo double get!! WTF
        locks.put(name, name);
        lock = name;
      }
    }
    synchronized (lock) {
      try {
        runnable.run();
      } catch (HolderRuntimeException e) {
        throw ((CacheException) e.getCause());
      }
    }
  }

}