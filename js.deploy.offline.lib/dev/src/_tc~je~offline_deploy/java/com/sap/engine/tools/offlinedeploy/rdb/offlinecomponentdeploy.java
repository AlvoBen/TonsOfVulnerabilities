/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.tools.offlinedeploy.rdb;

import java.io.File;

/**
 * This class provides offline deploy and undeploy of components to and from DB.
 *
 * <p>Components are packed in SDAs. There are seven supported software types of deployable SDAs:
 * 1) primary-interface 2) primary-library; 3) library; 4) primary-service; 5) engine-kernel; 6) engine-bootstrap;
 * 7) j2ee-template.</p>
 *
 * @author Dimitar Kostadinov
 * @version 710
 */

public interface OfflineComponentDeploy {

  public static final byte TYPE_INTERFACE = 100;
  public static final byte TYPE_LIBRARY   = 101;
  public static final byte TYPE_SERVICE   = 102;
  public static final byte TYPE_KERNEL    = 103;
  public static final byte TYPE_BOOTSTRAP = 104;
  public static final byte TYPE_TEMPLATE  = 105;

  /**
   * default sap providers.
   */
  public static final String[] SAP_PROVIDERS = new String[] {"engine.sap.com", "sap.com"};

  /**
   * Called to deploy component in DB.
   *
   * @param jar archive containing component to be deployed.
   * @param componentType type of component to be deployed.
   * @throws DeploymentException if there has been a fatal problem while prforming operation.
   * @return status of the deployment operation.
   */
  public ResultStatus deployComponent(File jar, byte componentType) throws DeploymentException;

  /**
   * Called to undeploy component with providerName and componentName given from DB.
   *
   * @param providerName name of the component provider.
   * @param componentName name of component to be undeployed.
   * @param componentType type of component to be undeployed.
   * @throws DeploymentException if there has been a fatal problem while prforming operation.
   * @throws ComponentNotDeployedException if component doesn't exist.
   * @return status of the undeployment operation.
   */
  public ResultStatus undeployComponent(String providerName, String componentName, byte componentType) throws DeploymentException, ComponentNotDeployedException;

  /**
   * Begin new DB transaction and switch off autocommit mode.
   * After invocation a set of components may be deployed in one transaction.
   * Use <code>commit()</code> to apply or <code>rollback()</code> to discard changes.
   */
  public void begin();

  /**
   * Rollback DB transaction in case of external error.
   * Use this method only after <code>begin()</code> is invoked.
   */
  public void rollback() throws DeploymentException;

  /**
   * Commit DB transaction, begin must be called first else the method silently return.
   * Use this method only after <code>begin()</code> is invoked.
   */
  public void commit() throws DeploymentException;

  /**
   * Close all open DB connections if an offline configuration manager is used.
   */
  public void close();

}