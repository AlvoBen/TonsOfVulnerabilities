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

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;

/**
 * Persists and loads the active <code>SessionID</code>.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public interface SessionIDStorageManager {

	/**
	 * Persists the given <code>SessionID</code> as active one.
	 * 
	 * NOTE : This method will NOT invoke commit() or rollback() of the
	 * <code>ConfigurationHandler</code> and will NOT invoke
	 * closeAllConfigurations().
	 * 
	 * @param sessionID
	 *            <code>SessionID</code>
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @throws ConfigurationException
	 *             in case the active <code>SessionID</code> cannot be
	 *             persisted.
	 */
	public void persistActiveSessionID(SessionID sessionID,
			ConfigurationHandler cfgHandler) throws ConfigurationException;

	/**
	 * Loads the active <code>SessionID</code>.
	 * 
	 * NOTE : This method WILL invoke with commit() or rollback() of the
	 * <code>ConfigurationHandler</code> and WILL invoke
	 * closeAllConfigurations().
	 * 
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @return <code>SessionID</code>
	 * @throws SessionIDException
	 *             in case the active <code>SessionID</code> cannot be loaded.
	 */
	public SessionID loadActiveSessionID(ConfigurationHandler cfgHandler)
			throws SessionIDException, SessionIDNotFoundException;

	/**
	 * @param cfgHandler
	 * @return array with all available offline deploy session IDs
	 * @throws NameNotFoundException
	 * @throws ConfigurationLockedException
	 * @throws ConfigurationException
	 */
	public String[] getDeployTransactionIDs(ConfigurationHandler cfgHandler)
			throws NameNotFoundException, ConfigurationLockedException,
			ConfigurationException;

	/**
	 * @param cfgHandler
	 * @return array with all availabel offline undeploy sessionIDs
	 * @throws NameNotFoundException
	 * @throws ConfigurationLockedException
	 * @throws ConfigurationException
	 */
	public String[] getUndeployTransactionIDs(ConfigurationHandler cfgHandler)
			throws NameNotFoundException, ConfigurationLockedException,
			ConfigurationException;

}
