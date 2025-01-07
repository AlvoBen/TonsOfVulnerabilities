package com.sap.engine.services.dc.lcm.impl;

import java.rmi.RemoteException;

import com.sap.engine.services.dc.lcm.LCMResult;
import com.sap.engine.services.dc.lcm.LCMResultStatus;
import com.sap.engine.services.dc.lcm.LifeCycleManager;
import com.sap.engine.services.dc.lcm.LifeCycleManagerFactory;
import com.sap.engine.services.dc.lcm.RemoteLCM;

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
public final class LifeCycleManagerFactoryImpl extends LifeCycleManagerFactory {

	public LifeCycleManagerFactoryImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.lcm.LifeCycleManagerFactory#
	 * createRemoteLifeCycleManager()
	 */
	public RemoteLCM createRemoteLifeCycleManager() throws RemoteException {
		return new RemoteLCMImpl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.lcm.LifeCycleManagerFactory#createLifeCycleManager
	 * ()
	 */
	public LifeCycleManager createLifeCycleManager() {
		return new LifeCycleManagerImpl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.lcm.LifeCycleManagerFactory#createLCMResult
	 * (com.sap.engine.services.dc.lcm.LCMResultStatus, java.lang.String)
	 */
	public LCMResult createLCMResult(LCMResultStatus lcmResultStatus,
			String description) {
		return new LCMResultImpl(lcmResultStatus, description);
	}

}
