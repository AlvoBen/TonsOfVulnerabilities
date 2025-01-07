/*
 * Created on May 5, 2005
 */
package com.sap.engine.services.dc.api.cluster;

import java.util.ArrayList;
import java.util.Iterator;

import com.sap.engine.services.dc.api.event.ClusterEvent;
import com.sap.engine.services.dc.api.event.ClusterListener;
import com.sap.engine.services.dc.api.event.DAEvent;
import com.sap.engine.services.dc.api.event.EventMode;
import com.sap.engine.services.dc.api.event.impl.AbstractListenerImpl;
import com.sap.engine.services.dc.api.util.DALog;
import com.sap.engine.services.rmi_p4.P4ConnectionException;
import com.sap.engine.services.rmi_p4.P4IOException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-27
 * 
 * @author Boris Savov(i030791)
 * @author Todor Stoitsev
 * @author Shenol Yousouf
 * @version 1.0
 * @since 7.1
 * 
 */
public class RemoteClusterListenerImpl extends AbstractListenerImpl implements
		com.sap.engine.services.dc.event.ClusterListener {

	RemoteClusterListenerImpl(DALog daLog, boolean isLocalListener) {
		super(daLog, isLocalListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.event.ClusterListener#clusterRestartTriggered
	 * (com.sap.engine.services.dc.event.ClusterEvent)
	 */
	public void clusterRestartTriggered(
			com.sap.engine.services.dc.event.ClusterEvent clusterEvt)
			throws P4IOException, P4ConnectionException {
		ClusterEvent daClusterEvent = ClusterEventMapper
				.mapClusterEvent(clusterEvt);
		clusterRestartTriggered(daClusterEvent);
	}

	public void clusterRestartTriggered(ClusterEvent daClusterEvent) {
		if (this.daLog != null && daClusterEvent != null && isLocalListener()) {
			if (daLog.isDebugLoggable()) {
				this.daLog.logDebug("Event received: [{0}]",
						new Object[] { daClusterEvent });
			}
		}

		dispatch(daClusterEvent);
	}

	public static final RemoteClusterListenerImpl join(
			RemoteClusterListenerImpl one, RemoteClusterListenerImpl other,
			boolean isLocal) {
		if ((one != null && one.getListenersCount() > 0)
				|| (other != null && other.getListenersCount() > 0)) {
			RemoteClusterListenerImpl joined = new RemoteClusterListenerImpl(
					joinLog(one, other), isLocal);
			joined.abstractJoin(one, other);
			return joined;
		} else {
			return null;
		}
	}

	protected void dispatchEvent(final DAEvent event, ArrayList listenersList) {
		ClusterEvent daClusterEvent = (ClusterEvent) event;
		for (Iterator iter = listenersList.iterator(); iter.hasNext();) {
			ClusterListener element = (ClusterListener) iter.next();
			try {
				element.clusterRestartTriggered(daClusterEvent);
			} catch (Exception e) {
				if (this.daLog != null) {
					this.daLog.logError("ASJ.dpl_api.001003", "Error: [{0}]",
							new Object[] { e.getLocalizedMessage() });
				}
			}
		}
	}

	protected boolean checkListenerType(final Object listener) {
		if (listener instanceof ClusterListener) {
			return true;
		}
		return false;
	}

}