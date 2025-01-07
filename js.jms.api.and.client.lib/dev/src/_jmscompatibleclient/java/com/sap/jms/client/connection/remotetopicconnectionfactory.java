/**
 * RemoteTopicConnectionFactory.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.connection;

import javax.jms.JMSException;
import javax.jms.TopicConnection;

import com.sap.jms.client.connection.Connection.ConnectionType;
import com.sap.jms.server.remote.JMSRemoteServer;

/**  
 * @author Desislav Bantchovski
 * @version 7.10 
 */

public class RemoteTopicConnectionFactory extends RemoteConnectionFactory implements RemoteTopicConnectionFactoryInterface {	

	static final long serialVersionUID = -5361872547749033994L;

	public RemoteTopicConnectionFactory(JMSRemoteServer server, String serverInstance, String factoryName, String systemID, String hardwareID, String[] resources, String clientID,boolean supportsOptimization) {
		super(server, serverInstance, factoryName, systemID, hardwareID, resources, clientID,supportsOptimization);
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
