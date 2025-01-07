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

import java.util.Arrays;
import java.util.Map;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.services.deploy.container.ComponentNotDeployedException;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.ContainerInterfaceExtension;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.container.op.start.ApplicationStartInfo;
import com.sap.engine.services.deploy.container.op.start.ContainerStartInfo;
import com.sap.engine.services.deploy.container.op.util.ModuleProvider;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.TransactionCommunicator;
import com.sap.engine.services.deploy.server.cache.containers.ContainerComparatorReverted;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.InitiallyStarted;
import com.sap.engine.services.deploy.server.editor.DIWriter;
import com.sap.engine.services.deploy.server.editor.EditorFactory;
import com.sap.engine.services.deploy.server.utils.ConfigUtils;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Transaction executed during the first start of an application after its
 * deployment. This transaction is performed only on one server node in the
 * cluster, during the first start of the application.
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class StartInitiallyTransaction extends ApplicationTransaction {
	private static final String PREFIX = "StartInitially:";
	private static final Location location = 
		Location.getLocation(StartInitiallyTransaction.class);

	private final ContainerInterface[] contIntfs;
	private final DeploymentInfo oldDInfo;

	private boolean isDownloadNeeded;

	/**
	 * Global start initially transaction executed as a part of global start
	 * transaction.
	 * 
	 * @param appName application name.
	 * @param ctx deploy service context.
	 * @throws DeploymentException
	 */
	public StartInitiallyTransaction(final String appName,
		final DeployServiceContext ctx) throws DeploymentException {
		super(ctx);
		init(appName, true);
		this.contIntfs = getContainers4Application(
			ctx.getTxCommunicator().getApplicationInfo(getModuleID()), true);
		Arrays.sort(contIntfs, ContainerComparatorReverted.instance);
		try {
			this.oldDInfo = (DeploymentInfo) getDeploymentInfo().clone();
		} catch (CloneNotSupportedException cnsEx) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION, new String[] {
					getTransactionType(), getModuleID() }, cnsEx);
			sde.setMessageID("ASJ.dpl_ds.005029");
			throw sde;
		}
	}

	/**
	 * Initiates local start initially transaction, as a result of received
	 * start initially command, which is sent by the initiator node. Start
	 * initially transaction will not send response to the initiator.
	 * 
	 * @param appName the name of the application which has to be started
	 * initially.
	 * @param ctx deploy service context.
	 * @param isDownloadNeeded
	 * @throws DeploymentException
	 */
	public StartInitiallyTransaction(final String appName,
		final DeployServiceContext ctx, final boolean isDownloadNeeded)
		throws DeploymentException {
		super(ctx);
		init(appName, isDownloadNeeded);
		this.contIntfs = null;// won't be needed, so it has to be null
		this.oldDInfo = null;// won't be needed, so it has to be null
		setLockType(LockingConstants.MODE_SHARED);
	}

	private void init(String applicationName, boolean isDownloadNeeded) {
		setModuleID(applicationName);
		setModuleType(DeployConstants.APP_TYPE);
		setTransactionType(DeployConstants.startInitiallyApp);
		setTrackable(false);
		this.isDownloadNeeded = isDownloadNeeded;
		setShmComponentStatusExpected();
		setShmComponentStartupMode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.DTransaction#begin()
	 */
	public void begin() throws DeploymentException,
		ComponentNotDeployedException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Begin [{0}] of application [{1}]", getTransactionType(),
				getModuleID());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.DTransaction#beginLocal()
	 */
	public void beginLocal() throws DeploymentException,
		ComponentNotDeployedException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Begin local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.DTransaction#prepare()
	 */
	public void prepare() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Prepare [{0}] of application [{1}]", getTransactionType(),
				getModuleID());
		}

		if (!isNeeded()) {
			if (location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"The application [{0}] has already been started initially.",
					getModuleID());
			}
			return;
		}
		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		ClassLoader loader = null;
		ClassLoader ctxClassLoader = null;
		try {
			openHandler();
			Configuration appsCfg = openApplicationConfiguration(
				DeployConstants.ROOT_CFG_APPS,
				ConfigurationHandler.WRITE_ACCESS);
			final Configuration deployCfg = openApplicationConfiguration(
				DeployConstants.ROOT_CFG_DEPLOY,
				ConfigurationHandler.WRITE_ACCESS);

			// OncePerInstance is not invoked
			bootstrapApplication();
			
			loader = communicator.bindLoader(getDeploymentInfo());
			// Some applications need sapxmltoolkit parser, which is obtained
			// through context classloader.
			ctxClassLoader = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(loader);
			final ContainerStartInfo cStartInfo = getContainerStartInfo(
					appsCfg, deployCfg, loader);
			if (location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null, 
					"{0}", cStartInfo);
			}
			ApplicationStartInfo appStartInfo = null;
			ContainerInterface ci = null;
			for (int i = 0; i < contIntfs.length; i++) {
				ci = contIntfs[i];
				appStartInfo = makeStartInitially(
						(ContainerInterfaceExtension) ci, cStartInfo);
				TransactionUtil.updateCDataInDInfo(this, getDeploymentInfo(),
						appStartInfo, ci);

			}
			ValidateUtils.missingDCinDIValidator(getDeploymentInfo(),
					getTransactionType(), contIntfs);

			ctx.getReferenceResolver().check4Cycles(getModuleID());

			// TODO - delete the files downloaded to the FS from the
			// ModuleProvider
			getDeploymentInfo().setInitiallyStarted(InitiallyStarted.YES);
			// its new value for DB
			getDeploymentInfo().setModuleProvider(null);
			// this will delete it from DB
			final DIWriter diWriter = 
				EditorFactory.getInstance().getDIWriter(appsCfg);
			diWriter.modifyDeploymentInfo(
				appsCfg, deployCfg, getDeploymentInfo());
			setHandler(ConfigUtils.commitHandler(getHandler(),
				getTransactionType() + " of " + getModuleID()));
		} finally {
			try {
				if (loader != null) {
					Thread.currentThread()
						.setContextClassLoader(ctxClassLoader);
					communicator.removeApplicationLoader(getModuleID());
				}
			} finally {
				setHandler(ConfigUtils.rollbackHandler(getHandler(),
					getTransactionType() + " of " + getModuleID()));
			}
		}

		// OncePerInstance should not be invoked on this instance
		bootstrapApplication();
	}

	@SuppressWarnings("boxing")
	private ApplicationStartInfo makeStartInitially(
		ContainerInterfaceExtension container, ContainerStartInfo cStartInfo)
		throws DeploymentException {
		ApplicationStartInfo appStartInfo = null;
		final String cName = container.getContainerInfo().getName();
		try {
			if (location.beDebug()) {
				SimpleLogger.trace(Severity.ERROR, location, null,
					"Starting operation [{0}] on [{1}] container for [{2}].",
					getTransactionType(), cName, getModuleID());
			}
			long time = System.currentTimeMillis();
			appStartInfo = container.makeStartInitially(cStartInfo);
			time = System.currentTimeMillis() - time;
			if (location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"Finished operation [{0}] on [{1}] container for [{2}]. It took [{3}] ms.",
					getTransactionType(), cName, getModuleID(), time);
			}
		} catch (DeploymentException de) {
			throw de;
		} catch (Exception th) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.EXCEPTION_DURING_START_INITIALLY,
				new String[] { getModuleID(), cName }, th);
			sde.setMessageID("ASJ.dpl_ds.005107");
			sde.setDcNameForObjectCaller(container);
			throw sde;
		} catch (OutOfMemoryError oofmer) {
			throw oofmer;
		} catch (ThreadDeath td) {
			throw td;
		} catch (Error err) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.EXCEPTION_DURING_START_INITIALLY,
					new String[] { getModuleID(), cName }, err);
			sde.setMessageID("ASJ.dpl_ds.005107");
			sde.setDcNameForObjectCaller(container);
			throw sde;
		} finally {
			addContainer(container, null);
		}
		return appStartInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.DTransaction#prepareLocal()
	 */
	public void prepareLocal() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Prepare local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}

		if (!isNeeded()) {
			if (location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"The application [{0}] has already been started initially.",
					getModuleID());
			}
			return;
		}

		openHandler();
		try {
			final Configuration appsCfg = openApplicationConfiguration(
				DeployConstants.ROOT_CFG_APPS,
				ConfigurationHandler.READ_ACCESS);
			// Will update the whole DeployInfo
			ctx.getTxCommunicator().refreshDeploymentInfoFromDB(
				getModuleID(), appsCfg, getHandler());
		} finally {
			setHandler(ConfigUtils.commitHandler(getHandler(),
				getTransactionType() + " of " + getModuleID()));
		}
		bootstrapApplication();
	}

	private void bootstrapApplication() throws DeploymentException {
		if (isDownloadNeeded) {
			bootstrapApplication(getModuleID());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.DTransaction#commit()
	 */
	public void commit() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Commit [{0}] of application [{1}]", getTransactionType(),
				getModuleID());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.DTransaction#commitLocal()
	 */
	public void commitLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Commit local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.DTransaction#rollback()
	 */
	public void rollback() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback in [{0}] for application [{1}].",
				getTransactionType(), getModuleID());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.DTransaction#rollbackLocal()
	 */
	public void rollbackLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback local in [{0}] for application [{1}].",
				getTransactionType(), getModuleID());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.DTransaction#rollbackPrepare()
	 */
	public void rollbackPrepare() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.ERROR, location, null,
				"Rollback prepare in [{0}] for application [{1}].",
				getTransactionType(), getModuleID());
		}

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
					final ServerDeploymentException sdex = 
						new ServerDeploymentException(
							ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
							new String[] { "rolling back start of application ["
									+ getModuleID() + "]" }, th);
					SimpleLogger.traceThrowable(Severity.ERROR, location, null,
						sdex.getLocalizedMessage(), th);
				}
			}
		}
		oldDInfo.setExceptionInfo(getDeploymentInfo().getExceptionInfo());
		ctx.getTxCommunicator().addApplicationInfo(
			oldDInfo.getApplicationName(), oldDInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.DTransaction#rollbackPrepareLocal()
	 */
	public void rollbackPrepareLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback prepare local in [{0}] for application [{1}].",
				getTransactionType(), getModuleID());
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isNeeded() {
		return InitiallyStarted.NO.equals(getDeploymentInfo()
				.getInitiallyStarted());
	}

	public boolean needForTransaction2DB() throws DeploymentException {
		if (isNeeded()) {
			refreshDeploymentInfoFromDB();
			// Optimize it to read only the InitiallyStarted information first.
		}
		return isNeeded();
	}

	public String getLockKeyForModuleID() {
		return PREFIX + getModuleID();
	}

	private ContainerStartInfo getContainerStartInfo(Configuration appsCfg,
			Configuration deployCfg, ClassLoader loader)
			throws DeploymentException {
		final ModuleProvider moduleProvider = EditorFactory.getInstance()
				.getDIReader(deployCfg).readModuleProvider(deployCfg,
						getDeploymentInfo().getThisAppWorkDir());
		return new ContainerStartInfo(getModuleID(), appsCfg, loader,
				getDeploymentInfo().getRemoteSupport(), moduleProvider,
				getDeploymentInfo().getFailOver(), getDeploymentInfo()
						.getProperties());
	}

	@Override
	protected void finalActions() throws WarningException, DeploymentException {
		if (location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
				"Final actions in [{0}] for application [{1}].",
				getTransactionType(), getModuleID());
		}
	}
	
	@Override
	protected Map<String, Object> prepareNotification() {
		final Map<String, Object> cmd = super.prepareNotification();
		final int dlSrvs[] = ctx.getClusterMonitorHelper()
			.findOneServerPerInstanceExceptCurrent();
		if (dlSrvs.length > 0) {
			cmd.put(DeployConstants.DOWNLOAD, dlSrvs);
		}
		return cmd;
	}

	public DeploymentInfo getDeploymentInfo() {
		return ctx.getTxCommunicator().getApplicationInfo(getModuleID());
	}
}