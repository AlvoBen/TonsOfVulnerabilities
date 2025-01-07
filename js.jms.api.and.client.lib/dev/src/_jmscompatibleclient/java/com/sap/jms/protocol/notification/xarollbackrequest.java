package com.sap.jms.protocol.notification;

import javax.jms.JMSException;
import javax.transaction.xa.Xid;

import com.sap.jms.protocol.PacketTypes;

public class XARollbackRequest extends XABasePacket {

	public static final byte TYPE_ID = XA_ROLLBACK_REQUEST;

	public XARollbackRequest(Xid xid) throws JMSException {
		super(TYPE_ID, xid, 0);
	}
	
	public XARollbackRequest() {
		// nothing here
	}
	
	public int getExpectedResponsePacketType() {
		return PacketTypes.XA_RESPONSE;
	}
}
