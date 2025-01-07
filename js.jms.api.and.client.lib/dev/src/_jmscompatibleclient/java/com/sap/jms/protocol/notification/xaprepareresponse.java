
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketImpl;

public class XAPrepareResponse extends PacketImpl {

	public static final byte TYPE_ID = XA_PREPARE_RESPONSE;

	private static final int POS_PREPARE_RESULT = 0;	
	private static final int SIZE = POS_PREPARE_RESULT + SIZEOF_INT;

	public XAPrepareResponse(int result) throws JMSException {
		super(TYPE_ID, SIZE);
		setInt(POS_PREPARE_RESULT, result);
	}

	public XAPrepareResponse() {
		// nothing here
	}

	public int getResult() throws JMSException {
		return getInt(POS_PREPARE_RESULT);
	}

}
