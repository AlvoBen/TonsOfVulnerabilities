package com.sap.engine.services.dc.cm.lock;

import java.rmi.Remote;

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
public interface RemoteLockManager extends Remote {

	public void lock(LockAction lockAction) throws DCAlreadyLockedException,
			DCLockException;

	public void unlock(LockAction lockAction) throws DCLockNotFoundException,
			DCLockException;

}
