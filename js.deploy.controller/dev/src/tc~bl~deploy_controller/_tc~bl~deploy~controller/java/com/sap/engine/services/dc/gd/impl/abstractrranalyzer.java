package com.sap.engine.services.dc.gd.impl;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.frame.container.deploy.zdm.RollingStatus;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.dscr.ClusterDscrFactory;
import com.sap.engine.services.dc.cm.dscr.ICMInfo;
import com.sap.engine.services.dc.cm.dscr.InstanceDescriptor;
import com.sap.engine.services.dc.cm.dscr.InstanceStatus;
import com.sap.engine.services.dc.cm.dscr.ItemStatus;
import com.sap.engine.services.dc.cm.dscr.ServerDescriptor;
import com.sap.engine.services.dc.cm.dscr.TestInfo;
import com.sap.engine.services.dc.gd.DeliveryException;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import static com.sap.engine.services.dc.util.RollingUtils.*;

abstract class AbstractRRAnalyzer extends AbstractRollingResultAnalyzer {

	protected InstanceDescriptor createInstanceDescriptor(
			ClusterDscrFactory clusterDscrFactory,
			com.sap.engine.frame.container.deploy.zdm.InstanceDescriptor frInstanceDescriptor) {
		int instanceId = frInstanceDescriptor.getInstanceID();
		Set<ServerDescriptor> serverDescriptors = new HashSet();
		Iterator frServerDescItr = frInstanceDescriptor.getServerDescriptors()
				.iterator();
		while (frServerDescItr.hasNext()) {
			com.sap.engine.frame.container.deploy.zdm.ServerDescriptor frServerDesc = (com.sap.engine.frame.container.deploy.zdm.ServerDescriptor) frServerDescItr
					.next();
			ItemStatus itemStatus = serverStatusMap(frServerDesc
					.getRollingStatus());
			int clusterId = frServerDesc.getClusterID();
			String desc = frServerDesc.getDescription();
			ServerDescriptor serverDesc = clusterDscrFactory
					.createServerDescriptor(clusterId, instanceId, itemStatus,
							desc);
			serverDescriptors.add(serverDesc);
		}
		RollingStatus rollingStatus = frInstanceDescriptor.getRollingStatus();
		InstanceStatus instanceStatus = updateInstanceStatusMap(rollingStatus);
		TestInfo testInfo = createTestInfo(instanceId);
		InstanceDescriptor instanceDescriptor = clusterDscrFactory
				.createInstanceDescriptor(instanceId, serverDescriptors,
						instanceStatus, testInfo, "");
		return instanceDescriptor;
	}

	private ItemStatus serverStatusMap(RollingStatus dsRollingStatus) {
		ItemStatus itemStatus;
		if (dsRollingStatus.equals(RollingStatus.ERROR)) {
			itemStatus = ItemStatus.ABORTED;
		} else if (dsRollingStatus.equals(RollingStatus.WARNING)) {
			itemStatus = ItemStatus.WARNING;
		} else if (dsRollingStatus.equals(RollingStatus.SUCCESS)) {
			itemStatus = ItemStatus.SUCCESS;
		} else {
			itemStatus = null;

		}
		return itemStatus;
	}

	abstract protected InstanceStatus updateInstanceStatusMap(
			RollingStatus dsRollingStatus);

}
