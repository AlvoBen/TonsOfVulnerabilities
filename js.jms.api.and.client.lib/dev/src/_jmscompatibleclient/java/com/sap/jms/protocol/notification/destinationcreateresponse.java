/**
 * DestinationCreateResponse.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import com.sap.jms.util.compat.PrintWriter;

import javax.jms.JMSException;

import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketWithConnectionIDImpl;
import com.sap.jms.protocol.PacketWithDestinationID;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class DestinationCreateResponse extends PacketWithConnectionIDImpl implements PacketWithDestinationID {

    public static final byte TYPE_ID = DESTINATION_CREATE_RESPONSE;

    protected static final int POS_DESTINATION_ID = POS_CONNECTION_ID + SIZEOF_LONG;
    protected static final int POS_DESTINATION_NAME = POS_DESTINATION_ID + SIZEOF_INT;
    protected static final int SIZE = POS_DESTINATION_NAME;

    public DestinationCreateResponse() {}

    /**
     * Constructor for DestinationCreateResponse
     * @param client_id the ID of the client who issued the request
     * @param destination_id the ID of the newly created destination
     * @exception JMSException thrown if something went wrong
     */
    public DestinationCreateResponse(long client_id, int destination_id, String destination_name) throws JMSException {
        super(TYPE_ID, SIZE + strlenUTF8(destination_name), client_id);
        setInt(POS_DESTINATION_ID, destination_id);
        setUTF8(POS_DESTINATION_NAME, destination_name);
    }

    /**
     *  Returns the destination ID
     *  @return the destination ID
     */
    public int getDestinationID() throws BufferUnderflowException {
        return getInt(POS_DESTINATION_ID);
    }

    /**
     *  Returns the destination name
     *  @return the destination name
     */
    public final String getDestinationName() throws BufferUnderflowException {
        return getString(POS_DESTINATION_NAME);
    }

    /**
     *  Returns a string representation of the packet
     *  @param out to writer to use to print the packet
     */
    protected void toString(PrintWriter out) throws Exception {
        super.toString(out);
        out.println("------------------------------ Destination Content -----------------------------");
        out.printf("%30s %d\n%30s %s", 
        		"DestinationID:", new Integer(getDestinationID()),
        		"DestinationName:", getDestinationName());
    } 
}
