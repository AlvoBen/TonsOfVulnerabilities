/*
 * Copyright (c) 2004 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.cache.spi.policy.impl;

import java.util.HashMap;
import java.util.Properties;

import com.sap.engine.cache.util.FastAccessList;
import com.sap.util.cache.exception.PluginException;
import com.sap.util.cache.spi.Pluggable;
import com.sap.util.cache.spi.PluginContext;
import com.sap.util.cache.spi.policy.ElementAttributes;
import com.sap.util.cache.spi.policy.EvictionPolicy;

/**
 * Date: Feb 27, 2004
 * Time: 10:46:55 AM
 * 
 * @author Petio Petev, i024139
 */

public class SimpleLRUEvictionPolicy implements EvictionPolicy {

  private String regionName = null;
  private String name = "SimpleLRU";
  private HashMap attrs = null;
  private FastAccessList list = null;
  private int size;

  public Pluggable getInstance() throws PluginException {
    return new SimpleLRUEvictionPolicy();
  }

  public void setPluginContext(PluginContext ctx) {
    this.regionName = ctx.getRegionName();
  }

  public void stop() {
  }

  /**
   * The cache region implementation will call this method before each put, so that the eviction policy can decide
   * if the cached object is ok to be put into the storage plugin.
   *
   * @param key The cached object key of the cached object
   * @throws NullPointerException if the key is null
   */
  public void onPut(String key, ElementAttributes eAttr) {
    if (list.addFirst(key) != null) {
      ElementAttributes _eAttr = (ElementAttributes) attrs.get(key);
      if (_eAttr != null) size -= _eAttr.getSize();
    }
    size += eAttr.getSize();
    attrs.put(key, eAttr);
  }

  /**
   * The cache region implementation will poll the eviction policy to see if a specific cached object key is
   * contained in the eviction policy
   *
   * @param key The cached object key that is polled
   * @return True if such key exists in the eviction policy
   * @throws NullPointerException if the key is null
   */
  public boolean exists(String key) {
    return list.contains(key);
  }

  public String choose() {
    String keyToEvict = null;
    keyToEvict = (String) list.removeLast();
    if (keyToEvict != null) {
      ElementAttributes eAttr = (ElementAttributes) attrs.remove(keyToEvict);
      if (eAttr != null) size -= eAttr.getSize();
    }
    return keyToEvict;
  }

  /**
   * The cache region implementation will call this method when an object is issued (tried to get) by cache users. The
   * method is combined with <code>exists</code> method
   *
   * @param key The cached object key of the cached object issued by cache user.
   * @throws NullPointerException if the key is null
   */
  public void onAccess(String key) {
    ElementAttributes eAttr = null; 
    list.addFirst(key);
    eAttr = (ElementAttributes) attrs.get(key);
    if (eAttr != null) {
      eAttr.setLastAccessTime(System.currentTimeMillis()); 
    }
  }

  /**
   * The cache region implementation will call this method when an object is removed from the region
   * by cache users. The method is combined with <code>exists</code> method
   *
   * @param key The cached object key of the cached object removed by cache user.
   * @throws NullPointerException if the key is null
   */
  public void onRemove(String key) {
    list.remove(key);
    ElementAttributes eAttr = (ElementAttributes) attrs.remove(key);
    if (eAttr != null) size -= eAttr.getSize();
  }

  /**
   * Returns the current count of objects in the region
   *
   * @return count of objects in the region
   */
  public int getCount() {
    if (list != null) {
      return list.size();
    } else {
      return -1;
    }
  }

  /**
   * Returns the current total size of objects in the region
   *
   * @return total size of objects in the region
   */
  public int getSize() {
    return size;
  }

  /**
   * Called once, after the creation of a cache region. Initializes the component
   *
   * @throws PluginException if the initialization did not succeed
   */
  public void init(String name, Properties properties) {
    this.name = name;
  }

  public void start() throws PluginException {
    list = new FastAccessList();
    attrs = new HashMap();
    size = 0;
  }

  /**
   * Called once, before shutting down a node
   *
   */
  public void shutdown() {
    list = null;
    attrs = null;
    size = 0;
  }

  /**
   * Returns the name of the component.
   *
   * @return Name of the component
   */
  public String getName() {
    if (regionName != null) {
      return name + "@" + regionName;
    }
    return name;
  }

  /**
   * Returns a short description of the component.
   *
   * @return Short description of the component
   */
  public String getDescription() {
    return "Simple LRU Eviction Policy using LinkedList \n\r(I know it is stupid, it is just an example)";
  }

	/* (non-Javadoc)
	 * @see com.sap.util.cache.spi.policy.EvictionPolicy#getElementAttributes(java.lang.String)
	 */
	public ElementAttributes getElementAttributes(String key) {
		return (ElementAttributes) attrs.get(key);
	}

	/* (non-Javadoc)
	 * @see com.sap.util.cache.spi.policy.EvictionPolicy#onInvalidate(java.lang.String)
	 */
	public void onInvalidate(String key) {
    ElementAttributes eAttr = null;
    list.remove(key);
    eAttr = (ElementAttributes) attrs.get(key);
    if (eAttr != null) {
      size -= eAttr.getSize();
      eAttr.setSize(0); 
    }
	}

}
