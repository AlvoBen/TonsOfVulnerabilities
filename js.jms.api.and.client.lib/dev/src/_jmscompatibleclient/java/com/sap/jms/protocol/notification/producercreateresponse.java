/**
 * ProducerCreateResponse.java
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
public class ProducerCreateResponse extends ProducerBasePacket {

	public static final byte TYPE_ID = PRODUCER_CREATE_RESPONSE;
 
	public ProducerCreateResponse() {}
  
	/**
	 * Constructor for ProducerCreateResponse
	 * @param session_id the ID of the session to which the producer is associated with
	 * @param producer_id the ID of the newly created consumer
	 * @exception JMSException thrown if something went wrong
	 */
	public ProducerCreateResponse(int session_id, long producer_id) throws JMSException {
		super(TYPE_ID, session_id, producer_id);
	}
}
