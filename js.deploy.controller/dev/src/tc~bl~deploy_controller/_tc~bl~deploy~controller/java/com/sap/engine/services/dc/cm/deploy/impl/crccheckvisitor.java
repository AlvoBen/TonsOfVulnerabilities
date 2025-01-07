/*
 * Created on 2005-3-21 by radoslav-i
 */
package com.sap.engine.services.dc.cm.deploy.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.dc.cm.CM;
import com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsEntry;
import com.sap.engine.services.dc.frame.DeployControllerFrame;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.repo.RepositoryContainer;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * @author radoslav-i
 */
final class CrcCheckVisitor implements DeploymentBatchItemVisitor {
	private final boolean isCrcCheckSwitchedOn;
	private final ComponentVersionHandlingRule versionRule;
	private  final Location location = DCLog.getLocation(this.getClass());
	
	CrcCheckVisitor(ComponentVersionHandlingRule versionRule) {
		this.isCrcCheckSwitchedOn = ServiceConfigurer.getInstance()
				.isCrcCheckSwitchedOn();
		this.versionRule = versionRule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
	 * (com.sap.engine.services.dc.cm.deploy.DeploymentItem)
	 */
	public void visit(DeploymentItem aDeploymentItem) {
		if (!this.isCrcCheckSwitchedOn
				|| ComponentVersionHandlingRule.UPDATE_ALL_VERSIONS
						.equals(this.versionRule)) {
			return;
		}

		if (!isCrcCheckPresented(aDeploymentItem)) {
			if (location.beDebug()) {
				traceDebug(location, 
						"Deploy controller will not perform CRC check for [{0}].",
						new Object[] { aDeploymentItem.getBatchItemId() });
			}
			return;
		}

		aDeploymentItem.startTimeStatEntry("CRC Check",
				TimeStatisticsEntry.ENTRY_TYPE_OTHER);
		final String tagName = "CRC check:" + aDeploymentItem.getSdu().getId();
		Accounting.beginMeasure(tagName, CrcCheckVisitor.class);
		try {
			isPerformedCrcCheck(aDeploymentItem);
		} finally {
			Accounting.endMeasure(tagName);
			aDeploymentItem.finishTimeStatEntry();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
	 * (com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem)
	 */
	public void visit(CompositeDeploymentItem compositeDeploymentItem) {
		// do nothing
	}

	private boolean isPerformedCrcCheck(DeploymentItem deploymentItem) {

		if (location.beDebug()) {
			traceDebug(location, "Performing CRC check for [{0}].",
					new Object[] { deploymentItem.getBatchItemId() });
		}

		Sdu sdu = deploymentItem.getSdu();
		Sdu oldSdu = deploymentItem.getOldSdu();

		if (oldSdu == null) {
			oldSdu = getOldSdu(deploymentItem);
		}

		if (oldSdu != null) {
			if (sdu.getCrc().equals(oldSdu.getCrc())) {
				// mark as already deployed
				deploymentItem
						.setDeploymentStatus(DeploymentStatus.ALREADY_DEPLOYED);
				deploymentItem
						.addDescription("Due to CRC check deployment status is set to '"
								+ DeploymentStatus.ALREADY_DEPLOYED
								+ "' to reduce downtime.\r\nHint:"
								+ " 1). Two components with equal checksum are reported as Already Deployed despite their key counters are different."
								+ " 2). To disable it change property [" + DeployControllerFrame.CRC_CHECK_MODE_KEY + "] of service [" + CM.SERVICE_NAME + "].");
				// log
				if (location.bePath()) {
					tracePath(location, 
							"Due to CRC check deployment status of deployment item [{0}] is set to [{1}]",
							new Object[] { deploymentItem.getBatchItemId(),
									DeploymentStatus.ALREADY_DEPLOYED });
				}
				return true;
			}

			if (location.beDebug()) {
				traceDebug(location, 
						"CRC-s are different. The old CRC is [{0}] and the current is [{1}] for deployment item: [{2}]",
						new Object[] { oldSdu.getCrc(), sdu.getCrc(),
								deploymentItem.getBatchItemId() });
			}

		} else {
			if (location.beDebug()) {
				traceDebug(location, 
						"No CRC check will be performed because there was no old SDU component found.");
			}
		}

		return false;
	}

	private boolean isCrcCheckPresented(DeploymentBatchItem aDeploymentBatchItem) {

		if (aDeploymentBatchItem.getSdu().getCrc() == null
				|| aDeploymentBatchItem.getSdu().getCrc().trim().equals("")) {
			return false;
		}

		return true;
	}

	private Sdu getOldSdu(DeploymentBatchItem deploymentBatchItem) {
		if (deploymentBatchItem.getOldSdu() != null) {
			return deploymentBatchItem.getOldSdu();
		}

		final Sdu batchSdu = deploymentBatchItem.getSdu();

		return RepositoryContainer.getDeploymentsContainer().getDeployment(
				batchSdu.getId());
	}
}
