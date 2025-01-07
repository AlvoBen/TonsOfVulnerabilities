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
package com.sap.engine.services.deploy.server.properties;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.naming.InitialContext;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.cluster.monitor.ClusterMonitor;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.load.LoadContext;
import com.sap.engine.frame.core.locking.ServerInternalLocking;
import com.sap.engine.frame.core.monitor.CoreMonitor;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.services.configuration.appconfiguration.impl.ApplicationConfigHandlerFactoryImpl;
import com.sap.engine.services.configuration.jta.DistributedConfigurationHandlerFactory;
import com.sap.engine.services.deploy.container.Component;

/**
 * This class is used to store the deploy service context.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public abstract class PropManager {

	public static final String SERVER_INTERNAL_LOCKING_ID = "$services.deploy";
	public static final String APPLICATION_RESYNCH = "Application_Resynch";
	public static final String APPLICATION_INITIALIZER_THREADS = "Application_Initializer_Threads";
	public static final String APPLICATION_INITIALIZER_TIMEOUT = "Application_Initializer_Timeout_In_Secs";
	public static final String DEPLOY_BOOTSTRAPPER_THREADS = "Deploy_Bootstrapper_Threads";
	public static final String DEPLOY_BOOTSTRAPPER_TIMEOUT = "Deploy_Bootstrapper_Timeout_In_Secs";

	public static final int ADDITIONAL_DEBUG_INFO_DEFAULT = 0;
	public static final int ADDITIONAL_DEBUG_INFO_TIME_STATS = 1;
	public static final int ADDITIONAL_DEBUG_INFO_ALL_STATS = 2;

	private static PropManager INSTANCE;

	protected PropManager() {
	}

	/**
	 * This method is called by PropManagerFactory before any calls to
	 * <code>getInstance()</code>. It has default access level, and is not
	 * intended to be called directly by the clients.
	 * 
	 * @param instance
	 *            the currently used PropManager instance.
	 */
	static void setInstance(PropManager instance) {
		INSTANCE = instance;
	}

	/**
	 * This method is called after setInstance. No need for synchronization.
	 * 
	 * @return the currently used PropManager instance. Not null.
	 */
	public static PropManager getInstance() {
		// check postconditions.
		assert INSTANCE != null;
		return INSTANCE;
	}

	public abstract String getServiceName();

	public abstract boolean isAdditionalDebugInfo();

	public abstract int getAdditionalDebugInfo();

	public abstract boolean isClearAfterFailure();

	public abstract ServerState getServerState();

	public abstract boolean isStrictJ2eeChecks();

	public abstract boolean isApplicationResynch();
	
	/**
	 * Specifies if the child thread will be able to obtain locks for the 
	 * parent thread or will fails fast during the lock attempt.
	 * @return <tt>false</tt> if every child thread will be able to obtain the
	 * locks for its parent or <tt>true</tt> if the lock attempt will fail 
	 * fast. The threads started via DeployCommunicator.execute() will always
	 *  be able to obtain the locks from its parents.
	 */
	public abstract boolean failFastOnLockingAttempt();

	/**
	 * Check whether transactional operations are supported by deploy service.
	 * 
	 * @return true if the transactional operations are supported.
	 */
	public abstract boolean isTxOperationSupported();

	/**
	 * @return true if the new reference resolver is used.
	 * @deprecated this method will be removed as soon, as the logic for the new
	 *             reference resolving is enough mature.
	 */
	public abstract boolean useNewReferenceResolver();

	/**
	 * @return <tt>true<tt> if the <b>SDA's</b> (Software Deployment Archive)
	 * <b>META_INF/SAP_MANIFEST.MF</b> file persisted in the DB application
	 * configuration, is readable.
	 */
	public abstract boolean isSapManifestReadable();

	/**
	 * @return <tt>true<tt> if the <b>META_INF/application.xml</b> deployment
	 *         descriptor file stored in the DB application configuration, is
	 *         readable.
	 */
	public abstract boolean isDdReadable();

	public abstract String getAppsWorkDir();

	/**
	 * @return the canonical path to deploy service work directory. The
	 *         canonical path is unique and absolute.
	 */
	public abstract String getServiceWorkDir();

	/**
	 * @return The standard references for every application.
	 */
	public abstract List<Component> getStandardAppRefs();

	/**
	 * @return an ordered set of interface names, which have not providing
	 *         service.
	 */
	public abstract Set<String> getInterfacesWithoutProvider();

	public abstract String[] getStandardJEELibraryNames();

	public abstract int getClElemID();

	public abstract String getClElemName();

	public abstract long getTimeout4StartInitially();

	public abstract long getTimeout4LibraryLoadedEvent();

	/**
	 * @return the timeout in ms to obtain the needed local lock. The default
	 *         value is 1200000 ms (20 minutes).
	 */
	public abstract long getTimeout4LocalLock();

	public abstract long getTimeout4RemoteOperation();

	public abstract int getTimeout4BootstrapLock();

	public abstract long getTimeout4FinalAppStatus();

	public abstract ApplicationServiceContext getAppServiceCtx();

	public abstract ConfigurationHandlerFactory getConfigurationHandlerFactory();

	public abstract DistributedConfigurationHandlerFactory getDistributedConfigurationHandlerFactory();

	public abstract ApplicationConfigHandlerFactoryImpl getApplicationConfigHandlerFactoryImpl();

	public abstract InitialContext getInitialContext();

	public abstract CoreMonitor getCoreMonitor();

	public abstract ClusterMonitor getClusterMonitor();

	public abstract LoadContext getLoadContext();

	public abstract ServerInternalLocking getServerInternalLocking();

	public abstract ThreadSystem getThreadSystem();

	public abstract Properties getServiceProperties();

	public abstract Date getDateFromDB();

	public abstract boolean getCheckReferenceCycles();

	public abstract int getApplicationInitializerThreads();

	public abstract long getApplicationInitializerTimeout();

	public abstract int getDeployBootstrapperThreads();

	public abstract long getDeployBootstrapperTimeout();

	public abstract String getAnnotationClassloaderName();

	public abstract String[] getExcludedJLinEETests();

	public abstract int getInitialApplicationStartThreads();

	public abstract long getParallelStartTimeout();

	public abstract int getApplicationsStopThreads();

	/**
	 * @return true if ShmComponent and Thread Task Reporting are switched off.
	 */
	public abstract boolean isBoostPerformance();

	/**
	 * @return true, if we use The First Wins Strategy for public resources
	 *         registration.
	 */
	public abstract boolean firstWins();

	/**
	 * Marks productive and development mode, where development one is more
	 * restrictive and throws exceptions, but productive one tries to recover.
	 * 
	 * @return true in productive mode, otherwise false
	 */
	public abstract boolean isProductiveMode();
}
