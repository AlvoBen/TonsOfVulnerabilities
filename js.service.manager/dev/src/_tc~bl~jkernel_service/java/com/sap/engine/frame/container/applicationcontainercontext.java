/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.frame.container;

import com.sap.engine.frame.container.deploy.DeployContext;

/**
 * It provides a way for iteraction with service container on server node
 *
 * @author Jasen Minov
 * @version 6.30
 */
public interface ApplicationContainerContext extends ServiceContainerContext {

  /**
   * Get DeployContext that provides a way for deploy/undeploy different kinds of
   * modules to the system
   *
   * @return  DeployContext for deploy/undeploy plug-in parts
   */
  public DeployContext getDeployContext();

}

