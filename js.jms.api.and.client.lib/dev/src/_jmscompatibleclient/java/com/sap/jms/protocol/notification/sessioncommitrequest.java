/**
 * SessionCreateRequest.java
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
public class SessionCommitRequest extends RequestWithMessageID {

    public static final byte TYPE_ID = SESSION_COMMIT_REQUEST;

    public SessionCommitRequest() {
        super();
    } //SessionCommitRequest

    /**
     * Constructor for SessionCommitRequest
     * @param session_id the ID of the session which should be committed
     * @param msgIds mapping between consumer_ids and pcounters of messages that should be committed
     * @exception JMSException thrown if something went wrong
     */
    public SessionCommitRequest(int session_id, Map/*<Long, Set<Long>>*/ msgIdsPerConsumer) throws JMSException {
    	super(TYPE_ID, session_id, msgIdsPerConsumer);
    }

	public int getExpectedResponsePacketType() {
		return PacketTypes.SESSION_COMMIT_RESPONSE;
	}
}
