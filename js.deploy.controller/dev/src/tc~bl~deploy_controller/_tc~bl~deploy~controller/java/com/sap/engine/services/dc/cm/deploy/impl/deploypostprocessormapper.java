package com.sap.engine.services.dc.cm.deploy.impl;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-7
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class DeployPostProcessorMapper {

	private static final DeployPostProcessorMapper INSTANCE = new DeployPostProcessorMapper();

	static DeployPostProcessorMapper getInstance() {
		return INSTANCE;
	}

	private DeployPostProcessorMapper() {
	}

	AbstractDeployPostProcessor map(DeployPhase deployPhase) {
		if (DeployPhase.OFFLINE.equals(deployPhase)) {
			return OfflineDeployPostProcessor.getNewInstance();
		} else {
			return DefaultDeployPostProcessor.getNewInstance();
		}
	}

	AbstractDeployPostProcessor safeMap(DeployPhase deployPhase) {
		if (DeployPhase.OFFLINE.equals(deployPhase)) {
			return OfflineDeployPostProcessor.getNewInstance();
		} else if (DeployPhase.POST_ONLINE.equals(deployPhase)) {

			return PostOnlineDeployPostProcessor.getInstance();

		} else { // assuming online
			return DefaultDeployPostProcessor.getNewInstance();
		}
	}

}
