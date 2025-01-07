package com.sap.engine.services.dc.cm.deploy.impl;

import static com.sap.engine.services.dc.util.RollingUtils.sendMessage;

import java.util.Collection;
import java.util.Iterator;

import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.services.dc.cm.deploy.CompositeSyncItem;
import com.sap.engine.services.dc.cm.deploy.DCNotAvailableException;
import com.sap.engine.services.dc.cm.deploy.DeployFactory;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.cm.deploy.DeploymentSyncItem;
import com.sap.engine.services.dc.cm.deploy.InstanceData;
import com.sap.engine.services.dc.cm.deploy.SyncException;
import com.sap.engine.services.dc.cm.deploy.SyncItem;
import com.sap.engine.services.dc.cm.deploy.SyncItemException;
import com.sap.engine.services.dc.cm.deploy.SyncItemVisitor;
import com.sap.engine.services.dc.cm.deploy.SyncRequest;
import com.sap.engine.services.dc.cm.deploy.SyncResult;
import com.sap.engine.services.dc.cm.deploy.Syncer;
import com.sap.engine.services.dc.cm.deploy.storage.DeplDataStorageException;
import com.sap.engine.services.dc.cm.deploy.storage.DeplDataStorageNotFoundException;
import com.sap.engine.services.dc.cm.deploy.storage.DeploymentDataStorageManager;
import com.sap.engine.services.dc.cm.dscr.InstanceDescriptor;
import com.sap.engine.services.dc.cm.lock.DCAlreadyLockedException;
import com.sap.engine.services.dc.cm.lock.DCLockException;
import com.sap.engine.services.dc.cm.lock.DCLockManager;
import com.sap.engine.services.dc.cm.lock.DCLockManagerFactory;
import com.sap.engine.services.dc.cm.lock.DCLockNotFoundException;
import com.sap.engine.services.dc.cm.lock.LockAction;
import com.sap.engine.services.dc.cm.server.Server;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.ServerService;
import com.sap.engine.services.dc.cm.server.spi.RestartServerService;
import com.sap.engine.services.dc.cm.server.spi.RestartServerService.RestartServerServiceException;
import com.sap.engine.services.dc.gd.GDFactory;
import com.sap.engine.services.dc.gd.RollingDeliveryException;
import com.sap.engine.services.dc.gd.SyncDelivery;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.manage.messaging.MessageConstants;
import com.sap.engine.services.dc.manage.messaging.MessagingException;
import com.sap.engine.services.dc.repo.RepositoryException;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

class SyncerImpl extends DeployerBase implements Syncer {

	private  final Location location = DCLog.getLocation(this.getClass());
	
	protected SyncerImpl() {
	}

	private void syncTransaction(SyncRequest syncRequest) throws SyncException {
		String transactionId = syncRequest.getSyncContext().getTransactionId();
		try {
			checkForAvailablility();
		} catch (DCNotAvailableException e) {
			throw new SyncException("Sync operation for transaction: "
					+ transactionId + " failed: ", e);
		}
		boolean hasOfflineDeployment = syncRequest.isOffline();

		ConfigurationHandlerFactory cfgFactory;
		DeploymentDataStorageManager ddsManager;
		try {
			cfgFactory = getConfigurationHandlerFactory();
			ddsManager = getDeploymentDataStorageManager(cfgFactory);
		} catch (DeploymentException e) {
			throw new SyncException("Sync operation for transaction: "
					+ transactionId + " failed: ", e);
		}

		// make rolling patch batch
		Collection<SyncItem> syncItemsForCommit = syncRequest.getSyncItems();
		if (hasOfflineDeployment) {
			final DCLockManager lock = DCLockManagerFactory.getInstance()
					.createDCLockManager();
			final LockAction lockAction = LockAction.SYNC_PROCESS;
			int instanceId = ServiceConfigurer.getInstance()
					.getClusterMonitor().getCurrentParticipant().getGroupId();
			InstanceData instanceData = DeployFactory.getInstance()
					.createInstanceData(instanceId, syncRequest, false);
			try {
				lock.lockEnqueueForCurrentInstance(lockAction);
			} catch (DCAlreadyLockedException e) {
				throw new SyncException("Sync operation for transaction: "
						+ transactionId + " failed: ", e);
			} catch (DCLockException e) {
				throw new SyncException("Sync operation for transaction: "
						+ transactionId + " failed: ", e);
			}
			try {
				ddsManager.persistInstanceData(transactionId, instanceData);
			} catch (DeplDataStorageNotFoundException e) {
				throw new SyncException("Sync operation for transaction: "
						+ transactionId + " failed: ", e);
			} catch (DeplDataStorageException e) {
				throw new SyncException("Sync operation for transaction: "
						+ transactionId + " failed: ", e);
			} catch (RepositoryException e) {
				throw new SyncException("Sync operation for transaction: "
						+ transactionId + " failed: ", e);
			}

			final Server server = ServerFactory.getInstance().createServer();
			final ServerService serverService = server
					.getServerService(ServerFactory.getInstance()
							.createRestartServerRequest());
			if (serverService == null
					|| !(serverService instanceof RestartServerService)) {
				final String errMsg = "ASJ.dpl_dc.003064 Received ServerService for restarting the server "
						+ "which is not of type RestartServerService.";
				throw new SyncException(errMsg);
			}

			final RestartServerService restartServerService = (RestartServerService) serverService;
			try {
				restartServerService.restartCurrentInstance();
			} catch (RestartServerServiceException e) {
				SyncException se = new SyncException(
						"An error occurred while restarting the server.",
						e);
				se.setMessageID("ASJ.dpl_dc.003560");
				throw se;
			} finally {
				try {
					lock.unlockEnqueueForCurrentInstance(lockAction);
				} catch (DCLockNotFoundException e) {
					String msg = DCLog
							.buildExceptionMessage(
									"ASJ.dpl_dc.001116",
									"Instance lock [{0}] cannot be removed during Commit operation.",
									new Object[] { lockAction });
					DCLog.logErrorThrowable(location, null, msg, e);
				} catch (DCLockException e) {
					String msg = DCLog
							.buildExceptionMessage(
									"ASJ.dpl_dc.001117",
									"Instance lock [{0}] cannot be removed during Commit operation.",
									new Object[] { lockAction });
					DCLog.logErrorThrowable(location, null, msg, e);
				}
			}
			SyncException se = new SyncException(
					" Sync operation for transaction: "
							+ transactionId
							+ " failed: An error occurred while restarting the server. ");
			se.setMessageID("ASJ.dpl_dc.003660");
			throw se;
		} else {
			Iterator itr = syncItemsForCommit.iterator();
			SyncDelivery syncDelivery = GDFactory.getInstance()
					.createSyncDelivery();
			SyncHelper syncHelper = new SyncHelper(syncDelivery, ddsManager,
					transactionId);
			while (itr.hasNext()) {
				SyncItem deploymentBatchItem = (SyncItem) itr.next();
				deploymentBatchItem.accept(syncHelper);
				syncHelper.checkException();
			}
		}
	}

	private static class SyncHelper implements SyncItemVisitor {

		private final SyncDelivery syncDelivery;
		private SyncItemException syncItemException;
		private final DeploymentDataStorageManager ddsManager;
		private final String sessionId;

		private SyncHelper(SyncDelivery syncDelivery,
				DeploymentDataStorageManager ddsManager, String sessionId) {
			this.syncDelivery = syncDelivery;
			this.ddsManager = ddsManager;
			this.sessionId = sessionId;
		}

		void checkException() throws SyncItemException {
			if (this.syncItemException != null) {
				throw this.syncItemException;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
		 * (com.sap.engine.services.dc.cm.deploy.DeploymentItem)
		 */
		public void visit(DeploymentSyncItem deploymentSyncItem) {
			this.syncItemException = null;

			try {
				InstanceDescriptor instanceDescriptor = syncDelivery
						.sync(deploymentSyncItem);
				ddsManager.persistInstanceDescriptor(sessionId,
						deploymentSyncItem, instanceDescriptor);
			} catch (RollingDeliveryException e) {
				this.syncItemException = new SyncItemException(
						"Sync operation for transaction: " + sessionId
								+ " failed: ", e, deploymentSyncItem);
			} catch (DeplDataStorageNotFoundException e) {
				this.syncItemException = new SyncItemException(
						"Sync operation for transaction: " + sessionId
								+ " failed: ", e, deploymentSyncItem);
			} catch (DeplDataStorageException e) {
				this.syncItemException = new SyncItemException(
						"Sync operation for transaction: " + sessionId
								+ " failed: ", e, deploymentSyncItem);
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
		 * (com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem)
		 */
		public void visit(CompositeSyncItem compositeSyncItem) {
			this.syncItemException = null;

			try {
				InstanceDescriptor instanceDescriptor = syncDelivery
						.sync(compositeSyncItem);
				ddsManager.persistInstanceDescriptor(sessionId,
						compositeSyncItem, instanceDescriptor);
			} catch (RollingDeliveryException e) {
				this.syncItemException = new SyncItemException(
						"Sync operation for transaction: " + sessionId
								+ " failed: ", e, compositeSyncItem);
			} catch (DeplDataStorageNotFoundException e) {
				this.syncItemException = new SyncItemException(
						"Sync operation for transaction: " + sessionId
								+ " failed: ", e, compositeSyncItem);
			} catch (DeplDataStorageException e) {
				this.syncItemException = new SyncItemException(
						"Sync operation for transaction: " + sessionId
								+ " failed: ", e, compositeSyncItem);
			}
		}

	}

	public void syncInstance(SyncRequest syncRequest) {
		SyncResult result;
		final int sendTo = syncRequest.getSenderId();
		final int sernderId = ServiceConfigurer.getInstance()
				.getClusterMonitor().getCurrentParticipant().getClusterId();
		try {
			try {
				syncTransaction(syncRequest);
				result = DeployFactory.getInstance().createSyncResult(
						syncRequest.getSyncContext(), sernderId, null);
			} catch (SyncException e) {
				result = DeployFactory.getInstance().createSyncResult(
						syncRequest.getSyncContext(), sernderId, e);
			}
			sendMessage(sendTo, result,
					MessageConstants.MSG_TYPE_ROLLING_EVENT_SYNCED);
		} catch (MessagingException e) {
			DCLog.logErrorThrowable(location, e);
		}
	}

}
