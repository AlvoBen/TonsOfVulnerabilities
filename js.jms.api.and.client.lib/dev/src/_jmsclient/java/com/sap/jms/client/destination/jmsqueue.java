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

	public JMSQueue(String name, int destinationId, String vpName) {
		super(name, destinationId, vpName, true, false);
	}
	
	protected JMSQueue(String name, int destinationId, String vpName, boolean isTemporary) {
		super(name, destinationId, vpName, true, isTemporary);
	}	

	public String getQueueName() {
		return super.getName();
	}
}
