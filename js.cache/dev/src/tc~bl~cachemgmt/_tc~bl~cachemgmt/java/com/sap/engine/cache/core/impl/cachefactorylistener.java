/*
 * Created on 2004.8.9
 *
 */
package com.sap.engine.cache.core.impl;

import com.sap.util.cache.CacheRegion;

/**
 * @author petio-p
 *
 */
public interface CacheFactoryListener {

//  /**
//   * This method will be invoked when a region is gotten by a cache user
//   *
//   * @param region The name of the region
//   * @param result The CacheRegion resulting from the factory
//   */
//  void onGetCacheRegion(String region, CacheRegion result);

  /**
   * This method will be invoked when a region is usedf by the user for the first time
   *
   * @param region The name of the region
   * @param result The CacheRegion resulting from the factory
   */
  void onFirstUseCacheRegion(String region, CacheRegion result);

}
