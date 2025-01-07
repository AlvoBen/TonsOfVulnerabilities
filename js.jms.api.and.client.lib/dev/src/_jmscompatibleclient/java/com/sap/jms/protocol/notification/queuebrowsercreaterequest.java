/**
 * QueueBrowserCreateRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import com.sap.jms.util.compat.PrintWriter;

import javax.jms.JMSException;

import com.sap.jms.protocol.BufferOverflowException;
import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.PacketWithDestinationID;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class QueueBrowserCreateRequest extends QueueBrowserBasePacket implements PacketWithDestinationID {

  public static final byte TYPE_ID = QUEUEBROWSER_CREATE_REQUEST;

  static final int POS_CONSUMER_LIMIT = POS_BROWSER_ID + SIZEOF_LONG;
  static final int POS_STRING_ARRAY = POS_CONSUMER_LIMIT + SIZEOF_INT;
  static final int SIZE = POS_STRING_ARRAY;

  public QueueBrowserCreateRequest() {}
  
  /**
   * Constructor for QueueBrowserCreateRequest.
   * @param session_id the ID of the session to which the queue browsers belongs to
   * @param destination_name the name of the queue
   * @param message_filter the JMS message filter string; <code>null</code> if none
   * @param client_limit the size of the browser buffer reserved for incoming messages
   */
  public QueueBrowserCreateRequest(int session_id, String queue_name, String message_filter, int client_limit) throws JMSException {
    String[] value = new String[] {queue_name, message_filter};
    allocate(TYPE_ID, SIZE + strlenUTF8(value));
    setSessionID(session_id);
    setInt(POS_CONSUMER_LIMIT, client_limit);
    setUTF8Array(POS_STRING_ARRAY, value);
  }

  /**
   *  Returns the destination name
   *  @return the destination name
   */
  public final String getDestinationName() throws BufferUnderflowException {
    String[] value = getStringArray(POS_STRING_ARRAY);
    return ((value != null) && (value.length >= 1)) ? value[0] : null;
  }

  /**
   *  Returns the message filter string
   *  @param the message filter string or <code>null</code> if none present
   */
  public final String getMessageFilter() throws BufferUnderflowException {
    String[] value = getStringArray(POS_STRING_ARRAY);
    return ((value != null) && (value.length >= 2)) ? value[1] : null;
  }

  /**
   *  Sets the browser ID
   *  @@param browser_id the browser ID
   */
  public void setBrowserID(long browser_id) throws BufferOverflowException {
    setLong(POS_BROWSER_ID, browser_id);
  }
  
  /**
   * Returns the size of the buffer of the browser.
   */
  public int getConsumerLimit() throws BufferUnderflowException {
    return getInt(POS_CONSUMER_LIMIT);
  }
  
  /**
   *  Returns a string representation of the packet
   *  @param out to writer to use to print the packet
   */
  protected void toString(PrintWriter out) throws Exception 
  {
		super.toString(out);
		//----------------------------------------------------------------
		// Print browser content
		//----------------------------------------------------------------
		out.printf("%30s %s\n%30s %s\n", 
				"DestinationName:", getDestinationName(),
				"MessageFilter:", getMessageFilter());
  }

	public int getExpectedResponsePacketType() {
		return PacketTypes.QUEUEBROWSER_CREATE_RESPONSE;
	}
}
