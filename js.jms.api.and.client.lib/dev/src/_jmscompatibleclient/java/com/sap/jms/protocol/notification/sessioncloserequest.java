/**
 * SessionCreateResponse.java
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
public class SessionCloseRequest extends PacketWithConnectionIDAndSessionIDImpl {

	public static final byte TYPE_ID = SESSION_CLOSE_REQUEST;

	static final int SIZE = POS_SESSION_ID + SIZEOF_INT;

	public SessionCloseRequest() {
	}

	/**
	 * Constructor for SessionCloseRequest.
	 * @param client_id the client ID of the client to which this sessions belongs to
	 * @param session_id the ID of the session which should be closed
	 * @exception JMSException thrown if something went wrong
	 */
	public SessionCloseRequest(long client_id, int session_id) throws JMSException {
		super(TYPE_ID, SIZE, client_id, session_id);
	}

	public int getExpectedResponsePacketType() {
		return PacketTypes.SESSION_CLOSE_RESPONSE;
	}
}
