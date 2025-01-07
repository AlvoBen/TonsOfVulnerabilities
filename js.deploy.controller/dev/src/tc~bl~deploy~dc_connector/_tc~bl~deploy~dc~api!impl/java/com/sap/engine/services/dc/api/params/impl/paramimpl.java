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
package com.sap.engine.services.dc.api.params.impl;

import com.sap.engine.services.dc.api.params.Param;

/**
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-9
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
class ParamImpl implements Param {
	private final String name;
	private String value;
	private final int hashCode;
	private String toString = null;

	ParamImpl(String name) {
		this(name, null);
	}

	ParamImpl(String name, String value) {
		this.name = name;
		this.value = value;
		this.hashCode = getHashCode();
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}

	public String toString() {
		if (this.toString == null) {
			this.toString = "ParamImpl[name=" + this.name + ",value="
					+ this.value + "]";
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

		if (!(obj instanceof Param)) {
			return false;
		}

		final Param other = (Param) obj;

		if (!this.getName().equals(other.getName())) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		return this.hashCode;
	}

	private int getHashCode() {
		final int offset = 17;
		int result = offset + this.getName().hashCode();
		return result;
	}
}