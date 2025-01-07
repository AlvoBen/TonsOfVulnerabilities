/**
 * SessionCreateResponse.java
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
public class SessionCommitResponse extends PacketWithConnectionIDAndSessionIDImpl {

    public static final byte TYPE_ID = SESSION_COMMIT_RESPONSE;

    public SessionCommitResponse() {
        super();
    }

    /**
     * Constructor for SessionCommitResponse
     * @param client_id the client ID of the client to which this sessions belongs to
     * @param session_id the ID of the session which should be commited
     * @exception JMSException thrown if something went wrong
     */
    public SessionCommitResponse(long client_id, int session_id) throws JMSException {
        super(TYPE_ID, SIZE, client_id, session_id);
    }
}
