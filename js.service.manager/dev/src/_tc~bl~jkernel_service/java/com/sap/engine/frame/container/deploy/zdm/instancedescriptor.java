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
import java.util.Set;

/**
 * Describes the status of the rolling patch per instance.
 * 
 * @author Dimitar Kostadinov
 * @version 1.00
 * @since 7.10
 */
public class InstanceDescriptor implements Serializable {

  private static final long serialVersionUID = 4608328495443592358L;

  private Set<ServerDescriptor> serverDescriptors;
  private RollingStatus rollingStatus;
  private int instanceID;

  private transient int hashCode = 0;

  public InstanceDescriptor(Set<ServerDescriptor> serverDescriptors, int instanceID) {
    this.serverDescriptors = serverDescriptors;
    this.instanceID = instanceID;
    this.rollingStatus = evaluateRollingStatus();
  }

  public Set<ServerDescriptor> getServerDescriptors() {
    return serverDescriptors;
  }

  public RollingStatus getRollingStatus() {
    return rollingStatus;
  }

  public int getInstanceID() {
    return instanceID;
  }

  public int hashCode() {
    if (hashCode == 0) {
      hashCode = serverDescriptors.hashCode() + 7 * instanceID + 11 * rollingStatus.hashCode();
    }
    return hashCode;
  }

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof InstanceDescriptor) {
      InstanceDescriptor instObj = (InstanceDescriptor) obj;
      if (instObj.instanceID == this.instanceID && instObj.rollingStatus.equals(this.rollingStatus)) {
        return instObj.serverDescriptors.equals(this.serverDescriptors);
      }
    }
    return false;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Instance ID:");
    sb.append(instanceID);
    sb.append(";Rolling Status:");
    sb.append(rollingStatus);
    sb.append(';');
    sb.append(serverDescriptors.toString());
    return sb.toString();
  }

  private RollingStatus evaluateRollingStatus() {
    RollingStatus result = RollingStatus.SUCCESS;
    for (ServerDescriptor serverDescriptor : serverDescriptors) {
      RollingStatus SDRollingStatus = serverDescriptor.getRollingStatus();
      if (SDRollingStatus.equals(RollingStatus.ERROR)) {
        result = RollingStatus.ERROR;
        break;
      } else if (SDRollingStatus.equals(RollingStatus.WARNING)) {
        result = RollingStatus.WARNING;
      }
    }
    return result;
  }

}