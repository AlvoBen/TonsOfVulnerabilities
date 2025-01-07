/**
 * ProducerCloseResponse.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class ProducerCloseResponse extends ProducerBasePacket {

  public static final byte TYPE_ID = PRODUCER_CLOSE_RESPONSE;
 
  public ProducerCloseResponse() {}
  
  /**
   * Constructor for ProducerCloseResponse
   * @param session_id the ID of the session to which the producer is associated with
   * @param producer_id the ID of the consumer which has been closed
   * @exception JMSException thrown if something went wrong
   */
  public ProducerCloseResponse(int session_id, long producer_id) throws JMSException {
    super(TYPE_ID, session_id, producer_id);
  }
}
