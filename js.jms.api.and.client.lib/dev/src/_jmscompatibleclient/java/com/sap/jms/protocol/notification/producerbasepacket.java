/**
 * ProducerCreateResponse.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import com.sap.jms.util.compat.PrintWriter;

import javax.jms.JMSException;

import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketWithProducerID;
import com.sap.jms.protocol.PacketWithSessionIDAndDestinationIDImpl;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class ProducerBasePacket extends PacketWithSessionIDAndDestinationIDImpl implements PacketWithProducerID {

    protected static final int POS_PRODUCER_ID = POS_DESTINATION_ID + SIZEOF_INT;
    protected static final int SIZE = POS_PRODUCER_ID + SIZEOF_LONG;

    protected ProducerBasePacket() {}

    /**
     * Constructor for ProducerCreateResponse.
     */
	protected ProducerBasePacket(byte type_id, int size, int session_id) throws JMSException {
		this(type_id, size, session_id, 0L);
	}

    /**
     * Constructor for ProducerCreateResponse.
     */
    protected ProducerBasePacket(byte type_id, int session_id, long producer_id) throws JMSException {
        this(type_id, SIZE, session_id, producer_id);
    }

    /**
     * Constructor for ProducerCreateResponse
     * @param session_id the ID of the session to which the producer is associated with
     * @param producer_id the ID of the newly created consumer
     * @exception JMSException thrown if something went wrong
     */
    protected ProducerBasePacket(byte type_id, int size, int session_id, long producer_id) throws JMSException {
        super(type_id, size, session_id);
        setLong(POS_PRODUCER_ID, producer_id);        
    }

    /**
     *  Returns the producer id
     *  @return the producer id
     */
    public final long getProducerID() throws BufferUnderflowException {
        return getLong(POS_PRODUCER_ID);
    }

    /**
     *  Returns a string representation of the packet
     *  @param out to writer to use to print the packet
     */
    protected void toString(PrintWriter out) throws Exception {
        super.toString(out);
        out.printf("%30s %d\n", "ProducerID:", new Long(getProducerID()));
    }
}
