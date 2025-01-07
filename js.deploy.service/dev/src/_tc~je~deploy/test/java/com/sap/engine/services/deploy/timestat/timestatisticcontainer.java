/*
 * Copyright (c) 2005 by SAP AG, Walldorf., http://www.sap.com All rights
 * reserved.
 * 
 * This software is the confidential and proprietary information of SAP AG,
 * Walldorf. You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered
 * into with SAP.
 */
package com.sap.engine.services.deploy.timestat;

import java.io.File;
import java.util.Properties;
import java.util.Vector;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.services.deploy.container.AdditionalAppInfo;
import com.sap.engine.services.deploy.container.ApplicationDeployInfo;
import com.sap.engine.services.deploy.container.ContainerDeploymentInfo;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.FileUpdateInfo;
import com.sap.engine.services.deploy.container.ProgressEvent;
import com.sap.engine.services.deploy.container.ProgressListener;
import com.sap.engine.services.deploy.container.TimeStatisticEvent;
import com.sap.engine.services.deploy.container.WarningException;

/**
 * A simple container class with progress listener 
 * implementation to provide time statistic events.
 * 
 * @author Todor Stoitsev
 */
public class TimeStatisticContainer implements ContainerInterface {
  private ContainerInfo mInfo;
  private Vector vProgressListeners;
  
  public TimeStatisticContainer(String sContainerName) {
    mInfo = new ContainerInfo();
    mInfo.setFileExtensions(new String[]{".test"} );
    mInfo.setFileNames(null);
    mInfo.setJ2EEContainer(false);
    mInfo.setModuleName(sContainerName);
    mInfo.setName(sContainerName);
    mInfo.setPriority(10);
    mInfo.setResourceTypes(new String[]{"type1","type2"});
    mInfo.setServiceName(sContainerName);    
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#getContainerInfo()
   */
  public ContainerInfo getContainerInfo() {
    return mInfo;
  }
  
  public ApplicationDeployInfo deploy(File[] files, ContainerDeploymentInfo deployInfoForContainer, Properties props) throws DeploymentException {
    ApplicationDeployInfo result = new ApplicationDeployInfo();
    result.setDeployedComponentNames(new String[] {"comp_0"});
    result.setDeployProperties(props);
    result.setFilesForClassloader(new String[0]);
    result.setLoaderName("TimeStatisticContainerLoaderName");
    result.setOptionalContainer(true);
    // time statistic recording
    TimeStatisticEvent evt = 
      new TimeStatisticEvent(
          this,
          getContainerInfo().getName(),
          "deploy/sub_1/sub_1.1/sub_1.1.1",
          0,
          1);
    fireProgressEvent(evt);
    
    evt = 
      new TimeStatisticEvent(
          this,
          getContainerInfo().getName(),
          "deploy/sub_1/sub_1.1/sub_1.1.2",
          0,
          3);
    fireProgressEvent(evt);
    
    evt = 
      new TimeStatisticEvent(
          this,
          getContainerInfo().getName(),
          "deploy/sub_1/sub_1.1",
          0,
          4);
    fireProgressEvent(evt);
    evt = 
      new TimeStatisticEvent(
          this,
          getContainerInfo().getName(),
          "deploy/sub_1/sub_1.2",
          0,
          6);
    fireProgressEvent(evt);
    
    evt = 
      new TimeStatisticEvent(
          this,
          getContainerInfo().getName(),
          "deploy/sub_1",
          0,
          10);
    fireProgressEvent(evt);
    evt = 
      new TimeStatisticEvent(
          this,
          getContainerInfo().getName(),
          "deploy/sub_2",
          0,
          0);
    fireProgressEvent(evt);
    return result;
  }
  
  /**
   * Fie progress event for all registred listeners.
   * @param evt
   */
  private void fireProgressEvent(ProgressEvent evt) {
    synchronized(vProgressListeners) {
      for(int i = 0; i < vProgressListeners.size(); i++) {
        ((ProgressListener)vProgressListeners.elementAt(i)).handleProgressEvent(evt);
      }
    }
  }
  
  public ApplicationDeployInfo makeUpdate(File[] arg0, ContainerDeploymentInfo arg1, Properties arg2) throws DeploymentException {
    ApplicationDeployInfo result = new ApplicationDeployInfo();        
    result.setFilesForClassloader(new String[0]);
    result.setLoaderName("TimeStatisticContainerLoaderName");
    result.setOptionalContainer(true);
    result.setDeployedComponentNames(new String[] {"Time statistic component"});
    // time statistic recording
    TimeStatisticEvent evt = 
      new TimeStatisticEvent(
          this,
          getContainerInfo().getName(),
          "makeUpdate/sub_1/sub_1.1/sub_1.1.1",
          0,
          1,
          0,
          1000);
    fireProgressEvent(evt);
    
    evt = 
      new TimeStatisticEvent(
          this,
          getContainerInfo().getName(),
          "makeUpdate/sub_1/sub_1.1/sub_1.1.2",
          0,
          3,
          12000,
          24000);
    fireProgressEvent(evt);
    
    evt = 
      new TimeStatisticEvent(
          this,
          getContainerInfo().getName(),
          "makeUpdate/sub_1/sub_1.1",
          0,
          4);
    fireProgressEvent(evt);
    evt = 
      new TimeStatisticEvent(
          this,
          getContainerInfo().getName(),
          "makeUpdate/sub_1/sub_1.2",
          0,
          6);
    fireProgressEvent(evt);
    
    evt = 
      new TimeStatisticEvent(
          this,
          getContainerInfo().getName(),
          "makeUpdate/sub_1",
          0,
          10,
          4000,
          8000);
    fireProgressEvent(evt);
    evt = 
      new TimeStatisticEvent(
          this,
          getContainerInfo().getName(),
          "makeUpdate/sub_2",
          0,
          0);
    fireProgressEvent(evt);
    return result;
  }
  
  public boolean needUpdate(File[] arg0, ContainerDeploymentInfo arg1, Properties arg2) throws DeploymentException, WarningException {

    // time statistic recording
    TimeStatisticEvent evt = 
      new TimeStatisticEvent(
          this,
          getContainerInfo().getName(),
          "needUpdate/sub_1/sub_1.1/sub_1.1.1",
          0,
          1);
    fireProgressEvent(evt);
    
    evt = 
      new TimeStatisticEvent(
          this,
          getContainerInfo().getName(),
          "needUpdate/sub_1/sub_1.1/sub_1.1.2",
          0,
          3);
    fireProgressEvent(evt);
    
    evt = 
      new TimeStatisticEvent(
          this,
          getContainerInfo().getName(),
          "needUpdate/sub_1/sub_1.1",
          0,
          4);
    fireProgressEvent(evt);
    evt = 
      new TimeStatisticEvent(
          this,
          getContainerInfo().getName(),
          "needUpdate/sub_1/sub_1.2",
          0,
          6);
    fireProgressEvent(evt);
    
    evt = 
      new TimeStatisticEvent(
          this,
          getContainerInfo().getName(),
          "needUpdate/sub_1",
          0,
          10);
    fireProgressEvent(evt);
    evt = 
      new TimeStatisticEvent(
          this,
          getContainerInfo().getName(),
          "needUpdate/sub_2",
          0,
          0);
    fireProgressEvent(evt);
    return true;
  }

  public boolean needStopOnUpdate(File[] arg0, ContainerDeploymentInfo arg1, Properties arg2) throws DeploymentException, WarningException {
    return false;
  }
  
  
  // fake method below
  
  public void notifyDeployedComponents(String arg0, Properties arg1) throws WarningException {
  }


  public String getApplicationName(File arg0) throws DeploymentException {
    return null;
  }
  
  public void prepareDeploy(String arg0, Configuration arg1) throws DeploymentException, WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#commitDeploy(java.lang.String)
   */
  public void commitDeploy(String arg0) throws WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#rollbackDeploy(java.lang.String)
   */
  public void rollbackDeploy(String arg0) throws WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#notifyUpdatedComponents(java.lang.String, com.sap.engine.frame.core.configuration.Configuration, java.util.Properties)
   */
  public void notifyUpdatedComponents(String arg0, Configuration arg1, Properties arg2) throws WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#prepareUpdate(java.lang.String)
   */
  public void prepareUpdate(String arg0) throws DeploymentException, WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#commitUpdate(java.lang.String)
   */
  public ApplicationDeployInfo commitUpdate(String arg0) throws WarningException {
    return null;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#rollbackUpdate(java.lang.String, com.sap.engine.frame.core.configuration.Configuration, java.util.Properties)
   */
  public void rollbackUpdate(String arg0, Configuration arg1, Properties arg2) throws WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#remove(java.lang.String)
   */
  public void remove(String arg0) throws DeploymentException, WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#downloadApplicationFiles(java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
   */
  public void downloadApplicationFiles(String arg0, Configuration arg1) throws DeploymentException, WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#prepareStart(java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
   */
  public void prepareStart(String arg0, Configuration arg1) throws DeploymentException, WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#commitStart(java.lang.String)
   */
  public void commitStart(String arg0) throws WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#rollbackStart(java.lang.String)
   */
  public void rollbackStart(String arg0) throws WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#prepareStop(java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
   */
  public void prepareStop(String arg0, Configuration arg1) throws DeploymentException, WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#commitStop(java.lang.String)
   */
  public void commitStop(String arg0) throws WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#rollbackStop(java.lang.String)
   */
  public void rollbackStop(String arg0) throws WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#notifyRuntimeChanges(java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
   */
  public void notifyRuntimeChanges(String arg0, Configuration arg1) throws WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#prepareRuntimeChanges(java.lang.String)
   */
  public void prepareRuntimeChanges(String arg0) throws DeploymentException, WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#commitRuntimeChanges(java.lang.String)
   */
  public ApplicationDeployInfo commitRuntimeChanges(String arg0) throws WarningException {
    return null;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#rollbackRuntimeChanges(java.lang.String)
   */
  public void rollbackRuntimeChanges(String arg0) throws WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#getClientJar(java.lang.String)
   */
  public File[] getClientJar(String arg0) {
    return null;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#addProgressListener(com.sap.engine.services.deploy.container.ProgressListener)
   */
  public void addProgressListener(ProgressListener arg0) {
    if(arg0 == null)
      return;
    if(vProgressListeners == null)
      vProgressListeners = new Vector();
    vProgressListeners.add(arg0);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#removeProgressListener(com.sap.engine.services.deploy.container.ProgressListener)
   */
  public void removeProgressListener(ProgressListener arg0) {
    vProgressListeners.remove(arg0);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#needStopOnSingleFileUpdate(com.sap.engine.services.deploy.container.FileUpdateInfo[], com.sap.engine.services.deploy.container.ContainerDeploymentInfo, java.util.Properties)
   */
  public boolean needStopOnSingleFileUpdate(FileUpdateInfo[] arg0, ContainerDeploymentInfo arg1, Properties arg2) throws DeploymentException, WarningException {
    return false;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#makeSingleFileUpdate(com.sap.engine.services.deploy.container.FileUpdateInfo[], com.sap.engine.services.deploy.container.ContainerDeploymentInfo, java.util.Properties)
   */
  public ApplicationDeployInfo makeSingleFileUpdate(FileUpdateInfo[] arg0, ContainerDeploymentInfo arg1, Properties arg2) throws DeploymentException {
    return null;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#notifySingleFileUpdate(java.lang.String, com.sap.engine.frame.core.configuration.Configuration, java.util.Properties)
   */
  public void notifySingleFileUpdate(String arg0, Configuration arg1, Properties arg2) throws WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#prepareSingleFileUpdate(java.lang.String)
   */
  public void prepareSingleFileUpdate(String arg0) throws DeploymentException, WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#commitSingleFileUpdate(java.lang.String)
   */
  public ApplicationDeployInfo commitSingleFileUpdate(String arg0) throws WarningException {
    return null;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#rollbackSingleFileUpdate(java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
   */
  public void rollbackSingleFileUpdate(String arg0, Configuration arg1) throws WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#applicationStatusChanged(java.lang.String, byte)
   */
  public void applicationStatusChanged(String arg0, byte arg1) {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#getResourcesForTempLoader(java.lang.String)
   */
  public String[] getResourcesForTempLoader(String arg0) throws DeploymentException {
    return null;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#acceptedAppInfoChange(java.lang.String, com.sap.engine.services.deploy.container.AdditionalAppInfo)
   */
  public boolean acceptedAppInfoChange(String arg0, AdditionalAppInfo arg1) throws DeploymentException {
    return false;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#needStopOnAppInfoChanged(java.lang.String, com.sap.engine.services.deploy.container.AdditionalAppInfo)
   */
  public boolean needStopOnAppInfoChanged(String arg0, AdditionalAppInfo arg1) {
    return false;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#makeAppInfoChange(java.lang.String, com.sap.engine.services.deploy.container.AdditionalAppInfo, com.sap.engine.frame.core.configuration.Configuration)
   */
  public void makeAppInfoChange(String arg0, AdditionalAppInfo arg1, Configuration arg2) throws WarningException, DeploymentException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#appInfoChangedCommit(java.lang.String)
   */
  public void appInfoChangedCommit(String arg0) throws WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#appInfoChangedRollback(java.lang.String)
   */
  public void appInfoChangedRollback(String arg0) throws WarningException {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.ContainerInterface#notifyAppInfoChanged(java.lang.String)
   */
  public void notifyAppInfoChanged(String arg0) throws WarningException {
  }

}
