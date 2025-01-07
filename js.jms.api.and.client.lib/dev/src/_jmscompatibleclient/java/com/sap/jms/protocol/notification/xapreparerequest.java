package com.sap.jms.protocol.notification;

import javax.jms.JMSException;
import javax.transaction.xa.Xid;
import com.sap.jms.protocol.PacketTypes;

public class XAPrepareRequest extends XABasePacket {
  
  public static final byte TYPE_ID = XA_PREPARE_REQUEST;
  
  public XAPrepareRequest(Xid xid) throws JMSException {
    super(TYPE_ID, xid, 0);
  }

  public XAPrepareRequest() {
	  // nothing here
  }
  
	public int getExpectedResponsePacketType() {
		return PacketTypes.XA_PREPARE_RESPONSE;
	}
}
