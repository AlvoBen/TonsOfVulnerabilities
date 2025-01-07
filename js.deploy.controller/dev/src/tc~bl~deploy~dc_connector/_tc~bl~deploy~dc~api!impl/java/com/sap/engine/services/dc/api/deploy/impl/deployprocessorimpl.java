/*
 * Created on Oct 17, 2004
 */
package com.sap.engine.services.dc.api.deploy.impl;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

import com.sap.engine.services.dc.api.APIException;
import com.sap.engine.services.dc.api.AuthenticationException;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.ErrorStrategy;
import com.sap.engine.services.dc.api.ErrorStrategyAction;
import com.sap.engine.services.dc.api.ServiceNotAvailableException;
import com.sap.engine.services.dc.api.cluster.ClusterObserver;
import com.sap.engine.services.dc.api.cluster.RemoteClusterListenerImpl;
import com.sap.engine.services.dc.api.deploy.AllItemsAlreadyDeployedValidaionException;
import com.sap.engine.services.dc.api.deploy.AllItemsFilteredValidaionException;
import com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule;
import com.sap.engine.services.dc.api.deploy.CrcValidationException;
import com.sap.engine.services.dc.api.deploy.DeployException;
import com.sap.engine.services.dc.api.deploy.DeployItem;
import com.sap.engine.services.dc.api.deploy.DeployProcessor;
import com.sap.engine.services.dc.api.deploy.DeployResult;
import com.sap.engine.services.dc.api.deploy.DeployResultNotFoundException;
import com.sap.engine.services.dc.api.deploy.DeployResultStatus;
import com.sap.engine.services.dc.api.deploy.DeploySettings;
import com.sap.engine.services.dc.api.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.api.deploy.EngineTimeoutException;
import com.sap.engine.services.dc.api.deploy.LifeCycleDeployStrategy;
import com.sap.engine.services.dc.api.deploy.RollingException;
import com.sap.engine.services.dc.api.deploy.TransportException;
import com.sap.engine.services.dc.api.deploy.ValidationException;
import com.sap.engine.services.dc.api.event.ClusterEvent;
import com.sap.engine.services.dc.api.event.ClusterEventAction;
import com.sap.engine.services.dc.api.event.DeploymentEvent;
import com.sap.engine.services.dc.api.event.DeploymentEventAction;
import com.sap.engine.services.dc.api.event.DeploymentListener;
import com.sap.engine.services.dc.api.event.EventMode;
import com.sap.engine.services.dc.api.event.ListenerMode;
import com.sap.engine.services.dc.api.filters.BatchFilter;
import com.sap.engine.services.dc.api.filters.SoftwareTypeBatchFilter;
import com.sap.engine.services.dc.api.impl.IRemoteReferenceHandler;
import com.sap.engine.services.dc.api.lock_mng.AlreadyLockedException;
import com.sap.engine.services.dc.api.model.ScaId;
import com.sap.engine.services.dc.api.model.SdaId;
import com.sap.engine.services.dc.api.model.SduId;
import com.sap.engine.services.dc.api.session.Session;
import com.sap.engine.services.dc.api.spi.ConnectionInfo;
import com.sap.engine.services.dc.api.spi.criticalshutdown.CriticalShutdownListener;
import com.sap.engine.services.dc.api.spi.criticalshutdown.CriticalShutdownListenerRegistry;
import com.sap.engine.services.dc.api.spi.criticalshutdown.Failure;
import com.sap.engine.services.dc.api.util.DAConstants;
import com.sap.engine.services.dc.api.util.DALog;
import com.sap.engine.services.dc.api.util.DAResultUtils;
import com.sap.engine.services.dc.api.util.DAUtils;
import com.sap.engine.services.dc.api.util.DeployApiMapper;
import com.sap.engine.services.dc.api.util.LimitingTimer;
import com.sap.engine.services.dc.api.util.LogUtil;
import com.sap.engine.services.dc.api.util.MeasurementUtils;
import com.sap.engine.services.dc.api.util.ServiceTimeWatcher;
import com.sap.engine.services.dc.api.util.ValidateUtils;
import com.sap.engine.services.dc.api.util.exception.APIExceptionConstants;
import com.sap.engine.services.dc.api.util.measurement.DAMeasurement;
import com.sap.engine.services.dc.cm.CM;
import com.sap.engine.services.dc.cm.CMException;
import com.sap.engine.services.dc.cm.DCNotAvailableException;
import com.sap.engine.services.dc.cm.deploy.Deployer;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.cm.lock.DCLockException;
import com.sap.engine.services.dc.cm.utils.filters.RemoteBatchFilterFactory;
import com.sap.engine.services.dc.cm.utils.measurement.DMeasurement;
import com.sap.engine.services.dc.manage.DCState;
import com.sap.engine.services.rmi_p4.P4ConnectionException;
import com.sap.engine.services.rmi_p4.P4IOException;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.P4RuntimeException;

/**
 * @author Georgi Danov
 * @author Boris Savov
 * @author Todor Stoitsev
 * 
 */
public final class DeployProcessorImpl extends ClusterObserver implements
		DeployProcessor, IRemoteReferenceHandler {

	protected final static String PROGRESS_SDA = " (sda)";
	protected final static String PROGRESS_SCA = " (SCA)";
	protected final static String PROGRESS_NO_SDU = " (noSDU)";

	private final String traceInfo_waitForOfflineDeploy = "DeployProcessor::waitResultForOfflineDeploy::";
	private static long internalCounter = 0;
	private final Session session;
	private long deployerId;

	private DeployItem[] items;

	private final DeploymentListenerMediator deploymentListenerMediator;

	private Hashtable offlineDeployments = new Hashtable();
	private long customServerTimeout = -1;
	private final String deployerIdInfo;
	private String lastDeploymentTransactionId = null;
	private Deployer m_deployer;
	private DeploySettings deploySettings = new DeploySettingsImpl();
	private boolean isDeploying = false;
	private Object oDplMonitor = new Object();
	private CM cm;

	// remote references to be handled within an instance of this class
	private Set remoteRefs = new HashSet();
	// a flag indicating whether the time statistics are enabled
	private boolean isTimeStatEnabled = true;

	ValidateUtils validateUtils;

	private DeployException criticalShutdownException;

	public DeployProcessorImpl(Session session) throws ConnectionException,
			DeployException {
		super(session.getLog());
		this.session = session;
		// add the instance as a remote reference handler to the session
		this.session.addRemoteReferenceHandler(this);
		synchronized (DeployProcessorImpl.class) {
			this.deployerId = ++internalCounter;
			if (internalCounter == Long.MAX_VALUE) {
				internalCounter = 0;
			}
			this.deployerIdInfo = new String("[ deployerId=" + this.deployerId
					+ " ]");
			if (daLog.isDebugTraceable()) {
				this.daLog.traceDebug("Deploy processor created [{0}]",
						new Object[] { this.deployerIdInfo });
			}
		}
		validateUtils = new ValidateUtils(this.session, this.deployerIdInfo);
		try {
			if (daLog.isDebugTraceable()) {
				this.daLog
						.traceDebug("Getting deploy controller's component manager");
			}
			this.cm = this.session.createCM();

			if (daLog.isPathTraceable()) {
				this.daLog.tracePath("get deployer from server. [{1}]",
						new Object[] { this.deployerIdInfo });
			}

			ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();
			m_deployer = cm.getDeployer();
			// register the reference to the obtained remote object
			registerRemoteReference(m_deployer);

			this.deploymentListenerMediator = new DeploymentListenerMediator(
					this.daLog, m_deployer);

			if (daLog.isPathTraceable()) {
				this.daLog.tracePath("deployer get successfully: [{1}. [{2}]]",
						new Object[] {
								serviceTimeWatcher.getElapsedTimeAsString(),
								this.deployerIdInfo });
			}

		} catch (CMException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			throw new DeployException(null, null, this.daLog.getLocation(),
					APIExceptionConstants.DC_CM_EXCEPTION, new String[] {
							exceptionName,
							", An error occurred during the deployer retrieval. Cause ="
									+ e.getMessage() }, e);
		}
	}

	public DeployResult deploy(DeployItem[] deployItems)
			throws ConnectionException, DeployResultNotFoundException,
			TransportException, ValidationException, EngineTimeoutException,
			DeployException, AlreadyLockedException, RollingException {

		if (deployItems == null) {
			throw new IllegalArgumentException(
					"[ERROR CODE DPL.DCAPI.1026] Deploy Items should not be null.");
		}

		// check if instance is already deploying
		synchronized (oDplMonitor) {
			if (this.isDeploying) {
				throw new RuntimeException(
						"[ERROR CODE DPL.DCAPI.1025] Concurrent error! A deploy operation is already running in this deploy processor");
			} else {
				this.isDeploying = true;

			}
		}
		this.items = deployItems;

		// assumes that sesion is already connected
		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();
		if (daLog.isInfoTraceable()) {
			this.daLog
					.traceInfo(
							"ASJ.dpl_api.001011",
							"+++++ Starting  D E P L O Y action +++++ [{0}] [ timerId={1} ]",
							new Object[] { this.deployerIdInfo,
									new Long(serviceTimeWatcher.getId()) });
		}

		try {

			validateUtils
					.logDeployInfo(deployItems, deploySettings, true, 0, 0);
			if (daLog.isDebugTraceable()) {
				this.daLog
						.traceDebug("Getting deploy controller's component manager");
			}

			String transactionId = null;
			try {
				if (daLog.isPathTraceable()) {
					this.daLog.tracePath("going to generate Session id. [{0}]",
							new Object[] { this.deployerIdInfo });
				}
				transactionId = cm.generateSessionId();
				this.lastDeploymentTransactionId = transactionId;
				if (this.daLog.isInfoTraceable()) {
					this.daLog
							.traceInfo("ASJ.dpl_api.001012",
									"Got Session id=[{0}],time:[{1}]. [{2}]",
									new Object[] {
											transactionId,
											serviceTimeWatcher
													.getElapsedTimeAsString(),
											this.deployerIdInfo });
				}
			} catch (CMException cme) {
				this.daLog.logThrowable("ASJ.dpl_api.001013",
						"Exception while generating ID: [{0}]", cme,
						new Object[] { cme.getMessage() });
				throw new DeployException(null, deployItems, this.daLog
						.getLocation(), APIExceptionConstants.DC_CM_EXCEPTION,
						new String[] { DAUtils.getThrowableClassName(cme),
								cme.getMessage() }, cme);
			}

			try {
				this.deploymentListenerMediator.deploymentStarted(
						transactionId, this.items);
				return deployItems(cm, transactionId, deployItems, false);
			} finally {
				this.deploymentListenerMediator.deploymentFinished();
			}

		} catch (AuthenticationException e) {
			throw new DeployException(null, deployItems, this.daLog
					.getLocation(),
					APIExceptionConstants.DC_DEPLOYMENT_EXCEPTION,
					new String[] { DAUtils.getThrowableClassName(e),
							e.getMessage() }, e);
		} finally {
			synchronized (oDplMonitor) {
				isDeploying = false;
			}
			if (daLog.isInfoTraceable()) {
				this.daLog
						.traceInfo(
								"ASJ.dpl_api.001014",
								"+++++ End  D E P L O Y action +++++ [{0}]. Total time: [{1}]",
								new Object[] {
										this.deployerIdInfo,
										serviceTimeWatcher
												.getTotalElapsedTimeAsString() });
			}
		}
	}

	public DeployResult commit(String transactionId)
			throws ConnectionException, EngineTimeoutException, APIException {
		// check if instance is already deploying
		synchronized (oDplMonitor) {
			if (isDeploying) {
				throw new RuntimeException(
						"[ERROR CODE DPL.DCAPI.1166] Concurrent error! A deploy operation is already running in this deploy processor");
			} else {
				isDeploying = true;
			}
		}
		// assumes that sesion is already connected
		// check the args
		if (transactionId == null) {
			throw new IllegalArgumentException("Session id cannot be null");
		}

		if (transactionId.trim().length() == 0) {
			throw new IllegalArgumentException("Session id cannot be emty");
		}

		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();
		this.daLog
				.logInfo(
						"ASJ.dpl_api.001015",
						"+++++ Starting  C O M M I T ( transactionID: [{0}] ) action +++++ [{1}] [ timerId={2}]",
						new Object[] { transactionId, this.deployerIdInfo,
								new Long(serviceTimeWatcher.getId()) });
		try {
			CM localCm = getCMinWorkingState();

			if (daLog.isDebugLoggable()) {
				this.daLog.logDebug(
						"going to commit transaction with ID: [{}] [{1}]",
						new Object[] { transactionId, this.deployerIdInfo });
			}
			com.sap.engine.services.dc.cm.deploy.DeployResult remoteDeployResult = localCm
					.getDeployer().commit(transactionId);
			Map fakeUploadedDeployItemsMap = new Hashtable();
			DeployItem[] fakeDeployItems = getFakeDeployItems(
					remoteDeployResult.getDeploymentItems(),
					fakeUploadedDeployItemsMap);
			if (daLog.isDebugLoggable()) {
				this.daLog.logDebug(
						"Preparing the result for transactrionID: [{0}] [{1}]",
						new Object[] { transactionId, this.deployerIdInfo });
			}
			DeployResult deployResult = parseDeployResult(transactionId,
					remoteDeployResult, fakeDeployItems,
					fakeUploadedDeployItemsMap);
			logDeployResult(deployResult, serviceTimeWatcher, transactionId);
			return deployResult;
		} catch (com.sap.engine.services.dc.cm.deploy.RollingDeployException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog.logError("ASJ.dpl_api.001017",
					"Rolling Exception during commit phase [{0}],cause=[{1}]",
					new Object[] { exceptionName, e.getMessage() });
			throw new RollingException(this.daLog.getLocation(),
					APIExceptionConstants.DC_DEPLOYMENT_EXCEPTION,
					new String[] { exceptionName, e.getMessage() }, e);
		} catch (DeploymentException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001018",
						"Commit operation failed [{0}].Reason [{1}]",
						new Object[] { exceptionName, e.getMessage() });
			}
			throw new DeployException(null, null, this.daLog.getLocation(),
					APIExceptionConstants.DC_DEPLOYMENT_EXCEPTION,
					new String[] { exceptionName, e.getMessage() }, e);
		} catch (com.sap.engine.services.dc.cm.deploy.DeployResultNotFoundException e) {
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001019",
						"Commit operation failed. Reason [{0}]",
						new Object[] { e.getMessage() });
			}
			throw new DeployResultNotFoundException(null, this.daLog
					.getLocation(),
					APIExceptionConstants.DC_DEPLOYRESULTNOTFOUND_EXCEPTION,
					new String[] { e.getMessage() }, e);
		} catch (CMException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001020",
						"Commit operation failed [{0}].Reason [{1}]",
						new Object[] { exceptionName, e.getMessage() });
			}
			throw new DeployException(null, null, this.daLog.getLocation(),
					APIExceptionConstants.DC_CM_EXCEPTION, new String[] {
							exceptionName, e.getMessage() }, e);
		} finally {
			synchronized (oDplMonitor) {
				isDeploying = false;
			}
			if (daLog.isPathLoggable()) {
				this.daLog
						.logPath(
								"+++++ End  C O M M I T ( transactionID: [{0}] )  action +++++ [{1}]. Total time:[{2}]",
								new Object[] {
										transactionId,
										this.deployerIdInfo,
										serviceTimeWatcher
												.getTotalElapsedTimeAsString() });
			}
		}

	}

	public DeployResult rollback(String transactionId, DeployItem[] deployItems)
			throws ConnectionException, DeployResultNotFoundException,
			TransportException, ValidationException, EngineTimeoutException,
			DeployException, AlreadyLockedException, RollingException,
			APIException {
		// check if instance is already deploying
		synchronized (oDplMonitor) {
			if (isDeploying) {
				throw new RuntimeException(
						"[ERROR CODE DPL.DCAPI.1067] Concurrent error! A deploy operation is already running in this deploy processor");
			} else {
				isDeploying = true;
			}
		}
		// assumes that sesion is already connected
		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();
		this.daLog
				.logInfo(
						"ASJ.dpl_api.001021",
						"+++++ Starting  R O L L B A C K action +++++ [{0}] [ timerId={1}]",
						new Object[] { this.deployerIdInfo,
								new Long(serviceTimeWatcher.getId()) });
		try {
			if (deployItems == null) {
				throw new IllegalArgumentException(
						"[ERROR CODE DPL.DCAPI.1168] Deploy Items should not be null.");
			}
			validateUtils
					.logDeployInfo(deployItems, deploySettings, true, 0, 0);
			if (daLog.isDebugTraceable()) {
				this.daLog
						.traceDebug("Getting deploy controller's component manager");
			}

			return deployItems(cm, transactionId, deployItems, true);
		} catch (AuthenticationException e) {
			throw new DeployException(null, deployItems, this.daLog
					.getLocation(),
					APIExceptionConstants.DC_DEPLOYMENT_EXCEPTION,
					new String[] { DAUtils.getThrowableClassName(e),
							e.getMessage() }, e);
		} finally {
			synchronized (oDplMonitor) {
				isDeploying = false;
			}
			if (daLog.isPathLoggable()) {
				this.daLog
						.logPath(
								"+++++ End  R O L L B A C K action +++++ [{0}]. Total time:[{1}]",
								new Object[] {
										this.deployerIdInfo,
										serviceTimeWatcher
												.getTotalElapsedTimeAsString() });
			}
		}
	}

	private Deployer internalInitDeployer(Deployer deployer, CM cm,
			DeployItem[] deployItems) throws DeployException {
		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();

		try {
			RemoteBatchFilterFactory remBatchFilterFactory = cm
					.getRemoteBatchFilterFactory();
			// register the reference to the obtained remote object
			registerRemoteReference(remBatchFilterFactory);

			RemoteBatchFilterVisitor remoteBatchFilterVisitor = new RemoteBatchFilterVisitor(
					this.daLog, remBatchFilterFactory);

			{// componentVersionHandlingRule
				if (getComponentVersionHandlingRule() != null) {
					deployer
							.setComponentVersionHandlingRule(DeployMapper
									.mapComponentVersionHandlingRule(getComponentVersionHandlingRule()));
				}
			}

			{// deployWorkflowStrategy
				if (getDeployWorkflowStrategy() != null) {
					deployer
							.setDeployWorkflowStrategy(DeployMapper
									.mapDeployWorkflowStrategy(getDeployWorkflowStrategy()));
				}
			}

			{// lifeCycleDeployStrategy
				if (getLifeCycleDeployStrategy() != null) {
					deployer
							.setLifeCycleDeployStrategy(DeployMapper
									.mapLifeCycleDeployStrategy(getLifeCycleDeployStrategy()));
				}
			}
			{// errorStrategies
				final Map errorStrategies = deploySettings.getErrorStrategies();
				if (!errorStrategies.entrySet().isEmpty()) {
					Map.Entry nextEntry;
					com.sap.engine.services.dc.api.ErrorStrategyAction errorAction;
					ErrorStrategy errorStrategy;
					for (Iterator iterator = errorStrategies.entrySet()
							.iterator(); iterator.hasNext();) {
						nextEntry = (Map.Entry) iterator.next();
						errorAction = (com.sap.engine.services.dc.api.ErrorStrategyAction) nextEntry
								.getKey();
						errorStrategy = (ErrorStrategy) nextEntry.getValue();
						deployer.setErrorStrategy(DeployApiMapper
								.mapErrorAction(errorAction), DeployApiMapper
								.mapErrorStrategy(errorStrategy));
					}
				}
			}
			{// batchFilters
				final List batchFilters = deploySettings.getBatchFilters();
				if (!batchFilters.isEmpty()) {
					BatchFilter nextBatchFilter;
					com.sap.engine.services.dc.cm.utils.filters.BatchFilter remoteBatchFilter;
					for (Iterator iterator = batchFilters.iterator(); iterator
							.hasNext();) {
						nextBatchFilter = (BatchFilter) iterator.next();
						remoteBatchFilter = remoteBatchFilterVisitor
								.createRemoteBatchFilter(nextBatchFilter,
										deployItems);
						deployer.addBatchFilter(remoteBatchFilter);
					}
				}
			}
			{// deploy listener - to trace progress in dc api traces
				DeploymentListener deploymentListener = new DeploymentListener() {
					public void deploymentPerformed(DeploymentEvent event) {
						final StringBuffer idStr = new StringBuffer();
						if (event.getDeployItem().getSdu() != null) {
							SduId id = event.getDeployItem().getSdu().getId();
							idStr.append(id.toString());
							if (id instanceof SdaId) {
								idStr.append(PROGRESS_SDA);
							} else if (id instanceof ScaId) {
								idStr.append(PROGRESS_SCA);
							} else {
								idStr.append(PROGRESS_NO_SDU);
							}
						} else {
							idStr.append(event.getDeployItem().getArchive());
							idStr.append(PROGRESS_NO_SDU);
						}

						if (event.getDeploymentEventAction() == DeploymentEventAction.DEPLOYMENT_TRIGGERED) {
							if (daLog.isInfoTraceable()) {
								daLog.traceInfo("ASJ.dpl_api.001022",
										"Deploying [{0}] ...",
										new Object[] { idStr });
							}
						} else {
							daLog.logInfo("ASJ.dpl_api.001023",
									"Deployment of [{0}] finished.",
									new Object[] { idStr });
						}
					}

				};
				addDeploymentListener(deploymentListener, ListenerMode.LOCAL,
						EventMode.ASYNCHRONOUS);
			}
			// enable the deployer time statistics
			try {
				deployer.enableTimeStats(this.isTimeStatEnabled);
			} catch (P4RuntimeException p4e) {
				// $JL_EXC$
				this.daLog.traceThrowable(p4e);
			} catch (MissingResourceException mre) {
				// $JL_EXC$
				this.daLog.traceThrowable(mre);
			} catch (NoSuchMethodError nsme) {
				// $JL_EXC$
				this.daLog.traceThrowable(nsme);
			}

			if (daLog.isDebugLoggable()) {
				this.daLog.logDebug(
						"deployer initialized successfully:[{0}]. [{1}]",
						new Object[] {
								serviceTimeWatcher.getElapsedTimeAsString(),
								this.deployerIdInfo });
			}
			return deployer;
		} catch (CMException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			throw new DeployException(null, deployItems, this.daLog
					.getLocation(), APIExceptionConstants.DC_CM_EXCEPTION,
					new String[] {
							exceptionName,
							",An error occurred during the deployment.cause="
									+ e.getMessage() }, e);
		} finally {
			if (daLog.isDebugLoggable()) {
				this.daLog.logDebug("Total time:[{0}]. [{1}]", new Object[] {
						serviceTimeWatcher.getTotalElapsedTimeAsString(),
						this.deployerIdInfo });
			}
		}
	}

	private com.sap.engine.services.dc.cm.deploy.DeployResult deploy(
			String[] archiveFilePathNames, String sessionId)
			throws com.sap.engine.services.dc.cm.deploy.ValidationException,
			DeploymentException, DCLockException {
		if (daLog.isInfoTraceable()) {
			daLog.traceInfo("ASJ.dpl_api.001180", "Start deploying.[{0}]",
					new Object[] { this.deployerIdInfo });
		}
		return m_deployer.deploy(archiveFilePathNames, sessionId);
	}

	private DeployResult deployItems(CM cm, final String transactionId,
			DeployItem[] deployItems, boolean isRollback)
			throws DeployResultNotFoundException, ValidationException,
			EngineTimeoutException, DeployException, AlreadyLockedException,
			AuthenticationException, ConnectionException, TransportException,
			RollingException {

		// First upload files to the server
		String[] remoteFilePaths = new String[deployItems.length];
		Map uploadedDeployItemsMap = validateUtils.uploadDeployItems(cm,
				transactionId, deployItems, remoteFilePaths);

		// Second deploy uploaded items
		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();
		if (daLog.isDebugTraceable()) {
			this.daLog.traceDebug(
					"Start deployment process timerId=[{0}]. [{1}]",
					new Object[] { new Long(serviceTimeWatcher.getId()),
							this.deployerIdInfo });
		}
		internalInitDeployer(m_deployer, cm, deployItems);
		this.offlineDeployments.put(transactionId, Boolean.FALSE);

		com.sap.engine.services.dc.cm.deploy.DeployResult remoteResult = null;

		Exception rootException = null;
		boolean isOfflineDeployment = false;

		try {
			com.sap.engine.services.dc.event.ClusterListener remoteClusterListener = new com.sap.engine.services.dc.event.ClusterListener() {

				public void clusterRestartTriggered(
						com.sap.engine.services.dc.event.ClusterEvent clusterEvent)
						throws P4IOException, P4ConnectionException {
					com.sap.engine.services.dc.event.ClusterEventAction clusterEventAction = clusterEvent
							.getClusterEventAction();
					if (daLog.isInfoTraceable()) {
						daLog
								.traceInfo(
										"ASJ.dpl_api.001028",
										"Cluster event [{0}] in deploy processor for transaction id [{1}] was received.",
										new Object[] { clusterEventAction,
												transactionId });
					}
					synchronized (DeployProcessorImpl.this.offlineDeployments) {
						if (DeployProcessorImpl.this.offlineDeployments
								.containsKey(transactionId)) {
							DeployProcessorImpl.this.offlineDeployments.put(
									transactionId, Boolean.TRUE);
							if (daLog.isDebugLoggable()) {
								daLog
										.logDebug(
												"Mark transaction [{0}] as offline phase.",
												new Object[] { transactionId });
							}
						}
					}
				}

				public int getId() {
					return hashCode();
				}
			};
			m_deployer.addClusterListener(remoteClusterListener,
					com.sap.engine.services.dc.event.ListenerMode.LOCAL,
					com.sap.engine.services.dc.event.EventMode.SYNCHRONOUS);
			if (daLog.isPathLoggable()) {
				this.daLog.logPath("Starting deployment. [{0}]",
						new Object[] { this.deployerIdInfo });
			}
			serviceTimeWatcher.clearElapsed();
			registerRemoteClusterListener(this.localClusterListener,
					com.sap.engine.services.dc.event.ListenerMode.LOCAL,
					com.sap.engine.services.dc.event.EventMode.SYNCHRONOUS);

			try {
				// check for
				// com.sap.engine.services.dc.cm.lock.DCLockException
				try {
					remoteResult = deploy(remoteFilePaths, transactionId);
				} catch (com.sap.engine.services.dc.cm.lock.DCLockException e) {
					if (DeployApiMapper.getDcLockExceptionTimeout() == 0) {
						throw e;
					}
					String exceptionName = DAUtils.getThrowableClassName(e);
					this.daLog.logThrowable("ASJ.dpl_api.001029",
							"[{0}] First Time. [{1}],cause=[{2}]", e,
							new Object[] { exceptionName, this.deployerIdInfo,
									e.getMessage() });
					try {
						Thread.sleep(DeployApiMapper
								.getDcLockExceptionTimeout());
					} catch (InterruptedException ie) {
						this.daLog
								.logThrowable(
										"ASJ.dpl_api.001030",
										"InterruptedException occurs. [{0}] cause=[{1}]",
										ie, new Object[] { this.deployerIdInfo,
												ie.getMessage() });
					}
					if (daLog.isDebugTraceable()) {
						this.daLog.traceDebug(
								"Second try to deploy after lock. [{0}]",
								new Object[] { this.deployerIdInfo });
					}

					uploadedDeployItemsMap = validateUtils.uploadDeployItems(
							cm, transactionId, deployItems, remoteFilePaths);
					if (isRollback) {
						remoteResult = m_deployer.rollback(transactionId,
								remoteFilePaths);
					} else {
						remoteResult = deploy(remoteFilePaths, transactionId);
					}
				}

				// try to catch and consume only the exception that is
				// caused by the restart
				// it could cause only P4RuntimeException. All others are
				// considered failures
			} catch (P4RuntimeException e) {

				Boolean isOffline = (Boolean) this.offlineDeployments
						.get(transactionId);
				if (Boolean.TRUE.equals(isOffline)) {

					isOfflineDeployment = true;
					this.cm = null;
					this.m_deployer = null;
					this.deploymentListenerMediator
							.setDeployer(this.m_deployer);

					String exceptionName = DAUtils.getThrowableClassName(e);
					if (this.daLog.isInfoTraceable()) {
						this.daLog
								.traceInfo(
										"ASJ.dpl_api.001031",
										"Caught exception [{0}]. Because cluster restart has been triggered beforehand, assuming a cluster restart.",
										new Object[] { exceptionName });
					}
					// log the stack trace with debug level
					String stackTrace = LogUtil.stackTraceToString(e
							.getStackTrace());
					if (daLog.isDebugTraceable()) {
						this.daLog.traceDebug(stackTrace);
					}

				} else {
					throw e;
				}
			} finally {
				try {
					if (this.m_deployer != null) {
						m_deployer.removeClusterListener(remoteClusterListener);
						unregisterRemoteClusterListener(this.localClusterListener);
					}
				} catch (Exception e) {
					String exceptionName = DAUtils.getThrowableClassName(e);
					if (daLog.isDebugTraceable()) {
						this.daLog
								.traceDebug(
										"Exception [{0}] during unregisterRemoteClusterListener: [{1}]",
										new Object[] { exceptionName,
												e.getMessage() });
					}
				}
			}
		} catch (com.sap.engine.services.dc.cm.deploy.AllItemsAlreadyDeployedValidationException e) {

			DeployItem[] sortedDeployItems = validateUtils.mapItems(
					transactionId, e.getDeploymentBatchItems(), e
							.getOrderedBatchItems(), uploadedDeployItemsMap);

			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001032",
							"All Items Already Deployed Validation Exception during deployment phase [{0}],cause=[{1}]",
							new Object[] { exceptionName, e.getMessage() });
			this.daLog.logError("ASJ.dpl_api.001033",
					"Deployment Items status: [{0}]",
					new Object[] { validateUtils.buildItemsInfo(
							sortedDeployItems, deployItems,
							this.isTimeStatEnabled) });
			throw new AllItemsAlreadyDeployedValidaionException(
					sortedDeployItems,
					deployItems,
					this.daLog.getLocation(),
					APIExceptionConstants.DC_ALL_ITEMS_ALREADY_DEPLOYED_VALIDATION_EXCEPTION,
					new String[] { exceptionName, e.getMessage() }, e);
		} catch (com.sap.engine.services.dc.cm.deploy.AllItemsFilteredValidaionException e) {

			DeployItem[] sortedDeployItems = validateUtils.mapItems(
					transactionId, e.getDeploymentBatchItems(), e
							.getOrderedBatchItems(), uploadedDeployItemsMap);

			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001034",
							"All Items Filtered Validation Exception during deployment phase [{0}],cause=[{1}]",
							new Object[] { exceptionName, e.getMessage() });
			this.daLog.logError("ASJ.dpl_api.001035",
					"Deployment Items status: [{0}]",
					new Object[] { validateUtils.buildItemsInfo(
							sortedDeployItems, deployItems,
							this.isTimeStatEnabled) });
			throw new AllItemsFilteredValidaionException(
					sortedDeployItems,
					deployItems,
					this.daLog.getLocation(),
					APIExceptionConstants.DC_ALL_ITEMS_FILTERED_VALIDATION_EXCEPTION,
					new String[] { exceptionName, e.getMessage() }, e);
		} catch (com.sap.engine.services.dc.cm.deploy.CrcValidationException e) {

			DeployItem[] sortedDeployItems = validateUtils.mapItems(
					transactionId, e.getDeploymentBatchItems(), e
							.getOrderedBatchItems(), uploadedDeployItemsMap);

			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001036",
							"Crc Validation Exception during deployment phase[{0}],cause=[{1}]",
							new Object[] { exceptionName, e.getMessage() });
			this.daLog.logError("ASJ.dpl_api.001037",
					"Deployment Items status: [{0}]",
					new Object[] { validateUtils.buildItemsInfo(
							sortedDeployItems, deployItems,
							this.isTimeStatEnabled) });
			throw new CrcValidationException(sortedDeployItems, deployItems,
					this.daLog.getLocation(),
					APIExceptionConstants.DC_CRC_VALIDATION_EXCEPTION,
					new String[] { exceptionName, e.getMessage() }, e);

		} catch (com.sap.engine.services.dc.cm.deploy.ValidationException e) {

			DeployItem[] sortedDeployItems = validateUtils.mapItems(
					transactionId, e.getDeploymentBatchItems(), e
							.getOrderedBatchItems(), uploadedDeployItemsMap);

			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001038",
							"Validation Exception during deployment phase [{0}],cause=[{1}]",
							new Object[] { exceptionName, e.getMessage() });
			this.daLog.logError("ASJ.dpl_api.001039",
					"Deployment Items status: [{0}]",
					new Object[] { validateUtils.buildItemsInfo(
							sortedDeployItems, deployItems,
							this.isTimeStatEnabled) });
			throw new ValidationException(sortedDeployItems, deployItems,
					this.daLog.getLocation(),
					APIExceptionConstants.DC_VALIDATION_EXCEPTION,
					new String[] { exceptionName, e.getMessage() }, e);
		} catch (com.sap.engine.services.dc.cm.deploy.RollingDeployException e) {

			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001040",
							"Rolling Exception during deployment phase [{0}],cause=[{1}]",
							new Object[] { exceptionName, e.getMessage() });
			throw new RollingException(this.daLog.getLocation(),
					APIExceptionConstants.DC_DEPLOYMENT_EXCEPTION,
					new String[] { exceptionName, e.getMessage() }, e);
		} catch (DeploymentException e) {

			DeployItem[] sortedDeployItems = validateUtils.mapItems(
					transactionId, e.getDeploymentBatchItems(), e
							.getOrderedBatchItems(), uploadedDeployItemsMap);

			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001041",
							"Deployment Exception during deployment phase [{0}], cause=[{1}]",
							new Object[] { exceptionName, e.getMessage() });
			this.daLog.logError("ASJ.dpl_api.001042",
					"Deployment Items status: [{0}]",
					new Object[] { validateUtils.buildItemsInfo(
							sortedDeployItems, deployItems,
							this.isTimeStatEnabled) });
			throw new DeployException(sortedDeployItems, deployItems,
					this.daLog.getLocation(),
					APIExceptionConstants.DC_DEPLOYMENT_EXCEPTION,
					new String[] { exceptionName, e.getMessage() }, e);
		} catch (com.sap.engine.services.dc.cm.lock.DCLockException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001043",
							"Lock Exception during deployment phase [{0}] Second Time,cause=[{1}]",
							new Object[] { exceptionName, e.getMessage() });
			throw new AlreadyLockedException(this.daLog.getLocation(),
					APIExceptionConstants.DA_DEPLOY_CONTROLLER_IS_LOCKED,
					new String[] { "deploy", e.getMessage() }, e);
		} catch (MissingResourceException e) {
			this.daLog.logThrowable("ASJ.dpl_api.001044", "{0}", e,
					new Object[] { e.getMessage() });
			throw new DeployResultNotFoundException(deployItems, this.daLog
					.getLocation(),
					APIExceptionConstants.DC_DEPLOYRESULTNOTFOUND_EXCEPTION,
					new String[] { e.getMessage() }, e);
		} catch (Exception e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			rootException = e;
			this.daLog.traceThrowable("ASJ.dpl_api.001045",
					"[{0}].[{1}],reason=[{2}]", e,
					new Object[] { exceptionName, this.deployerIdInfo,
							e.getMessage() });
		}
		this.offlineDeployments.remove(transactionId);

		if (isOfflineDeployment) {

			long serverTimeout = (this.customServerTimeout > 0) ? this.customServerTimeout
					: DeployApiMapper.getServerTimeout();
			long seconds = serverTimeout / 1000L;
			this.daLog
					.logInfo(
							"ASJ.dpl_api.001046",
							"+++ Server is being restarted for offline deploy +++. The currently set engine start timeout is [{0}] seconds.",
							new Object[] { new Long(seconds) });
			remoteResult = waitResultForOfflineDeploy(transactionId,
					deployItems, uploadedDeployItemsMap, serverTimeout);
		}

		if (remoteResult == null) {
			if (rootException != null) {
				this.daLog
						.logError(
								"ASJ.dpl_api.001047",
								"Exception occurred during deployment phase - [{0}],\ncause: [{1}]",
								new Object[] {
										DAUtils
												.getThrowableClassName(rootException),
										rootException.getLocalizedMessage() });
				throw new DeployException(null, deployItems, this.daLog
						.getLocation(),
						APIExceptionConstants.DC_DEPLOYMENT_EXCEPTION,
						new String[] {
								DAUtils.getThrowableClassName(rootException),
								rootException.getLocalizedMessage() },
						rootException);
			}
			this.daLog.logError("ASJ.dpl_api.001048",
					"Deploy result not found.");
			throw new DeployResultNotFoundException(deployItems, this.daLog
					.getLocation(),
					APIExceptionConstants.DC_NODEPLOYRESULT_EXCEPTION,
					new String[] { null }, rootException);
		}
		if (daLog.isPathLoggable()) {
			this.daLog
					.logPath(
							"Deployment completed. Preparing the deployment result. [{0}]",
							new Object[] { this.deployerIdInfo });
		}
		DeployResult deployResult = parseDeployResult(transactionId,
				remoteResult, deployItems, uploadedDeployItemsMap);
		logDeployResult(deployResult, serviceTimeWatcher, transactionId);
		return deployResult;
	}

	private void notifyForClusterRestartTriggered() {

		ClusterEvent event = new ClusterEvent(
				ClusterEventAction.CLUSTER_RESTART_TRIGGERED);

		try {
			
			this.daLog.traceDebug("Starting to notify local cluster listeners");
			
			if(this.localClusterListener != null){
				this.localClusterListener.clusterRestartTriggered(event);	
			}
			

			this.daLog.traceDebug("Starting to notify global cluster listeners");
			
			if(this.globalClusterListener != null){
				this.globalClusterListener.clusterRestartTriggered(event);	
			}
			

		} catch (OutOfMemoryError e) { // OOM, ThreadDeath and Internal error
			// are not consumed
			throw e;
		} catch (ThreadDeath e) {
			throw e;

		} catch (InternalError e) {
			throw e;

		} catch (Throwable t) { // all other throwables are just logged
			this.daLog
					.logThrowable(
							"ASJ.dpl_api.001284",
							"Unexpected exception during the notification of cluster listeners",
							t);
		}

	}

	private void logDeployResult(DeployResult deployResult,
			ServiceTimeWatcher serviceTimeWatcher, String sessionId) {

		if (this.daLog.isDebugTraceable()
				&& deployResult.getMeasurement() != null) {
			this.daLog.traceDebug("Measurement's document: \r\n [{0}]",
					new Object[] { deployResult.getMeasurement()
							.toDocumentAsString() });
		}

		if (daLog.isInfoTraceable()) {
			StringBuffer itemsInfoBuf = validateUtils.buildItemsInfo(
					deployResult.getSortedDeploymentItems(), deployResult
							.getDeploymentItems(), this.isTimeStatEnabled);
			itemsInfoBuf.insert(0, "Deployment result ");
			this.daLog.traceInfo("ASJ.dpl_api.001050", "{0}",
					new Object[] { itemsInfoBuf.toString() });
		}

		DeployResultStatus status = deployResult.getDeployResultStatus();
		String description = deployResult.getDescription();

		if (!DeployResultStatus.SUCCESS.equals(status) && description != null
				&& description.length() > 0) {
			this.daLog
					.logInfo("ASJ.dpl_api.001051",
							"{0}Deploy Result Description: [{1}] [{2}]",
							new Object[] { DAConstants.EOL, description,
									this.deployerIdInfo });
		}

		this.daLog
				.logInfo(
						"ASJ.dpl_api.001052",
						"+++ Deployment of session ID [{0}] finished with status [{1}] +++. [{2}] [{3}] [{4}]",
						new Object[] {
								sessionId,
								status,
								this.deployerIdInfo,
								serviceTimeWatcher
										.getTotalElapsedTimeAsString(),
								DAResultUtils.logSummary4Deploy(deployResult
										.getDeploymentItems()) });
	}

	private DeployResult parseDeployResult(String transactionId,
			com.sap.engine.services.dc.cm.deploy.DeployResult remoteResult,
			DeployItem[] deployItems, Map uploadedDeployItemsMap) {

		DeployItem[] sortedDeployItems = validateUtils.mapItems(transactionId,
				remoteResult.getDeploymentItems(), remoteResult
						.getSortedDeploymentBatchItems(),
				uploadedDeployItemsMap);

		com.sap.engine.services.dc.cm.deploy.DeployResultStatus remoteStatus = remoteResult
				.getDeployStatus();
		DeployResult deployResult = new DeployResultImpl(DeployMapper
				.mapResultStatus(remoteStatus), deployItems, sortedDeployItems,
				remoteResult.getDescription(), mapMeasurement(remoteResult
						.getMeasurement()));
		return deployResult;
	}

	private DAMeasurement mapMeasurement(DMeasurement remoteMeasurement) {
		return MeasurementUtils.map(remoteMeasurement);
	}

	private DeployItemImpl mapDistinctItem(
			String prefix,
			Map uploadedDeployItemsMap,
			com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem nextDeploymentBatchItem) {
		String sduFilePath = nextDeploymentBatchItem.getSduFilePath();
		DeployItemImpl origin = (DeployItemImpl) uploadedDeployItemsMap
				.get(sduFilePath);
		if (origin == null) {
			sduFilePath = DAUtils.getFileName(sduFilePath);
			origin = (DeployItemImpl) uploadedDeployItemsMap.get(prefix
					+ sduFilePath);
		}
		return origin;
	}

	private com.sap.engine.services.dc.api.deploy.ValidationResult parseVallidationResult(
			String transactionId,
			com.sap.engine.services.dc.cm.deploy.ValidationResult remoteValidationResult,
			DeployItem[] deployItems, Map uploadedDeployItemsMap) {

		DeployItem[] sortedDeployItems = validateUtils.mapItems(transactionId,
				remoteValidationResult.getDeploymentBatchItems(),
				remoteValidationResult.getSortedDeploymentBatchItems(),
				uploadedDeployItemsMap);

		com.sap.engine.services.dc.api.deploy.ValidationResult retValidationResult = new ValidationResultImpl(
				remoteValidationResult.isOfflinePhaseScheduled(), DeployMapper
						.mapValidationStatus(remoteValidationResult
								.getValidationStatus()), deployItems,
				sortedDeployItems);
		return retValidationResult;
	}

	public com.sap.engine.services.dc.api.deploy.ValidationResult validate(
			DeployItem[] deployItems) throws APIException, ConnectionException,
			TransportException, DeployException {

		synchronized (oDplMonitor) {
			if (this.isDeploying) {
				throw new RuntimeException(
						"[ERROR CODE DPL.DCAPI.1025] Concurrent error! A deploy operation is already running in this deploy processor");
			} else {
				this.isDeploying = true;
			}
		}

		// assumes that sesion is already connected
		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();

		if (daLog.isInfoTraceable()) {
			this.daLog
					.traceInfo(
							"ASJ.dpl_api.001053",
							"+++++ Starting  V A L I D A T E action +++++ [{0}]. timerId=[{1}]",
							new Object[] { this.deployerIdInfo,
									new Long(serviceTimeWatcher.getId()) });
		}
		try {
			if (deployItems == null) {
				throw new IllegalArgumentException(
						"[ERROR CODE DPL.DCAPI.1037] Deploy Items should not be null.");
			}
			validateUtils.logDeployInfo(deployItems, deploySettings, false, 0,
					0);
			// First upload files to the server
			Map uploadedDeployItemsMap = null;
			String[] remoteFilePaths = new String[deployItems.length];
			String transactionId = null;
			CM localCm = this.session.createCM();
			try {
				if (daLog.isDebugTraceable()) {
					this.daLog.traceDebug("going to generate Session id");
				}
				transactionId = localCm.generateSessionId();
				if (daLog.isDebugTraceable()) {
					this.daLog
							.traceDebug(
									"got Session id=[{0}],time: [{1}]. [{2}]",
									new Object[] {
											transactionId,
											serviceTimeWatcher
													.getElapsedTimeAsString(),
											this.deployerIdInfo });
				}
			} catch (DCNotAvailableException dcNotAvailableExc) {
				throw new ServiceNotAvailableException(
						this.daLog.getLocation(),
						APIExceptionConstants.DC_NOT_OPERATIONAL_YET,
						new Object[0]);
			} catch (CMException cme) {
				this.daLog
						.logError(
								"ASJ.dpl_api.001054",
								"Exception on creating transaction session.cause=[{0}]",
								new Object[] { cme.getMessage() });
				throw new ValidationException(null, deployItems, this.daLog
						.getLocation(), APIExceptionConstants.DC_CM_EXCEPTION,
						new String[] { DAUtils.getThrowableClassName(cme),
								cme.getMessage() }, cme);
			}
			uploadedDeployItemsMap = validateUtils.uploadDeployItems(localCm,
					transactionId, deployItems, remoteFilePaths);
			// Second validate uploaded items
			com.sap.engine.services.dc.cm.deploy.ValidationResult remoteValidationResult;
			internalInitDeployer(m_deployer, localCm, deployItems);
			try {
				if (this.daLog.isInfoTraceable()) {
					this.daLog.traceInfo("ASJ.dpl_api.001055",
							"Start validating.[{0}]",
							new Object[] { this.deployerIdInfo });
				}
				serviceTimeWatcher.clearElapsed();
				remoteValidationResult = m_deployer.validate(remoteFilePaths,
						transactionId);
				if (daLog.isPathLoggable()) {
					this.daLog
							.logPath("Validating completed. Preparing the validation result.");
				}
				com.sap.engine.services.dc.api.deploy.ValidationResult validationResult = parseVallidationResult(
						transactionId, remoteValidationResult, deployItems,
						uploadedDeployItemsMap);

				StringBuffer buffer = validateUtils.buildItemsInfo(
						validationResult.getSortedDeploymentBatchItems(),
						deployItems, this.isTimeStatEnabled);
				if (daLog.isPathLoggable()) {
					this.daLog.logPath("{0}",
							new Object[] { buffer.toString() });
				}
				this.daLog
						.logInfo(
								"ASJ.dpl_api.001056",
								"+++ Validation finished with status [{0}] +++ [{1}] [{2}]",
								new Object[] {
										validationResult.getValidationStatus(),
										this.deployerIdInfo,
										DAResultUtils
												.logSummary4Deploy(validationResult
														.getDeploymentBatchItems()) });
				return validationResult;
			} catch (com.sap.engine.services.dc.cm.deploy.AllItemsAlreadyDeployedValidationException e) {

				DeployItem[] sortedDeployItems = validateUtils
						.mapItems(transactionId, e.getDeploymentBatchItems(), e
								.getOrderedBatchItems(), uploadedDeployItemsMap);

				String exceptionName = DAUtils.getThrowableClassName(e);
				this.daLog
						.logError(
								"ASJ.dpl_api.001057",
								"All Items Already Deployed Validation Exception during validation phase[{0}],Reason=[{1}]. [{2}]",
								new Object[] { exceptionName, e.getMessage(),
										this.deployerIdInfo });
				throw new AllItemsAlreadyDeployedValidaionException(
						sortedDeployItems,
						deployItems,
						this.daLog.getLocation(),
						APIExceptionConstants.DC_ALL_ITEMS_ALREADY_DEPLOYED_VALIDATION_EXCEPTION,
						new String[] { exceptionName, e.getMessage() }, e);
			} catch (com.sap.engine.services.dc.cm.deploy.AllItemsFilteredValidaionException e) {

				DeployItem[] sortedDeployItems = validateUtils
						.mapItems(transactionId, e.getDeploymentBatchItems(), e
								.getOrderedBatchItems(), uploadedDeployItemsMap);

				String exceptionName = DAUtils.getThrowableClassName(e);
				this.daLog
						.logError(
								"ASJ.dpl_api.001058",
								"All Items Filtered Validation Exception during validation phase[{0}],Reason=[{1}]. [{2}]",
								new Object[] { exceptionName, e.getMessage(),
										this.deployerIdInfo });
				throw new AllItemsFilteredValidaionException(
						sortedDeployItems,
						deployItems,
						this.daLog.getLocation(),
						APIExceptionConstants.DC_ALL_ITEMS_FILTERED_VALIDATION_EXCEPTION,
						new String[] { exceptionName, e.getMessage() }, e);
			} catch (com.sap.engine.services.dc.cm.deploy.CrcValidationException e) {

				DeployItem[] sortedDeployItems = validateUtils
						.mapItems(transactionId, e.getDeploymentBatchItems(), e
								.getOrderedBatchItems(), uploadedDeployItemsMap);

				String exceptionName = DAUtils.getThrowableClassName(e);
				this.daLog
						.logError(
								"ASJ.dpl_api.001059",
								"Crc Validation Exception during validation phase[{0}],Reason=[{1}]. [{2}]",
								new Object[] { exceptionName, e.getMessage(),
										this.deployerIdInfo });
				throw new CrcValidationException(sortedDeployItems,
						deployItems, this.daLog.getLocation(),
						APIExceptionConstants.DC_CRC_VALIDATION_EXCEPTION,
						new String[] { exceptionName, e.getMessage() }, e);
			} catch (com.sap.engine.services.dc.cm.deploy.ValidationException e) {

				DeployItem[] sortedDeployItems = validateUtils
						.mapItems(transactionId, e.getDeploymentBatchItems(), e
								.getOrderedBatchItems(), uploadedDeployItemsMap);

				String exceptionName = DAUtils.getThrowableClassName(e);
				this.daLog
						.logError(
								"ASJ.dpl_api.001060",
								"Validation Exception during validation phase[{0}],Reason=[{1}]. [{2}]",
								new Object[] { exceptionName, e.getMessage(),
										this.deployerIdInfo });
				throw new ValidationException(sortedDeployItems, deployItems,
						this.daLog.getLocation(),
						APIExceptionConstants.DC_VALIDATION_EXCEPTION,
						new String[] { exceptionName, e.getMessage() }, e);
			}
		} finally {
			if (daLog.isPathLoggable()) {
				this.daLog
						.logPath(
								"+++++ End  V A L I D A T E action +++++ [{0}]. Total time: [{1}]",
								new Object[] {
										this.deployerIdInfo,
										serviceTimeWatcher
												.getTotalElapsedTimeAsString() });
			}
			synchronized (oDplMonitor) {
				isDeploying = false;
			}
		}
	}

	public ComponentVersionHandlingRule getComponentVersionHandlingRule() {
		return this.deploySettings.getComponentVersionHandlingRule();
	}

	public void setComponentVersionHandlingRule(
			ComponentVersionHandlingRule rule) {
		this.deploySettings.setComponentVersionHandlingRule(rule);
	}

	public ErrorStrategy getErrorStrategy(
			ErrorStrategyAction errorStrategyAction) {
		return (ErrorStrategy) this.deploySettings
				.getErrorStrategy(errorStrategyAction);
	}

	public void setErrorStrategy(ErrorStrategyAction errorStrategyAction,
			ErrorStrategy strategy) {
		if (DeployApiMapper.isValidErrorStrategyAction(errorStrategyAction)) {
			this.deploySettings.setErrorStrategy(errorStrategyAction, strategy);
		} else {
			throw new RuntimeException(
					"[ERROR CODE DPL.DCAPI.1040] Unknown strategy type "
							+ errorStrategyAction + " detected");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.api.deploy.DeployProcessor#
	 * getDeployWorkflowStrategy()
	 */
	public DeployWorkflowStrategy getDeployWorkflowStrategy() {
		return this.deploySettings.getDeployWorkflowStrategy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.api.deploy.DeployProcessor#
	 * setDeployWorkflowStrategy
	 * (com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy)
	 */
	public void setDeployWorkflowStrategy(
			DeployWorkflowStrategy workflowStrategy) {
		this.deploySettings.setDeployWorkflowStrategy(workflowStrategy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.api.deploy.DeployProcessor#
	 * getLifeCycleDeployStrategy()
	 */
	public LifeCycleDeployStrategy getLifeCycleDeployStrategy() {
		return this.deploySettings.getLifeCycleDeployStrategy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.api.deploy.DeployProcessor#
	 * setLifeCycleDeployStrategy
	 * (com.sap.engine.services.dc.cm.deploy.LifeCycleDeployStrategy)
	 */
	public void setLifeCycleDeployStrategy(
			LifeCycleDeployStrategy lifeCycleDeployStrategy) {
		this.deploySettings.setLifeCycleDeployStrategy(lifeCycleDeployStrategy);
	}

	public void addBatchFilter(BatchFilter batchFilter) {
		final List batchFilters = deploySettings.getBatchFilters();
		if (!batchFilters.contains(batchFilter)) {
			batchFilters.add(batchFilter);
		}
	}

	public void removeBatchFilter(BatchFilter batchFilter) {
		final List batchFilters = deploySettings.getBatchFilters();
		if (batchFilters.contains(batchFilter)) {
			batchFilters.remove(batchFilter);
		}
	}

	public DeployItem createDeployItem(String archiveLocation) {
		return new DeployItemImpl(archiveLocation);
	}

	private DeployItem[] getFakeDeployItems(Collection remoteDeploymentItems,
			Map fakeUploadedDeployItemsMap) {
		if (remoteDeploymentItems == null) {
			return new DeployItem[0];
		}
		DeployItem[] fakeDeployItems = new DeployItem[remoteDeploymentItems
				.size()];
		com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem nextRemoteItem;
		int index = 0;
		for (Iterator iter = remoteDeploymentItems.iterator(); iter.hasNext(); index++) {
			nextRemoteItem = (com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem) iter
					.next();
			fakeDeployItems[index] = DeployMapper.mapOrCreateDeployItem(null,
					nextRemoteItem);
			fakeUploadedDeployItemsMap.put(nextRemoteItem.getSduFilePath(),
					fakeDeployItems[index]);
		}
		return fakeDeployItems;
	}

	public DeployResult getDeployResultById(String transactionId)
			throws ConnectionException, DeployException,
			DeployResultNotFoundException, ServiceNotAvailableException {

		// check the args
		if (transactionId == null) {
			throw new IllegalArgumentException("Session id cannot be null");
		}

		if (transactionId.trim().length() == 0) {
			throw new IllegalArgumentException("Session id cannot be empty");
		}

		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();

		this.daLog
				.logInfo(
						"ASJ.dpl_api.001061",
						"+++++ Starting  G E T  D E P L O Y  R E S U L T ( transactionID: [{0}] ) action +++++ [{1}] [ timerId={2}]",
						new Object[] { transactionId, this.deployerIdInfo,
								new Long(serviceTimeWatcher.getId()) });
		try {
			CM localCm = getCMinWorkingState();

			if (daLog.isDebugLoggable()) {
				this.daLog
						.logDebug(
								"going to get remote deploy result for transactionID: [{0}], [{1}]",
								new Object[] { transactionId,
										this.deployerIdInfo });
			}

			com.sap.engine.services.dc.cm.deploy.DeployResult remoteDeployResult = localCm
					.getDeployer().getDeployResult(transactionId);
			Map fakeUploadedDeployItemsMap = new Hashtable();
			DeployItem[] fakeDeployItems = getFakeDeployItems(
					remoteDeployResult.getDeploymentItems(),
					fakeUploadedDeployItemsMap);
			if (daLog.isDebugLoggable()) {
				this.daLog.logDebug(
						"Preparing the result for transactrionID: [{0}][{1}]",
						new Object[] { transactionId, this.deployerIdInfo });
			}
			DeployResult deployResult = parseDeployResult(transactionId,
					remoteDeployResult, fakeDeployItems,
					fakeUploadedDeployItemsMap);
			logDeployResult(deployResult, serviceTimeWatcher, transactionId);
			return deployResult;
		} catch (DeploymentException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001062",
						"[{0}].Reason [{1}]", new Object[] { exceptionName,
								e.getMessage() });
			}
			throw new DeployException(null, null, this.daLog.getLocation(),
					APIExceptionConstants.DC_DEPLOYMENT_EXCEPTION,
					new String[] { exceptionName, e.getMessage() }, e);
		} catch (com.sap.engine.services.dc.cm.deploy.DeployResultNotFoundException e) {
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001063",
						"DeployResultNotFoundException occurs. Reason [{0}]",
						new Object[] { e.getMessage() });
			}
			throw new DeployResultNotFoundException(null, this.daLog
					.getLocation(),
					APIExceptionConstants.DC_DEPLOYRESULTNOTFOUND_EXCEPTION,
					new String[] { e.getMessage() }, e);
		} catch (CMException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001064",
						"[{0}].Reason [{1}]", new Object[] { exceptionName,
								e.getMessage() });
			}
			throw new DeployException(null, null, this.daLog.getLocation(),
					APIExceptionConstants.DC_CM_EXCEPTION, new String[] {
							exceptionName, e.getMessage() }, e);
		} finally {
			if (daLog.isPathLoggable()) {
				this.daLog
						.logPath(
								"+++++ End  G E T  D E P L O Y  R E S U L T ( transactionID: [{0}] )  action +++++ [{1}]. Total time: [{2}]",
								new Object[] {
										transactionId,
										this.deployerIdInfo,
										serviceTimeWatcher
												.getTotalElapsedTimeAsString() });
			}
		}
	}

	public String[] getOfflineDeployTransactionIDs() throws DeployException,
			ServiceNotAvailableException, ConnectionException {
		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();

		this.daLog
				.logInfo(
						"ASJ.dpl_api.001065",
						"+++++ Starting  G E T  A V A I L A B L E  D E P L O Y  T R A N S A C T I O N  I D S +++++ [{0}] [ timerId=[{1}]]",
						new Object[] { this.deployerIdInfo,
								new Long(serviceTimeWatcher.getId()) });
		try {
			CM localCm = getCMinWorkingState();
			if (daLog.isDebugLoggable()) {
				this.daLog
						.logDebug(
								"going to get all available offline deploy transactionIDs [{0}]",
								new Object[] { this.deployerIdInfo });
			}

			Deployer deployer = localCm.getDeployer();
			// register the reference to the obtained remote object
			registerRemoteReference(deployer);
			String[] transactionIDs = deployer.getOfflineDeployTransactionIDs();
			if (transactionIDs == null) {
				transactionIDs = new String[0];
			}
			if (daLog.isDebugLoggable()) {
				this.daLog.logDebug(
						"list of available transactrionIDs: [{0}] [{1}]",
						new Object[] { Arrays.asList(transactionIDs),
								this.deployerIdInfo });
			}
			return transactionIDs;
		} catch (DeploymentException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001066",
						"[{0}].Reason [{1}]", new Object[] { exceptionName,
								e.getMessage() });
			}
			throw new DeployException(null, null, this.daLog.getLocation(),
					APIExceptionConstants.DC_DEPLOYMENT_EXCEPTION,
					new String[] { exceptionName, e.getMessage() }, e);
		} catch (CMException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001067",
						"[{0}].Reason [{1}]", new Object[] { exceptionName,
								e.getMessage() });
			}
			throw new DeployException(null, null, this.daLog.getLocation(),
					APIExceptionConstants.DC_CM_EXCEPTION, new String[] {
							exceptionName, e.getMessage() }, e);
		} finally {
			if (daLog.isPathLoggable()) {
				this.daLog
						.logPath(
								"+++++ G E T  A V A I L A B L E  D E P L O Y  T R A N S A C T I O N  I D S +++++ [{0}]. Total time: [{1}]",
								new Object[] {
										this.deployerIdInfo,
										serviceTimeWatcher
												.getTotalElapsedTimeAsString() });
			}
		}
	}

	private CM getCMinWorkingState() throws ConnectionException, CMException,
			ServiceNotAvailableException {
		if (daLog.isDebugTraceable()) {
			this.daLog
					.traceDebug("Getting deploy controller's component manager");
		}

		CM localCm = this.session.createCM();
		if (daLog.isDebugLoggable()) {
			this.daLog
					.logDebug(
							"going to check if deploy controller is in working state. [{}]",
							new Object[] { this.deployerIdInfo });
		}

		DCState dcState = localCm.getState();
		if (daLog.isDebugLoggable()) {
			this.daLog.logDebug("Deploy Controller state is: [{0}] [{1}]",
					new Object[] { dcState, this.deployerIdInfo });
		}
		if (!DCState.WORKING.equals(dcState)) {
			throw new ServiceNotAvailableException(this.daLog.getLocation(),
					APIExceptionConstants.DC_NOT_OPERATIONAL_YET, new Object[0]);
		}
		return localCm;
	}

	private ConnectionInfo getConnectionInfo() {

		ConnectionInfo connection = new ConnectionInfo() {

			public String getInstanceHost() {

				return DeployProcessorImpl.this.session.getHost();
			}

			public int getSapcontrolPort() {

				return DeployProcessorImpl.this.session.getSapcontrolPort();
			}

			public int getP4Port() {

				return DeployProcessorImpl.this.session.getP4Port();
			}

		};

		return connection;

	}

	private com.sap.engine.services.dc.cm.deploy.DeployResult waitResultForOfflineDeploy(
			String transactionId, DeployItem[] deployItems,
			Map uploadedDeployItemsMap, long serverTimeout)
			throws DeployResultNotFoundException, EngineTimeoutException,
			DeployException, AuthenticationException {

		this.session.setDumpTrace(false);

		try {

			// exit from this loop only if one of the following conditions is
			// met:
			// 1.Exception
			// a) engine start timeout
			// b) thread interrupted while sleeping between polls for initial
			// context
			// c) problem during getDeployResult
			// 2.Deploy result obtained successfully

			boolean deployerConnected = false;
			while (true) {

				// a temp cluster listener
				RemoteClusterListenerImpl joinedSynchClusterListener = null;

				// reconnect deployer
				if (!deployerConnected) {

					reconnectDeployer(deployItems, serverTimeout);
					this.deploymentListenerMediator
							.setDeployer(this.m_deployer);
					deployerConnected = true;
					joinedSynchClusterListener = joinCommonClusterListener();

				}

				// wait until deployer is ready to return result
				try {

					waitUntilShouldGetResultFromCM();
				} catch (Exception e) {
					// Engine > AP6 PAT4 and a subsequent restart occurred (
					// safety workflow etc)
					String exceptionName = DAUtils.getThrowableClassName(e);
					this.daLog
							.logInfo(
									"ASJ.dpl_api.001068",
									"Assuming a subsequent restart of the server due to exception: [{0}], message: [{1}]",
									new Object[] { exceptionName,
											e.getMessage() });

					// log the whole trace if not P4RuntimeException
					if (!(e instanceof P4RuntimeException)) {
						daLog.traceThrowable(e);
					}

					// For P4RuntimeException trace the stack trace with debug
					if (daLog.isDebugTraceable()
							&& (e instanceof P4RuntimeException)) {

						if (daLog.isDebugTraceable()) {
							this.daLog.traceDebug("{0}", new Object[] { LogUtil
									.stackTraceToString(e.getStackTrace()) });
						}

					}

					this.cm = null;
					this.m_deployer = null;
					this.deploymentListenerMediator
							.setDeployer(this.m_deployer);
					
					if (joinedSynchClusterListener != null) {
						unregisterRemoteClusterListener(joinedSynchClusterListener);
					}
					
					deployerConnected = false;

					// hack - notify the cluster listeners about the subsequent
					// restart
					// this is necessary because there isn't a cluster event
					// generated on the
					// server side before the second restart ( safety workflow )
					notifyForClusterRestartTriggered();

					// try to reconnect the deployer again
					continue;
				}

				// try to get the result
				try {

					// unregister the joined listeners to avoid flooding user
					// local listeners with global events;
					if (joinedSynchClusterListener != null) {
						unregisterRemoteClusterListener(joinedSynchClusterListener);
					}

					// register the global cluster listener if any
					registerRemoteClusterListener(
							this.globalClusterListener,
							com.sap.engine.services.dc.event.ListenerMode.GLOBAL,
							com.sap.engine.services.dc.event.EventMode.SYNCHRONOUS);

					com.sap.engine.services.dc.cm.deploy.DeployResult result = null;
					result = getResultFromCM(transactionId, deployItems,
							uploadedDeployItemsMap, cm, m_deployer);
					return result;

				} catch (DeployException e) {
					// deployment has finished unsuccessfuly
					throw e;

				} catch (Exception e) {

					// Engine <= AP6 PAT4 and a subsequent restart occured (
					// safety workflow etc)
					String exceptionName = DAUtils.getThrowableClassName(e);
					if (daLog.isInfoTraceable()) {
						this.daLog
								.traceInfo(
										"ASJ.dpl_api.001069",
										"Assuming a subsequent restart due to exception: [{0}], message: [{1}]",
										new Object[] { exceptionName,
												e.getMessage() });
					}

					// log the whole trace with debug
					if (daLog.isDebugTraceable()) {
						this.daLog.traceDebug("{0}", new Object[] { LogUtil
								.stackTraceToString(e.getStackTrace()) });
					}

					this.cm = null;
					this.m_deployer = null;
					this.deploymentListenerMediator
							.setDeployer(this.m_deployer);
					
					if (joinedSynchClusterListener != null) {
						unregisterRemoteClusterListener(joinedSynchClusterListener);
					}
					
					deployerConnected = false;

					// hack - notify the cluster listeners about the subsequent
					// restart
					// this is necessary because there isn't a cluster event
					// generated on the
					// server side before the second restart ( safety workflow )
					notifyForClusterRestartTriggered();

					// try to reconnect the deployer again
					continue;
				}
			}
		} catch (Throwable t) {

			this.daLog.traceError("ASJ.dpl_api.001070",
					"Unexpected throwable: ");
			this.daLog.traceThrowable(t);
			throw new RuntimeException(t); // TODO replace with deploy exception

		} finally {
			this.session.setDumpTrace(true);
		}

	}

	/**
	 * Reconnect a temp cluster listener so we can receive the event from the
	 * second restart to both the global and the local listener and notify for
	 * event cluster available. The second restart should be treated as a local
	 * cluster restart event because it is caused by the current deployment.
	 * 
	 * @return the joined common listener or null if no listener has been
	 *         regiseted
	 */
	private RemoteClusterListenerImpl joinCommonClusterListener() {

		RemoteClusterListenerImpl joinedSynchClusterListener;

		joinedSynchClusterListener = RemoteClusterListenerImpl.join(
				this.localClusterListener, this.globalClusterListener, false);

		if (joinedSynchClusterListener != null) {

			// notify the listeners about the event cluster available
			joinedSynchClusterListener
					.clusterRestartTriggered(new com.sap.engine.services.dc.api.event.ClusterEvent(
							com.sap.engine.services.dc.api.event.ClusterEventAction.CLUSTER_AVAILABLE));

			registerRemoteClusterListener(joinedSynchClusterListener,
					com.sap.engine.services.dc.event.ListenerMode.GLOBAL,
					com.sap.engine.services.dc.event.EventMode.SYNCHRONOUS);

		}
		return joinedSynchClusterListener;
	}

	/**
	 * This method will try to determine if deploy controller is in working
	 * state so it is ok to get the deploy result. Engines below AP6 PAT5 do not
	 * have a getState() method so in this case the waiting is implemented on
	 * the server side and it is considered that it is OK to attempt to get the
	 * result.
	 * 
	 * The fact that this method returns true is not enough to conclude that the
	 * deployment is done. It might return true has finisIt might however turn
	 * out that the engine has been restarted again ( safety workflow )this case
	 * it might be
	 * 
	 * @throws CMException
	 *             , RuntimeException when the p4 connection is broken
	 * 
	 */
	private void waitUntilShouldGetResultFromCM() throws CMException {

		/*
		 * we need this because if the engine is < AP6 PAT4 there is no
		 * CM::getState() method yet. If so don't rely on this and assume that
		 * the waiting is implemented server-side
		 */
		try {
			DCState dcState = this.cm.getState(); // engines up to AP6.PAT04 do
			// not have this method
		} catch (Exception e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001071",
						"Assuming old engine version [{0}]. Cause=[{1}]",
						new Object[] { exceptionName, e.getMessage() });
			}
			return;
		}

		// got here so the method is present
		long pingTimeout = DeployApiMapper.getPingTimeout();

		final long LOGGING_TIMEOUT = DeployApiMapper
				.getReconnectLoggingInterval();
		LimitingTimer loggingTimer = new LimitingTimer(LOGGING_TIMEOUT);

		while (true) {

			DCState dcState = this.cm.getState();
			if (DCState.WORKING.equals(dcState)) {
				return;
			} else {
				if (loggingTimer.roll()) {
					if (this.daLog.isInfoTraceable()) {
						this.daLog.traceInfo("ASJ.dpl_api.001072",
								"Wait for the operation [{0}] to finish",
								new Object[] { dcState });
					}
				}
				try {
					Thread.sleep(pingTimeout);
				} catch (InterruptedException ie) {
					// TODO handle properly
					this.daLog
							.logThrowable("ASJ.dpl_api.001073", "cause=[{0}]",
									ie, new Object[] { ie.getMessage() });
				}
			}

		}

	}

	/**
	 * 
	 * This method polls the engine until it is able to get a deployer or the
	 * specified timeout value is exceeded
	 * 
	 * @param deployItems
	 *            current deploy items
	 * @throws EngineTimeoutException
	 *             if Deployer cannot be obtained within the specified timeout
	 *             value
	 * @throws AuthenticationException
	 *             if there is a problem with the authentication
	 * @see DeployApiMapper
	 * 
	 */
	private void reconnectDeployer(DeployItem[] deployItems, long serverTimeout)
			throws EngineTimeoutException, AuthenticationException,
			DeployException {

		final long PING_TIMEOUT = DeployApiMapper.getPingTimeout(); // few
		// seconds (
		// see
		// common_api
		// .
		// properties
		// )
		final long SERVER_TIMEOUT = serverTimeout;
		final long LOGGING_TIMEOUT = DeployApiMapper
				.getReconnectLoggingInterval();

		LimitingTimer loggingTimer = new LimitingTimer(LOGGING_TIMEOUT);

		this.criticalShutdownException = null;

		CriticalShutdownListener criticalListener = createCriticalShutdownListener();
		CriticalShutdownListenerRegistry registry = createCriticalShutdownRegistry();

		// the listener will be removed after it is notified if there is a
		// critical shutdown
		registry.addCriticalShutdownListener(criticalListener);

		long delay = 0;
		long startTime = System.currentTimeMillis();

		while (true) {
			delay = System.currentTimeMillis() - startTime;

			// reduce the volume of logging with severity INFO
			if (loggingTimer.roll()) {
				this.daLog
						.logInfo(
								"ASJ.dpl_api.001074",
								"+++ Wait for server response +++. Deploying offline [{0}] seconds. Will timeout in [{1}] seconds.",
								new Object[] {
										new Long(delay / 1000),
										new Long(
												(SERVER_TIMEOUT - delay) / 1000) });
			}

			if (delay > SERVER_TIMEOUT) {
				throw new EngineTimeoutException(deployItems, this.daLog
						.getLocation(),
						APIExceptionConstants.OFFLINE_ENGINE_TIMEOUTED,
						new String[] { String.valueOf(SERVER_TIMEOUT) });
			}

			if (this.criticalShutdownException != null) {

				// if there is no timeout yet but the server is in abnormal
				// state it will probably not
				// come up without manual interaction so there is no point to
				// wait
				throw this.criticalShutdownException;

			}

			try {

				this.session.getContext();

				this.cm = this.session.createCM();
				registerRemoteReference(this.cm);

				this.m_deployer = cm.getDeployer();
				registerRemoteReference(this.m_deployer);

				registry.removeCriticalShutdownListener(criticalListener);

				return;

			} catch (AuthenticationException e) {

				registry.removeCriticalShutdownListener(criticalListener);

				String exceptionName = DAUtils.getThrowableClassName(e);
				this.daLog.logError("ASJ.dpl_api.001075", "[{0}], cause=[{1}]",
						new Object[] { exceptionName, e.getMessage() });
				throw e;

			} catch (ConnectionException e) {

				String exceptionName = DAUtils.getThrowableClassName(e);
				if (daLog.isDebugTraceable()) {
					this.daLog.traceDebug("[{0}], cause=[{1}]", new Object[] {
							exceptionName, e.getMessage() });
				}

			} catch (CMException e) {

				String exceptionName = DAUtils.getThrowableClassName(e);
				if (daLog.isDebugTraceable()) {
					this.daLog.traceDebug("[{0}], cause=[{1}]", new Object[] {
							exceptionName, e.getMessage() });
				}

			} catch (Throwable t) {

				// do not try to remove the critical listener if an unexpected
				// problem
				// occurs. It should be garbage collected anyway when this
				// method completes,
				// because the reference to it is local to this method

				this.daLog.traceError("ASJ.dpl_api.001076",
						"Reconnect Deployer: Unexpected throwable: ");
				this.daLog.traceThrowable(t);
				throw new DeployException(
						null,
						deployItems,
						this.daLog.getLocation(),
						APIExceptionConstants.DC_DEPLOYMENT_EXCEPTION,
						new String[] { "Unexpected throwable while reconnecting the deployer" },
						t);

			}

			try {
				Thread.sleep(PING_TIMEOUT);
			} catch (InterruptedException ie) {
				this.daLog
						.logWarning("ASJ.dpl_api.001077",
								"The thread was interrupted while waiting for the engine to start.");
				throw new DeployException(
						null,
						deployItems,
						this.daLog.getLocation(),
						APIExceptionConstants.DC_DEPLOYMENT_EXCEPTION,
						new String[] { "The thread was interrupted while waiting for the engine to start. This will"
								+ "not stop the process on the server side and after the engine is up deployment should continue." });
			}

		}

	}

	private CriticalShutdownListenerRegistry createCriticalShutdownRegistry() {

		ConnectionInfo info = getConnectionInfo();
		return CriticalShutdownListenerRegistry
				.createInstance(info, this.daLog);
	}

	private CriticalShutdownListener createCriticalShutdownListener() {
		return new CriticalShutdownListener() {

			public void criticalShutdownOccured(Failure failure) {

				DeployProcessorImpl.this.criticalShutdownException =

				new DeployException(null, DeployProcessorImpl.this.items,
						DeployProcessorImpl.this.daLog.getLocation(),
						APIExceptionConstants.CRITICAL_SHUTDOWN_EXCEPTION,
						new String[] { failure.getProcesses().toString(),
								failure.getDescription() }

				);

			}

		};
	}

	private com.sap.engine.services.dc.cm.deploy.DeployResult getResultFromCM(
			String transactionId, DeployItem[] deployItems,
			Map uploadedDeployItemsMap, CM aCm, Deployer deployer)
			throws DeployException {
		if (this.daLog.isInfoTraceable()) {
			this.daLog.traceInfo("ASJ.dpl_api.001078",
					"Obtaining the deployment result from the server ...");
		}
		try {
			com.sap.engine.services.dc.cm.deploy.DeployResult remoteDeployResult;

			remoteDeployResult = deployer.getDeployResult(transactionId);

			return remoteDeployResult;

		} catch (DeploymentException e) {

			DeployItem[] sortedDeployItems = validateUtils.mapItems(
					transactionId, e.getDeploymentBatchItems(), e
							.getOrderedBatchItems(), uploadedDeployItemsMap);

			String exceptionName = DAUtils.getThrowableClassName(e);
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001079",
						"[{0}].Reason [{1}]", new Object[] { exceptionName,
								e.getMessage() });
			}
			throw new DeployException(sortedDeployItems, deployItems,
					this.daLog.getLocation(),
					APIExceptionConstants.DC_DEPLOYMENT_EXCEPTION,
					new String[] { exceptionName, e.getMessage() }, e);

		} catch (com.sap.engine.services.dc.cm.deploy.DeployResultNotFoundException e) {
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001080",
						"DeployResultNotFoundException.Reason [{0}]",
						new Object[] { e.getMessage() });
			}
			throw new DeployResultNotFoundException(deployItems, this.daLog
					.getLocation(),
					APIExceptionConstants.DC_DEPLOYRESULTNOTFOUND_EXCEPTION,
					new String[] { e.getMessage() }, e);

		}
		// catch (CMException e) {
		//      
		// String exceptionName = DAUtils.getThrowableClassName(e);
		// this.daLog.traceInfo(traceInfo + exceptionName + ".Reason " +
		// e.getMessage());
		//    	
		// throw new DeployException(null,
		// deployItems,
		// this.daLog.getLocation(),
		// APIExceptionConstants.DC_CM_EXCEPTION,
		// new String[] { exceptionName, e.getMessage() }, e);
		// }
	}

	private static class RemoteBatchFilterVisitor {
		private RemoteBatchFilterFactory remoteBatchFilterFactory;

		private final DALog daLog;

		RemoteBatchFilterVisitor(DALog daLog,
				RemoteBatchFilterFactory remoteBatchFilterFactory) {
			this.remoteBatchFilterFactory = remoteBatchFilterFactory;
			this.daLog = daLog;
		}

		com.sap.engine.services.dc.cm.utils.filters.BatchFilter createRemoteBatchFilter(
				BatchFilter batchFilter, DeployItem[] deployItems)
				throws DeployException {

			try {
				if (batchFilter instanceof SoftwareTypeBatchFilter) {
					return this.remoteBatchFilterFactory
							.createSoftwareTypeBatchFilter(
									((SoftwareTypeBatchFilter) batchFilter)
											.getSoftwareType().getName(),
									((SoftwareTypeBatchFilter) batchFilter)
											.getSoftwareType().getSubTypeName());
				}

				throw new DeployException(
						null,
						deployItems,
						this.daLog.getLocation(),
						APIExceptionConstants.DA_UNSUPPORTEDBATCHFILTER_EXCEPTION,
						new String[] { String.valueOf(batchFilter) });
			} catch (RemoteException re) {
				throw new DeployException(null, deployItems, this.daLog
						.getLocation(),
						APIExceptionConstants.DC_REMOTE_EXCEPTION,
						new String[] { DAUtils.getThrowableClassName(re),
								re.getMessage() }, re);
			}
		}
	}

	public String toString() {
		return "DeployProcessor[session='" + this.session + "',deployerId="
				+ this.deployerId + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.deploy.DeployProcessor#addDeploymentListener
	 * (com.sap.engine.services.dc.api.event.DeploymentListener,
	 * com.sap.engine.services.dc.api.event.ListenerMode,
	 * com.sap.engine.services.dc.api.event.EventMode)
	 */
	public void addDeploymentListener(DeploymentListener listener,
			ListenerMode listenerMode, EventMode eventMode) {

		this.deploymentListenerMediator.addDeploymentListener(listener,
				listenerMode, eventMode);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.api.deploy.DeployProcessor#
	 * removeDeploymentListener
	 * (com.sap.engine.services.dc.api.event.DeploymentListener)
	 */
	public void removeDeploymentListener(DeploymentListener listener) {

		this.deploymentListenerMediator.removeDeploymentListener(listener);

	}

	protected void unregisterRemoteClusterListener(
			RemoteClusterListenerImpl listener) {
		if (listener == null) {
			return;
		}
		try {
			listener.cleanUp();
			this.m_deployer.removeClusterListener(listener);
		} catch (Exception e) {
			if (daLog.isDebugLoggable()) {
				this.daLog.logDebug(
						"Failed while unregister RemoteClusterListener: [{0}]",
						new Object[] { e.getLocalizedMessage() });
			}
		}
	}

	public long setCustomServerTimeout(long newTimeout) {
		long oldOne = this.customServerTimeout;
		this.customServerTimeout = newTimeout;
		return oldOne;
	}

	public long getCustomServerTimeout() {
		return this.customServerTimeout;
	}

	public String getLastDeploymentTransactionId() {
		return this.lastDeploymentTransactionId;
	}

	protected void registerRemoteClusterListener(
			RemoteClusterListenerImpl remoteClusterListener,
			com.sap.engine.services.dc.event.ListenerMode listenerMode,
			com.sap.engine.services.dc.event.EventMode eventMode) {
		if (this.m_deployer != null && remoteClusterListener != null) {
			this.m_deployer.addClusterListener(remoteClusterListener,
					listenerMode, eventMode);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.api.impl.IRemoteReferenceHandler#
	 * registerRemoteReference(Remote)
	 */
	public void registerRemoteReference(Remote remote) {
		remoteRefs.add(remote);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.api.impl.IRemoteReferenceHandler#
	 * releaseRemoteReferences()
	 */
	public void releaseRemoteReferences() {
		// unregister the cluster listeners of this client
		unregisterRemoteClusterListener(this.localClusterListener);
		unregisterRemoteClusterListener(this.globalClusterListener);

		// try to release the remote references
		P4ObjectBroker broker = P4ObjectBroker.getBroker();
		if (broker == null) {
			this.daLog
					.logDebug(
							"ASJ.dpl_api.001081",
							"The P4ObjectBroker is null while trying to release remote references. The release operation is aborted!");
		} else {
			Iterator iter = this.remoteRefs.iterator();
			while (iter.hasNext()) {
				Remote remoteRef = (Remote) iter.next();
				if (remoteRef instanceof Deployer) {
					Deployer deployer = (Deployer) remoteRef;

					// unregister the remote listeners if any in order to avoid
					// server side exceptions
					if (this.deploymentListenerMediator
							.getDeployListenersCount() != 0) {

						// check if the deployer is still alive. If the server
						// has been restarted in the
						// meantime the stub may be broken
						if (isDeployerAlive(deployer)) {

							this.deploymentListenerMediator
									.setDeployer(deployer);
							this.deploymentListenerMediator
									.removeAllDeploymentListeners();

						} else {
							if (daLog.isInfoTraceable()) {
								this.daLog
										.traceInfo(
												"ASJ.dpl_api.001082",
												"The deployer is not alive. The remote listeners are not going be unregistered.");
							}
						}
					}

				}
				// separate error handling for each resource
				// to release as much remote refs. as possible
				try {
					broker.release(remoteRef);
				} catch (Exception e) {
					this.daLog
							.logThrowable(
									"ASJ.dpl_api.001083",
									"An exception occured while trying to release remote reference for object [{0}]",
									e, new Object[] { remoteRef });
				}
			}
		}
		remoteRefs.clear();
	}

	private boolean isDeployerAlive(Deployer deployer) {

		if (deployer == null) {
			return false;
		}

		try {
			deployer.getDeployWorkflowStrategy();
		} catch (Exception e) {
			return false;
		}

		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.deploy.DeployProcessor#setTimeStatEnabled
	 * (boolean)
	 */
	public void setTimeStatEnabled(boolean enabled) {
		this.isTimeStatEnabled = enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.deploy.DeployProcessor#getTimeStatEnabled
	 * ()
	 */
	public boolean getTimeStatEnabled() {
		return this.isTimeStatEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.deploy.DeployProcessor#getBatchFilters()
	 */
	public ArrayList getBatchFilters() {
		return deploySettings.getBatchFilters();
	}

}
