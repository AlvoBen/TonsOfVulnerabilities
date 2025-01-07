/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.frame;

import com.sap.engine.frame.cluster.ApplicationClusterContext;
import com.sap.engine.frame.container.ApplicationContainerContext;

/**
 * This interface is connection from the service working on Server node to
 * the system.
 *
 * @author Jasen Minov
 * @version 6.30
 */
public interface ApplicationServiceContext
  extends ServiceContext {

  /**
   * Gives access to the ApplicationClusterContext. It provides a way for
   * communication within the cluster. It contains some specific sub contexts
   * for interaction with dispatcher parts from the cluster.
   *
   * @return   ApplicationClusterContext for access to server specific cluster
   * communication.
   */
  public ApplicationClusterContext getClusterContext();


  /**
   * Gives access to the ApplicationContainerContext. It provides a way for
   * iteraction with service container
   *
   * @return   ApplicationContainerContext for access to server specific service
   * container methods.
   */
  public ApplicationContainerContext getContainerContext();

}

