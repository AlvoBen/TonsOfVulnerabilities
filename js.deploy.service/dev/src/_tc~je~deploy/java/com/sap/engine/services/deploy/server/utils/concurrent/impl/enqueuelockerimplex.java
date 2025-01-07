package com.sap.engine.services.deploy.server.utils.concurrent.impl;

import com.sap.engine.services.deploy.server.utils.concurrent.EnqueueLocker;

public class EnqueueLockerImplEx implements EnqueueLocker {
	public boolean lock(EnqueueLock lock) {
		return true;
	}

	public void unlock(EnqueueLock lock) {
		// Empty method.
	}
}