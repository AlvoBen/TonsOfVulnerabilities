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

import java.util.List;

import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.NodeRemoveException;
import com.sap.engine.lib.refgraph.ReferenceGraph;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceImpl;
import com.sap.tc.logging.Location;

/**
 * @author Assia Djambazova
 */
public class ParallelApplicationStopTraverser extends ParallelTraverser {

	private static final Location location = 
		Location.getLocation(ParallelApplicationStopTraverser.class);
	
	public ParallelApplicationStopTraverser(
			ReferenceGraph<Component> graphWithApplicationToBeStopped,
			DeployServiceImpl deploy) {
		super(graphWithApplicationToBeStopped, deploy);
		nextApplications = graphWithApplications.getNodesWithNoReferencesFrom();
		for (Component node : nextApplications) {
			if (location.beDebug()) {
				DSLog.traceDebug(location, "Applications that are in the first batch are : [{0}]",
						node.getName());
			}
		}
	}

	/**
	 * This method is called when an application is stopped successfully.
	 * Removes from the graph the application node and the edges, that contain
	 * the application.
	 * 
	 * @param app
	 *            The component of the application
	 */
	public synchronized void success(final Component app) {
		if (location.beDebug()) {
			DSLog.traceDebug(location, "Stop of application [{0}] is successful",
					app.getName());
		}
		try {
			for (Edge<Component> edge : graphWithApplications
					.getReferencesToOthersFrom(app)) {
				if (edge != null) {
					graphWithApplications.remove(edge);
					if (location.beDebug()) {
						DSLog
								.traceDebug(
										location,
										"Has removed from the graph edge: from [{0}] to [{1}]",
										edge.getFirst().getName(),
										edge
												.getSecond().getName());
					}
				}
			}
			if (graphWithApplications.containsNode(app)) {
				graphWithApplications.remove(app);
				if (location.beDebug()) {
					DSLog.traceDebug(location, "Has removed from the graph node: [{0}]",
							app
									.getName());
				}
			}
		} catch (NodeRemoveException n) {
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.006387",
					"Exception on remove node in reference graph", n);
		} catch (RuntimeException re) {
			DSLog
					.logErrorThrowable(location,
										"ASJ.dpl_ds.000323",
										"Unexpected Major Error during operation with the graph: stop success of application [{0}]",
										re, app);
		} finally {
			getApplicationsWithNoReferences();
		}
	}

	/**
	 * This method is called when the stop of the application has failed.
	 * Removes from the graph the application node and the edges, that contain
	 * the application.
	 * 
	 * @param app
	 *            The component of the application
	 */
	public synchronized void fail(Component app) {
		success(app);
	}

	public List<Component> getNextApplications() {
		return graphWithApplications.getNodesWithNoReferencesFrom();
	}

	/**
	 * Method that stops an application.
	 * 
	 * @param appName
	 *            The name of the application that has to be stopped
	 * @return <code>true</code> if the application is stopped successfully
	 * @throws <code>DeploymentException</code> if the stop fails
	 */
	public boolean execute(Component app) throws DeploymentException {
		deploy.stopApplicationLocalAndWait(app.getName(), null);
		return true;
	}

	/**
	 * Method that gives the name of the current
	 * 
	 * @return <code>String</code>: Deploy Parallel Stop Thread
	 */
	protected String getThreadName() {
		return DeployConstants.DEPLOY_PARALLEL_STOP_THREAD_NAME;
	}

}
