/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.application;

import java.rmi.RemoteException;
import java.util.Arrays;

import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.DUtils;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.TransactionCommunicator;
import com.sap.engine.services.deploy.server.TransactionStatistics;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.remote.ClusterMonitorHelper;
import com.sap.engine.services.deploy.server.remote.MessageResponse;
import com.sap.engine.services.deploy.timestat.TransactionTimeStat;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Parallel adapter is the superclass of start and stop transaction. These
 * transactions are noted as &quot;parallel&quot;, which means that they are
 * executed in parallel on the global node and all local nodes. The other
 * transactions are sequential - they are executed first on the global node and
 * after that in parallel on the local nodes.
 * 
 * @author Rumiana Angelova
 */
public abstract class ParallelAdapter extends ApplicationTransaction {
	private static final int TIMEOUT_CHUNK = 100;
	private static final int INVALID_NODE_ID = -1;

	protected static final Location location = 
		Location.getLocation(ParallelAdapter.class);

	protected final int initiatorId; // the id of the initiating node
	protected final DeploymentInfo dInfo;
	protected final int[] initialParticipants;

	private final Component cause;
	private int[] notifiedParticipants;

	/**
	 * This private constructor is called internally by public constructors.
	 * 
	 * @param appName the application name.
	 * @param ctx deploy service context.
	 * @param transType the transaction type.
	 * @param initialParticipants server IDs of nodes, which are involved in the
	 * transaction including eventually and the current node. When this 
	 * parameter is null, it means that this is local transaction.
	 * @param initiatorId the ID of the initiator node or INVALID_NODE_ID in 
	 * case of independent local transaction or global transaction.
	 * @param cause the cause for the parallel transaction. If it is 
	 * <tt>null</tt> that means that the transaction is triggered by user 
	 * request.
	 * @throws DeploymentException
	 */
	private ParallelAdapter(final String appName,
		final DeployServiceContext ctx, final String transType,
		final int[] initialParticipants, final int initiatorId,
		final Component cause) throws DeploymentException {
		super(ctx);
		this.initialParticipants = initialParticipants;
		this.initiatorId = initiatorId;
		this.cause = cause;
		setModuleID(appName);
		setModuleType(DeployConstants.APP_TYPE);
		setTransactionType(transType);
		dInfo = ifMissingReadFromDB();
		if(dInfo == null) {
			throw new ServerDeploymentException(
				ExceptionConstants.NOT_DEPLOYED,
				appName, transType);
		}
		if (!(ctx.isMarkedForShutdown() && ctx.getClusterMonitorHelper()
			.findOtherServersInCurrentInstance().length > 0)) {
			setShmComponentStatusExpected();
			setShmComponentStartupMode();
		}
	}

	/**
	 * Initiates local parallel transaction, as a result of received start or
	 * stop command, which is sent by the initiator node. We always need to send
	 * response to the initiator node.
	 * 
	 * @param appName the application name.
	 * @param ctx deploy service context.
	 * @param transType the transaction type.
	 * @param initiatorId the ID of the initiator node.
	 * @param cause the cause for the parallel transaction. If it is 
	 * <tt>null</tt> that means that the transaction is triggered by user 
	 * request.
	 * @throws DeploymentException
	 */
	public ParallelAdapter(final String appName,
		final DeployServiceContext ctx, final String transType,
		final int initiatorId, final Component cause) 
		throws DeploymentException {
		this(appName, ctx, transType, null, initiatorId, cause);
	}

	/**
	 * Creates standalone local or global parallel transaction. Note that global
	 * transactions always have not initiator.
	 * 
	 * @param appName the application name.
	 * @param ctx the deploy service context.
	 * @param transType the transaction type.
	 * @param initialParticipants server IDs of nodes, which are involved in the
	 * transaction including eventually and the current node. When this 
	 * parameter is null, it means that this is local transaction.
	 * @param cause the cause for the parallel transaction. If it is 
	 * <tt>null</tt> that means that the transaction is triggered by user 
	 * request.
	 * @throws DeploymentException
	 */
	public ParallelAdapter(final String appName,
		final DeployServiceContext ctx, final String transType,
		final int[] initialParticipants, final Component cause) 
		throws DeploymentException {
		this(appName, ctx, transType, initialParticipants, 
			INVALID_NODE_ID, cause);
	}

	/**
	 * Check whether this transaction is local or global. 
	 * @return flag indicating whether this transaction is local or global.
	 */
	protected boolean isLocal() {
		return initialParticipants == null;
	}

	@SuppressWarnings("deprecation")
	protected boolean isValid(Status current, Status... errStatus) {
		for (Status status : errStatus) {
			if (current.equals(status)) {
				return false;
			}
		}
		return true;
	}

	protected boolean isValid(final DeploymentInfo dInfo) {
		final String[] contNames = dInfo.getContainerNames();
		return contNames != null && contNames.length > 0;
	}

	@SuppressWarnings( { "boxing"})
	protected boolean needForTransaction(final Status targetStatus) {
		final boolean isLocalNeed;
		final boolean result;
		if (isLocal()) {
			// This is a local transaction. 
			isLocalNeed = isLocalNeedForTransaction(targetStatus);
			result = isLocalNeed;
		} else {
			// Global transaction
			ClusterMonitorHelper cmHelper = ctx.getClusterMonitorHelper();
			notifiedParticipants = findRemoteParticipants(targetStatus);
			isLocalNeed = cmHelper.findIndexOfCurrentServerId(initialParticipants) != -1 
				&& isLocalNeedForTransaction(targetStatus);
			result = notifiedParticipants.length > 0 || isLocalNeed;
		}
		if (location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
				"needForTransaction [{0}] on [{1}] returns [{2}]",
				getTransactionType(), getModuleID(), result);
		}
		setSuccessfullyFinished(!isLocalNeed);
		return result;
	}

	private boolean isLocalNeedForTransaction(final Status targetStatus) {
		final DeploymentInfo dInfo = ctx.getTxCommunicator()
			.getApplicationInfo(getModuleID());
		return dInfo != null && !targetStatus.equals(dInfo.getStatus());
	}

	/**
	 * This method is called for global transactions to find the remote 
	 * participants.
	 * @param targetStatus the target status.
	 */
	private int[] findRemoteParticipants(final Status targetStatus) {
		assert initialParticipants != null;
		final ClusterMonitorHelper cmHelper = ctx.getClusterMonitorHelper();
		final int[] eligibles = cmHelper.filterEligibleReceivers(initialParticipants);
		final int[] participants = new int [eligibles.length];
		int count = 0;
		for (final int serverId : eligibles) {
			final String serverName = cmHelper.getServerName(serverId);
			try {
				final String appStatus4ServerXX = ctx.getTxCommunicator()
					.getApplicationStatus(getModuleID(), serverName);
				if (!targetStatus.toString().equals(appStatus4ServerXX)) {
					participants[count++] = serverId;
				}
			} catch (RemoteException rex) {
				SimpleLogger.traceThrowable(Severity.ERROR, location, null,
					new ServerDeploymentException(
						ExceptionConstants.UNEXPECTED_EXCEPTION, new String[] {
							"[" + getTransactionType() + "]",
							"[" + getModuleID() + "]" }, rex).getMessage(),
					rex);
			}
		}
		
		if (location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
				"Global [{0}] of application [{1}] " +
				"will be executed on {2} server IDs.",
				getTransactionType(), getModuleID(),
				CAConvertor.toString(participants, ""));
		}
		return Arrays.copyOf(participants, count);
	}

	/**
	 * This method sends respond to the transaction initiator with the result of
	 * execution of the local transaction. It never throws any checked
	 * exceptions.
	 * 
	 * @throws ServerDeploymentException
	 */
	private void sendResponseToInitiator()  {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"sendResponseToInitiator() is called for {0} on {1}.",
				getTransactionType(), getModuleID());
		}
		try {
			ctx.getRemoteCaller().sendRespond(getModuleID(), 
				getTransactionType(), initiatorId, 
				currentStatistics.getWarnings(),
				currentStatistics.getErrors());
		} catch (ServerDeploymentException ex) {
			SimpleLogger.traceThrowable(Severity.ERROR, location, null, 
				ex.getLocalizedMessage(), ex);
		}
	}

	@Override
	protected void clearExceptionInfoAndNotifyFailed2Start() {
		final DeploymentInfo dInfo = Applications.get(getModuleID());
		if (dInfo != null && dInfo.getExceptionInfo() != null) {
			dInfo.setExceptionInfo(null);
			ctx.getTxCommunicator().getManagementListenerUtils().notify4Remove(
				getModuleID());
		}
	}

	/**
	 * This method is called when a response from remote node is received or
	 * when a node is no more available in the cluster. Notified participants 
	 * array is already initialized.
	 * @param senderId the server ID of the node, for which the transaction may
	 * be considered as finished. 
	 * @param warnings the warnings occurred during the execution.
	 * @param errors the errors occurred during the execution.
	 */
	@SuppressWarnings("boxing")
	public void serverFinished(final int senderId, 
		final String[] warnings, final String[] errors) {
		if (location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
				"The operation [{0}] with [{1}] invoked serverFinished: " +
				"[{2}], warnings=[{3}], errors=[{4}]",
				getTransactionType(), getModuleID(), senderId,
				CAConvertor.toString(warnings, ""), 
				CAConvertor.toString(errors, ""));
		}
		synchronized(this) {
			assert notifiedParticipants != null;
			try {
				remoteStatistics = mergeToRemoteStatistics(
					new MessageResponse(senderId, warnings, errors, null));
				notifiedParticipants = 
					DUtils.removeElement(notifiedParticipants, senderId);
			} finally {
				this.notify();
			}
		}
	}

	/**
	 * Global start transactions can be executed in two modes - parallel and
	 * sequential. Normally we will execute starts in parallel mode, but if we
	 * need to perform StartInitallyTransaction, we will have to execute the
	 * global start sequentially as all other transactions.
	 * 
	 * @see ParallelAdapter#makeAllPhases()
	 * @see ApplicationTransaction#makeAllPhases()
	 * 
	 * @throws DeploymentException
	 */
	protected void makeAllPhasesSequentially() throws DeploymentException {
		super.makeAllPhases();
	}

	@Override
	public void makeAllPhasesLocal() throws DeploymentException {
		assert isLocal();
		try {
			super.makeAllPhasesLocal();
		} catch (DeploymentException dex) {
			currentStatistics.addError(getStackTraceAsString(dex));
			throw dex;
		} finally {
			if (initiatorId != INVALID_NODE_ID) {
				// This is a local part of a global transaction.
				sendResponseToInitiator();
			}
		}
	}

	@Override
	public void notNeeded() {
		getCurrentStatistics().addWarning(
			"There is NO need to execute operation " +
			getTransactionType() + " with " + getModuleID() +
			", but anyway will send response for it, because it is required.");
		// We always need to send response for start and stop
		// local transactions if there is an initiator.
		if(initiatorId != INVALID_NODE_ID) {
			sendResponseToInitiator();
		}
	}

	/**
	 * We are in a global transaction and have to wait for a response from all
	 * remote nodes.
	 * 
	 * @throws DeploymentException
	 */
	private void waitResponceFromRemoteNodes() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Will wait for responce from remote node.");
		}
		synchronized (this) {
			// We need this approach in order to workaround 
			// the winter/summer daytime change and the spirious wakeup.
			final long retries = PropManager.getInstance()
				.getTimeout4RemoteOperation() / TIMEOUT_CHUNK;
			for(int retry = 0; retry < retries; retry++) {
				if (notifiedParticipants.length == 0) {
					// All notifications are received.
					final TransactionStatistics[] statistics = getStatistics();
					if (atLeastOneServerOK(statistics)) {
						// Exit normally.
						return;
					}
					// Errors on all server nodes.
					throw new ServerDeploymentException(
						ExceptionConstants.COMPLEX_ERROR, 
						getAllErrors(statistics));
				}
				try {
					this.wait(TIMEOUT_CHUNK);
				} catch(InterruptedException ex) {
					SimpleLogger.traceThrowable(Severity.ERROR, location,
						new ServerDeploymentException(
							ExceptionConstants.THREAD_INTERRUPTED,
							new String[] {
								"finishing [" + getTransactionType() +
								"] in the entire server ",
								"[" + getTransactionType() + "]",
								"[" + getModuleID() + "]" }, ex).getMessage(), 
						ex);
				}
			};
			// Timed out.
			SimpleLogger.trace(Severity.WARNING, location, 
				"ASJ.dpl_ds.000076",
				"Operation [{0} : {1}] will return the control " +
				"withOUT having respond from all notified nodes, " +
				"because it is timed out. This probably means that " +
				"the operation enqueue lock will not be released! " +
				"The nodes, which did not respond are [{2}]. " +
				"All nodes are [{3}].",
				getTransactionType(), getModuleID(),
				CAConvertor.toString(notifiedParticipants, ""),
				CAConvertor.toString(initialParticipants, ""));
		}
	}

	private String getAllErrors(
		final TransactionStatistics[] statistics) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < statistics.length; i++) {
			final String[] errors = statistics[i].getErrors();
			if (errors != null) {
					for (int j = 0; j < errors.length; j++) {
					sb.append("server ID ")
						.append(statistics[i].getClusterID())
						.append(":").append(errors[j]).append("\n");
					}
				}
			}
		return sb.toString();
	}

	private boolean atLeastOneServerOK(
		final TransactionStatistics[] statistics) {
		for (int i = 0; i < statistics.length; i++) {
			if (statistics[i].isOkResult()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Here we execute the global transactions parallel in the cluster:
	 * <p>The notification of the remote server nodes is done asynchronously 
	 * before the execution of the transaction on the global node. So we can 
	 * gain a performance executing the transactions in parallel on all server 
	 * nodes . (Normally the notification of the remote nodes is done 
	 * synchronously after the end of the transaction).</p>
	 * After that we have to wait for the responses by the remote nodes.
	 * @see 
	 * com.sap.engine.services.deploy.server.application.ApplicationTransaction
	 * 		#makeAllPhases()
	 */
	@Override
	public void makeAllPhases() throws DeploymentException {
		assert !isLocal();
		assert initiatorId == INVALID_NODE_ID;
		TransactionTimeStat.setAppName(getModuleID());
		// Notify other nodes without to wait.
		notifyRemotely(false);
		try {
			makeAllPhasesOnOneServer();
		} catch (DeploymentException dex) {
			if (!ctx.isMarkedForShutdown()) {
				currentStatistics.addError(getStackTraceAsString(dex));
				SimpleLogger.traceThrowable(Severity.ERROR, location, 
					"ASJ.dpl_ds.006373", 
						"Exception in make all phases on one server", dex);
			}
		} catch (OutOfMemoryError oofmer) {
			throw oofmer;
		} catch (ThreadDeath td) {
			throw td;
		} catch (Throwable th) {
			if (!ctx.isMarkedForShutdown()) {
				SimpleLogger.traceThrowable(Severity.ERROR, location,
					new ServerDeploymentException(
						ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
						new String[] {
							"[" + getTransactionType() + "]",
							"[" + getModuleID() + "]" }, th).getMessage(), th);
				currentStatistics.addError(getStackTraceAsString(th));
			}
		} finally {
			waitResponceFromRemoteNodes();
		}
	}

	protected DeploymentInfo ifMissingReadFromDB() throws DeploymentException {
		final TransactionCommunicator comm = ctx.getTxCommunicator();
		DeploymentInfo dInfo = comm.getApplicationInfo(getModuleID());
		if (dInfo == null) {
			dInfo = refreshDeploymentInfoFromDB();
		}
		return dInfo;
	}

	protected void traceMe() {
		if (PropManager.getInstance().isAdditionalDebugInfo()) {
			final Exception ex = 
				new Exception(this + " [" + getModuleID()+ "]");
			SimpleLogger.traceThrowable(Severity.ERROR, location,
				ex.getLocalizedMessage(), ex);
		}
	}
	
	@Override
	protected int[] getRemoteParticipants() {
		// We expect that findRemoteParticipants() is already called.
		assert notifiedParticipants != null;
		return notifiedParticipants;
	}

	/**
	 * @return the cause for the transaction. If it is <tt>null</tt> that
	 * means that the transaction is triggered by user request.
	 */
	protected Component getCause() {
		return cause;
	}
}