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
package com.sap.engine.services.dc.repo.version;

import java.util.Set;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.dc.repo.Repository;
import com.sap.engine.services.dc.repo.RepositoryException;
import com.sap.engine.services.dc.repo.RepositoryFactory;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.dc.version.ServerVersion;
import com.sap.engine.services.dc.version.VersionProvider;
import com.sap.engine.services.dc.version.VersionProviderException;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class VersionProviderImpl extends VersionProvider {

	private final Repository repo;

	public VersionProviderImpl() {
		this.repo = RepositoryFactory.getInstance().createRepository();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.util.version.VersionProvider#getServerVersion
	 * (java.util.Set)
	 */
	public ServerVersion getServerVersion(Set sdus)
			throws VersionProviderException {
		return new ServerVersionImpl(sdus);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.util.version.VersionProvider#getServerVersion
	 * (com.sap.engine.frame.core.configuration.ConfigurationHandler)
	 */
	public ServerVersion getServerVersion(ConfigurationHandler cfgHandler)
			throws VersionProviderException {
		final Set sdus;
		try {
			sdus = this.repo.loadSdus(cfgHandler, null);
		} catch (RepositoryException repoEx) {
			throw new VersionProviderException(
					DCExceptionConstants.SER_VER_CANNOT_READ_ALL_SDUS, repoEx);
		}
		return getServerVersion(sdus);
	}

}
