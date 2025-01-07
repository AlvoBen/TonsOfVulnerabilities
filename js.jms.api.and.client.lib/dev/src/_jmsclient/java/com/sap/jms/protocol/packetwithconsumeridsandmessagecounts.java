/*
 * PacketWithConsumerIDsAndMessageCounts.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2003.
 * All rights reserved.
 */
package com.sap.jms.protocol;

import javax.jms.JMSException;

/**
 * @author Dr. Bernd Follmeg
 * @version 6.30
 */
public interface PacketWithConsumerIDsAndMessageCounts {

    /**
     * Returns the array consumer IDs as an array of long.
     * @return long[] array of consumer IDs
     * @throws JMSException thrown when an error occurs
     */
    long[] getConsumerIDs() throws JMSException;

    /**
     * Returns the messages counts of acknowledged messages as an array of int.
     * @return int[] the messages counts
     * @throws JMSException thrown when an error occurs
     */
    int[] getMessageCounts() throws JMSException;
}
