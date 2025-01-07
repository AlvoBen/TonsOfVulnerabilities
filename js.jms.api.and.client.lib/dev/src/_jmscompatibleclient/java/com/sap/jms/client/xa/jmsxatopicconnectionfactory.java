/**
 * XATopicConnectionFactory.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.xa;

import javax.jms.JMSException;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.XATopicConnection;
import javax.jms.XATopicConnectionFactory;

import com.sap.jms.client.connection.Connection.ConnectionType;

public class JMSXATopicConnectionFactory extends JMSXAConnectionFactory implements XATopicConnectionFactory, TopicConnectionFactory {


	private static final long serialVersionUID = -2442582017958412369L;

	public JMSXATopicConnectionFactory(String[] hosts, int[] ports, String serverInstance, String userName, String password, int initialPoolSize, int maxPoolSize, String systemID, String hardwareID) {
		super(hosts, ports, serverInstance, userName, password, initialPoolSize, maxPoolSize, systemID, hardwareID);
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
		setIsPasswordFieldBringsPassword(true);
		return (TopicConnection) createConnection(userName, password, ConnectionType.TOPIC_CONNECTION);
	}

}
