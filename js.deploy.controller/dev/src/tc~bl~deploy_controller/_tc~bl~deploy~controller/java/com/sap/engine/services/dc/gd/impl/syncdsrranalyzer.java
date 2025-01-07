package com.sap.engine.services.dc.gd.impl;

import com.sap.engine.services.dc.cm.deploy.CompositeSyncItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentSyncItem;
import com.sap.engine.services.dc.cm.deploy.SyncItemVisitor;
import com.sap.engine.services.dc.cm.dscr.ClusterDescriptor;
import com.sap.engine.services.dc.cm.dscr.ClusterDscrFactory;
import com.sap.engine.services.dc.cm.dscr.InstanceDescriptor;
import com.sap.engine.services.dc.cm.dscr.InstanceStatus;
import com.sap.engine.services.deploy.zdm.DSRollingResult;
import com.sap.engine.services.deploy.zdm.utils.DSRollingStatus;

class SyncDSRRAnalyzer extends AbstractDSRRAnalyzer implements SyncItemVisitor {

	private DSRollingResult dsRollingResult;
	private InstanceDescriptor instanceDescriptor = null;

	SyncDSRRAnalyzer(DSRollingResult dsRollingResult) {
		this.dsRollingResult = dsRollingResult;
	}

	public void visit(DeploymentSyncItem deploymentSyncItem) {
		com.sap.engine.services.deploy.zdm.utils.InstanceDescriptor dsInstanceDescriptor = dsRollingResult
				.getInstanceDescriptor();
		ClusterDscrFactory clusterDscrFactory = ClusterDscrFactory
				.getInstance();
		instanceDescriptor = createInstanceDescriptor(clusterDscrFactory,
				dsInstanceDescriptor);
	}

	InstanceDescriptor getInstanceDescriptor() {
		return instanceDescriptor;
	}

	public void visit(CompositeSyncItem syncItem) {
		// do nothing

	}

	protected InstanceStatus updateInstanceStatusMap(
			DSRollingStatus dsRollingStatus) {
		InstanceStatus instanceStatus;
		if (dsRollingStatus.equals(DSRollingStatus.ERROR)) {
			instanceStatus = InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT;
		} else if (dsRollingStatus.equals(DSRollingStatus.WARNING)) {
			instanceStatus = InstanceStatus.NOT_PRODUCTIVE_AND_UPDATED;
		} else if (dsRollingStatus.equals(DSRollingStatus.SUCCESS)) {
			instanceStatus = InstanceStatus.NOT_PRODUCTIVE_AND_UPDATED;
		} else {
			instanceStatus = InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT;
		}
		return instanceStatus;
	}

}
