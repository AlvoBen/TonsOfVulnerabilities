/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.session;

import java.util.Properties;

import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.AuthenticationException;
import com.sap.engine.services.dc.api.util.DAConstants;
import com.sap.engine.services.dc.api.util.DALog;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Factory providing mechanism for creating <code>Session</code> objects.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-9-9</DD>
 * </DL>
 * 
 * @author Georgi Danov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public abstract class SessionFactory {

	private static SessionFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.api.session.impl.SessionFactoryImpl";

	protected SessionFactory() {
		// nothing fancy
	}

	/**
	 * this method is realized as singleton.
	 * 
	 * @return <code>SessionFactory</code> instance
	 */
	public static synchronized SessionFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static SessionFactory createFactory() {
		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (SessionFactory) classFactory.newInstance();
		} catch (Exception e) {
			String errMsg = "[ERROR CODE DPL.DCAPI.1147] An error occurred while creating an instance of class ClientFactory! "
					+ DAConstants.EOL + e.getMessage();
			throw new RuntimeException(errMsg);
		}
	}

	/**
	 * Creates new Session to <b>host </b> on port: <b>port </b> using user:
	 * <b>user </b> and password: <b>password </b>.Mbr> If the <b>host </b> is
	 * null then for creating session is used already initialized initial
	 * context. In order to retrieve working session when pass null to the host
	 * argument you should initialize the InitialContext preliminary.
	 * 
	 * @param daLog
	 *            log object all logs and traces goes to the log.daLog can not
	 *            be null.
	 * @param host
	 *            host to connect
	 * @param port
	 *            connection port
	 * @param user
	 *            name
	 * @param password
	 * @return new session with the given credentials
	 * @throws AuthenticationException
	 * @throws ConnectionException
	 */
	public abstract Session newSession(DALog daLog, String host, int port,
			String user, String password) throws AuthenticationException,
			ConnectionException;

	/**
	 * Creates new Session to <b>host </b> on port: <b>port </b> using user:
	 * <b>user </b> and password: <b>password </b>.Mbr> If the <b>host </b> is
	 * null then for creating session is used already initialized initial
	 * context. In order to retrieve working session when pass null to the host
	 * argument you should initialize the InitialContext preliminary.
	 * 
	 * @param daLog
	 *            log object all logs and traces goes to the log.daLog can not
	 *            be null.
	 * @param host
	 *            host to connect
	 * @param port
	 *            connection port
	 * @param user
	 *            name
	 * @param p4props
	 *            additional p4 properties
	 * @param password
	 * @return new session with the given credintials
	 * @throws AuthenticationException
	 * @throws ConnectionException
	 */
	public abstract Session newSession(DALog daLog, String host, int port,
			String user, String password, Properties p4props)
			throws AuthenticationException, ConnectionException;

	/**
	 * Creates new Session to <b>host </b> on port: <b>port </b> using user:
	 * <b>user </b> and password: <b>password </b>.Mbr> If the <b>host </b> is
	 * null then for creating session is used already initialized initial
	 * context. In order to retrieve working session when pass null to the host
	 * argument you should initialize the InitialContext preliminary.
	 * 
	 * @param daLog
	 *            log object all logs and traces goes to the log.daLog can not
	 *            be null.
	 * @param host
	 *            host to connect
	 * @param p4port
	 *            connection port
	 * @param sapcontrolPort
	 *            the port on whic the sapcontrol web service of the target java
	 *            instance is running on
	 * @param user
	 * @param password
	 * @param p4props
	 *            additional p4 properties
	 * @return new session with the given credintials
	 * @throws AuthenticationException
	 * @throws ConnectionException
	 */
	public abstract Session newSession(DALog daLog, String host, int p4port,
			int sapcontrolPort, String user, String password, Properties p4props)
			throws AuthenticationException, ConnectionException;

}