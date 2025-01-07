package com.sap.engine.services.dc.repo.impl;

import java.io.IOException;
import java.util.StringTokenizer;

import com.sap.engine.services.dc.repo.SduId;
import com.sap.engine.services.dc.util.StringUtils;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-21
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
abstract class SduIdImpl implements SduId {

	private static final long serialVersionUID = 396519758054107913L;

	private String name;
	private String vendor;
	private final int hashCode;
	private String toString;
	private String vendorAndName;

	SduIdImpl(final String name, final String vendor) {
		checkArg(name, "name");
		checkArg(vendor, "vendor");

		this.name = StringUtils.intern(name);
		this.vendor = StringUtils.intern(vendor);
		this.vendorAndName = StringUtils.intern(this.getVendor() + "/"
				+ this.getName());

		this.hashCode = calculateHashCode();
		this.toString = StringUtils.intern(this.getVendor() + "_"
				+ this.getName());
	}

	SduIdImpl(final String vendorAndName) {
		checkArg(vendorAndName, "vendorAndName");

		this.vendorAndName = StringUtils.intern(vendorAndName);
		initVendorAndNameProperties();
		this.hashCode = calculateHashCode();
		this.toString = StringUtils.intern(this.getVendor() + "_"
				+ this.getName());
	}

	private int calculateHashCode() {
		final int offset = 17;
		final int multiplier = 59;
		int result = offset + this.name.hashCode();
		return result * multiplier + this.vendor.hashCode();
	}

	private void initVendorAndNameProperties() {
		final StringTokenizer tokenizer = new StringTokenizer(
				this.vendorAndName, "/");
		final int count = tokenizer.countTokens();
		if (count < 2) {
			this.name = StringUtils.intern(tokenizer.nextToken());
			this.vendor = StringUtils.intern("sap.com");
		} else {
			this.vendor = StringUtils.intern(tokenizer.nextToken());
			this.name = StringUtils.intern(tokenizer.nextToken());
		}
	}

	public String getVendorAndName() {
		return this.vendorAndName;
	}

	public String getName() {
		return this.name;
	}

	public String getVendor() {
		return this.vendor;
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

		final SduIdImpl otherSduId = (SduIdImpl) obj;

		if (!this.getName().equals(otherSduId.getName())) {
			return false;
		}

		if (!this.getVendor().equals(otherSduId.getVendor())) {
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

	private void checkArg(Object arg, String argName) {
		if (arg == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DC.3425] The argument '" + argName
							+ "' is null.");
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		this.name = StringUtils.intern(this.name);
		this.vendor = StringUtils.intern(this.vendor);
		this.vendorAndName = StringUtils.intern(this.vendor + "/"
				+ this.name);

		this.toString = StringUtils.intern(this.toString);
	}
}
