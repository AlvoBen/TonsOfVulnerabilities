/* 
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy.server.cache.dpl_info;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.impl.Graph;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.server.DUtils;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;

/**
 * A registry for all applications, resources and components. Together with
 * ComponentsRepository provides a facade for registration of all components and
 * resources.
 * 
 * @author Luchesar Cekov
 */
public class Applications {
	// the map with deployment info.
	private static final Map<String, DeploymentInfo> mApplications;
	// the resource manager.
	private static final CompRefGraph resourceManager;
	// the managed resource graph.
	private static final Graph<Component> refGraph;
	// the map with missing resources.
	private static final Map<Resource, Set<Edge<Component>>> missingResources;
	// the map with provided resources.
	private static Map<Resource, Component> providedResources;

	/*
	 * Initialize the registry.
	 */
	static {
		mApplications = new ConcurrentHashMap<String, DeploymentInfo>(40);
		missingResources = new ConcurrentHashMap<Resource, Set<Edge<Component>>>();
		providedResources = new ConcurrentHashMap<Resource, Component>();
		refGraph = new Graph<Component>();
		resourceManager = new CompRefGraph(refGraph, providedResources,
				missingResources);
	}

	/**
	 * @return unmodifiable map of all deployed applications.
	 */
	public static Map<String, DeploymentInfo> getApplicationsMap() {
		return Collections.unmodifiableMap(mApplications);
	}

	public static Graph<Component> getReferenceGraph() {
		return refGraph;
	}

	/**
	 * Add new deployment info to the registry. If there is already registered
	 * application, the old info will be removed before to be replaced by the
	 * new one. This operation will update the reference graph and the maps of
	 * provided and missing resources.
	 * 
	 * @param dInfo
	 *            deployment info about the application. Cannot be null.
	 */
	public static synchronized void add(DeploymentInfo dInfo) {
		if (mApplications.containsKey(dInfo.getApplicationName())) {
			removeIndexedProperties(dInfo.getApplicationName());
		}
		mApplications.put(dInfo.getApplicationName(), dInfo);
		try {
			resourceManager.addApplication(dInfo);
		} catch (RuntimeException e) {
			remove(dInfo.getApplicationName());
			throw e;
		}
	}

	/**
	 * Remove the information about the given application from the registry.
	 * This operation will update the reference graph and the maps of provided
	 * and missing resources.
	 * 
	 * @param applicationName
	 *            the name of the application to be removed. Cannot be null.
	 * @throws IllegalArgumentException
	 *             if no such application was registered before the call.
	 */
	public static synchronized void remove(String applicationName) {
		removeIndexedProperties(applicationName);
		mApplications.remove(applicationName);
	}

	public static DeploymentInfo get(String appName) {
		DeploymentInfo di = mApplications.get(appName);
		if (di == null) {
			di = mApplications.get(DUtils.getApplicationID(appName));
		}
		return di;
	}

	public static Set<String> getNames() {
		return mApplications.keySet();
	}

	public static Collection<DeploymentInfo> getAll() {
		return mApplications.values();
	}

	public static String[] list() {
		return mApplications.keySet().toArray(new String[size()]);
	}

	public static int size() {
		return mApplications.size();
	}

	public static void clear() {
		mApplications.clear();
		resourceManager.clear();
	}

	private static void removeIndexedProperties(String applicationName) {
		DeploymentInfo dInfo = mApplications.get(applicationName);
		assert dInfo != null;
		resourceManager.removeApplication(dInfo);
	}

	/**
	 * Checks if the given name is a name of an deployed application.
	 * 
	 * @param appName
	 *            the name of the application, to be checked.
	 * @return true if the corresponding application is deployed.
	 * @deprecated use isDeployedApplication(String appName) instead.
	 */
	@Deprecated
	public static boolean isApplication(String appName) {
		// TODO: To be deleted.
		return isDeployedApplication(appName);
	}

	/**
	 * Checks if the given name is a name of an deployed application.
	 * 
	 * @param appName
	 *            the name of the application, to be checked.
	 * @return true if the corresponding application is deployed.
	 */
	public static boolean isDeployedApplication(String appName) {
		return mApplications.containsKey(appName);
	}

	public static synchronized void registerAloneResource(
			final Resource resource, final Component provider) {
		resourceManager.registerAloneResource(resource, provider);
	}

	/**
	 * 
	 * @param res
	 * @return <code>Component</code>, which might be
	 *         <code>CompRefGraph.RESOURCE_NOT_PROVIDED</code>
	 */
	public static Component getResourceProvider(final Resource res) {
		return providedResources.get(res);
	}

	/**
	 * 
	 * @param res
	 * @return the resource provider name, if the provider is application.
	 * Otherwise <tt>null</tt>.
	 */
	public static String getResourceProviderIfApplication(final Resource res) {
		final Component provider = getResourceProvider(res);
		if (provider != null && 
			provider.getType() == Component.Type.APPLICATION &&
			!CompRefGraph.RESOURCE_NOT_PROVIDED.equals(provider)) {
			return provider.getName();
		}
		return null;
	}
}