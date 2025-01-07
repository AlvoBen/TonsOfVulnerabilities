/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.patch;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.services.security.server.SecurityConfigurationPath;

/**
 * Apply the new CreateAssertionTicketLoginModule to the configured login modules 
 * for the authentication (active) user store.
 * 
 * @author Ekaterina Zheleva
 */
public class Change4 implements Change {
	public static final String ACTIVE = "active_userstore";
	static final String GENERIC_CREDENTIAL_NAME = "CreateAssertionTicketLoginModule";

	public void run() throws Exception {
		ConfigurationHandler configHandler = null;
		configHandler = ChangeDaemon.configFactory.getConfigurationHandler();
		modifyActiveUserstoreConfiguration(configHandler);
	}

	private void modifyActiveUserstoreConfiguration(ConfigurationHandler configHandler) throws Exception {
		try {
			Configuration configWrite = configHandler.openConfiguration(SecurityConfigurationPath.USERSTORES_PATH, ConfigurationHandler.WRITE_ACCESS);
			if (configWrite != null) {
				String activeUserStoreName = null;
				if (configWrite.existsConfigEntry(ACTIVE)) {
					activeUserStoreName = (String) configWrite.getConfigEntry(ACTIVE);
				}
				if (activeUserStoreName == null || activeUserStoreName.trim().length() == 0) {
					return;
				}

				Configuration activeUserStoreConfig = configWrite.getSubConfiguration(activeUserStoreName);
				if (activeUserStoreConfig.existsSubConfiguration("login-module")) {
					Configuration loginModules = activeUserStoreConfig.getSubConfiguration("login-module");
					if (!loginModules.existsSubConfiguration(GENERIC_CREDENTIAL_NAME)) {
						Configuration newLoginModuleConfig = loginModules.createSubConfiguration(GENERIC_CREDENTIAL_NAME);
						String[] subConfigs = new String[] {"not-suitable-mechanisms", "options", "suitable-mechanisms"};
						newLoginModuleConfig.createSubConfigurations(subConfigs);
						newLoginModuleConfig.addConfigEntry("class-name", "com.sap.security.core.server.jaas.CreateAssertionTicketLoginModule");
						newLoginModuleConfig.addConfigEntry("description", "Login module to create SAP Authentication Assertion Tickets after successful logon");
					}
				}
			}

			configHandler.commit();
		} catch (Exception e) {
			try {
				configHandler.rollback();
			} catch (Exception re) {
				throw re;
			}
			throw e;
		} finally {
			try {
				configHandler.closeAllConfigurations();
			} catch (Exception e) {
				throw e;
			}
		}
	}
}
