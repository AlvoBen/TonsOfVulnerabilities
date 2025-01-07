package com.sap.engine.services.dc.api.dscr;

import java.util.Set;

/**
 * 
 * @author I031421
 * @deprecated The interface will only be used for proofing the concept in the
 *             prototyping phase. It will not be shipped to external customers
 *             and is not considered as public interface, without reviewing it.
 */
public interface ClusterDscrFactory {

	public ClusterDescriptor createClusterDescriptor(Set instanceDescriptors,
			ClusterStatus clusterStatus);

	public InstanceDescriptor createInstanceDescriptor(int instanceId,
			Set serverDescriptors, InstanceStatus instanceStatus,
			TestInfo testInfo, String description);

	public ServerDescriptor createServerDescriptor(int clusterId,
			int instanceId, ItemStatus itemStatus, String description);

	public TestInfo createTestInfo(ICMInfo icmInfo);

	public ICMInfo createICMInfo(String host, int port);

}
