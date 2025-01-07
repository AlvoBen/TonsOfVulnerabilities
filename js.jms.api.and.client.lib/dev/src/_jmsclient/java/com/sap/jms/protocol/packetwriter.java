/**
 * PacketWriter.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.                                                                         
 */
package com.sap.jms.protocol;
 
/**
 * This interface provides write access to a packets's buffer.
 *
 * @version     1.0
 * @author      Dr. Bernd Follmeg
 */

//******************************************************************************
public interface PacketWriter extends Packet {
//******************************************************************************

	/**
	 *  Sets the byte at the specified position without moving the buffer's position
	 *  @param position the position where to set the byte 
	 *  @param value the byte to set
	 *  @exception BufferOverflowException thrown if the position is out of range
	 */
	PacketWriter setByte(int position, byte value) throws BufferOverflowException;

	/**
	 *  Puts a byte into the buffer and advances the buffer's position by one byte.
	 *  @param value the byte to write
	 *  @return the PacketWriter object
	 *  @exception BufferOverflowException thrown of the buffer if not 
	 *              large enough to hold the specified data
	 */
	 PacketWriter writeByte(byte value) throws BufferOverflowException;


	/**
	 *  Puts a byte array into the buffer and advances the buffer's position by the
	 *  specified length.
	 *  @param value the byte array to write
	 *  @param offset the offset into the source array 
	 *  @param length the number of bytes written to the buffer
	 *  @return the PacketWriter object
	 *  @exception BufferOverflowException thrown of the buffer if not 
	 *              large enough to hold the specified data
	 */
	 PacketWriter writeByteArray(byte[] value, int offset, int length) throws BufferOverflowException;

	/**
	 *  Appends a character to the buffer. The charater will be appended
	 *  as a sequence of two bytes (UCS2) with the high byte coming first.
	 *  @param value the character to write
	 *  @return the PacketWriter object
	 *  @exception BufferOverflowException thrown of the buffer if not 
	 *              large enough to hold the specified data
	 */
	 PacketWriter writeChar(char value) throws BufferOverflowException;

	/**
	 *  Stores the char at the specified position without moving the buffer's position
	 *  The character will be stored as a sequence of two bytes with the 
	 *  high byte coming first.
	 *  @param value the character to store
	 *  @exception BufferOverflowException thrown of the buffer if not 
	 *              large enough to hold the specified data
	 */
	PacketWriter setChar(int position, char value) throws BufferOverflowException;

	/**
	 *  Puts a short into the buffer and advances the buffer's position by two bytes.
	 *  @param value the short to write
	 *  @return the PacketWriter object
	 *  @exception BufferOverflowException thrown of the buffer if not 
	 *              large enough to hold the specified data
	 */
	 PacketWriter writeShort(short value) throws BufferOverflowException;

	/**
	 *  Stores the short at the specified position without moving the buffer's position
	 *  The short will be stored as a sequence of two bytes with the 
	 *  high byte coming first.
	 *  @param value the short to store
	 *  @exception BufferOverflowException thrown of the buffer if not 
	 *              large enough to hold the specified data
	 */
	PacketWriter setShort(int position, short value) throws BufferOverflowException;

	/**
	 *  Puts an int into the buffer and advances the buffer's position by four bytes.
	 *  @param value the integer to write
	 *  @return the PacketWriter object
	 *  @exception BufferOverflowException thrown of the buffer if not 
	 *              large enough to hold the specified data
	 */
	 PacketWriter writeInt(int value) throws BufferOverflowException;

	/**
	 *  Stores the int at the specified position without moving the buffer's position
	 *  @param position the position where to store the int 
	 *  @param value the int to store
	 *  @exception BufferOverflowException thrown if the buffer is not 
	 *              large enough to hold the specified data
	 */
	PacketWriter setInt(int position, int value) throws BufferOverflowException;
	
	/**
	 *  Puts a long into the buffer and advances the buffer's position by eight bytes.
	 *  @param value the long to write
	 *  @return the PacketWriter object
	 *  @exception BufferOverflowException thrown of the buffer if not 
	 *              large enough to hold the specified data
	 */
	 PacketWriter writeLong(long value) throws BufferOverflowException;

	/**
	 *  Stores the long at the specified position without moving the buffer's position
	 *  @param position the position where to store the long 
	 *  @param value the long to store
	 *  @exception BufferOverflowException thrown if the position is out of range
	 */
	PacketWriter setLong(int position, long value) throws BufferOverflowException;

	/**
	 *  Puts a float into the buffer and advances the buffer's position by four bytes.
	 *  @param value the float to write
	 *  @return the PacketWriter object
	 *  @exception BufferOverflowException thrown of the buffer if not 
	 *              large enough to hold the specified data
	 */
	 PacketWriter writeFloat(float value) throws BufferOverflowException;

	/**
	 *  Stores the float at the specified position without moving the buffer's position
	 *  @param position the position where to store the float 
	 *  @param value the float to store
	 *  @exception BufferOverflowException thrown of the buffer if not 
	 *              large enough to hold the specified data
	 */
	PacketWriter setFloat(int position, float value) throws BufferOverflowException;

	/**
	 *  Puts a double into the buffer and advances the buffer's position by eight bytes.
	 *  @param value the double to write
	 *  @return the PacketWriter object
	 *  @exception BufferOverflowException thrown of the buffer if not 
	 *              large enough to hold the specified data
	 */
	 PacketWriter writeDouble(double value) throws BufferOverflowException;

	/**
	 *  Stores the double at the specified position without moving the buffer's position
	 *  @param position the position where to store the double 
	 *  @param value the double to store
	 *  @exception BufferOverflowException thrown of the buffer if not 
	 *              large enough to hold the specified data
	 */
	PacketWriter setDouble(int position, double value) throws BufferOverflowException;

	/**
	 *  Puts an ASCII string into the buffer. Each character of the string
	 *  is appended to the stream as a sequence of bytes by discarding 
	 *  the high bytes of the characters.
	 *  @param value the double to write
	 *  @return the PacketWriter object
	 *  @exception BufferOverflowException thrown of the buffer if not 
	 *              large enough to hold the specified data
	 */
	 PacketWriter writeASCII(String value) throws BufferOverflowException;

	/**
	 *  Stores the string at the specified position without moving the buffer's position.
	 *  The string will be serialized using UTF8 encoding. A byte which specifies the
	 *  string's encoding followed by the string's length (int) preceeds the UTF8 data. 
	 *  @param position the position where to store the string 
	 *  @param value the string to store
	 *  @exception BufferOverflowException thrown of the buffer if not 
	 *              large enough to hold the specified data
	 */
	PacketWriter setUTF8(int position, String value) throws BufferOverflowException;

	/**
	 *  Puts a string into the buffer. Each character of the string
	 *  is appended to the stream as a sequence of two bytes with 
	 *  the high byte first.
	 *  @param value the string to write
	 *  @return the PacketWriter object
	 *  @exception BufferOverflowException thrown of the buffer if not 
	 *              large enough to hold the specified data
	 */
	 PacketWriter writeUCS2(String value) throws BufferOverflowException;

	/**
	 *  Stores the string at the specified position without moving the buffer's position.
	 *  The string will be serialized using UCS2 encoding. A byte which specifies the
	 *  string's encoding followed by the string's length (int) preceeds the UCS2 data. 
	 *  @param position the position where to store the string 
	 *  @param value the string to store
	 *  @exception BufferOverflowException thrown of the buffer if not 
	 *              large enough to hold the specified data
	 */
	PacketWriter setUCS2(int position, String value) throws BufferOverflowException;

	/**
	 *  Puts a string into the buffer using UTF8 encoding.
	 *  First, the length of the UTF8 bytes will be written followed by
	 *  the utf8 bytes.
	 *  @param value the string to write
	 *  @return the PacketWriter object
	 *  @exception BufferOverflowException thrown of the buffer if not 
	 *              large enough to hold the specified data
	 */
	 PacketWriter writeUTF8(String value) throws BufferOverflowException;

	/**
	 *  Stores the string at the specified position without moving the buffer's position.
	 *  The string will be serialized using ASCII encoding. A byte which specifies the
	 *  string's encoding followed by the string's length (int) preceeds the ASCII data. 
	 *  @param position the position where to store the string 
	 *  @param value the string to store
	 *  @exception BufferOverflowException thrown of the buffer if not 
	 *              large enough to hold the specified data
	 */
	PacketWriter setASCII(int position, String value) throws BufferOverflowException;
}

