package com.sap.engine.services.dc.api.lcm;

import com.sap.engine.services.dc.api.session.Session;
import com.sap.engine.services.dc.api.util.DAConstants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-24
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public abstract class LifeCycleManagerFactory {

	private static LifeCycleManagerFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.api.lcm.impl.LifeCycleManagerFactoryImpl";

	protected LifeCycleManagerFactory() {
	}

	public static synchronized LifeCycleManagerFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static LifeCycleManagerFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (LifeCycleManagerFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "[ERROR CODE DPL.DCAPI.1107] An error occurred while creating an instance of "
					+ "class LifeCycleManagerFactory. "
					+ DAConstants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract LifeCycleManager createLifeCycleManager(Session session);

	public abstract LCMResult createLCMResult(LCMResultStatus lcmResultStatus,
			String description);

}
