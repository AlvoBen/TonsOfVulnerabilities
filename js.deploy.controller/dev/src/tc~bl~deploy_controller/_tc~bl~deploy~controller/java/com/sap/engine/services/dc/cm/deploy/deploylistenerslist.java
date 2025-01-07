package com.sap.engine.services.dc.cm.deploy;

import static com.sap.engine.services.dc.util.logging.DCLog.isDebugLoggable;
import static com.sap.engine.services.dc.util.logging.DCLog.logDebugThrowable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.sap.engine.services.dc.event.ClusterListenersList;
import com.sap.engine.services.dc.event.DeploymentListener;
import com.sap.engine.services.dc.event.EventMode;
import com.sap.engine.services.dc.event.ListenerEntry;
import com.sap.engine.services.dc.event.ListenerMode;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.rmi_p4.P4RuntimeException;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-5-4
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class DeployListenersList extends ClusterListenersList {
	
	private  final Location location = DCLog.getLocation(this.getClass());

	private final List<ListenerEntry> deploymentListenersList;

	public static DeployListenersList createInstance() {
		return new DeployListenersList();
	}

	private DeployListenersList() {
		super();
		this.deploymentListenersList = new CopyOnWriteArrayList<ListenerEntry>();
	}

	public void addDeploymentListener(DeploymentListener listener,
			ListenerMode listenerMode, EventMode eventMode) {
		try {
			this.deploymentListenersList.add(ListenerEntry.createInstance(
					listener, listenerMode, eventMode));
		} catch (P4RuntimeException e) {
			// support for older client api with newer controller;
			// listeners will only be added, not removed as old implementation
			// did not support remove due to client <-> stub differences
			if (isDebugLoggable()) {
				String msg = DCLog
						.buildExceptionMessage(
								"ASJ.dpl_dc.001146",
								"DeployListenerList:addDeploymentListener: Error while adding deployment listener. Check if your client libraries version matches the deploy controller version on the AS Java");
				logDebugThrowable(location, null, msg, e);
			}
			this.deploymentListenersList.add(ListenerEntry.createInstance(
					listener, listenerMode, eventMode));
		}
	}

	public void removeDeploymentListener(final DeploymentListener listener) {
		try {
			for (final ListenerEntry listenerEntry: this.deploymentListenersList) {
				if (listenerEntry.getDeployControllerListener().equals(listener)) {
					deploymentListenersList.remove(listenerEntry); 
					//break;//let remove all					
				}				
			}	
		} catch (P4RuntimeException e) {
			if (isDebugLoggable()) {
				String msg = DCLog
						.buildExceptionMessage(
								"ASJ.dpl_dc.001147",
								"DeployListenerList:removeDeploymentListener: Error while removing deployment listener. Check if your client libraries version matches the Deploy Controller version on the AS Java.");
				logDebugThrowable(location, null, msg, e);
			}
		}
	}

	public Collection<ListenerEntry> getDeploymentListenerEntries() {
		return this.deploymentListenersList;
	}
}
