package com.sap.engine.services.dc.gd.impl;

import java.util.HashSet;
import java.util.Set;

import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.dscr.ClusterDescriptor;
import com.sap.engine.services.dc.cm.dscr.ClusterDscrFactory;
import com.sap.engine.services.dc.cm.dscr.ClusterStatus;
import com.sap.engine.services.dc.cm.dscr.InstanceDescriptor;
import com.sap.engine.services.dc.cm.dscr.InstanceStatus;
import com.sap.engine.services.dc.cm.dscr.RollingInfo;
import com.sap.engine.services.dc.gd.DeliveryException;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.deploy.zdm.DSRollingResult;
import com.sap.engine.services.deploy.zdm.utils.DSRollingStatus;

class DSRollingResultAnalyzer extends AbstractDSRRAnalyzer implements
		DeploymentBatchItemVisitor {

	private DSRollingResult dsRollingResult;
	protected DeliveryException de = null;

	DSRollingResultAnalyzer(DSRollingResult dsRollingResult) {
		this.dsRollingResult = dsRollingResult;
	}

	public void visit(CompositeDeploymentItem deploymentItem) {
		// do nothing
	}

	public void visit(DeploymentItem deploymentItem) {
		com.sap.engine.services.deploy.zdm.utils.InstanceDescriptor dsInstanceDescriptor = dsRollingResult
				.getInstanceDescriptor();
		ClusterDscrFactory clusterDscrFactory = ClusterDscrFactory
				.getInstance();
		InstanceDescriptor instanceDescriptor = createInstanceDescriptor(
				clusterDscrFactory, dsInstanceDescriptor);
		Set<InstanceDescriptor> oldInstanceDescriptors = deploymentItem
				.getClusterDescriptor().getInstanceDescriptors();
		Set<InstanceDescriptor> instanceDescriptors = new HashSet<InstanceDescriptor>();
		instanceDescriptors.add(instanceDescriptor);
		instanceDescriptors.addAll(oldInstanceDescriptors);
		ClusterStatus clusterStatus = analyseUpdateClusterStatus(instanceDescriptor
				.getInstanceStatus());
		RollingInfo rollingInfo = clusterDscrFactory
				.createRollingInfo(dsRollingResult.getApplicationName()
						.getApplicationName());
		ClusterDescriptor clusterDescriptor = clusterDscrFactory
				.createClusterDescriptor(instanceDescriptors, clusterStatus,
						rollingInfo);
		deploymentItem.setClusterDescriptor(clusterDescriptor);
		analyseDeploymentItemStatus(dsInstanceDescriptor, deploymentItem);
	}

	DeliveryException getException() {
		return de;
	}

	protected void analyseDeploymentItemStatus(
			com.sap.engine.services.deploy.zdm.utils.InstanceDescriptor dsInstanceDescriptor,
			DeploymentItem deploymentItem) {
		DSRollingStatus dsRollingStatus = dsInstanceDescriptor
				.getDSRollingStatus();
		if (dsRollingStatus.equals(DSRollingStatus.WARNING)) {
			deploymentItem.setDeploymentStatus(DeploymentStatus.WARNING);
			deploymentItem.addDescription("Warnings:\n"
					+ dsInstanceDescriptor.toString());
		} else if (dsRollingStatus.equals(DSRollingStatus.ERROR)) {
			deploymentItem.addDescription("Error:\n"
					+ dsInstanceDescriptor.toString());
			de = new DeliveryException(
					DCExceptionConstants.ROLLING_UPDATE_ERROR, new String[] {
							deploymentItem.toString(),
							dsInstanceDescriptor.toString() });
		} else if (!dsRollingStatus.equals(DSRollingStatus.SUCCESS)) {
			deploymentItem.addDescription("Error:\n"
					+ UNDETERMINED_INSTANCE_STATUS);
			de = new DeliveryException(
					DCExceptionConstants.ROLLING_UPDATE_ERROR, new String[] {
							deploymentItem.toString(),
							UNDETERMINED_INSTANCE_STATUS });
		}
	}

	protected InstanceStatus updateInstanceStatusMap(
			DSRollingStatus dsRollingStatus) {
		InstanceStatus instanceStatus;
		if (dsRollingStatus.equals(DSRollingStatus.ERROR)) {
			instanceStatus = InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_UPDATE;
		} else if (dsRollingStatus.equals(DSRollingStatus.WARNING)) {
			instanceStatus = InstanceStatus.NOT_PRODUCTIVE_AND_NEED_VALIDATION;
		} else if (dsRollingStatus.equals(DSRollingStatus.SUCCESS)) {
			instanceStatus = InstanceStatus.NOT_PRODUCTIVE_AND_NEED_VALIDATION;
		} else {
			instanceStatus = InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_UPDATE;
		}
		return instanceStatus;
	}

}
