package com.sap.sdm.api.remote;

import com.sap.sdm.api.remote.undeployresults.UnDeployResultType;

/**
 * Represents the result of a started undeployment process.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Thomas Brodkorb
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.undeploy.UndeployResult</code>.
 */
public interface UnDeployResult {

  /**
   * Returns the result type of the started undeployment process.
   *
   * @return a <code>UnDeployResultType</code> representing the result type 
   *
   * @see com.sap.sdm.api.remote.undeployresults
   */
  public UnDeployResultType getType() throws RemoteException;

  /**
   * Returns the result text of the started undeployment process.
   *
   * @return a <code>String</code> containing the result text
   */
  public String getResultText();
}
