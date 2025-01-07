package com.sap.engine.compatibility.frame.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.cluster.ApplicationClusterContext;
import com.sap.engine.frame.cluster.event.ClusterEventListener;
import com.sap.engine.frame.cluster.event.ServiceEventListener;
import com.sap.engine.frame.cluster.message.ListenerAlreadyRegisteredException;
import com.sap.engine.frame.container.ApplicationContainerContext;
import com.sap.engine.frame.container.deploy.DeployContext;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.monitor.SystemMonitor;
import com.sap.engine.frame.container.registry.ObjectRegistry;
import com.sap.engine.frame.container.runtime.RuntimeConfiguration;
import com.sap.engine.frame.core.CoreContext;
import com.sap.engine.frame.core.cache.CacheContext;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.database.DatabaseContext;
import com.sap.engine.frame.core.licensing.LicensingContext;
import com.sap.engine.frame.core.load.LoadContext;
import com.sap.engine.frame.core.locking.LockingContext;
import com.sap.engine.frame.core.monitor.CoreMonitor;
import com.sap.engine.frame.core.pool.PoolContext;
import com.sap.engine.frame.core.reflect.ReflectContext;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.frame.state.ManagementInterface;
import com.sap.engine.frame.state.PersistentContainer;
import com.sap.engine.frame.state.ServiceState;

@SuppressWarnings("deprecation")
public class ApplicationServiceContextComponent implements
		ApplicationServiceContext {
	private Bundle user;
	private List<ServiceReference> references;
	private ComponentContext cc;
	private CompatibilityCoreContext coreContext;
	private CompatibilityContainerContext containerContext;
	private CompatibilityServiceState serviceState;

	protected void activate(ComponentContext context) {
		user = context.getUsingBundle();
		cc = context;
		references = new ArrayList<ServiceReference>();
	}

	protected void deactivate(ComponentContext context) {
		user = null;
		cc = null;
		for (ServiceReference sr : references)
			cc.getBundleContext().ungetService(sr);
		references = null;
	}

	@SuppressWarnings("unchecked")
	private <E> E use(ServiceReference ref, E object) {
		if (object != null)
			return object;
		object = (E) cc.getBundleContext().getService(ref);
		if (object != null)
			references.add(ref);
		return object;
	}

	public ApplicationClusterContext getClusterContext() {
		// TODO Application Cluster Context
		return null;
	}

	public ApplicationContainerContext getContainerContext() {
		return containerContext == null ? containerContext = new CompatibilityContainerContext()
				: containerContext;
	}

	public CoreContext getCoreContext() {
		return coreContext == null ? coreContext = new CompatibilityCoreContext()
				: coreContext;
	}

	public ServiceState getServiceState() {
		return serviceState == null ? serviceState = new CompatibilityServiceState()
				: serviceState;
	}

	public String toString() {
		return "Compatibility Application Service Context for Bundle: "
				+ user.toString();
	}

	private class CompatibilityCoreContext implements CoreContext, PoolContext {

		public CacheContext getCacheContext() {
			return null;
		}

		public ConfigurationHandlerFactory getConfigurationHandlerFactory() {
			return null;
		}

		public CoreMonitor getCoreMonitor() {
			return null;
		}

		public DatabaseContext getDatabaseContext() {
			return null;
		}

		public LicensingContext getLicensingContext() {
			return null;
		}

		public LoadContext getLoadContext() {
			return null;
		}

		public LockingContext getLockingContext() {
			return null;
		}

		public ReflectContext getReflectContext() {
			return null;
		}

		public ThreadSystem getThreadSystem() {
			return null;
		}

		/**
		 * The pool context is a long-term deprecation victim, and is
		 * implemented here, not to be used by migrated components.
		 */

		public PoolContext getPoolContext() {
			return this;
		}

		public byte[] get(int size) throws IllegalArgumentException {
			return new byte[size];
		}

		public void release(byte[] buffer) {
			// do nothing.
		}
	}

	public class CompatibilityContainerContext implements
			ApplicationContainerContext {

		public DeployContext getDeployContext() {
			return null;
		}

		public ObjectRegistry getObjectRegistry() {
			return null;
		}

		public SystemMonitor getSystemMonitor() {
			return null;
		}
	}

	public class CompatibilityServiceState implements ServiceState {

		public PersistentContainer getPersistentContainer() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getPersistentDirectoryName() {
			// TODO Auto-generated method stub
			return null;
		}

		public Properties getProperties() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getProperty(String key) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getProperty(String key, String defaultValue) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getServiceName() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getWorkingDirectoryName() {
			// TODO Auto-generated method stub
			return null;
		}

		public void registerClusterEventListener(ClusterEventListener listener)
				throws ListenerAlreadyRegisteredException {
			// TODO Auto-generated method stub

		}

		public void registerContainerEventListener(int mask, Set names,
				ContainerEventListener listener) {
			// TODO Auto-generated method stub

		}

		public void registerContainerEventListener(
				ContainerEventListener listener) {
			// TODO Auto-generated method stub

		}

		public void registerRuntimeConfiguration(RuntimeConfiguration runtime) {
			// TODO Auto-generated method stub

		}

		public void registerServiceEventListener(ServiceEventListener listener)
				throws ListenerAlreadyRegisteredException {
			// TODO Auto-generated method stub

		}

		public void stopMe() throws ServiceException {
			// TODO Auto-generated method stub

		}

		public void unregisterClusterEventListener() {
			// TODO Auto-generated method stub

		}

		public void unregisterContainerEventListener() {
			// TODO Auto-generated method stub

		}

		public void unregisterManagementInterface() {
			// TODO Auto-generated method stub

		}

		public void unregisterRuntimeConfiguration() {
			// TODO Auto-generated method stub

		}

		public void unregisterServiceEventListener() {
			// TODO Auto-generated method stub

		}

		public void registerManagementInterface(ManagementInterface mi)
				throws ServiceException {
			// TODO Auto-generated method stub

		}
	}
}
