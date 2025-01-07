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
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.deploy.DeployItem</code>.
 */
public interface DeployItem {

  /**
	 * Return the archive name after deployment or validation has
	 * been executed and the archive was read successfully.
	 * Otherwise the returned value is null.
	 * 
	 * @return a <code>String</code>
	 */
	public String getName();

	/**
	 * Return the archive vendor after deployment or validation has
	 * been executed and the archive was read successfully.
	 * Otherwise the returned value is null.
	 * 
	 * @return a <code>String</code>
	 */
	public String getVendor();

  /**
   * Returns the deploy result of the linked archive after the deployment has
   * been executed.
   * 
   * @return a <code>DeployResult</code>
   */
  public DeployResult getDeployResult() throws RemoteException;

}
