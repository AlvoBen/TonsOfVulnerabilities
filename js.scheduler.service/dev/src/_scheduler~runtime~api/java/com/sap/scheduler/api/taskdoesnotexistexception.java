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
import com.sap.security.api.IUser;

/**
 * This exception is thrown when a task is accessed which does not exist
 * anymore.
 * 
 * @author Dirk Marwinski
 */
public class TaskDoesNotExistException extends SchedulerException {
	public TaskDoesNotExistException(String errMsg) {
		super(errMsg);
	}
	
	public TaskDoesNotExistException(SchedulerTaskID id, IUser user) {
		super("Task with id " + id + " was not found neither ammong" +
   			" the tasks scheduled by user " + user + " nor ammong the tasks that will be" +
    			" executed on behalf of this user");
	}

}
