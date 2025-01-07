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

/**
 * Date: Feb 26, 2004
 * Time: 10:23:33 AM
 *
 * A monitoring interface to access cache region runtime info.
 *
 * @author Petio Petev, i024139
 */

public interface Monitor {

  // called by monitoring module

  /**
   * Gets the current total size of cached objects
   *
   * @return The current total size of cached objects in bytes in the region
   */
  public int size();

  /**
   * Gets the current total size of cached objects attributes
   *
   * @return The current total size of cached objects attributes in bytes in the region
   */
  public int attributesSize();

  /**
   * Gets the current total size of cached objects names
   *
   * @return The current total size of cached objects names in bytes in the region
   */
  public int namesSize();


  /**
   * Gets the name of the cache region that this monitor is associated to
   * @return
   */
  public String name();

  /**
   * Gets the current total count of cached objects
   *
   * @return The current total count of cached objects in the region
   */
  public int count();

  /**
   * Returns the current hit rate in promilles (1000 * successful cached object gets / all cached objects gets)
   *
   * @return The current hit rate in the region
   */
  public int hitRate();

  /**
   * Gets the total number of put operations since creation of the region
   *
   * @return Total number of put operations
   */
  public int puts();

  /**
   * Gets the total number of get operations since creation of the region
   *
   * @return Total number of get operations
   */
  public int gets();

  /**
   * Gets the total number of successful get operations since creation of the region
   *
   * @return Total number of get operations
   */
  public int hits();

  /**
   * Gets the total number of modification (successive puts on the same cached object key) since creation of the region
   *
   * @return Total number of modification operations
   */
  public int modifications();

  /**
   * Gets the total number of remove operations since creation of the region
   *
   * @return The total number of remove operations
   */
  public int removals();

  /**
   * Gets the total number of evictions that eviction policy has made in the region
   *
   * @return The total number of evictions
   */
  public int evictions();

  /**
   * Gets the utilization of the cache region - accesses (all kinds of operations) per minute
   * @return The cache region utilization
   */
  public int utilization();

  // called by maintainer

  void onModify(int oldSize, int oldAttrSize, int newSize, int newAttrSize);
  void onPut(int size, int attrSize, int nameSize);
  void onRemove(int oldSize, int oldAttrSize, int nameSize);
  void onEvict(int oldSize, int oldAttrSize, int nameSize);
  void onGet(boolean success);
  void onNotify(int scope);

  /**
   * In order to hook calls, one can use this method. No need of synchronization.
   *
   * @param hook The monitor that will be notified about monitoring info changes
   */
  public void setHook(Monitor hook);

  public Monitor getHook();

}
