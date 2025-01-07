/*
 * Created on 2005-2-10
 *
 */
package com.sap.sdm.api.remote;

import com.sap.sdm.api.remote.validateresults.ValidateResultType;

/**
 *
 * Represents the result of the archive validation procedure.
 *  
 * @author ivan-mih
 * 
 *  @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.deploy.ValidationResult</code>.
 */
public interface ValidateResult {
  
  /**
   * 
   * @return a <code>ValidateResultType</code> representing result type 
   * of the validation.
   */
  public ValidateResultType getType() throws RemoteException;
  
  /**
   * 
   * @return a <code>String</code> describing the result. 
   */
  public String getResultText() throws RemoteException;
  
  /**
   * 
   * @return true if there is at least one deploy item for offline deploy, otherwise false
   */
  public boolean isOfflinePhaseScheduled() throws RemoteException;

  /**
   * 
   * @return array containing DeployItems sorted respecting to their place in the deploy order
   */
  public DeployItem[] getSortedDeploymentBatchItems() throws RemoteException;

  /**
   * 
   * @return array with the given for validating deployItems
   */
  public DeployItem[] getDeploymentBatchItems() throws RemoteException;
}
