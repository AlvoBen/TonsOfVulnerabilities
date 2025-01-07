/**
 * MessageConsumer.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.session;

import java.util.concurrent.atomic.AtomicReference;

import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.StreamMessage;

import com.sap.jms.client.connection.AsyncCloser;
import com.sap.jms.client.connection.Closeable;
import com.sap.jms.client.destination.JMSDestination;
import com.sap.jms.client.message.JMSBytesMessage;
import com.sap.jms.client.message.JMSMapMessage;
import com.sap.jms.client.message.JMSMessage;
import com.sap.jms.client.message.JMSObjectMessage;
import com.sap.jms.client.message.JMSStreamMessage;
import com.sap.jms.client.message.JMSTextMessage;
import com.sap.jms.protocol.MessageRequest;
import com.sap.jms.util.JMSLinkedBlockingQueue;
import com.sap.jms.util.Logging;
import com.sap.jms.util.LogUtil;
import com.sap.tc.logging.Severity;


public class JMSMessageConsumer implements MessageConsumer, Closeable {

	protected JMSSession session; // the session within which the consumer operates
	protected long consumerId;
	protected JMSDestination destination;
	protected String selector;


	private JMSLinkedBlockingQueue<JMSMessage> messages;
	private MessageListener messageListener;
	private AtomicReference<Thread> currentThread;
	private enum WorkingState {STATE_STOPPED, STATE_CLOSED, STATE_RUNNING, STATE_PAUSED};
	private AtomicReference<WorkingState> state;

	public JMSMessageConsumer(JMSDestination destination, long consumerId, JMSSession session, String selector) throws JMSException {
		this.destination = destination;
		this.consumerId = consumerId;
		this.session = session;
		this.selector = selector;

		messages = new JMSLinkedBlockingQueue<JMSMessage>();
		currentThread = new AtomicReference<Thread>(null);
		state = new AtomicReference<WorkingState>(WorkingState.STATE_RUNNING);
	}

	public void close() throws JMSException {
		synchronized (state) {
			if (state.get() != WorkingState.STATE_CLOSED) {
				
				if (Logging.isWritable(this, Severity.DEBUG)) {
                    Logging.log(this, Severity.DEBUG, "Will close consumer with id:", consumerId);
                }
				
				state.set(WorkingState.STATE_CLOSED);

				Thread t = currentThread.getAndSet(null);
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
		checkClosed();
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
	        throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "javax.jms.MessageConsumer is not thread safe by specification. Check your application!");
		}

		JMSMessage message = null;

		long start = System.currentTimeMillis();
		long delta = 0;

		if (!session.getConnection().isStarted()) {
			if (Logging.isCustomerWritable(Severity.WARNING)) {
                Logging.customerLog(this, Severity.WARNING, "The connection of consumer with id:", consumerId, "is stopped. receive() will not work. Check your application!");
            }
		}
		
		try {
			while (state.get() == WorkingState.STATE_RUNNING) {
				for (delta = 0; delta < timeout || timeout == 0; delta = System.currentTimeMillis() - start) {
					message = messages.poll(timeout);
					if (message == null) {
						if (Logging.isWritable(this, Severity.WARNING)) {
                            Logging.log(this, Severity.WARNING, "Timed out in receive for consumer with id:", consumerId);
                        }
						return null;
					}

					if (!isResizeMessage(message)) {
						if (isExpiredMessage(message)) {
							session.scheduleForExpiration(message, consumerId);
							continue;
						}

						JMSMessage client_msg = null;
						////////////
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

						client_msg.setSession(session);
						client_msg.reset();
						client_msg.setWriteEnabledProperties(false);

						session.getAckHandler().acknowledge(consumerId, message);

						// TODO fix data pointers for byte messages. fix in stupid packet.
						client_msg.reset();

						return client_msg;
					}

					int nextMessageSize = message.getIntProperty("JMS_SAP_NextSize");
					
					if (Logging.isWritable(this, Severity.DEBUG)) {
                        Logging.log(this, Severity.DEBUG, "Server requested", nextMessageSize, "more bytes client buffer space, for consumer with id:", consumerId);
                    }
					
					long connectionId = session.getConnection().getConnectionId();
					session.getConnection().getServerFacade().startMessageDelivery(connectionId, session.getSessionID(), consumerId, nextMessageSize, 0 /* parameter not used by DC */);
				}
			}
		} catch (InterruptedException e) {
			Thread.interrupted();
			if (state.get() == WorkingState.STATE_CLOSED) {
				return null;
			}

			if (state.get() == WorkingState.STATE_STOPPED) {
				// TODO calculate the delta
				long p = timeout - delta;
				if (p <= 0) {
					return null;
				}
				try {
					synchronized (state) {
						state.wait(p);
					}
				} catch (InterruptedException i) {
					// 3. InterruptedException -> not reachable
					Thread.interrupted();
					// TODO not reachable. log error
                    if (Logging.isWritable(this, Severity.ERROR)) {
                        Logging.log(this, Severity.ERROR, "Should not be reachable.");
                    }
				}
				if (state.get() != WorkingState.STATE_RUNNING) {
					// 1. Timeout (p) exceeded
                    if (Logging.isWritable(this, Severity.WARNING)) {
                        Logging.log(this, Severity.WARNING, "Timed out in receive for consumer with id: ", consumerId);
                    }
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
			if (Logging.isCustomerWritable(Severity.WARNING)) {
                Logging.customerLog(this, Severity.WARNING, "The connection of consumer with id:", consumerId, "is stopped. receiveNoWait() will not work. Check your application!");
            }
		}

		JMSMessage message = fetchMessage(true);
		JMSMessage client_msg = null;
		if (message != null) {
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

			client_msg.setSession(session);
			client_msg.reset();
			client_msg.setWriteEnabledProperties(false);

			session.getAckHandler().acknowledge(consumerId, message);

			// TODO fix data pointers for byte messages. fix in stupid packet.
			client_msg.reset();
		}

		return client_msg;
	}


	public JMSMessage fetchMessage(boolean force) throws JMSException {

		JMSMessage message = null;

		while (state.get() == WorkingState.STATE_RUNNING) {
			message = messages.peek();
			if (message == null) {
				if (force == false) {
					return null;
				}

				// a message must be in the buffer at this point if there was one on the server
				long connectionId = session.getConnection().getConnectionId(); 
				session.getConnection().getServerFacade().consumerRefresh(connectionId, session.getSessionID(), consumerId);
				if (messages.peek() == null) {
					// no messages for this consumer
					return null;
				}
				continue;
			}

			// there is a message in the queue at this point
			try {
				message = messages.poll(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				// must not happen
				Logging.exception(this, e);
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
			if (Logging.isCustomerWritable(Severity.DEBUG)) {
                Logging.customerLog(this, Severity.DEBUG, "Server requested", nextMessageSize, "more bytes client buffer space, for consumer with id:", consumerId);
            }
			long connectionId = session.getConnection().getConnectionId();
			session.getConnection().getServerFacade().startMessageDelivery(connectionId, session.getSessionID(), consumerId, nextMessageSize, 0); /* parameter not used by DC */			
		}

		return null;
	}


	public void setMessageListener(MessageListener messageListener) throws JMSException {
		checkClosed();

		this.messageListener = messageListener;
	}


	void push(JMSMessage msg) throws JMSException {
		try {
			Logging.log(this, Severity.DEBUG, "Push message with id: ",msg, " consumer id: ", consumerId);
			messages.put(msg);
			Logging.log(this, Severity.DEBUG, "Consumer with id: ", consumerId, " has now: ", messages);
			if (messageListener != null) {
				// schedule messages for delivery only of the consumer is paused
				// the consumer is paused when the delivery attempts are exhausted
				if (state.get() == WorkingState.STATE_RUNNING) {
					// start the Session
					session.scheduleForDelivery(consumerId);
				}
			}
		} catch (InterruptedException e) {
			JMSException jmse = new JMSException(e.getMessage());
	                jmse.setLinkedException(e);
	                throw jmse;
		}
	}

	// TODO fix vizibility for JMSXASession
	public void push2(JMSMessage msg) throws JMSException {
		try {
			messages.push(msg);
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
		    	Logging.exception(this, e);
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

		// do not schedule delivery task if there is no asynch receiver 
		if (messageListener == null) {
			return;
		}

		int messagesCount = messages.size();

		// NOTE!! Sure we can get a number of schedules for this
		// consumer id which are greater than the actual amount of 
		// messages in the buffer, but this is better than losing 
		// consumer schedules.  The run method handles this by jumping to the
		// next scheduled consumer in the list.
		for (int i=0; i < messagesCount; i++) {
			session.scheduleForDelivery(consumerId);
		}

	}

	void stop() {
		state.set(WorkingState.STATE_STOPPED);
		Thread t = currentThread.getAndSet(null);
		if (t != null) {
			t.interrupt();
		}

		synchronized (state) {
			state.notifyAll();
		}
	}
	
	// fix vizibility for JMSXASession
	public void pause() {
		state.compareAndSet(WorkingState.STATE_RUNNING, WorkingState.STATE_PAUSED);
	}

	void resume() {
		Logging.log(this, Severity.DEBUG, "Will resume consumer ( "+ state.get() +" ) with id: ", consumerId, " with messages: ", messages);

		if (!state.compareAndSet(WorkingState.STATE_PAUSED, WorkingState.STATE_RUNNING)) {
			return;
		}

		if (messageListener == null) {
			return;
		}

		int messagesCount = messages.size();

		Logging.log(this, Severity.DEBUG, "Will resume consumer with id: ", consumerId, " with messages: ", messages);

		// NOTE!! Sure we can get a number of schedules for this
		// consumer id which are greater than the actual amount of 
		// messages in the buffer, but this is better than losing 
		// consumer schedules.  The run method handles this by jumping to the
		// next scheduled consumer in the list.
		for (int i=0; i < messagesCount; i++) {
			session.scheduleForDelivery(consumerId);
		}
	}

	void clear() {
		messages.clear();
	}

	JMSDestination getDestination() {
		return destination;
	}
}
