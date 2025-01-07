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
import java.util.ArrayList;

import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.container.AdditionalAppInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.TransactionCommunicator;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.editor.DIWriter;
import com.sap.engine.services.deploy.server.editor.EditorFactory;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;
import com.sap.tc.logging.Location;

/**
 * AddAppInfoChangeTransaction Change application info. Before to inform all
 * containers about the additional application info, the application will be
 * stopped and later will be started again. 
 * 
 * @author Radoslav Tsiklovski
 * @version
 */
public class AddAppInfoChangeTransaction extends ApplicationTransaction {
	private static final Location location = 
		Location.getLocation(AddAppInfoChangeTransaction.class);
	
	private boolean needRestart;
	private final AdditionalAppInfo additionalInfo;
	private int[] needRestartServers = new int[0];

	// global
	public AddAppInfoChangeTransaction(final String _applicationName,
		final DeployServiceContext ctx,
		final AdditionalAppInfo _additionalInfo) {

		super(ctx);
		ValidateUtils.nullValidator(_additionalInfo,
			"additional application info");
		init(_applicationName);
		this.additionalInfo = _additionalInfo;
		this.setSerializable(_additionalInfo);
	}

	// local
	public AddAppInfoChangeTransaction(final String _applicationName,
		final DeployServiceContext ctx, final String[] containerNames,
		final AdditionalAppInfo _additionalInfo) throws DeploymentException {
		super(ctx);
		ValidateUtils.nullValidator(_additionalInfo,
			"additional application info");
		init(_applicationName);
		this.additionalInfo = _additionalInfo;

		if (containerNames != null && containerNames.length > 0) {
			ContainerInterface ci = null;
			final TransactionCommunicator communicator = ctx
				.getTxCommunicator();
			DeploymentInfo info = communicator
				.getApplicationInfo(getModuleID());
			if (info == null) {
				info = refreshDeploymentInfoFromDB();
			}
			for (int i = 0; i < containerNames.length; i++) {
				ci = communicator.getContainer(containerNames[i]);
				if (ci != null) {
					addContainer(ci, null);
				} else {
					//if (!info.isOptionalContainer(containerNames[i]))  - check deleted because of no real use cases	
						ServerDeploymentException sde = new ServerDeploymentException(
							ExceptionConstants.NOT_AVAILABLE_CONTAINER,
							containerNames[i], getTransactionType(), 
							getModuleID());
						sde.setMessageID("ASJ.dpl_ds.005006");
						throw sde;
				}
			}
		}
	}

	private void init(String _applicationName) {
		ValidateUtils.nullValidator(_applicationName, "application name");
		setModuleID(_applicationName);
		setModuleType(DeployConstants.APP_TYPE);
		// it could be MODULE_TYPE as well
		setTransactionType(DeployConstants.appInfoChange);
	}

	public void begin() throws DeploymentException {
		if (location.bePath()) {
			DSLog.tracePath(location, "Begin phase of operation [{0}]",
				getTransactionType());
		}
		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		DeploymentInfo deployment = communicator
				.getApplicationInfo(getModuleID());
		if (deployment == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NOT_DEPLOYED,
				getModuleID(), "make runtime changes");
			sde.setMessageID("ASJ.dpl_ds.005005");
			throw sde;
		}
		ContainerInterface container = null;
		String[] contNames = deployment.getContainerNames();
		if (contNames == null || contNames.length == 0) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NOT_AVAILABLE_INFO_ABOUT_CONTS,
				getModuleID(), getTransactionType());
			sde.setMessageID("ASJ.dpl_ds.005033");
			throw sde;
		}
		for (int i = 0; i < contNames.length; i++) {
			container = communicator.getContainer(contNames[i]);
			if (container != null) {
				try {
					if (container.acceptedAppInfoChange(getModuleID(),
							additionalInfo)) {
						addContainer(container, null);
					}
				} catch (DeploymentException de) {
					throw de;
				} catch (OutOfMemoryError oofmer) {
					throw oofmer;
				} catch (ThreadDeath td) {
					throw td;
				} catch (Throwable th) {
					ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
						new String[] { "accepting changes in additional information for application "
							+ getModuleID() }, th);
					sde.setMessageID("ASJ.dpl_ds.005082");
					throw sde;
				}
				needRestart = needRestart
						|| container.needStopOnAppInfoChanged(getModuleID(),
								additionalInfo);
			} else {
				//if (!deployment.isOptionalContainer(contNames[i]))  - check deleted because of no real use cases	
					ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.NOT_AVAILABLE_CONTAINER,
						contNames[i], getTransactionType(), getModuleID());
					sde.setMessageID("ASJ.dpl_ds.005006");
					throw sde;
			}
		}
		if (containers == null) {
			final ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NO_CONT_FOR_APPINFO_CHANGED,
				getModuleID());
			sde.setMessageID("ASJ.dpl_ds.005095");
			if (location.beDebug()) {
				DSLog.traceDebug(location,"{0}", sde.getMessage());
			}
		}
		needRestart = needRestart && 
			Status.STARTED.equals(deployment.getStatus());
		if (needRestart) {
			int[] servers = ctx.getClusterMonitorHelper().findServers();
			ArrayList<Integer> temp = new ArrayList<Integer>();
			String appStatus = null;
			if (servers != null) {
				ClusterElement cEment = null;
				for (int i = 0; i < servers.length; i++) {
					try {
						cEment = PropManager.getInstance().getAppServiceCtx()
								.getClusterContext().getClusterMonitor()
								.getParticipant(servers[i]);
						if (cEment != null) {
							appStatus = communicator.getApplicationStatus(
									getModuleID(), cEment.getName());
							if (appStatus != null &&
								appStatus.equals(
									DeployService.STARTED_APP_STATUS)) {
								temp.add(servers[i]);
							}
						}
					} catch (RemoteException re) {
						ServerDeploymentException sde = new ServerDeploymentException(
							ExceptionConstants.COMPLEX_ERROR,
							new String[] { "while getting status of ["
								+ getModuleID() + "]." }, re);
						sde.setMessageID("ASJ.dpl_ds.005030");
						DSLog.logErrorThrowable(location, sde);
					}
				}
			}
			if (temp.size() > 0) {
				needRestartServers = new int[temp.size()];
				for (int i = 0; i < temp.size(); i++) {
					needRestartServers[i] = temp.get(i).intValue();
				}
			}
			makeNestedParallelTransaction(
				new StopTransaction(getModuleID(), ctx, needRestartServers));
		}
		try {
			final DeploymentInfo dInfo4Modification = (DeploymentInfo) deployment
					.clone();
			modifyDeploymentInfoInMemory(dInfo4Modification);

			openHandler();
			final Configuration config = openApplicationConfiguration(
				DeployConstants.ROOT_CFG_APPS,
				ConfigurationHandler.WRITE_ACCESS);
			final Configuration deployConfig = openApplicationConfiguration(
				DeployConstants.ROOT_CFG_DEPLOY,
				ConfigurationHandler.WRITE_ACCESS);

			final DIWriter diWriter = EditorFactory.getInstance().getDIWriter(
				deployment.getVersion());
			diWriter.modifyDeploymentInfo(config, deployConfig,
				dInfo4Modification);

			if (containers != null) {// it might be null, for instance when
				// changing the startUp
				for (int i = 0; i < containers.length; i++) {
					try {
						containers[i].makeAppInfoChange(getModuleID(),
							additionalInfo, config);
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
							new String[] { "applying changes in additional information of application "
								+ getModuleID() }, th);
						sde.setMessageID("ASJ.dpl_ds.005082");
						throw sde;
					}
				}
			}
			commitHandler();
		} catch (CloneNotSupportedException cnsEx) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION, new String[] {
					getTransactionType(), getModuleID() }, cnsEx);
			sde.setMessageID("ASJ.dpl_ds.005029");
			throw sde;
		} catch (ConfigurationException e) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_COMMIT_HANDLER, new String[] {
					getTransactionType(), getModuleID() }, e);
			sde.setMessageID("ASJ.dpl_ds.005026");
			throw sde;
		}
	}

	public void beginLocal() throws DeploymentException {
		if (location.bePath()) {
			DSLog.tracePath(location, "Begin local phase of operation [{0}]",
					getTransactionType());
		}
		if (containers == null) {
			final ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NO_CONT_FOR_APPINFO_CHANGED,
				getModuleID());
			sde.setMessageID("ASJ.dpl_ds.005095");
			if (location.beDebug()) {
				DSLog.traceDebug(location, "{0}", sde.getMessage());
			}
		}
		if (containers != null) {// it might be null, for instance when changing
			// the startUp
			for (int i = 0; i < containers.length; i++) {
				try {
					containers[i].notifyAppInfoChanged(getModuleID());
				} catch (WarningException wex) {
					addWarnings(wex.getWarnings());
				} catch (OutOfMemoryError oofme) {
					throw oofme;
				} catch (ThreadDeath td) {
					throw td;
				} catch (Throwable th) {
					ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
						new String[] { "notifying [" + getTransactionType()
							+ "] of application [" + getModuleID()
							+ "]" }, th);
					sde.setMessageID("ASJ.dpl_ds.005082");
					DSLog.logErrorThrowable(location, sde);
				}
			}
		}
	}

	public void prepare() throws DeploymentException {
		if (location.bePath()) {
			DSLog.tracePath(location, "Prepare phase of operation [{0}]",
				getTransactionType());
		}
	}

	public void prepareLocal() throws DeploymentException {
		if (location.bePath()) {
			DSLog.tracePath(location, "Prepare local phase of operation [{0}]",
				getTransactionType());
		}
	}

	public void commit() {
		if (location.bePath()) {
			DSLog.tracePath(location, "Commit phase of operation [{0}]",
					getTransactionType());
		}
		if (containers != null) {// it might be null, for instance when changing
			// the startUp
			for (int i = 0; i < containers.length; i++) {
				try {
					containers[i].appInfoChangedCommit(getModuleID());
				} catch (WarningException wex) {
					addWarnings(wex.getWarnings());
				} catch (OutOfMemoryError oofme) {
					throw oofme;
				} catch (ThreadDeath td) {
					throw td;
				} catch (Throwable th) {
					ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
						new String[] { "committing ["
							+ getTransactionType()
							+ "] of application [" + getModuleID()
							+ "]" }, th);
					sde.setMessageID("ASJ.dpl_ds.005082");
					DSLog.logErrorThrowable(location, sde);
				}
			}
		}

		DeploymentInfo dInfo = ctx.getTxCommunicator().getApplicationInfo(
			getModuleID());
		modifyDeploymentInfoInMemory(dInfo);

		restartApp();
		this.setSuccessfullyFinished(true);
	}

	public void commitLocal() {
		if (location.bePath()) {
			DSLog.tracePath(location, "Commit local phase of operation [{0}]",
				getTransactionType());
		}

		final DeploymentInfo dInfo = ctx.getTxCommunicator()
				.getApplicationInfo(getModuleID());
		modifyDeploymentInfoInMemory(dInfo);

		this.setSuccessfullyFinished(true);
	}

	private void modifyDeploymentInfoInMemory(DeploymentInfo dInfo) {
		dInfo.setFailOver(additionalInfo.getFailOver());
		dInfo.setStartUpO(additionalInfo.getStartUpO());

		// update the deployment info,
		// no check is required here as version is always validated
		// when set to an info, so the one we get from the additional
		// info will already have been validated; java version set
		// through this transaction is always considered custom
		dInfo.setJavaVersion(additionalInfo.getJavaVersion(), true);
	}

	public void rollback() {
		if (location.bePath()) {
			DSLog.tracePath(location, "Rollback phase of operation [{0}]",
				getTransactionType());
		}
		try {
			if (containers != null) {
				for (int i = 0; i < containers.length; i++) {
					try {
						containers[i].appInfoChangedRollback(getModuleID());
					} catch (WarningException wex) {
						addWarnings(wex.getWarnings());
					} catch (OutOfMemoryError oofme) {
						throw oofme;
					} catch (ThreadDeath td) {
						throw td;
					} catch (Throwable th) {
						ServerDeploymentException sde = new ServerDeploymentException(
							ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
							new String[] { "rolling back ["
										+ getTransactionType()
										+ "] of application [" + getModuleID()
										+ "]" }, th);
						sde.setMessageID("ASJ.dpl_ds.005082");
						DSLog.logErrorThrowable(location, sde);
					}
				}
			}
			restartApp();
		} finally {
			try {
				if (getHandler() != null) {
					rollbackHandler();
				}
			} catch (ConfigurationException rex) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "rollback phase of operation ["
						+ getTransactionType() + "] for application ["
						+ getModuleID() + "].\nReason: "
						+ rex.toString() }, rex);
				sde.setMessageID("ASJ.dpl_ds.005082");
				DSLog.logErrorThrowable(location, sde);
			}
			this.setSuccessfullyFinished(false);
		}
	}

	private void restartApp() {
		if (needRestart) {
			assert needRestartServers != null;
			try {
				makeNestedParallelTransaction(new StartTransaction(
					getModuleID(), ctx, needRestartServers));
			} catch (DeploymentException rex) {
				DSLog.logErrorThrowable(location, rex);
			}
		}
	}

	public void rollbackLocal() {
		if (location.bePath()) {
			DSLog.tracePath(location, "Rollback local phase of operation [{0}]",
				getTransactionType());
		}
	}

	public void rollbackPrepare() {
		// Empty
	}

	public void rollbackPrepareLocal() {
		if (location.bePath()) {
			DSLog.tracePath(location, "Rollback prepare local phase of operation [{0}]",
				getTransactionType());
		}
	}
}