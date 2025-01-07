/*
 * Copyright (c) 2004 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.cache.communication;

import com.sap.util.cache.InvalidationListener;

/**
 * Date: Feb 26, 2004
 * Time: 11:12:53 AM
 *
 * An interface implemented by the cache implementation that can be registered to receive information
 * about changes in a specific region or overall changes in the cache
 *
 * @author Petio Petev, i024139
 */

public interface NotificationListener extends InvalidationListener {

  public static final byte EVENT_INTERNAL_INVALIDATION = 4;
  /**
   * The method is invoked when an event about changes in the cache region is due.
   *
   * @param regionId the id of the region being changed
   * @param key The cached object key of the object that has been changed.
   * @param event The type of the invalidation, can be
   * <code>InvalidationListener.EVENT_INVALIDATION</code> - if an explicit invalidation was done
   * <code>InvalidationListener.EVENT_REMOVAL</code> - if invalidation due to remove invokation was done
   * <code>InvalidationListener.EVENT_MODIFICATION</code> - if invalidation due to successive put invokation was done
   */
  public void invalidate(int regionId, String key, byte event);

  /**
   * The method is invoked when an event about changes in the cache region is due. This method is called
   * only when the storage plugin supports transportation of objects
   *
   * @param regionId the id of the region being changed
   * @param key The cached object key of the object that has been changed
   * @param cachedObject The value of the cached object that has been changed
   */
  public void invalidate(int regionId, String key, Object cachedObject);

}
