/**
 * MessageConsumer.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.session;

import com.sap.jms.util.compat.concurrent.atomic.AtomicReference;

import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import com.sap.jms.client.connection.AsyncCloser;
import com.sap.jms.client.connection.Closeable;
import com.sap.jms.client.destination.JMSDestination;
import com.sap.jms.client.message.JMSMessage;
import com.sap.jms.client.session.JMSSession.SessionState;
import com.sap.jms.interfaces.DSRInstrumentation;
import com.sap.jms.util.JMSLinkedBlockingQueue;
import com.sap.jms.util.logging.LogService;
import com.sap.jms.util.logging.LogServiceImpl;


public class JMSMessageConsumer implements MessageConsumer, Closeable {

	protected JMSSession session; // the session within which the consumer operates
	protected long consumerId;
	protected JMSDestination destination;
	protected String selector;


	private JMSLinkedBlockingQueue/*<JMSMessage>*/ messages;
	private MessageListener messageListener;
	private AtomicReference/*<Thread>*/ currentThread;
	public final static class WorkingState {
	    public static final WorkingState STATE_STOPPED = new WorkingState();
	    public static final WorkingState STATE_CLOSED = new WorkingState();
	    public static final WorkingState STATE_RUNNING = new WorkingState();

	    private WorkingState() {
	    }                            
	}
	private AtomicReference/*<WorkingState>*/ state;
	private DeliveryConfiguration deliveryConfiguarion;

	private LogService log = LogServiceImpl.getLogService(LogServiceImpl.CLIENT_LOCATION);
	private String LNAME;

	public JMSMessageConsumer(JMSDestination destination, long consumerId, JMSSession session, String selector) throws JMSException {
		this.destination = destination;
		this.consumerId = consumerId;
		this.session = session;
		this.selector = selector;

		messages = new JMSLinkedBlockingQueue/*<JMSMessage>*/();
		currentThread = new AtomicReference/*<Thread>*/(null);
		state = new AtomicReference/*<WorkingState>*/(WorkingState.STATE_RUNNING);
		deliveryConfiguarion = new DeliveryConfiguration();

		LNAME = getClass().getName();
	}

	public void close() throws JMSException {
		synchronized (state) {
			if (state.get() != WorkingState.STATE_CLOSED) {
				
				log.debug(LNAME, "Will close consumer with id: " + consumerId);
				
				state.set(WorkingState.STATE_CLOSED);

				Thread t = (Thread)currentThread.getAndSet(null);
				if (t != null) {
					t.interrupt();
				}
				session.removeConsumer(consumerId);
				clear();
				synchronized (state) {
					state.notifyAll();
				}
			}
		}
	}

	public MessageListener getMessageListener() throws JMSException {
// XXX		checkClosed();
		return messageListener;
	}

	public String getMessageSelector() throws JMSException {
		checkClosed();
		return selector;
	}

	public Message receive() throws JMSException {
		return receive(Long.MAX_VALUE);
	}

	public Message receive(long timeout) throws JMSException {
		checkClosed();

		if (!currentThread.compareAndSet(null, Thread.currentThread())) {
			throw new IllegalStateException("javax.jms.MessageConsumer is not thread safe by specification. Check your application!");
		}

		JMSMessage message = null;

		long start = System.currentTimeMillis();
		long delta = 0;

		if (!session.getConnection().isStarted()) {
			log.warning(LNAME, "The connection of consumer with id: " + consumerId + " is stopped. receive() will not work. Check your application!");
		}
		
		try {
			while (state.get() == WorkingState.STATE_RUNNING) {
				for (delta = 0; delta < timeout || timeout == 0; delta = System.currentTimeMillis() - start) {
					message = (JMSMessage)messages.poll(timeout);
					if (message == null) {
						log.warningTrace(LNAME, "Timed out in receive for consumer with id: " + consumerId);
						return null;
					}

					if (!isResizeMessage(message)) {
						if (isExpiredMessage(message)) {
							session.scheduleForExpiration(message, consumerId);
							continue;
						}

						session.getAckHandler().acknowledge(new Long(consumerId), message);

						DSRInstrumentation.onReceive(message, session.getConnection().getServerInstance());
						// TODO fix data pointers for byte messages. fix in stupid packet.
						message.reset();

						return message;
					}

					int nextMessageSize = message.getIntProperty("JMS_SAP_NextSize");
					log.debug(LNAME, "Server requested " + nextMessageSize + " more bytes client buffer space, for consumer with id: " + consumerId );
					session.getConnection().getServerFacade().startMessageDelivery(destination.getDestinationID(), consumerId, nextMessageSize, 0 /* parameter not used by DC */);
				}
			}
		} catch (InterruptedException e) {
			Thread.interrupted();
			if (state.get() == WorkingState.STATE_CLOSED) {
				return null;
			}

			if (state.get() == WorkingState.STATE_STOPPED) {
				// TODO caclulat the delta
				long p = timeout - delta;
				if (p <= 0) {
					return null;
				}
				try {
					synchronized (state) {
						state.wait(p);
					}
				} catch (InterruptedException i) {
					// 3. InterruptedException -> not reacheable
					Thread.interrupted();
					// TODO not reachable. log error
					log.errorTrace(LNAME, "Should not be reachable.");
				}
				if (state.get() != WorkingState.STATE_RUNNING) {
					// 1. Timeout (p) exeeded
					log.warningTrace(LNAME, "Timed out in receive for consumer with id: " + consumerId);
					return null;
				}
			}
		} finally {
			currentThread.set(null);
		}
		return null;
	}


	public Message receiveNoWait() throws JMSException {
		checkClosed();

		if (!session.getConnection().isStarted()) {
			log.warning(LNAME, "The connection of consumer with id: " + consumerId + " is stopped. receiveNoWait() will not work. Check your application!");
		}

		JMSMessage message = fetchMessage(true);
		if (message != null) {
			session.getAckHandler().acknowledge(new Long(consumerId), message);
		}

		DSRInstrumentation.onReceive(message, session.getConnection().getServerInstance());

		return message;
	}


	public JMSMessage fetchMessage(boolean force) throws JMSException {

		JMSMessage message = null;

		while (state.get() == WorkingState.STATE_RUNNING) {
			message = (JMSMessage)messages.peek();
			if (message == null) {
				if (force == false) {
					return null;
				}

				// a message must be in the buffer at this point if there was one on the server
				session.getConnection().getServerFacade().consumerRefresh(session.getSessionID(), consumerId, destination.getDestinationID());
				if (messages.peek() == null) {
					// no messages for this consumer
					return null;
				}
				continue;
			}

			// there is a message in the queue at this point
			try {
				message = (JMSMessage)messages.poll(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				// must not happen
				log.exception(LNAME, e);
			}

			if (!isResizeMessage(message)) {
				if (isExpiredMessage(message)) {
					session.scheduleForExpiration(message, consumerId);
					continue;
				}

				// TODO fix data pointers for byte messages. fix in stupid packet.
				message.reset();

				return message;
			}

			int nextMessageSize = message.getIntProperty("JMS_SAP_NextSize");
			log.debug(LNAME, "Server requested " + nextMessageSize + " more bytes client buffer space, for consumer with id: " + consumerId );
			session.getConnection().getServerFacade().startMessageDelivery(destination.getDestinationID(), consumerId, nextMessageSize, 0 /* parameter not used by DC */);
		}

		return null;
	}


	public void setMessageListener(MessageListener messageListener) throws JMSException {
		checkClosed();

		this.messageListener = messageListener;
	}


	void push(JMSMessage msg) throws JMSException {
		try {
			messages.put(msg);
			if (messageListener != null) {
				// start the Session
				session.scheduleForDelivery(new Long(consumerId));
			}
		} catch (InterruptedException e) {
			JMSException jmse = new JMSException(e.getMessage());
	                jmse.setLinkedException(e);
	                throw jmse;
		}
	}

	protected void checkClosed() throws IllegalStateException {
		if (state.get() == WorkingState.STATE_CLOSED) {
			throw new IllegalStateException("Consumer " + consumerId + " is closed.");
		}
	}

	protected void finalize() throws Throwable {//$JL-FINALIZE$
		try {
			if (state.get() == WorkingState.STATE_CLOSED) {
				return;
			}

			AsyncCloser.getInstance().scheduleForClose(this);

		} catch (Exception e) {
			log.exception(LNAME, e);
		} finally {
			super.finalize();
		}
	}

	private boolean isResizeMessage(JMSMessage message) throws JMSException {
		if (message.getBooleanProperty("JMS_SAP_EndMessage") == true){
			return true;
		}

		return false;
	}
	private boolean isExpiredMessage(JMSMessage message) throws JMSException {
		long expiration = message.getJMSExpiration();
		long currentTime = System.currentTimeMillis();
		if (expiration != 0 && currentTime >= expiration) {
			return true;
		}

		return false;
	}

	// TODO used only by the COnnectionConsumer
	public long getConsumerID() {
		return consumerId;
	}

	void start() {
		state.set(WorkingState.STATE_RUNNING);
		synchronized (state) {
			state.notifyAll();
		}
	}

	void stop() {
		state.set(WorkingState.STATE_STOPPED);
		Thread t = (Thread)currentThread.getAndSet(null);
		if (t != null) {
			t.interrupt();
		}

		synchronized (state) {
			state.notifyAll();
		}
	}

	void clear() {
		messages.clear();
	}

	DeliveryConfiguration getDeliveryConfiguarion() {
		return deliveryConfiguarion;
	}

	JMSDestination getDestination() {
		return destination;
	}
}