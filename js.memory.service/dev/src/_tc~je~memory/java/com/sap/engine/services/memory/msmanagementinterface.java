/**
 * Copyright (c) 1999 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.memory;

import com.sap.engine.frame.state.ManagementInterface;

/**
 * This class represents the Memory's management interface
 *
 * @author Nikolai Neichev
 */
public interface MSManagementInterface extends ManagementInterface{
  
  // public metrics
  public int getUsageRate();
  
  // private metrics

  /**
   * Returns the allocated memory from the jvm
   * @return The allocated memory
   */
  public int getAllocatedMemory();

  /**
   * Retruns the maximum possible memory that is available for this jvm
   * @return The available memory
   */
  public int getAvailableMemory();

  /**
   * Returns the used memory
   * @return The used memory
   */
  public int getUsedMemory();
  
  /**
   * Returns the ratio of the allocated memory to the available memory
   * @return the ratio in percents
   */
  public int getAllocatedMemoryRate();
  
  /**
   * Returns the ratio of the used memory to the available memory
   * @return the ratio in percents
   */
  public int getUsedMemoryRate();

}
