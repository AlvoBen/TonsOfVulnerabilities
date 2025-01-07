/**
 * Message.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.message;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.MessageNotWriteableException;

import com.sap.jms.client.Util;
import com.sap.jms.client.destination.JMSDestination;
import com.sap.jms.client.destination.JMSQueue;
import com.sap.jms.client.destination.JMSTemporaryQueue;
import com.sap.jms.client.destination.JMSTemporaryTopic;
import com.sap.jms.client.destination.JMSTopic;
import com.sap.jms.client.session.JMSSession;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.message.MessageRequest;
import com.sap.jms.util.MessageID;
import com.sap.jms.util.logging.LogService;


public class JMSMessage implements javax.jms.Message, Cloneable {

  public static final String LOG_COMPONENT = "message.Message";

  protected static final int R_MODE = 0; //read only mode
  protected static final int W_MODE = 1; //write only mode
  protected static final int RW_MODE = 2; //read and write mode

  protected int mode = W_MODE;
  protected int sessionID = -1; // the sessions sessionID  
  protected boolean writeEnabledProperties = true;
  protected MessageRequest messagePacket = null;
  protected MessageProperties messageProperties = null;

  private JMSSession session = null;
  protected long connectionID = 0;
  
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

  /*
   * Provides the name of a destination with the specified ID
   */
  private String resolveDestinationID(int id) throws JMSException {
    if (session == null) {
      throw new JMSException("Operation not allowed.");
    }
    
    String name = null;
    if (id != 0) {
      name = JMSDestination.getNameForID(id);
      if (name == null) {
        name = session.getServerFacade().destinationName(id);
        JMSDestination.setIDNameMapping(id, name);
      } //if
    } //if
    return name;
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#getJMSDestination()
   */
  public javax.jms.Destination getJMSDestination() throws JMSException {
    javax.jms.Destination destination = null;
    int destinationID = messagePacket.getDestinationID();
    String name = resolveDestinationID(destinationID);

    if (name == null) {   
      return null;
    }

    if (messagePacket.isDestinationTopic()) {
      if (messagePacket.isDestinationTemporary()) {
        if (session == null) {
          destination = new JMSTemporaryTopic(name, destinationID, null);
        } else {
          destination = new JMSTemporaryTopic(name, destinationID, session.getConnection());
        }
      } else {
        destination = new JMSTopic(name, destinationID);
      }
    } else {
      if (messagePacket.isDestinationTemporary()) {
        if (session == null) {
          destination = new JMSTemporaryQueue(name, destinationID, null);
        } else {
          destination = new JMSTemporaryQueue(name, destinationID, session.getConnection());
        }
      } else {
      	if (session == null) {
      		destination = new JMSQueue(name, destinationID);
      	} else {
            destination = new JMSQueue(name, destinationID);
      	}
      }
       
    } //if
   
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
    int destinationID = messagePacket.getJMSReplyToID();
    String name = resolveDestinationID(destinationID);

    if (name == null) {
      return null;
    }   
      
    if (messagePacket.isJMSReplyToTopic()) {
      if (messagePacket.isJMSReplyToTemporary()) {
        if (session == null) {
          destination = new JMSTemporaryTopic(name, destinationID, null);
        } else {
          destination = new JMSTemporaryTopic(name, destinationID, session.getConnection());
        }
      } else {
        destination = new JMSTopic(name, destinationID);
      }
    } else {
      if (messagePacket.isJMSReplyToTemporary()) {
        if (session == null) {
          destination = new JMSTemporaryQueue(name, destinationID, null);
        } else {
          destination = new JMSTemporaryQueue(name, destinationID, session.getConnection());
        }
      } else {
      	if (session == null) {
      		destination  = new JMSQueue(name, destinationID);
      	} else {
      		destination = new JMSQueue(name, destinationID);
      	}
      }
    } //if
       
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
    if (!writeEnabledProperties) {
      throw new MessageNotWriteableException("Message properties are read-only.");
    }
    messageProperties.setBooleanProperty(name, value);
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#setByteProperty(String, byte)
   */
  public void setByteProperty(String name, byte value) throws JMSException {
    checkName(name);    
    if (!writeEnabledProperties) {
      throw new MessageNotWriteableException("Message properties are read-only.");
    }
    messageProperties.setByteProperty(name, value);
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#setDoubleProperty(String, double)
   */
  public void setDoubleProperty(String name, double value) throws JMSException {
    checkName(name);    
    if (!writeEnabledProperties) {
      throw new MessageNotWriteableException("Message properties are read-only.");
    }
    messageProperties.setDoubleProperty(name, value);
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#setFloatProperty(String, float)
   */
  public void setFloatProperty(String name, float value) throws JMSException {
    checkName(name);    
    if (!writeEnabledProperties) {
      throw new MessageNotWriteableException("Message properties are read-only.");
    }
    messageProperties.setFloatProperty(name, value);
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#setIntProperty(String, int)
   */
  public void setIntProperty(String name, int value) throws JMSException {
    checkName(name);    
    if (!writeEnabledProperties) {
      throw new MessageNotWriteableException("Message properties are read-only.");
    }
    messageProperties.setIntProperty(name, value);
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#setLongProperty(String, long)
   */
  public void setLongProperty(String name, long value) throws JMSException {
    checkName(name);    
    if (!writeEnabledProperties) {
      throw new MessageNotWriteableException("Message properties are read-only.");
    }
    messageProperties.setLongProperty(name, value);
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#setObjectProperty(String, Object)
   */
  public void setObjectProperty(String name, Object value) throws JMSException {
    checkName(name);    
    if (!writeEnabledProperties) {
      throw new MessageNotWriteableException("Message properties are read-only.");
    }
    
    messageProperties.setObjectProperty(name, value);
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#setShortProperty(String, short)
   */
  public void setShortProperty(String name, short value) throws JMSException {
    checkName(name);    
    if (!writeEnabledProperties) {
      throw new MessageNotWriteableException("Message properties are read-only.");
    }
    messageProperties.setShortProperty(name, value);
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#setStringProperty(String, String)
   */
  public void setStringProperty(String name, String value) throws JMSException {
    checkName(name);    
    if (!writeEnabledProperties) {
      throw new MessageNotWriteableException("Message properties are read-only.");
    }
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
      messagePacket.setJMSDestinationID(((JMSDestination)destination).getDestinationID());
      
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
    } //if
    
    try {
    	setJMSMessageID(MessageID.toBytes(messageID));
    }
    catch (Exception ex) {
        Util.LOG_SERVICE.exception(LogService.WARNING, LOG_COMPONENT, ex);
      throw new JMSException("Incorrect message ID.");
    } //try
    
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
      messagePacket.setJMSReplyToID(((JMSDestination)replyTo).getDestinationID());
  
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
      JMSDestination.setIDNameMapping(((JMSDestination) replyTo).getDestinationID(),
      ((JMSDestination) replyTo).getName()); 
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

	  if (message instanceof JMSMessage) {
		  // just in case somebody receives and then sends the same message
		  ((JMSMessage)message).setWriteEnabledProperties(true);
		  return (JMSMessage) message;
	  } else if (message instanceof javax.jms.TextMessage) {
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

}
