/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.lib.util.cache;

/**
 * When you use the cache without weak part the objects
 * you cache MUST implement this interface. When an object
 * have to leave the cache according to LRU logic then
 * cacheFinalization() is called by the cache system.
 *
 * @author Iliyan Nenov, ilian.nenov@sap.com
 * @version SAP J2EE Engine 6.30
 */
public interface StableCacheObject {

  /**
   * This method is performed when the object has to leave the cache
   * according to LRU logic. The objects you want to cache MUST implement
   * this method if and only if you use the cache without weak part.
   */
  public void cacheFinalization();
}