package com.sap.engine.services.deploy.server.cache.resources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.sap.engine.frame.container.monitor.ComponentMonitor;
import com.sap.engine.frame.container.monitor.InterfaceMonitor;
import com.sap.engine.frame.container.monitor.LibraryMonitor;
import com.sap.engine.frame.container.monitor.ServiceMonitor;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.op.util.StartUp;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.server.ApplicationStatusResolver;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.properties.PropManager;

/**
 * This a cache for all components. Here we check that a given component
 * (library, service or interface) is online or not, and we process references
 * to components.
 * 
 * @author Emil Dinchev
 */
public class ComponentsRepository {

	/**
	 * Read write lock to handle concurrent access to the set of disabled
	 * components. So we can guarantee parallel read and mutual exclusive write.
	 * When the write lock is locked, only one thread has access to the set of
	 * disabled components.
	 */
	private final ReadWriteLock rwLock;

	/**
	 * Set of explicitly disabled components. Here are included all components
	 * which are explicitly disabled. Components are added or removed from this
	 * set via the events in <tt>ClusterServiceAdapter</tt>.
	 */
	private final Set<Component> explicitlyDisabled;

	public ComponentsRepository() {
		explicitlyDisabled = new HashSet<Component>();
		rwLock = new ReentrantReadWriteLock();
	}

	/**
	 * This method checks whether the given component is explicitly disabled. A
	 * given component will be added to the set of explicitly disabled 
	 * components if it is in process of being stopped. It will be removed from
	 * this list when it is being manually started.
	 * @param component the given component.
	 * @return <tt>true</tt> if the given component is explicitly disabled and 
	 * cannot be started right now. 
	 */
	public boolean isExplicitlyDisabled(final Component component) {
		Lock readLock = rwLock.readLock();
		readLock.lock();
		try {
			return explicitlyDisabled.contains(component);
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Informs the component repository that the given component has to be
	 * included in the set of explicitly disabled component. This method will be
	 * removed, when the new internal state of the libraries, services and
	 * interfaces is available (we will be able directly to check their state).
	 * 
	 * @param component the component to be disabled.
	 */
	public void disableComponent(final Component component) {
		Lock writeLock = rwLock.writeLock();
		writeLock.lock();
		try {
			explicitlyDisabled.add(component);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Informs the component repository that the given component has to be
	 * removed from the list of explicitly stopped components.
	 * 
	 * @param component the component to be enabled.
	 */
	public void enableComponent(final Component component) {
		Lock writeLock = rwLock.writeLock();
		writeLock.lock();
		try {
			explicitlyDisabled.remove(component);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Check whether the component is online.
	 * 
	 * @param componentName
	 * @param componentType
	 * @return true if the component is not included in offline set and is
	 *         available.
	 */
	public boolean isComponentEnabled(final Component component) {
		if (isExplicitlyDisabled(component)) {
			return false;
		}
		switch (component.getType()) {
		case INTERFACE:
			return isInterfaceEnabled(component.getName());
		case LIBRARY:
			return isLibraryEnabled(component.getName());
		case SERVICE:
			return isServiceEnabled(component.getName());
		case APPLICATION:
			return isApplicationEnabled(component.getName());
		default:
			throw new AssertionError(component);
		}
	}

	@SuppressWarnings("deprecation")
	private boolean isApplicationEnabled(final String appName) {
		final DeploymentInfo appInfo = Applications.get(appName);
		
		return appInfo != null && 
					! appInfo.getStartUpO().equals(StartUp.DISABLED) && 
					appInfo.getStatus().equals(Status.STARTED);
		// The application is available and is started.
	}

	private boolean isInterfaceEnabled(final String itfName) {
		final InterfaceMonitor iMonitor = PropManager.getInstance()
				.getAppServiceCtx().getContainerContext().getSystemMonitor()
				.getInterface(itfName);
		if (iMonitor == null
				|| iMonitor.getStatus() != ComponentMonitor.STATUS_LOADED) {
			return false;
		}
		final String itfProvider = iMonitor.getProvidingServiceName();
		if (itfProvider == null) {
			return PropManager.getInstance().getInterfacesWithoutProvider()
					.contains(itfName);
		}
		return isServiceEnabled(itfProvider);
	}

	private boolean isLibraryEnabled(final String componentName) {
		final LibraryMonitor lMonitor = PropManager.getInstance()
				.getAppServiceCtx().getContainerContext().getSystemMonitor()
				.getLibrary(componentName);
		return lMonitor != null
				&& lMonitor.getStatus() == ComponentMonitor.STATUS_LOADED;
	}

	private boolean isServiceEnabled(final String serviceName) {
		final ServiceMonitor sMonitor = PropManager.getInstance()
			.getAppServiceCtx().getContainerContext().getSystemMonitor()
			.getService(serviceName);
		return sMonitor != null && sMonitor.getStartupMode() != ServiceMonitor.DISABLED &&
			(sMonitor.getStatus() == ComponentMonitor.STATUS_ACTIVE || 
			 sMonitor.getStatus() == ComponentMonitor.STATUS_LOADED &&
			isThisServiceContainerProvider(serviceName));
	}

	private boolean isThisServiceContainerProvider(String serviceName) {
		ArrayList<String> containers = Containers.getInstance()
				.getContainersForComponent(serviceName);
		return containers != null && containers.size() > 0;
	}
}