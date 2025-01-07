package com.sap.engine.services.dc.cm.deploy.impl;

import com.sap.engine.services.dc.cm.deploy.SyncContext;
import com.sap.engine.services.dc.util.Constants;

public class SyncContextImpl implements SyncContext {

	private static final long serialVersionUID = -7490136872886238575L;
	protected String transactionId;
	protected long sessionId;
	protected long requestId;
	protected int instanceId;

	SyncContextImpl(String transactionId, long sessionId, long requestId,
			int instanceId) {
		this.transactionId = transactionId;
		this.sessionId = sessionId;
		this.requestId = requestId;
		this.instanceId = instanceId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public long getSessionId() {
		return sessionId;
	}

	public long getRequestId() {
		return requestId;
	}

	public int getInstanceId() {
		return instanceId;
	}

	public String toString() {
		final StringBuffer sb = new StringBuffer("SyncContext[");
		sb.append("TransactionId = ");
		sb.append(getTransactionId());
		sb.append(Constants.EOL);
		sb.append("SessionId = ");
		sb.append(getSessionId());
		sb.append(Constants.EOL);
		sb.append("RequestId = ");
		sb.append(getRequestId());
		sb.append(Constants.EOL);
		sb.append("InstanceId = ");
		sb.append(getInstanceId());
		sb.append(Constants.EOL);
		sb.append("]");
		return sb.toString();
	}

}
