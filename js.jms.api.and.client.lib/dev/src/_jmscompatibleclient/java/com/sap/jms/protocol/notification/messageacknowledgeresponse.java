/**
 * MessageAcknowledgeResponse.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import com.sap.jms.protocol.PacketImpl;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class MessageAcknowledgeResponse extends PacketImpl {

    public static final byte TYPE_ID = MESSAGE_ACKNOWLEDGE_RESPONSE;   

    /**
     * Constructor for MessageAcknowledgeResponse.
     */
    public MessageAcknowledgeResponse() {
    	super(TYPE_ID);
    } 

    /**
     * Constructor for MessageAcknowledgeResponse.
     * @param buffer
     * @param offset
     * @param length
     */
    public MessageAcknowledgeResponse(byte[] buffer, int offset, int length) {
        super(buffer, offset, length);
    } 
}
