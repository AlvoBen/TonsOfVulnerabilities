/**
 * QueueConnectionFactory.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.rmi;

import javax.jms.JMSException;
import javax.jms.QueueConnection;

import com.sap.jms.client.connection.Connection.ConnectionType;

public class RMIQueueConnectionFactory extends RMIConnectionFactory implements javax.jms.QueueConnectionFactory {


	static final long serialVersionUID = -5706810782152253529L;

	public RMIQueueConnectionFactory(RMIConnectionFactoryInterface connectionFactoryInterface, String vpName, String clientId, 
			boolean supportsOptimization, String connectionFactoryName, String redirectableKey) {
		super(connectionFactoryInterface, vpName, clientId, supportsOptimization, connectionFactoryName, redirectableKey);
	}


	/* (non-Javadoc)
	 * @see javax.jms.QueueConnectionFactory#createQueueConnection()
	 */
	public QueueConnection createQueueConnection() throws JMSException {
		return (QueueConnection) createConnection(ConnectionType.QUEUE_CONNECTION);
	}

	/* (non-Javadoc)
	 * @see javax.jms.QueueConnectionFactory#createQueueConnection(String, String)
	 */
	public QueueConnection createQueueConnection(String userName, String password) throws JMSException {
		return (QueueConnection) createConnection(userName, password, ConnectionType.QUEUE_CONNECTION);
	}

}
