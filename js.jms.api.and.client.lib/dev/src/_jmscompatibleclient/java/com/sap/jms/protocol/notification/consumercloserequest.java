/**
 * ConsumerCloseRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.PacketWithConsumerID;
import com.sap.jms.protocol.PacketWithDestinationID;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class ConsumerCloseRequest extends ConsumerBasePacket implements PacketWithConsumerID, PacketWithDestinationID {

  public static final byte TYPE_ID = CONSUMER_CLOSE_REQUEST;

  static final int POS_CONSUMED_MSGS = POS_CONSUMER_ID + SIZEOF_LONG;
  static final int SIZE = POS_CONSUMED_MSGS + SIZEOF_INT;


  public ConsumerCloseRequest() {}
  
  /**
   * Constructor for ConsumerCloseRequest
   * @param session_id the ID of the session to which the consumer is associated with
   * @param consumer_id the ID of the consumer which should be closed
   * @param consumed_msgs the number of consumed messages
   */
  public ConsumerCloseRequest(int session_id, long consumer_id, int consumed_msgs) throws JMSException {
//    super(TYPE_ID, session_id, consumer_id);
    allocate(TYPE_ID, SIZE);
    setSessionID(session_id);
    setLong(POS_CONSUMER_ID, consumer_id);
    setInt(POS_CONSUMED_MSGS, consumed_msgs);
  }

  /**
   * Returns the number consumed messages.
   * @return The number consumed messages
   * @throws JMSException
   */
  public int getConsumedMessages() throws JMSException {
    return getInt(POS_CONSUMED_MSGS);
  }
    
	public int getExpectedResponsePacketType() {
		return PacketTypes.CONSUMER_CLOSE_RESPONSE;
	}
}
