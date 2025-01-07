package com.sap.engine.services.dc.gd.impl;

import javax.naming.NamingException;

import com.sap.engine.services.dc.cm.deploy.DeploymentSyncItem;
import com.sap.engine.services.dc.cm.dscr.InstanceDescriptor;
import com.sap.engine.services.dc.cm.dscr.RollingInfo;
import com.sap.engine.services.dc.gd.RollingDeliveryException;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.deploy.server.dpl_info.module.ApplicationName;
import com.sap.engine.services.deploy.zdm.DSRollingException;
import com.sap.engine.services.deploy.zdm.DSRollingPatch;
import com.sap.engine.services.deploy.zdm.DSRollingResult;

class RollingApplicationSyncer implements Syncer {

	private static RollingApplicationSyncer INSTANCE;

	private final DSRollingPatch dsRollingPatch;

	static synchronized RollingApplicationSyncer getInstance()
			throws RollingDeliveryException {
		if (INSTANCE == null) {
			INSTANCE = new RollingApplicationSyncer();
		}

		return INSTANCE;
	}

	private RollingApplicationSyncer() throws RollingDeliveryException {
		try {
			this.dsRollingPatch = ServiceConfigurer.getInstance()
					.getDSRollingPatch();
		} catch (NamingException nex) {
			throw new RollingDeliveryException(DCExceptionConstants.GET_DSRP,
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
		ApplicationName applicationName = new ApplicationName(rollingInfo
				.getItemName());
		try {
			DSRollingResult dsRollingResult = dsRollingPatch
					.syncInstanceWithDB(applicationName);
			SyncDSRRAnalyzer syncDSRRAnalyzer = new SyncDSRRAnalyzer(
					dsRollingResult);
			deploymentSyncItem.accept(syncDSRRAnalyzer);
			return syncDSRRAnalyzer.getInstanceDescriptor();
		} catch (DSRollingException e) {
			throw new RollingDeliveryException(
					DCExceptionConstants.ROLLING_SYNC_FAILED,
					new String[] { deploymentSyncItem.getBatchItemId()
							.toString() }, e);
		}
	}

}
