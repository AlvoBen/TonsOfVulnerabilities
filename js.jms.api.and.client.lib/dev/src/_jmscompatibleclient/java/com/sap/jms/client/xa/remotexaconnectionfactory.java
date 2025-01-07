/**
 * RemoteXAConnectionFactory.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.xa;

import javax.jms.JMSException;

import com.sap.jms.client.connection.RemoteConnectionFactory;
import com.sap.jms.client.connection.Connection.ConnectionType;
import com.sap.jms.server.remote.JMSRemoteServer;

/**  
 * @author Desislav Bantchovski
 * @version 7.10 
 */

public class RemoteXAConnectionFactory extends RemoteConnectionFactory implements RemoteXAConnectionFactoryInterface {

	public RemoteXAConnectionFactory(
			JMSRemoteServer server,  
			String serverInstance,
			String factroyName,
			String systemID,
			String hardwareID,
			String[] resources,
			String clientID,
			boolean supportsOptimization) {	
		super(server, serverInstance, factroyName, systemID, hardwareID, resources, clientID,supportsOptimization);
	}

	/**
	 * Method createXAConnection. Creates an XAConnection with the default user identity. 
	 * The connection is created in stopped mode. No messages will be delivered until the 
	 * Connection.start method is explicitly called.
	 * @return XAConnection a newly created XAConnection
	 * @throws JMSException  if the JMS provider fails to create an XA connection due to some 
	 * internal error.
	 * @throws JMSSecurityException if client authentication fails due to an invalid user 
	 * name or password.
	 */
	public javax.jms.XAConnection createXAConnection() throws JMSException {
		return (javax.jms.XAConnection) createConnection(ConnectionType.XA_GENERIC_CONNECTION);
	}

	/**
	 * Method createXAConnection. Creates an XA connection with the specified user identity. 
	 * The connection is created in stopped mode. No messages will be delivered until the 
	 * Connection.start method is explicitly called.
	 * @param user the caller's user name
	 * @param password the caller's password
	 * @return XAConnection a newly created XAConnection
	 * @throws JMSException  if the JMS provider fails to create an XA connection due to some 
	 * internal error.
	 * @throws JMSSecurityException if client authentication fails due to an invalid user 
	 * name or password.
	 */
	public javax.jms.XAConnection createXAConnection(String user, String password) throws JMSException {
		return (javax.jms.XAConnection) createConnection(user, password, ConnectionType.XA_GENERIC_CONNECTION);
	}
}
