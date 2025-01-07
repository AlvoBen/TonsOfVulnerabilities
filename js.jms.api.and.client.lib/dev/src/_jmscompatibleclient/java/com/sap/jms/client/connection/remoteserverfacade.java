package com.sap.jms.client.connection;

import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

import com.sap.jms.client.message.JMSMessage;
import com.sap.jms.protocol.JMSXAException;
import com.sap.jms.protocol.Packet;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.notification.*;

public class RemoteServerFacade implements ServerFacade {

	protected NetworkAdapter networkAdapter;

	public RemoteServerFacade(NetworkAdapter networkAdapter) {
		this.networkAdapter = networkAdapter;
	}

	// !!!
	public long connectionCreate(String vpName, byte[] certificate, String username, String password, String connectionFactoryName) throws JMSException {
		Packet request = new ConnectionCreateRequest(vpName, username, password, connectionFactoryName);		
		ConnectionCreateResponse response = (ConnectionCreateResponse) sendPacket(request);
		long connectionId = response.getConnectionID();
		return connectionId;
	}
	/**
	 * Closes the connection with the specified connection id. 
	 * @param connectionId		The id of the connection
	 * @throws JMSException
	 */
	public void connectionClose(long connectionId) throws JMSException {
		Packet request = new ConnectionCloseRequest(connectionId);
		sendPacket(request);
	}

	/**
	 * Starts the connection with the specified connection id.
	 * @param connectionId		The id of the connection
	 * @throws JMSException
	 */
	public void connectionStart(long connectionId) throws JMSException {
		Packet request = new ConnectionStartRequest(connectionId);
		sendPacket(request);
	}
	
	/**
	 * Stops the connection with the specified connection id.
	 * @param connectionId		The id of the connection
	 * @throws JMSException
	 */
	public void connectionStop(long connectionId) throws JMSException {
		Packet request = new ConnectionStopRequest(connectionId);
		sendPacket(request);
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
	public long consumerCreate(int sessionId, String subscriptionName, String destinationName, 
			byte destinationType, boolean noLocal, String messageSelector, int clientLimit) throws JMSException {
		
		Packet request = new ConsumerCreateRequest(sessionId, subscriptionName, destinationName, destinationType, noLocal, messageSelector, clientLimit);
		ConsumerCreateResponse response = (ConsumerCreateResponse) sendPacket(request);
		long consumerId = response.getConsumerID();
		return consumerId;
	}
	
	/**
	 * Closes the specified consumer.
	 * @param sessionId			The id of the session
	 * @param consumerId		The id of the consumer
	 * @param consumedMessages	Number of consumed messages by the consumer
	 * @throws JMSException
	 */
	public void consumerClose(int sessionId, long consumerId, int consumedMessages) throws JMSException {
		Packet request = new ConsumerCloseRequest(sessionId, consumerId, consumedMessages);
		sendPacket(request);
	}
	
	/**
	 * Send a signal to the JMS provider to provide messages (if available) for the consumer.
	 * @param sessionId			The id of the session
	 * @param consumerId		The id of the consumer
	 * @param destinationId		The id of the destination
	 * @throws JMSException
	 */
	public void consumerRefresh(int sessionId, long consumerId, int destinationId) throws JMSException {
		Packet request = new ConsumerRefreshRequest(sessionId, consumerId, destinationId);
		sendPacket(request);
	}

	/**
	 * Used by consumers to signal that they are ready to consume messages.
	 * @param destinationId		The id of the destination
	 * @param consumerId		The id of the consumer
	 * @param clientLimit		The size of the buffer of the consumer reserved for incoming messages
	 * @param consumedMessages	The size of the memory allocated for the consumer
	 * @throws JMSException
	 */
	public void startMessageDelivery(int destinationId, long consumerId, int clientLimit, int consumedMessages) throws JMSException {
		Packet request = new StartMessageDeliveryRequest(destinationId, consumerId, clientLimit, consumedMessages);
		sendPacket(request);
	}
	
	/**
	 * Removes the specified subscriber. 
	 * @param subscriptionName	The name of the subscriber to be removed
	 * @throws JMSException
	 */
	public void subscriptionRemove(String subscriptionName) throws JMSException {
		Packet request = new SubscriptionRemoveRequest(subscriptionName);
		sendPacket(request);
	}

	/**
	 * Creates a new producer.
	 * @param sessionId			The ID of the session to which the producer is associated with
	 * @param destinationName	The name of the destination
	 * @param destinationType	The type of the destination; 0 Queue, 1 Topic
	 * @return					Returns the id of the newly created producer
	 */
	public long producerCreate(int sessionId, String destinationName, byte destinationType) throws JMSException {
		Packet request = new ProducerCreateRequest(sessionId, destinationName, destinationType);
		ProducerCreateResponse response = (ProducerCreateResponse)sendPacket(request);
		long producerId = response.getProducerID();
		return producerId;
	}

	/**
	 * Closes the specified producer.
	 * @param sessionId		The id of the session
	 * @param producerId	The id of the producer
	 * @throws JMSException
	 */
	public void producerClose(int sessionId, long producerId) throws JMSException {
		Packet request = new ProducerCloseRequest(sessionId, producerId);
		sendPacket(request);
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
	public DestinationDescriptor destinationCreate(long connectionId, String destinationName, byte destinationType, boolean isTemporary) throws JMSException {
		Packet request = new DestinationCreateRequest(connectionId, destinationName, destinationType, isTemporary);
		DestinationCreateResponse response = (DestinationCreateResponse)sendPacket(request);
		DestinationDescriptor result = new DestinationDescriptor();
		result.setDestinationId(response.getDestinationID());
		result.setDestinationName(response.getDestinationName());
		return result;
	}
	
	/**
	 * Deletes a (temporary) destination.
	 * @param connectionId		The id of the connection
	 * @param destinationId		The id of the destination 
	 * @throws JMSException
	 */
	public void destinationDelete(long connectionId, int destinationId) throws JMSException {
		Packet request = new DestinationDeleteRequest(connectionId, destinationId);
		sendPacket(request);
	}

	/**
	 * Retrieves the name of the specified destination.
	 * @param destinationId		The id of the destination
	 * @return					Returns the name of the destination
	 * @throws JMSException
	 */
	public String destinationName(int destinationId) throws JMSException {
		Packet request = new DestinationNameRequest(destinationId);
		DestinationNameResponse response = (DestinationNameResponse) sendPacket(request);
		String name = response.getName();
		return name;
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
	public SessionDescriptor sessionCreate(long connectionId, byte acknowledgeMode, boolean isXA) throws JMSException {
		Packet request = new SessionCreateRequest(connectionId, acknowledgeMode, isXA);
		SessionCreateResponse response = (SessionCreateResponse)sendPacket(request);
		SessionDescriptor result = new SessionDescriptor();
		result.setSessionId(response.getSessionID());
		result.setMessageIdBase(response.getConnectionID());
		return result;
	}

	/**
	 * Starts the specified session.
	 * @param connectionId		The id of the connection
	 * @param sessionId			The id of the session
	 * @throws JMSException
	 */
	public void sessionStart(long connectionId, int sessionId) throws JMSException {
		Packet request = new SessionStartRequest(connectionId, sessionId);
		sendPacket(request);
	}
	
	/**
	 * Stops the specified session.
	 * @param connectionId		The id of the connection
	 * @param sessionId			The id of the session
	 * @throws JMSException
	 */
	public void sessionStop(long connectionId, int sessionId) throws JMSException {
		Packet request = new SessionStopRequest(connectionId, sessionId);
		sendPacket(request);
	}

	/**
	 * Closes the specified session.
	 * @param connectionId		The id of the connection
	 * @param sessionId			The id of the session
	 * @throws JMSException
	 */
	public void sessionClose(long connectionId, int sessionId) throws JMSException {
		Packet request = new SessionCloseRequest(connectionId, sessionId);
		sendPacket(request);
	}
	
	/**
	 * Sends a message to the JMS server.
	 * @param message			The message to send
	 * @throws JMSException
	 */
	public void sendMessage(JMSMessage message) throws JMSException {
		message.flush();
		Packet request = message.getMessagePacket();
		sendPacket(request);	
	}	
	
	/**
	 * Acknowledges the delivered messages.
	 * @param sessionId			The id of the session
	 * @param deliveredMessages	A map containing a list of PCounters of the 
	 * 							messages for each consumer id.
	 * @throws JMSException
	 */
	public void sessionAcknowledge(int sessionId, Map/*<Long, Set<Long>>*/ deliveredMessages) throws JMSException {
		Packet request = new MessageAcknowledgeRequest(sessionId, deliveredMessages);
		sendPacket(request);
	}	

	/**
	 * Performs a commit on the specified session.
	 * @param sessionId			The id of the session
	 * @param deliveredMessages	A map containing a list of PCounters of the 
	 * 							messages for each consumer id.
	 * @throws JMSException
	 */
	public void sessionCommit(int sessionId, Map/*<Long, Set<Long>>*/ deliveredMessages) throws JMSException {
		Packet request = new SessionCommitRequest(sessionId, deliveredMessages);
		sendPacket(request);
	}

	/**
	 * Performs a after begin JTA synchronization on the specified session.
	 * @param sessionId			The id of the session
	 * @throws JMSException
	 */
	public void sessionAfterBegin(int sessionId) throws JMSException {
		Packet request = new SessionAfterBeginRequest(sessionId);
		sendPacket(request);
	}
	
	/**
	 * Performs a before completion JTA synchronization on the specified session.
	 * @param sessionId			The id of the session
	 * @param deliveredMessages	A map containing a list of PCounters of the 
	 * 							messages for each consumer id.
	 * @throws JMSException
	 */
	public void sessionBeforeCompletion(int sessionId, Map/*<Long, Set<Long>>*/ deliveredMessages) throws JMSException {
		Packet request = new SessionBeforeCompletionRequest(sessionId, deliveredMessages);
		sendPacket(request);
	}
	

	/**
	 * Performs a after completion JTA synchronization on the specified session.
	 * @param sessionId			The id of the session
	 * @param deliveredMessages	A map containing a list of PCounters of the 
	 * 							messages for each consumer id.
	 * @param status            The status of the JTA transaction ( failed or success )
	 * @throws JMSException
	 */
	public void sessionAfterCompletion(int sessionId, Map/*<Long, Set<Long>>*/ deliveredMessages,int status) throws JMSException {
		Packet request = new SessionAfterCompletionRequest(sessionId, deliveredMessages,status);
		sendPacket(request);
	}
	
	/**
	 * Performs a recover on the specified session.
	 * @param sessionId			The id of the session
	 * @param deliveredMessages	A map containing a list of PCounters of the 
	 * 							messages for each consumer id.
	 * @throws JMSException
	 */
	public void sessionRecover(int sessionId, Map/*<Long, Set<Long>>*/ deliveredMessages) throws JMSException {
		Packet request = new SessionRecoverRequest(sessionId, deliveredMessages);
		sendPacket(request);
	}
	
	/**
	 * Performs a rollback on the specified session.
	 * @param sessionId			The id of the session
	 * @param deliveredMessages	A map containing a list of PCounters of the 
	 * 							messages for each consumer id.
	 * @throws JMSException
	 */
	public void sessionRollback(int sessionId, Map/*<Long, Set<Long>>*/ deliveredMessages) throws JMSException {
		Packet request = new SessionRollbackRequest(sessionId, deliveredMessages);
		sendPacket(request);
	}
	
	/**
	 * Creates a queue browser and returns the browser id.
	 * @param sessionId			The id of the session
	 * @param queueName			The name of the queue
	 * @param messageSelector	Message selector
	 * @param clientLimit		The size of the buffer of the consumer reserved for incoming messages
	 * @return					Returns the id of the newly created browser
	 * @throws JMSException
	 */
	public long queueBrowserCreate(int sessionId, String queueName, String messageSelector, int clientLimit) throws JMSException {
		Packet request = new QueueBrowserCreateRequest(sessionId, queueName, messageSelector, clientLimit);	
		QueueBrowserCreateResponse response = (QueueBrowserCreateResponse)sendPacket(request);
		long browserId = response.getBrowserID();
		return browserId;
	}

	/**
	 * Creates a browser enumeration.
	 * @param sessionId			The id of the session
	 * @param browserId			The id of the browser
	 * @throws JMSException
	 */
	public void queueBrowserEnumerate(int sessionId, long browserId) throws JMSException {
		Packet request = new QueueBrowserEnumerationRequest(sessionId, browserId);
		sendPacket(request);
	}
	
	/**
	 * Closes the specified queue browser.
	 * @param sessionId			The id of the session
	 * @param browserId			The id of the browser
	 * @throws JMSException
	 */
	public void queueBrowserClose(int sessionId, long browserId) throws JMSException {
		Packet request = new QueueBrowserCloseRequest(sessionId, browserId);
		sendPacket(request);
	}

	/**
	 * Sends XAStartRequest to the server.
	 * @param sessionId - the session id of the JMSSesssion under which the call is made.
	 * @param xid - the xid of the transaction which should be initiated/resumed.
	 * @param flags - flags of the transaction.
	 * @param timeout - the timeout for this start request in miliseconds
	 */
	public void xaStart(int sessionId, Xid xid, int flags, long activeTimeout) throws XAException {
		try {
			Packet request = new XAStartRequest(xid, flags, sessionId, activeTimeout);
			sendPacket(request);
		} catch (JMSXAException e) {
			XAException xe = ((JMSXAException)e).getXaException();
			throw xe;
		} catch (JMSException e) {
			XAException xe = new XAException(XAException.XAER_RMERR);
			xe.initCause(e);
			throw xe;
		}
	}

	/**
	 * Sends XAEndRequest to the server.
	 * @param xid - the xid of the transaction which should be ended/suspended.
	 * @param msgsPerDstIds - mapping between destination ids and messages which were consumed from them.
	 */	
	public void xaEnd(Xid xid, int flags, Map/*<Long, Set<Long>>*/ msgsPerConsumerIds) throws XAException {
		try {		
			Packet request = new XAEndRequest(xid, flags, msgsPerConsumerIds);
			sendPacket(request);
		} catch (JMSXAException e) {
			XAException xe = ((JMSXAException)e).getXaException();
			throw xe;
		} catch (JMSException e) {
			XAException xe = new XAException(XAException.XAER_RMERR);
			xe.initCause(e);
			throw xe;
		}
	}

	/**
	 * Sends XACommitRequest to the server.
	 * @param xid - the xid of the transaction which should be commited.
	 * @param onePhase - indicates if the commit is single phase or not.
	 */	
	public void xaCommit(Xid xid, boolean onePhase) throws XAException {
		try {		
			Packet request = new XACommitRequest(xid, onePhase);
			sendPacket(request);
		} catch (JMSXAException e) {
			XAException xe = ((JMSXAException)e).getXaException();
			throw xe;
		} catch (JMSException e) {
			XAException xe = new XAException(XAException.XAER_RMERR);
			xe.initCause(e);
			throw xe;
		}
	}

	/**
	 * Sends XARollbackRequest to the server.
	 * @param xid - the xid of the transaction which should be rolled back.
	 */	
	public void xaRollback(Xid xid) throws XAException {
		try {		
			Packet request = new XARollbackRequest(xid);
			sendPacket(request);
		} catch (JMSXAException e) {
			XAException xe = ((JMSXAException)e).getXaException();
			throw xe;
		} catch (JMSException e) {
			XAException xe = new XAException(XAException.XAER_RMERR);
			xe.initCause(e);
			throw xe;
		}
	}

	/**
	 * Sends XAForgetRequest to the server.
	 * @param xid - the xid of the transaction which should be forgottten on the server.
	 */	
	public void xaForget(Xid xid) throws XAException {
		try {		
			Packet request = new XAForgetRequest(xid);
			sendPacket(request);
		} catch (JMSXAException e) {
			XAException xe = ((JMSXAException)e).getXaException();
			throw xe;
		} catch (JMSException e) {
			XAException xe = new XAException(XAException.XAER_RMERR);
			xe.initCause(e);
			throw xe;
		}
	}

	/**
	 * Sends XAPrepareRequest to the server.
	 * @param xid - the xid of the transaction which should be prepared on the server.
	 * @return - the vote on the commit request from the server.
	 */	
	public int xaPrepare(Xid xid) throws XAException {
		int vote = 0;
		
		try {		
			Packet request = new XAPrepareRequest(xid);
			XAPrepareResponse response = (XAPrepareResponse)sendPacket(request);
			vote = response.getResult();
		} catch (JMSXAException e) {
			XAException xe = ((JMSXAException)e).getXaException();
			throw xe;
		} catch (JMSException e) {
			XAException xe = new XAException(XAException.XAER_RMERR);
			xe.initCause(e);
			throw xe;
		}

		return vote;
	}

	/**
	 * Sends XARecoverRequest to the server.
	 * @param flags - One of TMSTARTRSCAN, TMENDRSCAN, TMNOFLAGS.
	 * @return - list of xids in prepared or heuristically completed states.
	 */	
	public Set/*<Xid>*/ xaRecover(int flags) throws XAException {
		Set/*<Xid>*/ xids = null;
		
		try {		
			Packet request = new XARecoverRequest(flags);
			XARecoverResponse response = (XARecoverResponse)sendPacket(request);
			xids = response.getXIDs();
		} catch (JMSXAException e) {
			XAException xe = ((JMSXAException)e).getXaException();
			throw xe;
		} catch (JMSException e) {
			XAException xe = new XAException(XAException.XAER_RMERR);
			xe.initCause(e);
			throw xe;
		}

		return xids;
	}
	

	/**
	 * Retrieves the current active timeout value from the server.
	 * @return active timeout in miliseconds.
	 */ 
	public long xaGetTimeout(int sessionId) throws XAException {
		long result = 0;
		
		try {		
			Packet request = new XATimeoutRequest(sessionId);
			XATimeoutResponse response = (XATimeoutResponse)sendPacket(request);
			result = response.getTimeout();
		} catch (JMSXAException e) {
			XAException xe = ((JMSXAException)e).getXaException();
			throw xe;
		} catch (JMSException e) {
			XAException xe = new XAException(XAException.XAER_RMERR);
			xe.initCause(e);
			throw xe;
		}
		
		return result;
	}
	
	private Packet sendPacket(Packet request) throws JMSException {
		try {
			Packet response = networkAdapter.sendAndWait(request);
			if (response == null) {
				JMSException e = new JMSException("Got no answer to request " + request.getPacketType());
		        throw e;
			}
			
			if (response.getPacketType() == PacketTypes.SERVER_EXCEPTION_RESPONSE) {
				throw ((ServerExceptionResponse)response).getException();
			} 
			
			if ( response.getPacketType() != request.getExpectedResponsePacketType()) {
				JMSException e = new JMSException("Unexpected response. Expected " + request.getExpectedResponsePacketType() + "got " + response.getPacketType());
				throw e;
			}
			
			return response;
		
		} catch (java.io.IOException ioe) {
			JMSException e = new JMSException("I/O error on send.");
			e.initCause(ioe);
			e.setLinkedException(ioe);
			throw e;
		}
	}	
}
