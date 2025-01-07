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
package com.sap.engine.frame.container.deploy.zdm;

import java.io.Serializable;

/**
 * Describes the status of the rolling patch per server node.
 *
 * @author Dimitar Kostadinov
 * @version 1.00
 * @since 7.10
 */
public class RollingName implements Serializable {

  private static final long serialVersionUID = 8662749782386346112L;

  private String runtimeName;
  private byte componentType;

  private transient int hashCode = 0;

  public RollingName(String runtimeName, byte componentType) {
    this.runtimeName = runtimeName;
    this.componentType = componentType;
  }

  /**
   * @return Returns runtime component name.
   */
  public String getName() {
    return runtimeName;
  }

  /**
   * @return Returns component type.
   */
  public byte getComponentType() {
    return componentType;
  }


  public int hashCode() {
    if (hashCode == 0) {
      this.hashCode = runtimeName.hashCode() + 1 << (componentType * 7);
    }
    return hashCode;
  }

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof RollingName) {
      RollingName rollObj = (RollingName) obj;
      if (rollObj.componentType == this.componentType) {
        return rollObj.runtimeName.equals(this.runtimeName);
      }
    }
    return false;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    switch (componentType) {
      case (RollingComponent.INTERFACE_TYPE) : sb.append("interface:"); break;
      case (RollingComponent.LIBRARY_TYPE) : sb.append("library:"); break;
      case (RollingComponent.SERVICE_TYPE) : sb.append("service:"); break;
    }
    sb.append(runtimeName);
    return sb.toString();
  }

}