/**
 * PacketWithBrowserID.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public interface PacketWithBrowserID {

  /**
   *  Returns the browser id
   *  @return the browser id
   */
  long getBrowserID() throws BufferUnderflowException;
}
