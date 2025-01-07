/**
 * ConsumerCreateResponse.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketWithConsumerID;
import com.sap.jms.protocol.PacketWithDestinationID;

/**
 * @author Dr. Bernd Follmeg
 * @version 1.0
 */
public class ConsumerCreateResponse extends ConsumerBasePacket implements PacketWithConsumerID, PacketWithDestinationID {

	public static final byte TYPE_ID = CONSUMER_CREATE_RESPONSE;

	public ConsumerCreateResponse() {
	}

	/**
	 * Constructor for ConsumerCreateResponse
	 * @param session_id the ID of the session to which the consumer is associated with
	 * @param consumer_id the ID of the newly created consumer
	 * @param destination_id the ID of the destination
	 * @exception JMSException thrown if something went wrong
	 */
	public ConsumerCreateResponse(int session_id, long consumer_id, int destination_id) throws JMSException {
		super(TYPE_ID, session_id, consumer_id);
		this.setDestinationID(destination_id);
	}
}
