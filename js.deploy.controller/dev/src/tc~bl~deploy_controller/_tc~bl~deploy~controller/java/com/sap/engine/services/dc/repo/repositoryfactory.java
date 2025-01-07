package com.sap.engine.services.dc.repo;

import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-13
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public abstract class RepositoryFactory {

	private static RepositoryFactory INSTANCE;
	// TODO: Could be read from a global deploy service configurator
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.repo.impl.RepositoryFactoryImpl";

	protected RepositoryFactory() {
	}

	public static synchronized RepositoryFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static RepositoryFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (RepositoryFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003368 An error occurred while creating an instance of "
					+ "class RepositoryFactory! "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract Repository createRepository();
}
