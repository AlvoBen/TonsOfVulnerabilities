/**
 * Queue.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.destination;

//import com.sap.jms.JMSConstants;


public class JMSQueue extends JMSDestination implements javax.jms.Queue {

	static final long serialVersionUID = -435569008772119566L;
//	long maxDeliveryCount = JMSConstants.LISTENER_REDELIVERY_ATTEMPTS;

	public JMSQueue(String name, int id) {
		super(name, id);
		isQueue = true;
	}

//	public JMSQueue(String name, int id, long maxDeliveryCount) {
//		super (name, id);
//		isQueue = true;
//		this.maxDeliveryCount = maxDeliveryCount;
//	}

	public String getQueueName() {
		return super.getName();
	}
}
