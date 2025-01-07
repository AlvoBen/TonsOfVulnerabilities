package com.sap.engine.services.dc.cm.deploy.impl;

import com.sap.engine.services.dc.cm.deploy.BatchItemId;
import com.sap.engine.services.dc.cm.deploy.DeploymentSyncItem;
import com.sap.engine.services.dc.cm.deploy.SyncItemVisitor;
import com.sap.engine.services.dc.cm.dscr.RollingInfo;
import com.sap.engine.services.dc.repo.SoftwareType;
import com.sap.engine.services.dc.util.Constants;

class DeploymentSyncItemImpl extends AbstractSyncItemImpl implements
		DeploymentSyncItem {

	private static final long serialVersionUID = -7643865490554787276L;

	final private RollingInfo rollingInfo;
	final private SoftwareType softwareType;

	DeploymentSyncItemImpl(BatchItemId batchItemId, RollingInfo rollingInfo,
			SoftwareType softwareType) {
		super(batchItemId);
		this.rollingInfo = rollingInfo;
		this.softwareType = softwareType;
	}

	public RollingInfo getRollingInfo() {
		return rollingInfo;
	}

	public SoftwareType getSoftwareType() {
		return softwareType;
	}

	public void accept(SyncItemVisitor visitor) {
		visitor.visit(this);
	}

	public String toString() {
		final StringBuffer sb = new StringBuffer("DeploymentSyncItem[");
		sb.append(super.toString());
		sb.append("RollingInfo = ");
		sb.append(getRollingInfo());
		sb.append(Constants.EOL);
		sb.append("SoftwareType = ");
		sb.append(getSoftwareType());
		sb.append(Constants.EOL);
		sb.append("]");
		return sb.toString();
	}

}
