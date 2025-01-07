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
package com.sap.engine.services.dc.cm.session_id;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.structure.tree.CfgBuilder;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public abstract class SessionIDFactory {

	private static SessionIDFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.session_id.impl.SessionIDFactoryImpl";

	protected SessionIDFactory() {
	}

	public static synchronized SessionIDFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static SessionIDFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (SessionIDFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003169 An error occurred while creating an instance of "
					+ "class SessionIDFactory! "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	/**
	 * Generates new <code>SessionID</code>.
	 * 
	 * @return <code>SessionID</code>
	 * @throws SessionIDException
	 *             in case the <code>SessionID</code> cannot be generated.
	 */
	public abstract SessionID generateSessionID() throws SessionIDException;

	/**
	 * Generates new <code>SessionID</code>.
	 * 
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @return <code>SessionID</code>
	 * @throws SessionIDException
	 *             in case the <code>SessionID</code> cannot be generated.
	 */
	public abstract SessionID generateSessionID(ConfigurationHandler cfgHandler)
			throws SessionIDException;

	/**
	 * Returns <code>CfgBuilder</code>
	 * 
	 * @return <code>CfgBuilder</code>
	 */
	public abstract CfgBuilder getCfgBuilder();

	/**
	 * Returns <code>SessionIDStorageManager</code>
	 * 
	 * @return <code>SessionIDStorageManager</code>
	 */
	public abstract SessionIDStorageManager getSessionIDStorageManager();

	/**
	 * Returns <code>SessionID</code> build from the given <code>String</code>
	 * 
	 * @param sessionID
	 *            <code>String</code>
	 * @return <code>SessionID</code>
	 */
	public abstract SessionID build(String sessionID);
}
