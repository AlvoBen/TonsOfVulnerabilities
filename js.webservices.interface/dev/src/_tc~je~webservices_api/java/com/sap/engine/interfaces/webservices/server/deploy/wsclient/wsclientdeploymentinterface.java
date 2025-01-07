package com.sap.engine.interfaces.webservices.server.deploy.wsclient;

import com.sap.engine.interfaces.webservices.server.deploy.WSDeploymentException;
import com.sap.engine.interfaces.webservices.server.deploy.WSWarningException;
import com.sap.engine.frame.core.configuration.Configuration;

/**
 * Title: WSClientDeploymentInterface
 * Description: This interface provides methods for notification on ws client deploy and remove phase.
 * Any component that is interested in notifying on these phases, should implement the interface.
 * Copyright: Copyright (c) 2004
 * Company: Sap Labs Bulgaria
 * @author Dimitrina Stoyanova
 * @version 6.30
 */

public interface WSClientDeploymentInterface {

  /**
   * This method is invoked on ws client deployment.
   * If the deploy phase is expected to fail WSDeploymentException should be thrown.
   * Otherwise if the phase is expected to finish successfully, but some warnings are generated during the process, WSWarningException should be thrown.
   *
   * @param applicationName - The name of the application, that the ws client belongs to.
   * @param wsClientName  - The name of the ws client, that is being deployed.
   * @param wsClientContext -  The context, that provides the deployment information of the ws client.
   * @param dir - The name of the directory, which should be used by the component to store its data on the local file system.
   * @param wsClientConfiguration - The ws client configuration, which should be used by the component to create its own subconfiguration and store its persistent data.
   * @exception WSDeploymentException - Such an exception should be thrown, when the deployment is going to fail.
   * @exception WSWarningException - Such an exception should be thrown, when the deployment is going to pass successfully, but some warnings are genereted during the process.
   */

  public void onDeploy(String applicationName, String wsClientName, WSClientBaseContext wsClientContext, String dir, Configuration wsClientConfiguration)
    throws WSDeploymentException, WSWarningException;

  /**
   * This method is invoked after the first ws client deploy phase.
   * Some initializations are expected to be done here that could make the whole deploy process fail.
   * If the post deploy phase is expected to fail WSDeploymentException should be thrown.
   * Otherwise if the phase is expected to finish successfully, but some warnings are generated during the process, WSWarningException should be thrown.
   *
   * @param applicationName - The name of the application, that the ws client belongs to.
   * @param wsClientName  - The name of the ws client, that is being deployed.
   * @param wsClientContext -  The context, that provides the deployment information of the ws client.
   * @param dir - The name of the directory, which should be used by the component to store its data on the local file system.
   * @param wsClientConfiguration - The ws client configuration, which should be used by the component to create its own subconfiguration and store its persistent data.
   * @exception WSDeploymentException - Such an exception should be thrown, when the deployment is going to fail.
   * @exception WSWarningException - Such an exception should be thrown, when the deployment is going to pass successfully, but some warnings are genereted during the process.
   */

  public void onPostDeploy(String applicationName, String wsClientName, WSClientContext wsClientContext, String dir, Configuration wsClientConfiguration)
    throws WSDeploymentException, WSWarningException;

  /**
   * This method is invoked to notify that the first and the second deployment phases have passed successfully.
   * Only WSWarningException is possible to be thrown, if some errors have been generated.
   *
   * @param applicationName - The name of the application, that the ws client belongs to.
   * @param wsClientName  - The name of the ws client, that is being deployed.
   * @exception WSWarningException - Such an exception should be thrown, when the deployment is going to pass successfully, but some warnings are genereted during the process.
   */

  public void onCommitDeploy(String applicationName, String wsClientName)
    throws WSWarningException;

  /**
   * This method is invoked to notify that the ws client deployment has failed. The deploy actions should be rolled back.
   * Only WSWarningException is possible to be thrown, if some errors have been generated.
   *
   * @param applicationName - The name of the application, that the ws client belongs to.
   * @param wsClientName  - The name of the ws client, that is being deployed.
   */

  public void onRollbackDeploy(String applicationName, String wsClientName)
    throws WSWarningException;

  /**
   * This method is invoked on application remove. The persistent stored data should be removed.
   * Only WSWarningException is possible to be thrown, if some errors have been generated.
   *
   * @param applicationName - The name of the application 
   */

  public void onRemove(String applicationName) throws WSWarningException;

}
