package com.sap.sdm.api.remote;

import com.sap.sdm.api.remote.deployresults.DeployResultType;

/**
 * Represents the result of a started deployment process.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.deploy.DeployResult</code>.
 */
public interface DeployResult {

  /**
   * Returns the result type of the started deployment process.
   *
   * @return a <code>DeployResultType</code> representing the result type 
   *
   * @see com.sap.sdm.api.remote.deployresults
   */
  public DeployResultType getType() throws RemoteException;

  /**
   * Returns the result text of the started deployment process.
   *
   * @return a <code>String</code> containing the result text
   */
  public String getResultText();
}
