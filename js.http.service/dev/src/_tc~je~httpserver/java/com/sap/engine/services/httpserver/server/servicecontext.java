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
package com.sap.engine.services.httpserver.server;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.services.httpserver.chain.ServerScope;
import com.sap.engine.services.httpserver.interfaces.properties.HttpProperties;
import com.sap.engine.services.httpserver.server.logongroups.LogonGroupsManager;

public class ServiceContext implements ServerScope {
  private static ServiceContext serviceContext = null;

  private ThreadSystem threadSystem = null;
  private ApplicationServiceContext applicationServiceContext = null;
  private HttpProviderImpl httpProvider = null;
  private HttpProperties httpProperties = null;
	private HttpHosts httpHosts = null;
  private static int icmClusterId = -1;
  
  private HttpMonitoring monitoring;
  private LogonGroupsManager logonGroupsManager;

  public static int getIcmClusterId() {
    return icmClusterId;
  }

  public static void setIcmClusterId(int icmClusterId) {
    ServiceContext.icmClusterId = icmClusterId;
  }

  public ServiceContext(ThreadSystem threadSystem) {
    this.threadSystem = threadSystem;
  }

  public static void init(ThreadSystem threadSystem) {
    serviceContext = new ServiceContext(threadSystem);
  }

  public static ServiceContext getServiceContext() {
    return serviceContext;
  }

  public void setThreadSystem(ThreadSystem ts) {
    threadSystem = ts;
  }

  public ThreadSystem getThreadSystem() {
    return threadSystem;
  }

  public ApplicationServiceContext getApplicationServiceContext() {
    return applicationServiceContext;
  }

  public static void setServiceContext(ServiceContext serviceContext) {
    ServiceContext.serviceContext = serviceContext;
  }

  public void setApplicationServiceContext(ApplicationServiceContext applicationServiceContext) {
    this.applicationServiceContext = applicationServiceContext;
  }

  public HttpProviderImpl getHttpProvider() {
    return httpProvider;
  }

  public void setHttpProvider(HttpProviderImpl httpProvider) {
    this.httpProvider = httpProvider;
  }

  public HttpProperties getHttpProperties() {
    return httpProperties;
  }

  public void setHttpProperties(HttpProperties httpProperties) {
    this.httpProperties = httpProperties;
  }
  
  public void setHttpHosts(HttpHosts httpHosts) {
  	this.httpHosts = httpHosts;
  }
  
  public HttpHosts getHttpHosts() {
  	return httpHosts;
  }

 public HttpMonitoring getHttpMonitoring() {
    return monitoring;
  }
  
  public void setHttpMoniroting(HttpMonitoring monitoring) {
    this.monitoring = monitoring;
  }  
  
  /**
   * Provides access to the logon groups manager
   * @return the <code>LogonGroupsManagerImpl</code> object 
   */
  public LogonGroupsManager getLogonGroupsManager() {
    return logonGroupsManager;
  }
  
  public void setLogonGroupsManager(LogonGroupsManager logonGroupsManager) {
    this.logonGroupsManager = logonGroupsManager;
  }
}
