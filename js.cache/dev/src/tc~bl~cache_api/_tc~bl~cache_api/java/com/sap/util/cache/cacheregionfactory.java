/*==============================================================================
    File:         CacheRegionFactory.java
    Created:      20.07.2004

    $Author: d039261 $
    $Revision: #5 $
    $Date: 2004/08/12 $
==============================================================================*/
package com.sap.util.cache;

import java.util.Iterator;
import java.util.Properties;

import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.PluginException;

/**
 * The <code>CacheRegionFactory</code> class is the facade and the user
 * interface for getting <code>CacheRegion</code> instances. It is the
 * main entry point into the overall cache functionality from a user's
 * point of view.
 *  
 * @author Petio Petev, Michael Wintergerst
 * @version $Revision: #5 $
 */
public abstract class CacheRegionFactory {

    /**
     * The concrete cache region factory instance to be used. The factory
     * is set by the cache management implementation within a startup step.
     */
    private static CacheRegionFactory rFactory = null;

    /**
     * Sets the cache region factory instance which is used to implement
     * this abstract factory.
     * 
     * @param rFactory the implementation of the cache region factory
     */
    protected static final void setInternalFactory(
        CacheRegionFactory rFactory) {
        
        // check whether the cache region factory has already been set 
        if (CacheRegionFactory.rFactory == null) {
            CacheRegionFactory.rFactory = rFactory;
        }
    }

    /**
     * Returns an instance of the cache region factory.
     * 
     * @return instance of the cache region factory
     */
    public static final CacheRegionFactory getInstance() {   //todo synchronize
      if (rFactory == null) {
        try {
          Class trial = Class.forName("com.sap.engine.cache.core.impl.InternalRegionFactory");
          rFactory = (CacheRegionFactory) trial.newInstance();
        } catch (Exception e) {
          logT(e);
					try {
						Class trial = Class.forName("com.sap.util.cache.impl.CacheRegionFactoryImpl");
            rFactory = (CacheRegionFactory) trial.newInstance();
					} catch (Exception e1) {
            logT(e1);
            rFactory = null;
					}
        }
      }
      return rFactory;
    }
    
    public static void logT(Throwable t) {
      t.printStackTrace();
    }

    public static void logTInfo(Throwable t) {
      t.printStackTrace();
    }

    /**
     * Gets the feature set of the corresponding cache implementation.
     * The feature set allows for getting information about the implementation
     * of features which are marked as optional in the API.
     * 
     * @return the feature set of the cache implementation
     */
    public abstract CacheFeatureSet getFeatureSet();
    
    /**
     * Gets an iterator over the available cache regions. 
     * <br>
     * The iterator elements are region names as String.
     * 
     * @return iterator over the available cache regions
     */
    public abstract Iterator iterateRegions();

    /**
     * Returns an interface to an already created and configured cache region.
     * <br>
     * If the cache region does not exist, <code>null</code> is returned.
     *
     * @param region the name of the cache region
     * 
     * @return cache region instance or <code>null</code>
     * 
     * @throws NullPointerException if the provided <code>region</code> 
     *         parameter is <code>null</code>
     */
    public abstract CacheRegion getCacheRegion(String region);
    
    /**
     * Defines programatically a region. This is used for non-preconfigured 
     * cache regions.
     *
     * @param regionName the name of the region to be defined
     * @param storagePlugin the global storage plugin name that will be 
     *                      associated with the region
     * @param evictionPolicy the global eviction policy name that will be 
     *                       associated with the region
     * @param configuration rhe region configuration for the defined region
     * 
     * @throws CacheException if the cache region cannot be defined
     * @throws NullPointerException if one of the parameters is 
     *         <code>null</code> 
     */
    public abstract void defineRegion(String regionName, String storagePlugin,
            String evictionPolicy, RegionConfigurationInfo configuration) 
            throws CacheException;
    
    /**
     * Defines programatically a region. This is used for non-preconfigured 
     * cache regions.
     *
     * @param regionName the name of the region to be defined
     * @param storagePlugin the global storage plugin name that will be 
     *                      associated with the region
     * @param evictionPolicy the global eviction policy name that will be 
     *                       associated with the region
     * @param configuration the region configuration mapping with the following properties names:
     *        _COUNT_START_OF_EVICTION_THRESHOLD
     *        _COUNT_CRITICAL_LIMIT_THRESHOLD
     *        _COUNT_UPPER_LIMIT_THRESHOLD
     *        _SIZE_START_OF_EVICTION_THRESHOLD
     *        _SIZE_CRITICAL_LIMIT_THRESHOLD
     *        _SIZE_UPPER_LIMIT_THRESHOLD
     *        _SIZE_CALCULATION_DEPTH
     *        _DIRECT_INVALIDATION_MODE
     *        _INVALIDATION_SCOPE
     *        _REGION_SCOPE
     *        _LOGGING_MODE
     *        _SYNCHRONOUS 
     * @throws CacheException if the cache region cannot be defined
     */
    public abstract void defineRegion(String regionName, String storagePlugin,
            String evictionPolicy, Properties configuration) 
            throws CacheException;
      
    /**
     * Initializes the default plugins. It is up to the implementation and
     * configuration which plugins are registered as default.
     * 
     * @throws PluginException if one of default plugins cannot be initialized
     * @throws CacheException if the plugin cannot be registered
     */
    public abstract void initDefaultPluggables() throws CacheException;
}