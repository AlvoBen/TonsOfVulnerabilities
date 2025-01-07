package com.sap.engine.services.dc.cm.deploy;

import java.io.Serializable;
import java.util.Collection;
import java.util.Enumeration;

public interface DeploymentBatch extends Serializable {

	public DeploymentBatchItem getDeploymentBatchItem(String name, String vendor);

	public void removeDeploymentBatchItem(
			DeploymentBatchItem deploymentBatchItem);

	public void removeDeploymentBatchItems(
			Collection<DeploymentBatchItem> deploymentBatchItems);

	public Collection<DeploymentBatchItem> getDeploymentBatchItems();

	/**
	 * @return
	 *         <code>Enumeration<> with the batch deployment items which are admitted.
	 */
	public Enumeration getAdmittedDeploymentBatchItems();

	/**
	 * 
	 * @return all the SDAs that are admitted for deployment
	 */
	public Collection<DeploymentBatchItem> getAllAdmittedDeplItems();

	/**
	 * 
	 * @return the composite items (SCA)
	 */
	public Collection<CompositeDeploymentItem> getAllCompositeDeplItems();

	/**
	 * Remove all the items from the batch
	 */
	public void clear();

}
