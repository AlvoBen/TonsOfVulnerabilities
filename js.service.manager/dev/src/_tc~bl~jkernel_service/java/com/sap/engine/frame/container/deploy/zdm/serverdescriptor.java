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
public class ServerDescriptor implements Serializable {

  private static final long serialVersionUID = 8829514225029732520L;
  
  private RollingStatus rollingStatus;
  private int clusterID;
  private String description;

  private transient int hashCode = 0;

  public ServerDescriptor(RollingStatus rollingStatus, int clusterID, String description) {
    this.rollingStatus = rollingStatus;
    this.clusterID = clusterID;
    this.description = description;
  }
  
  public RollingStatus getRollingStatus() {
    return rollingStatus;
  }
  
  public int getClusterID() {
    return clusterID;
  }

  public String getDescription() {
    return description;
  }
  
  public int hashCode() {
    if (hashCode == 0) {
      hashCode = clusterID + 17 * rollingStatus.hashCode();
    }
    return hashCode;
  }

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ServerDescriptor) {
      ServerDescriptor srvObj = (ServerDescriptor) obj;
      if (srvObj.clusterID == this.clusterID) {
        return srvObj.rollingStatus.equals(this.rollingStatus);
      }
    }
    return false;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Cluster ID:");
    sb.append(clusterID);
    sb.append(";Rolling Status:");
    sb.append(rollingStatus);
    if (description != null) {
      sb.append(';');
      sb.append(description);
    }
    return sb.toString();
  }

}