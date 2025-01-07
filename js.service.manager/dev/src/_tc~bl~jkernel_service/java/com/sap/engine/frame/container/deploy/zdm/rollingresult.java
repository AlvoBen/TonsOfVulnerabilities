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
 * Describes how the state of the instance was changed as a result from calling 
 * this method. Based on the result the method caller is responsible to decide 
 * is the new state of the instance acceptable or not.
 * 
 * @author Dimitar Kostadinov
 * @version 1.00
 * @since 7.10
 */
public class RollingResult implements Serializable {

  private static final long serialVersionUID = -8689952070793483016L;

  private RollingName rollingName;
  private InstanceDescriptor instanceDescriptor;

  private transient int hashCode = 0;

  public RollingResult(RollingName rollingName, InstanceDescriptor instanceDescriptor) {
    this.rollingName = rollingName;
    this.instanceDescriptor = instanceDescriptor;
  }

  public RollingName getRollingName() {
    return rollingName;
  }

  public InstanceDescriptor getInstanceDescriptor() {
    return instanceDescriptor;
  }

  public int hashCode() {
    if (hashCode == 0) {
      this.hashCode = rollingName.hashCode() + 7 * instanceDescriptor.hashCode();
    }
    return hashCode;
  }

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof RollingResult) {
      RollingResult rollObj = (RollingResult) obj;
      if (rollObj.rollingName.equals(this.rollingName)) {
        return rollObj.instanceDescriptor.equals(this.instanceDescriptor);
      }
    }
    return false;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(rollingName);
    sb.append(':');
    sb.append(instanceDescriptor);
    return sb.toString();
  }

}