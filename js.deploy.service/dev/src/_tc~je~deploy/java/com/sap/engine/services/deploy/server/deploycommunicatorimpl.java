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

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.load.LoadContext;
import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.lib.io.hash.Index;
import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.util.NotSupportedException;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.AdditionalAppInfo;
import com.sap.engine.services.deploy.container.AppConfigurationHandler;
import com.sap.engine.services.deploy.container.ApplicationDeployInfo;
import com.sap.engine.services.deploy.container.ApplicationInManualStartUpException;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ComponentReference;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.CyclicReferencesException;
import com.sap.engine.services.deploy.container.DeployCommunicator;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.ExceptionInfo;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;
import com.sap.engine.services.deploy.container.ReferenceType;
import com.sap.engine.services.deploy.container.migration.CMigrationInterface;
import com.sap.engine.services.deploy.container.migration.exceptions.CMigrationException;
import com.sap.engine.services.deploy.container.op.IOpConstants;
import com.sap.engine.services.deploy.container.op.util.FailOver;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.application.AppConfigurationHandlerImpl;
import com.sap.engine.services.deploy.server.application.RuntimeTransaction;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.cache.dpl_info.CompRefGraph;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.ContainerData;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.event.impl.DeployEventSystem;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.properties.ServerState;
import com.sap.engine.services.deploy.server.utils.DSRemoteException;
import com.sap.engine.services.deploy.server.utils.LockUtils;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;
import com.sap.engine.services.deploy.server.utils.concurrent.LockManager;
import com.sap.engine.services.deploy.server.utils.concurrent.impl.CleanRunnable;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Class providing implementation for DeployCommunicator interface. It is used
 * as a "communicator" between containers and Deploy Service. An instance of
 * this class is created per container by Deploy Service, when a container is
 * registered.
 * 
 * Copyright (c) 2003, SAP-AG
 * 
 * @author Rumiana Angelova
 * @version
 */
public final class DeployCommunicatorImpl implements DeployCommunicator {
	private static final Location location = 
		Location.getLocation(DeployCommunicatorImpl.class);
	private final DeployServiceImpl deploy;
	private final DeployEventSystem eventSystem;
	private final TransactionManager tManager;
	private final String containerName;
	private final Component component;

	private final Hashtable<String, ConfigurationHandler> runtimeHandlers;
	private final Hashtable<String, Configuration> runtimeConfigs;
	private final List<String> lockedApps;

	/**
	 * Creates a DeployCommunicator using the specified instance of Deploy
	 * service, application service context and container name.
	 * 
	 * @param deploy
	 *            the Deploy service instance to be used in the communication
	 *            process.
	 * @param containerName
	 *            the name of the container which will communicate with Deploy
	 *            service.
	 * @param eventSystem
	 *            the event system.
	 * @param tManager
	 *            the transaction manager.
	 */
	public DeployCommunicatorImpl(final DeployServiceImpl deploy,
			final ContainerInfo cInfo, final DeployEventSystem eventSystem,
			final TransactionManager tManager) {
		this.deploy = deploy;
		this.eventSystem = eventSystem;
		this.tManager = tManager;
		containerName = cInfo.getName();
		component = cInfo.getComponent();

		runtimeHandlers = new Hashtable<String, ConfigurationHandler>();
		runtimeConfigs = new Hashtable<String, Configuration>();
		lockedApps = new ArrayList<String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#getStatus
	 * (java.lang.String)
	 */
	public byte getStatus(String applicationName) {
		try {
			String status = deploy.getApplicationStatus(applicationName);

			if (status.equals(DeployService.STOPPED_APP_STATUS)) {
				return STOPPED;
			}

			if (status.equals(DeployService.STARTED_APP_STATUS)) {
				return STARTED;
			}

			if (status.equals(DeployService.UPGRADING_APP_STATUS)) {
				return UPGRADING;
			}

			if (status.equals(DeployService.STARTING_APP_STATUS)) {
				return STARTING;
			}

			if (status.equals(DeployService.STOPPING_APP_STATUS)) {
				return STOPPING;
			}
			if (status.equals(DeployService.UNKNOWN_APP_STATUS)) {
				return UNKNOWN;
			} else if (status.equals(DeployService.IMPLICIT_STOPPED_APP_STATUS)) {
				return IMPLICIT_STOPPED;
			}
		} catch (RemoteException rex) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.COMPLEX_ERROR,
				new String[] { "Cannot get the status of application ["
					+ applicationName + "]" }, rex);
			sde.setMessageID("ASJ.dpl_ds.005030");
			SimpleLogger.traceThrowable(Severity.ERROR, location, 
				sde.getLocalizedMessage(), sde);
			return UNKNOWN;
		}
		return UNKNOWN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#getExceptionInfo
	 * (java.lang.String)
	 */
	public ExceptionInfo getExceptionInfo(String applicationName) {
		final DeploymentInfo dInfo = deploy.getApplicationInfo(applicationName);
		ExceptionInfo exceptionInfo = null;
		if (dInfo != null) {
			exceptionInfo = dInfo.getExceptionInfo();
		}
		return exceptionInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * getAppNamesWithReference(java.lang.String)
	 */
	public String[] getAppNamesWithReference(String toLoader) {
		LoadContext loadCtx = PropManager.getInstance().getLoadContext();
		String[] resApps = null;
		try {
			String[] apps = deploy.listApplications(null, null);
			String[] libs = null;

			if (apps != null) {
				List<String> res = new ArrayList<String>();

				for (int i = 0; i < apps.length; i++) {
					libs = loadCtx.getReferences(apps[i]);

					if (libs != null) {
						for (int j = 0; j < libs.length; j++) {
							if (libs[j].equals(toLoader)) {
								res.add(apps[i]);
								break;
							}
						}
					}
				}

				resApps = new String[res.size()];
				res.toArray(resApps);
			}
		} catch (RemoteException rex) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.COMPLEX_ERROR,
				new String[] { "Cannot list applications." }, rex);
			sde.setMessageID("ASJ.dpl_ds.005030");
			SimpleLogger.traceThrowable(Severity.ERROR, location, 
				sde.getLocalizedMessage(), sde);
			return resApps;
		}
		return resApps;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * stopMyApplications(java.lang.String[])
	 */
	public void stopMyApplications(String[] appNames) throws RemoteException {
		// should wait, because otherwise the containers think, that their
		// applications are stopped,
		// when the method returns the control.
		stopMyApplications(appNames, true);
	}

	public void stopMyApplications(String[] appNames, boolean wait)
			throws RemoteException {
		startTransactionsLocal(appNames, wait, containerName,
				DeployConstants.stopApp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * startRuntimeChanges(java.lang.String, boolean)
	 */
	public Configuration startRuntimeChanges(String applicationName,
		boolean lockApplication) throws DeploymentException {
		applicationName = DUtils.getApplicationID(applicationName);
		if (lockApplication) {
			lock(applicationName);
		}
		ConfigurationHandlerFactory factory = PropManager.getInstance()
			.getConfigurationHandlerFactory();
		ConfigurationHandler handler = null;
		Configuration config = null;
		if (factory != null) {
			try {
				handler = factory.getConfigurationHandler();
			} catch (ConfigurationException ce) {
				throw new ServerDeploymentException(
					ExceptionConstants.CANNOT_GET_HANDLER, new String[] {
						DeployConstants.runtimeChanges,
						applicationName, ce.getMessage() }, ce);
			}
		}
		if (handler == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_GET_HANDLER,
				DeployConstants.runtimeChanges, applicationName, "");
			sde.setMessageID("ASJ.dpl_ds.005027");
			throw sde;
		}
		try {
			config = handler.openConfiguration("apps/" + applicationName,
				ConfigurationHandler.WRITE_ACCESS);
		} catch (ConfigurationException ce) {
			try {
				handler.rollback();
			} catch (ConfigurationException cex) {
				SimpleLogger.traceThrowable(Severity.ERROR, location, 
					"ASJ.dpl_ds.006356", cex.getLocalizedMessage(), cex);
			}
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_OPEN_CONFIGURATION, new String[] {
					"apps/" + applicationName, applicationName,
						DeployConstants.runtimeChanges, ce.getMessage() },
					ce);
			sde.setMessageID("ASJ.dpl_ds.005011");
			throw sde;
		}
		runtimeConfigs.put(applicationName, config);
		runtimeHandlers.put(applicationName, handler);
		return config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * startRuntimeChanges(java.lang.String)
	 */
	public Configuration startRuntimeChanges(String applicationName)
		throws DeploymentException {
		return startRuntimeChanges(applicationName, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * getApplicationProvidingResource(java.lang.String, java.lang.String)
	 */
	public String getApplicationProvidingResource(final String resourceName,
		final String resourceType) {
		return Applications.getResourceProviderIfApplication(
			new com.sap.engine.services.deploy.server.dpl_info.module.Resource(
				resourceName, resourceType));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * makeRuntimeChanges(java.lang.String, boolean)
	 */
	public void makeRuntimeChanges(String applicationName,
		boolean needRestartApplication) throws RemoteException {
		applicationName = DUtils.getApplicationID(applicationName);
		Configuration config = runtimeConfigs.get(applicationName);
		ConfigurationHandler handler = runtimeHandlers.get(applicationName);
		RuntimeTransaction runtime = null;
		try {
			if (config == null) {
				throw new RemoteException(
					"ASJ.dpl_ds.006119 Configuration apps/"
						+ applicationName + " is closed!");
			}
			if (handler == null) {
				throw new RemoteException(
					"ASJ.dpl_ds.006120 Handler for runtime changes with application "
						+ applicationName + " is closed!");
			}
			try {
				runtime = new RuntimeTransaction(applicationName, deploy
					.getDeployServiceContext(), containerName, handler,
					config, needRestartApplication);
			} catch (DeploymentException dex) {
				try {
					handler.rollback();
					handler.closeAllConfigurations();
				} catch (ConfigurationException cex) {
					SimpleLogger.traceThrowable(Severity.ERROR, location, 
						"ASJ.dpl_ds.006354", cex.getLocalizedMessage(), cex);
				}
				throw new RemoteException(
					"ASJ.dpl_ds.006121 Error occurred while making runtime changes with application "
					+ applicationName, dex);
			}
			try {
				tManager.registerTransaction(runtime);
				runtime.makeAllPhases();
			} catch (DeploymentException dex) {
				throw new RemoteException(
						"ASJ.dpl_ds.006122 Error occurred while making runtime changes with application "
								+ applicationName, dex);
			} finally {
				try {
					tManager.unregisterTransaction(runtime);
				} catch (ServerDeploymentException e) {
					throw new DSRemoteException(
							"Error occured while unregistering transaction", e);
				}
			}
			runtimeHandlers.remove(applicationName);
			runtimeConfigs.remove(applicationName);
		} finally {
			unlock(applicationName, "makeRuntimeChanges");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * rollbackRuntimeChanges(java.lang.String)
	 */
	public void rollbackRuntimeChanges(String applicationName)
			throws RemoteException {
		applicationName = DUtils.getApplicationID(applicationName);
		Configuration config = runtimeConfigs.get(applicationName);
		ConfigurationHandler handler = runtimeHandlers.get(applicationName);
		try {
			if (config == null) {
				throw new RemoteException(
						"ASJ.dpl_ds.006123 Configuration apps/"
								+ applicationName + " is closed!");
			}
			if (handler == null) {
				throw new RemoteException(
						"ASJ.dpl_ds.006124 Handler for runtime changes with application "
								+ applicationName + " is closed!");
			}
		} finally {
			unlock(applicationName, "rollbackRuntimeChanges");
		}
		// Handler cannot be null here
		try {
			handler.rollback();
			handler.closeAllConfigurations();
		} catch (ConfigurationException ce) {
			SimpleLogger.traceThrowable(Severity.ERROR, location, 
				"ASJ.dpl_ds.006355", ce.getLocalizedMessage(), ce);
		} finally {
			runtimeConfigs.remove(applicationName);
			runtimeHandlers.remove(applicationName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * getMyWorkDirectory(java.lang.String)
	 */
	public String getMyWorkDirectory(String applicationName) throws IOException {
		return deploy.getContainerWorkDir(containerName, applicationName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#getAllAliases ()
	 */
	public String[] getAllAliases() throws DeploymentException {
		String[] aliases = null;
		DeploymentInfo[] infoes = deploy.getAllDeploymentInfoes();
		if (infoes != null) {
			for (int i = 0; i < infoes.length; i++) {
				if (infoes[i] != null) {
					aliases = DUtils.concatArrays(aliases, DUtils
							.getAliases(infoes[i]));
				}
			}
			return aliases;
		} 
			return new String[0];
		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#getAliases
	 * (java.lang.String)
	 */
	public String[] getAliases(String appName) throws DeploymentException {
		final DeploymentInfo info = deploy.getApplicationInfo(appName);
		if (info == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NOT_DEPLOYED, 
				appName, "get its aliases");
			sde.setMessageID("ASJ.dpl_ds.005005");
			throw sde;
		}
		return DUtils.getAliases(info);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * getDeployedApplications()
	 */
	public String[] getDeployedApplications() {
		return Applications.list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#getWarName
	 * (java.lang.String, java.lang.String)
	 */
	public String getWarName(String alias, String appName) {
		if (alias == null) {
			return null;
		}
		DeploymentInfo info = deploy.getApplicationInfo(appName);
		if (info != null) {
			Properties props = null;
			String a = null;
			String tempAlias = null;
			props = info.getProperties();
			if (props != null) {
				Enumeration aliases = props.propertyNames();
				while (aliases.hasMoreElements()) {
					a = (String) aliases.nextElement();
					if (a.startsWith(DeployConstants.WEB)) {
						tempAlias = a.substring(DeployConstants.WEB.length());
						if (tempAlias.equals(alias)) {
							return props.getProperty(a);
						}
					}
				}
			}

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#getAlias(
	 * java.lang.String, java.lang.String)
	 */
	public String getAlias(String warName, String appName) {
		if (warName == null) {
			return null;
		}
		final DeploymentInfo info = deploy.getApplicationInfo(appName);
		if (info != null) {
			Properties props = null;
			props = info.getProperties();
			if (props != null) {
				Enumeration aliases = props.propertyNames();
				String a = null;
				while (aliases.hasMoreElements()) {
					a = (String) aliases.nextElement();
					if (a.startsWith("web:")
							&& props.getProperty(a).equals(warName)) {
						return a.substring("web:".length());
					}
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * startMyApplications(java.lang.String[])
	 */
	public void startMyApplications(final String[] appNames)
		throws RemoteException {
		if (appNames == null || appNames.length == 0) {
			return;
		}
		final String deployConstant = DeployConstants.startApp;
		// @See StartTransaction->isAppStartAcceptable(DeploymentInfo dInfo)
		final byte state = PropManager.getInstance().getClusterMonitor()
				.getCurrentParticipant().getState();
		if (state == ClusterElement.RUNNING) {
			final ServerState sState = PropManager.getInstance()
					.getServerState();
			if (sState.isAppStartNOTAcceptable()) {
				if (location.bePath()) {
					SimpleLogger.trace(Severity.PATH, location, null,
						"The [{0}] operation is not acceptable, " +
						"because the server mode and action are [{1}]." +
						"The concerned applications are [{2}]",
						deployConstant, sState.getName(), 
						CAConvertor.toString(appNames, ""));
				}
			} else {
				// should NOT wait, because otherwise might face deadlocks in
				// services start.
				boolean wait = false;
				startTransactionsLocal(
					appNames, wait, containerName, deployConstant);
			}
		} else {
			if (location.bePath()) {
				SimpleLogger.trace(Severity.PATH, location, null,
					"The [{0}] operation is not acceptable, because the server state is [{1}]. The concerned applications are [{2}]",
					deployConstant, ClusterElement.STATES[state],
					CAConvertor.toString(appNames, ""));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.DeployCommunicator#getMyApplications
	 * ()
	 */
	public String[] getMyApplications() {
		String[] res = null;
		try {
			res = deploy.getApplicationsForContainer(containerName);
			if (res == null) {
				res = new String[0];
			}
		} catch (DeploymentException dex) {
			SimpleLogger.traceThrowable(Severity.ERROR, location, 
				"ASJ.dpl_ds.006353", dex.getLocalizedMessage(), dex);
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * isStandAloneApplication(java.lang.String)
	 */
	public boolean isStandAloneApplication(String appName)
			throws DeploymentException {
		final DeploymentInfo info = deploy.getApplicationInfo(appName);
		if (info == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NOT_DEPLOYED, 
				appName, "check whether it is stand alone");
			sde.setMessageID("ASJ.dpl_ds.005005");
			throw sde;
		}
		return info.isStandAloneArchive();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * startApplicationLocalAndWait(java.lang.String)
	 */
	public void startApplicationLocalAndWait(final String appName)
		throws RemoteException {
		checkApplicationStartUpMode(appName);
		try {
			deploy.startApplicationLocalAndWait(appName, null);
		} catch (DeploymentException dex) {
			throw new RemoteException(
				"ASJ.dpl_ds.006125 Error occurred while starting application locally and wait.",
				dex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * stopApplicationLocalAndWait(java.lang.String)
	 */
	public void stopApplicationLocalAndWait(String appName)
		throws RemoteException {
		try {
			deploy.stopApplicationLocalAndWait(appName, null);
		} catch (DeploymentException dex) {
			throw new RemoteException(
				"ASJ.dpl_ds.006126 Error occurred while stopping application locally and wait.",
				dex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * startApplicationAndWait(java.lang.String)
	 */
	public void startApplicationAndWait(final String appName)
		throws RemoteException {
		startApplicationAndWait(appName, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * startApplicationAndWait(java.lang.String, java.lang.String[])
	 */
	public void startApplicationAndWait(final String appName,
		final String[] serverNames) throws RemoteException {
		checkApplicationStartUpMode(appName);
		// Do not check for authorization here, because the deploy communicator
		// needs to be able to start the application in every thread.
		deploy.startApplicationAndWait(appName, serverNames, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * stopApplicationAndWait(java.lang.String)
	 */
	public void stopApplicationAndWait(String appName) throws RemoteException {
		stopApplicationAndWait(appName, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * stopApplicationAndWait(java.lang.String, java.lang.String[])
	 */
	public void stopApplicationAndWait(String appName, String[] serverNames)
		throws RemoteException {
		deploy.stopApplicationAndWait(appName, serverNames, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * startApplicationLocal(java.lang.String)
	 */
	public void startApplicationLocal(final String appName)
		throws RemoteException {
		checkApplicationStartUpMode(appName);
		try {
			deploy.startApplicationLocal(appName);
		} catch (DeploymentException dex) {
			throw new RemoteException(
				"ASJ.dpl_ds.006128 Error occurred while starting application locally.",
				dex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * stopApplicationLocal(java.lang.String)
	 */
	public void stopApplicationLocal(String appName) throws RemoteException {
		try {
			deploy.stopApplicationLocal(appName);
		} catch (DeploymentException dex) {
			throw new RemoteException(
					"ASJ.dpl_ds.006129 Error occurred while stopping application locally.",
					dex);
		}
	}

	private void checkApplicationStartUpMode(String appName)
		throws ApplicationInManualStartUpException {
		final LockManager<Component> lockManager = deploy
			.getDeployServiceContext().getLockManager();
		if (location.beInfo()) {
			location.infoT(containerName + " tries to start " + appName
				+ " in the thread " + lockManager.dumpCurrentThread());
		}

		final DeploymentInfo info = deploy.getApplicationInfo(appName);
		if (info != null && info.getStartUp() == DeploymentInfo.STARTUP_MANUAL)
			throw new ApplicationInManualStartUpException(appName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.DeployCommunicator#startApplication
	 * (java.lang.String)
	 */
	public void startApplication(final String appName) throws RemoteException {
		checkApplicationStartUpMode(appName);
		deploy.startApplication(appName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.DeployCommunicator#stopApplication
	 * (java.lang.String)
	 */
	public void stopApplication(String appName) throws RemoteException {
		deploy.stopApplication(appName);
	}

	/**
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#addAloneComponent
	 *      (java.lang.String, java.lang.String, java.lang.String)
	 * @deprecated
	 */
	@Deprecated
	public void addAloneComponent(String componentName, String resourceType,
		String loaderName) throws DeploymentException {
		assert loaderName == null;
		addAloneResource(componentName, resourceType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * removeAloneComponent(java.lang.String, java.lang.String)
	 */
	public void removeAloneComponent(String resourceName, String resourceType)
		throws DeploymentException {
		removeAloneResource(resourceName, resourceType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * addNonPersistentApplicationInfo(java.lang.String, boolean,
	 * com.sap.engine.services.deploy.container.ApplicationDeployInfo)
	 */
	public void addNonPersistentApplicationInfo(String originalAppName,
			boolean isStandAloneApplication,
			ApplicationDeployInfo applicationInfo) throws DeploymentException {
		throw new NotSupportedException(
				"ASJ.dpl_ds.006130 This method is no longer supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * removeNonPersistentApplicationInfo(java.lang.String)
	 */
	public void removeNonPersistentApplicationInfo(String appName)
		throws DeploymentException {
		appName = DUtils.getApplicationID(appName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * registerAsExternalContainer()
	 */
	public void registerAsExternalContainer() {
		// TODO - delete this method
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * unregisterAsExternalContainer()
	 */
	public void unregisterAsExternalContainer() {
		// TODO - delete this method
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * getAppsReferencedComponent(java.lang.String)
	 */
	public String[] getAppsReferencedComponent(String component) {
		return deploy.getAppsReferencedComponent(component);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.DeployCommunicator#addReference
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	public void addReference(String from, String to, String refType) {
		ReferenceObject ref = new ReferenceObject();
		ref.setCompositeName(to);
		if (ReferenceObject.REF_TYPE_HARD.equals(refType)
				|| ReferenceObject.REF_TYPE_WEAK.equals(refType)) {
			ref.setReferenceType(refType);
		}
		ref.setReferenceTargetType(ReferenceObject.REF_TARGET_TYPE_APPLICATION);
		Applications.get(from).addReference(ref);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.DeployCommunicator#addReferences
	 * (java.lang.String,
	 * com.sap.engine.services.deploy.container.ReferenceObjectIntf[],
	 * com.sap.engine.services.deploy.container.ReferenceType[])
	 */
	public void addReferences(String applicationName,
		ReferenceObjectIntf refObjs[], ReferenceType characteristics[])
		throws DeploymentException, CyclicReferencesException {
		if (location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
				"Begin adding [{0}] references to [{1}] from container [{2}].",
				refObjs.length,
				applicationName, containerName);
		}
		try {
			validateArgs(applicationName, refObjs, characteristics);
			final DeploymentInfo oldDInfo = obtainDeploymentInfo(applicationName);
			if (location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"The application [{0}] is in state [{1}].",
					applicationName,
					oldDInfo.getStatus());
			}
			final DeploymentInfo newDInfo = modifyDeploymentInfo(
				oldDInfo, refObjs, characteristics);
			checkForCycles(newDInfo, oldDInfo);
			if (location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"Successfully added [{0}] references to [{1}] from container [{2}].",
					refObjs.length,
					applicationName, containerName);
			}
		} catch (DeploymentException dEx) {
			DSLog.logErrorThrowable(
				location, 
				"ASJ.dpl_ds.000480",
				"Failed to add [{0}] references to [{1}] from container [{2}].",
				dEx, refObjs.length, applicationName, containerName);
			throw dEx;
		} finally {
			if (location.beDebug()) {
				DSLog.traceDebug(location, "The deployment info for [{0}]. [{1}]",
					applicationName,
					CAConvertor.toString(
						deploy.getApplicationInfo(applicationName), ""));
			}
		}
	}

	private void checkForCycles(final DeploymentInfo newDInfo,
			final DeploymentInfo oldDInfo) throws CyclicReferencesException {

		if (location.beDebug()) {
			DSLog.traceDebug(location, "Will check for cycles the updated info of [{0}]. [{1}]",
				newDInfo.getApplicationName(),
				CAConvertor.toString(newDInfo, ""));
		}
		try {
			deploy.addApplicationInfo(newDInfo.getApplicationName(), newDInfo);
			Applications.getReferenceGraph().cycleCheck(
					Component.create(newDInfo.getApplicationName()));
		} catch (com.sap.engine.lib.refgraph.CyclicReferencesException e) {
			deploy.addApplicationInfo(oldDInfo.getApplicationName(), oldDInfo);
			throw new CyclicReferencesException(e);
		}
	}

	private DeploymentInfo modifyDeploymentInfo(final DeploymentInfo oldDInfo,
			final ReferenceObjectIntf[] refObjs,
			final ReferenceType[] characteristics)
			throws ServerDeploymentException {

		final DeploymentInfo newDInfo;
		try {
			newDInfo = (DeploymentInfo) oldDInfo.clone();
		} catch (CloneNotSupportedException e) {
			final ServerDeploymentException sdEx = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION, new String[] {
							"add references", oldDInfo.getApplicationName() },
					e);
			throw sdEx;
		}

		final ReferenceObject theRefObjs[] = new ReferenceObject[refObjs.length];
		for (int i = 0; i < refObjs.length; i++) {
			theRefObjs[i] = new ReferenceObject(refObjs[i]);
			theRefObjs[i].setCharacteristic(characteristics[i]);
			newDInfo.addReference(theRefObjs[i]);
		}
		return newDInfo;
	}

	private void validateArgs(final String applicationName,
			final ReferenceObjectIntf refObjs[],
		final ReferenceType characteristics[]) {

		ValidateUtils.nullValidator(applicationName, "applicationName");
		ValidateUtils.nullValidator(refObjs, "ReferenceObjectIntf[]");
		ValidateUtils.nullValidator(characteristics, "ReferenceType[]");

		if (refObjs.length != characteristics.length) {
			throw new IllegalArgumentException(
					"ASJ.dpl_ds.006104 The given refObjs.length=="
							+ refObjs.length + " and characteristics.length== "
							+ characteristics.length
							+ ". Their length must be equal.");
		}

		for (int i = 0; i < characteristics.length; i++) {
			final ReferenceType rType = characteristics[i];
			if (rType.isFunctional() && !rType.isClassloading()
					&& !rType.isPersistent()) {
				continue;
			} 
				throw new UnsupportedOperationException(
						"ASJ.dpl_ds.006105 The characteristics[" + i + "]= "
								+ rType + ".");
		}
	}

	private DeploymentInfo obtainDeploymentInfo(final String appName) {
		final DeploymentInfo dInfo = deploy.getApplicationInfo(appName);
		ValidateUtils.nullValidator(dInfo, appName
				+ " is not deployed and its DeploymentInfo");
		if (location.beDebug()) {
			DSLog.traceDebug(location, "The initial deployment info for [{0}]. [{1}]",
				appName, CAConvertor.toString(dInfo, ""));
		}
		return dInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.DeployCommunicator#setStartUpMode
	 * (java.lang.String, int)
	 */
	public void setStartUpMode(String appName, int mode) throws RemoteException {
		final DeploymentInfo dInfo = deploy.getApplicationInfo(appName);
		if (dInfo == null) {
			throw new RemoteException("ASJ.dpl_ds.006131 Application "
					+ appName
					+ " doesn't exist. Please use fully qualified name.");
		}
		AdditionalAppInfo addAppInfo = new AdditionalAppInfo();
		addAppInfo.setFailOver(dInfo.getFailOver());
		addAppInfo.setJavaVersion(dInfo.getJavaVersion(), dInfo
				.isCustomJavaVersion());
		addAppInfo.setStartUp(mode);
		deploy.setAdditionalAppInfo(appName, addAppInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.DeployCommunicator#getStartUpMode
	 * (java.lang.String)
	 */
	public int getStartUpMode(String appName) throws RemoteException {
		final DeploymentInfo dInfo = deploy.getApplicationInfo(appName);
		if (dInfo == null) {
			throw new RemoteException("ASJ.dpl_ds.006132 Application "
					+ appName
					+ " doesn't exist. Please use fully qualified name.");
		}
		return dInfo.getStartUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * getApplicationComponents(java.lang.String)
	 */
	public String[] getApplicationComponents(String appName)
			throws DeploymentException {
		final DeploymentInfo dInfo = deploy.getApplicationInfo(appName);
		if (dInfo == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.NOT_DEPLOYED, new String[] { appName,
							"get its additional modules" });
			sde.setMessageID("ASJ.dpl_ds.005005");
			throw sde;
		}
		Set<com.sap.engine.services.deploy.server.dpl_info.module.Resource> 
			providedResources = dInfo.getProvidedResources(containerName);
		String[] result = new String[providedResources.size()];
		int i = 0;
		for (final com.sap.engine.services.deploy.server.dpl_info.module.Resource r : 
			providedResources) {
			result[i++] = r.getName();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.DeployCommunicator#getAppConfigPath
	 * (java.lang.String)
	 */
	public String getAppConfigPath(String appname) {
		return "apps/" + appname;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.DeployCommunicator#startModification
	 * (java.lang.String)
	 */
	public void startModification(String application) throws LockException {
		deploy.startAppModification(application);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.DeployCommunicator#modificationDone
	 * (java.lang.String)
	 */
	public void modificationDone(String application) {
		deploy.appModificationDone(application);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * getAppConfigurationForReadAccess(java.lang.String)
	 */
	public Configuration getAppConfigurationForReadAccess(String applicationName)
			throws ConfigurationException {
		ConfigurationHandlerFactory factory = PropManager.getInstance()
				.getConfigurationHandlerFactory();
		ConfigurationHandler handler = null;
		handler = factory.getConfigurationHandler();
		return handler.openConfiguration("apps" + "/" + applicationName,
				ConfigurationHandler.READ_ACCESS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.DeployCommunicator#setMigrator
	 * (com.sap.engine.services.deploy.container.migration.CMigrationInterface)
	 */
	public void setMigrator(CMigrationInterface migrator)
		throws CMigrationException {
		deploy.registerMigrator(containerName, migrator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.DeployCommunicator#
	 * getRuntimeChangesConfigHandler(java.lang.String)
	 */
	public AppConfigurationHandler getRuntimeChangesConfigHandler(String appName) {
		if (runtimeHandlers != null &&
			runtimeHandlers.get(appName) != null) {
			return new AppConfigurationHandlerImpl(
				runtimeHandlers.get(appName));
		}
		return null;
	}

	private void lock(String applicationName) throws DeploymentException {
		deploy.lockApplication(applicationName, DeployConstants.runtimeChanges);
		lockedApps.add(applicationName);
		if (DSLog.isInfoTraceable()) {
			DSLog
					.traceInfo(
							location, 
							"ASJ.dpl_ds.000483",
							"Container [{0}] locked application [{1}] for making runtime changes.",
							containerName, applicationName);
		}
	}

	private void unlock(String applicationName, String operation)
			throws RemoteException {
		if (!lockedApps.contains(applicationName)) {
			return;
		}
		try {
			LockUtils.unlock(applicationName,
				LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE);
			lockedApps.remove(applicationName);
			if (location.beInfo()) {
				DSLog.traceInfo(location, "ASJ.dpl_ds.000484",
						"Container [{0}] unlocked application [{1}]",
						containerName, applicationName);
			}
		} catch (TechnicalLockException lex) {
			final ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "[" + operation + "] of [" + applicationName
							+ "]" }, lex);
			sde.setMessageID("ASJ.dpl_ds.005082");
			DSLog.logErrorThrowable(location, sde);
			throw new RemoteException(sde.getLocalizedMessage(), sde);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.DeployCommunicator#getFailOver
	 * (java.lang.String)
	 */
	public FailOver getFailOver(String applicationName)
			throws DeploymentException {
		final DeploymentInfo dInfo = deploy.getApplicationInfo(applicationName);
		if (dInfo == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.NOT_DEPLOYED, new String[] {
							applicationName, "get its fail over" });
			sde.setMessageID("ASJ.dpl_ds.005005");
			throw sde;
		}
		return dInfo.getFailOver();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.DeployCommunicator#
	 * getContainerDeployedComponents(java.lang.String)
	 */
	public Map<String, String[]> getContainerDeployedComponents(
			String applicationName) throws DeploymentException {
		DeploymentInfo di = deploy.getApplicationInfo(applicationName);
		if (di == null) {
			throw new DeploymentException("ASJ.dpl_ds.006133 The application "
					+ applicationName + " is not deployed.");
		}
		HashMap<String, String[]> result = new HashMap<String, String[]>(di
				.getCNameAndCData().size());

		String[] providedResourcesNames;
		for (final ContainerData cd : di.getCNameAndCData().values()) {
			providedResourcesNames = new String[cd.getProvidedResources()
					.size()];
			int i = 0;
			for (final com.sap.engine.services.deploy.server.dpl_info.module.Resource r : 
				cd.getProvidedResources()) {
				providedResourcesNames[i++] = r.getName();
			}
			result.put(cd.getContName(), providedResourcesNames);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.DeployCommunicator#
	 * getAdditionalAppProps(java.lang.String)
	 */
	public Properties getAdditionalAppProps(String applicationName)
			throws DeploymentException {
		DeploymentInfo di = deploy.getApplicationInfo(applicationName);
		if (di == null) {
			throw new DeploymentException("ASJ.dpl_ds.006134 The application "
					+ applicationName + " is not deployed.");
		}
		// get only the additional DS specific properties
		Properties additionalProps = new Properties();
		Properties props = di.getProperties();
		Enumeration propKeys = props.keys();
		while (propKeys.hasMoreElements()) {
			String nextKey = (String) propKeys.nextElement();
			if (nextKey.startsWith(IOpConstants.DS_ADDITIONAL_NS)) {
				additionalProps.put(nextKey, props.get(nextKey));
			}
		}
		return additionalProps;
	}

	private void startTransactionsLocal(String[] appNames, boolean wait,
		String whoCausedGroupOperation, String operation) {
		FinishListener listener = new FinishListener(appNames, wait,
				eventSystem, deploy.getDeployServiceContext(),
				whoCausedGroupOperation, operation);
		listener.makeOperation();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.DeployCommunicator#
	 * isTimeStatisticSwitchedOn()
	 */
	public boolean isTimeStatisticSwitchedOn() {
		return PropManager.getInstance().isAdditionalDebugInfo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.DeployCommunicator#hasReference
	 * (java.lang.String, java.lang.String)
	 */
	public boolean hasReference(String fromApplication, String toApplication) {
		final Component fromComp = Component.create(fromApplication);
		Set<Edge<Component>> referencesTo = Applications.getReferenceGraph()
				.getReferencesToOthersFrom(fromComp);

		for (Edge<Component> edge : referencesTo) {
			final Component predecessor = edge.getSecond();
			if (predecessor.getName().equals(toApplication) &&
				predecessor.getType() == Component.Type.APPLICATION) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.DeployCommunicator#getReferences
	 * (java.lang.String,
	 * com.sap.engine.services.deploy.container.ComponentReference)
	 */
	public Set<String> getReferences(final String appName,
		final Component.Type referenceType) {

		final Set<String> resultReferences = new HashSet<String>();
		final Component appComp = new Component(appName,
			Component.Type.APPLICATION);
		final Set<Edge<Component>> referencesTo = Applications
				.getReferenceGraph().getReferencesToOthersFrom(appComp);

		for (Edge<Component> edge : referencesTo) {
			final Component node = edge.getSecond();
			if (!node.equals(CompRefGraph.RESOURCE_NOT_PROVIDED)
					&& node.getType() == referenceType) {
				resultReferences.add(node.toString());
			}
		}
		return resultReferences;
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#getReferences(java.lang.String, com.sap.engine.services.deploy.container.ComponentReference)
	 */
	@Deprecated
	public Set<String> getReferences(String appName,
        ComponentReference referenceType) {
		SimpleLogger.trace(Severity.WARNING, location, null,
			"getReferences(java.lang.String, " +
			"com.sap.engine.services.deploy.container.ComponentReference) is " +
			"deprecated. Use getReferences(java.lang.String, " +
			"com.sap.engine.services.deploy.container.Component.Type)");
		return getReferences(
			appName, Component.Type.valueOf(referenceType.name()));
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * getDeployedFileNames(java.lang.String) The client has to hold the method
	 * contract, where appName is not null and corresponds to an deployed
	 * application.
	 */
	public Set<String> getDeployedFileNames(final String appName) {
		final ContainerData cData = Applications.get(appName)
				.getCNameAndCData().get(containerName);
		return (cData != null ? cData.getDeployedFileNames() : null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.DeployCommunicator#addAloneResource
	 * (java.lang.String, java.lang.String)
	 */
	public void addAloneResource(final String resourceName,
		final String resourceType) throws DeploymentException {
		if (component.getType() != Component.Type.SERVICE) {
			DeploymentException de = 
				new DeploymentException(
					ExceptionConstants.APP_CONTAINER_RESTRICTION,
					new String[] { containerName });
			de.setMessageID("ASJ.dpl_ds.005021");
			throw de;
		}
		deploy.getDeployServiceContext().getReferenceResolver()
			.aloneResourceIsAvailable(
				new Resource(resourceName, resourceType), component);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * removeAloneResource(java.lang.String, java.lang.String)
	 */
	public void removeAloneResource(String resourceName, String resourceType) {
		// This method is not supported.
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.container.DeployCommunicator#
	 * getIndexFS(java.lang.String)
	 */
	public Index getIndexFS(String appName) {
		if (deploy.getApplicationInfo(appName) != null) {
			return deploy.getApplicationInfo(appName).getIndexFS();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.DeployCommunicator#execute(java
	 * .lang.Runnable)
	 */
	public void execute(final Runnable runnable) throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		PropManager.getInstance().getThreadSystem().startCleanThread(
			new CleanRunnable<Component>(
				runnable, latch, 
				deploy.getDeployServiceContext().getLockManager()), 
			false, true);
		latch.await();
	}
}