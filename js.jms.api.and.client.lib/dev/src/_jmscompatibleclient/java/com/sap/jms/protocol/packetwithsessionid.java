/**
 * PacketWithSessionID.java
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
public interface PacketWithSessionID {

    /**
     *  Returns the session ID
     *  @return the session ID
     *  @exception BufferUnderflowException
     */
    int getSessionID() throws BufferUnderflowException, BufferOverflowException;

    /**
     *  Sets the session ID
     *  @param session_id the session ID
     */
    void setSessionID(int session_id) throws BufferOverflowException, BufferUnderflowException;
}
