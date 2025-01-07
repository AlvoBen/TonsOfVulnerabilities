/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Jan 4, 2006
 */
package com.sap.engine.services.dc.api.lcm.impl;

import java.util.ArrayList;
import java.util.Iterator;

import com.sap.engine.services.dc.api.event.EventMode;
import com.sap.engine.services.dc.api.event.LCEvent;
import com.sap.engine.services.dc.api.util.DALog;
import com.sap.engine.services.rmi_p4.P4ConnectionException;
import com.sap.engine.services.rmi_p4.P4IOException;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD></DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2005, SAP-AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>Jan 4, 2006</DD>
 * </DL>
 * 
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.1
 * 
 */

public class RemoteLCEventListener implements
		com.sap.engine.services.dc.event.LCEventListener {
	private transient final ArrayList listenersSynch = new ArrayList();
	private transient final ArrayList listenersAsynch = new ArrayList();
	private transient final DALog daLog;

	RemoteLCEventListener(DALog daLog) {
		this.daLog = daLog;
	}

	public void lifeCycleEventTriggered(
			com.sap.engine.services.dc.event.LCEvent dcEvent)
			throws P4IOException, P4ConnectionException {
		if (getListenersCount() == 0) {
			return;
		}
		final LCEvent daEvent = LCMEventMapper.mapLCEvent(dcEvent);
		processLifeCycleEventTriggeredSynch(daEvent);
		processLifeCycleEventTriggeredAsynch(daEvent);
	}

	private void processLifeCycleEventTriggeredSynch(LCEvent daEvent) {
		for (Iterator iter = this.listenersSynch.iterator(); iter.hasNext();) {
			com.sap.engine.services.dc.api.event.LCEventListener listener = (com.sap.engine.services.dc.api.event.LCEventListener) iter
					.next();
			try {
				listener.lifeCycleEventTriggered(daEvent);
			} catch (Exception e) {
				daLog.logError("ASJ.dpl_api.001099", "Error: [{0}]",
						new Object[] { e.getLocalizedMessage() });
			}
		}
	}

	private void processLifeCycleEventTriggeredAsynch(final LCEvent daEvent) {
		Runnable ceRbl = new Runnable() {
			public void run() {
				for (Iterator iter = listenersAsynch.iterator(); iter.hasNext();) {
					com.sap.engine.services.dc.api.event.LCEventListener listener = (com.sap.engine.services.dc.api.event.LCEventListener) iter
							.next();
					try {
						listener.lifeCycleEventTriggered(daEvent);
					} catch (Exception e) {
						daLog.logError("ASJ.dpl_api.001100", "Error: [{0}]",
								new Object[] { e.getLocalizedMessage() });
					}
				}
			}
		};
		Thread asynchClusterEvtProcessor = new Thread(ceRbl);
		asynchClusterEvtProcessor.start();
	}

	synchronized void addLCEventListener(
			com.sap.engine.services.dc.api.event.LCEventListener listener,
			EventMode eventMode) {
		this.listenersSynch.remove(listener);
		this.listenersAsynch.remove(listener);
		if (EventMode.SYNCHRONOUS.equals(eventMode)) {
			this.listenersSynch.add(listener);
		} else if (EventMode.ASYNCHRONOUS.equals(eventMode)) {
			this.listenersAsynch.add(listener);
		} else {
			throw new IllegalArgumentException(
					"[ERROR CODE DPL.DCAPI.1106] Unknown Event Mode '"
							+ eventMode + "'.");
		}
	}

	synchronized void removeLCEventListener(
			com.sap.engine.services.dc.api.event.LCEventListener listener) {
		this.listenersSynch.remove(listener);
		this.listenersAsynch.remove(listener);
	}

	synchronized int getListenersCount() {
		return this.listenersSynch.size() + this.listenersAsynch.size();
	}

	/**
	 * Required for the unique identification of the listener on server for
	 * add/remove
	 * 
	 * @see com.sap.engine.services.dc.event.LCEventListener#getId()
	 */
	public int getId() {
		return hashCode();
	}
}
