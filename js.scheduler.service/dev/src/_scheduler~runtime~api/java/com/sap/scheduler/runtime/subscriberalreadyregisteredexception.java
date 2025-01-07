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
 * @author Dirk Marwinski
 */
public class SubscriberAlreadyRegisteredException extends SchedulerException {

	/**
	 * 
	 */
	public SubscriberAlreadyRegisteredException() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param arg0
	 */
	public SubscriberAlreadyRegisteredException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param arg0
	 * @param arg1
	 */
	public SubscriberAlreadyRegisteredException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param arg0
	 */
	public SubscriberAlreadyRegisteredException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
}
