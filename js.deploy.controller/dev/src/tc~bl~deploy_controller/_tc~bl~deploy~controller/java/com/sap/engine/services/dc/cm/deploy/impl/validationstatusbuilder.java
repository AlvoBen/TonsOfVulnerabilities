package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.Collection;
import java.util.Iterator;

import com.sap.engine.services.dc.cm.deploy.DeploymentBatch;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.deploy.ValidationStatus;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.DCLogConstants;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-25
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class ValidationStatusBuilder {

	private  final Location location = DCLog.getLocation(this.getClass());
	
	private static final ValidationStatusBuilder INSTANCE = new ValidationStatusBuilder();

	static ValidationStatusBuilder getInstance() {
		return INSTANCE;
	}

	private ValidationStatusBuilder() {
	}

	ValidationStatus buildValidationStatus(DeploymentBatch deploymentBatch) {
		final Collection deploymentBatchItems = deploymentBatch
				.getDeploymentBatchItems();

		boolean oneFinishedInital = false;
		boolean oneFinishedSkipped = false;
		boolean oneFinishedAdmitted = false;
		boolean oneFinishedPrerequisiteViolated = false;
		boolean oneFinishedAlreadyDeployed = false;
		boolean oneFinishedFiltered = false;
		boolean oneFinishedOfflineAdmitted = false;

		final DeplBatchItemValidationStatusGetter statusGetter = new DeplBatchItemValidationStatusGetter();

		for (Iterator iter = deploymentBatchItems.iterator(); iter.hasNext();) {
			final DeploymentBatchItem deplBatchItem = (DeploymentBatchItem) iter
					.next();
			deplBatchItem.accept(statusGetter);
			final DeploymentStatus deplItemStatus = statusGetter
					.getDeploymentStatus();

			if (deplItemStatus.equals(DeploymentStatus.INITIAL)) {
				if (!oneFinishedInital) {
					oneFinishedInital = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.SKIPPED)) {
				if (!oneFinishedSkipped) {
					oneFinishedSkipped = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.ADMITTED)) {
				if (!oneFinishedAdmitted) {
					oneFinishedAdmitted = true;
				}
			} else if (deplItemStatus
					.equals(DeploymentStatus.PREREQUISITE_VIOLATED)) {
				if (!oneFinishedPrerequisiteViolated) {
					oneFinishedPrerequisiteViolated = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.ALREADY_DEPLOYED)) {
				if (!oneFinishedAlreadyDeployed) {
					oneFinishedAlreadyDeployed = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.FILTERED)) {
				if (!oneFinishedFiltered) {
					oneFinishedFiltered = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.OFFLINE_ADMITTED)) {
				if (!oneFinishedOfflineAdmitted) {
					oneFinishedOfflineAdmitted = true;
				}
			} else {
				DCLog
						.logError(location, 
								"ASJ.dpl_dc.001118",
								"Unknown deployment batch item status [{0}] detected for item [{1}]",
								new Object[] { deplItemStatus, deplBatchItem });
			}
		}

		if (oneFinishedInital || oneFinishedSkipped
				|| oneFinishedPrerequisiteViolated
				|| oneFinishedOfflineAdmitted) {
			return ValidationStatus.ERROR;
		} else if (oneFinishedAdmitted || oneFinishedOfflineAdmitted) {
			return ValidationStatus.SUCCESS;
		} else if (oneFinishedAlreadyDeployed || oneFinishedFiltered) {
			return ValidationStatus.ERROR;
		} else {
			DCLog
					.logError(location, "ASJ.dpl_dc.001119",
							"Entered illegal state while checking deploy status of deployment batch item");

			return ValidationStatus.ERROR;
		}
	}

}
