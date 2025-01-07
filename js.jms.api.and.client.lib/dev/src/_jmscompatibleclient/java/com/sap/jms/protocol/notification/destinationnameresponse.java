package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketImpl;

/**
 * version 1.0
 */
public class DestinationNameResponse extends PacketImpl {
	public static final byte TYPE_ID = DESTINATION_NAME_RESPONSE;
	public static final int POS_NAME = 0;

	public DestinationNameResponse() throws JMSException {
		super();
	}

	public DestinationNameResponse(String name) throws JMSException {
		super(TYPE_ID, PacketImpl.strlenUTF8(name));
		setUTF8(POS_NAME, name);
	}

	public String getName() throws BufferUnderflowException {
		return getString(POS_NAME);
	}
}
