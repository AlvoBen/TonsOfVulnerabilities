package com.sap.engine.services.dc.gd.impl;

import java.util.HashSet;
import java.util.Set;

import com.sap.engine.frame.container.deploy.zdm.RollingName;
import com.sap.engine.frame.container.deploy.zdm.RollingResult;
import com.sap.engine.frame.container.deploy.zdm.RollingStatus;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
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
import com.sap.engine.services.dc.gd.DeliveryException;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;

class RollingResultAnalyzer extends AbstractRRAnalyzer implements
		DeploymentBatchItemVisitor {

	private RollingResult rollingResult;
	protected DeliveryException de = null;

	RollingResultAnalyzer(RollingResult rollingResult) {
		this.rollingResult = rollingResult;
	}

	public void visit(CompositeDeploymentItem deploymentItem) {
		// do nothing
	}

	public void visit(DeploymentItem deploymentItem) {
		ClusterDscrFactory clusterDscrFactory = ClusterDscrFactory
				.getInstance();
		com.sap.engine.frame.container.deploy.zdm.InstanceDescriptor frInstanceDescriptor = rollingResult
				.getInstanceDescriptor();
		InstanceDescriptor instanceDescriptor = createInstanceDescriptor(
				clusterDscrFactory, frInstanceDescriptor);
		Set<InstanceDescriptor> oldInstanceDescriptors = deploymentItem
				.getClusterDescriptor().getInstanceDescriptors();
		Set<InstanceDescriptor> instanceDescriptors = new HashSet<InstanceDescriptor>();
		instanceDescriptors.add(instanceDescriptor);
		instanceDescriptors.addAll(oldInstanceDescriptors);
		ClusterStatus clusterStatus = analyseUpdateClusterStatus(instanceDescriptor
				.getInstanceStatus());
		RollingName rollingName = rollingResult.getRollingName();
		RollingInfo rollingInfo = clusterDscrFactory.createRollingInfo(
				rollingName.getName(), rollingName.getComponentType());
		ClusterDescriptor clusterDescriptor = clusterDscrFactory
				.createClusterDescriptor(instanceDescriptors, clusterStatus,
						rollingInfo);
		deploymentItem.setClusterDescriptor(clusterDescriptor);
		analyseDeploymentItemStatus(frInstanceDescriptor, deploymentItem);
	}

	DeliveryException getException() {
		return de;
	}

	protected void analyseDeploymentItemStatus(
			com.sap.engine.frame.container.deploy.zdm.InstanceDescriptor frInstanceDescriptor,
			DeploymentItem deploymentItem) {
		RollingStatus rollingStatus = frInstanceDescriptor.getRollingStatus();
		if (rollingStatus.equals(RollingStatus.WARNING)) {
			deploymentItem.setDeploymentStatus(DeploymentStatus.WARNING);
			deploymentItem.addDescription("Warnings:\n"
					+ frInstanceDescriptor.toString());
		} else if (rollingStatus.equals(RollingStatus.ERROR)) {
			deploymentItem.addDescription("Error:\n"
					+ frInstanceDescriptor.toString());
			de = new DeliveryException(
					DCExceptionConstants.ROLLING_UPDATE_ERROR, new String[] {
							deploymentItem.toString(),
							frInstanceDescriptor.toString() });
		} else if (!rollingStatus.equals(RollingStatus.SUCCESS)) {
			deploymentItem.addDescription("Error:\n"
					+ UNDETERMINED_INSTANCE_STATUS);
			de = new DeliveryException(
					DCExceptionConstants.ROLLING_UPDATE_ERROR, new String[] {
							deploymentItem.toString(),
							UNDETERMINED_INSTANCE_STATUS });
		}
	}

	protected InstanceStatus updateInstanceStatusMap(
			RollingStatus dsRollingStatus) {
		InstanceStatus instanceStatus;
		if (dsRollingStatus.equals(RollingStatus.ERROR)) {
			instanceStatus = InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_UPDATE;
		} else if (dsRollingStatus.equals(RollingStatus.WARNING)) {
			instanceStatus = InstanceStatus.NOT_PRODUCTIVE_AND_NEED_VALIDATION;
		} else if (dsRollingStatus.equals(RollingStatus.SUCCESS)) {
			instanceStatus = InstanceStatus.NOT_PRODUCTIVE_AND_NEED_VALIDATION;
		} else {
			instanceStatus = InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_UPDATE;
		}
		return instanceStatus;
	}

}
