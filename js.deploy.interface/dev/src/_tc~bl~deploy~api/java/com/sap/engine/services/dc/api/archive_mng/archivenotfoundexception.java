package com.sap.engine.services.dc.api.archive_mng;

import com.sap.engine.services.dc.api.APIException;
import com.sap.tc.logging.Location;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>The exception is risen if the searched component is not deployed on the
 * J2EE engine.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2005-4-20</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public class ArchiveNotFoundException extends APIException {
	/**
	 * Constructs an ArchiveNotFoundException object.
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
	public ArchiveNotFoundException(Location location, String patternKey,
			String[] parameters) {
		super(location, patternKey, parameters);
	}

	/**
	 * Constructs an ArchiveNotFoundException object.
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
	public ArchiveNotFoundException(Location location, String patternKey,
			String[] parameters, Throwable cause) {
		super(location, patternKey, parameters, cause);
	}

}
