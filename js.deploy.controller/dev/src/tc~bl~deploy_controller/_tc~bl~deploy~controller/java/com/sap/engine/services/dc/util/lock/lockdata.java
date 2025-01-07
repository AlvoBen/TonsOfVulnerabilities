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

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.ValidatorUtils;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class LockData implements Serializable {

	private static final long serialVersionUID = 7772224061937148939L;

	final Set lockItems;

	final String toString;
	final int hashCode;

	public LockData(Set lockItems) {
		ValidatorUtils.validateNull(lockItems, "Set");
		this.lockItems = lockItems;

		this.toString = generateToString();
		this.hashCode = generateHashCode();
	}

	/**
	 * Gets <code>Set<code> with <code>LockItem<code> objects.
	 * 
	 * @return Returns the lockItems.
	 */
	public Set getLockItems() {
		return lockItems;
	}

	public String toString() {
		return toString;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		LockData other = (LockData) obj;

		if (!this.getLockItems().equals(other.getLockItems())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return hashCode;
	}

	private String generateToString() {
		final StringBuffer sb = new StringBuffer(Constants.EOL);
		final Iterator iter = getLockItems().iterator();
		while (iter.hasNext()) {
			sb.append(iter.next().toString());
			sb.append(Constants.EOL);
		}
		return sb.toString();
	}

	private int generateHashCode() {
		return getLockItems().hashCode();
	}

	public void initLockParameters(String arguments[], char modes[],
			String gbArgument, char gbMode) {
		int i = arguments.length;
		final Iterator liIter = getLockItems().iterator();
		LockItem lItem = null;
		while (liIter.hasNext()) {
			lItem = (LockItem) liIter.next();
			i--;
			arguments[i] = lItem.getSduId().toString();
			modes[i] = lItem.getLockType().getLockType();
		}
		i--;
		arguments[i] = gbArgument;
		modes[i] = gbMode;
	}

}
