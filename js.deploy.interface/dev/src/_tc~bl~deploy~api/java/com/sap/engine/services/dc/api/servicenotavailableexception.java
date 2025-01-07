/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Mar 6, 2006
 */
package com.sap.engine.services.dc.api;

import com.sap.tc.logging.Location;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>This exception is thrown when Deploy Controller service is not available</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2005, SAP-AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>Mar 6, 2006</DD>
 * </DL>
 * 
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.1
 * 
 */

public class ServiceNotAvailableException extends APIException {

	private static final long serialVersionUID = -5969136451810120733L;

	/**
	 * Constructs a ServiceNotAvailableException object.
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
	public ServiceNotAvailableException(Location location, String patternKey,
			Object[] parameters) {
		super(location, patternKey, parameters);
	}

	/**
	 * Constructs a ServiceNotAvailableException object.
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
	public ServiceNotAvailableException(Location location, String patternKey,
			Object[] parameters, Throwable cause) {
		super(location, patternKey, parameters, cause);
	}

}
