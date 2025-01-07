package com.sap.jms.client.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.jms.TransactionInProgressException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.sap.jms.JMSConstants;
import com.sap.jms.client.message.JMSMessage;
import com.sap.jms.client.session.AckHandler;
import com.sap.jms.client.session.JMSSession;
import com.sap.jms.util.LogUtil;
import com.sap.jms.util.Logging;
import com.sap.tc.logging.Severity;

public class JMSXASession implements javax.jms.XASession, AckHandler {

	private JMSXAResource xaResource;
	private static Timer timer = new Timer();

	private Map<Xid, XAInfo> xaTransactions = new ConcurrentHashMap<Xid, XAInfo>();

	// TODO fix packages
	private static enum XAState {
		ACTIVE,			/** after xa_start() and before xa_end() */
		IDLE, 			/** after xa_end(SUCCESS), only xa_start(JOIN) can follow */
		SUSPENDED,		/** after xa_end(SUSPEND), only xa_start(RESUME) can follow */
		ROLLBACK_ONLY, 	/** after active_timeout from ACITVE and IDLE */
		PREPARED, 		/** after xa_prepare() */
		HEUR_ROLLEDBACK,/** after abandonment_timeout and rollback */
		HEUR_COMMITTED; 	/** after abandonment_timeout and commit */
	};

	private class XAInfo {

		/* The local id of the transaction. */ 
		public long txid;

		private Map<Long, List<JMSMessage>> consumedTransactedMessages;

		/* The state of the transaction. */
		public XAState state;

		public TimerTask timeoutTask;

		public XAInfo() {
			consumedTransactedMessages = new ConcurrentHashMap<Long, List<JMSMessage>>();
		}
	}

	private JMSSession session;

	public JMSXASession(JMSSession session) throws JMSException {

		this.session = session;

		xaResource = new JMSXAResource(this);

		// this class will handle acks
		session.setAckHandler(this);
	}

	public Session getSession() {
		return session;
	}

	public XAResource getXAResource() {
		return xaResource;
	}

	public void rollback() throws JMSException {
		throw new TransactionInProgressException("Cannot rollback() from a XASession");
	}

	public boolean getTransacted() {
		return xaResource.isTransactionActive();
	}

	public void commit() throws JMSException {
		throw new TransactionInProgressException("Cannot commit() from a XASession");
	}

	public void close() throws JMSException {
		
		for (XAInfo info: xaTransactions.values()) {
			if (info.timeoutTask != null) {
				info.timeoutTask.cancel();
				info.timeoutTask = null;
			}
		}
		
		session.close();
	}

	public QueueBrowser createBrowser(Queue queue) throws JMSException {
		return session.createBrowser(queue);
	}

	public QueueBrowser createBrowser(Queue queue, String msgSelector) throws JMSException {
		return session.createBrowser(queue, msgSelector);
	}

	public BytesMessage createBytesMessage() throws JMSException {
		return session.createBytesMessage();
	}

	public MessageConsumer createConsumer(Destination destination) throws JMSException {
		return session.createConsumer(destination);
	}

	public MessageConsumer createConsumer(Destination destination, String msgSelector) throws JMSException {
		return session.createConsumer(destination, msgSelector);
	}

	public MessageConsumer createConsumer(Destination destination, String msgSelector, boolean noLocal) throws JMSException {
		return session.createConsumer(destination, msgSelector, noLocal);
	}

	public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
		return session.createDurableSubscriber(topic, name);
	}

	public TopicSubscriber createDurableSubscriber(Topic topic, String name, String msgSelector, boolean noLocal) throws JMSException {
		return session.createDurableSubscriber(topic, name, msgSelector, noLocal);
	}

	public MapMessage createMapMessage() throws JMSException {
		return session.createMapMessage();
	}

	public Message createMessage() throws JMSException {
		return session.createMessage();
	}

	public ObjectMessage createObjectMessage() throws JMSException {
		return session.createObjectMessage();
	}

	public ObjectMessage createObjectMessage(Serializable object) throws JMSException {
		return session.createObjectMessage(object);
	}

	public MessageProducer createProducer(Destination destination) throws JMSException {
		return session.createProducer(destination);
	}

	public Queue createQueue(String name) throws JMSException {
		return session.createQueue(name);
	}

	public StreamMessage createStreamMessage() throws JMSException {
		return session.createStreamMessage();
	}

	public TemporaryQueue createTemporaryQueue() throws JMSException {
		return session.createTemporaryQueue();
	}

	public TemporaryTopic createTemporaryTopic() throws JMSException {
		return session.createTemporaryTopic();
	}

	public TextMessage createTextMessage() throws JMSException {
		return session.createTextMessage();
	}

	public TextMessage createTextMessage(String text) throws JMSException {
		return session.createTextMessage(text);
	}

	public Topic createTopic(String name) throws JMSException {
		return session.createTopic(name);
	}

	public int getAcknowledgeMode() throws JMSException {
		return session.getAcknowledgeMode();
	}

	public MessageListener getMessageListener() throws JMSException {
		return session.getMessageListener();
	}

	public void recover() throws JMSException {
		//		session.sendFullRecover();
		session.recover();
	}

	public void run() {
		session.run();
	}

	public void setMessageListener(MessageListener newListener) throws JMSException {
		session.setMessageListener(newListener);
	}

	public void unsubscribe(String subscriptionName) throws JMSException {
		session.unsubscribe(subscriptionName);
	}

	////////////

	// called in JMSMessageConsumer::receive
	// TODO visibility
	public void acknowledge(Long consumerId, JMSMessage message) throws JMSException {

		Xid xid = xaResource.getActiveXid();
		if (xid == null) {
			session.acknowledge(consumerId, message);
			return;
		}

		XAInfo info = xaTransactions.get(xid);

		if (info == null) {
			throw new JMSException("This JMS does not support migrating XAResources!");
		}
		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "Delivered message with id: ", message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER), " as part of a XA transaction with xid: ", xaResource.getActiveXid()," to consumer with id: ", consumerId);
		}

		if (info.consumedTransactedMessages.containsKey(consumerId)) {
			List<JMSMessage> tmpList = info.consumedTransactedMessages.get(consumerId);
			tmpList.add(message);
		} else {
			List<JMSMessage> messages = new ArrayList<JMSMessage>();
			messages.add(message);
			info.consumedTransactedMessages.put(consumerId, messages);
		}
	}

	// called in JMSSession::deliverMessage
	// TODO visibility
	public void scheduleForAcknowledge(Long consumerId, JMSMessage message) throws JMSException {

		Xid xid = xaResource.getActiveXid();
		if (xid == null) {
			session.scheduleForAcknowledge(consumerId, message);
			return;
		}

		XAInfo info = xaTransactions.get(xid);
		if (info == null) {
			throw new JMSException("This JMS does not support migrating XAResources!");
		}
		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "Delivered message with id: ", message.getLongProperty(JMSConstants.JMSX_SAP_PCOUNTER), " as part of a XA transaction to consumer with id: ", consumerId);
		}

		if (info.consumedTransactedMessages.containsKey(consumerId)) {
			List<JMSMessage> tmpList = info.consumedTransactedMessages.get(consumerId);
			tmpList.add(message);
		} else {
			List<JMSMessage> messages = new ArrayList<JMSMessage>();
			messages.add(message);
			info.consumedTransactedMessages.put(consumerId, messages);
		}
	}

	public void xaForget(Xid xid) throws XAException {
		XAInfo info = xaTransactions.get(xid);
		if ((info.state == XAState.HEUR_COMMITTED) || (info.state == XAState.HEUR_ROLLEDBACK)) {
			xaTransactions.remove(xid);
			return;
		}

		session.getConnection().getServerFacade().xaForget(xid);
	}

	public void xaStart(Xid xid, int flags, long activeTimeout) throws XAException {

		long txid = 0;
		XAInfo info = null;

		switch (flags) {

		case XAResource.TMRESUME: {
			info = xaTransactions.get(xid);

			if (info == null) {
				if (Logging.isWritable(this, Severity.ERROR)) {
					Logging.log(this, Severity.ERROR, "Cannot resume XA transaction: ", xid);
				}
				throw new XAException(XAException.XAER_NOTA);
			}

			synchronized(info) {
				if (info.state != XAState.SUSPENDED) {
					if (Logging.isWritable(this, Severity.ERROR)) {					
						Logging.log(this, Severity.ERROR, "Cannot resume XA transaction: ", xid, " in state: ", info.state);
					}
					throw new XAException(XAException.XAER_PROTO);
				}

				info.state = XAState.ACTIVE;
				txid = info.txid;
			}
			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "Resumed XA transaction with xid: ", xid, " and local txid: ", info.txid);
			}
		}
		break;
		case XAResource.TMJOIN: {
			info = xaTransactions.get(xid);

			if (info == null) {
				if (Logging.isWritable(this, Severity.ERROR)) {
					Logging.log(this, Severity.ERROR, "Cannot join XA transaction: ", xid);
				}
				throw new XAException(XAException.XAER_NOTA);
			}

			synchronized(info) {
				if (info.state != XAState.IDLE) {
					if (Logging.isWritable(this, Severity.ERROR)) {
						Logging.log(this, Severity.ERROR, "Cannot join XA transaction: ", xid, " in state: ", info.state);
					}
					throw new XAException(XAException.XAER_PROTO);
				}

				info.state = XAState.ACTIVE;
				txid = info.txid;
			}
			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "Joined XA transaction with xid: ", xid, " and local txid: ", info.txid);
			}
		}
		break;
		case XAResource.TMNOFLAGS: {
			if (xaTransactions.keySet().contains(xid)) {
				if (Logging.isWritable(this, Severity.ERROR)) {
					Logging.log(this, Severity.ERROR, "Cannot start XA transaction: ", xid);
				}
				throw new XAException(XAException.XAER_DUPID);
			}

			info = new XAInfo();
			info.state = XAState.ACTIVE;
			xaTransactions.put(xid, info);

			txid = 0;

			info.timeoutTask =  new XAActiveTimeoutTask(xid, xaResource);
			
			timer.schedule(info.timeoutTask, activeTimeout);

			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "Started XA transaction with xid: ", xid, " and local txid: ", info.txid);
			}

		}
		break;

		default:
			if (Logging.isWritable(this, Severity.ERROR)) {
				Logging.log(this, Severity.ERROR, "Invalid start flags: ", flags);
			}
			throw new XAException(XAException.XAER_INVAL);
		}

		// TODO only needed to mark the session transactional and get the txid
		info.txid = session.getConnection().getServerFacade().xaStart(getConnectionId(), session.getSessionID(), xid, txid);
	}


	public void xaEnd(Xid xid, int flags) throws XAException {

		XAInfo info = xaTransactions.get(xid);

		if (info == null) {
			if (Logging.isWritable(this, Severity.ERROR)) {
				Logging.log(this, Severity.ERROR, "Cannot end XA transaction: " , xid);
			}
			throw new XAException(XAException.XAER_NOTA);
		}

		switch (flags) {

		case XAResource.TMSUCCESS:
			synchronized(info) {
				if (info.state != XAState.ACTIVE) {
					Logging.log(this, Severity.ERROR, "Cannot end XA transaction:  ", xid, " in state: ", info.state);
					throw new XAException(XAException.XAER_PROTO);
				}


				info.state = XAState.IDLE;
			}

			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "Ended XA transaction with xid: ", xid, " and local txid: ", info.txid);
			}
			break;	
		case XAResource.TMFAIL:
			synchronized(info) {
				if (info.state != XAState.ACTIVE) {
					if (Logging.isWritable(this, Severity.ERROR)) {
						Logging.log(this, Severity.ERROR, "Cannot fail XA transaction: ", xid, " in state: ", info.state);
					}
					throw new XAException(XAException.XAER_PROTO);
				}

				info.state = XAState.ROLLBACK_ONLY;
			}
			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "Failed XA transaction with xid: ", xid, " and local txid: ", info.txid);
			}

			break;	
		case XAResource.TMSUSPEND:
			synchronized(info) {
				if (info.state != XAState.ACTIVE) {
					if (Logging.isWritable(this, Severity.ERROR)) {
						Logging.log(this, Severity.ERROR, "Cannot suspend XA transaction: ",xid," in state: ",info.state);
					}
					throw new XAException(XAException.XAER_PROTO);
				}

				info.state = XAState.SUSPENDED;
			}

			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "Suspended XA transaction with xid: ", xid, " and local txid: ", info.txid);
			}
			break;	

		default:
			if (Logging.isWritable(this, Severity.ERROR)) {
				Logging.log(this, Severity.ERROR, "Invalid end flags: ", flags);
			}
			throw new XAException(XAException.XAER_INVAL);
		}

		// TODO only needed to mark the session non-transactional
		session.getConnection().getServerFacade().xaEnd(getConnectionId(), session.getSessionID(), xid);
	}

	public Set<Xid> xaRecover() throws XAException {
		Set<Xid> xids = new HashSet<Xid>();

		for (Xid xid : xaTransactions.keySet()) {
			XAInfo info = xaTransactions.get(xid);
			if ((info.state == XAState.HEUR_COMMITTED) || (info.state == XAState.HEUR_ROLLEDBACK)) {
				xids.add(xid);
			}
		}

		return xids;
	}

	public int xaPrepare(Xid xid) throws XAException {

		XAInfo info = xaTransactions.get(xid);

		if (info == null) {
			if (Logging.isWritable(this, Severity.ERROR)) {
				Logging.log(this, Severity.ERROR, "Cannot prepare XA transaction: ", xid);
			}
			throw new XAException(XAException.XAER_NOTA);
		}

		synchronized (info) {
			if (info.state != XAState.IDLE) {
				if (Logging.isWritable(this, Severity.ERROR)) {
					Logging.log(this, Severity.ERROR, "Cannot prepare XA transaction: ", xid, " in state: ", info.state);
				}
				throw new XAException(XAException.XAER_PROTO);
			}

			// TODO from here call DC and move this exception there
			// here delete the info and keep only mapping xid-txid
			if (info.state == XAState.PREPARED) {
				if (Logging.isWritable(this, Severity.ERROR)) {
					Logging.log(this, Severity.ERROR, "XA transaction: ", xid, " is already prepared.");
				}
				throw new XAException(XAException.XAER_PROTO);
			}

			if (info.timeoutTask != null) {
				info.timeoutTask.cancel();
				info.timeoutTask = null;
			}

			info.state = XAState.PREPARED;
		}


		// TODO make consumedTransactedMessages atomicReference
		Map<Long, List<JMSMessage>> consumedMessages = info.consumedTransactedMessages;

		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "Will prepare XA transaction with xid: ", xid, " and local txid: ", info.txid, " with messages: ", consumedMessages);
		}

		Map<Long, List<Long>> tmp;

		// TODO remove this stupid conversion
		try {
			tmp = JMSSession.convertPendingMessages(consumedMessages);
		} catch (JMSException e) {
			throw new XAException(XAException.XAER_RMERR);
		}

		int vote = session.getConnection().getServerFacade().xaPrepare(getConnectionId(), session.getSessionID(), xid, info.txid, tmp);

		return vote;
	}

	public void xaCommit(Xid xid, boolean onePhase) throws XAException {

		XAInfo info = xaTransactions.get(xid);

		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "Will commit XA transaction with xid: ", xid);
		}

		// offline commit scenario
		if (info == null) {
			Map<Long, List<Long>> empty = new HashMap<Long, List<Long>>();
			session.getConnection().getServerFacade().xaCommit(getConnectionId(), session.getSessionID(), xid, 0, onePhase, empty);
			return;
		}

		long txid = info.txid;
		synchronized (info) {
			if (onePhase) {
				if (info.state != XAState.IDLE) {
					if (Logging.isWritable(this, Severity.ERROR)) {
						Logging.log(this, Severity.ERROR, "Cannot commit XA transaction (one pahse): ", xid, " in state: ", info.state);
					}
					throw new XAException(XAException.XAER_PROTO);
				}

			} else {

				for (Long consumerId: info.consumedTransactedMessages.keySet()) {
					// at least one consumer context in this transaction was closed. Try offline mode.
					if (!session.existsConsumer(consumerId)) {
						txid = 0;
						break;
					}
				}
			}

			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "About to commit xid: ",xid, " local txid: ", txid, " messages: ", info.consumedTransactedMessages);
			}
			Map<Long, List<Long>> tmp;

			// TODO remove this stupid conversion
			try {
				tmp = JMSSession.convertPendingMessages(info.consumedTransactedMessages);
			} catch (JMSException e) {
				throw new XAException(XAException.XAER_RMERR);
			}

			session.getConnection().getServerFacade().xaCommit(getConnectionId(), session.getSessionID(), xid, txid, onePhase, tmp);

			if (info.timeoutTask != null) {
				info.timeoutTask.cancel();
				info.timeoutTask = null;
			}

			xaTransactions.remove(xid);
		}
	}

	private void sendFullRecover(Xid xid) throws XAException  {
		try {
			session.getConnection().getServerFacade().sessionStop(session.getConnection().getConnectionId(), session.getSessionID());
			session.sendFullRecover();

			Map<Long, List<Long>> empty = new HashMap<Long, List<Long>>();
			session.getConnection().getServerFacade().xaRollback(getConnectionId(), session.getSessionID(), xid, 0, false, empty);
			session.getConnection().getServerFacade().sessionStart(session.getConnection().getConnectionId(), session.getSessionID());
		}
		catch (JMSException e) {
			Logging.log(this, Severity.ERROR, "Could not recover transaction with xid: ", xid);
			throw new XAException(XAException.XAER_RMERR);
		}

	}
	
	public void xaRollback(Xid xid) throws XAException {

		XAInfo info = xaTransactions.get(xid);

		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "Will rollback XA transaction with xid: ", xid);
		}

		if (info == null) {
			// offline rollback scenario
			sendFullRecover(xid);
			return;
		}

		synchronized (info) {

			if (info.timeoutTask != null) {
				info.timeoutTask.cancel();
				info.timeoutTask = null;
			}

			boolean onePhase = false;
			if (info.state == XAState.IDLE || info.state == XAState.ROLLBACK_ONLY){
				// TODO make an AtomicReference out of it
				Map<Long, List<JMSMessage>> messagesForRedeliver = info.consumedTransactedMessages;
				info.consumedTransactedMessages = new HashMap<Long, List<JMSMessage>>();
				try {
					session.redeliver(messagesForRedeliver);
				} catch (JMSException e) {
					Logging.log(this, Severity.ERROR, "Could not redeliver messages: ", messagesForRedeliver, "on xid: ", xid);
					throw new XAException(XAException.XAER_RMERR);
				}
			} else {
				long txid = info.txid;
				// check if consumer is still alive
				for (Long consumerId: info.consumedTransactedMessages.keySet()) {
					// at least one consumer context in this transaction was closed. Try offline mode.
					if (!session.existsConsumer(consumerId)) {
						txid = 0;
						break;
					}
				}

				try {
					com.sap.jms.client.connection.Connection connection = session.getConnection();
					long connectionId = connection.getConnectionId();
					int sessionId = session.getSessionID();

					connection.getServerFacade().sessionStop(connectionId, sessionId);
					session.redeliverAllUnconsumedMessages();

					Map<Long, List<Long>> tmp;

					// TODO remove this stupid conversion
					tmp = JMSSession.convertPendingMessages(info.consumedTransactedMessages);

					connection.getServerFacade().xaRollback(connectionId, sessionId, xid, txid, onePhase, tmp);
					connection.getServerFacade().sessionStart(connectionId, sessionId);
				} catch (JMSException e) {
					throw new XAException(XAException.XAER_RMERR);
				}
			}

			xaTransactions.remove(xid);
		}
	}

	public void redistributeMessagesWithoutConsumer(Map<Long, List<JMSMessage>> messagesWithoutConsumers) throws JMSException {
		if (xaResource.isTransactionActive() && !messagesWithoutConsumers.isEmpty()) {
			throw new JMSException("Called redistributeMessagesWithoutConsumer for messages:" + messagesWithoutConsumers + "while a XA transaction with id: "+ xaResource.getActiveXid() + " was active");
		} else {
			// When not in transaction the XASession is in AUTO_ACK mode
			Map<Long, List<Long>> tmp = JMSSession.convertPendingMessages(messagesWithoutConsumers);
			session.getConnection().getServerFacade().sessionRecover(session.getConnection().getConnectionId(), session.getSessionID(), tmp);
		}
	}

	public void onActiveTimeout(Xid xid, long heuristicTimeout) {

		XAInfo info = xaTransactions.get(xid);

		if (info == null) {
			if (Logging.isWritable(this, Severity.WARNING)) {
				Logging.log(this, Severity.WARNING, "Found no information for transaction with xid: ", xid);
			}
			return;
		}

		synchronized (info) {
			if (info.state == XAState.ACTIVE || info.state == XAState.IDLE || info.state == XAState.SUSPENDED) {
				info.state = XAState.ROLLBACK_ONLY;

				if (Logging.isWritable(this, Severity.DEBUG)) {
					Logging.log(this, Severity.DEBUG, "Got active timeout for transaction with xid: ", xid, " .Changeing the state to rollback only.");
				}

				// session is not transacted anymore
				try {
					session.getConnection().getServerFacade().xaEnd(getConnectionId(), session.getSessionID(), xid);
				} catch (Exception e){
					Logging.exception(this, e);
				}

				info.timeoutTask =  new XAInMemoryAbandonementTimeoutTask(xid, this);

				timer.schedule(info.timeoutTask, heuristicTimeout);

			} else {
				if (Logging.isWritable(this, Severity.WARNING)) {
					Logging.log(this, Severity.WARNING, "Got active timeout for transaction with xid: ", xid, " in state: ", info.state);
				}
			}
		}

		info.timeoutTask = null;
	}

	public void onInMemoryAbandonmentTimeout(Xid xid) {

		XAInfo info = xaTransactions.get(xid);

		if (info == null) {
			if (Logging.isWritable(this, Severity.WARNING)) {
				Logging.log(this, Severity.WARNING, "Found no information for transaction with xid: ", xid);
			}
			return;
		}

		synchronized (info) {
			if (info.state == XAState.ROLLBACK_ONLY){

				// TODO make an AtomicReference out of it
				Map<Long, List<JMSMessage>> messagesForRedeliver = info.consumedTransactedMessages;
				info.consumedTransactedMessages = new HashMap<Long, List<JMSMessage>>();
				try {
					session.redeliver(messagesForRedeliver);
				} catch (JMSException e) {
					if (Logging.isWritable(this, Severity.ERROR)) {
						Logging.log(this, Severity.ERROR, "Could not redeliver messages: ", messagesForRedeliver, "on xid: ", xid);
					}
				}

				info.state = XAState.HEUR_ROLLEDBACK;
				if (Logging.isWritable(this, Severity.DEBUG)) {				
					Logging.log(this, Severity.DEBUG, "Got in memory abandonment timeout for transaction with xid: ", xid, " .Changeing the state to HEUR_ROLLEDBACK?");
				}
			} else {
				if (Logging.isWritable(this, Severity.WARNING)) {				
					Logging.log(this, Severity.WARNING, "Got in memory abandonment timeout for transaction with xid: ", xid, " in state: ", info.state);
				}
			}
		}

	}

	private long getConnectionId() throws XAException {
		try {
			long connectionId = session.getConnection().getConnectionId();
			return connectionId;
		} catch (JMSException e) {
			throw new XAException(XAException.XAER_RMERR);
		}
	}
}
