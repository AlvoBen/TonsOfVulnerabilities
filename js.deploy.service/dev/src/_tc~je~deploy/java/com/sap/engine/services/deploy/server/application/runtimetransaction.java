/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.application;

import java.security.Policy;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.deploy.container.ApplicationDeployInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.TransactionCommunicator;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.ContainerData;
import com.sap.engine.services.deploy.server.editor.DIWriter;
import com.sap.engine.services.deploy.server.editor.EditorFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

public class RuntimeTransaction extends ApplicationTransaction {
	private static final Location location = 
		Location.getLocation(RuntimeTransaction.class);

	// global
	private boolean needRestart = false;
	private Configuration appConfig = null;

	/**
	 * Local runtime transaction.
	 * @param applicationName
	 * @param ctx
	 * @param containerName
	 * @throws DeploymentException
	 */
	public RuntimeTransaction(final String applicationName,
		final DeployServiceContext ctx, final String containerName)
		throws DeploymentException {
		this(applicationName, ctx, containerName, null, null, false);
	}

	/**
	 * Global runtime transaction.
	 * @param applicationName
	 * @param ctx
	 * @param containerName
	 * @param handler. Must not be null.
	 * @param appConfig
	 * @param needRestartApplication
	 * @throws DeploymentException
	 */
	public RuntimeTransaction(final String applicationName,
		final DeployServiceContext ctx, final String containerName,
		final ConfigurationHandler handler, final Configuration appConfig,
		final boolean needRestartApplication) throws DeploymentException {
		super(ctx);
		needRestart = needRestartApplication;
		if (applicationName == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NOT_SPECIFIED_APP_NAME,
				"", DeployConstants.runtimeChanges);
			sde.setMessageID("ASJ.dpl_ds.005012");
			throw sde;
		}
		setHandler(handler);
		this.appConfig = appConfig;
		setModuleID(applicationName);
		setModuleType(DeployConstants.APP_TYPE);
		// type could be MODULE_TYPE as well
		setTransactionType(DeployConstants.runtimeChanges);
		if (containerName == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NOT_SPECIFIED_CONT_NAME, 
				getTransactionType(), getModuleID());
			sde.setMessageID("ASJ.dpl_ds.005052");
			throw sde;
		}
		ContainerInterface ci = ctx.getTxCommunicator().getContainer(
			containerName);

		if (ci == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NOT_AVAILABLE_CONTAINER,
				containerName, getTransactionType(), getModuleID());
			sde.setMessageID("ASJ.dpl_ds.005006");
			throw sde;
		}
		addContainer(ci, null);
		assert containers != null;
	}

	public void begin() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Begin phase of operation [{0}]", getTransactionType());
		}
		final TransactionCommunicator comm = ctx.getTxCommunicator();
		DeploymentInfo deployment = comm.getApplicationInfo(getModuleID());
		if (needRestart) {
			if (getModuleID() != null &&
				!Status.STOPPED.equals(deployment.getStatus()) &&
				!Status.IMPLICIT_STOPPED.equals(deployment.getStatus())) {
				makeNestedParallelTransaction(
					new StopTransaction(getModuleID(), ctx, 
						ctx.getClusterMonitorHelper().findServers()));
			} else {
				needRestart = false;
			}
		}
	}

	/* 
	 * This is the local part of the transaction. The global part is already
	 * finished on the global server node.
	 */
	public void beginLocal() throws DeploymentException {
		assert getHandler() == null;
		assert appConfig == null;
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Begin local phase of operation [{0}]",
				getTransactionType());
		}
		openHandler();
		appConfig = openApplicationConfiguration(
			DeployConstants.ROOT_CFG_APPS, ConfigurationHandler.READ_ACCESS);
		// Here the handler and appConfig are already initialized (not null)
		ctx.getTxCommunicator().refreshDeploymentInfoFromDB(
			getModuleID(), appConfig, getHandler());
		try {
			containers[0].notifyRuntimeChanges(getModuleID(), appConfig);
		} catch (WarningException ce) {
			addWarnings(ce.getWarnings());
		} catch (OutOfMemoryError oofme) {
			throw oofme;
		} catch (ThreadDeath td) {
			throw td;
		} catch (Throwable th) {
			final ServerDeploymentException sdex = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { "notifying runtime changes of application ["
					+ getModuleID() + "]" }, th);

			SimpleLogger.traceThrowable(Severity.ERROR, location, 
				sdex.getLocalizedMessage(), th);
		} finally {
			try {
				rollbackHandler();
			} catch (ConfigurationException ex) {
				final ServerDeploymentException sdex = 
					new ServerDeploymentException(
						ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
						new String[] { "local begin phase of operation ["
							+ getTransactionType() + "] for application ["
							+ getModuleID() + "].\nReason: "
							+ ex.toString() }, ex);
				SimpleLogger.traceThrowable(Severity.ERROR, location, 
					sdex.getLocalizedMessage(), ex);
			}
		}
	}

	public void prepare() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Prepare phase of operation [{0}]", getTransactionType());
		}
		// Containers are checked in the constructor.
		try {
			containers[0].prepareRuntimeChanges(getModuleID());
		} catch (WarningException dex) {
			addWarnings(dex.getWarnings());
		} catch (DeploymentException de) {
			throw de;
		} catch (OutOfMemoryError oofmer) {
			throw oofmer;
		} catch (ThreadDeath td) {
			throw td;
		} catch (Throwable th) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { "preparing runtime changes of application "
					+ getModuleID() }, th);
			sde.setMessageID("ASJ.dpl_ds.005082");
			throw sde;
		}
	}

	public void prepareLocal() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Prepare local phase of operation [{0}]",
				getTransactionType());
		}
	}

	// NOTE: Container contract is wrong, because containers implement
	// commitRuntimeChanges method instead of prepareRuntimeChanges method, but 
	// in the same time commit methods of this
	// transaction cannot fail and in case
	// of failure cannot notify containers for such errors.
	// Main goal is to prevent DB data corruption and roll back the handler in
	// case of failure, which won't hide the issue,
	// because for end user the operation will fail and will report new issue,
	// which is supposed to be investigated in details
	// to find out why our logic in commit method can fail.
	public void commit() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Commit phase of operation [{0}]", getTransactionType());
		}
		ApplicationDeployInfo appInfo = null;
		try {
			appInfo = containers[0].commitRuntimeChanges(getModuleID());
		} catch (WarningException wex) {
			addWarnings(wex.getWarnings());
		} catch (OutOfMemoryError oofme) {
			throw oofme;
		} catch (ThreadDeath td) {
			throw td;
		} catch (Throwable th) {
			ServerDeploymentException sdex = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { "committing runtime changes of application ["
					+ getModuleID() + "]" }, th);
			SimpleLogger.traceThrowable(Severity.ERROR, location, 
				sdex.getLocalizedMessage(), th);
		}

		boolean isChanged = false;
		if (appInfo != null) {
			DeploymentInfo deployInfo = ctx.getTxCommunicator()
				.getApplicationInfo(getModuleID());
			try {
				final ContainerData cData = deployInfo
					.getOrCreateContainerData(
						containers[0].getContainerInfo().getName());
				TransactionUtil.updateCDataInDInfo(this, deployInfo,
					deployInfo, appInfo, cData.getDeployedFileNames(),
					containers[0]);

				final DIWriter diWriter = EditorFactory.getInstance()
					.getDIWriter(deployInfo.getVersion());
				diWriter.modifyDeploymentInfo(appConfig, null, deployInfo);
				isChanged = true;
			} catch (DeploymentException dex) {
				SimpleLogger.traceThrowable(Severity.ERROR, location, 
					dex.getLocalizedMessage(), dex);
			} finally {
				try {
					if (isChanged) {
						commitHandler();
					} else {
						rollbackHandler();
					}
				} catch (ConfigurationException rex) {
					final ServerDeploymentException sdex = 
						new ServerDeploymentException(
							ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
							new String[] { "commit phase of operation ["
									+ getTransactionType()
									+ "] for application [" + getModuleID()
									+ "].\nReason: " + rex.toString() }, rex);
					SimpleLogger.traceThrowable(Severity.ERROR, location, sdex
							.getLocalizedMessage(), rex);
				}
			}
		}

		Policy.getPolicy().refresh();
		this.setSuccessfullyFinished(true);
	}

	public void commitLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
					"Commit local phase of operation [{0}]",
					getTransactionType());
		}
		this.setSuccessfullyFinished(true);
	}

	public void rollback() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
					"Rollback phase of operation [{0}]", getTransactionType());
		}
		try {
			containers[0].rollbackRuntimeChanges(getModuleID());
		} catch (WarningException wex) {
			addWarnings(wex.getWarnings());
		} catch (OutOfMemoryError oofme) {
			throw oofme;
		} catch (ThreadDeath td) {
			throw td;
		} catch (Throwable th) {
			final ServerDeploymentException sdex = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { "rolling back runtime changes of application ["
					+ getModuleID() + "]" }, th);
			SimpleLogger.traceThrowable(Severity.ERROR, location, 
				sdex.getLocalizedMessage(), th);
		}
		try {
			if (getHandler() != null) {
				rollbackHandler();
			}
		} catch (ConfigurationException rex) {
			final ServerDeploymentException sdex = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { "rollback phase of operation ["
					+ getTransactionType() + "] for application ["
					+ getModuleID() + "].\nReason: " + rex.toString() },
				rex);
			SimpleLogger.traceThrowable(Severity.ERROR, location, 
				sdex.getLocalizedMessage(), rex);
		}
	}

	public void rollbackLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback local phase of operation [{0}]",
				getTransactionType());
		}
	}

	public void rollbackPrepare() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback prepare phase of operation [{0}]",
				getTransactionType());
		}
		try {
			containers[0].rollbackRuntimeChanges(getModuleID());
		} catch (WarningException wex) {
			addWarnings(wex.getWarnings());
		} catch (OutOfMemoryError oofme) {
			throw oofme;
		} catch (ThreadDeath td) {
			throw td;
		} catch (Throwable th) {
			final ServerDeploymentException sdex = 
				new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "rolling back runtime changes of application ["
						+ getModuleID() + "]" }, th);
			SimpleLogger.traceThrowable(Severity.ERROR, location, 
				sdex.getLocalizedMessage(), th);
		}
		try {
			if (getHandler() != null) {
				rollbackHandler();
			}
		} catch (ConfigurationException rex) {
			final ServerDeploymentException sdex = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { "rollback prepare phase of operation ["
					+ getTransactionType() + "] for application ["
					+ getModuleID() + "].\nReason: " + rex.toString() },
				rex);
			SimpleLogger.traceThrowable(Severity.ERROR, location, 
				sdex.getLocalizedMessage(), rex);
		}
	}

	public void rollbackPrepareLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
					"Rollback prepare local phase of operation [{0}]",
					getTransactionType());
		}
	}

	@Override
	protected void finalActions() throws WarningException, DeploymentException {
		if (needRestart) {
			final TransactionCommunicator communicator = ctx
					.getTxCommunicator();
			DeploymentInfo deployment = communicator
					.getApplicationInfo(getModuleID());
			try {
				if (getModuleID() != null &&
					(Status.STOPPED.equals(deployment.getStatus()) || 
					 Status.IMPLICIT_STOPPED.equals(deployment.getStatus()))) {
					makeNestedParallelTransaction(new StartTransaction(
						getModuleID(), ctx, 
						ctx.getClusterMonitorHelper().findServers()));
				}
			} catch (DeploymentException rex) {
				SimpleLogger.traceThrowable(Severity.ERROR, location, 
					rex.getLocalizedMessage(), rex);
			}
		}
	}
}