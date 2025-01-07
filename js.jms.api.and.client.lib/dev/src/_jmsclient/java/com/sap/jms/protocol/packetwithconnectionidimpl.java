/**
 * PacketWithConnectionIDImpl.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol;

import java.io.PrintWriter;

import javax.jms.JMSException;
import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketImpl;

/**
 * @author  Dr. Bernd Follmeg
 * @version 6.30
 */
public class PacketWithConnectionIDImpl extends PacketImpl implements Packet, PacketWithConnectionID {

	/* Position of the client id relative to the start of the payload area */
	protected static final int POS_CONNECTION_ID = 0;	
    protected static final int SIZE = POS_CONNECTION_ID + SIZEOF_LONG;
	
	/**
	 * Constructor for PacketWithClientIDImpl.
	 */
	protected PacketWithConnectionIDImpl() {}
	
	/**
	 * Constructor for PacketWithClientIDImpl.
	 * @param packet_type
	 * @param packet_size
	 */
	protected PacketWithConnectionIDImpl(byte packet_type, int packet_size, long client_id)  throws JMSException
	{
		super(packet_type, packet_size);
		setLong(POS_CONNECTION_ID, client_id);
	}

	/**
	 * Constructor for PacketWithClientIDImpl.
	 * @param buffer
	 * @param offset
	 * @param length
	 */
	protected PacketWithConnectionIDImpl(byte[] buffer, int offset, int length) 
	{
		super(buffer, offset, length);
	}

	/**
	 * Returns the connection ID
	 * @return the connection id
	 * @exception BufferUnderflowException thrown on buffer underflow
	 * @see com.sap.jms.protocol.PacketWithConnectionID#getClientID()
	 */
	public long getConnectionID() throws BufferUnderflowException
	{
		return getLong(POS_CONNECTION_ID);
	}

    /**
     * Sets the connection ID
     * @param connectionID
     * @throws BufferOverflowException
     */
    public void setConnectionID(long connectionID) throws BufferOverflowException
    {
        setLong(POS_CONNECTION_ID, connectionID);
    }

	/**
	 *  Returns a string representation of the packet
	 *  @param out to writer to use to print the packet
	 */
	protected void toString(PrintWriter out) throws Exception
	{
		super.toString(out);
		out.println("------------------------------ Message Content ---------------------------------");
		out.printf("%30s %d\n", "ConnectionID:", getConnectionID());
	}
}
