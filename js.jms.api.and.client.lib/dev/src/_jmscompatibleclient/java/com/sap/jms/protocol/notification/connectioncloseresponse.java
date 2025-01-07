/**
 * ConnectionCloseResponse.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;
import com.sap.jms.protocol.PacketWithConnectionIDImpl;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class ConnectionCloseResponse extends PacketWithConnectionIDImpl {

  /** The ID for this packet. */
  public static final byte TYPE_ID = CONNECTION_CLOSE_RESPONSE;
 
  public ConnectionCloseResponse() {}
  
  /**
   * Constructor for ConnectionCloseResponse.
   * @param client_id the client id
   */
  public ConnectionCloseResponse(long client_id) throws JMSException {
    super(TYPE_ID, SIZE, client_id);
  }

  /**
   * Constructor for ConnectionCloseResponse.
   * @param buffer
   * @param offset
   * @param length
   */
  public ConnectionCloseResponse(byte[] buffer, int offset, int length) {
    super(buffer, offset, length);
  }
}
