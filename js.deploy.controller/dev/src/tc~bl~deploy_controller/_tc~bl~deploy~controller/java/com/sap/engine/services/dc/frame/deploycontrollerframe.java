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
package com.sap.engine.services.dc.frame;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.cluster.monitor.ClusterMonitor;
import com.sap.engine.frame.container.registry.ObjectRegistry;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.services.dc.cm.CM;
import com.sap.engine.services.dc.cm.CMFactory;
import com.sap.engine.services.dc.cm.deploy.DeployParallelismStrategy;
import com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.spi.ServerMode;
import com.sap.engine.services.dc.cm.server.spi.ServerModeService.ServerModeServiceException;
import com.sap.engine.services.dc.cm.undeploy.UndeployParallelismStrategy;
import com.sap.engine.services.dc.cm.undeploy.UndeployWorkflowStrategy;
import com.sap.engine.services.dc.frame.HookInitializer.HookInitializationException;
import com.sap.engine.services.dc.frame.RepositoryContainerInitializer.InitializationException;
import com.sap.engine.services.dc.manage.DCManager;
import com.sap.engine.services.dc.manage.DCState;
import com.sap.engine.services.dc.manage.PathsConfigurer;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.manage.handle.HandleManager;
import com.sap.engine.services.dc.manage.handle.HandleManager.HandleManagerException;
import com.sap.engine.services.dc.repo.RepositoryContainer;
import com.sap.engine.services.dc.util.ClusterUtils;
import com.sap.engine.services.dc.util.FileUtils;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.prepare.CfgPreparator;
import com.sap.tc.logging.Location;

/**
 * Frame class for Deploy Controller
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class DeployControllerFrame implements ApplicationServiceFrame {
	
	private Location location = DCLog.getLocation(this.getClass());

	// ****************** PROPERTIES ******************
	public static final String ARCHIVES_DIR_NAME_KEY = "archives_dir_name";
	public static final String STORAGE_DIR_NAME_KEY = "storage_dir_name";
	public static final String CRC_CHECK_MODE_KEY = "crc_check_mode";
	public static final String DEPL_OFFLINE_STRATEGY = "depl_offline_strategy";
	public static final String UNDEPL_OFFLINE_STRATEGY = "undepl_offline_strategy";
	public static final String DEPL_PARALLELISM_STRATEGY = "depl_parallelism_strategy";
	public static final String UNDEPL_PARALLELISM_STRATEGY = "undepl_parallelism_strategy";
	public static final String GEN_SESSION_ID_4_SECS = "gen_session_id_4_secs";
	public static final String GEN_SESSION_IDS_AT_ONES = "gen_session_ids_at_ones";
	public static final String OFFLINE_RESULT_TIMEOUT = "offline_result_timeout";
	public static final String WEB_DISP_SERVER_INFO = "wed_disp_server_info";
	public static final String INST_ID_2_INST_PFL = "inst_id_2_inst_pfl";
	public static final String MIN_FREE_BYTES_TO_DEPLOY = "min_free_bytes_to_deploy";
	public static final String DEPLOY_THREADS_NUMBER = "deploy_threads";
	public static final String OS_USER = "os_user";
	public static final String OS_PASS = "os_pass";
	public static final String LOCKING_RETRIES = "locking_retries";
	public static final String LOCKING_INTERVAL = "locking_interval";
	public static final String SUPPRESS_DS_WARNINGS = "suppress_ds_warnings";
	// ****************** PROPERTIES ******************

	// ****************** VALUES ******************
	public static final String NORMAL = "NORMAL";
	public static final String SAFETY = "SAFETY";
	public static final String ROLLING = "ROLLING";
	public static final Set V_NORMAL_SAFETY = new HashSet();
	static {
		V_NORMAL_SAFETY.add(NORMAL);
		V_NORMAL_SAFETY.add(SAFETY);
	}
	public static final Set V_NORMAL_SAFETY_ROLLING = new HashSet();
	static {
		V_NORMAL_SAFETY_ROLLING.add(V_NORMAL_SAFETY);
		V_NORMAL_SAFETY.add(ROLLING);
	}

	public static final String ON = "ON";
	public static final String OFF = "OFF";
	public static final Set V_ON_OFF = new HashSet();
	static {
		V_ON_OFF.add(ON);
		V_ON_OFF.add(OFF);
	}
	// ****************** VALUES ******************

	private static final String CFG_PREPARE_WAIT_ARGUMENT_ = "deploy_controller_cfg_prepare";
	private static final int CFG_PREPARE_WAIT_MS = 1000 * 60;
	private ObjectRegistry objReg;

	public DeployControllerFrame() {
	}

	private void performInitialization(ApplicationServiceContext appServiceCtx,
			DCManager dcManager, HandleManager handleManager,
			DCLog.TimeWatcher timeWatcher) throws ServiceException {

		if (location.bePath()) {
						tracePath(location, 
					"Starting to initialize the service ...");
		}
		initServiceProperties(appServiceCtx);
		initServiceCfg();
		if (location.beDebug()) {
			traceDebug(location,
					"Service properties and configuration initialized :[{0}]",
					new Object[] { timeWatcher.getElapsedTimeAsString() });
		}

		initSoftwareTypeService();

		if (location.bePath()) {
						tracePath(location,
					"Starting to initialize the repository ...");
		}
		
		initRepositoryContainer();
		if (location.beDebug()) {
			traceDebug(location, "Repository initialized :[{0}]",
					new Object[] { timeWatcher.getElapsedTimeAsString() });
		}

		registerHooks();

		if (location.bePath()) {
			tracePath(location, "Registering Deploy Controller");
		}
		registerCM(appServiceCtx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.frame.ApplicationServiceFrame#start(com.sap.engine.frame
	 * .ApplicationServiceContext)
	 */
	public void start(final ApplicationServiceContext appServiceCtx)
			throws ServiceException {

		String prefix = appServiceCtx.getClusterContext().getClusterMonitor()
				.getCurrentParticipant().getName();
		DCLog.initLogging(prefix);

		final DCLog.TimeWatcher timeWatcher = DCLog.TimeWatcher.getInstance();
		if (location.beInfo()) {
			tracePath(location,
					"=========== Starting Deploy Controller ( TimerId: [{0}] ) ===========",
					new Object[] { String.valueOf(timeWatcher.getId()) });
		}

		try {
			ServiceConfigurer.getInstance().setApplicationServiceContext(
					appServiceCtx);
		} catch (TechnicalLockException tlEx) {
			throw new ServiceException(DCLog.getLocation(), tlEx);
		} catch (IllegalArgumentException iaEx) {
			throw new ServiceException(DCLog.getLocation(), iaEx);
		}

		setServerMode(); // TODO Revise and see if necessary

		final DCManager dcManager = DCManager.getInstance();
		dcManager.setDCState(DCState.INITIALIZING);

		if (location.bePath()) {
			tracePath(location, "Registering the handlers ...");
		}
		final HandleManager handleManager = registerHandlers(appServiceCtx);
		if (location.bePath()) {
			tracePath(location, "Handlers registered : [{0}]",
					new Object[] { timeWatcher.getElapsedTimeAsString() });
		}

		try {
			performInitialization(appServiceCtx, dcManager, handleManager,
					timeWatcher);
		} catch (ServiceException e) {
			DCLog.logErrorThrowable(location, e);
			throw e;
		}

		// the DC state should be set to WORKING only when all the services are
		// started
		dcManager.setDCState(DCState.INITIALIZED);

		if (location.bePath()) {
			tracePath(location, "Performing the start ...");
		}
		doStart(appServiceCtx);
		if (location.bePath()) {
			tracePath(location, "Start performed : [{0}]",
					new Object[] { timeWatcher.getElapsedTimeAsString() });
		}
		if (location.bePath()) {
			tracePath(
					location,
					"=========== Deploy Controller is started. Total elapsed: [{0}] =========== ",
					new Object[] { String.valueOf(timeWatcher
							.getTotalElapsedTimeAsString()) });
		}
	}

	private void setServerMode() throws ServiceException {
		final ConfigurationHandlerFactory factory = ServiceConfigurer
				.getInstance().getConfigurationHandlerFactory();
		try {
			ServerMode mode = ClusterUtils.getInstance().getServerMode(factory);
			ServiceConfigurer.getInstance().setServerMode(mode);
		} catch (ServerModeServiceException e) {
			throw new ServiceException(DCLog.getLocation(), e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.frame.ServiceFrame#stop()
	 */
	public void stop() throws ServiceRuntimeException {
		if (location.beInfo()) {
			tracePath(location, "=========== Stopping Deploy Controller ===========");
		}

		doStop(ServiceConfigurer.getInstance().getApplicationServiceContext());

		unregisterCM();

		RepositoryContainer.clear();

		HandleManager.getInstance().unregisterHandlers();
		ServiceConfigurer.getInstance().clear();

		if (location.beInfo()) {
			tracePath(location, "Deploy Controller has stopped");
		}
	}

	private void doStart(ApplicationServiceContext appServiceCtx) {

		final ServiceStateProcessor stateProcessor = ServiceStateProcessor
				.getInstance();

		try {
			stateProcessor.start();
		} catch (ServiceStateProcessingException e) {
			new ServiceException(DCLog.getLocation(), e);
		}
	}

	private void doStop(ApplicationServiceContext appServiceCtx) {

		final ServiceStateProcessor stateProcessor = ServiceStateProcessor
				.getInstance();
		try {
			stateProcessor.stop();
		} catch (ServiceStateProcessingException e) {
			new ServiceRuntimeException(DCLog.getLocation(), e);
		}
	}

	private void initServiceCfg() throws ServiceException {
		try {
			final ClusterMonitor cm = ServiceConfigurer.getInstance()
					.getClusterMonitor();
			final ConfigurationHandler cfgHandler = ServiceConfigurer
					.getInstance().getConfigurationHandler();
			CfgPreparator.getInstance().prepare(CFG_PREPARE_WAIT_ARGUMENT_,
					CFG_PREPARE_WAIT_MS, cm, cfgHandler);
		} catch (LockException le) {
			throw new ServiceException(DCLog.getLocation(), le);
		} catch (TechnicalLockException tle) {
			throw new ServiceException(DCLog.getLocation(), tle);
		} catch (IllegalArgumentException iae) {
			throw new ServiceException(DCLog.getLocation(), iae);
		} catch (ConfigurationException ce) {
			throw new ServiceException(DCLog.getLocation(), ce);
		}
	}

	private void initServiceProperties(
			final ApplicationServiceContext appServiceCtx)
			throws ServiceException {
		final Properties serviceProperties = appServiceCtx.getServiceState()
				.getProperties();
		final String workingDirPath = appServiceCtx.getServiceState()
				.getWorkingDirectoryName();

		// ReadersExtractDirPath
		// final String serviceExtractDirPath =
		// initDirProperty(
		// READERS_EXTRACT_DIR_NAME_KEY,
		// PathsConfigurer.getInstance().getReadersExtractDirPath(),
		// props,
		// workingDirPath);
		// PathsConfigurer.getInstance().setReadersExtractDirPath(
		// serviceExtractDirPath);

		// UploadDirName
		final String archivesDirPath = initDirProperty(ARCHIVES_DIR_NAME_KEY,
				PathsConfigurer.getInstance().getUploadDirName(""),
				serviceProperties, workingDirPath);
		PathsConfigurer.getInstance().setUploadDirName(archivesDirPath);

		// StorageDirName
		final String storageDirPath = initDirProperty(STORAGE_DIR_NAME_KEY,
				PathsConfigurer.getInstance().getStorageDirName(""),
				serviceProperties, workingDirPath);
		PathsConfigurer.getInstance().setStorageDirName(storageDirPath);

		// sets crc check mode to "on" or "off"
		setCrcCheckMode(serviceProperties);

		// sets the offline workflow strategy: normal(one restart) or safety(
		// two restarts )
		// properties to set:depl_offline_strategy, undepl_offline_strategy
		setOfflineStrategies(serviceProperties);

		// sets the parallelism strategy: normal(multi threaded) or
		// safety(single threaded)
		// properties to set: DEPL_PARALLELISM_STRATEGY,
		// UNDEPL_PARALLELISM_STRATEGY
		setParallelismStrategies(serviceProperties);

		// sets the parameters used when generating the session ids.
		setGenSessionId4Secs(serviceProperties);
		setGenSessionIdsAtOnes(serviceProperties);

		// how long to wait getResult thread during online deployment after the
		// offline phase
		setOfflineResultTimeout(serviceProperties);

		setWDServerInfo(serviceProperties);

		// min free bytes to deploy
		setMinFreeBytesToDeploy(serviceProperties);

		// number of threads that perform deployment
		setDeployThreadsNumber(serviceProperties);

		try {
			setInstId2InstPfl(serviceProperties);
		} catch (IOException e) {
			throw new ServiceException(DCLog.getLocation(), e);
		}
		setOsUser(serviceProperties);
		setOsPass(serviceProperties);

		setLockingInterval(serviceProperties);
		setLockingRetries(serviceProperties);

		setDSWarningsSuppressed(serviceProperties);
	}

	private void setLockingRetries(Properties props) {

		ServiceConfigurer.getInstance().setLockingRetries(
				Integer.parseInt(props.getProperty(LOCKING_RETRIES)));

	}

	private void setLockingInterval(Properties props) {
		ServiceConfigurer.getInstance().setLockingInterval(
				Integer.parseInt(props.getProperty(LOCKING_INTERVAL)));

	}

	private String initDirProperty(String propKey, String defaultValue,
			Properties props, String workingDirPath) {
		String dirName = props.getProperty(propKey);
		if (dirName == null) {
			dirName = defaultValue;
		}
		final String dirPath = FileUtils.concatDirs(workingDirPath, dirName);
		if (location.beDebug()) {
			traceDebug(location, "[{0}] = [{1}]", new Object[] {
					propKey, dirPath });
		}

		return FileUtils.mkdirs(dirPath);
	}

	private HandleManager registerHandlers(
			ApplicationServiceContext appServiceCtx) throws ServiceException {
		try {
			HandleManager handleManager = HandleManager.getInstance();
			handleManager.registerHandlers(appServiceCtx);
			return handleManager;
		} catch (HandleManagerException hme) {
			throw new ServiceException(DCLog.getLocation(), hme);
		}
	}

	private void registerHooks() throws ServiceException {
		try {
			HookInitializer.getInstance().init();
		} catch (final HookInitializationException hie) {
			throw new ServiceException(DCLog.getLocation(), hie);
		}
	}

	private void initRepositoryContainer() throws ServiceException {

		try {
			RepositoryContainerInitializer.getInstance().init();
		} catch (InitializationException ie) {
			throw new ServiceException(DCLog.getLocation(), ie);
		}

	}

	private void initSoftwareTypeService() {
		ServerFactory.getInstance().createServer().initServerService(
				ServerFactory.getInstance().createSoftwareTypeRequest());
	}

	private void setCrcCheckMode(Properties properties) {
		final String crcCheckMode = properties.getProperty(CRC_CHECK_MODE_KEY);

		if (crcCheckMode != null) {
			if (ON.equalsIgnoreCase(crcCheckMode)) {
				ServiceConfigurer.getInstance().setCrcCheckMode(true);
			} else if (OFF.equalsIgnoreCase(crcCheckMode)) {
				ServiceConfigurer.getInstance().setCrcCheckMode(false);
			} else {
				DCLog
						.logWarning(location, 
								"ASJ.dpl_dc.004358",
								"The [{0}] value of [{1}] service property [{2}] is wrong. The correct values are [{3}].",
								new Object[] { crcCheckMode, CM.SERVICE_NAME,
										CRC_CHECK_MODE_KEY, V_ON_OFF });
			}
		}
	}
	
	private void setDSWarningsSuppressed(Properties properties) {
		final String dsWarningsSuppressed = properties.getProperty(SUPPRESS_DS_WARNINGS);
		
		if (dsWarningsSuppressed != null) {
			if (ON.equalsIgnoreCase(dsWarningsSuppressed)) {
				ServiceConfigurer.getInstance().setDSWarningSuppressed(true);
			} else if (OFF.equalsIgnoreCase(dsWarningsSuppressed)) {
				ServiceConfigurer.getInstance().setDSWarningSuppressed(false);
			} else {
				DCLog
				.logWarning(location, 
						"ASJ.dpl_dc.004358",
						"The [{0}] value of [{1}] service property [{2}] is wrong. The correct values are [{3}].",
						new Object[] { dsWarningsSuppressed, CM.SERVICE_NAME,
								CRC_CHECK_MODE_KEY, V_ON_OFF });
			}
		}
	}

	private void setMinFreeBytesToDeploy(final Properties properties) {
		ServiceConfigurer.getInstance().setMinFreeBytesToDeploy(
				Long
						.parseLong(properties
								.getProperty(MIN_FREE_BYTES_TO_DEPLOY)));
	}

	private void setOfflineStrategies(Properties props) {
		final String deplOSValue = props.getProperty(DEPL_OFFLINE_STRATEGY);
		if (deplOSValue != null) {
			if (NORMAL.equalsIgnoreCase(deplOSValue)) {
				ServiceConfigurer.getInstance().setDeployWorkflowStrategy(
						DeployWorkflowStrategy.NORMAL);
			} else if (SAFETY.equalsIgnoreCase(deplOSValue)) {
				ServiceConfigurer.getInstance().setDeployWorkflowStrategy(
						DeployWorkflowStrategy.SAFETY);
			} else if (ROLLING.equalsIgnoreCase(deplOSValue)) {
				ServiceConfigurer.getInstance().setDeployWorkflowStrategy(
						DeployWorkflowStrategy.ROLLING);
			} else {
				DCLog
						.logWarning(location, 
								"ASJ.dpl_dc.004359",
								"The [{0}] value of [{1}] service property [{2}] is wrong. The correct values are [{3}].",
								new Object[] { deplOSValue, CM.SERVICE_NAME,
										DEPL_OFFLINE_STRATEGY,
										V_NORMAL_SAFETY_ROLLING });
			}
		}

		final String undeplOSValue = props.getProperty(UNDEPL_OFFLINE_STRATEGY);
		if (undeplOSValue != null) {
			if (NORMAL.equalsIgnoreCase(undeplOSValue)) {
				ServiceConfigurer.getInstance().setUndeployWorkflowStrategy(
						UndeployWorkflowStrategy.NORMAL);
			} else if (SAFETY.equalsIgnoreCase(undeplOSValue)) {
				ServiceConfigurer.getInstance().setUndeployWorkflowStrategy(
						UndeployWorkflowStrategy.SAFETY);
			} else {
				DCLog
						.logWarning(location, 
								"ASJ.dpl_dc.004360",
								"The [{0}] value of [{1}] service property [{2}] is wrong. The correct values are [{3}].",
								new Object[] { undeplOSValue, CM.SERVICE_NAME,
										UNDEPL_OFFLINE_STRATEGY,
										V_NORMAL_SAFETY });
			}
		}
	}

	private void setParallelismStrategies(Properties props) {
		final String deplPSValue = props.getProperty(DEPL_PARALLELISM_STRATEGY);
		if (deplPSValue != null) {
			if (NORMAL.equalsIgnoreCase(deplPSValue)) {
				ServiceConfigurer.getInstance().setDeployParallelismStrategy(
						DeployParallelismStrategy.NORMAL);
			} else if (SAFETY.equalsIgnoreCase(deplPSValue)) {
				ServiceConfigurer.getInstance().setDeployParallelismStrategy(
						DeployParallelismStrategy.SAFETY);
			} else {
				DCLog
						.logWarning(location, 
								"ASJ.dpl_dc.004361",
								"The [{0}] value of [{1}] service property [{2}] is wrong. The correct values are [{3}].",
								new Object[] { deplPSValue, CM.SERVICE_NAME,
										DEPL_PARALLELISM_STRATEGY,
										V_NORMAL_SAFETY });
			}
		}

		final String undeplPSValue = props
				.getProperty(UNDEPL_PARALLELISM_STRATEGY);
		if (undeplPSValue != null) {
			if (NORMAL.equalsIgnoreCase(undeplPSValue)) {
				ServiceConfigurer.getInstance().setUndeployParallelismStrategy(
						UndeployParallelismStrategy.NORMAL);
			} else if (SAFETY.equalsIgnoreCase(undeplPSValue)) {
				ServiceConfigurer.getInstance().setUndeployParallelismStrategy(
						UndeployParallelismStrategy.SAFETY);
			} else {
				DCLog
						.logWarning(location, 
								"ASJ.dpl_dc.004362",
								"The [{0}] value of [{1}] service property [{2}] is wrong. The correct values are [{3}].",
								new Object[] { undeplPSValue, CM.SERVICE_NAME,
										UNDEPL_PARALLELISM_STRATEGY,
										V_NORMAL_SAFETY });
			}
		}
	}

	private void setGenSessionId4Secs(Properties props) {
		final String genSessionId4Secs = props
				.getProperty(GEN_SESSION_ID_4_SECS);
		if (genSessionId4Secs != null) {
			ServiceConfigurer.getInstance().setGenSessionId4Secs(
					Integer.decode(genSessionId4Secs).intValue());
		}
	}

	private void setGenSessionIdsAtOnes(Properties props) {
		final String genSessionIdsAtOnes = props
				.getProperty(GEN_SESSION_IDS_AT_ONES);
		if (genSessionIdsAtOnes != null) {
			ServiceConfigurer.getInstance().setGenSessionIdsAtOnes(
					Integer.decode(genSessionIdsAtOnes).intValue());
		}
	}

	private void setOfflineResultTimeout(Properties props) {
		final String getOfflineResultTimeout = props
				.getProperty(OFFLINE_RESULT_TIMEOUT);
		if (getOfflineResultTimeout != null) {
			try {
				ServiceConfigurer.getInstance().setOfflineResultTimeout(
						Long.decode(getOfflineResultTimeout).intValue());
			} catch (NumberFormatException nfe) {
				DCLog.logErrorThrowable(location, nfe);
			}
		}
	}

	private void setWDServerInfo(Properties props) {
		ServiceConfigurer.getInstance().setWdServerInfo(
				props.getProperty(WEB_DISP_SERVER_INFO));
	}

	private void setInstId2InstPfl(Properties props) throws IOException {
		final Properties instId2InstPfl = new Properties();
		final String value = props.getProperty(INST_ID_2_INST_PFL);
		if (value != null) {
			instId2InstPfl.load(new StringBufferInputStream(value));
			ServiceConfigurer.getInstance().setInstId2InstPfl(instId2InstPfl);
		}
	}

	private void setOsUser(final Properties props) {
		ServiceConfigurer.getInstance().setOsUser(props.getProperty(OS_USER));
	}

	private void setOsPass(final Properties props) {
		ServiceConfigurer.getInstance().setOsPass(props.getProperty(OS_PASS));
	}

	private void setDeployThreadsNumber(final Properties props) {
		ServiceConfigurer.getInstance().setDeployThreadsNumber(
				Integer.parseInt(props.getProperty(DEPLOY_THREADS_NUMBER)));
	}

	private void registerCM(ApplicationServiceContext appServiceCtx)
			throws ServiceException {
		final CM cm;
		this.objReg = appServiceCtx.getContainerContext().getObjectRegistry();
		if (this.objReg.getServiceInterface(CM.SERVICE_NAME) != null) {
			return;
		}
		try {
			cm = CMFactory.getInstance().createComponentManager();
		} catch (RemoteException re) {
			throw new ServiceException(DCLog.getLocation(), re);
		}

		this.objReg.registerInterface(cm);
		if (location.beDebug()) {
			traceDebug(location, "[{0}] was binded in registry.",
					new Object[] { CM.class.getName() });
		}
	}

	private void unregisterCM() {
		if (this.objReg != null
				&& this.objReg.getServiceInterface(CM.SERVICE_NAME) != null) {
			this.objReg.unregisterInterface();
		}
	}

}
