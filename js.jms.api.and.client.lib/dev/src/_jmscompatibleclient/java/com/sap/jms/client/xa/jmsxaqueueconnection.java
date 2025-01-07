/**
 * XAQueueConnection.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.xa;

import javax.jms.ConnectionConsumer;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.XAQueueConnection;
import javax.jms.XAQueueSession;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.client.connection.NetworkAdapter;
import com.sap.jms.client.session.JMSSession.SessionType;


public class JMSXAQueueConnection extends JMSXAConnection implements XAQueueConnection, QueueConnection {


	public JMSXAQueueConnection(long connectionID, String serverInstance, NetworkAdapter networkAdapter, ThreadSystem threadSystem) {
		super(connectionID, serverInstance, networkAdapter, threadSystem);
	}

	public XAQueueSession createXAQueueSession() throws JMSException {
		return (XAQueueSession)createSession(true, Session.SESSION_TRANSACTED, SessionType.XA_QUEUE_SESSION);
	}

	public QueueSession createQueueSession(boolean transacted, int acknowledgeMode) throws JMSException {
		return (QueueSession)createSession(transacted, acknowledgeMode, SessionType.QUEUE_SESSION);
	}

	public ConnectionConsumer createConnectionConsumer(Queue destination, String messageSelector, ServerSessionPool serverSessionPool, int maxMessages) throws JMSException {
		return createConnectionConsumer(destination, messageSelector, serverSessionPool, maxMessages);
	}
}
