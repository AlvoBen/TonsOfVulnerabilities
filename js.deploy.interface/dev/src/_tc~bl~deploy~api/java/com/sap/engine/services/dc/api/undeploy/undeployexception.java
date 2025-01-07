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
package com.sap.engine.services.dc.api.undeploy;

import com.sap.engine.services.dc.api.APIException;
import com.sap.tc.logging.Location;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>The class <code>APIUndeployException</code> is a form of
 * <code>APIException</code> that indicates Undeploy exception. All undeploy
 * related exceptions are subclasses of <code>APIUndeployException</code>.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-11-30</DD>
 * </DL>
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public class UndeployException extends APIException {
	private final UndeployItem[] undeployItems;
	private final UndeployItem[] orderedUndeployItems;

	/**
	 * Constructs an UndeployException object.
	 * 
	 * @param orderedUndeployItems
	 *            an array of ordered UndeployItems in the batch
	 * @param undeployItems
	 *            an array of UndeployItems in the batch
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
	public UndeployException(UndeployItem[] orderedUndeployItems,
			UndeployItem[] undeployItems, Location location, String patternKey,
			String[] parameters) {
		super(location, patternKey, parameters);
		this.undeployItems = undeployItems;
		this.orderedUndeployItems = orderedUndeployItems;
	}

	/**
	 * Constructs an UndeployException object.
	 * 
	 * @param orderedUndeployItems
	 *            an array of ordered UndeployItems in the batch
	 * @param undeployItems
	 *            an array of UndeployItems in the batch
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
	public UndeployException(UndeployItem[] orderedUndeployItems,
			UndeployItem[] undeployItems, Location location, String patternKey,
			String[] parameters, Throwable cause) {
		super(location, patternKey, parameters, cause);
		this.undeployItems = undeployItems;
		this.orderedUndeployItems = orderedUndeployItems;
	}

	/**
	 * Returns the undeployment items in the batch.
	 * 
	 * @return undeployment items
	 */
	public UndeployItem[] getUndeployItems() {
		return this.undeployItems;
	}

	/**
	 * Returns the ordered undeployment items in the batch.
	 * 
	 * @return ordered undeployment items
	 */
	public UndeployItem[] getOrderedUndeployItems() {
		return this.orderedUndeployItems;
	}
}