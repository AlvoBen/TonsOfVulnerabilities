package com.sap.engine.services.dc.api;

import java.util.Properties;

import com.sap.engine.services.dc.api.util.DAConstants;
import com.sap.engine.services.dc.api.util.DALog;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * 
 * <DT><B>Description: </B></DT>
 * <DD>This factory provides mechanism for creation of clients.</DD>
 * <DD>This class is an entry point to the DC API.</DD>
 * <DT>The preferred mechanism for creating:</DT>
 * <DD>DALog.Logger customLooger = createYourLoggerHere();<BR>
 * DALog daLog = DALog.getInstance(customLooger);</DD>
 * <UL>
 * Create <code>Client</code> instance
 * <LI>On Server side in order to get client to deploy to the same java instance
 * you should do :<BR>
 * Client client = ClientFactory.getInstance().createClient(daLog );</LI>
 * 
 * <LI>On the Client side (or on server side if you wish to deploy against
 * different server) you should do:<BR>
 * Client client = ClientFactory.getInstance().createClient(daLog,hostName,
 * p4Port, userName, userPassword);</LI>
 * </UL>
 * 
 * Since Ehp1 there is some extra functionality that relies on the sapcontrol
 * web service. Because of this there is now a factory method that lets you
 * specify the sapcontrol webservice port of the target java instance. If this
 * port is not specified the implementation will try to calculate it based on
 * the p4 port and the formula:
 * 
 * 5<NR>13 where <NR> is a two digit instance number. For example for instance
 * number 0 NR=00 and the sapcontrol port is 50013
 * 
 * 
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-9-9</DD>
 * </DL>
 * 
 * @see com.sap.engine.services.dc.api.Client
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public abstract class ClientFactory {
	protected static boolean runningOnServer = false;

	private static ClientFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.api.impl.ClientFactoryImpl";

	/**
	 * Creates a client factory instance or returns already created one.
	 * 
	 * @return a client factory or null if the implementation is not available.
	 */
	public static synchronized ClientFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static ClientFactory createFactory() {
		try {
			final Class classFactory = Class.forName(FACTORY_IMPL, true, Thread
					.currentThread().getContextClassLoader());
			return (ClientFactory) classFactory.newInstance();
		} catch (ClassNotFoundException cnfe) {
			return null;
		} catch (NoClassDefFoundError ncde) {
			return null;
		} catch (Exception e) {
			final String errMsg = "[ERROR CODE DPL.DCAPI.1001] An error occurred while creating an instance of "
					+ "class ClientFactory! "
					+ DAConstants.EOL
					+ e.getMessage();
			throw new RuntimeException(errMsg);
		}
	}

	/**
	 * Returns true if this client is running on server side, false otherwise.
	 * 
	 * @return true if this client is running on server side, false otherwise
	 */
	public static boolean isRunningOnServerSide() {
		return runningOnServer;
	}

	/**
	 * Creates new client with preliminary created credentials. This method is
	 * convenienced for creating clients in server context where the credentials
	 * are set with default logger.
	 * 
	 * @return new client instance
	 * @throws AuthenticationException
	 * @throws ConnectionException
	 */
	public abstract Client createClient() throws AuthenticationException,
			ConnectionException;

	/**
	 * Creates new client with preliminary created credentials. This method is
	 * convenienced for creating clients in server context where the credentials
	 * are already set.
	 * 
	 * @param daLog
	 *            log instance - all traces and log goes to this one
	 * @return new client instance
	 * @throws AuthenticationException
	 * @throws ConnectionException
	 */
	public abstract Client createClient(DALog daLog)
			throws AuthenticationException, ConnectionException;

	/**
	 * Creates new client. Use this method if you are sure you have valid
	 * credentials. This Method actually creates a client with default logger
	 * which produces two files in the currentdir/log/dc_logs folder with names
	 * according to the pattern: deploy_&lt;timestamp&gt;.log|trc .
	 * 
	 * @param host
	 *            host name
	 * @param port
	 *            port number
	 * @param user
	 *            user name
	 * @param password
	 *            user password
	 * @return new client instance
	 * @throws AuthenticationException
	 * @throws ConnectionException
	 * @see DALog
	 */
	public abstract Client createClient(String host, int port, String user,
			String password) throws AuthenticationException,
			ConnectionException;

	/**
	 * Creates new client regarding given host, port, SAP Control port, user
	 * name and user password.
	 * 
	 * @param host
	 *            host name
	 * @param port
	 *            port number
	 * @param sapcontrolPort
	 *            SAP Control port
	 * @param user
	 *            user name
	 * @param password
	 *            user password
	 * @return new client instance
	 * @throws AuthenticationException
	 *             then credentials are wrong
	 * @throws ConnectionException
	 *             when cannot instanctiate connection to the server
	 */
	public abstract Client createClient(String host, int port,
			int sapcontrolPort, String user, String password)
			throws AuthenticationException, ConnectionException;

	/**
	 * Creates new client regarding given log implementation, host, port, and
	 * user password pairs.
	 * 
	 * @param daLog
	 *            log instance - all traces and log goes to this one
	 * @param host
	 *            host name
	 * @param port
	 *            port number
	 * @param user
	 *            user name
	 * @param password
	 *            user password
	 * @return new client instance
	 * @throws AuthenticationException
	 * @throws ConnectionException
	 */
	public abstract Client createClient(DALog daLog, String host, int port,
			String user, String password) throws AuthenticationException,
			ConnectionException;

	/**
	 * Creates new client with the given log implementation, host, port etc You
	 * can use the p4props to pass additional p4 properties, such as if SSL
	 * should be used.
	 * 
	 * @param daLog
	 *            log instance - all traces and log goes to this one
	 * @param host
	 *            host name
	 * @param port
	 *            port number
	 * @param user
	 *            user name
	 * @param password
	 *            user password
	 * @param p4props
	 *            additional P4 properties( could be null ).
	 * @return new client instance
	 * @throws AuthenticationException
	 * @throws ConnectionException
	 */
	public abstract Client createClient(DALog daLog, String host, int port,
			String user, String password, Properties p4props)
			throws AuthenticationException, ConnectionException;

	/**
	 * Creates new client with the given log implementation, host, port etc You
	 * can use the p4props to pass additional p4 properties, such as if SSL
	 * should be used.
	 * 
	 * @param daLog
	 *            - all traces and log goes to this one
	 * @param host
	 *            host name
	 * @param p4port
	 *            the port number of the p4 protocol on the target java instance
	 * @param sapcontrolPort
	 *            the port on which the sapcontrol web service is running
	 * @param user
	 *            user name
	 * @param password
	 *            user password
	 * @param p4props
	 *            additional P4 properties( could be null ).
	 * 
	 * @return an instance of the client
	 * @throws AuthenticationException
	 * @throws ConnectionException
	 */
	public abstract Client createClient(DALog daLog, String host, int p4port,
			int sapcontrolPort, String user, String password, Properties p4props)
			throws AuthenticationException, ConnectionException;

}