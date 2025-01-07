/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.scheduler.api;

import com.sap.scheduler.runtime.SchedulerException;

/**
 * A TaskValidationException is thrown on a problem with the task provided.
 * 
 * @author Dirk Marwinski
 */
public class TaskValidationException extends SchedulerException {

    /**
     * 
     *
     */
    public TaskValidationException() {
		super();
	}
    
    /**
     * 
     * @param message
     */
	public TaskValidationException(String message) {
		super(message);
	}
    
    /**
     * 
     * @param message
     * @param cause
     */
	public TaskValidationException(String message, Throwable cause) {
		super(message, cause);
	}
    
    /**
     * 
     * @param cause
     */
	public TaskValidationException(Throwable cause) {
		super(cause);
	}
}
