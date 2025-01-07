/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.version;

import java.util.Set;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.dc.util.Constants;

/**
 * Provides specific version information.
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public abstract class VersionProvider {

	private static VersionProvider INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.repo.version.VersionProviderImpl";

	protected VersionProvider() {
	}

	/**
	 * @return the object reference for the factory. The class is implemented as
	 *         a Singleton.
	 */
	public static synchronized VersionProvider getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static VersionProvider createFactory() {
		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (VersionProvider) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003402 An error occurred while creating an instance of "
					+ "class LockManagerFactory. "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	/**
	 * Builds the version information from the given SDUs.
	 * 
	 * @param sdus
	 *            <code>Set</code>, which contains <code>Sdu</code>
	 * @return <code>ServerVersion</code>
	 * @throws VersionProviderException
	 *             in case the version information cannot be built.
	 */
	public abstract ServerVersion getServerVersion(Set sdus)
			throws VersionProviderException;

	/**
	 * Builds the version information from DB.
	 * 
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code> to DB.
	 * @return <code>ServerVersion</code>
	 * @throws VersionProviderException
	 *             in case the version information cannot be built.
	 */
	public abstract ServerVersion getServerVersion(
			ConfigurationHandler cfgHandler) throws VersionProviderException;

}
