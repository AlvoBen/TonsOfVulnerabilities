package com.sap.engine.services.dc.cm.dscr.impl;

import java.util.Set;

import com.sap.engine.services.dc.cm.dscr.ClusterDescriptor;
import com.sap.engine.services.dc.cm.dscr.ClusterDscrFactory;
import com.sap.engine.services.dc.cm.dscr.ClusterStatus;
import com.sap.engine.services.dc.cm.dscr.ICMInfo;
import com.sap.engine.services.dc.cm.dscr.InstanceDescriptor;
import com.sap.engine.services.dc.cm.dscr.InstanceStatus;
import com.sap.engine.services.dc.cm.dscr.ItemStatus;
import com.sap.engine.services.dc.cm.dscr.RollingInfo;
import com.sap.engine.services.dc.cm.dscr.ServerDescriptor;
import com.sap.engine.services.dc.cm.dscr.TestInfo;

public class ClusterDscrFactoryImpl extends ClusterDscrFactory {

	@Override
	public ClusterDescriptor createClusterDescriptor(Set instanceDescriptors,
			ClusterStatus clusterStatus, RollingInfo rollingInfo) {
		return new ClusterDescriptorImpl(instanceDescriptors, clusterStatus,
				rollingInfo);
	}

	@Override
	public InstanceDescriptor createInstanceDescriptor(int instanceId,
			Set serverDescriptors, InstanceStatus instanceStatus,
			TestInfo testInfo, String description) {
		return new InstanceDescriptorImpl(serverDescriptors, instanceStatus,
				instanceId, testInfo, description);
	}

	@Override
	public ServerDescriptor createServerDescriptor(int clusterId,
			int instanceId, ItemStatus itemStatus, String description) {
		return new ServerDescriptorImpl(itemStatus, clusterId, instanceId,
				description);
	}

	@Override
	public TestInfo createTestInfo(ICMInfo icmInfo) {
		return new TestInfoImpl(icmInfo);
	}

	@Override
	public ICMInfo createICMInfo(String host, int port) {
		return new ICMInfoImpl(host, port);
	}

	@Override
	public RollingInfo createRollingInfo(String itemName) {
		return new RollingInfoImpl(itemName);
	}

	@Override
	public RollingInfo createRollingInfo(String itemName, byte itemType) {
		return new RollingInfoImpl(itemName, itemType);
	}

}
