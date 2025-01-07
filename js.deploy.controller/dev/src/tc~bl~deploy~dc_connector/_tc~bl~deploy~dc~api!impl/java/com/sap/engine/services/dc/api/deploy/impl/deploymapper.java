/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.deploy.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;

import com.sap.engine.services.dc.api.deploy.DeployItem;
import com.sap.engine.services.dc.api.dscr.ClusterDescriptor;
import com.sap.engine.services.dc.api.dscr.ClusterDscrFactory;
import com.sap.engine.services.dc.api.dscr.ClusterStatus;
import com.sap.engine.services.dc.api.dscr.ICMInfo;
import com.sap.engine.services.dc.api.dscr.InstanceStatus;
import com.sap.engine.services.dc.api.dscr.ItemStatus;
import com.sap.engine.services.dc.api.dscr.TestInfo;
import com.sap.engine.services.dc.api.model.Sdu;
import com.sap.engine.services.dc.api.model.impl.SduMapperVisitor;
import com.sap.engine.services.dc.api.util.DALog;
import com.sap.engine.services.dc.api.dscr.InstanceDescriptor;
import com.sap.engine.services.dc.api.dscr.impl.ClusterDscrFactoryImpl;
import com.sap.engine.services.dc.api.dscr.ServerDescriptor;
import com.sap.engine.services.rmi_p4.P4RuntimeException;

/**
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-15
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
final public class DeployMapper {

	private static Map deployResultStatus = new HashMap();
	private static Map componentVersionHandlingRules = new HashMap();
	private static Map deployItemStatuses = new HashMap();
	private static Map deployItemVersionStatus = new HashMap();
	private static Map validationStatuses = new HashMap();
	private static final Map deployWorkflowStrategies = new HashMap();
	private static final Map lifeCycleDeployStrategies = new HashMap();

	private static final Map clusterStatuses = new HashMap();
	private static final Map instanceStatuses = new HashMap();
	private static final Map itemStatuses = new HashMap();
	private static final DeployItemMapperVisitor deployItemMapperVisitor = new DeployItemMapperVisitor();
	private static final SduMapperVisitor sduMapper = new SduMapperVisitor();

	static {
		// init deploy status
		deployResultStatus.put(
				com.sap.engine.services.dc.cm.deploy.DeployResultStatus.ERROR,
				com.sap.engine.services.dc.api.deploy.DeployResultStatus.ERROR);

		deployResultStatus
				.put(
						com.sap.engine.services.dc.cm.deploy.DeployResultStatus.SUCCESS,
						com.sap.engine.services.dc.api.deploy.DeployResultStatus.SUCCESS);

		deployResultStatus
				.put(
						com.sap.engine.services.dc.cm.deploy.DeployResultStatus.UNKNOWN,
						com.sap.engine.services.dc.api.deploy.DeployResultStatus.UNKNOWN);

		deployResultStatus
				.put(
						com.sap.engine.services.dc.cm.deploy.DeployResultStatus.WARNING,
						com.sap.engine.services.dc.api.deploy.DeployResultStatus.WARNING);
		// init version handling rule
		componentVersionHandlingRules
				.put(
						com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule.UPDATE_ALL_VERSIONS,
						com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule.UPDATE_ALL_VERSIONS);
		componentVersionHandlingRules
				.put(
						com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule.UPDATE_LOWER_VERSIONS_ONLY,
						com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule.UPDATE_LOWER_VERSIONS_ONLY);
		componentVersionHandlingRules
				.put(
						com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule.UPDATE_SAME_AND_LOWER_VERSIONS_ONLY,
						com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule.UPDATE_SAME_AND_LOWER_VERSIONS_ONLY);
		componentVersionHandlingRules
				.put(
						com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule.UPDATE_LOWER_OR_CHANGED_VERSIONS_ONLY,
						com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule.UPDATE_LOWER_OR_CHANGED_VERSIONS_ONLY);
		// init deploy item statuses
		deployItemStatuses.put(
				com.sap.engine.services.dc.cm.deploy.DeploymentStatus.ABORTED,
				com.sap.engine.services.dc.api.deploy.DeployItemStatus.ABORTED);
		deployItemStatuses
				.put(
						com.sap.engine.services.dc.cm.deploy.DeploymentStatus.OFFLINE_ABORTED,
						//com.sap.engine.services.dc.api.deploy.DeployItemStatus
						// .OFFLINE_ABORTED
						com.sap.engine.services.dc.api.deploy.DeployItemStatus.ABORTED);
		deployItemStatuses
				.put(
						com.sap.engine.services.dc.cm.deploy.DeploymentStatus.ADMITTED,
						com.sap.engine.services.dc.api.deploy.DeployItemStatus.ADMITTED);
		deployItemStatuses
				.put(
						com.sap.engine.services.dc.cm.deploy.DeploymentStatus.OFFLINE_ADMITTED,
						//com.sap.engine.services.dc.api.deploy.DeployItemStatus
						// .OFFLINE_ADMITTED
						com.sap.engine.services.dc.api.deploy.DeployItemStatus.ADMITTED);
		deployItemStatuses
				.put(
						com.sap.engine.services.dc.cm.deploy.DeploymentStatus.ALREADY_DEPLOYED,
						com.sap.engine.services.dc.api.deploy.DeployItemStatus.ALREADY_DEPLOYED);
		deployItemStatuses
				.put(
						com.sap.engine.services.dc.cm.deploy.DeploymentStatus.DELIVERED,
						com.sap.engine.services.dc.api.deploy.DeployItemStatus.DELIVERED);
		deployItemStatuses
				.put(
						com.sap.engine.services.dc.cm.deploy.DeploymentStatus.FILTERED,
						com.sap.engine.services.dc.api.deploy.DeployItemStatus.FILTERED);
		deployItemStatuses.put(
				com.sap.engine.services.dc.cm.deploy.DeploymentStatus.INITIAL,
				com.sap.engine.services.dc.api.deploy.DeployItemStatus.INITIAL);
		deployItemStatuses
				.put(
						com.sap.engine.services.dc.cm.deploy.DeploymentStatus.PREREQUISITE_VIOLATED,
						com.sap.engine.services.dc.api.deploy.DeployItemStatus.PREREQUISITE_VIOLATED);
		deployItemStatuses.put(
				com.sap.engine.services.dc.cm.deploy.DeploymentStatus.SKIPPED,
				com.sap.engine.services.dc.api.deploy.DeployItemStatus.SKIPPED);
		deployItemStatuses.put(
				com.sap.engine.services.dc.cm.deploy.DeploymentStatus.SUCCESS,
				com.sap.engine.services.dc.api.deploy.DeployItemStatus.SUCCESS);
		deployItemStatuses
				.put(
						com.sap.engine.services.dc.cm.deploy.DeploymentStatus.OFFLINE_SUCCESS,
						//com.sap.engine.services.dc.api.deploy.DeployItemStatus
						// .OFFLINE_SUCCESS
						com.sap.engine.services.dc.api.deploy.DeployItemStatus.SUCCESS);
		deployItemStatuses.put(
				com.sap.engine.services.dc.cm.deploy.DeploymentStatus.WARNING,
				com.sap.engine.services.dc.api.deploy.DeployItemStatus.WARNING);
		deployItemStatuses
				.put(
						com.sap.engine.services.dc.cm.deploy.DeploymentStatus.OFFLINE_WARNING,
						//com.sap.engine.services.dc.api.deploy.DeployItemStatus
						// .OFFLINE_WARNING
						com.sap.engine.services.dc.api.deploy.DeployItemStatus.WARNING);
		deployItemStatuses
				.put(
						com.sap.engine.services.dc.cm.deploy.DeploymentStatus.REPEATED,
						com.sap.engine.services.dc.api.deploy.DeployItemStatus.REPEATED);

		// init deploy item version status
		deployItemVersionStatus
				.put(
						com.sap.engine.services.dc.cm.deploy.VersionStatus.HIGHER,
						com.sap.engine.services.dc.api.deploy.DeployItemVersionStatus.HIGHER);
		deployItemVersionStatus
				.put(
						com.sap.engine.services.dc.cm.deploy.VersionStatus.LOWER,
						com.sap.engine.services.dc.api.deploy.DeployItemVersionStatus.LOWER);
		deployItemVersionStatus
				.put(
						com.sap.engine.services.dc.cm.deploy.VersionStatus.NEW,
						com.sap.engine.services.dc.api.deploy.DeployItemVersionStatus.NEW);
		deployItemVersionStatus
				.put(
						com.sap.engine.services.dc.cm.deploy.VersionStatus.NOT_RESOLVED,
						com.sap.engine.services.dc.api.deploy.DeployItemVersionStatus.NOT_RESOLVED);
		deployItemVersionStatus
				.put(
						com.sap.engine.services.dc.cm.deploy.VersionStatus.SAME,
						com.sap.engine.services.dc.api.deploy.DeployItemVersionStatus.SAME);
		// init validaton statuses
		validationStatuses.put(
				com.sap.engine.services.dc.cm.deploy.ValidationStatus.ERROR,
				com.sap.engine.services.dc.api.deploy.ValidationStatus.ERROR);
		validationStatuses.put(
				com.sap.engine.services.dc.cm.deploy.ValidationStatus.SUCCESS,
				com.sap.engine.services.dc.api.deploy.ValidationStatus.SUCCESS);
		/*
		 * validationStatuses.put(
		 * com.sap.engine.services.dc.cm.deploy.ValidationStatus.WARNING,
		 * com.sap.engine.services.dc.api.deploy.ValidationStatus.WARNING );
		 */

		// init deploy workflow strategies
		deployWorkflowStrategies
				.put(
						com.sap.engine.services.dc.api.deploy.DeployWorkflowStrategy.NORMAL,
						com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy.NORMAL);
		deployWorkflowStrategies
				.put(
						com.sap.engine.services.dc.api.deploy.DeployWorkflowStrategy.SAFETY,
						com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy.SAFETY);
		deployWorkflowStrategies
				.put(
						com.sap.engine.services.dc.api.deploy.DeployWorkflowStrategy.ROLLING,
						com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy.ROLLING);

		// init deploy lifecycle strategies
		lifeCycleDeployStrategies
				.put(
						com.sap.engine.services.dc.api.deploy.LifeCycleDeployStrategy.BULK,
						com.sap.engine.services.dc.cm.deploy.LifeCycleDeployStrategy.BULK);
		lifeCycleDeployStrategies
				.put(
						com.sap.engine.services.dc.api.deploy.LifeCycleDeployStrategy.SEQUENTIAL,
						com.sap.engine.services.dc.cm.deploy.LifeCycleDeployStrategy.SEQUENTIAL);
		lifeCycleDeployStrategies
				.put(
						com.sap.engine.services.dc.api.deploy.LifeCycleDeployStrategy.DISABLE_LCM,
						com.sap.engine.services.dc.cm.deploy.LifeCycleDeployStrategy.DISABLE_LCM);

		// only for rolling patch scenario
		clusterStatuses
				.put(
						com.sap.engine.services.dc.cm.dscr.ClusterStatus.PRODUCTIVE_AND_COMMITED,
						com.sap.engine.services.dc.api.dscr.ClusterStatus.PRODUCTIVE_AND_COMMITED);
		clusterStatuses
				.put(
						com.sap.engine.services.dc.cm.dscr.ClusterStatus.PRODUCTIVE_AND_ROLLED_BACK,
						com.sap.engine.services.dc.api.dscr.ClusterStatus.PRODUCTIVE_AND_ROLLED_BACK);
		clusterStatuses
				.put(
						com.sap.engine.services.dc.cm.dscr.ClusterStatus.PRODUCTIVE_BUT_NEED_ROLL_BACK,
						com.sap.engine.services.dc.api.dscr.ClusterStatus.PRODUCTIVE_BUT_NEED_ROLL_BACK);
		clusterStatuses
				.put(
						com.sap.engine.services.dc.cm.dscr.ClusterStatus.PRODUCTIVE_BUT_NEED_VALIDATION,
						com.sap.engine.services.dc.api.dscr.ClusterStatus.PRODUCTIVE_BUT_NEED_VALIDATION);

		instanceStatuses
				.put(
						com.sap.engine.services.dc.cm.dscr.InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT,
						com.sap.engine.services.dc.api.dscr.InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT);
		instanceStatuses
				.put(
						com.sap.engine.services.dc.cm.dscr.InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_ROLL_BACK,
						com.sap.engine.services.dc.api.dscr.InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_ROLL_BACK);
		instanceStatuses
				.put(
						com.sap.engine.services.dc.cm.dscr.InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_UPDATE,
						com.sap.engine.services.dc.api.dscr.InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_UPDATE);
		instanceStatuses
				.put(
						com.sap.engine.services.dc.cm.dscr.InstanceStatus.NOT_PRODUCTIVE_AND_NEED_VALIDATION,
						com.sap.engine.services.dc.api.dscr.InstanceStatus.NOT_PRODUCTIVE_AND_NEED_VALIDATION);
		instanceStatuses
				.put(
						com.sap.engine.services.dc.cm.dscr.InstanceStatus.PRODUCTIVE_AND_COMMITTED,
						com.sap.engine.services.dc.api.dscr.InstanceStatus.PRODUCTIVE_AND_COMMITTED);
		instanceStatuses
				.put(
						com.sap.engine.services.dc.cm.dscr.InstanceStatus.PRODUCTIVE_AND_ROLLED_BACK,
						com.sap.engine.services.dc.api.dscr.InstanceStatus.PRODUCTIVE_AND_ROLLED_BACK);
		instanceStatuses
				.put(
						com.sap.engine.services.dc.cm.dscr.InstanceStatus.PRODUCTIVE_BUT_NOT_UPDATED,
						com.sap.engine.services.dc.api.dscr.InstanceStatus.PRODUCTIVE_BUT_NOT_UPDATED);
		instanceStatuses
				.put(
						com.sap.engine.services.dc.cm.dscr.InstanceStatus.NOT_PRODUCTIVE_AND_UPDATED,
						com.sap.engine.services.dc.api.dscr.InstanceStatus.NOT_PRODUCTIVE_AND_UPDATED);
		instanceStatuses
				.put(
						com.sap.engine.services.dc.cm.dscr.InstanceStatus.NOT_PRODUCTIVE_AND_NOT_UPDATED,
						com.sap.engine.services.dc.api.dscr.InstanceStatus.NOT_PRODUCTIVE_AND_NOT_UPDATED);

		itemStatuses.put(com.sap.engine.services.dc.cm.dscr.ItemStatus.ABORTED,
				com.sap.engine.services.dc.api.dscr.ItemStatus.ABORTED);
		itemStatuses.put(com.sap.engine.services.dc.cm.dscr.ItemStatus.SUCCESS,
				com.sap.engine.services.dc.api.dscr.ItemStatus.SUCCESS);
		itemStatuses.put(com.sap.engine.services.dc.cm.dscr.ItemStatus.WARNING,
				com.sap.engine.services.dc.api.dscr.ItemStatus.WARNING);

	}

	static com.sap.engine.services.dc.api.deploy.DeployItemStatus mapDeployItemStatus(
			com.sap.engine.services.dc.cm.deploy.DeploymentStatus remoteDeploymentStatus) {
		com.sap.engine.services.dc.api.deploy.DeployItemStatus ret = (com.sap.engine.services.dc.api.deploy.DeployItemStatus) deployItemStatuses
				.get(remoteDeploymentStatus);
		if (ret == null) {
			throw new RuntimeException(
					"[ERROR CODE DPL.DCAPI.1016] Unknown deploy result status "
							+ remoteDeploymentStatus + " detected");
		}
		return ret;
	}

	static com.sap.engine.services.dc.api.deploy.DeployItemVersionStatus mapDeployItemVersionStatus(
			com.sap.engine.services.dc.cm.deploy.VersionStatus remoteVersionStatus) {
		com.sap.engine.services.dc.api.deploy.DeployItemVersionStatus ret = (com.sap.engine.services.dc.api.deploy.DeployItemVersionStatus) deployItemVersionStatus
				.get(remoteVersionStatus);
		if (ret == null) {
			throw new RuntimeException(
					"[ERROR CODE DPL.DCAPI.1017] Unknown deploy version status "
							+ remoteVersionStatus + " detected");
		}
		return ret;
	}

	static com.sap.engine.services.dc.api.deploy.DeployResultStatus mapResultStatus(
			com.sap.engine.services.dc.cm.deploy.DeployResultStatus remoteDeployResultStatus) {
		com.sap.engine.services.dc.api.deploy.DeployResultStatus ret = (com.sap.engine.services.dc.api.deploy.DeployResultStatus) deployResultStatus
				.get(remoteDeployResultStatus);
		if (ret == null) {
			ret = com.sap.engine.services.dc.api.deploy.DeployResultStatus.UNKNOWN;
		}
		return ret;
	}

	public static com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule mapComponentVersionHandlingRule(
			com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule versionHandlingRule) {
		com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule ret = (com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule) componentVersionHandlingRules
				.get(versionHandlingRule);
		if (ret == null) {
			ret = com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule.UPDATE_LOWER_VERSIONS_ONLY;
		}
		return ret;
	}

	public static com.sap.engine.services.dc.api.deploy.ValidationStatus mapValidationStatus(
			com.sap.engine.services.dc.cm.deploy.ValidationStatus remoteValidationStatus) {
		com.sap.engine.services.dc.api.deploy.ValidationStatus ret = (com.sap.engine.services.dc.api.deploy.ValidationStatus) validationStatuses
				.get(remoteValidationStatus);
		if (ret == null) {
			throw new RuntimeException(
					"[ERROR CODE DPL.DCAPI.1018] Unknown validation status "
							+ remoteValidationStatus + " detected");
		}
		return ret;
	}

	public static com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy mapDeployWorkflowStrategy(
			com.sap.engine.services.dc.api.deploy.DeployWorkflowStrategy deployWorkflowStrategy) {
		final com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy workflowStrategy = (com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy) deployWorkflowStrategies
				.get(deployWorkflowStrategy);

		if (workflowStrategy == null) {
			throw new RuntimeException(
					"[ERROR CODE DPL.DCAPI.1019] Unknown deploy workflow strategy "
							+ deployWorkflowStrategy + " detected");
		}

		return workflowStrategy;
	}

	static com.sap.engine.services.dc.cm.deploy.LifeCycleDeployStrategy mapLifeCycleDeployStrategy(
			com.sap.engine.services.dc.api.deploy.LifeCycleDeployStrategy lifeCycleDeployStrategy) {
		final com.sap.engine.services.dc.cm.deploy.LifeCycleDeployStrategy lifeCycleStrategy = (com.sap.engine.services.dc.cm.deploy.LifeCycleDeployStrategy) lifeCycleDeployStrategies
				.get(lifeCycleDeployStrategy);

		if (lifeCycleStrategy == null) {
			throw new RuntimeException(
					"[ERROR CODE DPL.DCAPI.1020] Unknown deploy lifecycle strategy "
							+ lifeCycleStrategy + " detected");
		}

		return lifeCycleStrategy;
	}

	static synchronized DeployItem mapOrCreateDeployItem(
			DeployItem deployItem,
			com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem deploymentBatchItem) {
		if (deployItem == null) {

			// create a new item using the path of the remote item
			deployItem = new DeployItemImpl(null, deploymentBatchItem
					.getSduFilePath());
		}
		return mapDeployItem(deployItem, deploymentBatchItem);

	}

	/**
	 * Pick up the properties of the remote item, map them to local ones and set
	 * them local properties of the local item
	 * 
	 * @param deployItem
	 * @param deploymentBatchItem
	 * @return
	 */
	static synchronized DeployItem mapDeployItem(
			DeployItem deployItem,
			com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem deploymentBatchItem) {

		DeployItemImpl impl = (DeployItemImpl) deployItem;

		DeployMapper.setCommonItemProps(null, impl, deploymentBatchItem);

		Sdu sdu = mapSdu(deploymentBatchItem.getSdu());
		impl.setSdu(sdu);

		deployItemMapperVisitor.setCompositeDeploymentItem(deploymentBatchItem,
				impl);

		deploymentBatchItem.getSdu().accept(deployItemMapperVisitor);
		impl.setSdu(deployItemMapperVisitor.getGeneratedSdu());

		return deployItem;
	}

	static synchronized Sdu mapSdu(com.sap.engine.services.dc.repo.Sdu sdu) {
		sdu.accept(sduMapper);
		return sduMapper.getGeneratedSdu();

	}

	public static void setCommonItemProps(
			DALog daLog,
			DeployItemImpl dItem,
			com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem deploymentBatchItem) {
		if (dItem == null || deploymentBatchItem == null) {
			return;
		}
		dItem.setDeployItemStatus(mapDeployItemStatus(deploymentBatchItem
				.getDeploymentStatus()));
		dItem.setVersionStatus(mapDeployItemVersionStatus(deploymentBatchItem
				.getVersionStatus()));
		dItem.setDescription(deploymentBatchItem.getDescription());

		dItem.setClusterDescriptor(mapClusterDescriptor(deploymentBatchItem
				.getClusterDescriptor()));

		try {
			// time statistics support
			dItem.setTimeStatistics(deploymentBatchItem
					.getTimeStatisticEntries());
		} catch (P4RuntimeException p4e) {
			// $JL_EXC$
			if (daLog != null) {
				daLog.traceThrowable(p4e);
			}
		} catch (MissingResourceException mre) {
			// $JL_EXC$
			if (daLog != null) {
				daLog.traceThrowable(mre);
			}
		} catch (NoSuchMethodError nsme) {
			// $JL_EXC$
			if (daLog != null) {
				daLog.traceThrowable(nsme);
			}
		}
	}

	private static ClusterDescriptor mapClusterDescriptor(
			com.sap.engine.services.dc.cm.dscr.ClusterDescriptor clusterDescriptor) {
		if (clusterDescriptor == null)
			return null;
		Iterator itr = clusterDescriptor.getInstanceDescriptors().iterator();
		HashSet instanceDescriptors = new HashSet();
		while (itr.hasNext()) {
			instanceDescriptors
					.add(mapInstanceDescriptor((com.sap.engine.services.dc.cm.dscr.InstanceDescriptor) itr
							.next()));
		}

		return ClusterDscrFactoryImpl.getInstance().createClusterDescriptor(
				instanceDescriptors,
				(ClusterStatus) clusterStatuses.get(clusterDescriptor
						.getClusterStatus()));
	}

	private static InstanceDescriptor mapInstanceDescriptor(
			com.sap.engine.services.dc.cm.dscr.InstanceDescriptor descriptor) {
		if (descriptor == null)
			return null;
		Iterator itr = descriptor.getServerDescriptors().iterator();
		HashSet serverDescriptors = new HashSet();
		while (itr.hasNext()) {
			serverDescriptors
					.add(mapServerDescriptor((com.sap.engine.services.dc.cm.dscr.ServerDescriptor) itr
							.next()));
		}

		return ClusterDscrFactoryImpl.getInstance().createInstanceDescriptor(
				descriptor.getInstanceID(),
				serverDescriptors,
				(InstanceStatus) instanceStatuses.get(descriptor
						.getInstanceStatus()),
				mapTestInfo(descriptor.getTestInfo()),
				descriptor.getDescription());
	}

	private static TestInfo mapTestInfo(
			com.sap.engine.services.dc.cm.dscr.TestInfo testInfo) {
		if (testInfo == null)
			return null;
		return ClusterDscrFactoryImpl.getInstance().createTestInfo(
				mapICMInfo(testInfo.getICMInfo()));
	}

	private static ICMInfo mapICMInfo(
			com.sap.engine.services.dc.cm.dscr.ICMInfo info) {
		if (info == null)
			return null;
		return ClusterDscrFactoryImpl.getInstance().createICMInfo(
				info.getHost(), info.getPort());
	}

	private static ServerDescriptor mapServerDescriptor(
			com.sap.engine.services.dc.cm.dscr.ServerDescriptor descriptor) {
		if (descriptor == null)
			return null;
		return ClusterDscrFactoryImpl.getInstance().createServerDescriptor(
				descriptor.getClusterID(), descriptor.getInstanceID(),
				(ItemStatus) itemStatuses.get(descriptor.getItemStatus()),
				descriptor.getDescription());
	}

}