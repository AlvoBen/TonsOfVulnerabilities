package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.engine.services.dc.api.deploy.DeployItemStatus;
import com.sap.sdm.api.remote.DeployResult;
import com.sap.sdm.api.remote.deployresults.Aborted;
import com.sap.sdm.api.remote.deployresults.Admitted;
import com.sap.sdm.api.remote.deployresults.AlreadyDeployed;
import com.sap.sdm.api.remote.deployresults.Initial;
import com.sap.sdm.api.remote.deployresults.PreconditionViolated;
import com.sap.sdm.api.remote.deployresults.Skipped;
import com.sap.sdm.api.remote.deployresults.Success;
import com.sap.sdm.api.remote.deployresults.Warning;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-7-29
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class P4DeployResultMapper {

	static DeployResult map(
			com.sap.engine.services.dc.api.deploy.DeployItem dcDeployItem) {
		final DeployItemStatus deployItemStatus = dcDeployItem
				.getDeployItemStatus();
		final String deployItemDescription = dcDeployItem.getDescription();

		if (DeployItemStatus.INITIAL.equals(deployItemStatus)) {
			return new P4DeployResultImpl(new Initial() {
			}, deployItemDescription);
		} else if (DeployItemStatus.SKIPPED.equals(deployItemStatus)) {
			return new P4DeployResultImpl(new Skipped() {
			}, deployItemDescription);
		} else if (DeployItemStatus.ABORTED.equals(deployItemStatus)) {
			return new P4DeployResultImpl(new Aborted() {
			}, deployItemDescription);
		} else if (DeployItemStatus.ADMITTED.equals(deployItemStatus)) {
			return new P4DeployResultImpl(new Admitted() {
			}, deployItemDescription);
		} else if (DeployItemStatus.PREREQUISITE_VIOLATED
				.equals(deployItemStatus)) {
			return new P4DeployResultImpl(new PreconditionViolated() {
			}, deployItemDescription);
		} else if (DeployItemStatus.DELIVERED.equals(deployItemStatus)) {
			return new P4DeployResultImpl(new Warning() {
			}, deployItemDescription);
		} else if (DeployItemStatus.WARNING.equals(deployItemStatus)) {
			return new P4DeployResultImpl(new Warning() {
			}, deployItemDescription);
		} else if (DeployItemStatus.SUCCESS.equals(deployItemStatus)) {
			return new P4DeployResultImpl(new Success() {
			}, deployItemDescription);
		} else if (DeployItemStatus.ALREADY_DEPLOYED.equals(deployItemStatus)) {
			return new P4DeployResultImpl(new AlreadyDeployed() {
			}, deployItemDescription);
		}
		// else if ( DeployItemStatus.OFFLINE_ADMITTED.equals(deployItemStatus)
		// ) {
		// return new P4DeployResultImpl(new Admitted() {},
		// deployItemDescription);
		// }
		// else if ( DeployItemStatus.OFFLINE_SUCCESS.equals(deployItemStatus) )
		// {
		// return new P4DeployResultImpl(new Success() {},
		// deployItemDescription);
		// }
		// else if ( DeployItemStatus.OFFLINE_ABORTED.equals(deployItemStatus) )
		// {
		// return new P4DeployResultImpl(new Aborted() {},
		// deployItemDescription);
		// }
		else if (DeployItemStatus.FILTERED.equals(deployItemStatus)) {
			return new P4DeployResultImpl(new Skipped() {
			}, deployItemDescription);
		} else {
			final String fatalErrMsg = "Unknown deployment item status '"
					+ deployItemStatus + "' detected!";
			throw new IllegalStateException(fatalErrMsg);
		}

	}

	private P4DeployResultMapper() {
	}

}
