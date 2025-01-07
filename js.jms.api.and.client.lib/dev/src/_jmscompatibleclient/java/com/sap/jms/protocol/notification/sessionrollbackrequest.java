/**
 * SessionCreateResponse.java
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

/**
 * @author  Dr. Bernd Follmeg
 * @version 6.30
 */
public class SessionRollbackRequest extends RequestWithMessageID {

    public static final byte TYPE_ID = SESSION_ROLLBACK_REQUEST;

    public SessionRollbackRequest() {
        super();
    }

    /**
     * Constructor for SessionRollbackRequest.
     * @param session_id the ID of the session which should be rolled back
     * @param msgIds mapping between consumer_ids and pcounters of messages that should be rolled back
     * @exception JMSException thrown if something went wrong
     */
    public SessionRollbackRequest(int session_id, Map/*<Long, Set<Long>>*/ msgIdsPerConsumer) throws JMSException {
    	super(TYPE_ID, session_id, msgIdsPerConsumer);
    }

	public int getExpectedResponsePacketType() {
		return PacketTypes.SESSION_ROLLBACK_RESPONSE;
	}
}
