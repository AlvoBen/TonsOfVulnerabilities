/*
 * Created on 2005-3-22
 *
 * Author: Lalo Ivanov
 * Team: Software Deployment Manager(SDM)
 */
package com.sap.sdm.api.remote.cvers.results;

import com.sap.sdm.api.remote.RemoteException;

/**
 * This class encapsulates the result of operation with CVERS table.
 * 
 * @author lalo-i
 */
public interface CVersResultType {
 
  public static int CVERS_OPERATION_OK     = 0;
  
  public static int CVERS_OPERATION_FAILED = 1;  
  
  /**
   * @return int see {@link CVERS_OPERATION_OK} and {@link CVERS_OPERATION_FAILED} 
   */
  public int getResultType() throws RemoteException;
  
  /**
   * @return String text description of the result 
   */
  public String getResultText() throws RemoteException;  
  
}
