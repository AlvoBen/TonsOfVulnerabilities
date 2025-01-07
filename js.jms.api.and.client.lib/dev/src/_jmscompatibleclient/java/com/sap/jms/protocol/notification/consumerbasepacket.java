/**
 * ConsumerBasePacket.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import com.sap.jms.util.compat.PrintWriter;

import javax.jms.JMSException;

import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketWithConsumerID;
import com.sap.jms.protocol.PacketWithSessionIDAndDestinationIDImpl;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
class ConsumerBasePacket extends PacketWithSessionIDAndDestinationIDImpl implements PacketWithConsumerID {

    protected static final int POS_CONSUMER_ID = POS_DESTINATION_ID + SIZEOF_INT;
    protected static final int SIZE = POS_CONSUMER_ID + SIZEOF_LONG;

    protected ConsumerBasePacket() {}

    /**
     * Constructor for ConsumerBasePacket
     */
    protected ConsumerBasePacket(byte type_id, int size, int session_id, long consumer_id) throws JMSException {
        super(type_id, size, session_id);
        setLong(POS_CONSUMER_ID, consumer_id);
    }

    /**
     * Constructor for ConsumerBasePacket
     */
    protected ConsumerBasePacket(byte type_id, int session_id, long consumer_id) throws JMSException {
        super(type_id, SIZE, session_id);
        setLong(POS_CONSUMER_ID, consumer_id);
    }

    /**
     *  Returns the consumer id
     *  @return the consumer id
     */
    public final long getConsumerID() throws BufferUnderflowException {
        return getLong(POS_CONSUMER_ID);
    }

    /**
     *  Returns a string representation of the packet
     *  @param out to writer to use to print the packet
     */
    protected void toString(PrintWriter out) throws Exception {
        super.toString(out);
        out.printf("%30s %x\n", "ConsumerID:", new Long(getConsumerID()));
    }
}
