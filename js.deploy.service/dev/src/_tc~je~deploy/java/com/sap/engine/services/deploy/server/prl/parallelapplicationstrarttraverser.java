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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sap.engine.lib.refgraph.CyclicReferencesException;
import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.NodeRemoveException;
import com.sap.engine.lib.refgraph.ReferenceGraph;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.op.util.StatusDescriptionsEnum;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceImpl;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.tc.logging.Location;

/**
 * @author Elena Yaneva, Assia Djambazova
 */
public class ParallelApplicationStrartTraverser extends ParallelTraverser {

	private static final Location location = 
		Location.getLocation(ParallelApplicationStrartTraverser.class);
	
	public ParallelApplicationStrartTraverser(
			ReferenceGraph<Component> graphWithApplicationToBeStarted,
			DeployServiceImpl deploy) {
		super(graphWithApplicationToBeStarted, deploy);
		nextApplications = graphWithApplicationToBeStarted
				.getNodesWithNoReferencesTo();
		if (location.beDebug()) {
			StringBuffer sb = new StringBuffer(
					"Applications that are in the first batch are : ");
			for (Component node : nextApplications) {
				sb.append(node).append(", ");
			}
			DSLog.traceDebug(location, sb.toString());
		}
	}

	/**
	 * The method is called, when an application has been started successfully
	 * Removes from the graph the application node for the started application
	 * and the edges, that contain the application Calls the
	 * getApplicationsWithNoReferences method.
	 * 
	 * @param app
	 *            The component of the application, that has been started
	 */
	@Override
	public synchronized void success(Component app) {
		if (location.beDebug()) {
			DSLog.traceDebug(
							location, 
							"Start of application [{0}] is successful", 
							app.getName());
		}
		try {
			for (Edge<Component> edge : graphWithApplications
					.getReferencesFromOthersTo(app)) {
				if (edge != null) {
					graphWithApplications.remove(edge);
					if (location.beDebug()) {
						DSLog
								.traceDebug(
											location,
											"Has removed from the start-up graph, the following edge: from [{0}] to [{1}]",
											edge.getFirst().getName(), edge
													.getSecond().getName());
					}
				}
			}
			if (graphWithApplications.containsNode(app)) {
				graphWithApplications.remove(app);
				if (location.beDebug()) {
					DSLog
							.traceDebug(
										location, 
										"Has removed from the start-up graph, the following node: [{0}]",
										app.getName());
				}
			}
		} catch (NodeRemoveException n) {
			DSLog
					.logErrorThrowable(
										location, 
										"ASJ.dpl_ds.006390",
										"Exception on node remove in ref graph for successfully started application",
										n);
		} catch (RuntimeException re) {
			DSLog
					.logErrorThrowable(
										location, 
										"ASJ.dpl_ds.000329",
										"Unexpected Major Error during operation with the graph: start success of application [{0}]",
										re, app.getName());
		} finally {
			getApplicationsWithNoReferences();
		}
	}

	/**
	 * The method is called, when the start of an application fails. Removes the
	 * node from the graph. Calls recursively fail for all the application, that
	 * have hard dependency on the failed one. Sets the state of the depending
	 * applications to IMPLICIT_STOPPED If the dependency is weak, only the
	 * failed applications and its edges are removed from the graph.
	 * 
	 * @param app
	 *            The application, which start has failed
	 */
	@Override
	public synchronized void fail(Component app) {
		if (location.beDebug()) {
			DSLog.traceDebug(
							location, 
							"Start of application [{0}] has failed", 
							app.getName());
		}
		final List<Component> sortedApps;
		try {
			sortedApps = graphWithApplications.sortBackwardHard(app);
		} catch (CyclicReferencesException cre) {
			throw new AssertionError(cre.getLocalizedMessage());
		}
		DeploymentInfo dInfo = Applications.get(app.getName());

		for (Component predecessor : sortedApps) {
			for (Edge<Component> ref : graphWithApplications
				.getReferencesToOthersFrom(predecessor)) {
				graphWithApplications.remove(ref);
				if (location.beDebug()) {
					DSLog
							.traceDebug(
									location, 
									"Has removed from the start-up graph edge from [{0}] to [{1}]",
									ref.getFirst().getName(), 
									ref.getSecond().getName());
				}
			}

			for (Edge<Component> ref : graphWithApplications
					.getReferencesFromOthersTo(predecessor)) {
				graphWithApplications.remove(ref);
				if (location.beDebug()) {
					DSLog
							.traceDebug(
									location, 
									"Has removed from the start-up graph edge from [{0}] to [{1}] with type [{2}]",
									ref.getFirst().getName(), 
									ref.getSecond().getName(), 
									ref.getType());
				}
			}
			if (!predecessor.equals(app)) {
				// predecessor should always be deployed, because of the graph
				// building checks
				Applications
						.get(predecessor.getName())
						.setStatus(
								Status.IMPLICIT_STOPPED,
								StatusDescriptionsEnum.IMPLICIT_STOPPED_AS_START_AS_REFERRED_FAILED,
								new Object[] { app.getName(), Edge.Type.HARD });
			}
			try {
				if (graphWithApplications.containsNode(predecessor)) {
					graphWithApplications.remove(predecessor);
					if (location.beDebug()) {
						DSLog
								.traceDebug(
											location, 
											"Has removed from the start-up graph node [{0}]",
											predecessor);
					}
				}
			} catch (NodeRemoveException nre) {
				DSLog
						.logErrorThrowable(
								location,
								"ASJ.dpl_ds.006389",
								"Exception on node remove in ref graph for failed to start application",
								nre);
			} catch (RuntimeException re) {
				DSLog
						.logErrorThrowable(
								location, 
								"ASJ.dpl_ds.000334",
								"Unexpected Major Error during operation with the graph: startFail of application: [{0}]",
								re, app.getName());
			}
		}
		getApplicationsWithNoReferences();
	}

	/**
	 * The method gets all applications that are eligible to be started next.
	 */
	@Override
	protected synchronized List<Component> getNextApplications() {
		return graphWithApplications.getNodesWithNoReferencesTo();
	}

	/**
	 * Method that starts an application.
	 * 
	 * @param app
	 *            The component of the application that has to be started
	 * @return true if the application is started successfully
	 * @throws <code>DeploymentException</code> if the start fails
	 */
	@Override
	public boolean execute(Component app) throws DeploymentException {
		DeploymentInfo dInfo = Applications.get(app.getName());
		if (dInfo != null) {
			dInfo.setStatusDescription(
					StatusDescriptionsEnum.INITIALLY_STARTING,
					initLazyContainersNames(app.getName(), dInfo));
		}
		// TODO @Assia return false if the application is not deployed and
		// remove all successors
		deploy.startApplicationLocalAndWait(app.getName(), null);
		// returns true if an Exception isn't thrown before that
		return true;
	}

	// Private Method that constructs the object array with the containers
	// supporting lazy start
	// The array should be added to the status description
	private Object[] initLazyContainersNames(String appName,
			DeploymentInfo dInfo) {
		// TODO optimize
		String res = "";
		final String[] containerNames = dInfo.getContainerNames();
		ArrayList<String> nonLazyContainers = new ArrayList<String>();
		if (containerNames != null) {
			ContainerInterface cIntf = null;
			for (int i = 0; i < containerNames.length; i++) {
				cIntf = Containers.getInstance()
						.getContainer(containerNames[i]);
				if (cIntf == null
						|| !cIntf.getContainerInfo().isSupportingLazyStart()
						|| !cIntf.getContainerInfo().isSupportingLazyStart(
								appName)) {
					nonLazyContainers.add(containerNames[i]);
				}
			}

			if (!nonLazyContainers.isEmpty()) {
				res = " Non-lazy containers: ";
				for (Iterator<String> it = nonLazyContainers.iterator(); it
						.hasNext();) {
					res += it.next();
					if (it.hasNext()) {
						res += ", ";
					} else {
						res += ".";
					}
				}
			}
		}
		Object[] params = new String[1];
		params[0] = res;
		return params;
	}

	/**
	 * Method that gives the name of the current thread depending on its type
	 * 
	 * @return <code>String</code>: Deploy Parallel Start Thread
	 */
	@Override
	protected String getThreadName() {
		return DeployConstants.DEPLOY_PARALLEL_START_THREAD_NAME;
	}
}
