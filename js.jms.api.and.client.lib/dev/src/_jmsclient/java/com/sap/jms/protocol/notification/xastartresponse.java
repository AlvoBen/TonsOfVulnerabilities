package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketImpl;

public class XAStartResponse extends PacketImpl {


	public static final byte TYPE_ID = XA_START_RESPONSE;

	private static final int POS_START_RESULT = 0;	
	private static final int SIZE = POS_START_RESULT + SIZEOF_LONG;

	public XAStartResponse(long txid) throws JMSException {
		super(TYPE_ID, SIZE);
		setLong(POS_START_RESULT, txid);
	}

	public XAStartResponse() {
		// nothing here
	}

	public long getTxid() throws JMSException {
		return getLong(POS_START_RESULT);
	}

}
