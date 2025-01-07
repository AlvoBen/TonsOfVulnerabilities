/*
 * Created on 2005.8.8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.cache.core.impl;

import java.util.Map;
import java.util.Set;

import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.PrimitiveCacheFacade;
import com.sap.util.cache.exception.CacheException;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrimitiveCacheFacadeImpl extends PrimitiveCacheGroupImpl implements PrimitiveCacheFacade {

  private CacheFacade delegate = null;
  
  public PrimitiveCacheFacadeImpl(CacheFacade cacheFacade) {
    super(cacheFacade);
  }

  public void put(long key, Object cachedObject, String group) throws CacheException {
    delegate.put(transformLong(key), cachedObject, group);
  }

  public void putBundle(Map keyToObject, String group) throws CacheException {
    delegate.putBundle(transformMap(keyToObject), group);
  }

  public void put(long key, Object cachedObject, String group, boolean synchronous, boolean suppressInvalidation) throws CacheException {
    delegate.put(transformLong(key), cachedObject, group, synchronous, suppressInvalidation);
  }

  public void put(long key, Object cachedObject, String group, boolean synchronous, byte invalidationScope) throws CacheException {
    delegate.put(transformLong(key), cachedObject, group, synchronous, invalidationScope);
    
  }

  public void putBundle(Map keyToObject, String group, boolean synchronous, boolean suppressInvalidation) throws CacheException {
    delegate.putBundle(transformMap(keyToObject), group, synchronous, suppressInvalidation);
  }

  public void putBundle(Map keyToObject, String group, boolean synchronous, byte invalidationScope) throws CacheException {
    delegate.putBundle(transformMap(keyToObject), group, synchronous, invalidationScope);
  }

  public void put(long key, Object cachedObject, Map attributes, String group) throws CacheException {
    delegate.put(transformLong(key), cachedObject, attributes, group);
  }

  public void putBundle(Map keyToObject, Map keyToAttributes, String group) throws CacheException {
    delegate.putBundle(transformMap(keyToAttributes), keyToAttributes, group);
  }

  public void put(long key, Object cachedObject, Map attributes, String group, boolean synchronous, boolean suppressInvalidation) throws CacheException {
    delegate.put(transformLong(key), cachedObject, attributes, group, synchronous, suppressInvalidation);
  }

  public void put(long key, Object cachedObject, Map attributes, String group, boolean synchronous, byte invalidationScope) throws CacheException {
    delegate.put(transformLong(key), cachedObject, attributes, group, synchronous, invalidationScope);
  }

  public void putBundle(Map keyToObject, Map keyToAttributes, String group, boolean synchronous, boolean suppressInvalidation) throws CacheException {
    delegate.putBundle(transformMap(keyToAttributes), keyToAttributes, group, synchronous, suppressInvalidation);
  }

  public void putBundle(Map keyToObject, Map keyToAttributes, String group, boolean synchronous, byte invalidationScope) throws CacheException {
    delegate.putBundle(transformMap(keyToAttributes), keyToAttributes, group, synchronous, invalidationScope);
  }

  public Object getOrPut(long key, Object cachedObject, String group) throws CacheException {
    return delegate.getOrPut(transformLong(key), cachedObject, group);
  }

  public Map getOrPutBundle(Map keyToObject, String group) throws CacheException {
    return delegate.getOrPutBundle(transformMap(keyToObject), group);
  }

  public Object getOrPut(long key, Object cachedObject, String group, boolean synchronous, boolean suppressInvalidation) throws CacheException {
    return delegate.getOrPut(transformLong(key), cachedObject, group, synchronous, suppressInvalidation);
  }

  public Object getOrPut(long key, Object cachedObject, String group, boolean synchronous, byte invalidationScope) throws CacheException {
    return delegate.getOrPut(transformLong(key), cachedObject, group, synchronous, invalidationScope);
    
  }

  public Map getOrPutBundle(Map keyToObject, String group, boolean synchronous, boolean suppressInvalidation) throws CacheException {
    return delegate.getOrPutBundle(transformMap(keyToObject), group, synchronous, suppressInvalidation);
  }

  public Map getOrPutBundle(Map keyToObject, String group, boolean synchronous, byte invalidationScope) throws CacheException {
    return delegate.getOrPutBundle(transformMap(keyToObject), group, synchronous, invalidationScope);
  }

  public Object getOrPut(long key, Object cachedObject, Map attributes, String group) throws CacheException {
    return delegate.getOrPut(transformLong(key), cachedObject, attributes, group);
  }

  public Map getOrPutBundle(Map keyToObject, Map keyToAttributes, String group) throws CacheException {
    return delegate.getOrPutBundle(transformMap(keyToAttributes), keyToAttributes, group);
  }

  public Object getOrPut(long key, Object cachedObject, Map attributes, String group, boolean synchronous, boolean suppressInvalidation) throws CacheException {
    return delegate.getOrPut(transformLong(key), cachedObject, attributes, group, synchronous, suppressInvalidation);
  }

  public Object getOrPut(long key, Object cachedObject, Map attributes, String group, boolean synchronous, byte invalidationScope) throws CacheException {
    return delegate.getOrPut(transformLong(key), cachedObject, attributes, group, synchronous, invalidationScope);
  }

  public Map getOrPutBundle(Map keyToObject, Map keyToAttributes, String group, boolean synchronous, boolean suppressInvalidation) throws CacheException {
    return delegate.getOrPutBundle(transformMap(keyToAttributes), keyToAttributes, group, synchronous, suppressInvalidation);
  }

  public Map getOrPutBundle(Map keyToObject, Map keyToAttributes, String group, boolean synchronous, byte invalidationScope) throws CacheException {
    return delegate.getOrPutBundle(transformMap(keyToAttributes), keyToAttributes, group, synchronous, invalidationScope);
  }

  public void remove(Map attributes, String group) {
    delegate.remove(attributes, group);
  }

  public void remove(Map attributes, String group, boolean synchronous, boolean suppressInvalidation) {
    delegate.remove(attributes, group, synchronous, suppressInvalidation);
  }

  public void remove(Map attributes, String group, boolean synchronous, byte invalidationScope) {
    delegate.remove(attributes, group, synchronous, invalidationScope);
  }

  public void addChild(String parentGroupName, String childGroupName) throws CacheException {
    delegate.addChild(parentGroupName, childGroupName);
  }

  public void removeChild(String parentGroupName, String childGroupName) throws CacheException {
    delegate.removeChild(parentGroupName, childGroupName);
  }

  public Set getChildren(String groupName) throws CacheException {
    return delegate.getChildren(groupName);
  }

}
