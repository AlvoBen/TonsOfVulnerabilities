package com.sap.engine.services.dc.api.dscr.impl;

import java.util.Set;

import com.sap.engine.services.dc.api.dscr.ClusterDescriptor;
import com.sap.engine.services.dc.api.dscr.ClusterDscrFactory;
import com.sap.engine.services.dc.api.dscr.ClusterStatus;
import com.sap.engine.services.dc.api.dscr.ICMInfo;
import com.sap.engine.services.dc.api.dscr.InstanceDescriptor;
import com.sap.engine.services.dc.api.dscr.InstanceStatus;
import com.sap.engine.services.dc.api.dscr.ItemStatus;
import com.sap.engine.services.dc.api.dscr.ServerDescriptor;
import com.sap.engine.services.dc.api.dscr.TestInfo;

public class ClusterDscrFactoryImpl implements ClusterDscrFactory {

	private static ClusterDscrFactoryImpl instance;

	public synchronized static ClusterDscrFactory getInstance() {
		if (instance == null) {
			instance = new ClusterDscrFactoryImpl();
		}
		return instance;
	}

	public ClusterDescriptor createClusterDescriptor(Set instanceDescriptors,
			ClusterStatus clusterStatus) {
		return new ClusterDescriptorImpl(instanceDescriptors, clusterStatus);
	}

	public ICMInfo createICMInfo(String host, int port) {
		return new ICMInfoImpl(host, port);
	}

	public InstanceDescriptor createInstanceDescriptor(int instanceId,
			Set serverDescriptors, InstanceStatus instanceStatus,
			TestInfo testInfo, String description) {
		return new InstanceDescriptorImpl(serverDescriptors, instanceStatus,
				instanceId, testInfo, description);
	}

	public ServerDescriptor createServerDescriptor(int clusterId,
			int instanceId, ItemStatus itemStatus, String description) {
		return new ServerDescriptorImpl(itemStatus, clusterId, instanceId,
				description);
	}

	public TestInfo createTestInfo(ICMInfo icmInfo) {
		return new TestInfoImpl(icmInfo);
	}

}
