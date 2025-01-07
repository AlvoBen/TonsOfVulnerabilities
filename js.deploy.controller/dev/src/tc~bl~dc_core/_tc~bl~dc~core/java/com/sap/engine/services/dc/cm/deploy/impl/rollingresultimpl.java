package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.Collection;

import com.sap.engine.services.dc.cm.deploy.DeployResult;
import com.sap.engine.services.dc.cm.deploy.DeployResultStatus;
import com.sap.engine.services.dc.cm.utils.measurement.DMeasurement;

class RollingResultImpl implements DeployResult {

	private static final long serialVersionUID = -4837284531923827042L;
	private static final String EOL = System.getProperty("line.separator");

	private final Collection deploymentBatchItems;// $JL-SER$
	private final Collection sortedDeploymentBatchItems;// $JL-SER$
	private final DeployResultStatus deployResultStatus;
	private final String description;
	private final String toString;
	private final DMeasurement measurement;
	
	RollingResultImpl(final Collection deploymentBatchItems,
			final Collection sortedDeploymentBatchItems,
			final DeployResultStatus deployResultStatus,
			final String description,
			final DMeasurement measurement) {

		this.deploymentBatchItems = deploymentBatchItems;
		this.sortedDeploymentBatchItems = sortedDeploymentBatchItems;
		this.deployResultStatus = deployResultStatus;
		this.description = description;
		this.toString = null;
		this.measurement = measurement;
	}

	public DeployResultStatus getDeployStatus() {
		return deployResultStatus;
	}

	public Collection getDeploymentItems() {
		return deploymentBatchItems;
	}

	public String getDescription() {
		return description;
	}
	
	public DMeasurement getMeasurement() {
		return measurement;
	}

	public Collection getSortedDeploymentBatchItems() {
		return sortedDeploymentBatchItems;
	}

	public String toString() {
		return toString;
	}

	private String buildToString() {
		final DeployResultStatus deplStatus = getDeployStatus();
		final String descr = getDescription();

		return "The status of the deployed SDUs is " + deplStatus + ". " + EOL
				+ "Additional information: " + EOL + descr;
	}

}
