package com.sap.engine.services.dc.gd.impl;

import javax.naming.NamingException;

import com.sap.engine.frame.container.deploy.zdm.RollingComponent;
import com.sap.engine.frame.container.deploy.zdm.RollingException;
import com.sap.engine.frame.container.deploy.zdm.RollingName;
import com.sap.engine.frame.container.deploy.zdm.RollingPatch;
import com.sap.engine.frame.container.deploy.zdm.RollingResult;
import com.sap.engine.services.dc.cm.deploy.DeploymentSyncItem;
import com.sap.engine.services.dc.cm.dscr.InstanceDescriptor;
import com.sap.engine.services.dc.cm.dscr.RollingInfo;
import com.sap.engine.services.dc.gd.RollingDeliveryException;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;

class RollingLibrarySyncer implements Syncer {

	private static RollingLibrarySyncer INSTANCE;

	private final RollingPatch rollingPatch;

	static synchronized RollingLibrarySyncer getInstance()
			throws RollingDeliveryException {
		if (INSTANCE == null) {
			INSTANCE = new RollingLibrarySyncer();
		}

		return INSTANCE;
	}

	private RollingLibrarySyncer() throws RollingDeliveryException {
		try {
			this.rollingPatch = ServiceConfigurer.getInstance()
					.getRollingPatch();
		} catch (NamingException nex) {
			throw new RollingDeliveryException(DCExceptionConstants.GET_CORERP,
					nex);
		}
	}

	final public InstanceDescriptor performSync(
			DeploymentSyncItem deploymentSyncItem)
			throws RollingDeliveryException {
		RollingInfo rollingInfo = deploymentSyncItem.getRollingInfo();
		if (rollingInfo == null)
			throw new RollingDeliveryException(
					DCExceptionConstants.ROLLING_MISSED_ROLLING_INFO,
					new String[] { deploymentSyncItem.getBatchItemId()
							.toString() });
		RollingName rollingName = new RollingName(rollingInfo.getItemName(),
				rollingInfo.getItemType());
		try {
			RollingResult rollingResult = rollingPatch
					.syncInstanceWithDB(rollingName);
			com.sap.engine.frame.container.deploy.zdm.InstanceDescriptor instanceDescriptor = rollingResult
					.getInstanceDescriptor();
			SyncRRAnalyzer rollingResultAnalyzer = new SyncRRAnalyzer(
					rollingResult);
			deploymentSyncItem.accept(rollingResultAnalyzer);
			return rollingResultAnalyzer.getInstanceDescriptor();
		} catch (RollingException e) {
			throw new RollingDeliveryException(
					DCExceptionConstants.ROLLING_SYNC_FAILED,
					new String[] { deploymentSyncItem.getBatchItemId()
							.toString() }, e);
		}
	}

}
