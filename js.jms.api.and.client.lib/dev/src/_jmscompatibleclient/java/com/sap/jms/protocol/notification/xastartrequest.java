/**
 * XAStartRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;
import javax.transaction.xa.Xid;

import com.sap.jms.protocol.BufferOverflowException;
import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.PacketWithSessionID;

public class XAStartRequest extends XABasePacket implements PacketWithSessionID {

	public static final byte TYPE_ID = XA_START_REQUEST;

	public XAStartRequest(Xid xid, int flags, int sessionID, long activeTimeout) throws JMSException {
		super(TYPE_ID, xid, flags);
		
		int pos = POS_XID;
	    pos += (SIZEOF_INT + xid.getGlobalTransactionId().length);
	    pos += (SIZEOF_INT + xid.getBranchQualifier().length);
	    // format
	    pos+= (SIZEOF_INT);

	    allocate(TYPE_ID, pos + SIZEOF_INT + SIZEOF_LONG);

		setPosition(pos);
		writeInt(sessionID);
		writeLong(activeTimeout);
		
	}
	
	public XAStartRequest() {
		// nothing here
	}

	public int getSessionID() throws BufferUnderflowException, BufferOverflowException{
		Xid xid = getXID();

		int pos = POS_XID;
		pos += (SIZEOF_INT + xid.getGlobalTransactionId().length);
		pos += (SIZEOF_INT + xid.getBranchQualifier().length);
		// format
		pos+= (SIZEOF_INT);
		setPosition(pos);
		
		int sessionId = readInt();
		
		return sessionId;
	}

	public long getActiveTimeout() throws BufferUnderflowException, BufferOverflowException {
		Xid xid = getXID();

		int pos = POS_XID;
		pos += (SIZEOF_INT + xid.getGlobalTransactionId().length);
		pos += (SIZEOF_INT + xid.getBranchQualifier().length);
		// format
		pos+= (SIZEOF_INT);
		// sessionId
		pos += SIZEOF_INT;
		setPosition(pos);

		long activeTimeout = readLong(); 
		return activeTimeout;
	}

	public int getExpectedResponsePacketType() {
		return PacketTypes.XA_RESPONSE;
	}

	public void setSessionID(int session_id) throws BufferOverflowException, BufferUnderflowException {
		throw new BufferOverflowException("Not allowed. Use the constructor to set the sessionId.");
	}
}
