package com.sap.jms.protocol.notification;

import javax.jms.JMSException;
import com.sap.jms.protocol.PacketImpl;

public class XATimeoutResponse extends PacketImpl {

	public static final byte TYPE_ID = XA_TIMEOUT_RESPONSE;

	private static final int POS_TIMEOUT = 0;
	private static final int SIZE = POS_TIMEOUT + SIZEOF_LONG;

	public XATimeoutResponse(long timeout) throws JMSException {
		super(TYPE_ID, SIZE);
		setLong(POS_TIMEOUT, timeout);
	}

	public XATimeoutResponse(){
		// nothing here
	}

	/*
	 * @return negative on error; 
	 * >0 the current timeout value in milis (also after successfully setting the timeout) 
	 */
	public long getTimeout() throws JMSException {
		return getLong(POS_TIMEOUT);
	}

}
