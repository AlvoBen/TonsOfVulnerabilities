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
package com.sap.engine.services.deploy.server.editor.impl;

import java.util.Iterator;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.ApplicationName;
import com.sap.engine.services.deploy.server.editor.DIReader;
import com.sap.engine.services.deploy.server.editor.EditorFactory;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.system.ThreadWrapper;
import com.sap.tc.logging.Location;

/**
 * This class is used for parallel read of deployment info from the database.
 * The end effect of execution of the run method is to add all applications from
 * the supplied iterator to the collection of started applications.
 * 
 * @see Applications
 * @author Anton Georgiev
 * @version 7.1
 */
public class DIReaderThread implements Runnable {

	private static final Location location = 
		Location.getLocation(DIReaderThread.class);
	
	private static final Object monitor = new Object();
	private static int count = 0;

	private final String thName;
	private final Iterator<ApplicationName> appNameOIter;
	private final Configuration appsConfig, deployConfig, customConfig;
	private final Set<DIReaderThread> callbackMonitor;
	private final Set<String> failed2ReadAppNames;

	public DIReaderThread(int id, Iterator<ApplicationName> appNameOIter,
			Configuration appsConfig, Configuration deployConfig,
			Configuration customConfig, Set<DIReaderThread> callbackMonitor,
			Set<String> failed2ReadAppNames) {
		this.thName = "ApplicationInitializer " + id;
		this.appNameOIter = appNameOIter;
		this.appsConfig = appsConfig;
		this.deployConfig = deployConfig;
		this.customConfig = customConfig;
		this.callbackMonitor = callbackMonitor;
		this.failed2ReadAppNames = failed2ReadAppNames;
		DSLog.traceDebug(
						location, 
						"[{0}] was created successfully.", 
						thName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			while (true) {
				ApplicationName appNameO = null;
				try {
					synchronized (monitor) {
						if (appNameOIter.hasNext()) {
							appNameO = appNameOIter.next();
							count++;
						} else {
							DSLog.traceDebug(location, 
											"[{0}] will break, because there are no more applications, which deployment info to be read from DB.",
											thName);
							break;
						}
					}
					DSLog.traceDebug(location, 
									"[{0}] will read the deployment info of [{1}] application with number [{2}].",
									thName, appNameO, count);

					// >>>>>>>>>>>> PROCESS SINGLE APPS - START <<<<<<<<<<<< //
					readDI(appNameO);
					// >>>>>>>>>>>> PROCESS SINGLE APPS - END <<<<<<<<<<<< //

				} catch (OutOfMemoryError oom) {
					throw oom;
				} catch (ThreadDeath td) {
					throw td;
				} catch (Throwable ex) {
					failed2ReadAppNames.add(appNameO.getProvider() + "/"
							+ appNameO.getName());
					final ServerDeploymentException sde;
					if (ex instanceof ServerDeploymentException) {
						sde = (ServerDeploymentException) ex;
					} else {
						sde = new ServerDeploymentException(
								ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
								new String[] { "reading application ["
										+ appNameO.getProvider() + "/"
										+ appNameO.getName() + "]" }, ex);
						sde.setMessageID("ASJ.dpl_ds.005082");
					}
					DSLog.logErrorThrowable(
											location, 
											"ASJ.dpl_ds.006379",
											"Error in reading DeploymentInfo", 
											sde);
				}
			}
		} finally {
			synchronized (callbackMonitor) {
				DSLog.traceDebug(
								location, 
								"[{0}] will notify all threads waiting on [{1}] monitor.",
								thName, callbackMonitor);
				callbackMonitor.remove(this);
				callbackMonitor.notifyAll();
			}
		}
	}

	private void readDI(ApplicationName appNameO)
			throws ConfigurationException, ServerDeploymentException {
		// push task
		if (!PropManager.getInstance().isBoostPerformance()) {
			ThreadWrapper.pushTask(
					"Service [deploy] is reading deployment info for application ["
							+ appNameO.getApplicationName() + "]",
					ThreadWrapper.TS_PROCESSING);
		}
		// read DI
		try {
			final Configuration providerConfig = appsConfig
					.getSubConfiguration(appNameO.getProvider());
			final Configuration deployProviderConfig = deployConfig
					.getSubConfiguration(appNameO.getProvider());

			Configuration customProviderConfig = null;
			if (customConfig != null
					&& customConfig.existsSubConfiguration(appNameO
							.getProvider())) {
				customProviderConfig = customConfig
						.getSubConfiguration(appNameO.getProvider());
			}

			final Configuration appCfg = providerConfig
					.getSubConfiguration(appNameO.getName());
			final Configuration deployAppConfig = deployProviderConfig
					.getSubConfiguration(appNameO.getName());
			Configuration customAppConfig = null;
			if (customProviderConfig != null
					&& customProviderConfig.existsSubConfiguration(appNameO
							.getName())) {
				customAppConfig = customProviderConfig
						.getSubConfiguration(appNameO.getName());
			}
			final DIReader diReader = EditorFactory.getInstance().getDIReader(
					appCfg);
			final DeploymentInfo dInfo = diReader.readDI(appNameO.getProvider()
					+ "/" + appNameO.getName(), appCfg, deployAppConfig,
					customAppConfig);
			Applications.add(dInfo);
		} finally {
			// pop task
			if (!PropManager.getInstance().isBoostPerformance()) {
				ThreadWrapper.popTask();
			}
		}
	}
}
