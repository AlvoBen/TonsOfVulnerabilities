package com.sap.sdm.apiimpl.remote.client;

import com.sap.engine.services.dc.api.ClientFactory;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.util.DALog;
import com.sap.sdm.api.remote.AuthenticationException;
import com.sap.sdm.api.remote.Client;
import com.sap.sdm.api.remote.ClientSession;
import com.sap.sdm.api.remote.RemoteException;
import com.sap.sdm.api.remote.UnsupportedProtocolException;
import com.sap.sdm.apiimpl.remote.client.p4.P4ClientFactory;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-7-6
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
class P4APIClientSessionImpl implements ClientSession {

	private static final byte STATE_UNKNOWN = 1;
	private static final byte STATE_ACTIVE = 2;
	private static final byte STATE_INACTIVE = 4;
	private static final byte STATE_CLOSED = 8;

	private Client client;
	private com.sap.engine.services.dc.api.Client dcClient;
	private byte sessionState = STATE_UNKNOWN;

	private final int port;
	private final String host;
	private final String user;
	private final String password;

	static/* synchronized */ClientSession newInstance(int port, String host,
			String user, String password) throws UnsupportedProtocolException,
			AuthenticationException, RemoteException {

		return new P4APIClientSessionImpl(port, host, user, password);
	}

	private P4APIClientSessionImpl(int port, String host, String user,
			String password) throws UnsupportedProtocolException,
			AuthenticationException, RemoteException {

		this.port = port;
		this.host = host;
		this.user = user;
		this.password = password;

		initClient();
		this.sessionState = STATE_ACTIVE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.ClientSession#getClient()
	 */
	public synchronized Client getClient() throws RemoteException {
		if (this.sessionState == STATE_UNKNOWN) {
			throw new APIRemoteExceptionImpl(
					"The session was not initialized properly "
							+ "and cannot be used!");
		} else if (this.sessionState == STATE_CLOSED) {
			throw new APIRemoteExceptionImpl(
					"The session was closed and cannot be used anymore!");
		} else if (this.sessionState == STATE_INACTIVE) {
			try {
				initClient();
			} catch (APIAuthenticationException ae) {
				throw new APIRemoteExceptionImpl(
						"An authentication error occurred while "
								+ "getting the client with the credentials: "
								+ "user: '" + this.user + "', host: '"
								+ this.host + "' and port: '" + this.port
								+ "'!", ae);
			}
		}

		return this.client;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.ClientSession#passivateSession()
	 */
	public synchronized void passivateSession() throws RemoteException {
		if (this.sessionState == STATE_INACTIVE) {
			return;
		} else if (this.sessionState == STATE_CLOSED) {
			throw new APIRemoteExceptionImpl(
					"This session has been closed already - "
							+ "cannot close connection!");
		} else if (this.sessionState == STATE_UNKNOWN) {
			throw new APIRemoteExceptionImpl(
					"This session was not initialized properly - "
							+ "cannot close connection!");
		} else if (this.sessionState == STATE_ACTIVE) {
			close();
			this.client = null;
			this.sessionState = STATE_INACTIVE;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.ClientSession#closeSession()
	 */
	public synchronized void closeSession() throws RemoteException {
		if (this.sessionState == STATE_CLOSED) {
			return;
		}

		try {
			close();
		} finally {
			this.client = null;
			this.sessionState = STATE_CLOSED;
		}
	}

	private void initClient() throws APIAuthenticationException,
			APIRemoteExceptionImpl {
		try {
			this.dcClient = getDeployControllerClient(this.host, this.port,
					this.user, this.password);
		} catch (ConnectionException apice) {
			throw new APIAuthenticationException("The user '" + this.user
					+ "' could not be connected to the " + "specified host '"
					+ this.host + "'and port '" + this.port + "'.", apice);
		} catch (com.sap.engine.services.dc.api.AuthenticationException ae) {
			throw new APIAuthenticationException("The user '" + this.user
					+ "' could not be authenticated for the "
					+ "specified host '" + this.host + "'and port '"
					+ this.port + "'.", ae);
		} catch (com.sap.engine.services.dc.api.APIException apie) {
			throw new APIRemoteExceptionImpl(
					"An error occurred while getting the Deploy Controller API "
							+ "client with the specified host '" + this.host
							+ "', port '" + this.port + "' and user '"
							+ this.user + "'.", apie);
		}

		this.client = P4ClientFactory.getInstance().createClient(this.dcClient);
	}

	private com.sap.engine.services.dc.api.Client getDeployControllerClient(
			String host, int port, String user, String password)
			throws com.sap.engine.services.dc.api.APIException,
			com.sap.engine.services.dc.api.AuthenticationException,
			ConnectionException {
		return ClientFactory.getInstance().createClient(
				DALog.getInstance(null, true), // should always log to separate
				// file like SDM API always do
				host, port, user, password);
	}

	private void close() throws RemoteException {
		if (this.dcClient != null) {
			try {
				this.dcClient.close();
			} catch (ConnectionException apice) {
				throw new APIRemoteExceptionImpl(
						"Connection error occurred while closing the "
								+ "Deploy Controller API client.", apice);
			}
		}
	}

}
