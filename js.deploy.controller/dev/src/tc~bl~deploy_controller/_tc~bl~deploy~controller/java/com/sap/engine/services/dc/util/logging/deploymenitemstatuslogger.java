package com.sap.engine.services.dc.util.logging;


import com.sap.engine.services.dc.util.logging.DCLog;

import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemObserver;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.util.exception.ExceptionUtils;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2007 Company: SAP AG Date: 2007-01-04
 * 
 * @author Boyko Popov
 * 
 */

public class DeploymenItemStatusLogger implements DeploymentBatchItemObserver {

	private static DeploymenItemStatusLogger INSTANCE;
	private Location location = DCLog.getLocation(this.getClass());

	public static synchronized DeploymenItemStatusLogger getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DeploymenItemStatusLogger();
		}

		return INSTANCE;
	}

	private DeploymenItemStatusLogger() {
	}

	public void statusChanged(DeploymentBatchItem item,
			DeploymentStatus oldStatus, DeploymentStatus newStatus) {

		if (newStatus.equals(DeploymentStatus.SKIPPED)
				|| newStatus.equals(DeploymentStatus.FILTERED)
				|| newStatus.equals(DeploymentStatus.ABORTED)
				|| newStatus.equals(DeploymentStatus.PREREQUISITE_VIOLATED)) {
			if (location.beDebug()) {
				DCLog.traceDebug(location, ExceptionUtils.getStackTrace(new Exception(
						"Deployment status of [" + item.getBatchItemId()
								+ "] was changed from [" + oldStatus + "] to ["
								+ newStatus + "]")));
			}
		} else {
			if (location.beDebug()) {
				DCLog.traceDebug(
						location,
						"Deployment status of [{0}] was changed from [{1}] to [{2}]",
						new Object[] { item.getBatchItemId(), oldStatus,
								newStatus });
			}
		}

	}

}
