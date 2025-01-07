/**
 * SessionRollbackResponse.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2003.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketWithConnectionIDAndSessionIDImpl;

/**
 * @author  Dr. Bernd Follmeg
 * @version 6.30
 */
public class SessionRollbackResponse extends PacketWithConnectionIDAndSessionIDImpl {

    public static final byte TYPE_ID = SESSION_ROLLBACK_RESPONSE;

    public SessionRollbackResponse() {
        super();
    }

    /**
     * Constructor for SessionRollbackResponse
     * @param client_id the ID of the client the session is associated with
     * @param session_id the ID of the session
     * @exception JMSException if something went wrong
     */
    public SessionRollbackResponse(long client_id, int session_id) throws JMSException {
        super(TYPE_ID, SIZE, client_id, session_id);
    }
}
