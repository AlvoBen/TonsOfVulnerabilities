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

import com.sap.jms.server.remote.JMSRemoteServer;
import com.sap.jms.client.connection.RemoteTopicConnectionFactoryInterface;
import com.sap.jms.client.connection.Connection.ConnectionType;

/**  
 * @author Desislav Bantchovski
 * @version 7.10 
 */

public class RemoteXATopicConnectionFactory extends RemoteXAConnectionFactory
implements RemoteXATopicConnectionFactoryInterface, RemoteTopicConnectionFactoryInterface {


	public RemoteXATopicConnectionFactory(
			JMSRemoteServer server,  
			String serverInstance,
			String factoryName,
			String systemID,
			String hardwareID,
			String[] resources,
			String clientID,
			boolean supportsOptimization) {
		super(server, serverInstance, factoryName, systemID, hardwareID, resources, clientID, supportsOptimization);
	}


	/**
	 * Method createXATopicConnection. Creates an XA topic connection with the default user identity. 
	 * The connection is created in stopped mode. No messages will be delivered until the Connection.start 
	 * method is explicitly called.
	 * @return a newly created XA topic connection
	 * @throws JMSException if the JMS provider fails to create an XA topic connection due to some internal error.
	 * @throws JMSSecurityException if client authentication fails due to an invalid user name or password.
	 */
	public javax.jms.XATopicConnection createXATopicConnection() throws JMSException {
		return (javax.jms.XATopicConnection) createConnection(ConnectionType.XA_TOPIC_CONNECTION);
	}

	/**
	 * Method createXATopicConnection. Creates an XA topic connection with the specified user identity. 
	 * The connection is created in stopped mode. No messages will be delivered until the Connection.start 
	 * method is explicitly called.
	 * @param user the caller's user name
	 * @param password the caller's password
	 * @return a newly created XA topic connection
	 * @throws JMSException if the JMS provider fails to create an XA topic connection due to some internal error.
	 * @throws JMSSecurityException if client authentication fails due to an invalid user name or password.
	 */
	public javax.jms.XATopicConnection createXATopicConnection(String user, String password) throws JMSException {
		return (javax.jms.XATopicConnection) createConnection(user, password, ConnectionType.XA_TOPIC_CONNECTION);
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
