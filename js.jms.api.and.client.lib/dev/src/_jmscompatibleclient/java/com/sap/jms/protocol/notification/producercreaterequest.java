/**
 * ProducerCreateRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import com.sap.jms.util.compat.PrintWriter;

import javax.jms.JMSException;

import com.sap.jms.protocol.BufferOverflowException;
import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketTypes;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class ProducerCreateRequest extends ProducerBasePacket {

    public static final byte TYPE_ID = PRODUCER_CREATE_REQUEST;

    static final int POS_TYPE = POS_PRODUCER_ID + SIZEOF_LONG;
    static final int POS_DESTINATION_NAME = POS_TYPE + SIZEOF_BYTE;
    static final int SIZE = POS_DESTINATION_NAME;

    public ProducerCreateRequest() {}

    /**
     * Constructor for ProducerCreateRequest
     * @param session_id the ID of the session to which the producer is associated with
     * @param destination name the name if the destination
     * @param destination_type the type of the destination; 0 Queue, 1 Topic
     */
    public ProducerCreateRequest(int session_id, String destination_name, byte destination_type) throws JMSException {
        super(TYPE_ID, SIZE + strlenUTF8(destination_name), session_id);
        setByte(POS_TYPE, destination_type);
        setUTF8(POS_DESTINATION_NAME, destination_name);
    }

    /**
     *  Returns the type of the producer
     *  @return the type of the producer
     */
    public final byte getType() throws BufferUnderflowException {
        return getByte(POS_TYPE);
    }

    /**
     *  Returns the destination name
     *  @return the destination name
     */
    public final String getDestinationName() throws BufferUnderflowException {
        return getString(POS_DESTINATION_NAME);
    }

    /**
     *  Sets the producer ID
     *  @@param producer_id the producer ID
     */
    public void setProducerID(long producer_id) throws BufferOverflowException {
        setLong(POS_PRODUCER_ID, producer_id);
    }

    /**
      *  Returns a string representation of the packet
      *  @param out to writer to use to print the packet
      */
    protected void toString(PrintWriter out) throws Exception {
        out.println("------------------------------ Producer Content --------------------------------");
        super.toString(out);
        out.printf("%30s %s\n%30s %d\n", 
        		"DestinationName:", getDestinationName(),
        		"Type:", new Byte(getType()));
    }

	public int getExpectedResponsePacketType() {
		return PacketTypes.PRODUCER_CREATE_RESPONSE;
	}
}
