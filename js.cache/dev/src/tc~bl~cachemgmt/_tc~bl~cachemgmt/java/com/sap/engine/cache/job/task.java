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

import java.io.Serializable;

/**
 * Date: Feb 26, 2004
 * Time: 11:12:53 AM
 *
 * A background job entity. Such must be registered to be executed in background
 *
 * @author Petio Petev, i024139
 */

public interface Task extends Runnable, Serializable {

  public static final byte SCOPE_EVERY_NODE = 1;

  public static final byte SCOPE_EVERY_MACHINE = 2;

  public static final byte SCOPE_ONE_NODE = 3;

  /**
   * Returns the name of the task. Must not exceed 40 characters.
   *
   * @return The name of the task
   */
  public String getName();

  /**
   * Denotes if the task is a repeatable one. Repeatable tasks must provide a valid value for the interval
   * between repetitions. If a task is not repeatable. The interval means the time that will pass before
   * the task can be executed. Non-repeatable tasks are automatically unregistered after execution.
   *
   * @return True if the task is a repeatable one.
   *
   */
  public boolean repeatable();

  /**
   * The interval between successive executions. For non-repeatable tasks the interval is meant for waiting
   * before the execution of the task.
   *
   * @return The interval in milliseconds.
   */
  public int getInterval();

 /**
  * @return The scope of the task. Can be
  * <code>SCOPE_EVERY_NODE</code> - The task will be executed on every node
  * <code>SCOPE_EVERY_MACHINE</code> - The task will be executed on one node per machine
  * <code>SCOPE_ONE_NODE</code> - The task will be executed only on one node of the cluster
  */
  public byte getScope();

}
