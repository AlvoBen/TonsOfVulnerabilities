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
package com.sap.engine.services.deploy.server.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.sap.engine.lib.refgraph.CyclicReferencesException;
import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.ReferenceGraph;
import com.sap.engine.lib.refgraph.impl.Graph;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.tc.logging.Location;

/**
 * 
 * Class used to build reference graph from given set of applications
 * 
 * @author Assia Djambazova
 */
public class GraphBuilder {
	
	private static final Location location = 
		Location.getLocation(GraphBuilder.class);

	private final ReferenceGraph<Component> graph = new Graph<Component>();

	public GraphBuilder(Set<Component> applications, boolean buildBackwards)
			throws ServerDeploymentException {
		try {
			Applications.getReferenceGraph().cycleCheck();
		} catch (CyclicReferencesException ce) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "creating creating graph " }, ce);
			sde.setMessageID("ASJ.dpl_ds.005082");
			throw sde;
		}
		if (location.beDebug()) {
			DSLog.traceDebug(location, "Will create reference graph:");
		}
		if (buildBackwards) {
			buildRefGraphBackward(applications);
		} else {
			buildRefGraph(applications);
		}
	}

	/**
	 * Sorts the <code>ReferenceGraph</code>
	 * 
	 * @return Sorted <code>ReferenceGraph</code> with applications
	 */
	public ReferenceGraph<Component> getGraph() {
		return graph;
	}

	/*
	 * Private method that builds the reference graph from given set of
	 * applications
	 * 
	 * @param node
	 */
	private void buildRefGraph(Set<Component> applications)
			throws ServerDeploymentException {
		graph.clear();
		for (Component app : applications) {
			graph.add(app);
			if (location.beDebug()) {
				DSLog.traceDebug(
						location, 
						"In the buildRefGraph adding node [{0}]",
						app.getName());
			}
		}
		for (Component comp : applications) {
			for (Edge<Component> ref : Applications.getReferenceGraph()
					.getReferencesToOthersFrom(comp)) {
				final Component predecessor = ref.getSecond();
				// no self cycles are allowed anymore
				assert !comp.equals(predecessor);
				if (predecessor.getType() == Component.Type.APPLICATION
					&& Applications.isApplication(predecessor.getName())) {
					if (!graph.containsNode(predecessor)) {
						addMissing(predecessor);
					}
					graph.add(ref);
					if (location.beDebug()) {
						DSLog.traceDebug(
										location,
										"In the buildRefGraph adding edge from [{0}] to [{1}]",
										ref.getFirst(),
										ref.getSecond());
					}
				}
			}
		}
	}

	private void addMissing(Component comp) throws ServerDeploymentException {
		List<Component> sorted = new LinkedList<Component>();
		try {
			sorted = Applications.getReferenceGraph().sort(comp);
		} catch (CyclicReferencesException cre) {
			graph.clear();
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "sorting the applications used for building the reference graph " },
					cre);
			sde.setMessageID("ASJ.dpl_ds.005082");
			throw sde;
		}

		for (Component app : sorted) {
			if (app.getType() == Component.Type.APPLICATION
				&& Applications.isApplication(app.getName())
				&& !graph.containsNode(app)) {
				graph.add(app);
				if (location.beDebug()) {
					DSLog.traceDebug(
							location,
							"In the addMissing adding node [{0}]",
							app);
				}
			}
		}
		for (Component app : sorted) {
			if (app.getType() != Component.Type.APPLICATION
					|| !Applications.isApplication(app.getName())) {
				continue;
			}
			for (Edge<Component> ref : Applications.getReferenceGraph()
					.getReferencesToOthersFrom(app)) {
				final Component predecessor = ref.getSecond();
				// no more self cycles
				assert !predecessor.equals(app);
				if (predecessor.getType() == Component.Type.APPLICATION
						&& Applications.isApplication(predecessor.getName())) {
					if (!graph.containsNode(predecessor)) {
						graph.add(predecessor);
						if (location.beDebug()) {
							DSLog.traceDebug(
									location,
									"In the addMissing adding node [{0}]",
									predecessor.getName());
						}
					}
					graph.add(ref);
					if (location.beDebug()) {
						DSLog.traceDebug(
										location,
										"In the addMissing adding edge from [{0}] to [{1}]",
										app.getName(),
										predecessor.getName());
					}
				}
			}
		}
	}

	private void buildRefGraphBackward(Set<Component> applicationNames)
			throws ServerDeploymentException {
		graph.clear();
		for (Component app : applicationNames) {
			graph.add(app);
			if (location.beDebug()) {
				DSLog.traceDebug(location, "In the buildRefGraph adding node [{0}]",
						app);
			}
		}
		for (Component app : applicationNames) {
			for (Edge<Component> ref : Applications.getReferenceGraph()
					.getReferencesFromOthersTo(app)) {
				final Component successor = ref.getFirst();
				if (successor.getType() == Component.Type.APPLICATION
						&& Applications.isApplication(successor.getName())
						&& !successor.equals(app)) {
					if (!graph.containsNode(successor)) {
						addMissingBackwards(successor);
					}
					graph.add(ref);
					if (location.beDebug()) {
						DSLog
								.traceDebug(
										location,
										"In the buildRefGraph adding edge from [{0}] to [{1}]",
										successor.getName(),
										app.getName());
					}
				}
			}
		}
	}

	private void addMissingBackwards(Component appName)
			throws ServerDeploymentException {
		List<Component> sorted = new LinkedList<Component>();
		try {
			sorted = Applications.getReferenceGraph().sortBackward(appName);
		} catch (CyclicReferencesException cre) {
			graph.clear();
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "sorting the applications used for building the reference graph backwards" },
					cre);
			sde.setMessageID("ASJ.dpl_ds.005082");
			throw sde;
		}

		for (Component app : sorted) {
			if (app.getType() == Component.Type.APPLICATION
					&& Applications.isApplication(app.getName())
					&& !graph.containsNode(app)) {
				graph.add(app);
				if (location.beDebug()) {
					DSLog.traceDebug(location, "In the addMissing adding node [{0}]",
							app
									.getName());
				}
			}
		}
		for (Component app : sorted) {
			if (app.getType() != Component.Type.APPLICATION
					|| !Applications.isApplication(app.getName())) {
				continue;
			}
			for (Edge<Component> ref : Applications.getReferenceGraph()
					.getReferencesFromOthersTo(app)) {
				final Component successor = ref.getFirst();
				// No more self cycles.
				assert !successor.equals(app);
				if (successor.getType() == Component.Type.APPLICATION
						&& Applications.isApplication(successor.getName())) {
					if (!graph.containsNode(successor)) {
						graph.add(successor);
						if (location.beDebug()) {
							DSLog.traceDebug(location, "In the addMissing adding node [{0}]",
									successor.getName());
						}
					}
					graph.add(ref);
					if (location.beDebug()) {
						DSLog
								.traceDebug(
										location,
										"In the addMissing adding edge from [{0}] to [{1}]",
										successor.getName(),
										app.getName());
					}
				}
			}
		}
	}
}
