/*
 * Created on Oct 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.services.dc.api.impl;

import com.sap.engine.services.dc.api.Client;
import com.sap.engine.services.dc.api.ComponentManager;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.lcm.LifeCycleManager;
import com.sap.engine.services.dc.api.lcm.LifeCycleManagerFactory;
import com.sap.engine.services.dc.api.lock_mng.LockManager;
import com.sap.engine.services.dc.api.lock_mng.LockManagerFactory;
import com.sap.engine.services.dc.api.session.Session;
import com.sap.engine.services.dc.api.util.DALog;

/**
 * @author Georgi Danov
 * @author Boris Savov
 */
final class ClientImpl implements Client {

	private final Session session;

	ClientImpl(Session session) {
		this.session = session;
	}

	public ComponentManager getComponentManager() {
		return new ComponentManagerImpl(this.session);
	}

	public void close() throws ConnectionException {
		this.session.close();
	}

	public DALog getLog() {
		return this.session.getLog();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.Client#getLifeCycleManager()
	 */
	public LifeCycleManager getLifeCycleManager() {
		return LifeCycleManagerFactory.getInstance().createLifeCycleManager(
				this.session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.Client#getLockManager()
	 */
	public LockManager getLockManager() {
		return LockManagerFactory.getInstance().createLockManager(this.session);
	}
}