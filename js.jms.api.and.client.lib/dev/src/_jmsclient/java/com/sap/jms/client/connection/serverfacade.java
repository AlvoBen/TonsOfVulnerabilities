package com.sap.jms.client.connection;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.jms.JMSException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

import com.sap.jms.client.message.JMSMessage;
import static com.sap.jms.client.connection.DestinationInfo.DestinationType;

public interface ServerFacade {
	
    public static final int  PROTOCOL_VERSION = 710110;
	
	// ConnectionHandler

	public void connectionClose(long connectionId) throws JMSException;

	public void connectionStart(long connectionId) throws JMSException;
	
	public void connectionStop(long connectionId) throws JMSException;

	// ConsumerHandler

	public void consumerCreate(Subscription subscription) throws JMSException;

	public void consumerClose(long connectionId, int sessionId, long consumerId, int consumedMessages) throws JMSException;
	
	public void consumerRefresh(long connectionId, int sessionId, long consumerId) throws JMSException;

	//onConsumerStartDeliveryRequest
	public void startMessageDelivery(long connectionId, int sessionId, long consumerId, int clientLimit, int consumedMessages) throws JMSException;

	//onConsumerSubscriptionRemoveRequest
	public void subscriptionRemove(long connectionId, String subscriptionName) throws JMSException;

	// ProducerHandler

	public long producerCreate(long connectionId, int sessionId, int destinationId) throws JMSException;

	public void producerClose(long connectionId, int sessionId, long producerId) throws JMSException;

	// DestinationHandler
	
	public DestinationInfo destinationCreate(long connectionId, String destinationName, DestinationType type, boolean isTemporary) throws JMSException;
	
	public void destinationDelete(long connectionId, int destinationId) throws JMSException;

	public DestinationInfo getDestinationInfo(int destinationId) throws JMSException;

	// SessionHandler
	
	public SessionData sessionCreate(long connectionId, int acknowledgeMode, boolean isXA) throws JMSException;

	public void sessionStart(long connectionId, int sessionId) throws JMSException;
	
	public void sessionStop(long connectionId, int sessionId) throws JMSException;

	public void sessionClose(long connectionId, int sessionId) throws JMSException;

	public void sendMessage(JMSMessage message) throws JMSException;
	
	public void sessionAcknowledge(long connectionId, int sessionId, Map<Long, List<Long>> deliveredMessages) throws JMSException;

	public void sessionCommit(long connectionId, int sessionId, Map<Long, List<Long>> deliveredMessages) throws JMSException;

	public void sessionAfterBegin(long connectionId, int sessionId) throws JMSException;
	
	public void sessionBeforeCompletion(long connectionId, int sessionId, Map<Long, List<Long>> deliveredMessages) throws JMSException;
	
	public void sessionAfterCompletion(long connectionId, int sessionId, Map<Long, List<Long>> deliveredMessages,int status) throws JMSException;
	
	public void sessionRecover(long connectionId, int sessionId, Map<Long, List<Long>> deliveredMessages) throws JMSException;
	
	public void sessionRollback(long connectionId, int sessionId, Map<Long, List<Long>> deliveredMessages) throws JMSException;

	// QueueBrowserHandler

	public void queueBrowserEnumerate(long connectionId, int sessionId, long browserId) throws JMSException;
	
	public long xaStart(long connectionId, int sessionId, Xid xid, long txid) throws XAException;

	public void xaEnd(long connectionId, int sessionId, Xid xid) throws XAException;

	public void xaCommit(long connectionId, int sessionId, Xid xid, long txid, boolean onePhase, Map<Long, List<Long>> consumedMessages) throws XAException;

	public void xaRollback(long connectionId, int sessionId, Xid xid, long txid, boolean onePhase, Map<Long, List<Long>> consumedMessages) throws XAException;

	public void xaForget(Xid xid) throws XAException;

	public int xaPrepare(long connectionId, int sessionId, Xid xid, long txid, Map<Long, List<Long>> consumedMessages) throws XAException;

	public Set<Xid> xaRecover(int flags) throws XAException;
	
	public TimeoutData xaGetTimeout(int sessionId) throws XAException;
	
	public Map<Long, Set<DelayedDeliveryData>> messageDeliveryFailed(long connectionId, int sessionId, Map<Long, List<Long>> pendingMessages) throws JMSException;

	public static class DelayedDeliveryData implements java.io.Serializable {
    	public long pCounter;
    	public long delayInterval;
    }

	public static class TimeoutData implements java.io.Serializable {
		public long activeTimeout;
		public long heuristicTimeout;
	}

	public static class SessionData implements java.io.Serializable {
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
