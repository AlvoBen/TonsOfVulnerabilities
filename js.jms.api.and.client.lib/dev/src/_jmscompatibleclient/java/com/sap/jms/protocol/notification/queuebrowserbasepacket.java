/**
 * QueueBrowserBasePacket.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import com.sap.jms.util.compat.PrintWriter;

import javax.jms.JMSException;

import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketWithBrowserID;
import com.sap.jms.protocol.PacketWithDestinationID;
import com.sap.jms.protocol.PacketWithSessionIDAndDestinationIDImpl;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
class QueueBrowserBasePacket extends PacketWithSessionIDAndDestinationIDImpl implements PacketWithBrowserID, PacketWithDestinationID {

  protected static final int POS_BROWSER_ID = POS_DESTINATION_ID + SIZEOF_INT;
  protected static final int SIZE = POS_BROWSER_ID + SIZEOF_LONG;  

  protected QueueBrowserBasePacket() {}
  
  /**
   * Constructor for QueueBrowserBasePacket
   */
  protected QueueBrowserBasePacket(byte type_id, int size, int session_id, long browser_id) throws JMSException {
    super(type_id, size, session_id);
    setLong(POS_BROWSER_ID, browser_id);
  }

  /**
   * Constructor for QueueBrowserBasePacket
   */
  protected QueueBrowserBasePacket(byte type_id, int session_id, long browser_id) throws JMSException {
    super(type_id, SIZE, session_id);
    setLong(POS_BROWSER_ID, browser_id);
  }

  /**
   *  Returns the browser id
   *  @return the browser id
   */
  public final long getBrowserID() throws BufferUnderflowException {
    return getLong(POS_BROWSER_ID);
  }

 /**
   *  Returns a string representation of the packet
   *  @param out to writer to use to print the packet
   */
  protected void toString(PrintWriter out) throws Exception 
  {
		super.toString(out);
		out.printf("%30s %d\n", "BrowserID:", new Long(getBrowserID()));
  }
}
