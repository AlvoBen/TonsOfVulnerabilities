package com.sap.jms.protocol.notification;

import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketTypes;

public class SessionAfterCompletionRequest extends RequestWithMessageID {

    public static final byte TYPE_ID = SESSION_AFTER_COMPLETION_REQUEST;

    static {
        PACKET_NAMES.put(new Byte(TYPE_ID), "SessionAfterCompletionRequest");
    }

    public SessionAfterCompletionRequest() {
        super();
    }

    public SessionAfterCompletionRequest(int session_id, Map/*<Long, Set<Long>>*/ msgIdsPerConsumer,int status) throws JMSException {
    	super(TYPE_ID, session_id, msgIdsPerConsumer);
    	
    	allocate(TYPE_ID,getCapacity() + SIZEOF_INT);
    	
    	setPosition(getCapacity() - SIZEOF_INT);
    	writeInt(status);
    }
    
    public int getStatus() throws JMSException {
    	setPosition(getCapacity() - SIZEOF_INT);
    	return readInt();
    }

    public int getExpectedResponsePacketType() {
    	  return PacketTypes.SESSION_AFTER_COMPLETION_RESPONSE;
    }       

}
