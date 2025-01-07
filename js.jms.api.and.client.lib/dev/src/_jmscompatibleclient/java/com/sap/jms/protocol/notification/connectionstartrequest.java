/**
 * ConnectionStartRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.PacketWithConnectionIDImpl;

/**
 * @author Dr. Bernd Follmeg
 * @version 1.0
 */
public class ConnectionStartRequest extends PacketWithConnectionIDImpl {

	/** The ID for this packet */
	public static final byte TYPE_ID = CONNECTION_START_REQUEST;

	public ConnectionStartRequest() {}

	/**
	 * Constructor for ConnectionCloseRequest.
	 * @param client_id the client ID
	 */
	public ConnectionStartRequest(long client_id) throws JMSException {
		super(TYPE_ID, SIZE, client_id);
	}

	public int getExpectedResponsePacketType() {
		return PacketTypes.CONNECTION_START_RESPONSE;
	}
}
