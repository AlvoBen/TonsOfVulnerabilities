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
 * @author Hristo Sabev (i027642)
 *
 */
public class TooManyFireEventsException extends SchedulerException {

	public TooManyFireEventsException() {
		super();
	}

	public TooManyFireEventsException(String message) {
		super(message);
	}

	public TooManyFireEventsException(Throwable cause) {
		super(cause);
	}

	public TooManyFireEventsException(String message, Throwable cause) {
		super(message, cause);
	}
}
