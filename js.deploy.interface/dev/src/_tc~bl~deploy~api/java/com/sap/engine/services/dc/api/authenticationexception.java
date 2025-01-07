package com.sap.engine.services.dc.api;

import com.sap.engine.services.dc.api.util.exception.APIResourceAccessor;
import com.sap.exception.BaseException;
import com.sap.tc.logging.Location;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>This exception is thrown when there is a problem with getting server
 * initial context. On most of the cases this means wrong credential but the
 * reason coulb also missing classes or different version classes on the server
 * and client sides..</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-9-9</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public class AuthenticationException extends BaseException {

	/**
	 * Constructs an AuthenticationException object.
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
	 * @param cause
	 *            a Throwable object cause for the exception
	 */
	public AuthenticationException(Location location, String patternKey,
			Object[] parameters, Throwable cause) {
		super(
				// location,
				null, APIResourceAccessor.getInstance(), patternKey,
				parameters, cause);
	}

}