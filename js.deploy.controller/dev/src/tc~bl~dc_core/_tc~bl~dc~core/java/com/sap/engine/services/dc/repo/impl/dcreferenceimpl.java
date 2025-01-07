package com.sap.engine.services.dc.repo.impl;

import java.io.IOException;

import com.sap.engine.services.dc.repo.DCReference;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.StringUtils;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-11
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public class DCReferenceImpl implements DCReference {

	private static final long serialVersionUID = -4702642750871876382L;

	private String name;
	private String vendor;
	private final int hashCode;
	private String scAlias;

	public DCReferenceImpl(String name, String vendor) {
		this(name, vendor, null);
	}

	public DCReferenceImpl(String name, String vendor, String scAlias) {
		this.name = StringUtils.intern(name);
		this.vendor = StringUtils.intern(vendor);
		this.scAlias = StringUtils.intern(scAlias);

		this.hashCode = calcHashCode();
	}

	public String getName() {
		return this.name;
	}

	public String getVendor() {
		return this.vendor;
	}

	public String getScAlias() {
		return this.scAlias;
	}

	public void setScAlias(String scAlias) {
		final String emptyString = "";
		if (emptyString.equals(scAlias)) {
			this.scAlias = StringUtils.intern(emptyString);
		} else {
			this.scAlias = scAlias;
		}
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof DCReference)) {
			return false;
		}

		final DCReference otherRef = (DCReference) obj;

		if (!this.getName().equals(otherRef.getName())) {
			return false;
		}

		if (!this.getVendor().equals(otherRef.getVendor())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.hashCode;
	}

	public String toString() {
		final StringBuffer result = new StringBuffer();
		result.append("    name:    '").append(getName()).append("'").append(
				Constants.EOL).append("    vendor:    '").append(getVendor())
				.append("'").append(Constants.EOL).append("    sc-alias:    '")
				.append(getScAlias()).append("'");
		return result.toString();
	}

	private int calcHashCode() {
		final int offset = 17;
		final int multiplier = 59;
		int result = offset + this.getName().hashCode();

		return result * multiplier + this.getVendor().hashCode();
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		this.name = StringUtils.intern(this.name);
		this.vendor = StringUtils.intern(this.vendor);
		this.scAlias = StringUtils.intern(this.scAlias);
	}
}
