/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.runtime.timeout;

import java.util.TimerTask;

/**
 * Represents a list entry, this is extended by the session watch dogs
 *
 * @author Nikolai Neichev
 */
public abstract class ListEntry extends TimerTask {

	private ListEntry head;
	private ListEntry next;
	private long expirationTime;
	private long maxInactInterval;
	private boolean isCanceled;

	public ListEntry getHead() {
		return head;
	}

	public void setHead(ListEntry head) {
		this.head = head;
	}

	public ListEntry getNext() {
		return next;
	}

	public void setNext(ListEntry next) {
		this.next = next;
	}

	public long getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}

	public void setMaxInactiveInteval(long maxInactiveInterval) {
		this.maxInactInterval = maxInactiveInterval;
	}

	public long getMaxInactiveInterval() {
		return maxInactInterval;
	}

	public boolean cancel() {
		isCanceled = true;
		return super.cancel();
	}


	public boolean isCanceled() {
		return isCanceled;
	}
}