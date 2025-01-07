/**
 * MessageProducer.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.session;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jms.DeliveryMode;
import javax.jms.IllegalStateException;
import javax.jms.JMSException;

import com.sap.jms.client.connection.AsyncCloser;
import com.sap.jms.client.connection.Closeable;
import com.sap.jms.client.connection.DestinationInfo;
import com.sap.jms.client.destination.JMSDestination;
import com.sap.jms.client.message.JMSMessage;
import com.sap.jms.util.Logging;
import com.sap.tc.logging.Severity;


public class MessageProducer implements javax.jms.MessageProducer, Closeable {

	protected JMSSession session;  //  the session within which the producer operates
	protected long producerID = -1; // ID of the producer
	protected boolean isClosed = true; //  producer state  

	//  set default properties for the messages to be produced
	protected boolean messageIDEnabled = true;
	protected boolean messageTimestampEnabled = true;
	protected int deliveryMode = JMSMessage.DEFAULT_DELIVERY_MODE;
	protected int priority = JMSMessage.DEFAULT_PRIORITY;
	protected long timeToLive = JMSMessage.DEFAULT_TIME_TO_LIVE;
	protected javax.jms.Destination destination = null;
	protected Map helperProducers = null;

	private boolean normalSend = false;
	private Object syncThis = new Object();

	public MessageProducer(javax.jms.Destination destination, long producerID, JMSSession session) throws JMSException {
		this.session = session;
		this.destination = destination;

		if (destination == null) {
			helperProducers = new HashMap();
		} else {
			// Put this destination into the cache as to prevent server round-trips
			DestinationInfo info = ((JMSDestination) destination).getDestinationInfo();
			JMSDestination.setDestination(info);			
		} 

		this.producerID = producerID;
		isClosed = false;
	}


	public void close() throws JMSException {
		synchronized (syncThis) {

			if (Logging.isWritable(this, Severity.DEBUG)) {
                Logging.log(this, Severity.DEBUG, "Will close producer with id: ", producerID);
            }

			if (isClosed) {
				return;
			}

			isClosed = true;

			if (helperProducers != null) {

				for (Iterator iter = helperProducers.values().iterator(); iter.hasNext(); ) {
					((javax.jms.MessageProducer) iter.next()).close();
				}    

				helperProducers.clear();
				helperProducers = null;
				return;
			}

			session.removeProducer(producerID);

		}
	}


	public int getDeliveryMode() throws JMSException {
		attemptToUse();

		return deliveryMode;
	}


	public boolean getDisableMessageID() throws JMSException {
		attemptToUse();

		return !messageIDEnabled;
	}


	public boolean getDisableMessageTimestamp() throws JMSException {
		attemptToUse();

		return messageTimestampEnabled;
	}


	public int getPriority() throws JMSException {
		attemptToUse();

		return priority;
	}


	public long getTimeToLive() throws JMSException {
		attemptToUse();

		return timeToLive;
	}


	public void send(javax.jms.Message clientMessage) throws JMSException {
		send(clientMessage, deliveryMode, priority, timeToLive);
	}


	public void send(javax.jms.Message clientMessage, int deliveryMode, int priority, long timeToLive) throws JMSException {
		if (destination == null) {
			throw new UnsupportedOperationException("Destination is null;");
		}

		normalSend = true;
		try{
			send(destination, clientMessage, deliveryMode, priority, timeToLive);
		} finally {
			normalSend = false;
		}

	}


	public void send(javax.jms.Destination destination, javax.jms.Message message) throws JMSException {
		send(destination, message, deliveryMode, priority, timeToLive);
	}


	public void send(javax.jms.Destination destination, javax.jms.Message clientMessage, int deliveryMode, int priority, long timeToLive) throws JMSException {

		attemptToUse();

		if (!normalSend) {
			if (this.destination != null) {
				throw new UnsupportedOperationException("This producer is bound to a destination.");
			} else if (destination == null) {
				throw new UnsupportedOperationException("Destination must be specified.");
			} else {
				if (!(destination instanceof JMSDestination)) {
					throw new javax.jms.InvalidDestinationException("Illegal destination!");
				}

				MessageProducer helperProducer = (MessageProducer) helperProducers.get(destination);

				if (helperProducer == null) {
					String instanceName = ((JMSDestination) destination).getVPName();

					if (instanceName != null && !instanceName.equals(session.getConnection().getVPName())) {
						throw new javax.jms.InvalidDestinationException("Illegal destination!");
					}

					helperProducer = (MessageProducer) session.createProducer(destination);
					helperProducers.put(destination, helperProducer);
				}

				helperProducer.send(clientMessage, deliveryMode, priority, timeToLive);
			}

			return;
		} 

		JMSMessage message = null;
		long timestamp = 0L;

		/////
		//  sets the flags and header fields of the message
		clientMessage.setJMSDestination(destination);
		timestamp = (messageTimestampEnabled) ? System.currentTimeMillis() : 0L;

		if (messageTimestampEnabled) {
			clientMessage.setJMSTimestamp(timestamp);
		}

		timestamp = (timeToLive == 0) ? 0L : timeToLive + timestamp;
		clientMessage.setJMSExpiration(timestamp);
		clientMessage.setJMSPriority(priority);
		clientMessage.setJMSDeliveryMode(deliveryMode);

		String messageID = "";
		if (messageIDEnabled) {
			if (clientMessage instanceof JMSMessage) {
				byte messageIDAsBytes[] = session.generateMessageIDAsBytes();
				((JMSMessage)clientMessage).setJMSMessageID(messageIDAsBytes);
			} else {
				messageID = session.generateMessageID();
				clientMessage.setJMSMessageID(messageID);
			}
		} 

		//  convert external messages to internal ones.
		message = JMSMessage.convertMessage(clientMessage);
		if (!(clientMessage instanceof JMSMessage) && messageIDEnabled) {
			message.setJMSMessageID(messageID);
		}
		JMSSession temp = message.getSession();
		message.setSession(session);

		session.sendMessage(message);
		message.setSession(temp);

	}


	public void setDeliveryMode(int deliveryMode) throws JMSException {
		attemptToUse();

		switch (deliveryMode) {
		case DeliveryMode.NON_PERSISTENT: {
			this.deliveryMode = DeliveryMode.NON_PERSISTENT;
			break;
		}
		case DeliveryMode.PERSISTENT: {
			this.deliveryMode = DeliveryMode.PERSISTENT;
			break;
		}
		default: {
			break;
		}
		}
	}


	public void setDisableMessageID(boolean value) throws JMSException {
		attemptToUse();

		messageIDEnabled = !value;
	}

	public void setDisableMessageTimestamp(boolean value) throws JMSException {
		attemptToUse();

		messageTimestampEnabled = !value;
	}

	public void setPriority(int priority) throws JMSException {
		attemptToUse();

		if ((priority >= 0) && (priority <= 9)) {
			this.priority = priority;
		}
	}

	public void setTimeToLive(long timeToLive) throws JMSException {
		attemptToUse();

		if (timeToLive >= 0) {
			this.timeToLive = timeToLive;
		}
	}

	protected void attemptToUse() throws IllegalStateException {
		if (isClosed) {
			throw new IllegalStateException("The producer is closed. id: " + producerID);
		}
	}


	public javax.jms.Destination getDestination() throws javax.jms.JMSException {
		return destination;
	}


	protected void finalize() throws Throwable  {//$JL-FINALIZE$
		try {
			if (isClosed) {
				return;
			}

			AsyncCloser.getInstance().scheduleForClose(this);

		} catch (Exception e) {

		} finally {
			super.finalize();        
		}
	}
}