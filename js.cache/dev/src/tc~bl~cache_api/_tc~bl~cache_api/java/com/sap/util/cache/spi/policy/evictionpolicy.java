/*==============================================================================
    File:         EvictionPolicy.java
    Created:      21.07.2004

    $Author: d039261 $
    $Revision: #2 $
    $Date: 2004/08/09 $
==============================================================================*/
package com.sap.util.cache.spi.policy;

import com.sap.util.cache.spi.Pluggable;

/**
 * The <code>EvictionPolicy</code> constitutes a service provider interface
 * which has to be implemented by a service provider in order to be 
 * pluggable into a cache region as an eviction policy.
 *
 * @author Petio Petev, Michael Wintergerst
 */
public interface EvictionPolicy extends Pluggable {
    
    /**
     * The cache region implementation will call this method after each 
     * <code>put()</code> operation.
     *
     * @param key     the key of the cached object
     * @param elmAttr the element attributes of the corresponding object
     * 
     * @throws NullPointerException if the <code>key</code> parameter  
     *                              is <code>null</code>
     */
    public void onPut(String key, ElementAttributes elmAttr);

    /**
     * Checks whether a specified object key is stored in the eviction
     * policy.
     *
     * @param key The cached object key to check for existence
     * 
     * @return <code>true</code> if such key exists in the eviction policy;
     *         otherwise <code>false</code> is returned
     * 
     * @throws NullPointerException if the key is <code>null</code>
     */
    public boolean exists(String key);

    /**
     * Chooses eviction target
     *
     * @return The key of the object that should be evicted
     */
    public String choose();

    /**
     * The cache region implementation will call this method when an object
     * is issued (tried to get) by cache users. The method is combined with
     * the <code>exists()</code> method.
     *
     * @param key The cached object key of the cached object issued by 
     *            cache user
     * 
     * @throws NullPointerException if the key is <code>null</code>
     */
    public void onAccess(String key);

    /**
     * The cache region implementation will call this method when an object
     * is invalidated in a region. 
     *
     * @param key The object key of the invalidated cached object
     * 
     * @throws NullPointerException if the key is <code>null</code>
     */
    public void onInvalidate(String key);

    /**
     * The cache region implementation will call this method when an object
     * is removed from the region. 
     *
     * @param key The cached object key of the cached object removed 
     *            by the cache user
     * 
     * @throws NullPointerException if the <code>key</code> is 
     *         <code>null</code>
     */
    public void onRemove(String key);

    /**
     * Returns the current count of objects in the region
     *
     * @return count of objects in the region
     */
    public int getCount();

    /**
     * Returns the current total size of objects in the region.
     *
     * @return total size of objects in the region
     */
    public int getSize();
    
    /**
     * Gets the element attributes of the specified <code>key</code>.
     * 
     * @param key the object key whose element attributes aret to be returned
     * 
     * @return the element attributes of the specified <code>key</code>
     * 
     * @throws NullPointerException if the <code>key</code> parameter is 
     *         <code>null</code>
     */
    public ElementAttributes getElementAttributes(String key);
}