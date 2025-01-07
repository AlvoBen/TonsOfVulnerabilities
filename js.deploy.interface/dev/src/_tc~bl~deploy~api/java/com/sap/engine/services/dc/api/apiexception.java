/*
 * Created on Oct 19, 2004
 *
 */
package com.sap.engine.services.dc.api;

import com.sap.engine.services.dc.api.util.exception.APIResourceAccessor;
import com.sap.exception.BaseException;
import com.sap.tc.logging.Location;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Thrown when common problems have been arised. All the other exceptions
 * are its successors. A possible problem might be that there is a network
 * problem.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-11-29</DD>
 * </DL>
 * 
 * @author Georgi Danov
 * @author Boris Savov
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.10
 */
public class APIException extends BaseException {

	/**
	 * Constructs an APIException object.
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
	public APIException(Location location, String patternKey,
			Object[] parameters) {
		this(location, patternKey, parameters, null);
	}

	/**
	 * Constructs an APIException object.
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
	public APIException(Location location, String patternKey,
			Object[] parameters, Throwable cause) {
		super(
				// location,
				null, APIResourceAccessor.getInstance(), patternKey,
				parameters, cause);
	}

}