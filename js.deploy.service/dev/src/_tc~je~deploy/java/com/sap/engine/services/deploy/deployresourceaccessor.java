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
package com.sap.engine.services.deploy;

import com.sap.localization.ResourceAccessor;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

/**
 * DeployResourceAccessor class is necessary to determine the category and
 * location of DeployResourceBundle in order Deploy service to log its
 * exceptions.
 * 
 * @author Rumiana Angelova
 * @version 6.30
 */
public class DeployResourceAccessor extends ResourceAccessor {

	private static String BUNDLE_NAME = "com.sap.engine.services.deploy.DeployResourceBundle";
	private static ResourceAccessor resourceAccessor = null;
	/**
	 * The category of the resource accessor.
	 */
	public static Category category = null;

	/**
	 * The location of the resource accessor.
	 */
	public static Location location = null;

	/**
	 * Constructs DeployResourceAccessor for the DeployResourceBundle.
	 */
	public DeployResourceAccessor() {
		super(BUNDLE_NAME);
	}

	/**
	 * Initializes this resource accessor with the specified category and
	 * location.
	 * 
	 * @param _category
	 *            the category for this resource accessor.
	 * @param _location
	 *            the location for this resource accessor.
	 */
	public void init(Category _category, Location _location) {
		category = _category;
		location = _location;
	}

	/**
	 * Returns this resource accessor. If it is null, a new one is created and
	 * returned.
	 * 
	 * @return this resource accessor.
	 */
	public static synchronized ResourceAccessor getResourceAccessor() {
		if (resourceAccessor == null) {
			resourceAccessor = new DeployResourceAccessor();
		}
		return resourceAccessor;
	}

}
