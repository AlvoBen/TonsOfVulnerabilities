/**
 * MessageAcknowledgeRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2003.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketTypes;

public class MessageAcknowledgeRequest extends RequestWithMessageID {

    public static final byte TYPE_ID = MESSAGE_ACKNOWLEDGE_REQUEST;

    // keep for newInstance() to work
    public MessageAcknowledgeRequest() {
    }

    public MessageAcknowledgeRequest(int session_id, Map/*<Long, Set<Long>>*/ msgIdsPerConsumer) throws JMSException {
    	// int 		long	...	int 	int	...	long ...
    	// count    consId      count   msgCount		pcntr
    	super(TYPE_ID, session_id, msgIdsPerConsumer);
    }
    
	public int getExpectedResponsePacketType() {
		return PacketTypes.MESSAGE_ACKNOWLEDGE_RESPONSE;
	}
}
