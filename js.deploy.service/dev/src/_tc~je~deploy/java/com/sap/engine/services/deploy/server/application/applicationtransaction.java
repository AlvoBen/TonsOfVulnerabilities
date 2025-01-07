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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.NameAlreadyExistsException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.admin.ConfigurationHandlerExtension;
import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.lib.time.SystemTime;
import com.sap.engine.services.accounting.APredefinedComponent;
import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ComponentNotDeployedException;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.container.migration.utils.CMigrationStatus;
import com.sap.engine.services.deploy.container.op.util.StartUp;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.DTransaction;
import com.sap.engine.services.deploy.server.DUtils;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.TransactionCommunicator;
import com.sap.engine.services.deploy.server.TransactionManager;
import com.sap.engine.services.deploy.server.TransactionStatistics;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.ApplicationName;
import com.sap.engine.services.deploy.server.editor.DIReader;
import com.sap.engine.services.deploy.server.editor.EditorFactory;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.remote.RemoteCaller;
import com.sap.engine.services.deploy.server.utils.ShmComponentUtils;
import com.sap.engine.services.deploy.server.utils.TxOperationsHelper;
import com.sap.engine.services.deploy.server.utils.cfg.MigrationConfigUtils;
import com.sap.engine.services.deploy.server.utils.concurrent.ConflictingOperationLockException;
import com.sap.engine.services.deploy.server.utils.concurrent.LockSet;
import com.sap.engine.services.deploy.server.utils.concurrent.LockSetNotAcquiredException;
import com.sap.engine.services.deploy.server.utils.concurrent.eval.SingleNodeLockEvaluator;
import com.sap.engine.services.deploy.timestat.DeployOperationTimeStat;
import com.sap.engine.services.deploy.timestat.ITimeStatConstants;
import com.sap.engine.services.deploy.timestat.TransactionTimeStat;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;
import com.sap.transaction.TxException;

/**
 * ApplicationTransaction is the base class for all deploy service transactions.
 * Copyright (c) 2003, SAP-AG
 * 
 * @author Rumiana Angelova
 * @version
 */
public abstract class ApplicationTransaction implements DTransaction {
	private static final String CONTAINERS = "containers";
	private static final int WAITING_TIME_DURING_APP_CONFIG_CREATION = 500;
	private static final int MAX_ATTEMPTS_TO_CREATE_APP_CONFIG = 10;

	private static final Location location = 
		Location.getLocation(ApplicationTransaction.class);

	protected final DeployServiceContext ctx;
	protected final TransactionStatistics currentStatistics;
	private final TxOperationsHelper txHelper;

	protected byte moduleType;
	protected String transactionType;
	private boolean lockNeeded;
	protected ContainerInterface[] containers;
	protected Properties contProperties = new Properties();
	private ApplicationName appName;
	private String softwareType;
	private ConfigurationHandler handler;
	protected LockSet lockSet;
	private long beginTime;
	private Serializable serObject;
	protected TransactionStatistics[] remoteStatistics;
	private boolean okFinished;
	private char lockType = LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE;
	private boolean isTrackable = true;

	/**
	 * The default non-arguments constructor. Transactional operations are not
	 * supported.
	 */
	public ApplicationTransaction(final DeployServiceContext ctx) {
		this(ctx, false);
	}

	/**
	 * @param isTxOperationSupported
	 *            flag to indicate the support for transactional operations.
	 */
	public ApplicationTransaction(final DeployServiceContext ctx,
		final boolean isTxOperationSupported) {
		this.ctx = ctx;
		txHelper = new TxOperationsHelper(isTxOperationSupported);
		currentStatistics = new TransactionStatistics(
			ctx.getClusterMonitorHelper().getCurrentServerId());
	}

	public String getModuleID() {
		if (appName == null) {
			return null;
		}
		return appName.getApplicationName();
	}

	public String getSoftwareType() {
		if (softwareType == null) {
			final DeploymentInfo dInfo = ctx.getTxCommunicator()
					.getApplicationInfo(getModuleID());
			if (dInfo != null) {
				softwareType = dInfo.getSoftwareType();
			}
		}
		return softwareType;
	}

	public void setModuleID(String id) {
		appName = new ApplicationName(id);
	}

	public byte getModuleType() {
		return moduleType;
	}

	public String getModuleTypeAsString() {
		return DeployConstants.RESOURCE_TYPE_APPLICATION;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setAppName(String provider, String name) {
		appName = new ApplicationName(provider, name);
	}

	public void setModuleType(byte moduleType) {
		this.moduleType = moduleType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public ContainerInterface[] getAffectedContainers() {
		return containers;
	}

	public Properties getContainerProperties(String containerName) {
		return (Properties) contProperties.get(containerName);
	}

	public ConfigurationHandler getHandler() {
		return handler;
	}

	protected void setHandler(final ConfigurationHandler handler) {
		this.handler = handler;
	}

	/**
	 * Commit the open handler if it exists. On the end of the operation, the
	 * handler will be set to null. It is the client responsibility to open,
	 * commit or roll back handlers.
	 * 
	 * @see com.sap.engine.services.deploy.server.utils.TxOperationsHelper#commit(ConfigurationHandler
	 *      handler)
	 * @throws ConfigurationException
	 */
	protected void commitHandler() throws ConfigurationException {
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Commit handler for transaction [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}

		final long start = System.currentTimeMillis();
		final long cpuStartTime = SystemTime.currentCPUTimeUs();
		try {
			Accounting.beginMeasure(ITimeStatConstants.CFG_MNG_COMMIT_HANDLER,
				APredefinedComponent.ConfigurationManager);
			txHelper.commit(handler);
		} finally {
			Accounting.endMeasure(ITimeStatConstants.CFG_MNG_COMMIT_HANDLER);
			handler = null;
			final long end = System.currentTimeMillis();
			final long cpuEndTime = SystemTime.currentCPUTimeUs();
			TransactionTimeStat.addCfgMngOperation(new DeployOperationTimeStat(
				ITimeStatConstants.CFG_MNG_COMMIT_HANDLER, start, end,
				cpuStartTime, cpuEndTime));
		}		
	}

	/**
	 * Rolls back or just closes the opened handler, if it exists. On the end of
	 * the operation, the handler will be set to null. It is the client
	 * responsibility to open, commit or rollback handlers.
	 * 
	 * @see com.sap.engine.services.deploy.server.utils.TxOperationsHelper#rollback(ConfigurationHandler
	 *      handler)
	 * @throws ConfigurationException
	 */
	public void rollbackHandler() throws ConfigurationException {
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback handler of transaction [{0}] to avoid writing in DB",
				getTransactionType());
		}
		final long start = System.currentTimeMillis();
		final long cpuStartTime = SystemTime.currentCPUTimeUs();

		try {
			Accounting.beginMeasure(
				ITimeStatConstants.CFG_MNG_ROLLBACK_HANDLER,
				APredefinedComponent.ConfigurationManager);
			txHelper.rollback(handler);
		} finally {
			Accounting.endMeasure(ITimeStatConstants.CFG_MNG_ROLLBACK_HANDLER);
			handler = null;
			final long end = System.currentTimeMillis();
			final long cpuEndTime = SystemTime.currentCPUTimeUs();
			TransactionTimeStat.addCfgMngOperation(
				new DeployOperationTimeStat(
					ITimeStatConstants.CFG_MNG_ROLLBACK_HANDLER, start, end,
					cpuStartTime, cpuEndTime));
		}
	}

	/**
	 * @param root
	 *            configuration root path. Cannot be null.
	 * @param isStillNotCreated
	 *            the configuration is still not created.
	 * @return the created configuration.
	 * @throws DeploymentException
	 */
	protected Configuration createAppIndexedConfiguration(final String root,
		final boolean isStillNotCreated) throws DeploymentException {
		return createAppIndexedConfiguration(
			root, isStillNotCreated, MAX_ATTEMPTS_TO_CREATE_APP_CONFIG);
	}

	/*
	 * @param root configuration root path. Cannot be null.
	 * 
	 * @param isStillNotCreated the configuration is still not created.
	 * 
	 * @param attempts how many times we can retry.
	 * 
	 * @return the created configuration.
	 * 
	 * @throws DeploymentException
	 */
	private Configuration createAppIndexedConfiguration(final String root,
		final boolean isStillNotCreated, final int attempts)
		throws DeploymentException {
		final Configuration appConfig;

		try {
			appConfig = handler.createSubConfiguration(root + "/" +
				appName.getProvider() + "/" + appName.getName(),
				Configuration.CONFIG_TYPE_INDEXED);
			return appConfig;
		} catch (NameAlreadyExistsException naex) {
			if(isStillNotCreated) {
				// We have expected that the configuration does not exists.
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_CREATE_APP_CONFIG,
					new String[] { getTransactionType(), getModuleID(),
						naex.getMessage() }, naex);
				sde.setMessageID("ASJ.dpl_ds.005013");
				throw sde;
			}
			try {
				return handler.openConfiguration(root + "/" +
					appName.getProvider() + "/" + appName.getName(),
					ConfigurationHandler.WRITE_ACCESS);
			} catch (ConfigurationException cex) {
				SimpleLogger.traceThrowable(Severity.ERROR, location, null,
					cex.getLocalizedMessage(), cex);
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_CREATE_APP_CONFIG,
						new String[] { getTransactionType(), getModuleID(),
							cex.getMessage() }, cex);
				sde.setMessageID("ASJ.dpl_ds.005013");
				throw sde;
			}
		} catch (NameNotFoundException nnfe) {
			try {
				ensureProviderConfigExists(root);
			} catch (ConfigurationException cex) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_CREATE_APP_CONFIG,
						new String[] { getTransactionType(), getModuleID(),
							cex.getMessage() }, cex);
				sde.setMessageID("ASJ.dpl_ds.005013");
				throw sde;
			}
			return createAppIndexedConfiguration(
				root, isStillNotCreated, attempts - 1);
		} catch (ConfigurationLockedException cex) {
			if (attempts > 0) {
				try {
					Thread.sleep(WAITING_TIME_DURING_APP_CONFIG_CREATION);
				} catch (InterruptedException iex) {
					SimpleLogger.traceThrowable(Severity.ERROR, location, null,
						iex.getLocalizedMessage(), iex);
				}
				return createAppIndexedConfiguration(
					root, isStillNotCreated, attempts - 1);
			}
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_CREATE_APP_CONFIG, new String[] {
					getTransactionType(), getModuleID(),
					cex.getMessage() }, cex);
			sde.setMessageID("ASJ.dpl_ds.005013");
			throw sde;
		} catch (ConfigurationException cex) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_CREATE_APP_CONFIG, new String[] {
					getTransactionType(), getModuleID(),
					cex.getMessage() }, cex);
			sde.setMessageID("ASJ.dpl_ds.005013");
			throw sde;
		}
	}

	/**
	 * This method is called only when the provider (vendor) configuration is
	 * still not created. We have to commit the handler with the provider
	 * configuration, because we act in the cluster boundaries and it is not
	 * possible to achieve synchronization between all servers with other means.
	 * <p>
	 * When this method returns, the provider configuration is already created.
	 * 
	 * @param root
	 *            root of the provider configuration.
	 * @throws ConfigurationException
	 *             if there are problems during commit of the temporary handler
	 *             with new provider configuration.
	 * @throws DeploymentException
	 *             if there are problems during opening of temporary handler.
	 */
	private void ensureProviderConfigExists(final String root)
		throws ConfigurationException, DeploymentException {
		final String configName = root + "/" + appName.getProvider();
		try {
			final ConfigurationHandler oldHandler = getHandler();
			openHandler();
			handler.createSubConfiguration(configName);
			commitHandler();
			setHandler(oldHandler);
		} catch (NameAlreadyExistsException ex) {
			if(location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"Provider configuration [{0}] already created " +
					"by another JVM or thread. \n{1}",
					configName, ex);
			}
		} catch (ConfigurationLockedException ex) {
			if(location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"Another JVM or thread is trying to create " +
					"the same provider configuration [{0}].",
					configName);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException iex) {
				SimpleLogger.traceThrowable(Severity.ERROR, location, null,
					iex.getLocalizedMessage(), iex);
			}
		}
	}

	/**
	 * Create or get global application configuration.
	 * 
	 * @return the global application configuration. Cannot be null.
	 * @throws DeploymentException
	 *             in case that the configuration cannot be created.
	 */
	protected Configuration createOrGetGlobalApplicationConfiguration()
		throws DeploymentException {
		return createOrGetGlobalApplicationConfiguration(MAX_ATTEMPTS_TO_CREATE_APP_CONFIG);
	}

	/**
	 * Create or get global application configuration.
	 * 
	 * @return the global application configuration. Cannot be null.
	 * @throws DeploymentException
	 *             in case that the configuration cannot be created.
	 */
	private Configuration createOrGetGlobalApplicationConfiguration(
		final int attempts) throws DeploymentException {
		try {
			return handler.createSubConfiguration(
				DeployConstants.GLOBAL_CONFIG +
				"/" + appName.getProvider() + "/" + appName.getName());
		} catch (NameAlreadyExistsException naex) {
			try {
				return handler.openConfiguration(
					DeployConstants.GLOBAL_CONFIG + "/" +
					appName.getProvider() + "/" + appName.getName(),
					ConfigurationHandler.WRITE_ACCESS);
			} catch (ConfigurationException cex) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_CREATE_APP_CONFIG,
						new String[] { getTransactionType(), getModuleID(),
							cex.getMessage() }, cex);
				sde.setMessageID("ASJ.dpl_ds.005013");
				throw sde;
			}
		} catch (NameNotFoundException nnfe) {
			try {
				ensureProviderConfigExists(DeployConstants.GLOBAL_CONFIG);
			} catch (ConfigurationException cex) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_CREATE_APP_CONFIG,
						new String[] { getTransactionType(), getModuleID(),
							cex.getMessage() }, cex);
				sde.setMessageID("ASJ.dpl_ds.005013");
				throw sde;
			}
			return createOrGetGlobalApplicationConfiguration(attempts - 1);
		} catch (ConfigurationLockedException cex) {
			if (attempts > 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException iex) {
					SimpleLogger.traceThrowable(Severity.ERROR, location, null,
						iex.getLocalizedMessage(), iex);
				}
				return createOrGetGlobalApplicationConfiguration(attempts - 1);
			}
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_CREATE_APP_CONFIG, new String[] {
					getTransactionType(), getModuleID(),
					cex.getMessage() }, cex);
			sde.setMessageID("ASJ.dpl_ds.005013");
			throw sde;
		} catch (ConfigurationException cex) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_CREATE_APP_CONFIG, new String[] {
					getTransactionType(), getModuleID(),
					cex.getMessage() }, cex);
			sde.setMessageID("ASJ.dpl_ds.005013");
			throw sde;
		}
	}

	protected Configuration openCustomApplicationConfiguration(int access)
		throws DeploymentException {
		if (getHandler() == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_OPEN_CONFIGURATION, 
				DeployConstants.CUSTOM_GLOBAL_CONFIG + "/" +
					appName.getProvider() + "/"	+ appName.getName(), 
					getModuleID(), getTransactionType(), 
					" handler is null.");
			sde.setMessageID("ASJ.dpl_ds.005011");
			throw sde;						
		}
		if (access != ConfigurationHandler.READ_ACCESS &&
			access != ConfigurationHandler.WRITE_ACCESS) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_OPEN_CONFIGURATION,
				DeployConstants.CUSTOM_GLOBAL_CONFIG + "/"
					+ appName.getProvider() + "/" + appName.getName(), 
					getModuleID(), getTransactionType(),
					" access is none read, nor write.");
			sde.setMessageID("ASJ.dpl_ds.005011");
			throw sde;						
		}
		try {
			return ((ConfigurationHandlerExtension) getHandler())
				.openPossibleDerivedConfigurationExtension(
					DeployConstants.CUSTOM_GLOBAL_CONFIG + "/" +
					appName.getProvider() + "/"	+ appName.getName(), access);
		} catch (NameNotFoundException nnfex) {
			if (access == ConfigurationHandler.READ_ACCESS) {
				return null;
			}
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_OPEN_CONFIGURATION, new String[] {
					DeployConstants.CUSTOM_GLOBAL_CONFIG + "/" +
						appName.getProvider() + "/"	+ appName.getName(), 
						getModuleID(), getTransactionType(), 
				nnfex.getMessage() }, nnfex);
			sde.setMessageID("ASJ.dpl_ds.005011");
			throw sde;
		} catch (ConfigurationException cex) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_OPEN_CONFIGURATION, new String[] {
					DeployConstants.CUSTOM_GLOBAL_CONFIG + "/" + 
					appName.getProvider() + "/" + appName.getName(), 
					getModuleID(), getTransactionType(), cex.getMessage() 
				}, cex);
			sde.setMessageID("ASJ.dpl_ds.005011");
			sde.setDcNameForObjectCaller(getHandler());
			throw sde;
		}
	}

	/**
	 * Try to open application configuration.
	 * @param rootCfg The root configuration, which can be <b>apps</b> or 
	 * <b>deploy</b>.
	 * @param access access type.
	 * @return the opened application configuration or null if such 
	 * configuration does not exists and we are trying to open it for reading. 
	 * @throws DeploymentException
	 */
	protected Configuration openApplicationConfiguration(String rootCfg,
		int access) throws DeploymentException {
		final String confName = rootCfg + "/" + appName.getProvider() + "/" +
			appName.getName();
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"openApplicationConfiguration({0})", confName);
		}
		if (getHandler() == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_OPEN_CONFIGURATION,
				confName, getModuleID(), getTransactionType(), 
				" handler is null.");
			sde.setMessageID("ASJ.dpl_ds.005011");
			throw sde;
		}
		if (rootCfg == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_OPEN_CONFIGURATION,
				confName, getModuleID(), getTransactionType(),
				" root configuration name is null.");
			sde.setMessageID("ASJ.dpl_ds.005011");
			throw sde;
		}
		if (access != ConfigurationHandler.READ_ACCESS &&
			access != ConfigurationHandler.WRITE_ACCESS) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_OPEN_CONFIGURATION,
				confName, getModuleID(), getTransactionType(),
				" access is none read, nor write.");
			sde.setMessageID("ASJ.dpl_ds.005011");
			throw sde;
		}
		try {
			return getHandler().openConfiguration(
				confName, access);
		} catch (NameNotFoundException nnfex) {
			if (access == ConfigurationHandler.READ_ACCESS) {
				if(location.beDebug()) {
					SimpleLogger.trace(Severity.DEBUG, location, null,
						"Application configuration {0} does not exists " +
						"and cannot be open by openApplicationConfiguration()",
						confName);
				}
				return null;
			}
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_OPEN_CONFIGURATION, 
				new String[] { confName, getModuleID(), getTransactionType(), 
					nnfex.getMessage() }, nnfex);
			sde.setMessageID("ASJ.dpl_ds.005011");
			sde.setDcNameForObjectCaller(getHandler());
			throw sde;
		} catch (ConfigurationException cex) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_OPEN_CONFIGURATION, 
				new String[] { confName, getModuleID(),	getTransactionType(), 
					cex.getMessage() }, cex);
			sde.setMessageID("ASJ.dpl_ds.005011");
			sde.setDcNameForObjectCaller(getHandler());
			throw sde;
		}
	}

	/**
	 * Open a configuration handler to persist or read configurations. It is the
	 * client responsibility to open, commit or rollback handlers.
	 * 
	 * @see com.sap.engine.services.deploy.server.utils.TxOperationsHelper#openHandler()
	 * @throws DeploymentException
	 */
	protected void openHandler() throws DeploymentException {
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Open handler for transaction [{0}] of application [{1}].",
				getTransactionType(), getModuleID());
		}
		final long start = System.currentTimeMillis();
		final long cpuStartTime = SystemTime.currentCPUTimeUs();
		try {
			Accounting.beginMeasure(ITimeStatConstants.CFG_MNG_OPEN_HANDLER,
				APredefinedComponent.ConfigurationManager);
			handler = txHelper.openHandler();
		} catch (TxException tex) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_GET_HANDLER, new String[] {
					getTransactionType(), getModuleID(),
					tex.getMessage() }, tex);
			sde.setMessageID("ASJ.dpl_ds.005027");
			throw sde;
		} catch (ConfigurationException cex) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_GET_HANDLER, new String[] {
					getTransactionType(), getModuleID(),
					cex.getMessage() }, cex);
			sde.setMessageID("ASJ.dpl_ds.005027");
			throw sde;			
		} finally {
			Accounting.endMeasure(ITimeStatConstants.CFG_MNG_OPEN_HANDLER);
			final long end = System.currentTimeMillis();
			final long cpuEndTime = SystemTime.currentCPUTimeUs();
			TransactionTimeStat.addCfgMngOperation(
				new DeployOperationTimeStat(
					ITimeStatConstants.CFG_MNG_OPEN_HANDLER, start, end,
					cpuStartTime, cpuEndTime));
		}
	}

	protected void addContainer(ContainerInterface cont, Properties props) {
		containers = DUtils.addAsLastElement(containers, cont);
		contProperties = DUtils.addToContainerProperties(
			contProperties, cont.getContainerInfo().getName(), props);
	}

	protected void makeAllPhasesOnOneServer() throws DeploymentException {
		clearExceptionInfoAndNotifyFailed2Start();
		try {
			begin();
		} catch (DeploymentException rex) {
			rollbackPart(false, true, rex);
		} catch (Exception ex) {
			rollbackPart(false, true, ex);
		} catch (Error er) {
			rollbackPart(false, true, er);
		}
		try {
			prepare();
			// commit only after successful prepare phase.
			txHelper.commitTxOperation();
		} catch (DeploymentException rex) {
			rollbackPart(false, false, rex);
		} catch (Exception ex) {
			rollbackPart(false, false, ex);
		} catch (Error er) {
			rollbackPart(false, false, er);
		}

		try {
			this.commit();
		} catch (Exception ex) {
			final ServerDeploymentException sdex = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION, new String[] {
					getTransactionType(), getModuleID() }, ex);
			if (!ctx.isMarkedForShutdown()) {
				SimpleLogger.traceThrowable(Severity.ERROR, location,
					sdex.getLocalizedMessage(), sdex);
			}
			throw sdex;
		}
	}

	
	protected void clearExceptionInfoAndNotifyFailed2Start() {
		// Default method is empty.
	}

	/**
	 * This method is called for the global transactions (i.e. transactions 
	 * triggered directly by the client, via remote DeployService interface).
	 * All transactions (except start and stop) are executed sequentially in
	 * the cluster: they are executed first on the global node, and after that
	 * the remote nodes are notified in case of success.
	 * 
	 * @see com.sap.engine.services.deploy.server.DTransaction#makeAllPhases()
	 */
	public void makeAllPhases() throws DeploymentException {
		// order changed - one server - begin, prepare, commit, on all the rest
		// notifyDeployed components after commit.
		// The commit phase ends with configuration commit.

		// we set this flag for error handling purposes
		boolean isEndOfTryBlockReached = false;

		try {
			TransactionTimeStat.setAppName(getModuleID());
			makeAllPhasesOnOneServer();

			long start = System.currentTimeMillis();
			long cpuStartTime = SystemTime.currentCPUTimeUs();
			notifyRemotely(true);
			long end = System.currentTimeMillis();
			long cpuEndTime = SystemTime.currentCPUTimeUs();
			TransactionTimeStat.addDeployOperation(
				new DeployOperationTimeStat(
					"Cluster Communication Duration", start, end, 
					cpuStartTime, cpuEndTime));
			isEndOfTryBlockReached = true;
		} finally {
			try {
				finalActions();
			} catch (WarningException wex) {
				addWarnings(wex.getWarnings());
			} catch (Exception ex) { // throw the exception
				if (isEndOfTryBlockReached) {
					final ServerDeploymentException sdex = new ServerDeploymentException(
						ExceptionConstants.UNEXPECTED_EXCEPTION,
						new String[] { getTransactionType(), getModuleID() },
						ex);
					sdex.setMessageID("ASJ.dpl_ds.005029");
					throw sdex;
				}
				// We just log the exception here so that it does not
				// hide exceptions thrown above.
				SimpleLogger.traceThrowable(Severity.ERROR, location,
					ex.getLocalizedMessage(), ex);
			}
		}
	}

	protected final void notifyRemotely(boolean wait) {
		final String tagName = "Cluster Communication Duration";
		final RemoteCaller remote = ctx.getRemoteCaller();
		try {
			final Map<String, Object> cmd = prepareNotification();

			Accounting.beginMeasure(tagName, remote.getClass());
			final long start = System.currentTimeMillis();
			final long cpuStartTime = SystemTime.currentCPUTimeUs();
			remoteStatistics = remote.notifyRemotely(
				cmd, getRemoteParticipants(), wait);
			if (!wait) {
				final long end = System.currentTimeMillis();
				final long cpuEndTime = SystemTime.currentCPUTimeUs();
				TransactionTimeStat.addDeployOperation(
					new DeployOperationTimeStat(
						ITimeStatConstants.CLUSTER_COMMUNICATION_DURATION,
						start, end, cpuStartTime, cpuEndTime));
			}
		} catch (DeploymentException dex) {
			SimpleLogger.traceThrowable(Severity.ERROR, location, null,
				dex.getLocalizedMessage(), dex);
			currentStatistics.addError(
				"Error occurred while operation " + getTransactionType() +
				" with application " + getModuleID() +
				" notifyed other cluster elements. " +
				"For more information look at log file of Deploy Service." );
		} finally {
			Accounting.endMeasure(tagName);
		}
	}

	@SuppressWarnings("unused")
    protected void finalActions() 
		throws WarningException, DeploymentException {
		if(location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
				"Final actions in [{0}] for application [{1}].",
				getTransactionType(), getModuleID());
		}
	}

	public void lock() throws LockSetNotAcquiredException,
		InterruptedException, ConflictingOperationLockException {
		lockSet = ctx.getLockManager().lock(
			new SingleNodeLockEvaluator(getTransactionType(), getComponent(), 
				isEnqueueLockNeeded() ? getLockType() : 0,
				PropManager.getInstance().getTimeout4LocalLock()));
	}

	public void unlock() {
		ctx.getLockManager().unlock(lockSet);
	}

	public Component getComponent() {
		// TODO: cache it
		return new Component(getModuleID(), Component.Type.APPLICATION);
	}

	public void makeAllPhasesLocal() throws DeploymentException {
		clearExceptionInfoAndNotifyFailed2Start();
		try {
			beginLocal();
		} catch (DeploymentException rex) {
			rollbackPart(true, true, rex);
		} catch (Exception ex) {
			rollbackPart(true, true, ex);
		} catch (Error er) {
			rollbackPart(true, true, er);
		}
		try {
			prepareLocal();
			// commit only after successful prepareLocal phase.
			txHelper.commitTxOperation();
		} catch (DeploymentException rex) {
			rollbackPart(true, false, rex);
		} catch (Exception ex) {
			rollbackPart(true, false, ex);
		} catch (Error er) {
			rollbackPart(true, false, er);
		}
		try {
			this.commitLocal();
		} catch (Exception ex) {
			final ServerDeploymentException sdex = 
				new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION, 
					new String[] {
						getTransactionType(), getModuleID() }, ex);
			SimpleLogger.traceThrowable(Severity.ERROR, location, 
				sdex.getLocalizedMessage(), sdex);
			throw sdex;
		}
	}

	protected void rollbackPart(boolean local, boolean begin, Throwable th)
		throws DeploymentException {
		final ServerDeploymentException sdex = new ServerDeploymentException(
			ExceptionConstants.UNEXPECTED_EXCEPTION, new String[] {
				getTransactionType(), getModuleID() }, th);
		if (!ctx.isMarkedForShutdown()) {
			SimpleLogger.traceThrowable(Severity.ERROR, location,
			sdex.getLocalizedMessage(), sdex);
			// the exception is logged in other places too (e.g. in StartTransaction.maekAllPhases()) 
			//	currentStatistics.addError(
			//		TransactionUtil.getDescriptiveMessage(th));
		}
		try {
			if (local) {
				if (begin) {
					this.rollbackLocal();
				} else {
					this.rollbackPrepareLocal();
				}
			} else {
				if (begin) {
					this.rollback();
				} else {
					this.rollbackPrepare();
				}
			}
		} catch (Exception ex) {
			final ServerDeploymentException innerSdex = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION, new String[] {
					getTransactionType(), getModuleID() }, ex);
			if (!ctx.isMarkedForShutdown()) {
				SimpleLogger.trace(Severity.ERROR, location,
					innerSdex.getLocalizedMessage(), ex);
			}
		} finally {
			try {
				txHelper.rollbackTxOperation();
			} catch (TxException ex) {
				SimpleLogger.trace(Severity.ERROR, location,
					ex.getLocalizedMessage(), ex);
			}
		}
		if (th instanceof DeploymentException) {
			throw (DeploymentException) th;
		} else if (th instanceof Exception) {
			throw sdex;
		} else if (th instanceof OutOfMemoryError) {
			throw (OutOfMemoryError) th;
		} else if (th instanceof ThreadDeath) {
			throw (ThreadDeath) th;
		}
		throw sdex;
	}

	public void addWarnings(final Collection<String> warnings) {
		if(warnings == null) {
			return;
		}
		for(String warning : warnings) {
			currentStatistics.addWarning(warning);
	         }
       }

	public void addWarnings(final String[] warnings) {
		if(warnings == null) {
			return;
	        }
		for(String warning : warnings) {
			currentStatistics.addWarning(warning);
	        }
	}

	public void addWarning(String w) {
		this.currentStatistics.addWarning(w);
	}

	/**
	 * @return The IDs of the remote server nodes, which have to participate to 
	 * the current transaction. Not null but can be empty.
	 */
	protected int[] getRemoteParticipants() {
		return ctx.getClusterMonitorHelper().findEligibleReceivers();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.DTransaction#setLockNeeded(boolean)
	 */
	public void setEnqueueLockNeeded(final boolean lockNeeded) {
		this.lockNeeded = lockNeeded;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.DTransaction#isLockNeeded()
	 */
	public boolean isEnqueueLockNeeded() {
		return lockNeeded;
	}

	public void setBeginTime(long begin) {
		this.beginTime = begin;
	}

	public long getBeginTime() {
		return this.beginTime;
	}

	public Serializable getSerializableForSend() {
		return serObject;
	}

	protected void setSerializable(Serializable serObject) {
		this.serObject = serObject;
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DTransaction#getStatistics()
	 */
	public TransactionStatistics[] getStatistics() {
		return mergeToRemoteStatistics(currentStatistics);
	}

	protected TransactionStatistics[] mergeToRemoteStatistics(
		final TransactionStatistics statistics) {
		// Due to some concurrent issues we need to work on local copy. 
		final TransactionStatistics[] copy = remoteStatistics;
		final TransactionStatistics[] result;
		if (copy == null) {
			result = new TransactionStatistics[] { statistics };
		} else {
			result = new TransactionStatistics[copy.length + 1];
			System.arraycopy(copy, 0, result,	0, copy.length);
			result[copy.length] = statistics;
		}
		// Assert that the origin was not modified in the meantime.
		assert copy == remoteStatistics;
		return result;
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DTransaction
	 * 		#getCurrentStatistics()
	 */
	public TransactionStatistics getCurrentStatistics() {
		return currentStatistics;
	}

	/**
	 * This method is called to execute a nested transaction. The nested 
	 * transactions are always global. Currently this method is used only for
	 * RemoveTransaction and OncePerInstanceTransaction.
	 * 
	 * @param tx the nested transaction to be executed.
	 * @throws DeploymentException
	 * @throws ComponentNotDeployedException
	 */
	protected void makeNestedTransaction(ApplicationTransaction tx)
		throws DeploymentException, ComponentNotDeployedException {
		// nested transactions does not needs cluster wide lock.
		tx.setEnqueueLockNeeded(false);
		final TransactionManager txManager = ctx.getTxManager();
		txManager.registerTransaction(tx);
		try {
			if (tx.isNeeded()) {
				tx.makeAllPhases();
			}
		} finally {
			txManager.unregisterTransaction(tx);
		}
	}

	/**
	 * This method is called only for parallel child transactions
	 * (StartTransaction and StopTransaction) in order to execute them in the
	 * same thread.
	 * 
	 * @throws DeploymentException
	 */
	protected void makeNestedParallelTransaction(ParallelAdapter pa)
		throws DeploymentException {
		// nested transactions does not needs cluster wide lock.
		pa.setEnqueueLockNeeded(false);
		final TransactionManager txManager = ctx.getTxManager();
		txManager.registerTransaction(pa);
		try {
			if (pa.isNeeded()) {
				if(pa.isLocal()) {
					pa.makeAllPhasesLocal();
				} else {
					pa.makeAllPhases();
				}
			}
		} finally {
			txManager.unregisterTransaction(pa);
			if (pa.getCurrentStatistics() != null) {
				addWarnings(pa.getCurrentStatistics().getWarnings());
				addWarnings(pa.getCurrentStatistics().getErrors());
				// child errors are warnings for its parent.
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DTransaction#isNeeded()
	 */
	public boolean isNeeded() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DTransaction#notNeeded()
	 */
	public void notNeeded() {
		getCurrentStatistics().addWarning(
			"There was NO need to execute operation " + 
			getTransactionType() + " with "	+ getModuleID() +
			" and will NOT send response for it, because it is NOT required.");
	}
	
	public boolean isSuccessfullyFinished() {
		return okFinished;
	}

	@SuppressWarnings("boxing")
	protected void setSuccessfullyFinished(boolean ok) {
		if(location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
				"Transaction [{0}] for application [{1}]" +
				"finished with state [{2}]\nThe current thread is: {3}",
				getTransactionType(), getModuleID(), ok,
				ctx.getLockManager().dumpCurrentThread());
		}
		this.okFinished = ok;
	}

	public char getLockType() {
		return lockType;
	}

	public void setLockType(char lockType) {
		this.lockType = lockType;
	}

	/**
	 * Refresh the deployment info cache, reading the deployment info for the 
	 * corresponding application from DB.
	 * @return the deployment info object, as read from DB. Cannot be null, 
	 * rather an exception will be thrown.
	 * @throws DeploymentException
	 */
	protected DeploymentInfo refreshDeploymentInfoFromDB()
		throws DeploymentException {
		if(location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
				"Will read the deployment info of [{0}] " +
				"in transaction [{1}] from DB.",
				getModuleID(), getTransactionType());
		}
		DeploymentInfo info = null;
		try {
			openHandler();
			final Configuration config = openApplicationConfiguration(
				DeployConstants.ROOT_CFG_APPS, 
				ConfigurationHandler.READ_ACCESS);
			if (config != null) {
				info = ctx.getTxCommunicator().refreshDeploymentInfoFromDB(
					getModuleID(), config, getHandler());
			}
		} finally {
			try {
				rollbackHandler();
			} catch (ConfigurationException ex) {
				final ServerDeploymentException sdex = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { 
						"reading deployment information from DB " +
						"for application: " + getModuleID() + 
						".\nReason: " + ex.toString() }, ex);
				SimpleLogger.traceThrowable(Severity.ERROR, location,
					sdex.getLocalizedMessage(), sdex);
			}
		}
		return info;
	}

	protected String getStackTraceAsString(Throwable th) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		th.printStackTrace(new PrintStream(baos));
		return baos.toString();
	}

	public DeployServiceContext getDeployServiceContext() {
		return ctx;
	}

	public boolean isTrackable() {
		return isTrackable;
	}

	public void setTrackable(boolean isTrackable) {
		this.isTrackable = isTrackable;
	}

	@SuppressWarnings("boxing")
	public void checkMigrationStatus() throws DeploymentException {
		if(location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
				"Transaction [{0}] with [{1}] " +
				"will read the migration status from DB.",
				getTransactionType(), getModuleID());
		}
		long niStartLong = System.currentTimeMillis();
		if (getHandler() == null) {
			openHandler();
		}
		try {
			CMigrationStatus cMigStatus = MigrationConfigUtils
				.readAppMigrationStatus(getModuleID(), getHandler());
			if (cMigStatus != null) {
				if(location.beDebug()) {
					SimpleLogger.trace(Severity.DEBUG, location, null,
						"Transaction [{0}] with [{1}] " +
						"detected migration status [{2}].",
						getTransactionType(),
						getModuleID(), MigrationConfigUtils
							.getHumanReadableMigrationStatus(cMigStatus));
				}
				if(cMigStatus.getStatus() != CMigrationStatus.PASSED) {
					ServerDeploymentException sde = 
						new ServerDeploymentException(
							ExceptionConstants.APP_NOT_MIGRATED,
							getModuleID(), getTransactionType());
					sde.setMessageID("ASJ.dpl_ds.005109");
					throw sde;
				}
			}
		} catch (ConfigurationException e) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION, new String[] {
					getTransactionType(), getModuleID() }, e);
			sde.setMessageID("ASJ.dpl_ds.005029");
			throw sde;
		} finally {
			if(location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"Transaction [{0}] with [{1}] " +
					"ended migration status check. Check took [{2}] ms.",
					getTransactionType(), getModuleID(), 
					System.currentTimeMillis() - niStartLong);
			}
			try {
				commitHandler();
			} catch (ConfigurationException ce) {
				setHandler(null);
				SimpleLogger.traceThrowable(Severity.ERROR, location,
					new ServerDeploymentException(
						ExceptionConstants.CANNOT_COMMIT_HANDLER, new String[] {
						getTransactionType(), getModuleID() }, ce)
					.getLocalizedMessage(), ce);
			}
		}
	}

	/**
	 * Called by StartInitiallyTransaction and OncePerInstanceTransaction to 
	 * download application files.
	 * @param appName
	 * @throws DeploymentException
	 */
	protected void bootstrapApplication(String appName)
		throws DeploymentException {
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"The application [{0}] will be bootstrapped " +
				"in [{1}] transaction.",
				appName, getTransactionType());
		}
		final ConfigurationHandler cfgHandler = getHandler();
		try {
			openHandler();
			final Configuration config = openApplicationConfiguration(
				DeployConstants.ROOT_CFG_APPS,
				ConfigurationHandler.READ_ACCESS);
			final DIReader diReader = EditorFactory.getInstance()
				.getDIReader(config);
			final TransactionCommunicator comm = ctx.getTxCommunicator();
			diReader.bootstrapApp(config, comm, comm
					.getApplicationInfo(appName), getTransactionType());
			if(location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"The application [{0}] is bootstrapped successfully " +
					"during [{1}] transaction.",
					appName, getTransactionType());
			}
		} finally {
			try {
				rollbackHandler();
			} catch (ConfigurationException ce) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_COMMIT_HANDLER, new String[] {
						getTransactionType(), appName }, ce);
				sde.setMessageID("ASJ.dpl_ds.005026");
				sde.setDcNameForObjectCaller(cfgHandler);
				throw sde;
			} finally {
				setHandler(cfgHandler);
			}
		}
	}

	/**
	 * This method is called once for every instance in the cluster, to 
	 * download the application files.
	 * @param isSuccessfullyFinished
	 * @param downloadInGlobalPrepare
	 * @throws DeploymentException
	 */
	protected void oncePerInstanceTransaction(boolean isSuccessfullyFinished,
			boolean downloadInGlobalPrepare) throws DeploymentException {
		final TransactionCommunicator comm = ctx.getTxCommunicator();
		final DeploymentInfo dInfo = comm.getApplicationInfo(getModuleID());
		if (dInfo == null) {
			return;// in case of failed deployment.
		}
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"The [{0}] application binaries are going to be downloaded, " +
				"because JLinEE validation needs them " +
				"even the applicaion is stopped.",
				dInfo.getApplicationName());
		}
		try {
			makeNestedTransaction(new OncePerInstanceTransaction(
				dInfo.getApplicationName(), ctx, downloadInGlobalPrepare,
				isSuccessfullyFinished));
		} catch(ComponentNotDeployedException ex) {
			SimpleLogger.traceThrowable(Severity.ERROR, location, 
				ex.getLocalizedMessage(), ex);
		}
	}

	protected ContainerInterface[] getContainers4Application(
		DeploymentInfo dInfo, boolean ifMissingThrowException)
		throws DeploymentException {
		final String[] contNames = dInfo.getContainerNames();
		final List<ContainerInterface> tempContsList = new ArrayList<ContainerInterface>();
		ContainerInterface cont = null;
		for (int i = 0; i < contNames.length; i++) {
			cont = ctx.getTxCommunicator().getContainer(contNames[i]);
			if (cont == null) {
				//if (!dInfo.isOptionalContainer(contNames[i]))  - check deleted because of no real use cases	
					if (ifMissingThrowException) {
						ServerDeploymentException sde = 
							new ServerDeploymentException(
								ExceptionConstants.NOT_AVAILABLE_CONTAINER,
								contNames[i], getTransactionType(), 
								getModuleID());
						sde.setMessageID("ASJ.dpl_ds.005006");
						throw sde;						
					}
					// Do not throw exception here. Container might be
					// missing, because its service had been timed out.
			} else {
				tempContsList.add(cont);
			}
		}
		final ContainerInterface[] tempConts = new ContainerInterface[tempContsList
				.size()];
		tempContsList.toArray(tempConts);
		return tempConts;
	}

	/**
	 * This method is called to prepare the notification of remote server nodes.  
	 */
	protected Map<String, Object> prepareNotification() {
		final Map<String, Object> cmd =	new Hashtable<String, Object>();
		cmd.put(RemoteCaller.COMMAND, getTransactionType());
		cmd.put(RemoteCaller.APP_NAME, getModuleID());
		
		final ContainerInterface[] containers = getAffectedContainers();
		String[] containerNames = null;
		if (containers != null) {
			containerNames = new String[containers.length];
			for (int i = 0; i < containers.length; i++) {
				containerNames[i] = containers[i].getContainerInfo().getName();
				final Properties cp = 
					getContainerProperties(containerNames[i]);
				if (cp != null) {
					cmd.put("properties:" + containerNames[i], cp);
				}
			}
			cmd.put(CONTAINERS, containerNames);
		}
		// object
		if (getSerializableForSend() != null) {
			cmd.put("object", getSerializableForSend());
		}
		return cmd;
	}

	public void setShmComponentStatusStopped() {
		DeploymentInfo dInfo = Applications.get(getModuleID());
		if (! dInfo.isJ2EEApplication()) {
			return;
		}
		ShmComponentUtils.setExpectedStatus(Status.STOPPED, getModuleID());
		ShmComponentUtils.setLocalStatus(Status.STOPPED, getModuleID());
	}

	public void setShmComponentStatusExpected() {
		DeploymentInfo dInfo = Applications.get(getModuleID());
		if (! dInfo.isJ2EEApplication()) {
			return;
		}
		if (DeployConstants.stopApp.equals(getTransactionType())
				|| StartUp.DISABLED.equals(dInfo.getStartUpO())) {
			ShmComponentUtils.setExpectedStatus(Status.STOPPED, getModuleID());
		} else if (DeployConstants.startApp.equals(getTransactionType()) ||
			DeployConstants.startInitiallyApp.equals(getTransactionType())) {
			ShmComponentUtils.setExpectedStatus(Status.STARTED, getModuleID());
		} else {
			if(location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"setShmComponentStatusExpected - " +
					"The transaction type for application [{0}] is [{1}].",
					getModuleID(), getTransactionType());
			}
		}
	}

	public void setShmComponentStartupMode() {
		StartUp startup;
		DeploymentInfo dInfo = Applications.get(getModuleID());
		if (dInfo != null) {
			if (!dInfo.isJ2EEApplication()) {
				return;
			}
			startup = dInfo.getStartUpO();
		} else {
			startup = StartUp.MANUAL;
		}
		if (StartUp.ALWAYS.equals(startup)) {
			ShmComponentUtils.setStartupModeAlways(getModuleID());
		} else if (StartUp.LAZY.equals(startup)) {
			ShmComponentUtils.setStartupModeLazy(getModuleID());
		} else if (StartUp.DISABLED.equals(startup)) {
			ShmComponentUtils.setStartupModeDisabled(getModuleID());
		} else {
			ShmComponentUtils.setStartupModeManual(getModuleID());
		}
	}

	public void shmCloseOnDeployFailed() {
		ShmComponentUtils.close(getModuleID());
	}
	
	protected void removeContainerFromCacheIfAny() {
		Containers allContainers = Containers.getInstance();
		ArrayList<String> containers = allContainers
				.getContainersForComponent(getModuleID());
		if (containers != null && containers.size() > 0) {
			for (String container : containers) {
				if(location.beDebug()) {
					SimpleLogger.trace(Severity.DEBUG, location, null,
						"Removing from cache container [{0}] " +
						"provided by application [{1}].",
						container, getModuleID());
				}
				allContainers.remove(container);
			}
		}
	}
}