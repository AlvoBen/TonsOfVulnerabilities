package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketImpl;

public class SessionAfterBeginResponse extends PacketImpl {

	public static final byte TYPE_ID = SESSION_AFTER_BEGIN_RESPONSE;

	static final int SIZE = 0;

	public SessionAfterBeginResponse() throws JMSException {
		super(TYPE_ID, SIZE);
	}
}