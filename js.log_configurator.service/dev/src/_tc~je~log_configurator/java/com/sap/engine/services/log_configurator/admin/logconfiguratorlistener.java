package com.sap.engine.services.log_configurator.admin;

/**
 * Title:        Logging
 * Description:  Logging API
 * Copyright:    Copyright (c) 2002
 * Company:      SAP Labs Bulgaria LTD., Sofia, Bulgaria.
 * Url:          Http://www.saplabs.bg
 *               All rights reserved.
 *
 *               This software is the confidential and proprietary information
 *               of SAP AG International ("Confidential Information").
 *               You shall not disclose such  Confidential Information
 *               and shall use it only in accordance with the terms of
 *               the license agreement you entered into with SAP AG.
 */

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Georgi Manev
 * @version 6.30
 */
public interface LogConfiguratorListener extends Remote {

  /**
   *
   */
  public void configurationChanged(long newVersion) throws RemoteException;
  
  /**
   * Change state for several Log Controllers
   * 
   * @param state new state
   * @param parent the name of the first Log Controller in the hierarchy that is changed 
   * @param descriptors the names of all changed Log Controllers (all subtree from parent)
   */
  public void stateChanged( byte state, String parent, String[] descriptors ) throws RemoteException;

}
