package com.sap.sdm.apiimpl.remote.client;

import java.io.IOException;
import java.util.HashMap;

import com.sap.sdm.api.remote.Client;
import com.sap.sdm.api.remote.ClientSession;
import com.sap.sdm.api.remote.ClientSessionFactory;
import com.sap.sdm.api.remote.RemoteException;
import com.sap.sdm.api.remote.WrongPasswordException;
import com.sap.sdm.is.cs.cmd.CmdError;
import com.sap.sdm.is.cs.cmd.CmdIF;
import com.sap.sdm.is.cs.cmd.client.CmdClient;
import com.sap.sdm.is.cs.cmd.client.CmdClientFactory;
import com.sap.sdm.is.cs.remoteproxy.client.CmdClientGetter;
import com.sap.sdm.is.cs.remoteproxy.client.ExceptionFactory;
import com.sap.sdm.is.cs.remoteproxy.client.ProxyLockHandler;
import com.sap.sdm.is.cs.remoteproxy.client.RemoteProxyClient;
import com.sap.sdm.is.cs.remoteproxy.client.RemoteProxyClientFactory;
import com.sap.sdm.is.cs.remoteproxy.client.ServerExceptionWrapperFactory;
import com.sap.sdm.is.cs.session.common.CmdCloseConnection;
import com.sap.sdm.is.cs.session.common.CmdCloseSession;
import com.sap.sdm.is.cs.session.common.CmdCloseSessionAccepted;
import com.sap.sdm.is.cs.session.common.CmdFactory;
import com.sap.sdm.is.cs.session.common.CmdLoginAccepted;
import com.sap.sdm.is.cs.session.common.CmdLoginRequest;
import com.sap.sdm.is.cs.session.common.CmdReopenConnection;
import com.sap.sdm.is.cs.session.common.CmdReopenConnectionAccepted;
import com.sap.sdm.is.security.SecurityFactory;
import com.sap.sdm.util.log.Trace;

/**
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 * 
 */
class APIClientSessionImpl implements ClientSession {

	private static final Trace trace = Trace
			.getTrace(APIClientSessionImpl.class);

	private static int STATEUNKNOWN = 1;
	private static int STATEACTIVE = 2;
	private static int STATEINACTIVE = 4;
	private static int STATECLOSED = 8;

	private static int TIMEOUT_DEFAULT = 30000;

	private boolean connectionOK = false;
	private CmdClient cmdClient = null;

	private String sessionID = null;
	private int apiServerVersion = -1;
	private int localPort = -1;
	private String localHost = null;
	private int port = -1;
	private String host = null;
	private int loginTimeout = 30000;
	private int sessionState = STATEUNKNOWN;
	private RemoteProxyClient remoteProxyClient = null;
	private Client clientInstance = null;

	// Key: Server(host; port), Value: ClientSession
	private static HashMap SDMServerToSession = new HashMap(1);

	private Object proxyLock;

	private static class SDMServer {
		int port;
		String host;

		public SDMServer(String host, int port) {
			this.host = host;
			this.port = port;
		}

		public int hashCode() {
			return host.hashCode();
		}

		public boolean equals(Object obj) {
			if (!(obj instanceof SDMServer)) {
				return false;
			}

			SDMServer _obj = (SDMServer) obj;
			if (port != _obj.port) {
				return false;
			}

			if (!host.equals(_obj.host)) {
				return false;
			}

			return true;
		}
	}

	private static SDMServer getSDMServer(String host, int port) {
		return new SDMServer(host, port);
	}

	private APIClientSessionImpl(int localPort, String localHost,
			int remotePort, String remoteHost, String pw, int timeout)
			throws RemoteException, WrongPasswordException {
		proxyLock = new Object();
		this.loginTimeout = timeout; // set the socket timeout before creating a
		// connection in CmdClient
		initData(localPort, localHost, remotePort, remoteHost, pw);
	}

	private void initData(int localPort, String localHost, int port,
			String host, String password) throws RemoteException,
			WrongPasswordException {
		trace.entering("initData(" + localPort + ", " + port + ", " + host
				+ ")");
		try {
			this.localPort = localPort;
			this.localHost = localHost;
			this.port = port;
			this.host = host;
			String initCommResult = initCommunication(localPort, localHost,
					port, host);
			if (initCommResult == null) {
				this.connectionOK = true;
			} else {
				throw new APIRemoteExceptionImpl("Server " + host
						+ " did not accept login request as admin on port "
						+ port + ". Details: " + initCommResult);
			}

			boolean isHashed;
			boolean pwExceptionWanted;
			if (password != null) {
				password = SecurityFactory.getInstance().createHashedString(
						password).getSHAString();
				isHashed = true;
				pwExceptionWanted = true;
			} else {
				// remove the possibility to connect to the SDM server with
				// default password.
				// password = "apisdm";
				// isHashed = false;
				// pwExceptionWanted = false;
				throw new NoPasswordException(
						"Login Failed. No SDM password is provided.");
			}

			CmdLoginRequest cmdLoginRequest = CmdFactory.createCmdLoginRequest(
					Integer
							.toString(ClientSessionFactory
									.getAPIClientVersion()), "apiadmin",
					password, isHashed, "", pwExceptionWanted);

			CmdIF answerCmd = cmdClient
					.processCommandWithTimeout(cmdLoginRequest);
			if ((answerCmd == null)
					|| (!(answerCmd instanceof CmdLoginAccepted))) {
				StringBuffer errText = new StringBuffer("Server ");
				errText.append(host);
				errText
						.append(" did not accept login request as apiadmin on port ");
				errText.append(String.valueOf(port));
				try {
					cmdClient.close();
				} catch (IOException e) {
					trace.debug("The socket connection could not be closed "
							+ "after unsuccessful login: " + e.getMessage());
				}
				if ((answerCmd != null) && (answerCmd instanceof CmdError)) {
					errText.append(" - ");
					errText.append(((CmdError) answerCmd).getErrorText());
					if (((CmdError) answerCmd).isWrongPasswordSupplied()) {
						throw new APIWrongPasswordExceptionImpl(errText
								.toString());
					}
					if (((CmdError) answerCmd).getErrorText().startsWith(
							"Wrong password supplied")) {
						throw new APIWrongPasswordExceptionImpl(errText
								.toString());
					}
				} else {
					errText.append(" - received unknown answer from server!");
				}
				throw new APIRemoteExceptionImpl(errText.toString());
			}
			CmdLoginAccepted login = (CmdLoginAccepted) answerCmd;
			this.sessionID = login.getSessionID();
			this.apiServerVersion = login.getAPIServerVersionAsInt();

			this.remoteProxyClient = RemoteProxyClientFactory.getInstance()
					.createNewClient(
							this.sessionID, // send session ID for downward
							// compatibility
							Client.class.getClassLoader(),
							new CmdClientGetterImpl(),
							new ProxyLockHandlerImpl(), new ExceptionFactory() {
								public Exception create(String message) {
									return new APIRemoteExceptionImpl(message);
								}
							}, new ServerExceptionWrapperFactory() {
								public Throwable createWrapper(Throwable th) {
									Throwable resultTh;
									if (th instanceof Error) {
										resultTh = new Error(th);
									} else {
										resultTh = th;
									}
									return resultTh;
								}
							}, 100);
			this.sessionState = STATEACTIVE;
			this.clientInstance = null;
		} finally {
			trace.exiting("initData(" + port + ", " + host + ")");
		}
	}

	static synchronized ClientSession newInstance(int localPort,
			String localHost, int port, String host, String password,
			int lTimeout) throws RemoteException {

		try {
			return newInstanceIDE(localPort, localHost, port, host, password,
					lTimeout);
		} catch (WrongPasswordException pwExc) {
			throw new APIRemoteExceptionImpl(pwExc.getMessage());
		}
	}

	static synchronized ClientSession newInstanceIDE(int localPort,
			String localHost, int port, String host, String password,
			int lTimeout) throws RemoteException, WrongPasswordException {
		SDMServer server = getSDMServer(host, port);
		APIClientSessionImpl session = (APIClientSessionImpl) SDMServerToSession
				.get(server);
		if (session == null) {
			session = new APIClientSessionImpl(localPort, localHost, port,
					host, password, lTimeout);
			SDMServerToSession.put(server, session);
		} else if (session.sessionState == STATECLOSED) {
			session.initData(localPort, localHost, port, host, password);
		} else {
			// we have to check the state
			if ((session.sessionState == STATEACTIVE)
					|| (session.sessionState == STATEINACTIVE)) {
				throw new APIRemoteExceptionImpl("Another "
						+ session.toString()
						+ " is still valid - cannot create a new one");
			} else {
				throw new APIRemoteExceptionImpl(session.toString()
						+ " has unknown state - cannot proceed");
			}
		}

		return session;
	}

	/**
	 * @see com.sap.sdm.api.client.APIClientSession#getClient()
	 */
	public synchronized Client getClient() throws RemoteException {

		trace.entering("getClient()");
		try {

			if (this.sessionState == STATECLOSED) {
				throw new APIRemoteExceptionImpl("This Session (" + sessionID
						+ ") was invalidated and cannot be used anymore");
			}
			if (this.sessionState == STATEUNKNOWN) {
				throw new APIRemoteExceptionImpl("This Session (" + sessionID
						+ ") was not initialized properly and cannot be used");
			}

			if (this.clientInstance != null) {
				return this.clientInstance;
			}

			reopenConnectionIfNeeded();

			clientInstance = (Client) remoteProxyClient.createInitialProxy(
					Client.class.getName(), "create");

			return clientInstance;
		} catch (Exception e) {
			throw new APIRemoteExceptionImpl(e.getMessage());
		} finally {
			trace.exiting("getClient()");
		}
	}

	/**
	 * @see com.sap.sdm.api.client.APIClientSession#passivateConnection()
	 */
	public synchronized void passivateSession() throws RemoteException {
		trace.entering("passivateSession()");
		try {
			if (this.sessionState == STATEINACTIVE) {
				return;
			}
			if (this.sessionState == STATECLOSED) {
				throw new APIRemoteExceptionImpl("This session (" + sessionID
						+ ") was closed already - cannot close connection");
			}
			if (this.sessionState == STATEUNKNOWN) {
				throw new APIRemoteExceptionImpl(
						"This session ("
								+ sessionID
								+ ") was not initialized properly - cannot close connection");
			}
			if (this.sessionState == STATEACTIVE) {
				// ok we can close the connection
				CmdCloseConnection closeConnRequest = CmdFactory
						.createCmdCloseConnection(this.sessionID);
				CmdIF answerCmd = cmdClient.processCommand(closeConnRequest);

				if (answerCmd instanceof CmdError) {
					StringBuffer errText = new StringBuffer();
					errText.append("Could not close connection for session (");
					errText.append(sessionID);
					errText.append(") - ");
					errText.append(((CmdError) answerCmd).getErrorText());
					throw new APIRemoteExceptionImpl(errText.toString());
				}
				try {
					cmdClient.close();
				} catch (IOException e) {
					throw new APIRemoteExceptionImpl(
							"The socket connection could not be closed: "
									+ e.getMessage());
				}
				this.sessionState = STATEINACTIVE;
				this.cmdClient = null;
				this.connectionOK = false;
				return;
			}
			throw new APIRemoteExceptionImpl(toString()
					+ " has unknown state - cannot proceed");
		} finally {
			trace.exiting("passivateSession()");
		}
	}

	public synchronized void closeSession() throws RemoteException {
		trace.entering("closeSession()");
		try {

			if (this.sessionState == STATECLOSED) {
				return;
			}

			if ((this.sessionState == STATEACTIVE)
					|| (this.sessionState == STATEINACTIVE)) {
				reopenConnectionIfNeeded();
				CmdCloseSession closeCall = CmdFactory
						.createCmdCloseSession(this.sessionID);
				CmdIF answerCmd = cmdClient.processCommand(closeCall);
				try {
					cmdClient.close();
				} catch (IOException e) {
					throw new APIRemoteExceptionImpl(
							"The socket connection could not be closed: "
									+ e.getMessage());
				}
				if (answerCmd instanceof CmdError) {
					throw new APIRemoteExceptionImpl("Close session failed: "
							+ ((CmdError) answerCmd).getErrorText());
				}
			}

		} finally {
			trace.exiting("closeSession()");
			this.sessionState = STATECLOSED;
			SDMServerToSession.remove(getSDMServer(host, port));
			this.remoteProxyClient = null;
			this.cmdClient = null;
			this.connectionOK = false;
		}
	}

	private void reopenConnectionIfNeeded() throws RemoteException {
		trace.entering("reopenConnectionIfNeeded()");
		try {
			if (sessionState == STATECLOSED) {
				throw new APIRemoteExceptionImpl("Session (" + sessionID
						+ ") is closed - Connection cannot be reopened");
			}
			if (sessionState != STATEINACTIVE) {
				return;
			}
			String initCommResult = initCommunication(localPort, localHost,
					port, host);
			if (initCommResult == null) {
				this.connectionOK = true;
			} else {
				throw new APIRemoteExceptionImpl("Server " + host
						+ " did not accept reopen request as admin on port "
						+ port + ". Details: " + initCommResult);
			}
			CmdReopenConnection reopenRequest = CmdFactory
					.createCmdReopenConnection(sessionID);
			CmdIF answerCmd = cmdClient.processCommand(reopenRequest);

			if (answerCmd instanceof CmdError) {
				StringBuffer errText = new StringBuffer();
				errText.append("Server ");
				errText.append(host);
				errText
						.append(" did not accept reopen request as apiadmin on port ");
				errText.append(String.valueOf(port));
				errText.append(" - ");
				errText.append(((CmdError) answerCmd).getErrorText());
				throw new APIRemoteExceptionImpl(errText.toString());
			}
			if (answerCmd instanceof CmdReopenConnectionAccepted) {
				// Reopen request was accepted by server
				this.sessionState = STATEACTIVE;
				return;
			} else {
				throw new APIRemoteExceptionImpl("Server " + host
						+ " did not accept reopen request as apiadmin on port "
						+ port);
			}

		} finally {
			trace.exiting("reopenConnectionIfNeeded()");
		}
	}

	private class CmdClientGetterImpl implements CmdClientGetter {
		public CmdClient get() throws CmdClientGetter.Exception {
			try {
				reopenConnectionIfNeeded();
				return cmdClient;
			} catch (RemoteException e) {
				throw new CmdClientGetter.Exception(e.getMessage());
			}
		}
	}

	private class ProxyLockHandlerImpl implements ProxyLockHandler {
		public Object getLock() {
			return proxyLock;
		}
	}

	private String initCommunication(int localPort, String localHost, int port,
			String host) {
		trace.entering("initCommunication()");
		try {

			String lHost = host;
			if (lHost == null)
				lHost = "localhost";

			try {
				cmdClient = CmdClientFactory.getInstance().create(localHost,
						localPort, port, lHost, loginTimeout);
			} catch (IOException e) {
				String errText = "ERROR: Could not establish connection to server "
						+ host + " at port " + port + ": " + e.getMessage();
				trace.debug(errText);
				return errText;
			}

			return null;
		} finally {
			trace.exiting("initCommunication()");
		}

	}

	public String toString() {
		return "Session=[Local port:" + localPort + ", Host:" + host
				+ ", Port:" + port + ", SessionID:" + sessionID + "]";
	}

	protected void finalize() {
		try {
			super.finalize();
		} catch (Throwable t) {// $JL-EXC$
			// ignore errors
		}
		if ((this.sessionState == STATEACTIVE)
				|| (this.sessionState == STATEINACTIVE)) {
			CmdCloseSession closeCall = CmdFactory
					.createCmdCloseSession(this.sessionID);
			CmdIF answerCmd = cmdClient.processCommand(closeCall);
			if ((answerCmd != null)
					&& (answerCmd instanceof CmdCloseSessionAccepted)) {
				this.remoteProxyClient = null;
				this.sessionState = STATECLOSED;
				this.cmdClient = null;
				this.connectionOK = false;
			}
		}
	}

}
