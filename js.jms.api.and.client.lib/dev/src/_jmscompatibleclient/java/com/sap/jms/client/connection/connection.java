/**
 * Connection.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.connection;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import com.sap.jms.util.compat.concurrent.ConcurrentHashMap;

import javax.jms.ExceptionListener;
import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.JMSConstants;
import com.sap.jms.client.session.JMSQueueSession;
import com.sap.jms.client.session.JMSSession;
import com.sap.jms.client.session.JMSTopicSession;
import com.sap.jms.client.session.JMSSession.SessionType;
import com.sap.jms.client.xa.JMSXAQueueSession;
import com.sap.jms.client.xa.JMSXASession;
import com.sap.jms.client.xa.JMSXATopicSession;
import com.sap.jms.protocol.Packet;
import com.sap.jms.protocol.PacketWithConsumerID;
import com.sap.jms.protocol.PacketWithSessionID;
import com.sap.jms.util.logging.LogService;
import com.sap.jms.util.logging.LogServiceImpl;
import com.sap.jms.client.connection.ServerFacade.SessionDescriptor;

public class Connection implements javax.jms.Connection, Closeable {

	public final static class ConnectionType {
	    public static final ConnectionType QUEUE_CONNECTION = new ConnectionType();
	    public static final ConnectionType TOPIC_CONNECTION = new ConnectionType();
	    public static final ConnectionType GENERIC_CONNECTION = new ConnectionType();
	    public static final ConnectionType XA_QUEUE_CONNECTION = new ConnectionType();
	    public static final ConnectionType XA_TOPIC_CONNECTION = new ConnectionType();
	    public static final ConnectionType XA_GENERIC_CONNECTION = new ConnectionType();

	    private ConnectionType() {
	    }                            
	}
	
	protected static String LNAME;

	private static final ConnectionMetaData connectionMetaData = new ConnectionMetaData();

	private boolean isClosed = false;
	private boolean isStarted = false;
	private boolean adapterClosed = false;
	protected long connectionID = 0;
	protected NetworkAdapter networkAdapter = null;
	protected ServerFacade serverFacade = null;
	private ThreadSystem threadSystem = null;
	private Map/*<Integer, WeakReference<Session>> */ sessions;
	private javax.jms.ConnectionConsumer connectionConsumer = null;
	private String serverInstance = null;
	private String clientID = null;
	private boolean canSetClientID = true;
	protected LogService logService = LogServiceImpl.getLogService(LogServiceImpl.CLIENT_LOCATION);
	private Object syncThis = new Object();
	private boolean isStopping = false;
	private ExceptionListener exceptionListener = null;
	private JMSException asyncException = null;

	public Connection(long connectionID, String serverInstance, NetworkAdapter networkAdapter, ThreadSystem threadSystem) {
		this.networkAdapter = networkAdapter;
		this.serverFacade = new RemoteServerFacade(networkAdapter);
		this.threadSystem = threadSystem;
		this.connectionID = connectionID;
		this.serverInstance = serverInstance;
		networkAdapter.setConnection(this);
		sessions = new ConcurrentHashMap/*<Integer, WeakReference<Session>>*/();
		LNAME = getClass().getName();
	}

	public ServerFacade getServerFacade() {
		return serverFacade;
	}

	public void close() throws javax.jms.JMSException {
		synchronized (syncThis) {
			logService.debug(LNAME, "Will close connection with id: " + connectionID);

			if (isClosed) {
				return;
			}

			try {
				for (Iterator i = sessions.values().iterator(); i.hasNext(); ) {
					WeakReference sessionRef = (WeakReference) i.next(); 
					if (sessionRef != null && sessionRef.get() != null) {
						((Session)sessionRef.get()).close();
					}
				}
			} finally {
				isClosed = true;
			}
			getServerFacade().connectionClose(connectionID);


			try {
				networkAdapter.close();
			} catch (java.io.IOException ioe) {
				logService.exception(LogService.WARNING, LNAME, ioe);
				JMSException jmse = new JMSException("Cannot close socket");
				jmse.initCause(ioe);
				jmse.setLinkedException(ioe);
				throw jmse;
			}

			exceptionListener = null;
			connectionConsumer = null;
		}
	}

	public void closeSession(int sessionID) throws javax.jms.JMSException {
		logService.debug(LNAME, "Will close session with id: " + sessionID);

		// make sure we pass only once through close session for a particular sessionId
		WeakReference/*<Session>*/ sessionRef = (WeakReference)sessions.remove(new Integer(sessionID));
		if (sessionRef == null || sessionRef.get() == null) {
			logService.warningTrace(LNAME, "Session with id: " + sessionID + " under connection with id: " + connectionID + " is already closed.");
			return;
		}
		
		// no need to check isClosed, since if the sessions collection contained
		// this session the connection is not closed yet!
		getServerFacade().sessionClose(connectionID, sessionID);
	}

	public javax.jms.ConnectionConsumer createConnectionConsumer(javax.jms.Destination destination, String selector, ServerSessionPool pool, int maxMessages) throws javax.jms.JMSException {

		synchronized (syncThis) {
			attemptToUse();
			canSetClientID = false;

			connectionConsumer = new JMSConnectionConsumer(destination, selector, pool, this, maxMessages);
			return connectionConsumer;
		}
	}

	public javax.jms.ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscription, String selector, ServerSessionPool pool, int maxMessages) throws javax.jms.JMSException {

		synchronized (syncThis) {
			attemptToUse();
			canSetClientID = false;
			connectionConsumer = new JMSConnectionConsumer(topic, subscription, selector, pool, this, maxMessages);
			return connectionConsumer;
		}
	}

	public void closeConnectionConsumer() throws JMSException {
		synchronized (syncThis) {
			attemptToUse();
			canSetClientID = false;
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
			canSetClientID = false;

			if (transacted) {
				ackMode = JMSSession.SESSION_TRANSACTED;
			} else if (ackMode != JMSSession.AUTO_ACKNOWLEDGE
					&& ackMode != JMSSession.CLIENT_ACKNOWLEDGE
					&& ackMode != JMSSession.DUPS_OK_ACKNOWLEDGE) {
				throw new javax.jms.JMSException("Incorrect acknowledge mode specified.");
			}

			SessionDescriptor sd;
			if (sessionType == SessionType.GENERIC_SESSION || sessionType == SessionType.QUEUE_SESSION || sessionType == SessionType.TOPIC_SESSION) {
				sd = getServerFacade().sessionCreate(connectionID, (byte) ackMode, false);
			} else {
				logService.warning(LNAME,"Will use AUTO_ACKNOWLEDGE instead of SESSION_TRANSACTED for new type of xa session");
				ackMode = Session.AUTO_ACKNOWLEDGE;
				
				sd = getServerFacade().sessionCreate(connectionID, (byte) ackMode, true);
			}

			Session session = null;

			if (sessionType == SessionType.GENERIC_SESSION) {
				session = new JMSSession(sd.getSessionId(), sd.getMessageIdBase(), ackMode, this, threadSystem);
			} else if (sessionType == SessionType.QUEUE_SESSION) {
				session = new JMSQueueSession(sd.getSessionId(), sd.getMessageIdBase(), ackMode, this, threadSystem);
			} else if (sessionType == SessionType.TOPIC_SESSION) {
				session = new JMSTopicSession(sd.getSessionId(), sd.getMessageIdBase(), ackMode, this, threadSystem);
			} else if (sessionType == SessionType.XA_GENERIC_SESSION) {
					session = new JMSXASession(new JMSSession(sd.getSessionId(), sd.getMessageIdBase(), ackMode, this, threadSystem));
			} else if (sessionType == SessionType.XA_QUEUE_SESSION) {
					session = new JMSXAQueueSession(new JMSQueueSession(sd.getSessionId(), sd.getMessageIdBase(), ackMode, this, threadSystem));
			} else if (sessionType == SessionType.XA_TOPIC_SESSION) {
					session = new JMSXATopicSession(new JMSTopicSession(sd.getSessionId(), sd.getMessageIdBase(), ackMode, this, threadSystem));
			}


			sessions.put(new Integer(sd.getSessionId()), new WeakReference/*<Session>*/(session));

			logService.debug(LNAME, "Mapped " + session + "(ack: "+ ackMode + ", msgBase: " + sd.getMessageIdBase() +")"
					+ " to session with id: " + sd.getSessionId() + " under connection with id: " + connectionID );

			return session;
		}
	}

	public String getClientID() throws javax.jms.JMSException {
		attemptToUse();
		return clientID;
	}

	/**
	 * Returns the connection ID
	 *
	 * @return
	 * @throws javax.jms.JMSException
	 */
	public long getConnectionID() throws javax.jms.JMSException {
		attemptToUse();
		return connectionID;
	}

	public ExceptionListener getExceptionListener() throws javax.jms.JMSException {
		synchronized (syncThis) {
			attemptToUse();
			canSetClientID = false;
			return exceptionListener;
		}
	}

	public javax.jms.ConnectionMetaData getMetaData() throws javax.jms.JMSException {
		attemptToUse();
		canSetClientID = false;
		return connectionMetaData;
	}

	public void setClientID(String id) throws javax.jms.JMSException {
		attemptToUse();
		if (canSetClientID) {
			clientID = id;
			canSetClientID = false;
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
			canSetClientID = false;
			this.exceptionListener = exceptionListener;
		}
	}

	public void start() throws javax.jms.JMSException {
		synchronized (syncThis) {
			attemptToUse();

			logService.debug(LNAME, "Will start connection with id: " + connectionID);
			while (isStopping) {
				try {
					syncThis.wait();
				} catch (InterruptedException ie) {
					logService.warningTrace(LNAME, "JMS start connection failed due to interrupted thread! Messages cannot be received.");
					logService.exception(LogService.DEBUG, LNAME, ie);
				}
			}

			canSetClientID = false;

			if (isStarted) {
				return;
			}

			for (Iterator i = sessions.values().iterator(); i.hasNext(); ) {
				WeakReference sessionRef = (WeakReference) i.next(); 	
				if (sessionRef != null && sessionRef.get() != null) {
					if (sessionRef.get() instanceof JMSSession) {
						((JMSSession)sessionRef.get()).start();
					} else {
						((JMSSession)((JMSXASession)sessionRef.get()).getSession()).start();
					}
				}
			}

			getServerFacade().connectionStart(connectionID);
			isStarted = true;
		}
	}

	public void stop() throws javax.jms.JMSException {
		try {
			synchronized (syncThis) {

				attemptToUse();

				logService.debug(LNAME, "Will stop connection with id: " + connectionID);

				if (!isStarted) {
					return;
				}
				getServerFacade().connectionStop(connectionID);
				isStopping = true;
				isStarted = false;
			}

			for (Iterator i = sessions.values().iterator(); i.hasNext(); ) {
				WeakReference sessionRef = (WeakReference) i.next(); 	
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
			throw new IllegalStateException("The Connection is closed. id: " + connectionID);
		}
	}

	/**
	 * Method onPacketReceived. Creates a message from the received Packet and gives
	 * control to the relevant session.
	 * @param packet  the received packet
	 * @throws JMSException  thrown if internal error occurs
	 */
	public void onPacketReceived(Packet packet) throws javax.jms.JMSException {
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

		logService.debug(LNAME, "Got message for session with id: " + sessionID);
		logService.debug(LNAME, "Available sessions : " + sessions);

		Object session = ((WeakReference)sessions.get(new Integer(sessionID))).get();

		try {
			if (session instanceof JMSSession) {
				((JMSSession)session).onPacketReceived(new Long(consumerID), packet);
			} else {
				((JMSSession)((JMSXASession)session).getSession()).onPacketReceived(new Long(consumerID), packet);
			}
		} catch (Exception e) {
			logService.exception(LNAME, e);
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
		getServerFacade().destinationDelete(connectionID, destinationID);
	}

	/**
	 * Method startDeliveryToSession. Starts delivery of messages to a given session.
	 * A message to the server is sent to start delivery.
	 * @param sessionID  the ID of the session that should start receiving messages.
	 * @throws JMSException  thrown if delivery could not be started
	 */
	public void startDeliveryToSession(int sessionID) throws javax.jms.JMSException {
		getServerFacade().sessionStart(connectionID, sessionID);
	}


	/**
	 * Method getThreadSystem. Returns the ThreadSystem object associated with the connection.
	 * @return ThreadSystem the ThreadSystem object associated with the connection.
	 */
	public ThreadSystem getThreadSystem() {
		return threadSystem;
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

	public String getServerInstance() {
		return serverInstance;
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

}
