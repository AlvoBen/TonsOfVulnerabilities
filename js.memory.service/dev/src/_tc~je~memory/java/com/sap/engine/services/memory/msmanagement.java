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

import com.sap.engine.frame.state.ManagementListener;

/**
 * This class implements Memory's management interface
 *
 * @author Nikolai Neichev
 */
public class MSManagement implements MSManagementInterface {

  /**
   * Memory manager instance
   */
  MemoryManager memory = null;
  /**
   * java runtime instance
   */
  Runtime runtime = null;
  /**
   * memory representarion coefficient
   *       1 - bytes
   *    1000 - Kb
   * 1000000 - Mb
   */
  int coeff = 1024*1024;

  /**
   * Constructor
   * @param memory Memory service instance
   */
  public MSManagement(MemoryManager memory) {
    this.memory = memory;
    runtime = Runtime.getRuntime();
  }

  public int getUsageRate() {
    return getUsedMemoryRate();
  }

  /**
   * Returns the allocated memory from the jvm
   * @return The allocated memory
   */
  public int getAllocatedMemory() {
    return ( (int) (runtime.totalMemory() / coeff) );
  }

  /**
   * Retruns the maximum possible memory that is available for this jvm
   * @return The available memory
   */
  public int getAvailableMemory() {
    return ( (int) (memory.getMaxMemory() / coeff) );
  }

  /**
   * Returns the used memory
   * @return The used memory
   */
  public int getUsedMemory() {
    return ( (int) ((memory.getMaxMemory() - memory.getFreeMemory()) / coeff));
  }
  
  /**
   * Returns the ratio of the allocated memory to the available memory
   * @return the ratio in percents
   */
  public int getAllocatedMemoryRate() {
    return (getAllocatedMemory() * 100) / getAvailableMemory();
  }
  
  /**
   * Returns the ratio of the used memory to the available memory
   * @return the ratio in percents
   */
  public int getUsedMemoryRate() {
    return (getUsedMemory() * 100) / getAvailableMemory();
  }

  /**
   * Registers management listener
   * @param managementListener The management listener
   */
  public void registerManagementListener(ManagementListener managementListener) {
  }
}
