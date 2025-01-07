/**
 * QueueBrowserCreateResponse.java
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
public class QueueBrowserCreateResponse extends QueueBrowserBasePacket implements PacketWithDestinationID {

    public static final byte TYPE_ID = QUEUEBROWSER_CREATE_RESPONSE;

    public QueueBrowserCreateResponse() {
        super();
    } //QueueBrowserCreateResponse

    /**
     * Constructor for QueueBrowserCreateResponse
     * @param session_id the ID of the session this browser is associated with
     * @param browser_id the ID of the newly created queue browser
     * @exception JMSException thrown if something went wrong
     */
    public QueueBrowserCreateResponse(int session_id, long browser_id) throws JMSException {
        super(TYPE_ID, session_id, browser_id);
    } //QueueBrowserCreateResponse
}
