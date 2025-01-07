package com.sap.engine.services.dc.lcm;

import java.rmi.RemoteException;

import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-3-27
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public abstract class LifeCycleManagerFactory {

	private static LifeCycleManagerFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.lcm.impl.LifeCycleManagerFactoryImpl";

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
			final String errMsg = "ASJ.dpl_dc.003326 An error occurred while creating an instance of "
					+ "class LifeCycleManagerFactory. "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract RemoteLCM createRemoteLifeCycleManager()
			throws RemoteException;

	public abstract LifeCycleManager createLifeCycleManager();

	public abstract LCMResult createLCMResult(LCMResultStatus lcmResultStatus,
			String description);

}
