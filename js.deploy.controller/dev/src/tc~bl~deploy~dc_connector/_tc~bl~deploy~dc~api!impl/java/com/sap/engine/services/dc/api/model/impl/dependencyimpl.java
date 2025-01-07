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
package com.sap.engine.services.dc.api.model.impl;

import com.sap.engine.services.dc.api.model.Dependency;

/**
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-11
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
final class DependencyImpl implements Dependency {
	private final String name;
	private final String vendor;
	private final int hashCode;
	private String toString;

	DependencyImpl(String name, String vendor) {
		this.name = name;
		this.vendor = vendor;
		this.hashCode = getHashCode();
	}

	public String getName() {
		return this.name;
	}

	public String getVendor() {
		return this.vendor;
	}

	public String toString() {
		if (this.toString == null) {
			this.toString = getToString();
		}
		return this.toString;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof DependencyImpl)) {
			return false;
		}

		DependencyImpl other = (DependencyImpl) obj;

		if (!this.name.equals(other.name)) {
			return false;
		}

		if (!this.vendor.equals(other.vendor)) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.hashCode;
	}

	private synchronized String getToString() {
		return "name '" + this.name + "', vendor '" + this.vendor + "'";
	}

	private int getHashCode() {
		final int offset = 17;
		final int multiplier = 59;
		int result = offset + this.name.hashCode();
		result = result * multiplier + this.vendor.hashCode();
		return result;
	}

}