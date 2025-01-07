package com.sap.engine.services.dc.cm.deploy.impl;

import com.sap.engine.services.dc.cm.deploy.BatchItemId;
import com.sap.engine.services.dc.cm.deploy.SyncItem;
import com.sap.engine.services.dc.util.Constants;

abstract class AbstractSyncItemImpl implements SyncItem {

	protected final BatchItemId batchItemId;

	AbstractSyncItemImpl(BatchItemId batchItemId) {
		this.batchItemId = batchItemId;
	}

	public BatchItemId getBatchItemId() {
		return batchItemId;
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

		SyncItem otherSyncItem = (SyncItem) obj;

		if (!this.getBatchItemId().equals(otherSyncItem.getBatchItemId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		final int offset = 17;
		int result = offset + this.getBatchItemId().hashCode();

		return result;
	}

	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("BatchItemId = ");
		sb.append(getBatchItemId());
		sb.append(Constants.EOL);
		return sb.toString();
	}

}
