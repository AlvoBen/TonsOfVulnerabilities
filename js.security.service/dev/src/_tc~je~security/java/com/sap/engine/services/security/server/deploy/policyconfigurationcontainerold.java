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
 * Created on Aug 8, 2008 by I030797
 *   
 */
 
package com.sap.engine.services.security.server.deploy;

import java.io.File;
import java.util.Properties;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.deploy.container.AdditionalAppInfo;
import com.sap.engine.services.deploy.container.ApplicationDeployInfo;
import com.sap.engine.services.deploy.container.ContainerDeploymentInfo;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterfaceExtension;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.FileUpdateInfo;
import com.sap.engine.services.deploy.container.ProgressListener;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.container.op.start.ApplicationStartInfo;
import com.sap.engine.services.deploy.container.op.start.ContainerStartInfo;

/**
 * This container class is depricated and will be deleted in NW 7.30 version
 * @author I045045
 * @deprecated
 *
 */
public class PolicyConfigurationContainerOld implements ContainerInterfaceExtension {

  /**
   * This is the old name of PolicyConfigurationContainer
   */
  static final String NAME = "com.sap.security.policy-configurations";
    
  private ContainerInfo containerInfo;

  
  public PolicyConfigurationContainerOld(ApplicationServiceContext serviceContext) {
    containerInfo = new ContainerInfo();
    containerInfo.setServiceName(serviceContext.getServiceState().getServiceName());
    containerInfo.setName(NAME);
    containerInfo.setJ2EEContainer(false);
    containerInfo.setSupportingLazyStart(true);
    containerInfo.setModuleName(PolicyConfigurationContainerOld.class.getName());
    containerInfo.setSupportingParallelism(true);
    containerInfo.setSupportingSingleFileUpdate(true);
    containerInfo.setPriority(ContainerInfo.MIN_PRIORITY);
  }

  public boolean acceptedAppInfoChange(String appName, AdditionalAppInfo addAppInfo) throws DeploymentException {
    return false;
  }

  public void addProgressListener(ProgressListener listener) {
  }

  public void appInfoChangedCommit(String applicationName) throws WarningException {
  }

  public void appInfoChangedRollback(String applicationName) throws WarningException {
  }

  public void applicationStatusChanged(String applicationName, byte status) {
  }

  public void commitDeploy(String applicationName) throws WarningException {
    
  }

  public ApplicationDeployInfo commitRuntimeChanges(String applicationName) throws WarningException {
    return null;
  }

  public ApplicationDeployInfo commitSingleFileUpdate(String applicationName) throws WarningException {
    return null;
  }

  public void commitStart(String applicationName) throws WarningException {
  }

  public void commitStop(String applicationName) throws WarningException {
  }

  public ApplicationDeployInfo commitUpdate(String applicationName) throws WarningException {
   return null;
  }

  
  public ApplicationDeployInfo deploy(File[] archiveFiles, ContainerDeploymentInfo dInfo, Properties props) throws DeploymentException {
    return null;
  }
 
  public void downloadApplicationFiles(String applicationName, Configuration applicationConfig) throws DeploymentException, WarningException {
  }

  public String getApplicationName(File standaloneFile) throws DeploymentException {
    return null;
  }

  public File[] getClientJar(String applicationName) {
    return null;
  }

  public ContainerInfo getContainerInfo() {
    return containerInfo;
  }

  public String[] getResourcesForTempLoader(String applicationName) throws DeploymentException {
    return null;
  }

  public void makeAppInfoChange(String applicationName, AdditionalAppInfo addAppInfo, Configuration configuration) throws WarningException, DeploymentException {
  }

  public ApplicationDeployInfo makeSingleFileUpdate(FileUpdateInfo[] files, ContainerDeploymentInfo dInfo, Properties props) throws DeploymentException {
   return null;
  }

  public ApplicationDeployInfo makeUpdate(File[] archiveFiles, ContainerDeploymentInfo dInfo, Properties props) throws DeploymentException {
    return null;
  }

  public boolean needStopOnAppInfoChanged(String applicationName, AdditionalAppInfo addAppInfo) {
    return false;
  }

  public boolean needStopOnSingleFileUpdate(FileUpdateInfo[] files, ContainerDeploymentInfo dInfo, Properties props) throws DeploymentException, WarningException {
    return false;
  }

  public boolean needStopOnUpdate(File[] archiveFiles, ContainerDeploymentInfo dInfo, Properties props) throws DeploymentException, WarningException {
    return false;
  }

  public boolean needUpdate(File[] archiveFiles, ContainerDeploymentInfo dInfo, Properties props) throws DeploymentException, WarningException {
    return false;
  }

  public void notifyAppInfoChanged(String applicationName) throws WarningException {
  }

  public void notifyDeployedComponents(String applicationName, Properties props) throws WarningException {
  }

  public void notifyRuntimeChanges(String applicationName, Configuration applicationConfig) throws WarningException {
  }

  public void notifySingleFileUpdate(String applicationName, Configuration applicationConfig, Properties props) throws WarningException {
  }

  public void notifyUpdatedComponents(String applicationName, Configuration applicationConfig, Properties props) throws WarningException {
  }

  public void prepareDeploy(String applicationName, Configuration applicationConfig) throws DeploymentException, WarningException {
  }

  public void prepareRuntimeChanges(String applicationName) throws DeploymentException, WarningException {
  }

  public void prepareSingleFileUpdate(String applicationName) throws DeploymentException, WarningException {
  }

  public void prepareStart(String applicationName, Configuration applicationConfig) throws DeploymentException, WarningException {
  }

  public void prepareStop(String applicationName, Configuration applicationConfig) throws DeploymentException, WarningException {
  }

  public void prepareUpdate(String applicationName) throws DeploymentException, WarningException {
  }

  public void remove(String applicationName) throws DeploymentException, WarningException {
  }

  public void removeProgressListener(ProgressListener listener) {
  }

  public void rollbackDeploy(String applicationName) throws WarningException {
  }

  public void rollbackRuntimeChanges(String applicationName) throws WarningException {
  }

  public void rollbackSingleFileUpdate(String applicationName, Configuration config) throws WarningException {
  }

  public void rollbackStart(String applicationName) throws WarningException {
  }

  public void rollbackStop(String applicationName) throws WarningException {
  }

  public void rollbackUpdate(String applicationName, Configuration applicationConfig, Properties props) throws WarningException {
  }

  public void commitRemove(String applicationName) throws WarningException {
  }

  public ApplicationStartInfo makeStartInitially(ContainerStartInfo csInfo) throws DeploymentException {
    return null;
  }

  public void notifyRemove(String applicationName) throws WarningException {
  }

  public void remove(String applicationName, ConfigurationHandler operationHandler, Configuration appConfiguration) throws DeploymentException, WarningException {
  }

}
