/*==============================================================================
    File:         CacheFacade.java
    Created:      20.07.2004

    $Author: d039261 $
    $Revision: #1 $
    $Date: 2004/07/29 $
==============================================================================*/
package com.sap.util.cache;

import java.util.Map;
import java.util.Set;

import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.WriteException;

/**
 * Cache users access this interface to fulfill basic cache operations like 
 * <code>put()</code>, <code>get()</code> and <code>remove()</code>. 
 * 
 * @author Petio Petev, Michael Wintergerst
 * @version $Revision: #1 $
 */
public interface CacheFacade extends CacheGroup {

    // put

  /**
   * Puts an object associated with a key into a cache group.
   * <p>
   * The behavior of this operation is up to the region configuration.
   * To override the configuration cache users may use other overloaded
   * <code>put()</code> methods.
   * <p>
   * If a <code>put()</code> is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key the object key, later on cache users can get a cached 
   *            object using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on resources
   * @param group the group name that this object will belong to
   * 
   * @throws NullPointerException if one of parameters is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public void put(String key, Object cachedObject, String group) 
      throws CacheException;
  
  /**
   * Puts a set of objects into a cache group. 
   * <p>
   * The behavior of this operation is up to the region configuration.
   * To override the configuration cache users may use other overloaded
   * <code>put()</code> methods.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param keyToObject the objects keys and the objects themselves. 
   *        Later on cache users can get a cached object using its key
   * @param group The group name that this object will belong to
   * 
   * @throws NullPointerException if one of parameters is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public void putBundle(Map keyToObject, String group) throws CacheException;

  /**
   * Puts an object associated with a key into a cache group. 
   * <p>
   * The behavior of this operation is defined by the two flag parameters.
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key the object key, later on cache users can get a cached 
   *            object using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on resources
   * @param group the group name that this object will belong to
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if the <code>key</code>, the 
   *         <code>cachedObject</code> or the <code>group</code> is 
   *         <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public void put(String key, Object cachedObject, String group, 
      boolean synchronous, boolean suppressInvalidation) 
      throws CacheException;

  /**
   * Puts an object associated with a key into a cache group. 
   * <p>
   * The behavior of this operation is defined by the two flag parameters.
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key the object key, later on cache users can get a cached 
   *            object using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on resources
   * @param group the group name that this object will belong to
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if the <code>key</code>, the 
   *         <code>cachedObject</code> or the <code>group</code> is 
   *         <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public void put(String key, Object cachedObject, String group, 
      boolean synchronous, byte invalidationScope) 
      throws CacheException;

  /**
   * Puts a set of objects into a cache group. 
   * <p>
   * The behavior of this put is defined by the two flag parameters.
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param keyToObject The objects keys, and objects themselves. 
   *        Later cache users can get a cached object using its key
   * @param group The group name that this object will belong to
   * 
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if the <code>keyToObject</code> parameter
   *         or the <code>group</code> parameter is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public void putBundle(Map keyToObject, String group, boolean synchronous,
      boolean suppressInvalidation) throws CacheException;

  /**
   * Puts a set of objects into a cache group. 
   * <p>
   * The behavior of this put is defined by the two flag parameters.
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param keyToObject The objects keys, and objects themselves. 
   *        Later cache users can get a cached object using its key
   * @param group The group name that this object will belong to
   * 
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if the <code>keyToObject</code> parameter
   *         or the <code>group</code> parameter is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public void putBundle(Map keyToObject, String group, boolean synchronous,
      byte invalidationScope) throws CacheException;

  /**
   * Puts an object associated with a key and its attributes into a cache
   * group.
   * <p>
   * The behavior of this operation is up to the region configuration.
   * To override the configuration cache users may use other overloaded
   * <code>put()</code> methods.
   * <p>
   * If a <code>put()</code> is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key the object key, later on cache users can get a cached 
   *            object using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on resources
   * @param attributes the attributes that will be assigned to the cached 
   *                   object. Attributes can be separately modified.
   * @param group The group name that this object will belong to
   * 
   * @throws NullPointerException if the <code>key</code>, the
   *         <code>cachedObject</code> parameter or the 
   *         <code>attributes</code> is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public void put(String key, Object cachedObject, Map attributes, 
                  String group) throws CacheException;

  /**
   * Puts a set of objects with their attributes into a cache group. 
   * <p>
   * The behavior of this operation is up to the region configuration.
   * To override the configuration cache users may use other overloaded
   * <code>put()</code> methods.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param keyToObject the objects keys and the objects themselves. 
   *        Later on cache users can get a cached object using its key.
   * @param keyToAttributes The attributes that will be assigned to the 
   *        cached objects keys. Attributes can be separately modified.
   * @param group the group name that this object will belong to
   * 
   * @throws NullPointerException if the <code>keyToObject</code> parameter
   *         or the <code>group</code> parameter is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public void putBundle(Map keyToObject, Map keyToAttributes, String group) 
      throws CacheException;

  /**
   * Puts an object associated with key and its attributes into a cache
   * group.
   * <p>
   * The behavior of this operation is defined by the two flag parameters.
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key The object key, later cache users can get a cached object
   *            using its key
   * @param cachedObject The object that will be or will not be cached 
   *                     depending on the resources
   * @param attributes The attributes that will be assigned to the cached 
   *        object. Attributes can be separately modified.
   * @param group the group name this object will belong to
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if the <code>key</code>, the
   *         <code>cachedObject</code> or the <code>group</code>
   *         parameter is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public void put(String key, Object cachedObject, Map attributes, 
      String group, boolean synchronous, boolean suppressInvalidation) 
      throws CacheException;

  /**
   * Puts an object associated with key and its attributes into a cache
   * group.
   * <p>
   * The behavior of this operation is defined by the two flag parameters.
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key The object key, later cache users can get a cached object
   *            using its key
   * @param cachedObject The object that will be or will not be cached 
   *                     depending on the resources
   * @param attributes The attributes that will be assigned to the cached 
   *        object. Attributes can be separately modified.
   * @param group the group name this object will belong to
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if the <code>key</code>, the
   *         <code>cachedObject</code> or the <code>group</code>
   *         parameter is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public void put(String key, Object cachedObject, Map attributes, 
      String group, boolean synchronous, byte invalidationScope) 
      throws CacheException;

  /**
   * Puts a set of objects with their attributes into a cache group. 
   * 
   * <p>
   * The behavior of this operation is defined by the two flag parameters.
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param keyToObject the objects keys and the objects themselves. 
   *        Later on cache users can get a cached object using its key.
   * @param keyToAttributes The attributes that will be assigned to the 
   *        cached objects keys. Attributes can be separately modified.
   * @param group the group name this object will belong to
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if the <code>keyToObject</code> parameter
   *         or the <code>group</code> parameter is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public void putBundle(Map keyToObject, Map keyToAttributes, String group, 
      boolean synchronous, boolean suppressInvalidation) 
      throws CacheException;

  /**
   * Puts a set of objects with their attributes into a cache group. 
   * 
   * <p>
   * The behavior of this operation is defined by the two flag parameters.
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param keyToObject the objects keys and the objects themselves. 
   *        Later on cache users can get a cached object using its key.
   * @param keyToAttributes The attributes that will be assigned to the 
   *        cached objects keys. Attributes can be separately modified.
   * @param group the group name this object will belong to
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if the <code>keyToObject</code> parameter
   *         or the <code>group</code> parameter is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public void putBundle(Map keyToObject, Map keyToAttributes, String group, 
      boolean synchronous, byte invalidationScope) 
      throws CacheException;

  /**
   * Puts an object associated with a key into a cache group.
   * <p>
   * The behavior of this operation is up to the region configuration.
   * To override the configuration cache users may use other overloaded
   * <code>put()</code> methods.
   * <p>
   * If a <code>put()</code> is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key the object key, later on cache users can get a cached 
   *            object using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on resources
   * @param group the group name that this object will belong to
   * 
   * @throws NullPointerException if one of parameters is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public Object getOrPut(String key, Object cachedObject, String group) 
      throws CacheException;
  
  /**
   * Puts a set of objects into a cache group. 
   * <p>
   * The behavior of this operation is up to the region configuration.
   * To override the configuration cache users may use other overloaded
   * <code>put()</code> methods.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param keyToObject the objects keys and the objects themselves. 
   *        Later on cache users can get a cached object using its key
   * @param group The group name that this object will belong to
   * 
   * @throws NullPointerException if one of parameters is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public Map getOrPutBundle(Map keyToObject, String group) throws CacheException;

  /**
   * Puts an object associated with a key into a cache group. 
   * <p>
   * The behavior of this operation is defined by the two flag parameters.
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key the object key, later on cache users can get a cached 
   *            object using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on resources
   * @param group the group name that this object will belong to
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if the <code>key</code>, the 
   *         <code>cachedObject</code> or the <code>group</code> is 
   *         <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public Object getOrPut(String key, Object cachedObject, String group, 
      boolean synchronous, boolean suppressInvalidation) 
      throws CacheException;

  /**
   * Puts an object associated with a key into a cache group. 
   * <p>
   * The behavior of this operation is defined by the two flag parameters.
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key the object key, later on cache users can get a cached 
   *            object using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on resources
   * @param group the group name that this object will belong to
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if the <code>key</code>, the 
   *         <code>cachedObject</code> or the <code>group</code> is 
   *         <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public Object getOrPut(String key, Object cachedObject, String group, 
      boolean synchronous, byte invalidationScope) 
      throws CacheException;

  /**
   * Puts a set of objects into a cache group. 
   * <p>
   * The behavior of this put is defined by the two flag parameters.
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param keyToObject The objects keys, and objects themselves. 
   *        Later cache users can get a cached object using its key
   * @param group The group name that this object will belong to
   * 
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if the <code>keyToObject</code> parameter
   *         or the <code>group</code> parameter is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public Map getOrPutBundle(Map keyToObject, String group, boolean synchronous,
      boolean suppressInvalidation) throws CacheException;

  /**
   * Puts a set of objects into a cache group. 
   * <p>
   * The behavior of this put is defined by the two flag parameters.
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param keyToObject The objects keys, and objects themselves. 
   *        Later cache users can get a cached object using its key
   * @param group The group name that this object will belong to
   * 
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if the <code>keyToObject</code> parameter
   *         or the <code>group</code> parameter is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public Map getOrPutBundle(Map keyToObject, String group, boolean synchronous,
      byte invalidationScope) throws CacheException;

  /**
   * Puts an object associated with a key and its attributes into a cache
   * group.
   * <p>
   * The behavior of this operation is up to the region configuration.
   * To override the configuration cache users may use other overloaded
   * <code>put()</code> methods.
   * <p>
   * If a <code>put()</code> is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key the object key, later on cache users can get a cached 
   *            object using its key
   * @param cachedObject the object that will be or will not be cached 
   *                     depending on resources
   * @param attributes the attributes that will be assigned to the cached 
   *                   object. Attributes can be separately modified.
   * @param group The group name that this object will belong to
   * 
   * @throws NullPointerException if the <code>key</code>, the
   *         <code>cachedObject</code> parameter or the 
   *         <code>attributes</code> is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public Object getOrPut(String key, Object cachedObject, Map attributes, 
                  String group) throws CacheException;

  /**
   * Puts a set of objects with their attributes into a cache group. 
   * <p>
   * The behavior of this operation is up to the region configuration.
   * To override the configuration cache users may use other overloaded
   * <code>put()</code> methods.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param keyToObject the objects keys and the objects themselves. 
   *        Later on cache users can get a cached object using its key.
   * @param keyToAttributes The attributes that will be assigned to the 
   *        cached objects keys. Attributes can be separately modified.
   * @param group the group name that this object will belong to
   * 
   * @throws NullPointerException if the <code>keyToObject</code> parameter
   *         or the <code>group</code> parameter is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public Map getOrPutBundle(Map keyToObject, Map keyToAttributes, String group) 
      throws CacheException;

  /**
   * Puts an object associated with key and its attributes into a cache
   * group.
   * <p>
   * The behavior of this operation is defined by the two flag parameters.
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key The object key, later cache users can get a cached object
   *            using its key
   * @param cachedObject The object that will be or will not be cached 
   *                     depending on the resources
   * @param attributes The attributes that will be assigned to the cached 
   *        object. Attributes can be separately modified.
   * @param group the group name this object will belong to
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if the <code>key</code>, the
   *         <code>cachedObject</code> or the <code>group</code>
   *         parameter is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public Object getOrPut(String key, Object cachedObject, Map attributes, 
      String group, boolean synchronous, boolean suppressInvalidation) 
      throws CacheException;

  /**
   * Puts an object associated with key and its attributes into a cache
   * group.
   * <p>
   * The behavior of this operation is defined by the two flag parameters.
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param key The object key, later cache users can get a cached object
   *            using its key
   * @param cachedObject The object that will be or will not be cached 
   *                     depending on the resources
   * @param attributes The attributes that will be assigned to the cached 
   *        object. Attributes can be separately modified.
   * @param group the group name this object will belong to
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if the <code>key</code>, the
   *         <code>cachedObject</code> or the <code>group</code>
   *         parameter is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public Object getOrPut(String key, Object cachedObject, Map attributes, 
      String group, boolean synchronous, byte invalidationScope) 
      throws CacheException;

  /**
   * Puts a set of objects with their attributes into a cache group. 
   * 
   * <p>
   * The behavior of this operation is defined by the two flag parameters.
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param keyToObject the objects keys and the objects themselves. 
   *        Later on cache users can get a cached object using its key.
   * @param keyToAttributes The attributes that will be assigned to the 
   *        cached objects keys. Attributes can be separately modified.
   * @param group the group name this object will belong to
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param suppressInvalidation if set to <code>true</code>, no invalidation
   *        messages will be sent
   * 
   * @throws NullPointerException if the <code>keyToObject</code> parameter
   *         or the <code>group</code> parameter is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public Map getOrPutBundle(Map keyToObject, Map keyToAttributes, String group, 
      boolean synchronous, boolean suppressInvalidation) 
      throws CacheException;

  /**
   * Puts a set of objects with their attributes into a cache group. 
   * 
   * <p>
   * The behavior of this operation is defined by the two flag parameters.
   * These parameters override the region configuration.
   * <p>
   * If a <code>put()</code> operation is made a second time on the same 
   * <code>key</code> the new object will overwrite the already existing one.
   *
   * @param keyToObject the objects keys and the objects themselves. 
   *        Later on cache users can get a cached object using its key.
   * @param keyToAttributes The attributes that will be assigned to the 
   *        cached objects keys. Attributes can be separately modified.
   * @param group the group name this object will belong to
   * @param synchronous if set to <code>true</code>, the invalidation 
   *        messages will be sent synchronizely. The scope of the 
   *        invalidation is defined by the region configuration.
   * @param invalidationScope overrides the configuration of the cache. Can be
   *        equal or less then the value configured for the region.
   * 
   * @throws NullPointerException if the <code>keyToObject</code> parameter
   *         or the <code>group</code> parameter is <code>null</code>
   * @throws WriteException if the <code>put()</code> operation did not
   *         succeed
   */
  public Map getOrPutBundle(Map keyToObject, Map keyToAttributes, String group, 
      boolean synchronous, byte invalidationScope) 
      throws CacheException;

    // remove

    /**
     * Removes elements from the cache region. No or several objects will be 
     * removed depending on the group and attributes pattern being specified. 
     * <p>
     * If the pattern is a sub set of the attributes bound to an object key,
     * the cached object with that key is considered applying to the pattern
     * and will be removed.
     * <p>
     * The behavior of the <code>remove()</code> operation is up to the 
     * region configuration. To override the configuration other overloaded
     * <code>remove()</code> methods must be used.
     *
     * @param attributes The attributes that represent a pattern for objects
     *                   targeted to be removed
     * @param group      The group that objects must belong to in order to be
     *                   removed
     * 
     */
    public void remove(Map attributes, String group);

    /**
     * Removes elements from the cache region. No or several objects will be 
     * removed depending on the group and attributes pattern being specified. 
     * <p>
     * If the pattern is a sub set of the attributes bound to an object key,
     * the cached object with that key is considered applying to the pattern
     * and will be removed. 
     * <p>
     * The behavior of the <code>remove()</code> operation is defined by the 
     * two flag parameters. These parameters override the region configuration.
     *
     * @param attributes The attributes that represent a pattern for objects 
     *                   targeted to be removed
     * @param synchronous If set to <code>true</code>, the invalidation and 
     *                    the removal will be synchronized. The scope of the 
     *                    invalidation is defined by the region configuration.
     * @param suppressInvalidation If set to <code>true</code>, no invalidation
     *                             will be made, no listener on other node 
     *                             will receive any events about removal.
     * 
     */
    public void remove(Map attributes, String group, boolean synchronous, 
                       boolean suppressInvalidation);

    /**
     * Removes elements from the cache region. No or several objects will be 
     * removed depending on the group and attributes pattern being specified. 
     * <p>
     * If the pattern is a sub set of the attributes bound to an object key,
     * the cached object with that key is considered applying to the pattern
     * and will be removed. 
     * <p>
     * The behavior of the <code>remove()</code> operation is defined by the 
     * two flag parameters. These parameters override the region configuration.
     *
     * @param attributes The attributes that represent a pattern for objects 
     *                   targeted to be removed
     * @param synchronous If set to <code>true</code>, the invalidation and 
     *                    the removal will be synchronized. The scope of the 
     *                    invalidation is defined by the region configuration.
     * @param invalidationScope overrides the configuration of the cache. Can be
     *        equal or less then the value configured for the region.
     * 
     */
    public void remove(Map attributes, String group, boolean synchronous, 
                       byte invalidationScope);

    // hierarchical support
      
    /**
     * Creates parent-child hierarchical relation respectively between the 
     * specified parent group the specified child group names
     * 
     * @param parentGroupName the name of the parent group in the relation
     * @param childGroupName the name of the child group in the relation
     * @throws CacheException when adding did not succeed
     */
    public void addChild(String parentGroupName, String childGroupName) throws CacheException;
      
    /**
     * Removes parent-child hierarchical relation respectively between the 
     * specified parent group the specified child group names
     * 
     * @param parentGroupName the name of the parent group in the relation
     * @param childGroupName the name of the child group in the relation
     * @throws CacheException when removing did not succeed
     */
    public void removeChild(String parentGroupName, String childGroupName) throws CacheException;
    
    /**
     * Returns all groups names that are children of a group one 
     * current group and the one specified by name
     * 
     * @param groupName the name of the group to get children of
     * @throws CacheException
     */
    public Set getChildren(String groupName) throws CacheException;
    
    /**
     * Returns the size of the objects in the cache region. This method is worth to be used only
     * with SAP VM
     *  
     * @return The size of all objects cached in the region
     */
    public long getRegionSize();
    
    /**
     * Performs a best guess size calculation of the objects in the cache region.
     * <p>
     * WARNING: often usage of this method will cause serious performance degradation.
     *    
     * @return size of the cache region in bytes.
     * 
     * @throws IllegalArgumentException if some of the fields, that must be
     * calculated, could not be traversed.
     * @throws IllegalAccessException if there are security restrictions.
     */
    public int getRegionCalculatedSize() throws IllegalArgumentException, IllegalAccessException;
    
}