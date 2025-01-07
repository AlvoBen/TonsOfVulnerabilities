/**
 * Connection.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.connection;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.ExceptionListener;
import javax.jms.IllegalStateException;
import javax.jms.InvalidClientIDException;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;

import com.sap.jms.client.session.JMSQueueSession;
import com.sap.jms.client.session.JMSSession;
import com.sap.jms.client.session.JMSTopicSession;
import com.sap.jms.client.session.JMSXAQueueSession;
import com.sap.jms.client.session.JMSXASession;
import com.sap.jms.client.session.JMSXATopicSession;
import com.sap.jms.client.session.JMSSession.SessionType;
import com.sap.jms.protocol.Packet;
import com.sap.jms.protocol.PacketWithConsumerID;
import com.sap.jms.protocol.PacketWithSessionID;
import static com.sap.jms.client.connection.ServerFacade.SessionData;

import com.sap.jms.util.LogUtil;
import com.sap.jms.util.Logging;
import com.sap.jms.util.TaskManager;
import com.sap.tc.logging.Severity;

public class Connection implements javax.jms.Connection, ClientFacade, Closeable {

	public static enum ConnectionType {
		QUEUE_CONNECTION,
		TOPIC_CONNECTION,
		GENERIC_CONNECTION,
		XA_QUEUE_CONNECTION,
		XA_TOPIC_CONNECTION,
		XA_GENERIC_CONNECTION,
	};

	protected long connectionId = 0;
	protected ServerFacade serverFacade = null;	
	protected ClientFacade clientFacade = null;	
	private String vpName = null;
	private String clientId = null;
	private TaskManager taskManager = null;
	boolean supportsOptimization = false;

	private static final ConnectionMetaData connectionMetaData = new ConnectionMetaData();

	private boolean isClosed = false;
	private boolean isStarted = false;
	private boolean adapterClosed = false;
	private Map<Integer, WeakReference<Session>> sessions;
	private javax.jms.ConnectionConsumer connectionConsumer = null;
	private AtomicBoolean canSetClientID = null;
	private Object syncThis = new Object();
	private boolean isStopping = false;
	private ExceptionListener exceptionListener = null;
	private JMSException asyncException = null;
	private ClassLoader appClassLoader = null;

	public Connection(long connectionId, ServerFacade serverFacade, ClientFacade clientFacade, String vpName, String clientId, TaskManager taskManager, boolean supportsOptimization) {
		this.connectionId = connectionId;		
		this.serverFacade = serverFacade;
		this.clientFacade = clientFacade;
		this.vpName = vpName;
		this.clientId = clientId;
		this.taskManager = taskManager;
		this.supportsOptimization = supportsOptimization;
		
		sessions = new ConcurrentHashMap<Integer, WeakReference<Session>>();
		appClassLoader = Thread.currentThread().getContextClassLoader();
		
		boolean validClientId = clientId != null && clientId.length() > 0;
		canSetClientID = new AtomicBoolean(!validClientId);		
		Logging.log(this, Severity.FATAL, "In connection constructor TaskManager is " + taskManager);
	}

	public ServerFacade getServerFacade() {
		return serverFacade;
	}

	public void close() throws javax.jms.JMSException {
		close(true);
	}

	private void close(boolean hitTheServer) throws javax.jms.JMSException {
		synchronized (syncThis) {
			if (Logging.isWritable(this, Severity.DEBUG)) { Logging.log(this, Severity.DEBUG, "Will close connection with id: ", connectionId); }

			if (isClosed) {
				return;
			}

			try {
				for (WeakReference<Session> sessionRef: sessions.values()) {
					if (sessionRef != null && sessionRef.get() != null) {
						if (hitTheServer) {
						sessionRef.get().close();
						} else {
							((JMSSession)sessionRef.get()).close(false);
						}
						
					}
				}
			} finally {
				isClosed = true;
			}
			if (hitTheServer) {
			getServerFacade().connectionClose(connectionId);
			}

			exceptionListener = null;
			connectionConsumer = null;
			appClassLoader = null;
		}
	}

	public void closeSession(int sessionID) throws javax.jms.JMSException {
		if (Logging.isWritable(this, Severity.DEBUG)) { Logging.log(this, Severity.DEBUG, "Will close session with id: ", sessionID); }

		// make sure we pass only once through close session for a particular sessionId
		WeakReference<Session> sessionRef = sessions.remove(sessionID);
		if (sessionRef == null || sessionRef.get() == null) {
			if (Logging.isWritable(this, Severity.WARNING)) { Logging.log(this, Severity.WARNING, "Session with id: " + sessionID + " under connection with id: " + connectionId + " is already closed."); }
			return;
		}
		
		// no need to check isClosed, since if the sessions collection contained
		// this session the connection is not closed yet!
		getServerFacade().sessionClose(connectionId, sessionID);
	}

	public javax.jms.ConnectionConsumer createConnectionConsumer(javax.jms.Destination destination, String selector, ServerSessionPool pool, int maxMessages) throws javax.jms.JMSException {

		synchronized (syncThis) {
			attemptToUse();
			canSetClientID.set(false);

			connectionConsumer = new JMSConnectionConsumer(destination, selector, pool, this, maxMessages);
			return connectionConsumer;
		}
	}

	public javax.jms.ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscription, String selector, ServerSessionPool pool, int maxMessages) throws javax.jms.JMSException {

		synchronized (syncThis) {
			attemptToUse();
			canSetClientID.set(false);			
			connectionConsumer = new JMSConnectionConsumer(topic, subscription, selector, pool, this, maxMessages);
			return connectionConsumer;
		}
	}

	public void closeConnectionConsumer() throws JMSException {
		synchronized (syncThis) {
			attemptToUse();
			canSetClientID.set(false);
			connectionConsumer.close();
		}
	}

	public javax.jms.Session createSession(boolean transacted, int acknowledgeMode) throws javax.jms.JMSException {
		return createSession(transacted, acknowledgeMode, SessionType.GENERIC_SESSION);
	}

	/**
	 * Method createSession. Creates a Session object.
	 * @param transacted indicates whether the session is transacted
	 * @param ackMode  indicates whether the consumer or the client will
	 * acknowledge any messages it receives; ignored if the session is transacted.
	 * Legal values are Session.AUTO_ACKNOWLEDGE, Session.CLIENT_ACKNOWLEDGE, and
	 * Session.DUPS_OK_ACKNOWLEDGE.
	 * @param sessionType type ot the session (one of Session.QUEUE_SESSION,
	 * Session.TOPIC_SESSION, Session.GENERIC_SESSION)
	 * @return Session a newly created session
	 * @throws JMSException  if the Connection object fails to create a session
	 * due to some internal error
	 */
	public javax.jms.Session createSession(boolean transacted, int ackMode, SessionType sessionType) throws javax.jms.JMSException{
		synchronized (syncThis) {
			attemptToUse();
			canSetClientID.set(false);

			if (transacted) {
				ackMode = JMSSession.SESSION_TRANSACTED;
			} else if (ackMode != JMSSession.AUTO_ACKNOWLEDGE
					&& ackMode != JMSSession.CLIENT_ACKNOWLEDGE
					&& ackMode != JMSSession.DUPS_OK_ACKNOWLEDGE) {
				throw new javax.jms.JMSException(LogUtil.getFailedInComponentByCaller() + "Incorrect acknowledge mode specified.");
			}

			SessionData sd;
			if (sessionType == SessionType.GENERIC_SESSION || sessionType == SessionType.QUEUE_SESSION || sessionType == SessionType.TOPIC_SESSION) {
				sd = getServerFacade().sessionCreate(connectionId, ackMode, false);
			} else {
				if (Logging.isWritable(this, Severity.WARNING)) { Logging.log(this, Severity.WARNING, "Will use AUTO_ACKNOWLEDGE instead of SESSION_TRANSACTED for new type of xa session"); }
				ackMode = Session.AUTO_ACKNOWLEDGE;
				
				sd = getServerFacade().sessionCreate(connectionId, ackMode, true);
			}

			Session session = null;

			switch (sessionType) {
			case GENERIC_SESSION :
				session = new JMSSession(sd.getSessionId(), sd.getMessageIdBase(), ackMode, this, taskManager);
				break;

			case QUEUE_SESSION :
				session = new JMSQueueSession(sd.getSessionId(), sd.getMessageIdBase(), ackMode, this, taskManager);
				break;

			case TOPIC_SESSION :
				session = new JMSTopicSession(sd.getSessionId(), sd.getMessageIdBase(), ackMode, this, taskManager);
				break;
			case XA_GENERIC_SESSION :
					session = new JMSXASession(new JMSSession(sd.getSessionId(), sd.getMessageIdBase(), ackMode, this, taskManager));
				break;

			case XA_QUEUE_SESSION :
					session = new JMSXAQueueSession(new JMSQueueSession(sd.getSessionId(), sd.getMessageIdBase(), ackMode, this, taskManager));
				break;

			case XA_TOPIC_SESSION :
					session = new JMSXATopicSession(new JMSTopicSession(sd.getSessionId(), sd.getMessageIdBase(), ackMode, this, taskManager));
				break;
			}


			sessions.put(sd.getSessionId(), new WeakReference<Session>(session));

			if (Logging.isWritable(this, Severity.DEBUG)) {
				Logging.log(this, Severity.DEBUG, "Mapped ", session, "(ack: ", ackMode, ", msgBase: ", sd.getMessageIdBase(),")"
						, " to session with id: ", sd.getSessionId(), " under connection with id: ", connectionId );
			}
			return session;
		}
	}

	public String getClientID() throws javax.jms.JMSException {
		attemptToUse();
		return clientId;
	}

	/**
	 * Returns the connection ID
	 *
	 * @return
	 * @throws javax.jms.JMSException
	 */
	public long getConnectionId() throws javax.jms.JMSException {
		attemptToUse();
		return connectionId;
	}

	public ExceptionListener getExceptionListener() throws javax.jms.JMSException {
		synchronized (syncThis) {
			attemptToUse();
			canSetClientID.set(false);
			return exceptionListener;
		}
	}

	public javax.jms.ConnectionMetaData getMetaData() throws javax.jms.JMSException {
		attemptToUse();
		canSetClientID.set(false);
		return connectionMetaData;
	}

	public void setClientID(String id) throws javax.jms.JMSException {
		attemptToUse();
		if (canSetClientID.compareAndSet(true, false)) {
			clientId = id;
		} else {
			throw new IllegalStateException("ClientID is already assigned or an action has been performed on the connection.");
		}
	}

	public void setExceptionListener(ExceptionListener exceptionListener) throws javax.jms.JMSException {
		synchronized (syncThis) {
			if (asyncException != null && exceptionListener != null) {
				JMSException exp = asyncException;
				asyncException = null;
				exceptionListener.onException(exp);
			}
			attemptToUse();
			canSetClientID.set(false);			
			this.exceptionListener = exceptionListener;
		}
	}

	public void start() throws javax.jms.JMSException {
		synchronized (syncThis) {
			attemptToUse();

			if (Logging.isWritable(this, Severity.DEBUG)) { Logging.log(this, Severity.DEBUG, "Will start connection with id: ", connectionId); }
			while (isStopping) {
				try {
					syncThis.wait();
				} catch (InterruptedException ie) {
					if (Logging.isWritable(this, Severity.WARNING)) { Logging.log(this, Severity.WARNING, "JMS start connection failed due to interrupted thread! Messages cannot be received."); }
					Logging.exception(this, ie);
				}
			}

			canSetClientID.set(false);

			if (isStarted) {
				return;
			}

			for (WeakReference<Session> sessionRef: sessions.values()) {
				if (sessionRef != null && sessionRef.get() != null) {
					if (sessionRef.get() instanceof JMSSession) {
						((JMSSession)sessionRef.get()).start();
					} else {
						((JMSSession)((JMSXASession)sessionRef.get()).getSession()).start();
					}
				}
			}

			getServerFacade().connectionStart(connectionId);
			isStarted = true;
		}
	}

	public void stop() throws javax.jms.JMSException {
		try {
			synchronized (syncThis) {

				attemptToUse();

				if (Logging.isWritable(this, Severity.DEBUG)) { Logging.log(this, Severity.DEBUG, "Will stop connection with id: ", connectionId); }

				if (!isStarted) {
					return;
				}
				getServerFacade().connectionStop(connectionId);
				isStopping = true;
				isStarted = false;
			}

			for (WeakReference<Session> sessionRef: sessions.values()) {
				if (sessionRef != null && sessionRef.get() != null) {

					if (sessionRef.get() instanceof JMSSession) {
						((JMSSession)sessionRef.get()).pauseDelivery();
					} else {
						((JMSSession)((JMSXASession)sessionRef.get()).getSession()).pauseDelivery();
					}
				}
			}

		} finally {
			synchronized (syncThis) {
				isStopping = false;
				syncThis.notifyAll();
			}
		}
	}

	/**
	 * Method attemptToUse. Used to check whether the connectio is closed.
	 * @throws IllegalStateException  thrown if the connection is closed.
	 */
	protected void attemptToUse() throws IllegalStateException {
		if (isClosed) {
			throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "The Connection is closed. id: " + connectionId);
		}
	}
	
	public void onPacketReceived(Packet packet) throws JMSException {
		ClassLoader oldClassLoader = null;		
		try {
			if (appClassLoader != null) {
				oldClassLoader = Thread.currentThread().getContextClassLoader();
				Thread.currentThread().setContextClassLoader(appClassLoader);
			}
			if (packet != null) { 		  	
				onPacket(packet);
			}
		} catch (InvalidClientIDException e) {
			Logging.exception(this, e);
			throw e;
		} catch (Exception e) {
			Logging.exception(this, e);
			JMSException ex;
			if (e instanceof JMSException) {
				ex = (JMSException)e;
			} else {
				ex = new JMSException("Internal error");
				ex.initCause(e);
				ex.setLinkedException(e);
			} 
			try {
				ExceptionListenerCaller exceptionListenerCaller = new ExceptionListenerCaller(this, ex, appClassLoader);     
				Logging.log(this, Severity.FATAL, "In onPacketReceived TaskManager is " + getTaskManager());
				if (getTaskManager() != null) {    		
					getTaskManager().schedule(exceptionListenerCaller);
				} else {
					if (Logging.isWritable(this, Severity.INFO)) {
						Logging.log(ExceptionListener.class, Severity.INFO, "Exception Listener was invoked from the same application thread. The connection is ", this);
					}
					exceptionListenerCaller.execute();
				} 			
				close(false);
//				onException(ex);
			} catch (Exception x) {
	        	if (Logging.isWritable(this, Severity.INFO)) {
	        	    Logging.log(ExceptionListener.class, Severity.INFO, "Exception Listener was invoked from the same application thread. The connection is ", this);
	        	}				
			} finally {
				throw ex;
			}
		} finally {
			if (oldClassLoader != null) {		  	
				Thread.currentThread().setContextClassLoader(oldClassLoader);
			}			
		}
	}

	/**
	 * Method onPacketReceived. Creates a message from the received Packet and gives
	 * control to the relevant session.
	 * @param packet  the received packet
	 * @throws JMSException  thrown if internal error occurs
	 */
	public void onPacket(Packet packet) throws javax.jms.JMSException {
		int packetType = packet.getPacketType();
		if (!(packetType > 0)) {
			throw new javax.jms.JMSException("Incorrect message received.");
		}

		int sessionID = -1;
		long consumerID = -1;

		if (packet instanceof PacketWithSessionID) {
			sessionID = ((PacketWithSessionID) packet).getSessionID();
		}
		if (packet instanceof PacketWithConsumerID) {
			consumerID = ((PacketWithConsumerID) packet).getConsumerID();//getJMSConsumerID();
		}

		if (Logging.isWritable(this, Severity.DEBUG)) { Logging.log(this, Severity.DEBUG, "Got message for session with id: ", sessionID); }
		if (Logging.isWritable(this, Severity.DEBUG)) { Logging.log(this, Severity.DEBUG, "Available sessions : ", sessions); }

		Object session = ((WeakReference<Session>)sessions.get(sessionID)).get();

		if (session == null) {
			throw new javax.jms.JMSException("Session with session id:" + sessionID + " does not exist");
		}
		
		if (session instanceof JMSSession) {
			((JMSSession)session).onPacketReceived(consumerID, packet);
		} else {
			((JMSSession)((JMSXASession)session).getSession()).onPacketReceived(consumerID, packet);
		}
	}

	/**
	 * Method isClosed. Rerurns the status of the connection.
	 * @return boolean  true if the connection is closed
	 */
	public boolean isClosed() {
		return isClosed;
	}

	/**
	 * Method deleteTemporaryDestination. Sends a message to the server to delete a
	 * temporary destination.
	 * @param destinationID  ID of the destination to be deleted
	 * @throws JMSException  thrown if the destination could not be deleted
	 */
	public void deleteTemporaryDestination(int destinationID) throws javax.jms.JMSException {
		getServerFacade().destinationDelete(connectionId, destinationID);
	}

	/**
	 * Method startDeliveryToSession. Starts delivery of messages to a given session.
	 * A message to the server is sent to start delivery.
	 * @param sessionID  the ID of the session that should start receiving messages.
	 * @throws JMSException  thrown if delivery could not be started
	 */
	public void startDeliveryToSession(int sessionID) throws javax.jms.JMSException {
		getServerFacade().sessionStart(connectionId, sessionID);
	}


	public TaskManager getTaskManager() {
		return taskManager;
	}

	public void setAdapterClosed() {
		adapterClosed = true;
	}

	public boolean isAdapterClosed() {
		return adapterClosed;
	}

	public void onException(JMSException jmse) {
		synchronized (syncThis) {
			if (exceptionListener != null) {
				exceptionListener.onException(jmse);
			} else {
				asyncException = jmse;
			}
		}
	}

	public boolean isStarted() {
		return isStarted;
	}

	public String getVPName() {
		return vpName;
	}
	
	public boolean supportsOptimization() {
		return supportsOptimization;
	}

	protected void finalize() throws Throwable {//$JL-FINALIZE$
		try {
			if (isClosed) {
				return;
			}

			AsyncCloser.getInstance().scheduleForClose(this);

		} catch (Exception e) {
		} finally {
			super.finalize();
		}
	}

	public void closedConnection() {
		
	}
}
