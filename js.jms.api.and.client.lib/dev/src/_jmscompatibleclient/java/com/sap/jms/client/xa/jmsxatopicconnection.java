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
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.XATopicConnection;
import javax.jms.XATopicSession;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.client.connection.NetworkAdapter;
import com.sap.jms.client.session.JMSSession.SessionType;


public class JMSXATopicConnection extends JMSXAConnection implements XATopicConnection, TopicConnection {


	public JMSXATopicConnection(long connectionID, String serverInstance, NetworkAdapter networkAdapter, ThreadSystem threadSystem) {
		super(connectionID, serverInstance, networkAdapter, threadSystem);
	}

	public XATopicSession createXATopicSession() throws JMSException {
		return (XATopicSession)createSession(true, Session.SESSION_TRANSACTED, SessionType.XA_TOPIC_SESSION);
	}

	public TopicSession createTopicSession(boolean transacted, int acknowledgeMode) throws JMSException {
		return (TopicSession)createSession(transacted, acknowledgeMode, SessionType.TOPIC_SESSION);
	}

	public ConnectionConsumer createConnectionConsumer(Topic destination, String messageSelector, ServerSessionPool serverSessionPool, int maxMessages) throws JMSException {
		return createConnectionConsumer(destination, messageSelector, serverSessionPool, maxMessages);
	}
}
