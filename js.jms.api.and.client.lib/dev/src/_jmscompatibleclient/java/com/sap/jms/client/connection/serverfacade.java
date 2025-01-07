package com.sap.jms.client.connection;

import java.util.Map;
import java.util.Set;
import javax.jms.JMSException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

import com.sap.jms.client.message.JMSMessage;

public interface ServerFacade {
	
    public static final int  PROTOCOL_VERSION = 710110;
	
	// ConnectionHandler

	// !!!
	public long connectionCreate(String vpName, byte[] certificate, String username, String password, String connectionFactoryName) throws JMSException;

	public void connectionClose(long connectionId) throws JMSException;

	public void connectionStart(long connectionId) throws JMSException;
	
	public void connectionStop(long connectionId) throws JMSException;

	// ConsumerHandler

	public long consumerCreate(int sessionId, String subscriptionName, String destinationName, 
			byte destinationType, boolean noLocal, String messageSelector, int clientLimit) throws JMSException;

	public void consumerClose(int sessionId, long consumerId, int consumedMessages) throws JMSException;
	
	public void consumerRefresh(int sessionId, long consumerId, int destinationId) throws JMSException;

	//onConsumerStartDeliveryRequest
	public void startMessageDelivery(int destinationId, long consumerId, int clientLimit, int consumedMessages) throws JMSException;

	//onConsumerSubscriptionRemoveRequest
	public void subscriptionRemove(String subscriptionName) throws JMSException;

	// ProducerHandler

	public long producerCreate(int sessionId, String destinationName, byte destinationType) throws JMSException;

	public void producerClose(int sessionId, long producerId) throws JMSException;

	// DestinationHandler
	
	public DestinationDescriptor destinationCreate(long connectionId, String destinationName, byte destinationType, boolean isTemporary) throws JMSException;
	
	public void destinationDelete(long connectionId, int destinationId) throws JMSException;

	public String destinationName(int destinationId) throws JMSException;

	// SessionHandler
	
	public SessionDescriptor sessionCreate(long connectionId, byte acknowledgeMode, boolean isXA) throws JMSException;

	public void sessionStart(long connectionId, int sessionId) throws JMSException;
	
	public void sessionStop(long connectionId, int sessionId) throws JMSException;

	public void sessionClose(long connectionId, int sessionId) throws JMSException;

	public void sendMessage(JMSMessage message) throws JMSException;
	
	public void sessionAcknowledge(int sessionId, Map/*<Long, Set<Long>>*/ deliveredMessages) throws JMSException;

	public void sessionCommit(int sessionId, Map/*<Long, Set<Long>>*/ deliveredMessages) throws JMSException;

	public void sessionAfterBegin(int sessionId) throws JMSException;
	
	public void sessionBeforeCompletion(int sessionId, Map/*<Long, Set<Long>>*/ deliveredMessages) throws JMSException;
	
	public void sessionAfterCompletion(int sessionId, Map/*<Long, Set<Long>>*/ deliveredMessages,int status) throws JMSException;
	
	public void sessionRecover(int sessionId, Map/*<Long, Set<Long>>*/ deliveredMessages) throws JMSException;
	
	public void sessionRollback(int sessionId, Map/*<Long, Set<Long>>*/ deliveredMessages) throws JMSException;
	
	// QueueBrowserHandler

	public long queueBrowserCreate(int sessionId, String queueName, String messageSelector, int clientLimit) throws JMSException;

	public void queueBrowserEnumerate(int sessionId, long browserId) throws JMSException;
	
	public void queueBrowserClose(int sessionId, long browserId) throws JMSException;

	public void xaStart(int sessionId, Xid xid, int flags, long activeTimeout) throws XAException;

	public void xaEnd(Xid xid, int flags, Map/*<Long, Set<Long>>*/ msgsPerConsumerIds) throws XAException;

	public void xaCommit(Xid xid, boolean onePhase) throws XAException;

	public void xaRollback(Xid xid) throws XAException;

	public void xaForget(Xid xid) throws XAException;

	public int xaPrepare(Xid xid) throws XAException;

	public Set/*<Xid>*/ xaRecover(int flags) throws XAException;
	
	public long xaGetTimeout(int sessionId) throws XAException;
	
	public static class DestinationDescriptor {
		private int destinationId;
		private String destinationName;		
		
		public void setDestinationId(int destinationId) {
			this.destinationId = destinationId;
		}
		
		public int getDestinationId() {
			return destinationId;
		}
		
		public void setDestinationName(String destinationName) {
			this.destinationName = destinationName;
		}
		
		public String getDestinationName() {
			return destinationName;
		}
	}
	
	public static class SessionDescriptor {
		private int sessionId;
		private long messageIdBase;
		
		public void setSessionId(int sessionId) {
			this.sessionId = sessionId;
		}
		
		public int getSessionId() {
			return sessionId;
		}
		
		public void setMessageIdBase(long messageIdBase) {
			this.messageIdBase = messageIdBase;
		}
		
		public long getMessageIdBase() {
			return messageIdBase;
		}

	}
}
