package com.sap.engine.services.dc.api.model.impl;

import com.sap.engine.services.dc.api.model.Version;

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
final class VersionImpl implements Version {
	private static final int OFFSET = 17;
	private final String versionString;
	private final int hashCode;

	VersionImpl(String versionString) {
		checkArg(versionString, "versionString");
		this.versionString = versionString;
		this.hashCode = generateHashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.model.Version#getVersionAsString()
	 */
	public String getVersionAsString() {
		return this.versionString;
	}

	public String toString() {
		return this.getVersionAsString();
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Version)) {
			return false;
		}

		final Version other = (Version) obj;

		if (!this.getVersionAsString().equals(other.getVersionAsString())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.hashCode;
	}

	private int generateHashCode() {
		return OFFSET + getVersionAsString().hashCode();
	}

	private void checkArg(Object arg, String argName) {
		if (arg == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DCAPI.1117] The argument '" + argName
							+ "' is null.");
		}
	}

}
