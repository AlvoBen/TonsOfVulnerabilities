package com.sap.jms.client.rmi;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

import com.sap.jms.client.connection.DestinationInfo;
import com.sap.jms.client.connection.Subscription;
import com.sap.jms.client.connection.ServerFacade.DelayedDeliveryData;
import com.sap.jms.client.message.JMSMessage;
import com.sap.jms.client.session.JMSXAException;
import com.sap.engine.services.rmi_p4.P4RuntimeException;
import static com.sap.jms.client.connection.DestinationInfo.DestinationType;

import com.sap.jms.protocol.Packet;

public class RMIServerFacadeImpl implements RMIServerFacade {
	
	RMIServerFacade server = null;

	public RMIServerFacadeImpl(RMIServerFacade server) {
		this.server = server;
	}

	/**
	 * Closes the connection with the specified connection id. 
	 * @param connectionId		The id of the connection
	 * @throws JMSException
	 */
	public void connectionClose(long connectionId) throws JMSException {
		server.connectionClose(connectionId); 
	}

	/**
	 * Starts the connection with the specified connection id.
	 * @param connectionId		The id of the connection
	 * @throws JMSException
	 */
	public void connectionStart(long connectionId) throws JMSException {
		server.connectionStart(connectionId); 
	}
	
	/**
	 * Stops the connection with the specified connection id.
	 * @param connectionId		The id of the connection
	 * @throws JMSException
	 */
	public void connectionStop(long connectionId) throws JMSException {
		server.connectionStop(connectionId); 
	}

	/**
	 * Creates a new consumer.
	 * @param sessionId			The ID of the session to which the consumer is associated with
	 * @param subscriptionName	The name of a durable subscription; <code>null</code> if none
	 * @param destinationName	The name of the destination
	 * @param destinationType	The type of the destination; 0 queue; 1 topic
	 * @param noLocal			The noLocal flags; used for topic subscribers only
	 * @param messageSelector	The JMS message filter string; <code>null</code> if none
	 * @param clientLimit		The size of the buffer of the consumer reserved for incoming messages
	 * @return					Returns the id of the newly created consumer
	 * @throws JMSException
	 */
	public void consumerCreate(Subscription subscription) throws JMSException {
		server.consumerCreate(subscription);		 
	}
	
	/**
	 * Closes the specified consumer.
	 * @param sessionId			The id of the session
	 * @param consumerId		The id of the consumer
	 * @param consumedMessages	Number of consumed messages by the consumer
	 * @throws JMSException
	 */
	public void consumerClose(long connectionId, int sessionId, long consumerId, int consumedMessages) throws JMSException {
		server.consumerClose(connectionId, sessionId, consumerId, consumedMessages);
	}
	
	/**
	 * Send a signal to the JMS provider to provide messages (if available) for the consumer.
	 * @param sessionId			The id of the session
	 * @param consumerId		The id of the consumer
	 * @param destinationId		The id of the destination
	 * @throws JMSException
	 */
	public void consumerRefresh(long connectionId, int sessionId, long consumerId) throws JMSException {
		server.consumerRefresh(connectionId, sessionId, consumerId);
	}

	/**
	 * Used by consumers to signal that they are ready to consume messages.
	 * @param destinationId		The id of the destination
	 * @param consumerId		The id of the consumer
	 * @param clientLimit		The size of the buffer of the consumer reserved for incoming messages
	 * @param consumedMessages	The size of the memory allocated for the consumer
	 * @throws JMSException
	 */
	public void startMessageDelivery(long connectionId, int sessionId, long consumerId, int clientLimit, int consumedMessages) throws JMSException {
		server.startMessageDelivery(connectionId, sessionId, consumerId, clientLimit, consumedMessages);
	}
	
	/**
	 * Removes the specified subscriber. 
	 * @param subscriptionName	The name of the subscriber to be removed
	 * @throws JMSException
	 */
	public void subscriptionRemove(long connectionId, String subscriptionName) throws JMSException {
		server.subscriptionRemove(connectionId, subscriptionName);
	}

	/**
	 * Creates a new producer.
	 * @param sessionId			The ID of the session to which the producer is associated with
	 * @param destinationName	The name of the destination
	 * @param destinationType	The type of the destination; 0 Queue, 1 Topic
	 * @return					Returns the id of the newly created producer
	 */
	public long producerCreate(long connectionId, int sessionId, int destinationId) throws JMSException {
		long producerId = server.producerCreate(connectionId, sessionId, destinationId);
		return producerId;
	}

	/**
	 * Closes the specified producer.
	 * @param sessionId		The id of the session
	 * @param producerId	The id of the producer
	 * @throws JMSException
	 */
	public void producerClose(long connectionId, int sessionId, long producerId) throws JMSException {
		server.producerClose(connectionId, sessionId, producerId);
	}
	
	/**
	 * Creates a new destination.
	 * @param connectionId		The ID of the client which is responsible for the request
	 * @param destinationName	The name of the destination
	 * @param destinationType	The type of the destination; 0 queue; 1 topic
	 * @param isTemporary		<code>true</code> if the destination is temporary;
	 * 							<code>false</code> otherwise
	 * @return					Returns the id of the newly created destination
	 * @throws JMSException
	 */
	public DestinationInfo destinationCreate(long connectionId, String destinationName, DestinationType type, boolean isTemporary) throws JMSException {
		return server.destinationCreate(connectionId, destinationName, type, isTemporary);
	}
	
	/**
	 * Deletes a (temporary) destination.
	 * @param connectionId		The id of the connection
	 * @param destinationId		The id of the destination 
	 * @throws JMSException
	 */
	public void destinationDelete(long connectionId, int destinationId) throws JMSException {
		server.destinationDelete(connectionId, destinationId);
	}

	/**
	 * Retrieves the name of the specified destination.
	 * @param destinationId		The id of the destination
	 * @return					Returns the destination descriptor of the destination
	 * @throws JMSException
	 */
	public DestinationInfo getDestinationInfo(int destinationId) throws JMSException {	
		return server.getDestinationInfo(destinationId);
	}
		
	/**
	 * Creates a new session and returns its id.
	 * @param connectionId		The id of the session
	 * @param acknowledgeMode	Acknowledge mode 0 -> TRANSACTED, 1 -> AUTO_ACKNOWLEDGE, 
	 * 							2 -> CLIENT_ACKNOWLEDGE, 3 -> DUPS_OK_ACKNOWLEDGE
	 * @param isXA				True if the session is XA session.
	 * @return					Returns the id of the newly created session
	 * @throws JMSException
	 */
	public SessionData sessionCreate(long connectionId, int acknowledgeMode, boolean isXA) throws JMSException {
		SessionData result = server.sessionCreate(connectionId, acknowledgeMode, isXA); 
		return result;
	}

	/**
	 * Starts the specified session.
	 * @param connectionId		The id of the connection
	 * @param sessionId			The id of the session
	 * @throws JMSException
	 */
	public void sessionStart(long connectionId, int sessionId) throws JMSException {
		server.sessionStart(connectionId, sessionId);
	}
	
	/**
	 * Stops the specified session.
	 * @param connectionId		The id of the connection
	 * @param sessionId			The id of the session
	 * @throws JMSException
	 */
	public void sessionStop(long connectionId, int sessionId) throws JMSException {
		server.sessionStop(connectionId, sessionId);
	}

	/**
	 * Closes the specified session.
	 * @param connectionId		The id of the connection
	 * @param sessionId			The id of the session
	 * @throws JMSException
	 */
	public void sessionClose(long connectionId, int sessionId) throws JMSException {
		server.sessionClose(connectionId, sessionId);
	}
	
	/**
	 * Sends a message to the JMS server.
	 * @param message			The message to send
	 * @throws JMSException
	 */
	public void sendMessage(JMSMessage message) throws JMSException {
		server.sendMessage(message);
	}
	
	/**
	 * Acknowledges the delivered messages.
	 * @param sessionId			The id of the session
	 * @param deliveredMessages	A map containing a list of PCounters of the 
	 * 							messages for each consumer id.
	 * @throws JMSException
	 */
	public void sessionAcknowledge(long connectionId, int sessionId, Map<Long, List<Long>> deliveredMessages) throws JMSException {
		server.sessionAcknowledge(connectionId, sessionId, deliveredMessages);
	}	

	/**
	 * Performs a commit on the specified session.
	 * @param sessionId			The id of the session
	 * @param deliveredMessages	A map containing a list of PCounters of the 
	 * 							messages for each consumer id.
	 * @throws JMSException
	 */
	public void sessionCommit(long connectionId, int sessionId, Map<Long, List<Long>> deliveredMessages) throws JMSException {
		server.sessionCommit(connectionId, sessionId, deliveredMessages);
	}

	/**
	 * Performs a after begin JTA synchronization on the specified session.
	 * @param sessionId			The id of the session
	 * @throws JMSException
	 */
	public void sessionAfterBegin(long connectionId, int sessionId) throws JMSException {
		server.sessionAfterBegin(connectionId, sessionId);
	}
	
	/**
	 * Performs a before completion JTA synchronization on the specified session.
	 * @param sessionId			The id of the session
	 * @param deliveredMessages	A map containing a list of PCounters of the 
	 * 							messages for each consumer id.
	 * @throws JMSException
	 */
	public void sessionBeforeCompletion(long connectionId, int sessionId, Map<Long, List<Long>> deliveredMessages) throws JMSException {
		server.sessionBeforeCompletion(connectionId, sessionId, deliveredMessages);
	}
	

	/**
	 * Performs a after completion JTA synchronization on the specified session.
	 * @param sessionId			The id of the session
	 * @param deliveredMessages	A map containing a list of PCounters of the 
	 * 							messages for each consumer id.
	 * @param status            The status of the JTA transaction ( failed or success )
	 * @throws JMSException
	 */
	public void sessionAfterCompletion(long connectionId, int sessionId, Map<Long, List<Long>> deliveredMessages,int status) throws JMSException {
		server.sessionAfterCompletion(connectionId, sessionId, deliveredMessages, status);
	}
	
	/**
	 * Performs a recover on the specified session.
	 * @param sessionId			The id of the session
	 * @param deliveredMessages	A map containing a list of PCounters of the 
	 * 							messages for each consumer id.
	 * @throws JMSException
	 */
	public void sessionRecover(long connectionId, int sessionId, Map<Long, List<Long>> deliveredMessages) throws JMSException {
		server.sessionRecover(connectionId, sessionId, deliveredMessages);
	}
	
	/**
	 * Performs a rollback on the specified session.
	 * @param sessionId			The id of the session
	 * @param deliveredMessages	A map containing a list of PCounters of the 
	 * 							messages for each consumer id.
	 * @throws JMSException
	 */
	public void sessionRollback(long connectionId, int sessionId, Map<Long, List<Long>> deliveredMessages) throws JMSException {
		server.sessionRollback(connectionId, sessionId, deliveredMessages);
	}

	public Map<Long, Set<DelayedDeliveryData>> messageDeliveryFailed(long connectionId, int sessionId, Map<Long, List<Long>> msgIdsPerConsumer) throws JMSException {
		return server.messageDeliveryFailed(connectionId, sessionId, msgIdsPerConsumer);
	}
	
	/**
	 * Creates a browser enumeration.
	 * @param sessionId			The id of the session
	 * @param browserId			The id of the browser
	 * @throws JMSException
	 */
	public void queueBrowserEnumerate(long connectionId, int sessionId, long browserId) throws JMSException {
		server.queueBrowserEnumerate(connectionId, sessionId, browserId);
	}
	
	/**
	 * Sends XAStartRequest to the server.
	 * @param connectionId - the connection id of the JMSSesssion under which the call is made.	 * 
	 * @param sessionId - the session id of the JMSSesssion under which the call is made.
	 * @param xid - the xid of the transaction which should be initiated/resumed.
	 * @param flags - flags of the transaction.
	 * @param timeout - the timeout for this start request in miliseconds
	 */
	public long xaStart(long connectionId, int sessionId, Xid xid, long txid) throws XAException {
		try {
			return server.xaStart(connectionId, sessionId, xid, txid);
		} catch (P4RuntimeException e) {
			JMSXAException x = new JMSXAException(e, XAException.XAER_RMERR);
			throw x;
		}
	}	

	/**
	 * Sends XAEndRequest to the server.
	 * @param connectionId - the connection id of the JMSSesssion under which the call is made.	
	 * @param sessionId - the session id of the JMSSesssion under which the call is made.	 
	 * @param xid - the xid of the transaction which should be ended/suspended.
	 * @param msgsPerDstIds - mapping between destination ids and messages which were consumed from them.
	 */	
	public void xaEnd(long connectionId, int sessionId, Xid xid) throws XAException {
		try {
			server.xaEnd(connectionId, sessionId, xid);
		} catch (P4RuntimeException e) {
			JMSXAException x = new JMSXAException(e, XAException.XAER_RMERR);
			throw x;
		}
	}

	/**
	 * Sends XACommitRequest to the server.
	 * @param connectionId - the connection id of the JMSSesssion under which the call is made.	
	 * @param sessionId - the session id of the JMSSesssion under which the call is made.	  
	 * @param xid - the xid of the transaction which should be commited.
	 * @param onePhase - indicates if the commit is single phase or not.
	 */	
	public void xaCommit(long connectionId, int sessionId, Xid xid, long txid, boolean onePhase, Map<Long, List<Long>> consumedMessages) throws XAException {
		try {
			server.xaCommit(connectionId, sessionId, xid, txid, onePhase, consumedMessages);
		} catch (P4RuntimeException e) {
			JMSXAException x = new JMSXAException(e, XAException.XAER_RMERR);
			throw x;
		}
	}

	/**
	 * Sends XARollbackRequest to the server.
	 * @param connectionId - the connection id of the JMSSesssion under which the call is made.	
	 * @param sessionId - the session id of the JMSSesssion under which the call is made.	  
	 * @param xid - the xid of the transaction which should be rolled back.
	 */	
	public void xaRollback(long connectionId, int sessionId, Xid xid, long txid, boolean onePhase, Map<Long, List<Long>> consumedMessages) throws XAException {
		try {
			server.xaRollback(connectionId, sessionId, xid, txid, onePhase, consumedMessages);
		} catch (P4RuntimeException e) {
			JMSXAException x = new JMSXAException(e, XAException.XAER_RMERR);
			throw x;
		}
	}

	/**
	 * Sends XAForgetRequest to the server.
	 * @param xid - the xid of the transaction which should be forgotten on the server.
	 */	
	public void xaForget(Xid xid) throws XAException {
		try {
			server.xaForget(xid);
		} catch (P4RuntimeException e) {
			JMSXAException x = new JMSXAException(e, XAException.XAER_RMERR);
			throw x;
		}
	}

	/**
	 * Sends XAPrepareRequest to the server.
	 * @param xid - the xid of the transaction which should be prepared on the server.
	 * @return - the vote on the commit request from the server.
	 */	
	public int xaPrepare(long connectionId, int sessionId, Xid xid, long txid, Map<Long, List<Long>> consumedMessages) throws XAException {
		int vote = 0;
		
		try {		
			vote = server.xaPrepare(connectionId, sessionId, xid, txid, consumedMessages);
		} catch (P4RuntimeException e) {
			JMSXAException x = new JMSXAException(e, XAException.XAER_RMERR);
			throw x;
		}
		return vote;
	}

	/**
	 * Sends XARecoverRequest to the server.
	 * @param flags - One of TMSTARTRSCAN, TMENDRSCAN, TMNOFLAGS.
	 * @return - list of xids in prepared or heuristically completed states.
	 */	
	public Set<Xid> xaRecover(int flags) throws XAException {
		Set<Xid> xids = null;
		
		try {		
			xids = server.xaRecover(flags);
		} catch (P4RuntimeException e) {
			JMSXAException x = new JMSXAException(e, XAException.XAER_RMERR);
			throw x;
		}
		return xids;
	}
	

	/**
	 * Retrieves the current active timeout value from the server.
	 * @return active timeout in miliseconds.
	 */ 
	public TimeoutData xaGetTimeout(int sessionId) throws XAException {
		TimeoutData result;
		
		try {
			result = server.xaGetTimeout(sessionId);
		} catch (P4RuntimeException e) {
			JMSXAException x = new JMSXAException(e, XAException.XAER_RMERR);
			throw x;
		}
		return result;
	}

}
