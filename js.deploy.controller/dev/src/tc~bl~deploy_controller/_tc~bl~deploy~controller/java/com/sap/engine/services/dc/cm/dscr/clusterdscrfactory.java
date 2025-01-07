package com.sap.engine.services.dc.cm.dscr;

import java.util.Set;

import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.logging.DCLog;

/**
 * 
 * @author I031421
 * @deprecated The interface will only be used for proofing the concept in the
 *             prototyping phase. It will not be shipped to external customers
 *             and is not considered as public interface, without reviewing it.
 */
public abstract class ClusterDscrFactory {

	private static ClusterDscrFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.dscr.impl.ClusterDscrFactoryImpl";

	protected ClusterDscrFactory() {
	}

	/**
	 * @return the object reference for the factory. The class is implemented as
	 *         a Singleton.
	 */
	public static synchronized ClusterDscrFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static ClusterDscrFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (ClusterDscrFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003101 An error occurred while creating an instance of "
					+ "class ClusterDscrFactory!";

			throw new RuntimeException(errMsg, e);
		}
	}

	public abstract ClusterDescriptor createClusterDescriptor(
			Set<InstanceDescriptor> instanceDescriptors,
			ClusterStatus clusterStatus, RollingInfo rollingInfo);

	public abstract InstanceDescriptor createInstanceDescriptor(int instanceId,
			Set<ServerDescriptor> serverDescriptors,
			InstanceStatus instanceStatus, TestInfo testInfo, String descrption);

	public abstract ServerDescriptor createServerDescriptor(int clusterId,
			int instanceId, ItemStatus itemStatus, String description);

	public abstract TestInfo createTestInfo(ICMInfo icmInfo);

	public abstract ICMInfo createICMInfo(String host, int port);

	public abstract RollingInfo createRollingInfo(String itemName);

	public abstract RollingInfo createRollingInfo(String itemName, byte itemType);

}
