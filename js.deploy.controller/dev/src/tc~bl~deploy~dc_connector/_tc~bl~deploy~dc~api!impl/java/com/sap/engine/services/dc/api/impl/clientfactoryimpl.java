package com.sap.engine.services.dc.api.impl;

import java.util.Properties;

import com.sap.engine.services.dc.api.AuthenticationException;
import com.sap.engine.services.dc.api.Client;
import com.sap.engine.services.dc.api.ClientFactory;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.session.Session;
import com.sap.engine.services.dc.api.session.SessionFactory;
import com.sap.engine.services.dc.api.util.DALog;
import com.sap.engine.services.dc.api.util.Executor;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-9
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class ClientFactoryImpl extends ClientFactory {

	private Executor asyncExecutor;

	public ClientFactoryImpl() {
		// nothing fancy
	}

	public void setRunningOnServer(boolean runningOnServer) {
		super.runningOnServer = runningOnServer;
	}

	public void setAsyncExecutor(Executor executor) {
		this.asyncExecutor = executor;
	}

	public synchronized Executor getAsyncExecutor() {

		if (this.asyncExecutor == null) {
			// if we are on the server side the dc_connector frame should
			// have set the executor so far. Assuming we are on the client side

			// there used to be a bug in Security that was causing a NPE
			// during inheritance of thread locals while creating the executor
			// on demand.
			// see Change 170728 by i030797@SOFD60157766A on 2007/11/14 11:05:00
			// and CSN I-5857706 2007

			this.asyncExecutor = new DefaultAsyncExecutor();

		}

		return this.asyncExecutor;
	}

	/**
	 * 
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 * @return
	 * @throws AuthenticationException
	 * @throws ConnectionException
	 */
	public Client createClient(String host, int port, String user,
			String password) throws AuthenticationException,
			ConnectionException {

		return createClient(DALog.getInstance(), host, port, user, password);
	}

	/**
	 * 
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 * @return
	 * @throws AuthenticationException
	 * @throws ConnectionException
	 */
	public Client createClient(String host, int port, int sapcontrolPort,
			String user, String password) throws AuthenticationException,
			ConnectionException {

		return createClient(DALog.getInstance(), host, port, sapcontrolPort,
				user, password, new Properties());
	}

	/**
	 * Use this method when you access the API server side and you don't need to
	 * provide your own logger
	 * 
	 * @return
	 * @throws AuthenticationException
	 * @throws ConnectionException
	 */
	public Client createClient() throws AuthenticationException,
			ConnectionException {

		return createClient(DALog.getInstance());
	}

	public String toString() {
		return "Client Factory Implementation";
	}

	public Client createClient(DALog daLog, String host, int port, String user,
			String password) throws AuthenticationException,
			ConnectionException {

		Session session = SessionFactory.getInstance().newSession(daLog, host,
				port, user, password);
		Client client = new ClientImpl(session);
		return client;
	}

	public Client createClient(DALog daLog, String host, int port, String user,
			String password, Properties p4props)
			throws AuthenticationException, ConnectionException {

		Session session = SessionFactory.getInstance().newSession(daLog, host,
				port, user, password, p4props);
		Client client = new ClientImpl(session);
		return client;
	}

	public Client createClient(DALog daLog, String host, int p4port,
			int sapcontrolPort, String user, String password, Properties p4props)
			throws AuthenticationException, ConnectionException {

		Session session = SessionFactory.getInstance().newSession(daLog, host,
				p4port, sapcontrolPort, user, password, p4props);
		Client client = new ClientImpl(session);
		return client;

	}

	/**
	 * Use this method when you access the API server side and you need to
	 * provide your own logger
	 * 
	 * @param daLog
	 *            your
	 */
	public Client createClient(DALog daLog) throws AuthenticationException,
			ConnectionException {

		return createClient(daLog, null, -1, null, null);
	}

}
