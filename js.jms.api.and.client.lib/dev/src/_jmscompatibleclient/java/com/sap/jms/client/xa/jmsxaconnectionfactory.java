/**
 * XAConnectionFactory.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.xa;

import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;

import com.sap.jms.client.connection.Connection;
import com.sap.jms.client.connection.ConnectionFactory;
import com.sap.jms.client.connection.Connection.ConnectionType;


public class JMSXAConnectionFactory extends ConnectionFactory implements XAConnectionFactory {


	private static final long serialVersionUID = 3467119866348365139L;

	public JMSXAConnectionFactory(String[] hosts, int[] ports, String serverInstance, String userName, String password, int initialPoolSize, int maxPoolSize, String systemID, String hardwareID) {
		super(hosts, ports, serverInstance, userName, password, initialPoolSize, maxPoolSize, systemID, hardwareID);
	}

	public XAConnection createXAConnection() throws javax.jms.JMSException {
		return (XAConnection)createConnection(ConnectionType.XA_GENERIC_CONNECTION);
	}

	public XAConnection createXAConnection(String userName, String password) throws javax.jms.JMSException {
		return null;

	}

}
