package com.sap.jms.client.connection;


import javax.jms.Destination;
import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ServerSession;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.MessageConsumer;
import javax.jms.Topic;

import com.sap.jms.client.message.JMSMessage;
import com.sap.jms.client.session.JMSMessageConsumer;
import com.sap.jms.client.session.JMSSession;
import com.sap.jms.util.Logging;

// @note This class is not thread-safe. It is not intended to be.
// If this changes synchronise on closed.
public class JMSConnectionConsumer implements javax.jms.ConnectionConsumer, MessageListener {

	protected Connection connection;
	protected ServerSessionPool pool;
	protected Session session;
	protected MessageConsumer consumer;
	private boolean closed;
	

	public JMSConnectionConsumer (Destination destination, String selector, ServerSessionPool pool, Connection connection, int maxMessages) throws JMSException {
		this.connection = connection;
		this.pool = pool;
		this.closed = false;
		
		session = connection.createSession(true, Session.SESSION_TRANSACTED);
		consumer = ((JMSSession) session).createConsumer(destination, selector, false, true);
		consumer.setMessageListener(this);
	}

	public JMSConnectionConsumer (Destination destination, String subscription, String selector, ServerSessionPool pool, Connection connection, int maxMessages) throws JMSException {
		this.connection = connection;
		this.pool = pool;
		this.closed = false;

		session = connection.createSession(true, Session.SESSION_TRANSACTED);
		consumer = ((JMSSession) session).createDurableSubscriber((Topic)destination, subscription, selector, false, true);
		consumer.setMessageListener(this);
	}
	
	public void close() throws JMSException {
		if (!closed) {
			// no more messages will be delivered after this point
			// the consumer will be closed from the session.
			session.close();
			closed = true;
		}
	}

	public ServerSessionPool getServerSessionPool() throws JMSException {
		if (closed) {
			throw new IllegalStateException("This ConnectionConsumer is closed.");
		}

		return this.pool;
	}
	
	public void onMessage(Message msg) {
		try {
			ServerSession ss = pool.getServerSession();

			// TODO handle number of messages to deliver per cycle
			long consumerId = ((JMSMessageConsumer)consumer).getConsumerID();
			((JMSSession)ss.getSession()).receiveMessage((JMSMessage)msg, (JMSMessageConsumer)consumer);
			
			// Ugh!!!! remove message from scheduled messages in this session
			// the MDB session will take care of proper commit/rollback or ack/recover 
			((JMSSession)this.session).connectionConsumerFakeAcknowledge((JMSMessage)msg, consumerId);
			ss.start();
		} catch (JMSException e) {
			Logging.exception(this, e);			
			// TODO: handle exception
		}		
	}

	// NOTE Only for the session, to pause and resume the client
	public JMSMessageConsumer getConsumer() {
		return ((JMSMessageConsumer)consumer);
	}

	// Monitoring function
	public long getConsumerID() {
		return ((JMSMessageConsumer)consumer).getConsumerID();
	}
}
