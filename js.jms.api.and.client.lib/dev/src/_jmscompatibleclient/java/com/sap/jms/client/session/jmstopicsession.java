/**
 * TopicSession.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.session;

import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.TemporaryQueue;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.client.connection.Connection;


public class JMSTopicSession extends JMSSession implements TopicSession {

	public JMSTopicSession(int sessionID, long msgIdBase, int ackMode, Connection connection, ThreadSystem threadSystem) throws javax.jms.JMSException {
		super(sessionID, msgIdBase, ackMode, connection, threadSystem);
	}

	public TopicSubscriber createSubscriber(Topic topic) throws JMSException {
		return (JMSTopicSubscriber) createConsumer(topic);
	}

	public TopicSubscriber createSubscriber(Topic topic, String selector,boolean noLocal) throws JMSException {
		return (TopicSubscriber) createConsumer(topic, selector, noLocal);
	}
	
	public TopicPublisher createPublisher(Topic topic) throws JMSException {
		return (TopicPublisher)createProducer(topic);
	}

	public Queue createQueue(String name) throws JMSException {
		throw new IllegalStateException("Not supported.");
	}

	public TemporaryQueue createTemporaryQueue() throws JMSException {
		throw new IllegalStateException("Not supported.");
	}

	public QueueBrowser createBrowser(Queue queue) throws JMSException {
		throw new IllegalStateException("Not supported.");
	}

	public QueueBrowser createBrowser(Queue queue, String messageSelector) throws JMSException {
		throw new IllegalStateException("Not supported.");
	}
}
