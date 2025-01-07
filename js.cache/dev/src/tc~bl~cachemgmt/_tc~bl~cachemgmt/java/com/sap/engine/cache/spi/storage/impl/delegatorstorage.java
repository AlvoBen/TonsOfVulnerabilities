/*
 * Created on 2004.7.9
 *
 */
package com.sap.engine.cache.spi.storage.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.PluginException;
import com.sap.util.cache.spi.Pluggable;
import com.sap.util.cache.spi.PluginContext;
import com.sap.util.cache.spi.storage.StoragePlugin;

/** @author petio-p
 *
 */
public class DelegatorStorage implements StoragePlugin {
  
  private StoragePlugin aggregate = new DummyStorage();
   
  public void setAggregate(StoragePlugin aggregate) {
    this.aggregate = aggregate;
  }

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return aggregate.equals(obj);
	}

	/**
	 * @param key
	 */
	public void evict(String key) {
		aggregate.evict(key);
	}

	/**
	 * @param key
	 * @return
	 */
	public boolean exists(String key) {
		return aggregate.exists(key);
	}

	/**
	 * @throws CacheException
	 */
	public void flush() throws CacheException {
		aggregate.flush();
	}

	/**
	 * @param key
	 * @param copy
	 * @return
	 */
	public Object get(String key, boolean copy) {
		return aggregate.get(key, copy);
	}

	/**
	 * @param key
	 * @param copy
	 * @return
	 */
	public Map getAttributes(String key, boolean copy) {
		return aggregate.getAttributes(key, copy);
	}

	/**
	 * @param objectKey
	 * @return
	 */
	public int getAttributesSize(String objectKey) {
		return aggregate.getAttributesSize(objectKey);
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return aggregate.getDescription();
	}

	/**
	 * @return
	 * @throws PluginException
	 */
	public Pluggable getInstance() throws PluginException {
		return aggregate.getInstance();
	}

	/**
	 * @return
	 */
	public String getName() {
		return aggregate.getName();
	}

	/**
	 * @param objectKey
	 * @return
	 */
	public int getSize(String objectKey) {
		return aggregate.getSize(objectKey);
	}

	/**
	 * @param key
	 * @param copy
	 * @return
	 */
	public Map getSystemAttributes(String key, boolean copy) {
		return aggregate.getSystemAttributes(key, copy);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return aggregate.hashCode();
	}

	/**
	 * @param name
	 * @param properties
	 * @throws PluginException
	 */
	public void init(String name, Properties properties) throws PluginException {
		aggregate.init(name, properties);
	}

	/**
	 * @return
	 */
	public Set keySet() {
		return aggregate.keySet();
	}

	/**
	 * @param key
	 * @param cachedObject
	 * @throws CacheException
	 */
	public void put(String key, Object cachedObject) throws CacheException {
		aggregate.put(key, cachedObject);
	}

	/**
	 * @param key
	 * @param cachedObject
	 * @param attributes
	 * @param system
	 * @throws CacheException
	 */
	public void put(
		String key,
		Object cachedObject,
		Map attributes,
		boolean system)
		throws CacheException {
		aggregate.put(key, cachedObject, attributes, system);
	}

	/**
	 * @param key
	 * @param cachedObject
	 * @param attributes
	 * @param systemAttributes
	 * @throws CacheException
	 */
	public void put(
		String key,
		Object cachedObject,
		Map attributes,
		Map systemAttributes)
		throws CacheException {
		aggregate.put(key, cachedObject, attributes, systemAttributes);
	}

	/**
	 * @param key
	 * @param attributes
	 * @throws CacheException
	 */
	public void putAttributes(String key, Map attributes) throws CacheException {
		aggregate.putAttributes(key, attributes);
	}

	/**
	 * @param key
	 * @param attributes
	 * @param systemAttributes
	 * @throws CacheException
	 */
	public void putAttributes(String key, Map attributes, Map systemAttributes)
		throws CacheException {
		aggregate.putAttributes(key, attributes, systemAttributes);
	}

	/**
	 * @param key
	 * @param attributes
	 * @throws CacheException
	 */
	public void putSystemAttributes(String key, Map attributes)
		throws CacheException {
		aggregate.putSystemAttributes(key, attributes);
	}

	/**
	 * @param transportable
	 * @return
	 */
	public Object recreateTransported(Object transportable) {
		return aggregate.recreateTransported(transportable);
	}

	/**
	 * @param key
	 */
	public void remove(String key) {
		aggregate.remove(key);
	}

  /**
   * @param key
   */
  public void invalidate(String key) {
    aggregate.invalidate(key);
  }

	/**
	 * @param ctx
	 */
	public void setPluginContext(PluginContext ctx) {
		aggregate.setPluginContext(ctx);
	}

	/**
	 * 
	 */
	public void shutdown() {
		aggregate.shutdown();
	}

	/**
	 * @throws PluginException
	 */
	public void start() throws PluginException {
		aggregate.start();
	}

	/**
	 * 
	 */
	public void stop() {
		aggregate.stop();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (aggregate != null) {
			return super.toString() + " -> " + aggregate.toString();
		}
		return super.toString();
	}

	/**
	 * @param cachedObject
	 * @return
	 */
	public Object transport(Object cachedObject) {
		return aggregate.transport(cachedObject);
	}

	/* (non-Javadoc)
	 * @see com.sap.util.cache.spi.storage.StoragePlugin#getScope()
	 */
	public byte getScope() {
		return aggregate.getScope();
	}

  /**
   * Checks whether the specified object key is in the cache.
   *
   * @param group The cache group; if the <code>group</code> is not 
   *              <code>null</code>, the existence of the <code>key</code>
   *              is checked against this <code>group</code>; otherwise
   *              the whole cache region is considered  
   * @param key   The cached object key
   * 
   * @return <code>true</code> if the cached object exists (i.e., the 
   *         object is in the in-memory cache and no backend storage will be
   *         contacted); otherwise <code>false</code> is returned
   * 
   * @throws NullPointerException if either argument is <code>null</code>
   */
  public boolean exists(String group, String key) {
    return aggregate.exists(group, key);
  }

  /**
   * Gets the object associated with the specified key.
   *
   * @param group The cache group; if the <code>group</code> is not 
   *              <code>null</code>, the <code>key</code> must be in the
   *              group in order to a non-<code>null</code> return value;
   *              otherwise the whole cache region is considered
   * @param key   The cached object key
   * @param copy  indicates whether a copy of the original copy should
   *              be returned
   * 
   * @return the object associated with the specified key or 
   *         <code>null</code> if the object cannot be retrieved
   * 
   * @throws NullPointerException if the <code>group</code> or 
   *         <code>key</code> parameter is <code>null</code>
   */
  public Object get(String group, String key, boolean copy) {
    return aggregate.get(group, key, copy);
  }
  
  /**
   * Gets the user-defined attributes of the object associated with the 
   * specified key.
   *
   * @param group The cache group; if the <code>group</code> is not 
   *              <code>null</code>, the <code>key</code> must be in the
   *              group in order to a non-<code>null</code> return value;
   *              otherwise the whole cache region is considered
   * @param key   The cached object key the attributes are returned for
   * @param copy  indicates whether or not a copy of the attributes
   *              should be returned
   * 
   * @return The user-defined attributes of the cached object or 
   *         <code>null</code>
   * 
   * @throws NullPointerException if the <code>group</code> or the 
   *         <code>key</code> parameter is <code>null</code>
   */
  public Map getAttributes(String group, String key, boolean copy) {
    return aggregate.getAttributes(group, key, copy);
  }
  
  /**
   * Removes the elements inside the specified group. 
   * 
   * @param group the cache group to be deleted
   * 
   * @throws NullPointerException if the <code>group</code> parameter is
   *         set to <code>null</code>
   */        
  public void removeGroup(String group) {
    aggregate.removeGroup(group);
  }
      
  /**
   * Removes all elements in the cache region.
   */
  public void remove() {
    aggregate.remove();
  }
      
  /**
   * Removes the elements inside the specified group.
   * 
   * @param group  the cache group to be deleted
   * @param delete if set to <code>true</code> the whole cache region is
   *               deleted; otherwise only the elements inside the groups
   *               are removed
   * 
   * @throws NullPointerException if the <code>group</code> parameter is
   *         set to <code>null</code>
   */
  public void removeGroup(String group, boolean delete) {
    aggregate.removeGroup(group, delete);
  }
      
  /**
   * Removes the object associated with the specified <code>key</code>
   * from the cache.
   *
   * @param group The cache group; if the <code>group</code> is not 
   *              <code>null</code>, the <code>key</code> must be in the
   *              group in order to get removed;
   *              otherwise the whole cache region is considered
   * @param key   The cached object key to be deleted
   *  
   * @throws NullPointerException if the <code>group</code> or the 
   *         <code>key</code> is <code>null</code>
   */
  public void remove(String group, String key) {
    aggregate.remove(group, key);
  }
  
  /**
   * Invalidates all elements in the specified group. The difference to a 
   * <code>removeGroup()</code> operation is that the <code>keys</code> and
   * the attributes remain in the cache.
   * 
   * @param group the group name
   * 
   * @throws NullPointerException if the <code>group</code> parameter is
   *         <code>null</code>
   */
  public void invalidateGroup(String group) {
    aggregate.invalidateGroup(group);
  }
  
  /**
   * Invalidates all elements in the cache. 
   */
  public void invalidate() {
    aggregate.invalidate();
  }
      
  /**
   * Invalidates the key of the specified group. The difference to a 
   * <code>removeGroup()</code> operation is that the <code>keys</code> and
   * the attributes remain in the cache.
   * 
   * @param group the group name
   * @param key   the key of the group to be returned
   * 
   * @throws NullPointerException if either argument is <code>null</code>
   */
  public void invalidate(String group, String key) {
    aggregate.invalidate(group, key);
  }
      
  /**
   * Returns the number of cached objects.
   * 
   * @return the number of cached objects
   */
  public int size() {
    return aggregate.size();
  }
      
  /**
   * Returns the number of cached objects.
   * 
   * @param group the cache group whose number of objects should be returned;
   *              if set to <code>null</code> the whole cache region is
   *              considered
   * 
   * @return the number of cached objects
   * 
   * @throws NullPointerException if the <code>group</code> parameter 
   *         is <code>null</code>
   */
  public int size(String group) {
    return aggregate.size(group);
  }
      
  /**
   * Checks whether the storage plugin has stored some objects.
   * 
   * @return <code>true</code> if some objects are stored; otherwise
   *         <code>false</code> is returned 
   */
  public boolean isEmpty() {
    return aggregate.isEmpty();
  }
      
  /**
   * Checks whether the storage plugin has stored some objects.
   * 
   * @param group the cache group to be checked for emptiness; if 
   *              set to <code>null</code> the whole cache region is
   *              considered
   * 
   * @return <code>true</code> if some objects are stored; otherwise
   *         <code>false</code> is returned
   *  
   * @throws NullPointerException if the <code>group</code> parameter 
   *         is <code>null</code>
   */
  public boolean isEmpty(String group) {
    return aggregate.isEmpty(group);
  }
      
  /**
   * Inserts a cache group.
   * 
   * @param group   the cache group to be inserted
   * 
   * @throws NullPointerException if the <code>group</code> parameter is 
   *         <code>null</code>
   */
  public void insertGroup(String group) {
    aggregate.insertGroup(group);  
  }
          
  /**
   * Returns a key set of specified group.
   * 
   * @return key set of the specified group
   * 
   * @throws NullPointerException if the specified group parameter is
   *         <code>null</code>
   */
  public Set keySet(String group) {
    return aggregate.keySet(group);
  }
      
  /**
   * Returns the values of the whole cache region.
   * 
   * @return the values of the whole cache region as a collection
   */
  public Collection values() {
    return aggregate.values();
  }
      
  /**
   * Returns the values of the specified group.
   * 
   * @param group the values of the specified group as a collection
   * 
   * @throws NullPointerException if the specified <code>group</code> 
   *                              parameter is <code>null</code>
   */
  public Collection values(String group) {
    return aggregate.values(group);
  }
      
  /**
   * Returns an entry set of the whole cache region.
   * 
   * @return the entry set of the whole cache region
   */
  public Set entrySet() {
    return aggregate.entrySet();
  }
  
  /**
   * Returns an entry set of the specified cache region.
   * 
   * @param group the group name whose entry set is to be returned
   * 
   * @return the entry set of the whole cache region
   */
  public Set entrySet(String group) {
    return aggregate.entrySet(group);
  }
      
  /**
   * Gets a set of the available groups.
   * 
   * @return set of the available groups
   */
  public Set getGroupSet() {
    return aggregate.getGroupSet();
  }
      
  /**
   * Checks whether the specified group exists.
   * 
   * @return <code>true</code> if the specified group already exists;
   *         otherwise <code>false</code> is returned
   * 
   * @throws NullPointerException if the <code>group</code> parameter
   *         is <code>null</code>
   */
  public boolean existsGroup(String group) {
    return aggregate.existsGroup(group);
  }

}
