package com.sap.engine.services.dc.cm.lock.impl;

import java.rmi.RemoteException;

import com.sap.engine.services.dc.cm.lock.DCLockManager;
import com.sap.engine.services.dc.cm.lock.DCLockManagerFactory;
import com.sap.engine.services.dc.cm.lock.RemoteLockManager;
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
public final class DCLockManagerFactoryImpl extends DCLockManagerFactory {

	private DCLockManager dcLockManager = new DCLockManagerImpl();

	public DCLockManagerFactoryImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.lock.DCLockManagerFactory#createDCLockManager
	 * ()
	 */
	public DCLockManager createDCLockManager() {
		return this.dcLockManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.lock.DCLockManagerFactory#
	 * createRemoteLockManager()
	 */
	public RemoteLockManager createRemoteLockManager() throws RemoteException {
		return new RemoteLockManagerImpl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.lock.DCLockManagerFactory#getCfgBuilder()
	 */
	public CfgBuilder getCfgBuilder() {
		return DBLockCfgBuilderImpl.getInstance();
	}

}
