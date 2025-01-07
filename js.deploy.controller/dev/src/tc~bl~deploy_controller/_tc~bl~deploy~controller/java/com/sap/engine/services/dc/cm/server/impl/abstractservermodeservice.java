package com.sap.engine.services.dc.cm.server.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.lib.config.api.ClusterConfiguration;
import com.sap.engine.lib.config.api.CommonClusterFactory;
import com.sap.engine.lib.config.api.ConfigurationLevel;
import com.sap.engine.lib.config.api.exceptions.ClusterConfigurationException;
import com.sap.engine.lib.config.api.exceptions.NameNotFoundException;
import com.sap.engine.services.dc.cm.server.spi.ServerMode;
import com.sap.engine.services.dc.cm.server.spi.ServerModeService;
import com.sap.engine.services.dc.repo.LocationConstants;
import com.sap.engine.services.dc.util.SystemProfileManager;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.DCLogConstants;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-3-30
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
abstract class AbstractServerModeService implements ServerModeService {
	
	private Location location = DCLog.getLocation(this.getClass());

	AbstractServerModeService() {
	}

	protected abstract String getExecWorkDir();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.server.spi.ServerModeService#setServerMode
	 * (com.sap.engine.services.dc.cm.server.spi.ServerMode)
	 */
	public void setServerMode(ServerMode serverMode)
			throws ServerModeServiceException {
		String modeToSet;
		String actionToSet;
		String currentClusterInstanceId = getCurrentClusterInstanceId();
		if (location.beDebug()) {
			DCLog.traceDebug(location, 
				"Set server in [{0}], performerId [{1}]", new Object[] {
						serverMode, currentClusterInstanceId });
		}
		if (ServerMode.NORMAL.equals(serverMode)) {
			modeToSet = ConfigurationLevel.RUNTIME_MODE_NORMAL;
			actionToSet = ConfigurationLevel.RUNTIME_ACTION_NONE;
		} else if (ServerMode.SAFE.equals(serverMode)) {
			modeToSet = ConfigurationLevel.RUNTIME_MODE_SAFE;
			actionToSet = ConfigurationLevel.RUNTIME_ACTION_DEPLOY;
		} else {
			throw new ServerModeServiceException(
					"ASJ.dpl_dc.003156 The specified server mode '"
							+ serverMode + "' is not "
							+ "supported and could not be set.");
		}
		try {
			ConfigurationHandlerFactory cfgHandlerFactory = getConfigurationHandlerFactory();
			setRawServerMode(cfgHandlerFactory, modeToSet, actionToSet);
		} catch (Exception e) {
			throw new ServerModeServiceException(
					"ASJ.dpl_dc.003157 An error occurred while setting the "
							+ "server in '" + serverMode + "' mode", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.server.spi.ServerModeService#getServerMode
	 * ()
	 */
	public ServerMode getServerMode() {
		try {
			String mode[] = getRawServerMode();
			String startupMode = mode[0];
			String startupAction = mode[1];
			ServerMode serverMode;
			if (ConfigurationLevel.RUNTIME_MODE_NORMAL
					.equalsIgnoreCase(startupMode)) {
				serverMode = ServerMode.NORMAL;
			} else if (ConfigurationLevel.RUNTIME_MODE_SAFE
					.equalsIgnoreCase(startupMode)) {
				serverMode = ServerMode.SAFE;
			} else {
				serverMode = ServerMode.UNKNOWN;
			}
			if (location.beDebug()) {
				traceDebug(location, 
						"Server mode: [{0}], action: [{1}]. Ret:[{2}]",
						new Object[] { startupMode, startupAction, serverMode });
			}
			return serverMode;
		} catch (NameNotFoundException e) {
			return ServerMode.UNKNOWN;
		} catch (ClusterConfigurationException e) {
			return ServerMode.UNKNOWN;
		}
		/*
		 * final CoreMonitor coreMonitor =
		 * ServiceConfigurer.getInstance().getCoreMonitor(); final byte
		 * runtimeMode = coreMonitor.getRuntimeMode(); final byte runtimeAction
		 * = coreMonitor.getRuntimeAction();
		 * 
		 * if (CoreMonitor.RUNTIME_MODE_NORMAL == runtimeMode &&
		 * CoreMonitor.RUNTIME_ACTION_NONE == runtimeAction) { return
		 * ServerMode.NORMAL; } else if (CoreMonitor.RUNTIME_MODE_SAFE ==
		 * runtimeMode && CoreMonitor.RUNTIME_ACTION_DEPLOY == runtimeAction) {
		 * return ServerMode.SAFE; } else { return ServerMode.UNKNOWN; }
		 */
	}

	public void storeServerMode() throws NameNotFoundException,
			ConfigurationLockedException, ConfigurationException,
			ClusterConfigurationException {

		String[] serverMode = getRawServerMode();
		String runMode = serverMode[0];
		String runAction = serverMode[1];

		if (location.bePath()) {
			DCLog.tracePath(location, 
				"Store the server mode [{0} mode , action {1}]", new Object[] {
						runMode, runAction });
		}

		ConfigurationHandler handler = null;
		try {

			handler = getConfigurationHandlerFactory()
					.getConfigurationHandler();
			Configuration configuration = handler.openConfiguration(
					LocationConstants.ROOT_LOCK,
					ConfigurationHandler.WRITE_ACCESS);
			configuration.modifyConfigEntry(LocationConstants.RUN_MODE,
					runMode, true);
			configuration.modifyConfigEntry(LocationConstants.RUN_ACTION,
					runAction, true);
			handler.commit();

		} catch (ConfigurationException e) {

			handler.rollback();
			throw e;

		} finally {

			if (handler != null) {
				try {
					handler.closeAllConfigurations();
				} catch (ConfigurationException e) {
					if (isDebugLoggable()) {
						logDebugThrowable(location, 
								"ASJ.dpl_dc.004916",
								"Caught an exception while trying to close the configurations",
								e);
					}
				}
			}

		}

	}

	public void restoreServerMode() throws ConfigurationException,
			NameNotFoundException, ClusterConfigurationException {

		ConfigurationHandlerFactory configurationHandlerFactory = getConfigurationHandlerFactory();
		ConfigurationHandler handler = configurationHandlerFactory
				.getConfigurationHandler();

		try {
			Configuration configuration = handler.openConfiguration(
					LocationConstants.ROOT_LOCK,
					ConfigurationHandler.WRITE_ACCESS);

			String runMode, runAction;
			boolean removeRunMode = false;
			boolean removeRunAction = false;
			if (configuration.existsConfigEntry(LocationConstants.RUN_MODE)) {
				runMode = (String) configuration
						.getConfigEntry(LocationConstants.RUN_MODE);
				removeRunMode = true;
			} else {
				runMode = ConfigurationLevel.RUNTIME_MODE_NORMAL;
			}
			if (configuration.existsConfigEntry(LocationConstants.RUN_ACTION)) {
				runAction = (String) configuration
						.getConfigEntry(LocationConstants.RUN_ACTION);
				removeRunAction = true;
			} else {
				runAction = ConfigurationLevel.RUNTIME_ACTION_NONE;
			}

			if (location.bePath()) {
				DCLog.tracePath(location, 
					"Restore the server mode [{0} mode , action {1}]",
					new Object[] { runMode, runAction });
			}

			setRawServerMode(configurationHandlerFactory, runMode, runAction);
			if (removeRunMode) {
				configuration.deleteConfigEntry(LocationConstants.RUN_MODE);
			}
			if (removeRunAction) {
				configuration.deleteConfigEntry(LocationConstants.RUN_ACTION);
			}

			handler.commit();
			if (location.beInfo()) {
				traceInfo(location, 
						"Server mode restored to runMode=[{0}] runAction=[{1}]",
						new Object[] { runMode, runAction });
			}

		} catch (ConfigurationException e) {
			handler.rollback();
			throw e;
		} catch (ClusterConfigurationException e) {
			handler.rollback();
			throw e;
		} finally {
			try {
				handler.closeAllConfigurations();
			} catch (ConfigurationException e) {
				DCLog
						.logErrorThrowable(location, 
								"ASJ.dpl_dc.004919",
								"There was a problem closing the configuration [{0}] while restoring the server mode.",
								new Object[] { LocationConstants.ROOT_LOCK }, e);
			}
		}

	}

	/**
	 * 
	 * @return new String[]{ startupMode, startupAction };
	 * @throws NameNotFoundException
	 * @throws ClusterConfigurationException
	 */
	private String[] getRawServerMode() throws NameNotFoundException,
			ClusterConfigurationException {
		String currentClusterInstanceId = getCurrentClusterInstanceId();
		ConfigurationHandlerFactory cfgHandlerFactory = getConfigurationHandlerFactory();
		CommonClusterFactory factory = ClusterConfiguration
				.getClusterFactory(cfgHandlerFactory); // configFactory e
		// ConfigurationHandlerFactory
		// impl, offline ili
		// online configuration
		// manager-a
		ConfigurationLevel level; // this is the ID of the instance that will be
		// set as performer ID, the method expects
		// it in this syntax IDYYYYYYY.
		level = factory.openConfigurationLevel(
				CommonClusterFactory.LEVEL_INSTANCE, currentClusterInstanceId);
		String startupMode = level.getStartupMode();
		String startupAction = level.getStartupAction();
		if (location.beInfo()) {
			traceInfo(location, 
					"Server mode: [{0}], action: [{1}].", new Object[] {
							startupMode, startupAction });
		}
		return new String[] { startupMode, startupAction };
	}

	private void setRawServerMode(
			ConfigurationHandlerFactory cfgHandlerFactory, String runMode,
			String runAction) throws NameNotFoundException,
			ClusterConfigurationException {
		String currentClusterInstanceId = getCurrentClusterInstanceId();
		// try{
		CommonClusterFactory factory = ClusterConfiguration
				.getClusterFactory(cfgHandlerFactory); // configFactory e
		// ConfigurationHandlerFactory
		// impl, offline ili
		// online configuration
		// manager-a
		ConfigurationLevel level = factory.openConfigurationLevel(
				CommonClusterFactory.LEVEL_INSTANCE, currentClusterInstanceId); // this
		// is
		// the
		// ID
		// of
		// the
		// instance
		// that
		// will
		// be
		// set
		// as
		// performer
		// ID
		// ,
		// the
		// method
		// expects
		// it
		// in
		// this
		// syntax
		// IDYYYYYYY
		// .
		level.setStartupMode(runMode, runAction);
		// } catch (Exception e) {
		// throw new
		// ServerModeServiceException("An error occurred while setting the " +
		// "server in '" + runMode +" ( " +runAction + " ) "+ "' mode", e);
		// }
	}

	private String getCurrentClusterInstanceId() {
		String currentClusterInstanceId = SystemProfileManager
				.getSysParamValue(SystemProfileManager.CLUSTER_INSTANCE_ID);
		if (!currentClusterInstanceId.toUpperCase().startsWith("ID")) {
			currentClusterInstanceId = "ID" + currentClusterInstanceId;
		}
		return currentClusterInstanceId;
	}

	protected abstract ConfigurationHandlerFactory getConfigurationHandlerFactory();

}
