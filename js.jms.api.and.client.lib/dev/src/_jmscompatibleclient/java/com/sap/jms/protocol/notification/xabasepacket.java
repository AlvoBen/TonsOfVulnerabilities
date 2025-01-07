/**
 * XABasePacket.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;
import javax.transaction.xa.Xid;

import com.sap.jms.client.xa.JMSXid;
import com.sap.jms.protocol.BufferOverflowException;
import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketImpl;
import com.sap.jms.protocol.PacketWithXID;

/**
 * @author  Margarit Kirov
 * @version 1.0
 */
class XABasePacket extends PacketImpl implements PacketWithXID {

    
    protected static final int POS_FLAGS = 0;
    protected static final int POS_XID = POS_FLAGS + SIZEOF_INT;
    
    protected XABasePacket() {}

    /**
     * Constructor for XABasePacket
     */
    protected XABasePacket(byte typeID, Xid xid, int flags) throws JMSException {
      super(typeID, SIZEOF_INT + xid.getBranchQualifier().length + xid.getGlobalTransactionId().length + 3 * SIZEOF_INT);
        setInt(POS_FLAGS, flags);
        setXID(POS_XID, xid);
    }
    
    /**
     * Method setXID. Sets the Xid object into the buffer but adding consequently the 
     * global transaction id, the branch qualifier and the format id.
     * @param pos the position into the buffer where to put the data
     * @param xid the Xid object to be written
     * @return int the position a
     * @throws BufferOverflowException
     */
    protected void setXID(int pos, Xid xid) throws BufferOverflowException {
      setByteArray(pos, xid.getGlobalTransactionId());
      pos += (SIZEOF_INT + xid.getGlobalTransactionId().length);
      setByteArray(pos, xid.getBranchQualifier());
      pos += (SIZEOF_INT + xid.getBranchQualifier().length);
      setInt(pos, xid.getFormatId());
    }

    /**
     * Method getXID. Gets the Xid from the buffer
     * @return an Xid object
     * @throws BufferOverflowException
     */
    public Xid getXID() throws BufferUnderflowException {
      return getXID(POS_XID);
    }
    
    /**
     * Method getFlags. Gets the flags of a request from the buffer
     * @return the flags of the request as int
     * @throws BufferOverflowException
     */
    public int getFlags() throws BufferUnderflowException { 
      return getFlags(POS_FLAGS);
    }
    
    /**
     * Method getFlags. Gets the flags of a request from the buffer from the given position.
     * @return the flags of the request as int
     * @throws BufferOverflowException
     */
    protected int getFlags(int pos) throws BufferUnderflowException { 
      return getInt(pos);
    }
    
    /**
     * Method getXID. Gets the Xid from the buffer from the given position.
     */
    protected Xid getXID(int pos) throws BufferUnderflowException {
      final byte[] globalTransactionId = getByteArray(pos);
      pos += (SIZEOF_INT + globalTransactionId.length);
      final byte[] branchQualifier = getByteArray(pos);
      pos += (SIZEOF_INT + branchQualifier.length);
      final int formatId = getInt(pos);
      
      JMSXid xid = new JMSXid(formatId, branchQualifier, globalTransactionId);
      
      return xid;
    }
}
