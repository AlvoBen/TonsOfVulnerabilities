package com.sap.sdm.api.remote;

/**
 * Provides a link from an archive to its deploy result.
 * Use
 * {@link com.sap.sdm.api.remote.HelperFactory#createDeployItem(File)}
 * to create an instance of this interface.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.undeploy.UndeployItem</code>.
 */
public interface UnDeployItem {

  /**
   * Returns the undeploy result of the linked component after the undeployment 
   * has been executed.
   * 
   * @return a <code>UnDeployResult</code>
   */
  public UnDeployResult getUnDeployResult() throws RemoteException;
  
  /**
   * Returns the deploymentID of the undeployed component after the undeployment
   * has been executed.
   * 
   * @return a <code>String</code>
   */
  public String getDeploymentID() throws RemoteException;

}
