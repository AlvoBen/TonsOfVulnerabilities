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
import java.util.Collection;
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
import com.sap.engine.services.security.server.DeploySecurityContext;
import com.sap.engine.services.security.server.PolicyConfigurationRoot;
import com.sap.engine.services.security.server.PolicyConfigurationLog;
import com.sap.tc.logging.Location;

/**
 * @author I030797
 */
public class PolicyConfigurationContainer implements ContainerInterfaceExtension {

  /**
   * The name starts with "z_" because this container must be with lowest priority and must be called at the end.
   */ 
  static final String NAME = "z_com.sap.security.policy-configurations";
  
  private static final String[] EXTENSIONS = new String[] {
    ".ear", ".EAR", ".jar", ".JAR", ".rar", ".RAR", ".sda", ".SDA", ".war", ".WAR"
  };
  
  private static final Location LOCATION = PolicyConfigurationLog.location;
  private static final ThreadLocal<Boolean> PARALLEL_DEPLOYMENT = new ThreadLocal<Boolean>();
  
  private ContainerInfo containerInfo;

  
  public PolicyConfigurationContainer(ApplicationServiceContext serviceContext) {
    containerInfo = new ContainerInfo();
    containerInfo.setServiceName(serviceContext.getServiceState().getServiceName());
    containerInfo.setName(NAME);
    containerInfo.setFileNames(null);
    containerInfo.setFileExtensions(EXTENSIONS);
    containerInfo.setJ2EEContainer(false);
    containerInfo.setSupportingLazyStart(true);
    containerInfo.setModuleName(PolicyConfigurationContainer.class.getName());
    containerInfo.setSupportingParallelism(true);
    containerInfo.setSupportingSingleFileUpdate(true);
    containerInfo.setPriority(ContainerInfo.MIN_PRIORITY);
    containerInfo.setModuleDetector(new PolicyConfigurationModuleDetector());
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
    if (LOCATION.beDebug()) {
      LOCATION.debugT("PolicyConfigurationContainer.commitDeploy() for application: {0}", new Object[] {applicationName});
    }
    
    clearPolicyConfigurationRoots();
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
    if (LOCATION.beDebug()) {
      LOCATION.debugT("PolicyConfigurationContainer.commitUpdate() for application: {0}", new Object[] {applicationName});
    }
    
    return clearPolicyConfigurationRoots();
  }

  private static ApplicationDeployInfo clearPolicyConfigurationRoots() {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Clearing registered data from the current thread");
    }
    
    DeploySecurityContext.clearPolicyConfigurationRoots();
    setParallelDeployment(false);
    return null;
  }

  public ApplicationDeployInfo deploy(File[] archiveFiles, ContainerDeploymentInfo dInfo, Properties props) throws DeploymentException {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("PolicyConfigurationContainer.deploy()");
    }
    
    return prepareApplicationDeployInfo(getDeployInfoStrings(archiveFiles), dInfo, props);
  }

  public static boolean isParallelDeployment() {
    Boolean threadValue = PARALLEL_DEPLOYMENT.get();
    boolean isParallel = threadValue == null ? false : threadValue.booleanValue();
    
    if (LOCATION.beDebug()) {
      LOCATION.debugT("The deployment is " + (isParallel ? "" : "not") + " parallel");
    }
    
    return isParallel;
  }
  
  static void setParallelDeployment(boolean isParallel) {
    PARALLEL_DEPLOYMENT.set(isParallel);
  }
  
  private static String[] getDeployInfoStrings(File[] archiveFiles) {
    String[] res = new String[archiveFiles.length];
    
    for (int i = 0; i < archiveFiles.length; i++) {
      res[i] = archiveFiles[i].getPath();
    }
    
    return res;
  }

  private static String[] getDeployInfoStrings(FileUpdateInfo[] fileInfos) {
    String[] res = new String[fileInfos.length];
    
    for (int i = 0; i < fileInfos.length; i++) {
      res[i] = fileInfos[i].getArchiveEntryName();
    }
    
    return res;
  }

  private ApplicationDeployInfo prepareApplicationDeployInfo(String[] fileStrings, ContainerDeploymentInfo dInfo, Properties props) {
    if(!isParallelDeployment()) {
      return null;
    }
    
    Collection<PolicyConfigurationRoot> policyConfigurationRoots = DeploySecurityContext.getPolicyConfigurationRoots();
    
    if (policyConfigurationRoots == null || policyConfigurationRoots.isEmpty()) {
      setParallelDeployment(false);
      return null;
    }
    
    String[] names = new String[fileStrings.length];
    
    for (int i = 0; i < fileStrings.length; i++) {
      names[i] = NAME + ": " + fileStrings[i];
    }
    
    ApplicationDeployInfo deployInfo = new ApplicationDeployInfo();    
    deployInfo.setDeployedComponentNames(names);
    
    return deployInfo;
  }
  
  public static void registerPolicyConfigurationRoots() {
    Collection<PolicyConfigurationRoot> policyConfigurationRoots = DeploySecurityContext.getPolicyConfigurationRoots();
    
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Registering policy configuration roots {0}", new Object[] {policyConfigurationRoots});
    }
    
    if (policyConfigurationRoots != null) {
      for (PolicyConfigurationRoot root: policyConfigurationRoots) {
        root.getDeployContext().registerPolicyConfiguration(root);
      }
    }
  }

  public void downloadApplicationFiles(String applicationName, Configuration applicationConfig) throws DeploymentException, WarningException {
  }

  public String getApplicationName(File standaloneFile) throws DeploymentException {
    setParallelDeployment(true);
    StringBuilder name = new StringBuilder();
    name.append(containerInfo.getName()).append(": ");
    
    if (standaloneFile != null) {
      name.append(standaloneFile.getName());
    }
    
    return name.toString();
  }

  public File[] getClientJar(String applicationName) {
    return null;
  }

  public ContainerInfo getContainerInfo() {
    return containerInfo;
  }

  public String[] getResourcesForTempLoader(String applicationName) throws DeploymentException {
    return new String[0];
  }

  public void makeAppInfoChange(String applicationName, AdditionalAppInfo addAppInfo, Configuration configuration) throws WarningException, DeploymentException {
  }

  public ApplicationDeployInfo makeSingleFileUpdate(FileUpdateInfo[] files, ContainerDeploymentInfo dInfo, Properties props) throws DeploymentException {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("PolicyConfigurationContainer.makeSingleFileUpdate()");
    }
    
    return prepareApplicationDeployInfo(getDeployInfoStrings(files), dInfo, props);
  }

  public ApplicationDeployInfo makeUpdate(File[] archiveFiles, ContainerDeploymentInfo dInfo, Properties props) throws DeploymentException {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("PolicyConfigurationContainer.makeUpdate()");
    }
    
    return prepareApplicationDeployInfo(getDeployInfoStrings(archiveFiles), dInfo, props);
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
    return true;
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
    if (LOCATION.beDebug()) {
      LOCATION.debugT("PolicyConfigurationContainer.prepareDeploy() for application: {0}", new Object[] {applicationName});
    }
    
    registerPolicyConfigurationRoots();
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
    if (LOCATION.beDebug()) {
      LOCATION.debugT("PolicyConfigurationContainer.prepareUpdate() for application: {0}", new Object[] {applicationName});
    }
    
    registerPolicyConfigurationRoots();
  }

  public void remove(String applicationName) throws DeploymentException, WarningException {
  }

  public void removeProgressListener(ProgressListener listener) {
  }

  public void rollbackDeploy(String applicationName) throws WarningException {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("PolicyConfigurationContainer.rollbackDeploy() for application: {0}", new Object[] {applicationName});
    }
    
    clearPolicyConfigurationRoots();
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
    if (LOCATION.beDebug()) {
      LOCATION.debugT("PolicyConfigurationContainer.rollbackUpdate() for application: {0}", new Object[] {applicationName});
    }
    
    clearPolicyConfigurationRoots();
  }

  public void commitRemove(String applicationName) throws WarningException {
  }

  public ApplicationStartInfo makeStartInitially(ContainerStartInfo csInfo) throws DeploymentException {
    return null;
  }

  public void notifyRemove(String applicationName) throws WarningException {
  }

  public void remove(String applicationName, ConfigurationHandler operationHandler, Configuration appConfiguration) throws DeploymentException, WarningException {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("PolicyConfigurationContainer.remove() for application: {0}", new Object[] {applicationName});
    }
    
    clearPolicyConfigurationRoots();
  }

}
