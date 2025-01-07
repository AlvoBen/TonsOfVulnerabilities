/**
 * PacketWithSessionIDImpl.java
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
 * @version 1.0
 */
public class PacketWithSessionIDImpl extends PacketImpl implements Packet, PacketWithSessionID {

	/* Position of the session id relative to payload start */
	protected static final int POS_SESSION_ID = 0;
	protected static final int SIZE = POS_SESSION_ID + SIZEOF_INT;

	/**
	 * Constructor for PacketWithSessionIDImpl.
	 * @param packet_type the packet type
	 * @param packet_size  the size of the packet
	 * @param session_id the session ID
	 */
	protected PacketWithSessionIDImpl() 
	{
	}
	
	/**
	 * Constructor for PacketWithSessionIDImpl.
	 * @param packet_type the packet type
	 * @param packet_size  the size of the packet
	 * @param session_id the session ID
	 */
	protected PacketWithSessionIDImpl(byte packet_type, int packet_size, int session_id) throws JMSException 
	{
		super(packet_type, packet_size);
		setInt(POS_SESSION_ID, session_id);
	}

	/**
	 * Returns the session ID
	 * @return the session ID
	 * @exception
	 * @see com.sap.jms.protocol.PacketWithSessionID#getSessionID()
	 */
	public int getSessionID() throws BufferUnderflowException
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
		out.println("------------------------------ Message Content ---------------------------------");
		out.printf("%30s %d\n", "SessionID:", getSessionID());
	}
}
