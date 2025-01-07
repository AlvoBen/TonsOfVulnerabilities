/**
 * QueueConnection.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.connection;

import javax.jms.ConnectionConsumer;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.ServerSessionPool;

import com.sap.jms.client.session.JMSSession.SessionType;
import com.sap.jms.util.TaskManager;

public class QueueConnection extends Connection implements javax.jms.QueueConnection {

	public QueueConnection(long connectionId, ServerFacade serverFacade, ClientFacade clientFacade, String vpName, String clientId, TaskManager taskManager, boolean supportsOptimization) {
		super(connectionId, serverFacade, clientFacade, vpName, clientId, taskManager, supportsOptimization);
	}

	public QueueSession createQueueSession(boolean transacted, int acknowledgeMode) throws JMSException {
		return (QueueSession) createSession(transacted, acknowledgeMode, SessionType.QUEUE_SESSION);
	}

	public ConnectionConsumer createConnectionConsumer(Queue destination, String messageSelector, ServerSessionPool serverSessionPool, int maxMessages) throws JMSException {
		return super.createConnectionConsumer(destination, messageSelector, serverSessionPool, maxMessages);
	}

}
