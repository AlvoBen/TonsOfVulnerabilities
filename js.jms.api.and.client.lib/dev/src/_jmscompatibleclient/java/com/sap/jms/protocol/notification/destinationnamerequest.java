/**
 * DestinationNameRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketImpl;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.PacketWithDestinationID;

/**
 * @version 1.0
 */
public class DestinationNameRequest extends PacketImpl implements PacketWithDestinationID {
	public static final byte TYPE_ID = DESTINATION_NAME_REQUEST;
	public static final int POS_DESTINATION_ID = 0;
	public static final int SIZE = POS_DESTINATION_ID + SIZEOF_INT;

	public DestinationNameRequest() throws JMSException {
		super();
	}

	public DestinationNameRequest(int destinationID) throws JMSException {
		super(TYPE_ID, SIZE);
		setInt(POS_DESTINATION_ID, destinationID);
	}

	public int getDestinationID() throws BufferUnderflowException {
		return getInt(POS_DESTINATION_ID);
	}

	public int getExpectedResponsePacketType() {
		return PacketTypes.DESTINATION_NAME_RESPONSE;
	}
}
