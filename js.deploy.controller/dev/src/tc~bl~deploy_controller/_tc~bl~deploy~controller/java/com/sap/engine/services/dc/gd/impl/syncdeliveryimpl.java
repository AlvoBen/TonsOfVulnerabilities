package com.sap.engine.services.dc.gd.impl;

import com.sap.engine.services.dc.cm.deploy.CompositeSyncItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentSyncItem;
import com.sap.engine.services.dc.cm.dscr.InstanceDescriptor;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;
import com.sap.engine.services.dc.gd.RollingDeliveryException;
import com.sap.engine.services.dc.gd.SyncDelivery;
import com.sap.engine.services.dc.repo.SoftwareType;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;

class SyncDeliveryImpl implements SyncDelivery {

	public InstanceDescriptor sync(DeploymentSyncItem deploymentSyncItem)
			throws RollingDeliveryException {
		if (deploymentSyncItem == null) {
			throw new NullPointerException(
					"The specified deployment item could not be null.");
		}

		SoftwareType swtType = deploymentSyncItem.getSoftwareType();
		final SoftwareTypeService softwareTypeService = (SoftwareTypeService) ServerFactory
				.getInstance()
				.createServer()
				.getServerService(
						ServerFactory.getInstance().createSoftwareTypeRequest());

		if (softwareTypeService.getApplicationSoftwareTypes().contains(swtType)
				|| softwareTypeService.getSoftwareTypesByAttribute(
						SoftwareTypeService.APPLICATION_DELIVERY).contains(
						swtType)) {
			Syncer appSyncer = RollingApplicationSyncer.getInstance();

			return appSyncer.performSync(deploymentSyncItem);
		} else if (softwareTypeService.getSoftwareTypesByAttribute(
				SoftwareTypeService.LIBRARY_DELIVERY).contains(swtType)) {
			Syncer librarySyncer = RollingLibrarySyncer.getInstance();

			return librarySyncer.performSync(deploymentSyncItem);
		} else {
			throw new RollingDeliveryException(
					DCExceptionConstants.SW_TYPE_UNRECOGNIZED,
					new String[] { swtType.toString() });
		}
	}

	public InstanceDescriptor sync(CompositeSyncItem compositeSyncItem)
			throws RollingDeliveryException {
		RollingDeliveryException rollingDeliveryException = new RollingDeliveryException(
				DCExceptionConstants.ROLLING_SC_SYNC);
		throw rollingDeliveryException;

	}

}
