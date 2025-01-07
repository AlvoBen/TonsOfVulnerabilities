package com.sap.engine.services.dc.api.model.impl;

import com.sap.engine.services.dc.api.model.SoftwareType;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-1-25
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class SoftwareTypeImpl implements SoftwareType {

	private static final String DEFAULT_SUB_TYPE_NAME = "";
	private static final String DEFAULT_DESCRIPTION = "";

	private final String name;
	private final String subTypeName;
	private final String description;

	private final int hashCode;
	private final String toString;

	SoftwareTypeImpl(String name, String description) {
		this(name, DEFAULT_SUB_TYPE_NAME, description);
	}

	SoftwareTypeImpl(String name, String subTypeName, String description) {
		checkArg(name, "name");

		this.name = name;
		this.subTypeName = subTypeName != null ? subTypeName
				: DEFAULT_SUB_TYPE_NAME;
		this.description = description != null ? description
				: DEFAULT_DESCRIPTION;

		this.hashCode = generateHashCode();
		this.toString = generateToString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.model.SoftwareType#getName()
	 */
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.model.SoftwareType#getSubTypeName()
	 */
	public String getSubTypeName() {
		return this.subTypeName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.model.SoftwareType#getDescription()
	 */
	public String getDescription() {
		return this.description;
	}

	public String toString() {
		return this.toString;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof SoftwareType)) {
			return false;
		}

		final SoftwareType other = (SoftwareType) obj;

		if (!this.getName().equals(other.getName())) {
			return false;
		}

		if (!this.getSubTypeName().equals(other.getSubTypeName())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.hashCode;
	}

	private int generateHashCode() {
		final int offset = 17;
		final int multiplier = 59;

		int result = offset + getName().hashCode();
		result = result * multiplier + getSubTypeName().hashCode();

		return result;
	}

	private String generateToString() {
		return "'" + this.getName() + "', sub type '" + this.getSubTypeName()
				+ "'";
	}

	private void checkArg(Object arg, String argName) {
		if (arg == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DCAPI.1116] The argument '" + argName
							+ "' is null.");
		}
	}

}