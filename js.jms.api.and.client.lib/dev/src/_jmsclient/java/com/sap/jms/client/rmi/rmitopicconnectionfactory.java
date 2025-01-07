/**
 * TopicConnectionFactory.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.rmi;

import javax.jms.JMSException;
import javax.jms.TopicConnection;

import com.sap.jms.client.connection.Connection.ConnectionType;

public class RMITopicConnectionFactory extends RMIConnectionFactory implements javax.jms.TopicConnectionFactory {

	static final long serialVersionUID = -5361872547749033994L;
	
	public RMITopicConnectionFactory(RMIConnectionFactoryInterface connectionFactoryInterface, String vpName, String clientId, 
			boolean supportsOptimization, String connectionFactoryName, String redirectableKey) {
		super(connectionFactoryInterface, vpName, clientId, supportsOptimization, connectionFactoryName, redirectableKey);
	}

	/* (non-Javadoc)
	 * @see javax.jms.TopicConnectionFactory#createTopicConnection()
	 */
	public TopicConnection createTopicConnection() throws JMSException {
		return (TopicConnection) createConnection(ConnectionType.TOPIC_CONNECTION); 
	}

	/* (non-Javadoc)
	 * @see javax.jms.TopicConnectionFactory#createTopicConnection(String, String)
	 */
	public TopicConnection createTopicConnection(String userName, String password) throws JMSException {
		return (TopicConnection) createConnection(userName, password, ConnectionType.TOPIC_CONNECTION);
	}

	
}
