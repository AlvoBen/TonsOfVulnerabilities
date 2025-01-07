package com.sap.jms.protocol.notification;

import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketTypes;

public class SessionBeforeCompletionRequest extends RequestWithMessageID {

    public static final byte TYPE_ID = SESSION_BEFORE_COMPLETION_REQUEST;

    static {
        PACKET_NAMES.put(new Byte(TYPE_ID), "SessionBeforeCompletionRequest");
    }

    public SessionBeforeCompletionRequest() {
        super();
    }

    public SessionBeforeCompletionRequest(int session_id, Map/*<Long, Set<Long>>*/ msgIdsPerConsumer) throws JMSException {
    	super(TYPE_ID, session_id, msgIdsPerConsumer);
    }

    public int getExpectedResponsePacketType() {
  	  return PacketTypes.SESSION_BEFORE_COMPLETION_RESPONSE;
    }       
}
