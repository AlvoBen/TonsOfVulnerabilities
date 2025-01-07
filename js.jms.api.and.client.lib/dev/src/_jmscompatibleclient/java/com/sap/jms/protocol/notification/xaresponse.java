/**
 * XAResponse.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketImpl;

/**
 * @author  Margarit Kirov
 * @version 1.0
 */
public class XAResponse extends PacketImpl {
  /** Register packet with the packet name table PacketImpl.PACKET_NAMES hash map */

  public static final byte TYPE_ID = XA_RESPONSE;

  static final int SIZE = 0;

  /**
   * Constructor for XAResponse.
   * @exception JMSException thrown if something went wrong
   */
  public XAResponse() throws JMSException {
    super(TYPE_ID, SIZE);
  }
}
