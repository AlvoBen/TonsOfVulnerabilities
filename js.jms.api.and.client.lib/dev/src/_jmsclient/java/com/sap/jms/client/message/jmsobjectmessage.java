/**
 * ObjectMessage.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.message;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.MessageNotWriteableException;

import com.sap.jms.protocol.MessageRequest;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.util.Logging;
import com.sap.jms.JMSConstants;


public final class JMSObjectMessage extends JMSMessage implements javax.jms.ObjectMessage {

 /** An <CODE>ObjectMessage</CODE> object is used to send a message that contains
  * a serializable object in the Java programming language ("Java object").
  * It inherits from the <CODE>Message</CODE> interface and adds a body
  * containing a single reference to an object. Only <CODE>Serializable</CODE> 
  * Java objects can be used.
  *
  * <P>If a collection of Java objects must be sent, one of the 
  * <CODE>Collection</CODE> classes provided since JDK 1.2 can be used.
  *
  * <P>When a client receives an <CODE>ObjectMessage</CODE>, it is in read-only 
  * mode. If a client attempts to write to the message at this point, a 
  * <CODE>MessageNotWriteableException</CODE> is thrown. If 
  * <CODE>clearBody</CODE> is called, the message can now be both read from and 
  * written to.
  *
  *
  * @see         javax.jms.Session#createObjectMessage()
  * @see         javax.jms.Session#createObjectMessage(Serializable)
  * @see         javax.jms.BytesMessage
  * @see         javax.jms.MapMessage
  * @see         javax.jms.Message
  * @see         javax.jms.StreamMessage
  * @see         javax.jms.TextMessage
  */
	
  public JMSObjectMessage() throws JMSException {
    super(PacketTypes.JMS_OBJECT_MESSAGE);
    setMode(RW_MODE);
  }
  
  public JMSObjectMessage(MessageRequest messagePacket) throws JMSException {
    super(messagePacket);
  }
  
  protected JMSObjectMessage(javax.jms.ObjectMessage alien) throws JMSException {
    super(PacketTypes.JMS_OBJECT_MESSAGE);
    setMode(RW_MODE);
    copyPropertiesFrom(alien);
    setObject(alien.getObject());
  }
  
  /* (non-Javadoc)
   * @see javax.jms.Message#clearBody()
   */
  public void clearBody() throws JMSException {
    setMode(RW_MODE);    
    MessageRequest oldMessagePacket = messagePacket;
    messagePacket = new MessageRequest(PacketTypes.JMS_OBJECT_MESSAGE, MessageRequest.SIZE, connectionID, sessionID);
/*    
    try {
      messageProperties = new MessageProperties(oldMessagePacket);
    } catch (JMSException e) {
      messageProperties = new MessageProperties();
    }
*/    
  }

  /**
   *  Makes a copy of the message.
   *
   * @return  a copy of this message.
   */
  public Object clone() {
    try {    
      if (messageProperties != null) {
        messagePacket.setMessageProperties(messageProperties.getPropertiesTable());
      }
      
      return new JMSObjectMessage((MessageRequest)messagePacket.clone());
    } catch (JMSException e) {
      Logging.exception(this, e, "Could not clone ObjectMessage.");
      return null;
    }
  }
  
/** Gets the serializable object containing this message's data. The 
  * default value is null.
  *
  * @return the serializable object containing this message's data
  *  
  * @exception JMSException if the JMS provider fails to get the object
  *                         due to some internal error.
  * @exception MessageFormatException if object deserialization fails.
  */
  public Serializable getObject() throws JMSException {
    return (Serializable)messagePacket.getMessageBody();
  }
      
/** Sets the serializable object containing this message's data.
  * It is important to note that an <CODE>ObjectMessage</CODE>
  * contains a snapshot of the object at the time <CODE>setObject()</CODE>
  * is called; subsequent modifications of the object will have no 
  * effect on the <CODE>ObjectMessage</CODE> body.
  *
  * @param object the message's data
  *  
  * @exception JMSException if the JMS provider fails to set the object
  *                         due to some internal error.
  * @exception MessageFormatException if object serialization fails.
  * @exception MessageNotWriteableException if the message is in read-only
  *                                         mode.
  */
  public void setObject(Serializable object) throws JMSException {
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }

    flushToBuffer(object);
  }
  
  /**
   * Method flush. Used by senders to perform serialization of the message body 
   * into the message buffer.
   */
  public void flush() throws JMSException {
    flushToBuffer(null);
  }

}