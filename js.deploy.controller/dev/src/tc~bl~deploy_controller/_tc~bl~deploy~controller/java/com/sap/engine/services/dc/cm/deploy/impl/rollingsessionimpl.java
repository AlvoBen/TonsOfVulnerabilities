package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.HashMap;

import com.sap.engine.services.dc.cm.deploy.RollingSession;
import com.sap.engine.services.dc.cm.deploy.SyncRequest;
import com.sap.engine.services.dc.cm.deploy.SyncResult;

public class RollingSessionImpl implements RollingSession {

	private long sessionId;
	private HashMap<Long, SyncRequest> answeredSentMessages = new HashMap<Long, SyncRequest>();
	private HashMap<Long, SyncResult> receivedMessages = new HashMap<Long, SyncResult>();
	private boolean isClosed = false;

	RollingSessionImpl(long sessionId) {
		this.sessionId = sessionId;
	}

	public HashMap<Long, SyncRequest> getUnansweredSentMessages() {
		return answeredSentMessages;
	}

	public HashMap<Long, SyncResult> getReceivedMessages() {
		return receivedMessages;
	}

	public long getSessionId() {
		return sessionId;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void close() {
		isClosed = true;

	}

}
