/**
 * Copyright (c) 2008 by SAP Labs Bulgaria,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * 
 * Created on Aug 10, 2008 by I030797
 *   
 */
 
package com.sap.engine.services.security.server;


/**
 * @author I030797
 *
 */
public class PolicyConfigurationRoot {
  
  private static final String LS = System.getProperty("line.separator");
  
  private String id;
  private String path;
  private byte type;
  private DeploySecurityContext deployContext;

  
  public PolicyConfigurationRoot(String id, String path, byte type, DeploySecurityContext deployContext) {
    this.id = id;
    this.path = path;
    this.type = type;
    this.deployContext = deployContext;
  }
  
  public String getId() {
    return id;
  }
  
  public String getPath() {
    return path;
  }
  
  public byte getType() {
    return type;
  }

  public DeploySecurityContext getDeployContext() {
    return deployContext;
  }
  
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("PolicyConfigurationRoot").append(LS);
    builder.append("ID: ").append(id).append(LS);
    builder.append("Path: ").append(path).append(LS);
    builder.append("Type: ").append(type);
    return builder.toString();
  }
}

