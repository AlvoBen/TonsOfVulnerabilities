/**
 * ConnectionCloseRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.PacketWithConnectionIDImpl;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class ConnectionCloseRequest extends PacketWithConnectionIDImpl {

  /** The ID for this packet. */
  public static final byte TYPE_ID = CONNECTION_CLOSE_REQUEST;

  public ConnectionCloseRequest() {
  	super();
  }
  
  /**
   * Constructor for ConnectionCloseRequest.
   * @param client_id the client ID
   */
  public ConnectionCloseRequest(long client_id) throws JMSException {
    super(TYPE_ID, SIZE, client_id);
  }

  /**
   * Constructor for ConnectionCloseRequest.
   * @param buffer
   * @param offset
   * @param length
   */
  public ConnectionCloseRequest(byte[] buffer, int offset, int length) {
    super(buffer, offset, length);
  }

  public int getExpectedResponsePacketType() {
	  return PacketTypes.CONNECTION_CLOSE_RESPONSE;
  }
}

