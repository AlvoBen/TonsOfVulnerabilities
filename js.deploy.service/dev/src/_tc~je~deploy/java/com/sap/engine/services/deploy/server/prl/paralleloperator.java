/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.prl;

import java.util.HashSet;
import java.util.Set;

import com.sap.engine.lib.refgraph.ReferenceGraph;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DeployServiceImpl;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.GraphBuilder;
import com.sap.tc.logging.Location;

/**
 * @author Assia Djambazova
 */
public final class ParallelOperator {

	private static final Location location = 
		Location.getLocation(ParallelOperator.class);
	
	public DeployServiceImpl deploy;

	public ParallelOperator(DeployServiceImpl deploy) {
		this.deploy = deploy;
	}

	/**
	 * Will try to start all applications for specified time in specified
	 * threads.
	 * 
	 * @param applicationNames
	 *            The names of the applications that should be started.
	 * @param numberOfThreads
	 *            Number of threads in which the applications will be started
	 * @param timeOut
	 *            Timeout in ms.
	 * @throws <Code>ServerDeploymentException</code> if the <code>Graph</code>
	 *         with all applications on the engine has a cycle
	 * @throws <code>IllegalThreadStateException</code> if the operation has
	 *         taken more time than the expected timeout
	 */
	public void parallelStart(Set<Component> applicationNames,
			int numberOfThreads, long timeOut) throws ServerDeploymentException {
		operateParallel(new ParallelApplicationStrartTraverser(
				new GraphBuilder(applicationNames, false).getGraph(), deploy),
				numberOfThreads, timeOut);
	}

	/**
	 * Will try to stop all applications for specified time in specified
	 * threads.
	 * 
	 * @param applicationNames
	 *            The names of the applications that should be stopped
	 * @param numberOfThreads
	 *            Number of threads in which the applications will be started
	 * @param timeOut
	 *            Timeout in ms.
	 * @throws <Code>ServerDeploymentException</code> if the <code>Graph</code>
	 *         with all applications on the engine has a cycle
	 * @throws <code>IllegalThreadStateException</code> if the operation has
	 *         taken more time than the expected timeout
	 */
	public void parallelStop(Set<Component> applicationNames,
			int numberOfThreads, long timeOut) throws ServerDeploymentException {
		operateParallel(new ParallelApplicationStopTraverser(new GraphBuilder(
				applicationNames, true).getGraph(), deploy), numberOfThreads,
				timeOut);
	}

	/**
	 * Method used for the stop of all started applications. Used during engine
	 * stop.
	 */
	public void finalParallelApplicationStop() {
		try {
			long startToWait = System.currentTimeMillis();
			try {
				operateParallel(new ParallelApplicationStopTraverser(
						getAllApplicationNamesWhichHasToBeStoppedAsAGraph(),
						deploy), PropManager.getInstance()
						.getApplicationsStopThreads(), PropManager
						.getInstance().getParallelStartTimeout());
			} catch (IllegalThreadStateException e) {
				throw new IllegalThreadStateException(
						"Parallel Stop of applications TIMED OUT after "
								+ (System.currentTimeMillis() - startToWait)
								+ " ms waiting the started threads to finish their work.");
			}
		} catch (ServerDeploymentException e) {
			DSLog.logErrorThrowable(
									location, 
									"ASJ.dpl_ds.000340",
									"Error during final stop of applications", 
									e);
		}
		if (location.beDebug()) {
			DSLog.traceDebug(
							location, 
							"Parallel Stop: all [{0}] started threads finished.",
							PropManager.getInstance().getApplicationsStopThreads());
		}
	}

	/**
	 * Method used for the initial application start. Called during start-up of
	 * the engine.
	 * 
	 * @throws <code>ServerDeploymentException</code> if the <code>Graph</code>,
	 *         builded from the given applications, has a cycle
	 */
	public void initialParallelApplicationStart(Set<Component> applicationNames)
			throws ServerDeploymentException {
		long startTime = System.currentTimeMillis();
		try {
			operateParallel(new ParallelApplicationStrartTraverser(
					new GraphBuilder(applicationNames, false).getGraph(),
					deploy), PropManager.getInstance()
					.getInitialApplicationStartThreads(), PropManager
					.getInstance().getParallelStartTimeout());
		} catch (IllegalThreadStateException e) {
			throw new IllegalThreadStateException(
					"ASJ.dpl_ds.006201 Initial start of applications TIMED OUT after "
							+ (System.currentTimeMillis() - startTime)
							+ " ms. waiting the started threads to finish their work");
		}
		if (location.beDebug()) {
			DSLog.traceDebug(
							location, 
							"Parallel Start: all [{0}] started threads finished.",
							PropManager.getInstance()
									.getInitialApplicationStartThreads());
		}
	}

	private void operateParallel(ParallelTraverser traverse,
			int numberOfThreads, long timeout)
			throws IllegalThreadStateException {
		Runnable parallelThread;
		final Set<Runnable> callbackMonitor = new HashSet<Runnable>();

		synchronized (callbackMonitor) {
			try {
				for (int i = 1; i <= numberOfThreads; i++) {
					parallelThread = new ParallelApplicationThread(i,
							callbackMonitor, traverse);
					callbackMonitor.add(parallelThread);
					// Starting threads have to be system threads,
					// because only they have needed authorization.
					PropManager.getInstance().getThreadSystem().startThread(
							parallelThread, null,
							traverse.getThreadName() + " " + i, true, true);
				}
			} finally {
				try {
					long startToWait = System.currentTimeMillis(), waitedTime = -1;
					while (callbackMonitor.size() > 0) {
						if (location.beDebug()) {
							DSLog
									.traceDebug(
												location, 
												"Will wait [{0}] from [{1}] started threads to finish.",
												callbackMonitor.size(),
												numberOfThreads);
						}
						callbackMonitor.wait(timeout);
						waitedTime = System.currentTimeMillis() - startToWait;
						if (waitedTime >= timeout) {
							throw new IllegalThreadStateException();
						}
					}
				} catch (InterruptedException iEx) {
					DSLog.logErrorThrowable(
											location, 
											"ASJ.dpl_ds.006392",
											"Thread interrupted on waiting started threads to finish", 
											iEx);
				}
			}
		}
	}

	private ReferenceGraph<Component> getAllApplicationNamesWhichHasToBeStoppedAsAGraph()
			throws ServerDeploymentException {
		final String[] appNames = deploy.listJ2EEApplications(null);
		if (appNames == null || appNames.length == 0) {
			return null;
		}
		Set<Component> applications = new HashSet<Component>();

		for (int i = 0; i < appNames.length; i++) {
			if (deploy.getApplicationInfo(appNames[i]).getStatus() == Status.STARTED) {
				applications.add(
					new Component(appNames[i], Component.Type.APPLICATION));
			}
		}
		if (location.beDebug()) {
			DSLog
					.traceDebug(
								location, 
								"Following applications will be stopped during engine shutdown: [{0}]",
								applications);
		}
		return (new GraphBuilder(applications, true)).getGraph();
	}

}
