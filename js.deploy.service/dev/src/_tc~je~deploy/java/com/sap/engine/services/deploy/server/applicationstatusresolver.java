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
package com.sap.engine.services.deploy.server;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sap.engine.lib.config.api.ClusterConfiguration;
import com.sap.engine.lib.config.api.CommonClusterFactory;
import com.sap.engine.lib.config.api.ConfigurationLevel;
import com.sap.engine.lib.config.api.exceptions.ClusterConfigurationException;
import com.sap.engine.lib.config.api.filters.ComponentFilter;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.CyclicReferencesException;
import com.sap.engine.services.deploy.container.op.util.StartUp;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.op.util.StatusDescriptionsEnum;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.ApplicationName;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.Convertor;
import com.sap.engine.services.deploy.server.utils.ShmComponentUtils;
import com.sap.tc.logging.Location;

/**
 * Class that creates a list of applications that should be started during the
 * initial application start.
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class ApplicationStatusResolver {
	private static final Location location = 
		Location.getLocation(ApplicationStatusResolver.class);
	private static int instanceId;
	private final Set<Component> applicationNames;
	private final Set<Component> disabledApps;

	private static List<ComponentFilter> filters;

	public ApplicationStatusResolver(final int instanceId) 
		throws ServerDeploymentException {
		ApplicationStatusResolver.instanceId = instanceId;
		applicationNames = new HashSet<Component>();
		disabledApps = new HashSet<Component>();
		processFilters();
		if (location.beDebug()) {
			DSLog.traceDebug(
				location,			
				"Depending on the filters are the following applications should be started : [{0}]\n",
				applicationNames);
		}
		processOther();
		if (location.beDebug()) {
			DSLog.traceDebug(
					location,
					"Removing lazy, skipped, DB and FS applications : [{0}]\n",
					applicationNames);
		}
	}

	/**
	 * Returns <code>Set</code> with all applications that must be started
	 * during the initial start of the engine The applications are not sorted
	 * 
	 * @return Set with the applications
	 */
	public Set<Component> getApplicationNamesWhichHasToBeStarted() {
		return applicationNames;
	}

	private void processOther() {
		final Iterator<Component> appIter = applicationNames.iterator();
		while (appIter.hasNext()) {
			final Component appName = appIter.next();
			final DeploymentInfo dInfo = Applications.get(appName.getName());
			if (dInfo == null) {
				continue;
			}

			if (dInfo.isSupportingLazyStart()) {
				if (location.beDebug()) {
					DSLog
							.traceDebug(
									location,
									"The application [{0}] won't be started, because of its start up mode, which is [{1}] and in spite of the server filters.",
									dInfo.getApplicationName(), dInfo
											.getStartUpO().getName());
				}
				dInfo
						.setStatusDescription(
								StatusDescriptionsEnum.INITIALLY_NOT_STARTED_BECAUSE_OF_STARTUP_MODE,
								new Object[] { dInfo.getStartUpO().getName() });
				appIter.remove();
				continue;
			}

			if (!dInfo.isJ2EEApplication()) {
				if (location.beDebug()) {
					DSLog
							.traceDebug(
									location, 
									"The application [{0}] won't be started, because of its software type, which is [{1}] and in spite of the server filters.",
									dInfo.getApplicationName(), dInfo
											.getSoftwareType());
				}
				dInfo
						.setStatusDescription(
								StatusDescriptionsEnum.INITIALLY_NOT_STARTED_BECAUSE_OF_SOFTWARE_TYPE,
								new Object[] { dInfo.getSoftwareType() });
				appIter.remove();
				continue;
			}
		}
	}

	private void processFilters() 
		throws ServerDeploymentException {
		final String apps[] = DeployServiceFactory.getDeployService()
				.listJ2EEApplications(null);
		if (apps == null || apps.length == 0) {
			return;
		}
		extractFilters();

		if (filters == null || filters.size() == 0) {
			ServerDeploymentException sde =
			 new ServerDeploymentException(
					ExceptionConstants.NO_FILTERS_FOUND);
			sde.setMessageID("ASJ.dpl_ds.005134");
			throw sde;
		}

		ComponentFilter rule = null;
		for (Iterator<ComponentFilter> iter = filters.iterator(); iter
				.hasNext();) {
			rule = iter.next();
			// start rule
			if (rule.getAction() == ComponentFilter.ACTION_START) {
				if (rule.matches(ComponentFilter.ACTION_START,
						ComponentFilter.COMPONENT_APPLICATION, "*", "*")) {
					for (int k = 0; k < apps.length; k++) {
						applicationNames.add(Component.create(apps[k]));
						setExpectedStatus4StartFilter(apps[k]);
					}
				} else {
					for (int k = 0; k < apps.length; k++) {
						ApplicationName appNameO = Applications.get(apps[k])
								.getApplicationNameO();
						if (rule.matches(ComponentFilter.ACTION_START,
								ComponentFilter.COMPONENT_APPLICATION, appNameO
										.getProvider(), appNameO.getName())) {
							addAppToStartSet(appNameO.toString());
							setExpectedStatus4StartFilter(apps[k]);
						}
					}
				}
				// stop rule
			} else if (rule.matches(ComponentFilter.ACTION_STOP,
					ComponentFilter.COMPONENT_APPLICATION, "*", "*")
					|| rule.matches(ComponentFilter.ACTION_DISABLE,
							ComponentFilter.COMPONENT_APPLICATION, "*", "*")) {
				applicationNames.clear();
			} else {
				for (int k = 0; k < apps.length; k++) {
					ApplicationName appNameO = Applications.get(apps[k])
							.getApplicationNameO();
					if (rule.matches(ComponentFilter.ACTION_STOP,
							ComponentFilter.COMPONENT_APPLICATION, appNameO
									.getProvider(), appNameO.getName())
							|| rule.matches(ComponentFilter.ACTION_DISABLE,
									ComponentFilter.COMPONENT_APPLICATION,
									appNameO.getProvider(), appNameO.getName())) {
						removeAppFromStartSet(appNameO.toString());
						if (rule.matches(ComponentFilter.ACTION_DISABLE,
								ComponentFilter.COMPONENT_APPLICATION, appNameO
										.getProvider(), appNameO.getName())) {
							disabledApps.add(Component.create(apps[k]));
						}
					}
				}
			}
		}

		// explicitly set additional information for
		// the initial status description of filtered applications
		for (int i = 0; i < apps.length; i++) {
			ShmComponentUtils.setLocalStatus(Status.STOPPED, apps[i]);
			final DeploymentInfo dplInfo = Applications.get(apps[i]);
			if (applicationNames.contains(new Component(apps[i],
				Component.Type.APPLICATION))) {
				if (dplInfo.isSupportingLazyStart()) {
					ShmComponentUtils.setStartupModeLazy(dplInfo
							.getApplicationName());
				} else {
					ShmComponentUtils.setStartupModeAlways(dplInfo
							.getApplicationName());
				}
			} else {
				if (disabledApps.contains(new Component(apps[i],
					Component.Type.APPLICATION))) {
					dplInfo.setStartUpO(StartUp.DISABLED);
					ShmComponentUtils.setStartupModeDisabled(apps[i]);
				} else if (dplInfo.isSupportingLazyStart()) {
					ShmComponentUtils.setStartupModeLazy(dplInfo
							.getApplicationName());
				} else {
					ShmComponentUtils.setStartupModeManual(dplInfo
							.getApplicationName());
				}
				dplInfo
						.setStatusDescription(
								StatusDescriptionsEnum.INITIALLY_NOT_STARTED_AS_IN_STOP_FILTERS,
								null);
				ShmComponentUtils.setExpectedStatus(Status.STOPPED, apps[i]);

			}
			// add here containers initialization for application-containers
			// (loaded from DB)
			// check if application provides container
			if (dplInfo.getContainerInfoXML() != null) {
				byte[] xml = dplInfo.getContainerInfoXML().getBytes();
				ByteArrayInputStream is = new ByteArrayInputStream(xml);
				Containers.getInstance().addContainers(
					is, new Component(dplInfo.getApplicationName(),
								Component.Type.APPLICATION));
			}
		}
	}

	private void addAppToStartSet(String appName)
			throws ServerDeploymentException {
		try {
			final Component appComp = 
				new Component(appName, Component.Type.APPLICATION);
			for (Component comp : Applications.getReferenceGraph()
					.sort(appComp)) {
				if (comp.getType() == Component.Type.APPLICATION
						&& Applications.isDeployedApplication(comp.getName())) {
					applicationNames.add(comp);
				}
			}
		} catch (com.sap.engine.lib.refgraph.CyclicReferencesException e) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CYCLIC_REFERENCE_ON_ADD,
					new Object[] { appName }, new CyclicReferencesException(e));
			sde.setMessageID("ASJ.dpl_ds.005131");
			throw sde;
		}
	}

	private void removeAppFromStartSet(String appName)
			throws ServerDeploymentException {
		try {
			applicationNames.removeAll(Applications.getReferenceGraph()
					.sortBackward(Component.create(appName)));
			// TODO: is it possible to get Component instead String
		} catch (com.sap.engine.lib.refgraph.CyclicReferencesException e) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CYCLIC_REFERENCE_ON_REMOVE,
					new Object[] { appName }, new CyclicReferencesException(e));
			sde.setMessageID("ASJ.dpl_ds.005132");
			throw sde;
		}

	}

	public static boolean isApplicationDisabled(String aApplicationName)
		throws ServerDeploymentException {
		ApplicationName applicationName = new ApplicationName(aApplicationName);
		extractFilters();
		List<ComponentFilter> reversedFilters = new ArrayList<ComponentFilter>(
				filters.size());
		reversedFilters.addAll(filters);
		Collections.reverse(reversedFilters);
		ComponentFilter filter = null;
		for (Iterator<ComponentFilter> iter = reversedFilters.iterator(); 
			iter.hasNext();) {
			filter = iter.next();
			if (filter != null) {
				if (filter.matches(ComponentFilter.ACTION_START,
						ComponentFilter.COMPONENT_APPLICATION, "*", "*")
						|| filter.matches(ComponentFilter.ACTION_START,
								ComponentFilter.COMPONENT_APPLICATION,
								applicationName.getProvider(), applicationName
										.getName())
						|| filter
								.matches(ComponentFilter.ACTION_STOP,
										ComponentFilter.COMPONENT_APPLICATION,
										"*", "*")
						|| filter.matches(ComponentFilter.ACTION_STOP,
								ComponentFilter.COMPONENT_APPLICATION,
								applicationName.getProvider(), applicationName
										.getName())) {
					return false;
				} else if (filter.matches(ComponentFilter.ACTION_DISABLE,
						ComponentFilter.COMPONENT_APPLICATION, "*", "*")
						|| filter.matches(ComponentFilter.ACTION_DISABLE,
								ComponentFilter.COMPONENT_APPLICATION,
								applicationName.getProvider(), applicationName
										.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	private synchronized static void extractFilters()
		throws ServerDeploymentException {
		if (filters != null) {
			return;
		}
		if (location.beDebug()) {
			DSLog.traceDebug(location, "Will extract status filters from the data base.");
		}
		final CommonClusterFactory factory = ClusterConfiguration
			.getClusterFactory(PropManager.getInstance()
				.getConfigurationHandlerFactory());
		
		long start = System.currentTimeMillis();
		if (instanceId == 0) {
			instanceId = DeployServiceFactory.getDeployService()
			.getDeployServiceContext().getClusterMonitorHelper().getCurrentInstanceId();
		}
		try {
			final ConfigurationLevel level = factory.openConfigurationLevel(
				CommonClusterFactory.LEVEL_INSTANCE, "ID" + instanceId);

			List<ComponentFilter> temp = level.getFilters(true)
				.getUpperLevelsFilters();
			if (location.beDebug()) {
				DSLog.traceDebug(
					location,
					"level.getFilters(true).getUpperLevelsFilters() = [{0}]",
					Convertor.toString(temp, ""));
			}
			filters = temp;

			temp = level.getFilters(true).getFilter();
			if (location.beDebug()) {
				DSLog.traceDebug(
						location, 
						"level.getFilters(true).getFilter() = [{0}]",
						Convertor.toString(temp, ""));
			}
			filters.addAll(temp);

		} catch (ClusterConfigurationException ccEx) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { "reading the start up filters for the applications." },
				ccEx);
			sde.setMessageID("ASJ.dpl_ds.005082");
			sde.setDcNameForObjectCaller(factory);
			throw sde;
		} finally {
			if (location.beDebug()) {
				DSLog.traceDebug(
						location, 
						"The [{0}] filters were extracted for [{1}] ms.",
						(System.currentTimeMillis() - start));
			}
		}
	}

	public static void setExpectedStatus4StartFilter(String appName) {
		final DeploymentInfo dplInfo = Applications.get(appName);
		if (dplInfo.isSupportingLazyStart()) {
			ShmComponentUtils.setExpectedStatus(Status.STOPPED, dplInfo
				.getApplicationName());
			ShmComponentUtils.setLocalStatus(Status.STOPPED, dplInfo
				.getApplicationName());
		} else {
			ShmComponentUtils.setExpectedStatus(Status.STARTED, dplInfo
				.getApplicationName());
		}
	}
}
