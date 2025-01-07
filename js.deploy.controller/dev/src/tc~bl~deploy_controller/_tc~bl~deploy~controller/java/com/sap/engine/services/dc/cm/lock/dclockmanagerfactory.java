package com.sap.engine.services.dc.cm.lock;

import java.rmi.RemoteException;

import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.structure.tree.CfgBuilder;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-6
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public abstract class DCLockManagerFactory {

	private static DCLockManagerFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.lock.impl.DCLockManagerFactoryImpl";

	protected DCLockManagerFactory() {
	}

	public static synchronized DCLockManagerFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static DCLockManagerFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (DCLockManagerFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003108 An error occurred while creating an instance of "
					+ "class LockFactory! " + Constants.EOL + e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract DCLockManager createDCLockManager();

	public abstract RemoteLockManager createRemoteLockManager()
			throws RemoteException;

	public abstract CfgBuilder getCfgBuilder();

}
