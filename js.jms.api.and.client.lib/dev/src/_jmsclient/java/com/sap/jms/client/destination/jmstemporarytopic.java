﻿/**
 * TemporaryTopic.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.destination;

import com.sap.jms.client.connection.Connection;


public class JMSTemporaryTopic extends JMSTopic implements javax.jms.TemporaryTopic {

	private static final long serialVersionUID = -3836704064457870792L;
	private transient Connection connection;

	public JMSTemporaryTopic(String name, int destinationId, String vpName, Connection connection) {
		super(name, destinationId, vpName, true);
		this.connection = connection;
	}
	
	public JMSTemporaryTopic(String name, int destinationId, String vpName) {
		super(name, destinationId, vpName, true);
	}

	public void delete() throws javax.jms.JMSException {
		if (connection == null) {
			throw new javax.jms.JMSException("Operation not allowed.");
		}
		if (connection.isClosed()) {
			throw new javax.jms.JMSException("Connection is closed");
		}
		connection.deleteTemporaryDestination(getDestinationId());
	}

}