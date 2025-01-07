package com.sap.engine.services.dc.gd.impl;

import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.gd.DeliveryException;
import com.sap.engine.services.dc.gd.RollingDeliveryException;

/**
 * @author Rumiana Angelova
 * @version 7.0
 */
abstract class Deployer {

	protected String concatStrings(String[] arr) {
		final StringBuffer result = new StringBuffer("");
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				result.append(arr[i]).append("\n");
			}
		}

		return result.toString();
	}

	abstract void performUndeployment(UndeployItem undeployItem)
			throws DeliveryException, RollingDeliveryException;

	abstract void performDeployment(DeploymentItem deploymentItem)
			throws DeliveryException, RollingDeliveryException;

}
