/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.server;

import com.sap.engine.admin.model.ManagementModelManager;
import com.sap.engine.frame.state.ManagementInterface;

/**
 * @author Nikolai Neychev
 * @version 6.30
 */
public interface IIOPManagementInterface extends ManagementInterface {

  /**
   * Gets the exported object count
   * @return The object count
   */
  public int getExportedRemoteObjectsCount();

  public int getIIOPThreadUsageRate();
  
  public void registerResources(ManagementModelManager mmManager);
  
  public void unregisterResources();

}
