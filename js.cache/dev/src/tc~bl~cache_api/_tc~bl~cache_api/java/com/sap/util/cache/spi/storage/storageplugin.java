/*==============================================================================
    File:         StoragePlugin.java
    Created:      21.07.2004

    $Author: d039261 $
    $Revision: #2 $
    $Date: 2004/08/09 $
==============================================================================*/
package com.sap.util.cache.spi.storage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.spi.Pluggable;

/**
 * An SPI that has to be implemented to have a storage plugin. Storage plugins
 * provides a data structure of cached objects with possible transparent layer
 * for writing through or writing back cached object into
 * backend storage.
 *
 * @author Petio Petev, Michael Wintergerst
 */
public interface StoragePlugin extends Pluggable {

    /**
     * The default group name.
     */
    public static final String DEFAULT_GROUP_NAME = "_Default";
            

    /**
     * Puts an object associated with a key into the store.
     * 
     * <p> The <code>key</code> and the <code>cachedObject</code> parameter
     * must not be <code>null</code>.
     * 
     * @param key          The cached object key
     * @param cachedObject The cached object that will be put into the storage
     * 
     * @throws NullPointerException if either argument is <code>null</code>
     * @throws WriteException if it is not possible to store the object
     */
    public void put(String key, Object cachedObject) throws CacheException;

    /**
     * Puts the user-defined attributes for the specified object key into the
     * storage.
     * 
     * @param key        The cached object key the attributes belong to 
     * @param attributes The attributes of the cached object bound to the key
     *
     * @throws NullPointerException if the <code>key</code> argument is
     *                              <code>null</code> 
     * @throws WriteException if it is not possible to store the attributes
     */
    public void putAttributes(String key, Map attributes) 
        throws CacheException;

    /**
     * Put the system attributes for the specified object key into the storage.
     *
     * @param key The cached object key the system attributes belong to
     * @param sysAttributes The system attributes of the cached object
     *                      bound to the key
     * 
     * @throws NullPointerException of the <code>key</code> argument is 
     *         <code>null</code>
     * @throws WriteException if it is not possible to store the system
     *         attributes
     */
    public void putSystemAttributes(String key, Map sysAttributes) 
        throws CacheException;

    /**
     * Put the user-defined and system attributes for the specified object key
     * into the storage.
     *
     * @param key The cached object key the system attributes belong to
     * @param attributes The user-defined attributes of the cached object bound
     *                   to the key
     * @param sysAttributes The system attributes of the cached object
     *                      bound to the key
     * 
     * @throws NullPointerException of the <code>key</code> argument is 
     *         <code>null</code>
     * @throws WriteException if it is not possible to store the system
     *         attributes
     */
    public void putAttributes(String key, Map attributes, 
                              Map sysAttributes) throws CacheException;

    /**
     * Puts an object, user-defined and system attributes associated with a key
     * into the store.
     * 
     * <p> The <code>key</code> and the <code>cachedObject</code> parameter
     * must not be <code>null</code>.
     * 
     * @param key          The cached object key
     * @param cachedObject The cached object that will be put into the storage
     * @param attributes   The user-defined attributes to be associated with 
     *                     the cached object
     * @param sysAttributes The system attributes of the cached object to be
     *                      associated with the cached object
     * 
     * @throws NullPointerException if the <code>key</code> or the 
     *         <code>cachedObject</code> argument is <code>null</code>
     * @throws WriteException if it is not possible to store the object and
     *         its attributes
     */
    public void put(String key, Object cachedObject, Map attributes, 
                    Map sysAttributes) throws CacheException;

    /**
     * Puts an object and attributes associated with a key into the store.
     * <br>
     * The <code>system</code> indicates whether the attributes are system
     * or user-defined attributes.
     * 
     * <p> The <code>key</code> and the <code>cachedObject</code> parameter
     * must not be <code>null</code>.
     * 
     * @param key          The cached object key
     * @param cachedObject The cached object that will be put into the storage
     * @param attributes   The attributes to be associated with the cached
     *                     object
     * @param system       if set to <code>true</code> the attributes are
     *                     system attributes; otherwise they represent
     *                     user-defined attributes
     * 
     * @throws NullPointerException if the <code>key</code> or the 
     *         <code>cachedObject</code> argument is <code>null</code>
     * @throws WriteException if it is not possible to store the object and
     *         its attributes
     */
    public void put(String key, Object cachedObject, Map attributes, 
                    boolean system) throws CacheException;

    /**
     * Checks whether the specified object key is in the cache.
     *
     * @param key   The cached object key
     * 
     * @return <code>true</code> if the cached object exists (i.e., the 
     *         object is in the in-memory cache and no backend storage will be
     *         contacted); otherwise <code>false</code> is returned
     * 
     * @throws NullPointerException if the <code>key</code> parameter 
     *         is <code>null</code>
     */
    public boolean exists(String key);

    /**
     * Gets the object associated with the specified key.
     *
     * @param key   The cached object key
     * @param copy  indicates whether a copy of the original copy should
     *              be returned
     * 
     * @return the object associated with the specified key or 
     *         <code>null</code> if the object cannot be retrieved
     * 
     * @throws NullPointerException if the <code>key</code> parameter is 
     *         <code>null</code>
     */
    public Object get(String key, boolean copy);

    /**
     * Get the user-defined attributes of the object associated with the 
     * specified key.
     *
     * @param key   The cached object key the attributes are to be returned
     * @param copy  indicates whether or not a copy of the attributes
     *              should be returned
     * 
     * @return The user-defined attributes of the cached object or 
     *         <code>null</code>
     * 
     * @throws NullPointerException if the <code>key</code> parameter is
     *         <code>null</code>
     */
    public Map getAttributes(String key, boolean copy);

    /**
     * Get the system attributes of the object associated with the 
     * specified key.
     *
     * @param key   The cached object key the system attributes are to be 
     *              returned 
     * @param copy  indicates whether or not a copy of the attributes
     *              should be returned
     * 
     * @return The system attributes of the cached object or <code>null</code>
     * 
     * @throws NullPointerException if the <code>key</code> parameter is
     *         <code>null</code>
     */
    public Map getSystemAttributes(String key, boolean copy);

    /**
     * Removes a cached object specified by the <code>key</code> parameter.
     *
     * @param key The cached object key
     * 
     * @throws NullPointerException if the <code>key</code> parameter is
     *         <code>null</code>
     */
    public void remove(String key);

    /**
     * Evicts the object associated with the specified key from the cache. 
     * The diffence to the <code>remove()</code> method is that the 
     * storage plugin may store the evicted object in some backend storage
     * system. 
     *
     * @param key The cached object key
     * 
     * @throws NullPointerException if the <code>key</code> parameter
     *         is <code>null</code>
     */
    public void evict(String key);
    
    /**
     * A storage plugin specific method that converts cached object to 
     * transportable ones (serializeable and/or shareable).
     *
     * @param cachedObject The cached object to be transported
     * 
     * @return The transportable object (can be the same) or <code>null</code>
     *         if the storage plugin does not support transporting
     * 
     * @throws NullPointerException if the <code>cachedObject</code> parameter 
     *         is <code>null</code>.
     */
    public Object transport(Object cachedObject);

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
    public void invalidate(String key);
    
    /**
     * A storage plugin specific mehtod that converts a transportable object
     * (shareable and/or serializeable) to actual cached object.
     *
     * @param transportable The transportable state of a cached object
     * @return The cached object recreated using the transportable object
     *         (can be the same) as <code>transportable</code>
     * 
     * @throws NullPointerException if the <code>transportable</code> parameter
     *         is <code>null</code> 
     */
    public Object recreateTransported(Object transportable);

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
    public int getSize(String objectKey);

    /**
     * Returns a calculated size of a cached object
     *
     * @param objectKey The key of the cached object to get attributes size of
     * 
     * @return The size of the cached object's attributes in bytes
     * 
     * @throws NullPointerException if the <code>objectKey</code> parameter 
     *         is <code>null</code>
     */
    public int getAttributesSize(String objectKey);

    /**
     * Performs bulk operations that are collected by far. The storage plugin may not support
     * bulk operations. If it is so, this method must do nothing.
     *
     * @throws CacheException if some operation did not succeed
     */
    public void flush() throws CacheException;

    /**
     * Gets the effective scope (local, instance, cluster) of the Storage Plugin - the extent that
     * objects are distributed to
     * 
     * @return The effective scope
     */
    public byte getScope();
    
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
    public boolean exists(String group, String key);
  
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
    public Object get(String group, String key, boolean copy);
  
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
    public Map getAttributes(String group, String key, boolean copy);
  
    /**
     * Removes the elements inside the specified group. 
     * 
     * @param group the cache group to be deleted
     * 
     * @throws NullPointerException if the <code>group</code> parameter is
     *         set to <code>null</code>
     */        
    public void removeGroup(String group);
      
    /**
     * Removes all elements in the cache region.
     */
    public void remove();
      
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
    public void removeGroup(String group, boolean delete);
      
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
    public void remove(String group, String key);
  
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
    public void invalidateGroup(String group);
  
    /**
     * Invalidates all elements in the cache. 
     */
    public void invalidate();
      
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
    public void invalidate(String group, String key);
      
    /**
     * Returns the number of cached objects.
     * 
     * @return the number of cached objects
     */
    public int size();
      
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
    public int size(String group);
      
    /**
     * Checks whether the storage plugin has stored some objects.
     * 
     * @return <code>true</code> if some objects are stored; otherwise
     *         <code>false</code> is returned 
     */
    public boolean isEmpty();
      
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
    public boolean isEmpty(String group);
      
    /**
     * Inserts a cache group.
     * 
     * @param group   the cache group to be inserted
     * 
     * @throws NullPointerException if the <code>group</code> parameter is 
     *         <code>null</code>
     */
    public void insertGroup(String group);  
          
    /**
     * Returns a key set of the whole cache region.
     * 
     * @return key set of the whole cache region
     */
    public Set keySet();
      
    /**
     * Returns a key set of specified group.
     * 
     * @return key set of the specified group
     * 
     * @throws NullPointerException if the specified group parameter is
     *         <code>null</code>
     */
    public Set keySet(String group);
      
    /**
     * Returns the values of the whole cache region.
     * 
     * @return the values of the whole cache region as a collection
     */
    public Collection values();
      
    /**
     * Returns the values of the specified group.
     * 
     * @param group the values of the specified group as a collection
     * 
     * @throws NullPointerException if the specified <code>group</code> 
     *                              parameter is <code>null</code>
     */
    public Collection values(String group);
      
    /**
     * Returns an entry set of the whole cache region.
     * 
     * @return the entry set of the whole cache region
     */
    public Set entrySet();
  
    /**
     * Returns an entry set of the specified cache region.
     * 
     * @param group the group name whose entry set is to be returned
     * 
     * @return the entry set of the whole cache region
     */
    public Set entrySet(String group);
      
    /**
     * Gets a set of the available groups.
     * 
     * @return set of the available groups
     */
    public Set getGroupSet();
      
    /**
     * Checks whether the specified group exists.
     * 
     * @return <code>true</code> if the specified group already exists;
     *         otherwise <code>false</code> is returned
     * 
     * @throws NullPointerException if the <code>group</code> parameter
     *         is <code>null</code>
     */
    public boolean existsGroup(String group);
      
}