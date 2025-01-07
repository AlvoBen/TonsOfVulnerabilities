
/**
 * PacketReader.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.                                                                         
 */
package com.sap.jms.protocol;

import java.io.CharConversionException;
 
/**
 * This interface provides read access to a protocol's buffer.
 *
 * @version     1.0
 * @author      Dr. Bernd Follmeg
 */

//******************************************************************************
public interface PacketReader extends Packet {
//******************************************************************************
	
	/**
	 *  Reads a byte from the buffer's current position
	 *  @param position the position where to start reading
	 *  @return the byte
	 *  @exception BufferUnderflowException thrown if the buffer has
	 *      reached its end
	 */
	 byte readByte() throws BufferUnderflowException;

	/**
	 *  Moves the buffer's offset to the specified position and 
	 *  return a byte.
	 *  @param position the position where to start reading
	 *  @return the byte as read from the buffer
	 *  @exception BufferUnderflowException thrown if the buffer has
	 *      reached its end
	 */
	 byte getByte(int position) throws BufferUnderflowException;

	/**
	 *  Reads a char from the buffer's current position
	 *  @return the char as read from the buffer
	 *  @exception BufferUnderflowException thrown if the buffer has
	 *      reached its end
	 */
	 char readChar() throws BufferUnderflowException;

	/**
	 *  Moves the buffer's offset to the specified position and 
	 *  return a char
	 *  @param position the position where to start reading
	 *  @return the char as read from the buffer
	 *  @exception BufferUnderflowException thrown if the buffer has
	 *      reached its end
	 */
	 char getChar(int position) throws BufferUnderflowException;

	/**
	 *  Reads a short from the buffer's current position
	 *  @return the short
	 *  @exception BufferUnderflowException thrown if the buffer has
	 *      reached its end
	 */
	 short readShort() throws BufferUnderflowException;

	/**
	 *  Moves the buffer's offset to the specified position and 
	 *  returns a short
	 *  @param position the position where to start reading
	 *  @return the short as read from the buffer
	 *  @exception BufferUnderflowException thrown if the buffer has
	 *      reached its end
	 */
	 short getShort(int position) throws BufferUnderflowException;

	/**
	 *  Reads an int from the buffer's current position
	 *  @return the int as read from the buffer
	 *  @exception BufferUnderflowException thrown if the buffer has
	 *      reached its end
	 */
	 int readInt() throws BufferUnderflowException;

	/**
	 *  Moves the buffer's offset to the specified position and 
	 *  returns a short
	 *  @param position the position where to start reading
	 *  @return the short as read from the buffer
	 *  @exception BufferUnderflowException thrown if the buffer has
	 *      reached its end
	 */
	 int getInt(int position) throws BufferUnderflowException;

	/**
	 *  Reads an long from the buffer's current position
	 *  @return the long as read from the buffer
	 *  @exception BufferUnderflowException thrown if the buffer has
	 *      reached its end
	 */
	 long readLong() throws BufferUnderflowException;

	/**
	 *  Moves the buffer's offset to the specified position and 
	 *  returns a long
	 *  @param position the position where to start reading
	 *  @return the long as read from the buffer
	 *  @exception BufferUnderflowException thrown if the buffer has
	 *      reached its end
	 */
	 long getLong(int position) throws BufferUnderflowException;

	/**
	 *  Reads a float from the buffer's current position
	 *  @return the float as read from the buffer
	 *  @exception BufferUnderflowException thrown if the buffer has
	 *      reached its end
	 */
	 float readFloat() throws BufferUnderflowException;

	/**
	 *  Moves the buffer's offset to the specified position and 
	 *  returns a float
	 *  @param position the position where to start reading
	 *  @return the float as read from the buffer
	 *  @exception BufferUnderflowException thrown if the buffer has
	 *      reached its end
	 */
	 float getFloat(int position) throws BufferUnderflowException;

	/**
	 *  Reads a double from the buffer's current position
	 *  @return the double as read from the buffer
	 *  @exception BufferUnderflowException thrown if the buffer has
	 *      reached its end
	 */
	 double readDouble() throws BufferUnderflowException;

	/**
	 *  Moves the buffer's offset to the specified position and 
	 *  returns a double
	 *  @param position the position where to start reading
	 *  @return the double as read from the buffer
	 *  @exception BufferUnderflowException thrown if the buffer has
	 *      reached its end
	 */
	double getDouble(int position) throws BufferUnderflowException;

	/**
	 *  Reads a string from the buffer's current position
	 *  @return the string as read from the buffer
	 *  @exception BufferUnderflowException thrown if the buffer has
	 *  @exception CharConversionException thrown if a string could not
	 * 			be read due to illegal characters sequences in the buffer.
	 */
	String readString() throws BufferUnderflowException, CharConversionException;

	/**
	 *  Moves the buffer's offset to the specified position and 
	 *  returns a string
	 *  @param position the position where to start reading
	 *  @return the string as read from the buffer
	 *  @exception BufferUnderflowException thrown if the buffer has
	 *      reached its end
	 *  @exception CharConversionException thrown if a string could not
	 * 			be read due to illegal characters sequences in the buffer.
	 */
	String getString(int position) throws BufferUnderflowException, CharConversionException;
}
