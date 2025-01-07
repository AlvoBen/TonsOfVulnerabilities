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
package com.sap.engine.services.dc.cm.deploy.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.util.Collection;

import com.sap.engine.services.dc.cm.deploy.DeployListenersList;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.event.DeploymentEvent;
import com.sap.engine.services.dc.event.DeploymentEventAction;
import com.sap.engine.services.dc.event.DeploymentListener;
import com.sap.engine.services.dc.event.EventMode;
import com.sap.engine.services.dc.event.ListenerEntry;
import com.sap.engine.services.dc.event.ListenersNotifier;
import com.sap.engine.services.dc.event.msg.MessageEventDeploymentBatchItem;
import com.sap.engine.services.dc.event.msg.MessageEventFactory;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.rmi_p4.P4IOException;
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
class DeployListenersNotifier extends ListenersNotifier {
	
	private  final Location location = DCLog.getLocation(this.getClass());
	
	private static final DeployListenersNotifier INSTANCE = new DeployListenersNotifier();

	private DeployListenersNotifier() {
		super();
	}

	public static DeployListenersNotifier getInstance() {
		return INSTANCE;
	}

	/*
	 * void deploymentTriggered(DeploymentBatchItem component,
	 * DeployListenersList listenersList) { long beginTime =
	 * System.currentTimeMillis(); DCLog.logExtDebug(
	 * DCLogConstants.DEPLOY_STARTING_TO_NOTIFY_LISTENERS_ABOUT, new Object[] {
	 * DeploymentEventAction.DEPLOYMENT_TRIGGERED, component.getSdu().getId()
	 * });
	 * 
	 * final DeploymentEvent event = new DeploymentEvent(component,
	 * DeploymentEventAction.DEPLOYMENT_TRIGGERED);
	 * 
	 * if( listenersList!=null ){ final Collection ListenerEntries =
	 * listenersList.getDeploymentListenerEntries();
	 * 
	 * for (Iterator iter = ListenerEntries.iterator(); iter.hasNext();) { final
	 * ListenerEntry listenerEntry = (ListenerEntry) iter.next(); if (
	 * EventMode.SYNCHRONOUS.equals( listenerEntry.getEventMode() ) ) { try { (
	 * (DeploymentListener) listenerEntry.getDeployControllerListener() )
	 * .deploymentPerformed(event); } catch (P4IOException p4ioe) {
	 * unregisterDeploymentListener( iter,
	 * listenerEntry.getDeployControllerListener(), event, p4ioe); } catch
	 * (P4ConnectionException p4ce) { unregisterDeploymentListener( iter,
	 * listenerEntry.getDeployControllerListener(), event, p4ce); } catch
	 * (Throwable th) {
	 * DCLog.logExtThrowable("An error occurred while notifying the listener " +
	 * listenerEntry.getDeployControllerListener() + " about the event '" +
	 * event + "'. The following error occurred: " + th.getMessage(), th); } }
	 * else { //TODO: perform asynchroneous notification } } }
	 * this.globalListenersNotify( GlobalListenersList.ListenerType.DEPLOY,
	 * event );
	 * 
	 * DCLog.logExtDebug(
	 * DCLogConstants.DEPLOY_LISTENERS_ARE_NOTIFIED_ABOUT_ACTION, new Object[] {
	 * DeploymentEventAction.DEPLOYMENT_TRIGGERED, component.getSdu().getId(),
	 * String.valueOf( System.currentTimeMillis() - beginTime ) }); }
	 */
	void deploymentPerformed(DeploymentBatchItem component,
			DeployListenersList listenersList, boolean performed) {
		long beginTime = System.currentTimeMillis();
		if (location.beDebug()) {
			traceDebug(location, 
					"Starting to notify listeners about action [{0}]. Component:[{1}].",
					new Object[] {
							performed ? DeploymentEventAction.DEPLOYMENT_PERFORMED
									: DeploymentEventAction.DEPLOYMENT_TRIGGERED,
							component.getSdu().getId() });
		}

		MessageEventDeploymentBatchItem eventMessageComponent = MessageEventFactory
				.getInstance().createMessageEventDeploymentBatchItem(component);

		final DeploymentEvent event = new DeploymentEvent(
				eventMessageComponent,
				performed ? DeploymentEventAction.DEPLOYMENT_PERFORMED
						: DeploymentEventAction.DEPLOYMENT_TRIGGERED);

		if (listenersList != null) {
			final Collection<ListenerEntry> listenerEntries = listenersList
					.getDeploymentListenerEntries();

			for (final ListenerEntry listenerEntry : listenerEntries) {
				if (EventMode.SYNCHRONOUS.equals(listenerEntry.getEventMode())) {
					try {
						((DeploymentListener) listenerEntry
								.getDeployControllerListener())
								.deploymentPerformed(event);
					} catch (P4IOException p4ioe) {
						unregisterDeploymentListener(listenerEntries,
								listenerEntry, event, p4ioe);
					} catch (P4RuntimeException p4ce) {
						unregisterDeploymentListener(listenerEntries,
								listenerEntry, event, p4ce);
					} catch (Throwable th) {
						DCLog
								.logErrorThrowable(location, 
										"ASJ.dpl_dc.001057",
										"An error occurred while notifying the listener [{0}] about the event [{1}]. The following error occurred: [{2}]",
										new Object[] {
												listenerEntry
														.getDeployControllerListener(),
												event, th.getMessage() }, th);
					}
				} else {
					// TODO: perform asynchroneous notification
				}
			}
		}

		this.globalListenersNotify(event);

		if (location.bePath()) {
			tracePath(location, 
					"Listeners are notified about action [{0}]. Component:[{1}]. Delay:[{2}] ms.",
					new Object[] {
							performed ? DeploymentEventAction.DEPLOYMENT_PERFORMED
									: DeploymentEventAction.DEPLOYMENT_TRIGGERED,
							eventMessageComponent.getSdu().getId(),
							String.valueOf(System.currentTimeMillis()
									- beginTime) });
		}
	}

	private void unregisterDeploymentListener(
			Collection<ListenerEntry> listenerEntries,
			ListenerEntry listenerEntry, DeploymentEvent event, Exception ex) {

		DCLog
				.logWarning(location,
						"ASJ.dpl_dc.001059",
						"Listener [{0}] was unregistered because during the notification about the event [{1}] the following error occurred: [{2}]",
						new Object[] {
								listenerEntry.getDeployControllerListener(),
								event, ex.getMessage() });
		listenerEntries.remove(listenerEntry);
	}
}
