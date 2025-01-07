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

public class MemoryManagerObjectNode { // implements MonitoredMemory {

  private long allocatedMemory = -1;
  private long memoryInUse = -1;
  private long limit = -1;
  private String nodeName = "MemoryManager";
  private String[] attributes = new String[1];

  public MemoryManagerObjectNode() {

  }

  /**
   * Returns the name of the node that will be visualized.
   *
   * @return     name of the node
   */
  public String getName() {
    return nodeName;
  }

  /**
   * If the state of this node have been modified since the last retrieving
   * of current state. (I.e. is the nodediffers from the corresponding node
   * in the previous current tree)
   *
   * @return     if the state of this node have been modified
   */
  public boolean isModified() {
    return true;
  }

  /**
   * Returns the values of attributes associated with this node.
   *
   * @return   the values of attributes associated with this node.
   */
  public String[] getAttributes() {
    return attributes;
  }

  /**
   * Returns the children of this node.
   *
   * @return    the children of this node
   */
  //  public ObjectNode[] getSubnodes() {
  //    return NO_SUB_NODES;
  //  }
  /**
   *   Returns the allocated memory in bytes.
   */
  public long getAllocatedMemory() {
    return allocatedMemory;
  }

  /**
   *   Returns the amount of memory in bytes currently in use.
   */
  public long getMemoryInUse() {
    return memoryInUse;
  }

  /**
   *   Returns the limit of memory allocation in bytes set by the component.
   */
  public long getLimit() {
    return limit;
  }

  public void update(MemoryManagerImpl memoryManager) {
    Runtime runtime = Runtime.getRuntime();
    allocatedMemory = runtime.totalMemory();
    memoryInUse = runtime.totalMemory() - runtime.freeMemory();
    limit = memoryManager.getMaxMemory();
    attributes[0] = "" + memoryManager.level;
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Memory Usage: ");
    buffer.append(memoryInUse);
    buffer.append("/");
    buffer.append(allocatedMemory);
    buffer.append(" memory limit: ");
    buffer.append(limit);
    buffer.append(";");
    return buffer.toString();
  }

}

