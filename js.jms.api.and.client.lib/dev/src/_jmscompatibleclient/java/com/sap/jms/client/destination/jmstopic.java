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

	public JMSTopic (String name, int id) {
		super(name, id);
		isQueue = false;
	}

	public String getTopicName() {
		return super.getName();
	} 
}
