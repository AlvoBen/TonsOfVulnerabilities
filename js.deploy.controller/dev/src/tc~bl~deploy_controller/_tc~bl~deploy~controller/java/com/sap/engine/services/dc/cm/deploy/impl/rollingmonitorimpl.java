package com.sap.engine.services.dc.cm.deploy.impl;

import static com.sap.engine.services.dc.util.RollingUtils.sendMessage;
import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.util.HashMap;
import java.util.Hashtable;

import com.sap.engine.services.dc.cm.deploy.RollingMonitor;
import com.sap.engine.services.dc.cm.deploy.RollingSession;
import com.sap.engine.services.dc.cm.deploy.SyncContext;
import com.sap.engine.services.dc.cm.deploy.SyncRequest;
import com.sap.engine.services.dc.cm.deploy.SyncResult;
import com.sap.engine.services.dc.manage.messaging.MessageConstants;
import com.sap.engine.services.dc.manage.messaging.MessagingException;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

class RollingMonitorImpl implements RollingMonitor {
	private  final Location location = DCLog.getLocation(this.getClass());

	private static final int ANSWER_TIMEOUT = 3600000;

	private final static RollingMonitorImpl INSTANCE = new RollingMonitorImpl();

	private Hashtable<Long, RollingSessionImpl> sessions = new Hashtable<Long, RollingSessionImpl>();

	private RollingMonitorImpl() {
	}

	static RollingMonitorImpl getInstance() {
		return INSTANCE;
	}

	public long createSession() {
		RollingSessionImpl rollingSession = new RollingSessionImpl(System
				.currentTimeMillis());
		long result = rollingSession.getSessionId();
		sessions.put(rollingSession.getSessionId(), rollingSession);
		return result;
	}

	public void sendRequest(int sendTo, SyncRequest syncRequest)
			throws MessagingException {
		SyncContext syncContext = syncRequest.getSyncContext();
		RollingSessionImpl rollingSession = sessions.get(syncContext
				.getSessionId());
		if (rollingSession == null) {
			throw new RollingSessionRuntimeException("Session "
					+ syncContext.getSessionId()
					+ " declared in the message does not exist.");
		}
		sendMessage(sendTo, syncRequest,
				MessageConstants.MSG_TYPE_ROLLING_EVENT_SYNC);
		synchronized (rollingSession) {
			if (rollingSession.isClosed()) {
				throw new RollingSessionRuntimeException("Session "
						+ syncContext.getSessionId()
						+ " declared in the message has already closed.");
			}
			rollingSession.getUnansweredSentMessages().put(
					syncContext.getRequestId(), syncRequest);
		}
	}

	public void receiveResult(SyncResult syncResult) {
		SyncContext syncContext = syncResult.getSyncContext();
		RollingSessionImpl rollingSession = sessions.get(syncContext
				.getSessionId());
		if (rollingSession == null) {
			if (location.beDebug()) {
				traceDebug(location, 
						"Session [{0}] declared in the message does not exist.",
						new Object[] { syncContext.getSessionId() });
			}
		}
		synchronized (rollingSession) {
			if (rollingSession.isClosed()) {
				if (location.beDebug()) {
					traceDebug(location, 
							"Session [{0}] declared in the message has already closed.",
							new Object[] { syncContext.getSessionId() });
				}
			}
			HashMap<Long, SyncRequest> unansweredSentMessages = rollingSession
					.getUnansweredSentMessages();
			long requestId = syncContext.getRequestId();
			if (unansweredSentMessages.remove(requestId) != null) {
				rollingSession.getReceivedMessages().put(requestId, syncResult);
			}
			if (unansweredSentMessages.isEmpty()) {
				rollingSession.notifyAll();
			}
		}
	}

	public RollingSession closeSession(long sessionId) {
		RollingSessionImpl rollingSession = sessions.get(sessionId);
		if (rollingSession == null) {
			throw new RollingSessionRuntimeException("Session " + sessionId
					+ " does not exist.");
		}
		RollingSessionImpl result = null;
		synchronized (rollingSession) {
			if (rollingSession.isClosed()) {
				throw new RollingSessionRuntimeException("Session " + sessionId
						+ " has already closed.");
			}
			if (!rollingSession.getUnansweredSentMessages().isEmpty()) {
				try {
					rollingSession.wait(ANSWER_TIMEOUT);
				} catch (InterruptedException e) {
					DCLog.logErrorThrowable(location, e);
				}
			}
			rollingSession.close();
			result = rollingSession;
			sessions.remove(sessionId);
		}
		return result;
	}

}
