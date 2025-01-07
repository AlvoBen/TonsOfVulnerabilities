/**
 * ConsumerCreateRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import com.sap.jms.util.compat.PrintWriter;

import javax.jms.JMSException;

import com.sap.jms.protocol.*;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class ConsumerCreateRequest extends ConsumerBasePacket implements PacketWithConsumerID, PacketWithDestinationID, PacketWithConnectionID {

  public static final byte TYPE_ID = CONSUMER_CREATE_REQUEST;

  static final int POS_CONNECTION_ID = POS_CONSUMER_ID + SIZEOF_LONG;
  static final int POS_CLIENT_LIMIT = POS_CONNECTION_ID + SIZEOF_LONG;
  static final int POS_BITSET = POS_CLIENT_LIMIT + SIZEOF_INT;
  static final int POS_STRING_ARRAY = POS_BITSET + SIZEOF_BYTE;
  static final int SIZE = POS_STRING_ARRAY;
  
  /** The client doesn't want to receive its own publications */
  static final byte BIT_NOLOCAL = (byte) 0x01;

  public ConsumerCreateRequest() {}
  
  /**
   * Constructor for ConsumerCreateRequest.
   * @param session_id the ID of the session to which the consumer is associated with
   * @param subscription_name the name of a durable subscription; <code>null</code> if none
   * @param destination_name the name of the destination
   * @param destination_type the type of the destination; 0 queue; 1 topic
   * @param no_local the noLocal flags; used for topic subscribers only
   * @param client_limit the size of the buffer of the consumer reserved for incoming messages
   * @param message_filter the JMS message filter string; <code>null</code> if none
   */
  public ConsumerCreateRequest(int session_id, String subscription_name, String destination_name, byte destination_type, boolean no_local, String message_filter, int client_limit) throws JMSException {
    String[] value = new String[] {destination_name, message_filter, subscription_name};
    allocate(TYPE_ID, SIZE + strlenUTF8(value));
    setSessionID(session_id);
    setDestinationID(0);
    setByte(POS_BITSET, (byte) ((destination_type << 4) | (no_local ? BIT_NOLOCAL : 0)));
    setInt(POS_CLIENT_LIMIT, client_limit);
    setUTF8Array(POS_STRING_ARRAY, value);
  }

  /**
   *  Returns the type of the producer
   *  @return the type of the producer
   */
  public final byte getType() throws BufferUnderflowException {
    //----------------------------------------------------------	
    // Upper nibble used for type
    //----------------------------------------------------------	
    return (byte) ((getByte(POS_BITSET) >>> 4) & 0x0F);
  }

  /**
   *  Returns <code>true</code> if the producer is a topic; <code>false</code> otherwise
   *  @return true or false
   */
  public final boolean isNoLocal() throws BufferUnderflowException {
    return ((getByte(POS_BITSET) & BIT_NOLOCAL) != 0);
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
   *  Returns the message filter
   *  @return the message filter
   */
  public final String getMessageFilter() throws BufferUnderflowException {
    String[] value = getStringArray(POS_STRING_ARRAY);
    return ((value != null) && (value.length >= 2)) ? value[1] : null;
  }

  /**
   *  Returns the subscription name
   *  @return the subscription name
   */
  public final String getSubscriptionName() throws BufferUnderflowException {
    String[] value = getStringArray(POS_STRING_ARRAY);
    return ((value != null) && (value.length >= 3)) ? value[2] : null;
  }

  /**
   *  Sets the consumer ID
   *  @@param consumer_id the consumer ID
   */
  public void setConsumerID(long consumer_id) throws BufferOverflowException {
    setLong(POS_CONSUMER_ID, consumer_id);
  }

  /**
   * Sets the conneciton ID
   * @param connection_id the connection ID
   * @throws BufferOverflowException
   */
  public void setConnectionID(long connection_id) throws BufferOverflowException {
    setLong(POS_CONNECTION_ID, connection_id);
  }

  /**
   * Returns the connection ID
   * @return
   * @throws BufferUnderflowException
   */
  public long getConnectionID() throws BufferUnderflowException {
    return getLong(POS_CONNECTION_ID);
  }
  
  /**
   * Regurnes the consumer buffer size
   */
  public int getClientLimit() throws BufferUnderflowException {
    return getInt(POS_CLIENT_LIMIT);
  }

  /**
   *  Returns a string representation of the packet
   *  @param out to writer to use to print the packet
   */
  protected void toString(PrintWriter out) throws Exception 
  {
		super.toString(out);
		//----------------------------------------------------------------
		// Print consumer content
		//----------------------------------------------------------------
		out.println("------------------------------ Consumer Content ----------------------------------");
        out.printf("%30s %s\n%30s %s\n%30s %s\n%30s %d\n%30s %s\n", new Object[] {
        		"DestinationName:", getDestinationName(),
        		"MessageFilter:", getMessageFilter(),
        		"SubscriptionName:", getSubscriptionName(),
        		"Type:", new Byte(getType()),
        		"IsNoLocal:", isNoLocal() ? "true" : "false"});
  }

	public int getExpectedResponsePacketType() {
		return PacketTypes.CONSUMER_CREATE_RESPONSE;
	}
}
