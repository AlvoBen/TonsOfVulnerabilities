package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.Collection;
import java.util.Iterator;

import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;

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
final class DeploymentBatchItemStatusGetter implements
		DeploymentBatchItemVisitor {

	private DeploymentStatus deploymentStatus;

	DeploymentBatchItemStatusGetter() {
	}

	DeploymentStatus getDeploymentStatus() {
		return deploymentStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
	 * (com.sap.engine.services.dc.cm.deploy.DeploymentItem)
	 */
	public void visit(DeploymentItem deploymentItem) {
		deploymentStatus = deploymentItem.getDeploymentStatus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
	 * (com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem)
	 */
	public void visit(CompositeDeploymentItem compDeploymentItem) {
		setCompositeDeplItemDeployResult(compDeploymentItem);
		deploymentStatus = compDeploymentItem.getDeploymentStatus();
	}

	private void setCompositeDeplItemDeployResult(
			CompositeDeploymentItem compositeDeplItem) {

		final Collection deploymentItems = compositeDeplItem
				.getDeploymentItems();

		boolean oneFinishedInital = false;
		boolean oneFinishedSkipped = false;
		boolean oneFinishedAborted = false;
		boolean oneFinishedAdmitted = false;
		boolean oneFinishedPrerequisiteViolated = false;
		boolean oneFinishedDelivered = false;
		boolean oneFinishedWarning = false;
		boolean oneFinishedSuccess = false;
		boolean oneFinishedAlreadyDeployed = false;
		boolean oneFinishedFiltered = false;
		boolean oneFinishedRepeated = false;
		boolean oneFinishedOfflineAdmitted = false;
		boolean oneFinishedOfflineAborted = false;
		boolean oneFinishedOfflineSuccess = false;
		boolean oneFinishedOfflineWarning = false;

		for (Iterator iter = deploymentItems.iterator(); iter.hasNext();) {
			final DeploymentItem deploymentItem = (DeploymentItem) iter.next();
			final DeploymentStatus deplItemStatus = deploymentItem
					.getDeploymentStatus();
			if (deplItemStatus.equals(DeploymentStatus.INITIAL)) {
				if (!oneFinishedInital) {
					oneFinishedInital = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.SKIPPED)) {
				if (!oneFinishedSkipped) {
					oneFinishedSkipped = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.ABORTED)) {
				if (!oneFinishedAborted) {
					oneFinishedAborted = true;
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
			} else if (deplItemStatus.equals(DeploymentStatus.DELIVERED)) {
				if (!oneFinishedDelivered) {
					oneFinishedDelivered = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.WARNING)) {
				if (!oneFinishedWarning) {
					oneFinishedWarning = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.SUCCESS)) {
				if (!oneFinishedSuccess) {
					oneFinishedSuccess = true;
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
			} else if (deplItemStatus.equals(DeploymentStatus.OFFLINE_ABORTED)) {
				if (!oneFinishedOfflineAborted) {
					oneFinishedOfflineAborted = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.OFFLINE_SUCCESS)) {
				if (!oneFinishedOfflineSuccess) {
					oneFinishedOfflineSuccess = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.OFFLINE_WARNING)) {
				if (!oneFinishedOfflineWarning) {
					oneFinishedOfflineWarning = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.REPEATED)) {
				if (!oneFinishedRepeated) {
					oneFinishedRepeated = true;
				}
			}
			// else {
			// DCLog.logError(
			// DCLogConstants.DEPLOY_UKNOWN_DEPLOYMENT_ITEM_STATUS,
			// new Object[] {deplItemStatus, deploymentItem}
			// );
			// }
		}

		if (oneFinishedInital || oneFinishedAborted || oneFinishedAdmitted
				|| oneFinishedPrerequisiteViolated
				|| oneFinishedOfflineAdmitted || oneFinishedOfflineAborted) {
			compositeDeplItem.setDeploymentStatus(DeploymentStatus.ABORTED);
		} else if (oneFinishedSkipped) {
			compositeDeplItem.setDeploymentStatus(DeploymentStatus.SKIPPED);
		} else if (oneFinishedDelivered || oneFinishedWarning
				|| oneFinishedOfflineWarning) {
			compositeDeplItem.setDeploymentStatus(DeploymentStatus.WARNING);
		} else if (oneFinishedSuccess
				|| oneFinishedOfflineSuccess
				|| DeploymentStatus.SUCCESS.equals(compositeDeplItem
						.getDeploymentStatus())) {
			compositeDeplItem.setDeploymentStatus(DeploymentStatus.SUCCESS);
		} else if (oneFinishedAlreadyDeployed) {
			// compositeDeplItem.setDeploymentStatus(DeploymentStatus.
			// ALREADY_DEPLOYED);
		} else if (oneFinishedFiltered) {
			compositeDeplItem.setDeploymentStatus(DeploymentStatus.FILTERED);
		} else if (oneFinishedRepeated) {
			compositeDeplItem.setDeploymentStatus(DeploymentStatus.REPEATED);
		}
		// else {
		// DCLog.logError(
		// DCLogConstants.
		// DEPLOY_ILLEGAL_STATE_WHILE_SETTING_STATUS_FOR_COMPOSITE_ITEM,
		// new Object[] {compositeDeplItem });
		// }
	}

}
