/**
 * QueueBrowserCloseRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.PacketWithDestinationID;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class QueueBrowserCloseRequest extends QueueBrowserBasePacket implements PacketWithDestinationID {

  public static final byte TYPE_ID = QUEUEBROWSER_CLOSE_REQUEST;
   
  public QueueBrowserCloseRequest() {}
  
  /**
   * Constructor for QueueBrowserCloseRequest
   * @param session_id the ID of the session the browser is associated with
   * @param browser the ID of the browser which should be closed
   * @exception JMSException thrown if something went wrong
   */
  public QueueBrowserCloseRequest(int session_id, long browser_id) throws JMSException {
    super(TYPE_ID, session_id, browser_id);
  }

	public int getExpectedResponsePacketType() {
//		return PacketTypes.QUEUEBROWSER_CLOSE_RESPONSE;  // TODO: This should be the correct value!!!
		return PacketTypes.CONSUMER_CLOSE_RESPONSE;
	}
}
