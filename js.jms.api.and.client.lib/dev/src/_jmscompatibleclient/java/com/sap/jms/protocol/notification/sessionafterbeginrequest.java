package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.PacketWithSessionIDImpl;

public class SessionAfterBeginRequest extends PacketWithSessionIDImpl {

	public static final byte TYPE_ID = SESSION_AFTER_BEGIN_REQUEST;

	static final int SIZE = POS_SESSION_ID + SIZEOF_INT;

	public SessionAfterBeginRequest() {
	}

	public SessionAfterBeginRequest(int session_id) throws JMSException {
		super(TYPE_ID, SIZE, session_id);
	}

	public int getExpectedResponsePacketType() {
		return PacketTypes.SESSION_AFTER_BEGIN_RESPONSE;
	}

	
}
