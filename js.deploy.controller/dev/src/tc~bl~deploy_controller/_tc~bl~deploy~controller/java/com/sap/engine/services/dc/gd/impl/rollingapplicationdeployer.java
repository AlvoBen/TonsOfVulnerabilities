package com.sap.engine.services.dc.gd.impl;

import java.util.Properties;

import javax.naming.NamingException;

import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.gd.DeliveryException;
import com.sap.engine.services.dc.gd.RollingDeliveryException;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.deploy.zdm.DSRollingException;
import com.sap.engine.services.deploy.zdm.DSRollingPatch;
import com.sap.engine.services.deploy.zdm.DSRollingResult;
import com.sap.engine.services.deploy.zdm.utils.ApplicationComponent;

/**
 * @author Rumiana Angelova
 * @version 7.0
 * 
 */
class RollingApplicationDeployer extends InitialApplicationDeployer {

	private static RollingApplicationDeployer INSTANCE;

	private final DSRollingPatch dsRollingPatch;

	static synchronized RollingApplicationDeployer getInstance()
			throws DeliveryException, RollingDeliveryException {
		if (INSTANCE == null) {
			INSTANCE = new RollingApplicationDeployer();
		}

		return INSTANCE;
	}

	private RollingApplicationDeployer() throws DeliveryException,
			RollingDeliveryException {
		try {
			this.dsRollingPatch = ServiceConfigurer.getInstance()
					.getDSRollingPatch();
		} catch (NamingException nex) {
			throw new RollingDeliveryException(DCExceptionConstants.GET_DSRP,
					nex);
		}
	}

	final protected void update(DeploymentItem deploymentItem,
			boolean isStandAlone, String newSdaPath,
			Properties deploymentProperties) throws DeliveryException,
			RollingDeliveryException {
		ApplicationComponent applicationComponent = new ApplicationComponent(
				deploymentItem.getSda().getVendor(), deploymentItem.getSda()
						.getName(), newSdaPath, isStandAlone,
				deploymentProperties);
		try {
			DSRollingResult dsRollingResult = dsRollingPatch
					.updateInstanceAndDB(applicationComponent);
			DSRollingResultAnalyzer dsRollingResultAnalyzer = new DSRollingResultAnalyzer(
					dsRollingResult);
			deploymentItem.accept(dsRollingResultAnalyzer);
			DeliveryException exception = dsRollingResultAnalyzer
					.getException();
			if (exception != null) {
				throw exception;
			}
		} catch (DSRollingException e) {
			throw new RollingDeliveryException(
					DCExceptionConstants.ROLLING_UPDATE_FAILED,
					new String[] { deploymentItem.toString() }, e);
		}
	}

	final protected void deploy(DeploymentItem deploymentItem,
			boolean isStandAlone, String newSdaPath,
			Properties deploymentProperties) throws DeliveryException,
			RollingDeliveryException {
		throw new RollingDeliveryException(
				DCExceptionConstants.ROLLING_DEPLOY_ERROR);
	}

	void performUndeployment(UndeployItem undeployItem)
			throws DeliveryException, RollingDeliveryException {
		throw new RollingDeliveryException(
				DCExceptionConstants.ROLLING_UNEPLOY_ERROR);
	}

} // class EngineJ2EE620OnlineDeployer

