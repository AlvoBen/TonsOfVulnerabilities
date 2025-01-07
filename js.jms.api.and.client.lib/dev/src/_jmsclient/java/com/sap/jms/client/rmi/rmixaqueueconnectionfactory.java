/**
 * XAQueueConnectionFactory.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.rmi;

import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.XAQueueConnection;
import javax.jms.XAQueueConnectionFactory;

import com.sap.engine.frame.core.thread.ThreadSystem;

import com.sap.jms.client.connection.Connection.ConnectionType;

public class RMIXAQueueConnectionFactory extends RMIXAConnectionFactory implements XAQueueConnectionFactory, QueueConnectionFactory {

	private static final long serialVersionUID = 7437742037798072946L;

	public RMIXAQueueConnectionFactory(RMIConnectionFactoryInterface connectionFactoryInterface, String vpName, String clientId, 
			boolean supportsOptimization, String connectionFactoryName, String redirectableKey) {
		super(connectionFactoryInterface, vpName, clientId, supportsOptimization, connectionFactoryName, redirectableKey);
	}

	public javax.jms.XAQueueConnection createXAQueueConnection() throws JMSException {
		return (javax.jms.XAQueueConnection)createConnection(ConnectionType.XA_QUEUE_CONNECTION);
	}

	public javax.jms.XAQueueConnection createXAQueueConnection(String user, String password) throws JMSException {
		return (XAQueueConnection)createConnection(user, password, ConnectionType.XA_QUEUE_CONNECTION);
	}

	public QueueConnection createQueueConnection() throws JMSException {
		return (QueueConnection)createConnection(ConnectionType.QUEUE_CONNECTION);
	}

	public QueueConnection createQueueConnection(String userName, String password) throws JMSException {
		return (QueueConnection)createConnection(userName, password, ConnectionType.QUEUE_CONNECTION);
	}

}
