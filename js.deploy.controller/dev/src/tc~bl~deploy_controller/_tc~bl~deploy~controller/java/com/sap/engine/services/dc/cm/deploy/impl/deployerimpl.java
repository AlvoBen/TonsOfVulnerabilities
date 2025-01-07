package com.sap.engine.services.dc.cm.deploy.impl;

import static com.sap.engine.services.dc.cm.utils.ResultUtils.logSummary4Deploy;
import static com.sap.engine.services.dc.util.RollingUtils.createTestInfo;
import static com.sap.engine.services.dc.util.StringBuilderUtils.concat;
import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.sap.engine.boot.soft.CriticalOperationNotAlowedException;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.monitor.ClusterMonitor;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.thread.execution.Executor;
import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.accounting.measurement.AMeasurement;
import com.sap.engine.services.dc.cm.CMException;
import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.ErrorStrategyAction;
import com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule;
import com.sap.engine.services.dc.cm.deploy.DependenciesResolvingException;
import com.sap.engine.services.dc.cm.deploy.DeployFactory;
import com.sap.engine.services.dc.cm.deploy.DeployListenersList;
import com.sap.engine.services.dc.cm.deploy.DeployParallelismStrategy;
import com.sap.engine.services.dc.cm.deploy.DeployResult;
import com.sap.engine.services.dc.cm.deploy.DeployResultNotFoundException;
import com.sap.engine.services.dc.cm.deploy.DeployResultStatus;
import com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.deploy.Deployer;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatch;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentObserver;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.deploy.LifeCycleDeployStrategy;
import com.sap.engine.services.dc.cm.deploy.RollingDeployException;
import com.sap.engine.services.dc.cm.deploy.RollingMonitor;
import com.sap.engine.services.dc.cm.deploy.RollingSession;
import com.sap.engine.services.dc.cm.deploy.SduLoadingException;
import com.sap.engine.services.dc.cm.deploy.SyncException;
import com.sap.engine.services.dc.cm.deploy.SyncItem;
import com.sap.engine.services.dc.cm.deploy.SyncItemException;
import com.sap.engine.services.dc.cm.deploy.SyncRequest;
import com.sap.engine.services.dc.cm.deploy.SyncResult;
import com.sap.engine.services.dc.cm.deploy.ValidationException;
import com.sap.engine.services.dc.cm.deploy.ValidationResult;
import com.sap.engine.services.dc.cm.deploy.impl.lock.DBILockEvaluatorFactory;
import com.sap.engine.services.dc.cm.deploy.impl.sorters.DeploymentBatchItemsSorterFactory;
import com.sap.engine.services.dc.cm.deploy.impl.sorters.DeploymentBatchSorter;
import com.sap.engine.services.dc.cm.deploy.impl.sorters.SortException;
import com.sap.engine.services.dc.cm.deploy.storage.DeplDataStorageException;
import com.sap.engine.services.dc.cm.deploy.storage.DeplDataStorageNotFoundException;
import com.sap.engine.services.dc.cm.deploy.storage.DeploymentDataStorageManager;
import com.sap.engine.services.dc.cm.dscr.ClusterDescriptor;
import com.sap.engine.services.dc.cm.dscr.ClusterDscrFactory;
import com.sap.engine.services.dc.cm.dscr.ClusterStatus;
import com.sap.engine.services.dc.cm.dscr.InstanceDescriptor;
import com.sap.engine.services.dc.cm.dscr.InstanceStatus;
import com.sap.engine.services.dc.cm.dscr.ServerDescriptor;
import com.sap.engine.services.dc.cm.lock.DCLockException;
import com.sap.engine.services.dc.cm.lock.DCLockManager;
import com.sap.engine.services.dc.cm.lock.DCLockManagerFactory;
import com.sap.engine.services.dc.cm.lock.LockAction;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;
import com.sap.engine.services.dc.cm.session_id.SessionIDFactory;
import com.sap.engine.services.dc.cm.session_id.SessionIDStorageManager;
import com.sap.engine.services.dc.cm.utils.filters.BatchFilter;
import com.sap.engine.services.dc.cm.utils.measurement.DataMeasurements;
import com.sap.engine.services.dc.cm.utils.measurement.MeasurementUtils;
import com.sap.engine.services.dc.event.ClusterListener;
import com.sap.engine.services.dc.event.DeploymentListener;
import com.sap.engine.services.dc.event.EventMode;
import com.sap.engine.services.dc.event.GlobalListenersList;
import com.sap.engine.services.dc.event.ListenerMode;
import com.sap.engine.services.dc.manage.DCManager;
import com.sap.engine.services.dc.manage.DCState;
import com.sap.engine.services.dc.manage.InstanceFailoverManager;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.manage.messaging.MessagingException;
import com.sap.engine.services.dc.repo.SduId;
import com.sap.engine.services.dc.util.CallerInfo;
import com.sap.engine.services.dc.util.ClusterInfo;
import com.sap.engine.services.dc.util.ClusterUtils;
import com.sap.engine.services.dc.util.CollectionEnumerationMapper;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.ThreadUtil;
import com.sap.engine.services.dc.util.Utils;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.dc.util.exception.DCResourceAccessor;
import com.sap.engine.services.dc.util.lock.LockData;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-19
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class DeployerImpl extends DeployerBase implements Deployer {

	private final Location location = DCLog.getLocation(this.getClass());

	private static final int MIN_AVAILABLE_INSTANCES = 2;
	private final ErrorStrategies errorStrategies;
	private final DeploymentBatchFilterProcessor batchFilterProcessor;
	private final Collection<DeploymentObserver> observers;
	private final DeployListenersList deployListenersList;
	private ComponentVersionHandlingRule componentVersionHandlingRule = ComponentVersionHandlingRule.UPDATE_LOWER_VERSIONS_ONLY;

	private DeployWorkflowStrategy deployWorkflowStrategy = DeployWorkflowStrategy.NORMAL;
	private LifeCycleDeployStrategy lifeCycleDeployStrategy = LifeCycleDeployStrategy.BULK;
	private DeployParallelismStrategy deployParallelismStrategy = DeployParallelismStrategy.NORMAL;
	private boolean timeStatEnabled = false;
	private static final Set<DeploymentStatus> acceptedStatuses = new HashSet<DeploymentStatus>(
			8);

	private boolean onlineDeploymentOfCoreComponents = false;
	private final String performerUserUniqueId;

	static {
		acceptedStatuses.add(DeploymentStatus.OFFLINE_ADMITTED);
		acceptedStatuses.add(DeploymentStatus.DELIVERED);
		acceptedStatuses.add(DeploymentStatus.WARNING);
		acceptedStatuses.add(DeploymentStatus.OFFLINE_WARNING);
		acceptedStatuses.add(DeploymentStatus.OFFLINE_SUCCESS);
		acceptedStatuses.add(DeploymentStatus.SUCCESS);
		acceptedStatuses.add(DeploymentStatus.REPEATED);
		acceptedStatuses.add(DeploymentStatus.FILTERED);

	}

	/**
	 * @throws RemoteException
	 */
	protected DeployerImpl(final String performerUserUniqueId)
			throws RemoteException {
		super();

		this.performerUserUniqueId = performerUserUniqueId;
		this.errorStrategies = ErrorStrategies.createInstance();
		this.batchFilterProcessor = new DeploymentBatchFilterProcessor();
		this.observers = new ArrayList<DeploymentObserver>();
		this.deployListenersList = DeployListenersList.createInstance();

		if (ServiceConfigurer.getInstance().getDeployWorkflowStrategy() != null) {
			this.deployWorkflowStrategy = ServiceConfigurer.getInstance()
					.getDeployWorkflowStrategy();
		}

		if (ServiceConfigurer.getInstance().getDeployParallelismStrategy() != null) {
			this.deployParallelismStrategy = ServiceConfigurer.getInstance()
					.getDeployParallelismStrategy();
		}

		attachObservers();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#deploy(java.lang.String[],
	 * java.lang.String)
	 */
	public DeployResult deploy(final String[] archiveFilePathNames,
			final String sessionId) throws ValidationException,
			DeploymentException, DCLockException {

		DeployResult result = null;

		try {
			ServiceConfigurer.getInstance().enterCriticalOperation(sessionId);

		} catch (CriticalOperationNotAlowedException e1) {
			throw new DCLockException(
					"The operation cannot be performed because a shutdown has been initiated.",
					e1);
		}

		final String tagName = "Deploy with session id " + sessionId;

		try {
			DCLog.Session.begin(sessionId);
			checkForAvailablility();

			if (location.beDebug()) {
				traceDebug(location, "=================================================");
			}
			if (location.beDebug()) {
				traceDebug(location, "++++++++++++++ Starting deployment ++++++++++++++");
			}
			if (location.beDebug()) {
				traceDebug(location, "=================================================");
			}

			doCheckSessionId(sessionId);
			doCheckArchivePaths(archiveFilePathNames);

			result = deployInternal(archiveFilePathNames, sessionId);

			if (location.beDebug() && result.getMeasurement() != null) {
				traceDebug(location, "Measurement from deploy operation: [{0}]",
						new Object[] { result.getMeasurement()
								.toDocumentAsString() });
			}

			DCLog.logInfo(location, "ASJ.dpl_dc.000001", "{0}",
					new Object[] { logSummary4Deploy(result
							.getDeploymentItems()) });

			return result;
		} catch (DeploymentException de) {
			// trace result
			DCLog.logInfo(location, "ASJ.dpl_dc.000002", "{0}",
					new Object[] { logSummary4Deploy(de
							.getDeploymentBatchItems()) });
			throw de;
		} catch (RuntimeException e) {

			// log the runtime exceptions before they leave deploy controller
			// except on restart
			if (!DCManager.getInstance().getDCState()
					.equals(DCState.RESTARTING)) {
				DCLog
						.logError(location, 
								"ASJ.dpl_dc.004701",
								"An unhandled runtime error/exception will leave deploy controller: [{0}]",
								new Object[] { e.getMessage() });
				DCLog.logErrorThrowable(location, e);
			} else {
				if (isDebugLoggable()) {
					logDebugThrowable(location,
							"ASJ.dpl_dc.004702",
							"Caught a runtime exception while in state RESTARTING. Assuming a clean exit of the thread.",
							e);
				}

			}

			throw e;

		} catch (java.lang.Error e) {
			// log the runtime errors before they leave deploy controller
			logError(location, 
					"ASJ.dpl_dc.004703",
					"An unhandled runtime error/exception will leave deploy controller: [{0}]",
					new Object[] { e.getMessage() });
			logErrorThrowable(location, e);
			throw e;

		} finally {
			if (isDebugLoggable()) {
				// logDebugExt("ASJ.dpl_dc.00", "Measurement: {0}", new Object[]
				// { measurement.toDocumentAsString() });
			}
			DeploymentArchivesCleaner.getInstance().clean(sessionId);
			if (!DCManager.getInstance().getDCState()
					.equals(DCState.RESTARTING)) {
				if (location.beDebug()) {
					traceDebug(location,  "=================================================");
				}
				if (location.beDebug()) {
					traceDebug(location, "++++++++++++++ Deployment finished ++++++++++++++");
				}
				if (location.beDebug()) {
					traceDebug(location, 
							"================================================={0}",
							new Object[] { System.getProperty("line.separator") });
				}
				ServiceConfigurer.getInstance()
						.exitCriticalOperation(sessionId);
			}
			Utils.setOnlineDeploymentOfCoreComponents(false);
			DCLog.Session.clear();
		}
	}

	private DeployResult deployInternal(final String[] archiveFilePathNames,
			final String sessionId) throws ValidationException,
			DeploymentException, DCLockException {

		final boolean isRolling = DeployWorkflowStrategy.ROLLING
				.equals(this.deployWorkflowStrategy);
		if (isRolling) {
			ClusterInfo clusterInfo = ClusterUtils
					.getClusterInfoWithThis(ServiceConfigurer.getInstance()
							.getClusterMonitor());
			checkReadinessForRollingPatch(clusterInfo);
			if (archiveFilePathNames.length > 1)
				throw new RollingDeployException(
						"ASJ.dpl_dc.003466 Given archives are "
								+ archiveFilePathNames.length
								+ ". Only one component can be update with ROLLING strategy.");
		}

		logArchives(archiveFilePathNames);

		final String tagName = "PreProcess";
		Accounting.beginMeasure(tagName, DeployerImpl.class);

		final DeploymentBatch deploymentBatch = loadArchives(sessionId,
				archiveFilePathNames);

		if (isRolling
				&& (deploymentBatch.getAllCompositeDeplItems().size() > 0)) {
			throw new RollingDeployException(
					"ASJ.dpl_dc.003467 SCA cannot be updated with ROLLING strategy.");
		}

		if (this.onlineDeploymentOfCoreComponents) {

			if (deploymentBatch.getDeploymentBatchItems().size() != 1) {
				throw new IllegalStateException(
						"When online deployment of core components is enabled"
								+ "there should be only one item in the batch");
			}

			DeploymentBatchItem item = deploymentBatch
					.getDeploymentBatchItems().iterator().next();

			if (!(item instanceof DeploymentItem)) {
				throw new IllegalStateException(
						"Only SDAs which contain primary services can be deployed online");
			}

			DeploymentItem deplItem = (DeploymentItem) item;
			if (!(deplItem.getSda().getSoftwareType().getName()
					.equalsIgnoreCase("primary-service")
					|| deplItem.getSda().getSoftwareType().getName()
							.equalsIgnoreCase("primary-interface") || deplItem
					.getSda().getSoftwareType().getName().equalsIgnoreCase(
							"primary-library"))) {
				throw new IllegalStateException(
						"Only SDAs which contain primary services, interfaces and libraries can be deployed online");
			}

			// so far we made sure that we have just a single sda passed for
			// deployment and it is a primary service

			// set this as a thread local in order to avoid putting this
			// property in all the method
			// signatures ( its a prototype only at this stage )
			Utils.setOnlineDeploymentOfCoreComponents(true);
		}

		logSetups();

		if (location.bePath()) {
			tracePath(location, "Starting deployment checks ...");
		}

		doCheckDeploymentBatch(deploymentBatch);

		this.batchFilterProcessor.applyBatchFilters(deploymentBatch);

		final ErrorStrategy validationErrorStrategy = this
				.getErrorStrategy(ErrorStrategyAction.PREREQUISITES_CHECK_ACTION);

		final ErrorStrategy deploymentErrorStrategy = this
				.getErrorStrategy(ErrorStrategyAction.DEPLOYMENT_ACTION);

		Collection<DeploymentBatchItem> sortedAdmittedDeploymentItems = null;
		try {

			final PrerequisitesValidator prerequisitesValidator = getPrerequisitesValidator(getSoftwareTypeService());
			prerequisitesValidator.doValidate(deploymentBatch);

			final SoftwareTypeService softwareTypeService = prerequisitesValidator
					.getNewSoftwareTypeService();

			// this list will contain the admitted and resolved dep
			// the operation will adjust the status of the items with unresolved
			// dependencies
			List<DeploymentItem> sortedByDependency = resolveDeploymentBatch(
					deploymentBatch, softwareTypeService);

			// after resolving, lets check admitted items again and apply the
			// strategy
			prerequisitesValidator
					.applyTheErrorStrategyAfterCheck(deploymentBatch);

			sortedAdmittedDeploymentItems = sortDeploymentBatch(
					deploymentBatch, sortedByDependency, softwareTypeService);

			removeRepeatedDeploymentItems(sortedAdmittedDeploymentItems);

			if (location.bePath()) {
				tracePath(location, "Evaluating lock data ...");
			}

			final LockData lockData = DBILockEvaluatorFactory
					.createDBILockEvaluator().evaluateLockData(
							sortedAdmittedDeploymentItems,
							this.deployParallelismStrategy,
							this.deployWorkflowStrategy);

			if (location.beInfo()) {
				traceInfo(location, 
						"[{0}] operation can be executed with the following lock data [{1}].",
						new String[] {
								(lockData != null ? "Parallel" : "Single"),
								(lockData != null ? lockData.toString()
										: "null") });
			}

			final AMeasurement measurement = Accounting.endMeasure(tagName);
			final DataMeasurements dataMeasurements = new DataMeasurements();
			dataMeasurements.setPrePhaseMeasurement(MeasurementUtils
					.map(measurement));

			final DeploymentData deploymentData = DeployFactory.getInstance()
					.createDeploymentData(sortedAdmittedDeploymentItems,
							deploymentBatch, sessionId, this.observers,
							deploymentErrorStrategy,
							this.deployWorkflowStrategy,
							this.deployParallelismStrategy,
							this.lifeCycleDeployStrategy,
							this.deployListenersList,
							this.performerUserUniqueId, CallerInfo.getHost(),
							"", this.timeStatEnabled, null, lockData,
							dataMeasurements,
							ServiceConfigurer.getInstance().getOsUser(),
							ServiceConfigurer.getInstance().getOsPass());

			return doDeploy(sortedAdmittedDeploymentItems, deploymentData);
		} catch (ValidationException ve) {
			if (ve.getDeploymentBatchItems().isEmpty()
					&& deploymentBatch != null) {
				DeploymentBatchAnalyzer.getInstance().analyseDeploymentBatch(
						validationErrorStrategy, deploymentBatch);
				ve.addDeploymentBatchItems(sortedAdmittedDeploymentItems,
						deploymentBatch.getDeploymentBatchItems());
			}
			logErrorThrowable(location, ve);
			throw ve;
		} catch (DeploymentException de) {
			if (de.getDeploymentBatchItems().isEmpty()
					&& deploymentBatch != null) {
				DeploymentBatchAnalyzer.getInstance().analyseDeploymentBatch(
						deploymentErrorStrategy, deploymentBatch);
				de.addDeploymentBatchItems(sortedAdmittedDeploymentItems,
						deploymentBatch.getDeploymentBatchItems());
			}
			logErrorThrowable(location, de);
			throw de;
		}
	}

	private SoftwareTypeService getSoftwareTypeService() {
		// return default
		return (SoftwareTypeService) ServerFactory
				.getInstance()
				.createServer()
				.getServerService(
						ServerFactory.getInstance().createSoftwareTypeRequest());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#getComponentVersionHandlingRule
	 * ()
	 */
	public ComponentVersionHandlingRule getComponentVersionHandlingRule() {
		return this.componentVersionHandlingRule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#enableTimeStats(boolean
	 * enabled)
	 */
	public void enableTimeStats(final boolean enabled) {
		this.timeStatEnabled = enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.deploy.Deployer#getTimeStatsEnabled()
	 */
	public boolean getTimeStatsEnabled() {
		return this.timeStatEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#setComponentVersionHandlingRule
	 * (com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule)
	 */
	public void setComponentVersionHandlingRule(
			final ComponentVersionHandlingRule rule) {
		this.componentVersionHandlingRule = rule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#getErrorStrategy(java.lang
	 * .Integer)
	 */
	public ErrorStrategy getErrorStrategy(
			final ErrorStrategyAction errorStrategyAction) {
		return this.errorStrategies.getErrorStrategy(errorStrategyAction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#setErrorStrategy(java.lang
	 * .Integer, com.sap.engine.services.dc.cm.ErrorStrategy)
	 */
	public void setErrorStrategy(final ErrorStrategyAction errorStrategyAction,
			final ErrorStrategy stategy) {
		this.errorStrategies.setErrorStrategy(errorStrategyAction, stategy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#getDeployWorkflowStrategy()
	 */
	public DeployWorkflowStrategy getDeployWorkflowStrategy() {
		return this.deployWorkflowStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#setDeployWorkflowStrategy
	 * (com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy)
	 */
	public void setDeployWorkflowStrategy(
			final DeployWorkflowStrategy workflowStrategy) {
		this.deployWorkflowStrategy = workflowStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#getLifeCycleDeployStrategy
	 * ()
	 */
	public LifeCycleDeployStrategy getLifeCycleDeployStrategy() {
		return this.lifeCycleDeployStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#setLifeCycleDeployStrategy
	 * (com.sap.engine.services.dc.cm.deploy.LifeCycleDeployStrategy)
	 */
	public void setLifeCycleDeployStrategy(
			final LifeCycleDeployStrategy lifeCycleDeployStrategy) {
		this.lifeCycleDeployStrategy = lifeCycleDeployStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#addBatchFilter(com.sap.
	 * engine.services.dc.cm.utils.filters.BatchFilter)
	 */
	public void addBatchFilter(final BatchFilter batchFilter) {
		this.batchFilterProcessor.addBatchFilter(batchFilter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#removeBatchFilter(com.sap
	 * .engine.services.dc.cm.utils.filters.BatchFilter)
	 */
	public void removeBatchFilter(final BatchFilter batchFilter) {
		this.batchFilterProcessor.removeBatchFilter(batchFilter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#addObserver(com.sap.engine
	 * .services.dc.cm.deploy.DeploymentObserver)
	 */
	public void addObserver(final DeploymentObserver observer) {
		this.observers.add(observer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#removeObserver(com.sap.
	 * engine.services.dc.cm.deploy.DeploymentObserver)
	 */
	public void removeObserver(final DeploymentObserver observer) {
		this.observers.remove(observer);
	}

	private void waitByLock() {

		DCManager.getInstance().waitUntilWorking(
				ServiceConfigurer.getInstance().getOfflineResultTimeout());

	}

	public String[] getOfflineDeployTransactionIDs() throws DeploymentException {
		checkForAvailablility();

		ConfigurationHandlerFactory cfgFactory = getConfigurationHandlerFactory();
		SessionIDFactory sessionIDFactory = SessionIDFactory.getInstance();
		SessionIDStorageManager sessionIDStorageManager = sessionIDFactory
				.getSessionIDStorageManager();
		ConfigurationHandler cfgHandler = null;
		try {
			cfgHandler = cfgFactory.getConfigurationHandler();
			return sessionIDStorageManager.getDeployTransactionIDs(cfgHandler);
		} catch (ConfigurationException e) {
			DCLog.logErrorThrowable(location, e);
			DeploymentException de = new DeploymentException(
					"Exception during getting list with available "
							+ "deploy offline transaction IDs.Reason:"
							+ e.getLocalizedMessage(), e);
			de.setMessageID("ASJ.dpl_dc.003036");
			throw de;
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
	 * com.sap.engine.services.dc.cm.deploy.Deployer#getDeployResult(java.lang
	 * .String)
	 */
	public DeployResult getDeployResult(final String sessionId)
			throws ValidationException, DeploymentException,
			DeployResultNotFoundException {

		doCheckSessionId(sessionId);
		waitByLock();
		try {
			final DeploymentData dData;
			try {
				dData = getDeploymentData(sessionId);
			} catch (DeplDataStorageNotFoundException ddsnfe) {
				DeployResultNotFoundException drnfe = new DeployResultNotFoundException(
						"Cannot get DeployResult for " + sessionId
								+ " session ID.", ddsnfe);
				drnfe.setMessageID("ASJ.dpl_dc.003037");
				throw drnfe;
			} catch (DeplDataStorageException ddse) {
				DeploymentException de = new DeploymentException(
						"Cannot get DeployResult for " + sessionId
								+ " session ID.", ddse);
				de.setMessageID("ASJ.dpl_dc.003038");
				throw de;
			}

			final DeployResult deployResult = DeployResultBuilder.getInstance()
					.build(dData);

			// trace result
			logInfo(location, "ASJ.dpl_dc.000003", "{0}",
					new Object[] { logSummary4Deploy(deployResult
							.getDeploymentItems()) });

			return deployResult;
		} finally {
			DeploymentArchivesCleaner.getInstance().clean(sessionId);
		}
	}

	public DeployResult getDeployResult(final String sessionId,
			final DeploymentListener synchListener,
			final DeploymentListener asynchListener)
			throws DeploymentException, DeployResultNotFoundException {
		if (synchListener != null) {
			GlobalListenersList.getInstance().addListener(synchListener);
		}
		if (asynchListener != null) {
			GlobalListenersList.getInstance().addListener(asynchListener);
		}
		try {
			return getDeployResult(sessionId);
		} finally {
			if (synchListener != null) {
				GlobalListenersList.getInstance().removeListener(synchListener);
			}
			if (asynchListener != null) {
				GlobalListenersList.getInstance()
						.removeListener(asynchListener);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#validate(java.lang.String
	 * [], java.lang.String)
	 */
	public ValidationResult validate(String[] archiveFilePathNames,
			String sessionId) throws ValidationException {

		final long begin = System.currentTimeMillis();
		if (location.bePath()) {
			tracePath(location, "Starting validation ...");
		}

		// @TODO extract the common functionality between deploy and validate in
		// a method
		doCheckSessionId(sessionId);
		doCheckArchivePaths(archiveFilePathNames);

		// if DC is busy and cannot serve the request an exception will be
		// thrown
		checkForAvailablility();

		try {

			// final ErrorStrategy errorStrategy =
			// this.getErrorStrategy(ErrorStrategyAction.
			// PREREQUISITES_CHECK_ACTION);

			final DeploymentBatchValidator.ValidatorData validatorData = new DeploymentBatchValidator.ValidatorData(
					sessionId, archiveFilePathNames,
					ErrorStrategy.ON_ERROR_SKIP_DEPENDING,
					this.batchFilterProcessor, this
							.getComponentVersionHandlingRule(),
					this.deployWorkflowStrategy);

			return DeploymentBatchValidator.getInstance().doValidate(
					validatorData);
		} finally {
			DeploymentArchivesCleaner.getInstance().clean(sessionId);
			if (location.bePath()) {
				tracePath(location, "Validation took [{0}] ms",
						new Object[] { new Long(System.currentTimeMillis()
								- begin) });
			}

		}
	}

	private DeployResult doDeploy(
			final Collection sortedAdmittedDeploymentItems,
			final DeploymentData deploymentData) throws DCLockException,
			DeploymentException {
		// Locking the deploy controller according to the parallelism options
		final DCLockManager lock = DCLockManagerFactory.getInstance()
				.createDCLockManager();
		final LockData lockData = deploymentData.getLockData();
		final LockAction deployAction = LockAction.DEPLOY;
		lock.lockEnqueue(deployAction, lockData);

		doRolling(deploymentData, lockData, lock, deployAction);

		try {
			if (location.bePath()) {
				tracePath(location, "Performing deployment ...");
			}
			// TODO:rolling - if deploy workfloe strategy is ROLLLING set server
			// mode=ROLLING
			performDeploy(sortedAdmittedDeploymentItems, deploymentData);

			if (location.bePath()) {
				tracePath(location, "Building the deploy result ...");
			}

			final DeployResult deployResult = DeployResultBuilder.getInstance()
					.build(deploymentData);
			final String ddDescription = deploymentData.getDescription();
			if (ddDescription != null && ddDescription.length() > 0) {
				// TODO:add description to the DeployResult
			}
			logInfo(location, "ASJ.dpl_dc.001048",
					"Deployment finished with result: [{0}]",
					new Object[] { deployResult });

			return deployResult;
		} finally {
			if (!DeployWorkflowStrategy.ROLLING.equals(deploymentData
					.getDeployWorkflowStrategy())) {
				if (!DCState.RESTARTING.equals(DCManager.getInstance()
						.getDCState())) {
					lock.unlockEnqueue(deployAction, lockData);
				} else {
					if (location.bePath()) {
						tracePath(location, "Enqueue will not be unlocked because the Deploy Controller state is RESTARTING");
					}
				}
			} else {
				// persists the deploymentSupplement
				if (location.bePath()) {
					tracePath(location, "Persisting temporary deployment data ...");
				}

				try {
					final ConfigurationHandlerFactory cfgFactory = getConfigurationHandlerFactory();
					final DeploymentDataStorageManager ddsManager = getDeploymentDataStorageManager(cfgFactory);

					ddsManager.persistDeploymentData(deploymentData);
				} catch (DeplDataStorageException ddse) {
					throw new DeploymentException(
							DCResourceAccessor
									.getInstance()
									.getMessageText(
											DCExceptionConstants.DEPLOYMENT_DATA_PERSISTENCE_ERROR),
							ddse);
				}

			}
		}

	}

	private void doCheckDeploymentBatch(final DeploymentBatch deploymentBatch) {
		if (deploymentBatch == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003043 The deployment batch could not be null.");
		}

		if (deploymentBatch.getDeploymentBatchItems() == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003044 The deployment batch items could not be null.");
		}
	}

	private List<DeploymentItem> resolveDeploymentBatch(
			final DeploymentBatch deploymentBatch,
			final SoftwareTypeService softwareTypeService)
			throws DependenciesResolvingException {
		if (location.bePath()) {
			tracePath(location, "Resolving dependencies ...");
		}

		final ErrorStrategy errorStrategy = this
				.getErrorStrategy(ErrorStrategyAction.PREREQUISITES_CHECK_ACTION);
		final DependencyResolver.ResolverData resolverData = new DependencyResolver.ResolverData(
				errorStrategy, deploymentBatch, softwareTypeService);

		List<DeploymentItem> sortedByDependency = DependencyResolver
				.getInstance().resolve(resolverData);
		return sortedByDependency;
	}

	/**
	 * Futher sort the items with respect to the deployment phase and merge the
	 * SCAs
	 */
	private Collection<DeploymentBatchItem> sortDeploymentBatch(
			final DeploymentBatch deploymentBatch,
			final List<DeploymentItem> sortedByDependency,
			final SoftwareTypeService softwareTypeService) throws SortException {
		final String tagName = "Sort deployment batch";
		Accounting.beginMeasure(tagName, DeployerImpl.class);
		try {

			if (location.bePath()) {
				tracePath(location, "Sorting deployment items ...");
			}

			final DeploymentBatchSorter sorter = DeploymentBatchItemsSorterFactory
					.getInstance().createDependencyAndSoftwareTypeSorter(
							softwareTypeService);

			return sorter.sort(deploymentBatch, sortedByDependency);
		} finally {
			Accounting.endMeasure(tagName);
		}
	}

	private boolean isNeededFakeOfflineRestart(
			DeployPhaseGetter deployPhaseGetter,
			Collection sortedAdmittedDeploymentItems,
			DeploymentData deploymentData) throws DeploymentException {
		if (!DeployWorkflowStrategy.SAFETY.equals(deploymentData
				.getDeployWorkflowStrategy())) {
			return false;
		}

		for (Iterator iSortedAdmittedDeploymentItems = sortedAdmittedDeploymentItems
				.iterator(); iSortedAdmittedDeploymentItems.hasNext();) {
			final DeploymentBatchItem deploymentBatchItem = (DeploymentBatchItem) iSortedAdmittedDeploymentItems
					.next();

			DeployPhase deployPhase = deployPhaseGetter
					.getPhase(deploymentBatchItem);
			// if there is at least one offline component
			// there is no need to restart the engine because it will be
			// scheduled
			if (DeployPhase.OFFLINE.equals(deployPhase)) {
				return false;
			}
		}
		// There is no one offline admitted component so it is necessary to
		// trigger
		// fake offline( make sure that the applications are going to be
		// deployed
		// during engine safe mode
		return true;
	}

	private void performDeploy(
			final Collection<DeploymentBatchItem> sortedAdmittedDeploymentItems,
			final DeploymentData deploymentData) throws DeploymentException {

		final String tagName = "Online";
		Accounting.beginMeasure(tagName, DeployerImpl.class);

		// Do not traverse items that are not admitted. Such items might be
		// added
		// to the sorted items in order to have their sdu information persisted
		// this is required overcome a design problem because of which
		// the sdu information of items that are not in the sorted items of
		// deployment data is not persisted
		Set<DeploymentStatus> acceptedStatuses = new HashSet<DeploymentStatus>();
		acceptedStatuses.add(DeploymentStatus.ADMITTED);

		final Enumeration<DeploymentBatchItem> sortedAdmittedDeplBatchItemsEnum = CollectionEnumerationMapper
				.map(sortedAdmittedDeploymentItems, acceptedStatuses);

		final DeploymentParallelTraverser parallelTraverser = getParallelTraverser(
				sortedAdmittedDeplBatchItemsEnum, deploymentData);

		final DeployPhaseGetter deployPhaseGetter = DeployPhaseGetter
				.createInstance();
		DeployPhase currentDeployPhase = deployPhaseGetter
				.getPhase(sortedAdmittedDeploymentItems.iterator().next());

		boolean isPhaseFinishOK = true;

		try {

			if (this.onlineDeploymentOfCoreComponents) {

				currentDeployPhase = DeployPhase.ONLINE;
				deployCoreComponentOnline(sortedAdmittedDeploymentItems
						.iterator().next(), deploymentData);
				return;

			}

			// Check if safety strategy is set and there are only online
			// components
			// in such case additional restart is needed( assure application
			// deployment will be performed
			// when the engine is in safe mode
			final boolean isNeededFakeOfflineRestart = isNeededFakeOfflineRestart(
					deployPhaseGetter, sortedAdmittedDeploymentItems,
					deploymentData);
			if (isNeededFakeOfflineRestart) {
				currentDeployPhase = DeployPhase.OFFLINE;
			} else {
				try {
					if (1 >= ServiceConfigurer.getInstance()
							.getDeployThreadsNumber()) {
						performDeploymentInCurrentThread(parallelTraverser,
								deploymentData);
					} else {
						performDeploymentInExecutor(parallelTraverser,
								deploymentData);
					}
				} catch (DeploymentException e) {
					isPhaseFinishOK = false;
					throw e;
				}
			}
		} finally {
			final AMeasurement measurement = Accounting.endMeasure(tagName);
			final DataMeasurements dataMeasurements = deploymentData
					.getMeasurements();
			dataMeasurements.setOnlineMeasurement(MeasurementUtils
					.map(measurement));

			doPostProcessing(currentDeployPhase, deploymentData,
					isPhaseFinishOK);
		}
	}

	private void deployCoreComponentOnline(DeploymentBatchItem deploymentItem,
			DeploymentData deploymentData) throws DeploymentException {

		final AbstractDeployProcessor deployProcessor = DeliverOnlineDeployProcessor
				.getInstance();
		deployProcessor.deploy(deploymentItem, deploymentData,
				this.deployListenersList);

	}

	private void performDeploymentInCurrentThread(
			final DeploymentParallelTraverser parallelTraverser,
			final DeploymentData deploymentData) throws DeploymentException {
		DeploymentBatchItem dbItem;
		DeploymentException deploymentException = null;
		final DeployPhaseGetter deployPhaseGetter = DeployPhaseGetter
				.createInstance();

		while ((dbItem = parallelTraverser.nextElement()) != null) {
			deploymentException = null;
			final DeployPhase deployPhase = deployPhaseGetter.getPhase(dbItem);
			final AbstractDeployProcessor deployProcessor = 
				DeployProcessorMapper.getInstance().map(dbItem,
														deployPhase,
														deploymentData.getLifeCycleDeployStrategy() );
			try {
				deployProcessor.deploy(dbItem, deploymentData,
						this.deployListenersList);
			} catch (final DeploymentException de) {
				deploymentException = de;

			} catch (OutOfMemoryError e) {
				// OOM, ThreadDeath and Internal error are not consumed
				throw e;

			} catch (ThreadDeath e) {
				throw e;

			} catch (InternalError e) {
				throw e;

			} catch (Throwable t) {
				// catch all the rest of the throwables
				// and wrap them in a deployment exception
				// to improve error reporting
				deploymentException = new DeploymentException(
						"ASJ.dpl_dc.003475 Unexpected throwable occured during the deployment operation",
						t);

			} finally {
				parallelTraverser.notifyPerformed(dbItem, deploymentException);
			}
		}
		parallelTraverser.getDeploymentException();
	}

	private void performDeploymentInExecutor(
			final DeploymentParallelTraverser parallelTraverser,
			final DeploymentData deploymentData) throws DeploymentException {
		final Executor deployExecutor = getDeployExecutor();
		DeploymentBatchItem dbItem;
		final String threadNamePrefix = "DeployThread[", threadNameSufix = "]", deployOperation = "deploy";
		final CountDownLatch maxThreadDumpsCount = new CountDownLatch(10);
		final DeployPhaseGetter deployPhaseGetter = DeployPhaseGetter
				.createInstance();
		while ((dbItem = parallelTraverser.nextElement()) != null) {
			final String threadName = concat(threadNamePrefix, dbItem
					.getBatchItemId().toString(), threadNameSufix);
			final String threadTask = ThreadUtil.evaluateTaskMessage(
					deployOperation, dbItem.getSdu().getName(), dbItem.getSdu()
							.getVendor());
			// construct deploy thread and execute it
			final DeployPhase deployPhase = deployPhaseGetter.getPhase(dbItem);
			final AbstractDeployProcessor deployProcessor = 
				DeployProcessorMapper.getInstance().map(dbItem,
							   							deployPhase,
							   							deploymentData.getLifeCycleDeployStrategy());
			
			final Runnable deployRunnable = new DeployThread(dbItem,
					parallelTraverser, deployProcessor, deploymentData,
					this.deployListenersList, maxThreadDumpsCount);
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

	private void doPostProcessing(final DeployPhase currentDeployPhase,
			final DeploymentData deploymentData, final boolean isPhaseFinishOK)
			throws DeploymentException {
		if (DeployPhase.OFFLINE.equals(currentDeployPhase) && !isPhaseFinishOK) {
			// do nothing
			return;
		}
		final AbstractDeployPostProcessor postProcessor = DeployPostProcessorMapper
				.getInstance().map(currentDeployPhase);
		postProcessor.postProcess(deploymentData);
	}

	private PrerequisitesValidator getPrerequisitesValidator(
			final SoftwareTypeService softwareTypeService) {
		final ErrorStrategy errorStrategy = this
				.getErrorStrategy(ErrorStrategyAction.PREREQUISITES_CHECK_ACTION);
		return new PrerequisitesValidator(this.deployWorkflowStrategy, this
				.getComponentVersionHandlingRule(), errorStrategy,
				softwareTypeService);

	}

	private DeploymentParallelTraverser getParallelTraverser(
			final Enumeration<DeploymentBatchItem> admittedDeplItemsEnum,
			final DeploymentData deploymentData) throws DeploymentException {
		final ErrorStrategy errorStrategy = this
				.getErrorStrategy(ErrorStrategyAction.DEPLOYMENT_ACTION);

		if (errorStrategy.equals(ErrorStrategy.ON_ERROR_SKIP_DEPENDING)) {
			return new OnErrorSkipDependingParallelTraverser(
					admittedDeplItemsEnum, deploymentData, acceptedStatuses);
		} else {
			return new OnErrorStopParallelTraverser(admittedDeplItemsEnum,
					deploymentData, acceptedStatuses);
		}
	}

	private DeploymentBatch loadArchives(final String sessionId,
			final String[] archives) throws SduLoadingException {
		final ErrorStrategy errorStrategy = this
				.getErrorStrategy(ErrorStrategyAction.PREREQUISITES_CHECK_ACTION);
		final SduLoader sduLoader = new SduLoader(errorStrategy);
		DeploymentBatch deploymentBatch = sduLoader.load(sessionId, archives,
				this.timeStatEnabled);

		return deploymentBatch;
	}

	private void attachObservers() {
		final Set observersSet = InternalDeplObserversInitializer.getInstance()
				.initDeployObservers();
		for (Iterator iter = observersSet.iterator(); iter.hasNext();) {
			final DeploymentObserver deplObserver = (DeploymentObserver) iter
					.next();
			addObserver(deplObserver);
		}
	}

	private void logArchives(String[] archiveFilePathNames) {
		logInfo(location,  "ASJ.dpl_dc.001053",
				"Archives specified for deployment are: ");
		for (int i = 0; i < archiveFilePathNames.length; i++) {
			logInfo(location,  "ASJ.dpl_dc.001054", "[{0}]",
					new Object[] { archiveFilePathNames[i] });
		}
	}

	private void logSetups() {
		logInfo(location, 
				"ASJ.dpl_dc.001055",
				"Deployment settings are:{0}{1}{0} Version Handling Rule: [{2}]{0} Deployment Strategy: [{3}]{0} Life Cycle Deployment Strategy: [{4}]{0}{5}",
				new Object[] { Constants.EOL, this.errorStrategies.toString(),
						this.componentVersionHandlingRule,
						this.deployWorkflowStrategy,
						this.lifeCycleDeployStrategy,
						this.batchFilterProcessor.toString() });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#addDeploymentListener(com
	 * .sap.engine.services.dc.event.DeploymentListener,
	 * com.sap.engine.services.dc.event.ListenerMode,
	 * com.sap.engine.services.dc.event.EventMode)
	 */
	public void addDeploymentListener(final DeploymentListener listener,
			final ListenerMode listenerMode, final EventMode eventMode) {
		if (ListenerMode.LOCAL.equals(listenerMode)) {

			this.deployListenersList.addDeploymentListener(listener,
					listenerMode, eventMode);

		} else if (ListenerMode.GLOBAL.equals(listenerMode)) {
			GlobalListenersList.getInstance().addListener(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#removeDeploymentListener
	 * (com.sap.engine.services.dc.event.DeploymentListener)
	 */
	public void removeDeploymentListener(final DeploymentListener listener) {
		this.deployListenersList.removeDeploymentListener(listener);

		GlobalListenersList.getInstance().removeListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#addClusterListener(com.
	 * sap.engine.services.dc.event.ClusterListener,
	 * com.sap.engine.services.dc.event.ListenerMode,
	 * com.sap.engine.services.dc.event.EventMode)
	 */
	public void addClusterListener(final ClusterListener listener,
			final ListenerMode listenerMode, final EventMode eventMode) {
		if (ListenerMode.LOCAL.equals(listenerMode)) {
			this.deployListenersList.addClusterListener(listener, listenerMode,
					eventMode);
		} else if (ListenerMode.GLOBAL.equals(listenerMode)) {

			GlobalListenersList.getInstance().addListener(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.Deployer#removeClusterListener(com
	 * .sap.engine.services.dc.event.ClusterListener)
	 */
	public void removeClusterListener(final ClusterListener listener) {
		this.deployListenersList.removeClusterListener(listener);

		GlobalListenersList.getInstance().removeListener(listener);
	}

	private void removeRepeatedDeploymentItems(Collection sortedDeploymentItems) {
		Iterator<DeploymentBatchItem> deploymentBatchItems = sortedDeploymentItems
				.iterator();
		while (deploymentBatchItems.hasNext()) {
			if (DeploymentStatus.REPEATED.equals(deploymentBatchItems.next()
					.getDeploymentStatus())) {
				deploymentBatchItems.remove();
			}
		}
	}

	/**
	 * @deprecated The method will only be used for proofing the concept in the
	 *             prototyping phase.
	 * 
	 */
	public DeployResult commit(final String transactionId)
			throws DeploymentException, DCLockException, RollingDeployException {
		doCheckSessionId(transactionId);
		checkForAvailablility();

		ClusterMonitor clusterMonitor = ServiceConfigurer.getInstance()
				.getClusterMonitor();
		ClusterInfo clusterInfo = ClusterUtils
				.getClusterInfoWithThis(clusterMonitor);
		checkReadinessForRollingPatch(clusterInfo);
		DeploymentData dData;
		try {
			dData = getDeploymentData(transactionId);
			if (!DeployWorkflowStrategy.ROLLING.equals(dData
					.getDeployWorkflowStrategy()))
				throw new RollingDeployException(
						"ASJ.dpl_dc.003461 Commit operation failed. Transaction "
								+ transactionId
								+ "is not triggered with ROLLING strategy.");
		} catch (DeplDataStorageNotFoundException e) {
			throw new RollingDeployException(
					"ASJ.dpl_dc.003462 Commit operation failed due to lack of deployment data for transaction Id: "
							+ transactionId, e);
		} catch (DeplDataStorageException e) {
			throw new RollingDeployException(
					"ASJ.dpl_dc.003463 Commit operation failed due to an error during reading of deployment data for transaction Id: "
							+ transactionId, e);
		}
		DeployResult deployResult = DeployResultBuilder.getInstance().build(
				dData);
		// make rolling patch batch
		HashSet<ClusterStatus> clustStatuses = new HashSet<ClusterStatus>();
		clustStatuses.add(ClusterStatus.PRODUCTIVE_BUT_NEED_VALIDATION);
		Collection<SyncItem> syncItems = getRollingPatchItems(dData
				.getSortedDeploymentBatchItem(), clustStatuses);

		if (syncItems.size() == 0)
			throw new RollingDeployException(
					"ASJ.dpl_dc.003464  There is not items for commitment in the deployment batch of transaction: "
							+ transactionId);

		int[] allInstances = clusterInfo.getGroupIDs();
		int[] instances = new int[allInstances.length - 1];
		int j = 0;
		ClusterElement currentParticipant = clusterMonitor
				.getCurrentParticipant();
		int currentClusterId = currentParticipant.getClusterId();
		int currentInstanceId = currentParticipant.getGroupId();
		// exclude the current instance id from the array of instances
		for (int i = 0; i < allInstances.length; i++) {
			if (allInstances[i] != currentInstanceId)
				instances[j++] = allInstances[i];
		}

		ClusterDscrFactory clusterDscrFactory = ClusterDscrFactory
				.getInstance();

		// init instance descriptor map witch contains instance descriptors for
		// every sync item
		// current instance descriptor is reinit with new state -
		// from NOT_PRODUCTIVE_AND_NEED_VALIDATION to NOT_PRODUCTIVE_AND_UPDATED
		Map<SyncItem, Set<InstanceDescriptor>> itemsInstanceDescs = new HashMap<SyncItem, Set<InstanceDescriptor>>();
		for (SyncItem syncItem : syncItems) {
			SduId sduId = syncItem.getBatchItemId().getSduId();
			DeploymentBatchItem dbItem = dData.getDeploymentBatch()
					.getDeploymentBatchItem(sduId.getName(), sduId.getVendor());
			Set<InstanceDescriptor> instDescs = dbItem.getClusterDescriptor()
					.getInstanceDescriptors();
			Set<InstanceDescriptor> newInstDescs = new HashSet<InstanceDescriptor>();
			for (InstanceDescriptor instDesc : instDescs) {
				if (instDesc.getInstanceID() == currentInstanceId) {
					InstanceDescriptor newInstDesc = clusterDscrFactory
							.createInstanceDescriptor(instDesc.getInstanceID(),
									instDesc.getServerDescriptors(),
									InstanceStatus.NOT_PRODUCTIVE_AND_UPDATED,
									instDesc.getTestInfo(), instDesc
											.getDescription());
					newInstDescs.add(newInstDesc);
				} else {
					newInstDescs.add(instDesc);
				}
			}
			itemsInstanceDescs.put(syncItem, newInstDescs);
		}

		boolean hasOfflineDeployment = DeploymentBatchValidator.getInstance()
				.isOfflinePhaseScheduled(
						deployResult.getSortedDeploymentBatchItems());

		int count = instances.length;
		int middleIndex = count / 2;
		String firstPartStr = arrayToString(instances, 0, middleIndex);
		String secondPartStr = arrayToString(instances, middleIndex, count);

		DeployFactory deployFactory = DeployFactory.getInstance();
		RollingMonitor rollingMonitor = RollingMonitorImpl.getInstance();

		final ConfigurationHandlerFactory cfgFactory = getConfigurationHandlerFactory();
		final DeploymentDataStorageManager ddsManager = getDeploymentDataStorageManager(cfgFactory);

		boolean commitIsFailed = false;
		String errorMsg = "";

		// first step: unbind the first half of the instances
		final ArrayList<Integer> notifiedInstances = new ArrayList<Integer>();
		for (int i = 0; i < middleIndex; i++) {
			try {
				InstanceFailoverManager.getInstance().unbindInstance(
						instances[i]);
				notifiedInstances.add(instances[i]);
			} catch (CMException e) {
				errorMsg = new StringBuilder()
						.append(
								"Action: commit; Step: unbind the first part of cluster instances [")
						.append(firstPartStr).append("].").append(
								"Error: Cannot unbind instance ").append(
								instances[i]).append(":")
						.append(e.getMessage()).toString();
				if (notifiedInstances.isEmpty()) {
					throw new RollingDeployException(errorMsg, e);
				}
				commitIsFailed = true;
				break;
			}
		}

		final Set<InstanceDescriptor> notifiedInstDescs = new HashSet<InstanceDescriptor>();
		for (Integer notifiedInstance : notifiedInstances) {
			notifiedInstDescs.add(clusterDscrFactory.createInstanceDescriptor(
					notifiedInstance, new HashSet<ServerDescriptor>(),
					InstanceStatus.NOT_PRODUCTIVE_AND_NOT_UPDATED,
					createTestInfo(notifiedInstance), ""));
		}

		// reinit instance descriptor map
		for (SyncItem syncItem : syncItems) {
			Set<InstanceDescriptor> newInstDescSet = new HashSet<InstanceDescriptor>();
			newInstDescSet.addAll(notifiedInstDescs);
			Set<InstanceDescriptor> oldInstDescSet = itemsInstanceDescs
					.get(syncItem);
			newInstDescSet.addAll(oldInstDescSet);
			itemsInstanceDescs.put(syncItem, newInstDescSet);
		}

		if (commitIsFailed) {
			return makeRollingResult(dData, syncItems, itemsInstanceDescs,
					errorMsg, ClusterStatus.PRODUCTIVE_BUT_NEED_ROLL_BACK,
					DeployResultStatus.ERROR);
		}

		// end of the first step

		// second step: sync the first half of the instances
		errorMsg = new StringBuilder()
				.append(
						"Action: commit; Step: sync the first part of cluster instances [")
				.append(firstPartStr).append("].").toString();
		long sessionId = rollingMonitor.createSession();
		for (int i = 0; i < middleIndex; i++) {
			int[] servers = clusterInfo.getServerIDs(instances[i]);
			try {
				SyncRequest syncRequest = deployFactory.createSyncRequest(
						currentClusterId, transactionId, syncItems,
						hasOfflineDeployment, sessionId, instances[i]);
				rollingMonitor.sendRequest(servers[0], syncRequest);
			} catch (MessagingException e) {
				errorMsg = new StringBuilder(errorMsg).append(Constants.EOL)
						.append("Error: Cannot sync instance ").append(
								instances[i]).append(": ").append(
								e.getMessage()).toString();
				commitIsFailed = true;
				break;
			}
		}

		RollingSession rollingSession = rollingMonitor.closeSession(sessionId);

		Set<InstanceDescriptor> failedInstanceDescs = new HashSet<InstanceDescriptor>();
		Collection<SyncRequest> syncRequestes = rollingSession
				.getUnansweredSentMessages().values();
		if (!syncRequestes.isEmpty()) {
			int[] notRespondInstances = new int[syncRequestes.size()];
			int i = 0;
			for (SyncRequest unansweredSyncRequest : syncRequestes) {
				int instanceId = unansweredSyncRequest.getSyncContext()
						.getInstanceId();
				failedInstanceDescs
						.add(clusterDscrFactory
								.createInstanceDescriptor(
										instanceId,
										new HashSet<ServerDescriptor>(),
										InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT,
										createTestInfo(instanceId),
										"Action: commit - the instance didn't confirm rolling sync operation."));
				notRespondInstances[i++] = instanceId;
			}
			errorMsg = new StringBuilder(errorMsg).append(Constants.EOL)
					.append("Error: Instances [").append(
							arrayToString(notRespondInstances, 0,
									notRespondInstances.length)).append(
							"] didn't confirm rolling sync operation.")
					.toString();
			commitIsFailed = true;
		}

		Iterator<SyncResult> syncResultItr = rollingSession
				.getReceivedMessages().values().iterator();
		ArrayList<Integer> failedInstances = new ArrayList<Integer>();
		while (syncResultItr.hasNext()) {
			SyncResult syncResult = syncResultItr.next();
			SyncException syncException = syncResult.getSyncException();
			if ((syncException != null)
					&& !(syncException instanceof SyncItemException)) {
				int instanceId = syncResult.getSyncContext().getInstanceId();
				failedInstanceDescs
						.add(clusterDscrFactory
								.createInstanceDescriptor(
										instanceId,
										new HashSet<ServerDescriptor>(),
										InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT,
										createTestInfo(instanceId),
										"Action: commit - sync operation failed on the instance. Error: "
												+ syncException.getMessage()));
				syncResultItr.remove();
				failedInstances.add(instanceId);
			}
		}

		if (!failedInstances.isEmpty()) {
			errorMsg = new StringBuilder(errorMsg).append(Constants.EOL)
					.append("Error: Sync operation failed on instances [")
					.append(
							arrayToString(failedInstances, 0, failedInstances
									.size())).append("].").toString();
			commitIsFailed = true;
		}

		Collection<SyncResult> syncResults = rollingSession
				.getReceivedMessages().values();
		for (SyncItem syncItem : syncItems) {
			Set<InstanceDescriptor> newItemInstanceDescs = new HashSet<InstanceDescriptor>(
					failedInstanceDescs);
			for (SyncResult syncResult : syncResults) {
				int instanceId = syncResult.getSyncContext().getInstanceId();
				InstanceDescriptor instanceDescriptor;
				SyncException syncException = syncResult.getSyncException();
				if ((syncException != null)
						&& (syncException instanceof SyncItemException)
						&& (((SyncItemException) syncException).getSyncItem()
								.equals(syncItem))) {
					instanceDescriptor = clusterDscrFactory
							.createInstanceDescriptor(
									instanceId,
									new HashSet<ServerDescriptor>(),
									InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT,
									createTestInfo(instanceId),
									"Action: commit - sync operation on the instance failed. Error: "
											+ syncException.getMessage());
				} else {
					try {
						instanceDescriptor = ddsManager.loadInstanceDescriptor(
								transactionId, syncItem, instanceId);
					} catch (DeplDataStorageNotFoundException e) {
						instanceDescriptor = clusterDscrFactory
								.createInstanceDescriptor(
										instanceId,
										new HashSet<ServerDescriptor>(),
										InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT,
										createTestInfo(instanceId),
										"Action: commit - sync operation on the instance failed. Error: "
												+ e.getMessage());
					} catch (DeplDataStorageException e) {
						instanceDescriptor = clusterDscrFactory
								.createInstanceDescriptor(
										instanceId,
										new HashSet<ServerDescriptor>(),
										InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT,
										createTestInfo(instanceId),
										"Action: commit - sync operation on the instance failed. Error: "
												+ e.getMessage());
					}
				}
				newItemInstanceDescs.add(instanceDescriptor);
				if (InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT
						.equals(instanceDescriptor.getInstanceStatus()))
					commitIsFailed = true;
			}
			Set<InstanceDescriptor> oldInstDescSet = itemsInstanceDescs
					.get(syncItem);
			newItemInstanceDescs.addAll(oldInstDescSet);
			itemsInstanceDescs.put(syncItem, newItemInstanceDescs);
		}

		if (commitIsFailed) {
			return makeRollingResult(dData, syncItems, itemsInstanceDescs,
					errorMsg, ClusterStatus.PRODUCTIVE_BUT_NEED_ROLL_BACK,
					DeployResultStatus.ERROR);
		}

		// end of the second step

		// third step: unbind the second half of the instances
		notifiedInstances.clear();
		for (int i = middleIndex; i < count; i++) {
			try {
				InstanceFailoverManager.getInstance().unbindInstance(
						instances[i]);
				notifiedInstances.add(instances[i]);
			} catch (CMException e) {
				errorMsg = new StringBuilder()
						.append(
								"Action: commit; Step: unbind the second part of cluster instances [")
						.append(secondPartStr).append("].").append(
								"Error: Cannot unbind instance ").append(
								instances[i]).append(":")
						.append(e.getMessage()).toString();
				commitIsFailed = true;
				break;
			}
		}
		notifiedInstDescs.clear();
		for (Integer notifiedInstance : notifiedInstances) {
			notifiedInstDescs.add(clusterDscrFactory.createInstanceDescriptor(
					notifiedInstance, new HashSet<ServerDescriptor>(),
					InstanceStatus.NOT_PRODUCTIVE_AND_NOT_UPDATED,
					createTestInfo(notifiedInstance), ""));
		}

		// reinit instance descriptor map
		for (SyncItem syncItem : syncItems) {
			Set<InstanceDescriptor> newInstDescSet = new HashSet<InstanceDescriptor>();
			newInstDescSet.addAll(notifiedInstDescs);
			Set<InstanceDescriptor> oldInstDescSet = itemsInstanceDescs
					.get(syncItem);
			newInstDescSet.addAll(oldInstDescSet);
			itemsInstanceDescs.put(syncItem, newInstDescSet);
		}

		if (commitIsFailed) {
			return makeRollingResult(dData, syncItems, itemsInstanceDescs,
					errorMsg, ClusterStatus.PRODUCTIVE_BUT_NEED_ROLL_BACK,
					DeployResultStatus.ERROR);
		}

		// end of the third step

		// fourth step: bind the fisrt half of the instances
		notifiedInstances.clear();

		try {
			InstanceFailoverManager.getInstance().bindInstance(
					currentInstanceId);
			notifiedInstances.add(currentInstanceId);
		} catch (CMException e) {
			errorMsg = new StringBuilder().append(
					"Action: commit; Step: bind the main instance [").append(
					currentInstanceId).append("].").append(
					"Error: Cannot bind the main instance ").append(
					currentInstanceId).append(":").append(e.getMessage())
					.toString();
			commitIsFailed = true;
		}

		if (!commitIsFailed) {
			for (int i = 0; i < middleIndex; i++) {
				try {
					InstanceFailoverManager.getInstance().bindInstance(
							instances[i]);
					notifiedInstances.add(instances[i]);
				} catch (CMException e) {
					errorMsg = new StringBuilder()
							.append(
									"Action: commit; Step: bind the first part of cluster instances [")
							.append(firstPartStr).append("].").append(
									"Error: Cannot bind instance ").append(
									instances[i]).append(":").append(
									e.getMessage()).toString();
					commitIsFailed = true;
					break;
				}
			}
			notifiedInstDescs.clear();
			for (Integer notifiedInstance : notifiedInstances) {
				notifiedInstDescs.add(clusterDscrFactory
						.createInstanceDescriptor(notifiedInstance,
								new HashSet<ServerDescriptor>(),
								InstanceStatus.PRODUCTIVE_AND_COMMITTED,
								createTestInfo(notifiedInstance), ""));
			}

			// reinit instance descriptor map
			for (SyncItem syncItem : syncItems) {
				Set<InstanceDescriptor> newInstDescSet = new HashSet<InstanceDescriptor>();
				newInstDescSet.addAll(notifiedInstDescs);
				Set<InstanceDescriptor> oldInstDescSet = itemsInstanceDescs
						.get(syncItem);
				newInstDescSet.addAll(oldInstDescSet);
				itemsInstanceDescs.put(syncItem, newInstDescSet);
			}
		}

		if (commitIsFailed) {
			return makeRollingResult(dData, syncItems, itemsInstanceDescs,
					errorMsg, ClusterStatus.PRODUCTIVE_BUT_NEED_ROLL_BACK,
					DeployResultStatus.ERROR);
		}

		// end of the fourth step

		// fifth step: sync the second half of the instances
		errorMsg = new StringBuilder()
				.append(
						"Action: commit; Step: sync the second part of cluster instances [")
				.append(secondPartStr).append("].").toString();
		sessionId = rollingMonitor.createSession();
		for (int i = middleIndex; i < count; i++) {
			int[] servers = clusterInfo.getServerIDs(instances[i]);
			try {
				SyncRequest syncRequest = deployFactory.createSyncRequest(
						currentClusterId, transactionId, syncItems,
						hasOfflineDeployment, sessionId, instances[i]);
				rollingMonitor.sendRequest(servers[0], syncRequest);
			} catch (MessagingException e) {
				errorMsg = new StringBuilder(errorMsg).append(Constants.EOL)
						.append("Error: Cannot sync instance ").append(
								instances[i]).append(": ").append(
								e.getMessage()).toString();
				commitIsFailed = true;
				break;
			}
		}

		rollingSession = rollingMonitor.closeSession(sessionId);

		failedInstanceDescs = new HashSet<InstanceDescriptor>();
		syncRequestes = rollingSession.getUnansweredSentMessages().values();
		if (!syncRequestes.isEmpty()) {
			int[] notRespondInstances = new int[syncRequestes.size()];
			int i = 0;
			for (SyncRequest unansweredSyncRequest : syncRequestes) {
				int instanceId = unansweredSyncRequest.getSyncContext()
						.getInstanceId();
				failedInstanceDescs
						.add(clusterDscrFactory
								.createInstanceDescriptor(
										instanceId,
										new HashSet<ServerDescriptor>(),
										InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT,
										createTestInfo(instanceId),
										"Action: commit - the instance didn't confirm rolling sync operation."));
				notRespondInstances[i++] = instanceId;
			}
			errorMsg = new StringBuilder(errorMsg).append(Constants.EOL)
					.append("Error: Instances [").append(
							arrayToString(notRespondInstances, 0,
									notRespondInstances.length)).append(
							"] didn't confirm rolling sync operation.")
					.toString();
			commitIsFailed = true;
		}

		syncResultItr = rollingSession.getReceivedMessages().values()
				.iterator();
		failedInstances = new ArrayList<Integer>();
		while (syncResultItr.hasNext()) {
			SyncResult syncResult = syncResultItr.next();
			SyncException syncException = syncResult.getSyncException();
			if ((syncException != null)
					&& !(syncException instanceof SyncItemException)) {
				int instanceId = syncResult.getSyncContext().getInstanceId();
				failedInstanceDescs
						.add(clusterDscrFactory
								.createInstanceDescriptor(
										instanceId,
										new HashSet<ServerDescriptor>(),
										InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT,
										createTestInfo(instanceId),
										"Action: commit - sync operation failed on the instance. Error: "
												+ syncException.getMessage()));
				syncResultItr.remove();
				failedInstances.add(instanceId);
			}
		}

		if (!failedInstances.isEmpty()) {
			errorMsg = new StringBuilder(errorMsg).append(Constants.EOL)
					.append("Error: Sync operation failed on instances [")
					.append(
							arrayToString(failedInstances, 0, failedInstances
									.size())).append("].").toString();
			commitIsFailed = true;
		}

		syncResults = rollingSession.getReceivedMessages().values();
		for (SyncItem syncItem : syncItems) {
			Set<InstanceDescriptor> newItemInstanceDescs = new HashSet<InstanceDescriptor>(
					failedInstanceDescs);
			for (SyncResult syncResult : syncResults) {
				int instanceId = syncResult.getSyncContext().getInstanceId();
				InstanceDescriptor instanceDescriptor;
				SyncException syncException = syncResult.getSyncException();
				if ((syncException != null)
						&& (syncException instanceof SyncItemException)
						&& (((SyncItemException) syncException).getSyncItem()
								.equals(syncItem))) {
					instanceDescriptor = clusterDscrFactory
							.createInstanceDescriptor(
									instanceId,
									new HashSet<ServerDescriptor>(),
									InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT,
									createTestInfo(instanceId),
									"Action: commit - sync operation on the instance failed. Error: "
											+ syncException.getMessage());
				} else {
					try {
						instanceDescriptor = ddsManager.loadInstanceDescriptor(
								transactionId, syncItem, instanceId);
					} catch (DeplDataStorageNotFoundException e) {
						instanceDescriptor = clusterDscrFactory
								.createInstanceDescriptor(
										instanceId,
										new HashSet<ServerDescriptor>(),
										InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT,
										createTestInfo(instanceId),
										"Action: commit - sync operation on the instance failed. Error: "
												+ e.getMessage());
					} catch (DeplDataStorageException e) {
						instanceDescriptor = clusterDscrFactory
								.createInstanceDescriptor(
										instanceId,
										new HashSet<ServerDescriptor>(),
										InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT,
										createTestInfo(instanceId),
										"Action: commit - sync operation on the instance failed. Error: "
												+ e.getMessage());
					}
				}
				newItemInstanceDescs.add(instanceDescriptor);
				if (InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT
						.equals(instanceDescriptor.getInstanceStatus()))
					commitIsFailed = true;
			}
			Set<InstanceDescriptor> oldInstDescSet = itemsInstanceDescs
					.get(syncItem);
			newItemInstanceDescs.addAll(oldInstDescSet);
			itemsInstanceDescs.put(syncItem, newItemInstanceDescs);
		}

		if (commitIsFailed) {
			return makeRollingResult(dData, syncItems, itemsInstanceDescs,
					errorMsg, ClusterStatus.PRODUCTIVE_BUT_NEED_ROLL_BACK,
					DeployResultStatus.ERROR);
		}

		// end of the fifth step

		// sixth step: bind the fisrt half of the instances
		notifiedInstances.clear();
		for (int i = middleIndex; i < count; i++) {
			try {
				InstanceFailoverManager.getInstance()
						.bindInstance(instances[i]);
				notifiedInstances.add(instances[i]);
			} catch (CMException e) {
				errorMsg = new StringBuilder()
						.append(
								"Action: commit; Step: bind the second part of cluster instances [")
						.append(firstPartStr).append("].").append(
								"Error: Cannot bind instance ").append(
								instances[i]).append(":")
						.append(e.getMessage()).toString();
				commitIsFailed = true;
				break;
			}
		}
		notifiedInstDescs.clear();
		for (Integer notifiedInstance : notifiedInstances) {
			notifiedInstDescs.add(clusterDscrFactory.createInstanceDescriptor(
					notifiedInstance, new HashSet<ServerDescriptor>(),
					InstanceStatus.PRODUCTIVE_AND_COMMITTED,
					createTestInfo(notifiedInstance), ""));
		}

		// reinit instance descriptor map
		for (SyncItem syncItem : syncItems) {
			Set<InstanceDescriptor> newInstDescSet = new HashSet<InstanceDescriptor>();
			newInstDescSet.addAll(notifiedInstDescs);
			Set<InstanceDescriptor> oldInstDescSet = itemsInstanceDescs
					.get(syncItem);
			newInstDescSet.addAll(oldInstDescSet);
			itemsInstanceDescs.put(syncItem, newInstDescSet);
		}

		if (commitIsFailed) {
			return makeRollingResult(dData, syncItems, itemsInstanceDescs,
					errorMsg, ClusterStatus.PRODUCTIVE_BUT_NEED_ROLL_BACK,
					DeployResultStatus.ERROR);
		}

		// end of the sixth step

		// TODO: if the commit is successful set mode=NORMAL
		DeployResult rollingResult = makeRollingResult(dData, syncItems,
				itemsInstanceDescs, "Action Commit finished SUCCESSFULLY.",
				ClusterStatus.PRODUCTIVE_AND_COMMITED,
				DeployResultStatus.SUCCESS);

		final DCLockManager lock = DCLockManagerFactory.getInstance()
				.createDCLockManager();
		final LockAction deployAction = LockAction.DEPLOY;
		lock.unlockEnqueue(deployAction, dData.getLockData());
		return rollingResult;
	}

	private DeployResult makeRollingResult(DeploymentData dData,
			Collection<SyncItem> syncItems,
			Map<SyncItem, Set<InstanceDescriptor>> itemsInstanceDescs,
			String msg, ClusterStatus clusterStatus,
			DeployResultStatus deployResultStatus) throws DeploymentException {
		ClusterDscrFactory clustDscrFactory = ClusterDscrFactory.getInstance();
		for (SyncItem syncItem : syncItems) {
			Set<InstanceDescriptor> syncItemInstDescs = itemsInstanceDescs
					.get(syncItem);
			SduId sduId = syncItem.getBatchItemId().getSduId();
			DeploymentBatchItem dbItem = dData.getDeploymentBatch()
					.getDeploymentBatchItem(sduId.getName(), sduId.getVendor());
			ClusterDescriptor oldSyncItemClusterDescriptor = dbItem
					.getClusterDescriptor();
			ClusterDescriptor newSyncItemClusterDesc = clustDscrFactory
					.createClusterDescriptor(syncItemInstDescs, clusterStatus,
							oldSyncItemClusterDescriptor.getRollingInfo());
			dbItem.setClusterDescriptor(newSyncItemClusterDesc);
		}
		try {
			final ConfigurationHandlerFactory cfgFactory = getConfigurationHandlerFactory();
			final DeploymentDataStorageManager ddsManager = getDeploymentDataStorageManager(cfgFactory);

			ddsManager.persistDeploymentData(dData);
		} catch (DeplDataStorageException ddse) {
			throw new DeploymentException(
					DCResourceAccessor
							.getInstance()
							.getMessageText(
									DCExceptionConstants.DEPLOYMENT_DATA_PERSISTENCE_ERROR),
					ddse);
		}

		return new RollingResultImpl(dData.getDeploymentBatch()
				.getDeploymentBatchItems(), dData
				.getSortedDeploymentBatchItem(), deployResultStatus, msg, null);
	}

	private String arrayToString(final int[] instances, final int beginIndex,
			final int endIndex) {
		if (endIndex <= beginIndex)
			return "";
		StringBuilder firstPartBuilder = new StringBuilder();
		for (int i = beginIndex; i < endIndex - 1; i++) {
			firstPartBuilder.append(instances[i]).append(",");
		}
		firstPartBuilder.append(instances[endIndex - 1]);
		return firstPartBuilder.toString();
	}

	private String arrayToString(final List<Integer> instances,
			final int beginIndex, final int endIndex) {
		if (endIndex <= beginIndex)
			return "";
		StringBuilder firstPartBuilder = new StringBuilder();
		for (int i = beginIndex; i < endIndex - 1; i++) {
			firstPartBuilder.append(instances.get(i)).append(",");
		}
		firstPartBuilder.append(instances.get(endIndex - 1));
		return firstPartBuilder.toString();
	}

	/**
	 * @deprecated The method will only be used for proofing the concept in the
	 *             prototyping phase.
	 * 
	 */
	public DeployResult rollback(final String transactionId,
			final String[] archiveFilePathNames) throws ValidationException,
			DeploymentException, DCLockException, RollingDeployException {

		if (archiveFilePathNames.length > 1)
			throw new RollingDeployException(
					"Given archives are "
							+ archiveFilePathNames.length
							+ ". Only one component can be rollbacked with ROLLING strategy.");

		doCheckSessionId(transactionId);
		checkForAvailablility();

		ClusterMonitor clusterMonitor = ServiceConfigurer.getInstance()
				.getClusterMonitor();
		ClusterInfo clusterInfo = ClusterUtils
				.getClusterInfoWithThis(clusterMonitor);
		checkReadinessForRollingPatch(clusterInfo);
		DeploymentData dData;
		try {
			dData = getDeploymentData(transactionId);
			if (!DeployWorkflowStrategy.ROLLING.equals(dData
					.getDeployWorkflowStrategy()))
				throw new RollingDeployException(
						"Rollback operation failed. Transaction "
								+ transactionId
								+ "is not triggered with ROLLING strategy.");
		} catch (DeplDataStorageNotFoundException e) {
			throw new RollingDeployException(
					"Rollback operation failed due to lack of deployment data for transaction Id: "
							+ transactionId, e);
		} catch (DeplDataStorageException e) {
			throw new RollingDeployException(
					"Rollback operation failed due to an error during reading of deployment data for transaction Id: "
							+ transactionId, e);
		}
		DeployResult deployResult = DeployResultBuilder.getInstance().build(
				dData);
		// make rolling patch batch
		HashSet<ClusterStatus> clustStatuses = new HashSet<ClusterStatus>();
		clustStatuses.add(ClusterStatus.PRODUCTIVE_BUT_NEED_VALIDATION);
		clustStatuses.add(ClusterStatus.PRODUCTIVE_BUT_NEED_ROLL_BACK);
		Collection<SyncItem> syncItems = getRollingPatchItems(dData
				.getSortedDeploymentBatchItem(), clustStatuses);

		if (syncItems.size() == 0)
			throw new RollingDeployException(
					"There is not items for rollback in the deployment batch of transaction: "
							+ transactionId);

		ClusterElement currentParticipant = clusterMonitor
				.getCurrentParticipant();
		int currentClusterId = currentParticipant.getClusterId();
		int currentInstanceId = currentParticipant.getGroupId();

		ClusterDscrFactory clusterDscrFactory = ClusterDscrFactory
				.getInstance();

		// init instance descriptor map witch contains instance descriptors for
		// every sync item
		// current instance descriptor is reinit with new state -
		// from NOT_PRODUCTIVE_AND_NEED_VALIDATION to NOT_PRODUCTIVE_AND_UPDATED
		Map<SyncItem, Set<InstanceDescriptor>> itemsInstanceDescs = new HashMap<SyncItem, Set<InstanceDescriptor>>();
		for (SyncItem syncItem : syncItems) {
			SduId sduId = syncItem.getBatchItemId().getSduId();
			DeploymentBatchItem dbItem = dData.getDeploymentBatch()
					.getDeploymentBatchItem(sduId.getName(), sduId.getVendor());
			Set<InstanceDescriptor> instDescs = dbItem.getClusterDescriptor()
					.getInstanceDescriptors();
			Set<InstanceDescriptor> newInstDescs = new HashSet<InstanceDescriptor>();
			for (InstanceDescriptor instDesc : instDescs) {
				if ((instDesc.getInstanceID() == currentInstanceId)
						&& InstanceStatus.NOT_PRODUCTIVE_AND_NEED_VALIDATION
								.equals(instDesc.getInstanceStatus())) {
					InstanceDescriptor newInstDesc = clusterDscrFactory
							.createInstanceDescriptor(instDesc.getInstanceID(),
									instDesc.getServerDescriptors(),
									InstanceStatus.NOT_PRODUCTIVE_AND_UPDATED,
									instDesc.getTestInfo(), instDesc
											.getDescription());
					newInstDescs.add(newInstDesc);
				} else {
					newInstDescs.add(instDesc);
				}
			}
			itemsInstanceDescs.put(syncItem, newInstDescs);
		}

		boolean hasOfflineDeployment = DeploymentBatchValidator.getInstance()
				.isOfflinePhaseScheduled(
						deployResult.getSortedDeploymentBatchItems());

		logArchives(archiveFilePathNames);
		DeploymentBatch deploymentBatch = loadArchives(transactionId,
				archiveFilePathNames);
		doCheckDeploymentBatch(deploymentBatch);

		this.batchFilterProcessor.applyBatchFilters(deploymentBatch);

		// TODO: should filtered all items that it is not needed to be deploy
		// again with appropriate description

		// final ErrorStrategy deploymentErrorStrategy =
		// this.getErrorStrategy(ErrorStrategyAction.DEPLOYMENT_ACTION);

		final PrerequisitesValidator prerequisitesValidator = getPrerequisitesValidator(getSoftwareTypeService());
		prerequisitesValidator.doValidate(deploymentBatch);
		final SoftwareTypeService softwareTypeService = prerequisitesValidator
				.getNewSoftwareTypeService();

		// this list will contain the admitted and resolved dep
		// the operation will adjust the status of the items with unresolved
		// dependencies
		List<DeploymentItem> sortedByDependency = resolveDeploymentBatch(
				deploymentBatch, softwareTypeService);
		// if there are errors and we will get this far the error strategy is
		// skip depending
		prerequisitesValidator.applyTheErrorStrategyAfterCheck(deploymentBatch);
		final Collection<DeploymentBatchItem> sortedAdmittedDeploymentItems = sortDeploymentBatch(
				deploymentBatch, sortedByDependency, softwareTypeService);
		removeRepeatedDeploymentItems(sortedAdmittedDeploymentItems);

		Iterator<DeploymentBatchItem> newSortedItems = sortedAdmittedDeploymentItems
				.iterator();
		while (newSortedItems.hasNext()) {
			newSortedItems.next().getBatchItemId().getSduId();
		}

		// TODO: rolling - set mode=NORMAL, make deploy result again and unlock
		// enqueue locks
		return null;
	}

	private void checkReadinessForRollingPatch(final ClusterInfo clusterInfo)
			throws RollingDeployException {
		int instanceCount = clusterInfo.getGroupIDs().length;
		if (instanceCount < MIN_AVAILABLE_INSTANCES)
			throw new RollingDeployException(
					"There is only "
							+ instanceCount
							+ " instance in the cluster, but to perform rolling at least "
							+ MIN_AVAILABLE_INSTANCES + " are needed.");
	}

	private void doRolling(final DeploymentData deploymentData,
			final LockData lockData, final DCLockManager lock,
			final LockAction deployAction) throws DCLockException,
			RollingDeployException {
		// TODO:rolling - add rolling lock in order to allow only one rolling
		// update
		final int instanceId = ServiceConfigurer.getInstance()
				.getClusterMonitor().getCurrentParticipant().getGroupId();// may
		// be
		// should
		// moved
		// after
		// if
		if (!DeployWorkflowStrategy.ROLLING.equals(deploymentData
				.getDeployWorkflowStrategy())) {
			return;
		}

		try {
			InstanceFailoverManager.getInstance().unbindInstance(instanceId);
		} catch (CMException e) {
			// TODO: add error code
			lock.unlockEnqueue(deployAction, lockData);
			throw new RollingDeployException(
					"ASJ.dpl_dc.003465 The instance cannot be unbinded from the cluster: ",
					e);
		}
		ClusterDscrFactory clusterDscrFactory = ClusterDscrFactory
				.getInstance();
		ClusterMonitor clusterMonitor = ServiceConfigurer.getInstance()
				.getClusterMonitor();
		ClusterInfo clusterInfo = ClusterUtils
				.getClusterInfoWithThis(clusterMonitor);
		int[] allInstances = clusterInfo.getGroupIDs();
		Set<InstanceDescriptor> instanceDescs = new HashSet<InstanceDescriptor>();
		int currentInstance = clusterMonitor.getCurrentParticipant()
				.getGroupId();
		instanceDescs.add(clusterDscrFactory.createInstanceDescriptor(
				currentInstance, new HashSet<ServerDescriptor>(),
				InstanceStatus.NOT_PRODUCTIVE_AND_NOT_UPDATED,
				createTestInfo(instanceId), ""));
		Set<InstanceDescriptor> instanceDescrSet = new HashSet<InstanceDescriptor>();
		for (int i = 0; i < allInstances.length; i++) {
			instanceDescrSet.add(clusterDscrFactory.createInstanceDescriptor(
					allInstances[i], new HashSet<ServerDescriptor>(),
					InstanceStatus.PRODUCTIVE_BUT_NOT_UPDATED,
					createTestInfo(allInstances[i]), ""));
		}
		instanceDescs.addAll(instanceDescrSet);
		ClusterDescriptor clusterDescriptor = clusterDscrFactory
				.createClusterDescriptor(instanceDescs,
						ClusterStatus.PRODUCTIVE_BUT_NEED_ROLL_BACK, null);
		Enumeration admittedItems = deploymentData.getDeploymentBatch()
				.getAdmittedDeploymentBatchItems();
		while (admittedItems.hasMoreElements()) {
			DeploymentItem dplItem = (DeploymentItem) admittedItems
					.nextElement();
			dplItem.setClusterDescriptor(clusterDescriptor);
		}
	}

	public void setOnlineDeployemtOfCoreComponents(boolean value) {

		this.onlineDeploymentOfCoreComponents = value;

	}

}
