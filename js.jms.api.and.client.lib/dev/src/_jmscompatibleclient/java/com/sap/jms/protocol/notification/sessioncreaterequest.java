/**
 * SessionCreateRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2003.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import com.sap.jms.util.compat.PrintWriter;

import javax.jms.JMSException;

import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.PacketWithConnectionIDImpl;

/**
 * @author  Dr. Bernd Follmeg
 * @version 6.30
 */
public class SessionCreateRequest extends PacketWithConnectionIDImpl {

    public static final byte TYPE_ID = SESSION_CREATE_REQUEST;

    static final int POS_IS_TRANSACTED = POS_CONNECTION_ID + SIZEOF_LONG;
    static final int SIZE = POS_IS_TRANSACTED + SIZEOF_BYTE;

    public SessionCreateRequest() {
        super();
    }

    /**
     * Constructor for SessionCreateRequest.
     * @param client_id the client ID of the client to which this sessions will belong to.
     * @param session_mode: 0 -> TRANSACTED, 1 -> AUTO_ACKNOWLEDGE, 2 -> CLIENT_ACKNOWLEDGE, 3 -> DUPS_OK_ACKNOWLEDGE
     * @param is_xa whether the session is XA session or not
     * @exception JMSException if something went wrong
     */
    public SessionCreateRequest(long client_id, byte session_mode, boolean is_xa) throws JMSException {
        super(TYPE_ID, SIZE, client_id);
        byte flags = (byte)((session_mode & 0x0F) | (is_xa ? 0x10 : 0x00));
        setByte(POS_IS_TRANSACTED, flags);
    }

    /**
     * Returns the sesson mode, i.e. 0 -> TRANSACTED, 1 -> AUTO_ACKNOWLEDGE, 2 -> CLIENT_ACKNOWLEDGE, 3 -> DUPS_OK_ACKNOWLEDGE
     * @return the session mode
     */
    public byte getMode() throws BufferUnderflowException {
        return (byte)(getByte(POS_IS_TRANSACTED) & 0x0F);
    }

    /**
     * Checks whether this is a transacted session.
     * @return <code>true</code> if this is a transacted session; <code>false</code> otherwise.
     */
    public boolean isTransacted() throws BufferUnderflowException {
        return (getByte(POS_IS_TRANSACTED) & 0x0F) == 0;
    }

    /**
     * Checks whether this is a XA session
     * @return true if the session is a XA session, false otherwise
     */
    public boolean isXA() throws BufferUnderflowException {
        return (getByte(POS_IS_TRANSACTED) & 0x10) != 0;
    }

    /**
     *  Returns a string representation of the packet
     *  @param out to writer to use to print the packet
     */
    protected void toString(PrintWriter out) throws Exception {
        super.toString(out);
        out.println("------------------------------ Session Content ---------------------------------");
        out.printf("%30s %s\n", "IsTransacted:", isTransacted() ? "true" : "false");
        out.printf("%30s %s\n", "IsXA:", isXA() ? "true" : "false");
    } 

	public int getExpectedResponsePacketType() {
		return PacketTypes.SESSION_CREATE_RESPONSE;
	}
}
