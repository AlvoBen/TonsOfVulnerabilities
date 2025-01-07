/*
 * Created on 2004-9-9
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.mngt_model.plugins;


import com.sap.engine.admin.model.ManagementModelManager;
import com.sap.engine.management.action.LifecycleAction;
import com.sap.engine.management.action.ModelDomain;
import com.sap.engine.services.mngt_model.service.ServiceEventProvider;
import com.sap.engine.services.mngt_model.service.ServiceFrame;
import com.sap.engine.services.mngt_model.service.ServiceListener;
import com.sap.engine.services.mngt_model.spi.ActionDelivery;
import com.sap.engine.services.mngt_model.spi.ActionDeliveryBroker;
import com.sap.engine.services.mngt_model.spi.ActionSource;
import com.sap.engine.services.mngt_model.spi.ActionSourceException;

/**
 * @author hristo-s
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LifecycleActionSource implements ActionSource {
	
	private ActionDeliveryBroker adb;
	private final ServiceListenerImpl sl = new ServiceListenerImpl();
	private ManagementModelManager mmm;
	
	public LifecycleActionSource() {
		super();
	}
	
	public void init() throws ActionSourceException {
		this.adb = (ActionDeliveryBroker)ServiceFrame
			.getServiceInstance().getPlugin(ActionDeliveryBroker.class);
		final ServiceEventProvider sep = (ServiceEventProvider)ServiceFrame
			.getServiceInstance().getPlugin(ServiceEventProvider.class);
		mmm = (ManagementModelManager)ServiceFrame.getServiceInstance().getServiceContext()
			.getContainerContext().getObjectRegistry()
			.getServiceInterface("basicadmin");
		if (mmm == null) {
			throw new ActionSourceException("Service basic admin not started.");		
		}
		Runnable r = new Runnable() {
			public void run() {
				sep.registerListener(sl);
			}
		};
		ServiceFrame.getServiceInstance().getServiceContext().getCoreContext()
			.getThreadSystem().startThread(r ,true);
	}

	public void destroy() throws ActionSourceException {
		ServiceEventProvider sep = (ServiceEventProvider)ServiceFrame
			.getServiceInstance().getPlugin(ServiceEventProvider.class);
		sep.removeListener(sl);
	}
	
	private class ServiceListenerImpl implements ServiceListener {
		public void serviceStarted(String name, Object service) {
			Object mngtIntf = ServiceFrame.getServiceInstance().getServiceContext()
							.getContainerContext().getSystemMonitor()
							.getService(name).getManagementInterface();
			if(!(mngtIntf instanceof ModelDomain)) return;
			LifecycleAction la = new LifecycleAction(mmm);	
			adb.deliverAction(new ActionDelivery(la, name));
		}

		public void serviceStopped(String name) {
			
		}

		public void beginServiceStop(String name) {
			LifecycleAction la = new LifecycleAction();
			ActionDelivery ad = name.equals(ServiceFrame.getServiceInstance().getServiceName()) ?
				new ActionDelivery(la) : new ActionDelivery(la, name);
			adb.deliverAction(ad);
		}	
	}
}
