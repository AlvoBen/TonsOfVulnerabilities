/*
 * Created on 2004-9-2
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.mngt_model.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.monitor.ServiceMonitor;
import com.sap.engine.services.mngt_model.spi.PluginException;
import com.sap.engine.services.mngt_model.spi.ServiceComponent;
import com.sap.tc.logging.Severity;

/**
 * @author Hristo-S
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class ServiceEventProvider implements ServiceComponent {

	ArrayList serviceListeners = new ArrayList();
	private final EventMultiplicator em = new EventMultiplicator();
	
	ServiceEventProvider() {
		super();
	}
	
	public void init() throws PluginException {
		if (ServiceFrame.debugMode()) 
			ServiceFrame.location.debugT("Registering ServiceEventProvider as ContainerEventListener for service: " + ServiceFrame.getServiceInstance().getServiceName()); 
		
		ServiceFrame.getServiceInstance().getServiceContext()
			.getServiceState().registerContainerEventListener(em);
		if (ServiceFrame.debugMode()) 
			ServiceFrame.location.debugT("ServiceEentProvider registered as ContainerEventLister for service: " + ServiceFrame.getServiceInstance().getServiceName());
		
	}

	public void destroy() throws PluginException {
		ServiceFrame.getServiceInstance().getServiceContext()
			.getServiceState().unregisterContainerEventListener();
	}
	
	public void registerListener(ServiceListener sl) {
		synchronized (serviceListeners) {
			if (sl == null) {
				throw new NullPointerException("sl");
			}
			if (ServiceFrame.location.getEffectiveSeverity() <= Severity.DEBUG) {
				ServiceFrame.location.debugT("Registering ServiceListener: " + sl + " in ServiceEventProvider");
			}
			serviceListeners.add(sl);
		}
		em.deliverStartedServices(sl);	
	}
	
	public void removeListener(ServiceListener sl) {
		synchronized (serviceListeners) {
			if (sl == null) {
				throw new NullPointerException("sl");	
			}
			if (ServiceFrame.debugMode()) {
				ServiceFrame.location.debugT("Unregistering ServiceListener: " + sl + " from ServiceEventProvider");
			}
			serviceListeners.remove(sl);
		}
	}
	
	private final class EventMultiplicator implements ContainerEventListener {
		private final Method[] multipliedEvents;
	
		
		public EventMultiplicator() {
			super();
			Class selc = ServiceListener.class;
			try {
				multipliedEvents = new Method[] {
					selc.getMethod("beginServiceStop", new Class[] {String.class}),
					selc.getMethod("serviceStarted", new Class[] {String.class, Object.class}),
					selc.getMethod("serviceStopped", new Class[] {String.class})		
				};
			} catch (NoSuchMethodException nsme) {
				throw new AssertionError(nsme);
			}
		
			
		}

		/* (non-Javadoc)
		 * @see com.sap.engine.frame.container.event.ContainerEventListener#containerStarted()
		 */
		public void containerStarted() {
			traceIgnoredEvent("containerStarted");
		}

		/* (non-Javadoc)
		 * @see com.sap.engine.frame.container.event.ContainerEventListener#beginContainerStop()
		 */
		public void beginContainerStop() {
			traceIgnoredEvent("beginContainerStop");
		}

		/* (non-Javadoc)
		 * @see com.sap.engine.frame.container.event.ContainerEventListener#serviceStarted(java.lang.String, java.lang.Object)
		 */
		public void serviceStarted(String name, Object arg1) {
			Object mngtIntf = ServiceFrame.getServiceInstance().getServiceContext()
				.getContainerContext().getSystemMonitor()
				.getService(name).getManagementInterface();
			multiply(multipliedEvents[1], new Object[] {name, mngtIntf});
		}

		/* (non-Javadoc)
		 * @see com.sap.engine.frame.container.event.ContainerEventListener#serviceNotStarted(java.lang.String)
		 */
		public void serviceNotStarted(String arg0) {
			traceIgnoredEvent("serviceNotStarted");
		}

		/* (non-Javadoc)
		 * @see com.sap.engine.frame.container.event.ContainerEventListener#beginServiceStop(java.lang.String)
		 */
		public void beginServiceStop(String arg0) {
			String serviceName = ServiceFrame.getServiceInstance().getServiceName();
			if (serviceName.equals(arg0)) {
				if (ServiceFrame.debugMode()) 
					ServiceFrame.location.debugT("Unregistering ServiceEventProvider as ContainerEventListener for Service: " + serviceName);

				ServiceFrame.getServiceInstance().getServiceContext().getServiceState().unregisterContainerEventListener();	

				if (ServiceFrame.debugMode()) 
					ServiceFrame.location.debugT("ServiceEventProvider unregistered as ContainerEventListener for Service: " + serviceName);
			}
			multiply(multipliedEvents[0], new Object[] {arg0});
		}

		/* (non-Javadoc)
		 * @see com.sap.engine.frame.container.event.ContainerEventListener#serviceStopped(java.lang.String)
		 */
		public void serviceStopped(String arg0) {
			multiply(multipliedEvents[2], new Object[] {arg0});
		}

		/* (non-Javadoc)
		 * @see com.sap.engine.frame.container.event.ContainerEventListener#interfaceAvailable(java.lang.String, java.lang.Object)
		 */
		public void interfaceAvailable(String arg0, Object arg1) {
			traceIgnoredEvent("interfaceAvailable");

		}

		/* (non-Javadoc)
		 * @see com.sap.engine.frame.container.event.ContainerEventListener#interfaceNotAvailable(java.lang.String)
		 */
		public void interfaceNotAvailable(String arg0) {
			traceIgnoredEvent("interfaceNotAvailable");

		}

		/* (non-Javadoc)
		 * @see com.sap.engine.frame.container.event.ContainerEventListener#markForShutdown(long)
		 */
		public void markForShutdown(long arg0) {
			traceIgnoredEvent("markForShutdown");

		}

		/* (non-Javadoc)
		 * @see com.sap.engine.frame.container.event.ContainerEventListener#setServiceProperty(java.lang.String, java.lang.String)
		 */
		public boolean setServiceProperty(String arg0, String arg1) {
			traceIgnoredEvent("getServiceProperty");
			return false;
		}

		/* (non-Javadoc)
		 * @see com.sap.engine.frame.container.event.ContainerEventListener#setServiceProperties(java.util.Properties)
		 */
		public boolean setServiceProperties(Properties arg0) {
			traceIgnoredEvent("setServiceProperties");
			return false;
		}
	
		private synchronized void deliverStartedServices(ServiceListener sl) {
			ServiceMonitor[] sms = ServiceFrame.getServiceInstance().getServiceContext()
			.getContainerContext().getSystemMonitor().getServices();
			for (int i = 0; i < sms.length; i++) {
				if (sms[i].getStartupState() == ServiceMonitor.STARTUP_STATE_STARTED) {
					Object[] args = new Object[] {
						sms[i].getComponentName(),
						sms[i].getManagementInterface()};
					deliverSingleListener(sl, multipliedEvents[1], args);				
				}
			}
		}
		
	
		private synchronized void multiply(Method m, Object[] args) {
			synchronized(serviceListeners) {
				if (ServiceFrame.debugMode()) 
					ServiceFrame.location.debugT("Multiplying event: " + m.getName());
			
				Iterator i = serviceListeners.iterator();
				while (i.hasNext()) {
					ServiceListener sl = (ServiceListener)i.next();
					deliverSingleListener(sl, m, args);		
				}
			}
		}
		
		private void deliverSingleListener(ServiceListener sl, Method m, Object[] args) {
			try {
				if (ServiceFrame.debugMode()) {
					ServiceFrame.location.debugT("Delivering event: " + m.getName() + " to ServiceListener: " + sl);
				}
				m.invoke(sl, args);
				if (ServiceFrame.debugMode()) {
					ServiceFrame.location.debugT("Event: " + m.getName() + "successfully delivered to ServiceListener: " + sl);		
				}
			} catch (InvocationTargetException ite) {
				ServiceFrame.location.traceThrowableT(Severity.ERROR, "Error occurred while delivering event: " + m.getName() + " to ServiceListener: " + sl, ite);	
			} catch (IllegalAccessException iae) {
				throw new AssertionError(iae);
			}	
		}

		private void traceIgnoredEvent(String eventMethod) {
			if (ServiceFrame.debugMode()) 
				ServiceFrame.location.debugT("Event: " + eventMethod + " was ignored by EventMultiplier.");	
		}
	}
}
