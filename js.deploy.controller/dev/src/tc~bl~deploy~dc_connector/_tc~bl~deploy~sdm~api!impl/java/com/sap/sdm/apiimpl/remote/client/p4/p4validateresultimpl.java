/*
 * Created on 2005-7-4 by radoslav-i
 */
package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.sdm.api.remote.DeployItem;
import com.sap.sdm.api.remote.RemoteException;
import com.sap.sdm.api.remote.ValidateResult;
import com.sap.sdm.api.remote.validateresults.ValidateResultType;

/**
 * @author radoslav-i
 */
class P4ValidateResultImpl implements ValidateResult {

	final com.sap.engine.services.dc.api.deploy.ValidationResult dcValidateResult;
	private DeployItem[] deploymentBatchItems;
	private DeployItem[] sortedDeploymentBatchItems;
	private ValidateResultType type;
	private boolean isOfflinePhaseScheduled = false;
	private String resultText = "";

	P4ValidateResultImpl() {
		dcValidateResult = null;
	}

	P4ValidateResultImpl(
			com.sap.engine.services.dc.api.deploy.ValidationResult dcValidateResult) {
		this.dcValidateResult = dcValidateResult;
		this.type = P4ValidateResultTypeMapper.map(dcValidateResult);
		this.isOfflinePhaseScheduled = dcValidateResult
				.isOfflinePhaseScheduled();
		this.resultText = dcValidateResult.toString();
		// this.deploymentBatchItems =
		// buildDeployItems(dcValidateResult.getDeploymentBatchItems());
		// this.sortedDeploymentBatchItems =
		// buildDeployItems(dcValidateResult.getSortedDeploymentBatchItems());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.ValidateResult#getType()
	 */
	public ValidateResultType getType() throws RemoteException {
		return this.type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.ValidateResult#setType()
	 */
	public void setType(ValidateResultType validateResultType)
			throws RemoteException {
		this.type = validateResultType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.ValidateResult#getResultText()
	 */
	public String getResultText() throws RemoteException {
		return resultText;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.ValidateResult#isOfflinePhaseScheduled()
	 */
	public boolean isOfflinePhaseScheduled() throws RemoteException {
		return this.isOfflinePhaseScheduled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.ValidateResult#getSortedDeploymentBatchItems()
	 */
	public DeployItem[] getSortedDeploymentBatchItems() throws RemoteException {
		return this.sortedDeploymentBatchItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.ValidateResult#getDeploymentBatchItems()
	 */
	public DeployItem[] getDeploymentBatchItems() throws RemoteException {
		return deploymentBatchItems;
	}

	void setDeploymentBatchItems(
			com.sap.engine.services.dc.api.deploy.DeployItem[] dcDeployItems)
			throws RemoteException {
		this.deploymentBatchItems = buildDeployItems(dcDeployItems);
	}

	void setDeploymentBatchItems(DeployItem[] deployItems)
			throws RemoteException {
		this.deploymentBatchItems = deployItems;
	}

	void setSortedDeploymentBatchItems(
			com.sap.engine.services.dc.api.deploy.DeployItem[] dcDeployItems)
			throws RemoteException {
		this.sortedDeploymentBatchItems = buildDeployItems(dcDeployItems);
	}

	private DeployItem[] buildDeployItems(
			com.sap.engine.services.dc.api.deploy.DeployItem[] dcDeployItems) {
		if (dcDeployItems == null) {
			return null;
		}

		DeployItem[] deployItems = new DeployItem[dcDeployItems.length];
		for (int i = 0; i < dcDeployItems.length; i++) {
			P4DeployItemImpl p4DeployItem = new P4DeployItemImpl(
					dcDeployItems[i]);
			p4DeployItem.setDeployResult(P4ValidateResultMapper
					.map(dcDeployItems[i]));
			deployItems[i] = p4DeployItem;
		}
		return deployItems;
	}

	// private void printItems (
	// com.sap.engine.services.dc.api.deploy.DeployItem[] dcDeployItems) {
	// System.out.println("+++++ Printing....");
	// if ( dcDeployItems == null ) {
	// return;
	// }
	//        
	// for (int i = 0; i < dcDeployItems.length; i++) {
	// System.out.println(dcDeployItems[i]);
	// }
	// }
}
