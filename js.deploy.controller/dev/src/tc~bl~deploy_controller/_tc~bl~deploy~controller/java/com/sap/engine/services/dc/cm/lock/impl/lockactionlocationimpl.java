/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.cm.lock.impl;

import com.sap.engine.services.dc.cm.lock.LockAction;
import com.sap.engine.services.dc.cm.lock.LockActionLocation;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
final class LockActionLocationImpl extends LockActionLocation {

	private final String location;
	private LockAction lockAction;
	private final int hashCode;
	private final String toString;

	LockActionLocationImpl(String location) {
		this.location = location;
		this.hashCode = getLocation().hashCode();
		this.toString = getLocation();
	}

	LockActionLocationImpl(String location, LockAction lockAction) {
		this(location);
		this.lockAction = lockAction;
	}

	public String getLocation() {
		return location;
	}

	public LockAction getLockAction() {
		return lockAction;
	}

	public void setLockAction(LockAction action) {
		lockAction = action;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		final LockActionLocationImpl other = (LockActionLocationImpl) obj;

		if (!this.getLocation().equals(other.getLocation())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.hashCode;
	}

	public String toString() {
		return this.toString;
	}

}
