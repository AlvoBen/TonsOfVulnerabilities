package com.sap.jms.client.connection;

import javax.jms.JMSException;
import com.sap.jms.protocol.Packet;

public interface ClientFacade {
	
	public void onPacketReceived(Packet packet) throws JMSException;
	public void closedConnection();	

}
