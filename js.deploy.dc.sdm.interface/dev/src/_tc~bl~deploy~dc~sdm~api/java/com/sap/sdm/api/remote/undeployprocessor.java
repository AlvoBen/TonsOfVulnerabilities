package com.sap.sdm.api.remote;

/**
 * Provides methods for executing the undeployment process on the remote server.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Thomas Brodkorb
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.undeploy.UndeployProcessor</code>.
 */
public interface UnDeployProcessor {
  /**
   * Executes the undeployment of the specified components on the SDM server.
   * Each component is specified via a <code>UnDeployItem</code> that also
   * provides a description of the undeploy result for the particular component
   * after the method has returned.
   * 
   * @param unDeployItems an array of <code>UnDeployItem</code>
   */
  public void undeploy(UnDeployItem[] undeployItems) throws RemoteException;
  

}
