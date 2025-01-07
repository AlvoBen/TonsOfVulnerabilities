/*
 * Created on 2005-3-22
 *
 * Author: Lalo Ivanov
 * Team: Software Deployment Manager(SDM)
 */
package com.sap.sdm.api.remote.cvers;

import com.sap.sdm.api.remote.cvers.results.CVersSyncResult;
import com.sap.sdm.api.remote.RemoteException;

/**
 * Provides methods for working with the CVERS(BC_COMPVERS) database table.
 *  
 * @author Lalo Ivanov
 * @version 1.0
 * 
 */
public interface CVersManager {
  
  /**
   * Trigger synchronisation between SDM repository and CVERS table.
   * 
   * @return CVersSyncResult
   */
  public CVersSyncResult triggerCVersSync() throws RemoteException;
  
}
