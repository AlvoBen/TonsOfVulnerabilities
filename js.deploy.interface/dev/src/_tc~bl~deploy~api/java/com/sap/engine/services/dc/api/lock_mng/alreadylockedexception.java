package com.sap.engine.services.dc.api.lock_mng;

import com.sap.tc.logging.Location;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Signals that the engine is already locked for the requested action. The
 * exception will be thrown by
 * {@link com.sap.engine.services.dc.api.deploy.DeployProcessor#deploy(DeployItem[])},
 * {@link com.sap.engine.services.dc.api.undeploy.UndeployProcessor#undeploy(UndeployItem[])}
 * and
 * {@link com.sap.engine.services.dc.api.lock_mng.LockManager#lock(LockAction) }
 * commands.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2005-4-26</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public class AlreadyLockedException extends LockException {

	/**
	 * Constructs an AlreadyLockedException object.
	 * 
	 * @param location
	 *            a Location object
	 * @param patternKey
	 *            a String key from
	 *            com.sap.engine.services.dc.api.util.exception
	 *            .resources.exceptions.properties
	 * @param parameters
	 *            a String array of parameters witch are substituted in the
	 *            exception message
	 */
	public AlreadyLockedException(Location location, String patternKey,
			String[] parameters) {
		super(location, patternKey, parameters);
	}

	/**
	 * Constructs an AlreadyLockedException object.
	 * 
	 * @param location
	 *            a Location object
	 * @param patternKey
	 *            a String key from
	 *            com.sap.engine.services.dc.api.util.exception
	 *            .resources.exceptions.properties
	 * @param parameters
	 *            a String array of parameters witch are substituted in the
	 *            exception message
	 * @param cause
	 *            a Throwable object cause for the exception
	 */
	public AlreadyLockedException(Location location, String patternKey,
			String[] parameters, Throwable cause) {
		super(location, patternKey, parameters, cause);
	}

}