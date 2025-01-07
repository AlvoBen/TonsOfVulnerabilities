package com.sap.engine.services.dc.cm.deploy.impl;

import com.sap.engine.services.dc.cm.deploy.SyncContext;
import com.sap.engine.services.dc.cm.deploy.SyncException;
import com.sap.engine.services.dc.cm.deploy.SyncResult;

class SyncResultImpl implements SyncResult {

	private static final long serialVersionUID = -8461639379883843387L;
	private final SyncException syncException;
	private final SyncContext syncContext;
	private final int senderId;

	SyncResultImpl(SyncContext syncContext, int senderId,
			SyncException syncException) {
		this.syncContext = syncContext;
		this.senderId = senderId;
		this.syncException = syncException;
	}

	public SyncException getSyncException() {
		return syncException;
	}

	public int getSenderId() {
		return senderId;
	}

	public SyncContext getSyncContext() {
		return syncContext;
	}

}
