package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketImpl;
import com.sap.jms.protocol.PacketTypes;


/**
 * @author Margarit Kirov
 * @version 1.0
 */
public class XARecoverRequest extends PacketImpl {
  
  public static final byte TYPE_ID = XA_RECOVER_REQUEST;
  
  private static final int POS_FLAGS = 0;
  private static final int SIZE = POS_FLAGS + SIZEOF_INT;

  public XARecoverRequest(int flags) throws JMSException {
	    super(TYPE_ID, SIZE);
	    setInt(POS_FLAGS, flags);
	  }
  
  public XARecoverRequest() {
	  // nothing here
  }
  
  public int getFlags() throws BufferUnderflowException {
    return getInt(POS_FLAGS);
  }
  
	public int getExpectedResponsePacketType() {
		return PacketTypes.XA_RECOVER_RESPONSE;
	}
}
