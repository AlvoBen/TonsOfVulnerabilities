package com.sap.engine.services.dc.cm.deploy.impl;

import static com.sap.engine.services.dc.util.StringBuilderUtils.concat;
import com.sap.engine.services.dc.util.logging.DCLog;

import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.security.auth.Subject;

import com.sap.engine.frame.core.thread.execution.Executor;
import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.accounting.measurement.AMeasurement;
import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.cm.deploy.DeploymentObserver;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.deploy.SafeModeDeployer;
import com.sap.engine.services.dc.cm.deploy.storage.DeplDataStorageException;
import com.sap.engine.services.dc.cm.deploy.storage.DeploymentDataStorageFactory;
import com.sap.engine.services.dc.cm.deploy.storage.DeploymentDataStorageManager;
import com.sap.engine.services.dc.cm.server.spi.OnlineOfflineSoftwareType;
import com.sap.engine.services.dc.cm.utils.SecurityUtil;
import com.sap.engine.services.dc.cm.utils.measurement.DMeasurement;
import com.sap.engine.services.dc.cm.utils.measurement.MeasurementUtils;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.CollectionEnumerationMapper;
import com.sap.engine.services.dc.util.ThreadUtil;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-11
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.0
 * 
 */
final class SafeModeDeployerImpl implements SafeModeDeployer {

	private static final Location location = 
		DCLog.getLocation(SafeModeDeployerImpl.class);
	
	// private final SoftwareTypeAdmittedChecker swtTypeAdmittedChecker;
	private PostProcessorObserver postProcessorObserver;
	private DeploymentData deploymentData;
	private DeploymentDataStorageManager deplDataStorageManager;

	SafeModeDeployerImpl(DeploymentData deploymentData) {
		init(deploymentData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.SafeModeDeployer#deployOfflineData()
	 */
	public void deployOfflineData() throws DeploymentException {
		doProcessDeploymentData(this.deploymentData,
				OnlineOfflineSoftwareType.OFFLINE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.SafeModeDeployer#deployOnlineData()
	 */
	public void deployOnlineData() throws DeploymentException {
		final String tagName = "Online";
		Accounting.beginMeasure(tagName, SafeModeDeployerImpl.class);
		try {
			doProcessDeploymentData(this.deploymentData,
					OnlineOfflineSoftwareType.ONLINE);
		} finally {
			final AMeasurement onlineMeasurement = Accounting
					.endMeasure(tagName);
			setMeasurementInDeploymentData(onlineMeasurement,
					DeployPhase.ONLINE);
			// Always perform post process operation
			doPostProcess(this.deploymentData, DeployPhase.ONLINE);
		}
	}

	private void setMeasurementInDeploymentData(
			final AMeasurement onlineMeasurement, final DeployPhase deployPhase)
			throws DeploymentException {
		if (this.deploymentData == null) {
			return;
		}
		final DMeasurement dOnlineMeasurement = MeasurementUtils
				.map(onlineMeasurement);
		if (DeployPhase.ONLINE.equals(deployPhase)) {
			this.deploymentData.getMeasurements().setOnlineMeasurement(
					dOnlineMeasurement);
		} else if (DeployPhase.POST_ONLINE.equals(deployPhase)) {
			this.deploymentData.getMeasurements().setPostOnlineMeasurement(
					dOnlineMeasurement);
		} else {
			throw new DeploymentException(
					"Internal error: no set method for phase: [" + deployPhase
							+ "]");
		}

	}

	public void deployPostOnlineData(List<DeploymentBatchItem> postOnlines)
			throws DeploymentException {
		final String tagName = "PostOnline";
		Accounting.beginMeasure(tagName, SafeModeDeployerImpl.class);
		try {
			doProcessDeploymentData(this.deploymentData,
					OnlineOfflineSoftwareType.POST_ONLINE);
		} finally {
			final AMeasurement onlineMeasurement = Accounting
					.endMeasure(tagName);
			setMeasurementInDeploymentData(onlineMeasurement,
					DeployPhase.POST_ONLINE);
			// Always perform post process operation
			doPostProcess(this.deploymentData, DeployPhase.POST_ONLINE);
		}
	}

	public void finalizeDeployment() {
		this.deplDataStorageManager.persistMeasurements(this.deploymentData);
		this.deplDataStorageManager.persistTimeStats(this.deploymentData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.deploy.SafeModeDeployer#clearData()
	 */
	public void clearData() {
		this.deploymentData.clear();
	}

	private void init(DeploymentData _deploymentData) {
		if (_deploymentData == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003088 The loaded deployment is null. The system "
							+ "could not perform the safe mode deployment.");
		}

		this.deploymentData = _deploymentData;

		attachDeplObservers(this.deploymentData);
	}

	private void doPostProcess(final DeploymentData deplData,
			final DeployPhase phase) throws DeploymentException {
		final AbstractDeployPostProcessor postProcessor = DeployPostProcessorMapper
				.getInstance().safeMap(phase);

		postProcessor
				.addDeployPostProcessorObserver(getPostProcessorObserver());

		postProcessor.postProcess(deplData);
	}

	/**
	 * 
	 * @param deplData
	 * @param onOffSoftwareType
	 *            determine the current deploy phase: OFFLINE: just the offline
	 *            SDAs and the SCAs immediately after them should be processed
	 *            ONLINE: just the online SDAs and the SCAs immediately after
	 *            them should be processed POST_ONLINE: only the post online
	 *            SDAs and the SCAs immediately after them should be processed
	 * @throws DeploymentException
	 */
	private void doProcessDeploymentData(final DeploymentData deplData,
			final OnlineOfflineSoftwareType onOffSoftwareType)
			throws DeploymentException {

		final String tagName = "Process deploment data:" + onOffSoftwareType;
		Accounting.beginMeasure(tagName, SafeModeDeployerImpl.class);
		try {
			final DeploymentParallelTraverser parallelTraverser = getDeploymentParallelTraverser(
					deplData, CollectionEnumerationMapper.map(deplData
							.getSortedDeploymentBatchItem()), onOffSoftwareType);
			if (1 >= ServiceConfigurer.getInstance().getDeployThreadsNumber()) {
				performDeploymentInCurrentThread(deplData, parallelTraverser);
			} else {
				performDeployInExecutor(deplData, parallelTraverser);
			}
		} finally {
			Accounting.endMeasure(tagName);
		}
	}

	private void performDeploymentInCurrentThread(
			final DeploymentData deploymentData,
			final DeploymentParallelTraverser parallelTraverser)
			throws DeploymentException {
		DeploymentBatchItem dbItem;
		DeploymentException deploymentException = null;
		while ((dbItem = parallelTraverser.nextElement()) != null) {
			deploymentException = null;
			final AbstractDeplStatusSafeDeplProcessor deplStatusDeplProcessor = DeplStatusSafeDeplProcessorMapper
					.getInstance().map(dbItem.getDeploymentStatus());

			try {
				deplStatusDeplProcessor.process(dbItem, deploymentData);
			} catch (final DeploymentException de) {
				deploymentException = de;
			} catch (OutOfMemoryError e) { // OOM, ThreadDeath and Internal error
				// are not consumed
				throw e;

			} catch (ThreadDeath e) {
				throw e;

			} catch (InternalError e) {
				throw e;

			} catch (Throwable t) { // // catch all the rest of the throwables and
				// wrap them in a deployment exception to
				// improve error reporting
				deploymentException = new DeploymentException(
						"[ERROR CODE DPL.DC.3475] Unexpected throwable occured during the deployment operation",
						t);

			} finally {
				parallelTraverser.notifyPerformed(dbItem, deploymentException);
			}
		}
		parallelTraverser.getDeploymentException();
	}

	private void performDeployInExecutor(final DeploymentData deplData,
			final DeploymentParallelTraverser parallelTraverser)
			throws DeploymentException {
		final Subject userSubject = SecurityUtil.getUserSubject(deplData
				.getUserUniqueId());
		final Executor deployExecutor = getDeployExecutor();
		DeploymentBatchItem dbItem;
		final String threadNamePrefix = "DeployThread[", threadNameSufix = "]", deployOperation = "deploy";
		final CountDownLatch maxThreadDumpsCount = new CountDownLatch(10);
		while ((dbItem = parallelTraverser.nextElement()) != null) {
			final String threadName = concat(threadNamePrefix, dbItem
					.getBatchItemId().toString(), threadNameSufix);
			final String threadTask = ThreadUtil.evaluateTaskMessage(
					deployOperation, dbItem.getSdu().getName(), dbItem.getSdu()
							.getVendor());
			// construct deploy thread and execute it
			final Runnable deployRunnable = new PostDeployThread(dbItem,
					parallelTraverser, deploymentData, userSubject, maxThreadDumpsCount);
			deployExecutor.execute(deployRunnable, threadTask, threadName);
		}

		try {
			parallelTraverser.await();
		} catch (final InterruptedException ie) {
			throw new DeploymentException(
					"Error occurs while processing threads ", ie);
		}

		parallelTraverser.getDeploymentException();
	}

	private Executor getDeployExecutor() {
		return ServiceConfigurer.getInstance().getParallelDeployExecutor();
	}

	private void attachDeplObservers(DeploymentData deplData) {
		final Set deployObservers = InternalDeplObserversInitializer
				.getInstance().initDeployObservers();
		for (Iterator iter = deployObservers.iterator(); iter.hasNext();) {
			final DeploymentObserver observer = (DeploymentObserver) iter
					.next();
			deplData.addDeploymentObserver(observer);
		}
	}

	private DeploymentParallelTraverser getDeploymentParallelTraverser(
			DeploymentData deplData, Enumeration admittedDeplItemsEnum,
			OnlineOfflineSoftwareType onOffSoftwareType)
			throws DeploymentException {

		final Set<DeploymentStatus> acceptedStatuses = new HashSet<DeploymentStatus>(
				6);
		acceptedStatuses.add(DeploymentStatus.OFFLINE_ADMITTED);
		acceptedStatuses.add(DeploymentStatus.DELIVERED);
		acceptedStatuses.add(DeploymentStatus.WARNING);
		acceptedStatuses.add(DeploymentStatus.OFFLINE_WARNING);
		acceptedStatuses.add(DeploymentStatus.OFFLINE_SUCCESS);
		acceptedStatuses.add(DeploymentStatus.SUCCESS);
		final ErrorStrategy errorStrategy = deplData
				.getDeploymentErrorStrategy();
		final DeploymentDataStorageManager dplDataStorageMng = getDeplDataStorageMng();

		if (ErrorStrategy.ON_ERROR_SKIP_DEPENDING.equals(errorStrategy)) {
			return new PostDplOnErrorSkipDepParallelTraverser(
					admittedDeplItemsEnum, acceptedStatuses, dplDataStorageMng,
					deplData, onOffSoftwareType);
		}

		return new PostDplOnErrorStopParallelTraverser(admittedDeplItemsEnum,
				acceptedStatuses, dplDataStorageMng, deplData,
				onOffSoftwareType);
	}

	private synchronized DeploymentDataStorageManager getDeplDataStorageMng() {
		if (this.deplDataStorageManager == null) {
			this.deplDataStorageManager = DeploymentDataStorageFactory
					.getInstance().createDeploymentDataStorageManager(
							ServiceConfigurer.getInstance()
									.getConfigurationHandlerFactory());
		}

		return this.deplDataStorageManager;
	}

	private synchronized PostProcessorObserver getPostProcessorObserver() {
		if (this.postProcessorObserver == null) {
			this.postProcessorObserver = new PostProcessorObserver(
					getDeplDataStorageMng());
		}

		return this.postProcessorObserver;
	}

	private static class PostProcessorObserver implements
			DeployPostProcessorObserver {

		private final DeploymentDataStorageManager storageManager;

		PostProcessorObserver(DeploymentDataStorageManager _storageManager) {
			this.storageManager = _storageManager;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.deploy.impl.DeployPostProcessorObserver
		 * #
		 * deployPerformed(com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem
		 * , com.sap.engine.services.dc.cm.deploy.DeploymentData)
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
				de.setMessageID("ASJ.dpl_dc.003092");
				throw de;
			}

			DCLog.logInfo(location, 
					"ASJ.dpl_dc.001105",
					"Deployment of component [{0}] finished. Item status is [{1}]",
					new Object[] { deplBatchItem.getBatchItemId(),
							deplBatchItem.getDeploymentStatus() });

		}
	}

	private interface PostDeployPrivilegedAction extends PrivilegedAction {
		void getDeploymentException() throws DeploymentException;
	}

}
