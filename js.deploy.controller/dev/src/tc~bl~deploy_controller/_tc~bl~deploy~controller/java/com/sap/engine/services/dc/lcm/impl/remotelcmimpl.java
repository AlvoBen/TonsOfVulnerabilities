package com.sap.engine.services.dc.lcm.impl;

import java.rmi.RemoteException;

import com.sap.engine.services.dc.event.EventMode;
import com.sap.engine.services.dc.event.LCEventListener;
import com.sap.engine.services.dc.lcm.LCMCompNotFoundException;
import com.sap.engine.services.dc.lcm.LCMException;
import com.sap.engine.services.dc.lcm.LCMResult;
import com.sap.engine.services.dc.lcm.LCMStatus;
import com.sap.engine.services.dc.lcm.LifeCycleManager;
import com.sap.engine.services.dc.lcm.LifeCycleManagerFactory;
import com.sap.engine.services.dc.lcm.RemoteLCM;
import com.sap.engine.services.dc.repo.SdaId;

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
public final class RemoteLCMImpl implements RemoteLCM {

	private final LifeCycleManager lifeCycleManager;

	RemoteLCMImpl() throws RemoteException {
		super();

		this.lifeCycleManager = LifeCycleManagerFactory.getInstance()
				.createLifeCycleManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.lcm.LifeCycleManager#start(java.lang.String,
	 * java.lang.String)
	 */
	public LCMResult start(String componentName, String componentVendor)
			throws LCMCompNotFoundException, LCMException {
		return this.lifeCycleManager.start(componentName, componentVendor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.lcm.LifeCycleManager#stop(java.lang.String,
	 * java.lang.String)
	 */
	public LCMResult stop(String componentName, String componentVendor)
			throws LCMCompNotFoundException, LCMException {
		return this.lifeCycleManager.stop(componentName, componentVendor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.lcm.LifeCycleManager#getLCMStatus(java.lang
	 * .String, java.lang.String)
	 */
	public LCMStatus getLCMStatus(String componentName, String componentVendor)
			throws LCMCompNotFoundException, LCMException {
		return this.lifeCycleManager.getLCMStatus(componentName,
				componentVendor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.lcm.LifeCycleManager#getLCMStatuses(com.sap
	 * .engine.services.dc.repo.SdaId[])
	 */
	public LCMStatus[] getLCMStatuses(SdaId[] sdaIds) {
		return this.lifeCycleManager.getLCMStatuses(sdaIds);
	}

	public void addLCEventListener(LCEventListener listener, EventMode eventMode)
			throws LCMException {
		this.lifeCycleManager.addLCEventListener(listener, eventMode);
	}

	public void removeLCEventListener(LCEventListener listener)
			throws LCMException {
		this.lifeCycleManager.removeLCEventListener(listener);
	}

}
