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
package com.sap.engine.services.dc.event;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;

import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.services.dc.manage.messaging.Message;
import com.sap.engine.services.dc.manage.messaging.MessageConstants;
import com.sap.engine.services.dc.manage.messaging.MessageSender;
import com.sap.engine.services.dc.manage.messaging.MessagingException;
import com.sap.engine.services.dc.manage.messaging.MessagingFactory;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.rmi_p4.P4ConnectionException;
import com.sap.engine.services.rmi_p4.P4IOException;
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
public abstract class ListenersNotifier {
	
	private Location location = DCLog.getLocation(this.getClass());
	
	private static final int MAX_MESSAGE_LENGTH = 7 * 1024 * 1024;// 7Mb < 8Mb

	// max
	// allowed
	// size

	protected ListenersNotifier() {
	}

	public void clusterRestartTriggered(ClusterListenersList listenersList) {
		long beginTime = System.currentTimeMillis();

		if (location.bePath()) {
			tracePath(location, 
				"Starting to notify listeners about action [{0}]. Component:[{1}].",
				new Object[] {
						ClusterEventAction.CLUSTER_RESTART_TRIGGERED, "n/a" });
		}
			
		final ClusterEvent event = new ClusterEvent(
				ClusterEventAction.CLUSTER_RESTART_TRIGGERED);

		final Collection<ListenerEntry> listenerEntries = listenersList
				.getClusterListenerEntries();
		for (final ListenerEntry listenerEntry : listenerEntries) {
			if (EventMode.SYNCHRONOUS.equals(listenerEntry.getEventMode())) {
				try {
					((ClusterListener) listenerEntry
							.getDeployControllerListener())
							.clusterRestartTriggered(event);
				} catch (P4IOException p4ioe) {
					unregisterClusterListener(listenerEntries, listenerEntry,
							event, p4ioe);
				} catch (P4ConnectionException p4ce) {
					unregisterClusterListener(listenerEntries, listenerEntry,
							event, p4ce);
				} catch (Throwable th) {
					DCLog.logErrorThrowable(location, null,
							"An error occurred while notifying the listener "
									+ listenerEntry
											.getDeployControllerListener()
									+ " about the event '" + event
									+ "'. The following error occurred: "
									+ th.getMessage(), th);
				}
			} else {
				// TODO: perform asynchroneous notification
			}
		}

		this.globalListenersNotify(event);

		if (location.bePath()) {
			tracePath(location, "Listeners are notified about action [{0}]. Component:[{1}]. Delay:[{2}] ms.",
				new Object[] {
						ClusterEventAction.CLUSTER_RESTART_TRIGGERED,
						"n/a",
						String.valueOf(System.currentTimeMillis()
								- beginTime) });
		}
	}

	private void unregisterClusterListener(
			Collection<ListenerEntry> listenerEntries,
			ListenerEntry listenerEntry, ClusterEvent event, Exception ex) {
		if (location.beDebug()) {
			traceDebug(
					location,
					"Listener [{0}] was unregistered because during the notification about the event [{1}] the following error occurred: [{2}]",
					new Object[] { listenerEntry.getDeployControllerListener(),
							event, ex.getMessage() });
		}
		listenerEntries.remove(listenerEntry);
	}

	protected void globalListenersNotify(DeploymentEvent event) {
		GlobalListenersList.getInstance().fireGlobalEvent(event);
		byte messageType = MessageConstants.MSG_TYPE_GLOBAL_EVENT_DEPLOY;
		notifyOtherNodesForGlobalEvent(event, messageType);
	}

	protected void globalListenersNotify(UndeploymentEvent event) {

		GlobalListenersList.getInstance().fireGlobalEvent(event);
		byte messageType = MessageConstants.MSG_TYPE_GLOBAL_EVENT_UNDEPLOY;
		notifyOtherNodesForGlobalEvent(event, messageType);
	}

	protected void globalListenersNotify(ClusterEvent event) {

		GlobalListenersList.getInstance().fireGlobalEvent(event);
		byte messageType = MessageConstants.MSG_TYPE_GLOBAL_EVENT_CLUSTER;
		notifyOtherNodesForGlobalEvent(event, messageType);
	}

	private void notifyOtherNodesForGlobalEvent(DCEvent event, byte messageType) {

		MessagingFactory messagingFactory = MessagingFactory.getInstance();
		MessageSender messageSender = messagingFactory.createMessageSender();

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(event);
			oos.close();
			baos.close();
			int msgSize = baos.size();
			if (location.beDebug()) {
				traceDebug(
						location,
						"Distribute event. type: [{0}], size :[{1}].",
						new Object[] { messageType, msgSize });
			}
			if (msgSize < MAX_MESSAGE_LENGTH) {
				byte[] bytes = baos.toByteArray();
				Message message = messagingFactory.createMessage(-1,
						ClusterElement.SERVER, messageType, bytes, 0,
						bytes.length);
				messageSender.sendAndWait(message);
			} else {
				DCLog.logError(
								location,
								"ASJ.dpl_dc.001162",
								"The message [{0}] will not be distributed because of its size[{1}].",
								new Object[] { event, String.valueOf(msgSize) });
			}
		} catch (MessagingException e) {
			DCLog.logErrorThrowable(location, e);
		} catch (IOException e) {
			DCLog.logErrorThrowable(location, e);
		}
	}

}
