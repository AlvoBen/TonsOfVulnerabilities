/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.utils.concurrent.impl;

/**
 * Class to hold timeout interval.
 * 
 * @author Emil Dinchev
 */
final class Timeout {
	private final long endTime;
	private final long startTime;

	/**
	 * @param timeout
	 *            timeout in milliseconds.
	 */
	public Timeout(final long timeout) {
		this.startTime = System.currentTimeMillis();
		this.endTime = startTime + timeout;
	}

	/**
	 * @return the remaining time in milliseconds. The remaining time is always
	 *         great or equal to 1 millisecond in order to avoid forever lasting
	 *         wait or throwing of IllegalArgumentException when the value is
	 *         negative.
	 */
	public long getRemainingTime() {
		final long remaining = endTime - System.currentTimeMillis();
		return remaining <= 0 ? 1 : remaining;
	}

	/**
	 * Check whether the timeout is elapsed
	 * 
	 * @return <tt>true</tt> if the timeout is elapsed.
	 */
	public boolean isElapsed() {
		return System.currentTimeMillis() >= endTime;
	}
}