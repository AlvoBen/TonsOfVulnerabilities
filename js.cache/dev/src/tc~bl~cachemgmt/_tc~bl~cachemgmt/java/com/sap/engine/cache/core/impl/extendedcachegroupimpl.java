/*
 * Created on 2004.12.2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.cache.core.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.sap.util.cache.CacheGroup;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.ElementConfiguration;
import com.sap.util.cache.ElementConfigurationInfo;
import com.sap.util.cache.ExtendedCacheGroup;
import com.sap.util.cache.exception.CacheException;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExtendedCacheGroupImpl extends CommonKeyTransformer implements ExtendedCacheGroup {

  protected CacheGroup delegate = null;

  public ExtendedCacheGroupImpl(CacheGroup cacheGroup) {
    this.delegate = cacheGroup;
  }

  public void addChild(String childGroupName) throws CacheException {
    delegate.addChild(childGroupName);
  }
  
  public void clear() {
    delegate.clear();
  }
  
  public boolean containsKey(Object key) {
    return delegate.containsKey(transformObject(key));
  }
  
  public boolean equals(Object arg0) {
    return delegate.equals(arg0);
  }
  
  public Object get(Object key) {
    return delegate.get(transformObject(key));
  }
  
  public Object get(Object key, boolean copy) {
    return delegate.get(transformObject(key), copy);
  }
  
  public Map getAttributes(Object key) {
    return delegate.getAttributes(transformObject(key));
  }
  
  public Map getAttributes(Object key, boolean copy) {
    return delegate.getAttributes(transformObject(key), copy);
  }
  
  public Map getBundle(Set keys) {
    return reverseMap(delegate.getBundle(transformSet(keys)));
  }
  
  public Map getBundle(Set keys, boolean copy) {
    return reverseMap(delegate.getBundle(transformSet(keys), copy));
  }
  
  public CacheRegion getCacheRegion() {
    return delegate.getCacheRegion();
  }
  
  public Set getChildren() throws CacheException {
    return delegate.getChildren();
  }
  
  public ElementConfiguration getElementConfiguration() {
    return delegate.getElementConfiguration();
  }
  
  public ElementConfigurationInfo getElementConfigurationInfo() {
    return delegate.getElementConfigurationInfo();
  }
  
  public int hashCode() {
    return delegate.hashCode();
  }
  
  public boolean isEmpty() {
    return delegate.isEmpty();
  }
  
  public Set keySet() {
    return reverseSet(delegate.keySet());
  }
  
  public void put(Object key, Object cachedObject) throws CacheException {
    delegate.put(transformObject(key), cachedObject);
  }
  
  public void put(Object key, Object cachedObject, boolean synchronous,
      boolean suppressInvalidation) throws CacheException {
    delegate.put(transformObject(key), cachedObject, synchronous, suppressInvalidation);
  }
  
  public void put(Object key, Object cachedObject, boolean synchronous,
      byte invalidationScope) throws CacheException {
    delegate.put(transformObject(key), cachedObject, synchronous, invalidationScope);
  }
  
  public void put(Object key, Object cachedObject, Map attributes)
      throws CacheException {
    delegate.put(transformObject(key), cachedObject, attributes);
  }
  
  public void put(Object key, Object cachedObject, Map attributes,
      boolean synchronous, boolean suppressInvalidation) throws CacheException {
    delegate.put(transformObject(key), cachedObject, attributes, synchronous,
        suppressInvalidation);
  }
  
  public void put(Object key, Object cachedObject, Map attributes,
      boolean synchronous, byte invalidationScope) throws CacheException {
    delegate.put(transformObject(key), cachedObject, attributes, synchronous, invalidationScope);
  }
  
  public void putAttributes(Object key, Map attributes) throws CacheException {
    delegate.putAttributes(transformObject(key), attributes);
  }
  
  public void putAttributesBundle(Map keyToAttributes) throws CacheException {
    delegate.putAttributesBundle(transformMap(keyToAttributes));
  }
  
  public void putBundle(Map keyToObject) throws CacheException {
    delegate.putBundle(transformMap(keyToObject));
  }
  
  public void putBundle(Map keyToObject, boolean synchronous,
      boolean suppressInvalidation) throws CacheException {
    delegate.putBundle(transformMap(keyToObject), synchronous, suppressInvalidation);
  }
  
  public void putBundle(Map keyToObject, boolean synchronous,
      byte invalidationScope) throws CacheException {
    delegate.putBundle(transformMap(keyToObject), synchronous, invalidationScope);
  }
  
  public void putBundle(Map keyToObject, Map keyToAttributes)
      throws CacheException {
    delegate.putBundle(transformMap(keyToObject), keyToAttributes);
  }
  
  public void putBundle(Map keyToObject, Map keyToAttributes,
      boolean synchronous, boolean suppressInvalidation) throws CacheException {
    delegate.putBundle(transformMap(keyToObject), keyToAttributes, synchronous,
        suppressInvalidation);
  }
  
  public void putBundle(Map keyToObject, Map keyToAttributes,
      boolean synchronous, byte invalidationScope) throws CacheException {
    delegate.putBundle(transformMap(keyToObject), keyToAttributes, synchronous,
        invalidationScope);
  }
  
  public Object getOrPut(Object key, Object cachedObject) throws CacheException {
    return delegate.getOrPut(transformObject(key), cachedObject);
  }
  
  public Object getOrPut(Object key, Object cachedObject, boolean synchronous,
      boolean suppressInvalidation) throws CacheException {
    return delegate.getOrPut(transformObject(key), cachedObject, synchronous, suppressInvalidation);
  }
  
  public Object getOrPut(Object key, Object cachedObject, boolean synchronous,
      byte invalidationScope) throws CacheException {
    return delegate.getOrPut(transformObject(key), cachedObject, synchronous, invalidationScope);
  }
  
  public Object getOrPut(Object key, Object cachedObject, Map attributes)
      throws CacheException {
    return delegate.getOrPut(transformObject(key), cachedObject, attributes);
  }
  
  public Object getOrPut(Object key, Object cachedObject, Map attributes,
      boolean synchronous, boolean suppressInvalidation) throws CacheException {
    return delegate.getOrPut(transformObject(key), cachedObject, attributes, synchronous,
        suppressInvalidation);
  }
  
  public Object getOrPut(Object key, Object cachedObject, Map attributes,
      boolean synchronous, byte invalidationScope) throws CacheException {
    return delegate.getOrPut(transformObject(key), cachedObject, attributes, synchronous, invalidationScope);
  }
  
  public Map getOrPutAttributes(Object key, Map attributes) throws CacheException {
    return delegate.getOrPutAttributes(transformObject(key), attributes);
  }
  
  public Map getOrPutAttributesBundle(Map keyToAttributes) throws CacheException {
    return delegate.getOrPutAttributesBundle(transformMap(keyToAttributes));
  }
  
  public Map getOrPutBundle(Map keyToObject) throws CacheException {
    return delegate.getOrPutBundle(transformMap(keyToObject));
  }
  
  public Map getOrPutBundle(Map keyToObject, boolean synchronous,
      boolean suppressInvalidation) throws CacheException {
    return delegate.getOrPutBundle(transformMap(keyToObject), synchronous, suppressInvalidation);
  }
  
  public Map getOrPutBundle(Map keyToObject, boolean synchronous,
      byte invalidationScope) throws CacheException {
    return delegate.getOrPutBundle(transformMap(keyToObject), synchronous, invalidationScope);
  }
  
  public Map getOrPutBundle(Map keyToObject, Map keyToAttributes)
      throws CacheException {
    return delegate.getOrPutBundle(transformMap(keyToObject), keyToAttributes);
  }
  
  public Map getOrPutBundle(Map keyToObject, Map keyToAttributes,
      boolean synchronous, boolean suppressInvalidation) throws CacheException {
    return delegate.getOrPutBundle(transformMap(keyToObject), keyToAttributes, synchronous,
        suppressInvalidation);
  }
  
  public Map getOrPutBundle(Map keyToObject, Map keyToAttributes,
      boolean synchronous, byte invalidationScope) throws CacheException {
    return delegate.getOrPutBundle(transformMap(keyToObject), keyToAttributes, synchronous,
        invalidationScope);
  }
  
  public void remove(Object key) {
    delegate.remove(transformObject(key));
  }
  
  public void remove(Object key, boolean synchronous,
      boolean suppressInvalidation) {
    delegate.remove(transformObject(key), synchronous, suppressInvalidation);
  }
  
  public void remove(Object key, boolean synchronous, byte invalidationScope) {
    delegate.remove(transformObject(key), synchronous, invalidationScope);
  }
  
  public void remove(Map attributes) {
    delegate.remove(attributes);
  }
  
  public void remove(Map attributes, boolean synchronous,
      boolean suppressInvalidation) {
    delegate.remove(attributes, synchronous, suppressInvalidation);
  }
  
  public void remove(Map attributes, boolean synchronous, byte invalidationScope) {
    delegate.remove(attributes, synchronous, invalidationScope);
  }
  
  public void removeBundle(Set keySet) {
    delegate.removeBundle(transformSet(keySet));
  }
  
  public void removeBundle(Set keySet, boolean synchronous,
      boolean suppressInvalidation) {
    delegate.removeBundle(transformSet(keySet), synchronous, suppressInvalidation);
  }
  
  public void removeBundle(Set keySet, boolean synchronous,
      byte invalidationScope) {
    delegate.removeBundle(transformSet(keySet), synchronous, invalidationScope);
  }
  
  public void removeChild(String childGroupName) throws CacheException {
    delegate.removeChild(childGroupName);
  }
  public String toString() {
    return delegate.toString();
  }

  public Collection values() {
    return delegate.values();
  }
  
}
