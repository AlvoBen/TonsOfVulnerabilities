package com.sap.engine.services.dc.cm.lock.impl;

import java.rmi.RemoteException;

import com.sap.engine.services.dc.cm.lock.DCAlreadyLockedException;
import com.sap.engine.services.dc.cm.lock.DCLockException;
import com.sap.engine.services.dc.cm.lock.DCLockManager;
import com.sap.engine.services.dc.cm.lock.DCLockManagerFactory;
import com.sap.engine.services.dc.cm.lock.DCLockNotFoundException;
import com.sap.engine.services.dc.cm.lock.LockAction;
import com.sap.engine.services.dc.cm.lock.RemoteLockManager;

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
public final class RemoteLockManagerImpl implements RemoteLockManager {

	private final DCLockManager dcLockManager;

	RemoteLockManagerImpl() throws RemoteException {
		this.dcLockManager = DCLockManagerFactory.getInstance()
				.createDCLockManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.lock.RemoteLockManager#lock(com.sap.engine
	 * .services.dc.cm.lock.LockAction)
	 */
	public void lock(LockAction lockAction) throws DCAlreadyLockedException,
			DCLockException {
		this.dcLockManager.lockEnqueue(lockAction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.lock.RemoteLockManager#unlock(com.sap.engine
	 * .services.dc.cm.lock.LockAction)
	 */
	public void unlock(LockAction lockAction) throws DCLockNotFoundException,
			DCLockException {
		this.dcLockManager.unlockEnqueue(lockAction);
	}

}
