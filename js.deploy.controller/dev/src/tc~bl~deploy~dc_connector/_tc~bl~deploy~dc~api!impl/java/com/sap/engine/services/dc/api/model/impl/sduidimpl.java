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

import com.sap.engine.services.dc.api.model.SduId;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: Apr 19, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public abstract class SduIdImpl implements SduId {
	private final String name;
	private final String vendor;
	private final int hashCode;
	private final String toString;

	protected SduIdImpl(String name, String vendor) {
		this.name = name;
		this.vendor = vendor;
		this.hashCode = (17 + name.hashCode()) * 59 + vendor.hashCode();
		this.toString = "name: '" + name + "', vendor: '" + vendor + "'";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.model.SduId#getName()
	 */
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.model.SduId#getVendor()
	 */
	public String getVendor() {
		return this.vendor;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}

		SduId other = (SduId) obj;

		if (!this.getName().equals(other.getName())) {
			return false;
		}
		if (!this.getVendor().equals(other.getVendor())) {
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
