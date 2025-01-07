/**
 * PacketWithConsumerID.java
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
public interface PacketWithConsumerID {

	/**
	 *  Returns the consumer ID
	 *  @return the consumer ID
	 *  @exception BufferUnderflowException thrown on buffer underflow	
	 */
	long getConsumerID() throws BufferUnderflowException;
}
