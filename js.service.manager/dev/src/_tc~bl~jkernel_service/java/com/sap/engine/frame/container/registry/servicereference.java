/*
 * Copyright (c) 2002 by SAP Labs Sofia AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.frame.container.registry;

import java.rmi.Remote;

/**
 * Interface used for binding the service interfaces in the naming registry
 *
 * @author Krasimir Semerdzhiev (krasimir.semerdzhiev@sap.com)
 * @version 6.30
 */
public interface ServiceReference extends Remote {

  /**
   * Gets the service interface
   *
   * @return   The ServiceReference requested
   */
  public Remote getServiceInterface();

  /**
   *  Releases the referenced service
   */
  public void releaseService();

}

