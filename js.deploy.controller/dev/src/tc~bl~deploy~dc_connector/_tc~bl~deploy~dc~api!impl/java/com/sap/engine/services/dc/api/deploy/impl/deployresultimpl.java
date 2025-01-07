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
import com.sap.engine.services.dc.api.deploy.DeployResult;
import com.sap.engine.services.dc.api.deploy.DeployResultStatus;
import com.sap.engine.services.dc.api.util.DAConstants;
import com.sap.engine.services.dc.api.util.measurement.DAMeasurement;

/**
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-5
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
final class DeployResultImpl implements DeployResult {
	private final DeployResultStatus deployResultStatus;
	private final DeployItem[] deployItems;
	private final DeployItem[] sortedDeployItems;
	private final String description;
	private String toString = null;
	private final DAMeasurement measurement;

	DeployResultImpl(DeployResultStatus deployResultStatus,
			DeployItem[] deployItems, DeployItem[] sortedDeployItems,
			String description,
			DAMeasurement measurement) {
		this.deployResultStatus = deployResultStatus;
		this.deployItems = deployItems;
		this.sortedDeployItems = sortedDeployItems;
		this.description = description;
		this.measurement = measurement;
	}

	public DeployResultStatus getDeployResultStatus() {
		return this.deployResultStatus;
	}

	public DeployItem[] getDeploymentItems() {
		return this.deployItems;
	}

	public DeployItem[] getSortedDeploymentItems() {
		return this.sortedDeployItems;
	}

	public String getDescription() {
		return this.description;
	}
	
	public DAMeasurement getMeasurement() {
		return this.measurement;
	}

	public String toString() {
		if (this.toString == null) {
			this.toString = "DeployResult[status='" + this.deployResultStatus
					+ "', description=" + DAConstants.EOL_INDENT
					+ this.description +  "', measurement=" + this.measurement + "]";
		}
		return this.toString;
	}

	public int hashCode() {

		return this.deployResultStatus.hashCode() + 7 * this.deployItems.length;
	}

	public boolean equals(Object obj) {

		if (obj == this) {
			return true;
		}

		if (!(obj instanceof DeployResultImpl)) {
			return false;
		}

		DeployResultImpl other = (DeployResultImpl) obj;

		if (!this.deployResultStatus.equals(other.deployResultStatus)) {
			return false;
		}

		if (!Arrays.equals(this.deployItems, other.deployItems)) {
			return false;
		}

		if (!Arrays.equals(this.sortedDeployItems, other.sortedDeployItems)) {
			return false;
		}

		return true;

	}

}