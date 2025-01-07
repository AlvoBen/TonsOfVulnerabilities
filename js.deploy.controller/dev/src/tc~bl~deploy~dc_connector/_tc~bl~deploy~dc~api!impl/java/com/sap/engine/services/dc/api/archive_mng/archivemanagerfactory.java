package com.sap.engine.services.dc.api.archive_mng;

import com.sap.engine.services.dc.api.session.Session;
import com.sap.engine.services.dc.api.util.DAConstants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-20
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public abstract class ArchiveManagerFactory {
	private static ArchiveManagerFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.api.archive_mng.impl.ArchiveManagerFactoryImpl";

	protected ArchiveManagerFactory() {
	}

	/**
	 * @return the object reference for the factory. The class is inmplemented
	 *         as a Singleton.
	 */
	public static synchronized ArchiveManagerFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static ArchiveManagerFactory createFactory() {
		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (ArchiveManagerFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "[ERROR CODE DPL.DCAPI.1009] An error occurred while creating an instance of "
					+ "class ArchiveManagerFactory. "
					+ DAConstants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract ArchiveManager createArchiveManager(Session session);
}
