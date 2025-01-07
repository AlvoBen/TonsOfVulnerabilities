/**
 * XAConnectionFactory.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.rmi;

import javax.jms.JMSException;
import javax.jms.XAConnectionFactory;

import com.sap.jms.client.connection.Connection.ConnectionType;


public class RMIXAConnectionFactory extends RMIConnectionFactory implements XAConnectionFactory {


	private static final long serialVersionUID = 3467119866348365139L;

	public RMIXAConnectionFactory(RMIConnectionFactoryInterface connectionFactoryInterface, String vpName, String clientId, 
			boolean supportsOptimization, String connectionFactoryName, String redirectableKey) {
		super(connectionFactoryInterface, vpName, clientId, supportsOptimization, connectionFactoryName, redirectableKey);
	}

	public javax.jms.XAConnection createXAConnection() throws JMSException {
		return (javax.jms.XAConnection) createConnection(ConnectionType.XA_GENERIC_CONNECTION);
	}

	public javax.jms.XAConnection createXAConnection(String user, String password) throws JMSException {
		return (javax.jms.XAConnection) createConnection(user, password, ConnectionType.XA_GENERIC_CONNECTION);
	}
}
