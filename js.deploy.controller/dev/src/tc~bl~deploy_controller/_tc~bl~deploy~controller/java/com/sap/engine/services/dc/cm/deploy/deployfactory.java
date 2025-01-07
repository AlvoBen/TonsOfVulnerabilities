package com.sap.engine.services.dc.cm.deploy;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Set;

import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.dscr.RollingInfo;
import com.sap.engine.services.dc.cm.utils.measurement.DataMeasurements;
import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SduId;
import com.sap.engine.services.dc.repo.SoftwareType;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.lock.LockData;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: Software Deployment Manager Description: The class acts as a factory
 * for objects of type Deployer.
 * 
 * @see com.sap.engine.services.dc.cm.deploy.Deployer Copyright: Copyright (c)
 *      2003 Company: SAP AG Date: 2004-3-19
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public abstract class DeployFactory {

	private static final Location location = 
		DCLog.getLocation(DeployFactory.class);

	private static DeployFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.deploy.impl.DeployFactoryImpl";

	protected DeployFactory() {
	}

	/**
	 * @return the object reference for the factory. The class is implemented as
	 *         a Singleton.
	 */
	public static synchronized DeployFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static DeployFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (DeployFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = DCLog
					.buildExceptionMessage(
							"ASJ.dpl_dc.001145",
							"An error occurred while creating an instance of class DeployFactory{0}{1}",
							new Object[] { Constants.EOL, e.getMessage() });

			DCLog.logErrorThrowable(location, null, errMsg, e);
			throw new RuntimeException(errMsg);
		}
	}

	/**
	 * @return a new <code>Deployer</code>.
	 * @throws RemoteException
	 */
	public abstract Deployer createDeployer(String performerUserUniqueId)
			throws RemoteException;

	public abstract DeploymentItem createDeploymentItem(Sda sda,
			String sdaFilePath, boolean enableTimeStats);

	public abstract DeploymentItem createDeploymentItem(Sda sda,
			String sdaFilePath, BatchItemId parentId, boolean enableTimeStats);

	public abstract DeploymentItem createDeploymentItem(
			BatchItemId batchItemId, String sdaFilePath, boolean enableTimeStats);

	public abstract DeploymentItem createDeploymentItem(
			BatchItemId batchItemId, String sdaFilePath, BatchItemId parentId,
			boolean enableTimeStats);

	public abstract CompositeDeploymentItem createCompositeDeploymentItem(
			Sca sca, String scaFilePath, Collection deploymentItems,
			boolean enableTimeStats);

	public abstract CompositeDeploymentItem createCompositeDeploymentItem(
			BatchItemId batchItemId, String scaFilePath,
			Collection deploymentItems, boolean enableTimeStats);

	public abstract DeploymentBatch createDeploymentBatch(
			Collection deploymentBatchItems);

	/**
	 * @return a new <code>DeploymentData</code>.
	 */
	public abstract DeploymentData createDeploymentData(
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
			String osUserPass);

	public abstract SafeModeDeployer createSafeModeDeployer(
			DeploymentData deploymentData) throws DeploymentException;

	public abstract BatchItemId createBatchItemId(SduId sduId);

	public abstract ValidationResult createValidationResult(
			ValidationStatus validationStatus, boolean offlinePhaseScheduled,
			Collection sortedDeploymentBatchItems,
			Collection deploymentBatchItems);

	public abstract Syncer createSyncer();

	public abstract SyncRequest createSyncRequest(int senderId,
			String transactionId, Collection<SyncItem> syncItems,
			boolean isOffline, long sessionId, int instanceId);

	public abstract SyncResult createSyncResult(SyncContext syncContext,
			int senderId, SyncException syncException);

	public abstract InstanceData createInstanceData(int instanceId,
			SyncRequest syncRequest, boolean isProcessed);

	public abstract DeploymentSyncItem createDeploymentSyncItem(
			BatchItemId batchItemId, RollingInfo rollingInfo,
			SoftwareType softwareType);

	public abstract CompositeSyncItem createCompositeSyncItem(
			BatchItemId batchItemId,
			Collection<DeploymentSyncItem> deploymentSyncItems);

	public abstract RollingMonitor getRollingMonitor();
}
