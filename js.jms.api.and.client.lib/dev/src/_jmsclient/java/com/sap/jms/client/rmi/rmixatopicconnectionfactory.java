/**
 * XATopicConnectionFactory.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.rmi;

import javax.jms.JMSException;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.XATopicConnection;
import javax.jms.XATopicConnectionFactory;

import com.sap.jms.client.connection.Connection.ConnectionType;

import com.sap.engine.frame.core.thread.ThreadSystem;


public class RMIXATopicConnectionFactory extends RMIXAConnectionFactory implements XATopicConnectionFactory, TopicConnectionFactory {


	private static final long serialVersionUID = -2442582017958412369L;

	public RMIXATopicConnectionFactory(RMIConnectionFactoryInterface connectionFactoryInterface, String vpName, String clientId, 
			boolean supportsOptimization, String connectionFactoryName, String redirectableKey) {
		super(connectionFactoryInterface, vpName, clientId, supportsOptimization, connectionFactoryName, redirectableKey);
	}
	
	public XATopicConnection createXATopicConnection() throws JMSException {
		return (XATopicConnection) createConnection(ConnectionType.XA_TOPIC_CONNECTION);
	}

	public javax.jms.XATopicConnection createXATopicConnection(String user, String password) throws JMSException {
		return (XATopicConnection) createConnection(user, password, ConnectionType.XA_TOPIC_CONNECTION);
	}

	public TopicConnection createTopicConnection() throws JMSException {
		return (TopicConnection) createConnection(ConnectionType.TOPIC_CONNECTION);
	}

	public TopicConnection createTopicConnection(String userName, String password) throws JMSException {
		return (TopicConnection) createConnection(userName, password, ConnectionType.TOPIC_CONNECTION);
	}

}
