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
package com.sap.engine.services.deploy.server.cache.dpl_info;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.NodeRemoveException;
import com.sap.engine.lib.refgraph.impl.Graph;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.dpl_info.module.ResourceReference;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Class used to manage the components, resources and references. The methods of
 * this class are never called directly, but through the Applications class. So,
 * the Applications class is responsible for the synchronization.
 * <p>
 * The reference graph has components as nodes (services, libraries, interfaces
 * or applications). A given component will be included in the graph if:
 * <li>it is a deployed application</li>
 * <li>if it is referenced by deployed application</li>
 * <li>if it provides some public resources</li><br>
 * Note that it is not necessary that a given component to be available, to
 * include it as a node in the reference graph.
 * </p>
 * <p>
 * A component will be removed from the graph if:
 * <li>it is an unreferenced library or interface</li>
 * <li>it is an unreferenced service, which does not provide any public 
 * resources</li>
 * <li>it is an unreferenced and not deployed application</li></p>
 * <p>
 * We can have multiple edges between 2 nodes in the graph, every edge
 * associated with different resource. Self cycles are not allowed, because the
 * self provided resources are processed before to add every application to the
 * reference graph. Only public resources can connect different applications.
 * </p>
 * 
 * @author Luchesar Cekov
 */
public final class CompRefGraph {
	private static final Location location = 
		Location.getLocation(CompRefGraph.class);

	public static final Component RESOURCE_NOT_PROVIDED = 
		new Component("###/###This Resource is not provided##",
			Component.Type.APPLICATION);
	private final Map<Resource, Set<Edge<Component>>> missingResources;
	private final Map<Resource, Component> providedResources;
	private final Graph<Component> refGraph;

	/**
	 * The constructor has package private accesses level and is called only by
	 * Applications class during the static initialization.
	 * 
	 * @param refGraph
	 *            reference graph.
	 * @param providedResourses
	 *            map of provided resources.
	 * @param missingResources
	 *            map of missing resources.
	 */
	CompRefGraph(final Graph<Component> refGraph,
		final Map<Resource, Component> providedResourses,
		final Map<Resource, Set<Edge<Component>>> missingResources) {
		this.refGraph = refGraph;
		this.providedResources = providedResourses;
		this.missingResources = missingResources;
		refGraph.add(RESOURCE_NOT_PROVIDED);
	}

	/**
	 * Clear the managed maps and the reference graph.
	 */
	void clear() {
		providedResources.clear();
		missingResources.clear();
		refGraph.clear();
		refGraph.add(RESOURCE_NOT_PROVIDED);
	}

	/**
	 * Add a new application, updating the reference graph and the maps of
	 * provided and missing resources.
	 * 
	 * @param dInfo
	 *            deployment info about the application, to be added to the
	 *            reference graph. Not null.
	 */
	void addApplication(DeploymentInfo dInfo) {
		assert dInfo != null;
		final Component app = new Component(
			dInfo.getApplicationName(), Component.Type.APPLICATION);
		if (!refGraph.containsNode(app)) {
			refGraph.add(app);
		}

		addReferences(dInfo, app);
		final Set<Resource> resources = dInfo.getAllProvidedResources();
		addResourceReferences(dInfo, app, resources);
		registerPublicResources(app, resources);
	}

	/*
	 * Update reference graph, based on the information in the deployment info
	 * for direct references. (Reference, which is not based on any resource,
	 * but is specified in <tt>application-j2ee-engine.xml</tt> file).
	 * 
	 * @param dInfo deployment info for the current component.
	 * 
	 * @param app the current component
	 */
	private void addReferences(DeploymentInfo dInfo, final Component app) {
		final ReferenceObject[] references = dInfo.getReferences();
		if (references != null) {
			for (int i = 0; i < references.length; i++) {
				final String refName = references[i].getName();
				final String refType = references[i].getReferenceTargetType();
				final Component refComp = new Component(refName, refType);
				if (!refGraph.containsNode(refComp)) {
					refGraph.add(refComp);
				}
				refGraph.add(new Edge<Component>(app, refComp,
					ReferenceObjectIntf.REF_TYPE_WEAK.equals(
						references[i].getReferenceType()) ? 
							Edge.Type.WEAK : Edge.Type.HARD, null));
			}
		}
	}

	/**
	 * Remove the given application, updating the reference graph and the maps
	 * of provided and missing resources. All referenced resources provided by
	 * this application will be moved to the map of missing resources. All
	 * publicly provided resources, will be removed from the map of provided
	 * resources (See the comments in the code about First Wins Strategy). All
	 * references to predecessors will be unconditional removed. The node itself
	 * will be removed only it is not directly referenced (Reference, which is
	 * not based on any resource. Direct references are specified in
	 * <tt>application-j2ee-engine.xml</tt> file).
	 * 
	 * @param dInfo
	 *            deployment info about the application, which has to be
	 *            removed. Not null.
	 */
	void removeApplication(DeploymentInfo dInfo) {
		final Component comp = new Component(
			dInfo.getApplicationName(), Component.Type.APPLICATION);
		removeEdgesToPredecessors(comp);
		final boolean referenced = removeEdgesFromSuccessors(comp);
		if (!referenced) {
			removeUnreferencedComponent(comp);
		}
		unregisterPublicResources(dInfo, comp);
	}

	/**
	 * Update the reference graph and the maps of provided and missing 
	 * resources, when a new alone resource is provided. Such resources can be 
	 * provided only by containers via <tt>DeployCommunicator</tt> interface.
	 * 
	 * @param resource newly available resource. Not null.
	 * @param provider the container component which provides the resource. Can
	 * be service or application. Not null.
	 */
	void registerAloneResource(Resource resource, Component provider) {
		if (!refGraph.containsNode(provider)) {
			// Resource providers has to be included in the reference graph.
			refGraph.add(provider);
		}
		resourceIsProvided(resource, provider, true);
	}

	private void removeUnreferencedComponent(Component comp) {
		try {
			refGraph.remove(comp);
		} catch (NodeRemoveException ex) {
			// This should never happen (invariant)
			throw new AssertionError(ex);
		}
	}

	/**
	 * Update the graph and the maps of provided and missing resources, when a
	 * given resource is unregistered.
	 * 
	 * @param resource the resource which is unregistered. Not null. We suppose
	 * that this resource was already registered via <tt>DeployCommunicator</tt>
	 * interface. The provider of the resource is a container, which can be 
	 * service or application.
	 */
	void unregisterAloneResource(Resource resource) {
		final Component provider = providedResources.get(resource);
		assert provider != null;
		assert provider.getType() == Component.Type.SERVICE ||
			provider.getType() == Component.Type.APPLICATION;

		final Set<Edge<Component>> refsFromSuccessors = 
			refGraph.getReferencesFromOthersTo(provider);

		for (Edge<Component> ref : refsFromSuccessors) {
			if (resource.equals(ref.getNestedObject())) {
				refGraph.remove(ref);
				resourceIsMissing(ref.getFirst(), resource, ref.getType());
			}
		}
		providedResources.remove(resource);
		if(isNeedToRemove(provider)) {
			removeUnreferencedComponent(provider);
		}
	}

	private void unregisterPublicResources(DeploymentInfo dInfo,
		final Component comp) {
		for (Resource res : dInfo.getAllProvidedResources()) {
			// We not throw exception if more than one components provide
			// the same resource, but we use The First Wins Strategy. Therefore
			// we need the following check, to remove the resource only if the
			// provider is the same.
			if (res.getAccessType() == Resource.AccessType.PUBLIC && 
				comp.equals(providedResources.get(res))) {
				providedResources.remove(res);
			}
		}
	}

	/*
	 * Add edges in the graph, based on the resource references and update the
	 * maps of provided and needed resources.
	 * 
	 * @param dInfo deployment info for the application.
	 * 
	 * @param node the node, corresponding to the application.
	 */
	private void addResourceReferences(final DeploymentInfo dInfo,
		final Component node, final Set<Resource> resources) {
		final Set<ResourceReference> resourceRefs = dInfo
				.getNotSelfProvidedResourceReferences(resources);
		for(ResourceReference reference : resourceRefs) {
			final Resource resource = new Resource(
				reference.getResRefName(),
				reference.getResRefType());
			makeResRef(node, resource, ReferenceObjectIntf.REF_TYPE_WEAK
				.equals(reference.getReferenceType()) ? 
					Edge.Type.WEAK : Edge.Type.HARD);
		}
	}

	private void registerPublicResources(final Component node,
		final Set<Resource> resources) {
		for (Resource resource : resources) {
			if (resource.getAccessType() == Resource.AccessType.PUBLIC) {
				resourceIsProvided(resource, node, 
					PropManager.getInstance().firstWins());
			}
		}
	}

	/*
	 * Make an edge in the graph between the consumer and the provider of the
	 * given resource. If the resource is not provided, it will be added to the
	 * map of missing resources.
	 * 
	 * @param consumer the resource consumer.
	 * 
	 * @param resource needed resource.
	 * 
	 * @param refType reference type (HARD or WEAK).
	 */
	private void makeResRef(final Component consumer, final Resource resource,
		final Edge.Type refType) {
		final Component provider = providedResources.get(resource);
		if (provider == null) {
			resourceIsMissing(consumer, resource, refType);
		} else {
			refGraph.add(new Edge<Component>(
				consumer, provider, refType, resource));
		}
	}

	/*
	 * Update the graph and the map of missing resources, when a given resource
	 * is missing.
	 * 
	 * @param consumer the resource consumer.
	 * 
	 * @param resource the missing resource.
	 * 
	 * @param refType reference type (HARD or WEAK).
	 */
	private void resourceIsMissing(final Component consumer,
		final Resource resource, final Edge.Type refType) {
		assert consumer.getType() == Component.Type.APPLICATION;
		final Edge<Component> edge = new Edge<Component>(
			consumer, RESOURCE_NOT_PROVIDED, refType, resource);
		refGraph.add(edge);
		Set<Edge<Component>> refsToResource = missingResources.get(resource);
		if (refsToResource == null) {
			refsToResource = new HashSet<Edge<Component>>();
			missingResources.put(resource, refsToResource);
		}
		refsToResource.add(edge);
	}

	/*
	 * Update the graph and the maps of provided and missing resources, when a
	 * new resource is available.
	 * 
	 * @param resource newly available resource. Not null.
	 * 
	 * @param provider resource provider. We suppose that it is already included
	 * as node in the graph. Not null.
	 * 
	 * @param firstWins whether we use The First Wins Strategy.
	 */
	private void resourceIsProvided(final Resource resource,
		final Component provider, final boolean firstWins) {
		if (providedResources.containsKey(resource)) {
			if (location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					reportCurrentProvider(resource));
			}
			if(firstWins) {
				// The First Wins Strategy is used.
				return;
			}
		}
		providedResources.put(resource, provider);
		final Set<Edge<Component>> refsToResourse = 
			missingResources.get(resource);
		if (refsToResourse == null) {
			// There is still not applications needed this resource.
			return;
		}
		for (Edge<Component> edge : refsToResourse) {
			// Remove the edges to RESOURCE_NOT_PROVIDED node.
			refGraph.remove(edge);
			// And replace them with edges to the resource provider.
			refGraph.add(new Edge<Component>(edge.getFirst(), provider, 
				edge.getType(), resource));
		}
		missingResources.remove(resource);
	}

	private String reportCurrentProvider(final Resource resource) {
		final StringBuilder report = new StringBuilder();
		final Component provider = providedResources.get(resource);
		report.append("The resource [").append(resource).append("]\n")
			.append("is already provided by [").append(provider).append("].\n")
			.append("The current consumers of this resource are:");
		final Set<Edge<Component>> refsFromConsumers = 
			refGraph.getReferencesFromOthersTo(provider);
		for(Edge<Component> ref : refsFromConsumers) {
			if(resource.equals(ref.getNestedObject())) {
				report.append("\t").append(ref.getFirst()).append("\n");
			}
		}
		return report.toString();
	}

	/**
	 * Unconditional remove references to predecessors and update the map of
	 * missing resources if the predecessor is the RESOURCE_NOT_PROVIDED node.
	 * Traversing those references we will remove all unreferenced libraries 
	 * and interfaces; all unreferenced and undeployed applications; all 
	 * unreferenced services which does not provide public resources. So the
	 * applications will stay in the graph until they are undeployed. The same
	 * is true and for all services which provide public resources.
	 * 
	 * Predecessors are all component from which the given component depends.
	 * 
	 * @param component the given component.
	 */
	private void removeEdgesToPredecessors(Component component) {
		for (Edge<Component> edge : 
			refGraph.getReferencesToOthersFrom(component)) {
			// the references to the predecessor is unconditional removed.
			refGraph.remove(edge);
			final Component predecessor = edge.getSecond();
			if (predecessor.equals(RESOURCE_NOT_PROVIDED)) {
				// The references to this node are always based on resources,
				// which are currently missing.
				removeMissingResourceRefs(edge);
			} else if(isNeedToRemove(predecessor)) {
				// Only deployed applications have predecessors.
				// It is safe to remove the node here.
				removeUnreferencedComponent(predecessor);
			}
		}
	}

	private void removeMissingResourceRefs(Edge<Component> edge) {
		final Resource res = (Resource) edge.getNestedObject();
		assert res != null;
		final Set<Edge<Component>> refsToResourse = 
			missingResources.get(edge.getNestedObject());
		assert refsToResourse != null;
		refsToResourse.remove(edge);
		if (refsToResourse.size() == 0) {
			missingResources.remove(edge.getNestedObject());
		}
	}

	/**
	 * Check whether the given node can be removed. We can remove an 
	 * unreferenced node if:
	 * <li>it is library or interface</li>
	 * <li>it is an already undeployed application</li>
	 * <li>it is a service, which does not provide any resources</li>
	 * 
	 * @param node the node to be removed if needed.
	 */
	private boolean isNeedToRemove(Component node) {
		final boolean result;
		if(refGraph.getReferencesFromOthersTo(node).size() > 0) {
			// This node is still referenced.
			result = false;
		} else {
			switch(node.getType()) {
			case INTERFACE:
			case LIBRARY:
				result = true;
				break;
			case APPLICATION:
				// Undeployed applications cannot provide resources.
				result = !Applications.isDeployedApplication(node.getName());
				break;
			case SERVICE:
				result = !providedResources.values().contains(node);
				break;
			default: throw new AssertionError("Unsupported component type.");
			}
		}
		return result;
	}
	
	/*
	 * Remove the references from successors, if they are based on a given
	 * resource. All direct references (references, which are not based on any
	 * resource) will be preserved in the reference graph.
	 * 
	 * @param node the current node.
	 * 
	 * @return return true if the node is still referenced via direct reference.
	 */
	private boolean removeEdgesFromSuccessors(Component node) {
		boolean referenced = false;
		for (Edge<Component> edge : refGraph.getReferencesFromOthersTo(node)) {
			final Resource res = (Resource) edge.getNestedObject();
			if (res != null) {
				// Remove references based on resources.
				refGraph.remove(edge);
				resourceIsMissing(edge.getFirst(), res, edge.getType());
			} else {
				// Direct reference.
				referenced = true;
			}
		}
		return referenced;
	}
}