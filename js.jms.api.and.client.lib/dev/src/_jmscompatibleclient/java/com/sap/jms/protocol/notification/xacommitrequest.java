/**
 * XACommitRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;
import javax.transaction.xa.Xid;

import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketTypes;

/**
 * @author Margarit Kirov
 * @version 1.0
 */
public class XACommitRequest extends XABasePacket {
  
  public static final byte TYPE_ID = XA_COMMIT_REQUEST;
  public static final int POS_XID = 0;
  public static final int SIZE = POS_XID;
  
  public XACommitRequest(Xid xid, boolean onePhase) throws JMSException {
    super(TYPE_ID, xid, (onePhase ? 1 : 0));
  }
	
  public XACommitRequest() {
	  // nothing here
  }

  public boolean isOnePhase() throws BufferUnderflowException {
    return (getFlags() == 1) ? true : false;
  }

	public int getExpectedResponsePacketType() {
		return PacketTypes.XA_RESPONSE;
	}
}
