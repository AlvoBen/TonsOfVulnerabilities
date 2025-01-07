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
package com.sap.engine.services.scheduler.runtime;

/**
 * Exception thrown when there is a problem triggering the job
 * 
 * @author Dirk Marwinski
 */
public class JobTriggerException extends Exception {

	/**
	 * 
	 */
	public JobTriggerException() {
		super();
	}

	/**
	 * @param arg0
	 */
	public JobTriggerException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public JobTriggerException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public JobTriggerException(Throwable arg0) {
		super(arg0);
	}

}
