package com.sap.engine.services.dc.api.lcm;

import com.sap.engine.services.dc.api.APIException;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.event.EventMode;
import com.sap.engine.services.dc.api.event.LCEventListener;
import com.sap.engine.services.dc.api.model.SdaId;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>An entry point for all Life Cycle Management related operation.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2005-4-24</DD>
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * @see com.sap.engine.services.dc.api.Client#getLifeCycleManager()
 */
public interface LifeCycleManager {

	/**
	 * Starts components
	 * 
	 * @param componentName
	 * @param componentVendor
	 * @return <code>LCMResult</code> from the operation.
	 * @throws LCMCompNotFoundException
	 * @throws LCMException
	 * @throws ConnectionException
	 * @throws APIException
	 * @see LCMResult
	 */
	public LCMResult start(String componentName, String componentVendor)
			throws LCMCompNotFoundException, LCMException, ConnectionException,
			APIException;

	/**
	 * Stops component
	 * 
	 * @param componentName
	 * @param componentVendor
	 * @return <code>LCMResult</code> from the operation.
	 * @throws LCMCompNotFoundException
	 * @throws LCMException
	 * @throws ConnectionException
	 * @throws APIException
	 * @see LCMResult
	 */
	public LCMResult stop(String componentName, String componentVendor)
			throws LCMCompNotFoundException, LCMException, ConnectionException,
			APIException;

	/**
	 * gets current state of the component
	 * 
	 * @param componentName
	 * @param componentVendor
	 * @return <code>LCMResult</code> from the operation.
	 * @throws LCMCompNotFoundException
	 * @throws LCMException
	 * @throws ConnectionException
	 * @throws APIException
	 * @see LCMResult
	 */
	public LCMStatus getLCMStatus(String componentName, String componentVendor)
			throws LCMCompNotFoundException, LCMException, ConnectionException,
			APIException;

	/**
	 * gets the current state of all components given as incoming array. The
	 * first status represents the state of the dirst component id in the array.
	 * 
	 * @param sdaIds
	 *            array with the components which statuses we are interested in
	 * @return an array with the statuses mapped to the incoming array
	 * @throws ConnectionException
	 * @throws APIException
	 * @see LCMResult
	 */
	public LCMStatus[] getLCMStatuses(SdaId[] sdaIds)
			throws ConnectionException, APIException;

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
			throws ConnectionException, LCMException, APIException;

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