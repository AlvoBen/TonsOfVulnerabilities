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

import com.sap.engine.services.dc.api.model.Sca;
import com.sap.engine.services.dc.api.model.ScaId;
import com.sap.engine.services.dc.api.model.SduId;
import com.sap.engine.services.dc.api.model.SduVisitor;
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
final class ScaImpl extends SduImpl implements Sca {
	private final Set sdaIds;
	private final Set originalSdaIds;
	private final Set notDeployedSdaIds;
	private String toString = null;
	private final int hashCode;
	private final ScaId scaId;

	/**
	 * @param name
	 * @param vendor
	 * @param location
	 * @param version
	 */
	ScaImpl(final String name, final String vendor, final String location,
			final Version version, final String componentElementXML,
			final String csnComponent, final Set sdaIds, final Set originalSdaIds,
			final Set notDeployedSdaIds) {
		super(name, vendor, location, version, componentElementXML,
				csnComponent);

		this.scaId = new ScaIdImpl(name, vendor);
		this.sdaIds = (sdaIds != null) ? sdaIds : new HashSet(0);
		this.originalSdaIds = (originalSdaIds != null) ? originalSdaIds : new HashSet(0);
		this.notDeployedSdaIds = (notDeployedSdaIds != null) ? notDeployedSdaIds : new HashSet(0);
		this.hashCode = generateHashCode();		
	}

	private int generateHashCode() {
		int result = super.hashCode();
		if (this.sdaIds != null) {
			result = result * MULTIPLIER + this.sdaIds.hashCode();			
		}
		if (this.originalSdaIds != null) {
			result = result * MULTIPLIER + this.originalSdaIds.hashCode();			
		}
		if (this.notDeployedSdaIds != null) {
			result = result * MULTIPLIER + this.notDeployedSdaIds.hashCode();			
		}
		return result;
	}

	public void accept(SduVisitor visitor) {
		visitor.visit(this);
	}

	public Set getSdaIds() {
		return this.sdaIds;
	}
	
	public Set getOriginalSdaIds() {
		return this.originalSdaIds;
	}
	
	public Set getNotDeployedSdaIds() {
		return this.notDeployedSdaIds;
	}

	public SduId getId() {
		return this.scaId;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Sca)) {
			return false;
		}

		Sca otherSca = (Sca) obj;
		if (!this.sduEquals(otherSca)) {
			return false;
		}

		if (!this.sdaIds.equals(otherSca.getSdaIds())) {
			return false;
		}
		
		if (!this.originalSdaIds.equals(otherSca.getOriginalSdaIds())) {
			return false;
		}
		
		if (!this.notDeployedSdaIds.equals(otherSca.getNotDeployedSdaIds())) {
			return false;
		}
		return true;
	}

	public String toString() {
		if (this.toString == null) {
			this.toString = "ScaImpl[name=" + getName() + ",vendor="
					+ getVendor() + ",location=" + getLocation() + ",version="
					+ getVersion() + ",sdas:" + DAConstants.EOL_INDENT
					+ this.sdaIds + "]" 
					+ DAConstants.EOL + ",original sdas:" + DAConstants.EOL_INDENT
					+ this.originalSdaIds + "]" 
					+ DAConstants.EOL + ",not deployed sdas:" + DAConstants.EOL_INDENT
					+ this.notDeployedSdaIds + "]"; 

		}
		return this.toString;
	}

	public int hashCode() {
		return this.hashCode;
	}
}
