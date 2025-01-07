package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.engine.services.dc.api.undeploy.UndeployItemStatus;
import com.sap.sdm.api.remote.UnDeployResult;
import com.sap.sdm.api.remote.undeployresults.Error;
import com.sap.sdm.api.remote.undeployresults.NotExecuted;
import com.sap.sdm.api.remote.undeployresults.Success;
import com.sap.sdm.api.remote.undeployresults.Warning;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-27
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class P4UnDeployResultMapper {

	static UnDeployResult map(
			com.sap.engine.services.dc.api.undeploy.UndeployItem undeployItem) {
		final UndeployItemStatus undeployItemStatus = undeployItem
				.getUndeployItemStatus();
		final String undeployItemDescription = undeployItem.getDescription();

		if (UndeployItemStatus.INITIAL.equals(undeployItemStatus)) {
			return new P4UnDeployResultImpl(new NotExecuted() {
			}, undeployItemDescription);
		} else if (UndeployItemStatus.SKIPPED.equals(undeployItemStatus)) {
			return new P4UnDeployResultImpl(new NotExecuted() {
			}, undeployItemDescription);
		} else if (UndeployItemStatus.SUCCESS.equals(undeployItemStatus)) {
			return new P4UnDeployResultImpl(new Success() {
			}, undeployItemDescription);
		} else if (UndeployItemStatus.WARNING.equals(undeployItemStatus)) {
			return new P4UnDeployResultImpl(new Warning() {
			}, undeployItemDescription);
		} else if (UndeployItemStatus.ABORTED.equals(undeployItemStatus)) {
			return new P4UnDeployResultImpl(new Error() {
			}, undeployItemDescription);
		} else if (UndeployItemStatus.NOT_DEPLOYED.equals(undeployItemStatus)) {
			return new P4UnDeployResultImpl(new NotExecuted() {
			}, undeployItemDescription);
		} else if (UndeployItemStatus.ADMITTED.equals(undeployItemStatus)) {
			return new P4UnDeployResultImpl(new NotExecuted() {
			}, undeployItemDescription);
		} else if (UndeployItemStatus.NOT_SUPPORTED.equals(undeployItemStatus)) {
			return new P4UnDeployResultImpl(new NotExecuted() {
			}, undeployItemDescription);
		}
		// else if (
		// UndeployItemResult.OFFLINE_ADMITTED.equals(undeployItemStatus) ) {
		// return new P4UnDeployResultImpl(new NotExecuted() {},
		// undeployItemDescription);
		// }
		// else if (
		// UndeployItemResult.OFFLINE_SUCCESS.equals(undeployItemStatus) ) {
		// return new P4UnDeployResultImpl(new NotExecuted() {},
		// undeployItemDescription);
		// }
		// else if (
		// UndeployItemResult.OFFLINE_ABORTED.equals(undeployItemStatus) ) {
		// return new P4UnDeployResultImpl(new Error() {},
		// undeployItemDescription);
		// }
		else if (UndeployItemStatus.PREREQUISITE_VIOLATED
				.equals(undeployItemStatus)) {
			return new P4UnDeployResultImpl(new NotExecuted() {
			}, undeployItemDescription);
		} else {
			final String fatalErrMsg = "Unknown undeployment item status '"
					+ undeployItemStatus + "' detected!";
			throw new IllegalStateException(fatalErrMsg);
		}
	}

	private P4UnDeployResultMapper() {
	}

}
