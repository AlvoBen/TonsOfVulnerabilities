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
import java.util.LinkedHashSet;
import java.util.Set;

import com.sap.engine.lib.refgraph.ReferenceGraph;
import com.sap.engine.lib.refgraph.impl.Graph;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DeployServiceImpl;
import com.sap.tc.logging.Location;

/**
 * 
 * @author Elena Yaneva, Assia Djambazova
 * 
 */

public abstract class ParallelTraverser {

	private static final Location location = 
		Location.getLocation(ParallelTraverser.class);
	
	ReferenceGraph<Component> graphWithApplications = new Graph<Component>();
	protected List<Component> nextApplications;
	protected Set<Component> visitedApplications = new LinkedHashSet<Component>();
	protected DeployServiceImpl deploy;

	public ParallelTraverser(ReferenceGraph<Component> graphWithApplications,
			DeployServiceImpl deploy) {
		if (graphWithApplications != null) {
			this.graphWithApplications = graphWithApplications;
		}
		this.deploy = deploy;
	}

	/**
	 * This method returns the next application, that is eligible for operation.
	 * Returns the next application in the nextApplications List. If
	 * nextApplications is empty the thread waits. Will be notified, when
	 * another application exits the success or fail methods. Returns null, if
	 * the graphWithApplicationToBeStarted is empty which means that are started
	 * all applications,that should be initially started
	 */
	public synchronized Component next() {
		while ((graphWithApplications.size() != 0)) {
			while (true) {
				if (nextApplications.iterator().hasNext()) {
					Component app = nextApplications.iterator().next();
					nextApplications.remove(app);
					if (!visitedApplications.contains(app) && (app != null)) {
						if (location.beDebug()) {
							DSLog.traceDebug(
											location,
											"The next method will return [{0}]", 
											app);
						}
						visitedApplications.add(app);
						return app;
					}
				} else {
					break;
				}
			}
			try {
				if (location.beDebug()) {
					DSLog
							.traceDebug(
									location, 
									"Waiting for an application to start or stop or fail and to add elements to the nextApplications");
					DSLog.traceDebug(
									location, 
									"The graph has [{0}] nodes",
									graphWithApplications.size());
				}
				wait();
			} catch (InterruptedException ie) {
				DSLog
						.logErrorThrowable(
											location,
											"ASJ.dpl_ds.006393",
											"Thread interrupted while waiting for an application to start or stop or fail",
											ie);
						}
		}
		notifyAll();
		return null;
	}

	/**
	 * This method is called when an operation with the given application has
	 * finished successfully. Removes from the graph the application node and
	 * the edges, that contain the application.
	 * 
	 * @param applicationName
	 *            The name of the application
	 */
	public abstract void success(Component applicationName);

	/**
	 * This method is called when an operation with the given application has
	 * failed. Removes from the graph the application node and the edges, that
	 * contain the application.
	 * 
	 * @param applicationName
	 *            The name of the application
	 */
	public abstract void fail(Component applicationName);

	/**
	 * The method gets all applications that are eligible for operation.
	 * Notifies all threads, that a new application has been added to the ready
	 * for start applications
	 */
	public synchronized void getApplicationsWithNoReferences() {
		try {
			nextApplications = this.getNextApplications();
			if (location.beDebug()) {
				DSLog
						.traceDebug(
									location, 
									"getNextApplications: [{0}]",
									nextApplications);
			}
		} catch (RuntimeException re) {
			DSLog
					.logErrorThrowable(
										location,
										"ASJ.dpl_ds.000349",
										"Unexpected Major Error during operation with the graph: getApplicationsWithNoReferences()",
										re);
		} finally {
			notifyAll();
		}
	}

	/**
	 * The method gets all applications that are eligible for operation.
	 */
	protected abstract List<Component> getNextApplications();

	/**
	 * Method that starts or stops given application depending on the type of
	 * the traverser
	 * 
	 * @return true if the operation is successful
	 * @throws DeploymentException
	 * 
	 */
	public abstract boolean execute(Component appName)
			throws DeploymentException;

	/**
	 * Method that gives the name of the current thread depending on its type
	 * 
	 * @return Deploy Parallel Stop Thread if the traverser is
	 *         <code>ParallelApplicationStopTraverser</code> or Deploy Parallel
	 *         Start Thread if the traverser is
	 *         <code>ParallelApplicationStartTraverser</code>
	 */
	protected abstract String getThreadName();
}