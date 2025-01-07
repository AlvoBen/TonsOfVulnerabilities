/**
 * Topic.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.destination;


public class JMSTopic extends JMSDestination implements javax.jms.Topic {

	static final long serialVersionUID = 1853292543998041301L;

	public JMSTopic(String name, int destinationId, String vpName) {
		super(name, destinationId, vpName, false, false);
	}
	
	protected JMSTopic(String name, int destinationId, String vpName, boolean isTemporary) {
		super(name, destinationId, vpName, false, isTemporary);
	}
	
	public String getTopicName() {
		return super.getName();
	} 
}
