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
package com.sap.engine.services.dc.api.deploy;

import com.sap.engine.services.dc.api.APIException;
import com.sap.tc.logging.Location;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>The class <code>RollingException</code> is a form of
 * <code>APIException</code> that indicates Deploy exception. All deploy related
 * exceptions are subclasses of <code>DeployException</code>.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2006</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>13.12.2006</DD>
 * </DL>
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 * @deprecated The exception will only be used for proofing the concept in the
 *             prototyping phase. It will not be shipped to external customers
 *             and is not considered as public interface, without reviewing it.
 */
public class RollingException extends APIException {

	public RollingException(Location location, String patternKey,
			String[] parameters) {
		super(location, patternKey, parameters);
	}

	public RollingException(Location location, String patternKey,
			String[] parameters, Throwable cause) {
		super(location, patternKey, parameters, cause);
	}

}
