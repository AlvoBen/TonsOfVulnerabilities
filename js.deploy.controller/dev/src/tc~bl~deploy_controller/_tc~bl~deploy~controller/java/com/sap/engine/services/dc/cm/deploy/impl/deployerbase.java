package com.sap.engine.services.dc.cm.deploy.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DCNotAvailableException;
import com.sap.engine.services.dc.cm.deploy.DeployFactory;
import com.sap.engine.services.dc.cm.deploy.DeployResult;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor;
import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.deploy.DeploymentSyncItem;
import com.sap.engine.services.dc.cm.deploy.RollingDeployException;
import com.sap.engine.services.dc.cm.deploy.SyncItem;
import com.sap.engine.services.dc.cm.deploy.ValidationException;
import com.sap.engine.services.dc.cm.deploy.storage.DeplDataStorageException;
import com.sap.engine.services.dc.cm.deploy.storage.DeplDataStorageNotFoundException;
import com.sap.engine.services.dc.cm.deploy.storage.DeploymentDataStorageFactory;
import com.sap.engine.services.dc.cm.deploy.storage.DeploymentDataStorageManager;
import com.sap.engine.services.dc.cm.dscr.ClusterDescriptor;
import com.sap.engine.services.dc.cm.dscr.ClusterStatus;
import com.sap.engine.services.dc.manage.DCManager;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.dc.util.exception.DCResourceAccessor;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.DCLogConstants;

public abstract class DeployerBase {

	/**
	 * Checks if the DC is ready to server
	 * 
	 * @throws DCNotAvailableException
	 *             if DC performs some operation after the offline phase or the
	 *             repo is still initializing
	 */
	public static void checkForAvailablility() throws DCNotAvailableException {
		if (!DCManager.getInstance().isInWorkingMode()) {
			DCNotAvailableException dnae = new DCNotAvailableException( DCResourceAccessor.getInstance().getMessageText(
							DCExceptionConstants.DC_NOT_AVAILABLE_YET));
			dnae.setMessageID("ASJ.dpl_dc.003047");
			throw dnae;
		}
	}

	protected DeploymentData getDeploymentData(String sessionId)
			throws DeploymentException, DeplDataStorageNotFoundException,
			DeplDataStorageException {
		doCheckSessionId(sessionId);

		final ConfigurationHandlerFactory cfgFactory = getConfigurationHandlerFactory();
		final DeploymentDataStorageManager ddsManager = getDeploymentDataStorageManager(cfgFactory);

		final DeploymentData dData;
		dData = ddsManager.loadFullDeploymentData(sessionId);

		return dData;
	}

	public static void doCheckSessionId(String sessionId)
			throws ValidationException {
		if (sessionId == null) {
			ValidationException ve =  new ValidationException(
					"The sessionId could not be null.");
			ve.setMessageID("ASJ.dpl_dc.003040");
			throw ve;
		}

		try {
			final long sessionIdAsLong = Long.parseLong(sessionId);
			if (sessionIdAsLong < 0) {
				ValidationException ve = new ValidationException(
						"The specified session id '"
								+ sessionId + "' could not be negative long.");
				ve.setMessageID("ASJ.dpl_dc.003041");
				throw ve;
			}
		} catch (NumberFormatException nfe) {
			// $JL-EXC$
			ValidationException ve = new ValidationException(
					"The specified session id '"
							+ sessionId + "' is not a valid long.");
			ve.setMessageID("ASJ.dpl_dc.003042");
			throw ve;
		}
	}

	public static void doCheckArchivePaths(String[] archiveFilePathNames) {

		if (archiveFilePathNames == null) {
			throw new IllegalArgumentException(
					"ASJ.dpl_dc.003039 The specified array with archive file paths could not be null");
		}
		if (archiveFilePathNames.length == 0) {
			throw new IllegalArgumentException(
					"ASJ.dpl_dc.003035 The specified array with archive file paths could not be zero length.");
		}

		for (String pathName : archiveFilePathNames) {
			/** @TODO add logging for these error conditions */
			if (pathName == null) {
				throw new IllegalArgumentException(
						"ASJ.dpl_dc.003432 The specified archive path names contains a null entry.");
			}
			if (pathName.trim().length() == 0) {
				throw new IllegalArgumentException(
						"ASJ.dpl_dc.003433 The specified archive path names contain an empty entry.");
			}

			File file = new File(pathName);
			if (!file.exists()) {
				throw new IllegalArgumentException(
						"ASJ.dpl_dc.003434 The specified file does not exist: "
								+ file);
			}
			if (!file.isFile()) {
				throw new IllegalArgumentException(
						"ASJ.dpl_dc.003435 The specified pathname is not a file: "
								+ file);
			}
			if (!file.canRead()) {
				throw new IllegalArgumentException(
						"ASJ.dpl_dc.003436 The specified file cannot be read: "
								+ file);
			}
		}
	}

	protected ConfigurationHandlerFactory getConfigurationHandlerFactory()
			throws DeploymentException {
		try {
			return ServiceConfigurer.getInstance()
					.getConfigurationHandlerFactory();
		} catch (Exception e) {
			DeploymentException de = new DeploymentException(
					"An error occurred while getting a "
							+ "Configuration Handler Factory from the Deployer.",
					e);
			de.setMessageID("ASJ.dpl_dc.003045");
			throw de;
		}
	}

	protected DeploymentDataStorageManager getDeploymentDataStorageManager(
			ConfigurationHandlerFactory cfgFactory) throws DeploymentException {
		final DeploymentDataStorageManager ddsManager;
		try {
			ddsManager = DeploymentDataStorageFactory.getInstance()
					.createDeploymentDataStorageManager(cfgFactory);
		} catch (Exception e) {
			DeploymentException de = new DeploymentException(
					"An error occurred while getting a "
							+ "Deployment Data Storage Factory from the Deployer.",
					e);
			de.setMessageID("ASJ.dpl_dc.003046");
			throw de;
		}
		return ddsManager;
	}

	protected Collection<SyncItem> getRollingPatchItems(
			Collection sortedDeploymentBatchItems,
			Set<ClusterStatus> clusterStatuses) throws RollingDeployException {
		Collection<SyncItem> sortedItems = new ArrayList<SyncItem>();
		Iterator sortedItemsItr = sortedDeploymentBatchItems.iterator();
		SyncItemMaker syncerHelper = new SyncItemMaker();
		Collection wrongItems = new ArrayList();
		while (sortedItemsItr.hasNext()) {
			DeploymentBatchItem item = (DeploymentBatchItem) sortedItemsItr
					.next();
			ClusterDescriptor clusterDescriptor = item.getClusterDescriptor();
			if (clusterDescriptor != null) {
				if (clusterStatuses.contains(clusterDescriptor
						.getClusterStatus())) {
					item.accept(syncerHelper);
					sortedItems.add(syncerHelper.getSyncItem());
				} else {
					wrongItems.add(item);
				}
			}
		}
		if (!wrongItems.isEmpty()) {
			RollingDeployException rde = new RollingDeployException(
					"The rolling update phase has been interrupted. The cluster descriptor status of all items should belong to "
							+ clusterStatuses
							+ " set. But the items below have a different status: \n"
							+ wrongItems);
			rde.setMessageID("ASJ.dpl_dc.003460");
			throw rde;
		}
		return sortedItems;
	}

	private class SyncItemMaker implements DeploymentBatchItemVisitor {
		private SyncItem syncItem = null;
		private DeployFactory deployFactory;

		SyncItemMaker() {
			deployFactory = DeployFactory.getInstance();
		};

		SyncItem getSyncItem() {
			return syncItem;
		}

		public void visit(DeploymentItem item) {
			syncItem = deployFactory.createDeploymentSyncItem(item
					.getBatchItemId(), item.getClusterDescriptor()
					.getRollingInfo(), item.getSda().getSoftwareType());
		}

		public void visit(CompositeDeploymentItem item) {
			Collection<DeploymentSyncItem> deploymentSuncItems = new ArrayList<DeploymentSyncItem>();
			Collection deploymentItems = item.getDeploymentItems();
			if ((deploymentItems != null) && (deploymentItems.size() > 0)) {
				Iterator itr = deploymentItems.iterator();
				while (itr.hasNext()) {
					DeploymentItem deploymentItem = (DeploymentItem) itr.next();
					DeploymentSyncItem deploymentSyncItem = deployFactory
							.createDeploymentSyncItem(deploymentItem
									.getBatchItemId(), deploymentItem
									.getClusterDescriptor().getRollingInfo(),
									deploymentItem.getSda().getSoftwareType());
					deploymentSuncItems.add(deploymentSyncItem);
				}
			}
			syncItem = deployFactory.createCompositeSyncItem(item
					.getBatchItemId(), deploymentSuncItems);
		}

	}
}
