/*
 * Copyright (c) 2004 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */

package com.sap.engine.cache.job;

/**
 * Date: Feb 26, 2004
 * Time: 11:12:53 AM
 *
 * An interface used by the cache implementation to register background tasks
 *
 * @author Petio Petev, i024139
 */

public interface Background {

  /**
   * Registers background task. Depending on the scope of the task, it can be executed on one or several VMs.
   * Tasks have to be serializeable.
   *
   * @param task The background task that will be registered
   * @throws IllegalArgumentException Thrown if
   * <code>task.getName()</code> returns a name with more than 40 characters
   * <code>task.getInterval()</code> returns a non-meaningful interval
   * <code>task.getScope()</code> returns a non-meaningful constant
   */
  public void registerTask(Task task);

  /**
   * Unregisters a background task. Task that are repetetive need to be unregistered explicitly
   * @param task The task that will be unregistered
   */
  public void unregisterTask(Task task);

}
