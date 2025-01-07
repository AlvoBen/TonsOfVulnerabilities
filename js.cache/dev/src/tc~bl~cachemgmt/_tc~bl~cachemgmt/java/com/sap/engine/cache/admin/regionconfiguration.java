/*
 * Copyright (c) 2004 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */

package com.sap.engine.cache.admin;

import com.sap.util.cache.RegionConfigurationInfo;
import com.sap.util.cache.spi.policy.EvictionPolicy;
import com.sap.util.cache.spi.storage.StoragePlugin;

/**
 * Date: Feb 26, 2004
 * Time: 10:23:33 AM
 *
 * An adminitrative interface to access cache region configuration.
 *
 * @author Petio Petev, i024139
 */

public interface RegionConfiguration extends RegionConfigurationInfo {

  // called by admin module

  /**
   * Sets the configured scope of the region.
   *
   * @param scope Can be <code>RegionConfigurationInfo.SCOPE_LOCAL</code>, <code>RegionConfigurationInfo.SCOPE_MACHINE</code> and
   * <code>RegionConfigurationInfo.SCOPE_CLUSTER</code>
   * @throws IllegalArgumentException Thrown if the <code>scope</code> is not a valid constant
   */
  public void setRegionScope(byte scope);

  /**
   * Sets the automatic invalidation scope configured for the region.
   *
   * @param scope Can be <code>RegionConfigurationInfo.SCOPE_NONE</code>, <code>RegionConfigurationInfo.SCOPE_LOCAL</code>, <code>RegionConfigurationInfo.SCOPE_MACHINE</code> and
   * <code>RegionConfigurationInfo.SCOPE_CLUSTER</code>
   * @throws IllegalArgumentException Thrown if the <code>scope</code> is not a valid constant
   */
  public void setInvalidationScope(byte scope);

  /**
   * Sets the configured name of the region
   *
   * @param name The name can be 40 characters long
   * @throws IllegalArgumentException Thrown if <code>name</code> is bigger than 40 characters
   * @throws NullPointerException Thrown if <code>name</code> is null
   */
  public void setName(String name);

  /**
   * Sets the configured maximum total size for the region's cached objects
   *
   * @param size The maximum total object sizes in bytes
   * @throws IllegalArgumentException Thrown if <code>size</code> is non-positive
   */
  public void setSizeQuota(int size, byte level);

  /**
   * Sets the configured maximum total count of the region's cached objects
   *
   * @param count The maximum total count
   * @throws IllegalArgumentException Thrown if <code>count</code> is non-positive
   */
  public void setCountQouta(int count, byte level);

  /**
   * Sets the id for the cache region
   *
   * @param id The cluster-wide unique id of the region
   */
  public void setId(int id);

  /**
   * Sets the eviction policy bound to the cache region
   *
   * @param evictionPolicy The eviction policy that admin module has chosen for this region
   * @throws NullPointerException Thrown if <code>evictionPolicy</code> is null
   */
  public void setEvictionPolicy(EvictionPolicy evictionPolicy);

  /**
   * Sets the storage plugin bound to the cache region
   *
   * @param storagePlugin The storage plugin that admin module has chosen for this region
   * @throws NullPointerException Thrown if <code>storagePlugin</code> null
   */
  public void setStoragePlugin(StoragePlugin storagePlugin);

  /**
   * Gets the storage plugin bound to the cache region
   *
   * @return The storage plugin that is configured for this region
   */
  public StoragePlugin getStoragePlugin();

  /**
   * Gets the eviction policy bound to the cache region
   *
   * @return The eviction policy that is configured for this region
   */
  public EvictionPolicy getEvictionPolicy();

  /**
   * Sets the direct invalidation mode set to the region. If the mode is set to true,
   * the cached objects that are implementing <code>InvalidationListener</code> interface
   * will be invoked upon invalidation
   *
   */
  public void setDirectObjectInvalidationMode(boolean flag);

  /**
   * Sets the trace mode of the region. If true - all operations will be traced.
   *
   * @param flag Trace mode flag
   */
  public void getTraceMode(boolean flag);

  /**
   * Sets the logging mode of the region. If true - all operations will be logged.
   *
   * @param flag Logging mode flag
   */
  public void setLoggingMode(boolean flag);

  public void setSynchronous(boolean synchronous);

  /**
   * Sets wheather the cache region is configured not to receive its local notifications
   * 
   * @param flag the mode type <coede>true<code> means the sender will receive its notification messages, <code>false<code> the sender will receive its notifications. 
   */
  public void setSenderIsReceiverMode(boolean flag);
  
  /**
   * Sets wheather put operation is treated as a modification even if the operation did 
   * not overwrite an already cached object
   * 
   * @param flag the mode type <coede>true<code> means put is always a modification operation, <code>false<code> is not.  
   */
  public void setPutIsModificationMode(boolean flag);
  
//  /**
//   * Sets if only one Invalidation Listener to be notified in case of shared memory is used.  
//   * 
//   * @param flag <code>true<code> means that only one notification listener will be notifed per instance
//   */
//  public void setNotifyOneListenerPerInstance(boolean flag);

}
