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
package com.sap.engine.frame.container.deploy;

import com.sap.engine.frame.container.deploy.zdm.RollingPatch;

import java.io.File;

/**
 * DeployContext is taking care for all server components management. This inlcudes
 * deploy, remove, making of references and status changes for the three defined
 * components - Libraries, Plug-in Interfaces and Services. It also includes
 * some methods for retrieving information about these components.
 *
 * @author   Monika Kovachka
 * @version   6.30
 */
public interface DeployContext {

  /**
   * Deploys new interface. Jar name represent the name of this component.
   *
   * @param   jar - the jar that define this interface.
   * @exception   ComponentDeploymentException  - thrown if could not deploy the interface.
   *
   * @return runtime name of the interface
   */
  public String deployInterface(File jar) throws ComponentDeploymentException;

  /**
   * Deploys new library. Jar name represent the name of this component
   *
   * @param   jar - the jar that define this library.
   * @exception   ComponentDeploymentException  - thrown if could not deploy the library.
   *
   * @return runtime name of the library
   */
  public String deployLibrary(File jar) throws ComponentDeploymentException;

  /**
   * Deploys new service. Jar name represent the name of this component
   *
   * @param   jar - the jar that define this service.
   * @exception   ComponentDeploymentException  - thrown if could not deploy the service.
   *
   * @return runtime name of the service
   */
  public String deployService(File jar) throws ComponentDeploymentException;

  /**
   * Removes the plugin interface with the specified name from the available interfaces.
   *
   * @param providerName - name of the provider (keyvendor from SAP_MANIFEST.MF)
   * @param intfName - the name of the interface which will be removed (keyname from SAP_MANIFEST.MF)
   * @exception   ComponentNotDeployedException - thrown if removed component doesn't exist.
   * @exception   ComponentDeploymentException - thrown if the remove is not successful.
   */
  public void removeInterface(String providerName, String intfName) throws ComponentNotDeployedException, ComponentDeploymentException;

  /**
   * Removes the library with the specified name from the defined libraries.
   *
   * @param providerName - name of the provider (keyvendor from SAP_MANIFEST.MF)
   * @param libName - the name of the library which will be removed (keyname from SAP_MANIFEST.MF)
   * @exception   ComponentNotDeployedException - thrown if removed component doesn't exist.
   * @exception   ComponentDeploymentException - thrown if the remove is not successful.
   */
  public void removeLibrary(String providerName, String libName) throws ComponentNotDeployedException, ComponentDeploymentException;

  /**
   * Removes the service with the specified name from the deployed services.
   *
   * @param providerName - name of the provider (keyvendor from SAP_MANIFEST.MF)
   * @param serviceName - the name of the service which will be removed (keyname from SAP_MANIFEST.MF)
   * @exception   ComponentNotDeployedException - thrown if removed component doesn't exist.
   * @exception   ComponentDeploymentException - thrown if the remove is not successful.
   */
  public void removeService(String providerName, String serviceName) throws ComponentNotDeployedException, ComponentDeploymentException;

  /**
   * Notifies the kernel that the initial startup of the applications finishes. 
   * 
   * <p>The deploy container has to invoke this in order the kernel to be 
   * notified that the initial startup of the applications is finished and the 
   * node is fully up and running. </p>
   * 
   * <p>As a consequence, for example, the running state in the startup 
   * framework of the node will be change from 'Applications Starting' to 
   * 'Running'.</p> 
   */
  public void applicationsStarted();

  /**
   * Returns <code>RollingPatch</code> instance. This instance is used to apply rolling approach for online
   * components at instance level
   *
   * @return RollingPatch instance
   *
   * @see com.sap.engine.frame.container.deploy.zdm.RollingPatch
   * @deprecated
   */
  public RollingPatch getRollingPatch();

}