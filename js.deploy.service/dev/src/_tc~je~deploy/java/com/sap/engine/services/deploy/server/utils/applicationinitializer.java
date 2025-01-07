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
package com.sap.engine.services.deploy.server.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.NameAlreadyExistsException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.logging.DSLogConstants;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.module.ApplicationName;
import com.sap.engine.services.deploy.server.editor.DIReader;
import com.sap.engine.services.deploy.server.editor.EditorFactory;
import com.sap.engine.services.deploy.server.editor.impl.DIReaderThread;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.properties.impl.PropManagerImpl;
import com.sap.tc.logging.Location;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class ApplicationInitializer {

	private static final Location location = 
		Location.getLocation(ApplicationInitializer.class);

	private static final String PREFIX = "ApplicationInitializer: ";

	private static final long RETRY_AFTER = 1000 * 10;
	private static final byte RETRIES = 100;

	public ApplicationInitializer() {
	}

	public static void initializeApplications(ConfigurationHandler cfgHandler,
			Map<String, ReferenceObject[]> appName2ReferenceObjectAsArr,
			String clusterConfigApps) {
		byte counter = 0;
		while (true && (RETRIES >= counter)) {// read till success or time out
			try {// retry and count error
				try {// roll back in finally
					prepareDB4DSNeeds(cfgHandler);

					final Configuration config = ConfigUtils.openConfiguration(
							cfgHandler, DeployConstants.ROOT_CFG_APPS,
							ConfigurationHandler.READ_ACCESS,
							"initializing applications");
					final Configuration deployConfig = ConfigUtils
							.openConfiguration(cfgHandler,
									DeployConstants.ROOT_CFG_DEPLOY,
									ConfigurationHandler.READ_ACCESS,
									"initializing applications");
					Configuration customConfig = null;
					try {
						customConfig = cfgHandler.openConfiguration(
								clusterConfigApps,
								ConfigurationHandler.READ_ACCESS);
					} catch (NameNotFoundException cex) {
						// $JL-EXC$ - it's normal not be created for old
						// deployed applications
					} catch (ConfigurationException cex) {
						throwUnexpectedException("obtaining "
								+ clusterConfigApps
								+ " configuration for all applications", cex);
					}

					readApplicationsFromDB(config, deployConfig, customConfig);// fists
					readRefsOfNotDeployedApplicationsFromDB(deployConfig,
							appName2ReferenceObjectAsArr);// second
					break;
				} finally {
					ConfigUtils.rollbackHandler(cfgHandler,
							"initialize applications");
				}
			} catch (DeploymentException de) {
				counter++;
				DSLog.logErrorThrowable(
								location,
								"ASJ.dpl_ds.006394",
								"Error on reading applications or their references from DB",
								de);
				try {
					Thread.sleep(RETRY_AFTER);
				} catch (InterruptedException iex) {
					DSLog
							.traceDebugThrowable(
									location,
									null, iex,
									"ASJ.dpl_ds.000369",
									"Service [deploy] was interupted while waiting a remote server to finish writing in configuration.");
				}
			}
		}
	}

	private static void readRefsOfNotDeployedApplicationsFromDB(
			Configuration deployConfig,
			Map<String, ReferenceObject[]> appName2ReferenceObjectAsArr)
			throws DeploymentException {
		ValidateUtils.nullValidator(deployConfig, "deployConfig");

		long global = System.currentTimeMillis();
		String providerNames[] = null;
		try {
			providerNames = deployConfig.getAllSubConfigurationNames();
		} catch (ConfigurationException ce) {
			throwUnexpectedException("reading application data", ce);
		}
		if (providerNames == null) {
			return;
		}

		final Set<String> failed2ReadAppReferences = new HashSet<String>();
		Configuration deployProviderConfig = null;
		for (int i = 0; i < providerNames.length; i++) {
			if (providerNames[i] != null) {
				// references
				try {
					deployProviderConfig = deployConfig
							.getSubConfiguration(providerNames[i]);
				} catch (ConfigurationException ce) {
					throwUnexpectedException("reading application data from "
							+ providerNames[i] + " provider", ce);
				}
				initReferencesWithoutApplications(providerNames[i],
						deployProviderConfig, appName2ReferenceObjectAsArr,
						failed2ReadAppReferences);
			}
		}
		if (failed2ReadAppReferences.size() <= 0) {
			if (location.beDebug()) {
				DSLog.traceDebug(
								location,
								"The deploy service read from DB all the references of not deployed applications for [{0}] ms.",
								(System.currentTimeMillis() - global));
			}
		} else {
			DSLog.logWarning(
							location,
							"ASJ.dpl_ds.003007",
							"The deploy service was NOT able to read from DB all the references of not deployed applications for [{0}] ms, because the references in [{1}] configurations are wrong.",
							(System.currentTimeMillis() - global),
							failed2ReadAppReferences);
		}
		if (location.beDebug()) {
			DSLog.traceDebug(location, "The appName2ReferenceObjectAsArr = [{0}].",
					appName2ReferenceObjectAsArr);
		}
	}

	/**
	 * Read deployment info from the DB in parallel threads. The method waits
	 * for all started threads to finish. As result of its call, Applications
	 * map and reference graph are build.
	 * 
	 * @param appsConfig
	 * @param deployConfig
	 * @param customConfig
	 * @throws DeploymentException
	 */
	private static void readApplicationsFromDB(Configuration appsConfig,
			Configuration deployConfig, Configuration customConfig)
			throws DeploymentException {

		// validation
		ValidateUtils.nullValidator(appsConfig, "appsCfg");
		ValidateUtils.nullValidator(deployConfig, "deployConfig");

		// obtain provider names
		String providerNames[] = null;
		long global = System.currentTimeMillis();
		try {
			providerNames = appsConfig.getAllSubConfigurationNames();
		} catch (ConfigurationException ce) {
			throwUnexpectedException("reading application data", ce);
			return;
		}

		// build application names set
		final Set<ApplicationName> appNamesO = new HashSet<ApplicationName>();
		for (int i = 0; i < providerNames.length; i++) {
			if (providerNames[i] != null) {
				try {
					final Configuration providerConfig = appsConfig
							.getSubConfiguration(providerNames[i]);
					final String[] appNames = providerConfig
							.getAllSubConfigurationNames();
					if (appNames != null) {
						for (int j = 0; j < appNames.length; j++) {
							if (appNames[j] != null) {
								appNamesO.add(new ApplicationName(
										providerNames[i], appNames[j]));
							}
						}
					}
				} catch (ConfigurationException ce) {
					throwUnexpectedException("reading application data from "
							+ providerNames[i] + " provider", ce);
				}
			}
		}
		if (location.bePath()) {
			DSLog.tracePath(
							location,
							"{0}Start reading deployment info of deployed applications [{1}].",
							PREFIX, appNamesO);
		}
		global = System.currentTimeMillis();

		final Iterator<ApplicationName> appNameOIter = appNamesO.iterator();
		final Set<DIReaderThread> callbackMonitor = new HashSet<DIReaderThread>();
		final Set<String> failed2ReadAppNames = new HashSet<String>();
		boolean areAllThreadsStarted = false;
		synchronized (callbackMonitor) {
			try {
				// start all threads to process application names set in
				// parallel.
				for (int i = 1; i <= PropManager.getInstance()
						.getApplicationInitializerThreads(); i++) {
					final DIReaderThread dirThread = new DIReaderThread(i,
							appNameOIter, appsConfig, deployConfig,
							customConfig, callbackMonitor, failed2ReadAppNames);
					callbackMonitor.add(dirThread);
					PropManager.getInstance().getThreadSystem().startThread(
							dirThread, null,
							DeployConstants.DEPLOY_DI_THREAD + " " + i, true,
							true); // reads from DB
				}
				areAllThreadsStarted = true;
			} finally {
				// wait for threads to finish.
				try {
					long startToWait = System.currentTimeMillis();
					long waitedTime = -1;
					long timeout = PropManager.getInstance()
							.getApplicationInitializerTimeout();
					while (callbackMonitor.size() > 0) {
						if (location.beDebug()) {
							DSLog.traceDebug(
											location,
											"{0}will wait [{1}] from [{2}] started threads to finish on [{3}] monitor.",
											PREFIX,
											callbackMonitor.size(),
											PropManager
													.getInstance()
													.getApplicationInitializerThreads(),
											callbackMonitor);
						}
						callbackMonitor.wait(timeout);
						waitedTime = System.currentTimeMillis() - startToWait;
						if (waitedTime >= timeout) {
							throw new IllegalThreadStateException(
									"ASJ.dpl_ds.006096 The application initialization TIMED OUT after "
											+ waitedTime
											+ " ms waiting the started threads to finish their work. The issue might be resolved by reconfiguring the "
											+ PropManagerImpl.APPLICATION_INITIALIZER_THREADS
											+ " and "
											+ PropManagerImpl.APPLICATION_INITIALIZER_TIMEOUT
											+ " properties of deploy service.");
						}
					}
					if (location.beDebug()) {
						DSLog.traceDebug(
								location,
								"{0}all [{1}] started threads finished.",
								PREFIX, PropManager.getInstance()
										.getApplicationInitializerThreads());
					}

				} catch (InterruptedException iEx) {
					throwUnexpectedException("reading application data", iEx);
				} finally {
					if (failed2ReadAppNames.size() == 0
							&& callbackMonitor.size() == 0
							&& areAllThreadsStarted) {
						if (location.beDebug()) {
							DSLog.traceDebug(
											location,
											"The deploy service read from DB the deployment infos of all applications for [{0}] ms.",
											(System.currentTimeMillis() - global));
						}
					} else {
						DSLog.logWarning(
										location,
										"ASJ.dpl_ds.003008",
										"The deploy service was NOT able to read from DB the deployment infos of all applications for [{0}] ms {1}{2}{3}",
										(System.currentTimeMillis() - global),
										(failed2ReadAppNames.size() <= 0 ? "."
												: ", because applications ["
														+ failed2ReadAppNames
														+ "] are wrong, "),
										(callbackMonitor.size() <= 0 ? ""
												: "threads [" + callbackMonitor
														+ "] did not finish "),
										(areAllThreadsStarted ? "."
												: " and some threads failed to start."));
					}
				}
			}
		}
	}

	private static void initReferencesWithoutApplications(String providerName,
			Configuration deployProviderConfig,
			Map<String, ReferenceObject[]> appName2ReferenceObjectAsArr,
			Set<String> failed2ReadAppReferences)
			throws ServerDeploymentException {
		String deployNames[] = null;
		try {
			deployNames = deployProviderConfig.getAllSubConfigurationNames();
		} catch (ConfigurationException ce) {
			throwUnexpectedException("reading application data from "
					+ providerName + " provider", ce);
		}
		if (deployNames == null) {
			return;
		}

		DIReader diReader = null;
		ReferenceObject[] refObj = null;
		Configuration deplAppConfig = null;
		ApplicationName appNameO = null;
		for (int j = 0; j < deployNames.length; j++) {
			appNameO = new ApplicationName(providerName, deployNames[j]);
			if (Applications.get(appNameO.getApplicationName()) == null) {
				// this is a reference of an undeployed application.
				try {// The DIReader is created with deplAppConfig, there woun't
					// be a version field
					deplAppConfig = deployProviderConfig
							.getSubConfiguration(deployNames[j]);
					diReader = EditorFactory.getInstance().getDIReader(
							deplAppConfig);
					refObj = diReader.readReferences(deplAppConfig);
					if (location.beDebug()) {
						DSLog.traceDebug(
										location,
										"The references of not deployed application from [{0}] configuration are [{1}]",
										deplAppConfig.getPath(), refObj);
					}
					if (refObj != null && refObj.length > 0) {
						appName2ReferenceObjectAsArr.put(providerName + "/"
								+ deployNames[j], refObj);
					}
				} catch (OutOfMemoryError oom) {
					throw oom;
				} catch (ThreadDeath td) {
					throw td;
				} catch (Throwable ex) {
					failed2ReadAppReferences.add(deplAppConfig.getPath());
					final ServerDeploymentException sde;
					if (ex instanceof ServerDeploymentException) {
						sde = (ServerDeploymentException) ex;
					} else {
						sde = new ServerDeploymentException(
								ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
								new String[] { "reading the references of not deployed application from ["
										+ deplAppConfig.getPath()
										+ "] configuration." }, ex);
					}
					sde.setMessageID("ASJ.dpl_ds.005082");
					DSLog.logErrorThrowable(location, sde);
				}
			}
		}
	}

	private static void throwUnexpectedException(String message, Exception ex)
			throws ServerDeploymentException {
		final ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { message }, ex);
		sde.setMessageID("ASJ.dpl_ds.005082");
		throw sde;
	}

	/**
	 * This method ensures the existence of applications, deploy and global root
	 * configurations.
	 * 
	 * @param cfgHandler
	 *            configuration handler used to check or create the
	 *            configurations.
	 * @throws DeploymentException
	 */
	private static void prepareDB4DSNeeds(ConfigurationHandler cfgHandler)
			throws DeploymentException {
		final String[] allRoots;
		try {
			allRoots = cfgHandler.getAllRootNames();
		} catch (ConfigurationException cex) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_GET_ALL_ROOT_NAMES, cex);
			sde.setMessageID("ASJ.dpl_ds.005059");
			sde.setDcNameForObjectCaller(cfgHandler);
			throw sde;
		}
		boolean foundApps = false;
		boolean foundDeploy = false;
		if (allRoots != null) {
			for (int i = 0; i < allRoots.length; i++) {
				if (DeployConstants.ROOT_CFG_APPS.equals(allRoots[i])) {
					foundApps = true;
				} else if (DeployConstants.ROOT_CFG_DEPLOY.equals(allRoots[i])) {
					foundDeploy = true;
				}
			}
		}
		try {
			cfgHandler.createSubConfiguration(DeployConstants.GLOBAL_CONFIG);
		} catch (NameAlreadyExistsException naeex) {
			// $JL-EXC$ - in case this configuration does not exist.
		} catch (ConfigurationLockedException clex) {
			// $JL-EXC$ - in case this configuration is locked from another
			// server.
		} catch (ConfigurationException cex) {
			final ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "trying to create the ["
							+ DeployConstants.GLOBAL_CONFIG + "] configuration" },
					cex);
			sde.setMessageID("ASJ.dpl_ds.005082");
			DSLog.logErrorThrowable(location, sde);
		}

		if (!foundApps || !foundDeploy) {
			if (!foundApps) {
				ConfigUtils.createRootConfiguration(cfgHandler,
						DeployConstants.ROOT_CFG_APPS,
						"initializing applications");
			}
			if (!foundDeploy) {
				ConfigUtils.createRootConfiguration(cfgHandler,
						DeployConstants.ROOT_CFG_DEPLOY,
						"initializing applications");
			}
			ConfigUtils.commitHandler(cfgHandler, "initialize applications");
		}
	}
}