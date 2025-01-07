/**
 * PacketWithConnectionIDAndSessionIDImpl.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol;

import java.io.PrintWriter;

import javax.jms.JMSException;


/**
 * @author  Dr. Bernd Follmeg
 * @version 6.30
 */
public class PacketWithConnectionIDAndSessionIDImpl extends PacketWithConnectionIDImpl
	implements Packet, PacketWithConnectionID, PacketWithSessionID {

	/** Position of the session ID parameter */
	protected static final int POS_SESSION_ID = POS_CONNECTION_ID + SIZEOF_LONG;	
	protected static final int SIZE = POS_SESSION_ID + SIZEOF_INT;
	
	protected PacketWithConnectionIDAndSessionIDImpl() 
	{
		super();
	}

	/**
	 * Constructor for PacketWithConnectionIDAndSessionIDImpl.
	 * @param packet_type
	 * @param packet_size
	 * @param client_id
	 * @param session_id
	 */
	protected PacketWithConnectionIDAndSessionIDImpl(byte packet_type, int packet_size, long client_id, int session_id) throws JMSException
	{
		super(packet_type, packet_size, client_id);
		setInt(POS_SESSION_ID, session_id);
	}

	/**
	 * @see com.sap.jms.protocol.PacketWithConnectionID#getClientID()
	 */
	public long getConnectionID() throws BufferUnderflowException
	{
		return getLong(POS_CONNECTION_ID);
	}

	/**
	 * @see com.sap.jms.protocol.PacketWithSessionID#getSessionID()
	 */
	public int getSessionID()  throws BufferUnderflowException
	{
		return getInt(POS_SESSION_ID);
	}

	/**
	 *  Sets the session ID
	 *  @param session_id the session ID
	 */
	public void setSessionID(int session_id) throws BufferOverflowException 
	{
		setInt(POS_SESSION_ID, session_id);	
	}
	
	/**
	 *  Returns a string representation of the packet
	 *  @param out to writer to use to print the packet
	 */
	protected void toString(PrintWriter out) throws Exception
	{
		super.toString(out);
	
		//----------------------------------------------------------------
		// Print message content
		//----------------------------------------------------------------
		out.printf("%30s %d\n", 
				"SessionID:", getSessionID());
	}
}
