package com.sap.engine.cache.spi.storage.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.ArrayList;

import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.PluginException;
import com.sap.util.cache.spi.Pluggable;
import com.sap.util.cache.spi.PluginContext;
import com.sap.util.cache.spi.storage.StoragePlugin;

/**
 * @author Petev, Petio, i024139
 */
public class DummyStorage implements StoragePlugin {

  private String name = "DummyStorage";

  public Pluggable getInstance() throws PluginException {
    return this;
  }

  public void setPluginContext(PluginContext ctx) {
  }

  public void stop() {
  }

  public int getAttributesSize(String objectKey) {
    return 0;
  }

  public void putAttributes(String key, Map attributes, Map systemAttributes) throws CacheException {
  }

  public void put(String key, Object cachedObject, Map attributes, boolean system) throws CacheException {
  }

  public Object transport(Object cachedObject) {
    return null;
  }

  public Object recreateTransported(Object transportable) {
    return null;
  }

  public int getSize(String cachedObject) {
    return 0;
  }

  public void put(String key, Object cachedObject) throws CacheException {
  }

  public void put(String key, Object cachedObject, Map attributes, Map systemAttributes) throws CacheException {
  }

  public void putAttributes(String key, Map attributes) throws CacheException {
  }

  public void putSystemAttributes(String key, Map attributes) throws CacheException {
  }

  public boolean exists(String key) {
    return false;
  }

  public Object get(String key, boolean copy) {
    return null;
  }

  public Map getAttributes(String key, boolean copy) {
    return null;
  }

  public Map getSystemAttributes(String key, boolean copy) {
    return null;
  }

  public void remove(String key) {
  }

  public void invalidate(String key) {
  }

  public void evict(String key) {
  }

  public void flush() throws CacheException {
  }

  public Set keySet() {
    return new HashSet(0);
  }

  public void init(Properties properties) throws PluginException {
  }

  public void init(String name, Properties properties) throws PluginException {
    this.name = name;
  }

  public void start() throws PluginException {
  }

  public void shutdown() {
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return null;
  }

	public byte getScope() {
		return 0;
	}

	public boolean exists(String arg0, String arg1) {
		return false;
	}

	public Object get(String arg0, String arg1, boolean arg2) {
		return null;
	}

	public Map getAttributes(String arg0, String arg1, boolean arg2) {
		return null;
	}

	public void removeGroup(String arg0) {
	}

	public void remove() {
	}

	public void removeGroup(String arg0, boolean arg1) {
	}

	public void remove(String arg0, String arg1) {
	}

	public void invalidateGroup(String arg0) {
	}

	public void invalidate() {
	}

	public void invalidate(String arg0, String arg1) {
	}

	public int size() {
		return 0;
	}

	public int size(String arg0) {
		return 0;
	}

	public boolean isEmpty() {
		return true;
	}

	public boolean isEmpty(String arg0) {
		return true;
	}

	public void insertGroup(String arg0) {
	}

	public Set keySet(String arg0) {
		return new HashSet(0);
	}

	public Collection values() {
		return new ArrayList(0);
	}

	public Collection values(String arg0) {
		return new ArrayList(0);
	}

	public Set entrySet() {
    return new HashSet(0);
	}

	public Set entrySet(String arg0) {
    return new HashSet(0);
	}

	public Set getGroupSet() {
		return new HashSet(0);
	}

	public boolean existsGroup(String arg0) {
		return false;
	}
}
