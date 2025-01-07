/*
 * Created on 2004-9-7
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.mngt_model.plugins;

import java.rmi.RemoteException;
import java.util.Hashtable;

import com.sap.engine.management.action.ApplicationAction;
import com.sap.engine.services.deploy.DeployEvent;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.mngt_model.service.DeployServiceIntegration;
import com.sap.engine.services.mngt_model.service.ServiceFrame;
import com.sap.engine.services.mngt_model.spi.ActionDelivery;
import com.sap.engine.services.mngt_model.spi.ActionFilter;
import com.sap.engine.services.mngt_model.spi.ActionFilteringException;
import com.sap.engine.services.mngt_model.spi.DomainAttributes;
import com.sap.engine.services.mngt_model.spi.PluginException;
import com.sap.tc.logging.Severity;

/**
 * @author hristo-s
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ApplicationActionFilter implements ActionFilter {
	private ComponentContainerMapper ccm;
		
	public boolean filterAction(ActionDelivery ad, DomainAttributes da)
		throws ActionFilteringException {
		if (!(ad.getDeliveredAction() instanceof ApplicationAction)) {
			if (ServiceFrame.debugMode())
				ServiceFrame.location.debugT("ApplicationActionFilter: Not an application action. Passing the action through. Model domain is: " + da.getDomainName()); 
			return false;
		} 
		String container = ccm.getContainerName(da.getDomainName());
		if (container == null) {
			if (ServiceFrame.debugMode())
				ServiceFrame.location.debugT("ApplicationActionFilter: Model domain " + da.getDomainName() + " does not model container. Filtering action out.");
			return true;
			
		} 
		ApplicationAction aa = (ApplicationAction)ad.getDeliveredAction();
		try {
			String[] deployedIn = ccm.getDeployService()
						.getApplicationInformation(aa.getApplicationName()).getContainerNames();
			for (int i = 0; i < deployedIn.length; i++) 
				if (deployedIn[i].equals(container)) {
					if (ServiceFrame.debugMode())
						ServiceFrame.location.debugT("ApplicationActionFilter: Application " + aa.getApplicationName() + " is deployed in modeled container " +  da.getDomainName() + ". Passing action through");
					return false;
				} 
			if (ServiceFrame.debugMode())
				ServiceFrame.location.debugT("ApplicationFilter: Application " + aa.getApplicationName() + "is not deployed in modeled container " + da.getDomainName() + ". Filtering action out");		
			return true;
		} catch (RemoteException rx) {
			throw new ActionFilteringException(rx);
		}
	}

	public void init() throws PluginException {
		try {
			ccm = new ComponentContainerMapper();	
		} catch (Exception e) {
			throw new PluginException(e);
		}
	}

	public void destroy() {
		try {
			ccm.disintegrate();
			ccm = null;
		} catch (Exception e) {
			ServiceFrame.location.traceThrowableT(Severity.ERROR, "", e);
		}
	}
	
	private static class ComponentContainerMapper extends DeployServiceIntegration {
		private Hashtable comp2cont = new Hashtable();
	
		public ComponentContainerMapper() throws Exception {
			String thisServerName = ServiceFrame.getServiceInstance().getServerName();
			synchronized (comp2cont) {
				super.integrate();
				String soFar[] = deployService.listContainers(new String[] {thisServerName});
				for (int i = 0; i < soFar.length; i++) {
					ContainerInfo ci = deployService.getContainerInfo(soFar[i], thisServerName);
					assert comp2cont.get(ci.getServiceName()) == null;
					comp2cont.put(ci.getServiceName(), soFar[i]);
					if (ServiceFrame.debugMode())
						ServiceFrame.location.debugT("Registered already started container: " + soFar[i] + ". Provider is: " + ci.getServiceName());
				}
			}
		}
	
		public String getContainerName(String serviceName) {
			return (String)comp2cont.get(serviceName);
		}
	
		public void processServiceEvent(DeployEvent event) {
			if (event.getActionType() == DeployEvent.REGISTER_CONTAINER_INTERFACE) {
					Object debug_prev = comp2cont.put(event.getComponentName(),
						event.whoCausedGroupOperation());
				assert debug_prev != null;
					if (ServiceFrame.debugMode())
						ServiceFrame.location.debugT("Registered container: " + event.whoCausedGroupOperation() + ". Container provider is: " + event.getComponentName());
			} else if (event.getActionType() == DeployEvent.UNREGISTER_CONTAINER_INTERFACE) {
				Object debug_prev = comp2cont.remove(event.getComponentName());
				assert debug_prev != null;
				if (ServiceFrame.debugMode())
					ServiceFrame.location.debugT("Unregistered container: " + event.whoCausedGroupOperation() + ". Container provider was: " + event.getComponentName());
			}
		}
	}
}
