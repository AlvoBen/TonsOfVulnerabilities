/**
 * QueueReceiver.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.session;

import com.sap.jms.client.destination.JMSDestination;
import com.sap.jms.client.destination.JMSQueue;


public class JMSQueueReceiver extends JMSMessageConsumer implements javax.jms.QueueReceiver {

//	private JMSQueue queue;
	
	protected JMSQueueReceiver(JMSQueue queue, long consumerId, JMSSession session, String selector) throws javax.jms.JMSException {
		super((JMSDestination)queue, consumerId, session, selector);
//		this.queue = queue;
	}


	public javax.jms.Queue getQueue() throws javax.jms.JMSException {
		checkClosed();
		return (javax.jms.Queue)super.getDestination();
	}

}
