package com.sap.engine.services.dc.gd.impl;

import javax.naming.NamingException;

import com.sap.engine.frame.container.deploy.zdm.RollingComponent;
import com.sap.engine.frame.container.deploy.zdm.RollingException;
import com.sap.engine.frame.container.deploy.zdm.RollingPatch;
import com.sap.engine.frame.container.deploy.zdm.RollingResult;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.VersionStatus;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.gd.DeliveryException;
import com.sap.engine.services.dc.gd.RollingDeliveryException;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;

public class RollingLibraryDeployer extends Deployer {

	private static RollingLibraryDeployer INSTANCE;

	private final RollingPatch rollingPatch;

	static synchronized RollingLibraryDeployer getInstance()
			throws DeliveryException, RollingDeliveryException {
		if (INSTANCE == null) {
			INSTANCE = new RollingLibraryDeployer();
		}

		return INSTANCE;
	}

	private RollingLibraryDeployer() throws DeliveryException,
			RollingDeliveryException {
		try {
			this.rollingPatch = ServiceConfigurer.getInstance()
					.getRollingPatch();
		} catch (NamingException nex) {
			throw new RollingDeliveryException(DCExceptionConstants.GET_CORERP,
					nex);
		}
	}

	void performDeployment(DeploymentItem deploymentItem)
			throws DeliveryException, RollingDeliveryException {
		if (deploymentItem.getVersionStatus().equals(VersionStatus.NEW)) {
			throw new RollingDeliveryException(
					DCExceptionConstants.ROLLING_DEPLOY_ERROR);
		} else {
			RollingComponent rollingComponent = new RollingComponent(
					deploymentItem.getSduFilePath(),
					RollingComponent.LIBRARY_TYPE);
			try {
				RollingResult rollingResult = rollingPatch
						.updateInstanceAndDB(rollingComponent);
				RollingResultAnalyzer rollingResultAnalyzer = new RollingResultAnalyzer(
						rollingResult);
				deploymentItem.accept(rollingResultAnalyzer);
				DeliveryException exception = rollingResultAnalyzer
						.getException();
				if (exception != null) {
					throw exception;
				}
			} catch (RollingException e) {
				throw new RollingDeliveryException(
						DCExceptionConstants.ROLLING_UPDATE_FAILED,
						new String[] { deploymentItem.toString() }, e);
			}
		}

	}

	void performUndeployment(UndeployItem undeployItem)
			throws DeliveryException, RollingDeliveryException {
		throw new RollingDeliveryException(
				DCExceptionConstants.ROLLING_UNEPLOY_ERROR);
	}

}
