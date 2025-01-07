package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketImpl;
import com.sap.jms.protocol.PacketTypes;

public class XATimeoutRequest extends PacketImpl {

	public static final byte TYPE_ID = XA_TIMEOUT_REQUEST;

	private static final int POS_SESSION_ID = 0;
	private static final int SIZE = POS_SESSION_ID + SIZEOF_INT;

	public XATimeoutRequest(int sessionId) throws JMSException {
		super(TYPE_ID, SIZE);
		setInt(POS_SESSION_ID, sessionId);
	}

	public XATimeoutRequest() {
		// nothing here
	}

	public int getSessionId() throws BufferUnderflowException {
		return getInt(POS_SESSION_ID);
	}

	public int getExpectedResponsePacketType() {
		return PacketTypes.XA_TIMEOUT_RESPONSE;
	}
}
