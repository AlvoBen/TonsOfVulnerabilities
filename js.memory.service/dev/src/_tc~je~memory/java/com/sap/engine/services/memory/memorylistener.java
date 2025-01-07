/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.memory;

/**
 * @author
 * @version 1.0.0
 */
public interface MemoryListener {

  public final static int CRITICAL = 5;
  public final static int VERY_HIGH = 4;
  public final static int HIGH = 3;
  public final static int NORMAL = 2;
  public final static int LOW = 1;
  public final static int VERY_LOW = 0;


  /**
   * This method is invoked when there is any change of the
   * level of the used memory.
   *
   * @param   memoryUsageLevel - the new level of used memory
   * @param   increaseFlag - shows if there increasing or decreasing
   *   of the used memory. If the flag is <code>true</code> it means
   * that there is increasing of the level, otherwise decreasing.
   */
  public void notify(int memoryUsageLevel, boolean increaseFlag);

}

