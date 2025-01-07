package com.sap.engine.services.dc.cm.deploy.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.io.File;
import java.util.Iterator;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatch;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.cm.lock.DCLockException;
import com.sap.engine.services.dc.cm.lock.DCLockManager;
import com.sap.engine.services.dc.cm.lock.DCLockManagerFactory;
import com.sap.engine.services.dc.cm.lock.LockAction;
import com.sap.engine.services.dc.cm.lock.LockActionBuilder;
import com.sap.engine.services.dc.cm.lock.LockActionLocation;
import com.sap.engine.services.dc.manage.DCManager;
import com.sap.engine.services.dc.manage.DCState;
import com.sap.engine.services.dc.manage.PathsConfigurer;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-12-4
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class DeploymentArchivesCleaner {

	private  final Location location = DCLog.getLocation(this.getClass());
	
	private DeploymentArchivesCleaner() {
	}

	public static synchronized DeploymentArchivesCleaner getInstance() {
		return new DeploymentArchivesCleaner();
	}

	public void clean(String sessionId) {
		if (isCleanUpNeeded()) {
			final String batchUploadDirPath = PathsConfigurer.getInstance()
					.getUploadDirName(sessionId);
			if (location.bePath()) {
				tracePath(location, 
						"Starting to delete directory [{0}]",
						new Object[] { batchUploadDirPath });
			}

			FileUtils.deleteDirectory(new File(batchUploadDirPath));
			if (location.beDebug()) {
				DCLog.traceDebug(location, 
					"Directory [{0}] was successfully deleted",
					new Object[] { batchUploadDirPath });
			}
		}
	}

	void clean(DeploymentBatch deplBatch, String sessionId) {
		if (deplBatch != null && isCleanUpNeeded()) {
			final DeploymentBatchCleanUpVisitor cleanUpVisitor = DeploymentBatchCleanUpVisitor
					.createInstance();
			for (Iterator iter = deplBatch.getDeploymentBatchItems().iterator(); iter
					.hasNext();) {
				final DeploymentBatchItem item = (DeploymentBatchItem) iter
						.next();

				item.accept(cleanUpVisitor);
			}

			final String batchUploadDirPath = PathsConfigurer.getInstance()
					.getUploadDirName(sessionId);
			final File batchUploadDir = new File(batchUploadDirPath);
			if (batchUploadDir.exists() && batchUploadDir.isDirectory()) {
				final String[] dirFilesPaths = batchUploadDir.list();

				if (dirFilesPaths != null && dirFilesPaths.length == 0) {
					FileUtils.deleteDirectory(batchUploadDir);
				}
			}
		}
	}

	private boolean isCleanUpNeeded() {
		if (DCState.RESTARTING.equals(DCManager.getInstance().getDCState())) {
			if (location.bePath()) {
				DCLog
					.tracePath(location, 
							"Deploy Controller is in [{0}] state. Deployment batch files are not going to be deleted",
							new Object[] { DCState.RESTARTING });
			}
			return false;
		}

		final DCLockManager dcLockManager = DCLockManagerFactory.getInstance()
				.createDCLockManager();
		final LockActionLocation location1 = LockActionBuilder.getInstance()
				.buildSingleThread();
		try {
			final LockAction lockAction = dcLockManager.getDBLockAction(
					getConfigurationHandler(), location1);

			return lockAction == null ? true : false;
		} catch (DCLockException dcle) {// $JL-EXC$
			DCLog
					.logWarning(location, 
							"ASJ.dpl_dc.001063",
							"Lock problem occurred while checking whether the system has to delete all deployment batch files. [{0}]",
							new Object[] { dcle.getMessage() });

			return false;
		} catch (DeploymentException de) {// $JL-EXC$
			DCLog
					.logWarning(location, 
							"ASJ.dpl_dc.001064",
							"Deployment problem occurred while checking whether the system has to delete all deployment batch files. [{0}]",
							new Object[] { de.getMessage() });

			return false;
		}
	}

	private ConfigurationHandler getConfigurationHandler()
			throws DeploymentException {
		try {
			return ServiceConfigurer.getInstance().getConfigurationHandler();
		} catch (Exception e) {
			DeploymentException de = new DeploymentException(
					"An error occurred while getting a "
							+ "Configuration Handler from the Deployer.", e);
			de.setMessageID("ASJ.dpl_dc.003048");
			throw de;
		}
	}

}
