package com.sap.engine.services.dc.cm.deploy;

import java.util.HashMap;

public interface RollingSession {

	public HashMap<Long, SyncRequest> getUnansweredSentMessages();

	public HashMap<Long, SyncResult> getReceivedMessages();

	public long getSessionId();

	public boolean isClosed();

	public void close();

}
