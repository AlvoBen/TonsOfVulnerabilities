/**
 * MessageResponse.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2003.
 * All rights reserved.
 */
package com.sap.jms.protocol.message;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketWithSessionIDImpl;

/**
 * A packet of this type is sended back to the producer for each produced message.
 * It contains the unique message id associated to the message by the server.
 *
 * @author Margarit Kirov, Bernd Follmeg
 * @version 6.30
 */
public class MessageResponse extends PacketWithSessionIDImpl {

    public static final byte TYPE_ID = MESSAGE_RESPONSE;

    static final int POS_NUM_MESSAGE_IDS = POS_SESSION_ID + SIZEOF_INT;
    static final int POS_MESSAGE_IDS = POS_NUM_MESSAGE_IDS + SIZEOF_INT;
    static final int SIZE = POS_MESSAGE_IDS;

    /**
     * Default constructor
     */
    public MessageResponse() {
        super();
    } 

    /**
     * Constructor for MessageResponse
     * @param session_id the ID of the session the message is a associated with
     * @param message_id the ID of the message which should be acknowledged
     * @exception JMSException thrown if something went wrong
     */
    public MessageResponse(int session_id, byte[] message_id) throws JMSException {
        super(TYPE_ID, SIZE + (SIZEOF_INT + SIZEOF_MESSAGE_ID), session_id);
        setInt(POS_NUM_MESSAGE_IDS, 1);
        setByteArray(POS_MESSAGE_IDS, message_id);
    } 

    /**
     * Constructor for MessageResponse
     * @param session_id the ID of the session the messages are a associated with
     * @param message_ids the list of message IDs which should be acknowledged
     * @exception JMSException thrown if something went wrong
     */
    public MessageResponse(int session_id, byte[][] message_ids) throws JMSException {
        super(TYPE_ID, SIZE + (SIZEOF_INT + SIZEOF_MESSAGE_ID) * message_ids.length, session_id);
        setInt(POS_NUM_MESSAGE_IDS, message_ids.length);
        int inc = POS_MESSAGE_IDS + 4;
        for (int i = 0, pos = POS_MESSAGE_IDS; i < message_ids.length; i++, pos += inc) {
            this.setByteArray(pos, message_ids[i]);
        }
    }
}
