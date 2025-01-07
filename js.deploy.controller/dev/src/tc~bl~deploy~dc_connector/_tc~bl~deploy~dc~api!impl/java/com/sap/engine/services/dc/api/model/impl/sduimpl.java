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

import com.sap.engine.services.dc.api.model.Sdu;
import com.sap.engine.services.dc.api.model.SduVisitor;
import com.sap.engine.services.dc.api.model.Version;

/**
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-10
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
abstract class SduImpl implements Sdu {
	protected final static int OFFSET = 17;
	protected final static int MULTIPLIER = 59;

	private final String name;
	private final String vendor;
	private final String location;
	private final Version version;
	private final String componentElementXML;
	private final String csnComponent;
	private final int baseHashCode;

	SduImpl(final String name, final String vendor, final String location,
			final Version version, final String componentElementXML,
			final String csnComponent) {
		if (name == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DCAPI.1114] sdu name can not be null.");
		}
		if (vendor == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DCAPI.1115] sdu vendor can not be null.");
		}
		this.name = name;
		this.vendor = vendor;
		this.location = location;
		this.version = version;
		this.componentElementXML = componentElementXML;
		this.baseHashCode = generateBaseHashCode();
		this.csnComponent = csnComponent == null ? "" : csnComponent;
	}

	/**
	 * @return sdu name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return vendors name
	 */
	public String getVendor() {
		return this.vendor;
	}

	/**
	 * @return location
	 */
	public String getLocation() {
		return this.location;
	}

	/**
	 * @return version info
	 */
	public Version getVersion() {
		return this.version;
	}

	public String getComponentElementXML() {
		return this.componentElementXML;
	}

	public String getCsnComponent() {
		return this.csnComponent;
	}

	/**
	 * type visitor.visit(this);
	 * 
	 * @param visitor
	 */
	public abstract void accept(SduVisitor visitor);

	private int generateBaseHashCode() {
		int result = OFFSET + this.name.hashCode();
		result = result * MULTIPLIER + this.vendor.hashCode();
		if (this.location != null) {
			result = result * MULTIPLIER + this.location.hashCode();
		}
		if (this.version != null) {
			result = result * MULTIPLIER + this.version.hashCode();
		}
		return result;
	}

	protected boolean sduEquals(Sdu otherSdu) {
		if (!this.name.equals(otherSdu.getName())) {
			return false;
		}
		if (!this.vendor.equals(otherSdu.getVendor())) {
			return false;
		}
		if (!this.version.equals(otherSdu.getVersion())) {
			return false;
		}
		if (!this.location.equals(otherSdu.getLocation())) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		return this.baseHashCode;
	}
}