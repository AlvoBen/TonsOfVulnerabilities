package com.sap.jms.client.session;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.IllegalStateException;
import javax.jms.InvalidClientIDException;
import javax.jms.InvalidDestinationException;
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

import com.sap.engine.system.ThreadWrapper;
import com.sap.jms.JMSConstants;
import com.sap.jms.client.connection.DestinationInfo;
import com.sap.jms.client.connection.Subscription;
import com.sap.jms.client.connection.AsyncCloser;
import com.sap.jms.client.connection.Closeable;
import com.sap.jms.client.connection.Connection;
import com.sap.jms.client.connection.ServerFacade;
import com.sap.jms.client.destination.JMSDestination;
import com.sap.jms.client.destination.JMSQueue;
import com.sap.jms.client.destination.JMSTemporaryQueue;
import com.sap.jms.client.destination.JMSTemporaryTopic;
import com.sap.jms.client.destination.JMSTopic;
import com.sap.jms.client.message.JMSBytesMessage;
import com.sap.jms.client.message.JMSMapMessage;
import com.sap.jms.client.message.JMSMessage;
import com.sap.jms.client.message.JMSObjectMessage;
import com.sap.jms.client.message.JMSStreamMessage;
import com.sap.jms.client.message.JMSTextMessage;
import com.sap.jms.protocol.MessageRequest;
import com.sap.jms.interfaces.DSRInstrumentation;
import com.sap.jms.protocol.Packet;
import com.sap.jms.util.MessageID;
import com.sap.jms.util.Logging;
import com.sap.jms.util.LogUtil;
import com.sap.jms.client.connection.Closeable;
import com.sap.jms.client.connection.ServerFacade.DelayedDeliveryData;
import com.sap.jms.client.connection.Subscription.ConsumerType;
import com.sap.jms.interfaces.DSRInstrumentation;
import com.sap.tc.logging.Severity;
import static com.sap.jms.client.connection.DestinationInfo.DestinationType;

import com.sap.jms.util.TaskManager;
import com.sap.jms.util.Task;


public class JMSSession implements javax.jms.Session, Closeable, AckHandler, Task {

	public static enum SessionType {
		QUEUE_SESSION,
		TOPIC_SESSION,
		GENERIC_SESSION,
		XA_QUEUE_SESSION,
		XA_TOPIC_SESSION,
		XA_GENERIC_SESSION,
	};

	private AtomicBoolean closed;
	private AtomicBoolean closing;

	private Map<Long, WeakReference<MessageProducer>> producers;
	private Map<Long, WeakReference<JMSMessageConsumer>> consumers;
	private Map<Long, WeakReference<JMSQueueBrowser>> browsers;
	private Map<String, Long> subscribers;

	private AtomicReference<Map<Long, List<JMSMessage>>> dlvrdMsgsPerConsumer;

	private java.util.Queue<Long> scheduledConsumerIds;

	private enum SessionState {STOPPED, SCHEDULED, RUNNING};
	private AtomicReference<SessionState> state;

	private AtomicReference<MessageListener> dedicatedMessageListener;
	private AtomicReference<JMSMessage> dedicatedMessage;
	private JMSMessageConsumer dedicatedConsumer;

	private AtomicLong msgCounter;
	private long msgIdBase;

	private AtomicInteger consumerCounter;

	private com.sap.jms.client.connection.Connection connection;
	private int sessionId;
	private int ackMode;
	private TaskManager taskManager;

	private AtomicBoolean optimizedMode;
	private boolean beforeCompletionInvoked = false;
	private AtomicReference<Map<Long, List<JMSMessage>>> pendingOptimizedMessages;


	private AckHandler ackHandler;
	private static Timer timer = new Timer();
	private Map<Long, TimerTask> delayedDeliveryTasks;

	private AtomicBoolean didSendMessages;

	public JMSSession(int sessionId, long msgIdBase, int ackMode, com.sap.jms.client.connection.Connection connection, TaskManager taskManager) {

		this.sessionId = sessionId;
		this.msgIdBase = msgIdBase;
		this.ackMode = ackMode;
		this.connection = connection;
		this.taskManager = taskManager;

		closed = new AtomicBoolean(false);
		closing = new AtomicBoolean(false);
		producers = new ConcurrentHashMap<Long, WeakReference<MessageProducer>>();
		consumers = new ConcurrentHashMap<Long, WeakReference<JMSMessageConsumer>>();
		browsers = new ConcurrentHashMap<Long, WeakReference<JMSQueueBrowser>>();
		subscribers = new ConcurrentHashMap<String, Long>();
		dlvrdMsgsPerConsumer = new AtomicReference<Map<Long, List<JMSMessage>>>();
		dlvrdMsgsPerConsumer.set(new ConcurrentHashMap<Long, List<JMSMessage>>());
		scheduledConsumerIds = new ConcurrentLinkedQueue<Long>();
		state = new AtomicReference<SessionState>(SessionState.STOPPED);
		dedicatedMessageListener = new AtomicReference<MessageListener>(null);
		dedicatedMessage = new  AtomicReference<JMSMessage>(null);
		msgCounter = new AtomicLong(0);
		consumerCounter = new AtomicInteger(0);
		optimizedMode = new AtomicBoolean(false);
		pendingOptimizedMessages = new AtomicReference<Map<Long, List<JMSMessage>>>();
		ackHandler = this;
		delayedDeliveryTasks = new HashMap<Long, TimerTask>();
		didSendMessages = new AtomicBoolean(false);
	}


	// TODO make default / move to jms package; called only by Message#acknowledge
	public synchronized void acknowledge() throws JMSException {
		checkClosed();

		if (ackMode != Session.CLIENT_ACKNOWLEDGE) {
			return;
		}

		Map<Long, List<JMSMessage>> pendingMessages = getPendingMessages();

		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "Will acknowledge: ", pendingMessages);
		}

		Map<Long, List<Long>> tmp = convertPendingMessages(pendingMessages);

		connection.getServerFacade().sessionAcknowledge(connection.getConnectionId(), sessionId, tmp);
	}

	public void close() throws JMSException {
		close(true);
	}

	public void close(boolean hitTheServer) throws JMSException {

		synchronized (closed) {
			if (closed.get() == true) {
				return;
			}

			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "Will stop session with id: ", sessionId); 
			}

			if (hitTheServer) {
			getServerFacade().sessionStop(connection.getConnectionId(), sessionId);
			}
			


			if (!supportsOptimization()) {
				Map<Long, List<JMSMessage>> pendingMessages = getPendingMessages();
				// should be full rollback.

				Map<Long, List<Long>> tmp = convertPendingMessages(pendingMessages);

				if (ackMode == Session.SESSION_TRANSACTED) {
					if (((!tmp.isEmpty() || didSendMessages.get() == true)) && hitTheServer)  {
						connection.getServerFacade().sessionRollback(connection.getConnectionId(), sessionId, tmp);
					}
				} else {
					if ((!tmp.isEmpty()) && hitTheServer) {
						connection.getServerFacade().sessionRecover(connection.getConnectionId(), sessionId, tmp);
					}
				}
			} else {
				// TODO what to do ?
			}
			if (timer != null) {
				for (TimerTask task: delayedDeliveryTasks.values()) {
					if (task != null) {
						task.cancel();
					}
				}
			}

			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "Will close consumers: ", consumers);
			}

			for (Long id: consumers.keySet()) {
				WeakReference<JMSMessageConsumer> c = consumers.get(id);
				if (c != null && c.get() != null) {
					c.get().close();
				}
			}

			consumers.clear();

			synchronized (state) {
				closing.set(true);
				SessionState s = state.getAndSet(SessionState.STOPPED);
				if ( s == SessionState.RUNNING || s == SessionState.SCHEDULED) {

					if (s == SessionState.RUNNING) {
						try {
							state.wait();
						} catch (InterruptedException e) {
							Logging.exception(this, e);
						}
					}
				}
			}

			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "Will close producers: ", producers);
			}

			for (Long id: producers.keySet()) {
				WeakReference<MessageProducer> p = producers.get(id);
				if (p != null && p.get() != null) {
					p.get().close();
				}
			}

			producers.clear();

			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "Will close browsers: ", browsers);
			}

			for (Long id: browsers.keySet()) {
				WeakReference<JMSQueueBrowser> b = browsers.get(id);
				if (b != null && b.get() != null) {
					b.get().close();
				}
			}

			browsers.clear();

			if (hitTheServer) {
			connection.closeSession(sessionId);
			}

			closed.set(true);
		}
	}

	public synchronized void commit() throws JMSException {
		checkClosed();

		if (!getTransacted()) {
            throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "The method is called on a non-transacted session.");
		}

		if (optimizedMode.get() == true) {
            throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "The method is called on a transacted session in optimized mode.");
		}

		Map<Long, List<JMSMessage>> pendingMessages = getPendingMessages();

		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "Will commit: ", pendingMessages);
		}

		Map<Long, List<Long>> tmp = convertPendingMessages(pendingMessages);

		connection.getServerFacade().sessionCommit(connection.getConnectionId(), sessionId, tmp);
		didSendMessages.set(false);
	}

	public QueueBrowser createBrowser(Queue queue) throws JMSException {
		return createBrowser(queue,null);
	}

	public synchronized QueueBrowser createBrowser(Queue queue, String msgSelector) throws JMSException {
		checkClosed();

		if (queue == null || !(queue instanceof JMSQueue)) {
			throw new InvalidDestinationException("Illegal destination!");
		}

		JMSQueue jmsQueue = (JMSQueue) queue;
		if (jmsQueue.getVPName() != null && !jmsQueue.getVPName().equals(connection.getVPName())) {
			throw new InvalidDestinationException("Illegal destination!");
		}

		JMSQueueBrowser browser = null;

		ServerFacade facade = connection.getServerFacade();
		long browserId = ((long) sessionId << 32) | consumerCounter.incrementAndGet();

		Subscription subscription = createSubscription(ConsumerType.QueueBrowser, msgSelector, 102400, 
				connection.getConnectionId(), browserId, jmsQueue.getDestinationId(), null, false, false);

		browser = new JMSQueueBrowser(jmsQueue, browserId, this, msgSelector);
		browsers.put(browserId, new WeakReference<JMSQueueBrowser>(browser));

		try {			
			facade.consumerCreate(subscription);

			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "Mapped ", browser, "(queue id: ", jmsQueue.getDestinationId(),
						", selector: ", msgSelector, ") to browser with id: ", browserId, " under session with id: ", sessionId);
			}
		} catch (JMSException e) {
			browsers.remove(browserId);
			Logging.exception(this, e,"Unable to create " +  browser + "(destination id: " + jmsQueue.getDestinationId()+
					", selector: " + msgSelector + ") to consumer with id: " + browserId + " under session with id: " + sessionId);
			throw e;
		}

		return browser;
	}

	public synchronized BytesMessage createBytesMessage() throws JMSException {
		checkClosed();

		JMSBytesMessage msg = new JMSBytesMessage();
		msg.setSession(this);

		return msg;
	}

	public MessageConsumer createConsumer(Destination destination) throws JMSException {
		return createConsumer(destination, (String)null);
	}

	public MessageConsumer createConsumer(Destination destination, String msgSelector) throws JMSException {
		return createConsumer(destination, msgSelector, false);
	}

	public MessageConsumer createConsumer(Destination destination, String msgSelector, boolean noLocal) throws JMSException {
		return createConsumer(destination, msgSelector, noLocal, false);
	}

	// TODO XXX Fix the visibility of this method
	public synchronized MessageConsumer createConsumer(Destination destination, String msgSelector, boolean noLocal, boolean connectionConsumer) throws JMSException {
		checkClosed();

		if (destination == null) {
			throw new InvalidDestinationException("null destination.");
		}

		if (!(destination instanceof JMSDestination)) {
			throw new InvalidDestinationException(destination.toString());
		}

		JMSDestination jmsDestination = (JMSDestination) destination; 
		String vpName = ((JMSDestination)destination).getVPName();
		if (vpName != null && !vpName.equals(connection.getVPName())) {
			throw new InvalidDestinationException(destination.toString());
		}

		JMSMessageConsumer consumer;

		ServerFacade facade = connection.getServerFacade();
		int clientLimit = 102400 ; // TODO memoryManager.getChunkSize();
		long consumerId = ((long) sessionId << 32) | consumerCounter.incrementAndGet();
		ConsumerType type = destination instanceof javax.jms.Queue ? ConsumerType.QueueReceiver : ConsumerType.TopicSubscriber;
		Subscription subscription = createSubscription(type, msgSelector, clientLimit, connection.getConnectionId(), 
				consumerId, jmsDestination.getDestinationId(), null, noLocal, connectionConsumer);

		if (type == ConsumerType.QueueReceiver) {
			consumer = new JMSQueueReceiver((JMSQueue)destination, consumerId, this, msgSelector);
		} else {
			consumer = new JMSTopicSubscriber((JMSTopic)destination, consumerId, this, msgSelector, noLocal);
		}

		consumers.put(consumerId, new WeakReference<JMSMessageConsumer>(consumer));
		// Put this destination into the cache as to prevent server round-trips
		DestinationInfo info = ((JMSDestination) destination).getDestinationInfo();
		JMSDestination.setDestination(info);

		try {
			facade.consumerCreate(subscription);
			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "Mapped ", consumer, "(destination : ", ((JMSDestination)destination).getDestinationId(),
						", selector: ", msgSelector, ") to consumer with id: ", consumerId, " under session with id: ", sessionId);
			}

		} catch (JMSException e) {
			consumers.remove(consumerId);
			Logging.exception(this, e,"Unable to create " +  consumer + "(destination id: " + ((JMSDestination)destination).getDestinationId()+
					", selector: " + msgSelector+ ") to consumer with id: " + consumerId + " under session with id: " + sessionId);
			throw e;
		}
		return consumer;
	}

	public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
		return createDurableSubscriber(topic, name, (String)null, false);
	}

	public TopicSubscriber createDurableSubscriber(Topic topic, String subscriptionName, String msgSelector, boolean noLocal) throws JMSException {
		return createDurableSubscriber(topic, subscriptionName, msgSelector, noLocal, false);
	}

	// TODO XXX Fix teh visibility of this method
	public synchronized TopicSubscriber createDurableSubscriber(Topic topic, String subscriptionName, String msgSelector, boolean noLocal, boolean connectionConsumer) throws JMSException {
		checkClosed();

		if (topic == null || !(topic instanceof JMSTopic)) {
			throw new InvalidDestinationException("Invalid destination");
		}
		JMSTopic jmsTopic = (JMSTopic) topic;		

		if (jmsTopic.getVPName() != null && !jmsTopic.getVPName().equals(connection.getVPName())) {
			throw new InvalidDestinationException("Illegal destination!");
		}

		if (subscriptionName == null || subscriptionName.equals("")) {
			throw new JMSException("Subscription name NULL or empty");
		}

		subscriptionName = createSubscriptionName(subscriptionName);

		if (subscribers.containsKey(subscriptionName)) {
			throw new JMSException("There is an active subscriber for this subscription!");
		}

		ServerFacade facade = connection.getServerFacade();
		JMSTopicSubscriber subscriber;
		int clientLimit = 102400 ; // TODO memoryManager.getChunkSize();
		long consumerId = ((long) sessionId << 32) | consumerCounter.incrementAndGet();
		Subscription subscription = createSubscription(ConsumerType.DurableSubscriber, msgSelector, clientLimit, 
				connection.getConnectionId(), consumerId, jmsTopic.getDestinationId(), subscriptionName, noLocal, connectionConsumer);



		subscriber = new JMSTopicSubscriber(jmsTopic, consumerId, this, msgSelector, noLocal);

		consumers.put(consumerId, new WeakReference<JMSMessageConsumer>(subscriber));
		subscribers.put(subscriptionName, consumerId);
		try {
			facade.consumerCreate(subscription);

			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "Mapped ", subscriber, "(destination id: ", ((JMSDestination)topic).getDestinationId(),
						", selector: ", msgSelector, ", subscription: ", subscriptionName, ") to consumer with id: ", consumerId, " under session with id: ", sessionId);
			}

		} catch (JMSException e) {
			consumers.remove(consumerId);
			subscribers.remove(subscriptionName);
			Logging.exception(this, e,"Unable to create " +  subscriber + "(destination id: " + ((JMSDestination)topic).getDestinationId()+
					", selector: " + msgSelector, "+ subscription: " + subscriptionName + ") to consumer with id: " + consumerId + " under session with id: "+ sessionId);
			throw e;
		}

		return subscriber;
	}

	public synchronized MapMessage createMapMessage() throws JMSException {
		checkClosed();

		JMSMapMessage msg = new JMSMapMessage();
		msg.setSession(this);

		return msg;
	}

	public synchronized Message createMessage() throws JMSException {
		checkClosed();

		Message msg = new JMSMessage();
		((JMSMessage)msg).setSession(this);

		return msg;
	}

	public ObjectMessage createObjectMessage() throws JMSException {
		checkClosed();

		JMSObjectMessage msg = new JMSObjectMessage();
		msg.setSession(this);

		return msg;
	}

	public synchronized ObjectMessage createObjectMessage(Serializable object) throws JMSException {
		checkClosed();

		JMSObjectMessage msg = new JMSObjectMessage();
		msg.setSession(this);
		msg.setObject(object);

		return msg;
	}

	public synchronized MessageProducer createProducer(Destination destination) throws JMSException {
		checkClosed();

		MessageProducer producer;

		if (destination == null) {
			if (this instanceof JMSQueueSession) {
				producer = new com.sap.jms.client.session.QueueSender(null, 0, this);
			} else if (this instanceof JMSTopicSession) {
				producer = new com.sap.jms.client.session.TopicPublisher(null, 0, this);
			} else {
				producer = new com.sap.jms.client.session.MessageProducer(null, 0, this);
			}

			return producer;
		}


		int destinationId  = ((JMSDestination) destination).getDestinationId();

		long producerID = connection.getServerFacade().producerCreate(connection.getConnectionId(), sessionId, destinationId);

		if (destination instanceof javax.jms.Queue) {
			producer = new com.sap.jms.client.session.QueueSender((javax.jms.Queue)destination, producerID, this);
		} else {
			producer = new com.sap.jms.client.session.TopicPublisher((javax.jms.Topic)destination, producerID, this);
		}

		producers.put(producerID, new WeakReference<MessageProducer>(producer));

		return producer;
	}

	public synchronized Queue createQueue(String name) throws JMSException {
		checkClosed();

		ServerFacade facade = connection.getServerFacade();
		DestinationInfo destination = facade.destinationCreate(connection.getConnectionId(), name, DestinationType.QUEUE, false);

		JMSDestination.setDestination(destination);
		JMSQueue queue = (JMSQueue) JMSDestination.resolveDestination(destination, connection);
		return queue;
	}

	public synchronized StreamMessage createStreamMessage() throws JMSException {
		checkClosed();

		JMSStreamMessage msg = new JMSStreamMessage();
		msg.setSession(this);

		return msg;
	}

	public synchronized TemporaryQueue createTemporaryQueue() throws JMSException {
		checkClosed();

		ServerFacade facade = connection.getServerFacade();
		long connId = connection.getConnectionId();
		DestinationInfo destination = facade.destinationCreate(connId, null, DestinationType.QUEUE, true);
		JMSDestination.setDestination(destination);
		JMSTemporaryQueue queue = (JMSTemporaryQueue) JMSDestination.resolveDestination(destination, connection);
		return queue;
	}

	public synchronized TemporaryTopic createTemporaryTopic() throws JMSException {
		checkClosed();

		ServerFacade facade = connection.getServerFacade();
		long connId = connection.getConnectionId();
		DestinationInfo destination = facade.destinationCreate(connId, null, DestinationType.TOPIC, true);
		JMSDestination.setDestination(destination);
		JMSTemporaryTopic topic = (JMSTemporaryTopic) JMSDestination.resolveDestination(destination, connection);
		return topic;
	}

	public synchronized TextMessage createTextMessage() throws JMSException {
		checkClosed();

		JMSTextMessage msg = new JMSTextMessage();
		msg.setSession(this);

		return msg;
	}

	public synchronized TextMessage createTextMessage(String text) throws JMSException {
		checkClosed();

		JMSTextMessage msg = new JMSTextMessage();
		msg.setSession(this);
		msg.setText(text);

		return msg;
	}

	public synchronized Topic createTopic(String name) throws JMSException {
		checkClosed();

		ServerFacade facade = connection.getServerFacade();
		long connId = connection.getConnectionId();
		DestinationInfo destination = facade.destinationCreate(connId, name, DestinationType.TOPIC, false);
		JMSDestination.setDestination(destination);
		JMSTopic topic = (JMSTopic) JMSDestination.resolveDestination(destination, connection);
		return topic;
	}

	public synchronized int getAcknowledgeMode() throws JMSException {
		checkClosed();

		return ackMode;
	}

	public synchronized MessageListener getMessageListener() throws JMSException {
		checkClosed();

		return dedicatedMessageListener.get();
	}

	public synchronized boolean getTransacted() throws JMSException {
		checkClosed();

		if (ackMode == SESSION_TRANSACTED) {
			return true;
		}

		return false;
	}

	public synchronized void recover() throws JMSException {
		checkClosed();

		connection.getServerFacade().sessionStop(connection.getConnectionId(), sessionId);

		Map<Long, List<JMSMessage>> pendingMessages = getPendingMessages();
		redeliver(pendingMessages);

		connection.getServerFacade().sessionStart(connection.getConnectionId(), sessionId);

	}

	public synchronized void rollback() throws JMSException {
		checkClosed();

		if (!getTransacted()) {
            throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "The method is called on a non-transacted session.");
		}

		if (optimizedMode.get() == true) {
            throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "The method is called on a transacted session in optimized mode.");
		}

		connection.getServerFacade().sessionStop(connection.getConnectionId(), sessionId);

		Map<Long, List<JMSMessage>> pendingMessages = getPendingMessages();
		redeliver(pendingMessages);

		didSendMessages.set(false);

		connection.getServerFacade().sessionStart(connection.getConnectionId(), sessionId);
	}

	public void run() {

		JMSMessage message;

		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "Will attempt delivery to consumers: ", scheduledConsumerIds, " Dedicated consumer is: ", dedicatedConsumer);
		}

		StringBuilder taskDescription = new StringBuilder();
		if (dedicatedConsumer != null) {
			taskDescription.append("Delivering to dedicated consumer with id: ");
			taskDescription.append(dedicatedConsumer.getConsumerID());
		} else {
			taskDescription.append("Delivering to consumers with id: ");
			taskDescription.append(scheduledConsumerIds);
		}

		ThreadWrapper.pushTask(taskDescription.toString(), ThreadWrapper.TS_PROCESSING);

		try {		
			state.set(SessionState.RUNNING);
			{
				MessageListener listener = dedicatedMessageListener.get();
				if ( listener != null && (message = removeDedicatedMessage()) != null) {
					// dedicated message listener
					try {
					    DSRInstrumentation.beforeOnMessage(message, connection.getVPName());
					    
						deliverMessage(dedicatedConsumer.getConsumerID(), listener, message);
					} catch (Exception e) {
						Logging.exception(this, e);						
					}
				} else {
					// asynchronous receivers in the consumers
					while (closing.get() == false) {
						try{
							Long consumerId = scheduledConsumerIds.poll();
							if (consumerId == null) {
								// This should not happen.  If it does however, isEmpty() at the end will stop the delivery.
								if (Logging.isWritable(this, Severity.WARNING)) {
									Logging.log(this, Severity.WARNING, "Got no consumer to deliver to. The list of scheduled consumers is: ", scheduledConsumerIds);
								}
							} else {

								WeakReference<JMSMessageConsumer> consumerRef = consumers.get(consumerId);
								if (consumerRef == null) {
									if (Logging.isWritable(this, Severity.WARNING)) {
										Logging.log(this, Severity.WARNING, "Reference to consumer with id: ", consumerId, "was null while trying to deliver a message");
									}
									continue;
								}

								JMSMessageConsumer consumer = consumerRef.get();
								if (consumer == null) {
									if (Logging.isWritable(this, Severity.WARNING)) {
										Logging.log(this, Severity.WARNING, "Consumer with id: ", consumerId, "was null while trying to deliver a message");
									}
									continue;
								}

								synchronized (consumer) {

									MessageListener messageListener = consumer.getMessageListener();
									message = consumer.fetchMessage(false);
									if (message != null) {
									    try {
									        DSRInstrumentation.beforeOnMessage(message, connection.getVPName());
									        
									        deliverMessage(consumerId, messageListener, message);
									    } finally {
									        DSRInstrumentation.afterOnMessage();
									    }
									}
								}
							}

						} catch (Exception e) {
							Logging.exception(this, e);
						}

						synchronized (scheduledConsumerIds) {
							if (scheduledConsumerIds.isEmpty()){
								state.set(SessionState.STOPPED);
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Logging.exception(this, e);
		} finally {

			// integation witm mmc.
			ThreadWrapper.popTask();

			state.set(SessionState.STOPPED);


			synchronized (state) {
				state.notifyAll();
			}
		}
	}

	// TODO package access
	// start delivery by MessageConsumer after message push
	public void scheduleForDelivery(Long consumerId) {
		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "Schedule delivery on behalf of consumer with id: ", consumerId);
		}


		if (state.compareAndSet(SessionState.STOPPED, SessionState.SCHEDULED)) {
			scheduledConsumerIds.offer(consumerId);
			taskManager.schedule(this);
			return;
		}

		synchronized (scheduledConsumerIds) {
			scheduledConsumerIds.offer(consumerId);

			if (state.get() != SessionState.RUNNING && state.get() != SessionState.SCHEDULED) {
				taskManager.schedule(this);				
			}
		}
	}

	private void deliverMessage(Long consumerId, MessageListener listener, JMSMessage message) throws JMSException {

        long expiration = message.getJMSExpiration();


		// it could expire during the delivery attempts
		long currentTime = System.currentTimeMillis();
		if (expiration != 0 && currentTime >= expiration) {
			scheduleForExpiration(message, consumerId);
			return;
		}

		JMSMessage client_msg = null;
		try {

			MessageRequest request = message.getMessagePacket();

			if (message instanceof JMSBytesMessage) {
				client_msg = new JMSBytesMessage(request);
			} else if (message instanceof JMSMapMessage) {
				client_msg = new JMSMapMessage(request);
			} else if (message instanceof JMSTextMessage) {
				client_msg = new JMSTextMessage(request);
			}else if (message instanceof JMSStreamMessage) {
				client_msg = new JMSStreamMessage(request);
				((JMSStreamMessage)client_msg).copyBodyStreamFrom((StreamMessage)message);
			}else if (message instanceof JMSObjectMessage) {
				client_msg = new JMSObjectMessage(request);
			}else if (message instanceof JMSMessage) {
				client_msg = new JMSMessage(request);
			} 

            client_msg.setJMSDestination(message.getJMSDestination());
            client_msg.copyPropertiesFrom(message);
            client_msg.flush();

			client_msg.setSession(this);
			client_msg.reset();
			client_msg.setWriteEnabledProperties(false);




			// schedule for acknowledge so rollback in onMessage will work
			ackHandler.scheduleForAcknowledge(consumerId, message);

            // integration with MMC. show which consumer we are delivering to.
            //ThreadWrapper.pushSubtask("Consumer id: " + consumerId + " msg id: " + message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER), ThreadWrapper.TS_PROCESSING);

            // TODO fix data pointers for byte messages. fix in stupid packet.
            client_msg.reset();
            listener.onMessage(client_msg);

            if ((ackMode == Session.AUTO_ACKNOWLEDGE || ackMode == Session.DUPS_OK_ACKNOWLEDGE) && !optimizedMode.get()) {

                Map<Long, List<JMSMessage>> pendingMessages = getPendingMessages();
                if (!pendingMessages.isEmpty()) {
                    if (Logging.isWritable(this, Severity.DEBUG)) {
                        Logging.log(this, Severity.DEBUG, "Will acknowledge: ", pendingMessages);
                    }
					Map<Long, List<Long>> tmp = convertPendingMessages(pendingMessages);

					connection.getServerFacade().sessionAcknowledge(connection.getConnectionId(), sessionId, tmp);
				} else {
					if (Logging.isWritable(this, Severity.DEBUG)) {
						Logging.log(this, Severity.DEBUG, "Nothing to acknowledge.");
					}
				}
			}

		} catch (Exception e) {
			Logging.exception(this, e);

            if (ackMode == Session.AUTO_ACKNOWLEDGE || ackMode == Session.DUPS_OK_ACKNOWLEDGE) {
                message.setJMSRedelivered(true); 
            }    
            
            connection.getServerFacade().sessionStop(connection.getConnectionId(), sessionId);
            Map<Long, List<JMSMessage>> pendingMessages = getPendingMessages();
            redeliver(pendingMessages);
            connection.getServerFacade().sessionStart(connection.getConnectionId(), sessionId);

        } finally {
            // integration with MMC. show which consumer we were delivering to.
            // ThreadWrapper.popSubtask();
        }
    }

	public synchronized void setMessageListener(MessageListener newListener) throws JMSException {
		checkClosed();

		if (newListener == null) {
			// TODO interrupt local delivery. send interrupt. possibly large delay time
		}

		dedicatedMessageListener.set(newListener);
	}

	public synchronized void unsubscribe(String subscriptionName) throws JMSException {
		checkClosed();

		subscriptionName = createSubscriptionName(subscriptionName);

		Long subscriberId = subscribers.remove(subscriptionName);
		if ( subscriberId != null) {

			WeakReference<JMSMessageConsumer> consumerRef = consumers.get(subscriberId);
			JMSMessageConsumer consumer = consumerRef.get();
			if (consumer != null) {
				consumer.stop();

				if (Logging.isWritable(this, Severity.DEBUG)) {
					Logging.log(this, Severity.DEBUG, "Will close consumer with id: ", subscriberId);
				}
				consumer.close();
			}
		}

		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "Will unsubscribe consumer with id: ", subscriberId);
		}
		connection.getServerFacade().subscriptionRemove(connection.getConnectionId(), subscriptionName);
	}


	protected void checkClosed() throws IllegalStateException {
		if (closed.get() == true) {
			throw new IllegalStateException("The Session is closed. id: " + sessionId);
		}
	}


	private Map<Long, List<JMSMessage>> getPendingMessages() {

		Map<Long, List<JMSMessage>> empty = new ConcurrentHashMap<Long, List<JMSMessage>>();
		Map<Long, List<JMSMessage>> pendingMessages = dlvrdMsgsPerConsumer.getAndSet(empty);

		return pendingMessages;
	}


	private String createSubscriptionName(String subscriptionName) {
		String clientId = null;

		try {
			clientId = connection.getClientID();
		} catch (JMSException e) { //$JL-EXC$

		}

		return ((clientId != null) ? clientId : "") + "_" + subscriptionName;
	}


	// connectionConsumer loads the dedicated message
	public void receiveMessage(JMSMessage message, JMSMessageConsumer consumer) throws JMSException {

		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "Got message with id: ", message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER), " for dedicated message listener via consumer with id: ", consumer.getConsumerID());
		}

		if (dedicatedMessageListener.get() == null) {
			if (Logging.isWritable(this, Severity.ERROR)) {
				Logging.log(this, Severity.ERROR, "Received message with id: ", message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER), " for null dedicated message listener via consumer with id: ", consumer.getConsumerID());
			}
			return;
		}

		dedicatedConsumer = consumer;
		addDedicatedMessage(message);

		// schedule the Session for execution
		if (state.compareAndSet(SessionState.STOPPED, SessionState.SCHEDULED)) {
			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "Scheduled message delivery for message with id: ", message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER), " received via consumer with id: ", consumer.getConsumerID());
			}
		}
	}

	public void onPacketReceived(Long consumerId, Packet packet) throws JMSException {

		JMSMessage message = null;

		switch (packet.getPacketType()) {
		case MessageRequest.JMS_BYTES_MESSAGE :
			message = new JMSBytesMessage((MessageRequest) packet);
			break;
		case MessageRequest.JMS_MAP_MESSAGE :
			message = new JMSMapMessage((MessageRequest) packet);
			break;
		case MessageRequest.JMS_TEXT_MESSAGE :
			message = new JMSTextMessage((MessageRequest) packet);
			break;
		case MessageRequest.JMS_STREAM_MESSAGE :
			message = new JMSStreamMessage((MessageRequest) packet);
			break;
		case MessageRequest.JMS_OBJECT_MESSAGE :
			message = new JMSObjectMessage((MessageRequest) packet);
			break;
		case MessageRequest.JMS_GENERIC_MESSAGE :
			message = new JMSMessage((MessageRequest) packet);
			break;
		default :
			javax.jms.JMSException jmse = new javax.jms.JMSException("Incorrect message received.");
		throw jmse;
		}

		message.setSession(this);
		message.reset();
		message.setWriteEnabledProperties(false);

		Long pCounter;
		try {
			pCounter = message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER);
			Logging.log(this, Severity.DEBUG,  "Got message with pCounter: ", pCounter, " for consumer with id: ", consumerId);
		} catch (Exception e) {
			Logging.log(this, Severity.DEBUG, "Got message without pCounter for consumer with id: ", consumerId, " pCounter defaults to 0L.");
			message.setLongProperty(JMSConstants.JMSX_SAP_PCOUNTER, 0L);
		}


		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "Available consumers : ", consumers);
		}
		WeakReference<JMSMessageConsumer> consumerRef = consumers.get(consumerId);
		if (consumerRef != null && consumerRef.get() != null) {
			JMSMessageConsumer consumer = consumerRef.get();

			// TODO The consumerId and DestinationId share the same position
			// in the MessageRequest packet. Duuh! Fix it and remove the
			// following row
			JMSDestination destination = consumer.getDestination();
			message.setJMSDestination(destination);

			consumer.push(message);

			return;
		}

		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "Available browsers : ", browsers);
		}
		WeakReference<JMSQueueBrowser> browserRef = browsers.get(consumerId);
		if (browserRef != null && browserRef.get() != null) {
			JMSQueueBrowser browser = browserRef.get();

			// TODO The consumerId and DestinationId share the same position
			// in the MessageRequest packet. Duuh! Fix it and remove the
			// following row
			JMSQueue queue = browser.getDestination();
			message.setJMSDestination(queue);
///////////////////
			browser.push(message);

			return;
		}

		// no consumer for this message
		throw new InvalidClientIDException("No consumer or browser with id: " + consumerId);
	}


/////////////////////////////////////////////////////////////////////////


	// TODO package access
	public void pauseDelivery() {

		for (WeakReference<JMSMessageConsumer> consumerRef: consumers.values()) {
			JMSMessageConsumer consumer = consumerRef.get();
			if (consumer != null) {
				consumer.stop();
			}
		}

		synchronized (state) {
			SessionState s = state.getAndSet(SessionState.STOPPED);
			if ( s == SessionState.RUNNING || s == SessionState.SCHEDULED) {

				if (s == SessionState.RUNNING) {
					try {
						state.wait();
					} catch (InterruptedException e) {
						Logging.exception(this, e);
					}
				}
			}
		}
	}

	public void removeConsumer(long consumerId) throws JMSException {

		// remove TimerTask for this consumer
		TimerTask task = delayedDeliveryTasks.get(consumerId);
		if (task != null) {
			task.cancel();
		}

		int numDeliveredMessages = 0;
		if (dlvrdMsgsPerConsumer.get().containsKey(consumerId)) {
			numDeliveredMessages = dlvrdMsgsPerConsumer.get().get(consumerId).size();
		}
		// interrupt message flow first
		connection.getServerFacade().consumerClose(connection.getConnectionId(), sessionId, consumerId, numDeliveredMessages);


		WeakReference<JMSMessageConsumer> consumerRef = consumers.get(consumerId);
		if (consumerRef != null && consumerRef.get() != null) {
			JMSMessageConsumer consumer = consumerRef.get();
			// make sure delivery to a consumer and its closing exclude each other
			synchronized (consumer) {
				consumers.remove(consumerId);
				while(scheduledConsumerIds.remove(consumerId));
			}
		} else {
			if (Logging.isWritable(this, Severity.WARNING)) {
				Logging.log(this, Severity.WARNING, "Consumer with id: ", consumerId, " or its reference was null while trying to remove it.");
			}
			while(scheduledConsumerIds.remove(consumerId));
		}

		for (String subscription: subscribers.keySet()) {
			Long subscriberId = subscribers.get(subscription);

			if (subscriberId == consumerId) {
				subscribers.remove(subscription);
				break;
			}
		}

		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "removed consumer with id: ", consumerId);
		}
	}

	public void removeProducer(long producerId) throws JMSException {
		connection.getServerFacade().producerClose(connection.getConnectionId(), sessionId, producerId);
		producers.remove(producerId);
		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "removed producer with id: ", producerId);
		}
	}

	public void removeBrowser(long browserId) throws JMSException {
		connection.getServerFacade().consumerClose(connection.getConnectionId(), sessionId, browserId, 0);
		browsers.remove(browserId);
		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "removed browser with id: ", browserId);
		}
	}


	public void sendMessage(JMSMessage message) throws JMSException {
		message.getMessagePacket().setConnectionID(connection.getConnectionId());
		message.getMessagePacket().setSessionID(sessionId);

		message.getMessagePacket().setOptimizedMode(optimizedMode.get());
		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "Will send message with id: ", message.getJMSMessageID(), " to destination with id: ", ((JMSDestination) message.getJMSDestination()).getDestinationId());
		}

		try {
			DSRInstrumentation.beforeSend(message, connection.getVPName());

			message.flush();
			connection.getServerFacade().sendMessage(message);
			didSendMessages.set(true);
		} finally {
			DSRInstrumentation.afterSend();
		}
	}

	public ServerFacade getServerFacade() {
		return connection.getServerFacade();
	}

	public Connection getConnection() {
		return connection;
	}

	public final String generateMessageID() {
		return MessageID.toString(msgIdBase, msgCounter.incrementAndGet());
	}

	public final byte[] generateMessageIDAsBytes() {
		return MessageID.toBytes(msgIdBase, msgCounter.incrementAndGet());
	}


	// TODO remove
	public JMSSession getBoundSession() {
		return null;
	}


	private JMSMessage removeDedicatedMessage() {
		return dedicatedMessage.getAndSet(null);
	}

	// when the messageListener is set
	private void addDedicatedMessage(JMSMessage message) {
		dedicatedMessage.compareAndSet(null, message);
	}

	public void start() {
		for (WeakReference<JMSMessageConsumer> consumerRef: consumers.values()) {
			JMSMessageConsumer consumer = consumerRef.get();
			if (consumer != null) {
				consumer.start();
			}
		}
	}

	public boolean supportsOptimization() {
		return connection.supportsOptimization();
	}

	public void setOptimizedMode(boolean isOptimized) {
		optimizedMode.set(isOptimized);
	}

	public void afterBegin() throws JMSException {
		connection.getServerFacade().sessionAfterBegin(connection.getConnectionId(), sessionId);
	}

	public void beforeCompletion() throws JMSException {
		checkClosed();

		if (optimizedMode.get() == false) {
            throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "The method is called on a transacted session in none optimized mode.");
		}

		Map<Long, List<JMSMessage>> pendingMessages = getPendingMessages();
		if (!pendingOptimizedMessages.compareAndSet(null, pendingMessages)) {
            throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "pendingOptimizedMessages was not null in beforeCompletion.");
		}
		beforeCompletionInvoked = true;
		Map<Long, List<Long>> tmp = convertPendingMessages(pendingMessages);

		connection.getServerFacade().sessionBeforeCompletion(connection.getConnectionId(), sessionId, tmp);
	}

	public void afterCompletion(int status) throws JMSException {
		checkClosed();

		if (optimizedMode.get() == false) {
            throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "The method is called on a transacted session in none optimized mode.");
		}

		Map<Long, List<JMSMessage>> pendingMessages = null;
		if (beforeCompletionInvoked) {
			pendingMessages = pendingOptimizedMessages.getAndSet(null);
		} else {
			pendingMessages = getPendingMessages();
		}
		beforeCompletionInvoked = false;
		if (pendingMessages == null) {
            throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "pendingOptimizedMessages was null in afterCompletion.");
		}

		Map<Long, List<Long>> tmp = convertPendingMessages(pendingMessages);

		connection.getServerFacade().sessionAfterCompletion(connection.getConnectionId(), sessionId, tmp, status);
	}

	protected void finalize() throws Throwable {//$JL-FINALIZE$
		try {
			if (closed.get() == true) {
				return;
			}
			AsyncCloser.getInstance().scheduleForClose(this);
		} catch (Exception e) {
			/* nothing here */
		} finally {
			super.finalize();
		}
	}


	public void scheduleForExpiration(JMSMessage message, long consumerId) throws JMSException {

		long expiration = message.getJMSExpiration();
		long currentTime = System.currentTimeMillis();

		if (Logging.isWritable(this, Severity.WARNING)) {
			Logging.log(this, Severity.WARNING, "Message with id: ", message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER), " for consumer with id: ", consumerId,
					" has expired at ", new Date(currentTime), " was due at: ", new Date(expiration));
		}

		// message has expired. Tell the server to remove it, and exit.
		Map<Long, List<JMSMessage>> messagesPerConsumer = new HashMap<Long, List<JMSMessage>>();
		List<JMSMessage> messages = new ArrayList<JMSMessage>();
		messages.add(message);
		messagesPerConsumer.put(consumerId, messages);

		Map<Long, List<Long>> tmp = JMSSession.convertPendingMessages(messagesPerConsumer);

		if (ackMode == Session.SESSION_TRANSACTED){
			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "Will commit expired messages: ", messagesPerConsumer);
			}
			connection.getServerFacade().sessionCommit(connection.getConnectionId(), sessionId, tmp);
		} else {
			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "Will acknowledge expired messages: ", messagesPerConsumer);
			}
			connection.getServerFacade().sessionAcknowledge(connection.getConnectionId(), sessionId, tmp);
		}
	}

	public int getSessionID() {
		return sessionId;
	}

	// Used _ONLY_ by the JMSConnectionConsumer to compencate for the fact
	// that the real commit/rollback, ack/recover is done by the MDB session
	public void connectionConsumerFakeAcknowledge(JMSMessage msg, long consumerId) throws JMSException {
		List<JMSMessage> messages = dlvrdMsgsPerConsumer.get().get(consumerId);
		if (messages != null) {
			messages.remove(msg);
		} else {
			if (Logging.isWritable(this, Severity.WARNING)) {
				Logging.log(this, Severity.WARNING, "Got null messages for consumer with id: ", consumerId);
			}
		}
	}


	public void setAckHandler(AckHandler ackHandler) {
		this.ackHandler = ackHandler;
	}

	public void resetAckHandler() {
		this.ackHandler = this;
	}

	AckHandler getAckHandler() {
		return ackHandler;
	}

	// TODO visibility
	public void sendFullRecover() throws JMSException {

//		TODO only used in XA offline rollback
//		connection.getServerFacade().sessionStop(connection.getConnectionId(), sessionId);


		if (getTransacted()) {
            throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "The method is called on a transacted session.");
		}

		Map<Long, List<JMSMessage>> pendingMessages = getPendingMessages();

		for (Long consumerId: consumers.keySet()) {
			// DCManager#recoverById needs the consumerIds
			// in order to call recoverRemainingMessages()
			// and trigger redelivery of the messages which were in
			// the client buffer but deleted by recover()
			if (!pendingMessages.keySet().contains(consumerId)) {
				pendingMessages.put(consumerId, new ArrayList<JMSMessage>());
			}

			while(scheduledConsumerIds.remove(consumerId));

			JMSMessageConsumer consumer = consumers.get(consumerId).get();
			if (consumer!= null) {
				consumer.clear();
			}
		}

		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "Will recover: ", pendingMessages);
		}

		Map<Long, List<Long>> tmp = convertPendingMessages(pendingMessages);

		connection.getServerFacade().sessionRecover(connection.getConnectionId(), sessionId, tmp);

//		TODO only used in XA offline rollback
//		connection.getServerFacade().sessionStart(connection.getConnectionId(), sessionId);
	}

	// TODO visibility
	public void acknowledge(Long consumerId, JMSMessage message) throws JMSException {

		if (ackMode != Session.AUTO_ACKNOWLEDGE && ackMode != Session.DUPS_OK_ACKNOWLEDGE) {
			ackHandler.scheduleForAcknowledge(consumerId, message);
			return;
		}

		// remove the message from delivered if it was scheduled
		if (dlvrdMsgsPerConsumer.get().containsKey(consumerId)) {
			List<JMSMessage> tmpList = dlvrdMsgsPerConsumer.get().get(consumerId);
			tmpList.remove(message);
		}

		List<JMSMessage> messages = new ArrayList<JMSMessage>();
		messages.add(message);
		Map<Long, List<JMSMessage>> messagesPerConsumer = new HashMap<Long, List<JMSMessage>>();
		messagesPerConsumer.put(consumerId, messages);

		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "Will acknowledge: ", messagesPerConsumer);
		}

		Map<Long, List<Long>> tmp = convertPendingMessages(messagesPerConsumer);
		connection.getServerFacade().sessionAcknowledge(connection.getConnectionId(), sessionId, tmp);
	}

	// TODO visibility
	public void scheduleForAcknowledge(Long consumerId, JMSMessage message) throws JMSException {

		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "Schedule for acknowledge message with id: ", message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER), " from consumer with id: ", consumerId);
		}

		if (dlvrdMsgsPerConsumer.get().containsKey(consumerId)) {
			List<JMSMessage> tmpList = dlvrdMsgsPerConsumer.get().get(consumerId);
			tmpList.add(message);

		} else {
			List<JMSMessage> messages = new ArrayList<JMSMessage>();
			messages.add(message);
			dlvrdMsgsPerConsumer.get().put(consumerId, messages);
		}
	}


	// TODO visibility
	public void clearConsumerBuffers() {

		for (Long consumerId: consumers.keySet()) {

			while(scheduledConsumerIds.remove(consumerId));

			JMSMessageConsumer consumer = consumers.get(consumerId).get();
			if (consumer!= null) {
				consumer.clear();
			}
		}
	}
//	TODO make JMSXASession and JMSSession friends
	public void clearConsumerBuffer(long consumerId) {

		while(scheduledConsumerIds.remove(consumerId));

		JMSMessageConsumer consumer = consumers.get(consumerId).get();
		if (consumer!= null) {
			consumer.clear();
		}
	}

	public boolean existsConsumer(Long consumerId) {
		boolean result = consumers.containsKey(consumerId) || dedicatedConsumer.consumerId == consumerId;
		return result;
	}

	public void redeliver(Map<Long, List<JMSMessage>> messagesForRedeliver) throws JMSException {

		Map<Long, List<JMSMessage>> messagesWithoutConsumers = new HashMap<Long, List<JMSMessage>>();

		Logging.log(this, Severity.DEBUG, "Will redeliver: ",messagesForRedeliver);

		long consumerWillRedeliver = 0L;

		for (Long consumerId: messagesForRedeliver.keySet()) {

			// stop the consumer before deleting it from the scheduledConsumerIds.
			JMSMessageConsumer consumer;
			if (dedicatedConsumer != null && consumerId == dedicatedConsumer.getConsumerID()) {
				consumer = dedicatedConsumer;
			} else {
				WeakReference<JMSMessageConsumer> consumerRef = consumers.get(consumerId);
				if (consumerRef == null) {
					messagesWithoutConsumers.put(consumerId, messagesForRedeliver.get(consumerId));
					messagesForRedeliver.remove(consumerId);
					continue;
				}

				consumer = consumerRef.get();
				if (consumer == null) {
					messagesWithoutConsumers.put(consumerId, messagesForRedeliver.get(consumerId));
					messagesForRedeliver.remove(consumerId);
					continue;
				}
			}

			consumer.pause();
		}


		ackHandler.redistributeMessagesWithoutConsumer(messagesWithoutConsumers);

		// a dead consumer. Do full recover
		if (!messagesWithoutConsumers.isEmpty()) {
			ackHandler.redistributeMessagesWithoutConsumer(messagesForRedeliver);
			for (Long consumerId: consumers.keySet()) {

				// stop the consumer before deleting it from the scheduledConsumerIds.
				JMSMessageConsumer consumer;
				if (dedicatedConsumer != null && consumerId == dedicatedConsumer.getConsumerID()) {
					consumer = dedicatedConsumer;
				} else {
					WeakReference<JMSMessageConsumer> consumerRef = consumers.get(consumerId);
					if (consumerRef == null) {
						continue;
					}

					consumer = consumerRef.get();
					if (consumer == null) {
						continue;
					}
				}

				while(scheduledConsumerIds.remove(consumerId));

				consumer.clear();
				consumer.resume();
			}
			return;
		}
		Map<Long, List<Long>> tmp2 = JMSSession.convertPendingMessages(messagesForRedeliver);

		Map<Long, Set<DelayedDeliveryData>> delayIntervals = connection.getServerFacade().messageDeliveryFailed(connection.getConnectionId(), sessionId, tmp2); 
		for (Long consumerId: delayIntervals.keySet()) {

			// stop the consumer before deleting it from the scheduledConsumerIds.
			JMSMessageConsumer consumer;
			if (dedicatedConsumer != null && consumerId == dedicatedConsumer.getConsumerID()) {
				consumer = dedicatedConsumer;
			} else {
				WeakReference<JMSMessageConsumer> consumerRef = consumers.get(consumerId);
				if (consumerRef == null) {
					continue;
				}

				consumer = consumerRef.get();
				if (consumer == null) {
					continue;
				}
			}


			JMSMessage message = null;
			List<JMSMessage> messages = messagesForRedeliver.get(consumerId);
			for (int i = messages.size() - 1; i >= 0; i --) {
//				for (JMSMessage tmp :  messages) {
				JMSMessage tmp = messages.get(i);
				long pCounter = 0;
				long delayInterval = 0;
				for (DelayedDeliveryData deliveryInfo: delayIntervals.get(consumerId)) {
					pCounter = deliveryInfo.pCounter;
					delayInterval = deliveryInfo.delayInterval;
					if (pCounter == tmp.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER)) {
						message = tmp;
						break;
					}
				}


				if (message == null) {
					if (Logging.isWritable(this, Severity.ERROR)) {
						Logging.log(this, Severity.ERROR, "Unknown failed message with pCounter: ",pCounter,". Skip it.");
					}
					continue;
				}

				// message has become dead
				if (delayInterval == 0) {
					messages.remove(message);
					continue;
				}

				// message will be redelivered
				while(scheduledConsumerIds.remove(consumerId));

				message.setJMSRedelivered(true);

				// TODO insert at the beginning of the queue
				Logging.log(this, Severity.DEBUG, "Insert message: ", message, " for redelivery to consumer with id: ", consumerId);
				
				consumer.push2(message);

				// this is an async listener
				if (consumer.getMessageListener() != null) {
					consumerWillRedeliver = delayInterval;
				}
			}

			if (consumerWillRedeliver > 0) {
				TimerTask task =  new DelayedDeliveryTask(consumer);
				delayedDeliveryTasks.put(consumerId, task);
				if (timer == null) {
					timer = new Timer();
				}
				timer.schedule(task, consumerWillRedeliver);
				consumerWillRedeliver = 0L;
			} else {
				// handles sync receivers and any messages left in the consumer queue
				consumer.resume();
			}
		}
	}

	public void redistributeMessagesWithoutConsumer(Map<Long, List<JMSMessage>> messagesWithoutConsumers) throws JMSException {
		Map<Long, List<Long>> tmp = convertPendingMessages(messagesWithoutConsumers);

		if (ackMode == Session.SESSION_TRANSACTED) {
			connection.getServerFacade().sessionRollback(connection.getConnectionId(), sessionId, tmp);
		} else {
			connection.getServerFacade().sessionRecover(connection.getConnectionId(), sessionId, tmp);
		}

	}


	public void redeliverAllUnconsumedMessages() throws JMSException {

		Map<Long, List<JMSMessage>> pendingMessages = getPendingMessages();

		for (Long consumerId: consumers.keySet()) {
			// DCManager#recoverById needs the consumerIds
			// in order to call recoverRemainingMessages()
			// and trigger redelivery of the messages which were in
			// the client buffer but deleted by recover()
			if (!pendingMessages.keySet().contains(consumerId)) {
				pendingMessages.put(consumerId, new ArrayList<JMSMessage>());
			}

			while(scheduledConsumerIds.remove(consumerId));

			JMSMessageConsumer consumer = consumers.get(consumerId).get();
			if (consumer!= null) {
				consumer.clear();
			}
		}

		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "Retriger delivery of messages: ", pendingMessages);
		}

		Map<Long, List<Long>> tmp = convertPendingMessages(pendingMessages);

		if (ackMode == Session.SESSION_TRANSACTED) {
			connection.getServerFacade().sessionRollback(connection.getConnectionId(), sessionId, tmp);
		} else {
			connection.getServerFacade().sessionRecover(connection.getConnectionId(), sessionId, tmp);
		}
	}

	public void execute() {
		run();
	}


	public String getName() {
		return "JMS Session " + sessionId;
	}

	// TODO XXX !!! Fix me !
	private static Subscription createSubscription(ConsumerType type, String selector, int clientLimit, 
			long connectionId, long clientConsumerId, int destinationId, String durableSubscriberName, boolean noLocal, boolean connectionConsumer) {
		Subscription subscription = new Subscription();
		subscription.setConsumerType(type);
		subscription.setSelector(selector);
		subscription.setClientLimit(clientLimit);
		subscription.setConnectionId(connectionId);
		subscription.setClientConsumerId(clientConsumerId);
		subscription.setDestinationId(destinationId);
		subscription.setDurableSubscriberName(durableSubscriberName);
		subscription.setNoLocal(noLocal);
		subscription.setConnectionConsumer(connectionConsumer);
		return subscription;
	}

	public static Map<Long, List<Long>> convertPendingMessages(Map<Long, List<JMSMessage>> msgsPerConsumerIds) throws JMSException {

		Map<Long, List<Long>> tmp = new HashMap<Long, List<Long>>();
		for (Long consumerId: msgsPerConsumerIds.keySet()) {
			List<JMSMessage> messages = msgsPerConsumerIds.get(consumerId);
			List<Long> pCounters = new ArrayList<Long>();
			for (JMSMessage message: messages) {

				pCounters.add(message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER));
			}
			tmp.put(consumerId, pCounters);
		}

		return tmp;
	}
}
