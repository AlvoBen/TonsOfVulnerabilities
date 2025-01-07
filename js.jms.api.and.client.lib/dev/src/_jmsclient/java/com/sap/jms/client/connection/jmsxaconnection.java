package com.sap.jms.client.connection;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.XAConnection;
import javax.jms.XASession;

import com.sap.jms.client.session.JMSSession.SessionType;
import com.sap.jms.util.TaskManager;

public class JMSXAConnection extends Connection	implements XAConnection {

	public JMSXAConnection(long connectionId, ServerFacade serverFacade, ClientFacade clientFacade, String vpName, String clientId, TaskManager taskManager, boolean supportsOptimization) {
		super(connectionId, serverFacade, clientFacade, vpName, clientId, taskManager, supportsOptimization);

	}

	public XASession createXASession() throws JMSException {
		return (XASession)createSession(true, Session.SESSION_TRANSACTED, SessionType.XA_GENERIC_SESSION);
	}

}
