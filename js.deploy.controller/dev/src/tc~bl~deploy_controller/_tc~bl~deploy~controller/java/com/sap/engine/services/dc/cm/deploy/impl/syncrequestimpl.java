package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.Collection;

import com.sap.engine.services.dc.cm.deploy.SyncContext;
import com.sap.engine.services.dc.cm.deploy.SyncItem;
import com.sap.engine.services.dc.cm.deploy.SyncRequest;
import com.sap.engine.services.dc.util.Constants;

public class SyncRequestImpl implements SyncRequest {

	private static final long serialVersionUID = -4908527442164238119L;
	private final Collection<SyncItem> syncItems; // $JL-SER$
	private final boolean isOffline;
	private final SyncContext syncContext;
	private int senderId;

	SyncRequestImpl(String transactionId, int senderId,
			Collection<SyncItem> syncItems, boolean isOffline, long sessionId,
			int instanceId) {
		this.syncContext = new SyncContextImpl(transactionId, sessionId, System
				.currentTimeMillis(), instanceId);
		this.senderId = senderId;
		this.syncItems = syncItems;
		this.isOffline = isOffline;
	}

	public Collection<SyncItem> getSyncItems() {
		return syncItems;
	}

	public boolean isOffline() {
		return isOffline;
	}

	public int getSenderId() {
		return senderId;
	}

	public SyncContext getSyncContext() {
		return syncContext;
	}

	public String toString() {
		final StringBuffer sb = new StringBuffer("SyncRequest[");
		sb.append("SyncItems = ");
		sb.append(getSyncItems());
		sb.append(Constants.EOL);
		sb.append("IsOffline = ");
		sb.append(isOffline());
		sb.append(Constants.EOL);
		sb.append("SyncContext = ");
		sb.append(getSyncContext());
		sb.append(Constants.EOL);
		sb.append("]");
		return sb.toString();
	}
}
