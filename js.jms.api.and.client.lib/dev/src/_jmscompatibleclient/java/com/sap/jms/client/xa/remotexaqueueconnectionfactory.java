/**
 * XAQueueConnectionFactory.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.xa;

import javax.jms.JMSException;
import javax.jms.QueueConnection;

import com.sap.jms.server.remote.JMSRemoteServer;
import com.sap.jms.client.connection.RemoteQueueConnectionFactoryInterface;
import com.sap.jms.client.connection.Connection.ConnectionType;

/**  
 * @author Desislav Bantchovski
 * @version 7.10 
 */

public class RemoteXAQueueConnectionFactory
extends RemoteXAConnectionFactory
implements RemoteXAQueueConnectionFactoryInterface, RemoteQueueConnectionFactoryInterface {


	public RemoteXAQueueConnectionFactory(
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
	 * Method createXAQueueConnection. Creates an XA queue connection with the default user identity. 
	 * The connection is created in stopped mode. No messages will be delivered until the Connection.start 
	 * method is explicitly called.
	 * @return a newly created XA queue connection
	 * @throws JMSException if the JMS provider fails to create an XA queue connection due to some internal error.
	 * @throws JMSSecurityException if client authentication fails due to an invalid user name or password.
	 */
	public javax.jms.XAQueueConnection createXAQueueConnection() throws JMSException {
		return (javax.jms.XAQueueConnection) createConnection(ConnectionType.XA_QUEUE_CONNECTION);
	}

	/**
	 * Method createXAQueueConnection. Creates an XA queue connection with the specified user identity. 
	 * The connection is created in stopped mode. No messages will be delivered until the Connection.start 
	 * method is explicitly called.
	 * @param user the caller's user name
	 * @param password the caller's password
	 * @return a newly created XA queue connection
	 * @throws JMSException if the JMS provider fails to create an XA queue connection due to some internal error.
	 * @throws JMSSecurityException if client authentication fails due to an invalid user name or password.
	 */
	public javax.jms.XAQueueConnection createXAQueueConnection(String user, String password) throws JMSException {
		return (javax.jms.XAQueueConnection) createConnection(user, password, ConnectionType.XA_QUEUE_CONNECTION);
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
