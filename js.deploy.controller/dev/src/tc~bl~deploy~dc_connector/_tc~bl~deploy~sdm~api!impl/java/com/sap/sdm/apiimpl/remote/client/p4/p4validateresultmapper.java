/*
 * Created on 2005-7-4 by radoslav-i
 */
package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.engine.services.dc.api.deploy.DeployItemStatus;
import com.sap.sdm.api.remote.DeployResult;
import com.sap.sdm.api.remote.deployresults.Admitted;
import com.sap.sdm.api.remote.deployresults.AlreadyDeployed;
import com.sap.sdm.api.remote.deployresults.PreconditionViolated;

/**
 * @author radoslav-i
 */
final class P4ValidateResultMapper {

	static DeployResult map(
			com.sap.engine.services.dc.api.deploy.DeployItem dcDeployItem) {
		final DeployItemStatus dcDeployItemStatus = dcDeployItem
				.getDeployItemStatus();
		final String deployItemDescription = dcDeployItem.getDescription();

		if (DeployItemStatus.INITIAL.equals(dcDeployItemStatus)) {
			return new P4DeployResultImpl(new PreconditionViolated() {
			}, deployItemDescription);
		} else if (DeployItemStatus.PREREQUISITE_VIOLATED
				.equals(dcDeployItemStatus)) {
			return new P4DeployResultImpl(new PreconditionViolated() {
			}, deployItemDescription);
		} else if (DeployItemStatus.ADMITTED.equals(dcDeployItemStatus)) {
			return new P4DeployResultImpl(new Admitted() {
			}, deployItemDescription);
		} else if (DeployItemStatus.ALREADY_DEPLOYED.equals(dcDeployItemStatus)) {
			return new P4DeployResultImpl(new AlreadyDeployed() {
			}, deployItemDescription);
		}

		else {
			final String fatalErrMsg = "Unknown validate status '"
					+ dcDeployItemStatus + "' detected!";
			throw new IllegalStateException(fatalErrMsg);
		}
	}

	private P4ValidateResultMapper() {
	}
}
