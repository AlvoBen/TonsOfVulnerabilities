package com.sap.jms.client.rmi;

import javax.jms.JMSException;

import com.sap.jms.client.connection.ClientFacade;

import com.sap.jms.client.Util;
import com.sap.jms.protocol.Packet;
import com.sap.jms.util.Logging;
import com.sap.tc.logging.Severity;

/**
 * @author Desislav Bantchovski
 * @version 7.30 
 */

public class RMIClientFacadeImpl implements RMIClientFacade {
	
	private ClientFacade clientFacade = null;
	
	void setClientFacade(ClientFacade clientFacade) {
		this.clientFacade = clientFacade;
	}		

	public void onPacketReceived(Packet packet) throws JMSException {
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
