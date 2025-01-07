/**
 * Message.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.message;

import java.util.Enumeration;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotWriteableException;

import com.sap.jms.JMSConstants;
import com.sap.jms.client.connection.Connection;
import com.sap.jms.client.connection.DestinationInfo;
import com.sap.jms.client.destination.JMSDestination;
import com.sap.jms.client.session.JMSSession;
import com.sap.jms.protocol.MessageRequest;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.util.LogUtil;
import com.sap.jms.util.Logging;
import com.sap.jms.util.MessageID;

public class JMSMessage implements javax.jms.Message, Cloneable, java.io.Serializable {

	protected static final int R_MODE = 0; //read only mode
	protected static final int W_MODE = 1; //write only mode
	protected static final int RW_MODE = 2; //read and write mode

	protected transient int mode = W_MODE;
	protected transient int sessionID = -1; // the sessions sessionID  
	protected transient boolean writeEnabledProperties = true;
	protected MessageRequest messagePacket = null;
	protected MessageProperties messageProperties = null;

	private transient JMSSession session = null;
	protected transient long connectionID = 0;

	public JMSMessage() throws JMSException {
		messagePacket = new MessageRequest(PacketTypes.JMS_GENERIC_MESSAGE, MessageRequest.SIZE, connectionID, sessionID);
		messageProperties = new MessageProperties();
	}

	protected JMSMessage(javax.jms.Message message) throws JMSException {
		this();
		copyPropertiesFrom(message);
	}

	protected JMSMessage(byte type) throws JMSException {
		messagePacket = new MessageRequest(type, MessageRequest.SIZE, connectionID, sessionID);
		messageProperties = new MessageProperties();
	}

	public JMSMessage(MessageRequest messagePacket) {
		this.messagePacket = messagePacket;
		messageProperties = new MessageProperties(messagePacket);
	}

	public void clearBody() throws JMSException {
		//to be overriden
	}

	/**
	 * Method setSession. Used by sessions to associate themselves to the messages
	 * they recieve. When acknowledge is invoked it is handled by the associated with
	 * the message session.
	 * @param session  session hat will be associated to the message
	 * @throws JMSException  thrown if internal provider error occurs
	 */
	public void setSession(JMSSession session) {
		this.session = session;
	}

	public JMSSession getSession() {
		return session;
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#acknowledge()
	 */
	public void acknowledge() throws JMSException {

		if (session != null) {
			session.acknowledge();
		}
	}

	/**
	 * Method reset. Puts the message body in read-only mode.
	 * For some message types it should perform additional tasks.
	 *
	 * @throws JMSException
	 * @see javax.jms.BytesMessage
	 * @see javax.jms.StreamMessage
	 */
	public void reset() throws JMSException {
		mode = R_MODE;
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#clearProperties()
	 */
	public void clearProperties() throws JMSException {
		if (messageProperties == null) {
			messageProperties = new MessageProperties();
		} else {
			messageProperties.clearProperties();
		}
		writeEnabledProperties = true;
	}

	/**
	 * Method setWriteEnabledProperties. Set the flag indicating the write
	 * state of the properites.
	 * @param flag  the value that will be set to the flag
	 */
	public void setWriteEnabledProperties(boolean flag) {
		writeEnabledProperties = flag;
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getBooleanProperty(String)
	 */
	public boolean getBooleanProperty(String name) throws JMSException {
		if (messageProperties == null) {
			messageProperties = new MessageProperties(messagePacket);
		}

		return messageProperties.getBooleanProperty(name);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getByteProperty(String)
	 */
	public byte getByteProperty(String name) throws JMSException {
		if (messageProperties == null) {
			messageProperties = new MessageProperties(messagePacket);
		}

		return messageProperties.getByteProperty(name);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getDoubleProperty(String)
	 */
	public double getDoubleProperty(String name) throws JMSException {
		if (messageProperties == null) {
			messageProperties = new MessageProperties(messagePacket);
		}

		return messageProperties.getDoubleProperty(name);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getFloatProperty(String)
	 */
	public float getFloatProperty(String name) throws JMSException {
		if (messageProperties == null) {
			messageProperties = new MessageProperties(messagePacket);
		}

		return messageProperties.getFloatProperty(name);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getIntProperty(String)
	 */
	public int getIntProperty(String name) throws JMSException {
		if (messageProperties == null) {
			messageProperties = new MessageProperties(messagePacket);
		}

		return messageProperties.getIntProperty(name);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getLongProperty(String)
	 */
	public long getLongProperty(String name) throws JMSException {
		if (messageProperties == null) {
			messageProperties = new MessageProperties(messagePacket);
		}

		return messageProperties.getLongProperty(name);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getObjectProperty(String)
	 */
	public Object getObjectProperty(String name) throws JMSException {
		if (messageProperties == null) {
			messageProperties = new MessageProperties(messagePacket);
		}

		return messageProperties.getObjectProperty(name);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getShortProperty(String)
	 */
	public short getShortProperty(String name) throws JMSException {
		if (messageProperties == null) {
			messageProperties = new MessageProperties(messagePacket);
		}

		return messageProperties.getShortProperty(name);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getStringProperty(String)
	 */
	public String getStringProperty(String name) throws JMSException {
		if (messageProperties == null) {
			messageProperties = new MessageProperties(messagePacket);
		}

		return messageProperties.getStringProperty(name);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getJMSDeliveryMode()
	 */
	public int getJMSDeliveryMode() throws JMSException {
		return messagePacket.getJMSDeliveryMode();
	}

	private DestinationInfo resolveDestinationId(int id) throws JMSException {
		if (session == null) {
			throw new JMSException("Operation not allowed.");
		}
		DestinationInfo destination = null; 
		if (id != 0) {
			destination = JMSDestination.getDestination(id);
			if (destination == null) {
				destination = session.getServerFacade().getDestinationInfo(id);
				if (destination != null) {
					JMSDestination.setDestination(destination);
				}
			}	
		} 
		return destination;
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getJMSDestination()
	 */
	public javax.jms.Destination getJMSDestination() throws JMSException {
		javax.jms.Destination destination = null;
		int destinationId = messagePacket.getDestinationID();
		DestinationInfo info = resolveDestinationId(destinationId); 

		if (info == null) {   
			return null;
		}
		Connection connection = session != null ? session.getConnection() : null;
		destination = JMSDestination.resolveDestination(info, connection);
		return destination;
	}



	/* (non-Javadoc)
	 * @see javax.jms.Message#getJMSExpiration()
	 */
	public long getJMSExpiration() throws JMSException {
		return messagePacket.getJMSExpiration();
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getJMSRedelivered()
	 */
	public boolean getJMSRedelivered() throws JMSException {
		return messagePacket.getJMSRedelivered();
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getJMSMessageID()
	 */
	public String getJMSMessageID() throws JMSException {
		return messagePacket.getJMSMessageID();
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getJMSPriority()
	 */
	public int getJMSPriority() throws JMSException {
		return messagePacket.getJMSPriority();
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getJMSReplyTo()
	 */
	public javax.jms.Destination getJMSReplyTo() throws JMSException {
		javax.jms.Destination destination = null;
		int destinationId = messagePacket.getJMSReplyToID();
		DestinationInfo info = resolveDestinationId(destinationId); 

		if (info == null) {   
			return null;
		}   
		Connection connection = session != null ? session.getConnection() : null;
		destination = JMSDestination.resolveDestination(info, connection);
		return destination;
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getJMSTimestamp()
	 */
	public long getJMSTimestamp() throws JMSException {
		return messagePacket.getJMSTimestamp();
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getJMSType()
	 */
	public String getJMSType() throws JMSException {
		return messagePacket.getJMSType();
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getJMSCorrelationID()
	 */
	public String getJMSCorrelationID() throws JMSException {
		return messagePacket.getJMSCorrelationID();
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getJMSCorrelationIDAsBytes()
	 */
	public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
		throw new java.lang.UnsupportedOperationException("Not supported.");
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#getPropertyNames()
	 */
	public java.util.Enumeration getPropertyNames() throws JMSException {
		if (messageProperties == null) {
			messageProperties = new MessageProperties(messagePacket);
		}

		return messageProperties.getPropertyNames();
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#propertyExists(String)
	 */
	public boolean propertyExists(String name) throws JMSException {
		if (messageProperties == null) {
			messageProperties = new MessageProperties(messagePacket);
		}

		return messageProperties.propertyExists(name);
	}

	protected void checkName (String name) throws IllegalArgumentException {
		if (name == null || name.length() ==0) {
			throw new IllegalArgumentException("Not supported.");
		} 
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setBooleanProperty(String, boolean)
	 */
	public void setBooleanProperty(String name, boolean value) throws JMSException {
		checkName(name);    
		checkWriteEnabled();
		messageProperties.setBooleanProperty(name, value);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setByteProperty(String, byte)
	 */
	public void setByteProperty(String name, byte value) throws JMSException {
		checkName(name);    
		checkWriteEnabled();
		messageProperties.setByteProperty(name, value);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setDoubleProperty(String, double)
	 */
	public void setDoubleProperty(String name, double value) throws JMSException {
		checkName(name);    
		checkWriteEnabled();
		messageProperties.setDoubleProperty(name, value);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setFloatProperty(String, float)
	 */
	public void setFloatProperty(String name, float value) throws JMSException {
		checkName(name);    
		checkWriteEnabled();
		messageProperties.setFloatProperty(name, value);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setIntProperty(String, int)
	 */
	public void setIntProperty(String name, int value) throws JMSException {
		checkName(name);    
		checkWriteEnabled();
		messageProperties.setIntProperty(name, value);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setLongProperty(String, long)
	 */
	public void setLongProperty(String name, long value) throws JMSException {
		checkName(name);    
		checkWriteEnabled();
		messageProperties.setLongProperty(name, value);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setObjectProperty(String, Object)
	 */
	public void setObjectProperty(String name, Object value) throws JMSException {
		checkName(name);    
		checkWriteEnabled();

		messageProperties.setObjectProperty(name, value);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setShortProperty(String, short)
	 */
	public void setShortProperty(String name, short value) throws JMSException {
		checkName(name);    
		checkWriteEnabled();
		messageProperties.setShortProperty(name, value);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setStringProperty(String, String)
	 */
	public void setStringProperty(String name, String value) throws JMSException {
		checkName(name);    
		checkWriteEnabled();
		messageProperties.setStringProperty(name, value);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setJMSCorrelationID(String)
	 */
	public void setJMSCorrelationID(String correlationID) throws JMSException {
		if (correlationID != null) {
			messagePacket.setJMSCorrelationID(correlationID);
		}
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setJMSCorrelationIDAsBytes(byte[])
	 */
	public void setJMSCorrelationIDAsBytes(byte[] correlationID) throws JMSException {
		throw new java.lang.UnsupportedOperationException("Not supported.");
	}

	public void setJMSDeliveryMode(int deliveryMode) throws JMSException {
		messagePacket.setJMSDeliveryMode(deliveryMode);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setJMSDestination(Destination)
	 */
	public void setJMSDestination(javax.jms.Destination destination) throws JMSException {
		if (destination != null && (destination instanceof JMSDestination)) {
			messagePacket.setJMSDestinationID(((JMSDestination)destination).getDestinationId());

			if (((JMSDestination)destination).isTopic()) {
				messagePacket.setDestinationTopic(true);
			} else {
				messagePacket.setDestinationTopic(false);
			}

			if (((JMSDestination) destination).isTemporary()) {
				messagePacket.setDestinationTemporary(true);
			} else {
				messagePacket.setDestinationTemporary(false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setJMSExpiration(long)
	 */
	public void setJMSExpiration(long expiration) throws JMSException {
		messagePacket.setJMSExpiration(expiration);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setJMSMessageID(String)
	 */
	public void setJMSMessageID(String messageID) throws JMSException {
		if (messageID == null) {
			return;
		} 

		try {
			setJMSMessageID(MessageID.toBytes(messageID));
		} catch (Exception ex) {
			Logging.exception(this, ex);
			throw new JMSException("Incorrect message ID.");
		} 

	}

	public void setJMSMessageID(byte[] messageID) throws JMSException {
		if (messageID != null) {
			messagePacket.setJMSMessageIDAsBytes(messageID);
		}
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setJMSPriority(int)
	 */
	public void setJMSPriority(int priority) throws JMSException {
		messagePacket.setJMSPriority(priority);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setJMSRedelivered(boolean)
	 */
	public void setJMSRedelivered(boolean redelivered) throws JMSException {
		messagePacket.setJMSRedelivered(redelivered);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setJMSReplyTo(Destination)
	 */
	public void setJMSReplyTo(javax.jms.Destination replyTo) throws JMSException {
		if (replyTo != null && (replyTo instanceof JMSDestination)) {
			messagePacket.setJMSReplyToID(((JMSDestination)replyTo).getDestinationId());

			if (((JMSDestination)replyTo).isTopic()) {
				messagePacket.setJMSReplyToTopic(true);
			} else {
				messagePacket.setJMSReplyToTopic(false);
			}

			if (((JMSDestination) replyTo).isTemporary()) {
				messagePacket.setJMSReplyToTemporary(true);
			} else {
				messagePacket.setJMSReplyToTemporary(false);
			}

			//    Put this destination into the cache as to prevent server round-trips
			DestinationInfo destination = ((JMSDestination) replyTo).getDestinationInfo();
			JMSDestination.setDestination(destination); 
		}
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setJMSTimestamp(long)
	 */
	public void setJMSTimestamp(long timestamp) throws JMSException {
		messagePacket.setJMSTimestamp(timestamp);
	}

	/* (non-Javadoc)
	 * @see javax.jms.Message#setJMSType(String)
	 */
	public void setJMSType(String type) throws JMSException {
		if (type != null) {
			messagePacket.setJMSType(type);
		}
	}

	/**
	 * Method setMode. Sets the read/write mode of the message. Possible modes are
	 * RMODE (read only), WMODE (write only), RWMODE (read and write)
	 * @param mode  the value of the mode to be set
	 */
	protected void setMode(int mode) {
		this.mode = mode;
	}

	/**
	 * Method convertMessage. Converts a message form another JMS provider into a specific
	 * message for this provider.
	 * @param message  the source message to be converted
	 * @return Message  the converted message
	 * @throws JMSException  thrown if internall error occurs during the conversion process.
	 */
	public static JMSMessage convertMessage(javax.jms.Message message) throws JMSException {
		JMSMessage mess = null;

		if (message instanceof javax.jms.TextMessage) {
			mess = new JMSTextMessage((javax.jms.TextMessage)message);
		} else if (message instanceof javax.jms.BytesMessage) {
			mess = new JMSBytesMessage((javax.jms.BytesMessage)message);
		} else if (message instanceof javax.jms.ObjectMessage) {
			mess = new JMSObjectMessage((javax.jms.ObjectMessage)message);
		} else if (message instanceof javax.jms.StreamMessage) {
			mess = new JMSStreamMessage((javax.jms.StreamMessage)message);
		} else if (message instanceof javax.jms.MapMessage) {
			mess = new JMSMapMessage((javax.jms.MapMessage)message);
		} else {
			mess = new JMSMessage((javax.jms.Message) message);
		} 

		return mess;
	}

	/**
	 * Method copyPropertiesFrom. Copies the properties of a given message.
	 * @param message  the source message from which the properties will be copied
	 * @throws JMSException  thron if internal error occurs during the copy process
	 */
	public void copyPropertiesFrom(javax.jms.Message message) throws JMSException {
		String key;
		Enumeration e = message.getPropertyNames();

		while (e.hasMoreElements()) {
			key = (String) e.nextElement();
			setObjectProperty(key, message.getObjectProperty(key));
		}

		setJMSCorrelationID(message.getJMSCorrelationID());
		setJMSDeliveryMode(message.getJMSDeliveryMode());
		setJMSDestination(message.getJMSDestination());
		setJMSExpiration(message.getJMSExpiration());

		if (message instanceof JMSMessage) {
			setJMSMessageID(message.getJMSMessageID());
		} 

		setJMSPriority(message.getJMSPriority());
		setJMSRedelivered(message.getJMSRedelivered());
		setJMSReplyTo(message.getJMSReplyTo());
		setJMSTimestamp(message.getJMSTimestamp());
		setJMSType(message.getJMSType());
	}

	/**
	 * Method getMessagePacket. Returns the MessageRequest object holding the message data.
	 * @return Packet the MessageRequest object holding the message data
	 */
	public MessageRequest getMessagePacket() {
		return messagePacket;
	}

	/**
	 * Method flushBuffer. Used by senders to perform serialization of the message body
	 * into the message buffer.
	 */
	protected void flushToBuffer(Object body) throws JMSException {
		if (messageProperties != null) {
			messagePacket.setMessageProperties(messageProperties.getPropertiesTable());
		}

		if (body != null) {
			messagePacket.setMessageBody(body);
		}

		messagePacket.flush();
	}

	protected void flushToBuffer(byte[] body, int length) throws JMSException {
		if (messageProperties != null) {
			messagePacket.setMessageProperties(messageProperties.getPropertiesTable());
		}

		if (body != null) {
			messagePacket.setMessageBody(body, length);
		}

		messagePacket.flush();
	}

	public void flush() throws JMSException {
		flushToBuffer(null);
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof JMSMessage)) {
			return false;
		}

		JMSMessage msg = (JMSMessage)o;

		String msgId = null;
		String other_msgId = null;
		Long pCounter = null;
		Long other_pCounter = null;
		Integer destination_id = null;
		Integer other_destination_id = null;

		try {
			msgId = getJMSMessageID();
			other_msgId = msg.getJMSMessageID();

			pCounter = getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER);
			other_pCounter = msg.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER);

			destination_id = ((JMSDestination)getJMSDestination()).getDestinationId();
			other_destination_id =((JMSDestination)msg.getJMSDestination()).getDestinationId();
		} catch (JMSException e) {
			Logging.exception(this, e);
		} finally {
			if (msgId != null && !msgId.isEmpty()) {
				if (msgId.equals(other_msgId)){
					return true;
				}
			} 

			if (pCounter != null && destination_id != null) {
				if (pCounter.equals(other_pCounter) && destination_id.equals(other_destination_id)) {
					return true;
				}
			} 

			if (super.equals(msg))
				return true;
		}

		return false;
	}

	public int hashCode(){
		int code = 0;
		String msgId = null;
		Long pCounter = null;
		Destination destination = null;

		try {
			msgId = getJMSMessageID();
			pCounter = getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER);
			destination = getJMSDestination();
		} catch (JMSException e) {
			Logging.exception(this, e);
		} finally {
			if (msgId != null && !msgId.isEmpty()) {
				code = msgId.hashCode();
			} else if (pCounter != null) {
				code = 5381;
				int c;
				c = (int)(pCounter & 0xFFFF);
				code = ((code << 5) + code) + c;
				c = (int)((pCounter>>32) & 0xFFFF);
				code = ((code << 5) + code) + c;
				if (destination != null) {
					int dstId = ((JMSDestination)destination).getDestinationId();
					code = ((code << 5) + code) + dstId;
				}
			} else {
				// TODO ERR
code = super.hashCode();
			}
		}

		return code;
	}

	private void checkWriteEnabled() throws MessageNotWriteableException {
		if (!writeEnabledProperties) {
			throw new MessageNotWriteableException(LogUtil.getFailedInComponentByCaller() + "Message properties are read-only.");
		}
	}

	protected MessageFormatException throwIncorrectTypeMessageFormatException() {
		return new MessageFormatException(LogUtil.getFailedInComponentByCaller() + "Incorrect type conversion.");
	}

	public String toString() {
		try {
			return Long.toString(getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER));
		} catch (Exception e) {
			return "null";
		}
	}
}
