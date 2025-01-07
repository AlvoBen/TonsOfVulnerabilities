package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.Collection;
import java.util.Iterator;

import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;

public abstract class ValidationVisitor implements DeploymentBatchItemVisitor {
	protected boolean isThereAvailabaleItems(
			CompositeDeploymentItem compositeDeploymentItem) {
		final Collection deploymentItems = compositeDeploymentItem
				.getDeploymentItems();

		if (deploymentItems.isEmpty())
			return true;

		for (Iterator dplItemsItr = deploymentItems.iterator(); dplItemsItr
				.hasNext();) {
			DeploymentItem dplItem = (DeploymentItem) dplItemsItr.next();
			if (DeploymentStatus.ALREADY_DEPLOYED.equals(dplItem
					.getDeploymentStatus())
					|| DeploymentStatus.ADMITTED.equals(dplItem
							.getDeploymentStatus())) {
				return true;
			}
		}

		return false;
	}
}
