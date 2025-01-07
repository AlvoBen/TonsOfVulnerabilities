/**
 * TopicSubscriber.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.session;

import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import com.sap.jms.client.destination.JMSDestination;
import com.sap.jms.client.destination.JMSTopic;


public class JMSTopicSubscriber extends JMSMessageConsumer implements TopicSubscriber {

	private boolean noLocal = false;
//	private JMSTopic topic;
	
	protected JMSTopicSubscriber(JMSTopic topic, long consumerId, JMSSession session, String selector, boolean noLocal) throws JMSException {
		super((JMSDestination)topic, consumerId, session, selector);
//		this.topic = topic;
		this.noLocal = noLocal;
	}

	public boolean getNoLocal() throws JMSException {
		checkClosed();
		return noLocal;
	}  

	public Topic getTopic() throws JMSException {
		checkClosed();
		return (javax.jms.Topic)super.getDestination();
	}  

}
