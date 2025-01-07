/*==============================================================================
    File:         CacheRegion.java
    Created:      20.07.2004

    $Author: d039261 $
    $Revision: #2 $
    $Date: 2004/08/11 $
==============================================================================*/
package com.sap.util.cache;

import java.util.Set;

import com.sap.util.cache.exception.CacheException;

/**
 * The <code>CacheRegion</code> instance encapsulates the operations that 
 * can be performed on a certain cache region.
 * 
 * @author Petio Petev, Michael Wintergerst
 * @version $Revision: #2 $
 */
public interface CacheRegion {

    // information

    /**
     * Returns a read-only interface to the configuration of this cache region. 
     *
     * @return cache region configuration
     * 
     * @throws IllegalStateException if the cache region instance has been 
     *         closed
     */
    public RegionConfigurationInfo getRegionConfigurationInfo();

    // control

    /**
     * Returns a <code>CacheControl</code> instance bound to this region.
     * 
     * @return the cache control instance of this cache region
     * 
     * @throws IllegalStateException if the cache region instance has been 
     *         closed
     */
    public CacheControl getCacheControl();

    /**
     * Returns an extended version of the cache control instance bound to this region.
     * The extended version has Object as cached object keys instead of String.
     * 
     * @return the cache control instance of this cache region
     * 
     * @throws IllegalStateException if the cache region instance has been 
     *         closed
     */
    //public ExtendedCacheControl getExtendedCacheControl();

    /**
     * Returns a primitive version of the cache control instance bound to this region.
     * The primitive version has <code>long</code> as cached object keys instead of String.
     * 
     * @return the cache control instance of this cache region
     * 
     * @throws IllegalStateException if the cache region instance has been 
     *         closed
     */
    //public PrimitiveCacheControl getPrimitiveCacheControl();

    // cache access

    /**
     * Returns a cache facade instance bound to this region.
     *
     * <p>
     * The cache facade instance allows for performing the basic cache 
     * operations.
     *
     * @return the cache facade for performing the basic cache operations
     * 
     * @throws IllegalStateException if the cache region instance has been 
     *         closed
     */
    public CacheFacade getCacheFacade();

    /**
     * Returns an extended version of the cache facade instance bound to this region.
     * The extended version has Object as cached object keys instead of String
     *
     * <p>
     * The cache facade instance allows for performing the basic cache 
     * operations.
     *
     * @return the cache facade for performing the basic cache operations
     * 
     * @throws IllegalStateException if the cache region instance has been 
     *         closed
     */
    public ExtendedCacheFacade getExtendedCacheFacade();

    /**
     * Returns a primitive version of the cache facade instance bound to this region.
     * The primitive version has <code>long</code> as cached object keys instead of String.
     *
     * <p>
     * The cache facade instance allows for performing the basic cache 
     * operations.
     *
     * @return the cache facade for performing the basic cache operations
     * 
     * @throws IllegalStateException if the cache region instance has been 
     *         closed
     */
    //public PrimitiveCacheFacade getPrimitiveCacheFacade();

    /**
     * Returns a named cache group bound to this region.
     *
     * @param group cache group name
     * 
     * @return cache group specified by the parameter or <code>null</code>
     * 
     * @throws NullPointerException if the <code>group</code> parameter is 
     *         <code>null</code>
     * @throws IllegalStateException if the cache region instance has been 
     *         closed
     */
    public CacheGroup getCacheGroup(String group);

    /**
     * Returns an extended version of named cache group bound to this region.
     * Extended versions have Object cached object keys instead of String.
     *
     * @param group cache group name
     * 
     * @return cache group specified by the parameter or <code>null</code>
     * 
     * @throws NullPointerException if the <code>group</code> parameter is 
     *         <code>null</code>
     * @throws IllegalStateException if the cache region instance has been 
     *         closed
     */
    //public ExtendedCacheGroup getExtendedCacheGroup(String group);

    /**
     * Returns a primitive version of named cache group bound to this region.
     * The primitive version has <code>long</code> as cached object keys instead of String.
     *
     * @param group cache group name
     * 
     * @return cache group specified by the parameter or <code>null</code>
     * 
     * @throws NullPointerException if the <code>group</code> parameter is 
     *         <code>null</code>
     * @throws IllegalStateException if the cache region instance has been 
     *         closed
     */
    //public PrimitiveCacheGroup getPrimitiveCacheGroup(String group);

    /**
     * Returns the names of all existing groups in the region
     *
     * @return The names of all existing groups in the region
     * 
     * @throws IllegalStateException if the cache region instance has been 
     *         closed
     */
    public Set getCacheGroupNames();

    /**
     * Indicates that the cache region is no longer used. Thus, it is possible
     * to put the cache region instance back into a pool or to release 
     * resources held by corresponding plugins.
     * <br>
     * After calling this method this instance is not longer valid. If methods
     * are called on a closed cache region instance, an 
     * <code>IllegalStateException</code> instance will be thrown.
     * <br>
     * The behavior of instances created by this cache region (i.e., 
     * <code>CacheFacade</code>, <code>CacheGroup</code>, etc.) is not
     * specified after having closed the corresponding cache region. 
     */
    public void close();
    
    /**
     * Removes all cache groups. That means all elements and the 
     * configuration of the cache groups are removed.
     * <p>
     * The behavior of the removal is defined by the region configuration.
     */
    public void removeCacheGroup();
    
    /**
     * Removes all cache groups. That means all elements and the 
     * configuration of the cache groups are removed.
     * <p>
     * The behavior of the removal is defined by the two flag parameters. 
     * These parameters override the region configuration.
     *
     * @param synchronous if set to <code>true</code>, the invalidation 
     *        messages will be sent synchronizely. The scope of the 
     *        invalidation is defined by the region configuration.
     * @param suppressInvalidation if set to <code>true</code>, no invalidation
     *        messages will be sent
     */
    public void removeCacheGroup(boolean synchronous, 
                                 boolean suppressInvalidation);
    
    /**
     * Removes all cache groups. That means all elements and the 
     * configuration of the cache groups are removed.
     * <p>
     * The behavior of the removal is defined by the two flag parameters. 
     * These parameters override the region configuration.
     *
     * @param synchronous if set to <code>true</code>, the invalidation 
     *        messages will be sent synchronizely. The scope of the 
     *        invalidation is defined by the region configuration.
     * @param invalidationScope overrides the configuration of the cache. Can be
     *        equal or less then the value configured for the region.
     */
    public void removeCacheGroup(boolean synchronous, 
                                 byte invalidationScope);
      
    /**
     * Removes the specified cache group. That means all elements and the 
     * configuration of the cache group are removed.
     * <p>
     * The behavior of the removal is defined by the region configuration.
     * <p>
     * If the specified group does not exist, nothing happens. 
     * 
     * @param group the group name to be removed
     * 
     * @throws NullPointerException if the <code>group</code> parameter is 
     *         <code>null</code>
     */
    public void removeCacheGroup(String group);

    /**
     * Removes the specified cache group. That means all elements and the 
     * configuration of the cache group are removed.
     * <p>
     * The behavior of the removal is defined by the two flag parameters. 
     * These parameters override the region configuration.
     * <p>
     * If the specified group does not exist, nothing happens. 
     * 
     * @param group the group name to be removed
     * @param synchronous if set to <code>true</code>, the invalidation 
     *        messages will be sent synchronizely. The scope of the 
     *        invalidation is defined by the region configuration.
     * @param suppressInvalidation if set to <code>true</code>, no invalidation
     *        messages will be sent
     * 
     * @throws NullPointerException if the <code>group</code> parameter is 
     *         <code>null</code>
     */
    public void removeCacheGroup(String group, boolean synchronous,
                                 boolean suppressInvalidation);

    /**
     * Removes the specified cache group. That means all elements and the 
     * configuration of the cache group are removed.
     * <p>
     * The behavior of the removal is defined by the two flag parameters. 
     * These parameters override the region configuration.
     * <p>
     * If the specified group does not exist, nothing happens. 
     * 
     * @param group the group name to be removed
     * @param synchronous if set to <code>true</code>, the invalidation 
     *        messages will be sent synchronizely. The scope of the 
     *        invalidation is defined by the region configuration.
     * @param invalidationScope overrides the configuration of the cache. Can be
     *        equal or less then the value configured for the region.
     * 
     * @throws NullPointerException if the <code>group</code> parameter is 
     *         <code>null</code>
     */
    public void removeCacheGroup(String group, boolean synchronous,
                                 byte invalidationScope);

    // exclusive (in one VM) execution
    
    /**
     * Executes an action exclusively. This must be used to prevent several threads/vms to recreate
     * simultaneously an object that has been invalidated. The following pattern must be used:
     * 
     * <code>Object result = Cache.get(<name>);
     * If (result == null) {
     *   Cache.getLocker(<name>).execute(new Runnable() {
     *     public void run() {
     *       result = Cache.get(<name>);
     *       if (result == null) {
     *         result = create(<name>, <specific conditions>);
     *         Cache.put(<name>, result);
     *       }
     *     }
     *   });
     * }</code>
     * 
     * @param name The key of the object that needs to be recreated exclusively
     * @param runnable A runnable implementing the user recreation of the object in its run() method
     * 
     * @throws CacheException if the run() method throws an exception, it is delegated as a CacheException,
     * so it can be processed by the user code. 
     */
    public void execute(String name, Runnable runnable) throws CacheException;
}