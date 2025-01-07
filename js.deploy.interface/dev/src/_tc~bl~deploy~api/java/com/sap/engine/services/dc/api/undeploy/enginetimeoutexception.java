/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Oct 3, 2005
 */
package com.sap.engine.services.dc.api.undeploy;

import com.sap.tc.logging.Location;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>The exception is thrown if the preliminary set timeout exceeded during
 * the offline phase.</DD>
 * <DT><B>Copyright</B></DT>
 * <DD>Copyright (c) 2005, SAP-AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>Oct 3, 2005</DD>
 * </DL>
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * @see com.sap.engine.services.dc.api.undeploy.UndeployProcessor#undeploy(UndeployItem[])
 */
public class EngineTimeoutException extends UndeployException {

	/**
	 * Constructs an EngineTimeoutException object.
	 * 
	 * @param undeployItems
	 *            in the undeployment batch
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
	public EngineTimeoutException(UndeployItem[] undeployItems,
			Location location, String patternKey, String[] parameters) {
		super(null, undeployItems, location, patternKey, parameters);
	}

}
