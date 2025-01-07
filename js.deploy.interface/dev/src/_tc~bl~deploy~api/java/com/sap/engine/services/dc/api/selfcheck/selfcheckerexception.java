/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.selfcheck;

import com.sap.engine.services.dc.api.APIException;
import com.sap.tc.logging.Location;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>SelfChecker related exception. The exception is thrown by
 * {@link com.sap.engine.services.dc.api.selfcheck.SelfChecker#doCheck()} or by
 * {@link com.sap.engine.services.dc.api.ComponentManager#getSelfChecker()}
 * during getting a <code>SelfChecker</code> instance from the server.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>Apr 4, 2005</DD>
 * </DL>
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public class SelfCheckerException extends APIException {

	/**
	 * @param location
	 * @param patternKey
	 */
	public SelfCheckerException(Location location, String patternKey,
			Object[] parameters) {
		super(location, patternKey, parameters);
	}

	/**
	 * @param location
	 * @param patternKey
	 * @param parameters
	 * @param cause
	 */
	public SelfCheckerException(Location location, String patternKey,
			String[] parameters, Throwable cause) {
		super(location, patternKey, parameters, cause);
	}
}
