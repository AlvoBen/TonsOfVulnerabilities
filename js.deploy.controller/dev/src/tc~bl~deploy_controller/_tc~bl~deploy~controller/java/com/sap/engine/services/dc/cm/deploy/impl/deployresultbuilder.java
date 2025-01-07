package com.sap.engine.services.dc.cm.deploy.impl;

import com.sap.engine.services.dc.cm.deploy.DeployResult;
import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.cm.utils.measurement.MeasurementUtils;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-8-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class DeployResultBuilder {

	private static final DeployResultBuilder INSTANCE = new DeployResultBuilder();

	private DeployResultBuilder() {
	}

	static DeployResultBuilder getInstance() {
		return INSTANCE;
	}

	DeployResult build(DeploymentData deploymentData) {
		DeploymentBatchAnalyzer.getInstance().analyseDeploymentBatch(
				deploymentData.getDeploymentErrorStrategy(),
				deploymentData.getDeploymentBatch());
		return new DeployResultImpl(deploymentData.getDescription(),
				deploymentData.getSortedDeploymentBatchItem(), deploymentData
						.getDeploymentBatch().getDeploymentBatchItems(),
						MeasurementUtils.build(deploymentData.getMeasurements(), deploymentData.getSessionId() ));
	}
	
}
