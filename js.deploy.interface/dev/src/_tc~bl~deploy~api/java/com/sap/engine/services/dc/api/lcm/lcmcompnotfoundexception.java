package com.sap.engine.services.dc.api.lcm;

import com.sap.tc.logging.Location;

/**
 *<DL>
 *<DT><B>Title:</B></DT>
 * J2EE Deployment Team
 * <DT><B>Description:</B></DT>
 * <DD>Exception indicates that the requested component is not deployed on the
 * J2EE engine.</DD>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company:</B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2005-4-24</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public class LCMCompNotFoundException extends LCMException {

	/**
	 * Constructs a LCMCompNotFoundException object.
	 * 
	 * @param location
	 *            a Location object
	 * @param patternKey
	 *            a String key from
	 *            com.sap.engine.services.dc.api.util.exception
	 *            .resources.exceptions.properties
	 * @param parameters
	 *            an Object array of parameters witch are substituted in the
	 *            exception message
	 */
	public LCMCompNotFoundException(Location location, String patternKey,
			Object[] parameters) {
		super(location, patternKey, parameters);
	}

	/**
	 * Constructs a LCMCompNotFoundException object.
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
	public LCMCompNotFoundException(Location location, String patternKey,
			String[] parameters, Throwable cause) {
		super(location, patternKey, parameters, cause);
	}

}
