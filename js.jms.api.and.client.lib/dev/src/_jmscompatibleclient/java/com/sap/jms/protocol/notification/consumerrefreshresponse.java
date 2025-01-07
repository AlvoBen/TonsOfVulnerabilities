/**
 * ConsumerRefreshResponse.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2004.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketImpl;

/**
 * The purpose of the Packet is to flush all existing communication in the
 * socket wrapper. No specific data is needed in the package
 */
public class ConsumerRefreshResponse extends PacketImpl {
	public static final byte TYPE_ID = CONSUMER_REFRESH_RESPONSE;

	public ConsumerRefreshResponse() throws JMSException {
		super(TYPE_ID);
	}

	public ConsumerRefreshResponse(byte[] buffer, int offset, int length) {
		super(buffer, offset, length);
	}
}
