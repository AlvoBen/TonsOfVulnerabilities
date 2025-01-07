/**
 * Packet.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.                                                                         
 */
package com.sap.jms.protocol;
 
import javax.jms.JMSException;

/**
 * Basic protocol constants
 *
 * @version     1.0
 * @author      Dr. Bernd Follmeg
 */

//******************************************************************************
public interface Packet extends Cloneable {
//******************************************************************************

	/** List of types for typed arguments */
	static final byte BYTE       = (byte) -1;
	static final byte CHAR       = (byte) -2;
	static final byte SHORT      = (byte) -3;
	static final byte INT        = (byte) -4;
	static final byte LONG       = (byte) -5;
	static final byte FLOAT      = (byte) -6;
	static final byte DOUBLE     = (byte) -7;
	static final byte BOOLEAN    = (byte) -8;
	static final byte UCS2       = (byte) -9;
	static final byte UTF8       = (byte)-10;
	static final byte ASCII      = (byte)-11;
	static final byte BYTE_ARRAY = (byte)-12;
	static final byte OBJECT     = (byte)-13;
    static final byte NULL       = (byte)-14;
  
    
    //static final byte 
	
	/** Size of variables */
	static final int  SIZEOF_BYTE       = 1;
	static final int  SIZEOF_CHAR       = 2;
	static final int  SIZEOF_SHORT      = 2;
	static final int  SIZEOF_INT        = 4;
	static final int  SIZEOF_LONG       = 8;
	static final int  SIZEOF_FLOAT      = 4;
	static final int  SIZEOF_DOUBLE     = 8;
	static final int  SIZEOF_BOOLEAN    = 1;
	static final int  SIZEOF_MESSAGE_ID = 14;
	static final int  SIZEOF_TXID       = 20;
		
	/** Position of packet size parameter (4 bytes) */
	static final int  POS_PACKET_SIZE = 0;

	/** Position of the packet type parameter (1 byte) */
	static final int  POS_PACKET_TYPE  = POS_PACKET_SIZE + SIZEOF_INT;

	/** Position of the packet flags parameter (1 byte) */
	static final int  POS_PACKET_FLAGS = POS_PACKET_TYPE + SIZEOF_BYTE;
	
	/** Position of the request id (8 bytes) */
	static final int  POS_REQUEST_ID = POS_PACKET_FLAGS + SIZEOF_BYTE;
	
	/** Position where the payload starts */
	static final int  POS_PAYLOAD_START = POS_REQUEST_ID + SIZEOF_LONG;
	 
	/** Length of the protocol header of the packet */
	public static final int  LEN_PACKET_HEADER = POS_PAYLOAD_START;
	 		
  
    /** Version of the protocol  
     * The version is made of 2 parts - major protocol version, which currently is not used for anything except
     * debug statements and a compatibility version - the last 3 digits of the protocol version.
     * In case the compatibility version between the client and the server is different an exception
     * will be thrown.
     * */
    public static final int  PROTOCOL_VERSION = 710110;
    
	
	/**
	 *  Returns a clone of the packet
	 *  @return a clone of the packet 
	 */
	Object clone();
	
    /**
     *  Clears the buffer on which the protocol operates
     */
    void clearBuffer();
	
	/**
	 * @throws BufferUnderflowException
	 *  Sets the backing byte array on which the packet buffer operates.
	 *  @param buffer the buffer for the packet's data
	 *  @param start the offset into the byte array. Data before this index
	 *               will never be touched.
	 *  @param capacity the length of the buffer. 
	 */
	void setBuffer(byte[] buffer, int start, int capacity);
	
 	/**
	 *  Returns the start index into the packet's backing byte array 
	 *  @return the start index of the buffer
 	 */
	int getOffset();

 	/**
	 *  Returns the overall size the packet's backing byte array 
	 *  @return the length of the byte buffer
   *  @throws JMSException
 	 */
	int getLength() throws JMSException;

	/**
	 *  Returns a reference to the backing byte array 
	 *  @return byte[] the packet buffer
   *  @throws JMSException
 	 */
	byte[] getBuffer() throws JMSException;
 
	/**
	 *  Returns the capacity of the payload area, i.e. the total capacity
	 *  of the backing byte array minus the protocol header length
	 *  @return the capacity of the payload area in bytes
   *  @throws JMSException
	 */
	int getCapacity() throws JMSException;

	/**
	 *  Resets the buffer's position to the start of the payload area
	 */
	void reset();	
    
	/**
	 *  Returns the current position in the buffer
	 *  @return the buffer's position
	 */
	int getPosition();

	/**
	 *  Sets the position in the buffer
	 *  @param position the new position of the buffer's backing array
	 *  @exception IndexOutOfBoundsException thrown of the position
	 *              if out of range
	 */
	void setPosition(int position) throws JMSException;

	/**
	 *  Checks whether this packet is a notification (system message) packet.
	 *  @return <code>true</code> if this is a notification packet; 
	 * 			<code>false</code> otherwise. 
	 *  @exception JMSException thrown if the buffer has reached its end
	 */
	boolean isNotification() throws JMSException;

	/**
	 *  Checks whether this packet is a JMS message packet.
	 *  @return <code>true</code> if this is a JMS message packet; <code>false</code> otherwise;
	 *  @exception JMSException thrown if the buffer has prematurely reached its end
	 */
	boolean isMessage() throws JMSException; 
	
	/**
	 *  Returns the packet type
	 *  @return the packet type
	 *  @exception JMSException thrown if the buffer has prematurely reached its end
	 */
	int getPacketType() throws JMSException;
	
	/**
	 * Returns the expected packet type of the response packet.
	 * For request packets this method returns {@link PacketTypes#INVALID_PACKET_TYPE} 
	 */
	int getExpectedResponsePacketType();

	/**
	 *  Returns the packet type as a string
	 *  @return the packet type as a string
	 *  @exception JMSException thrown if the buffer has prematurely reached its end
	 */
	String getPacketTypeAsString() throws JMSException;

	/**
	 *  Returns the request ID
	 *  @return the request ID
	 *  @exception JMSException thrown if the buffer has prematurely reached its end
	 */
	long getRequestID() throws JMSException;

	/**
	 *  Sets the request ID
	 *  @param request_id the request ID
	 *  @exception JMSException thrown if the buffer has prematurely reached its end
	 */
	void setRequestID(long request_id) throws JMSException;

	/**
	 *   Returns a string representation of the packet
	 */
	String toString();
}
