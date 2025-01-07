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
package com.sap.engine.deployment.proxy;

import java.util.ArrayList;
import java.rmi.RemoteException;
import javax.enterprise.deploy.shared.ModuleType;

import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;
import com.sap.engine.deployment.Logger;
import com.sap.engine.lib.io.SerializableFile;
import com.sap.engine.services.deploy.ApplicationInformation;
import com.sap.engine.services.deploy.ear.J2EEModule;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.deployment.SAPTargetModuleID;
import com.sap.engine.deployment.SAPTarget;
import com.sap.engine.deployment.Module;
import com.sap.engine.deployment.TargetCluster;
import com.sap.engine.deployment.exceptions.ExceptionConstants;
import com.sap.engine.deployment.exceptions.SAPRemoteException;
import com.sap.engine.deployment.exceptions.SAPTargetException;
import com.sap.engine.deployment.exceptions.SAPIllegalStateException;

/**
 * @author Mariela Todorova
 */
public class ManagementProxyImpl extends ConnectionManagerImpl implements
		ManagementProxy {
	private static final Location location = Location
			.getLocation(ManagementProxyImpl.class);
	private static final String STARTED = "STARTED";
	private static final String EJB_CONT = "EJBContainer";
	private static final String WEB_CONT = "servlet_jsp";
	private static final String APPCLIENT_CONT = "appclient";
	private static final String CONNECTOR_CONT = "connector";
	private static final String EJB_ = "- EJB";
	private static final String WEB_ = "- WEB";
	private static final String APPCLIENT_ = "- JAVA";
	private static final String CONNECTOR_ = "- CONNECTOR";

	public SAPTargetModuleID[] getRunningModules(ModuleType moduleType,
			SAPTarget[] targetList) throws SAPTargetException,
			SAPIllegalStateException, SAPRemoteException {
		Logger.trace(location, Severity.DEBUG, "Getting running " + moduleType
				+ " modules");
		SAPTargetModuleID[] modules = getAvailableModules(moduleType,
				targetList);

		if (modules == null) {
			Logger.trace(location, Severity.DEBUG, "No available " + moduleType
					+ " modules");
			return null;
		}

		if (modules.length == 0) {
			Logger.trace(location, Severity.DEBUG, "Empty array of available "
					+ moduleType + " modules");
		}

		ArrayList started = new ArrayList();

		try {
			for (int i = 0; i < modules.length; i++) {
				if (ds.getApplicationStatus(modules[i].getModuleID(),
						modules[i].getTarget().getName()).equals(STARTED)) {
					started.add(modules[i]);
					Logger.log(location, Severity.INFO, "Obtained running "
							+ moduleType + " module " + modules[i].toString());
				}
			}
		} catch (RemoteException re) {// $JL-EXC$
			SAPRemoteException sre = new SAPRemoteException(location,
					ExceptionConstants.COULD_NOT_GET_RUNNING_MODULES,
					new String[] { moduleType.toString(), re.getMessage() }, re);
			Logger.trace(location, Severity.ERROR, sre.getMessage());
			throw sre;
		}

		Logger.trace(location, Severity.PATH, "Returning running " + moduleType
				+ " modules");
		return (SAPTargetModuleID[]) started.toArray(new SAPTargetModuleID[0]);
	}

	public SAPTargetModuleID[] getNonRunningModules(ModuleType moduleType,
			SAPTarget[] targets) throws SAPTargetException,
			SAPIllegalStateException, SAPRemoteException {
		Logger.trace(location, Severity.DEBUG, "Getting non-running "
				+ moduleType + " modules");
		SAPTargetModuleID[] modules = getAvailableModules(moduleType, targets);

		if (modules == null) {
			Logger.trace(location, Severity.DEBUG, "No available " + moduleType
					+ " modules");
			return null;
		}

		if (modules.length == 0) {
			Logger.trace(location, Severity.DEBUG, "Empty array of available "
					+ moduleType + " modules");
		}

		ArrayList stopped = new ArrayList();

		try {
			for (int i = 0; i < modules.length; i++) {
				if (!ds.getApplicationStatus(modules[i].getModuleID(),
						modules[i].getTarget().getName()).equals(STARTED)) {
					stopped.add(modules[i]);
					Logger.log(location, Severity.INFO, "Obtained non-running "
							+ moduleType + " module " + modules[i].toString());
				}
			}
		} catch (RemoteException re) {// $JL-EXC$
			SAPRemoteException sre = new SAPRemoteException(location,
					ExceptionConstants.COULD_NOT_GET_NON_RUNNING_MODULES,
					new String[] { moduleType.toString(), re.getMessage() }, re);
			Logger.trace(location, Severity.ERROR, sre.getMessage());
			throw sre;
		}

		Logger.trace(location, Severity.PATH, "Returning non-running "
				+ moduleType + " modules");
		return (SAPTargetModuleID[]) stopped.toArray(new SAPTargetModuleID[0]);
	}

	public SAPTargetModuleID[] getAvailableModules(ModuleType moduleType,
			SAPTarget[] targets) throws SAPTargetException,
			SAPIllegalStateException, SAPRemoteException {
		Logger.trace(location, Severity.DEBUG,
				"Getting available modules of type " + moduleType);

		if (moduleType == null) {
			return null;
		}

		SAPTarget[] destinations = getDestinations(targets, false);

		if (destinations == null) {
			Logger.trace(location, Severity.DEBUG, "No server nodes returned");
			return null;
		}

		String[] result = null;
		String[] servers = new String[destinations.length];

		for (int i = 0; i < destinations.length; i++) {
			servers[i] = destinations[i].getName();
			Logger.trace(location, Severity.DEBUG, "Server " + servers[i]);
		}

		String container = getContainerName(moduleType, servers[0]);

		try {
			result = ds.listApplications(container, servers);
		} catch (RemoteException re) {// $JL-EXC$
			SAPRemoteException sre = new SAPRemoteException(location,
					ExceptionConstants.COULD_NOT_GET_AVAILABLE_MODULES,
					new String[] { moduleType.toString(), re.getMessage() }, re);
			Logger.trace(location, Severity.ERROR, sre.getMessage());
			throw sre;
		}

		if (result == null) {
			Logger.log(location, Severity.INFO, "No available " + moduleType
					+ " modules");
			return null;
		}

		ArrayList apps = filterApplications(result, container);
		ArrayList modules = new ArrayList();
		SAPTargetModuleID module = null;

		if (apps != null) {
			for (int j = 0; j < destinations.length; j++) {
				for (int i = 0; i < apps.size(); i++) {
					module = new SAPTargetModuleID(destinations[j], new Module(
							(String) apps.get(i), moduleType, null));

					if (!modules.contains(module)) {
						modules.add(module);
						Logger.log(location, Severity.INFO,
								"Obtained available " + moduleType + " module "
										+ module.toString());
					}
				}
			}
		}

		Logger.trace(location, Severity.PATH, "Returning available "
				+ moduleType + " modules");
		return (SAPTargetModuleID[]) modules.toArray(new SAPTargetModuleID[0]);
	}

	public SAPTargetModuleID[] determineTargetModules(String[] modules,
			SAPTarget[] targets) throws SAPRemoteException {
		Logger.trace(location, Severity.PATH, "Determining target modules");

		if (targets == null || targets.length == 0) {
			Logger.trace(location, Severity.DEBUG, " No targets specified");
			return new SAPTargetModuleID[0];
		}

		if (modules == null || modules.length == 0) {
			Logger.trace(location, Severity.DEBUG, " No modules specified");
			return new SAPTargetModuleID[0];
		}

		Module application = determineApplication(modules);
		Logger.trace(location, Severity.DEBUG, "Determined application "
				+ application);

		if (application == null) {
			return new SAPTargetModuleID[0];
		}

		ArrayList list = new ArrayList();
		SAPTargetModuleID tMod = null;

		for (int i = 0; i < targets.length; i++) {
			tMod = new SAPTargetModuleID(targets[i], application);

			if (!list.contains(tMod)) {
				list.add(tMod);
				Logger.trace(location, Severity.INFO,
						"Determined target module " + tMod);
			}
		}

		Logger.trace(location, Severity.PATH, "Returning target modules");
		return (SAPTargetModuleID[]) list.toArray(new SAPTargetModuleID[0]);
	}

	protected SAPTarget[] getDestinations(SAPTarget[] targets, boolean all)
			throws SAPRemoteException {
		Logger.trace(location, Severity.PATH, "Getting destinations");

		if (targets == null || targets.length == 0) {
			Logger.trace(location, Severity.DEBUG, "No targets specified");
			return null;
		}

		TargetCluster cluster = getCluster();
		SAPTarget target = null;
		ArrayList dest = new ArrayList();
		boolean found = false;

		for (int i = 0; i < targets.length; i++) {
			target = targets[i];

			if (cluster.containsTarget(target)) {
				dest.add(target);
				found = true;
				Logger.trace(location, Severity.INFO, "Obtained destination "
						+ target);
			}
		}

		if (!found) {
			Logger.trace(location, Severity.WARNING,
					"No destinations found in cluster " + cluster);
			return null;
		}

		if (all) {
			Logger.trace(location, Severity.INFO,
					"Returning all destinations in cluster");
			return cluster.getTargets();
		} else {
			Logger.trace(location, Severity.INFO,
					"Returning only needed destinations in cluster");
			return (SAPTarget[]) dest.toArray(new SAPTarget[0]);
		}
	}

	protected String getContainerName(ModuleType moduleType, String serverName)
			throws SAPRemoteException {
		Logger.trace(location, Severity.DEBUG,
				"Getting container name for module type " + moduleType);

		if (moduleType.equals(ModuleType.EAR)) {
			return null;
		}

		String[] contNames = null;
		try {
			contNames = ds.listContainers(new String[] { serverName });
			ContainerInfo info = null;
			String name = null;

			if (contNames != null) {
				for (int i = 0; i < contNames.length; i++) {
					info = ds.getContainerInfo(contNames[i], serverName);

					if (info.isJ2EEContainer()) {
						if (moduleType.equals(ModuleType.EJB)
								&& info.getJ2EEModuleName().equals(
										J2EEModule.ejb)
								|| moduleType.equals(ModuleType.CAR)
								&& info.getJ2EEModuleName().equals(
										J2EEModule.java)
								|| moduleType.equals(ModuleType.RAR)
								&& info.getJ2EEModuleName().equals(
										J2EEModule.connector)
								|| moduleType.equals(ModuleType.WAR)
								&& info.getJ2EEModuleName().equals(
										J2EEModule.web)) {

							name = info.getName();
							Logger.trace(location, Severity.DEBUG,
									"Returning container name " + name);
							return name;
						}
					}
				}
			}
		} catch (RemoteException re) {
			SAPRemoteException sre = new SAPRemoteException(location,
					ExceptionConstants.COULD_NOT_GET_AVAILABLE_MODULES,
					new String[] { moduleType.toString(), re.getMessage() }, re);
			Logger.trace(location, Severity.ERROR, sre.getMessage());
			throw sre;
		}

		Logger.trace(location, Severity.DEBUG,
				"Returning no container name for module type " + moduleType);
		return null;
	}

	private Module determineApplication(String[] modules)
			throws SAPRemoteException {
		Logger.trace(location, Severity.DEBUG, "Determining modules");

		String current = modules[0];
		Module application = null;

		Logger.trace(location, Severity.DEBUG, "Root module " + current);

		if (current == null || current.trim().equals("")) {
			Logger.trace(location, Severity.WARNING, "Root module missing");
			return application;
		}

		if (current.startsWith("Application : ")) {
			current = current.substring(14);
		}

		ApplicationInformation appInfo = null;

		try {
			appInfo = ds.getApplicationInformation(current);
		} catch (RemoteException re) {// $JL-EXC$
			SAPRemoteException sre = new SAPRemoteException(location,
					ExceptionConstants.COULD_NOT_GET_APPLICATION_INFORMATION,
					new String[] { current, re.toString() }, re);
			Logger.trace(location, Severity.ERROR, sre.getMessage());
			throw sre;
		}

		if (appInfo == null) {
			Logger.trace(location, Severity.WARNING,
					"No application information available for " + current);
			return application;
		}

		String[] containers = null;
		String container = null;
		Module mod = null;

		// standalone
		if (appInfo.isStandAloneModule()) {
			Logger.trace(location, Severity.DEBUG, "Module " + current
					+ " is standalone");
			containers = appInfo.getContainerNames();

			if (containers == null || containers.length == 0) {
				Logger.trace(location, Severity.WARNING,
						"No container for standalone module " + current);
				return application;
			}

			if (containers.length > 1) {
				Logger.trace(location, Severity.WARNING,
						"More than one container for standalone module "
								+ current
								+ "; the first Java EE one will be taken");
			}

			for (int i = 0; i < containers.length; i++) {
				container = containers[i];
				Logger.trace(location, Severity.DEBUG,
						"Container for standalone module " + current + " is "
								+ container);

				if (container.equals(CONNECTOR_CONT)) {
					application = new Module(current, ModuleType.RAR, null);
					Logger.trace(location, Severity.INFO,
							"Determined standalone " + ModuleType.RAR
									+ " module " + application);
					break;
				} else if (container.equals(EJB_CONT)) {
					application = new Module(current, ModuleType.EJB, null);
					Logger.trace(location, Severity.INFO,
							"Determined standalone " + ModuleType.EJB
									+ " module " + application);
					break;
				} else if (container.equals(WEB_CONT)) {
					application = new Module(current, ModuleType.WAR, null);
					Logger.trace(location, Severity.INFO,
							"Determined standalone " + ModuleType.WAR
									+ " module " + application);
					break;
				} else if (container.equals(APPCLIENT_CONT)) {
					application = new Module(current, ModuleType.CAR, null);
					Logger.trace(location, Severity.INFO,
							"Determined standalone " + ModuleType.CAR
									+ " module " + application);
					break;
				} else {
					Logger.trace(location, Severity.INFO, "Container "
							+ container
							+ " not recognized as a Java EE container");
				}
			}
		} else { // application
			Logger.trace(location, Severity.DEBUG, "Module " + current
					+ " is application");
			application = new Module(current, ModuleType.EAR, null);
			Logger.trace(location, Severity.INFO, "Determined application "
					+ application.toString());

			// list elements & add children
			Logger.trace(location, Severity.PATH,
					"Listing and adding child modules");

			for (int j = 2; j < modules.length; j++) {
				current = modules[j];
				Logger.trace(location, Severity.DEBUG, "Child module "
						+ current);

				if (current == null || current.trim().equals("")) {
					continue;
				}

				if (current.toUpperCase().endsWith(EJB_)) {
					mod = new Module(
							current.substring(0, current.indexOf("-")),
							ModuleType.EJB, application);
					Logger.trace(location, Severity.INFO, "Child "
							+ ModuleType.EJB + " module " + mod);
					application.addChild(mod);
				} else if (current.toUpperCase().endsWith(APPCLIENT_)) {
					mod = new Module(
							current.substring(0, current.indexOf("-")),
							ModuleType.CAR, application);
					Logger.trace(location, Severity.INFO, "Child "
							+ ModuleType.CAR + " module " + mod);
					application.addChild(mod);
				} else if (current.toUpperCase().endsWith(WEB_)) {
					mod = new Module(
							current.substring(0, current.indexOf("-")),
							ModuleType.WAR, application);
					mod.setWebURL(current.substring(0, current.indexOf("-")));
					Logger.trace(location, Severity.INFO, "Child "
							+ ModuleType.WAR + " module " + mod);
					application.addChild(mod);
				} else if (current.toUpperCase().endsWith(CONNECTOR_)) {
					mod = new Module(
							current.substring(0, current.indexOf("-")),
							ModuleType.RAR, application);
					Logger.trace(location, Severity.INFO, "Child "
							+ ModuleType.RAR + " module " + mod);
					application.addChild(mod);
				} else {
					Logger.trace(location, Severity.INFO,
							"Child module type not recognized");
				}
			}
		}

		return application;
	}

	private ArrayList filterApplications(String[] applications, String container) {
		if (container == null) {
			Logger.trace(location, Severity.DEBUG, "Filtering applications");
		} else {
			Logger.trace(location, Severity.DEBUG,
					"Filtering stand-alone modules for container " + container);
		}

		ArrayList apps = new ArrayList();
		String app = null;
		ApplicationInformation info = null;

		for (int i = 0; i < applications.length; i++) {
			app = applications[i];

			try {
				info = ds.getApplicationInformation(app);
			} catch (RemoteException re) {// $JL-EXC$
				Logger.trace(location, Severity.ERROR,
						"Could not get application information for " + app
								+ " due to " + re.getMessage());
				continue;
			}

			if (info == null) {
				Logger.trace(location, Severity.WARNING,
						"No application information available for " + app);
				continue;
			}

			if (info.isStandAloneModule()) {
				if (container != null && !container.equals("")) {
					Logger.trace(location, Severity.DEBUG,
							"Stand-alone module " + app);
					apps.add(app);
				}
			} else {
				if (container == null) {
					Logger
							.trace(location, Severity.DEBUG, "Application "
									+ app);
					apps.add(app);
				}
			}
		}

		return apps;
	}

	public SerializableFile getClientJar(SAPTargetModuleID[] targetModuleIDs)
			throws SAPRemoteException {
		if (targetModuleIDs == null || targetModuleIDs.length == 0) {
			Logger.trace(location, Severity.DEBUG,
					"No target modules specified");
			return null;
		}

		SAPTargetModuleID tMod = null;
		String mod = null;
		SerializableFile clJar = null;

		try {
			for (int i = 0; i < targetModuleIDs.length; i++) {
				tMod = targetModuleIDs[i];

				if (tMod == null) {
					continue;
				}

				mod = tMod.getModuleID();
				clJar = ds.getClientJar(mod);

				if (clJar == null) {
					Logger.log(location, Severity.INFO,
							"No client jar exists for application " + mod);
				} else {
					Logger.log(location, Severity.INFO, "Obtained client jar "
							+ clJar.getAbsoluteFilePath() + " for application "
							+ mod);
				}

				return clJar;
			}
		} catch (RemoteException re) {
			Logger.trace(location, Severity.ERROR,
					"Could not get application client jar");
			SAPRemoteException sre = new SAPRemoteException(location,
					ExceptionConstants.COULD_NOT_GET_CLIENT_JAR,
					new String[] { mod }, re);
			throw sre;
		}

		return null;
	}

}