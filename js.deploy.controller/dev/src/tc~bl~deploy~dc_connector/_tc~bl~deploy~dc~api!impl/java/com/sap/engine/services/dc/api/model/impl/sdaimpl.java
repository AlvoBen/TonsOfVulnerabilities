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

import java.util.HashSet;
import java.util.Set;

import com.sap.engine.services.dc.api.model.ScaId;
import com.sap.engine.services.dc.api.model.Sda;
import com.sap.engine.services.dc.api.model.SdaId;
import com.sap.engine.services.dc.api.model.SduId;
import com.sap.engine.services.dc.api.model.SduVisitor;
import com.sap.engine.services.dc.api.model.SoftwareType;
import com.sap.engine.services.dc.api.model.Version;
import com.sap.engine.services.dc.api.util.DAConstants;

/**
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-11
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
final class SdaImpl extends SduImpl implements Sda {

	private final SoftwareType softwareType;
	private final Set dependencies;
	private final Set dependingFrom;
	private final int hashCode;
	private final SdaId sdaId;
	private final ScaId scaId;
	private String toString = null;

	SdaImpl(final String name, final String vendor, final String location,
			final Version version, final SoftwareType softwareType,
			final String componentElementXML, final String csnComponent,
			final Set dependencies, final Set dependingFrom, final ScaId scaId) {
		super(name, vendor, location, version, componentElementXML,
				csnComponent);

		this.sdaId = new SdaIdImpl(name, vendor);
		this.softwareType = softwareType;
		this.dependencies = dependencies != null ? dependencies
				: new HashSet(0);
		this.dependingFrom = dependingFrom != null ? dependingFrom
				: new HashSet(0);
		this.scaId = scaId;
		this.hashCode = generateHashCode();
	}

	private int generateHashCode() {
		int result = super.hashCode();
		if (this.softwareType != null) {
			result = result * MULTIPLIER + this.softwareType.hashCode();
		}
		if (this.dependencies != null) {
			result = result * MULTIPLIER + this.dependencies.hashCode();
		}
		return result;
	}

	public void accept(SduVisitor visitor) {
		visitor.visit(this);
	}

	public SoftwareType getSoftwareType() {
		return this.softwareType;
	}

	public Set getDependencies() {
		return this.dependencies;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.model.Sda#getScaId()
	 */
	public ScaId getScaId() {
		return this.scaId;
	}

	public SduId getId() {
		return this.sdaId;
	}

	public String toString() {
		if (this.toString == null) {
			this.toString = "name '" + getName() + "', vendor '" + getVendor()
					+ "', location '" + getLocation() + "', version '"
					+ getVersion() + "', softwareType '" + this.softwareType
					+ "'" + "', csnComponent '" + getCsnComponent() + "'";
			if (this.dependencies != null && !this.dependencies.isEmpty()) {
				this.toString = this.toString + ", dependencies '"
						+ this.dependencies + "'" + DAConstants.EOL_INDENT
						+ "'";
			}
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

		if (!(obj instanceof Sda)) {
			return false;
		}

		Sda otherSda = (Sda) obj;
		if (!this.sduEquals(otherSda)) {
			return false;
		}

		if (!this.softwareType.equals(otherSda.getSoftwareType())) {
			return false;
		}

		if (!this.dependencies.equals(otherSda.getDependencies())) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		return this.hashCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.model.Sda#getDependingFrom()
	 */
	public Set getDependingFrom() {
		return this.dependingFrom;
	}

}
