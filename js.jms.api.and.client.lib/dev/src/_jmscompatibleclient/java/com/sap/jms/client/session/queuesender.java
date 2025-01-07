/**
 * QueueSender.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.session;

/**
 * @author Margarit Kirov
 * @version 1.0
 */
public class QueueSender extends MessageProducer implements javax.jms.QueueSender {
  
  protected QueueSender(javax.jms.Queue destination, long producerID, JMSSession session) throws javax.jms.JMSException {
    super(destination, producerID, session);  
  }
    
/* (non-Javadoc)
 * @see javax.jms.QueueSender#send(Queue, Message)
 */
  public void send(javax.jms.Queue queue, javax.jms.Message message) throws javax.jms.JMSException {
    super.send(queue, message);
  }
  
/* (non-Javadoc)
 * @see javax.jms.QueueSender#send(Queue, Message, int, int, long)
 */
  public void send(javax.jms.Queue queue, javax.jms.Message message, int deliveryMode, int priority, long timeToLive) throws javax.jms.JMSException {
    super.send(queue, message, deliveryMode, priority, timeToLive);
  }
  
/* (non-Javadoc)
 * @see javax.jms.QueueSender#getQueue()
 */
  public javax.jms.Queue getQueue() throws javax.jms.JMSException {
    attemptToUse();
    return (javax.jms.Queue)destination;
  }

}
