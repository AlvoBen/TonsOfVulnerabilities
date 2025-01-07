/*==============================================================================
    File:         CacheControl.java
    Created:      20.07.2004

    $Author: d039261 $
    $Revision: #1 $
    $Date: 2004/07/29 $
==============================================================================*/
package com.sap.util.cache;

import java.util.Map;
import java.util.Set;

/**
 * Cache users access this interface to register 
 * <code>InvalidationListener</code>-s and explicitly invalidate objects bound
 * to specific keys.
 * 
 * @author Petio Petev, Michael Wintergerst
 */
public interface CacheControl {

    /**
     * Registers invalidation listener for the region. 
     * <code>InvalidationListener</code>-s will get events when objects are 
     * removed, modified or explicitly invalidated
     *
     * @param iListener The <code>InvalidationListener</code> instance that the
     *        cache user provides
     * 
     * @throws NullPointerException if <code>iListener</code> is 
     *         <code>null</code>
     */
    public void registerInvalidationListener(InvalidationListener iListener);

    /**
     * Unregisters invalidation listener from the region.
     *
     * @param iListener The <code>InvalidationListener</code> instance that 
     *        will be unregistered
     * 
     * @throws NullPointerException if <code>iListener</code> is 
     *         <code>null</code>
     */
    public void unregisterInvalidationListener(InvalidationListener iListener);

    /**
     * Explicitly invalidates an object that is bound to the specified key. 
     * Explicit invalidation will provoke events firing.
     * The behavior of this invalidation is up to the region 
     * <code>configuration</code>. To override the <code>configuration</code>
     * cache users may use another <code>invalidate</code> method.
     *
     * @param key The key that cache user provides to specify the object that
     *        will be invalidated
     */
    public void invalidate(String key);

    /**
     * Explicitly invalidates an object that is bound to the specified key. 
     * Explicit invalidation will provoke events firing.
     * The behavior of this invalidation is up to the region 
     * <code>configuration</code>. To override the <code>configuration</code>
     * cache users may use another <code>invalidate</code> method.
     *
     * @param key The key that cache user provides to specify the object that
     *        will be invalidated
     * @param invalidationScope - the overriden scope of the invalidation
     */
    public void invalidate(String key, byte invalidationScope);

    /**
     * Explicitly invalidates objects that are bound to the specified keys. 
     * Explicit invalidation will provoke events firing.
     * The behavior of this invalidation is up to the region 
     * <code>configuration</code>.
     * To override the <code>configuration</code> cache users may use another 
     * <code>invalidate()</code> method.
     *
     * @param keySet The keys that cache user provides to specify the objects
     *        that will be invalidated
     */
    public void invalidateBundle(Set keySet);

    /**
     * Explicitly invalidates objects that are bound to the specified keys. 
     * Explicit invalidation will provoke events firing.
     * The behavior of this invalidation is up to the region 
     * <code>configuration</code>.
     * To override the <code>configuration</code> cache users may use another 
     * <code>invalidate()</code> method.
     *
     * @param keySet The keys that cache user provides to specify the objects
     *        that will be invalidated
     * @param invalidationScope - the overriden scope of the invalidation
     */
    public void invalidateBundle(Set keySet, byte invalidationScope);

    /**
     * Explicitly invalidates objects based on attributes pattern. If the pattern is a sub set of the attributes bound to an object key,
     * the cached object with that key is considered applying to the pattern and will be invalidated.
     * The behavior of this invalidation is up to the region <code>configuration</code>.
     * To override the <code>configuration</code> cache users may use another invalidate method.
     *
     * @param attributes The attributes that represent a pattern for objects targeted to be invalidated
     */
    public void invalidate(Map attributes);

    /**
     * Explicitly invalidates objects based on attributes pattern. If the pattern is a sub set of the attributes bound to an object key,
     * the cached object with that key is considered applying to the pattern and will be invalidated.
     * The behavior of this invalidation is up to the region <code>configuration</code>.
     * To override the <code>configuration</code> cache users may use another invalidate method.
     *
     * @param attributes The attributes that represent a pattern for objects targeted to be invalidated
     * @param invalidationScope - the overriden scope of the invalidation
     */
    public void invalidate(Map attributes, byte invalidationScope);
  
    /**
     * Explicitly invalidates objects based on attributes pattern and a group. If the pattern is a sub set of the attributes bound to an
     * object key, belonging to the specified group the cached object with that key is considered applying to
     * the pattern and will be invalidated.
     * The behavior of this invalidation is up to the region <code>configuration</code>.
     * To override the <code>configuration</code> cache users may use another invalidate method.
     *
     * @param attributes The attributes that represent a pattern for objects targeted to be invalidated
     * @param group The group that objects must belong to in order to be invalidated
     * If <code>null</code>, all object apply to them
     */
    public void invalidate(Map attributes, String group);
  
    /**
     * Explicitly invalidates objects based on attributes pattern and a group. If the pattern is a sub set of the attributes bound to an
     * object key, belonging to the specified group the cached object with that key is considered applying to
     * the pattern and will be invalidated.
     * The behavior of this invalidation is up to the region <code>configuration</code>.
     * To override the <code>configuration</code> cache users may use another invalidate method.
     *
     * @param attributes The attributes that represent a pattern for objects targeted to be invalidated
     * @param group The group that objects must belong to in order to be invalidated
     * If <code>null</code>, all object apply to them
     * @param invalidationScope - the overriden scope of the invalidation
     */
    public void invalidate(Map attributes, String group, byte invalidationScope);
    
    /**
     * Explicitly invalidates an object that is bound to the specified key. Explicit invalidation will
     * provoke events firing.
     * The behavior of this invalidation is defined by the flag parameter. The parameter overrides
     * the region <code>configuration</code>.
     *
     * @param key The key that cache user provides to specify the object that will be invalidated
     * @param synchronous If true, the invalidation will be synchronized. The scope of the invalidation
     * is defined by the region <code>configuration</code>.
     */
    public void invalidate(String key, boolean synchronous);
  
    /**
     * Explicitly invalidates an object that is bound to the specified key. Explicit invalidation will
     * provoke events firing.
     * The behavior of this invalidation is defined by the flag parameter. The parameter overrides
     * the region <code>configuration</code>.
     *
     * @param key The key that cache user provides to specify the object that will be invalidated
     * @param synchronous If true, the invalidation will be synchronized. The scope of the invalidation
     * is defined by the region <code>configuration</code>.
     * @param invalidationScope - the overriden scope of the invalidation
     */
    public void invalidate(String key, boolean synchronous, byte invalidationScope);
    
    /**
     * Explicitly invalidates objects that are bound to the specified keys. Explicit invalidation will
     * provoke events firing.
     * The behavior of this invalidation is defined by the flag parameter. The parameter overrides
     * the region <code>configuration</code>.
     *
     * @param keySet The keys that cache user provides to specify the objects that will be invalidated
     * @param synchronous If true, the invalidation will be synchronized. The scope of the invalidation
     * is defined by the region <code>configuration</code>.
     */
    public void invalidateBundle(Set keySet, boolean synchronous);
  
    /**
     * Explicitly invalidates objects that are bound to the specified keys. Explicit invalidation will
     * provoke events firing.
     * The behavior of this invalidation is defined by the flag parameter. The parameter overrides
     * the region <code>configuration</code>.
     *
     * @param keySet The keys that cache user provides to specify the objects that will be invalidated
     * @param synchronous If true, the invalidation will be synchronized. The scope of the invalidation
     * is defined by the region <code>configuration</code>.
     * @param invalidationScope - the overriden scope of the invalidation
     */
    public void invalidateBundle(Set keySet, boolean synchronous, byte invalidationScope);
    
    /**
     * Explicitly invalidates objects based on attributes pattern. If the pattern is a sub set of the attributes bound to an object key,
     * the cached object with that key is considered applying to the pattern and will be invalidated.
     * The behavior of this invalidation is defined by the flag parameter. The parameter overrides
     * the region <code>configuration</code>.
     *
     * @param attributes The attributes that represent a pattern for objects targeted to be invalidated
     * @param synchronous If true, the invalidation will be synchronized. The scope of the invalidation
     */
    public void invalidate(Map attributes, boolean synchronous);
  
    /**
     * Explicitly invalidates objects based on attributes pattern. If the pattern is a sub set of the attributes bound to an object key,
     * the cached object with that key is considered applying to the pattern and will be invalidated.
     * The behavior of this invalidation is defined by the flag parameter. The parameter overrides
     * the region <code>configuration</code>.
     *
     * @param attributes The attributes that represent a pattern for objects targeted to be invalidated
     * @param synchronous If true, the invalidation will be synchronized. The scope of the invalidation
     * @param invalidationScope - the overriden scope of the invalidation
     */
    public void invalidate(Map attributes, boolean synchronous, byte invalidationScope);
    
    /**
     * Explicitly invalidates objects based on attributes pattern and a group. If the pattern is a sub set of the attributes bound to an
     * object key, belonging to the specified group the cached object with that key is considered applying to
     * the pattern and will be invalidated.
     * The behavior of this invalidation is defined by the flag parameter. The parameter overrides
     * the region <code>configuration</code>.
     *
     * @param attributes The attributes that represent a pattern for objects targeted to be invalidated
     * @param group The group that objects must belong to in order to be invalidated
     * @param synchronous If true, the invalidation will be synchronized. The scope of the invalidation
     * If <code>null</code>, all object apply to them
     */
    public void invalidate(Map attributes, String group, boolean synchronous);

    /**
     * Explicitly invalidates objects based on attributes pattern and a group. If the pattern is a sub set of the attributes bound to an
     * object key, belonging to the specified group the cached object with that key is considered applying to
     * the pattern and will be invalidated.
     * The behavior of this invalidation is defined by the flag parameter. The parameter overrides
     * the region <code>configuration</code>.
     *
     * @param attributes The attributes that represent a pattern for objects targeted to be invalidated
     * @param group The group that objects must belong to in order to be invalidated
     * @param synchronous If true, the invalidation will be synchronized. The scope of the invalidation
     * If <code>null</code>, all object apply to them
     * @param invalidationScope - the overriden scope of the invalidation
     */
    public void invalidate(Map attributes, String group, boolean synchronous, byte invalidationScope);

}
