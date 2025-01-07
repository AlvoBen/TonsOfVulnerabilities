package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketWithConnectionIDAndSessionIDImpl;

public class SessionBeforeCompletionResponse extends PacketWithConnectionIDAndSessionIDImpl {

    public static final byte TYPE_ID = SESSION_BEFORE_COMPLETION_RESPONSE;

    static {
        PACKET_NAMES.put(new Byte(TYPE_ID), "SessionBeforeCompletionResponse");
    }

    public SessionBeforeCompletionResponse() {
        super();
    }
    
    public SessionBeforeCompletionResponse(long client_id, int session_id) throws JMSException {
        super(TYPE_ID, SIZE, client_id, session_id);
    }    
}
