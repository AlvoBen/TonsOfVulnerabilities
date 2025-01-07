/**
 * QueueSession.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.session;

import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.QueueReceiver;
import javax.jms.TopicSubscriber;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.client.connection.Connection;

public class JMSQueueSession extends JMSSession implements QueueSession {

	public JMSQueueSession(int sessionID, long msgIdBase, int ackMode, Connection connection, ThreadSystem threadSystem) throws JMSException {
		super(sessionID, msgIdBase, ackMode, connection, threadSystem);
	}

	public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
		throw new IllegalStateException("Not supported.");
	}

	public TopicSubscriber createDurableSubscriber(Topic topic, String name, String msgSelector, boolean noLocal) throws JMSException {
		throw new IllegalStateException("Not supported.");
	}

	public Topic createTopic(String name) throws JMSException {
		throw new IllegalStateException("Not supported.");
	}

	public TemporaryTopic createTemporaryTopic() throws JMSException {
		throw new IllegalStateException("Not supported.");
	}

	public void unsubscribe(String name) throws JMSException {
		throw new IllegalStateException("Not supported.");
	}

	public QueueReceiver createReceiver(Queue queue) throws JMSException {
		return (QueueReceiver)createConsumer(queue);
	}

	public QueueReceiver createReceiver(Queue queue, String selector) throws JMSException {
		return (QueueReceiver)createConsumer(queue, selector);
	}

	public javax.jms.QueueSender createSender(Queue queue) throws JMSException {
		return (QueueSender)createProducer(queue);
	}
}
