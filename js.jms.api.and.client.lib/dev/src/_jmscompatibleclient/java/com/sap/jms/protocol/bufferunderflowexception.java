/**
 * BufferUnderflowException.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2003.
 * All rights reserved.
 */
package com.sap.jms.protocol;

import javax.jms.JMSException;


public class BufferUnderflowException extends JMSException {

    /**
     * Construct an Exception with a reason
     * 
     * @param reason
     */
    public BufferUnderflowException(String reason) {
        super(reason);
    }
}