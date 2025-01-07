/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.util.lock;

import com.sap.engine.frame.core.locking.LockingContext;
import com.sap.engine.services.dc.util.Constant;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class LockType extends Constant {

	private final char lockType;

	public final static LockType EXCLUSIVE = new LockType(new Byte((byte) 0),
			"EXCLUSIVE", LockingContext.MODE_EXCLUSIVE_NONCUMULATIVE);

	public final static LockType SHARED = new LockType(new Byte((byte) 1),
			"SHARED", LockingContext.MODE_SHARED);

	LockType(Byte id, String name, char lockType) {
		super(id, name);
		this.lockType = lockType;
	}

	/**
	 * This lock type is valid for the LockingContext.
	 * 
	 * @return Returns the lockType.
	 */
	public char getLockType() {
		return lockType;
	}

	public boolean isLower(final LockType otherType) {
		if (this.equals(otherType)) {
			return false;
		}
		if (EXCLUSIVE.equals(this)) {
			return false;
		}
		return true;
	}

	public boolean isEquivalent(final LockType otherType) {
		return this.equals(otherType);
	}

	public boolean isLowerOrEquivalent(final LockType otherType) {
		return this.isLower(otherType) || this.isEquivalent(otherType);
	}

}
