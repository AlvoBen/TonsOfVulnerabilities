/*
 * Created on 2004-9-7
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.mngt_model.service;

import com.sap.engine.frame.container.registry.ObjectRegistry;
import com.sap.engine.services.deploy.DeployCallback;
import com.sap.engine.services.deploy.DeployEvent;
import com.sap.engine.services.deploy.DeployListener;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.container.ProgressEvent;

/**
 * @author hristo-s
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class DeployServiceIntegration implements DeployCallback {
	protected final DeployService deployService;

	public DeployServiceIntegration() {
		ObjectRegistry or = ServiceFrame.getServiceInstance()
				.getServiceContext().getContainerContext().getObjectRegistry();	
		deployService = (DeployService)or.getServiceInterface("deploy");
	}

	public void integrate() throws Exception {
		String thisServerName = ServiceFrame.getServiceInstance().getServerName();
		deployService.registerDeployCallback(this, new String[] {thisServerName});
	}

	public DeployService getDeployService() {
		return deployService;
	}

	public void disintegrate() throws Exception {
		String thisServerName = ServiceFrame.getServiceInstance().getServerName();
		deployService.unregisterDeployCallback(this, new String[] {thisServerName});		
	}

	public void processApplicationEvent(DeployEvent event) {
	}

	public void processLibraryEvent(DeployEvent event) {
		//do nothing		
	}

	public void processInterfaceEvent(DeployEvent event) {
		//do nothing
	}

	public void processServiceEvent(DeployEvent event) {
	}

	public void processContainerEvent(ProgressEvent event) {
		//do nothing
			}  
  
	public void processReferenceEvent(DeployEvent event) {
				//do nothing
	}

	public void processStandaloneModuleEvent(DeployEvent event) {
		//do nothing
	}

	public void addDeployListener(DeployListener listener) {
		//do nothing
	}
  
	private int found(DeployListener l) {
		return -1;
	}

	public void removeDeployListener(DeployListener listener) {
		//do nothing
	}

 
	public DeployListener[] getDeployListeners() {
		throw new UnsupportedOperationException();
	}

	public void callbackLost(String serverName) {
		//do nothing  
			}
  
	public void serverAdded(String serverName) {
		//go nothing
	}	
}
