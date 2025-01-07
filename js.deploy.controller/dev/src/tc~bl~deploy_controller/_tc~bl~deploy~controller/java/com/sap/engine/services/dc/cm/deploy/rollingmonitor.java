package com.sap.engine.services.dc.cm.deploy;

import com.sap.engine.services.dc.cm.deploy.RollingSession;
import com.sap.engine.services.dc.manage.messaging.MessagingException;

public interface RollingMonitor {

	public long createSession();

	public void sendRequest(int sendTo, SyncRequest syncRequest)
			throws MessagingException;

	public void receiveResult(SyncResult syncResult);

	public RollingSession closeSession(long sessionId);

}
