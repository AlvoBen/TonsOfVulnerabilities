package com.sap.jms.client.connection;

import javax.jms.JMSException;
import com.sap.jms.client.connection.Connection.ConnectionType;

public interface ConnectionFactoryInterface {
	public ConnectionData connectionCreate(String username, String password, ConnectionType type, String vpName, byte[] certificate, String connectionFactoryName, ClientFacade clientFacade, boolean local) throws JMSException;
}
