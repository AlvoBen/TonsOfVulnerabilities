/**
 * DestinationDeleteRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.PacketWithConnectionIDImpl;
import com.sap.jms.protocol.PacketWithDestinationID;

/**
 * @author Dr. Bernd Follmeg
 * @version 1.0
 */
public class DestinationDeleteRequest extends PacketWithConnectionIDImpl implements PacketWithDestinationID {

	public static final byte TYPE_ID = DESTINATION_DELETE_REQUEST;

	protected static final int POS_DESTINATION_ID = POS_CONNECTION_ID + SIZEOF_LONG;
	
	protected static final int SIZE = POS_DESTINATION_ID + SIZEOF_INT;

	public DestinationDeleteRequest() {
	}

	/**
	 * Constructor for DestinationDeleteRequest.
	 * @param client_id the ID of the client who issued the request
	 * @param destination_id the id of the destination which should be deleted
	 * @exception JMSException thrown if something went wrong
	 */
	public DestinationDeleteRequest(long client_id, int destination_id) throws JMSException {
		super(TYPE_ID, SIZE, client_id);
		setInt(POS_DESTINATION_ID, destination_id);
	}

	/**
	 * Returns the destination ID
	 * @return the destination ID
	 */
	public int getDestinationID() throws BufferUnderflowException {
		return getInt(POS_DESTINATION_ID);
	}

	public int getExpectedResponsePacketType() {
		return PacketTypes.DESTINATION_DELETE_RESPONSE;
	}
}
