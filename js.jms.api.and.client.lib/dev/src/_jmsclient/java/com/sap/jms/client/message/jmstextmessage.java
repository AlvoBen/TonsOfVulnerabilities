/**
 * TextMessage.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.message;

import javax.jms.JMSException;
import javax.jms.MessageNotWriteableException;

import com.sap.jms.protocol.MessageRequest;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.util.Logging;
import com.sap.jms.JMSConstants;


/**
 *  A TextMessage is used to send a message containing a java.lang.String. It inherits
 * from Message and adds a text message body.
 *
 *  The inclusion of this message type is based on our presumption that XML will likely
 * become a popular mechanism for representing content of all kinds including the
 * content of JMS messages.
 *
 *  When a client receives a TextMessage, it is in read-only mode. If a client attempts
 * to write to the message at this point, a MessageNotWriteableException is
 * thrown. If clearBody is called, the message can now be both read from and written to.
 */
public final class JMSTextMessage extends JMSMessage implements javax.jms.TextMessage {

  /////
  //  contains the text of the message
  private String body = null;
  boolean flushed = false;

  /**
   *  Creates an empty text message.
   */
  public JMSTextMessage() throws JMSException {
    super(PacketTypes.JMS_TEXT_MESSAGE);
    setMode(RW_MODE);
  }
  
  public JMSTextMessage(MessageRequest messagePacket) throws JMSException {
    super(messagePacket);
    flushed = true;
  }

  protected JMSTextMessage(javax.jms.TextMessage message) throws JMSException {
    super(PacketTypes.JMS_TEXT_MESSAGE);
    copyPropertiesFrom(message);
    body = message.getText();
    setMode(RW_MODE);
  }

  /**
   * Clear out the message body. Clearing a message's body does not clear its header values or property entries.

   * If this message body was read-only, calling this method leaves the message body is in the same state as an empty body in a newly created message.
   *
   * @exception  JMSException - if JMS fails to due to some internal JMS error.
   */
	public void clearBody() throws JMSException {
    setMode(RW_MODE);
    
    if (flushed) {
      MessageRequest oldMessagePacket = messagePacket;
      messagePacket = new MessageRequest(PacketTypes.JMS_TEXT_MESSAGE, MessageRequest.SIZE, connectionID, sessionID);
      
      messageProperties = new MessageProperties(oldMessagePacket);

      flushed = false;
    }
    
    body = null;
	}

  /**
   *  Makes a copy of the message.
   *
   * @return  a copy of this message.
   */
  public Object clone() {
    try {
      if (body != null) {
        messagePacket.setMessageBodyAndType(body, PacketTypes.JMS_TEXT_MESSAGE);
      }
      
      if (messageProperties != null) {
        messagePacket.setMessageProperties(messageProperties.getPropertiesTable());
      }
      
      return new JMSTextMessage((MessageRequest)messagePacket.clone());
    } catch (JMSException e) {
      Logging.exception(this, e, "Could not clone TextMessage.");
      return null;
    }
  }


  /**
   *  Gets the string containing this message's data.  The default value is null.
   *
   * @return the <CODE>String</CODE> containing the message's data
   *
   * @exception JMSException if the JMS provider fails to get the text due to some internal error.
   */
  public String getText() throws JMSException {
    if (flushed) {
      body = (String)messagePacket.getMessageBody(); 
    }
    
    return body;
  }


  /**
   *  Sets the string containing this message's data.
   *
   * @param string the <CODE>String</CODE> containing the message's data
   *
   * @exception JMSException if the JMS provider fails to set the text due to some internal error.
   * @exception MessageNotWriteableException if the message is in read-only mode.
   */
  public void setText(String string) throws JMSException {
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }

		body = string;
        flushed = false;    
	}
  
  /**
   * Method flush. Used by senders to perform serialization of the message body 
   * into the message buffer.
   */
  public void flush() throws JMSException {
    flushToBuffer(body);
    flushed = true;
  }

}
