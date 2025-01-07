package com.sap.engine.services.dc.cm.deploy.impl;

import com.sap.engine.services.dc.cm.deploy.InstanceData;
import com.sap.engine.services.dc.cm.deploy.SyncRequest;
import com.sap.engine.services.dc.util.Constants;

class InstanceDataImpl implements InstanceData {
	private final int instanceId;
	private final SyncRequest syncRequest;
	private boolean isProcessed;
	private final int hashCode;
	private final StringBuffer toString;

	InstanceDataImpl(int instanceId, SyncRequest syncRequest,
			boolean isProcessed) {
		this.instanceId = instanceId;
		this.syncRequest = syncRequest;
		this.isProcessed = isProcessed;

		this.hashCode = evaluateHashCode();
		this.toString = evaluateToString();

	}

	public int getInstanceId() {
		return instanceId;
	}

	public SyncRequest getSyncRequest() {
		return syncRequest;
	}

	public boolean isProcessed() {
		return isProcessed;
	}

	public void setProcessed(boolean isProcessed) {
		this.isProcessed = isProcessed;
	}

	// ************************** OBJECT **************************//

	public int hashCode() {
		return hashCode;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}

		final InstanceData otherInstanceDescriptor = (InstanceData) obj;
		if (this.getInstanceId() != otherInstanceDescriptor.getInstanceId()) {
			return false;
		}

		return true;
	}

	public String toString() {
		return toString.toString();
	}

	// ************************** OBJECT **************************//

	// ************************** PRIVATE **************************//

	public int evaluateHashCode() {
		final int offset = 17;

		int result = offset + getInstanceId();

		return result;
	}

	private StringBuffer evaluateToString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("InstanceID = ");
		sb.append(getInstanceId());
		sb.append(Constants.EOL);
		sb.append("SyncRequest = ");
		sb.append(getSyncRequest());
		sb.append(Constants.EOL);
		sb.append("IsProcessed = ");
		sb.append(isProcessed());
		sb.append(Constants.EOL);
		return sb;
	}

	// ************************** PRIVATE **************************//

}
