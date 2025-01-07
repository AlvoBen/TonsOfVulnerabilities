/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.deploy.impl;

import java.util.Arrays;

import com.sap.engine.services.dc.api.deploy.DeployItem;
import com.sap.engine.services.dc.api.deploy.ValidationResult;
import com.sap.engine.services.dc.api.deploy.ValidationStatus;
import com.sap.engine.services.dc.api.util.DAConstants;

/**
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-16
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
final class ValidationResultImpl implements ValidationResult {

	private final boolean isOfflinePhaseScheduled;
	private final ValidationStatus validationStatus;
	private final DeployItem[] deploymentBatchItems;
	private final DeployItem[] sortedDeploymentBatchItems;
	private String toString = null;

	ValidationResultImpl(boolean isOfflinePhaseScheduled,
			ValidationStatus validationStatus,
			DeployItem[] deploymentBatchItems,
			DeployItem[] sortedDeploymentBatchItems) {
		this.isOfflinePhaseScheduled = isOfflinePhaseScheduled;
		this.validationStatus = validationStatus;
		this.deploymentBatchItems = deploymentBatchItems;
		this.sortedDeploymentBatchItems = sortedDeploymentBatchItems;
	}

	public ValidationStatus getValidationStatus() {
		return this.validationStatus;
	}

	public boolean isOfflinePhaseScheduled() {
		return this.isOfflinePhaseScheduled;
	}

	public DeployItem[] getSortedDeploymentBatchItems() {
		return this.sortedDeploymentBatchItems;
	}

	public DeployItem[] getDeploymentBatchItems() {
		return this.deploymentBatchItems;
	}

	public String toString() {
		if (this.toString == null) {
			this.toString = "ValidationResult[isOfflinePhaseScheduled="
					+ this.isOfflinePhaseScheduled
					+ DAConstants.EOL_INDENT
					+ "validationStatus="
					+ this.validationStatus
					+ DAConstants.EOL_INDENT
					+ "deploymentBatchItems="
					+ ((this.deploymentBatchItems != null) ? Arrays.asList(
							this.deploymentBatchItems).toString() : "null")
					+ DAConstants.EOL_INDENT
					+ "sortedDeploymentBatchItems="
					+ ((this.sortedDeploymentBatchItems != null) ? Arrays
							.asList(this.sortedDeploymentBatchItems).toString()
							: "null") + DAConstants.EOL + "]";
		}
		return this.toString;
	}

}