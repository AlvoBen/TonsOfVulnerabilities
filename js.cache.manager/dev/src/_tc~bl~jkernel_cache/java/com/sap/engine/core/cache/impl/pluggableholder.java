package com.sap.engine.core.cache.impl;

import java.util.Properties;

import com.sap.util.cache.exception.PluginException;
import com.sap.util.cache.spi.Pluggable;
import com.sap.util.cache.spi.policy.EvictionPolicy;
import com.sap.util.cache.spi.storage.StoragePlugin;

/**
 * @author Petev, Petio, i024139
 */
class PluggableHolder {

  public final static int TYPE_NOT_DEFINED = 1;
  public final static int TYPE_EVICTION_POLICY = 1;
  public final static int TYPE_STORAGE_PLUGIN = 2;

  private String name = null;
  private String description = null;
  private int type = 0;
  private String className = null;
  private Properties properties = null;
  private Pluggable instance = null;

  // denotes that an initialization of the pluggable has succeeded
  private boolean ok = false;

  protected PluggableHolder(int type, String name, String className, Properties properties) {
    this.type = type;
    this.className = className;
    this.properties = properties;
    this.name = name;
  }

  protected boolean init() {
    if (instance == null) {
      if (className.startsWith("com.sap.engine.cache.spi")) {
        try {
          Class pluggableBase = Class.forName(className);
          Pluggable pluggable = (Pluggable) pluggableBase.newInstance();
          this.description = pluggable.getDescription();
          this.instance = pluggable;
        } catch (ClassNotFoundException e) {
          CacheManagerImpl.traceT(e);
          return false;
        } catch (InstantiationException e) {
          CacheManagerImpl.traceT(e);
          return false;
        } catch (IllegalAccessException e) {
          CacheManagerImpl.traceT(e);
          return false;
        }
      }
    }
    try {
      instance.init(name, properties);
    } catch (PluginException e) {
      CacheManagerImpl.traceT(e);
      return false;
    }
    ok = true;
    return true;
  }

  protected void shutdown() {
    if (instance != null) {
      instance.shutdown();
    }
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public int getType() {
    return type;
  }

  public String getClassName() {
    return className;
  }

  public Properties getProperties() {
    return properties;
  }

  public boolean isOk() {
    return ok;
  }

  protected static PluggableHolder createHolder(Pluggable pluggable, Properties properties) {
    int type = TYPE_NOT_DEFINED;
    if (pluggable instanceof StoragePlugin) {
      type = TYPE_STORAGE_PLUGIN;
    } else if (pluggable instanceof EvictionPolicy) {
      type = TYPE_EVICTION_POLICY;
    }
    PluggableHolder holder = new PluggableHolder(type, pluggable.getName(), pluggable.getClass().getName(), properties);
    holder.instance = pluggable;
    return holder;
  }

  public Pluggable getInstance() {
    return instance;
  }

}
