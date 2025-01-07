package com.sap.engine.services.deploy.server;

import java.util.ArrayList;
import java.util.Properties;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.event.ServiceEventListener;
import com.sap.engine.frame.container.event.AdminContainerEventListener;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.state.ManagementInterface;
import com.sap.engine.interfaces.cross.CrossInterface;
import com.sap.engine.interfaces.shell.ShellInterface;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.library.DeployLibTransaction;
import com.sap.engine.services.deploy.server.prl.ParallelOperator;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.tc.logging.Location;

/**
 * This class is used internally by DeployServiceImpl to listen for admin
 * container events and for service events.
 * 
 * @author Monika Kovachka, Rumiana Angelova
 * @version 6.25
 */
public final class ClusterServicesAdapter 
	implements AdminContainerEventListener, ServiceEventListener {
	
	private static final Location location = 
		Location.getLocation(ClusterServicesAdapter.class);

	private final DeployServiceImpl deploy;

	private ReferenceResolver resolver;
	private InitialStartTrigger isTrigger;
	private CrossInterface crossInterface;

	public ClusterServicesAdapter(final DeployServiceImpl deploy,
		final InitialStartTrigger isTrigger) {
		this.deploy = deploy;
		this.isTrigger = isTrigger;
	}

	/**
	 * Is fired when all AS Java services are started or timed out during the
	 * attempt to start. Here we are informing the
	 * @see com.sap.engine.frame.container.event.ContainerEventListener
	 * 		#containerStarted()
	 */
	public void containerStarted() {
		isTrigger.containerStarted();
		// Free the initial start trigger to be GC
		isTrigger = null;
	}

	/**
	 * Notifies DS that a cluster element is joined to the cluster.
	 * @see com.sap.engine.frame.cluster.event.ServiceEventListener
	 * 		#serviceStarted(com.sap.engine.frame.cluster.ClusterElement)
	 */
	public void serviceStarted(ClusterElement element) {
		deploy.elementJoin(element);
	}

	/**
	 * Notifies DS that a cluster element is stopped.
	 * @see com.sap.engine.frame.cluster.event.ServiceEventListener
	 * 		#serviceStopped(com.sap.engine.frame.cluster.ClusterElement)
	 */
	public void serviceStopped(ClusterElement element) {
		deploy.elementLoss(element);
	}

	/*
	 * (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener
	 * 		#serviceStarted(java.lang.String, java.lang.Object)
	 */
	public void serviceStarted(String serviceName, Object serviceInterface) {
		if (serviceName.equals(DeployConstants.SERVICE_BASICADMIN)) {
			deploy.registerAllManagedObjects();
		}
		// Enable the service removing it from the negative cache.
		componentIsAvailable(
			new Component(serviceName, Component.Type.SERVICE));
	}

	/**
	 * Service stopped. 
	 * @see com.sap.engine.frame.container.event.ContainerEventListener
	 * 		#beginServiceStop(java.lang.String)
	 */
	public void beginServiceStop(String serviceName) {
		if (serviceName.equals(DeployConstants.SERVICE_BASICADMIN)) {
			deploy.unregisterAllManagedObjects();
		}
		componentIsGettingUnavailable(
			new Component(serviceName, Component.Type.SERVICE));
	}

	/*
	 * (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener
	 *   	#interfaceAvailable(java.lang.String, java.lang.Object)
	 */
	public void interfaceAvailable(String interfaceName, Object interfaceImpl) {
		if (interfaceName.equals(DeployConstants.INTERFACE_SHELL)) {
			ShellInterface shell = (ShellInterface) interfaceImpl;
			if (deploy.getCommands() != null) {
				shell.registerCommands(deploy.getCommands());
			}
		} else if (interfaceName.equals(DeployConstants.INTERFACE_CROSS)) {
			crossInterface = (CrossInterface) interfaceImpl;
			if (deploy.getDeployService() != null) {
				crossInterface.setInitialObject(PropManager.getInstance()
						.getServiceName(), deploy.getDeployService());
			}
		}
		componentIsAvailable(
			new Component(interfaceName, Component.Type.INTERFACE));
	}

	/*
	 * (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener
	 *   	#interfaceNotAvailable(java.lang.String)
	 */
	public void interfaceNotAvailable(String interfaceName) {
		if (interfaceName.equals(DeployConstants.INTERFACE_CROSS)) {
			crossInterface = null;
		}
		componentIsGettingUnavailable( new Component(
			interfaceName, Component.Type.INTERFACE));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.frame.container.event.AdminContainerEventListener
	 *   #componentLoaded(java.lang.String, byte)
	 */
	public void componentLoaded(String componentName, byte componentType) {
		if (componentType == ContainerEventListener.LIBRARY_TYPE) {
			componentIsAvailable(
				new Component(componentName, Component.Type.LIBRARY));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.frame.container.event.AdminContainerEventListener#
	 * beginComponentUnload(java.lang.String, byte)
	 */
	public void beginComponentUnload(String componentName, byte componentType) {
		if (componentType == ContainerEventListener.LIBRARY_TYPE) {
			componentIsGettingUnavailable(
				new Component(componentName, Component.Type.LIBRARY));
		}
	}

	public void activate(final ReferenceResolver resolver) {
		this.resolver = resolver;
	}
	
	/**
	 * Called by DeployServiceImpl during service stop.
	 */
	public void deactivate() {
		ApplicationServiceContext sdc = 
			PropManager.getInstance().getAppServiceCtx();
		CrossInterface cross = (CrossInterface) sdc.getContainerContext()
			.getObjectRegistry().getProvidedInterface(
				DeployConstants.INTERFACE_CROSS);
		if (cross != null) {
			cross.removeInitialObject(
				PropManager.getInstance().getServiceName());
		}
	}

	/**
	 * Notification that the current server will be stopped for shutdown. Deploy
	 * Service should stop all applications that are currently started
	 * 
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#beginContainerStop()
	 */
	public void beginContainerStop() {
		deploy.markForShutdown();
		if (location.beDebug()) {
			DSLog.traceDebug(location, "beginContainerStop event recieved: will stop all applications");
		}
		(new ParallelOperator(deploy)).finalParallelApplicationStop();
	}

	/*
	 * (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener
	 * 		#serviceNotStarted(java.lang.String)
	 */
	public void serviceNotStarted(String serviceName) {
		// Not handled event.
	}

	/*
	 * (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener
	 * 		#serviceStopped(java.lang.String)
	 */
	public void serviceStopped(String serviceName) {
		ArrayList<String> containerNames = Containers.getInstance()
			.getContainersForComponent(serviceName);
		if (containerNames != null) {
			DSLog.logWarning(
				location, 
				"ASJ.dpl_ds.008725",
				"The service [{0}] is stopped before all containers " +
				"that it provides have been unregistered", serviceName);
			for (String container : containerNames) {
				if (deploy.getContainer(container) != null) {
					deploy.getDeployServiceContext().getContainerManagement()
						.unregisterContainer(container);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener
	 * 		#setServiceProperty(java.lang.String, java.lang.String)
	 */
	public boolean setServiceProperty(String key, String value) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener
	 * 		#setServiceProperties(java.util.Properties)
	 */
	public boolean setServiceProperties(Properties serviceProperties) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.AdminContainerEventListener
	 * 		#componentRegistered(java.lang.String, byte)
	 */
	public void componentRegistered(String componentName, byte componentType) {
		// Not handled event.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.frame.container.event.AdminContainerEventListener
	 * 		#componentResolved(java.lang.String, byte)
	 */
	public void componentResolved(String componentName, byte componentType) {
		// Not handled event.		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.frame.container.event.AdminContainerEventListener
	 * 		#componentUnresolved(java.lang.String, byte)
	 */
	public void componentUnresolved(String componentName, byte componentType) {
		// Not handled event.		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.frame.container.event.AdminContainerEventListener
	 * 		#componentUnloaded(java.lang.String, byte)
	 */
	public void componentUnloaded(String componentName, byte componentType) {
		// Not handled event.		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.frame.container.event.AdminContainerEventListener
	 * 		#beginComponentUndeploy(java.lang.String, byte)
	 */
	public void beginComponentUndeploy(String componentName, 
		byte componentType) {
		// Not handled event.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.frame.container.event.AdminContainerEventListener
	 * 		#componentUndeployed(java.lang.String, byte)
	 */
	public void componentUndeployed(String componentName, byte componentType) {
		// Not handled event.
	}

	/*
	 * (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.AdminContainerEventListener
	 * 		#managementInterfaceRegistered(java.lang.String,
	 * 			com.sap.engine.frame.state.ManagementInterface)
	 */
	public void managementInterfaceRegistered(String serviceName,
		ManagementInterface managementInterface) {
		// Not handled event.
	}

	/*
	 * (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.AdminContainerEventListener
	 * 		#managementInterfaceUnregistered(java.lang.String)
	 */
	public void managementInterfaceUnregistered(String serviceName) {
		// Not handled event.		
	}

	/*
	 * (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.AdminContainerEventListener
	 * 		#componentNotLoaded(java.lang.String, byte)
	 */
	public void componentNotLoaded(String componentName, byte componentType) {
		// Not handled event.
	}
	
	private void componentIsAvailable(final Component component) {
		// Enable the component removing it from the negative cache.
		resolver.getComponentsRepository().enableComponent(component);
		DeployLibTransaction.notifyAllComponentLoadedEvent(component.getName());
		resolver.componentIsAvailable(component);		
	}
	
	private void componentIsGettingUnavailable(final Component component) {
		// Disable the component adding it to the negative cache.
		resolver.getComponentsRepository().disableComponent(component);
		resolver.componentIsGettingUnavailable(component);
	}

}