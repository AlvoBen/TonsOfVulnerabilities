/**
 * ConsumerRefreshRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2004.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketTypes;
/**
 * That packet serves as a signal to the jms provider to provide messages (if available) for
 * the consumer.
 */
public class ConsumerRefreshRequest extends ConsumerBasePacket {
    public static final byte TYPE_ID = CONSUMER_REFRESH_REQUEST;

    /**
     * Empty constructor called by the framework
     */
    public ConsumerRefreshRequest(){}
    
    public ConsumerRefreshRequest(int session_id, long consumer_id,int destination_id)
      throws JMSException {
      super(TYPE_ID, session_id, consumer_id);
      setDestinationID(destination_id);
    }
    
	public int getExpectedResponsePacketType() {
		return PacketTypes.CONSUMER_REFRESH_RESPONSE;
	}
}
