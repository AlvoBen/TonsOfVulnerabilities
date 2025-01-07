/*
 * Created on 2004.12.2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.util.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.WriteException;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ExtendedCacheGroup {


  // configuration

  /**
   * Returns the element configuration of the cache region that the 
   * cache user is using.
   *
   * @return cache region element configuration
   */
  public ElementConfigurationInfo getElementConfigurationInfo();

  /**
   * Returns the element configuration of specific cache group in the cache
   * region that the cache user is using.
   *
   * @return Cache user cache region group element configuration.
   */
  public ElementConfiguration getElementConfiguration();

  // put

  /**
   * Puts an object associated with a key into the cache. 
   * The behavior of this operation is up to the region configuration.
   * To override the configuration cache users may use other overloaded
   * <code>put()</code> methods.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key the object key, later on cache users can get a cached object 
   *            using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on resources
   * 
   * @throws NullPointerException if the <code>key</code> or the
   *         <code>cachedObject</code> parameter is <code>null</code> 
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public void put(Object key, Object cachedObject) throws CacheException;

  // get or put
  
  /**
   * Puts an object associated with a key into the cache. 
   * The behavior of this operation is up to the region configuration.
   * To override the configuration cache users may use other overloaded
   * <code>put()</code> methods.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key the object key, later on cache users can get a cached object 
   *            using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on resources
   * 
   * @throws NullPointerException if the <code>key</code> or the
   *         <code>cachedObject</code> parameter is <code>null</code> 
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public Object getOrPut(Object key, Object cachedObject) throws CacheException;

  /**
   * Puts the objects inside the specified map into the cache. 
   * The behavior of this operation is up to the region configuration.
   * To override the configuration cache users may use other overloaded
   * <code>put</code> methods.
   * <p>
   * If a put is made a second time on the same <code>key</code> the new 
   * object will overwrite the already existing one.
   * <p>
   * Note that the object keys must be of type <code>java.lang.String</code>.
   *
   * @param keyToObject the object keys and objects themselves. 
   *        Later on cache users can get a cached object using its key
   * 
   * @throws NullPointerException if one of map entries is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not 
   *         succeed
   */
  public void putBundle(Map keyToObject) throws CacheException;

  /**
   * Puts the objects inside the specified map into the cache. 
   * The behavior of this operation is up to the region configuration.
   * To override the configuration cache users may use other overloaded
   * <code>put</code> methods.
   * <p>
   * If a put is made a second time on the same <code>key</code> the new 
   * object will overwrite the already existing one.
   * <p>
   * Note that the object keys must be of type <code>java.lang.String</code>.
   *
   * @param keyToObject the object keys and objects themselves. 
   *        Later on cache users can get a cached object using its key
   * 
   * @throws NullPointerException if one of map entries is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not 
   *         succeed
   */
  public Map getOrPutBundle(Map keyToObject) throws CacheException;

  /**
   * Puts the object associated with the specified key into the cache. 
   * The behavior of this operation is defined by the two flag parameters. 
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key the object key, later on cache users can get a cached object 
   *            using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on resources
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if the <code>key</code> or the 
   *         <code>cachedObject</code> is <code>null</code>
   * @throws WriteException if the <code>put</code> operation did not succeed
   */
  public void put(Object key, Object cachedObject, boolean synchronous, 
                  boolean suppressInvalidation) throws CacheException;

  /**
   * Puts the object associated with the specified key into the cache. 
   * The behavior of this operation is defined by the two flag parameters. 
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key the object key, later on cache users can get a cached object 
   *            using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on resources
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if the <code>key</code> or the 
   *         <code>cachedObject</code> is <code>null</code>
   * @throws WriteException if the <code>put</code> operation did not succeed
   */
  public Object getOrPut(Object key, Object cachedObject, boolean synchronous, 
                  boolean suppressInvalidation) throws CacheException;

  /**
   * Puts the object associated with the specified key into the cache. 
   * The behavior of this operation is defined by the two flag parameters. 
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key the object key, later on cache users can get a cached object 
   *            using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on resources
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if the <code>key</code> or the 
   *         <code>cachedObject</code> is <code>null</code>
   * @throws WriteException if the <code>put</code> operation did not succeed
   */
  public void put(Object key, Object cachedObject, boolean synchronous, 
                  byte invalidationScope) throws CacheException;

  /**
   * Puts the object associated with the specified key into the cache. 
   * The behavior of this operation is defined by the two flag parameters. 
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key the object key, later on cache users can get a cached object 
   *            using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on resources
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if the <code>key</code> or the 
   *         <code>cachedObject</code> is <code>null</code>
   * @throws WriteException if the <code>put</code> operation did not succeed
   */
  public Object getOrPut(Object key, Object cachedObject, boolean synchronous, 
                  byte invalidationScope) throws CacheException;

  /**
   * Puts the objects inside the specified map into the cache. 
   * The behavior of this operation is defined by the two flag parameters. 
   * These parameters override the region configuration.
   * <p>
   * If a <code>put</code> operation is made a second time on the same 
   * <code>key</code> the new  object will overwrite the already existing 
   * one.
   * <p>
   * Note that the object keys must be of type <code>java.lang.String</code>.
   *
   * @param keyToObject the object keys and objects themselves. 
   *        Later on cache users can get a cached object using its key.
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if one of map entries is <code>null</code>
   * @throws WriteException if the <code>put</code> operation did not succeed
   */
  public void putBundle(Map keyToObject, boolean synchronous, 
                        boolean suppressInvalidation) throws CacheException;

  /**
   * Puts the objects inside the specified map into the cache. 
   * The behavior of this operation is defined by the two flag parameters. 
   * These parameters override the region configuration.
   * <p>
   * If a <code>put</code> operation is made a second time on the same 
   * <code>key</code> the new  object will overwrite the already existing 
   * one.
   * <p>
   * Note that the object keys must be of type <code>java.lang.String</code>.
   *
   * @param keyToObject the object keys and objects themselves. 
   *        Later on cache users can get a cached object using its key.
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if one of map entries is <code>null</code>
   * @throws WriteException if the <code>put</code> operation did not succeed
   */
  public Map getOrPutBundle(Map keyToObject, boolean synchronous, 
                        boolean suppressInvalidation) throws CacheException;

  /**
   * Puts the objects inside the specified map into the cache. 
   * The behavior of this operation is defined by the two flag parameters. 
   * These parameters override the region configuration.
   * <p>
   * If a <code>put</code> operation is made a second time on the same 
   * <code>key</code> the new  object will overwrite the already existing 
   * one.
   * <p>
   * Note that the object keys must be of type <code>java.lang.String</code>.
   *
   * @param keyToObject the object keys and objects themselves. 
   *        Later on cache users can get a cached object using its key.
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if one of map entries is <code>null</code>
   * @throws WriteException if the <code>put</code> operation did not succeed
   */
  public void putBundle(Map keyToObject, boolean synchronous, 
                        byte invalidationScope) throws CacheException;

  /**
   * Puts the objects inside the specified map into the cache. 
   * The behavior of this operation is defined by the two flag parameters. 
   * These parameters override the region configuration.
   * <p>
   * If a <code>put</code> operation is made a second time on the same 
   * <code>key</code> the new  object will overwrite the already existing 
   * one.
   * <p>
   * Note that the object keys must be of type <code>java.lang.String</code>.
   *
   * @param keyToObject the object keys and objects themselves. 
   *        Later on cache users can get a cached object using its key.
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if one of map entries is <code>null</code>
   * @throws WriteException if the <code>put</code> operation did not succeed
   */
  public Map getOrPutBundle(Map keyToObject, boolean synchronous, 
                        byte invalidationScope) throws CacheException;

  /**
   * Puts the cached object with the corresponding attributes and associated
   * with the specified key into the cache. 
   * The behavior of this operation is up to the region configuration. 
   * To override the configuration cache users may use other overloaded
   * <code>put()</code> methods.
   * <p> 
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   * 
   * @param key the object key, later on cache users can get a cached object 
   *            using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on the resources
   * @param attributes the attributes that will be assigned to the cached
   *                   object. Attributes can be separately modified.
   * 
   * @throws NullPointerException if the <code>key</code> or the 
   *         <code>cachedObject</code> is <code>null</code>
   * @throws WriteException if the <code>put</code> operation did not succeed
   */
  public void put(Object key, Object cachedObject, Map attributes) 
      throws CacheException;

  /**
   * Puts the cached object with the corresponding attributes and associated
   * with the specified key into the cache. 
   * The behavior of this operation is up to the region configuration. 
   * To override the configuration cache users may use other overloaded
   * <code>put()</code> methods.
   * <p> 
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   * 
   * @param key the object key, later on cache users can get a cached object 
   *            using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on the resources
   * @param attributes the attributes that will be assigned to the cached
   *                   object. Attributes can be separately modified.
   * 
   * @throws NullPointerException if the <code>key</code> or the 
   *         <code>cachedObject</code> is <code>null</code>
   * @throws WriteException if the <code>put</code> operation did not succeed
   */
  public Object getOrPut(Object key, Object cachedObject, Map attributes) 
      throws CacheException;

  /**
   * Puts the objects within the specified map and the corresponding 
   * attributes into the cache.  The behavior of this operation is up to the
   * region configuration. To override the configuration cache users may use
   * other overloaded <code>put()</code> methods.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param keyToObject the object keys and the objects themselves. 
   *        Later on cache users can get a cached object using its key
   * @param keyToAttributes The attributes that will be assigned to the 
   *        cached objects keys. Attributes can be modified separately.
   * 
   * @throws NullPointerException if the <code>key</code> or the
   *         <code>cachedObject</code> is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not 
   *         succeed
   */
  public void putBundle(Map keyToObject, Map keyToAttributes) 
      throws CacheException;

  /**
   * Puts the objects within the specified map and the corresponding 
   * attributes into the cache.  The behavior of this operation is up to the
   * region configuration. To override the configuration cache users may use
   * other overloaded <code>put()</code> methods.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param keyToObject the object keys and the objects themselves. 
   *        Later on cache users can get a cached object using its key
   * @param keyToAttributes The attributes that will be assigned to the 
   *        cached objects keys. Attributes can be modified separately.
   * 
   * @throws NullPointerException if the <code>key</code> or the
   *         <code>cachedObject</code> is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not 
   *         succeed
   */
  public Map getOrPutBundle(Map keyToObject, Map keyToAttributes) 
      throws CacheException;

  /**
   * Puts an object with the corresponding attributes and associated with
   * the specified object key into the cache. The behavior of this operation
   * is defined by the two flag parameters. These parameters override the
   * region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key the object key, later on cache users can get a cached object 
   *            using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on the resources
   * @param attributes the attributes that will be assigned to the cached
   *                   object. Attributes can be separately modified.
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if the <code>key</code> or the 
   *         <code>cachedObject</code> is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public void put(Object key, Object cachedObject, Map attributes, 
          boolean synchronous, boolean suppressInvalidation) 
          throws CacheException;

  /**
   * Puts an object with the corresponding attributes and associated with
   * the specified object key into the cache. The behavior of this operation
   * is defined by the two flag parameters. These parameters override the
   * region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key the object key, later on cache users can get a cached object 
   *            using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on the resources
   * @param attributes the attributes that will be assigned to the cached
   *                   object. Attributes can be separately modified.
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if the <code>key</code> or the 
   *         <code>cachedObject</code> is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public Object getOrPut(Object key, Object cachedObject, Map attributes, 
          boolean synchronous, boolean suppressInvalidation) 
          throws CacheException;

  /**
   * Puts an object with the corresponding attributes and associated with
   * the specified object key into the cache. The behavior of this operation
   * is defined by the two flag parameters. These parameters override the
   * region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key the object key, later on cache users can get a cached object 
   *            using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on the resources
   * @param attributes the attributes that will be assigned to the cached
   *                   object. Attributes can be separately modified.
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if the <code>key</code> or the 
   *         <code>cachedObject</code> is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public void put(Object key, Object cachedObject, Map attributes, 
          boolean synchronous, byte invalidationScope) 
          throws CacheException;

  /**
   * Puts an object with the corresponding attributes and associated with
   * the specified object key into the cache. The behavior of this operation
   * is defined by the two flag parameters. These parameters override the
   * region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key the object key, later on cache users can get a cached object 
   *            using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on the resources
   * @param attributes the attributes that will be assigned to the cached
   *                   object. Attributes can be separately modified.
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if the <code>key</code> or the 
   *         <code>cachedObject</code> is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public Object getOrPut(Object key, Object cachedObject, Map attributes, 
          boolean synchronous, byte invalidationScope) 
          throws CacheException;

  /**
   * Puts the objects within the specified map and the corresponding 
   * attributes into the cache.  The behavior of this put is defined by the 
   * two flag parameters. These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param keyToObject the object keys and the objects themselves. 
   *        Later on cache users can get a cached object using its key
   * @param keyToAttributes The attributes that will be assigned to the 
   *        cached objects keys. Attributes can be modified separately.
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if the <code>key</code> or the 
   *          <code>cachedObject</code> is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not 
   *         succeed
   */
  public void putBundle(Map keyToObject, Map keyToAttributes, 
          boolean synchronous, boolean suppressInvalidation) 
          throws CacheException;

  /**
   * Puts the objects within the specified map and the corresponding 
   * attributes into the cache.  The behavior of this put is defined by the 
   * two flag parameters. These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param keyToObject the object keys and the objects themselves. 
   *        Later on cache users can get a cached object using its key
   * @param keyToAttributes The attributes that will be assigned to the 
   *        cached objects keys. Attributes can be modified separately.
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if the <code>key</code> or the 
   *          <code>cachedObject</code> is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not 
   *         succeed
   */
  public Map getOrPutBundle(Map keyToObject, Map keyToAttributes, 
          boolean synchronous, boolean suppressInvalidation) 
          throws CacheException;

  /**
   * Puts the objects within the specified map and the corresponding 
   * attributes into the cache.  The behavior of this put is defined by the 
   * two flag parameters. These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param keyToObject the object keys and the objects themselves. 
   *        Later on cache users can get a cached object using its key
   * @param keyToAttributes The attributes that will be assigned to the 
   *        cached objects keys. Attributes can be modified separately.
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if the <code>key</code> or the 
   *          <code>cachedObject</code> is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not 
   *         succeed
   */
  public void putBundle(Map keyToObject, Map keyToAttributes, 
          boolean synchronous, byte invalidationScope) 
          throws CacheException;

  /**
   * Puts the objects within the specified map and the corresponding 
   * attributes into the cache.  The behavior of this put is defined by the 
   * two flag parameters. These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param keyToObject the object keys and the objects themselves. 
   *        Later on cache users can get a cached object using its key
   * @param keyToAttributes The attributes that will be assigned to the 
   *        cached objects keys. Attributes can be modified separately.
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if the <code>key</code> or the 
   *          <code>cachedObject</code> is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not 
   *         succeed
   */
  public Map getOrPutBundle(Map keyToObject, Map keyToAttributes, 
          boolean synchronous, byte invalidationScope) 
          throws CacheException;

  /**
   * Assigns attributes to a cached object key, disregarding of the existence
   * of that cached object (disregarding of predecessing puts). 
   * <p>
   * Attributes are overwritten if a <code>put()</code> operation 
   * is made a second time. 
   * <p>
   * The behavior of this operation is up to the region configuration. 
   * To override the configuration> cache users may use other overloaded 
   * <code>putAttributes()</code> methods.
   *
   * @param key The object key that the attributes will be assigned to
   * @param attributes The attributes that will be assigned to the cached object
   * 
   * @throws NullPointerException if the <code>key</code> is 
   *         <code>null</code> 
   * @throws WriteException if the specified <code>attributes</code> cannot
   *         be put into the cache
   */
  public void putAttributes(Object key, Map attributes) 
      throws CacheException;

  /**
   * Assigns attributes to a cached object key, disregarding of the existence
   * of that cached object (disregarding of predecessing puts). 
   * <p>
   * Attributes are overwritten if a <code>put()</code> operation 
   * is made a second time. 
   * <p>
   * The behavior of this operation is up to the region configuration. 
   * To override the configuration> cache users may use other overloaded 
   * <code>putAttributes()</code> methods.
   *
   * @param key The object key that the attributes will be assigned to
   * @param attributes The attributes that will be assigned to the cached object
   * 
   * @throws NullPointerException if the <code>key</code> is 
   *         <code>null</code> 
   * @throws WriteException if the specified <code>attributes</code> cannot
   *         be put into the cache
   */
  public Map getOrPutAttributes(Object key, Map attributes) 
      throws CacheException;

  /**
   * Assigns attributes to a cached object key, disregarding of the existence
   * of that cached object (disregarding of predecessing puts). 
   * <p>
   * Attributes are overwritten if a <code>put()</code> operation 
   * is made a second time. 
   * <p>
   * The behavior of this operation is up to the region configuration. 
   * To override the configuration> cache users may use other overloaded 
   * <code>putAttribute()</code> methods.
   * <p>
   * Note that the keys inside the specified map must be of type
   * <code>java.lang.String</code>.
   *
   * @param keyToAttributes The attributes that will be assigned to the 
   *        cached objects keys
   * 
   * @throws NullPointerException if the <code>keyToAttributes</code> 
   *         parameter is <code>null</code>
   * @throws WriteException if the specified <code>attributes</code> cannot
   *         be put into the cache
   */
  public void putAttributesBundle(Map keyToAttributes) throws CacheException;

  /**
   * Assigns attributes to a cached object key, disregarding of the existence
   * of that cached object (disregarding of predecessing puts). 
   * <p>
   * Attributes are overwritten if a <code>put()</code> operation 
   * is made a second time. 
   * <p>
   * The behavior of this operation is up to the region configuration. 
   * To override the configuration> cache users may use other overloaded 
   * <code>putAttribute()</code> methods.
   * <p>
   * Note that the keys inside the specified map must be of type
   * <code>java.lang.String</code>.
   *
   * @param keyToAttributes The attributes that will be assigned to the 
   *        cached objects keys
   * 
   * @throws NullPointerException if the <code>keyToAttributes</code> 
   *         parameter is <code>null</code>
   * @throws WriteException if the specified <code>attributes</code> cannot
   *         be put into the cache
   */
  public Map getOrPutAttributesBundle(Map keyToAttributes) throws CacheException;

  // get

  /**
   * Gets an object from the cache group. 
   * <p>
   * Note that the returned object should be regarded as a read-only
   * object. It depends on the configured storage plugin what will be 
   * happened if the returned object is modified.
   *
   * @param key the key of the cached object to be returned
   * 
   * @return the cached object or <code>null</code>
   * 
   * @throws NullPointerException if <code>key</code> is <code>null</code>
   */
  public Object get(Object key);

  /**
   * Gets an object from the cache group. 
   * <p>
   * If the <code>copy</code> parameter is set to <code>true</code>, 
   * the returned object is a copy, which can be modified. However, 
   * the changes are not reflected directly in the cache.
   * <p>
   * If the <code>copy</code> parameter is set to <code>false</code>,
   * the behavior is identical to calling {@link #get(java.lang.String)}.
   *
   * @param key the key of the cached object to be returned
   * @param copy indicates whether or not the returned object should
   *             be changeable 
   * 
   * @return the cached object or <code>null</code>
   * 
   * @throws NullPointerException if <code>key</code> is <code>null</code>
   */
  public Object get(Object key, boolean copy);

  /**
   * Gets the attributes bound to the specified cached object keys.
   * <p>
   * Note that the returned maps should be regarded as read-only
   * objects. It depends on the configured storage plugin what will be 
   * happened if the returned map is modified.
   *
   * @param keys the keys of the cached objects which attributes are to be 
   *             returned 
   * 
   * @return the attributes bound to the cached object keys 
   * 
   * @throws NullPointerException if the <code>key</code> parameter or one
   *         of the objects inside the specified array is <code>null</code>
   */
  public Map getBundle(Set keys);

  /**
   * Gets the attributes bound to a specific cached object key.
   * <p>
   * If the <code>copy</code> parameter is set to <code>true</code>, 
   * the returned map is a copy, which can be modified. However, 
   * the changes are not reflected directly in the cache.
   * <p>
   * If the <code>copy</code> parameter is set to <code>false</code>,
   * the behavior is identical to calling 
   * {@link #getAttributes(java.lang.String)}.
   *
   * @param keys the key the cache user wants to get attributes from
   * @param copy indicates whether or not the returned object should
   *             be changeable 
   * 
   * @return the attributes bound to the cached object key or 
   *         <code>null</code>
   * 
   * @throws NullPointerException if <code>key</code> is <code>null</code>
   */
  public Map getBundle(Set keys, boolean copy);

  /**
   * Gets the attributes bound to a specific cached object key.
   * <p>
   * Note that the returned map should be regarded as a read-only
   * object. It depends on the configured storage plugin what will be 
   * happened if the returned map is modified.
   *
   * @param key the key the cache user wants to get attributes from
   * 
   * @return the attributes bound to the cached object key or 
   *         <code>null</code>
   * 
   * @throws NullPointerException if <code>key</code> is <code>null</code>
   */
  public Map getAttributes(Object key);

  /**
   * Gets the attributes bound to a specific cached object key.
   * <p>
   * If the <code>copy</code> parameter is set to <code>true</code>, 
   * the returned map is a copy, which can be modified. However, 
   * the changes are not reflected directly in the cache.
   * <p>
   * If the <code>copy</code> parameter is set to <code>false</code>,
   * the behavior is identical to calling 
   * {@link #getAttributes(java.lang.String)}.
   *
   * @param key the key the cache user wants to get attributes from
   * @param copy indicates whether or not the returned object should
   *             be changeable 
   * 
   * @return the attributes bound to the cached object key or 
   *         <code>null</code>
   * 
   * @throws NullPointerException if <code>key</code> is <code>null</code>
   */
  public Map getAttributes(Object key, boolean copy);

  // remove

  /**
   * Removes all cached objects from the cache group. 
   * <p>
   * The behavior of this removal is up to the region configuration. 
   * To override the configuration cache users may use overloaded
   * <code>remove()</code> methods.
   */
  public void clear();

  /**
   * Removes an object identified by the specified <code>key</code> 
   * parameter. 
   * <p>
   * The behavior of this removal is up to the region configuration. 
   * To override the configuration cache users may use other overloaded
   * <code>remove()</code> methods.
   * <p> 
   * If the specified object key is not in the cache, a call to this
   * method has no affect.
   *
   * @param key the object key of the cached object the cache user wants
   *        to remove
   * 
   * @throws NullPointerException if <code>key</code> is <code>null</code>
   */
  public void remove(Object key);

  /**
   * Removes the objects identified by the <code>keySet</code> parameter. 
   * <p>
   * The behavior of this removal is up to the region configuration. 
   * To override the configuration cache users may use other overloaded
   * <code>remove()</code> methods.
   * <p> 
   * If one of the specified object keys is not in the cache, this has no
   * affect.
   *
   * @param keySet the objects keys of the cached objects the cache user
   *               wants to remove
   * 
   * @throws NullPointerException if the <code>keySet</code> parameter 
   *         is <code>null</code>
   */
  public void removeBundle(Set keySet);

  /**
   * Removes objects identified by the specified attributes from the cache. 
   * <p>
   * No or several objects will be removed depending on the attribute 
   * pattern. If the pattern is a sub set of the attributes bound to an 
   * object key, the cached object with that key is considered applying to 
   * the pattern and will be removed.
   * <p>
   * The behavior of this removal is up to the region configuration.
   * To override the configuration cache users may use other overloaded
   * <code>remove()</code> methods.
   *
   * @param attributes the attributes representing a pattern for objects
   *        targeted to be removed
   * 
   * @throws NullPointerException if <code>attributes</code> parameter is
   *         <code>null</code>
   */
  public void remove(Map attributes);

  /**
   * Removes an object identified by the specified key from the key. 
   * <p>
   * The behavior of this removal is defined by the two flag parameters. 
   * These parameters override the region configuration.
   *
   * @param key the object key of the cached object the cache user wants
   *            to remove.
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if the <code>key</code> parameter 
   *         is <code>null</code>
   */
  public void remove(Object key, boolean synchronous, 
                     boolean suppressInvalidation);

  /**
   * Removes an object identified by the specified key from the key. 
   * <p>
   * The behavior of this removal is defined by the two flag parameters. 
   * These parameters override the region configuration.
   *
   * @param key the object key of the cached object the cache user wants
   *            to remove.
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if the <code>key</code> parameter 
   *         is <code>null</code>
   */
  public void remove(Object key, boolean synchronous, 
                     byte invalidationScope);

  /**
   * Removes the objects specified by the <code>keySet</code> parameter from
   * the cache group. 
   * <p> 
   * The behavior of this removal is defined by the two flag parameters. 
   * These parameters override the region configuration.
   *
   * @param keySet the objects keys of the cached objects the cache user 
   *        wants to remove
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if the <code>keySet</code> parameter 
   *         is <code>null</code>
   */
  public void removeBundle(Set keySet, boolean synchronous, 
                           boolean suppressInvalidation);

  /**
   * Removes the objects specified by the <code>keySet</code> parameter from
   * the cache group. 
   * <p> 
   * The behavior of this removal is defined by the two flag parameters. 
   * These parameters override the region configuration.
   *
   * @param keySet the objects keys of the cached objects the cache user 
   *        wants to remove
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if the <code>keySet</code> parameter 
   *         is <code>null</code>
   */
  public void removeBundle(Set keySet, boolean synchronous, 
                           byte invalidationScope);

  /**
   * Removes objects identified by the specified attributes from the cache. 
   * <p>
   * No or several objects will be removed depending on the attribute
   * pattern. If the pattern is a sub set of the attributes bound to an 
   * object key, the cached object with that key is considered applying to
   * the pattern and will be removed.
   * <p>
   * The behavior of this removal is defined by the two flag parameters. 
   * These parameters override the region configuration.
   *
   * @param attributes the attributes representing a pattern for objects 
   *        targeted to be removed
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if the <code>attributes</code> parameter 
   *         is <code>null</code>
   */
  public void remove(Map attributes, boolean synchronous, 
                     boolean suppressInvalidation);

  /**
   * Removes objects identified by the specified attributes from the cache. 
   * <p>
   * No or several objects will be removed depending on the attribute
   * pattern. If the pattern is a sub set of the attributes bound to an 
   * object key, the cached object with that key is considered applying to
   * the pattern and will be removed.
   * <p>
   * The behavior of this removal is defined by the two flag parameters. 
   * These parameters override the region configuration.
   *
   * @param attributes the attributes representing a pattern for objects 
   *        targeted to be removed
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if the <code>attributes</code> parameter 
   *         is <code>null</code>
   */
  public void remove(Map attributes, boolean synchronous, 
                     byte invalidationScope);

  // map-like methods

  /**
   * Checks if the cache group contains no cached objects.
   *
   * @return <code>true</code> if the region contains no cached objects;
   *         otherwise <code>false</code> is returned
   */
  boolean isEmpty();

  /**
   * Checks whether the specified object key is in the cache.
   * 
   * @param key the cached object key whose presence in the region is
   *        to be tested
   * 
   * @return <code>true</code> if the group contains a cached object bound
   *         to the specified cached object key; otherwise <code>false</code>
   *         is returned
   * @throws NullPointerException if the <code>key</code> parameter is
   *         <code>null</code>
   */
  boolean containsKey(Object key);

  /**
   * Returns a set of the keys contained in the region. 
   * <p>
   * The key set is a copy, so operations over the returned set are not 
   * reflected in the group and vice-versa. However, it is possible to 
   * remove keys from the underlying cache group.
   *
   * @return a set of the keys contained in the region
   */
  public Set keySet();

  /**
   * Returns a collection of the cached objects contained in the group.  
   * <p>
   * The collection is a copy, so operations over the returned collection 
   * are not reflected in the region and vice-versa.
   *
   * @return a collection of the cached objects contained in the region
   */
  public Collection values();
  
  // hierarchical support
  
  /**
   * Creates parent-child hierarchical relation respectively between the 
   * current group and the one specified by name
   * 
   * @param childGroupName the name of the child group in the relation
   * @throws CacheException when adding did not succeed
   */
  public void addChild(String childGroupName) throws CacheException;
  
  /**
   * Removes parent-child hierarchical relation respectively between the 
   * current group and the one specified by name
   * 
   * @param childGroupName the name of the child group in the relation
   * @throws CacheException when removing did not succeed
   */
  public void removeChild(String childGroupName) throws CacheException;
  
  /**
   * Returns all groups names that are children of the current one 
   * current group and the one specified by name
   * 
   * @throws CacheException
   */
  public Set getChildren() throws CacheException;
  
  // navigation between interfaces
  
  /**
   * Returns the CacheRegion instance that contains this group.
   * 
   * @return the cache region containing this group
   */
  public CacheRegion getCacheRegion();
  
  /**
   * Gives the user the oportunity to transform object to key (and vice versa)
   * in a proprietary way.
   * 
   * @param transformer User implementation of transformer;
   */
  public void hookTransformer(ObjectKeyTransformer transformer);
  
}
