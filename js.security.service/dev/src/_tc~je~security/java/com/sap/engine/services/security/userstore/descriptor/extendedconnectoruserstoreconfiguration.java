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
package com.sap.engine.services.security.userstore.descriptor;

import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;
import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.Properties;
import java.util.Hashtable;
import java.util.Enumeration;
import java.lang.reflect.Method;

public class ExtendedConnectorUserstoreConfiguration implements UserStoreConfiguration {

	private UserStoreConfiguration baseUserStoreConfig = null;
	private final static Location LOCATION = Location.getLocation(ExtendedConnectorUserstoreConfiguration.class);

	public ExtendedConnectorUserstoreConfiguration(UserStoreConfiguration baseUserStoreConfig) {
		this.baseUserStoreConfig = baseUserStoreConfig;
	}

	public void clearStartupConfiguration() {
		try {
			Method method = baseUserStoreConfig.getClass().getMethod("clearStartupConfiguration", null);
			if (method != null) {
				method.invoke(baseUserStoreConfig, null);
			}
		} catch (Throwable e) {
		  LOCATION.traceThrowableT(Severity.INFO, " Unsuccessfull clear startup configuration.", e);
		}
	}

	/**
	 *  Returns the description of the user store.
	 *
	 * @return  printable text.
	 */
	public String getDescription() {
		return baseUserStoreConfig.getDescription();
	}

	public String getAnonymousUser() {
		return UserStoreConfiguration.NONE_ANONYMOUS_USER;
	}

	/**
	 *  Returns the display name of the user store.
	 *
	 * @return  display name.
	 */
	public String getName() {
		return baseUserStoreConfig.getName();
	}

	/**
	 *  Returns the configured login modules for this user store.
	 *
	 * @return  an array of login module configurations.
	 */
	public LoginModuleConfiguration[] getLoginModules() {
		LoginModuleConfiguration allLoginModules[] = null;
		LoginModuleConfiguration basedLoginModulesArray[] = baseUserStoreConfig.getLoginModules();
		LoginModuleConfiguration extLoginModulesArray[] = new LoginModuleConfiguration[5];
		extLoginModulesArray[0] = new ConnectorLoginModuleConfiguration(ConnectorLoginModuleConfiguration.CALLER_IMPERSONATION_NAME);
		extLoginModulesArray[1] = new ConnectorLoginModuleConfiguration(ConnectorLoginModuleConfiguration.CONFIGURED_IDENTITY_NAME);
		extLoginModulesArray[2] = new ConnectorLoginModuleConfiguration(ConnectorLoginModuleConfiguration.CREDENTIALS_MAPPING_NAME);
		extLoginModulesArray[3] = new ConnectorLoginModuleConfiguration(ConnectorLoginModuleConfiguration.PRINCIPAL_MAPPING_NAME);
		extLoginModulesArray[4] = new ConnectorLoginModuleConfiguration(ConnectorLoginModuleConfiguration.GENERIC_CREDENTIAL_NAME);
		Hashtable basedLoginModules = new Hashtable();
		if (basedLoginModulesArray != null) {
			for (int i = 0; i < basedLoginModulesArray.length; i++) {
				if (basedLoginModulesArray[i] != null) {
					String basedLoginModuleName = basedLoginModulesArray[i].getName();
					if (   !basedLoginModuleName.equals(ConnectorLoginModuleConfiguration.CALLER_IMPERSONATION_NAME)
							&& !basedLoginModuleName.equals(ConnectorLoginModuleConfiguration.CREDENTIALS_MAPPING_NAME)
							&& !basedLoginModuleName.equals(ConnectorLoginModuleConfiguration.CONFIGURED_IDENTITY_NAME)
							&& !basedLoginModuleName.equals(ConnectorLoginModuleConfiguration.PRINCIPAL_MAPPING_NAME)) {
						if (basedLoginModuleName.equals(ConnectorLoginModuleConfiguration.GENERIC_CREDENTIAL_NAME)) {
							extLoginModulesArray[4] = basedLoginModulesArray[i];
						} else {
							basedLoginModules.put(basedLoginModuleName, basedLoginModulesArray[i]);
						}
					}
				}
			}
			basedLoginModulesArray = new LoginModuleConfiguration[basedLoginModules.size()];
			int i = 0;
			Enumeration loginModuleNames = basedLoginModules.keys();
			while (loginModuleNames.hasMoreElements()) {
				String loginModuleName = (String)loginModuleNames.nextElement();
				basedLoginModulesArray[i++] = (LoginModuleConfiguration)basedLoginModules.get(loginModuleName);
			}
			allLoginModules = new LoginModuleConfiguration[basedLoginModulesArray.length + extLoginModulesArray.length];
			System.arraycopy(basedLoginModulesArray, 0, allLoginModules, 0, basedLoginModulesArray.length);
			System.arraycopy(extLoginModulesArray, 0, allLoginModules, basedLoginModulesArray.length, extLoginModulesArray.length);
		} else {
			allLoginModules = extLoginModulesArray;
		}
		return allLoginModules;
	}

	/**
	 *  Returns the class name of the user context spi for the user store.
	 *
	 * @return  class name.
	 */
	public String getUserSpiClassName() {
		return baseUserStoreConfig.getUserSpiClassName();
	}

	/**
	 *  Returns the class name of the group context spi for the user store.
	 *
	 * @return  class name.
	 */
	public String getGroupSpiClassName() {
		return baseUserStoreConfig.getGroupSpiClassName();
	}

	/**
	 *  Returns the properties of the user store.
	 *
	 * @return  the properties set for this userstore.
	 */
	public Properties getUserStoreProperties() {
		return baseUserStoreConfig.getUserStoreProperties();
	}

	/////////////////////////////////////////////////////////////////////////////////
	///////////////////GUI///////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	/**
	 *  Returns the class name of the configuration editor for the user store.
	 *
	 * @return  class name.
	 */
	public String getConfigurationEditorClassName() {
		return baseUserStoreConfig.getConfigurationEditorClassName();
	}

}

