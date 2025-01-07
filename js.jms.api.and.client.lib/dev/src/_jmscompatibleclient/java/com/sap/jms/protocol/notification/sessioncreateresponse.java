/**
 * SessionCreateResponse.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2003.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import com.sap.jms.util.compat.PrintWriter;

import javax.jms.JMSException;

import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketWithConnectionIDAndSessionIDImpl;

/**
 * @author  Dr. Bernd Follmeg
 * @version 6.30
 */
public class SessionCreateResponse extends PacketWithConnectionIDAndSessionIDImpl {

    public static final byte TYPE_ID = SESSION_CREATE_RESPONSE;

    static final int POS_MESSAGE_ID_BASE = POS_SESSION_ID + SIZEOF_INT;
    static final int SIZE = POS_MESSAGE_ID_BASE + SIZEOF_LONG;

    public SessionCreateResponse() {
        super();
    }

    /**
     * Constructor for SessionCreateResponse
     * @param client_id the ID of the client the session is associated with
     * @param session_id the ID of the newly created session
     * @param txid the transaction ID if the session is transaction; <code>null</code> otherwise
     * @param message_id_base the base of a unique message id
     * @exception JMSException if something went wrong
     */
    public SessionCreateResponse(long client_id, int session_id, long message_id_base) throws JMSException {
        super(TYPE_ID, SIZE, client_id, session_id);
		setLong(POS_MESSAGE_ID_BASE, message_id_base);  
    }

    /**
     *  Returns the base of a unique message id
     *  @return the base of a unique message id
     */
    public final long getMessageIDBase() throws BufferUnderflowException {
        return getLong(POS_MESSAGE_ID_BASE);
    }

    /**
     *  Returns a string representation of the packet
     *  @param out to writer to use to print the packet
     */
    protected void toString(PrintWriter out) throws Exception {
        super.toString(out);
        out.println("------------------------------ Session Content ----------------------------------");
        out.printf("%30s %d\n", "MessageIDBase:", new Long(getMessageIDBase()));
    } 
}
