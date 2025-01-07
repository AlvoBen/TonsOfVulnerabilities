/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.lib.config.api.ClusterConfiguration;
import com.sap.engine.lib.config.api.CommonClusterFactory;
import com.sap.engine.lib.config.api.ConfigurationLevel;
import com.sap.engine.lib.config.api.exceptions.ClusterConfigurationException;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeployExceptionConstContainer;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;
import com.sap.engine.services.deploy.container.migration.CMigrationInterface;
import com.sap.engine.services.deploy.container.migration.exceptions.CMigrationException;
import com.sap.engine.services.deploy.container.migration.utils.CMigrationInfo;
import com.sap.engine.services.deploy.container.migration.utils.CMigrationResult;
import com.sap.engine.services.deploy.container.migration.utils.CMigrationState;
import com.sap.engine.services.deploy.container.migration.utils.CMigrationStatistic;
import com.sap.engine.services.deploy.container.migration.utils.CMigrationStatus;
import com.sap.engine.services.deploy.container.migration.utils.CMigratorResult;
import com.sap.engine.services.deploy.ear.common.CloneUtils;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.application.AppConfigurationHandlerImpl;
import com.sap.engine.services.deploy.server.cache.containers.ContainerComparatorReverted;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.editor.DIReader;
import com.sap.engine.services.deploy.server.editor.DIWriter;
import com.sap.engine.services.deploy.server.editor.EditorFactory;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.properties.ServerState;
import com.sap.engine.services.deploy.server.utils.ConfigUtils;
import com.sap.engine.services.deploy.server.utils.cfg.MigrationConfigUtils;
import com.sap.tc.logging.Location;

/**
 * Migrates online all applications.
 * 
 * @author Anton Georgiev
 * @version 7.00
 */
class CMigrationInvoker {
	private static final Location location = 
		Location.getLocation(CMigrationInvoker.class);
	private final static String ACTION = "OnlineMigration: ";
	
	private static CMigrationInvoker INSTANCE;

	private final Hashtable<String, CMigrationInterface> cmName_cmImpl;
	
	private CMigrationState cMigState;
	private Hashtable<String, CMigrationStatus> appName_cMigStatus;
	private CMigrationStatistic cMigStatistic;
	private Date startDate;
	private Date endDate;

	private CMigrationInvoker(ServerState sState) {
		if (sState.isValid4ContainerMigration()) {
			cMigState = CMigrationState.WILL_BE_STARTED;
		} else {
			cMigState = CMigrationState.WONT_BE_STARTED;
		}
		cmName_cmImpl = new Hashtable<String, CMigrationInterface>();
	}

	public synchronized static CMigrationInvoker getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CMigrationInvoker(
				PropManager.getInstance().getServerState());
		}
		return INSTANCE;
	}

	/**
	 * Starts the migration of all deployed applications.
	 */
	public void start(DeployServiceContext ctx) throws DeploymentException {
		cMigState = CMigrationState.RUNNING;
		startDate = new Date();

		appName_cMigStatus = new Hashtable<String, CMigrationStatus>();
		try {
			if (cmName_cmImpl.size() == 0) {
				if (location.bePath()) {
					DSLog.tracePath(
						location, 
						"[{0}] migration is canceled, because on no registered migrators.",
						ACTION);
				}
				return;
			}
			String allApps[] = ctx.getTxCommunicator().listApplications();
			if (allApps == null || allApps.length == 0) {
				if (location.bePath()) {
					DSLog.tracePath(
						location, 
						"[{0}] migration is canceled, because on no deployed applications.",
						ACTION);
				}
				return;
			}

			final ConfigurationHandler handler = ConfigUtils
				.getConfigurationHandler(PropManager.getInstance()
					.getConfigurationHandlerFactory(), ACTION);

			try {
				// begin the migration from rootApps
				for (int i = 0; i < allApps.length; i++) {
					try {
						performAppMigration(allApps[i], handler, ctx);
					} catch (OutOfMemoryError oome) {
						throw oome;
					} catch (ThreadDeath td) {
						throw td;
					} catch (Throwable th) {
						DSLog.logErrorThrowable(
							location, 
							"ASJ.dpl_ds.006351",
							"Exception while performing application migration",
							new Exception(ACTION + allApps[i], th));
					}
				}
			} finally {
				clearLoaders(allApps, ctx);
			}

		} finally {
			final TransactionCommunicator communicator = ctx
					.getTxCommunicator();
			notifyMigratorsForResult(communicator);
			endDate = new Date();

			final CMigrationStatistic cMigStat = getCMigrationStatistic(communicator);
			final ConfigurationHandler cfgHandler4CMigStat = ConfigUtils
				.getConfigurationHandler(PropManager.getInstance()
					.getConfigurationHandlerFactory(), ACTION);

			// persists the container migration statistic
			MigrationConfigUtils.storeMigrationStatistic(cMigStat,
				cfgHandler4CMigStat, ACTION);

			// change the server mode and action is case of successful migration
			changeServerModeAndAction(cMigStat);
		}
	}

	private void changeServerModeAndAction(CMigrationStatistic cMigStat)
			throws DeploymentException {
		if (!CMigrationState.FINISHED_SUCCESSFULLY.equals(
				cMigStat.getCMigrationState()) &&
			!CMigrationState.FINISHED_WITH_WARNINGS.equals(
				cMigStat.getCMigrationState())) {
			DSLog.logWarning(
				location, 
				"ASJ.dpl_ds.000422",
				"OnlineMigration: the container migration finished with state [{0}] and the server mode and action will stay [{1}].",
				cMigStat.getCMigrationState(), 
				PropManager.getInstance().getServerState());
			return;
		}
		if (location.beDebug()) {
			DSLog.traceDebug(
				location, 
				"OnlineMigration: the container migration finished with state [{0}] and the server mode and action will be changed from [{1}] into [{2}].",
				cMigStat.getCMigrationState(),
				PropManager.getInstance().getServerState(), 
				ServerState.SAFE_DEPLOY);
		}
		CommonClusterFactory cfgFactory = ClusterConfiguration
			.getClusterFactory(PropManager.getInstance()
					.getConfigurationHandlerFactory());
		ConfigurationLevel level = null;
		try {
			level = cfgFactory.openConfigurationLevel(
				CommonClusterFactory.LEVEL_INSTANCE, "ID"
				+ PropManager.getInstance().getClusterMonitor()
					.getCurrentParticipant().getGroupId());
			level.setStartupMode(ConfigurationLevel.RUNTIME_MODE_NORMAL,
				ConfigurationLevel.RUNTIME_ACTION_NONE);
			level.setStartupMode(ConfigurationLevel.RUNTIME_MODE_SAFE,
				ConfigurationLevel.RUNTIME_ACTION_DEPLOY);
		} catch (com.sap.engine.lib.config.api.exceptions.NameNotFoundException e) {
			changeServerModeAndAction(e, ACTION);
		} catch (ClusterConfigurationException e) {
			changeServerModeAndAction(e, ACTION);
		}
	}

	private void changeServerModeAndAction(Throwable th, String prefix)
		throws DeploymentException {
		final ServerDeploymentException sde = new ServerDeploymentException(
			ExceptionConstants.CHANGE_SERVER_MODE_AND_ACTION, new String[] {
				PropManager.getInstance().getServerState() + "",
				ServerState.SAFE_DEPLOY + prefix }, th);
		sde.setMessageID("ASJ.dpl_ds.005138");
		DSLog.logErrorThrowable(location, sde);
		throw sde;
	}

	private void notifyMigratorsForResult(TransactionCommunicator communicator) {
		String concatMigrators = "";
		Enumeration cmNamesEnum = cmName_cmImpl.keys();
		if (cmNamesEnum != null) {
			String cmName = null;
			CMigrationInterface cmImpl = null;
			CMigrationStatus cmStatus[] = null;
			while (cmNamesEnum.hasMoreElements()) {
				cmName = (String) cmNamesEnum.nextElement();
				concatMigrators = concatMigrators + cmName + " ; ";
				cmImpl = cmName_cmImpl.get(cmName);
				try {
					cmStatus = getCMigrationStatus(cmName, communicator);
					cmImpl.notifyForMigrationResult(cmStatus);
				} catch (OutOfMemoryError oome) {
					throw oome;
				} catch (ThreadDeath td) {
					throw td;
				} catch (Exception ex) {
					DSLog.logErrorThrowable(location, "ASJ.dpl_ds.000424", "{0}", ex,
						ACTION);
				} catch (Error er) {
					DSLog.logErrorThrowable(location, "ASJ.dpl_ds.000425", "{0}", er,
						ACTION);
				}
			}
		}
		if (!concatMigrators.trim().equals("")) {
			if (location.bePath()) {
				DSLog.tracePath(location, "[{0}] the migration status was delivered to: [{1}]",
					ACTION,
					concatMigrators);
			}
		} else {
			if (location.bePath()) {
				DSLog.tracePath(
					location, 
					"[0] there were no migrators, to which migration status to be delivered.",
					ACTION);
			}
		}
	}

	private CMigrationStatus[] getCMigrationStatus(String migrator,
		TransactionCommunicator communicator) {
		CMigrationStatus cmStatus[] = new CMigrationStatus[0];
		String currApps[] = communicator.listApplications(migrator);
		if (currApps != null && currApps.length != 0) {
			cmStatus = new CMigrationStatus[currApps.length];
			String appName = null;
			for (int i = 0; i < currApps.length; i++) {
				appName = currApps[i];
				cmStatus[i] = appName_cMigStatus.get(appName);
				if (cmStatus[i] == null) {
					DSLog.traceWarning(
						location, 
						"ASJ.dpl_ds.000428",
						"OnlineMigration: there is no information for the migration status of application [{0}].",
						appName);
				}
			}
		}
		return cmStatus;
	}

	// Unregisters the application loaders for the given application names.
	private void clearLoaders(final String apps[],
		final DeployServiceContext ctx) {
		if (apps == null) {
			return;
		}
		for (int i = 0; i < apps.length; i++) {
			try {
				ctx.getTxCommunicator().removeApplicationLoader(apps[i]);
			} catch (Exception ex) {
				DSLog.logErrorThrowable(location, "ASJ.dpl_ds.000429", "{0}", ex, ACTION);
			}
		}
	}

	/**
	 * Recursive migration of applications. We will creep over the reference
	 * graph. 
	 * @param appName
	 * @param handler
	 * @param ctx
	 */
	private void performAppMigration(final String appName,
		final ConfigurationHandler handler, final DeployServiceContext ctx) {
		final TransactionCommunicator comm = ctx.getTxCommunicator();
		if (comm.getApplicationInfo(appName) == null) {
			return;
		} else if (appName_cMigStatus.get(appName) != null) {
			return;
		}

		// Will migrate all references applications.
		for(final String referencedApp : ctx.getReferenceResolver()
			.getRelatedApplications(appName)) {
			performAppMigration(referencedApp, handler, ctx);
		}

		if (location.beDebug()) {
			DSLog.traceDebug(location, "{0}Start migrating application [{1}].",
				ACTION, appName);
		}
		// All applications on which the current application depends 
		// are already migrated.
		put(appName, CMigrationStatus.UNKNOWN,
			"The initial migration status of each application is failed.");
		Configuration appConfig = null;
		Configuration deployConfig = null;
		try {
			String appCfgPath = DeployConstants.ROOT_CFG_APPS + "/" + appName;
			try {
				appConfig = handler.openConfiguration(appCfgPath,
					ConfigurationHandler.WRITE_ACCESS);
			} catch (ConfigurationException ce) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_OPEN_CONFIGURATION_ON_PRINCIPLE,
					new String[] { appCfgPath,
						ConfigurationHandler.WRITE_ACCESS + "", }, ce);
				sde.setMessageID("ASJ.dpl_ds.005062");
				throw sde;
			}
			deployConfig = ConfigUtils.openConfiguration(handler,
				DeployConstants.ROOT_CFG_DEPLOY + "/" + appName,
				ConfigurationHandler.WRITE_ACCESS, ACTION + "of" + appName);
		} catch (DeploymentException dex) {
			rollbackHandler(appName, handler);
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.000431", "{0}", dex, ACTION);
			return;
		}

		byte status = CMigrationStatus.UNKNOWN;// NOTE: if the result is
		// changed,
		String description = null; // please change the description.
		long ms = -1;
		try {
			try {// migrate single application
				ms = System.currentTimeMillis();
				migrateSingleApplication(appConfig, handler, deployConfig,
						appName, comm);
				ms = System.currentTimeMillis() - ms;
				{
					status = CMigrationStatus.PASSED;
					description = "The application " + appName
							+ " was migrated successfully for " + ms + " ms.";
				}
			} catch (OutOfMemoryError oome) {
				throw oome;
			} catch (ThreadDeath td) {
				throw td;
			} catch (Throwable t) {
				if (t instanceof DeploymentException) {
					throw (DeploymentException) t;
				}
				throw new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "migrating application " + appName },
					t);
			}
			ConfigUtils.commitHandler(handler, ACTION + appName);
		} catch (DeploymentException dex) {
			if (CMigrationStatus.FAILED != status) {
				status = CMigrationStatus.FAILED;
				description = printStackTrace(dex);
			}
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.000432", "{0}", dex, ACTION);
		} finally {
			put(appName, status, description);
			if (CMigrationStatus.PASSED == status) {
				if (location.bePath()) {
					DSLog.tracePath(
						location, 
						"{0}The application [{1}] was migrated successfully for [{2}] ms.",
						ACTION,
						appName, ms);
				}
				ms = -1;
			} else {
				DSLog.traceError(
					location, 
					"ASJ.dpl_ds.000434",
					"OnlineMigration: The migration of application [{0}] FAILED.",
					appName);
			}
			rollbackHandler(appName, handler);
		}
	}

	private void rollbackHandler(String appName, ConfigurationHandler handler) {
		try {
			ConfigUtils.rollbackHandler(handler, ACTION + appName);
			if (location.bePath()) {
				DSLog.tracePath(
					location, 
					"{0}The configuration handler for application [{1}] rollbacked successfully.",
					ACTION,
					appName);
			}
		} catch (DeploymentException rollbackDex) {
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.000436", "{0}", rollbackDex,
				ACTION);
		}
	}

	private String printStackTrace(Throwable th) {
		String description = null;
		if (th != null) {
			final StringWriter strWr = new StringWriter();
			th.printStackTrace(new PrintWriter(strWr));
			description = "\n" + strWr.toString();
		}
		return description;
	}

	private void put(String appName, byte state, String description) {
		final CMigrationStatus cMigStatus = new CMigrationStatus(
			appName, state, description);
		appName_cMigStatus.put(cMigStatus.getAppName(), cMigStatus);
	}

	/**
	 * Migrates one single application and register its loader in the
	 * LoadContext.
	 * 
	 * @param appConfig the application configuration with parent
	 * DeployService.ROOT_CFG_APPS root configuration.
	 * @param deployConfig the deploy configuration with parent
	 * DeployService.ROOT_CFG_DEPLOY root configuration.
	 * @param appName the application name.
	 * @param communicator
	 * 
	 * @return application loader name, if null - no such deployed application
	 * @throws DeploymentException indicates migration failure.
	 */
	private String migrateSingleApplication(Configuration appConfig,
		ConfigurationHandler cfgHandler, Configuration deployConfig,
		String appName, TransactionCommunicator communicator)
		throws DeploymentException {
		DeploymentInfo dInfo = communicator.getApplicationInfo(appName);
		if (dInfo == null) {
			if (location.beWarning()) {
				DSLog.traceWarning(
					location, 
					"ASJ.dpl_ds.003010",
					"OnlineMigration: application with name [{0}] is not deployed.",
					appName);
			}
			return null;
		}
		try {
			dInfo = (DeploymentInfo) dInfo.clone();
		} catch (CloneNotSupportedException cnsEx) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION, new String[] {
					ACTION, appName }, cnsEx);
			sde.setMessageID("ASJ.dpl_ds.005029");
			throw sde;
		}

		// check are all container on which this application is deployed are active
		ContainerInterface[] tempConts = getContainers4Application(dInfo,
				communicator);
		// download the application before making its loader.
		final DIReader diReader = EditorFactory.getInstance().getDIReader(
				appConfig);
		diReader.bootstrapApp(appConfig, communicator, dInfo, ACTION);
		// the loader must be bind even if the application won't be migrated,
		// because the other application might refer it.
		ClassLoader appMigLoader = bindAppMigLoader(dInfo, communicator);
		// create the container migration info
		final CMigrationInfo cmInfo = createCMigrationInfo(
			dInfo, appConfig, cfgHandler, appMigLoader);

		String cmName = null;
		CMigrationInterface cmImpl = null;
		CMigrationResult cmResult = null;
		boolean isDInfoChanged = false;
		// sort decreasing, like in the deploy operation
		Arrays.sort(tempConts, ContainerComparatorReverted.instance);
		// invokes migration only on the containers, which the current
		// application is deployed
		for (int i = 0; i < tempConts.length; i++) {
			cmName = tempConts[i].getContainerInfo().getName();
			if (cmName_cmImpl.get(cmName) == null) {
				if (location.beDebug()) {
					DSLog.traceDebug(
						location, 
						"{0} there is no [{1}] migrator with number [{2}] for migration of application [{3}].",
						ACTION,
						cmName, i, appName);
				}
				continue;
			}
			if (location.beDebug()) {
				DSLog.traceDebug(
					location, 
					"{0} invoking [{1}] migrator with number [{2}] for migration of application [{3}].",
					ACTION,
					cmName, i, appName);
			}
			cmImpl = cmName_cmImpl.get(cmName);
			try {
				cmResult = cmImpl.migrateContainerLogic(cmInfo);
			} catch (CMigrationException cme) {
				ServerDeploymentException sde =
				 new ServerDeploymentException(
					ExceptionConstants.MIGRATOR_FAILURE, new String[] {
						appName, cmName }, cme);
				sde.setMessageID("ASJ.dpl_ds.005104");
				throw sde;
			}
			if (processCMigrationResult(dInfo, cmResult, communicator, cmName,
					cmInfo)) {
				isDInfoChanged = true;
			}
		}// invocation

		if (isDInfoChanged) {
			// Save the deployment info in DB only when it is changed.
			final DIWriter diWriter = EditorFactory.getInstance()
				.getDIWriter(dInfo.getVersion());
			diWriter.modifyDeploymentInfo(appConfig, deployConfig, dInfo);
			communicator.addApplicationInfo(appName, dInfo);
		}
		return dInfo.getApplicationName();
	}

	private boolean processCMigrationResult(DeploymentInfo dInfo,
			CMigrationResult cmResult, TransactionCommunicator communicator,
			String cmName, CMigrationInfo cmInfo) throws DeploymentException {
		boolean isDInfoChanged = false;
		if (cmResult != null) {
			final String addFilesForCL[] = cmResult.getFilesForClassLoader();
			if (addFilesForCL != null && addFilesForCL.length > 0) {
				isDInfoChanged = true;
				if (location.beDebug()) {
					DSLog.traceDebug(
						location, 
						"{0}The additional files for class loader of [{1}] application : {2}",
						ACTION,
						dInfo.getApplicationName(), addFilesForCL);
				}
				dInfo.addContName_FilesForCL(cmName, addFilesForCL);
			}
			final ReferenceObjectIntf refIntf[] = cmResult.getReferences();
			if (refIntf != null && refIntf.length > 0) {
				isDInfoChanged = true;
				final ReferenceObject addRefs[] = new ReferenceObject[refIntf.length];
				for (int y = 0; y < refIntf.length; y++) {
					addRefs[y] = new ReferenceObject(refIntf[y]);
				}
				if (location.beDebug()) {
					DSLog.traceDebug(
						location, 
						"{0}The additional references for class loader of [{1}] application : [{2}]",
						ACTION,
						dInfo.getApplicationName(), addRefs);
				}
				dInfo.addReference(addRefs);
			}
			if (isDInfoChanged) {
				unbindAppMigLoader(dInfo, communicator);
				final ClassLoader appMigLoader = bindAppMigLoader(dInfo,
						communicator);
				cmInfo.setAppLoader(appMigLoader);
			}
		}
		return isDInfoChanged;
	}

	private ContainerInterface[] getContainers4Application(
		DeploymentInfo dInfo, TransactionCommunicator communicator)
		throws DeploymentException {
		final String[] contNames = dInfo.getContainerNames();
		final List<ContainerInterface> tempContsList = new ArrayList<ContainerInterface>();
		ContainerInterface cont = null;
		for (int i = 0; i < contNames.length; i++) {
			cont = communicator.getContainer(contNames[i]);
			if (cont == null) {
				if (!dInfo.isOptionalContainer(contNames[i])) {
					final ServerDeploymentException sde = 
						new ServerDeploymentException(
							ExceptionConstants.NOT_AVAILABLE_CONTAINER,
							contNames[i], ACTION, dInfo.getApplicationName());
					sde.setMessageID("ASJ.dpl_ds.005006");
					throw sde;
				}
				if (location.beWarning()) {
					DSLog.traceWarning(
						location, 
						"ASJ.dpl_ds.003011",
						"OnlineMigration: Optional container [{0}] is not active at the moment.",
						contNames[i]);
				}
			} else {
				tempContsList.add(cont);
			}
		}
		final ContainerInterface[] tempConts = 
			new ContainerInterface[tempContsList.size()];
		tempContsList.toArray(tempConts);
		return tempConts;
	}

	// clone the deployment info into the container migration info
	private CMigrationInfo createCMigrationInfo(DeploymentInfo dInfo,
		Configuration appConfig, ConfigurationHandler cfgHandler,
		ClassLoader appMigLoader) {
		return new CMigrationInfo(appConfig,
			new AppConfigurationHandlerImpl(cfgHandler), appMigLoader,
			dInfo.getApplicationName(), dInfo.isStandAloneArchive(),
			CloneUtils.cloneStringArray(dInfo.getRemoteSupport()),
			CloneUtils.clonePropertiesString(dInfo.getProperties()));
	}

	private ClassLoader bindAppMigLoader(DeploymentInfo dInfo,
		TransactionCommunicator communicator) throws DeploymentException {
		return communicator.bindLoader(dInfo);
	}

	private void unbindAppMigLoader(DeploymentInfo dInfo,
		TransactionCommunicator communicator) throws DeploymentException {
		communicator.removeApplicationLoader(dInfo.getApplicationName());
	}

	/**
	 * @param migratorName
	 * @param migrator
	 * @throws CMigrationException
	 */
	public void registerMigrator(String migratorName,
		CMigrationInterface migrator) throws CMigrationException {
		synchronized (cmName_cmImpl) {
			checkIsRunning("registered", migratorName);
			if (cmName_cmImpl.get(migratorName) != null) {
				throw new CMigrationException(
					DeployExceptionConstContainer.CAN_NOT_REGISTER_MIGRATOR,
					new String[] { migratorName });
			}
			if (location.beDebug()) {
				DSLog.traceDebug(
					location, 
					"{0}A migrator with name [{1}] was registered successfully.",
					ACTION,
					migratorName);
			}
			cmName_cmImpl.put(migratorName, migrator);
		}
	}

	public boolean existsMigrator(String migratorName) {
		synchronized (cmName_cmImpl) {
			return (cmName_cmImpl.get(migratorName) != null ? true : false);
		}
	}

	public void unregisterMigrator(String migratorName)
		throws CMigrationException {
		synchronized (cmName_cmImpl) {
			checkIsRunning("UNregistered", migratorName);
			if (cmName_cmImpl.get(migratorName) == null) {
				throw new CMigrationException(
					DeployExceptionConstContainer.CAN_NOT_UNREGISTER_MIGRATOR,
					new String[] { migratorName });
			}
			if (location.beDebug()) {
				DSLog.traceDebug(
					location, 
					"{0}A migrator with name [{1}] was UNregistered successfully.",
					ACTION,
					migratorName);
			}
			cmName_cmImpl.remove(migratorName);
		}
	}

	private void checkIsRunning(String operation, String migratorName)
			throws CMigrationException {
		if (CMigrationState.RUNNING.equals(cMigState)) {
			throw new CMigrationException(
				DeployExceptionConstContainer.MIGRATION_IS_RUNNING,
				new String[] { operation, migratorName });
		}
	}

	public CMigrationStatistic getCMigrationStatistic(
		TransactionCommunicator communicator) {
		if (cMigStatistic == null ||
			CMigrationState.WILL_BE_STARTED.equals(
				cMigStatistic.getCMigrationState()) ||
			CMigrationState.RUNNING.equals(
				cMigStatistic.getCMigrationState())) {
			// continue
		} else {
			return cMigStatistic;
		}
		final ServerState sState = PropManager.getInstance().getServerState();
		if (sState.isValid4ContainerMigration()) {
			final CMigratorResult cMigResults[] = colectCMigratorResults(communicator);
			return new CMigrationStatistic(
				evaluateCMigrationState(cMigResults), cMigResults,
				evaluateDuration());
		}
		return new CMigrationStatistic(
			CMigrationState.WONT_BE_STARTED, null, evaluateDuration());
	}

	private Long evaluateDuration() {
		if (startDate != null) {
			if (endDate != null) {
				return (new Long(endDate.getTime() - startDate.getTime()));
			}
			return (new Long((new Date()).getTime() - startDate.getTime()));
		}
		return (new Long(-1));
	}

	private CMigrationState evaluateCMigrationState(
			CMigratorResult cMigResults[]) {
		if (endDate != null) {
			if (cMigResults != null) {
				CMigrationStatus cmStatuses[] = null;
				for (int r = 0; r < cMigResults.length; r++) {
					cmStatuses = cMigResults[r].getCMigrationStatuses();
					if (cmStatuses != null) {
						for (int s = 0; s < cmStatuses.length; s++) {
							if (cmStatuses[s] != null
									&& CMigrationStatus.PASSED != cmStatuses[s]
											.getStatus()) {
								cMigState = CMigrationState.FINISHED_WITH_ERRORS;
								return cMigState;
							}
						}
					}
				}
			}
			cMigState = CMigrationState.FINISHED_SUCCESSFULLY;
			return cMigState;
		}
		return cMigState;
	}

	private CMigratorResult[] colectCMigratorResults(
		TransactionCommunicator communicator) {
		final Set<CMigratorResult> cMigResults = new HashSet<CMigratorResult>();
		Enumeration cmNamesEnum = cmName_cmImpl.keys();
		if (cmNamesEnum != null) {
			String cmName = null;
			CMigrationStatus cmStatus[] = null;
			while (cmNamesEnum.hasMoreElements()) {
				cmName = (String) cmNamesEnum.nextElement();
				cmStatus = getCMigrationStatus(cmName, communicator);
				cMigResults.add(new CMigratorResult(cmName, cmStatus));
			}
		}

		final CMigratorResult[] result = new CMigratorResult[cMigResults.size()];
		cMigResults.toArray(result);
		return result;
	}

	// ////////////////////////////////////////////////
	// The following methods are only for tests. //
	// Please comment them, before submit. //

	// private void regEJBMigrator() {
	// try {
	// registerMigrator("EJBContainer", new CMigrationInterface(){
	//      
	// public CMigrationResult migrateContainerLogic(CMigrationInfo arg0) throws
	// CMigrationException {
	// CMigrationResult cmResult = new CMigrationResult();
	// if ("sap.com/Hello".equals(arg0.getAppName())) {
	// cmResult.setFilesForClassLoader(new
	// String[]{PropManager.getInstance().getAppsWorkDir() + "hello.jar"});
	//            
	// final ReferenceObject refObj = new ReferenceObject();
	//refObj.setReferenceTargetType(ReferenceObject.REF_TARGET_TYPE_APPLICATION)
	// ;
	// refObj.setReferenceProviderName("sap.com");
	// refObj.setReferenceTarget("Calculator");
	// refObj.setReferenceType(ReferenceObject.REF_TYPE_WEAK);
	//            
	// cmResult.setReferences(new ReferenceObject[]{refObj});
	// }
	// return cmResult;
	// }
	//      
	// public void notifyForMigrationResult(CMigrationStatus[] arg0) {
	// DSLog.traceDebug(location, Convertor.toString(arg0, ""));
	// }});
	// } catch (CMigrationException cmEx) {
	// DSLog.logThrowable(location, cmEx, PREFIX);
	// }
	// }
	//  
	// private void regWebMigrator() {
	// try {
	// registerMigrator("servlet_jsp", new CMigrationInterface(){
	//    
	// public CMigrationResult migrateContainerLogic(CMigrationInfo arg0) throws
	// CMigrationException {
	// CMigrationResult cmResult = new CMigrationResult();
	// if ("sap.com/Calculator".equals(arg0.getAppName())) {
	// cmResult.setFilesForClassLoader(new
	// String[]{PropManager.getInstance().getAppsWorkDir() + "inc.jar"});
	//            
	// final ReferenceObject refObj = new ReferenceObject();
	//refObj.setReferenceTargetType(ReferenceObject.REF_TARGET_TYPE_APPLICATION)
	// ;
	// refObj.setReferenceProviderName("sap.com");
	// refObj.setReferenceTarget("Increment");
	// refObj.setReferenceType(ReferenceObject.REF_TYPE_WEAK);
	//            
	// cmResult.setReferences(new ReferenceObject[]{refObj});
	// } else if ("sap.com/Increment".equals(arg0.getAppName())) {
	// throw new NullPointerException("This is a test exception.");
	// }
	// return cmResult;
	// }
	//    
	// public void notifyForMigrationResult(CMigrationStatus[] arg0) {
	// DSLog.traceDebug(location, Convertor.toString(arg0, ""));
	// }});
	// } catch (CMigrationException cmEx) {
	// DSLog.logThrowable(location, cmEx, PREFIX);
	// }
	// }

	// The upper methods are only for tests. //
	// Please comment them, before submit. //
	// ////////////////////////////////////////////////

}