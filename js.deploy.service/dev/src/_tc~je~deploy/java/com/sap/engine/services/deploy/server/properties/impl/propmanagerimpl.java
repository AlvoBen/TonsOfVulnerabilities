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
package com.sap.engine.services.deploy.server.properties.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.monitor.ClusterMonitor;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.load.LoadContext;
import com.sap.engine.frame.core.locking.ServerInternalLocking;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.frame.core.monitor.CoreMonitor;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.services.configuration.appconfiguration.impl.ApplicationConfigHandlerFactoryImpl;
import com.sap.engine.services.configuration.jta.DistributedConfigurationHandlerFactory;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.properties.ServerState;
import com.sap.engine.services.deploy.server.utils.Convertor;
import com.sap.engine.services.deploy.server.utils.DSConstants;
import com.sap.engine.services.deploy.server.utils.FSUtils;
import com.sap.engine.services.deploy.server.utils.StringUtils;
import com.sap.tc.logging.Location;

/**
 * An implementation of <code>PropManager</code> interface.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public final class PropManagerImpl extends PropManager {
	
	private static final Location location = 
		Location.getLocation(PropManagerImpl.class);
	

	// do not change the service name
	private final static String SERVICE_NAME = "deploy";

	// service property
	private static final String APPS_WORK_DIR = "Applications_Folder";
	private final String appsWorkDir;

	// service property - hidden one
	private static final String ADDITIONAL_DEBUG_INFO = "Additional_Debug_Info";
	private final int additionalDebugInfo;

	// service property
	private static final String CLEAR_AFTER_FAILURE = "Clear_After_Failure";
	private final boolean isClearAfterFailure;

	// service property
	private static final String FAIL_FAST_ON_LOCK_ATTEMPT = "Fail_Fast_On_Lock_Attempt";
	private final boolean failFastOnLockAttempt;

	// service property
	private static final String STRICT_J2EE_CHECKS = "Strict_J2EE_Checks";
	private final boolean isStrictJ2eeChecks;

	// service property
	private static final String TX_OPERATION_SUPPORTED = "Tx_Operation_Supported";
	private final boolean isTxOperationSupported;

	// service property
	private static final String FIRST_WINS = "First_Wins";
	private final boolean firstWins;

	// service property
	private static final String USE_NEW_REF_RESOLVER = "Use_New_Ref_Resolver";
	private final boolean useNewRefResolver;

	// service property
	private static final String IS_SAP_MANIFEST_READABLE = "Is_Sap_Manifest_Readable";
	private final boolean isSapManifestReadable;

	// service property
	private static final String IS_DD_READABLE = "Is_DD_Readable";
	private final boolean isDdReadable;

	// service property
	private static final String STANDARD_APP_REFS = "StandardApplicationReferences";
	private final List<Component> standardAppRefs;

	// service property
	private static final String ITF_WITHOUT_PROVIDER = "Interfaces_Without_Provider";
	private final Set<String> itfWithoutProvider;

	// service property
	private static final String STANDARD_JEE_LIBRARY_NAMES = "StandardJEELibraryNames";
	private final String[] standardJEELibraryNames;

	// service property
	private enum ApplicationResynch {
		DETECT, FORCE
	}

	private final ApplicationResynch applicationResynch;

	// service property
	private static final String TIMEOUT_4_START_INITIALLY = "Timeout_For_Start_Initially_In_Secs";
	private final long timeout4StartInitially;

	// service property
	private static final String TIMEOUT_4_LOCAL_LOCK = "Timeout_For_Local_Lock_In_Secs";
	private final long timeout4LocalLock;

	// service property
	private static final String TIMEOUT_4_LIBRARY_DEPLOY_EVENT = "Timeout_For_Library_Deploy_Event_In_Secs";
	private final long timeout4LibraryLoadedEvent;

	// service property
	private static final String TIMEOUT_4_REMOTE_OPERATION = "Timeout_For_Remote_Operation_In_Secs";
	private final long timeout4RemoteOperation;

	// service property
	private static final String TIMEOUT_4_BOOTSTRAP_LOCK = "Timeout_For_Bootstrap_Lock_In_Secs";
	private final int timeout4BootstrapLock;

	// service property
	private static final String TIMEOUT_4_FINAL_APP_STATUS = "Timeout_For_Final_App_Status_In_Secs";
	private final long timeout4FinalAppStatus;

	// service property
	private static final String CHECK_REFERENCE_CYCLES = "Check_Reference_Cycles";
	private final boolean checkReferenceCycles;

	// service property
	private final int applicationInitializerThreads;

	// service property
	private final long applicationInitializerTimeout;

	// service property
	private final int deployBootstrapperThreads;

	// service property
	private final long deployBootstrapperTimeout;

	// depends from the server status
	private static final String SERVICE_WORK_DIR = "Service_Work_Dir";
	private final String serviceWorkDir;

	// depends from the server status
	private static final String SERVER_STATE = "serverState";
	private final ServerState serverState;

	// depends from cluster set up.
	private static final String CL_ELEM_ID = "clElemID";
	private final int clElemID;

	// depends from cluster set up.
	private static final String CL_ELEM_NAME = "clElemName";
	private final String clElemName;

	// depends from cluster set up.
	private static final String APP_SERVICE_CTX = "appServiceCtx";
	private final ApplicationServiceContext asCtx;

	// -
	private static final String SERVER_INTERNAL_LOCKING_DESCR = "This locking is used from the deploy service.";
	private final ServerInternalLocking serverInternalLocking;

	// service property
	public static final String ANNOTATION_CLASS_LOADER_NAME = "Annotation_Class_Loader_Name";
	private final String annotationClassLoaderName;

	// service property
	private static final String EXCLUDED_JLINEE_TESTS = "Excluded_JLinEE_Tests";
	private final String[] excludedJLinEETests;

	// service property - parallel initial application start
	private static final String INITIAL_APPLICATION_START_THREADS = "Initial_Application_Start_Threads";
	private final long initialApplicationStartThreads;

	// service property
	private static final String DEPLOY_PARALLEL_STARTER_TIMEOUT = "Deploy_Parallel_Starter_Timeout_In_Secs";
	private final long deployParallelStartTimeout;

	// service property - parallel application stop
	private static final String APPLICATIONS_STOP_THREADS = "Applications_Stop_Threads";
	private final long applicationsStopThreads;

	// property for performance - when true, switches off the ShmComponent and
	// Thread Task reporting
	private static final String PERFORMANCE_BOOSTER = "com.sap.engine.disable.monitoring";
	private final boolean boostPerformance;

	// property to mark productive and development mode
	private static final String IS_PRODUCTIVE_MODE = "Is_Productive_Mode";
	private final boolean isProductiveMode;

	// cached one
	private static final String INITIAL_CONTEXT = "InitialContext";
	private final InitialContext initialContext;

	// cached one
	private static final String DISTRIBUTED_CONFIGURATION_FACTORY = "DistributedConfigurationsFactory";
	// TODO - need to be final, but cannot due to cycle runtime reference making
	// reference to configuration service strong, but not weak one
	private/* final */DistributedConfigurationHandlerFactory distributedConfigurationHandlerFactory;

	// cached one
	private static final String APPLICATION_CONFIGURATION = "ApplicationConfiguration";
	// TODO - need to be final, but cannot due to cycle runtime reference making
	// reference to configuration service strong, but not weak one
	private ApplicationConfigHandlerFactoryImpl applicationConfigHandlerFactoryImpl;

	/**
	 * The standard constructor, used by PropManagerFactory to initialize the
	 * property manager during deploy service start.
	 * 
	 * @param asCtx
	 * @throws ServiceException
	 *             in case of problems with application work directory.
	 */
	public PropManagerImpl(ApplicationServiceContext asCtx)
			throws ServiceException {
		this.asCtx = asCtx;
		if (location.beDebug()) {
			DSLog.traceDebug(location, "Setting [{0}] = [{1}]", APP_SERVICE_CTX,
					asCtx);
		}

		serviceWorkDir = readServiceWorkDir();
		appsWorkDir = readAppsWorkDir();
		failFastOnLockAttempt = getBooleanProperty(FAIL_FAST_ON_LOCK_ATTEMPT, false);
		isClearAfterFailure = getBooleanProperty(CLEAR_AFTER_FAILURE, true);
		additionalDebugInfo = readAdditionalDebugInfo();
		serverState = readServerState();
		isStrictJ2eeChecks = getBooleanProperty(STRICT_J2EE_CHECKS, false);
		isTxOperationSupported = getBooleanProperty(TX_OPERATION_SUPPORTED,
				false);
		firstWins = getBooleanProperty(FIRST_WINS, false);
		useNewRefResolver = getBooleanProperty(USE_NEW_REF_RESOLVER, false);
		applicationResynch = ApplicationResynch.valueOf(getStringProperty(
				APPLICATION_RESYNCH, ApplicationResynch.DETECT.name())
				.toUpperCase());
		timeout4StartInitially = getIntegerProperty(TIMEOUT_4_START_INITIALLY,
				60) * 1000;
		timeout4LibraryLoadedEvent = getIntegerProperty(
				TIMEOUT_4_LIBRARY_DEPLOY_EVENT, 60) * 1000;
		timeout4RemoteOperation = getIntegerProperty(
				TIMEOUT_4_REMOTE_OPERATION, 60 * 20) * 1000;
		timeout4LocalLock = getIntegerProperty(TIMEOUT_4_LOCAL_LOCK, 60 * 20) * 1000;
		timeout4BootstrapLock = getIntegerProperty(TIMEOUT_4_BOOTSTRAP_LOCK,
				60 * 15) * 1000;
		timeout4FinalAppStatus = getIntegerProperty(TIMEOUT_4_FINAL_APP_STATUS,
				60 * 5) * 1000;
		standardAppRefs = readStandardAppRefs();
		itfWithoutProvider = StringUtils
				.parse2Set(getStringProperty(ITF_WITHOUT_PROVIDER));
		standardJEELibraryNames = StringUtils
				.parse2String(getStringProperty(STANDARD_JEE_LIBRARY_NAMES));
		serverInternalLocking = createServerInternalLocking();
		checkReferenceCycles = getBooleanProperty(CHECK_REFERENCE_CYCLES, true);
		applicationInitializerThreads = getIntegerProperty(
				APPLICATION_INITIALIZER_THREADS, 5);
		applicationInitializerTimeout = getIntegerProperty(
				APPLICATION_INITIALIZER_TIMEOUT, 60 * 60) * 1000;
		deployBootstrapperThreads = getIntegerProperty(
				DEPLOY_BOOTSTRAPPER_THREADS, 5);
		deployBootstrapperTimeout = getIntegerProperty(
				DEPLOY_BOOTSTRAPPER_TIMEOUT, 60 * 60) * 1000;
		annotationClassLoaderName = getStringProperty(ANNOTATION_CLASS_LOADER_NAME);
		excludedJLinEETests = StringUtils
				.parse2String(getStringProperty(EXCLUDED_JLINEE_TESTS));
		initialApplicationStartThreads = getIntegerProperty(
				INITIAL_APPLICATION_START_THREADS, 1);

		deployParallelStartTimeout = getIntegerProperty(
				DEPLOY_PARALLEL_STARTER_TIMEOUT, 60 * 60) * 1000;
		applicationsStopThreads = getIntegerProperty(APPLICATIONS_STOP_THREADS,
				1);
		final ClusterElement ce = obtainClusterElement();
		clElemID = ce.getClusterId();
		clElemName = ce.getName();
		isSapManifestReadable = getBooleanProperty(IS_SAP_MANIFEST_READABLE,
				true);
		isDdReadable = getBooleanProperty(IS_DD_READABLE, true);
		boostPerformance = Boolean.getBoolean(PERFORMANCE_BOOSTER);
		isProductiveMode = getBooleanProperty(IS_PRODUCTIVE_MODE, false);
		initialContext = initInitialContext();
		distributedConfigurationHandlerFactory = initDistributedConfigurationHandlerFactory();
		applicationConfigHandlerFactoryImpl = initApplicationConfigHandlerFactoryImpl();
		if (location.beDebug()) {
			DSLog.traceDebug(location, "{0}", toString());
		}
	}

	/**
	 * This constructor is used only by some JUnit tests.
	 * 
	 * @param appsWorkDir
	 *            applications work directory. Not null.
	 * @param clusterElemId
	 *            . Id of the cluster element. Always positive.
	 * @param clusterElemName
	 *            cluster element name. Not null.
	 */
	public PropManagerImpl(final String appsWorkDir, final int clusterElemId,
			final String clusterElemName) {

		// check the method's contract
		assert appsWorkDir != null;
		assert clusterElemId >= 0;

		asCtx = null;
		additionalDebugInfo = ADDITIONAL_DEBUG_INFO_DEFAULT;
		failFastOnLockAttempt = false;
		isClearAfterFailure = true;
		this.appsWorkDir = appsWorkDir;
		isStrictJ2eeChecks = false;
		clElemID = clusterElemId;
		clElemName = clusterElemName;
		isTxOperationSupported = false;
		firstWins = false;
		useNewRefResolver = true;
		isSapManifestReadable = true;
		isDdReadable = true;
		standardAppRefs = Collections.emptyList();
		itfWithoutProvider = Collections.emptySet();
		standardJEELibraryNames = StringUtils.emptyStringArray();
		applicationResynch = ApplicationResynch.DETECT;
		timeout4StartInitially = -1;
		timeout4LibraryLoadedEvent = -1;
		timeout4RemoteOperation = -1;
		timeout4BootstrapLock = -1;
		timeout4FinalAppStatus = -1;
		timeout4LocalLock = -1;
		checkReferenceCycles = true;
		applicationInitializerThreads = -1;
		applicationInitializerTimeout = -1;
		deployBootstrapperThreads = -1;
		deployBootstrapperTimeout = -1;
		serviceWorkDir = null;
		serverState = null;
		serverInternalLocking = null;
		annotationClassLoaderName = null;
		excludedJLinEETests = StringUtils.emptyStringArray();
		initialApplicationStartThreads = -1;
		deployParallelStartTimeout = -1;
		applicationsStopThreads = -1;
		boostPerformance = false;
		isProductiveMode = false;
		initialContext = null;
		distributedConfigurationHandlerFactory = null;
		applicationConfigHandlerFactoryImpl = null;
		if (location.beDebug()) {
			DSLog.traceDebug(location, "{0}", toString());
		}
	}

	@Override
	public String getAppsWorkDir() {
		return appsWorkDir;
	}

	private void validateAppsWorkDir(String src) throws IOException {
		if (src == null) {
			src = getDefaultAppsWorkDir();
		}
		final String PATH_DOT = ".";
		final String PATH_DOUBLE_DOT = "..";

		final File sourceDir = new File(src);
		final File canSrcDir = sourceDir.getCanonicalFile();
		if (sourceDir.equals(canSrcDir)) {
			return;// the "apps" folder will be the same for all server nodes.
		} else {
			if (src.indexOf(PATH_DOUBLE_DOT) == -1) {// there are NO ".."
				if (src.startsWith(PATH_DOT)) {// starts with "."
					// the "apps" folder is in "serverX" folder
					throwIllegalArgumentException4AppsWorkDir(src,
							"it doesn't contain [" + PATH_DOUBLE_DOT
									+ "], but starts with [" + PATH_DOT + "]");
				} else {
					// the "apps" folder will be the same for all server nodes.
					return;
				}
			} else {// there are ".."
				if (src.startsWith(PATH_DOT)) {// starts with "."
					final File fileDot = new File(PATH_DOT);
					if (canSrcDir.getCanonicalPath().startsWith(
							fileDot.getCanonicalPath())) {
						// the "apps" folder is in "serverX" folder
						throwIllegalArgumentException4AppsWorkDir(src,
								"it contains [" + PATH_DOUBLE_DOT
										+ "], starts with [" + PATH_DOT + "]"
										+ " and its canonical path ["
										+ canSrcDir.getCanonicalPath()
										+ "] starts with ["
										+ fileDot.getCanonicalPath() + "]");
					} else {
						// the "apps" folder will be the same for all server
						// nodes.
						return;
					}
				} else {
					// the "apps" folder will be the same for all server nodes.
					return;
				}
			}
		}
	}

	private void throwIllegalArgumentException4AppsWorkDir(
			String invalidAppsWorkDir, String error) {
		throw new IllegalArgumentException(
				"ASJ.dpl_ds.006067 Could you please change the property "
						+ APPS_WORK_DIR
						+ " of "
						+ SERVICE_NAME
						+ " service in a way to be resolvable from each server node "
						+ "in each instance in the same way. The current value is "
						+ invalidAppsWorkDir + " and it is invalid, because "
						+ error + ".");
	}

	private String readAppsWorkDir() throws ServiceException {
		final String dir = asCtx.getServiceState().getProperties().getProperty(
				APPS_WORK_DIR);

		try {
			validateAppsWorkDir(dir);
			return FSUtils.dirNormalizer(dir, getDefaultAppsWorkDir(), false);
		} catch (IOException ioe) {
			throw createServiceException(
					", because of wrong application work dir '" + dir + "' .",
					ioe);
		}
	}

	private String getDefaultAppsWorkDir() {
		return "." + File.separator + ".." + File.separator + "apps"
				+ File.separator;
	}

	public String getServiceWorkDir() {
		return serviceWorkDir;
	}

	private String getDefaultServiceWorkDir() {
		return "." + File.separator;
	}

	public boolean isAdditionalDebugInfo() {
		return additionalDebugInfo != ADDITIONAL_DEBUG_INFO_DEFAULT;
	}

	public int getAdditionalDebugInfo() {
		return additionalDebugInfo;
	}

	private int readAdditionalDebugInfo() {
		final int addDI;
		final int value = getIntegerProperty(ADDITIONAL_DEBUG_INFO,
				ADDITIONAL_DEBUG_INFO_DEFAULT);
		if (value < ADDITIONAL_DEBUG_INFO_DEFAULT) {
			addDI = ADDITIONAL_DEBUG_INFO_DEFAULT;
		} else if (value > ADDITIONAL_DEBUG_INFO_ALL_STATS) {
			addDI = ADDITIONAL_DEBUG_INFO_ALL_STATS;
		} else {
			addDI = value;
		}
		return addDI;
	}

	public boolean isClearAfterFailure() {
		return isClearAfterFailure;
	}

	public ServerState getServerState() {
		return serverState;
	}

	public boolean isStrictJ2eeChecks() {
		return isStrictJ2eeChecks;
	}

	public boolean isApplicationResynch() {
		return applicationResynch.equals(ApplicationResynch.FORCE);
	}

	public long getTimeout4StartInitially() {
		return timeout4StartInitially;
	}

	public long getTimeout4LibraryLoadedEvent() {
		return timeout4LibraryLoadedEvent;
	}

	public long getTimeout4LocalLock() {
		return timeout4LocalLock;
	}

	public long getTimeout4RemoteOperation() {
		return timeout4RemoteOperation;
	}

	public int getTimeout4BootstrapLock() {
		return timeout4BootstrapLock;
	}

	public long getTimeout4FinalAppStatus() {
		return timeout4FinalAppStatus;
	}

	public boolean getCheckReferenceCycles() {
		return checkReferenceCycles;
	}

	public int getApplicationInitializerThreads() {
		return applicationInitializerThreads;
	}

	public long getApplicationInitializerTimeout() {
		return applicationInitializerTimeout;
	}

	public int getDeployBootstrapperThreads() {
		return deployBootstrapperThreads;
	}

	public long getDeployBootstrapperTimeout() {
		return deployBootstrapperTimeout;
	}

	public List<Component> getStandardAppRefs() {
		return standardAppRefs;
	}

	public String[] getStandardJEELibraryNames() {
		return standardJEELibraryNames;
	}

	private List<Component> readStandardAppRefs() {
		final List<String> usedAppRefs = StringUtils.parse2List(asCtx
				.getServiceState().getProperty(STANDARD_APP_REFS));
		final List<Component> appRefs = new ArrayList<Component>(usedAppRefs
				.size());
		for (String appRef : usedAppRefs) {
			appRefs.add(Component.create(appRef));
		}
		return Collections.unmodifiableList(appRefs);
	}

	public int getClElemID() {
		return clElemID;
	}

	public String getClElemName() {
		return clElemName;
	}

	public ApplicationServiceContext getAppServiceCtx() {
		return asCtx;
	}

	private ClusterElement obtainClusterElement() {
		final ClusterElement ce = asCtx.getClusterContext().getClusterMonitor()
				.getCurrentParticipant();
		if (ce == null) {
			throw new IllegalStateException(
					"ASJ.dpl_ds.006090 The 'ClusterElement' of the current participant is NULL.");
		}
		return ce;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getSimpleName());
		sb.append(DSConstants.EOL).append(APP_SERVICE_CTX).append(
				DSConstants.EQUALS).append(getAppServiceCtx());
		sb.append(DSConstants.EOL).append(SERVICE_WORK_DIR).append(
				DSConstants.EQUALS).append(getServiceWorkDir());
		sb.append(DSConstants.EOL).append(APPS_WORK_DIR).append(
				DSConstants.EQUALS).append(getAppsWorkDir());
		sb.append(DSConstants.EOL).append(CLEAR_AFTER_FAILURE).append(
				DSConstants.EQUALS).append(isClearAfterFailure());
		sb.append(DSConstants.EOL).append(ADDITIONAL_DEBUG_INFO).append(
				DSConstants.EQUALS).append(getAdditionalDebugInfo());
		sb.append(DSConstants.EOL).append(SERVER_STATE).append(
				DSConstants.EQUALS).append(getServerState());
		sb.append(DSConstants.EOL).append(STRICT_J2EE_CHECKS).append(
				DSConstants.EQUALS).append(isStrictJ2eeChecks());
		sb.append(DSConstants.EOL).append(TX_OPERATION_SUPPORTED).append(
				DSConstants.EQUALS).append(isTxOperationSupported());
		sb.append(DSConstants.EOL).append(USE_NEW_REF_RESOLVER).append(
				DSConstants.EQUALS).append(useNewReferenceResolver());
		sb.append(DSConstants.EOL).append(APPLICATION_RESYNCH).append(
				DSConstants.EQUALS).append(isApplicationResynch());
		sb.append(DSConstants.EOL).append(TIMEOUT_4_START_INITIALLY).append(
				DSConstants.EQUALS).append(getTimeout4StartInitially() / 1000);
		sb.append(DSConstants.EOL).append(TIMEOUT_4_LIBRARY_DEPLOY_EVENT)
				.append(DSConstants.EQUALS).append(
						getTimeout4LibraryLoadedEvent() / 1000);
		sb.append(DSConstants.EOL).append(TIMEOUT_4_REMOTE_OPERATION).append(
				DSConstants.EQUALS).append(getTimeout4RemoteOperation() / 1000);
		sb.append(DSConstants.EOL).append(TIMEOUT_4_BOOTSTRAP_LOCK).append(
				DSConstants.EQUALS).append(getTimeout4BootstrapLock() / 1000);
		sb.append(DSConstants.EOL).append(TIMEOUT_4_FINAL_APP_STATUS).append(
				DSConstants.EQUALS).append(getTimeout4FinalAppStatus() / 1000);
		sb.append(DSConstants.EOL).append(STANDARD_APP_REFS).append(
				DSConstants.EQUALS).append(getStandardAppRefs());
		sb.append(DSConstants.EOL).append(ITF_WITHOUT_PROVIDER).append(
				DSConstants.EQUALS).append(getInterfacesWithoutProvider());
		sb.append(DSConstants.EOL).append(STANDARD_JEE_LIBRARY_NAMES).append(
				DSConstants.EQUALS).append(
				Convertor.toString(getStandardJEELibraryNames(), ""));
		sb.append(DSConstants.EOL).append(SERVER_INTERNAL_LOCKING_ID).append(
				DSConstants.EQUALS).append(getServerInternalLocking());
		sb.append(DSConstants.EOL).append(CHECK_REFERENCE_CYCLES).append(
				DSConstants.EQUALS).append(getCheckReferenceCycles());
		sb.append(DSConstants.EOL).append(APPLICATION_INITIALIZER_THREADS)
				.append(DSConstants.EQUALS).append(
						getApplicationInitializerThreads());
		sb.append(DSConstants.EOL).append(APPLICATION_INITIALIZER_TIMEOUT)
				.append(DSConstants.EQUALS).append(
						getApplicationInitializerTimeout() / 1000);
		sb.append(DSConstants.EOL).append(DEPLOY_BOOTSTRAPPER_THREADS).append(
				DSConstants.EQUALS).append(getDeployBootstrapperThreads());
		sb.append(DSConstants.EOL).append(DEPLOY_BOOTSTRAPPER_TIMEOUT).append(
				DSConstants.EQUALS).append(
				getDeployBootstrapperTimeout() / 1000);
		sb.append(DSConstants.EOL).append(ANNOTATION_CLASS_LOADER_NAME).append(
				DSConstants.EQUALS).append(getAnnotationClassloaderName());
		sb.append(DSConstants.EOL).append(EXCLUDED_JLINEE_TESTS).append(
				DSConstants.EQUALS).append(
				Convertor.toString(getExcludedJLinEETests(), ""));
		sb.append(DSConstants.EOL).append(INITIAL_APPLICATION_START_THREADS)
				.append(DSConstants.EQUALS).append(
						getInitialApplicationStartThreads());
		sb.append(DSConstants.EOL).append(DEPLOY_PARALLEL_STARTER_TIMEOUT)
				.append(DSConstants.EQUALS).append(
						getParallelStartTimeout() / 1000);
		sb.append(DSConstants.EOL).append(APPLICATIONS_STOP_THREADS).append(
				DSConstants.EQUALS).append(getApplicationsStopThreads());
		sb.append(DSConstants.EOL).append(CL_ELEM_ID)
				.append(DSConstants.EQUALS).append(getClElemID());
		sb.append(DSConstants.EOL).append(CL_ELEM_NAME).append(
				DSConstants.EQUALS).append(getClElemName());
		sb.append(DSConstants.EOL).append(IS_SAP_MANIFEST_READABLE).append(
				DSConstants.EQUALS).append(isSapManifestReadable());
		sb.append(DSConstants.EOL).append(IS_DD_READABLE).append(
				DSConstants.EQUALS).append(isDdReadable());
		sb.append(DSConstants.EOL).append(PERFORMANCE_BOOSTER).append(
				DSConstants.EQUALS).append(isBoostPerformance());
		sb.append(DSConstants.EOL).append(IS_PRODUCTIVE_MODE).append(
				DSConstants.EQUALS).append(isProductiveMode());
		sb.append(DSConstants.EOL).append(INITIAL_CONTEXT).append(
				DSConstants.EQUALS).append(getInitialContext());
		sb.append(DSConstants.EOL).append(DISTRIBUTED_CONFIGURATION_FACTORY)
				.append(DSConstants.EQUALS).append(
						getDistributedConfigurationHandlerFactoryWithNull());
		sb.append(DSConstants.EOL).append(APPLICATION_CONFIGURATION).append(
				DSConstants.EQUALS).append(
				getApplicationConfigHandlerFactoryImplWithNull());
		sb.append(DSConstants.EOL);
		return sb.toString();
	}

	public String getServiceName() {
		return SERVICE_NAME;
	}

	public ConfigurationHandlerFactory getConfigurationHandlerFactory() {
		return getAppServiceCtx().getCoreContext()
				.getConfigurationHandlerFactory();
	}

	public DistributedConfigurationHandlerFactory getDistributedConfigurationHandlerFactory() {
		getDistributedConfigurationHandlerFactoryWithNull();
		if (distributedConfigurationHandlerFactory == null) {
			throw new IllegalStateException(
					"Deploy service was not able to look up "
							+ DISTRIBUTED_CONFIGURATION_FACTORY
							+ ", because it needs strong runtime reference to configuration"
							+ " service, but introducing such currently leads to a cycle.");
		}
		return distributedConfigurationHandlerFactory;
	}

	private DistributedConfigurationHandlerFactory getDistributedConfigurationHandlerFactoryWithNull() {
		if (distributedConfigurationHandlerFactory == null) {
			distributedConfigurationHandlerFactory = initDistributedConfigurationHandlerFactory();
		}
		return distributedConfigurationHandlerFactory;
	}

	public ApplicationConfigHandlerFactoryImpl getApplicationConfigHandlerFactoryImpl() {
		getApplicationConfigHandlerFactoryImplWithNull();
		if (applicationConfigHandlerFactoryImpl == null) {
			throw new IllegalStateException(
					"Deploy service was not able to look up "
							+ APPLICATION_CONFIGURATION
							+ ", because it needs strong runtime reference to configuration"
							+ " service, but introducing such currently leads to a cycle.");
		}
		return applicationConfigHandlerFactoryImpl;
	}

	private ApplicationConfigHandlerFactoryImpl getApplicationConfigHandlerFactoryImplWithNull() {
		if (applicationConfigHandlerFactoryImpl == null) {
			applicationConfigHandlerFactoryImpl = initApplicationConfigHandlerFactoryImpl();
		}
		return applicationConfigHandlerFactoryImpl;
	}

	public InitialContext getInitialContext() {
		return initialContext;
	}

	public CoreMonitor getCoreMonitor() {
		return asCtx.getCoreContext().getCoreMonitor();
	}

	public ClusterMonitor getClusterMonitor() {
		return asCtx.getClusterContext().getClusterMonitor();
	}

	public LoadContext getLoadContext() {
		return asCtx.getCoreContext().getLoadContext();
	}

	public ServerInternalLocking getServerInternalLocking() {
		return serverInternalLocking;
	}

	public ServerInternalLocking createServerInternalLocking()
			throws ServiceException {
		try {
			return getAppServiceCtx().getCoreContext().getLockingContext()
					.createServerInternalLocking(SERVER_INTERNAL_LOCKING_ID,
							SERVER_INTERNAL_LOCKING_DESCR);
		} catch (TechnicalLockException tlEx) {
			throw createServiceException(
					", because cannot create server internal locking for '"
							+ SERVER_INTERNAL_LOCKING_ID + "' .", tlEx);
		} catch (IllegalArgumentException iaEx) {
			throw createServiceException(
					", because cannot create server internal locking for '"
							+ SERVER_INTERNAL_LOCKING_ID + "' .", iaEx);
		}
	}

	public ThreadSystem getThreadSystem() {
		// Has to handle this for the JUnit tests
		final ApplicationServiceContext asCtx = getAppServiceCtx();
		return asCtx != null ? asCtx.getCoreContext().getThreadSystem() : null;
	}

	public Properties getServiceProperties() {
		return getAppServiceCtx().getServiceState().getProperties();
	}

	public Date getDateFromDB() {
		return new Date();
	}

	private String readServiceWorkDir() throws ServiceException {
		try {
			return FSUtils.dirNormalizer(asCtx.getServiceState()
					.getWorkingDirectoryName(), getDefaultServiceWorkDir(),
					true);
		} catch (IOException ioe) {
			throw createServiceException(
					", because of wrong service work dir '" + serviceWorkDir
							+ "' .", ioe);
		}
	}

	private ServiceException createServiceException(final String msg,
			final Exception ex) {
		return new ServiceException(ServiceException.SERVICE_NOT_STARTED,
				new String[] { getServiceName(), msg }, ex);
	}

	private RuntimeException createRuntimeException(final String msg,
			final Exception ex) {
		return new RuntimeException("Cannot " + msg, ex);
	}

	private String getStringProperty(final String propertyName,
			final String defaultValue) {
		final String value = getStringProperty(propertyName);
		return value != null ? value : defaultValue;
	}

	private String getStringProperty(String propertyName) {
		return asCtx.getServiceState().getProperty(propertyName);
	}

	private int getIntegerProperty(final String propertyName, int defaultValue) {
		int res = defaultValue;
		final String value = asCtx.getServiceState().getProperty(propertyName);
		if (value != null) {
			try {
				res = Integer.parseInt(value);
			} catch (NumberFormatException nfEx) {
				DSLog
						.logErrorThrowable(
											location,
											"ASJ.dpl_ds.000353",
											"Will set service property [{0}] to its default value [{1}].",
											nfEx, propertyName, defaultValue);
			}
		}
		return res;
	}

	private boolean getBooleanProperty(final String propertyName,
		final boolean defaultValue) {
		final String value = asCtx.getServiceState().getProperty(propertyName);
		return value != null ? Boolean.parseBoolean(value) : defaultValue;
	}

	private ServerState readServerState() {
		final CoreMonitor cm = asCtx.getCoreContext().getCoreMonitor();
		final byte sMode = cm.getRuntimeMode();
		final byte sAction = cm.getRuntimeAction();
		if (sMode == CoreMonitor.RUNTIME_MODE_SAFE
				&& sAction == CoreMonitor.RUNTIME_ACTION_MIGRATE) {
			return ServerState.SAFE_MIGRATE;
		} else if (sMode == CoreMonitor.RUNTIME_MODE_SAFE
				&& sAction == CoreMonitor.RUNTIME_ACTION_APPLICATION_MIGRATE) {
			return ServerState.SAFE_APP_MIGRATE;
		} else if (sMode == CoreMonitor.RUNTIME_MODE_SAFE
				&& sAction == CoreMonitor.RUNTIME_ACTION_DEPLOY) {
			return ServerState.SAFE_DEPLOY;
		} else if (sMode == CoreMonitor.RUNTIME_MODE_SAFE
				&& sAction == CoreMonitor.RUNTIME_ACTION_UPGRADE) {
			return ServerState.SAFE_UPGRADE;
		} else if (sMode == CoreMonitor.RUNTIME_MODE_SAFE
				&& sAction == CoreMonitor.RUNTIME_ACTION_SWITCH) {
			return ServerState.SAFE_SWITCH;
		} else {
			return ServerState.NORMAL_NONE;
		}
	}

	private InitialContext initInitialContext() throws ServiceException {
		try {
			return new InitialContext();
		} catch (NamingException ne) {
			throw createServiceException(", because cannot instantiate ["
					+ INITIAL_CONTEXT + "].", ne);
		}
	}

	private DistributedConfigurationHandlerFactory initDistributedConfigurationHandlerFactory() {
		try {
			return (DistributedConfigurationHandlerFactory) getInitialContext()
					.lookup(DISTRIBUTED_CONFIGURATION_FACTORY);
		} catch (NameNotFoundException nnfe) {
			// not bound jet
			return null;
		} catch (NamingException ne) {
			throw createRuntimeException("look up ["
					+ DISTRIBUTED_CONFIGURATION_FACTORY + "].", ne);
		}
	}

	private ApplicationConfigHandlerFactoryImpl initApplicationConfigHandlerFactoryImpl() {
		try {
			return (ApplicationConfigHandlerFactoryImpl) getInitialContext()
					.lookup(APPLICATION_CONFIGURATION);
		} catch (NameNotFoundException nnfe) {
			// not bound jet
			return null;
		} catch (NamingException ne) {
			throw createRuntimeException("look up ["
					+ APPLICATION_CONFIGURATION + "].", ne);
		}
	}

	public String getAnnotationClassloaderName() {
		return annotationClassLoaderName;
	}

	public String[] getExcludedJLinEETests() {
		return excludedJLinEETests;
	}

	public int getInitialApplicationStartThreads() {
		return (int) initialApplicationStartThreads;
	}

	public long getParallelStartTimeout() {
		return deployParallelStartTimeout;
	}

	public boolean isTxOperationSupported() {
		return isTxOperationSupported;
	}

	public int getApplicationsStopThreads() {
		return (int) applicationsStopThreads;
	}

	public boolean isBoostPerformance() {
		return boostPerformance;
	}

	@Override
	public boolean useNewReferenceResolver() {
		return useNewRefResolver;
	}

	@Override
	public Set<String> getInterfacesWithoutProvider() {
		return this.itfWithoutProvider;
	}

	@Override
	public boolean isSapManifestReadable() {
		return isSapManifestReadable;
	}

	@Override
	public boolean isDdReadable() {
		return isDdReadable;
	}

	@Override
	public boolean firstWins() {
		return firstWins;
	}

	@Override
	public boolean isProductiveMode() {
		return isProductiveMode;
	}

	@Override
	public boolean failFastOnLockingAttempt() {
		return failFastOnLockAttempt;
	}
}