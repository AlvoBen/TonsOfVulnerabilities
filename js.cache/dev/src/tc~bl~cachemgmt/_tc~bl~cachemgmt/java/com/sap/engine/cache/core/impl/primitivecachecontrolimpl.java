/*
 * Created on 2005.8.8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.cache.core.impl;

import java.util.Map;
import java.util.Set;

import com.sap.util.cache.CacheControl;
import com.sap.util.cache.InvalidationListener;
import com.sap.util.cache.PrimitiveCacheControl;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrimitiveCacheControlImpl extends PrimitiveKeyTransformer implements PrimitiveCacheControl {

  private CacheControl delegate = null;
  
  public PrimitiveCacheControlImpl(CacheControl cacheControl) {
    this.delegate = cacheControl;
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ExtendedCacheControl#registerInvalidationListener(com.sap.util.cache.InvalidationListener)
   */
  public void registerInvalidationListener(InvalidationListener iListener) {
    delegate.registerInvalidationListener(iListener);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ExtendedCacheControl#unregisterInvalidationListener(com.sap.util.cache.InvalidationListener)
   */
  public void unregisterInvalidationListener(InvalidationListener iListener) {
    delegate.unregisterInvalidationListener(iListener);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ExtendedCacheControl#invalidate(java.lang.Object)
   */
  public void invalidate(long key) {
    delegate.invalidate(transformLong(key));
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ExtendedCacheControl#invalidate(java.lang.Object, byte)
   */
  public void invalidate(long key, byte invalidationScope) {
    delegate.invalidate(transformLong(key), invalidationScope);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ExtendedCacheControl#invalidateBundle(java.util.Set)
   */
  public void invalidateBundle(Set keySet) {
    delegate.invalidateBundle(transformSet(keySet));
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ExtendedCacheControl#invalidateBundle(java.util.Set, byte)
   */
  public void invalidateBundle(Set keySet, byte invalidationScope) {
    delegate.invalidateBundle(transformSet(keySet), invalidationScope);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ExtendedCacheControl#invalidate(java.util.Map)
   */
  public void invalidate(Map attributes) {
    delegate.invalidate(attributes);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ExtendedCacheControl#invalidate(java.util.Map, byte)
   */
  public void invalidate(Map attributes, byte invalidationScope) {
    delegate.invalidate(attributes, invalidationScope);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ExtendedCacheControl#invalidate(java.util.Map, java.lang.String)
   */
  public void invalidate(Map attributes, String group) {
    delegate.invalidate(attributes, group);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ExtendedCacheControl#invalidate(java.util.Map, java.lang.String, byte)
   */
  public void invalidate(Map attributes, String group, byte invalidationScope) {
    delegate.invalidate(attributes, group, invalidationScope);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ExtendedCacheControl#invalidate(java.lang.Object, boolean)
   */
  public void invalidate(long key, boolean synchronous) {
    delegate.invalidate(transformLong(key), synchronous);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ExtendedCacheControl#invalidate(java.lang.Object, boolean, byte)
   */
  public void invalidate(long key, boolean synchronous, byte invalidationScope) {
    delegate.invalidate(transformLong(key), synchronous, invalidationScope);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ExtendedCacheControl#invalidateBundle(java.util.Set, boolean)
   */
  public void invalidateBundle(Set keySet, boolean synchronous) {
    delegate.invalidateBundle(transformSet(keySet), synchronous);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ExtendedCacheControl#invalidateBundle(java.util.Set, boolean, byte)
   */
  public void invalidateBundle(Set keySet, boolean synchronous,
      byte invalidationScope) {
    delegate.invalidateBundle(transformSet(keySet), synchronous, invalidationScope);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ExtendedCacheControl#invalidate(java.util.Map, boolean)
   */
  public void invalidate(Map attributes, boolean synchronous) {
    delegate.invalidate(attributes, synchronous);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ExtendedCacheControl#invalidate(java.util.Map, boolean, byte)
   */
  public void invalidate(Map attributes, boolean synchronous,
      byte invalidationScope) {
    delegate.invalidate(attributes, synchronous, invalidationScope);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ExtendedCacheControl#invalidate(java.util.Map, java.lang.String, boolean)
   */
  public void invalidate(Map attributes, String group, boolean synchronous) {
    delegate.invalidate(attributes, group, synchronous);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ExtendedCacheControl#invalidate(java.util.Map, java.lang.String, boolean, byte)
   */
  public void invalidate(Map attributes, String group, boolean synchronous,
      byte invalidationScope) {
    delegate.invalidate(attributes, group, synchronous, invalidationScope);
  }


}
