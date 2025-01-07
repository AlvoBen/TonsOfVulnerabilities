/**
 * ConsumerCloseResponse.java
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
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class ConsumerCloseResponse extends ConsumerBasePacket implements PacketWithConsumerID, PacketWithDestinationID {

  public static final byte TYPE_ID = CONSUMER_CLOSE_RESPONSE;

  public ConsumerCloseResponse() {}
  
  /**
   * Constructor for ConsumerCloseResponse
   * @param session_id the ID of the session to which the consumer is associated with
   * @param consumer_id the ID of the consumer which has been closed
   * @exception JMSException thrown if something went wrong
   */
  public ConsumerCloseResponse(int session_id, long consumer_id, int destination_id) throws JMSException {
    super(TYPE_ID, session_id, consumer_id);
	setDestinationID(destination_id);
  }
}
