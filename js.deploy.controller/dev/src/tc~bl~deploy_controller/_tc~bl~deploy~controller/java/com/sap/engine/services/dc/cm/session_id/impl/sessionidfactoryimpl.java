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
package com.sap.engine.services.dc.cm.session_id.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.util.ArrayList;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.dc.cm.session_id.SessionID;
import com.sap.engine.services.dc.cm.session_id.SessionIDException;
import com.sap.engine.services.dc.cm.session_id.SessionIDFactory;
import com.sap.engine.services.dc.cm.session_id.SessionIDStorageManager;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.DCLogConstants;
import com.sap.engine.services.dc.util.structure.tree.CfgBuilder;
import com.sap.tc.logging.Location;

/**
 * Implements <code>SessionIDFactory</code>
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class SessionIDFactoryImpl extends SessionIDFactory {
	
	private Location location = DCLog.getLocation(this.getClass());

	private final static ArrayList sessionIds = new ArrayList();

	/**
	 * @see com.sap.engine.services.dc.cm.session_id.SessionIDFactory#generateSessionID()
	 */
	public SessionID generateSessionID() throws SessionIDException {
		final ConfigurationHandler cfgHandler = getCfgHandler();

		return generateSessionID(cfgHandler);
	}

	/**
	 * @see com.sap.engine.services.dc.cm.session_id.SessionIDFactory#generateSessionID(com.sap.engine.frame.core.configuration.ConfigurationHandler)
	 */
	public SessionID generateSessionID(ConfigurationHandler cfgHandler)
			throws SessionIDException {
		try {
			try {
				final SessionID sessionID = doGenerateSessionID(cfgHandler);
				cfgHandler.commit();
				return sessionID;
			} catch (ConfigurationException ce) {
				cfgHandler.rollback();
				throw ce;
			} catch (SessionIDException sIdEx) {
				cfgHandler.rollback();
				throw sIdEx;
			} finally {
				cfgHandler.closeAllConfigurations();
			}
		} catch (ConfigurationException ce) {
			throw handleGenerationException(ce);
		}
	}

	/**
	 * @see com.sap.engine.services.dc.cm.session_id.SessionIDFactory#getCfgBuilder()
	 */
	public CfgBuilder getCfgBuilder() {
		return SessionIDCfgBuilderImpl.getInstance();
	}

	/**
	 * @see com.sap.engine.services.dc.cm.session_id.SessionIDFactory#getSessionIDStorageManager(com.sap.engine.frame.core.configuration.ConfigurationHandler)
	 */
	public SessionIDStorageManager getSessionIDStorageManager() {
		return SessionIDStorageManagerImpl.getInstance();
	}

	/**
	 * @see com.sap.engine.services.dc.cm.session_id.SessionIDFactory#build(java.lang.String)
	 */
	public SessionID build(String sessionID) {
		return new SessionIDImpl(sessionID);
	}

	private ConfigurationHandler getCfgHandler() throws SessionIDException {
		try {
			return ServiceConfigurer.getInstance().getConfigurationHandler();
		} catch (ConfigurationException ce) {
			throw new SessionIDException(
					DCExceptionConstants.CANNOT_CFG_HANDLER,
					new String[] { "get" }, ce);
		}
	}

	private SessionIDException handleGenerationException(Exception e) {
		return new SessionIDException(
				DCExceptionConstants.SESSION_ID_CANNOT_GENERATE, e);
	}

	public synchronized SessionID doGenerateSessionID(
			ConfigurationHandler cfgHandler) throws ConfigurationException,
			SessionIDException {
		if (sessionIds.size() <= 0) {
			reinitLocalSessionIDs(cfgHandler);
		}
		final Long genLong = (Long) sessionIds.remove(0);
		final SessionID sessionID = build(genLong.toString());
		if (location.beDebug()) {
			traceDebug(location, "Will return session id [{0}].",
					new String[] { sessionID.toString() });
		}
		return sessionID;
	}

	private void reinitLocalSessionIDs(ConfigurationHandler cfgHandler)
			throws ConfigurationException, SessionIDException {
		final SessionIDLocation sidLocation = SessionIDLocationBuilder
				.getInstance().buildGenerate();
		Configuration sidCfg = null;
		long ms = System.currentTimeMillis();
		long genSessionId4Ms = ServiceConfigurer.getInstance()
				.getGenSessionId4Secs() * 1000;
		while ((System.currentTimeMillis() - ms) < genSessionId4Ms) {
			try {
				sidCfg = cfgHandler.openConfiguration(
						sidLocation.getLocation(),
						ConfigurationHandler.WRITE_ACCESS);
			} catch (ConfigurationLockedException cfgLockedEx) {// $JL-EXC$ -
				// will use the
				// configuration
				// lock in the
				// cluster.
				continue;
			}
			if (sidCfg != null) {
				break;
			}
		}
		if (sidCfg == null) {
			throw new SessionIDException(
					DCExceptionConstants.SESSION_ID_GENERATION_TIMED_OUT,
					new String[] { "" + (System.currentTimeMillis() - ms) });
		}

		Long genLong;
		try {
			genLong = (Long) sidCfg.getConfigEntry(SessionIDConstants.VALUE);
		} catch (NameNotFoundException nnfe) {
			genLong = new Long(0);
		}
		final String siFromDB = genLong.toString();

		final long howManyToGen = ServiceConfigurer.getInstance()
				.getGenSessionIdsAtOnes();
		for (int i = 0; i < howManyToGen; i++) {
			genLong = addOneMoreSessionID(genLong);
		}

		sidCfg.modifyConfigEntry(SessionIDConstants.VALUE, genLong, true);

		if (location.beInfo()) {
			traceInfo(location, 
					"The session id read from DB was [{0}]. The local session ids are [{1}] and updated the DB with [{2}].",
					new String[] { siFromDB, sessionIds.toString(),
							genLong.toString() });
		}
	}

	private Long addOneMoreSessionID(Long min) {
		Long genLong;
		if (min.longValue() == Long.MAX_VALUE) {
			genLong = new Long(0);
		} else {
			genLong = new Long(min.longValue() + 1);
		}
		sessionIds.add(genLong);
		return genLong;
	}

}
