package com.sap.engine.services.dc.cm.deploy;

import java.io.Serializable;

public interface SyncContext extends Serializable {

	public String getTransactionId();

	public long getSessionId();

	public long getRequestId();

	public int getInstanceId();

}
