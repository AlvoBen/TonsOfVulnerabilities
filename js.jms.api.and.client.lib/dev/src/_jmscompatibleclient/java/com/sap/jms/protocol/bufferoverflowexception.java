/**
 * BufferOverflowException.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2003.
 * All rights reserved.
 */
package com.sap.jms.protocol;

import javax.jms.JMSException;


public class BufferOverflowException extends JMSException {

    /**
     * Construct an Exception with a reason
     * 
     * @param reason
     */
    public BufferOverflowException(String reason) {
        super(reason);
    }
}