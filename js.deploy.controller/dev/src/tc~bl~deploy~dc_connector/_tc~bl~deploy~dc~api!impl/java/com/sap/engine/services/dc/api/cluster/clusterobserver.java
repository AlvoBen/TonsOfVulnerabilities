/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.cluster;

import com.sap.engine.services.dc.api.event.ClusterListener;
import com.sap.engine.services.dc.api.event.EventMode;
import com.sap.engine.services.dc.api.event.ListenerMode;
import com.sap.engine.services.dc.api.util.DALog;
import com.sap.engine.services.dc.cm.undeploy.Undeployer;

/**
 * @author Todor Stoitsev
 * @version 7.1
 * 
 *          The class encapsulates common logic for cluster event handling.
 */

public abstract class ClusterObserver {

	protected final DALog daLog;
	protected RemoteClusterListenerImpl localClusterListener;
	protected RemoteClusterListenerImpl globalClusterListener;

	public ClusterObserver(DALog daLog) {
		this.daLog = daLog;
		// this.globalClusterListener = new RemoteClusterListenerImpl(
		// this.daLog, false );
	}

	protected abstract void registerRemoteClusterListener(
			RemoteClusterListenerImpl listener,
			com.sap.engine.services.dc.event.ListenerMode listenerMode,
			com.sap.engine.services.dc.event.EventMode eventMode);

	protected abstract void unregisterRemoteClusterListener(
			RemoteClusterListenerImpl remoteClusterListener);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.deploy.DeployProcessor#addClusterListener
	 * (com.sap.engine.services.dc.api.event.ClusterListener,
	 * com.sap.engine.services.dc.api.event.ListenerMode,
	 * com.sap.engine.services.dc.api.event.EventMode)
	 */
	public void addClusterListener(ClusterListener listener,
			ListenerMode listenerMode, EventMode eventMode) {
		RemoteClusterListenerImpl remoteClusterListenerImpl = null;
		if (ListenerMode.LOCAL.equals(listenerMode)) {
			if (this.localClusterListener == null) {
				this.localClusterListener = new RemoteClusterListenerImpl(
						this.daLog, true);
			}
			remoteClusterListenerImpl = this.localClusterListener;

		} else if (ListenerMode.GLOBAL.equals(listenerMode)) {
			if (this.globalClusterListener == null) {
				this.globalClusterListener = new RemoteClusterListenerImpl(
						this.daLog, false);
				registerRemoteClusterListener(this.globalClusterListener,
						com.sap.engine.services.dc.event.ListenerMode.GLOBAL,
						com.sap.engine.services.dc.event.EventMode.SYNCHRONOUS);
			}
			remoteClusterListenerImpl = this.globalClusterListener;

		} else {
			throw new IllegalArgumentException(
					"[ERROR CODE DPL.DCAPI.1012] Unknown Listener Mode '"
							+ listener + "'.");
		}
		remoteClusterListenerImpl.addListener(listener, eventMode);
	}

	protected void removeFromDistinctClusterListener(
			RemoteClusterListenerImpl remoteClusterListener,
			ClusterListener clusterListener) {
		if (remoteClusterListener == null) {
			return;
		}
		remoteClusterListener.removeListener(clusterListener);
		if (remoteClusterListener.getListenersCount() == 0) {
			if (remoteClusterListener.isLocalListener()) {
				unregisterRemoteClusterListener(this.localClusterListener);
				this.localClusterListener = null;
			} else {
				unregisterRemoteClusterListener(this.globalClusterListener);
				this.globalClusterListener = null;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.deploy.DeployProcessor#removeClusterListener
	 * (com.sap.engine.services.dc.api.event.ClusterListener)
	 */
	public void removeClusterListener(ClusterListener listener) {
		removeFromDistinctClusterListener(this.localClusterListener, listener);
		removeFromDistinctClusterListener(this.globalClusterListener, listener);
	}
}
