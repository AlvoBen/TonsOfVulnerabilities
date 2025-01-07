/**
 * PacketWithConnectionID.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol;

/**
 * @author  Dr. Bernd Follmeg
 * @version 6.30
 */
public interface PacketWithConnectionID {

	/**
	 *  Returns the connection ID
	 *  @return the connection ID
	 *  @exception BufferUnderflowException thrown on buffer underflow	
	 */
	long getConnectionID() throws BufferUnderflowException;

    /**
     * Sets the connection ID
     * @param connectionID
     * @throws BufferOverflowException
     */
    void setConnectionID(long connectionID) throws BufferOverflowException;
}
