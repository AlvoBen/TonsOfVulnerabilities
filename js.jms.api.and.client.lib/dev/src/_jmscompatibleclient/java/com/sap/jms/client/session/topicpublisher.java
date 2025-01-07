/**
 * TopicPublisher.java
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
public class TopicPublisher extends MessageProducer implements javax.jms.TopicPublisher {
  
  protected TopicPublisher(javax.jms.Topic destination, long producerID, JMSSession session) throws javax.jms.JMSException {
    super(destination, producerID, session);
  }  
    
  public javax.jms.Topic getTopic() throws javax.jms.JMSException {
    attemptToUse();
    return (javax.jms.Topic)destination;
  }
  
  public void publish(javax.jms.Message message) throws javax.jms.JMSException {
    super.send(message);
  }
  
  public void publish(javax.jms.Message message, int deliveryMode, int priority, long timeToLive) throws javax.jms.JMSException {
    super.send(message, deliveryMode, priority, timeToLive);
  }
             
  public void publish(javax.jms.Topic topic, javax.jms.Message message) throws javax.jms.JMSException {
    super.send(topic, message);
  }
  
  public void publish(javax.jms.Topic topic, javax.jms.Message message, int deliveryMode, int priority, long timeToLive) throws javax.jms.JMSException {
    super.send(topic, message, deliveryMode, priority, timeToLive);
  }

}
