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
package com.sap.scheduler.runtime;

/**
 * This exception is thrown when an oparation on a job fails because it 
 * is in the wrong state. As an example a cancel request on a job will fail 
 * with this exception when when the job is already
 * {@link JobStatus#COMPLETED COMPLETED}.
 * 
 * @author Dirk Marwinski
 */
public class JobIllegalStateException extends SchedulerException {

    static final long serialVersionUID = -1724825844572450341L;
    
	/**
	 * Constructs a new JobIllegalStateException object with no arguments.
	 */
	public JobIllegalStateException() {
		super();
	}

	/**
     * Constructs a new JobIllegalStateException object with no arguments.
	 *
     * @param msg message string
	 */
	public JobIllegalStateException(String msg) {
		super(msg);
	}
}
