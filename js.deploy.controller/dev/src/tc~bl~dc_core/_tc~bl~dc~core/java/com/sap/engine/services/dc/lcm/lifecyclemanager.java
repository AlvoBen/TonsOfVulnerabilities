package com.sap.engine.services.dc.lcm;

import com.sap.engine.services.dc.event.EventMode;
import com.sap.engine.services.dc.event.LCEventListener;
import com.sap.engine.services.dc.repo.SdaId;

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
public interface LifeCycleManager {

	public LCMResult start(String componentName, String componentVendor)
			throws LCMCompNotFoundException, LCMException;

	public LCMResult stop(String componentName, String componentVendor)
			throws LCMCompNotFoundException, LCMException;

	public LCMStatus getLCMStatus(String componentName, String componentVendor)
			throws LCMCompNotFoundException, LCMException;

	/**
	 * This method does not throws any exception (Except runtime eventually).
	 * The idea is to return as many as possible statuses. The returned arrays
	 * is related to the given argument array. In other words the statuses[5]
	 * represents the status of the 5th sdaId from the incoming array.
	 * 
	 * @param sdaIds
	 * @return array with statuses for each sda from the argument array
	 */
	public LCMStatus[] getLCMStatuses(SdaId[] sdaIds);

	/**
	 * Adds the specified <code>LCEventListener</code> to the list with all the
	 * other listeners.
	 * 
	 * @param listener
	 *            <code>LCEventListener</code> which will be triggered on
	 *            specific life cycle events like start and stop component.
	 * @param eventMode
	 *            specifies whether the events will be synchronous or
	 *            asynchronous.
	 * @throws LCMException
	 */
	public void addLCEventListener(LCEventListener listener, EventMode eventMode)
			throws LCMException;

	/**
	 * Removes the specified <code>LCEventListener</code>.
	 * 
	 * @param listener
	 *            <code>LCEventListener</code> which has to be removed from the
	 *            list with registered listeners.
	 * @throws LCMException
	 */
	public void removeLCEventListener(LCEventListener listener)
			throws LCMException;

}
