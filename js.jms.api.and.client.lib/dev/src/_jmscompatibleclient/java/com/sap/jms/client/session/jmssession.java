package com.sap.jms.client.session;

import com.sap.jms.JMSConstants;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.sap.jms.util.compat.concurrent.ConcurrentHashMap;
import com.sap.jms.util.compat.concurrent.ConcurrentLinkedQueue;
import com.sap.jms.util.compat.concurrent.atomic.AtomicBoolean;
import com.sap.jms.util.compat.concurrent.atomic.AtomicLong;
import com.sap.jms.util.compat.concurrent.atomic.AtomicReference;

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

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.JMSConstants;
import com.sap.jms.client.connection.AsyncCloser;
import com.sap.jms.client.connection.Connection;
import com.sap.jms.client.connection.RemoteConnection;
import com.sap.jms.client.connection.ServerFacade;
import com.sap.jms.client.connection.ServerFacade.DestinationDescriptor;
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
import com.sap.jms.protocol.ConfigurationPropertiesPacket;
import com.sap.jms.protocol.Packet;
import com.sap.jms.protocol.message.MessageRequest;
import com.sap.jms.util.MessageID;
import com.sap.jms.util.logging.LogService;
import com.sap.jms.util.logging.LogServiceImpl;
import com.sap.jms.client.connection.Closeable;
import com.sap.jms.interfaces.DSRInstrumentation;

public class JMSSession implements javax.jms.Session, Closeable, AckHandler {

	public final static class SessionType {
	    public static final SessionType QUEUE_SESSION = new SessionType();
	    public static final SessionType TOPIC_SESSION = new SessionType();
	    public static final SessionType GENERIC_SESSION = new SessionType();
	    public static final SessionType XA_QUEUE_SESSION = new SessionType();
	    public static final SessionType XA_TOPIC_SESSION = new SessionType();
	    public static final SessionType XA_GENERIC_SESSION = new SessionType();

	    private SessionType() {
	    }                            
	}
	
	private AtomicBoolean closed;
	private AtomicBoolean closing;

	private Map/*<Long, WeakReference<MessageProducer>>*/ producers;
	private Map/*<Long, WeakReference<JMSMessageConsumer>>*/ consumers;
	private Map/*<Long, WeakReference<JMSQueueBrowser>>*/ browsers;
	private Map/*<String, Long>*/ subscribers;

	// make sure onPacketReceived can only be called by the Connection
	// after the consumer data structures are completely initialised
	// with the new consumer
	private Object createConsumerMonitor;

	private AtomicReference/*<Map<Long, Set<Long>>>*/ dlvrdMsgsPerConsumer;

	private List/*<Long>*/ scheduledConsumerIds;

	public final static class SessionState {
	    public static final SessionState STOPPED = new SessionState();
	    public static final SessionState SCHEDULED = new SessionState();
	    public static final SessionState RUNNING = new SessionState();

	    private SessionState() {
	    }                            
	}

	private AtomicReference/*<SessionState>*/ state;

	private AtomicReference/*<MessageListener>*/ dedicatedMessageListener;
	private AtomicReference/*<JMSMessage>*/ dedicatedMessage;
	private Long dedicatedConsumerId;
	private DeliveryConfiguration dedicatedDeliveryConfig;

	private AtomicLong msgCounter;
	private long msgIdBase;

	private com.sap.jms.client.connection.Connection connection;
	private int sessionId;
	private int ackMode;
	private long runInvocationCount;
	private ThreadSystem threadSystem;

	private AtomicBoolean optimizedMode;
	private boolean beforeCompletionInvoked = false;
	private AtomicReference/*<Map<Long, Set<Long>>>*/ pendingOptimizedMessages;

	private LogService log = LogServiceImpl.getLogService(LogServiceImpl.CLIENT_LOCATION);
	private String LNAME;
	private AckHandler ackHandler;


	public JMSSession(int sessionId, long msgIdBase, int ackMode, com.sap.jms.client.connection.Connection connection, ThreadSystem threadSystem) {

		this.sessionId = sessionId;
		this.msgIdBase = msgIdBase;
		this.ackMode = ackMode;
		this.connection = connection;
		this.threadSystem = threadSystem;

		closed = new AtomicBoolean(false);
		closing = new AtomicBoolean(false);
		producers = new ConcurrentHashMap/*<Long, WeakReference<MessageProducer>>*/();
		consumers = new ConcurrentHashMap/*<Long, WeakReference<JMSMessageConsumer>>*/();
		browsers = new ConcurrentHashMap/*<Long, WeakReference<JMSQueueBrowser>>*/();
		subscribers = new ConcurrentHashMap/*<String, Long>*/();
		dlvrdMsgsPerConsumer = new AtomicReference/*<Map<Long, Set<Long>>>*/();
		dlvrdMsgsPerConsumer.set(new ConcurrentHashMap/*<Long, Set<Long>>*/());
		scheduledConsumerIds = new ArrayList/*<Long>*/();
		state = new AtomicReference/*<SessionState>*/(SessionState.STOPPED);
		dedicatedMessageListener = new AtomicReference/*<MessageListener>*/(null);
		dedicatedMessage = new  AtomicReference/*<JMSMessage>*/(null);
		msgCounter = new AtomicLong(0);
		createConsumerMonitor = new Object();
		dedicatedDeliveryConfig = new DeliveryConfiguration();
		optimizedMode = new AtomicBoolean(false);
		pendingOptimizedMessages = new AtomicReference/*<Map<Long, Set<Long>>>*/();
		runInvocationCount = 0;
		ackHandler = this;

		LNAME = getClass().getName();
	}


	// TODO make default / move to jms package; called only by Message#acknowledge
	public synchronized void acknowledge() throws JMSException {
		checkClosed();

		if (ackMode != Session.CLIENT_ACKNOWLEDGE) {
			return;
		}

		Map/*<Long, Set<Long>> */pendingMessages = getPendingMessages();

		log.debug(LNAME, "Will acknowledge: " + pendingMessages);

		connection.getServerFacade().sessionAcknowledge(sessionId, pendingMessages);
	}

	public void close() throws JMSException {

		synchronized (closed) {
			if (closed.get() == true) {
				return;
			}

			log.debug(LNAME, "Will stop session with id: " + sessionId);
			getServerFacade().sessionStop(connection.getConnectionID(), sessionId);


			if (!supportsOptimization()) {
				if (getTransacted()) {
					ackHandler.sendRollback();
				} else if (ackMode == Session.CLIENT_ACKNOWLEDGE) {
					ackHandler.sendRecover();
				}
			} else {
				// TODO what to do ?
			}

			log.debug(LNAME, "Will close consumers: " + consumers);

		for (Iterator i = consumers.keySet().iterator(); i.hasNext(); ) {
			Long id = (Long) i.next();
				WeakReference/*<JMSMessageConsumer>*/ c = (WeakReference)consumers.get(id);
				if (c != null && c.get() != null) {
					((JMSMessageConsumer)c.get()).close();
				}
			}

			consumers.clear();

			synchronized (state) {
				closing.set(true);
				SessionState s = (SessionState)state.getAndSet(SessionState.STOPPED);
				if ( s == SessionState.RUNNING || s == SessionState.SCHEDULED) {

					if (s == SessionState.RUNNING) {
						try {
							state.wait();
						} catch (InterruptedException e) {
							log.exception(LNAME, e);
						}
					}
				}
			}

			log.debug(LNAME, "Will close producers: " + producers);

		for (Iterator i = producers.keySet().iterator(); i.hasNext(); ) {
			Long id = (Long) i.next();
				WeakReference/*<MessageProducer>*/ p = (WeakReference)producers.get(id);
				if (p != null && p.get() != null) {
					((MessageProducer)p.get()).close();
				}
			}

			producers.clear();

			log.debug(LNAME, "Will close browsers: " + browsers);

		for (Iterator i = browsers.keySet().iterator(); i.hasNext(); ) {
			Long id = (Long) i.next();			
				WeakReference/*<JMSQueueBrowser>*/ b = (WeakReference)browsers.get(id);
				if (b != null && b.get() != null) {
					((JMSQueueBrowser)b.get()).close();
				}
			}

			browsers.clear();

			connection.closeSession(sessionId);

			closed.set(true);
		}
	}

	public synchronized void commit() throws JMSException {
		checkClosed();

		if (!getTransacted()) {
			throw new IllegalStateException("The method is called on a non-transacted session.");
		}

		if (optimizedMode.get() == true) {
			throw new IllegalStateException("The method is called on a transacted session in optimized mode.");
		}

		Map/*<Long, Set<Long>>*/ pendingMessages = getPendingMessages();

		log.debug(LNAME, "Will commit: " + pendingMessages);

		connection.getServerFacade().sessionCommit(sessionId, pendingMessages);
	}

	public QueueBrowser createBrowser(Queue queue) throws JMSException {
		return createBrowser(queue,null);
	}

	public synchronized QueueBrowser createBrowser(Queue queue, String msgSelector) throws JMSException {
		checkClosed();

		if (queue == null || !(queue instanceof Queue)) {
			throw new InvalidDestinationException("Illegal destination!");
		}

		String instanceName = ((JMSQueue)queue).getInstanceName();

		if (instanceName != null && !instanceName.equals(connection.getServerInstance())) {
			throw new InvalidDestinationException("Illegal destination!");
		}

		JMSQueueBrowser browser = null;

		ServerFacade facade = connection.getServerFacade();
		String qName = queue.getQueueName();
		// TODO memoryManager.getChunkSize();
		long browserId;

		synchronized (createConsumerMonitor) {
			browserId = facade.queueBrowserCreate(sessionId, qName, msgSelector, 102400);
			browser = new JMSQueueBrowser((JMSQueue)queue, browserId, this, msgSelector);
			browsers.put(new Long(browserId), new WeakReference/*<JMSQueueBrowser>*/(browser));
		}

		log.debug(LNAME, "Mapped " + browser + "(queue id: " + ((JMSDestination)queue).getDestinationID() +
				", selector: "+ msgSelector + ")"+ " to browser with id: " + browserId + " under session with id: " + sessionId);

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

	public synchronized MessageConsumer createConsumer(Destination destination, String msgSelector, boolean noLocal) throws JMSException {
		checkClosed();

		if (destination == null) {
			throw new InvalidDestinationException("null destination.");
		}

		if (!(destination instanceof JMSDestination)) {
			throw new InvalidDestinationException(destination.toString());
		}

		String instName = ((JMSDestination)destination).getInstanceName();
		if (instName != null && !instName.equals(connection.getServerInstance())) {
			throw new InvalidDestinationException(destination.toString());
		}

		JMSMessageConsumer consumer;

		long consumerId;
		ServerFacade facade = connection.getServerFacade();
		int clientLimit = 102400 ; // TODO memoryManager.getChunkSize();

		String name;
		byte destinationType;


		if (destination instanceof javax.jms.Queue) {
			destinationType = JMSConstants.DESTINATION_QUEUE;
			javax.jms.Queue queue = (javax.jms.Queue)destination;
			name = queue.getQueueName();
		} else {
			destinationType = JMSConstants.DESTINATION_TOPIC;
			javax.jms.Topic topic = (javax.jms.Topic) destination;
			name = topic.getTopicName();
		}

		synchronized (createConsumerMonitor) {
			consumerId = facade.consumerCreate(sessionId, null, name, destinationType, noLocal, msgSelector, clientLimit);

			if (destinationType == JMSConstants.DESTINATION_QUEUE) {
				consumer = new JMSQueueReceiver((JMSQueue)destination, consumerId, this, msgSelector);
			} else {
				consumer = new JMSTopicSubscriber((JMSTopic)destination, consumerId, this, msgSelector, noLocal);
			}

			consumers.put(new Long(consumerId), new WeakReference/*<JMSMessageConsumer>*/(consumer));
			// Put this destination into the cache as to prevent server round-trips
			JMSDestination.setIDNameMapping(((JMSDestination) destination).getDestinationID(),((JMSDestination) destination).getName());

		}

		log.debug(LNAME, "Mapped " + consumer + "(destination id: " + ((JMSDestination)destination).getDestinationID() +
				", selector: "+ msgSelector + ")"+ " to consumer with id: " + consumerId + " under session with id: " + sessionId);

		return consumer;
	}

	public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
		return createDurableSubscriber(topic, name, (String)null, false);
	}

	public synchronized TopicSubscriber createDurableSubscriber(Topic topic, String subscriptionName, String msgSelector, boolean noLocal) throws JMSException {
		checkClosed();

		if (topic == null || !(topic instanceof JMSTopic)) {
			throw new InvalidDestinationException("Invalid destination");
		}

		String instName = ((JMSTopic) topic).getInstanceName();
		if (instName != null && !instName.equals(connection.getServerInstance())) {
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
		long consumerId;
		synchronized (createConsumerMonitor) {
			consumerId = facade.consumerCreate(sessionId, subscriptionName, topic.getTopicName(), JMSConstants.DESTINATION_TOPIC, noLocal, msgSelector, clientLimit);
			subscriber = new JMSTopicSubscriber((JMSTopic)topic, consumerId, this, msgSelector, noLocal);

			consumers.put(new Long(consumerId), new WeakReference/*<JMSMessageConsumer>*/(subscriber));
			subscribers.put(subscriptionName, new Long(consumerId));
		}

		log.debug(LNAME, "Mapped " + subscriber + "(destination id: " + ((JMSDestination)topic).getDestinationID() +
				", selector: "+ msgSelector + ", subscription: " + subscriptionName + ")"+ " to consumer with id: " + consumerId + " under session with id: " + sessionId);


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


		byte destinationType;
		String destinationName;

		if (destination instanceof Queue) {
			destinationType = JMSConstants.DESTINATION_QUEUE;
			destinationName = ((Queue)destination).getQueueName();
		} else if (destination instanceof Topic) {
			destinationType = JMSConstants.DESTINATION_TOPIC;
			destinationName = ((Topic)destination).getTopicName();
		} else {
			throw new InvalidDestinationException(destination.toString());
		}

		long producerID = connection.getServerFacade().producerCreate(sessionId, destinationName, destinationType);

		if (destination instanceof javax.jms.Queue) {
			producer = new com.sap.jms.client.session.QueueSender((javax.jms.Queue)destination, producerID, this);
		} else {
			producer = new com.sap.jms.client.session.TopicPublisher((javax.jms.Topic)destination, producerID, this);
		}

		producers.put(new Long(producerID), new WeakReference/*<MessageProducer>*/(producer));

		return producer;
	}

	public synchronized Queue createQueue(String name) throws JMSException {
		checkClosed();

		ServerFacade facade = connection.getServerFacade();
		DestinationDescriptor dd;
		long connectionId = connection.getConnectionID();
		dd = facade.destinationCreate(connectionId, name, JMSConstants.DESTINATION_QUEUE, false);

		JMSDestination.setIDNameMapping(dd.getDestinationId(), name);
		JMSQueue queue = new JMSQueue(name, dd.getDestinationId());
		queue.setInstanceName(connection.getServerInstance());

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
		DestinationDescriptor dd;
		long connId = connection.getConnectionID();
		dd = facade.destinationCreate(connId, null, JMSConstants.DESTINATION_QUEUE, true);
		JMSDestination.setIDNameMapping(dd.getDestinationId(), dd.getDestinationName());
		JMSTemporaryQueue dest = new JMSTemporaryQueue(dd.getDestinationName(), dd.getDestinationId(), connection);
		dest.setInstanceName(connection.getServerInstance());

		return dest;
	}

	public synchronized TemporaryTopic createTemporaryTopic() throws JMSException {
		checkClosed();

		ServerFacade facade = connection.getServerFacade();
		long connId = connection.getConnectionID();
		DestinationDescriptor dd;
		dd = facade.destinationCreate(connId, null, JMSConstants.DESTINATION_TOPIC, true);
		JMSDestination.setIDNameMapping(dd.getDestinationId(), dd.getDestinationName());
		JMSTemporaryTopic dest = new JMSTemporaryTopic(dd.getDestinationName(), dd.getDestinationId(), connection);
		dest.setInstanceName(connection.getServerInstance());

		return dest;
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
		long connId = connection.getConnectionID();
		DestinationDescriptor dd;
		dd = facade.destinationCreate(connId, name, JMSConstants.DESTINATION_TOPIC, false);
		JMSDestination.setIDNameMapping(dd.getDestinationId(), dd.getDestinationName());
		JMSTopic dest = new JMSTopic(name, dd.getDestinationId());
		dest.setInstanceName(connection.getServerInstance());

		return dest;
	}

	public synchronized int getAcknowledgeMode() throws JMSException {
		checkClosed();

		return ackMode;
	}

	public synchronized MessageListener getMessageListener() throws JMSException {
		checkClosed();

		return (MessageListener)dedicatedMessageListener.get();
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

		connection.getServerFacade().sessionStop(connection.getConnectionID(), sessionId);

		ackHandler.sendRecover();

		connection.getServerFacade().sessionStart(connection.getConnectionID(), sessionId);

	}

	public synchronized void rollback() throws JMSException {
		checkClosed();

		if (!getTransacted()) {
			throw new IllegalStateException("The method is called on a non-transacted session.");
		}

		if (optimizedMode.get() == true) {
			throw new IllegalStateException("The method is called on a transacted session in optimized mode.");
		}

		connection.getServerFacade().sessionStop(connection.getConnectionID(), sessionId);

		ackHandler.sendRollback();

		connection.getServerFacade().sessionStart(connection.getConnectionID(), sessionId);
	}

	public void run() {

		JMSMessage message;

		log.debug(LNAME, "Will attempt delivery to consumers: " + scheduledConsumerIds + 
				" Dedicated consumer is: " + dedicatedConsumerId);

		// integration with MMC. shows the task.
		//Thread.currentThread().setName("JMS Session " + sessionId);

		StringBuffer taskDescription = new StringBuffer();
		if (dedicatedConsumerId != null) {
			taskDescription.append("Delivering to dedicated consumer with id: ");
			taskDescription.append(dedicatedConsumerId);
		} else {
			taskDescription.append("Delivering to consumers with id: ");
			taskDescription.append(scheduledConsumerIds);
		}

//		ThreadWrapper.pushTask(taskDescription.toString(), ThreadWrapper.TS_PROCESSING);

		try {		
			state.set(SessionState.RUNNING);
			{
				MessageListener listener = (MessageListener)dedicatedMessageListener.get();
				if ( listener != null && (message = removeDedicatedMessage()) != null) {
					// dedicated message listener
					try {
						deliverMessage(dedicatedConsumerId, listener, message, dedicatedDeliveryConfig);
					} catch (Exception e) {
						log.exception(LNAME, e);						
					}
				} else {
					// asynchronous receivers in the consumers
					while (closing.get() == false) {
						try{
							Long consumerId = (Long) (scheduledConsumerIds.isEmpty() ? null : scheduledConsumerIds.remove(0));

							if (consumerId == null) {
								// This should not happen.  If it does however, isEmpty() at the end will stop the delivery.
								log.warningTrace(LNAME, "Got no consumer to deliver to. The list of scheduled consumers is: " + scheduledConsumerIds);
							} else {

								WeakReference/*<JMSMessageConsumer>*/ consumerRef = (WeakReference)consumers.get(consumerId);
								if (consumerRef == null) {
									log.warningTrace(LNAME, "Reference to consumer with id: " + consumerId + "was null while trying to deliver a message");
									continue;
								}

								JMSMessageConsumer consumer = (JMSMessageConsumer)consumerRef.get();
								if (consumer == null) {
									log.warningTrace(LNAME, "Consumer with id: " + consumerId + "was null while trying to deliver a message");
									continue;
								}

								synchronized (consumer) {
									DeliveryConfiguration deliveryConfig = consumer.getDeliveryConfiguarion();

									MessageListener messageListener = consumer.getMessageListener();
									message = consumer.fetchMessage(false);
									if (message != null) {
										deliverMessage(consumerId, messageListener, message, deliveryConfig);
									}
								}
							}

						} catch (Exception e) {
							log.exception(LNAME, e);
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
			log.exception(LNAME, e);
		} finally {

			// integation witm mmc.
//			ThreadWrapper.popTask();

			state.set(SessionState.STOPPED);


			synchronized (state) {
				state.notifyAll();
			}
		}
	}

	// TODO package access
	// start delivery by MessageConsumer after message push
	public void scheduleForDelivery(Long consumerId) {
		log.debug(LNAME, "Schedule delivery on behalf of consumer with id: " + consumerId);


		if (state.compareAndSet(SessionState.STOPPED, SessionState.SCHEDULED)) {
			scheduledConsumerIds.add(consumerId);
			threadSystem.startThread(this, /*"", "JMS Session " + sessionId + " (" + runInvocationCount++ + ")",*/ false );

			return;
		}

		synchronized (scheduledConsumerIds) {
			scheduledConsumerIds.add(consumerId);

			if (state.get() != SessionState.RUNNING && state.get() != SessionState.SCHEDULED) {
				threadSystem.startThread(this, /*"", "JMS Session " + sessionId + " (" + runInvocationCount++ + ")",*/ false );
			}
		}


	}

	private void deliverMessage(Long consumerId, MessageListener listener, JMSMessage message, DeliveryConfiguration deliveryConfiguration) throws JMSException {

		long expiration = message.getJMSExpiration();
		long pCounter = message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER);

		for (int i = 0; i < deliveryConfiguration.getDeliveryAttempts() ; i++) {
			// it could expire during the delivery attempts
			long currentTime = System.currentTimeMillis();
			if (expiration != 0 && currentTime >= expiration) {
				scheduleForExpiration(message, consumerId.longValue());
				return;
			}

			try {
				// schedule for acknowledge so rollback in onMessage will work
				ackHandler.scheduleForAcknowledge(consumerId, message);

				// integration with MMC. show which consumer we are delivering to.
//				ThreadWrapper.pushSubtask("Consumer id: " + consumerId + " msg id: " + message.getJMSMessageID(), ThreadWrapper.TS_PROCESSING);

				DSRInstrumentation.beforeOnMessage(message, connection.getServerInstance());

				listener.onMessage(message);

				DSRInstrumentation.afterOnMessage();

				if ((ackMode == Session.AUTO_ACKNOWLEDGE || ackMode == Session.DUPS_OK_ACKNOWLEDGE) && !optimizedMode.get()) {

					Map/*<Long, Set<Long>>*/ pendingMessages = getPendingMessages();
					if (!pendingMessages.isEmpty()) {
						log.debug(LNAME, "Will acknowledge: " + pendingMessages);
						connection.getServerFacade().sessionAcknowledge(sessionId, pendingMessages);
					} else {
						log.debug(LNAME, "Nothing to acknowledge.");
					}
				}
				break;
			} catch (Exception e) {
				log.exception(LNAME, e);

				// is this the last delivery retry ?
				if (i >= deliveryConfiguration.getDeliveryAttempts() - 1) {

					// add message to list of delivered but unacknowledged messages
					scheduleForAcknowledge(consumerId, message);

					if (ackMode == Session.SESSION_TRANSACTED || optimizedMode.get()) {
						log.warningTrace(LNAME, "The delivery attempts for message with pCounter: " + pCounter + " were exhausted. Will rollback!");
						rollback();
					} else {
						log.warningTrace(LNAME, "The delivery attempts for message with pCounter: " + pCounter + " were exhausted. Will recover!");
						recover();
					}
					return;
				}

				if (ackMode == Session.AUTO_ACKNOWLEDGE || ackMode == Session.DUPS_OK_ACKNOWLEDGE) {
					message.setJMSRedelivered(true);
				}

				try {
					// TODO note current thread for interrupt by close()
					Thread.sleep(deliveryConfiguration.getDeliveryDelay());
				} catch (InterruptedException ie) {
					log.exception(LNAME, e);
				}
			} finally {
				// integration with MMC. show which consumer we were delivering to.
//				ThreadWrapper.popSubtask();
			}
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

		log.debug(LNAME, "unsubscribe " + subscriptionName + " from " + subscribers);
		Long subscriberId = (Long)subscribers.remove(subscriptionName);
		log.debug(LNAME, "after unsubscribe " + subscriptionName + " left " + subscribers);
		if ( subscriberId != null) {

			WeakReference/*<JMSMessageConsumer>*/ consumerRef = (WeakReference)consumers.get(subscriberId);
			if (consumerRef != null && consumerRef.get() != null) {
				JMSMessageConsumer consumer = (JMSMessageConsumer)consumerRef.get();

				consumer.stop();

				log.debug(LNAME, "Will close consumer with id: " + subscriberId);
				consumer.close();
			}
		}

		log.debug(LNAME, "Will unsubscribe consumer with id: " + subscriberId);
		connection.getServerFacade().subscriptionRemove(subscriptionName);
	}


	protected void checkClosed() throws IllegalStateException {
		if (closed.get() == true) {
			throw new IllegalStateException("The Session is closed. id: " + sessionId);
		}
	}


	private Map/*<Long, Set<Long>>*/ getPendingMessages() {

		Map/*<Long, Set<Long>>*/ empty = new ConcurrentHashMap/*<Long, Set<Long>>*/();
		Map/*<Long, Set<Long>>*/ pendingMessages = (Map)dlvrdMsgsPerConsumer.getAndSet(empty);

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
	public void receiveMessage(JMSMessage message, long consumerId) throws JMSException {

		Long pCounter = new Long(0L);
		try {
			pCounter = new Long(message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER));
			log.debug(LNAME, "Got message with pCounter: " + pCounter + " for dedicated message listener via consumer with id: " + consumerId);
		} catch (Exception e) {
			log.debug(LNAME, "Got message without pCounter for dedicated message listener via consumer with id: " + consumerId + " pCounter defaults to 0L.");
			message.setLongProperty(JMSConstants.JMSX_SAP_PCOUNTER, 0L);
		}

		if (dedicatedMessageListener.get() == null) {
			log.errorTrace(LNAME, "Received message with pCounter: " + pCounter + "for null dedicated message listener via consumer with id: " + consumerId);
			return;
		}

		dedicatedConsumerId = new Long(consumerId);
		addDedicatedMessage(message);

		// schedule the Session for execution
		if (state.compareAndSet(SessionState.STOPPED, SessionState.SCHEDULED)) {
			log.debug(LNAME, "Scheduling message delivery for message with pCounter: " + pCounter + " received via consumer with id: ", consumerId);
		}
	}

	public void onPacketReceived(Long consumerId, Packet packet) throws JMSException {

		if (packet.getPacketType() == MessageRequest.CONFIGURATION_PROPERTIES_PACKET ) {

			ConfigurationPropertiesPacket cfg = (ConfigurationPropertiesPacket)packet;
			HashMap/*<String, Long>*/ delayIntervals = (HashMap/*<String, Long>*/)cfg.getProperties();

			for (Iterator i = delayIntervals.keySet().iterator(); i.hasNext(); ) {
				String consumerIdString = (String) i.next();
				long tmpConsumerId = Long.parseLong(consumerIdString.substring(consumerIdString.indexOf(':') + 1, consumerIdString.length()));
				long delayInterval = ((Long)delayIntervals.get(consumerIdString)).longValue();

				if (dedicatedConsumerId != null && tmpConsumerId == dedicatedConsumerId.longValue()) {
					// TODO Hmmm. not sent with the packet.
					dedicatedDeliveryConfig.setDeliveryAttempts(JMSConstants.DEFAULT_MAX_DELIVERY_ATTEMPTS);
					dedicatedDeliveryConfig.setDeliveryDelay(delayInterval);
					continue;
				}

				if (!consumers.containsKey(new Long(tmpConsumerId))) {
					log.warningTrace(LNAME, "No consumer with id: " + tmpConsumerId);
					continue;
				}

				log.debug(LNAME, "Got configuration packet (delay: " + delayInterval + ") for consumer with id: " + tmpConsumerId);

				JMSMessageConsumer consumer = (JMSMessageConsumer)((WeakReference)consumers.get(new Long(tmpConsumerId))).get();
				if (consumer == null) {
					log.warningTrace(LNAME, "Null reference for consumer with id: " + tmpConsumerId);
					continue;
				}

				DeliveryConfiguration deliveryConfig = consumer.getDeliveryConfiguarion();
				deliveryConfig.setDeliveryAttempts(JMSConstants.DEFAULT_MAX_DELIVERY_ATTEMPTS);
				deliveryConfig.setDeliveryDelay(delayInterval);
			}

			return;
		}

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
			pCounter = new Long(message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER));
			log.debug(LNAME, "Got message with pCounter: " + pCounter + " for consumer with id: " + consumerId);
		} catch (Exception e) {
			log.debug(LNAME, "Got message without pCounter for consumer with id: " + consumerId + " pCounter defaults to 0L.");
			message.setLongProperty(JMSConstants.JMSX_SAP_PCOUNTER, 0L);
		}


		synchronized (createConsumerMonitor) {


			log.debug(LNAME, "Available consumers : " + consumers);
			WeakReference/*<JMSMessageConsumer>*/ consumerRef = (WeakReference)consumers.get(consumerId);
			if (consumerRef != null && consumerRef.get() != null) {
				JMSMessageConsumer consumer = (JMSMessageConsumer)consumerRef.get();

				// TODO The consumerId and DestinationId share the same position
				// in the MessageRequest packet. Duuh! Fix it and remove the
				// following row
				JMSDestination destination = consumer.getDestination();
				message.setJMSDestination(destination);

				consumer.push(message);

				return;
			}

			log.debug(LNAME, "Available browsers : " + browsers);
			WeakReference/*<JMSQueueBrowser>*/ browserRef = (WeakReference)browsers.get(consumerId);
			if (browserRef != null && browserRef.get() != null) {
				JMSQueueBrowser browser = (JMSQueueBrowser)browserRef.get();

				// TODO The consumerId and DestinationId share the same position
				// in the MessageRequest packet. Duuh! Fix it and remove the
				// following row
				JMSQueue queue = browser.getDestination();
				message.setJMSDestination(queue);
///////////////////
				browser.push(message);

				return;
			}
		}

		// no consumer for this message
		throw new InvalidClientIDException("No consumer or browser with id: " + consumerId);
	}


/////////////////////////////////////////////////////////////////////////


	// TODO package access
	public void pauseDelivery() {

		for (Iterator i = consumers.values().iterator(); i.hasNext(); ) {		
			WeakReference/*<JMSMessageConsumer>*/ consumerRef = (WeakReference) i.next();
			JMSMessageConsumer consumer = (JMSMessageConsumer)consumerRef.get();
			if (consumer != null) {
				consumer.stop();
			}
		}

		synchronized (state) {
			SessionState s = (SessionState)state.getAndSet(SessionState.STOPPED);
			if ( s == SessionState.RUNNING || s == SessionState.SCHEDULED) {

				if (s == SessionState.RUNNING) {
					try {
						state.wait();
					} catch (InterruptedException e) {
						log.exception(LNAME, e);
					}
				}
			}
		}
	}

	public void removeConsumer(long consumerId) throws JMSException {

		int numDeliveredMessages = 0;
		if (((Map)dlvrdMsgsPerConsumer.get()).containsKey(new Long(consumerId))) {
			numDeliveredMessages = ((Set)((Map)dlvrdMsgsPerConsumer.get()).get(new Long(consumerId))).size();
		}
		// interrupt message flow first
		connection.getServerFacade().consumerClose(sessionId, consumerId, numDeliveredMessages);

		
		WeakReference/*<JMSMessageConsumer>*/ consumerRef = (WeakReference)consumers.get(new Long(consumerId));
		if (consumerRef != null && consumerRef.get() != null) {
			JMSMessageConsumer consumer = (JMSMessageConsumer)consumerRef.get();
			// make sure delivery to a consumer and its closing exclude each other
			synchronized (consumer) {
				consumers.remove(new Long(consumerId));
				while(scheduledConsumerIds.remove(new Long(consumerId)));
			}
		} else {
			log.warningTrace(LNAME, "Consumer with id: " + consumerId + " or its reference was null while trying to remove it.");
			while(scheduledConsumerIds.remove(new Long(consumerId)));
		}

		for (Iterator i = subscribers.keySet().iterator(); i.hasNext(); ) {
			String subscription = (String) i.next();
			Long subscriberId = (Long)subscribers.get(subscription);

			if (subscriberId.longValue() == consumerId) {
				subscribers.remove(subscription);
				break;
			}
		}

		log.debug(LNAME, "removed consumer with id: " + consumerId);
	}

	public void removeProducer(long producerId) throws JMSException {
		connection.getServerFacade().producerClose(sessionId, producerId);
		producers.remove(new Long(producerId));
		log.debug(LNAME, "removed producer with id: " + producerId);
	}

	public void removeBrowser(long browserId) throws JMSException {
		connection.getServerFacade().queueBrowserClose(sessionId, browserId);
		browsers.remove(new Long(browserId));
		log.debug(LNAME, "removed browser with id: " + browserId);
	}


	public void sendMessage(JMSMessage message) throws JMSException {
		message.getMessagePacket().setConnectionID(connection.getConnectionID());
		message.getMessagePacket().setSessionID(sessionId);

		message.getMessagePacket().setOptimizedMode(optimizedMode.get());
		log.debug(LNAME, "Will send message with id: "+ message.getJMSMessageID() + " to destination with id: " + ((JMSDestination)message.getJMSDestination()).getDestinationID());

		DSRInstrumentation.beforeSend(message, connection.getServerInstance());

		connection.getServerFacade().sendMessage(message);

		DSRInstrumentation.afterSend();
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
		return (JMSMessage)dedicatedMessage.getAndSet(null);
	}

	// when the messageListener is set
	private void addDedicatedMessage(JMSMessage message) {
		dedicatedMessage.compareAndSet(null, message);
	}

	public void start() {
		for (Iterator i = consumers.values().iterator(); i.hasNext(); ) {		
			WeakReference/*<JMSMessageConsumer>*/ consumerRef = (WeakReference) i.next();
			JMSMessageConsumer consumer = (JMSMessageConsumer)consumerRef.get();
			if (consumer != null) {
				consumer.start();
			}
		}
	}

/////////////////////////
	public boolean supportsOptimization() {
		if (connection instanceof RemoteConnection) {
			return ((RemoteConnection)connection).supportsOptimization();
		}

		return false;
	}

	public void setOptimizedMode(boolean isOptimized) {
		optimizedMode.set(isOptimized);
	}

	public void afterBegin() throws JMSException {
		connection.getServerFacade().sessionAfterBegin(sessionId);
	}

	public void beforeCompletion() throws JMSException {
		checkClosed();

		if (optimizedMode.get() == false) {
			throw new IllegalStateException("The method is called on a transacted session in none optimized mode.");
		}

		Map/*<Long, Set<Long>>*/ pendingMessages = getPendingMessages();
		if (!pendingOptimizedMessages.compareAndSet(null, pendingMessages)) {
			throw new IllegalStateException("pendingOptimizedMessages was not null in beforeCompletion.");
		}
		beforeCompletionInvoked = true;
		connection.getServerFacade().sessionBeforeCompletion(sessionId, pendingMessages);
	}

	public void afterCompletion(int status) throws JMSException {
		checkClosed();

		if (optimizedMode.get() == false) {
			throw new IllegalStateException("The method is called on a transacted session in none optimized mode.");
		}

		Map/*<Long, Set<Long>>*/ pendingMessages = null;
		if (beforeCompletionInvoked) {
			pendingMessages = (Map)pendingOptimizedMessages.getAndSet(null);
		} else {
			pendingMessages = getPendingMessages();
		}
		beforeCompletionInvoked = false;
		if (pendingMessages == null) {
			throw new IllegalStateException("pendingOptimizedMessages was null in afterCompletion.");
		}

		connection.getServerFacade().sessionAfterCompletion(sessionId, pendingMessages, status);
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

		long pCounter = message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER);
		long expiration = message.getJMSExpiration();
		long currentTime = System.currentTimeMillis();

		log.warningTrace(LNAME, "Message with pCounter: " + pCounter + " for consumer with id: " + consumerId + " has expired at " + new Date(currentTime) + " was due at: " + new Date(expiration));

		// message has expired. Tell the server to remove it, and exit.
		Set/*<Long>*/ pCounters = new HashSet/*<Long>*/();
		pCounters.add(new Long(pCounter));
		Map/*<Long, Set<Long>>*/ messagesPerConsumer = new HashMap/*<Long, Set<Long>>*/();
		messagesPerConsumer.put(new Long(consumerId), pCounters);

		if (ackMode == Session.SESSION_TRANSACTED){
			log.debug(LNAME, "Will commit expired messages: " + messagesPerConsumer);
			connection.getServerFacade().sessionCommit(sessionId, messagesPerConsumer);
		} else {
			log.debug(LNAME, "Will acknowledge expired messages: " + messagesPerConsumer);
			connection.getServerFacade().sessionAcknowledge(sessionId, messagesPerConsumer);
		}
	}

	public int getSessionID() {
		return sessionId;
	}

	// Used _ONLY_ by the JMSConnectionConsumer to compencate for the fact
	// that the real commit/rollback, ack/recover is done by the MDB session
	public void connectionConsumerFakeAcknowledge(JMSMessage msg, long consumerId) throws JMSException {
		long pcounter = msg.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER);
		Set/*<Long>*/ messages = (Set)((Map)dlvrdMsgsPerConsumer.get()).get(new Long(consumerId));
		if (messages != null) {
			messages.remove(new Long(pcounter));
		} else {
			log.warningTrace(LNAME, "Got null messages for consumer with id: " + consumerId);
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
	public void sendRecover() throws JMSException {

		if (getTransacted()) {
			throw new IllegalStateException("The method is called on a transacted session.");
		}

		Map/*<Long, Set<Long>>*/ pendingMessages = getPendingMessages();

		for (Iterator i = consumers.keySet().iterator(); i.hasNext(); ) {
			Long consumerId = (Long) i.next();
			// DCManager#recoverById needs the consumerIds
			// in order to call recoverRemainingMessages()
			// and trigger redelivery of the messages which were in
			// the client buffer but deleted by recover()
			if (!pendingMessages.keySet().contains(consumerId)) {
				pendingMessages.put(consumerId, new HashSet/*<Long>*/());
			}

			while(scheduledConsumerIds.remove(consumerId));

			JMSMessageConsumer consumer = (JMSMessageConsumer)((WeakReference)consumers.get(consumerId)).get();
			if (consumer!= null) {
				consumer.clear();
			}
		}

		log.debug(LNAME, "Will recover: " + pendingMessages);

		connection.getServerFacade().sessionRecover(sessionId, pendingMessages);
	}


	// TODO visibility
	public void sendRollback()throws JMSException {

		Map/*<Long, Set<Long>>*/ pendingMessages = getPendingMessages();

		for (Iterator i = consumers.keySet().iterator(); i.hasNext(); ) {
			Long consumerId = (Long) i.next();
			// DCManager#rollbackById needs the consumerIds
			// in order to call rollbackRemainingMessages()
			// and trigger redelivery of the messages which were in
			// the client buffer but deleted by rollback()
			if (!pendingMessages.keySet().contains(consumerId)) {
				pendingMessages.put(consumerId, new HashSet/*<Long>*/());
			}

			while(scheduledConsumerIds.remove(consumerId));

			JMSMessageConsumer consumer = (JMSMessageConsumer)((WeakReference)consumers.get(consumerId)).get();
			if (consumer!= null) {
				consumer.clear();
			}
		}

		log.debug(LNAME, "Will rollback: " + pendingMessages);

		connection.getServerFacade().sessionRollback(sessionId, pendingMessages);
	}


	// TODO visibility
	public void acknowledge(Long consumerId, JMSMessage message) throws JMSException {

		Long pCounter = new Long(message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER));

		if (ackMode != Session.AUTO_ACKNOWLEDGE && ackMode != Session.DUPS_OK_ACKNOWLEDGE) {
			ackHandler.scheduleForAcknowledge(consumerId, message);
			return;
		}

		// remove the message from delivered if it was scheduled
		if (((Map)dlvrdMsgsPerConsumer.get()).containsKey(consumerId)) {
			Set/*<Long>*/ tmpList = (Set)((Map)dlvrdMsgsPerConsumer.get()).get(consumerId);
			tmpList.remove(pCounter);
		}

		Set/*<Long>*/ pCounters = new HashSet/*<Long>*/();
		pCounters.add(pCounter);
		Map/*<Long, Set<Long>> */messagesPerConsumer = new HashMap/*<Long, Set<Long>>*/();
		messagesPerConsumer.put(consumerId, pCounters);

		log.debug(LNAME, "Will acknowledge: " + messagesPerConsumer);
		connection.getServerFacade().sessionAcknowledge(sessionId, messagesPerConsumer);
	}

	// TODO visibility
	public void scheduleForAcknowledge(Long consumerId, JMSMessage message) throws JMSException {
		Long pCounter = new Long(message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER));

		log.debug(LNAME, "Schedule for acknowledge message with pCounter: " + pCounter + " from consumer with id: " + consumerId);

		if (((Map)dlvrdMsgsPerConsumer.get()).containsKey(consumerId)) {
			Set/*<Long>*/ tmpList = (Set)((Map)dlvrdMsgsPerConsumer.get()).get(consumerId);
			tmpList.add(pCounter);

		} else {
			Set/*<Long>*/ pCounters = new HashSet/*<Long>*/();
			pCounters.add(pCounter);
			((Map)dlvrdMsgsPerConsumer.get()).put(consumerId, pCounters);
		}
	}


	// TODO visibility
	public void clearConsumerBuffers() {

		for (Iterator i = consumers.keySet().iterator(); i.hasNext(); ) {
			Long consumerId = (Long) i.next();
			
			while(scheduledConsumerIds.remove(consumerId));

			JMSMessageConsumer consumer = (JMSMessageConsumer)((WeakReference)consumers.get(consumerId)).get();
			if (consumer!= null) {
				consumer.clear();
			}
		}
	}


}
