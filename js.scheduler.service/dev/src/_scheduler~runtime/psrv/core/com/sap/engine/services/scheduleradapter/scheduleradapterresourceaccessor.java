/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.scheduleradapter;

import com.sap.localization.ResourceAccessor;

/**
 * Error codes for scheduler~runtime service.
 * 
 * @author Dirk Marwinski
 */
public class SchedulerAdapterResourceAccessor extends ResourceAccessor {

	public static final String UNABLE_TO_REGISTER_IN_JNDI   = "SchedulerAdapter_0001";
	public static final String UNABLE_TO_REGISTER_DEPLOY    = "SchedulerAdapter_0002";
	public static final String UNABLE_TO_INITIALIZE_LOCKING = "SchedulerAdapter_0003";
	public static final String NO_SCHEDULER_PROVIDER        = "SchedulerAdapter_0004";
	public static final String POSITIVE_NUMBER              = "SchedulerAdapter_0005";
	public static final String NO_PROVIDER                  = "SchedulerAdapter_0006";
	public static final String NO_CLASS_PROPERTY            = "SchedulerAdapter_0007";
	public static final String CANNOT_FIND_CLASS            = "SchedulerAdapter_0008";
	public static final String CANNOT_ACCESS_CLASS          = "SchedulerAdapter_0009";
	public static final String SCHEDULER_INIT_ERROR         = "SchedulerAdapter_0010";
	public static final String DATASOURCE_LOG_NOT_INIT      = "SchedulerAdapter_0011";
    public static final String UNABLE_TO_INIT_TABLE_DEFAULT = "SchedulerAdapter_0012";

	private static String BUNDLE_NAME = "com.sap.engine.services.scheduler.SchedulerAdapterResourceBundle";
	private static ResourceAccessor resourceAccessor = null; 

	public SchedulerAdapterResourceAccessor() {
	    super(BUNDLE_NAME);
	}

	public static synchronized ResourceAccessor getResourceAccessor() {  
	    if(resourceAccessor == null) {
	        resourceAccessor = new SchedulerAdapterResourceAccessor();
	    }
	    return resourceAccessor;  
	}
}
