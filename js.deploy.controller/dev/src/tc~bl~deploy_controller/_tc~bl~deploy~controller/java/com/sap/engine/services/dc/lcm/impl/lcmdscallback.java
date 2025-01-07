/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Dec 21, 2005
 */
package com.sap.engine.services.dc.lcm.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.isDebugLoggable;
import static com.sap.engine.services.dc.util.logging.DCLog.logDebugThrowable;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.sap.engine.services.dc.event.EventMode;
import com.sap.engine.services.dc.event.LCEvent;
import com.sap.engine.services.dc.event.LCEventAction;
import com.sap.engine.services.dc.event.LCEventListener;
import com.sap.engine.services.dc.event.ListenerEntry;
import com.sap.engine.services.dc.event.ListenerMode;
import com.sap.engine.services.dc.lcm.LCMCompNotFoundException;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.explorer.AbstractRemoteRepositoryExplorerFactory;
import com.sap.engine.services.dc.repo.explorer.RepositoryExplorer;
import com.sap.engine.services.dc.repo.explorer.RepositoryExploringException;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.DCLogConstants;
import com.sap.engine.services.deploy.DeployCallback;
import com.sap.engine.services.deploy.DeployEvent;
import com.sap.engine.services.deploy.container.ProgressEvent;
import com.sap.engine.services.rmi_p4.P4ConnectionException;
import com.sap.engine.services.rmi_p4.P4IOException;
import com.sap.engine.services.rmi_p4.P4RuntimeException;
import com.sap.tc.logging.Location;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD></DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2005, SAP-AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>Dec 21, 2005</DD>
 * </DL>
 * 
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.1
 * 
 */

public class LCMDSCallback implements DeployCallback {
	
	private Location location = DCLog.getLocation(this.getClass());
	
	private final LinkedHashMap listenersMap;
	private int idCounter;

	LCMDSCallback() {
		this.listenersMap = new LinkedHashMap();
	}

	public void addLCEventListener(LCEventListener listener, EventMode eventMode) {
		try {
			this.listenersMap.put(new Integer(listener.getId()), ListenerEntry
					.createInstance(listener, ListenerMode.GLOBAL, eventMode));
		} catch (P4RuntimeException e) {
			// support for older client api with newer controller;
			// listeners will only be added, not removed as old implementation
			// did not support remove due to client <-> stub differences
			if (isDebugLoggable()) {
				logDebugThrowable(
						location,
						null,
						"LCMDSCallback:addLCEventListener: Error while adding LC event listener. Please check if your client libraries version matches the deploy controller version on the engine!",
						e);
			}
			this.listenersMap.put(new Integer(generateId()), ListenerEntry
					.createInstance(listener, ListenerMode.GLOBAL, eventMode));
		}
	}

	public void removeLCEventListener(LCEventListener listener) {
		try {
			this.listenersMap.remove(new Integer(listener.getId()));
		} catch (P4RuntimeException e) {
			if (isDebugLoggable()) {
				logDebugThrowable(
						location,
						null,
						"LCMDSCallback:removeLCEventListener: Error while removing LC event listener. Please check if your client libraries version matches the deploy controller version on the engine!",
						e);
			}
		}
	}

	public boolean isEmpty() {
		return this.listenersMap.isEmpty();
	}

	public void processApplicationEvent(DeployEvent deployEvent) {
		byte action = deployEvent.getAction();
		if (DeployEvent.ACTION_FINISH == action
				|| DeployEvent.LOCAL_ACTION_FINISH == action) {
			byte actionType = deployEvent.getActionType();
			switch (actionType) {
			case DeployEvent.START_APP:
			case DeployEvent.STOP_APP: {
				String[] errors = deployEvent.getErrors();
				String[] warnings = deployEvent.getWarnings();
				String componentName = deployEvent.getComponentName();
				int pos = componentName.indexOf('/');
				String name;
				String vendor;
				if (pos != -1) {
					vendor = componentName.substring(0, pos);
					name = componentName.substring(pos + 1);
				} else {
					name = componentName;
					vendor = "sap.com";
				}

				LCEvent lcEvent = null;
				try {

					Sda sda = null;
					RepositoryExplorer repoExplorer = AbstractRemoteRepositoryExplorerFactory
							.getInstance().createRepositoryExplorer();
					sda = repoExplorer.findSda(name, vendor);
					if (sda == null) {
						throw new LCMCompNotFoundException(
								"ASJ.dpl_dc.003319 The system did not find registered development "
										+ "component  with name '" + name
										+ "' and vendor '" + vendor + "'");
					}

					LCEventAction eventAction = DeployEvent.START_APP == actionType ? LCEventAction.COMPONENT_STARTED
							: LCEventAction.COMPONENT_STOPPED;
					lcEvent = new LCEvent(sda, eventAction, errors, warnings);
				} catch (LCMCompNotFoundException e) {
					DCLog
							.logErrorThrowable(
									location,
									null,
									"Life cycle event fired from the DS "
											+ "but component not found exception occurred for the pair :"
											+ "name '" + name + "', vendor '"
											+ vendor + "'.", e);
				} catch (RepositoryExploringException e) {
					DCLog
							.logErrorThrowable(
									location,
									null,
									"Life cycle event fired from the DS "
											+ "but repository exploring exception occurred for the pair :"
											+ "name '" + name + "', vendor '"
											+ vendor + "'.", e);
				}
				notifyAllListeners(lcEvent);

			}
				break;
			default:
				// don't care
				break;
			}
		}
	}

	public void processServiceEvent(DeployEvent deployEvent) {
	}

	public void processLibraryEvent(DeployEvent deployEvent) {
	}

	public void processInterfaceEvent(DeployEvent deployEvent) {
	}

	public void processContainerEvent(ProgressEvent deployEvent) {
	}

	public void processReferenceEvent(DeployEvent deployEvent) {
	}

	public void processStandaloneModuleEvent(DeployEvent deployEvent) {
	}

	public void callbackLost(String deployEvent) {
	}

	public void serverAdded(String deployEvent) {
	}

	private void notifyAllListeners(LCEvent lcEvent) {
		if (this.listenersMap != null) {
			Collection listenerEntries = ((LinkedHashMap) this.listenersMap
					.clone()).values();
			ListenerEntry nextListenerEntry;
			for (Iterator iter = listenerEntries.iterator(); iter.hasNext();) {
				nextListenerEntry = (ListenerEntry) iter.next();
				if (EventMode.SYNCHRONOUS.equals(nextListenerEntry
						.getEventMode())) {
					try {
						((LCEventListener) nextListenerEntry
								.getDeployControllerListener())
								.lifeCycleEventTriggered(lcEvent);
					} catch (P4IOException e) {
						this.listenersMap.remove(nextListenerEntry
								.getDeployControllerListener());
						DCLog
								.logInfo(location, 
										"ASJ.dpl_dc.005614",
										"Listener [{0}] was unregistered because during the notification about the event [{1}] the following error occurred: [{2}]",
										new Object[] {
												nextListenerEntry
														.getDeployControllerListener(),
												lcEvent, e.getMessage() });
					} catch (P4ConnectionException p4ce) {
						this.listenersMap.remove(nextListenerEntry
								.getDeployControllerListener());
						DCLog
								.logInfo(location, 
										"ASJ.dpl_dc.005615",
										"Listener [{0}] was unregistered because during the notification about the event [{1}] the following error occurred: [{2}]",
										new Object[] {
												nextListenerEntry
														.getDeployControllerListener(),
												lcEvent, p4ce.getMessage() });
					} catch (Throwable th) {
						DCLog.logErrorThrowable(location, null,
								"An error occurred while notifying the listener "
										+ nextListenerEntry
												.getDeployControllerListener()
										+ " about the event '" + lcEvent
										+ "'. The following error occurred: "
										+ th.getMessage(), th);
					}
				} else {
					// TODO: perform asynchroneous notification
				}
			}
		}
	}

	public int generateId() {
		return idCounter++;
	}
}
