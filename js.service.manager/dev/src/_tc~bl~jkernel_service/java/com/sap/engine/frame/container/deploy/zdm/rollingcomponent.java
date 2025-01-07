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

import com.sap.engine.frame.container.event.ContainerEventListener;
import java.io.Serializable;

/**
 * Describes the component, which is going to be patched using the
 * rolling approach.
 * 
 * @author Dimitar Kostadinov
 * @version 1.00
 * @since 7.10
 */
public class RollingComponent implements Serializable {

  private static final long serialVersionUID = -8256674138835040672L;

  /** Component with software type interface */
  public static final byte INTERFACE_TYPE = ContainerEventListener.INTERFACE_TYPE;
  /** Component with software type library */
  public static final byte LIBRARY_TYPE = ContainerEventListener.LIBRARY_TYPE;
  /** Component with software type service */
  public static final byte SERVICE_TYPE = ContainerEventListener.SERVICE_TYPE;

  /** Path to component archive */
  private String filePath;
  /** Current component type */
  private byte componentType;

  private int hashCode = 0;

  /**
   * Create rolling component descriptor
   *
   * @param filePath - path to component archive
   * @param componentType - component type
   */
  public RollingComponent(String filePath, byte componentType) {
    this.filePath = filePath;
    this.componentType = componentType;
  }

  /**
   * Returns path to SDA archive
   *
   * @return - path to component archive
   */
  public String getFilePath() {
    return filePath;
  }

  public String getFilePath1() {
    return filePath;
  }

  /**
   * Software type of the rolling component (INTERFACE_TYPE, LIBRARY_TYPE, SERVICE_TYPE)
   *
   * @return - component type
   */
  public byte getComponentType() {
    return componentType;
  }

  public int hashCode() {
    if (hashCode == 0) {
      this.hashCode = filePath.hashCode() + 1 << (componentType * 7);
    }
    return hashCode;
  }

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof RollingComponent) {
      RollingComponent rollObj = (RollingComponent) obj;
      if (rollObj.componentType == this.componentType) {
        return rollObj.filePath.equals(this.filePath);
      }
    }
    return false;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    switch (componentType) {
      case (INTERFACE_TYPE) : sb.append("Interface : "); break;
      case (LIBRARY_TYPE) : sb.append("Library : "); break;
      case (SERVICE_TYPE) : sb.append("Service : "); break;
    }
    sb.append(filePath);
    return sb.toString();
  }

}