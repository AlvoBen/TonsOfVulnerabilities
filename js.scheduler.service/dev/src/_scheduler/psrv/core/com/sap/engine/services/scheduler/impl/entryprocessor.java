/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.scheduler.impl;

public interface EntryProcessor {
  
  public static final int NO_MORE_EXECUTION_TIMES = -1; 

  /**
   * Returns the next execution time from the current time on or 
   * NO_MORE_EXECUTION_TIMES if there is no next execution time
   * @return the next execution time from the current time on or 
   * NO_MORE_EXECUTION_TIMES if there is no next execution time
   */
  public long getNextExecutionTime();

  /**
   * Returns the next execution time after the specified time or 
   * NO_MORE_EXECUTION_TIMES if there is no next execution time
   * @param baseTime the specified time
   * @return the next execution time that is > baseTime or
   * NO_MORE_EXECUTION_TIMES if there is no next execution time
   */
  public long getNextExecutionTime(long baseTime);

}