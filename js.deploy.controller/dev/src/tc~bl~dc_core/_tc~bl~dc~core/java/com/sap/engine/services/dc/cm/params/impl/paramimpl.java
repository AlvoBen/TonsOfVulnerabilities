package com.sap.engine.services.dc.cm.params.impl;

import com.sap.engine.services.dc.cm.params.Param;
import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-4
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class ParamImpl implements Param {

	static final long serialVersionUID = -7153020834384512208L;

	private final String name;
	private final String value;
	private final int hashCode;
	private final String toString;

	ParamImpl(String name, String value) {
		this.name = name;
		this.value = value;
		this.hashCode = getHashCode();
		this.toString = getToString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.params.Param#getName()
	 */
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.params.Param#getValue()
	 */
	public String getValue() {
		return this.value;
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

		final Param other = (Param) obj;

		if (!this.getName().equals(other.getName())) {
			return false;
		}

		if ((this.getValue() != null && !this.getValue().equals(
				other.getValue()))
				|| (this.getValue() == null && other.getValue() != null)) {
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

	private int getHashCode() {
		final int offset = 17;
		final int multiplier = 59;

		int result = offset + this.getName().hashCode();

		if (this.getValue() != null) {
			result = result * multiplier + this.getValue().hashCode();
		}

		return result;
	}

	private String getToString() {
		final StringBuffer sbTosTring = new StringBuffer();
		sbTosTring.append("param name: ").append(this.getName()).append(
				Constants.EOL).append("param value: ").append(this.getValue())
				.append(Constants.EOL);

		return sbTosTring.toString();
	}

}
