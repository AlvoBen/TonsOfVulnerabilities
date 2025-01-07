/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Jun 30, 2005
 */
package com.sap.engine.services.dc.cm.undeploy;

import static com.sap.engine.services.dc.util.logging.DCLog.isDebugLoggable;
import static com.sap.engine.services.dc.util.logging.DCLog.logDebugThrowable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.sap.engine.services.dc.event.ClusterListenersList;
import com.sap.engine.services.dc.event.EventMode;
import com.sap.engine.services.dc.event.ListenerEntry;
import com.sap.engine.services.dc.event.ListenerMode;
import com.sap.engine.services.dc.event.UndeploymentListener;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.rmi_p4.P4RuntimeException;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright (c) 2005, SAP-AG Date: Jun 30, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public final class UndeployListenersList extends ClusterListenersList {
	
	private Location location = DCLog.getLocation(this.getClass());

	private final List<ListenerEntry> undeploymentListenersList;

	public static UndeployListenersList createInstance() {
		return new UndeployListenersList();
	}

	private UndeployListenersList() {
		super();
		this.undeploymentListenersList = new CopyOnWriteArrayList<ListenerEntry>();
	}

	public void addUndeploymentListener(UndeploymentListener listener,
			ListenerMode listenerMode, EventMode eventMode) {
		try {
			this.undeploymentListenersList.add(ListenerEntry
					.createInstance(listener, listenerMode, eventMode));
		} catch (P4RuntimeException e) {
			// support for older client api with newer controller;
			// listeners will only be added, not removed as old implementation
			// did not support remove due to client <-> stub differences
			if (isDebugLoggable()) {
				logDebugThrowable(location, 
						null,
						"UndeployListenersList:addUndeploymentListener: Error while adding undeployment listener. Please check if your client libraries version matches the deploy controller version on the engine!",
						e);
			}
			this.undeploymentListenersList.add(ListenerEntry.createInstance(
					listener, listenerMode, eventMode));
		}
	}

	public void removeUndeploymentListener(UndeploymentListener listener) {
		try {
			for (final ListenerEntry listenerEntry: this.undeploymentListenersList) {
				if (listenerEntry.getDeployControllerListener().equals(listener)) {
					undeploymentListenersList.remove(listenerEntry); 
					//break; //let remove all					
				}				
			}
		} catch (P4RuntimeException e) {
			if (isDebugLoggable()) {
				logDebugThrowable(location, 
						null,
						"UndeployListenersList:removeUndeploymentListener: Error while removing undeployment listener. Please check if your client libraries version matches the deploy controller version on the engine!",
						e);
			}
		}
	}

	public Collection<ListenerEntry> getUndeploymentListenerEntries() {
		return this.undeploymentListenersList;
	}
}
