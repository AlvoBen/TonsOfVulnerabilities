package com.sap.jms.client.xa;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.XAConnection;
import javax.jms.XASession;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.client.connection.Connection;
import com.sap.jms.client.connection.NetworkAdapter;
import com.sap.jms.client.session.JMSSession.SessionType;



public class JMSXAConnection extends Connection	implements XAConnection {

	public JMSXAConnection(long connectionID, String serverInstance, NetworkAdapter networkAdapter, ThreadSystem threadSystem) {
		super(connectionID, serverInstance, networkAdapter, threadSystem);

	}

	public XASession createXASession() throws JMSException {
		return (XASession)createSession(true, Session.SESSION_TRANSACTED, SessionType.XA_GENERIC_SESSION);
	}

}
