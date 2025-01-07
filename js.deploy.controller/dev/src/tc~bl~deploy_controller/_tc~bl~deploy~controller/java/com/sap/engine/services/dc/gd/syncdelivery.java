package com.sap.engine.services.dc.gd;

import com.sap.engine.services.dc.cm.deploy.CompositeSyncItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentSyncItem;
import com.sap.engine.services.dc.cm.dscr.InstanceDescriptor;

public interface SyncDelivery {

	public InstanceDescriptor sync(DeploymentSyncItem deploymentSyncItem)
			throws RollingDeliveryException;

	public InstanceDescriptor sync(CompositeSyncItem compositeSyncItem)
			throws RollingDeliveryException;

}
