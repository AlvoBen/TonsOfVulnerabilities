package com.sap.engine.cache.core.impl;


import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.cache.spi.storage.impl.DelegatorStorage;
import com.sap.engine.cache.util.dump.LogUtil;
import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.PluginException;
import com.sap.util.cache.spi.Pluggable;
import com.sap.util.cache.spi.PluginContext;
import com.sap.util.cache.spi.storage.StoragePlugin;

/**
 * @author Petev, Petio, i024139
 */
public class CombinatorStorage extends DelegatorStorage implements StoragePlugin {

  private Properties properties;

  private CacheFacade facade;
  
  private StoragePlugin _additional = null;
  
  private String regionName = null;


  
  public Object get(String key, boolean copy) {
    Object result = super.get(key, copy);
    if (result == null) {
      result = _additional.get(key, copy);
      Map attributes = _additional.getAttributes(key, copy);
      Map systemAttributes = _additional.getSystemAttributes(key, copy);
      try {
        if (facade == null) {
          facade = CacheRegionFactory.getInstance().getCacheRegion(regionName).getCacheFacade();
        }
        if (systemAttributes != null) {
          super.putSystemAttributes(key, systemAttributes);
        }
        if (result != null) {
          facade.put(key, result, attributes);
        }
      } catch (CacheException e) {
        LogUtil.logT(e);
      }
    }
    return result;
  }

  public Map getAttributes(String key, boolean copy) {
    Map result = super.getAttributes(key, copy);
    if (result == null) {
      if (!super.exists(key)) {
        result = _additional.getAttributes(key, copy);
        if (result != null) {
          try {
            super.putAttributes(key, result);
          } catch (CacheException e) {
            LogUtil.logT(e);
          }
        }
      }
    }
    return result;
  }

  public Map getSystemAttributes(String key, boolean copy) {
    Map result = super.getSystemAttributes(key, copy);
    if (result == null) {
      if (!super.exists(key)) {
        result = _additional.getSystemAttributes(key, copy);
        if (result != null) {
          try {
            super.putSystemAttributes(key, result);
          } catch (CacheException e) {
            LogUtil.logT(e);
          }
        }
      }
    }
    return result;
  }

  public void remove(String key) {
    try {
      _additional.put(key, super.get(key, false));
      _additional.putAttributes(key, super.getAttributes(key, false));
      _additional.putSystemAttributes(key, super.getSystemAttributes(key, false));
    } catch (CacheException e) {
      LogUtil.logTInfo(e);
    }
    super.remove(key);
  }

  public void evict(String key) {
    try {
      _additional.put(key, super.get(key, false));
      _additional.putAttributes(key, super.getAttributes(key, false));
      _additional.putSystemAttributes(key, super.getSystemAttributes(key, false));
    } catch (CacheException e) {
      LogUtil.logTInfo(e);
    }
    super.evict(key);
  }

  public void flush() throws CacheException {
    super.flush();
    _additional.flush();
  }

  public Set keySet() {
    Set globalSet = new HashSet();
    globalSet.addAll(super.keySet());
    globalSet.addAll(_additional.keySet());
    return globalSet;
  }

  public void init(String name, Properties properties) {
    if (properties.getProperty("CombinatorStorage.FRONTEND_STORAGE") == null) {
      properties.setProperty("CombinatorStorage.FRONTEND_STORAGE", "SoftStorage");
    }
    if (properties.getProperty("CombinatorStorage.BACKEND_STORAGE") == null) {
      properties.setProperty("CombinatorStorage.BACKEND_STORAGE", "FileStorage");
    }
    this.properties = properties;
  }

  public void start() throws PluginException {
    super.start();
    _additional.start();
  }

  public Pluggable getInstance() throws PluginException {
    CombinatorStorage storage = new CombinatorStorage();
    String frontend = properties.getProperty("CombinatorStorage.FRONTEND_STORAGE");
    String backend = properties.getProperty("CombinatorStorage.BACKEND_STORAGE");
    storage.setAggregate((StoragePlugin)PluggableFramework.getStoragePlugin(frontend).getInstance());
    storage._additional= (StoragePlugin)PluggableFramework.getStoragePlugin(backend).getInstance();
    return storage;
    
  }

  public void setPluginContext(PluginContext ctx) {
    super.setPluginContext(ctx);
    _additional.setPluginContext(ctx);
    regionName = ctx.getRegionName();
  }

  public void stop() {
    super.stop();
    _additional.stop();
  }

  public void shutdown() {
    super.shutdown();
    _additional.shutdown();
    _additional = null;
  }

  public String getName() {
    return "CombinatorStorage ( " + 
           this.properties.get("CombinatorStorage.FRONTEND_STORAGE") + 
           " + " +
           this.properties.get("CombinatorStorage.BACKEND_STORAGE") +
           " )";
  }

  public String getDescription() {
    return null;
  }

	public String toString() {
		return super.toString() + " -> " + _additional;
	}

}
