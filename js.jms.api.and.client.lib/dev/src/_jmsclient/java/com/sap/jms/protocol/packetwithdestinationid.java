/**
 * PacketWithDestinationID.java
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
public interface PacketWithDestinationID {

	/**
	 *  Returns the destination ID
	 *  @return the destination ID
	 *  @exception BufferUnderflowException thrown on buffer underflow	
	 */
	int getDestinationID() throws BufferUnderflowException;
}
