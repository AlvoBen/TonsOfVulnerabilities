/*
 * Copyright (c) 1999 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.memory.server;

public class MemoryManagerSnapShot { // implements SnapShot {

  private MemoryManagerImpl memoryManager = null;
  private MemoryManagerObjectNode node = null;

  public MemoryManagerSnapShot(MemoryManagerImpl memoryManager) {
    this.memoryManager = memoryManager;
    node = new MemoryManagerObjectNode();
  }

  /**
   * Returns the name of the provider responsible for this snap shot -
   * i.e. returns a String identifier of the monitored subsystem.
   *
   * @return     the name of the provider of this snap shot
   */
  public String getSnapShotProviderName() {
    return "MemoryManager";
  }

  /**
   * Retrieaves current values for the observed parameters and generates
   * a tree structure to represent them. If some calculations are needed
   * it is responsible for keeping the previous values of these parameters.
   *
   * @param   rootNode  a root for the tree structure represented the current state
   */
  //  public ObjectNode updateNode() {
  //    node.update(memoryManager);
  //    return node;
  //  }
  /**
   *  Tells this snap shot to start gathering information about the observed
   * parameters of the subsystem, it represents.
   */
  public void start() {

  }

  /**
   *  Tells this snap shot to stop gathering information about the observed
   * parameters of the subsystem, it represents.
   */
  public void stop() {

  }

  /**
   *  Resets all values, containing information about the observed parameters
   * of the subsystem, which this snap shot has gathered.
   */
  public void reset() {

  }

}

