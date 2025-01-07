package com.sap.jms.protocol.notification;

import javax.jms.JMSException;
import javax.transaction.xa.Xid;
import com.sap.jms.protocol.PacketTypes;

public class XAForgetRequest extends XABasePacket {

	public static final byte TYPE_ID = XA_FORGET_REQUEST;

	public XAForgetRequest(Xid xid) throws JMSException {
		super(TYPE_ID, xid, 0);
	}

	public XAForgetRequest() {
		// nothing here
	}

	public int getExpectedResponsePacketType() {
		return PacketTypes.XA_RESPONSE;
	}
}
