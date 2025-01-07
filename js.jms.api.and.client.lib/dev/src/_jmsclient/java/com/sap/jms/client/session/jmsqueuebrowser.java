/**
 * QueueBrowser.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.session;

import java.util.Date;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;

import com.sap.jms.JMSConstants;
import com.sap.jms.client.connection.AsyncCloser;
import com.sap.jms.client.connection.Closeable;
import com.sap.jms.client.destination.JMSDestination;
import com.sap.jms.client.destination.JMSQueue;
import com.sap.jms.client.message.JMSMessage;
import com.sap.jms.util.JMSLinkedBlockingQueue;
import com.sap.jms.util.Logging;
import com.sap.tc.logging.Severity;


public class JMSQueueBrowser implements QueueBrowser, Enumeration<Object>, Closeable {

    private AtomicBoolean closed;

    // TODO make it private
	private JMSQueue queue;
	private long browserId;
	private JMSSession session;
	private String messageSelector;
	private JMSLinkedBlockingQueue<JMSMessage> messages;
	private AtomicBoolean browserIsActive;

	
	
	public JMSQueueBrowser(JMSQueue queue, long browserId, JMSSession session, String selector) throws JMSException {
		this.browserId = browserId;
		this.queue = queue;
		this.messageSelector = selector;
		this.session = session;    

		closed = new AtomicBoolean(false);
		messages = new JMSLinkedBlockingQueue<JMSMessage>();
		browserIsActive = new AtomicBoolean(false);
	}

	public Queue getQueue() throws JMSException {
		checkClosed();
		
		return queue;
	}

	public String getMessageSelector() throws JMSException {
		checkClosed();
		
		return messageSelector;
	}

	public void close() throws JMSException {
		synchronized (closed) {
			if (closed.get() != true) {

				session.removeBrowser(browserId);
				closed.set(true);
			}
		}
	}
	
	private boolean isResizeMessage(JMSMessage message) throws JMSException {
		if (message.getBooleanProperty("JMS_SAP_EndMessage") == true){
			return true;
		}
		
		return false;
	}  

	private void resize(int nextMessageSize) throws JMSException {

		long connectionId = session.getConnection().getConnectionId(); 
		session.getConnection().getServerFacade().startMessageDelivery(connectionId, session.getSessionID(), browserId, nextMessageSize, 0); /* parameter not used by DC */ 

	}

	public Enumeration<Object> getEnumeration() throws JMSException {
		checkClosed();

		if (!browserIsActive.compareAndSet(false, true)) {
			throw new JMSException("Cannot create enumeration. There is another active enumeration.");
		}

		if (!session.getConnection().isStarted()) {
			return new EmptyEnumeration();    	  
		}
		long connectionId = session.getConnection().getConnectionId();
		session.getServerFacade().queueBrowserEnumerate(connectionId, session.getSessionID(), browserId);
		
		browserIsActive.set(true);
		
		return this;
	}
	
	public Object nextElement() {
		
		if (!hasMoreElements()) {
			throw new NoSuchElementException();
		} 

		JMSMessage message = null;
		
		try {
			message = messages.poll(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			Logging.exception(this, e);
		}
		return message;
	}

	public boolean hasMoreElements() {

		Long timeout = 0L;

		if (browserIsActive.get() == false) {
			return false;
		}
		
		while(true){		

			JMSMessage message;
			try {
				message = messages.peek(timeout);

				// queue is empty
				if (message == null) {
					// block until a new message
					timeout = Long.MAX_VALUE;
					continue;
				}

				if (isResizeMessage(message)) {
					int nextMessageSize = message.getIntProperty("JMS_SAP_NextSize");
					resize(nextMessageSize);
					messages.poll(0);	
					continue;
				}

				if (message.getBooleanProperty("JMS_SAP_EnumEnd")) {
					messages.clear();
					browserIsActive.set(false);
					return false;
				}

				if (message instanceof JMSMessage) {
					
					long expiration = message.getJMSExpiration();
					long currentTime = System.currentTimeMillis();
					if (expiration != 0 && currentTime >= expiration) { 
						if (Logging.isWritable(this, Severity.WARNING)) {
                            Logging.log(this, Severity.WARNING, "Message with id: ", message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER), " for browser with id: ", browserId, " has expired at ", new Date());
                        }
						// remove expired message from head
						messages.poll(0);
						continue;
					}	
					
					return true;
				}
			} catch (Exception e) {
				Logging.exception(this, e);
			}
		}
	}

	
	void push(JMSMessage msg) throws JMSException {
		try {
			messages.put(msg);
		} catch (InterruptedException e) { 
			JMSException jmse = new JMSException(e.getMessage());
	                jmse.setLinkedException(e);
	                throw jmse;
		}
	}

	private void checkClosed() throws IllegalStateException {
		if (closed.get() == true) {
			throw new IllegalStateException("The QueueBrowser is closed. id: " + browserId);
		}
	}
	
	static class EmptyEnumeration implements Enumeration<Object> {
		public boolean hasMoreElements() {
			return false;
		}

		public Object nextElement() {
			throw new NoSuchElementException();
		}
	}


	protected void finalize() throws Throwable {//$JL-FINALIZE$
		if (closed.get() == true) {
			return;
		}

		try {
			AsyncCloser.getInstance().scheduleForClose(this);
		} catch (Exception e) {

		} finally {
			super.finalize();        
		}
	}

	// TODO UUgh only for Session#onPacket
	JMSQueue getDestination() {
		return queue;
	}


}
