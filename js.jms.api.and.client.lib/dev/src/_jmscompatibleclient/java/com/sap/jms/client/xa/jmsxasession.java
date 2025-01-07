package com.sap.jms.client.xa;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.sap.jms.util.compat.concurrent.ConcurrentHashMap;
import com.sap.jms.util.compat.concurrent.atomic.AtomicReference;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.jms.TransactionInProgressException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.sap.jms.JMSConstants;
import com.sap.jms.client.message.JMSMessage;
import com.sap.jms.client.session.AckHandler;
import com.sap.jms.client.session.JMSSession;
import com.sap.jms.util.logging.LogService;
import com.sap.jms.util.logging.LogServiceImpl;

public class JMSXASession implements javax.jms.XASession, AckHandler {

	private JMSXAResource xaResource;
	private AtomicReference/*<Map<Long, Set<Long>>>*/ dlvrdMsgsPerConsumer;
	
	private LogService log = LogServiceImpl.getLogService(LogServiceImpl.CLIENT_LOCATION);
	private String LNAME;
	private JMSSession session;

	
	public JMSXASession(JMSSession session) throws JMSException {
		
		this.session = session;
		
		dlvrdMsgsPerConsumer = new AtomicReference/*<Map<Long, Set<Long>>>*/();
		dlvrdMsgsPerConsumer.set(new ConcurrentHashMap/*<Long, Set<Long>>*/());
		
		xaResource = new JMSXAResource(this);

		// this class will handle acks
		session.setAckHandler(this);

		LNAME = getClass().getName();
	}

	public Session getSession() {
		return session;
	}

	public XAResource getXAResource() {
		return xaResource;
	}

    public void rollback() throws JMSException {
        throw new TransactionInProgressException("Cannot rollback() from a XASession");
    }
    
    public boolean getTransacted() {
    	return xaResource.isTransactionActive();
    }
 
    public void commit() throws JMSException {
        throw new TransactionInProgressException("Cannot commit() from a XASession");
    }

	public void close() throws JMSException {
		session.close();
	}

	public QueueBrowser createBrowser(Queue queue) throws JMSException {
		return session.createBrowser(queue);
	}

	public QueueBrowser createBrowser(Queue queue, String msgSelector) throws JMSException {
		return session.createBrowser(queue, msgSelector);
	}

	public BytesMessage createBytesMessage() throws JMSException {
		return session.createBytesMessage();
	}

	public MessageConsumer createConsumer(Destination destination) throws JMSException {
		return session.createConsumer(destination);
	}

	public MessageConsumer createConsumer(Destination destination, String msgSelector) throws JMSException {
		return session.createConsumer(destination, msgSelector);
	}

	public MessageConsumer createConsumer(Destination destination, String msgSelector, boolean noLocal) throws JMSException {
		return session.createConsumer(destination, msgSelector, noLocal);
	}

	public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
		return session.createDurableSubscriber(topic, name);
	}

	public TopicSubscriber createDurableSubscriber(Topic topic, String name, String msgSelector, boolean noLocal) throws JMSException {
		return session.createDurableSubscriber(topic, name, msgSelector, noLocal);
	}

	public MapMessage createMapMessage() throws JMSException {
		return session.createMapMessage();
	}

	public Message createMessage() throws JMSException {
		return session.createMessage();
	}

	public ObjectMessage createObjectMessage() throws JMSException {
		return session.createObjectMessage();
	}

	public ObjectMessage createObjectMessage(Serializable object) throws JMSException {
		return session.createObjectMessage(object);
	}

	public MessageProducer createProducer(Destination destination) throws JMSException {
		return session.createProducer(destination);
	}

	public Queue createQueue(String name) throws JMSException {
		return session.createQueue(name);
	}

	public StreamMessage createStreamMessage() throws JMSException {
		return session.createStreamMessage();
	}

	public TemporaryQueue createTemporaryQueue() throws JMSException {
		return session.createTemporaryQueue();
	}

	public TemporaryTopic createTemporaryTopic() throws JMSException {
		return session.createTemporaryTopic();
	}

	public TextMessage createTextMessage() throws JMSException {
		return session.createTextMessage();
	}

	public TextMessage createTextMessage(String text) throws JMSException {
		return session.createTextMessage(text);
	}

	public Topic createTopic(String name) throws JMSException {
		return session.createTopic(name);
	}

	public int getAcknowledgeMode() throws JMSException {
		return session.getAcknowledgeMode();
	}

	public MessageListener getMessageListener() throws JMSException {
		return session.getMessageListener();
	}

	public void recover() throws JMSException {
		session.recover();
	}

	public void run() {
		session.run();
	}

	public void setMessageListener(MessageListener newListener) throws JMSException {
		session.setMessageListener(newListener);
	}

	public void unsubscribe(String subscriptionName) throws JMSException {
		session.unsubscribe(subscriptionName);
	}

	////////////

	// called in JMSMessageConsumer::receive
	// TODO visibility
	public void acknowledge(Long consumerId, JMSMessage message) throws JMSException {

		if (!getTransacted()) {
			session.acknowledge(consumerId, message);
			return;
		}
		
		Long pCounter = new Long(message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER));
		
		log.debug(LNAME, "Delivered message with pCounter: " + pCounter + " as part of a XA transaction to consumer with id: " + consumerId);

		if (((Map)dlvrdMsgsPerConsumer.get()).containsKey(consumerId)) {
			Set/*<Long>*/ tmpList = (Set)((Map)dlvrdMsgsPerConsumer.get()).get(consumerId);
			tmpList.add(pCounter);

		} else {
			Set/*<Long>*/ pCounters = new HashSet/*<Long>*/();
			pCounters.add(pCounter);
			((Map)dlvrdMsgsPerConsumer.get()).put(consumerId, pCounters);
		}
	}

	// called in JMSSession::deliverMessage
	// TODO visibility
	public void scheduleForAcknowledge(Long consumerId, JMSMessage message) throws JMSException {

		if (!getTransacted()) {
			session.scheduleForAcknowledge(consumerId, message);
			return;
		}
		
		Long pCounter = new Long(message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER));

		log.debug(LNAME, "Delivered message with pCounter: " + pCounter + " as part of a XA transaction to consumer with id: " + consumerId);

		if (((Map)dlvrdMsgsPerConsumer.get()).containsKey(consumerId)) {
			Set/*<Long>*/ tmpList = (Set)((Map)dlvrdMsgsPerConsumer.get()).get(consumerId);
			tmpList.add(pCounter);

		} else {
			Set/*<Long>*/ pCounters = new HashSet/*<Long>*/();
			pCounters.add(pCounter);
			((Map)dlvrdMsgsPerConsumer.get()).put(consumerId, pCounters);
		}
	}
	
	public void sendRecover() throws JMSException {
		if (!getTransacted()) {
			session.sendRecover();
			return;
		}

		//session.clearConsumerBuffers();
	}

	public void sendRollback() throws JMSException {
		// if session#close before xa_end
		Xid xid = xaResource.getActiveXid();

		log.warningTrace(LNAME, "Got sendRecover while transaction with xid: " + xid + " was in active state.");
		
		if (xid == null) {
			return;
		}

		try {
			xaResource.rollback(xid);
		} catch (Exception e) {
			JMSException ex = new JMSException("Got XAException while rolling back transaction with xid: " + xid);
			ex.setLinkedException(e);
			throw ex;
		}
	}

	public Map/*<Long, Set<Long>>*/ getDlvrdMsgsPerConsumer() {
		Map/*<Long, Set<Long>>*/ empty = new ConcurrentHashMap/*<Long, Set<Long>>*/();
		Map/*<Long, Set<Long>>*/ pendingMessages = (Map)dlvrdMsgsPerConsumer.getAndSet(empty);

		return pendingMessages;
	}

}
