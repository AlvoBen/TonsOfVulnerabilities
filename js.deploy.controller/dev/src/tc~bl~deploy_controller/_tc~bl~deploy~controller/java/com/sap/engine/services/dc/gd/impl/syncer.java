package com.sap.engine.services.dc.gd.impl;

import com.sap.engine.services.dc.cm.deploy.DeploymentSyncItem;
import com.sap.engine.services.dc.cm.dscr.InstanceDescriptor;
import com.sap.engine.services.dc.gd.RollingDeliveryException;

interface Syncer {

	InstanceDescriptor performSync(DeploymentSyncItem deploymentSyncItem)
			throws RollingDeliveryException;

}
