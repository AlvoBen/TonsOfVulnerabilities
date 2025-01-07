package com.sap.engine.cache.core.impl;

import java.util.HashMap;
import java.util.Iterator;

import com.sap.engine.cache.job.Task;
import com.sap.util.cache.spi.policy.EvictionPolicy;
import com.sap.util.cache.spi.storage.StoragePlugin;

/**
 * @author Petev, Petio, i024139
 */
public class TimeInvalidationEntity {

  private StoragePlugin storage;

	private EvictionPolicy eviction;

	private CacheRegionImpl region = null;
  
  private HashMap singleTasks = null;
  
  private class SingleTask implements Task {
    
    private byte scope = 1;
    private int interval = 1;
    private EvictionPolicy eviction = null;
    private StoragePlugin storage = null;
    private String key;
    private CacheRegionImpl region;
    private boolean bypass = false;
    
    public SingleTask(CacheRegionImpl region, String key, StoragePlugin storage, EvictionPolicy eviction, byte scope, int interval) {
      this.storage = storage;
      this.eviction = eviction;
      this.scope = scope;
      this.interval = interval;
      this.key = key;
      this.region = region;
    }

		public String getName() {
			return "_TTL_AED";
		}

		public boolean repeatable() {
			return false;
		}

		public int getInterval() {
			return interval;
		}

		public byte getScope() {
			return scope;
		}

		public void run() {
      if (!bypass) {
        synchronized (region) {
          region.evict(key);
          eviction.onInvalidate(key);
          singleTasks.remove(key);
        }
      }
		} 
  }
  
  protected TimeInvalidationEntity(CacheRegionImpl region) {
    this.region = region;
    this.eviction = region.getRegionConfiguration().getEvictionPolicy();
    this.storage= region.getRegionConfiguration().getStoragePlugin();
    this.singleTasks = new HashMap();
  }
  
  protected void registerTTL(String key, long interval) {
    if (interval > 0) {
      // unregister prior task
      SingleTask task = (SingleTask) singleTasks.get(key);
      if (task != null) {
        CacheRegionImpl.background.unregisterTask(task);
        // and reuse it
        task.interval = (int) interval;
        task.bypass = false;
      } else {
        // and create a new one
        task = this.new SingleTask(region, key, storage, eviction, region.getRegionConfiguration().getRegionScope(), (int) interval);
        singleTasks.put(key, task);
      }
      CacheRegionImpl.background.registerTask(task);
    }
  }
  
  protected void registerAET(String key, long time) {
    registerTTL(key, time - System.currentTimeMillis());
  }
  
  protected void removeRegistered(String key) {
    SingleTask task = (SingleTask) singleTasks.remove(key);
    if (task != null) {
      // bypass only
      task.bypass = true;
    }
  }

  protected void removeRegistered() {
    Iterator keys = singleTasks.keySet().iterator();
    while (keys.hasNext()) {
      String key = (String) keys.next();
      Task task = (Task) singleTasks.get(key);
      if (task != null) {
        CacheRegionImpl.background.unregisterTask(task);
      }
    }
    singleTasks.clear();
  }
}
