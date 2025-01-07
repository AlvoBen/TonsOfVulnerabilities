package com.sap.engine.services.dc.cm.deploy.impl;

import com.sap.engine.services.dc.util.logging.DCLog;

import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.cm.deploy.LifeCycleDeployStrategy;
import com.sap.engine.services.dc.cm.deploy.storage.DeplDataStorageException;
import com.sap.engine.services.dc.cm.deploy.storage.DeploymentDataStorageFactory;
import com.sap.engine.services.dc.cm.deploy.storage.DeploymentDataStorageManager;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-12
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class AdmittedSafeDeplProcessor extends
		AbstractDeplStatusSafeDeplProcessor {

	// private final DependingDeliveredChecker dependingDeliveredChecker
	// = new DependingDeliveredChecker();

	private ProcessorObserver processorObserver;

	private static final Location location = 
		DCLog.getLocation(AdmittedSafeDeplProcessor.class);
	
	AdmittedSafeDeplProcessor() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.impl.AbstractDeplStatusSafeDeplProcessor
	 * #process(com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem,
	 * java.util.Enumeration,
	 * com.sap.engine.services.dc.cm.deploy.DeploymentData,
	 * com.sap.engine.services.dc.repo.Repository)
	 */
	void process(final DeploymentBatchItem item,
			final DeploymentData deploymentData) throws DeploymentException {
		if (location.beDebug()) {
			DCLog.traceDebug(
					location,
					"Admitted Safe Deploy Processor processes component [{0}]",
					new Object[] { item });
		}

		final DeployPhase deployPhase = DeployPhaseGetter.createInstance()
				.getPhase(item);

		DCLog.logInfo(location, "ASJ.dpl_dc.001016", "Deploying component [{0}]",
				new Object[] { item.getBatchItemId() });

		doDeploy(item, deploymentData, deployPhase);
	}

	private void doDeploy(final DeploymentBatchItem item,
			final DeploymentData deploymentData, final DeployPhase deployPhase)
			throws DeploymentException {
		if (item.getSdu() == null) {
			DeploymentException de = new DeploymentException(
					"The safe mode deployment "
							+ "could not be performed for the component '"
							+ item
							+ "'. The deployment does not contain SDU. Solution: Please, perform "
							+ "the deployment process again.");
			de.setMessageID("ASJ.dpl_dc.003029");
			throw de;
		}

		final AbstractDeployProcessor deployProcessor = 
			DeployProcessorMapper.getInstance().map(deploymentData.getLifeCycleDeployStrategy());

		ProcessorObserver observer = null;

		// TODO move this logic in the post processor observer itself
		if (((DeployPhase.ONLINE.equals(deployPhase) || DeployPhase.POST_ONLINE
				.equals(deployPhase)) && !LifeCycleDeployStrategy.BULK
				.equals(deploymentData.getLifeCycleDeployStrategy()))
				|| (DeployPhase.OFFLINE.equals(deployPhase))) {

			observer = getProcessorObserver();
			deployProcessor.addDeployProcessorObserver(observer);

		}

		try {
			deployProcessor.deploy(item, deploymentData, null);
		} finally {
			if (observer != null) {
				deployProcessor.removeDeployProcessorObserver(observer);
			}

		}

	}

	private synchronized ProcessorObserver getProcessorObserver() {
		if (this.processorObserver == null) {
			this.processorObserver = new ProcessorObserver();
		}

		return this.processorObserver;
	}

	private static class ProcessorObserver implements DeployProcessorObserver {

		private final DeploymentDataStorageManager storageManager;

		ProcessorObserver() {
			this.storageManager = DeploymentDataStorageFactory.getInstance()
					.createDeploymentDataStorageManager(
							ServiceConfigurer.getInstance()
									.getConfigurationHandlerFactory());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.deploy.impl.DeployProcessorObserver
		 * #deployPerformed
		 * (com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem,
		 * com.sap.engine.services.dc.cm.deploy.DeploymentData)
		 */
		public void deployPerformed(DeploymentBatchItem deplBatchItem,
				DeploymentData deploymentData) throws DeploymentException {
			if (location.bePath()) {
				DCLog.tracePath(location,
						"Persisting component [{0}] into temporary deployment data",
						new Object[] { deplBatchItem.getBatchItemId() });
			}
			try {
				this.storageManager.persist(deploymentData.getSessionId(),
						deplBatchItem);
			} catch (DeplDataStorageException ddse) {
				DeploymentException de = new DeploymentException(
						"An error occurred while persisting the "
								+ "item '" + deplBatchItem + "'.", ddse);
				de.setMessageID("ASJ.dpl_dc.003030");
				throw de;
			}

			DCLog.logInfo(location, 
					"ASJ.dpl_dc.001018",
					"Deployment of component [{0}] finished. Item status is [{1}]",
					new Object[] { deplBatchItem.getBatchItemId(),
							deplBatchItem.getDeploymentStatus() });
		}

	}

}
