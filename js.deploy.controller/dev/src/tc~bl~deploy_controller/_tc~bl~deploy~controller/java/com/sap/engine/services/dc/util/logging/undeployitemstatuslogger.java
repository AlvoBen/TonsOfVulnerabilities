package com.sap.engine.services.dc.util.logging;

import com.sap.engine.services.dc.util.logging.DCLog;

import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemObserver;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus;
import com.sap.engine.services.dc.util.exception.ExceptionUtils;
import com.sap.tc.logging.Location;

public class UndeployItemStatusLogger implements UndeployItemObserver {

	private Location location = DCLog.getLocation(this.getClass());
	
	private static UndeployItemStatusLogger INSTANCE;

	public static synchronized UndeployItemStatusLogger getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new UndeployItemStatusLogger();
		}

		return INSTANCE;
	}

	private UndeployItemStatusLogger() {
	}

	public void statusChanged(GenericUndeployItem item,
			UndeployItemStatus oldStatus, UndeployItemStatus newStatus) {
		if (newStatus.equals(UndeployItemStatus.SKIPPED)
				|| newStatus.equals(UndeployItemStatus.NOT_DEPLOYED)
				|| newStatus.equals(UndeployItemStatus.ABORTED)
				|| newStatus.equals(UndeployItemStatus.NOT_SUPPORTED)
				|| newStatus.equals(UndeployItemStatus.PREREQUISITE_VIOLATED)) {
			if (location.beDebug()) {
				DCLog.traceDebug(location, ExceptionUtils.getStackTrace(new Exception(
						"Undeployment status of [" + item.getName()
								+ "] was changed from [" + oldStatus + "] to ["
								+ newStatus + "]")));
			}
		} else {
			if (location.beDebug()) {
				DCLog.traceDebug(
						location,
						"Undeployment status of [{0}] was changed from [{1}] to [{2}]",
						new Object[] { item.getName(), oldStatus, newStatus });
			}
		}
	}

}
