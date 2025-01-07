/**
 * SessionStartRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.PacketWithConnectionIDAndSessionIDImpl;

/**
 * @author Dr. Bernd Follmeg
 * @version 1.0
 */
public class SessionStartRequest extends PacketWithConnectionIDAndSessionIDImpl {

	public static final byte TYPE_ID = SESSION_START_REQUEST;

	static final int SIZE = POS_SESSION_ID + SIZEOF_INT;

	public SessionStartRequest() {
	}

	/**
	 * Constructor for SessionStartRequest.
	 * @param client_id the client ID of the client to which this sessions belongs to
	 * @param session_id the ID of the session for which the message delivery should be started
	 * @exception JMSException thrown if something went wrong
	 */
	public SessionStartRequest(long client_id, int session_id)
			throws JMSException {
		super(TYPE_ID, SIZE, client_id, session_id);
	}

	public int getExpectedResponsePacketType() {
		return PacketTypes.SESSION_START_RESPONSE;
	}
}
