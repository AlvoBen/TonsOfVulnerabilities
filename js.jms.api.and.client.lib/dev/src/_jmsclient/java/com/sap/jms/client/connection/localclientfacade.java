package com.sap.jms.client.connection;

import javax.jms.JMSException;

import com.sap.jms.protocol.Packet;
import com.sap.jms.util.Logging;
import com.sap.tc.logging.Severity;

public class LocalClientFacade implements ClientFacade {
	
	ClientFacade clientFacade = null;
	
	public void setClientFacade(ClientFacade clientFacade) {
		this.clientFacade = clientFacade;
	}
	
	public void onPacketReceived(Packet packet)  throws JMSException {
		if (clientFacade != null) {
			clientFacade.onPacketReceived(packet);
		} else {
			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "onPacketReceived() has been invoked on LocalClientFacade without Connection");
			}
		}
	}
	
	public void closedConnection() {
		if (clientFacade != null) {
			clientFacade.closedConnection();
		} else {
			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "closedConnection() has been invoked on LocalClientFacade without Connection");
			}
		}
	}
}
