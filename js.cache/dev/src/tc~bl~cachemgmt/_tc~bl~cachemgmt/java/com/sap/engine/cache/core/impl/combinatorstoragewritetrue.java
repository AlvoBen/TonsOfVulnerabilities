/*
 * Created on 2004-10-6
 *
 */
package com.sap.engine.cache.core.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


import com.sap.engine.cache.util.dump.LogUtil;
import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.PluginException;
import com.sap.util.cache.spi.Pluggable;
import com.sap.util.cache.spi.PluginContext;
import com.sap.util.cache.spi.storage.StoragePlugin;

/**
 * @author ilian-n
 *
 * This storage plugin is a variety of Combinator storage which never removes/evicts 
 * an object form its back end storage 
 */
public class CombinatorStorageWriteTrue implements StoragePlugin {
  
  private StoragePlugin back_end = null;
  private StoragePlugin front_end = null;

  private Properties properties;
  private CacheFacade facade;
  private String regionName = null;

  public CombinatorStorageWriteTrue() {
    properties = new Properties();
    properties.put("CombinatorStorage.FRONTEND_STORAGE", "HashMapStorage");
    properties.put("CombinatorStorage.BACKEND_STORAGE", "DBStorage");
  }
 
  /**
   * Sets the front-end storage
   *  
   * @param _front_end The front end storage.
   */
  public void setFrontEnd(StoragePlugin _front_end) {
    this.front_end = _front_end;
  }
  
  /**
   * Sets the back-end storage
   * 
   * @param _back_end The back end storage.
   */
  public void setBackEnd(StoragePlugin _back_end) {
    this.back_end = _back_end;
  }

  /**
   * @param key          The cached object key
   * @param cachedObject The cached object that will be put into the storage
   * @throws CacheException
   */
  public void put(String key, Object cachedObject) throws CacheException {
    back_end.put(key, cachedObject);
    front_end.put(key, cachedObject);
  }

  /**
   * @param key          The cached object key
   * @param cachedObject The cached object that will be put into the storage
   * @param attributes   The attributes to be associated with the cached
   *                     object
   * @param system       if set to <code>true</code> the attributes are
   *                     system attributes; otherwise they represent
   *                     user-defined attributes
   * @throws CacheException
   */
  public void put(String key, Object cachedObject, Map attributes, boolean system) throws CacheException {
    front_end.put(key, cachedObject, attributes, system);
    back_end.put(key, cachedObject, attributes, system);
  }

  /**
   * @param key          The cached object key
   * @param cachedObject The cached object that will be put into the storage
   * @param attributes   The user-defined attributes to be associated with
   *                     the cached object
   * @param systemAttributes The system attributes of the cached object to be
   *                      associated with the cached object
   * @throws CacheException
   */
  public void put(String key, Object cachedObject, Map attributes, Map systemAttributes) throws CacheException {
    front_end.put(key, cachedObject, attributes, systemAttributes);
    back_end.put(key, cachedObject, attributes, systemAttributes);
  }
  
  /**
   * The eviction logic followed by the storage plugin 
   */
  public void evict(String key) {
    front_end.evict(key);
  }
  
  /**
   * 
   */
  public Pluggable getInstance() throws PluginException {
    CombinatorStorageWriteTrue storage = new CombinatorStorageWriteTrue();
    String frontEnd = properties.getProperty("CombinatorStorage.FRONTEND_STORAGE");
    String backEnd = properties.getProperty("CombinatorStorage.BACKEND_STORAGE");
    storage.setFrontEnd((StoragePlugin)PluggableFramework.getStoragePlugin(frontEnd).getInstance());
    storage.setBackEnd((StoragePlugin)PluggableFramework.getStoragePlugin(backEnd).getInstance());
    return storage;
  }
  
  /**
   * 
   */
  public Object get(String key, boolean copy) {
    Object result;
    result = front_end.get(key, copy);
    if (result == null) {
      result = back_end.get(key, copy);
      Map attributes = back_end.getAttributes(key, copy);
      Map systemAttributes = back_end.getSystemAttributes(key, copy);
      try {
        if (facade == null) {
          facade = CacheRegionFactory.getInstance().getCacheRegion(regionName).getCacheFacade();
        }
        if (systemAttributes != null) {
          front_end.putSystemAttributes(key, systemAttributes);
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
    Map result = front_end.getAttributes(key, copy);
    if (result == null) {
      if (!front_end.exists(key)) {
        result = back_end.getAttributes(key, copy);
        if (result != null) {
          try {
            front_end.putAttributes(key, result);
          } catch (CacheException e) {
            LogUtil.logTInfo(e);
          }
        }
      }
    }
    return result;
  }

  public Map getSystemAttributes(String key, boolean copy) {
    Map result = front_end.getSystemAttributes(key, copy);
    if (result == null) {
      if (!front_end.exists(key)) {
        result = back_end.getSystemAttributes(key, copy);
        if (result != null) {
          try {
            front_end.putSystemAttributes(key, result); // TODO CHECK IF I MAY COMMENT THIS LINE?
          } catch (CacheException e) {
            LogUtil.logTInfo(e);
          }
        }
      }
    }
    return result;
  }

  public void remove(String key) {
    front_end.remove(key);
    back_end.remove(key);
  }

  public void flush() throws CacheException {
    front_end.flush();
    back_end.flush();
  }

  public Set keySet() {
    Set globalSet = new HashSet();
    globalSet.addAll(front_end.keySet());
    globalSet.addAll(back_end.keySet());
    return globalSet;
  }

  public void init(String name, Properties _properties) throws PluginException {
    if (properties != null)  {
      this.properties = _properties;
    }

    if (_properties.getProperty("CombinatorStorage.FRONTEND_STORAGE") == null) {
      this.properties.setProperty("CombinatorStorage.FRONTEND_STORAGE", "HashMapStorage");
    }
    if (_properties.getProperty("CombinatorStorage.BACKEND_STORAGE") == null) {
      this.properties.setProperty("CombinatorStorage.BACKEND_STORAGE", "DBStorage");
    }
  }

  public void start() throws PluginException {
    front_end.start();
    back_end.start();
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.spi.storage.StoragePlugin#putAttributes(java.lang.String, java.util.Map)
   */
  public void putAttributes(String arg0, Map arg1) throws CacheException {
    front_end.putAttributes(arg0, arg1);
    back_end.putAttributes(arg0, arg1);    
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.spi.storage.StoragePlugin#putSystemAttributes(java.lang.String, java.util.Map)
   */
  public void putSystemAttributes(String arg0, Map arg1) throws CacheException {
    front_end.putSystemAttributes(arg0, arg1);
    back_end.putSystemAttributes(arg0, arg1);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.spi.storage.StoragePlugin#putAttributes(java.lang.String, java.util.Map, java.util.Map)
   */
  public void putAttributes(String arg0, Map arg1, Map arg2) throws CacheException {
    front_end.putAttributes(arg0, arg1, arg2);
    back_end.putAttributes(arg0, arg1, arg2);    
  }

  /**
   * Checks whether the specified object key is in the cache.
   *
   * @param key   The cached object key
   * 
   * @return <code>true</code> if the cached object exists (i.e., the 
   *         object is in the in-memory cache and no back end storage will be
   *         contacted); otherwise <code>false</code> is returned
   * 
   * @throws NullPointerException if the <code>key</code> parameter 
   *         is <code>null</code>
   */
  public boolean exists(String key) {
    return front_end.exists(key) || back_end.exists(key);
  }

  /**
   * A storage plugin specific method that converts cached object to 
   * transportable ones (serializable and/or shareable).
   *
   * @param cachedObject The cached object to be transported
   * 
   * @return The transportable object (can be the same) or <code>null</code>
   *         if the storage plugin does not support transporting
   * 
   * @throws NullPointerException if the <code>cachedObject</code> parameter 
   *         is <code>null</code>.
   */
  public Object transport(Object cachedObject) {
    Object result;
    result = front_end.transport(cachedObject);
    if (result == null) {
      result = back_end.transport(cachedObject);
    }
    return result;
  }

  /**
   * Invalidates the specified key. The difference to a 
   * <code>remove()</code> operation is that the <code>key</code> and
   * the attributes remain in the cache.
   * 
   * @param key the key to be invalidated
   * 
   * @throws NullPointerException if the <code>key</code> parameter is
   *         <code>null</code>
   */
  public void invalidate(String key) {
    front_end.invalidate(key);
    back_end.invalidate(key);
  }

  /**
   * A storage plugin specific method that converts a transportable object
   * (shareable and/or serializable) to actual cached object.
   *
   * @param transportable The transportable state of a cached object
   * @return The cached object recreated using the transportable object
   *         (can be the same) as <code>transportable</code>
   * 
   * @throws NullPointerException if the <code>transportable</code> parameter
   *         is <code>null</code> 
   */
  public Object recreateTransported(Object transportable) {
    Object result;
    result = front_end.recreateTransported(transportable);
    if (result == null) {
      result = back_end.recreateTransported(transportable);
    }
    return result;
  }

  /**
   * Returns a calculated size of a cached object.
   *
   * @param objectKey The key of the cached object to get the size of
   * 
   * @return The size of the cached object in bytes
   * 
   * @throws NullPointerException if <code>objectKey</code> is 
   *         <code>null</code>
   */
  public int getSize(String objectKey) {
  	if (front_end.exists(objectKey)) {
  	  return front_end.getSize(objectKey);
  	}
    return back_end.getSize(objectKey);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.spi.storage.StoragePlugin#getAttributesSize(java.lang.String)
   */
  public int getAttributesSize(String arg0) {
    return back_end.getAttributesSize(arg0);
  }

  /**
   * Gets the effective scope (local, instance, cluster) of the Storage Plugin - the extent that
   * objects are distributed to
   * 
   * @return The effective scope
   */
  public byte getScope() {
    return back_end.getScope() > front_end.getScope() ? back_end.getScope() : front_end.getScope();
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
   *         object is in the in-memory cache and no back end storage will be
   *         contacted); otherwise <code>false</code> is returned
   * 
   * @throws NullPointerException if either argument is <code>null</code>
   */
  public boolean exists(String group, String key) {
    boolean result = front_end.exists(group, key);
    if (!result) {
      return back_end.exists(group, key);
    }
    return result;
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
    Object result;
    result = front_end.get(group, key, copy);
    if (result == null) {
      result = back_end.get(group, key, copy);
    }
    
    return result;
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
    Map result;
    result = front_end.getAttributes(group, key, copy);
    if (result == null) {
      result = back_end.getAttributes(group, key, copy);
    }
    
    return result;
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
    front_end.remove(group);
    back_end.remove(group);
    
  }

  /**
   * Removes all elements in the cache region.
   */
  public void remove() {
    front_end.remove();
    back_end.remove();    
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
    front_end.removeGroup(group, delete);
    back_end.removeGroup(group, delete);
    
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
    front_end.remove(group, key);
    back_end.remove(group, key);
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
    front_end.invalidateGroup(group);
    back_end.invalidateGroup(group);
  }

  /**
   * Invalidates all elements in the cache. 
   */
  public void invalidate() {
    front_end.invalidate();
    back_end.invalidate();    
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
    front_end.invalidate(group, key);
    back_end.invalidate(group, key); // How to get the invalidation scope and to call this method only if the scope is CLUSTER
  }

  /**
   * Returns the number of cached objects.
   * 
   * @return the number of cached objects
   */
  public int size() {
    return back_end.size();
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
    return back_end.size(group);
  }

  /**
   * Checks whether the storage plugin has stored some objects.
   * 
   * @return <code>true</code> if some objects are stored; otherwise
   *         <code>false</code> is returned 
   */
  public boolean isEmpty() {
    return front_end.isEmpty() && back_end.isEmpty();
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
    return front_end.isEmpty(group) && back_end.isEmpty(group);
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
    front_end.insertGroup(group);
    back_end.insertGroup(group);
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
    Set result = new HashSet();
    result.addAll(front_end.keySet(group));
    result.addAll(back_end.keySet(group));
    return result; 
  }

  /**
   * Returns the values of the whole cache region.
   * 
   * @return the values of the whole cache region as a collection
   */
  public Collection values() {
    Collection result = new HashSet();
    result.addAll(front_end.values());
    result.addAll(back_end.values());
    return result;
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
    Collection result = new HashSet();
    result.addAll(front_end.values(group));
    result.addAll(back_end.values(group));
    return result;
  }

  /**
   * Returns an entry set of the whole cache region.
   * 
   * @return the entry set of the whole cache region
   */
  public Set entrySet() {
    Set result = new HashSet();
    result.addAll(front_end.entrySet());
    result.addAll(back_end.entrySet());
    return result;
  }

  /**
   * Returns an entry set of the specified cache region.
   * 
   * @param group the group name whose entry set is to be returned
   * 
   * @return the entry set of the whole cache region
   */
  public Set entrySet(String group) {
    Set result = new HashSet();
    result.addAll(front_end.entrySet(group));
    result.addAll(back_end.entrySet(group));
    return result;
  }

  /**
   * Gets a set of the available groups.
   * 
   * @return set of the available groups
   */
  public Set getGroupSet() {
    Set result = new HashSet();
    result.addAll(front_end.getGroupSet());
    result.addAll(back_end.getGroupSet());
    return result;
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
  public boolean existsGroup(String arg0) {
    return front_end.existsGroup(arg0) || back_end.existsGroup(arg0);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.spi.Pluggable#setPluginContext(com.sap.util.cache.spi.PluginContext)
   */
  public void setPluginContext(PluginContext arg0) {
    regionName = arg0.getRegionName();
    front_end.setPluginContext(arg0);
    back_end.setPluginContext(arg0);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.spi.Pluggable#stop()
   */
  public void stop() {
    front_end.stop();
    back_end.stop();
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.spi.Pluggable#shutdown()
   */
  public void shutdown() {
    front_end.shutdown();
    back_end.shutdown();   
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.spi.Pluggable#getName()
   */
  public String getName() {
    return "CombinatorStorageWriteTrue (" + front_end.getName() + " + " + back_end.getName() + ")";
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.spi.Pluggable#getDescription()
   */
  public String getDescription() {
    return getName();
  }

  public String toString() {
    return super.toString() + " frontEnd: " + front_end + " backEnd: " + back_end;    
  }
}
