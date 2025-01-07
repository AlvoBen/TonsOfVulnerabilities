package com.sap.jms.protocol.notification;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.jms.JMSException;
import javax.transaction.xa.Xid;

import com.sap.jms.client.xa.JMSXid;
import com.sap.jms.protocol.BufferOverflowException;
import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketImpl;


public class XARecoverResponse extends PacketImpl {

	public static final byte TYPE_ID = XA_RECOVER_RESPONSE;

	public XARecoverResponse(Set/*<Xid>*/ xids) throws JMSException {
		super(TYPE_ID, getSize(xids));
		setPosition(0);
		writeInt(xids.size());

		for (Iterator i = xids.iterator(); i.hasNext(); ) {
			Xid xid = (Xid) i.next();
			writeInt(xid.getFormatId());
			writeByteArray(xid.getBranchQualifier());
			writeByteArray(xid.getGlobalTransactionId());
		}
	}

	public XARecoverResponse() throws JMSException {
		// nothing here 
	}

	public Set/*<Xid>*/ getXIDs() throws BufferUnderflowException, BufferOverflowException {

		setPosition(0);
		int count = readInt();

		Set/*<Xid>*/ xids = new HashSet/*<Xid>*/();
		
		for (int i = 0; i < count; i++) {
			int formatId = readInt();
			byte[] branchQualifier = readByteArray();
			byte[] globalTransactionId = readByteArray();
			xids.add(new JMSXid(formatId, branchQualifier, globalTransactionId));
		}

		return xids;
	}

	private static int getSize(Set/*<Xid>*/ xids) {
		int sum = SIZEOF_INT;

		for (Iterator i = xids.iterator(); i.hasNext(); ) {
			Xid xid = (Xid) i.next();
			sum += (xid.getBranchQualifier().length + xid.getGlobalTransactionId().length);
		}

		return sum + (3 * xids.size() * SIZEOF_INT);
	} 


}
