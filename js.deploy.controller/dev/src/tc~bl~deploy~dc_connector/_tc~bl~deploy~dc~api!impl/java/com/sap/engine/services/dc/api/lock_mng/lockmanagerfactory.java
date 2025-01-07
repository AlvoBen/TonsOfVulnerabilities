package com.sap.engine.services.dc.api.lock_mng;

import com.sap.engine.services.dc.api.session.Session;
import com.sap.engine.services.dc.api.util.DAConstants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-26
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public abstract class LockManagerFactory {

	private static LockManagerFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.api.lock_mng.impl.LockManagerFactoryImpl";

	protected LockManagerFactory() {
	}

	/**
	 * @return the object reference for the factory. The class is inmplemented
	 *         as a Singleton.
	 */
	public static synchronized LockManagerFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static LockManagerFactory createFactory() {
		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (LockManagerFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "[ERROR CODE DPL.DCAPI.1113] An error occurred while creating an instance of "
					+ "class LockManagerFactory. "
					+ DAConstants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract LockManager createLockManager(Session session);

}
