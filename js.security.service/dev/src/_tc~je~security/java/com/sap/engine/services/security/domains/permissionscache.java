/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.domains;

import com.sap.engine.services.security.Util;
import com.sap.tc.logging.Severity;
import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.CacheFacade;
import static com.sap.util.cache.RegionConfigurationInfo.*;
import com.sap.util.cache.exception.CacheException;

import java.util.Vector;
import java.util.Properties;
import java.security.PermissionCollection;

/**
 *
 *
 *
 *
 *
 * User: I024108
 * Date: Jul 25, 2008
 * Time: 10:02:58 AM
 * Version: 7.12
 */
public class PermissionsCache {
  private static final String COLLECTION_CACHE = "Permission Collections";
  private static final String VECTOR_CACHE = "Permission Vectors";

  private static CacheFacade permissionsAsVector;
  private static CacheFacade permissionsAsCollection;
  private static final Properties CACHE_DESCRIPTOR = new Properties();

  static {
    CACHE_DESCRIPTOR.setProperty(PROP_REGION_SCOPE, Integer.toString(SCOPE_LOCAL));
    CACHE_DESCRIPTOR.setProperty(PROP_COUNT_START_OF_EVICTION_THRESHOLD, "3000");
    CACHE_DESCRIPTOR.setProperty(PROP_COUNT_UPPER_LIMIT_THRESHOLD, "5500");
    CACHE_DESCRIPTOR.setProperty(PROP_COUNT_CRITICAL_LIMIT_THRESHOLD, "6000");
    CACHE_DESCRIPTOR.setProperty(PROP_INVALIDATION_SCOPE, Integer.toString(SCOPE_LOCAL));
    CACHE_DESCRIPTOR.setProperty(PROP_SYNCHRONOUS, "false");
    CACHE_DESCRIPTOR.setProperty(PROP_DIRECT_INVALIDATION_MODE, "false");
    CACHE_DESCRIPTOR.setProperty(PROP_LOGGING_MODE, "false");
    CACHE_DESCRIPTOR.setProperty("_OWNER", "Security");

    try {
      CacheRegionFactory factory = CacheRegionFactory.getInstance();
      factory.initDefaultPluggables();

      CacheRegion region = factory.getCacheRegion(COLLECTION_CACHE);
      if (region == null) {
        factory.defineRegion(COLLECTION_CACHE,"HashMapStorage", "SimpleLRU" , CACHE_DESCRIPTOR);
      }
      region = factory.getCacheRegion(COLLECTION_CACHE);
      permissionsAsVector = region.getCacheFacade();

      CacheRegion region2 = factory.getCacheRegion(VECTOR_CACHE);
      if (region2 == null) {
        factory.defineRegion(VECTOR_CACHE,"HashMapStorage", "SimpleLRU" , CACHE_DESCRIPTOR);
      }
      region2 = factory.getCacheRegion(VECTOR_CACHE);
      permissionsAsCollection = region2.getCacheFacade();
    } catch (CacheException e) {
      // the cache is unusable
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "CodeBased permissions cache not functional", e);
    }
  }

  /**
   *
   *
   *
   * @return NULL if some of the internal cache facades is not avaiable
   */
  static PermissionsCache getInstance() {
    if (permissionsAsCollection == null || permissionsAsVector == null) {
      return null;
    } else {
      return new PermissionsCache();
    }
  }

  /**
   *  used only by getInstance()
   *
   */
  private PermissionsCache() {
  }


  /**
   *
   *
   * @param encodedComponent an encoded component name
   */
  void invalidateCachedComponent(String encodedComponent) {
    permissionsAsCollection.remove(encodedComponent);
    permissionsAsVector.remove(encodedComponent);
  }


  /**
   *
   * @param encodedComponent an encoded component name
   * @param permissions non null permissions vector
   */
  void cachePermissionsAsVector(String encodedComponent, Vector permissions) {
    try {
      permissionsAsVector.put(encodedComponent, permissions);
    } catch (CacheException e) {
      if (Util.SEC_SRV_LOCATION.beDebug()) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "Unable to cache permissions of component [" + encodedComponent+ "]", e);
      }
    }
  }

  /**
   *
   * @param encodedComponent an encoded component name
   * @param permissions
   */
  void cachePermissionsAsCollection(String encodedComponent, PermissionCollection permissions) {
    try {
      permissionsAsCollection.put(encodedComponent, permissions);
    } catch (CacheException e) {
      if (Util.SEC_SRV_LOCATION.beDebug()) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "Unable to cache permissions of component [" + encodedComponent+ "]", e);
      }
    }
  }

  /**
   *
   * @param encodedComponent an encoded component's name
   * @return null if the specified component is not yet cached
   */
  Vector getCachedPermissionsVector(String encodedComponent) {
    return (Vector) permissionsAsVector.get(encodedComponent);
  }


  /**
   *
   *
   * @param encodedComponent an encoded component name
   * @return null if the specified component is not yet cached
   */
  PermissionCollection getCachedPermissionsCollection(String encodedComponent) {
    return (PermissionCollection) permissionsAsCollection.get(encodedComponent);
  }

}
