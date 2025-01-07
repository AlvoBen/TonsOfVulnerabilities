/**
 * DestinationCreateRequest.java
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
import com.sap.jms.protocol.PacketWithConnectionIDImpl;
import com.sap.jms.protocol.PacketWithDestinationID;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class DestinationCreateRequest extends PacketWithConnectionIDImpl implements PacketWithDestinationID {

  public static final byte TYPE_ID = DESTINATION_CREATE_REQUEST;
  public static final byte QUEUE = (byte)0x00;
  public static final byte TOPIC = (byte)0x01;

  static final int POS_DESTINATION_ID = POS_CONNECTION_ID + SIZEOF_LONG; 
  static final int POS_BITSET = POS_DESTINATION_ID + SIZEOF_INT;
  static final int POS_DESTINATION_NAME = POS_BITSET + SIZEOF_BYTE;
  static final int SIZE = POS_DESTINATION_NAME;
  static final byte BIT_TEMPORARY = (byte) 0x01;

  public DestinationCreateRequest() {}
  
  /**
   * Constructor for DestinationCreateRequest.
   * @param client_id the ID of the client which is responsible for the request
   * @param destination_name the name of the destination
   * @param destination_type the type of the destination; 0 queue; 1 topic
   * @param is_temporary <code>true</code> if the destination is temporary;
   * <code>false</code> otherwise
   */
  public DestinationCreateRequest(long client_id, String destination_name, byte destination_type, boolean is_temporary) throws JMSException {
    super(TYPE_ID, (SIZE + strlenUTF8(destination_name)), client_id);
    setByte(POS_BITSET, (byte) ((destination_type << 4) | (is_temporary ? BIT_TEMPORARY : 0)));
    setUTF8(POS_DESTINATION_NAME, destination_name);
  }

  /**
   *  Returns the type of the producer; 0 queue; 1 topic
   *  @return the type of the producer
   */
  public final byte getType() throws BufferUnderflowException {
    //----------------------------------------------------------	
    // Upper nibble used for type
    //----------------------------------------------------------	
    return (byte) ((getByte(POS_BITSET) >>> 4) & 0x0F);
  }
  
  /**
   *  Returns <code>true</code> if the destination is temporary; <code>false</code> otherwise
   *  @return true or false
   */
  public final boolean isTemporary() throws BufferUnderflowException {
    return ((getByte(POS_BITSET) & BIT_TEMPORARY) != 0);
  }

  /**
   *  Returns the destination name
   *  @return the destination name
   */
  public final String getDestinationName() throws BufferUnderflowException {
    return getString(POS_DESTINATION_NAME);
  }

  /**
   *  Returns the destination ID
   *  @return the destination ID
   */
  public int getDestinationID() throws BufferUnderflowException {
    return getInt(POS_DESTINATION_ID);
  }

  /**
   *  Sets the destination ID
   *  @@param destion_id the destination ID
   */
  public void setDestinationID(int destination_id) throws BufferOverflowException {
    setInt(POS_DESTINATION_ID, destination_id);
  }
  
  /**
   *  Returns a string representation of the packet
   *  @param out to writer to use to print the packet
   */
  protected void toString(PrintWriter out) throws Exception 
  {
		super.toString(out);
		out.println("------------------------------ Destination Content -----------------------------");
        out.printf("%30s %d\n%30s %s\n%30s %s\n%30s %s\n", new Object[] { 
        		"DestinationID:", new Integer(getDestinationID()),
        		"DestinationName:", getDestinationName(),
        		"Type:", getType() == TOPIC ? "Topic" : "Queue",
     			"IsTemporary:", isTemporary() ? "true" : "false"});
  }
  
	public int getExpectedResponsePacketType() {
		return PacketTypes.DESTINATION_CREATE_RESPONSE;
	}
}
