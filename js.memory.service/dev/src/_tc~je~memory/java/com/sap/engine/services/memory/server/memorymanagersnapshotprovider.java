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

public class MemoryManagerSnapShotProvider { // implements SnapShotProvider {

  private MemoryManagerImpl memoryManager = null;

  public MemoryManagerSnapShotProvider(MemoryManagerImpl memoryManager) {
    this.memoryManager = memoryManager;
  }

  //  public SnapShot createSnapShot() {
  //    return new MemoryManagerSnapShot(memoryManager);
  //  }

}

