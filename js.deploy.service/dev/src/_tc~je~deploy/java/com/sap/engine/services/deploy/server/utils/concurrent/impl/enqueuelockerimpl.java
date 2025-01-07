package com.sap.engine.services.deploy.server.utils.concurrent.impl;

import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.services.deploy.server.utils.LockUtils;
import com.sap.engine.services.deploy.server.utils.concurrent.EnqueueLocker;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

public class EnqueueLockerImpl implements EnqueueLocker {
	private static final int ENQUEUE_LOCK_TIMEOUT = 2000;
	private static final Location location = 
		Location.getLocation(EnqueueLockerImpl.class);
	private static final char UNLOCKED = 0;
	
	public boolean lock(EnqueueLock lock) {
		if(lock.getType() == UNLOCKED) {
			return true;
		}
		try {
			LockUtils.lockAndWait(
				lock.getKey(), lock.getType(), ENQUEUE_LOCK_TIMEOUT); 
			return true;
		} catch (LockException ex) {
			SimpleLogger.traceThrowable(Severity.WARNING, location, 
				ex.getLocalizedMessage(), ex);
		} catch (TechnicalLockException ex) {
			SimpleLogger.traceThrowable(Severity.WARNING, location, 
				ex.getLocalizedMessage(), ex);
		}
		return false;
	}

	public void unlock(EnqueueLock lock) {
		if(lock.getType() == UNLOCKED) {
			return;
		}
		try {
			LockUtils.unlock(lock.getKey(), lock.getType());
		} catch (TechnicalLockException ex) {
			SimpleLogger.traceThrowable(Severity.WARNING, location, 
				ex.getLocalizedMessage(), ex);
		}
	}
}