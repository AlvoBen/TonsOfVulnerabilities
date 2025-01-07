/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.frame;

import com.sap.engine.frame.cluster.CommunicationClusterContext;
import com.sap.engine.frame.container.CommunicationContainerContext;

/**
 * This interface is connection from the service working on Dispatcher node to
 * the system.
 *
 * @author Jasen Minov
 * @version 6.30
 * @deprecated Dispatcher is replaced with ICM.
 */
public interface CommunicationServiceContext
  extends ServiceContext {

  /**
   * Gives access to the CommunicationClusterContext. It provides a way for
   * communication within the cluster. It contains some specific sub contexts
   * for interaction with clients.
   *
   * @return   CommunicationClusterContext for access to dispatcher specific
   * cluster communication.
   */
  public CommunicationClusterContext getClusterContext();


  /**
   * Gives access to the CommunicationContainerContext. It provides a way for
   * iteraction with service container
   *
   * @return   CommunicationContainerContext for access to dispatcher specific
   * service container methods.
   */
  public CommunicationContainerContext getContainerContext();

}

