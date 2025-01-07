package com.sap.sdm.api.remote.cvers.results;

import com.sap.sdm.api.remote.RemoteException;

/**
 * This class encapsulates the result of <i>synchronization</i> operation with CVERS table.
 * 
 * @author Lalo Ivanov
 */
public interface CVersSyncResult extends CVersResultType {
  
  public static int CVERS_OPERATION_PARTIALLY_FAILED = 2; 
  
  /**
   * @return the total count of components(SCs or DCs) that 
   *         were synchronized with the CVERS table 
   */
  public int getTotalSyncComponentsCount() throws RemoteException;
  
  /**
   * @return the count of components(SCs or DCs) that failed
   *         to be synchronized with CVERS table
   */
  public int getFailedSyncComponentsCount() throws RemoteException;
  
  /**
   * @return the count of components(SCs or DCs) that were
   *         successfully synchronized with CVERS table
   */
  public int getSuccessSyncComponentsCount() throws RemoteException;

}