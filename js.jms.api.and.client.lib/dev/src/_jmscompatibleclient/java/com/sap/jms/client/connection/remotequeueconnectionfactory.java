/**
 * QueueConnectionFactory.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.connection;

import javax.jms.JMSException;
import javax.jms.QueueConnection;

import com.sap.jms.client.connection.Connection.ConnectionType;
import com.sap.jms.server.remote.JMSRemoteServer;

/**  
 * @author Desislav Bantchovski
 * @version 7.10 
 */

public class RemoteQueueConnectionFactory extends RemoteConnectionFactory implements RemoteQueueConnectionFactoryInterface {

	static final long serialVersionUID = -5706810782152253529L;


	public RemoteQueueConnectionFactory(JMSRemoteServer server, String serverInstance, String factoryName, String systemID, String hardwareID, String[] resources, String clientID,boolean supportsOptimization) {
		super(server, serverInstance, factoryName, systemID, hardwareID, resources, clientID,supportsOptimization);
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
