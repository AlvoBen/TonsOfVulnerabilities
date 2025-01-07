/**
 * SessionRecoverResponse.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketImpl;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class SessionRecoverResponse extends PacketImpl {

  public static final byte TYPE_ID = SESSION_RECOVER_RESPONSE;

  static final int SIZE = 0;

  /**
   * Constructor for SessionRecoverResponse.
   * @param client_id the client ID of the client to which this sessions belongs to
   * @param session_id the ID of the session which has been recovered
   * @exception JMSException thrown if something went wrong
   */
  public SessionRecoverResponse() throws JMSException {
    super(TYPE_ID, SIZE);
  }
}

