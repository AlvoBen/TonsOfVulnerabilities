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
package com.sap.scheduler.api;

import com.sap.scheduler.runtime.SchedulerException;

/**
 * @author Thomas Mueller (d040939)
 *
 * @deprecated This exception will never be thrown
 */
public class TaskNotCancelableException extends SchedulerException {

	public TaskNotCancelableException() {
		super();
	}

	public TaskNotCancelableException(String message) {
		super(message);
	}

	public TaskNotCancelableException(Throwable cause) {
		super(cause);
	}

	public TaskNotCancelableException(String message, Throwable cause) {
		super(message, cause);
	}
}
