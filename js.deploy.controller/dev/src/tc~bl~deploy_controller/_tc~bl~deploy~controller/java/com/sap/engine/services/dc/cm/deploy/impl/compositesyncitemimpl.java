package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.Collection;

import com.sap.engine.services.dc.cm.deploy.BatchItemId;
import com.sap.engine.services.dc.cm.deploy.CompositeSyncItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentSyncItem;
import com.sap.engine.services.dc.cm.deploy.SyncItemVisitor;
import com.sap.engine.services.dc.cm.dscr.RollingInfo;
import com.sap.engine.services.dc.util.Constants;

class CompositeSyncItemImpl extends AbstractSyncItemImpl implements
		CompositeSyncItem {

	private static final long serialVersionUID = -5452315773144860718L;
	final private Collection<DeploymentSyncItem> deploymentSyncItems;

	CompositeSyncItemImpl(BatchItemId batchItemId,
			Collection<DeploymentSyncItem> deploymentSyncItems) {
		super(batchItemId);
		this.deploymentSyncItems = deploymentSyncItems;
	}

	public void accept(SyncItemVisitor visitor) {
		visitor.visit(this);
	}

	public Collection<DeploymentSyncItem> getDeploymentSyncItems() {
		return deploymentSyncItems;
	}

	public String toString() {
		final StringBuffer sb = new StringBuffer("CompositeSyncItem[");
		sb.append(super.toString());
		sb.append("DeploymentSyncItems = ");
		sb.append(getDeploymentSyncItems());
		sb.append(Constants.EOL);
		sb.append("]");
		return sb.toString();
	}

}
