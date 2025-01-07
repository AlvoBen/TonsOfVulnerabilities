package com.sap.engine.services.dc.gd.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.frame.cluster.monitor.ClusterMonitor;
import com.sap.engine.services.dc.cm.dscr.ClusterDscrFactory;
import com.sap.engine.services.dc.cm.dscr.ICMInfo;
import com.sap.engine.services.dc.cm.dscr.InstanceDescriptor;
import com.sap.engine.services.dc.cm.dscr.InstanceStatus;
import com.sap.engine.services.dc.cm.dscr.ItemStatus;
import com.sap.engine.services.dc.cm.dscr.ServerDescriptor;
import com.sap.engine.services.dc.cm.dscr.TestInfo;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import static com.sap.engine.services.dc.util.RollingUtils.*;

import com.sap.engine.services.dc.util.ClusterInfo;
import com.sap.engine.services.dc.util.ClusterUtils;
import com.sap.engine.services.deploy.zdm.utils.DSRollingStatus;

abstract class AbstractDSRRAnalyzer extends AbstractRollingResultAnalyzer {

	protected InstanceDescriptor createInstanceDescriptor(
			ClusterDscrFactory clusterDscrFactory,
			com.sap.engine.services.deploy.zdm.utils.InstanceDescriptor dsInstanceDescriptor) {
		Set<ServerDescriptor> serverDescriptors = new HashSet();
		Iterator dsServerDescItr = dsInstanceDescriptor.getServerDescriptors()
				.iterator();
		while (dsServerDescItr.hasNext()) {
			com.sap.engine.services.deploy.zdm.utils.ServerDescriptor dsServerDesc = (com.sap.engine.services.deploy.zdm.utils.ServerDescriptor) dsServerDescItr
					.next();
			ItemStatus itemStatus = serverStatusMap(dsServerDesc
					.getDSRollingStatus());
			int clusterId = dsServerDesc.getClusterID();
			int instanceId = dsServerDesc.getInstanceID();
			String desc = dsServerDesc.getDescription();
			ServerDescriptor serverDesc = clusterDscrFactory
					.createServerDescriptor(clusterId, instanceId, itemStatus,
							desc);
			serverDescriptors.add(serverDesc);
		}
		DSRollingStatus dsRollingStatus = dsInstanceDescriptor
				.getDSRollingStatus();
		InstanceStatus instanceStatus = updateInstanceStatusMap(dsRollingStatus);
		int instanceId = dsInstanceDescriptor.getInstanceID();
		TestInfo testInfo = createTestInfo(instanceId);
		InstanceDescriptor instanceDescriptor = clusterDscrFactory
				.createInstanceDescriptor(instanceId, serverDescriptors,
						instanceStatus, testInfo, "");
		return instanceDescriptor;

	}

	private ItemStatus serverStatusMap(DSRollingStatus dsRollingStatus) {
		ItemStatus itemStatus;
		if (dsRollingStatus.equals(DSRollingStatus.ERROR)) {
			itemStatus = ItemStatus.ABORTED;
		} else if (dsRollingStatus.equals(DSRollingStatus.WARNING)) {
			itemStatus = ItemStatus.WARNING;
		} else if (dsRollingStatus.equals(DSRollingStatus.SUCCESS)) {
			itemStatus = ItemStatus.SUCCESS;
		} else {
			itemStatus = null;

		}
		return itemStatus;
	}

	abstract protected InstanceStatus updateInstanceStatusMap(
			DSRollingStatus dsRollingStatus);

}
