/**
 * QueueBrowserCloseResponse.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketWithDestinationID;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class QueueBrowserCloseResponse extends QueueBrowserBasePacket implements PacketWithDestinationID {

    public static final byte TYPE_ID = QUEUEBROWSER_CLOSE_RESPONSE;

    public QueueBrowserCloseResponse() {
        super();
    } //QueueBrowserCloseResponse

    /**
     * Constructor for QueueBrowserCloseResponse
     * @param session_id the ID of the session the browser is associated with
     * @param browser_id the ID of the browser which has been closed
     * @param destination_id the ID of the destination
     * @exception JMSException thrown if something went wrong
     */
    public QueueBrowserCloseResponse(int session_id, long browser_id) throws JMSException {
        super(TYPE_ID, session_id, browser_id);
    } //QueueBrowserCloseResponse
}
