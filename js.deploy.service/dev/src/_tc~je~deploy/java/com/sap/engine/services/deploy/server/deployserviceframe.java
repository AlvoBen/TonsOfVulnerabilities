/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
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

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.lib.security.domain.ProtectionDomainFactory;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ContainerManagement;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.event.impl.DeployEventSystem;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.properties.PropManagerFactory;
import com.sap.engine.services.deploy.server.remote.ClusterMonitorHelper;
import com.sap.engine.services.deploy.server.remote.RemoteCaller;
import com.sap.engine.services.deploy.server.utils.concurrent.LockManager;
import com.sap.engine.services.deploy.server.utils.concurrent.impl.EnqueueLockerImpl;
import com.sap.engine.services.deploy.server.utils.concurrent.impl.LockManagerImpl;
import com.sap.tc.logging.Location;

/**
 * Every service has to implement ApplicationServiceFrame interface, which
 * provides methods to start and stop the service. The service manager will
 * instantiate the corresponding service frame using its public non-arguments
 * constructor. The class name of the service frame comes from the file
 * <code>/server/provider.xml</code> of the corresponding SDA archive.
 * 
 * @author Monika Kovachka
 * @version 4.0.0
 */
public final class DeployServiceFrame implements ApplicationServiceFrame {
	private static final Location location = 
		Location.getLocation(DeployServiceFrame.class);
	private DeployServiceContext ctx;
	private DeployServiceImpl deploy;

	/**
	 * The server framework will start the deploy service, using this method.
	 * 
	 * @param appServContext application service context. Not null.
	 * @see com.sap.engine.frame.ApplicationServiceFrame
	 *      #start(com.sap.engine.frame.ApplicationServiceContext)
	 */
	public void start(final ApplicationServiceContext asCtx)
		throws ServiceException {
		// Check the method's contract.
		assert asCtx != null;
		init(asCtx);
		ctx.getReferenceResolver().activate(ctx);
		ctx.getLockManager().activate();
		ctx.getEventSystem().activate(ctx);
		activateDeployService();
		// The name of the DeployService interface is the same as the component
		// name, which is specified in the provider.xml
		asCtx.getContainerContext().getObjectRegistry()
			.registerInterface(deploy);
		try {
			// Registration for the ContainerManagement interface
			asCtx.getContainerContext().getObjectRegistry()
					.registerInterfaceProvider(
							ContainerManagement.INTERFACE_NAME,
							ctx.getContainerManagement());
		} catch (Exception ex) {
			throwException(
					", because could not register provider for interface ", ex);
		}
		try {
			asCtx.getClusterContext().getMessageContext()
				.registerListener(ctx.getRemoteCaller());
		} catch (Exception ex) {
			throwException(", because can not register message listener.", ex);
		}
		try {
			asCtx.getServiceState().registerManagementInterface(
					new DeployRuntimeControlImpl(deploy));
		} catch (Exception ex) {
			throwException(", because can not register management interface.",
					ex);
		}
	}

	private void activateDeployService() throws ServiceException {
		try {
			deploy.activate(ctx);
		} catch (DeploymentException rex) {
			throwException(
					", because could not read application data from DB ", rex);
		} catch (OutOfMemoryError oome) {
			throw oome;
		} catch (ThreadDeath td) {
			throw td;
		} catch (Exception ex) {
			throwException(
					", because could not read application data from DB ", ex);
		}
	}


	/**
	 * The server framework will stop the deploy service, using this method.
	 * 
	 * @see com.sap.engine.frame.ServiceFrame#stop()
	 */
	public void stop() {
		final ApplicationServiceContext appServiceCtx = PropManager
				.getInstance().getAppServiceCtx();

		appServiceCtx.getClusterContext().getMessageContext()
				.unregisterListener();
		appServiceCtx.getContainerContext().getObjectRegistry()
				.unregisterInterface();
		appServiceCtx.getServiceState().unregisterManagementInterface();
		appServiceCtx.getContainerContext().getObjectRegistry()
				.unregisterInterfaceProvider(ContainerManagement.INTERFACE_NAME);
		ctx.getLockManager().deactivate();
		ctx.getEventSystem().deactivate();
		deploy.deactivate();
	}


	// Private methods
	@SuppressWarnings("deprecation")
	private void throwException(String message, Exception ex)
		throws ServiceException {
		throw new ServiceException(ServiceException.SERVICE_NOT_STARTED,
				new String[] { "ASJ.dpl_ds.006139 ",
						PropManager.getInstance().getServiceName(), message },
				ex);
		}


	/**
	 * Set the protection domain during the service start.
	 * 
	 * @param appWorkDir
	 *            application's work directory.
	 * @throws ServiceException
	 */
	private void setProtectionDomain(final String appWorkDir)
		throws ServiceException {
		try {
			ProtectionDomainFactory.setAppsDir(appWorkDir);
		} catch (IOException ioe) {
			throwException(" because could not set '" + appWorkDir
					+ "' into protection domain factory.", ioe);
		}
	}

	private void init(final ApplicationServiceContext asCtx)
		throws ServiceException {
		// Initialize the property manager.
		final PropManager pm = PropManagerFactory.initInstance(asCtx);
		setProtectionDomain(pm.getAppsWorkDir());
		try {
			deploy = DeployServiceFactory.createDeployService();
		} catch (RemoteException ex) {
			throwException("Cannot instantiate deploy service.", ex);
		}

		final LockManager<Component> lockManager = 
			new LockManagerImpl<Component>(
				pm.getThreadSystem(), new EnqueueLockerImpl(), 
				pm.failFastOnLockingAttempt());
		final ReferenceResolver resolver = new ReferenceResolver();
		final DeployEventSystem eventSystem = new DeployEventSystem(
			pm.getClElemName(), pm.isAdditionalDebugInfo());
		final ClusterMonitorHelper cmHelper = new ClusterMonitorHelper(
			asCtx.getClusterContext().getClusterMonitor());
		final RemoteCaller remote = new RemoteCaller(
			cmHelper, deploy, asCtx.getClusterContext().getMessageContext());
		final TransactionManager txManager = 
			new TransactionManager(eventSystem);
		final Containers containers = Containers.getInstance();
		try {
			containers.initContainers();
		} catch (RemoteException ex) {
			throwException("Cannot initialize the containers.", ex);
		}
		final ContainerManagement cManagement = new ContainerManagementImpl(
			deploy, containers, txManager, eventSystem);
		ctx = new DeployServiceContextImpl(
			deploy, deploy, resolver, eventSystem, remote, 
			lockManager, txManager, cmHelper, cManagement);
	}
}
