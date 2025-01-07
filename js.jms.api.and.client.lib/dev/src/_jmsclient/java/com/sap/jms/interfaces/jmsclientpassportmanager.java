/*
 * Created on 2004-10-22
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.jms.interfaces;

/**
 * @author desislav-b
 *
 */
public interface JMSClientPassportManager {
	/**
	* Method onSend.
	* Called in class:    com.sap.jms.client.session.MessageProducer
	* 	method: send(javax.jms.Destination destination, javax.jms.Message clientMessage, int deliveryMode, int priority, long timeToLive)
    * @param bytesToSend 		bytesToSend
    * @param vpName 			vpName
    * @param destinationName 	destinationName
	*/	
	byte[] onSend(int bytesToSend, String vpName, String destinationName);
	
	/**
	* Method beforeOnMessage.
	* Called in class:	com.sap.jms.client.session.Session
	* 	method: run()
	* @param dsrPassport		dsrPassport
    * @param receivedBytes		receivedBytes
    * @param vpName 			vpName
    * @param destinationName 	destinationName
	*/		
	void beforeOnMessage(byte[] dsrPassport, int receivedBytes, String vpName, String destinationName);
	
	/**
	* Method afterOnMessage.
	* Called in class:	com.sap.jms.client.session.Session
	* 	method: run()
	*/	
	void afterOnMessage();
	
	/**
	* Method beforeEnqueue.
	* Called in class:	com.sap.jms.server.destinationcontainer.messagequeue.impl.pubsub.QueuingEnginePubSubImpl
	* 	methods:   
	* 		enqueue(MessageItem messageItem),
	*		enqueue(MessageItem messageItem, long tx_id, boolean updateInMemory) 
	*    
	* 	class:	com.sap.jms.server.destinationcontainer.messagequeue.impl.ptp.QueuingEnginePTPImpl
	* 	methods:    
	* 		enqueue(MessageItem messageItem),
	*		enqueue(MessageItem messageItem, long tx_id, boolean updateInMemory)
	* 
	* @param dsrPassport		dsrPassport
    * @param receivedBytes		receivedBytes
    * @param vpName 			vpName
    * @param destinationName 	destinationName
	*/	
	void beforeEnqueue(byte[] dsrPassport, int receivedBytes, String vpName, String destinationName);
	
	/**
	* Method afterEnqueue.
	* Called in class:	com.sap.jms.server.destinationcontainer.messagequeue.impl.pubsub.QueuingEnginePubSubImpl
	* 	methods:   
	* 		enqueue(MessageItem messageItem),
	*		enqueue(MessageItem messageItem, long tx_id, boolean updateInMemory) 
	*
	* 	class:	com.sap.jms.server.destinationcontainer.messagequeue.impl.ptp.QueuingEnginePTPImpl
	*	methods:    
	*		enqueue(MessageItem messageItem),
	* 		enqueue(MessageItem messageItem, long tx_id, boolean updateInMemory)
	*/	
	void afterEnqueue();
	
	/**
	* Method beforePeek.
	* @param dsrPassport		dsrPassport
    * @param receivedBytes		receivedBytes
    * @param vpName 			vpName
    * @param destinationName 	destinationName
    * @deprecated
	*/	
	void beforePeek(byte[] dsrPassport, int receivedBytes, String vpName, String destinationName);

	/**
	* Method afterPeek.
	* @deprecated
	*/	
	void afterPeek();
	
	/**
	* Method beforeCleanup.
	* Called in class:	com.sap.jms.server.destinationcontainer.service.impl
	* 	method: cleanup()
    * @param vpName 			vpName
	*/	
	void beforeCleanup(String vpName);

	/**
	* Method afterCleanup.
	* Called in class:	com.sap.jms.server.destinationcontainer.service.impl
	* 	method: cleanup()
    * @param vpName 			vpName
	*/					
	void afterCleanup(String vpName);	
	
	/**
	 * Called by the jms runtime (client) before sending a message.
	 * The dsr passport returned by this method is then sent in the properties of the
	 * message serialized as a hex string. 
	 * @return dsr passport
	 */
	byte[] beforeSend(int bytesToSend, String vpName, String destinationName);
	
	/**
	 * Called by the jms runtime (client) after sending a message.
	 */
	void afterSend();
}
