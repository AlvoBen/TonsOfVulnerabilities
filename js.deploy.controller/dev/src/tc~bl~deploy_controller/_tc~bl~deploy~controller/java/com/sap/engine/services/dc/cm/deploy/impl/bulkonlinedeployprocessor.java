package com.sap.engine.services.dc.cm.deploy.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.deploy.VersionStatus;
import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsEntry;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-3-28
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
final class BulkOnlineDeployProcessor extends OnlineDeployProcessor {

	
	private  final Location location = DCLog.getLocation(this.getClass());
	
	private static BulkOnlineDeployProcessor INSTANCE;
	
	static synchronized BulkOnlineDeployProcessor getInstance() {

		if (INSTANCE == null) {
			INSTANCE = new BulkOnlineDeployProcessor();
		}
		return INSTANCE;
	}

	private BulkOnlineDeployProcessor() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.impl.AbstractDeployProcessor#deploy
	 * (com.sap.engine.services.dc.cm.deploy.DeploymentItem,
	 * com.sap.engine.services.dc.cm.deploy.DeploymentData)
	 */
	protected void deploy(DeploymentItem deplItem, DeploymentData deploymentData)
			throws DeploymentException {
		deplItem.startTimeStatEntry("Bulk deployment",
				TimeStatisticsEntry.ENTRY_TYPE_OTHER);
		final String tagName = "Bulk deployment:" + deplItem.getSdu().getId();
		Accounting.beginMeasure(tagName, BulkOnlineDeployProcessor.class);
		try {
			if (!VersionStatus.NEW.equals(deplItem.getVersionStatus())) {
				this.performStop(deplItem, deploymentData);
			}

			if (location.bePath()) {
				tracePath(location, "Starting bulk delivery ...");
			}

			this.performDelivery(deplItem, deploymentData);
			if (location.bePath()) {
				tracePath(location, "Delivery finished");
			}

			if (!DeploymentStatus.DELIVERED.equals(deplItem
					.getDeploymentStatus())
					&& !DeploymentStatus.WARNING.equals(deplItem
							.getDeploymentStatus())) {
				return;
			}

			if (location.bePath()) {
				tracePath(location, "Starting to notify deploy observers");
			}
			// notify observers- time statistics inside
			this.notifyObservers(deplItem, deploymentData);
			if (location.bePath()) {
				tracePath(location, "Observers have been notified. Component:[{0}].",
						new Object[] { deplItem.getSdu().getId() });
			}
		} finally {
			Accounting.endMeasure(tagName);
			deplItem.finishTimeStatEntry();
		}
	}

}
