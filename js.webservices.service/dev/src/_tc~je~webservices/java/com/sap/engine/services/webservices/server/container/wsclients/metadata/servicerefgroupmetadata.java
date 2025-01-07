/*
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
 
package com.sap.engine.services.webservices.server.container.wsclients.metadata;

/**
 * Title: ServiceRefGroupMetaData
 * Description: ServiceRefGroupMetaData
 * 
 * @author Dimitrina Stoyanova
 * @version
 */

public class ServiceRefGroupMetaData {
 
  private String applicationName;
  private String moduleName; 
  private String serviceRefGroupName; 
    
  public ServiceRefGroupMetaData() {
  }  
  
  public ServiceRefGroupMetaData(String applicationName, String moduleName, String serviceRefGroupName) {
    this.applicationName = applicationName; 
    this.moduleName = moduleName; 
    this.serviceRefGroupName = serviceRefGroupName;   
  }  
  
  /**
   * @param applicationName
   */
  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  /**
   * @param moduleName 
   */
  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  /**
   * @param serviceRefGroupName
   */
  public void setServiceRefGroupName(String serviceRefGroupName) {
    this.serviceRefGroupName = serviceRefGroupName;
  }
     
  /**
   * @return application name
   */
  public String getApplicationName() {
    return applicationName;
  }

  /**
   * @return module name
   */
  public String getModuleName() {
    return moduleName;
  }

  /**
   * @return service reference group name
   */
  public String getServiceRefGroupName() {
    return serviceRefGroupName;
  }
  
}
