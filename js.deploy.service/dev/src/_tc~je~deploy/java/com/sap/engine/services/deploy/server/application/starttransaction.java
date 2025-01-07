package com.sap.engine.services.deploy.server.application;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.ExceptionInfo;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.container.op.util.StartUp;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.op.util.StatusDescriptionsEnum;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.exceptions.DisabledApplicationException;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.ApplicationStatusResolver;
import com.sap.engine.services.deploy.server.DUtils;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.TransactionCommunicator;
import com.sap.engine.services.deploy.server.cache.containers.ContainerComparatorReverted;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.properties.ServerState;
import com.sap.engine.services.deploy.server.utils.LockUtils;
import com.sap.engine.services.deploy.server.utils.ShmComponentUtils;
import com.sap.engine.services.deploy.server.utils.concurrent.ConflictingOperationLockException;
import com.sap.engine.services.deploy.server.utils.concurrent.LockSetNotAcquiredException;
import com.sap.engine.services.deploy.server.utils.concurrent.eval.StartLockEvaluator;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * @author Rumiana Angelova
 * @version 6.30
 */
public class StartTransaction extends ParallelAdapter {
	private final boolean readDIFromDB;
	private Status initialState;
	private StartInitiallyTransaction sit;
	private boolean need4StartInitially;

	/**
	 * Creates an induced local start transaction, as a reaction of received
	 * start command, which is sent by the initiator node. We always need to
	 * send response to the initiator node.
	 * 
	 * @param appName the name of the application which has to be started.
	 * @param ctx deploy service context.
	 * @param initiatorId the id of the node, which was the initiator of the 
	 * start transaction.
	 * @param containerNames the names of involved containers.
	 * @param readDIFromDB whether the deployment info has to be read from DB.
	 * @throws DeploymentException
	 */
	public StartTransaction(final String appName,
		final DeployServiceContext ctx, final int initiatorId,
		final String[] containerNames, final boolean readDIFromDB)
		throws DeploymentException {
		super(appName, ctx, DeployConstants.startApp, initiatorId, null);
		if (location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
					"Creates induced local start transaction for [{0}]",
					appName);
		}
		this.readDIFromDB = readDIFromDB;
		isAppStartAcceptable(dInfo.getStartUpO());
		if (containerNames == null && dInfo.getContainerNames() == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NOT_AVAILABLE_INFO_ABOUT_CONTS,
				getModuleID(), getTransactionType());
			sde.setMessageID("ASJ.dpl_ds.005033");
			throw sde;
		}
	}

	/**
	 * Creates a global start transaction.
	 * 
	 * @param appName the name of the application which has to be
	 * started.
	 * @param ctx deploy service context.
	 * @param initialParticipants server IDs of nodes, which are involved in the
	 * transaction including eventually and the current node.
	 * @throws DeploymentException
	 */
	public StartTransaction(final String appName, 
		final DeployServiceContext ctx, final int[] initialParticipants)
		throws DeploymentException {
		super(appName, ctx, DeployConstants.startApp, initialParticipants, null);
		assert initialParticipants != null;
		this.readDIFromDB = false;
		if (location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
				"Creates global start transaction for [{0}] " +
				"initially invoked for [{1}] server IDs.",
				appName, 
				CAConvertor.toString(initialParticipants, ""));
		}
		isAppStartAcceptable(dInfo.getStartUpO());
	}

	/**
	 * Creates an independent local start transaction. There is not need to 
	 * send any responses, because this transaction is created independently.
	 * 
	 * @param appName the name of the application which has to be started.
	 * @param ctx deploy service context.
	 * @param readDIFromDB whether the deployment info has to be read from DB.
	 * @param cause the cause for the parallel transaction. If it is 
	 * <tt>null</tt> that means that the transaction is triggered by user 
	 * request.
	 * @throws DeploymentException
	 */
	public StartTransaction(final String appName,
		final DeployServiceContext ctx, final boolean readDIFromDB,
		final Component cause) throws DeploymentException {
		super(appName, ctx, DeployConstants.startApp, null, cause);
		if (location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
					"Creates independent local start transaction for [{0}]",
					appName);
		}
		this.readDIFromDB = readDIFromDB;
		isAppStartAcceptable(dInfo.getStartUpO());
		if (dInfo.getContainerNames() == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NOT_AVAILABLE_INFO_ABOUT_CONTS,
				getModuleID(), getTransactionType());
			sde.setMessageID("ASJ.dpl_ds.005033");
			throw sde;
		}
	}

	@Override
	public boolean isNeeded() {
		return needForTransaction(Status.STARTED);
	}

	@Override
	public void lock() throws LockSetNotAcquiredException,
		InterruptedException, ConflictingOperationLockException {
		if(getCause() != null) {
			super.lock();
		} else {
			lockSet = ctx.getLockManager().lock(
				new StartLockEvaluator(Applications.getReferenceGraph(), 
					ctx.getLockManager().getLockTracker(), getComponent(),
					isEnqueueLockNeeded() ? getLockType() : 0,
						PropManager.getInstance().getTimeout4LocalLock()));
		}
	}

	public void begin() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Begin [{0}] of application [{1}]", getTransactionType(),
				getModuleID());
		}
		if (!isSuccessfullyFinished()) {
			// The transaction can be finished during needForTransaction check.
			beginCommon();
		}
	}

	

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DTransaction#beginLocal()
	 */
	public void beginLocal() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Begin local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		readDIFromDB();
		beginCommon();
	}

	private void beginCommon() throws DeploymentException {
		traceMe();
		final TransactionCommunicator comm = ctx.getTxCommunicator();
		final DeploymentInfo info = comm.getApplicationInfo(getModuleID());

		assert isValid(info.getStatus(), Status.STARTED, Status.STARTING,
			Status.STOPPING);

		// TODO: Start may be also after deployment/update/start of referent.
		if(getCause() == null) {
			// Start on user request.
			comm.setLocalApplicationStatus(getModuleID(), Status.STARTING,
				StatusDescriptionsEnum.STARTING_ON_USER_REQUEST, null);
			initialState = Status.IMPLICIT_STOPPED;
			final String[] warnings = ctx.getReferenceResolver()
				.startReferencedComponents(info);
			addWarnings(warnings);
		} else {
			comm.setLocalApplicationStatus(getModuleID(), Status.STARTING,
				StatusDescriptionsEnum.STARTING_AS_RESOURCE_AVAILABLE, 
				new Object[] { getCause() });
		}
		initialState = Status.STOPPED;
		openHandler();
	}

	private void readDIFromDB() throws DeploymentException {
		if (readDIFromDB) {
			refreshDeploymentInfoFromDB();
		}
	}

	
	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DTransaction#prepare()
	 */
	public void prepare() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Prepare [{0}] of application [{1}]", getTransactionType(),
				getModuleID());
		}
		if (!isSuccessfullyFinished()) {
			// The transaction can be marked as finished
			// on the current node during needForTransaction check.
			prepareCommon();
		}
	}

	public void prepareLocal() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
					"Prepare local [{0}] of application [{1}]",
					getTransactionType(), getModuleID());
		}
		prepareCommon();
	}

	private void prepareCommon() throws DeploymentException {
		doStartInitially();
		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		DeploymentInfo info = communicator.getApplicationInfo(getModuleID());
		ContainerInterface[] tempConts = getContainers4Application(info, true);
		Arrays.sort(tempConts, ContainerComparatorReverted.instance);
		final Properties props = info.getProperties();
		if (props != null && props.getProperty(
			DeployService.debugProperty, "false").equals("true")) {
			props.setProperty(DeployService.debugProperty, "false");
		}

		Configuration config = openApplicationConfiguration(
			DeployConstants.ROOT_CFG_APPS, ConfigurationHandler.READ_ACCESS);
		info = communicator.getApplicationInfo(getModuleID());
		communicator.bindLoader(info);
		for (int i = 0; i < tempConts.length; i++) {
			try {
				tempConts[i].prepareStart(getModuleID(), config);
			} catch (WarningException wex) {
				addWarnings(wex.getWarnings());
			} catch (DeploymentException de) {
				throw de;
			} catch (OutOfMemoryError oofmer) {
				throw oofmer;
			} catch (ThreadDeath td) {
				throw td;
			} catch (Throwable th) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "preparing start of application "
						+ getModuleID() }, th);
				sde.setMessageID("ASJ.dpl_ds.005082");
				throw sde;
			} finally {
				addContainer(tempConts[i], null);
			}
		}
		if (containers == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NO_AVAILABLE_CONTS_FOR_APP,
				getModuleID(), getTransactionType());
			sde.setMessageID("ASJ.dpl_ds.005136");
			throw sde;
		}
	}

	public void commit() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Commit [{0}] of application [{1}]", getTransactionType(),
				getModuleID());
		}
		if (!isSuccessfullyFinished()) {
			// The transaction can be finished during needForTransaction check.
			commonCommit();
		}
	}

	public void commitLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
					"Commit local [{0}] of application [{1}]",
					getTransactionType(), getModuleID());
		}
		commonCommit();
	}

	private void commonCommit() {
		try {
			for (int i = 0; i < containers.length; i++) {
				try {
					containers[i].commitStart(getModuleID());
				} catch (WarningException wex) {
					addWarnings(wex.getWarnings());
				} catch (OutOfMemoryError oofme) {
					throw oofme;
				} catch (ThreadDeath td) {
					throw td;
				} catch (Throwable th) {
					SimpleLogger.traceThrowable(Severity.ERROR, location, 
						new ServerDeploymentException(
							ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
							new String[] { "committing start of application ["
								+ getModuleID() + "]" }, th).getMessage(), th);
				}
			}
			try {
				rollbackHandler();
			} catch (ConfigurationException cex) {
				SimpleLogger.traceThrowable(Severity.ERROR, location,
					new ServerDeploymentException(
						ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
						new String[] { (isLocal() ? "local " : "") +
							"commit phase of operation [" +
							getTransactionType() + "] for application [" +
							getModuleID() + "].\nReason: " +
							cex.toString() }, cex).getMessage(), cex);
			}
		} finally {
			// here the status may be set on start after
			// deploy/update/start of referent
			final TransactionCommunicator communicator = 
				ctx.getTxCommunicator();
			communicator.setLocalApplicationStatus(
				getModuleID(), Status.STARTED,
					StatusDescriptionsEnum.STARTED_ON_USER_REQUEST, null);
			ctx.getReferenceResolver().componentIsAvailable(getComponent());
		}
		setSuccessfullyFinished(true);
	}

	public void rollback() {
		assert !isSuccessfullyFinished();
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		rollbackCommon();
	}

	public void rollbackLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		rollbackCommon();
	}

	private void rollbackCommon() {
		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		if (Status.IMPLICIT_STOPPED.equals(initialState)) {
			communicator.setLocalApplicationStatus(
				getModuleID(), Status.IMPLICIT_STOPPED,
				StatusDescriptionsEnum.IMPLICIT_STOPPED_AS_USER_REQUESTED_START_FAILED,
				null);
		} else {
			communicator.setLocalApplicationStatus(
				getModuleID(), Status.STOPPED,
				StatusDescriptionsEnum.STOPPED_AS_USER_REQUESTED_START_FAILED,
				null);
		}
		try {
			if (getHandler() != null) {
				rollbackHandler();
			}
		} catch (ConfigurationException cex) {
			SimpleLogger.traceThrowable(Severity.ERROR, location,
				new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "rollback phase of operation [" +
						getTransactionType() + "] for application [" +
						getModuleID() + "].\nReason: " + cex.toString() }, 
					cex).getMessage(), cex);
		}
	}

	public void rollbackPrepare() {
		assert !isSuccessfullyFinished();
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback prepare [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		rollbackPrepareCommon();
	}

	public void rollbackPrepareLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback prepare local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		rollbackPrepareCommon();
	}

	private void rollbackPrepareCommon() {
		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		communicator.setLocalApplicationStatus(
			getModuleID(), Status.STOPPED,
			StatusDescriptionsEnum.STOPPED_DURING_ROLLBACK_AS_USER_REQUESTED_START_FAILED,
			null);
		if (containers != null) {
			for (int i = 0; i < containers.length; i++) {
				try {
					containers[i].rollbackStart(getModuleID());
				} catch (WarningException wex) {
					addWarnings(wex.getWarnings());
				} catch (OutOfMemoryError oofme) {
					throw oofme;
				} catch (ThreadDeath td) {
					throw td;
				} catch (Throwable th) {
					SimpleLogger.traceThrowable(Severity.ERROR, location,
						new ServerDeploymentException(
							ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
							new String[] { 
								"rolling back start of application [" + 
								getModuleID() + "]" }, 
							th).getMessage(), th);
				}
			}
		}
		try {
			communicator.removeApplicationLoader(getModuleID());
		} catch (DeploymentException dex) {
			SimpleLogger.traceThrowable(
				Severity.ERROR, location, dex.getMessage(), dex);
		}
		rollbackCommon();
	}

	private void isAppStartAcceptable(final StartUp startUp)
		throws ServerDeploymentException {
		try {
			
			 if (StartUp.DISABLED.equals(startUp)) {
				DisabledApplicationException dae = new DisabledApplicationException(
					getModuleID(),
					ExceptionConstants.APPLICATION_START_DISABLED_IN_XML);
				dae.setMessageID("ASJ.dpl_ds.005123");
				throw dae;
			} else if (ApplicationStatusResolver.isApplicationDisabled(getModuleID())) {
				dInfo.setStartUpO(StartUp.DISABLED);
				DisabledApplicationException dae = new DisabledApplicationException(
						getModuleID(),
						ExceptionConstants.APPLICATION_START_DISABLED_IN_FILTERS);
					dae.setMessageID("ASJ.dpl_ds.006021");
					throw dae;
			}
			// @see DeployCommunicatorImpl->startMyApplications(String[]
			// appNames)
			final byte state = PropManager.getInstance().getClusterMonitor()
				.getCurrentParticipant().getState();
			if (state == ClusterElement.RUNNING) {
				final ServerState sState = PropManager.getInstance()
					.getServerState();
				if (sState.isAppStartNOTAcceptable()) {
					ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.WILL_NOT_START_APP,
						getModuleID(), sState.getName());
					sde.setMessageID("ASJ.dpl_ds.005106");
					throw sde;
				} else {
					// go on and start it
				}
			} else {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.WILL_NOT_START_APP, 
					getModuleID(), ClusterElement.STATES[state]);
				sde.setMessageID("ASJ.dpl_ds.005106");
				throw sde;
			}
		} catch (ServerDeploymentException sdEx) {
			setExceptionInfo(sdEx);
			throw sdEx;
		}
	}

	private void doStartInitially() throws DeploymentException {
		initStartInitiallyTransaction();
		if (!sit.needForTransaction2DB()) {
			if (location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"The [{0}] of application [{1}] is already done.", 
					sit.getTransactionType(), sit.getModuleID());
			}
			return;
		}

		try {
			if (isLocal()) {
				doStartInitiallyLocal();
			} else {
				doStartInitiallyGlobal();
			}
		} catch (WarningException wex) {
			addWarnings(wex.getWarnings());
		}
	}

	private void doStartInitiallyGlobal() throws DeploymentException {
		// Start initially transaction is already initialized here.
		assert sit != null;
		sit.makeAllPhasesOnOneServer();
		// No notifications are sent to remote servers,
		// because we rely on global start transaction to do that.
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"The [{0}] of application [{1}] "
				+ "finished successfully on this server.",
				getTransactionType(), getModuleID());
		}
		oncePerInstanceTransaction(true, false);
	}

	private void doStartInitiallyLocal() throws DeploymentException,
		WarningException {

		// Start initially transaction is already initialized here.
		assert sit != null;
		boolean isLocked = false;
		final char lockType = LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE;
		final String lockKeyForModuleID = sit.getLockKeyForModuleID();
		try {
			try {
				if (location.beDebug()) {
					SimpleLogger.trace(Severity.DEBUG, location, null,
						"Trying to lock [{0}] for [{1}]",
						lockKeyForModuleID, sit.getTransactionType());
				}
				// We need exclusive lock here, to handle the situation
				// with simultaneous start-initially on two server nodes.
				LockUtils.lockAndWait(lockKeyForModuleID, lockType,
					(int) PropManager.getInstance()
						.getTimeout4StartInitially());
				if (location.beDebug()) {
					SimpleLogger.trace(Severity.DEBUG, location, null,
						"The lock [{0}] for [{1}] was done.", 
						sit.getModuleID(), sit.getTransactionType());
				}
				isLocked = true;
				if(!sit.isNeeded()) {
					if (location.beDebug()) {
						SimpleLogger.trace(Severity.DEBUG, location, null,
							"The [{0}] of application [{1}] is already done "
							+ "after trying to lock it.", 
							sit.getTransactionType(), sit.getModuleID());
					}
					return;
				}

				// Do not register the StartInitiallyTransaction
				sit.makeAllPhases();
				DUtils.processWarningsAndErrors(sit);
			} catch (LockException ex) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.ALREADY_STARTED_OPERATION,
					new String[] { lockKeyForModuleID }, ex);
				sde.setMessageID("ASJ.dpl_ds.005070");
				throw sde;
			} catch (TechnicalLockException tlex) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_LOCK_BECAUSE_OF_TECHNICAL_PROBLEMS,
					new String[] { getModuleID(), tlex.getMessage() }, tlex);
				sde.setMessageID("ASJ.dpl_ds.005071");
				throw sde;
			}

			// Evaluate the result of the execution of startInitially operation
			// on this server node.
			if (sit.isNeeded()) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED, 
					"waiting the [" + sit.getTransactionType() +
					"] of [" + getModuleID() +
					"] to finish on any of the remote server nodes. " +
					"The notification did not arrive to [" +
					ctx.getClusterMonitorHelper().getCurrentServerId() +
					"] or the operation failed.");
				sde.setMessageID("ASJ.dpl_ds.005136");
				throw sde;
			}
			if (location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"The [{0}] of application [{1}] finished [{2}]",
					getTransactionType(),
					getModuleID(),
					(isLocked ? "globally on this server node with success."
						: "by receiving local notification for success."));
			}
		} finally {
			if (isLocked) {
				if (location.beDebug()) {
					SimpleLogger.trace(Severity.DEBUG, location, null,
						"Will try to UNlock [{0}] for [{1}].",
						lockKeyForModuleID, sit.getTransactionType());
				}
				try {
					LockUtils.unlock(lockKeyForModuleID, lockType);
				} catch (TechnicalLockException tlex) {
					ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.CANNOT_LOCK_BECAUSE_OF_TECHNICAL_PROBLEMS,
						new String[] { lockKeyForModuleID,
							tlex.getMessage() }, tlex);
					sde.setMessageID("ASJ.dpl_ds.005136");
					throw sde;
				}
			} else {
				if (location.beDebug()) {
					SimpleLogger.trace(Severity.DEBUG, location, null,
						"Will NOT try to UNlock [{0}] for [{1}].",
						lockKeyForModuleID, sit.getTransactionType());
				}
			}
		}
	}

	@Override
	public void makeAllPhases() {
		try {
			initStartInitiallyTransaction();
			need4StartInitially = sit.isNeeded();
			if (need4StartInitially) {
				// We cannot execute the start in parallel on the whole cluster
				// as default, because the initial start is sequential.
				if (location.beDebug()) {
					SimpleLogger.trace(Severity.DEBUG, location, null,
						"The start of [{0}] will be executed sequentially.",
						getModuleID());
				}
				makeAllPhasesSequentially();
			} else {
				// This is the default behavior.
				if (location.beDebug()) {
					SimpleLogger.trace(Severity.DEBUG, location, null,
						"The start of [{0}] will be executed in parallel.",
						getModuleID());
				}
				super.makeAllPhases();
			}
		} catch (DeploymentException dex) {
			SimpleLogger.traceThrowable(Severity.ERROR, location, 
				dex.getLocalizedMessage(), dex);
			currentStatistics.addError(getStackTraceAsString(dex));
		}
	}

	private void initStartInitiallyTransaction() throws DeploymentException {
		if (sit == null) {
			sit = new StartInitiallyTransaction(getModuleID(), ctx);
		}
	}

	@Override
	public Map<String, Object> prepareNotification() {
		// read deployment info from DB after start initially
		final Map<String, Object> cmd = super.prepareNotification();
		cmd.put(DeployConstants.READ_DI_FROM_DB, "" + need4StartInitially);
		return cmd;
	}

	@Override
	protected void rollbackPart(boolean local, boolean begin, Throwable th)
		throws DeploymentException {
		try {
			super.rollbackPart(local, begin, th);
		} finally {
			setExceptionInfo(th);
		}
	}

	private void setExceptionInfo(Throwable th) {
		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		final DeploymentInfo dInfo = communicator
			.getApplicationInfo(getModuleID());
		final ExceptionInfo exceptionInfo = new ExceptionInfo(
			ctx.getClusterMonitorHelper().getCurrentServerId(), 
			DeployConstants.startApp, th);
		dInfo.setExceptionInfo(exceptionInfo);
		if (location.beDebug()) {
			SimpleLogger.trace(
				Severity.DEBUG, location, null,
				"Set exception info for start transaction of [{0}] to: {1}",
				getModuleID(), exceptionInfo);
		}
		if ( dInfo.isJ2EEApplication() ){
			if (!StartUp.DISABLED.equals(dInfo.getStartUpO())) {
				ShmComponentUtils.setLocalStatusFailed(getModuleID());
				communicator.getManagementListenerUtils().notify4Add(getModuleID());
			} else {
				ShmComponentUtils.setStartupModeDisabled(getModuleID());
			}	
		}
	}
}