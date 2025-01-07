/**
 * PacketWithSessionIDAndDestinationIDImpl.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol;

import com.sap.jms.util.compat.PrintWriter;

import javax.jms.JMSException;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class PacketWithSessionIDAndDestinationIDImpl extends PacketWithSessionIDImpl
    implements PacketWithSessionID, PacketWithDestinationID {

    protected static final int POS_DESTINATION_ID = POS_SESSION_ID + SIZEOF_INT;
    protected static final int SIZE = POS_DESTINATION_ID + SIZEOF_INT;

    protected PacketWithSessionIDAndDestinationIDImpl() {
        super();
    } 

    /**
     * Constructor for PacketWithSessionIDAndDestinationIDImpl
     */
    protected PacketWithSessionIDAndDestinationIDImpl(byte type_id, int size, int session_id) throws JMSException {
        super(type_id, size, session_id);
    } 

    /**
     *  Returns the destination ID
     *  @return the destination ID
     */
    public int getDestinationID() throws BufferUnderflowException {
        return getInt(POS_DESTINATION_ID);
    } 

    /**
     *  Sets the destination ID
     *  @@param destion_id the destination ID
     */
    public void setDestinationID(int destination_id) throws BufferOverflowException {
        setInt(POS_DESTINATION_ID, destination_id);
    } 

    /**
     *  Returns a string representation of the packet
     *  @param out to writer to use to print the packet
     */
    protected void toString(PrintWriter out) throws Exception {
        super.toString(out);
        out.printf("%30s %d\n", "DestinationID:", new Integer(getDestinationID()));
    }
}
