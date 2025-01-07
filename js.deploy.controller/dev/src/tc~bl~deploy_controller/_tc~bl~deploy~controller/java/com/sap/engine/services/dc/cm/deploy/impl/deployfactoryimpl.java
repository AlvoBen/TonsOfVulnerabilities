package com.sap.engine.services.dc.cm.deploy.impl;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Set;

import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.deploy.BatchItemId;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.CompositeSyncItem;
import com.sap.engine.services.dc.cm.deploy.DeployFactory;
import com.sap.engine.services.dc.cm.deploy.DeployListenersList;
import com.sap.engine.services.dc.cm.deploy.DeployParallelismStrategy;
import com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.deploy.Deployer;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatch;
import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentSyncItem;
import com.sap.engine.services.dc.cm.deploy.InstanceData;
import com.sap.engine.services.dc.cm.deploy.LifeCycleDeployStrategy;
import com.sap.engine.services.dc.cm.deploy.RollingMonitor;
import com.sap.engine.services.dc.cm.deploy.SafeModeDeployer;
import com.sap.engine.services.dc.cm.deploy.SyncContext;
import com.sap.engine.services.dc.cm.deploy.SyncException;
import com.sap.engine.services.dc.cm.deploy.SyncItem;
import com.sap.engine.services.dc.cm.deploy.SyncRequest;
import com.sap.engine.services.dc.cm.deploy.SyncResult;
import com.sap.engine.services.dc.cm.deploy.Syncer;
import com.sap.engine.services.dc.cm.deploy.ValidationResult;
import com.sap.engine.services.dc.cm.deploy.ValidationStatus;
import com.sap.engine.services.dc.cm.dscr.RollingInfo;
import com.sap.engine.services.dc.cm.utils.measurement.DataMeasurements;
import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SduId;
import com.sap.engine.services.dc.repo.SoftwareType;
import com.sap.engine.services.dc.util.lock.LockData;
import com.sap.engine.services.dc.util.logging.DeploymenItemStatusLogger;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-19
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public class DeployFactoryImpl extends DeployFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.deploy.DeployFactory#createDeployer()
	 */
	public Deployer createDeployer(final String performerUserUniqueId)
			throws RemoteException {
		return new DeployerImpl(performerUserUniqueId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeployFactory#createDeploymentItem
	 * (com.sap.engine.services.dc.repo.Sda, java.lang.String)
	 */
	public DeploymentItem createDeploymentItem(Sda sda, String sdaFilePath,
			boolean enableTimeStats) {

		DeploymentItemImpl item = new DeploymentItemImpl(sda, sdaFilePath,
				enableTimeStats);
		item.addDeploymentBatchItemObserver(DeploymenItemStatusLogger
				.getInstance());

		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeployFactory#createDeploymentItem
	 * (com.sap.engine.services.dc.repo.Sda, java.lang.String,
	 * com.sap.engine.services.dc.cm.deploy.BatchItemId)
	 */
	public DeploymentItem createDeploymentItem(Sda sda, String sdaFilePath,
			BatchItemId parentId, boolean enableTimeStats) {
		DeploymentItemImpl item = new DeploymentItemImpl(sda, sdaFilePath,
				parentId, enableTimeStats);
		item.addDeploymentBatchItemObserver(DeploymenItemStatusLogger
				.getInstance());

		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeployFactory#createDeploymentItem
	 * (com.sap.engine.services.dc.cm.deploy.BatchItemId, java.lang.String)
	 */
	public DeploymentItem createDeploymentItem(BatchItemId batchItemId,
			String sdaFilePath, boolean enableTimeStats) {
		DeploymentItemImpl item = new DeploymentItemImpl(batchItemId,
				sdaFilePath, enableTimeStats);
		item.addDeploymentBatchItemObserver(DeploymenItemStatusLogger
				.getInstance());

		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeployFactory#createDeploymentItem
	 * (com.sap.engine.services.dc.cm.deploy.BatchItemId, java.lang.String,
	 * com.sap.engine.services.dc.cm.deploy.BatchItemId)
	 */
	public DeploymentItem createDeploymentItem(BatchItemId batchItemId,
			String sdaFilePath, BatchItemId parentId, boolean enableTimeStats) {
		DeploymentItemImpl item = new DeploymentItemImpl(batchItemId,
				sdaFilePath, parentId, enableTimeStats);
		item.addDeploymentBatchItemObserver(DeploymenItemStatusLogger
				.getInstance());

		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.deploy.DeployFactory#
	 * createCompositeDeploymentItem(com.sap.engine.services.dc.repo.Sca,
	 * java.lang.String, java.util.Collection)
	 */
	public CompositeDeploymentItem createCompositeDeploymentItem(Sca sca,
			String scaFilePath, Collection deploymentItems,
			boolean enableTimeStats) {
		CompositeDeploymentItemImpl compItem = new CompositeDeploymentItemImpl(
				sca, scaFilePath, deploymentItems, enableTimeStats);
		compItem.addDeploymentBatchItemObserver(DeploymenItemStatusLogger
				.getInstance());
		return compItem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.deploy.DeployFactory#
	 * createCompositeDeploymentItem
	 * (com.sap.engine.services.dc.cm.deploy.BatchItemId, java.lang.String,
	 * java.util.Collection)
	 */
	public CompositeDeploymentItem createCompositeDeploymentItem(
			BatchItemId batchItemId, String scaFilePath,
			Collection deploymentItems, boolean enableTimeStats) {
		CompositeDeploymentItemImpl compItem = new CompositeDeploymentItemImpl(
				batchItemId, scaFilePath, deploymentItems, enableTimeStats);
		compItem.addDeploymentBatchItemObserver(DeploymenItemStatusLogger
				.getInstance());
		return compItem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeployFactory#createDeploymentBatch
	 * (java.util.Collection)
	 */
	public DeploymentBatch createDeploymentBatch(Collection deploymentBatchItems) {
		return new DeploymentBatchImpl(deploymentBatchItems);
	}

	public SafeModeDeployer createSafeModeDeployer(DeploymentData deploymentData)
			throws DeploymentException {
		return new SafeModeDeployerImpl(deploymentData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeployFactory#createDeploymentData
	 * (java.util.Collection,
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatch, java.lang.String,
	 * java.util.Collection, com.sap.engine.services.dc.cm.ErrorStrategies)
	 */
	public DeploymentData createDeploymentData(
			Collection sortedDeploymentBatchItem,
			DeploymentBatch deploymentBatch, String sessionId,
			Collection deploymentObservers, ErrorStrategy errorStrategy,
			DeployWorkflowStrategy deployStrategy,
			DeployParallelismStrategy deployParallelismStrategy,
			LifeCycleDeployStrategy lifeCycleDeployStrategy,
			DeployListenersList deployListenersList, String userUniqueId,
			String callerHost, String aDescription, boolean aTimeStatEnabled,
			Set<InstanceData> instancesData, LockData lockData,
			DataMeasurements dataMeasurements, String osUserName,
			String osUserPass) {
		return new DeploymentDataImpl(sortedDeploymentBatchItem,
				deploymentBatch, sessionId, deploymentObservers, errorStrategy,
				deployStrategy, deployParallelismStrategy,
				lifeCycleDeployStrategy, deployListenersList, userUniqueId,
				callerHost, aDescription, aTimeStatEnabled, instancesData,
				lockData, dataMeasurements, osUserName, osUserPass);
	}

	public BatchItemId createBatchItemId(SduId sduId) {
		return new BatchItemIdImpl(sduId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeployFactory#createValidationResult
	 * (com.sap.engine.services.dc.cm.deploy.ValidationStatus, boolean,
	 * java.util.Collection, java.util.Collection)
	 */
	public ValidationResult createValidationResult(
			ValidationStatus validationStatus, boolean offlinePhaseScheduled,
			Collection sortedDeploymentBatchItems,
			Collection deploymentBatchItems) {
		return new ValidationResultImpl(validationStatus,
				offlinePhaseScheduled, sortedDeploymentBatchItems,
				deploymentBatchItems);
	}

	public Syncer createSyncer() {
		return new SyncerImpl();
	}

	public SyncRequest createSyncRequest(int senderId, String transactionId,
			Collection<SyncItem> syncItems, boolean isOffline, long sessionId,
			int instanceId) {
		return new SyncRequestImpl(transactionId, senderId, syncItems,
				isOffline, sessionId, instanceId);
	}

	public SyncResult createSyncResult(SyncContext syncContext, int senderId,
			SyncException syncException) {
		return new SyncResultImpl(syncContext, senderId, syncException);
	}

	public InstanceData createInstanceData(int instanceId,
			SyncRequest syncRequest, boolean isProcessed) {
		return new InstanceDataImpl(instanceId, syncRequest, isProcessed);
	}

	public CompositeSyncItem createCompositeSyncItem(BatchItemId batchItemId,
			Collection<DeploymentSyncItem> deploymentSyncItems) {
		return new CompositeSyncItemImpl(batchItemId, deploymentSyncItems);
	}

	public DeploymentSyncItem createDeploymentSyncItem(BatchItemId batchItemId,
			RollingInfo rollingInfo, SoftwareType softwareType) {
		return new DeploymentSyncItemImpl(batchItemId, rollingInfo,
				softwareType);
	}

	public RollingMonitor getRollingMonitor() {
		return RollingMonitorImpl.getInstance();
	}

}
