/**
 * ConnectionCreateResponse.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import com.sap.jms.util.compat.PrintWriter;

import javax.jms.JMSException;

import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketWithConnectionIDImpl;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class ConnectionCreateResponse extends PacketWithConnectionIDImpl {

  /** The ID for this packet. */
  public static final byte TYPE_ID = CONNECTION_CREATE_RESPONSE;

  /** Position of the protocol version number */
  protected static final int POS_PROTOCOL_VERSION = POS_CONNECTION_ID + SIZEOF_LONG;
  protected static final int SIZE = POS_PROTOCOL_VERSION + SIZEOF_INT;
  
  /**
   *The packet size before introduction of the PROTOCOL_VERSION
   *In case the packet length is that size, we will assume the packet was
   *from earlier version and it has a 000 version
   */
  private static final int OLD_PACKET_SIZE = PacketWithConnectionIDImpl.SIZE;  

  public ConnectionCreateResponse() {}
  
  
  
  private ConnectionCreateResponse(byte packet_type, int packet_size, long client_id) throws JMSException {
	  super(packet_type,packet_size,client_id); 
  }  
  

  public static ConnectionCreateResponse createOldConnectionCreateResponse(long client_id) throws JMSException {
	  ConnectionCreateResponse response = new ConnectionCreateResponse(TYPE_ID, OLD_PACKET_SIZE, client_id);
	  response.setLong(POS_CONNECTION_ID, client_id);
	  return response;
  }
  
  /**
   * Constructor for ConnectionCreateResponse.
   * @param client_id the client id
   */
  public ConnectionCreateResponse(long client_id) throws JMSException {
    super(TYPE_ID, SIZE, client_id);
    setInt(POS_PROTOCOL_VERSION,  PROTOCOL_VERSION);    
  }

  /**
   * Constructor for ConnectionCreateResponse.
   * @param buffer
   * @param offset
   * @param length
   */
  public ConnectionCreateResponse(byte[] buffer, int offset, int length) {
    super(buffer, offset, length);
  }

  /**
   *  @return the compatibility protocol version as set by the client
   */
  public final int getProtocolCompatibilityVersion() throws BufferUnderflowException, JMSException
  {
	  // In case the packet is old, before introduce of protocol version,
      // there will be no version and accessing
	  // it will cause problems, we will return 0 directly in that case
	    
	    if (getLength() == LEN_PACKET_HEADER + OLD_PACKET_SIZE){
	        return 0;  
	    }

	    int version = getProtocolNumber(); 
	    return version % 1000;
  }

  private int getProtocolNumber () throws BufferUnderflowException {
    return getInt(POS_PROTOCOL_VERSION);
  }
  
  protected void toString(PrintWriter out) throws Exception
  {
    super.toString(out);
    //----------------------------------------------------------------
    // Print connection specific properties
    //----------------------------------------------------------------
    out.printf("%30s %d\n%30s %d\n", 
    		"Protocol:", new Integer(getProtocolNumber()),
    		"ProtocolCompatibilityVersion:", new Integer(getProtocolCompatibilityVersion()));
  }  
}

