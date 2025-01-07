package com.sap.engine.services.dc.gd.impl;

import com.sap.engine.frame.container.deploy.zdm.RollingResult;
import com.sap.engine.frame.container.deploy.zdm.RollingStatus;
import com.sap.engine.services.dc.cm.deploy.CompositeSyncItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentSyncItem;
import com.sap.engine.services.dc.cm.deploy.SyncItemVisitor;
import com.sap.engine.services.dc.cm.dscr.ClusterDescriptor;
import com.sap.engine.services.dc.cm.dscr.ClusterDscrFactory;
import com.sap.engine.services.dc.cm.dscr.InstanceDescriptor;
import com.sap.engine.services.dc.cm.dscr.InstanceStatus;

public class SyncRRAnalyzer extends AbstractRRAnalyzer implements
		SyncItemVisitor {

	private RollingResult rollingResult;
	private InstanceDescriptor instanceDescriptor = null;

	SyncRRAnalyzer(RollingResult rollingResult) {
		this.rollingResult = rollingResult;
	}

	public void visit(DeploymentSyncItem deploymentSyncItem) {
		com.sap.engine.frame.container.deploy.zdm.InstanceDescriptor frInstanceDescriptor = rollingResult
				.getInstanceDescriptor();
		ClusterDscrFactory clusterDscrFactory = ClusterDscrFactory
				.getInstance();
		instanceDescriptor = createInstanceDescriptor(clusterDscrFactory,
				frInstanceDescriptor);
	}

	InstanceDescriptor getInstanceDescriptor() {
		return instanceDescriptor;
	}

	public void visit(CompositeSyncItem syncItem) {
		// do nothing
	}

	protected InstanceStatus updateInstanceStatusMap(
			RollingStatus dsRollingStatus) {
		InstanceStatus instanceStatus;
		if (dsRollingStatus.equals(RollingStatus.ERROR)) {
			instanceStatus = InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT;
		} else if (dsRollingStatus.equals(RollingStatus.WARNING)) {
			instanceStatus = InstanceStatus.NOT_PRODUCTIVE_AND_UPDATED;
		} else if (dsRollingStatus.equals(RollingStatus.SUCCESS)) {
			instanceStatus = InstanceStatus.NOT_PRODUCTIVE_AND_UPDATED;
		} else {
			instanceStatus = InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT;
		}
		return instanceStatus;
	}

}
