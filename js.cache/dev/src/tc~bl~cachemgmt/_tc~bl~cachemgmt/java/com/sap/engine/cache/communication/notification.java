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

import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.spi.storage.StoragePlugin;

/**
 * Date: Feb 26, 2004
 * Time: 10:23:33 AM
 *
 * An interface used by cache implementation to distribute events machine- and cluster-wide
 *
 * @author Petio Petev, i024139
 */

public interface Notification {

  /**
   * Registers a listener that will be notified when changes in the region are made
   *
   * @param listener The listener that cache implementation registers
   */
  public void registerListener(CacheRegion region, NotificationListener listener);

  /**
   * Unregisters a listener
   *
   * @param region The listener instance that was previously registered
   */
  public void unregisterListener(CacheRegion region);

  /**
   * Initiates notification when the cache implementation has registered change in the cache region
   *
   * @param region The cache region that has been changed
   * @param storage The storage plugin bound to the cache region
   * @param key The cached object key of the changed object
   * @param operation The operation that was made on the cached object
   * @param cachedObject The cached object that will be tried to transport if the storage plugin supports
   *        transporting of cached objects
   * @param invalidationScope The override scope, must be -1 if no override is needed
   */
  public void notify(CacheRegion region, StoragePlugin storage, String key, byte operation, Object cachedObject, byte invalidationScope);

  /**
   * The cache implementation may use this method to send several change notifications in one message
   *
   * @param region the id of the region that groups several events in a block
   */
  public void beginEventBlock(CacheRegion region);

  /**
   * The cache implementation must use this method with combination of <code>beginEventBlock</code> to denote
   * that the gathering events in group has ended.
   *
   * @param region the id of the region that groups several events in a block
   * @param sendNotifications If true, the notification will be executed, otherwise no notifications will be sent
   */
  public void endEventBlock(CacheRegion region, boolean sendNotifications);

  /**
   * Initiates notification when the cache implementation has registered change in the cache region
   *
   * @param region The cache region that has been changed
   * @param storage The storage plugin bound to the cache region
   * @param key The cached object key of the changed object
   * @param operation The operation that was made on the cached object
   * @param cachedObject The cached object that will be tried to transport if the storage plugin supports
   *        transporting of cached objects
   * @param invalidationScope The override scope, must be -1 if no override is needed
   * @param synchronous If true, the method will return after all destinations are notified
   */
  public void notify(CacheRegion region, StoragePlugin storage, String key, byte operation, Object cachedObject, byte invalidationScope, boolean synchronous);

  /**
   * The cache implementation must use this method with combination of <code>beginEventBlock</code> to denote
   * that the gathering events in group has ended.
   *
   * @param region the id of the region that groups several events in a block
   * @param sendNotifications If true, the notification will be executed, otherwise no notifications will be sent
   * @param synchronous If true, the method will return after all destinations are notified
   */
  public void endEventBlock(CacheRegion region, boolean sendNotifications, boolean synchronous);

  /**
   * Method called to notify registered listeners
   *
   * @param regionId the regionId represents the region that the listeners bound to will be notified
   * @param message the message body (can contain one or bulk notification)
   */
  public void receive(int regionId, NotificationMessage message);

  public void hook(NotificationHook hook);

}
