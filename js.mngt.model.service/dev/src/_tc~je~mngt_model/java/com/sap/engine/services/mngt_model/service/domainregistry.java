/*
 * Created on 2004-9-2
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.mngt_model.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import com.sap.engine.management.action.ModelDomain;
import com.sap.engine.services.deploy.DeployEvent;
import com.sap.engine.services.mngt_model.spi.ActionDelivery;
import com.sap.engine.services.mngt_model.spi.ActionDeliveryBroker;
import com.sap.engine.services.mngt_model.spi.ActionFilter;
import com.sap.engine.services.mngt_model.spi.ActionFilteringException;
import com.sap.engine.services.mngt_model.spi.DomainAttributes;
import com.sap.engine.services.mngt_model.spi.PluginException;
import com.sap.engine.services.mngt_model.spi.ServiceComponent;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Severity;


class DomainRegistry implements ActionDeliveryBroker, ServiceComponent {
	
	private final ServiceEventIntegration sei;
	private final DeliveryPostponer dp;
	private final Hashtable name2domain = new Hashtable();
	private ActionFilter[] filters;
	
	public DomainRegistry() {
		sei = new ServiceEventIntegration();
		dp = new DeliveryPostponer();
	}
	
	public void init() throws PluginException {
		ServiceEventProvider sep = (ServiceEventProvider)ServiceFrame
			.getServiceInstance().getPlugin(ServiceEventProvider.class);
		try {
			dp.integrate();
		} catch (Exception e) {
			throw new PluginException(e);
		}
		sep.registerListener(sei);
		ActionFiltersRepository afr = (ActionFiltersRepository)ServiceFrame
			.getServiceInstance().getPlugin(ActionFiltersRepository.class);
		filters = afr.getActionFilters();	
	}
	
	public void destroy() throws PluginException {
		ServiceEventProvider sep = (ServiceEventProvider)ServiceFrame
			.getServiceInstance().getPlugin(ServiceEventProvider.class);
		sep.removeListener(sei);
		filters = null;
	}
	
	public void deliverAction(ActionDelivery ad) {
		if (dp.postpone(ad)) return;
		synchronized (name2domain) {
			Iterator i = name2domain.values().iterator();
			while (i.hasNext()) {
				RegistryEntry re = (RegistryEntry)i.next();
				if (!filterAction(ad, re)) {
					try {
						re.getModelDomain().processModelAction(ad.getDeliveredAction());	
					} catch (Exception mae) {
						Category.SYS_SERVER.logThrowable(Severity.ERROR, ServiceFrame.location,
							(Object)"error.action.process", new Object[] {re, ad.getDeliveredAction()} ,
							 "Exception thrown by domain " + re.getDomainName() + " while processing action: " + ad.getDeliveredAction(), mae);	
					}
				}
			}
		}
	}
	
	private boolean filterAction(ActionDelivery ad, DomainAttributes da) {
		int i = 0;
		try {
			for (; i < filters.length; i++) 
				if (filters[i].filterAction(ad, da))
					return true; // filter it out
			return false;// pass it
		} catch (ActionFilteringException afe) {
			Category.SYS_SERVER.logThrowable(Severity.ERROR, ServiceFrame.location,
				(Object)"error.action.filter", new Object[] {filters[i], da.getDomainName(), ad.getDeliveredAction()},
				"Exception thrown by Action Filter: " + afe + ".Action will not be delivered to domain: " + da.getDomainName()
				 + ". Action is: " + ad.getDeliveredAction(), afe);
			return true; //filter it out
		}
	}
	
	public static class RegistryEntry implements DomainAttributes {

		private final ModelDomain md;
		private final String name;
	
	
		public RegistryEntry(String name, ModelDomain md) {
			super();
			if ((this.md = md) == null) {
				throw new NullPointerException("md"); 
			}
			if ((this.name = name) == null || name.length() == 0) {
				throw new IllegalArgumentException("name"); 
			}
		}
	
		public ModelDomain getModelDomain() {
			return md;
		}
		
		public String getDomainName() {
			return name;			
		}
	}
	
	
	private class ServiceEventIntegration implements ServiceListener {
		
		public void serviceStarted(String name, Object mngtIntf) {
			if(!(mngtIntf instanceof ModelDomain)) return;
			synchronized(name2domain) {
				RegistryEntry newEntry = new RegistryEntry(name, (ModelDomain)mngtIntf);
				name2domain.put(name, newEntry);
			}
		}

		public void serviceStopped(String name) {
			name2domain.remove(name);
		}

		public void beginServiceStop(String name) {
		}	
	}
	
	private class DeliveryPostponer extends DeployServiceIntegration {
	
		private ArrayList postponed = new ArrayList();
		
		public synchronized void processApplicationEvent(DeployEvent de) {
			if (de.getActionType() == DeployEvent.INITIAL_START_APPLICATIONS 
					&& (de.getAction() == DeployEvent.ACTION_FINISH 
					|| de.getAction() == DeployEvent.LOCAL_ACTION_FINISH)) {
				redeliver();
			}
		}
		
		private synchronized void redeliver() {
			final Iterator i = postponed.iterator();
			postponed = null;
			Runnable redeliverer = new Runnable() {
				public void run() {
					synchronized(DeliveryPostponer.this) {
						while (i.hasNext()) {
							try {
								DomainRegistry.this.deliverAction((ActionDelivery)i.next());
							} catch (Exception e) {
								//if a problem occured it would be handled in deliverAction
								continue;
							}
						}
					}
				}
			};
			ServiceFrame.getServiceInstance()
				.getServiceContext().getCoreContext()
				.getThreadSystem().startCleanThread(redeliverer, false);
		}
		
		public synchronized boolean postpone(ActionDelivery ad) {
			if (postponed == null) return false;
			postponed.add(ad);
			return true;
		}
	}
}
