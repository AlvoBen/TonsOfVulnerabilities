package com.sap.engine.services.dc.cm.undeploy.impl;

import static com.sap.engine.services.dc.cm.utils.ResultUtils.logSummary4Undeploy;
import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sap.engine.boot.soft.CriticalOperationNotAlowedException;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.accounting.measurement.AMeasurement;
import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.ErrorStrategyAction;
import com.sap.engine.services.dc.cm.lock.DCLockException;
import com.sap.engine.services.dc.cm.lock.DCLockManager;
import com.sap.engine.services.dc.cm.lock.DCLockManagerFactory;
import com.sap.engine.services.dc.cm.lock.LockAction;
import com.sap.engine.services.dc.cm.session_id.SessionIDFactory;
import com.sap.engine.services.dc.cm.session_id.SessionIDStorageManager;
import com.sap.engine.services.dc.cm.undeploy.DCNotAvailableException;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployListenersList;
import com.sap.engine.services.dc.cm.undeploy.UndeployParallelismStrategy;
import com.sap.engine.services.dc.cm.undeploy.UndeployResult;
import com.sap.engine.services.dc.cm.undeploy.UndeployResultNotFoundException;
import com.sap.engine.services.dc.cm.undeploy.UndeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.undeploy.Undeployer;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentBatch;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentData;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentException;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentHelperFactory;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentObserver;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentProcessor;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentProcessorFactory;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentStrategy;
import com.sap.engine.services.dc.cm.undeploy.impl.lock.UILockEvaluatorFactory;
import com.sap.engine.services.dc.cm.undeploy.impl.sorters.UndeployItemsSorter;
import com.sap.engine.services.dc.cm.undeploy.impl.sorters.UndeployItemsSorterFactory;
import com.sap.engine.services.dc.cm.undeploy.storage.UndeplDataStorageException;
import com.sap.engine.services.dc.cm.undeploy.storage.UndeplDataStorageNotFoundException;
import com.sap.engine.services.dc.cm.undeploy.storage.UndeploymentDataStorageFactory;
import com.sap.engine.services.dc.cm.undeploy.storage.UndeploymentDataStorageManager;
import com.sap.engine.services.dc.cm.utils.measurement.DataMeasurements;
import com.sap.engine.services.dc.cm.utils.measurement.MeasurementUtils;
import com.sap.engine.services.dc.event.ClusterListener;
import com.sap.engine.services.dc.event.EventMode;
import com.sap.engine.services.dc.event.GlobalListenersList;
import com.sap.engine.services.dc.event.ListenerMode;
import com.sap.engine.services.dc.event.UndeploymentListener;
import com.sap.engine.services.dc.manage.DCManager;
import com.sap.engine.services.dc.manage.DCState;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.CallerInfo;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.Utils;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.dc.util.exception.DCResourceAccessor;
import com.sap.engine.services.dc.util.lock.LockData;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.UndeployItemStatusLogger;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class UndeployerImpl implements Undeployer {
	
	private Location location = DCLog.getLocation(this.getClass());

	private final ErrorStrategies errorStrategies;
	private final Collection<UndeploymentObserver> observers;
	private final UndeployListenersList undeployListenersList;

	private UndeployWorkflowStrategy undeployWorkflowStrategy = UndeployWorkflowStrategy.NORMAL;
	private UndeploymentStrategy undeploymentStrategy = UndeploymentStrategy.IF_DEPENDING_STOP;
	private UndeployParallelismStrategy undeployParallelismStrategy = UndeployParallelismStrategy.NORMAL;

	private boolean onlineDeploymentOfCoreComponents = false;
	private final String performerUserUniqueId;

	UndeployerImpl(final String performerUserUniqueId) throws RemoteException {
		super();

		this.performerUserUniqueId = performerUserUniqueId;
		this.observers = new ArrayList<UndeploymentObserver>();
		this.errorStrategies = ErrorStrategies.createInstance();
		this.undeployListenersList = UndeployListenersList.createInstance();

		if (ServiceConfigurer.getInstance().getUndeployWorkflowStrategy() != null) {
			this.undeployWorkflowStrategy = ServiceConfigurer.getInstance()
					.getUndeployWorkflowStrategy();
		}

		if (ServiceConfigurer.getInstance().getUndeployParallelismStrategy() != null) {
			this.undeployParallelismStrategy = ServiceConfigurer.getInstance()
					.getUndeployParallelismStrategy();
		}

		attachObservers();
	}

	public UndeployResult undeploy(UndeployItem[] undeployItems,
			String sessionId) throws UndeploymentException, DCLockException {
		return undeploy((GenericUndeployItem[]) undeployItems, sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.Undeployer#undeploy(com.sap.engine
	 * .services.dc.cm.undeploy.UndeployItem[])
	 */
	public UndeployResult undeploy(GenericUndeployItem[] undeployItems,
			String sessionId) throws UndeploymentException, DCLockException {

		
		try {
			ServiceConfigurer.getInstance().enterCriticalOperation(sessionId);
			
		} catch (CriticalOperationNotAlowedException e1) {
			throw new DCLockException("The operation cannot be performed because a shutdown has been initiated.", e1);
		}
		
		try {
			Utils
					.setOnlineDeploymentOfCoreComponents(this.onlineDeploymentOfCoreComponents);
			return undeployInternal(undeployItems, sessionId);

		} catch (RuntimeException e) {

			// log the runtime exceptions before they leave deploy controller
			// except on restart
			if (!DCManager.getInstance().getDCState()
					.equals(DCState.RESTARTING)) {
				DCLog
						.logError(location, 
								"ASJ.dpl_dc.002531",
								"An unhandled runtime error/exception will leave deploy controller: [{0}]",
								new Object[] { e.getMessage() });
				DCLog.logErrorThrowable(location, e);
			} else {
				if (isDebugLoggable()) {
					final String message = DCLog
							.buildExceptionMessage(
									"ASJ.dpl_dc.002532",
									"Caught a runtime exception while in state RESTARTING. Assuming a clean exit of the thread.");
					logDebugThrowable(location, null, message, e);
				}
			}

			throw e;

		} catch (java.lang.Error e) {
			// log the runtime errors before they leave deploy controller
			DCLog
					.logError(location, 
							"ASJ.dpl_dc.002533",
							"An unhandled runtime error/exception will leave deploy controller: [{0}]",
							new Object[] { e.getMessage() });
			DCLog.logErrorThrowable(location, e);
			throw e;

		} finally {
			ServiceConfigurer.getInstance().exitCriticalOperation(sessionId);
			Utils.setOnlineDeploymentOfCoreComponents(false);
		}

	}

	private UndeployResult undeployInternal(
			GenericUndeployItem[] undeployItems, String sessionId)
			throws UndeploymentException, DCLockException {

		DCLog.Session.begin(sessionId);

		if (location.beDebug()) {
			traceDebug(location, "===================================================");
		}
		if (location.beDebug()) {
			traceDebug(location, "++++++++++++++ Starting undeployment ++++++++++++++");
		}
		if (location.beDebug()) {
			traceDebug(location, "===================================================");
		}

		final String tagName = "PreProcess";
		Accounting.beginMeasure(tagName, UndeployerImpl.class);

		checkForAvailablility();

		logUndeployItems(undeployItems);
		logSetups();

		doCheckSessionId(sessionId);

		// add observers
		for (GenericUndeployItem item : undeployItems) {
			item
					.addUndeployItemObserver(UndeployItemStatusLogger
							.getInstance());
		}

		final UndeploymentBatch undeploymentBatch = UndeploymentBatchImpl
				.createUndeploymentBatch(undeployItems);

		final ErrorStrategy prerequisitesErrorStrategy = this
				.getErrorStrategy(ErrorStrategyAction.PREREQUISITES_CHECK_ACTION);

		if (location.bePath()) {
			tracePath(location, "Start undeployment checks ...");
		}
		UndeployItemInitializer.getInstance().init(undeploymentBatch,
				prerequisitesErrorStrategy);

		if (location.bePath()) {
			tracePath(location, "Sorting components for undeployment ...");
		}
		final UndeployItemsSorter sorter = UndeployItemsSorterFactory
				.getInstance().createUndeploymentsSorter();
		final List<GenericUndeployItem> sortedUndeployItems = sorter
				.sort(undeploymentBatch);
		undeploymentBatch.addOrderedUndeployItems(sortedUndeployItems);

		final ErrorStrategy undeployErrorStrategy = this
				.getErrorStrategy(ErrorStrategyAction.UNDEPLOYMENT_ACTION);

		final AMeasurement measurement = Accounting.endMeasure(tagName);
		final DataMeasurements dataMeasurements = new DataMeasurements();
		dataMeasurements.setPrePhaseMeasurement(MeasurementUtils
				.map(measurement));

		final UndeploymentData undeploymentData = UndeploymentHelperFactory
				.getInstance().createUndeploymentData(sortedUndeployItems,
						undeploymentBatch, sessionId, this.observers,
						undeployErrorStrategy, this.undeploymentStrategy,
						this.undeployWorkflowStrategy,
						this.undeployParallelismStrategy,
						this.undeployListenersList, dataMeasurements,
						this.performerUserUniqueId, CallerInfo.getHost());

		// Locking the deploy controller according to the parallelism options
		final DCLockManager lock = DCLockManagerFactory.getInstance()
				.createDCLockManager();
		final LockAction undeployAction = LockAction.UNDEPLOY;
		final LockData lockData = UILockEvaluatorFactory.getInstance()
				.getUILockEvaluator(undeploymentData.getUndeploymentStrategy())
				.evaluateLockData(sortedUndeployItems,
						undeploymentData.getSessionId(),
						this.undeployParallelismStrategy,
						this.undeployWorkflowStrategy);
		lock.lockEnqueue(undeployAction, lockData);
		try {
			final UndeploymentProcessor undeploymentProcessor = UndeploymentProcessorFactory
					.getInstance()
					.createUndeploymentProcessor(undeploymentData);

			if (location.bePath()) {
				tracePath(location, "Performing undeployment ...");
			}
			final UndeployResult undeployResult = undeploymentProcessor
					.process(undeploymentBatch);
			DCLog.logInfo(location, "ASJ.dpl_dc.002540",
					"Undeployment has finished with result [{0}]",
					new Object[] { undeployResult });

			// trace result
			DCLog.logInfo(location, "ASJ.dpl_dc.002541", "{0}",
					new Object[] { logSummary4Undeploy(undeploymentBatch
							.getUndeployItems()) });

			return undeployResult;
		} catch (UndeploymentException ue) {
			if (ue.getUndeployItems().isEmpty()) {
				ue.addUndeployItems(
						undeploymentBatch.getOrderedUndeployItems(),
						undeploymentBatch.getUndeployItems());
			}

			// trace result
			DCLog.logInfo(location, "ASJ.dpl_dc.002542", "{0}",
					new Object[] { logSummary4Undeploy(undeploymentBatch
							.getUndeployItems()) });

			throw ue;
		} finally {
			// unlock the deploy controller
			if (!DCManager.getInstance().getDCState()
					.equals(DCState.RESTARTING)) {
				lock.unlockEnqueue(undeployAction, lockData);

				if (location.beDebug()) {
					traceDebug(location, "===================================================");
				}
				if (location.beDebug()) {
					traceDebug(location, "++++++++++++++ Undeployment finished ++++++++++++++");
				}
				if (location.beDebug()) {
					traceDebug(location, "==================================================={0}",
						new Object[] { System.getProperty("line.separator") });
				}
			} else {
				if (location.bePath()) {
					tracePath(location, 
								"Enqueue will not be unlocked because the Deploy Controller state is RESTARTING");
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.Undeployer#setUndeploymentStrategy
	 * (com.sap.engine.services.dc.cm.undeploy.UndeploymentStrategy)
	 */
	public void setUndeploymentStrategy(
			UndeploymentStrategy undeploymentStrategy) {
		this.undeploymentStrategy = undeploymentStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.Undeployer#getUndeploymentStrategy
	 * ()
	 */
	public UndeploymentStrategy getUndeploymentStrategy() {
		return this.undeploymentStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.Undeployer#setUndeployWorkflowStrategy
	 * (com.sap.engine.services.dc.cm.undeploy.UndeployWorkflowStrategy)
	 */
	public void setUndeployWorkflowStrategy(
			UndeployWorkflowStrategy workflowStrategy) {
		this.undeployWorkflowStrategy = workflowStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.Undeployer#getUndeployWorkflowStrategy
	 * ()
	 */
	public UndeployWorkflowStrategy getUndeployWorkflowStrategy() {
		return this.undeployWorkflowStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.Undeployer#getErrorStrategy(java
	 * .lang.Integer)
	 */
	public ErrorStrategy getErrorStrategy(
			ErrorStrategyAction errorStrategyAction) {
		return this.errorStrategies.getErrorStrategy(errorStrategyAction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.Undeployer#setErrorStrategy(java
	 * .lang.Integer, com.sap.engine.services.dc.cm.ErrorStrategy)
	 */
	public void setErrorStrategy(ErrorStrategyAction errorStrategyAction,
			ErrorStrategy stategy) {
		this.errorStrategies.setErrorStrategy(errorStrategyAction, stategy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.Undeployer#addObserver(com.sap
	 * .engine.services.dc.cm.undeploy.UndeploymentObserver)
	 */
	public void addObserver(UndeploymentObserver observer) {
		this.observers.add(observer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.Undeployer#removeObserver(com.
	 * sap.engine.services.dc.cm.undeploy.UndeploymentObserver)
	 */
	public void removeObserver(UndeploymentObserver observer) {
		this.observers.remove(observer);
	}

	public String[] getOfflineUndeployTransactionIDs()
			throws UndeploymentException {
		ConfigurationHandlerFactory cfgFactory = getConfigurationHandlerFactory();
		SessionIDFactory sessionIDFactory = SessionIDFactory.getInstance();
		SessionIDStorageManager sessionIDStorageManager = sessionIDFactory
				.getSessionIDStorageManager();
		ConfigurationHandler cfgHandler = null;
		try {
			cfgHandler = cfgFactory.getConfigurationHandler();
			return sessionIDStorageManager
					.getUndeployTransactionIDs(cfgHandler);
		} catch (ConfigurationException e) {
			DCLog.logErrorThrowable(location, e);
			UndeploymentException ue = new UndeploymentException(
					"Exception during getting list with available "
							+ "undeploy offline transaction IDs.Reason:"
							+ e.getLocalizedMessage(), e);
			ue.setMessageID("ASJ.dpl_dc.003210");
			throw ue;
		} finally {
			if (cfgHandler != null) {
				try {
					cfgHandler.rollback();
					cfgHandler.closeAllConfigurations();
				} catch (ConfigurationException e) {
					// $JL-EXC$
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.Undeployer#getUndeployResult(java
	 * .lang.String)
	 */
	public UndeployResult getUndeployResult(String sessionId)
			throws UndeploymentException, UndeployResultNotFoundException {
		waitByLock();

		doCheckSessionId(sessionId);

		final ConfigurationHandlerFactory cfgFactory = getConfigurationHandlerFactory();
		final UndeploymentDataStorageManager uddsManager = getUndeploymentDataStorageManager(cfgFactory);

		final UndeploymentData udData;
		try {
			udData = uddsManager.loadUndeploymentData(sessionId);
		} catch (UndeplDataStorageNotFoundException uddsnfe) {
			UndeployResultNotFoundException urnfe = new UndeployResultNotFoundException(
					"Cannot get UndeployResult for "
							+ sessionId + " session ID.", uddsnfe);
			urnfe.setMessageID("ASJ.dpl_dc.003211");
			throw urnfe;
		} catch (UndeplDataStorageException uddse) {
			UndeploymentException ue = new UndeploymentException(
					"Cannot get UndeployResult for "
							+ sessionId + " session ID.", uddse);
			ue.setMessageID("ASJ.dpl_dc.003212");
			throw ue;
		}

		final UndeployResult udResult = UndeployResultBuilder.getInstance()
				.build(udData);
		return udResult;
	}

	private void doCheckSessionId(String sessionId) {
		if (sessionId == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003213 The sessionId could not be null.");
		}

		try {
			final long sessionIdAsLong = Long.parseLong(sessionId);
			if (sessionIdAsLong < 0) {
				throw new IllegalArgumentException(
						"ASJ.dpl_dc.003214 The specified session id '"
								+ sessionId + "' could not be negative long.");
			}
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException(
					"ASJ.dpl_dc.003215 The specified session id '"
							+ sessionId + "' is not a valid long.");
		}
	}

	private void attachObservers() {
		final Set observersSet = InternalUndeplObserversInitializer
				.getInstance().initUndeployObservers();
		for (Iterator iter = observersSet.iterator(); iter.hasNext();) {
			final UndeploymentObserver observer = (UndeploymentObserver) iter
					.next();
			addObserver(observer);
		}
	}

	private ConfigurationHandlerFactory getConfigurationHandlerFactory()
			throws UndeploymentException {
		final ConfigurationHandlerFactory cfgFactory;
		try {
			cfgFactory = ServiceConfigurer.getInstance()
					.getConfigurationHandlerFactory();
		} catch (Exception e) {
			UndeploymentException ue = new UndeploymentException(
					"An error occurred while getting a "
							+ "Configuration Handler Factory from the Undeployer.",
					e);
			ue.setMessageID("ASJ.dpl_dc.003216");
			throw ue;
		}
		return cfgFactory;
	}

	private UndeploymentDataStorageManager getUndeploymentDataStorageManager(
			ConfigurationHandlerFactory cfgFactory)
			throws UndeploymentException {
		final UndeploymentDataStorageManager uddsManager;
		try {
			uddsManager = UndeploymentDataStorageFactory.getInstance()
					.createUndeploymentDataStorageManager(cfgFactory);
		} catch (Exception e) {
			UndeploymentException ue = new UndeploymentException(
					"An error occurred while getting a "
							+ "Undeployment Data Storage Factory from the Undeployer.",
					e);
			ue.setMessageID("ASJ.dpl_dc.003217");
			throw ue;
		}
		return uddsManager;
	}

	private void logUndeployItems(GenericUndeployItem[] undeployItems) {
		DCLog.logInfo(location, "ASJ.dpl_dc.002547",
				"Components specified for undeployment are:");
		for (int i = 0; i < undeployItems.length; i++) {
			DCLog.logInfo(location, "ASJ.dpl_dc.002548", "SDU: {0}",
					new Object[] { undeployItems[i].toString() });
		}
	}

	private void logSetups() {
		DCLog
				.logInfo(location, 
						"ASJ.dpl_dc.002549",
						"Undeployment settings are: {0}{1}{0}Undeployment Strategy: [{2}]",
						new Object[] { Constants.EOL,
								this.errorStrategies.toString(),
								this.undeploymentStrategy });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.Undeployer#addUndeploymentListener
	 * (com.sap.engine.services.dc.event.UndeploymentListener,
	 * com.sap.engine.services.dc.event.ListenerMode,
	 * com.sap.engine.services.dc.event.EventMode)
	 */
	public void addUndeploymentListener(UndeploymentListener listener,
			ListenerMode listenerMode, EventMode eventMode) {
		if (ListenerMode.LOCAL.equals(listenerMode)) {
			this.undeployListenersList.addUndeploymentListener(listener,
					listenerMode, eventMode);
		} else if (ListenerMode.GLOBAL.equals(listenerMode)) {
			GlobalListenersList.getInstance().addListener(listener);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.Undeployer#removeUndeploymentListener
	 * (com.sap.engine.services.dc.event.UndeploymentListener)
	 */
	public void removeUndeploymentListener(UndeploymentListener listener) {
		this.undeployListenersList.removeUndeploymentListener(listener);

		GlobalListenersList.getInstance().removeListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.Undeployer#addClusterListener(
	 * com.sap.engine.services.dc.event.ClusterListener,
	 * com.sap.engine.services.dc.event.ListenerMode,
	 * com.sap.engine.services.dc.event.EventMode)
	 */
	public void addClusterListener(ClusterListener listener,
			ListenerMode listenerMode, EventMode eventMode) {
		if (ListenerMode.LOCAL.equals(listenerMode)) {
			this.undeployListenersList.addClusterListener(listener,
					listenerMode, eventMode);
		} else if (ListenerMode.GLOBAL.equals(listenerMode)) {
			GlobalListenersList.getInstance().addListener(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.Undeployer#removeClusterListener
	 * (com.sap.engine.services.dc.event.ClusterListener)
	 */
	public void removeClusterListener(ClusterListener listener) {
		this.undeployListenersList.removeClusterListener(listener);

		GlobalListenersList.getInstance().removeListener(listener);
	}

	/**
	 * Checks if the DC is ready to serve
	 * 
	 * @throws DCNotAvailableException
	 *             if DC performs some operation after the offline phase or the
	 *             repo is still initializing
	 */
	private static void checkForAvailablility() throws DCNotAvailableException {
		if (!DCManager.getInstance().isInWorkingMode()) {
			throw new DCNotAvailableException(DCResourceAccessor.getInstance()
					.getMessageText(DCExceptionConstants.DC_NOT_AVAILABLE_YET));
		}
	}

	private void waitByLock() {

		DCManager.getInstance().waitUntilWorking(
				ServiceConfigurer.getInstance().getOfflineResultTimeout());
	}

	public void setOnlineDeployemtOfCoreComponents(boolean value) {

		this.onlineDeploymentOfCoreComponents = value;

	}
}
