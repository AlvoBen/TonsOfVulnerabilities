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
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.TransactionCommunicator;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.editor.DIReader;
import com.sap.engine.services.deploy.server.editor.EditorFactory;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.ConfigUtils;
import com.sap.engine.services.deploy.timestat.TransactionTimeStat;
import com.sap.engine.system.ThreadWrapper;
import com.sap.tc.logging.Location;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class DIBootstrapperThread implements Runnable {

	private static final Location location = 
		Location.getLocation(DIBootstrapperThread.class);
	
	private static final Object monitor = new Object();
	private static int count = 0;

	private final Iterator<String> appIter;
	private final TransactionCommunicator communicator;
	private final ConfigurationHandler handler;
	private final Set<DIBootstrapperThread> callbackMonitor;
	private final Set<String> failed2BootstrapAppNames;

	private TransactionTimeStat timeStat = null;
	private Configuration appConfig = null;
	private DIReader diReader = null;
	private DeploymentInfo dInfo = null;

	public DIBootstrapperThread(int id, Iterator<String> appIter,
			TransactionCommunicator communicator, ConfigurationHandler handler,
			Set<DIBootstrapperThread> callbackMonitor,
			Set<String> failed2BootstrapAppNames) {
		this.appIter = appIter;
		this.communicator = communicator;
		this.handler = handler;
		this.callbackMonitor = callbackMonitor;
		this.failed2BootstrapAppNames = failed2BootstrapAppNames;
		if (location.beDebug()) {
			DSLog.traceDebug(location, "[ "
					+ DeployConstants.DEPLOY_APPBIN_THREAD + id
					+ "] was created successfully.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			while (true) {
				String appName = null;
				timeStat = null;
				final String tagName = "Bootstrap Single Application";
				Accounting.beginMeasure(tagName, DIBootstrapperThread.class);
				try {
					synchronized (monitor) {
						if (appIter.hasNext()) {
							appName = (String) appIter.next();
							count++;
						} else {
							if (location.beDebug()) {
								DSLog.traceDebug(
												location, 
												"[{0}] will break, because there are not more applications, which binaries to be downloaded from DB.",
												Thread.currentThread()
														.getName());
							}
							break;
						}
					}

					timeStat = TransactionTimeStat.createIfNotAvailable(
							"Bootstrap Single Application", appName);

					// >>>>>>>>>>>> PROCESS SINGLE APPS - START <<<<<<<<<<<< //
					bootstrapApp(appName);
					// >>>>>>>>>>>> PROCESS SINGLE APPS - END <<<<<<<<<<<< //

				} catch (OutOfMemoryError oom) {
					throw oom;
				} catch (ThreadDeath td) {
					throw td;
				} catch (Throwable ex) {
					failed2BootstrapAppNames.add(appName);
					final ServerDeploymentException sde;
					if (ex instanceof ServerDeploymentException) {
						sde = (ServerDeploymentException) ex;
					} else {
						sde = new ServerDeploymentException(
								ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
								new String[] { "downloading application binaries ["
										+ appName + "]" }, ex);
						sde.setMessageID("ASJ.dpl_ds.005082");
					}					
					DSLog.logErrorThrowable(
											location, 
											"ASJ.dpl_ds.006378", 
											"Error in application bootstrap", 
											sde);
				} finally {
					Accounting.endMeasure(tagName);
					if (timeStat != null) {
						timeStat.finish();
					}
				}
			}
		} finally {
			synchronized (callbackMonitor) {
				if (location.beDebug()) {
					DSLog.traceDebug(
									location, 
									"[{0}] will notify all threads waiting on [{1}] monitor.",
									Thread.currentThread().getName(),
									callbackMonitor);
				}
				callbackMonitor.remove(this);
				callbackMonitor.notifyAll();
			}
		}
	}

	private void bootstrapApp(String appName) throws ServerDeploymentException {
		// push task
		if ( ! PropManager.getInstance().isBoostPerformance()) {
			ThreadWrapper.pushTask(
					"Service [deploy] is bootstrapping binaries for application ["
							+ appName + "]", ThreadWrapper.TS_PROCESSING);
		}
		// bootstrap app
		try {
			if (location.beDebug()) {
				DSLog
						.traceDebug(
								location, 
								"[{0}] will download the binaries of [{1}] application with number [{2}].",
								Thread.currentThread().getName(), appName,
								count);
			}

			appConfig = ConfigUtils.openConfiguration(handler,
					DeployConstants.ROOT_CFG_APPS + "/" + appName,
					ConfigurationHandler.READ_ACCESS, Thread.currentThread()
							.getName()
							+ appName);

			dInfo = communicator.getApplicationInfo(appName);

			diReader = EditorFactory.getInstance().getDIReader(appConfig);
			diReader.bootstrapApp(appConfig, communicator, dInfo, Thread
					.currentThread().getName());
		} finally {
			// pop task
			if ( ! PropManager.getInstance().isBoostPerformance()) {
				ThreadWrapper.popTask();
			}
		}
	}

}
