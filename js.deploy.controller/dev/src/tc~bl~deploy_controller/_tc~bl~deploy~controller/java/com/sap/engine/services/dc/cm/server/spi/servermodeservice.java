package com.sap.engine.services.dc.cm.server.spi;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.lib.config.api.exceptions.ClusterConfigurationException;
import com.sap.engine.lib.config.api.exceptions.NameNotFoundException;
import com.sap.engine.services.dc.cm.server.ServerService;
import com.sap.engine.services.dc.util.exception.DCBaseException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-7
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface ServerModeService extends ServerService {

	public void setServerMode(ServerMode serverMode)
			throws ServerModeServiceException;

	public ServerMode getServerMode() throws ServerModeServiceException;

	/**
	 * Stores the current engine mode
	 * 
	 * @throws NameNotFoundException
	 * @throws ConfigurationLockedException
	 * @throws ConfigurationException
	 * @throws ClusterConfigurationException
	 */
	public void storeServerMode() throws NameNotFoundException,
			ConfigurationLockedException, ConfigurationException,
			ClusterConfigurationException;

	/**
	 * restores the previous engine mode.
	 * 
	 * @throws ConfigurationException
	 * @throws NameNotFoundException
	 * @throws ClusterConfigurationException
	 */
	public void restoreServerMode() throws ConfigurationException,
			NameNotFoundException, ClusterConfigurationException;

	public class ServerModeServiceException extends DCBaseException {

		public ServerModeServiceException(String patternKey) {
			super(patternKey);
		}

		public ServerModeServiceException(String patternKey, Throwable cause) {
			super(patternKey, cause);
		}

	}

}
