package com.sap.engine.services.deploy.server.application;

import static com.sap.engine.services.deploy.logging.DSLog.logThrowableAlwaysSucceeds;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import javax.naming.InitialContext;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.ContainerManagement;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.op.util.StatusDescriptionsEnum;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.DeployCommunicatorImpl;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.TransactionCommunicator;
import com.sap.engine.services.deploy.server.cache.containers.ContainerComparator;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.concurrent.ConflictingOperationLockException;
import com.sap.engine.services.deploy.server.utils.concurrent.LockSetNotAcquiredException;
import com.sap.engine.services.deploy.server.utils.concurrent.eval.StopLockEvaluator;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * @author Rumiana Angelova
 * @version 6.25
 */
public class StopTransaction extends ParallelAdapter {

	private Configuration config = null;
	
	/**
	 * Initiates a local stop transaction, as a result of received stop command,
	 * which is sent by the initiator node. We always need to send response to
	 * the initiator node.
	 * 
	 * @param appName the name of the application which has to be stopped.
	 * @param ctx deploy service context.
	 * @param initiatorId the id of the node, which was the initiator of the
	 * stop transaction.
	 * @param containerNames the names of involved containers.
	 * @throws DeploymentException
	 */
	public StopTransaction(final String appName,
		final DeployServiceContext ctx, final int initiatorId,
		final String[] containerNames) throws DeploymentException {
		super(appName, ctx, DeployConstants.stopApp, initiatorId, null);
		if (containerNames == null && dInfo.getContainerNames() == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.NOT_AVAILABLE_INFO_ABOUT_CONTS,
				getModuleID(), getTransactionType());
			sde.setMessageID("ASJ.dpl_ds.005033");
			throw sde;
		}

	}

	/**
	 * Creates a global stop transaction.
	 * 
	 * @param applicationName the name of the application which has to be 
	 * stopped.
	 * @param ctx deploy service context.
	 * @param initialParticipants server IDs of nodes, which are involved in the
	 * transaction including eventually and the current node. Not <tt>null</tt>. 
	 * 
	 * @throws DeploymentException
	 */
	public StopTransaction(final String applicationName,
		final DeployServiceContext ctx, final int[] initialParticipants)
		throws DeploymentException {
		super(applicationName, ctx, DeployConstants.stopApp, 
			initialParticipants, null);
		assert initialParticipants != null;
		if (location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
				"Global [{0}] of application [{1}] initially invoked for [{2}] server IDs.",
				getTransactionType(), applicationName, 
				CAConvertor.toString(initialParticipants, ""));
		}
	}

	/**
	 * Creates a standalone local stop transaction. There is not need to send
	 * any responses, because this transaction is independent.
	 * 
	 * @param appName the name of the application which has to be stopped.
	 * @param ctx deploy service context.
	 * @param cause the cause for the stop transaction. If this parameter is 
	 * <tt>null</tt> the transaction is executed on user request.
	 * @throws DeploymentException
	 */
	public StopTransaction(final String appName, final Component cause,
		final DeployServiceContext ctx) throws DeploymentException {
		super(appName, ctx, DeployConstants.stopApp, null, cause);
		if (dInfo.getContainerNames() == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NOT_AVAILABLE_INFO_ABOUT_CONTS,
				getModuleID(), getTransactionType());
			sde.setMessageID("ASJ.dpl_ds.005033");
			throw sde;
		}
	}

	@Override
	public void lock() throws LockSetNotAcquiredException,
		InterruptedException, ConflictingOperationLockException {
		lockSet = ctx.getLockManager().lock(
			new StopLockEvaluator(Applications.getReferenceGraph(), 
				ctx.getLockManager().getLockTracker(),
				getTransactionType(), false, getComponent(),
				isEnqueueLockNeeded() ? getLockType() : 0,
				PropManager.getInstance().getTimeout4LocalLock()));
	}

	@Override
	public boolean isNeeded() {
		return needForTransaction(Status.STOPPED);
	}

	public void begin() throws DeploymentException {
		if (location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
				"Begin [{0}] of application [{1}]", getTransactionType(),
				getModuleID());
		}
		if (!isSuccessfullyFinished()) {
			// The stop transaction can be marked as finished on the current node 
			// during needForTransaction check or beginCommon.
			beginCommon();
		}
	}

	public void beginLocal() throws DeploymentException {
		if (location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
					"Begin local [{0}] of application [{1}]",
					getTransactionType(), getModuleID());
		}
		beginCommon();
	}

	private void beginCommon() throws DeploymentException {
		assert isValid(dInfo.getStatus(), Status.STOPPED, Status.STARTING,
			Status.STOPPING);
		traceMe();
		unregisterContainerOnApplicationStop();
		final TransactionCommunicator comm = ctx.getTxCommunicator();
		final String moduleId = getModuleID();
		if (Status.IMPLICIT_STOPPED.equals(dInfo.getStatus())) {
			comm.setLocalApplicationStatus(moduleId, Status.STOPPED,
				StatusDescriptionsEnum.STOPPED_ON_USER_REQUEST, null);
			setSuccessfullyFinished(true);
		} else {
			// TODO: Here stopping may be set at update/stop of referred app.
			if(getCause() == null) {
				comm.setLocalApplicationStatus(getModuleID(), Status.STOPPING,
				StatusDescriptionsEnum.STOPPING_ON_USER_REQUEST, null);
			} else {
				comm.setLocalApplicationStatus(getModuleID(), Status.STOPPING,
				    StatusDescriptionsEnum.STOPPING_AS_REFERS, 
				    new Object[] { getCause().getName(), getCause().getType()});
			}
			// Stop the successors.
			ctx.getReferenceResolver()
				.componentIsGettingUnavailable(getComponent());
			initConfig();
		}
	}

	public void prepare() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Prepare [{0}] of application [{1}]", getTransactionType(),
				getModuleID());
		}
		prepareCommon();
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
		if (isSuccessfullyFinished()) {
			return;
		}

		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		DeploymentInfo info = communicator.getApplicationInfo(getModuleID());
		ContainerInterface[] tempConts = getContainers4Application(info, false);
		Arrays.sort(tempConts, ContainerComparator.instance);
		for (int i = 0; i < tempConts.length; i++) {
			try {
				tempConts[i].prepareStop(getModuleID(), config);
			} catch (Throwable th) {
				logThrowableAlwaysSucceeds(this, tempConts[i]
						.getContainerInfo().getName()
						+ " container", th);
			} finally {
				addContainer(tempConts[i], null);
			}
		}
		if (containers == null) {
			// if the server is shutting down - don't throw exception
			if (!ctx.isMarkedForShutdown()) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.NO_AVAILABLE_CONTS_FOR_APP,
					getModuleID(), getTransactionType());
				sde.setMessageID("ASJ.dpl_ds.005036");
				throw sde;
			}
		}
	}

	public void commonCommitFinished() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Commit [{0}] finished of application [{1}]",
				getTransactionType(), getModuleID());
		}
		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		try {
			communicator.getApplicationInfo(getModuleID());
			if (containers != null) {
				for (int i = 0; i < containers.length; i++) {
					try {
						containers[i].commitStop(getModuleID());
					} catch (Throwable th) {
						logThrowableAlwaysSucceeds(this, containers[i]
								.getContainerInfo().getName()
								+ " container", th);
					}
				}
			}
			communicator.removeApplicationLoader(getModuleID());
			Hashtable env = new Hashtable();
			env.put("clear_cache", "true");
			(new InitialContext(env)).lookup("");
			if (location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
						"Cache cleared, while stopping [{0}] application.",
						getModuleID());
			}
		} catch (Exception ex) {
			logThrowableAlwaysSucceeds(this, "commit", ex);
		}
		this.setSuccessfullyFinished(true);
	}

	public void commit() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Commit [{0}] of application [{1}]", getTransactionType(),
				getModuleID());
		}
		commitCommon();
	}

	public void commitLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Commit local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		commitCommon();
	}

	private void commitCommon() {
		if (isSuccessfullyFinished()) {
			return;
		}
		try {
			commonCommitFinished();
		} finally {
			setFinalLocalApplicationStatus();
			try {
				rollbackHandler();
			} catch (ConfigurationException cex) {
				logThrowableAlwaysSucceeds(
					this, (isLocal() ? "local " : "") + "commit", cex);
			}
			notifyAppCfgHandler4AppStop();
		}
	}

	private void notifyAppCfgHandler4AppStop() {
		try {
			PropManager.getInstance().getApplicationConfigHandlerFactoryImpl()
				.applicationStopped(getModuleID());
		} catch (Exception e) {
			logThrowableAlwaysSucceeds(this, "notifyAppCfgHandler4AppStop", e);
		}
	}

	public void rollback() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		rollbackCommon();
	}

	/**
	 * Set the final application status. If the transaction was performed by
	 * user request, the status will be set to <tt>STOPPED</tt>. If there is a
	 * cause for transaction, the new status will be set to <tt>STOPPED</tt> if
	 * this is lazy application not referenced hard by any applications with 
	 * startup mode <tt>ALWAYS</tt>. Otherwise the status will be set to
	 * <tt>IMPLICIT_STOPPED</tt> in order to provide &quot;a path &quot; in the 
	 * chain, which will be used during the start. For example:<br>  
	 * <b>a -> b -> c </b> and now we are stopping <b>b</b>, because <b>c</b> 
	 * is unavailable.<br>
	 * <li>If <b>a</b> has <tt>ALWAYS</tt> startup mode, its new status will be
	 * <tt>IMPLICIT_STOPPED</tt> and we have to set the same status for all
	 * applications in between.</li>
	 * <li>If <b>a</b> and <b>b</b> are lazy, then <b>a</b> and <b>b</b> will 
	 * remain in <tt>STOPPED</tt> status.
	 */
	private void setFinalLocalApplicationStatus() {
		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		if(getCause() == null) {
			// Performed by user request.
			communicator.setLocalApplicationStatus(getModuleID(),
				Status.STOPPED,
				StatusDescriptionsEnum.STOPPED_ON_USER_REQUEST, null);
		} else if(dInfo.isSupportingLazyStart() && ! ctx.getReferenceResolver()
			.isReferencedHardByImplicitStoppedApps(getComponent())) {
			// Lazy application not referenced 
			// by implicit stopped applications.
			communicator.setLocalApplicationStatus(getModuleID(),
				Status.STOPPED,
				StatusDescriptionsEnum.STOPPED_AS_RESOURCE_UNAVAILABLE,
				new Object[] { getCause() });
		} else {
			// Lazy start is not supported or is referenced hard by some
			// implicit stopped applications.
			communicator.setLocalApplicationStatus(getModuleID(),
				Status.IMPLICIT_STOPPED,
				StatusDescriptionsEnum.IMPLICIT_STOPPED_AS_RESOURCE_UNAVAILABLE,
				new Object[] { getCause() });
		}
	}

	public void rollbackLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		rollbackCommon();
	}

	public void rollbackCommon() {
		assert !isSuccessfullyFinished();

		setFinalLocalApplicationStatus();
		try {
			if (getHandler() != null) {
				rollbackHandler();
			}
		} catch (ConfigurationException cex) {
			logThrowableAlwaysSucceeds(this, "rollback", cex);
		}

		notifyAppCfgHandler4AppStop();

	}

	public void rollbackPrepare() {
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
		assert !isSuccessfullyFinished();
		if (containers != null) {
			for (int i = 0; i < containers.length; i++) {
				try {
					containers[i].rollbackStop(getModuleID());
				} catch (Throwable th) {
					logThrowableAlwaysSucceeds(this, containers[i]
						.getContainerInfo().getName()
						+ " container", th);
				}
			}
		}
		try {
			ctx.getTxCommunicator().removeApplicationLoader(getModuleID());
		} catch (DeploymentException dex) {
			logThrowableAlwaysSucceeds(this, "remove application loader", dex);
		}
		rollback();
	}

	private void initConfig() throws DeploymentException {
		openHandler();
		config = openApplicationConfiguration(
			DeployConstants.ROOT_CFG_APPS, ConfigurationHandler.READ_ACCESS);
	}

	private void unregisterContainerOnApplicationStop() {
		ArrayList<String> containers = Containers.getInstance()
				.getContainersForComponent(getModuleID());
		if (containers != null && containers.size() > 0) {
			final ContainerManagement cm = ctx.getContainerManagement();
			for (String cont : containers) {
				// stopMyApplications
				DeployCommunicatorImpl dComm = (DeployCommunicatorImpl) Containers
						.getInstance().getCommunicator(cont);
				if (dComm != null) {
					try {
						dComm.stopMyApplications(dComm.getMyApplications(),
								true);
					} catch (RemoteException re) {
						SimpleLogger.traceThrowable(Severity.ERROR, location, 
							"ASJ.dpl_ds.006374",
							"Error in stop my applications on remote node",
							re);
					}
				}
				// unregister container
				cm.unregisterContainer(cont);
			}
		}
	}
}