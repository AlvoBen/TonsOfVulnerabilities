package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.deploy.DeployListenersList;
import com.sap.engine.services.dc.cm.deploy.DeployParallelismStrategy;
import com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatch;
import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.cm.deploy.DeploymentObserver;
import com.sap.engine.services.dc.cm.deploy.InstanceData;
import com.sap.engine.services.dc.cm.deploy.LifeCycleDeployStrategy;
import com.sap.engine.services.dc.cm.utils.measurement.DataMeasurements;
import com.sap.engine.services.dc.util.lock.LockData;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-7
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class DeploymentDataImpl implements DeploymentData {

	private final DeployListenersList deployListenersList;
	private final Collection sortedDeploymentBatchItem;
	private final DeploymentBatch deploymentBatch;
	private final String sessionId;
	private final Collection deploymentObservers;
	private final ErrorStrategy deploymentErrorStrategy;
	private final DeployWorkflowStrategy deployWorkflowStrategy;
	private final DeployParallelismStrategy deployParallelismStrategy;
	private final LifeCycleDeployStrategy lifeCycleDeployStrategy;
	private final String userUniqueId;
	private final String callerHost;
	private final boolean timeStatEnabled;
	private String description;
	private final Set<InstanceData> instancesData;
	private final LockData lockData;
	private final DataMeasurements dataMeasurements;
	private final String osUserName;
	private final String osUserPass;

	DeploymentDataImpl(Collection sortedDeploymentBatchItem,
			DeploymentBatch deploymentBatch, String sessionId,
			Collection deploymentObservers,
			ErrorStrategy deploymentErrorStrategy,
			DeployWorkflowStrategy workflowStrategy,
			DeployParallelismStrategy deployParallelismStrategy,
			LifeCycleDeployStrategy lifeCycleDeployStrategy,
			DeployListenersList deployListenersList, String userUniqueId,
			String callerHost, String aDescription, boolean aTimeStatEnabled,
			Set<InstanceData> instancesData, LockData lockData,
			DataMeasurements dataMeasurements, String osUserName,
			String osUserPass) {
		this.sortedDeploymentBatchItem = sortedDeploymentBatchItem;
		this.deploymentBatch = deploymentBatch;
		this.sessionId = sessionId;
		this.deploymentObservers = deploymentObservers;
		this.deploymentErrorStrategy = deploymentErrorStrategy;
		this.deployWorkflowStrategy = workflowStrategy;
		this.deployParallelismStrategy = deployParallelismStrategy;
		this.lifeCycleDeployStrategy = lifeCycleDeployStrategy;
		this.deployListenersList = deployListenersList;
		this.userUniqueId = userUniqueId;
		this.callerHost = callerHost;
		this.description = aDescription;
		this.timeStatEnabled = aTimeStatEnabled;
		if (instancesData == null) {
			this.instancesData = new HashSet<InstanceData>();
		} else {
			this.instancesData = instancesData;
		}
		this.lockData = lockData;
		this.dataMeasurements = (dataMeasurements == null) ? new DataMeasurements()
				: dataMeasurements;
		this.osUserName = osUserName;
		this.osUserPass = osUserPass;
	}

	public DataMeasurements getMeasurements() {
		return dataMeasurements;
	}

	public String getUserUniqueId() {
		return this.userUniqueId;
	}

	public String getCallerHost() {
		return this.callerHost;
	}

	public Collection getSortedDeploymentBatchItem() {
		return this.sortedDeploymentBatchItem;
	}

	public DeploymentBatch getDeploymentBatch() {
		return this.deploymentBatch;
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public Collection getDeploymentObservers() {
		return this.deploymentObservers;
	}

	public ErrorStrategy getDeploymentErrorStrategy() {
		return this.deploymentErrorStrategy;
	}

	public DeployWorkflowStrategy getDeployWorkflowStrategy() {
		return this.deployWorkflowStrategy;
	}

	public DeployParallelismStrategy getDeployParallelismStrategy() {
		return this.deployParallelismStrategy;
	}

	public void addDeploymentObserver(DeploymentObserver observer) {
		this.deploymentObservers.add(observer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.deploy.DeploymentData#
	 * getLifeCycleDeployStrategy()
	 */
	public LifeCycleDeployStrategy getLifeCycleDeployStrategy() {
		return this.lifeCycleDeployStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.deploy.DeploymentData#clear()
	 */
	public void clear() {
		this.sortedDeploymentBatchItem.clear();
		this.deploymentBatch.clear();
		this.deploymentObservers.clear();
		this.instancesData.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentData#getDeployListenersList
	 * ()
	 */
	public DeployListenersList getDeployListenersList() {
		return this.deployListenersList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.deploy.DeploymentData#getDescription()
	 */
	public String getDescription() {
		return this.description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.deploy.DeploymentData#setDescription()
	 */
	public void setDescription(String aDescription) {
		this.description = aDescription;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentData#getTimeStatsEnabled()
	 */
	public boolean getTimeStatsEnabled() {
		return this.timeStatEnabled;
	}

	public void addInstanceData(InstanceData instanceData) {
		instancesData.add(instanceData);
	}

	public void removeInstanceData(InstanceData instanceData) {
		instancesData.remove(instanceData);
	}

	public Set<InstanceData> getInstancesData() {
		return instancesData;
	}

	public LockData getLockData() {
		return lockData;
	}

	public String getOsUserName() {
		return osUserName;
	}

	public String getOsUserPass() {
		return osUserPass;
	}

}
